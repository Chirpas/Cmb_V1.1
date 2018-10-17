import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.Arrays;

public class WalkingD
{
    private MethodProvider api;

    public WalkingD(MethodProvider methodProvider)
    {
        this.api = methodProvider;
    }

    //interactable object
    RS2Object interact = null;

    //Positional data for area identification
    Area pathOneArea = new Area(
            new int[][]{
                    { 3096, 9867 },
                    { 3099, 9867 },
                    { 3099, 9886 },
                    { 3097, 9887 },
                    { 3098, 9905 },
                    { 3104, 9905 },
                    { 3104, 9914 },
                    { 3094, 9914 },
                    { 3094, 9879 },
                    { 3096, 9879 }
            }
    );

    Area pathTwoArea = new Area(
            new int[][]{
                    { 3104, 9912 },
                    { 3104, 9907 },
                    { 3136, 9907 },
                    { 3135, 9916 },
                    { 3135, 9918 },
                    { 3129, 9918 },
                    { 3128, 9912 }
            }
    );

    Area pathThreeArea = new Area(
            new int[][]{
                    { 3131, 9919 },
                    { 3134, 9919 },
                    { 3136, 9930 },
                    { 3129, 9939 },
                    { 3122, 9934 },
                    { 3123, 9928 }
            }
    );

    Area edgeville = new Area(3098, 3467, 3086, 3500);

    Area druids =  new Area(
            new int[][]{
                    { 3125, 9926 },
                    { 3118, 9937 },
                    { 3109, 9945 },
                    { 3101, 9944 },
                    { 3101, 9935 },
                    { 3109, 9923 },
                    { 3120, 9920 }
            }
    );

    //path data
    Position[] path1 = {
            new Position(3097, 9869, 0),
            new Position(3097, 9879, 0),
            new Position(3097, 9880, 0),
            new Position(3095, 9890, 0),
            new Position(3095, 9890, 0),
            new Position(3095, 9900, 0),
            new Position(3095, 9905, 0),
            new Position(3101, 9909, 0)
    };
    Position[] path1r = {
            new Position(3101, 9910, 0),
            new Position(3095, 9906, 0),
            new Position(3095, 9896, 0),
            new Position(3095, 9886, 0),
            new Position(3095, 9882, 0),
            new Position(3096, 9872, 0),
            new Position(3097, 9868, 0)
    };

    Position[] path2 = {
            new Position(3105, 9909, 0),
            new Position(3115, 9909, 0),
            new Position(3125, 9909, 0),
            new Position(3130, 9909, 0),
            new Position(3132, 9914, 0)
    };
    Position[] path2r = {
            new Position(3132, 9914, 0),
            new Position(3130, 9909, 0),
            new Position(3125, 9909, 0),
            new Position(3115, 9909, 0),
            new Position(3105, 9909, 0)
    };

    Position[] path3 = {
            new Position(3132, 9919, 0),
            new Position(3132, 9926, 0),
            new Position(3127, 9930, 0),
            new Position(3117, 9928, 0),
            new Position(3114, 9928, 0)
    };
    Position[] path3r = {
            new Position(3105, 9942, 0),
            new Position(3105, 9936, 0),
            new Position(3112, 9931, 0),
            new Position(3117, 9928, 0),
            new Position(3127, 9930, 0),
            new Position(3129, 9930, 0),
            new Position(3132, 9920, 0),
            new Position(3132, 9919, 0)
    };

    Area edgeA = new Area(3099, 3466, 3089, 3500);
    Area path1A = new Area(3089, 9917, 3103, 9865);
    Area path2A = new Area(3104, 9917, 3135, 9902);
    Area path3A = new Area(3135, 9918, 3122, 9937);

    //Variable to determine if the bot is walking to/from druids
    public boolean toDruids;

    public void startWalk(boolean toDruids) throws InterruptedException
    {
        this.toDruids = toDruids;
        if(toDruids == true)
        {
            walkToDruids();
        }
        else
        {
            walkFromDruids();
        }
    }

