package ru.vatolin.time_to_fly;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class Ship {
    //ship characteristic
    float movementSpeed;
    int shield;

    //position and dimensions
    Rectangle boundingBox;

    //graphics
    TextureRegion shipTextureRegion;
    TextureRegion shieldTextureRegion;
    TextureRegion laserTextureRegion;

    //laser information
    float laserWidth;
    float laserHeight;
    float laserMovementSpeed;
    float timeBetweenShots;
    float timeSinceLastShot = 0;

    public Ship(float movementSpeed, int shield, float xPosition, float yPosition, float width, float height,
                float laserWidth, float laserHeight, float laserMovementSpeed, float timeBetweenShots,
                TextureRegion shipTextureRegion,
                TextureRegion shieldTextureRegion,
                TextureRegion laserTextureRegion) {

        this.movementSpeed = movementSpeed;
        this.shield = shield;
        this.boundingBox = new Rectangle(xPosition - width / 2, yPosition - height / 2, width, height);
        this.laserWidth = laserWidth;
        this.laserHeight = laserHeight;
        this.laserMovementSpeed = laserMovementSpeed;
        this.timeBetweenShots = timeBetweenShots;
        this.shipTextureRegion = shipTextureRegion;
        this.shieldTextureRegion = shieldTextureRegion;
        this.laserTextureRegion = laserTextureRegion;
    }

    public void update(float deltaTime) {
        timeSinceLastShot += deltaTime;
    }

    public boolean canFireLasers() {
        return (timeSinceLastShot - timeBetweenShots >= 0);
    }

    public abstract Laser[] fireLasers();

    public boolean intersects(Rectangle otherRectangle) {
        return boundingBox.overlaps(otherRectangle);
    }

    public boolean hitAndCheckDestroyed(Laser laser) {
        if (shield > 0) {
            shield--;
            return false;
        }
        return true;
    }

    public void translate(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, boundingBox.y + yChange);
    }

    public void draw(Batch batch) {
        batch.draw(shipTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        if (shield > 0) {
            batch.draw(shieldTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        }
    }
}
