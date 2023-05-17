package com.mariogame.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mariogame.game.MarioGame;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private int worldTimer;
    private float timeCount;
    private static int score;

    private BitmapFont font;
    private Label marioLabel;
    private Label worldLabel;
    private Label timeLabel;
    private Label livesLabel;

    private static Label scoreLabel;
    private Label levelLabel;
    private Label countDownLabel;
    private static Label livesNumLabel;

    public Hud(SpriteBatch sb, String levelNum, String worldNum, int lives) {
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(MarioGame.V_WIDTH, MarioGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);
        font = new BitmapFont();

        Table table = new Table();
        table.top();
        table.setFillParent(true);// Sets table to size of stage

        /* Creates Hud Labels */
        marioLabel = new Label("MARIO", new Label.LabelStyle(font, Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(font, Color.WHITE));
        levelLabel = new Label(worldNum + " - " + levelNum, new Label.LabelStyle(font, Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(font, Color.WHITE));
        countDownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(font, Color.WHITE));
        livesLabel = new Label("LIVES", new Label.LabelStyle(font, Color.WHITE));
        livesNumLabel = new Label(String.format("%03d", lives), new Label.LabelStyle(font, Color.WHITE));

        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.add(livesLabel).expandX().padTop(10);
        table.row();

        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countDownLabel).expandX();
        table.add(livesNumLabel).expandX();

        stage.addActor(table);
    }

    public void update(float dt, int lives) {
        timeCount += dt;
        if (timeCount >= 1) {
            if (worldTimer != 0) {
                worldTimer--;
            }
            countDownLabel.setText(String.format("%03d", worldTimer));
            livesNumLabel.setText(String.format("%03d", lives));
            timeCount = 0;
        }
    }

    public static void addScore(int value) {
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    public float getWorldTimer() {
        return worldTimer;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
