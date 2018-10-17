
import org.osbot.rs07.script.MethodProvider;

public abstract class Handler {

    protected MethodProvider api;
    protected String status;
    protected boolean stopped;

    public Handler(MethodProvider methodProvider) {
        status = "Initializing";
        api = methodProvider;
    }

    public abstract void handleNextState() throws InterruptedException;


    public void start()
    {
        stopped = false;
    }

    public void stop()
    {
        stopped = true;
    }

    public String getStatus()
    {
        return status;
    }

    public boolean isStopped()
    {
        return stopped;
    }
}