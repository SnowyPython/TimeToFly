package ru.vatolin.time_to_fly;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Laser {
    //position and dimensions
    Rectangle boundingBox;

    //characteristics
    float movementSpeed;

    //graphics
    TextureRegion textureRegion;

    public Laser(float xCenter, float yCenter, float width, float height, float movementSpeed, TextureRegion textureRegion) {
        this.boundingBox = new Rectangle(xCenter - width / 2, yCenter, width, height);
        this.movementSpeed = movementSpeed;
        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch) {
        batch.draw(textureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }
}