    private void walkToDruids() throws InterruptedException
    {
        if(edgeA.contains(api.myPlayer())) {
            api.log("Walking to trapdoor");
            api.walking.webWalk(new Area(3092, 3472, 3096, 3468));
            api.sleep(500);
            interact = api.getObjects().closest(o -> o.getName().equals("Trapdoor"));
            if (interact.getId() == 1579) {
                interact.interact("Open");
                api.sleep(400);
                new ConditionalSleep(4000) {
                    public boolean condition() {
                        return !api.myPlayer().isMoving();
                    }
                }.sleep();
                interact = api.getObjects().closest(o -> o.getName().equals("Trapdoor"));
            }
            interact.interact("Climb-down");
            api.sleep(400);
            new ConditionalSleep(4000) {
                public boolean condition() {
                    return !api.myPlayer().isAnimating() && !api.myPlayer().isMoving();
                }
            }.sleep();
        }
        if(path1A.contains(api.myPlayer())) {
            api.log("Walking to gate1");
            api.walking.walkPath(Arrays.asList(path1));
            api.sleep(500);
            interact = api.getObjects().closest(o -> o.getName().equals("Gate"));

            if (interact != null) {
                if (interact.getId() == 1568) {
                    interact.interact("Open");
                    api.sleep(750);
                    new ConditionalSleep(4000) {
                        public boolean condition() {
                            return !api.myPlayer().isMoving();
                        }
                    }.sleep();
                }

                api.walking.walk(new Position(3109, 9909, 0));
            }
        }

        if(path2A.contains(api.myPlayer())) {
            api.log("Walking to Gate2");
            api.walking.walkPath(Arrays.asList(path2));
            interact = api.getObjects().closest(o -> o.getName().equals("Gate"));
            if (interact != null) {
                interact.interact("Open");
                api.sleep(1000);
                new ConditionalSleep(4000) {
                    public boolean condition() {
                        return !api.myPlayer().isMoving();
                    }
                }.sleep();

                api.walking.walk(new Position(3132, 9924, 0));
            }
        }

        if(path3A.contains(api.myPlayer())) {
            api.log("Walking to Druids");
            api.walking.walkPath(Arrays.asList(path3));
        }
    }

    private void walkFromDruids() throws InterruptedException
    {
        if(new Area(3103, 9942, 3136, 9918).contains(api.myPlayer()))
        {
            api.log("Walking to Gate2");
            api.walking.walkPath(Arrays.asList(path3r));
            interact = api.getObjects().closest(o -> o.getName().equals("Gate"));
            if (interact != null) {
                interact.interact("Open");
                api.sleep(400);
                new ConditionalSleep(4000) {
                    public boolean condition() {
                        return !api.myPlayer().isMoving();
                    }
                }.sleep();
                api.walking.walk(new Position(3131, 9914, 0));
            }
        }

        if(path2A.contains(api.myPlayer()))
        {
            api.log("Walking to Gate1");
            api.walking.walkPath(Arrays.asList(path2r));
            interact = api.getObjects().closest(o -> o.getName().equals("Gate"));

            if (interact != null)
            {
                if (interact.getId() == 1568)
                {
                    interact.interact("Open");
                    api.sleep(1000);
                    new ConditionalSleep(4000) {
                        public boolean condition()
                        {
                            return !api.myPlayer().isMoving();
                        }
                    }.sleep();
                }
                api.walking.walk(new Position(3101, 9909, 0));
            }
        }

        if(path1A.contains(api.myPlayer()))
        {
            api.log("Walking to Ladder");
            api.walking.walkPath(Arrays.asList(path1r));
            interact = api.getObjects().closest(o -> o.getName().equals("Ladder"));

            if (interact != null) {
                if (interact.getId() == 1568) {
                    interact.interact("Climb-up");
                    api.sleep(700);
                    new ConditionalSleep(4000) {
                        public boolean condition() {
                            return !api.myPlayer().isMoving();
                        }
                    }.sleep();
                }
            }
        }

        api.log("Walking to bank...");
        api.walking.webWalk(new Area(3091, 3493, 3095, 3488));
    }
}

