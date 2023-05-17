package com.mariogame.game.Sprites.Items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.mariogame.Tools.Assets;
import com.mariogame.game.MarioGame;
import com.mariogame.game.Screens.LevelScreen;
import com.mariogame.game.Sprites.Mario;

public class FireFlower extends Item {
    private final Animation<TextureRegion> fireFlower;
    Array<TextureRegion> frames;
    private float stateTime;

    public FireFlower(LevelScreen screen, float x, float y) {
        super(screen, x, y);

        frames = new Array<TextureRegion>();
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(Assets.atlas.findRegion("fire_flower"),  i * 16, 0, 16, 16));
        }
        stateTime = 0;
        fireFlower = new Animation(0.4f, frames);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioGame.PPM);
        fdef.filter.categoryBits = MarioGame.ITEM_BIT; // What is this fixture
        fdef.filter.maskBits = MarioGame.MARIO_BIT | MarioGame.OBJECT_BIT | MarioGame.GROUND_BIT | MarioGame.COIN_BIT | MarioGame.BRICK_BIT; // What can this fixture collide with

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void useItem(Mario mario) {
        destroy();
        mario.flowerUp();
    }

    public void update(float dt) {
        super.update(dt);
        stateTime += dt;
        if (!destroyed) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2 + 1 / MarioGame.PPM);
            setRegion(fireFlower.getKeyFrame(stateTime, true));
        }
    }

}
