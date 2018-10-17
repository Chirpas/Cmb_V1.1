import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.List;

public class Looting
{
    private MethodProvider api;

    private int emptySlots;

    public Looting(MethodProvider methodProvider)
    {
        this.api = methodProvider;
    }


    public void lootItems(List<GroundItem> oldList, List<GroundItem> newList, String[] lootFilter, String food) throws InterruptedException
    {
        List<GroundItem> toPickUp = siftItems(oldList,newList);


        api.log("Picking up loot!");
        for(int i = 0; i < toPickUp.size(); i++)
        {
            //compare with loot filter
            for(int j = 0; j < lootFilter.length; j++)
            {
                if(toPickUp.get(i).getName().equals(lootFilter[j]))
                {
                    api.log("Picking up " + toPickUp.get(i).getName());
                    emptySlots = api.inventory.getEmptySlotCount();
                    if(api.inventory.contains(food) && api.inventory.getEmptySlotCount() == 0)
                    {
                        api.inventory.getItem(food).interact("Eat");
                        api.sleep(MethodProvider.random(400,600));
                    }
                    if(toPickUp.get(i).interact("Take"))
                    {
                        new ConditionalSleep(2000)
                        {
                            public boolean condition()
                            {
                                return api.inventory.getEmptySlotCount() < emptySlots;
                            }
                        }.sleep();
                    }
                }
            }
        }

    }


    //get all objects on the ground
    public List<GroundItem> getObjects(int x, int y)
    {
        return api.groundItems.get(x,y);
    }

    //find new items in the list
    private List<GroundItem> siftItems(List<GroundItem> oldList, List<GroundItem> newList)
    {
        List<GroundItem> temp = newList;
        temp.removeAll(oldList);
        return temp;
    }
}
