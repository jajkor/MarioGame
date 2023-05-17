package com.mariogame.game.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.mariogame.Tools.Assets;
import com.mariogame.game.MarioGame;
import com.mariogame.game.Scenes.Hud;
import com.mariogame.game.Screens.LevelScreen;
import com.mariogame.game.Sprites.Mario;

public class Brick extends InteractiveTileObject {

    public Brick(LevelScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioGame.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()) {
            setCategoryFilter(MarioGame.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            Assets.break_block.play();
        }
        Assets.bump.play();
    }

}
