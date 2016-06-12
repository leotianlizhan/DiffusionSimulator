package com.zhan.leo.diffusionandosmosis;

import android.graphics.Point;

/**
 * Created by Linda on 2/4/2016.
 */
public class Particle {
    //Stores the velocity of the particle
    public float vX = 0, vY = 0;

    //Stores the location of the particle
    public Point location;

    //Stores the radius of the particle
    public int radius;

    public Particle(float velocityX, float velocityY, Point location, int r)
    {
        vX = velocityX;
        vY = velocityY;
        this.location = location;
        radius = r;
    }
}
