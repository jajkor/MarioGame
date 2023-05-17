package com.mariogame.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mariogame.game.MarioGame;
import com.mariogame.game.Screens.LevelScreen;
import com.mariogame.game.Sprites.Enemies.Enemy;
import com.mariogame.game.Sprites.Enemies.Turtle;
import com.mariogame.game.Sprites.TileObjects.Brick;
import com.mariogame.game.Sprites.TileObjects.Coin;
import com.mariogame.game.Sprites.Enemies.Goomba;

public class B2WorldCreator {
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;

    private float spawnX;
    private float spawnY;

    private MapProperties mapProp;
    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    private String worldNum;
    private String lvlNum;

    public B2WorldCreator(LevelScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        /* Storing Tiled Map Info, like world & level numbers */
        mapProp = map.getProperties();
        mapWidth = mapProp.get("width", Integer.class);
        mapHeight = mapProp.get("height", Integer.class);
        tileWidth = mapProp.get("tilewidth", Integer.class);
        tileHeight = mapProp.get("tileheight", Integer.class);
        worldNum = mapProp.get("World", String.class);
        lvlNum = mapProp.get("Level", String.class);

        /* Player spawn location */
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            spawnX = rect.getX();
            spawnY = rect.getY();
        }

        /* Create Ground, bodies / fixtures */
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioGame.PPM, (rect.getY() + rect.getHeight() / 2) / MarioGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioGame.PPM, rect.getHeight() / 2 / MarioGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }


        /* Create Pipe, bodies / fixtures */
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioGame.PPM, (rect.getY() + rect.getHeight() / 2) / MarioGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioGame.PPM, rect.getHeight() / 2 / MarioGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioGame.OBJECT_BIT;
            body.createFixture(fdef);
        }

        /* Create Brick, bodies / fixtures */
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            new Brick(screen, object);
        }

        /* Create Coin bodies, / fixtures */
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            new Coin(screen, object);
        }

        /* Create Goombas */
        goombas = new Array<Goomba>();
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rect.getX() / MarioGame.PPM, rect.getY() / MarioGame.PPM));
        }

        /* Create Turtles */
        turtles = new Array<Turtle>();
        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(screen, rect.getX() / MarioGame.PPM, rect.getY() / MarioGame.PPM));
        }
    }

    public String getWorldNum() {
        return worldNum;
    }

    public String getLvlNum() {
        return lvlNum;
    }

    public float getSpawnX() {
        return spawnX;
    }

    public float getSpawnY() {
        return spawnY;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }

    public Array<Turtle> getTurtles() {
        return turtles;
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }
}
