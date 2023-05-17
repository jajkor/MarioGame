package com.mariogame.game.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.mariogame.game.MarioGame;
import com.mariogame.game.Scenes.Hud;
import com.mariogame.game.Screens.LevelScreen;
import com.mariogame.game.Sprites.Items.FireFlower;
import com.mariogame.game.Sprites.Items.ItemDef;
import com.mariogame.game.Sprites.Items.Mushroom;
import com.mariogame.game.Sprites.Items.OneUPMushroom;
import com.mariogame.game.Sprites.Mario;

import com.mariogame.Tools.Assets;

import java.util.Random;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28; // Tiled ID number for the blank coin in tileset

    public Coin(LevelScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioGame.COIN_BIT);
    }

    /* Method decides what spawns from coin after head collision depending on mario's state */
    @Override
    public void onHeadHit(Mario mario) {
        float start = 1;
        float end = 100;
        float random = new Random().nextFloat();
        float result = start + (random * (end - start));
        System.out.println(result);

        if (getCell().getTile().getId() == BLANK_COIN) {
            Assets.bump.play();
        } else {
            if (object.getProperties().containsKey("item") && !mario.isBig()) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioGame.PPM), Mushroom.class));
                Assets.powerup_spawn.play();
            } else if ((object.getProperties().containsKey("item") && mario.isBig()) && result <= 10) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioGame.PPM), OneUPMushroom.class));
                Assets.powerup_spawn.play();
            } else if ((object.getProperties().containsKey("item") && mario.isBig()) && result >= 10) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioGame.PPM), FireFlower.class));
                Assets.powerup_spawn.play();
            } else {
                Assets.coin.play();
                Hud.addScore(200);
            }
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));
    }

}
