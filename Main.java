//osbot libraries.
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.utility.ConditionalSleep;

//for graphics/paint
import java.awt.*;
import java.util.Arrays;

@ScriptManifest(
        author = "Chirpas",
        version = 1.00,
        info = "Generic Combat",
        logo = "",
        name = "Druid Killer"
)

public class Main extends Script
{
    private final int SLEEP_TIME = 200;

    //create handle for script
    private FightHandle stateHandler = new FightHandle(this,this);

    private Area topStairs = new Area(3093, 3472, 3096, 3469);


    @Override
    public void onStart() throws InterruptedException
    {
        log("Script Begun!");
        stateHandler.newFightTask("Chaos druid",9999);

    }

    @Override
    public int onLoop() throws InterruptedException
    {
        if(stateHandler.isStopped())
        {
            log("StateHandler has stopped!");
            stop();
        }
        else
        {
            if(stateHandler.hasTask)
            {
                stateHandler.handleNextState();
            }
            else
            {
                log("Current task finished.");
                stop();
            }
        }
        return SLEEP_TIME;
    }

    @Override
    public void onExit()
    {

    }

    public void onPaint(Graphics2D g)
    {

    }
}
