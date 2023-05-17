package com.mariogame.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mariogame.Tools.WorldContactListener;
import com.mariogame.game.Scenes.Hud;
import com.mariogame.game.MarioGame;
import com.mariogame.game.Sprites.Enemies.Enemy;
import com.mariogame.game.Sprites.Items.*;
import com.mariogame.game.Sprites.Mario;
import com.mariogame.Tools.B2WorldCreator;

import com.mariogame.Tools.Assets;

import java.util.concurrent.LinkedBlockingQueue;

public class LevelScreen implements Screen {
    private MarioGame game;

    /* PlayScreen Variables */
    public OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    /* Tiled Variables */
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    /* Box2d Variables */
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public LevelScreen(MarioGame game) {
        this.game = game;

        /* PlayScreen initializations */
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(MarioGame.V_WIDTH / MarioGame.PPM, MarioGame.V_HEIGHT / MarioGame.PPM, gameCam);

        /* Tiled initializations */
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("assets/level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioGame.PPM);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        /* Create the Box2D world, setting gravity to -10 in Y */
        world = new World(new Vector2(0,-10), true);

        /* Shows collision brushes, hiding all instances removes */
        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);

        player = new Mario(this, creator.getSpawnX(), creator.getSpawnY());

        hud = new Hud(game.batch, creator.getWorldNum(), creator.getLvlNum(), player.getLives());

        world.setContactListener(new WorldContactListener());

        music = Assets.music;
        music.setLooping(true);
        music.setVolume(0.05f);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
            if (idef.type == OneUPMushroom.class) {
                items.add(new OneUPMushroom(this, idef.position.x, idef.position.y));
            }
            if (idef.type == FireFlower.class) {
                items.add(new FireFlower(this, idef.position.x, idef.position.y));
            }
        }
    }

    public boolean gameOver() {
        if ((player.currentState == Mario.State.DEAD || hud.getWorldTimer() == 0) && player.getStateTime() > 3) {
            return true;
        }
        return false;
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public void boundCamera() {
        /* Keeps camera within map bounds from left side, keeps position.x within it's initial value */
        if (gameCam.position.x < MarioGame.V_WIDTH / MarioGame.PPM / 2) {
            gameCam.position.x = MarioGame.V_WIDTH / MarioGame.PPM / 2;
        } else if (gameCam.position.x > (creator.getMapWidth() * creator.getTileWidth() / MarioGame.PPM) - MarioGame.V_WIDTH / MarioGame.PPM / 2) {
            gameCam.position.x = creator.getMapWidth() * creator.getTileWidth() / MarioGame.PPM - MarioGame.V_WIDTH / MarioGame.PPM / 2;
        }
        if (gameCam.position.y < MarioGame.V_HEIGHT / MarioGame.PPM / 2) {
            gameCam.position.y = MarioGame.V_HEIGHT / MarioGame.PPM / 2;
        } else if (gameCam.position.y > (creator.getMapHeight() * creator.getTileHeight() / MarioGame.PPM) - MarioGame.V_HEIGHT / MarioGame.PPM / 2) {
            gameCam.position.y = creator.getMapHeight() * creator.getTileHeight() / MarioGame.PPM - MarioGame.V_HEIGHT / MarioGame.PPM / 2;
        }
    }

    @Override
    public void show() {

    }

    public void update(float dt) {
        player.handleInput(dt);

        world.step(1 / 60f, 6, 2);

        /* Continually renders player graphics */
        player.update(dt);
        handleSpawningItems();

        /* Activates enemies within certain distance */
        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 400 / MarioGame.PPM) {
                enemy.b2body.setActive(true);
            }
        }

        /* Continually renders item graphics */
        for(Item item : items) {
            item.update(dt);
        }

        /* Continually renders the hud */
        hud.update(dt, player.getLives());

        /* Freezes game camera after death */
        if (player.currentState != Mario.State.DEAD) {
            gameCam.position.x = player.b2body.getPosition().x;
        }

        /* Update's Game Camera position */
        boundCamera();
        gameCam.update();

        /* Only renders tiled graphics within the camera */
        renderer.setView(gameCam);
    }

    @Override
    public void render(float dt) {
        update(dt);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /* Renders the game map */
        renderer.render();

        /* Draws debug outlines to the screen */
        b2dr.render(world, gameCam.combined);

        /* Draws entities to the screen */
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getEnemies()) {
            enemy.draw(game.batch);
        }
        for (Item item : items) {
            item.draw(game.batch);
        }
        game.batch.end();

        /* Set our batch to draw what the hud camera sees */
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        /* Changes screen after game over */
        if (gameOver()) {
            game.setScreen(new GameOverScreen(game, player.getLives()));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

}
