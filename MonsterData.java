import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.MethodProvider;

public class MonsterData
{
    //variables accessible
    //equipment overide is used for objects such as slayer equipment. Dire stuff.
    public String[] equipmentOveride = null;
    public Area location = null;
    public String[] lootFilter = null;
    public int maxHit;
    public String[] inventName = null;
    public int[] inventQuant = null;

    //method variables
    private Monsters tmp = null;

    //api method
    MethodProvider api =null;

    public MonsterData(MethodProvider methodProvider)
    {
        this.api = methodProvider;
    }


    //case statement of all monster data that can be returned.
    public void setData(Monsters monster)
    {
        //api.log("SwitchCase: " + monster.toString());
        switch(monster)
        {
            case COW:
                this.equipmentOveride = null;
                this.location = new Area(3022, 3313, 3042, 3297);
                this.lootFilter = new String[]{"Grimy avantoe", "Grimy irit leaf", "Grimy kwuarm", "Grimy ranarr weed", "Grimy lantadyme", "Grimy dwarf weed", "Grimy cadantine", "Law rune", "Nature rune", "Ensouled chaos druid head", "Dragon spear", "Shield left half", "Tooth half of key", "Loop half of key"};
                this.maxHit = 1;
                this.inventName = null;
                this.inventQuant = null;
                break;

            case CHAOS_DRUID:
                this.equipmentOveride = null;
                this.location = new Area(3102, 9945, 3124, 9920);
                this.lootFilter = new String[]{"Looting bag", "Grimy avantoe", "Grimy irit leaf", "Grimy kwuarm", "Grimy ranarr weed", "Grimy lantadyme", "Grimy dwarf weed", "Grimy cadantine", "Law rune", "Nature rune", "Ensouled chaos druid head", "Dragon spear", "Shield left half", "Tooth half of key", "Loop half of key"};
            this.maxHit = 1;
                this.inventName = new String[]{"Salmon"};
                this.inventQuant = new int[]{15};

        }
    }


    //list of all monsters in database
    public enum Monsters
    {
        CHAOS_DRUID,
        COW
    }
}

