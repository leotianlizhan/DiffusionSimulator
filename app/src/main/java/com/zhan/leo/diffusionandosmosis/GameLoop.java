package com.zhan.leo.diffusionandosmosis;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Telephony;
import android.view.SurfaceHolder;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameLoop implements Runnable {

    //Whether the game is running or not
    private boolean isRunning;
    private GameState state;

    private static final float V_MAX = 4f;
    private static final float V_MIN = 0f;
    private static final float ACCEL = 0.001f;
    private static final float AREA = 0.9f*MainActivity.densityDpi;
    private static final float AMOUNT = 0.12f;
    private static final int radius = 20;
    private int width;
    private int height;
    private int rectWidth;
    private int rectLeft;
    private int rectRight;
    private Rect membrane;
    private int leftSideCount;


    //Stores the holder on which everything will be drawn
    private SurfaceHolder surfaceHolder;

    private ConcurrentLinkedQueue<Particle> particles;

    private Random rng = new Random();

    private static int elapsed = 1;

    Paint paint;

    public static float fps;

    public static final float FPS = 1000/60;

    public GameLoop(SurfaceHolder surfaceHolder, ConcurrentLinkedQueue<Particle> p, GameState state) {

        this.surfaceHolder = surfaceHolder;
        this.state = state;

        particles = p;

        isRunning = true;

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(100f);

        if(state == GameState.Diffusion) {
            width = MainActivity.width;
            height = MainActivity.height;
            //generate diffusion particles
            generateParticles(radius, (int) (MainActivity.densityDpi * AMOUNT));
        }else if(state == GameState.Osmosis){
            if(Build.VERSION.SDK_INT >= 19)
                width = MainActivity.realHeight;
            else
                width = MainActivity.height;
            height = MainActivity.width;

            //newly added, need testing
            rectWidth = width/50;
            rectLeft = width/2-rectWidth/2;
            rectRight = rectLeft+rectWidth;
            membrane = new Rect(rectLeft, 0, rectRight, height);

            generateParticles(radius, (int)(MainActivity.densityDpi * AMOUNT));

            leftSideCount = 0;
        }
    }

    @Override
    public void run() {

        long last = System.currentTimeMillis();

        while (isRunning) {

            long current = System.currentTimeMillis();
            elapsed = (int) (current - last);

            if (elapsed >= FPS) {

                processInput();
                update();
                render();

                last = current;

            }

        }

    }


    private Rect bounds = new Rect();
    private void render() {

        Canvas canvas = null;

        try {

            canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 255, 255, 255));

            if(state == GameState.Osmosis) {
                paint.getTextBounds(Integer.toString(leftSideCount), 0, Integer.toString(leftSideCount).length(), bounds);
                canvas.drawText(Integer.toString(leftSideCount), width / 4 - bounds.width() / 2, bounds.height() + 2, paint);
                int rightSideCount = (int) (MainActivity.densityDpi * AMOUNT) - leftSideCount;
                paint.getTextBounds(Integer.toString(rightSideCount), 0, Integer.toString(rightSideCount).length(), bounds);
                canvas.drawText(Integer.toString(rightSideCount), width * 3 / 4 - bounds.width() / 2, bounds.height() + 2, paint);
                leftSideCount=0;
                canvas.drawRect(membrane, paint);
            }

            for (Particle p : particles) {
                canvas.drawCircle(p.location.x, p.location.y, p.radius, paint);
            }

        } catch (NullPointerException npe) {

            npe.printStackTrace();

        } finally {

            if (canvas != null)
                surfaceHolder.unlockCanvasAndPost(canvas);

        }

    }

    private void processInput() {

        //process here
    }

    private void update() {

        fps = 1000.0F / elapsed;

        //update here
        if(!Input.touchDown) {
            for (Particle particle : particles) {
                //handles motion
                particle.location.x += Math.round(particle.vX);
                particle.location.y += Math.round(particle.vY);
            }
        }else{
            double dx, dy, d, aX, aY, a;
            for (Particle p : particles) {
                dx = Input.tap.x - p.location.x;
                dy = Input.tap.y - p.location.y;
                d = Math.sqrt(dx*dx + dy*dy);
                if(d>AREA){
                    a = ACCEL*d;
                    aX = a*dx/d;
                    aY = a*dy/d;
                    if(aX*p.vX<0) aX*=2;
                    if(aY*p.vY<0) aY*=2;

                }else{
                    aX = 0;
                    aY = 0;
                }
                p.location.x += p.vX + aX;
                p.vX += aX;
                p.location.y += p.vY + aY;
                p.vY += aY;
            }
        }

        //handles collision
        handleCollisions();
    }

    private boolean checkProximity(Particle firstBall, Particle secondBall)
    {
        //AABB check to see if the particles are nearby, for performance purposes
        if (firstBall.location.x + firstBall.radius + secondBall.radius > secondBall.location.x
                && firstBall.location.x < secondBall.location.x + firstBall.radius + secondBall.radius
                && firstBall.location.y + firstBall.radius + secondBall.radius > secondBall.location.y
                && firstBall.location.y < secondBall.location.y + firstBall.radius + secondBall.radius)
        {
            return true;
        }
        return false;
    }

    public void handleCollisions(){
        double xDist, yDist;
        for(Particle A : particles){
            if(state == GameState.Osmosis&&A.radius<=radius&&A.location.x<=width/2)
                leftSideCount++;
            handleBorderCollision(A);
            if(state == GameState.Osmosis){
                handleMembraneCollision(A);
            }
            for(Particle B : particles){
                if(A==B) continue;
                xDist = A.location.x - B.location.x;
                yDist = A.location.y - B.location.y;
                double distSquared = xDist*xDist + yDist*yDist;
                //Check the squared distances instead of the the distances, same result, but avoids a square root.
                if(distSquared <= (A.radius + B.radius)*(A.radius + B.radius)){
                    double xVelocity = B.vX - A.vX;
                    double yVelocity = B.vY - A.vY;
                    double dotProduct = xDist*xVelocity + yDist*yVelocity;
                    //Neat vector maths, used for checking if the objects moves towards one another.
                    if(dotProduct > 0){
                        double collisionScale = dotProduct / distSquared;
                        double xCollision = xDist * collisionScale;
                        double yCollision = yDist * collisionScale;
                        //The Collision vector is the speed difference projected on the Dist vector,
                        //thus it is the component of the speed difference needed for the collision.
                        double combinedMass = A.radius + B.radius;
                        double collisionWeightA = 2 * B.radius / combinedMass;
                        double collisionWeightB = 2 * A.radius / combinedMass;
                        A.vX += collisionWeightA * xCollision;
                        A.vY += collisionWeightA * yCollision;
                        B.vX -= collisionWeightB * xCollision;
                        B.vY -= collisionWeightB * yCollision;
                    }
                }
            }
        }
    }

    private void handleBorderCollision(Particle particle)
    {
        if(particle.location.x<particle.radius) {
            particle.location.x = particle.radius + 1;
            particle.vX = -particle.vX;
        }
        else if(particle.location.x+particle.radius>width){
            particle.location.x = width-particle.radius - 1;
            particle.vX = -particle.vX;
        }
        if(particle.location.y<particle.radius) {
            particle.location.y = particle.radius + 1;
            particle.vY = -particle.vY;
        }
        else if(particle.location.y>height-particle.radius){
            particle.location.y = height-particle.radius - 1;
            particle.vY = -particle.vY;
        }
    }

    //newly added, need testing
    private void handleMembraneCollision(Particle p){
        if(p.radius>GameLoop.radius) {
            if(p.location.x>rectRight&&p.location.x-p.radius<rectRight){
                p.location.x = rectRight+p.radius+1;
                p.vX = -p.vX;
            }else if(p.location.x<rectLeft&&p.location.x+p.radius>rectLeft){
                p.location.x = rectLeft-p.radius-1;
                p.vX = -p.vX;
            }
        }
    }

    public void stop() {

        isRunning = false;

    }

    //also newly added content, need testing
    private void generateParticles(int radius, int amount)
    {
        particles.clear();
        Particle newP;
        float randVx;
        float randVy;
        int x, y;
        //randomly generate solutes
        if(Game.state == GameState.Osmosis){
            int soluteAmount = amount/2;
            int soluteRadius = radius+radius;
            //generate solutes
            for(int i=0; i<soluteAmount; i++)
            {
                randVx = rng.nextFloat()*(V_MAX-V_MIN)+V_MIN;
                randVy = rng.nextFloat()*(V_MAX-V_MIN)+V_MIN;
                x = rng.nextInt(width/2 - soluteRadius * 2 - rectWidth/2 - 1) + width/2 + soluteRadius + rectWidth/2;
                y = rng.nextInt(height - soluteRadius*2) + soluteRadius;
                newP = new Particle(randVx, randVy, new Point(x, y), soluteRadius);
                for(Particle otherP : particles)
                {
                    while(checkProximity(newP, otherP))
                    {
                        x = rng.nextInt(width/2 - soluteRadius * 2 - rectWidth/2 - 1) + width/2 + soluteRadius + rectWidth/2;
                        y = rng.nextInt(height - soluteRadius*2) + soluteRadius;
                        newP = new Particle(randVx, randVy, new Point(x, y), soluteRadius);
                    }
                }
                particles.add(newP);
            }
            //generate solvent on the left side
            for (int i = 0; i < amount - amount/2; i++) {
                randVx = rng.nextFloat() * (V_MAX - V_MIN) + V_MIN;
                randVy = rng.nextFloat() * (V_MAX - V_MIN) + V_MIN;
                x = rng.nextInt(width/2 - radius * 2 - rectWidth/2-1) + radius;
                y = rng.nextInt(height - radius * 2) + radius;
                newP = new Particle(randVx, randVy, new Point(x, y), radius);
                for (Particle otherP : particles) {
                    while (checkProximity(newP, otherP)) {
                        x = rng.nextInt(width/2 - radius * 2 - rectWidth/2) + radius;
                        y = rng.nextInt(height - radius * 2) + radius;
                        newP = new Particle(randVx, randVy, new Point(x, y), radius);
                    }
                }
                particles.add(newP);
            }
            //generate solvent on the right side
            for (int i = 0; i < amount/2; i++) {
                randVx = rng.nextFloat() * (V_MAX - V_MIN) + V_MIN;
                randVy = rng.nextFloat() * (V_MAX - V_MIN) + V_MIN;
                x = rng.nextInt(width/2 - radius * 2 - rectWidth/2 - 1) + width/2 + radius + rectWidth/2;
                y = rng.nextInt(height - radius * 2) + radius;
                newP = new Particle(randVx, randVy, new Point(x, y), radius);
                for (Particle otherP : particles) {
                    while (checkProximity(newP, otherP)) {
                        x = rng.nextInt(width/2 - radius * 2 - rectWidth/2 - 1) + width/2 + radius + rectWidth/2;
                        y = rng.nextInt(height - radius * 2) + radius;
                        newP = new Particle(randVx, randVy, new Point(x, y), radius);
                    }
                }
                particles.add(newP);
            }
        }
        else {
            //randomly generate particles
            for (int i = 0; i < amount; i++) {
                randVx = rng.nextFloat() * (V_MAX - V_MIN) + V_MIN;
                randVy = rng.nextFloat() * (V_MAX - V_MIN) + V_MIN;
                x = rng.nextInt(width - radius * 2) + radius;
                y = rng.nextInt(height - radius * 2) + radius;
                newP = new Particle(randVx, randVy, new Point(x, y), radius);
                for (Particle otherP : particles) {
                    while (checkProximity(newP, otherP)) {
                        x = rng.nextInt(width - radius * 2) + radius;
                        y = rng.nextInt(height - radius * 2) + radius;
                        newP = new Particle(randVx, randVy, new Point(x, y), radius);
                    }
                }
                particles.add(newP);
            }
        }
    }

}
