import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.Settings;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.List;

/*
    TODO: Add Monster data
    TODO: TESTING!

 */

public class FightHandle extends Handler
{
    //Constants
    private final int NPC_DEATH_TIMER = 4000;
    private final int FIGHT_SLEEP_TIMER = 100;
    private final long TIME_OUT = 15000;
    private final Area deathArea = new Area(3201, 3236, 3241, 3199);

    //main script variables
    public boolean hasTask;
    private int targCount;
    private Area npcArea = null;
    private String targetName = null;
    private NPC target = null;
    private boolean containsFood;
    private int maxHit;

    //Inventory & equipment
    private final String[] baseEquipment = {"Mithril scimitar"};
    private String[] equipment = null;
    private String[] inventPreset = null;
    private int[] inventAmmounts = null;
    public String food = "Salmon";

    //looting
    private List<GroundItem> oldPile = null;
    private List<GroundItem> newPile = null;
    private String[] lootFilter = null;

    //objects
    private Banking banking = null;
    private Looting looting = null;
    private MonsterData monsterData = null;
    private AntiBot antiBot = new AntiBot();
    private WalkingD walkingD = null;
    private Script script;

    //init time for target timeout
    private long timePrev = System.currentTimeMillis();
    private long timeElapse = System.currentTimeMillis();

    //construct Handle object, and accept banking object
    public FightHandle(MethodProvider methodProvider, Script script)
    {
        super(methodProvider);
        this.script = script;
        this.banking = new Banking(script, methodProvider);
        this.monsterData = new MonsterData(methodProvider);
        this.looting = new Looting(methodProvider);
        this.walkingD = new WalkingD(methodProvider);
    }

    private enum State
    {
        BANKING,
        WALKING,
        LOW_HP,
        TARG_DYING,
        FIGHTING,
        END_OF_TASK,
        ERROR
    }

    public State getState()
    {
        containsFood = api.inventory.contains(food);

        if(deathArea.contains(api.myPlayer()))
        {
            api.log("you are dead.");
            stop();
        }
        if(underAttkByPlayer() == true)
        {
            api.log("Under Attack.");
            script.settings.setRunning(true);
            return State.BANKING;
        }
        if(targCount <= 0)
        {
            return State.END_OF_TASK;
        }
        else if((!containsFood && (api.skills.getDynamic(Skill.HITPOINTS)) < api.random(32,40)) || (!containsFood && (api.inventory.getEmptySlotCount() == 0)))
        {
            return State.BANKING;
        }
        //make dynamic, above monsters max hit
        else if(api.getSkills().getDynamic(Skill.HITPOINTS) < api.random(32,40))
        {
            return State.LOW_HP;
        }
        else if(!npcArea.contains(api.myPlayer()))
        {
            return State.WALKING;
        }
        else if(isDeathAnimating(target))
        {
            return State.TARG_DYING;
        }
        return State.FIGHTING;
    }

