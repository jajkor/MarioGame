package com.mariogame.Tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {
    private final AssetManager assets;
    public static TextureAtlas atlas;

    /*private static TextureRegion marioStand;
    private static TextureRegion bigMarioStand;
    private static TextureRegion marioJump;
    private static TextureRegion bigMarioJump;
    private static TextureRegion marioDead;

    private static Animation<TextureRegion> marioRun;
    private static Animation<TextureRegion> bigMarioRun;
    private static Animation<TextureRegion> growMario;*/

    public static Music music;
    public static Sound game_over;

    public static Sound super_jump;
    public static Sound little_jump;
    public static Sound mario_dies;
    public static Sound stomp;

    public static Sound break_block;
    public static Sound bump;
    public static Sound coin;

    public static Sound powerup_spawn;
    public static Sound power_up;
    public static Sound power_down;
    public static Sound one_up;
    public static Sound fireball;

    public Assets() {
        assets = new AssetManager();
        assets.load("assets/Mario_and_Stuff.atlas", TextureAtlas.class);

        assets.load("assets/audio/music/mario_music.ogg", Music.class);

        assets.load("assets/audio/sounds/mariojumpsuper.wav", Sound.class);
        assets.load("assets/audio/sounds/mariojumplittle.wav", Sound.class);
        assets.load("assets/audio/sounds/mariodie.wav", Sound.class);
        assets.load("assets/audio/sounds/stomp.wav", Sound.class);

        assets.load("assets/audio/sounds/breakblock.wav", Sound.class);
        assets.load("assets/audio/sounds/bump.wav", Sound.class);
        assets.load("assets/audio/sounds/coin.wav", Sound.class);

        assets.load("assets/audio/sounds/powerup_spawn.wav", Sound.class);
        assets.load("assets/audio/sounds/power_up.wav", Sound.class);
        assets.load("assets/audio/sounds/powerdown.wav", Sound.class);
        assets.load("assets/audio/sounds/1-up.wav", Sound.class);
        assets.load("assets/audio/sounds/fireball.wav", Sound.class);

        assets.load("assets/audio/sounds/gameover.wav", Sound.class);

        assets.finishLoading();
    }

    public void finishLoading() {
        atlas = assets.get("assets/Mario_and_Stuff.atlas", TextureAtlas.class);

        music = assets.get("assets/audio/music/mario_music.ogg", Music.class);

        super_jump = assets.get("assets/audio/sounds/mariojumpsuper.wav", Sound.class);
        little_jump = assets.get("assets/audio/sounds/mariojumplittle.wav", Sound.class);
        mario_dies = assets.get("assets/audio/sounds/mariodie.wav", Sound.class);
        stomp = assets.get("assets/audio/sounds/stomp.wav", Sound.class);
        break_block = assets.get("assets/audio/sounds/breakblock.wav", Sound.class);
        bump = assets.get("assets/audio/sounds/bump.wav", Sound.class);
        coin = assets.get("assets/audio/sounds/coin.wav", Sound.class);
        powerup_spawn = assets.get("assets/audio/sounds/powerup_spawn.wav", Sound.class);
        power_up = assets.get("assets/audio/sounds/power_up.wav", Sound.class);
        power_down = assets.get("assets/audio/sounds/powerdown.wav", Sound.class);
        one_up = assets.get("assets/audio/sounds/1-up.wav", Sound.class);
        fireball = assets.get("assets/audio/sounds/fireball.wav", Sound.class);
        game_over = assets.get("assets/audio/sounds/gameover.wav", Sound.class);
    }

    public void dispose() {
        assets.dispose();
    }

}
