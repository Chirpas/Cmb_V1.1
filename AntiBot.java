import java.util.Random;

public class AntiBot
{
    private Random r = new Random();


    //gaussian distribution for sleep times
    public int randomGauss(int mean, int std)
    {
        return (int)(r.nextGaussian()*std) + mean;
    }
}


/*
    TODO: Before you go anyfurther, implement some antiban techniques!! more than just a gaussian sleep distribution!

    1. Camera angle turning

    2. hover mouse over next action (SOMETIMES, NOT ALL THE TIME!)

    3. check xp related to current activity

    4. change the music currently playing!

    5. Re-arrange Inventory!

    6. zoom in/out

    7. examine random objects/players

    8. open the map maybe?
 */