    //Main control sequence
    public void handleNextState() throws InterruptedException
    {
        //var to hold current bot state
        State state = getState();

        //handle possible timeout
        timeElapse = System.currentTimeMillis();
        if(!api.myPlayer().isMoving() && !api.myPlayer().isAnimating() && !api.myPlayer().isUnderAttack() && timeOut(15000))
        {
            target = null;
        }


        //perform actions depending on state.
        switch(state)
        {
            case END_OF_TASK:
                hasTask = false;
                api.log("Finished task.");
                stop();
                break;

            case BANKING:
                walkingD.startWalk(false);
                banking.runToBank(banking.getClosestBank());
                banking.openBank();
                banking.depositAllExcept(new String[]{"Looting bag"});
                banking.emptyLootBag();
                banking.withdrawInventory(inventPreset,inventAmmounts);
                break;

            case WALKING:
                walkingD.startWalk(true);
                break;

            case LOW_HP:
                if(api.inventory.contains(food))
                {
                    api.inventory.getItem(food).interact("Eat");
                }
                break;

            case TARG_DYING:
                api.log("**************************");
                //get other player loot at target square
                if(target != null)
                {
                    int x = target.getX();
                    int y = target.getY();
                    oldPile = looting.getObjects(x,y);
                    new ConditionalSleep(NPC_DEATH_TIMER)
                    {
                        public boolean condition()
                        {
                            return !target.exists();
                        }
                    }.sleep();
                    api.sleep(antiBot.randomGauss(1000,50));
                    newPile = looting.getObjects(x,y);
                    looting.lootItems(oldPile, newPile, lootFilter, food);
                    //finished looting
                    target = null;
                }
                //decrease target count only if non-infinite task.
                if(targCount < 9000)
                {
                    targCount--;
                }
                timePrev = System.currentTimeMillis();
                break;

            //TODO: IMPROVE FIGHTING ALGORITHM
            case FIGHTING:
                //check if under attack by a diff target
                List<NPC> npcs = api.getNpcs().getAll();
                if (npcs != null)
                {
                    for(int i = 0; i < npcs.size(); i++)
                    {
                        if(npcs.get(i).getInteracting() == api.myPlayer())
                        {
                            target = npcs.get(i);
                            break;
                        }
                    }
                    if(target != null)
                    {
                        if (!api.myPlayer().isInteracting(target)) {
                            if (target.interact("Attack")) {
                                timePrev = System.currentTimeMillis();
                                new ConditionalSleep(2000) {
                                    @Override
                                    public boolean condition() {
                                        return api.myPlayer().isAnimating();
                                    }
                                }.sleep();
                            }
                        }
                    }
                }

                if(target == null)
                {
                    target = api.getNpcs().closest(o->o.getName().equals(targetName) && o.getInteracting() == null && npcArea.contains(o));
                    if(target != null && target.interact("Attack"))
                    {
                        timePrev = System.currentTimeMillis();
                        new ConditionalSleep(2000)
                        {
                            @Override
                            public  boolean condition()
                            {
                                return api.myPlayer().isAnimating();
                            }
                        }.sleep();
                    }
                }

                api.sleep(FIGHT_SLEEP_TIMER);
                break;

            case ERROR:
                api.log("An Error has occurred! Stopping Program!");
                stop();
                break;
        }
    }

    //Sets new combat task. Called from main/other class
    public void newFightTask(String targ, int ammount) throws InterruptedException
    {
        //set fight handle variables
        hasTask = true;
        targetName = targ;
        targCount = ammount;

        api.log("targetName: " + targetName);

        monsterData.setData(MonsterData.Monsters.valueOf(targetName.replaceAll(" ", "_").toUpperCase()));

        lootFilter = monsterData.lootFilter;
        npcArea = monsterData.location;
        maxHit = monsterData.maxHit;
        equipment = monsterData.equipmentOveride;
        inventPreset = monsterData.inventName;
        inventAmmounts = monsterData.inventQuant;
    }

    private void firstBank() throws InterruptedException
    {
        banking.runToBank(banking.getClosestBank());
        banking.openBank();
        banking.changeEquipment(baseEquipment);

        //put on over-ride equipment
        banking.overideEquipment(equipment);
        banking.withdrawInventory(inventPreset,inventAmmounts);
    }


    //check if npc is dying
    private boolean isDeathAnimating(Character character)
    {
        if(character != null) {
            return character.getHealthPercent() == 0 && character.isAnimating();
        }
        else
        {
            return false;
        }
    }

    private boolean timeOut(long timeOutTime)
    {
        if(timeElapse-timePrev > timeOutTime)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean underAttkByPlayer()
    {
        List<Player> localPlayers = api.players.getAll();
        if(localPlayers != null)
        {
            for(int i = 0; i < localPlayers.size(); i++)
            {
                if(localPlayers.get(i).getInteracting() == api.myPlayer())
                    return true;
            }
        }
        return false;
    }
}
