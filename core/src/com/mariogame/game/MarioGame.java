package com.mariogame.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MarioGame extends Game {
    public SpriteBatch batch;
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

}
