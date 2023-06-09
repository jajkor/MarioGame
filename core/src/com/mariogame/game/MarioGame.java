package com.mariogame.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mariogame.Tools.Assets;
import com.mariogame.game.Screens.LevelScreen;

public class MarioGame extends Game {
    /* Renders things */
    public SpriteBatch batch;

    /* Asset Manager */
    private Assets assets;

    /* Viewport sizes */
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;

    /* Pixels Per Meter, Used for scaling */
    public static final float PPM = 100;

    /* Box2D Collision Bits */
    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;
    public static final short MARIO_HEAD_BIT = 512;
    public static final short PROJECTILE_BIT = 1024;

    @Override
    public void create() {
        batch = new SpriteBatch();

        assets = new Assets();
        assets.finishLoading();

        setScreen(new LevelScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
        batch.dispose();
    }

}
