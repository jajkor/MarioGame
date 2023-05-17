package com.mariogame.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mariogame.Tools.Assets;
import com.mariogame.game.MarioGame;

public class GameOverScreen implements Screen {
    private MarioGame game;

    private Viewport viewport;
    private Stage stage;

    private Label playAgainLabel;
    private Label livesRemaining;
    private Label gameOverLabel;

    private int lives;

    public GameOverScreen(MarioGame game, int lives) {
        this.game = game;
        this.lives = lives;

        viewport = new FitViewport(MarioGame.V_WIDTH, MarioGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        if (lives == 0) {
            gameOverLabel = new Label("GAME OVER", font);
            Assets.game_over.play();

            table.add(gameOverLabel).expandX();
        } else {
            livesRemaining = new Label("Lives Remaining: " + lives, font);
            playAgainLabel = new Label("Click to Play Again", font);

            table.add(livesRemaining).expandX();
            table.row();
            table.add(playAgainLabel).expandX().padTop(10f);
        }
        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isTouched() && lives != 0) {
            game.setScreen(new LevelScreen(game));
            dispose();
        } else if (Gdx.input.isTouched()) {
            System.exit(0);
        }
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
    public void dispose() {
        stage.dispose();
    }
}
