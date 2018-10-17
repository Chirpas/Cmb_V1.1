import org.osbot.In;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;


public class Banking
{

    private Script script;
    private AntiBot antiBot = new AntiBot();
    private MethodProvider api;

    RS2Widget lootingBag = null;



    public Banking(Script s, MethodProvider methodProvider)
    {
        this.script = s;
        this.api = methodProvider;
    }


    public boolean runToBank(Area bank)
    {
        this.script.walking.webWalk(bank.getRandomPosition());
        if(bank.contains(this.script.myPlayer()))
        {
            return true;
        }
        else
            return false;
    }

    /**
     * Gets the closest bank from your position and returns that Area object<br>
     * @return Area
     */

    public Area getClosestBank()
    {
        Area closestBank = null;
        int l = banks.length;
        if(this.script.myPlayer() != null)
        {
            Position p = this.script.myPosition();
            for(int i = 0; i < l; i ++)
            {
                Area currBank = banks[i];
                if(closestBank == null)
                    closestBank = currBank;
                else
                if(currBank.getRandomPosition().distance(p) < closestBank.getRandomPosition().distance(p))
                    closestBank = currBank;
            }
        }
        return closestBank;
    }

    public void openBank() throws InterruptedException
    {
        this.script.bank.open();
        new ConditionalSleep(3000)
        {
            public boolean condition()
            {
                return script.bank.isOpen();
            }
        }.sleep();
        this.script.sleep(antiBot.randomGauss(500,55));
    }


    /*
        Loops through a list of given equipment and equips
     */
    public boolean changeEquipment(String[] equipment) throws InterruptedException
    {
        api.log("Changing Equipment!");
        //make sure inventry is empty
        depositAllItems();
        this.script.sleep(antiBot.randomGauss(500,40));
        this.script.bank.depositWornItems();
        this.script.sleep(antiBot.randomGauss(500,40));
        //withdraw required equipment
        for(int i = 0; i < equipment.length; i++)
        {
            //check if equipment exists
            if(this.script.bank.contains(equipment[i]))
            {
                this.script.bank.withdraw(equipment[i], 1);
                this.script.sleep(antiBot.randomGauss(400,25));
            }
            else
            {
                api.log("ERROR: item(s) not found in bank - " + equipment[i]);
                api.log("Halting Script!");
            }
        }
        //equip withdrawn equipment
        this.script.bank.close();
        this.script.sleep(antiBot.randomGauss(600,80));
        for(int i = 0; i < equipment.length; i++)
        {
            if(this.script.inventory.getItem(equipment[i]).hasAction("Wear"))
            {
                this.script.inventory.getItem(equipment[i]).interact("Wear");
            }
            else if(this.script.inventory.getItem(equipment[i]).hasAction("Wield"))
            {
                this.script.inventory.getItem(equipment[i]).interact("Wield");
            }
            else if(this.script.inventory.getItem(equipment[i]).hasAction("Equip"))
            {
                this.script.inventory.getItem(equipment[i]).interact("Equip");
            }
            this.script.sleep(antiBot.randomGauss(375,10));
        }
        //re-open bank and deposit any swapped out equipment
        openBank();
        depositAllItems();
        api.log("Done withdrawing Equipment");
        //leave bank open!
        return true;
    }

    public boolean overideEquipment(String[] equipment) throws InterruptedException
    {
        api.log("Overiding Equipment!");
        this.script.sleep(antiBot.randomGauss(500,40));
        //withdraw overide equipment
        if(equipment != null) {
            for (int i = 0; i < equipment.length; i++) {
                //check if equipment exists
                if (this.script.bank.contains(equipment[i])) {
                    this.script.bank.withdraw(equipment[i], 1);
                    this.script.sleep(antiBot.randomGauss(400, 25));
                } else {
                    api.log("ERROR: item(s) not found in bank - " + equipment[i]);
                    api.log("Halting Script!");
                }
            }
            //equip withdrawn equipment
            this.script.bank.close();
            this.script.sleep(antiBot.randomGauss(400, 80));
            for (int i = 0; i < equipment.length; i++) {
                if (this.script.inventory.getItem(equipment[i]).hasAction("Wear")) {
                    this.script.inventory.getItem(equipment[i]).interact("Wear");
                } else if (this.script.inventory.getItem(equipment[i]).hasAction("Wield")) {
                    this.script.inventory.getItem(equipment[i]).interact("Wield");
                } else if (this.script.inventory.getItem(equipment[i]).hasAction("Equip")) {
                    this.script.inventory.getItem(equipment[i]).interact("Equip");
                }
                this.script.sleep(antiBot.randomGauss(375, 10));
            }
            //re-open bank and deposit any swapped out equipment
            openBank();
            depositAllItems();
            api.log("Done overiding Equipment");
            //leave bank open!
        }
        return true;
    }

    /*
        implemented on 10/10/2018 - Chris.
        Takes a list of items and corresponding quantities.
        fills invent with items
     */
    public void withdrawInventory(String[] items, int[] quantaties) throws InterruptedException
    {
        if(items != null)
        {
            script.log("Withdrawing Inventory!");
            for (int i = 0; i < items.length; i++) {
                script.log("Withdrawing " + quantaties[i] + items[i]);
                this.script.bank.withdraw(items[i], quantaties[i]);
                this.script.sleep(antiBot.randomGauss(500, 75));
            }
            script.log("done");
            this.script.bank.close();
            this.script.sleep(antiBot.randomGauss(600, 50));
        }
    }


    //wrapper to deposit items
    public void depositAllItems() throws InterruptedException
    {
        this.script.bank.depositAll();
        this.script.sleep(antiBot.randomGauss(400,40));
    }

    public void depositAllExcept(String[] items) throws InterruptedException
    {
        this.script.bank.depositAllExcept(items);
        this.script.sleep(antiBot.randomGauss(400,40));
    }

    public void depositWornItems() throws InterruptedException
    {
        this.script.bank.depositWornItems();
        this.script.sleep(antiBot.randomGauss(500,40));
    }

    public void emptyLootBag() throws InterruptedException
    {
        if(api.inventory.contains("Looting bag"))
        {
            api.inventory.getItem("Looting bag").interact("View");
            api.sleep(api.random(500,750));
            lootingBag = api.getWidgets().get(15,5);
            if (lootingBag != null && lootingBag.isVisible())
            {
                api.log("Emptying looting bag");
                lootingBag.interact("Deposit loot");
                api.sleep(500);
            }

        }
    }

    //bank list
    private final Area[] banks =
            {
                    Banks.AL_KHARID,
                    Banks.DRAYNOR,
                    Banks.EDGEVILLE,
                    Banks.FALADOR_EAST,
                    Banks.FALADOR_WEST,
                    Banks.VARROCK_EAST,
                    Banks.VARROCK_WEST,
                    Banks.CAMELOT,
                    Banks.CATHERBY,
                    Banks.ARDOUGNE_NORTH,
                    Banks.ARDOUGNE_SOUTH,
                    Banks.CANIFIS,
                    Banks.CASTLE_WARS,
                    Banks.DUEL_ARENA,
                    Banks.GNOME_STRONGHOLD,
            };
}