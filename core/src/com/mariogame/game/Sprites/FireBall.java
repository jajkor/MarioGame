package com.mariogame.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mariogame.Tools.Assets;
import com.mariogame.game.MarioGame;
import com.mariogame.game.Screens.LevelScreen;
import com.mariogame.game.Sprites.Enemies.Turtle;

public class FireBall extends Sprite {
    public enum State {BOUNCING, EXPLODING};
    public FireBall.State currentState;
    public FireBall.State previousState;

    private final Animation<TextureRegion> fireAnimation;
    private final Animation<TextureRegion> fireHitAnimation;
    Array<TextureRegion> frames;
    private float stateTime;
    float destroyTimer;

    private boolean runningRight;
    private boolean destroyed;
    private boolean setToDestroy;
    private World world;
    private Body body;

    public FireBall(LevelScreen screen, float x, float y, boolean runningRight) {
        this.runningRight = runningRight;
        this.world = screen.getWorld();

        frames = new Array<TextureRegion>();
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(Assets.atlas.findRegion("fireball"),  i * 8, 0, 8, 8));
        }
        fireAnimation = new Animation(0.2f, frames);
        frames.clear();

        for (int i = 0; i < 3; i++) {
            frames.add(new TextureRegion(Assets.atlas.findRegion("fireballhit"),  i * 16, 0, 16, 16));
        }
        fireHitAnimation = new Animation(0.4f, frames);
        frames.clear();

        stateTime = 0;
        destroyTimer = 3;
        currentState = previousState = FireBall.State.BOUNCING;

        setBounds(x, y, 8 / MarioGame.PPM, 8 / MarioGame.PPM);
        defineFireBall();
    }

    public void defineFireBall() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);
        body.setBullet(true);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / MarioGame.PPM);
        fdef.filter.categoryBits = MarioGame.PROJECTILE_BIT; // What is this fixture
        fdef.filter.maskBits = MarioGame.ENEMY_HEAD_BIT | MarioGame.ENEMY_BIT | MarioGame.OBJECT_BIT | MarioGame.GROUND_BIT | MarioGame.COIN_BIT | MarioGame.BRICK_BIT; // What can this fixture collide with

        fdef.shape = shape;
        fdef.friction = 0f;
        fdef.restitution = 1f;
        body.createFixture(fdef).setUserData(this);

        body.setLinearVelocity(new Vector2(runningRight ? 2 : -2, 2f));
        Assets.fireball.play();
    }

    /*public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;

        switch (currentState){
            case EXPLODING:
                region = fireHitAnimation.getKeyFrame(stateTime);
                break;
            case BOUNCING:
            default:
                region = fireAnimation.getKeyFrame(stateTime, true);
                break;
        }

        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        System.out.println(stateTime);
        if ((stateTime > 3 || setToDestroy) && !destroyed) {
            body.setType(BodyDef.BodyType.StaticBody);
            return State.EXPLODING;
        } else {
            return State.BOUNCING;
        }
    }*/

    public void update(float dt) {
        stateTime += dt;
        destroyTimer -= dt;
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRegion(fireAnimation.getKeyFrame(stateTime, true));
/*        setRegion(getFrame(dt));
        if (currentState == State.EXPLODING && fireHitAnimation.isAnimationFinished(stateTime)) {
            world.destroyBody(body);
            destroyed = true;
        }*/
        
        // It may also be beneficial to move the destroyBody call outside of the isAnimationFinished call
        // Then add another boolean isAnimationFinished that will fire to stop the rendering of the animation
        if (stateTime > 1 && !destroyed) {
            setToDestroy();
        }
        if (setToDestroy && !destroyed) { 
            body.setType(BodyDef.BodyType.StaticBody);
            setRegion(fireHitAnimation.getKeyFrame(stateTime));
            world.destroyBody(body);
            if (fireHitAnimation.isAnimationFinished(stateTime)) {
                destroyed = true;
            }
        }
        if (body.getLinearVelocity().y > 2f) {
            body.setLinearVelocity(body.getLinearVelocity().x, 2f);
        }
    }

    public void setToDestroy(){
        setToDestroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }

    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
        }
    }

}
