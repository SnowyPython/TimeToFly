package ru.vatolin.time_to_fly;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerShip extends Ship {
    public PlayerShip(float movementSpeed, int shield, float xPosition, float yPosition, float width, float height,
                      float laserWidth, float laserHeight, float laserMovementSpeed, float timeBetweenShots,
                      TextureRegion shipTextureRegion,
                      TextureRegion shieldTextureRegion,
                      TextureRegion laserTextureRegion) {

        super(movementSpeed, shield, xPosition, yPosition, width, height, laserWidth, laserHeight,
            laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[2];
        laser[0] = new Laser(boundingBox.x + boundingBox.width * 0.07f, boundingBox.y + boundingBox.height * 0.45f,
            laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);
        laser[1] = new Laser(boundingBox.x + boundingBox.width * 0.93f, boundingBox.y + boundingBox.height * 0.45f,
            laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);

        timeSinceLastShot = 0;

        return laser;
    }
}
