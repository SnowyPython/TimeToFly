package ru.vatolin.time_to_fly;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class EnemyShip extends Ship {
    Vector2 directionVector;
    float timeSinceLastDirectionChange = 0;
    float directionChangeFrequency = 0.75f;
    public EnemyShip(float movementSpeed, int shield, float xPosition, float yPosition, float width, float height,
                     float laserWidth, float laserHeight, float laserMovementSpeed, float timeBetweenShots,
                     TextureRegion shipTextureRegion,
                     TextureRegion shieldTextureRegion,
                     TextureRegion laserTextureRegion) {

        super(movementSpeed, shield, xPosition, yPosition, width, height, laserWidth, laserHeight,
            laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);

        directionVector = new Vector2(0, -1);
    }

    public Vector2 getDirectionVector() {
        return directionVector;
    }

    private void randomizeDirectionVector() {
        double bearing = TimeToFlyGame.random.nextDouble()*6.283125; //0 to 2*PI
        directionVector.x = (float) Math.sin(bearing);
        directionVector.y = (float) Math.cos(bearing);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        timeSinceLastDirectionChange += deltaTime;
        if (timeSinceLastDirectionChange > directionChangeFrequency) {
            randomizeDirectionVector();
            timeSinceLastDirectionChange -= directionChangeFrequency;
        }
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[2];
        laser[0] = new Laser(boundingBox.x + boundingBox.width * 0.18f, boundingBox.y,
            laserWidth, laserHeight, -laserMovementSpeed, laserTextureRegion);
        laser[1] = new Laser(boundingBox.x + boundingBox.width * 0.82f, boundingBox.y,
            laserWidth, laserHeight, -laserMovementSpeed, laserTextureRegion);

        timeSinceLastShot = 0;

        return laser;
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(shipTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        if (shield > 0) {
            batch.draw(shieldTextureRegion, boundingBox.x, boundingBox.y - boundingBox.height * 0.2f, boundingBox.width, boundingBox.height);
        }
    }
}
