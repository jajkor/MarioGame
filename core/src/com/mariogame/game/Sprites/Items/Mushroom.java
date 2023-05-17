package com.mariogame.game.Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mariogame.Tools.Assets;
import com.mariogame.game.MarioGame;
import com.mariogame.game.Screens.LevelScreen;
import com.mariogame.game.Sprites.Mario;

public class Mushroom extends Item {
    public Mushroom(LevelScreen screen, float x, float y) {
        super(screen, x, y);

        setRegion(Assets.atlas.findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
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
        mario.grow();
    }

    public void update(float dt) {
        super.update(dt);
        if (!destroyed) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2 + 1 / MarioGame.PPM);
            velocity.y = body.getLinearVelocity().y;
            body.setLinearVelocity(velocity);
        }
    }

}
