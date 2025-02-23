package ru.vatolin.time_to_fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.ListIterator;

public class GameScreen implements Screen {
    //Screen
    private Camera camera;
    private Viewport viewport;

    //Graphics
    private SpriteBatch spriteBatch;
    private TextureAtlas textureAtlas;
    private TextureRegion[] backgrounds;
    private TextureRegion playerShipTextureRegion;
    private TextureRegion playerShieldTextureRegion;
    private TextureRegion playerLaserTextureRegion;
    private TextureRegion enemyShipTextureRegion;
    private TextureRegion enemyShieldTextureRegion;
    private TextureRegion enemyLaserTextureRegion;

    //timing
    //private int backgroundOffSet;
    private float[] backgroundOffSets = {0, 0, 0, 0};
    private float backgroundMaxScrollingSpeed;

    //worldParameters
    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD = 0.5f;

    //gameObjects
    private Ship playerShip;
    private Ship enemyShip;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;

    public GameScreen() {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //setup texture atlas
        textureAtlas = new TextureAtlas("images.atlas");

        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("Starscape00");
        backgrounds[1] = textureAtlas.findRegion("Starscape01");
        backgrounds[2] = textureAtlas.findRegion("Starscape02");
        backgrounds[3] = textureAtlas.findRegion("Starscape03");

        backgroundMaxScrollingSpeed = (float) (WORLD_HEIGHT) / 4;

        //textures
        playerShipTextureRegion = textureAtlas.findRegion("playerShip1_blue");
        playerShieldTextureRegion = textureAtlas.findRegion("shield1");
        playerLaserTextureRegion = textureAtlas.findRegion("laserBlue04");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyBlack3");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield2");
        enemyShieldTextureRegion.flip(false, true);
        enemyLaserTextureRegion = textureAtlas.findRegion("laserRed03");

        //setup game objects
        playerShip = new PlayerShip(48, 3, WORLD_WIDTH / 2, WORLD_HEIGHT / 4,
            10, 10, 0.4f, 4, 45, 0.5f,
            playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);

        enemyShip = new EnemyShip(2, 1, WORLD_WIDTH / 2, WORLD_HEIGHT * 3/4,
            10, 10, 0.3f, 4, 40, 0.8f,
            enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion);

        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();

        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render(float deltaTime) {
        spriteBatch.begin();

        detectInput(deltaTime);

        playerShip.update(deltaTime);
        enemyShip.update(deltaTime);

        //Scrolling background
        renderBackground(deltaTime);

        //enemy ships
        enemyShip.draw(spriteBatch);

        //player ships
        playerShip.draw(spriteBatch);

        //lasers

        //create lasers
        renderLasers(deltaTime);

        //detect collisions between lasers and ships
        detectCollisions();

        //explosions
        renderExplosions(deltaTime);

        spriteBatch.end();
    }

    private void detectInput(float deltaTime) {
        //keyboard input
        float leftLimit, rightLimit, upLimit, downLimit;

        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = WORLD_HEIGHT / 2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0) {
            playerShip.translate(Math.min(playerShip.movementSpeed * deltaTime, rightLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0) {
            playerShip.translate(0f, Math.min(playerShip.movementSpeed * deltaTime, upLimit));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0) {
            playerShip.translate(Math.max(-playerShip.movementSpeed * deltaTime, leftLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0) {
            playerShip.translate(0f, Math.max(-playerShip.movementSpeed * deltaTime, downLimit));
        }

        //touch input
        if (Gdx.input.isTouched()) {
            //get the screen position of the touch
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            //convert to world position
            Vector2 touchPoint = new Vector2(xTouchPixels, yTouchPixels);
            touchPoint = viewport.unproject(touchPoint);

            //calculate the x and y differences
            Vector2 playerShipCenter = new Vector2(
                playerShip.boundingBox.x + playerShip.boundingBox.width / 2,
                playerShip.boundingBox.y + playerShip.boundingBox.height / 2);

            float touchDistance = touchPoint.dst(playerShipCenter);

            if (touchDistance > TOUCH_MOVEMENT_THRESHOLD) {
                float xTouchDifference = touchPoint.x - playerShipCenter.x;
                float yTouchDifference = touchPoint.y - playerShipCenter.y;

                //scale to the maximum speed of the ship
                float xMove = xTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;
                float yMove = yTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;

                if (xMove > 0) xMove = Math.min(xMove, rightLimit);
                else xMove = Math.max(xMove, leftLimit);

                if (yMove > 0) yMove = Math.min(yMove, upLimit);
                else yMove = Math.max(yMove, downLimit);

                playerShip.translate(xMove, yMove);
            }
        }
    }

    private void renderBackground(float deltaTime) {
        backgroundOffSets[0] += deltaTime * backgroundMaxScrollingSpeed / 8;
        backgroundOffSets[1] += deltaTime * backgroundMaxScrollingSpeed / 4;
        backgroundOffSets[2] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffSets[3] += deltaTime * backgroundMaxScrollingSpeed;

        for (int layer = 0; layer < backgroundOffSets.length; layer++) {
            if (backgroundOffSets[layer] > WORLD_HEIGHT) {
                backgroundOffSets[layer] = 0;
            }
            spriteBatch.draw(backgrounds[layer], 0, -backgroundOffSets[layer], WORLD_WIDTH, WORLD_HEIGHT);
            spriteBatch.draw(backgrounds[layer], 0, -backgroundOffSets[layer] + WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);
        }
    }

    private void renderLasers(float deltaTime) {
        //player lasers
        if (playerShip.canFireLasers()) {
            Laser[] lasers = playerShip.fireLasers();
            for (Laser laser : lasers) {
                playerLaserList.add(laser);
            }
        }
        //enemy lasers
        if (enemyShip.canFireLasers()) {
            Laser[] lasers = enemyShip.fireLasers();
            for (Laser laser : lasers) {
                enemyLaserList.add(laser);
            }
        }

        //draw lasers
        //remove lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(spriteBatch);
            laser.boundingBox.y += laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y > WORLD_HEIGHT) {
                iterator.remove();
            }
        }

        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(spriteBatch);
            laser.boundingBox.y += laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
    }

    private void renderExplosions(float deltaTime) {

    }

    private void detectCollisions() {
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            if (enemyShip.intersects(laser.boundingBox)) {
                enemyShip.hit(laser);
                iterator.remove();
            }
        }

        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            if (playerShip.intersects(laser.boundingBox)) {
                playerShip.hit(laser);
                iterator.remove();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void dispose() {

    }
}
