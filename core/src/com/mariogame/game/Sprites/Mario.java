package com.mariogame.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mariogame.Tools.Assets;
import com.mariogame.game.MarioGame;
import com.mariogame.game.Scenes.Hud;
import com.mariogame.game.Screens.LevelScreen;
import com.mariogame.game.Sprites.Enemies.Enemy;
import com.mariogame.game.Sprites.Enemies.Turtle;

public class Mario extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD, BIG }
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private TextureRegion bigMarioStand;
    private TextureRegion marioJump;
    private TextureRegion bigMarioJump;
    private TextureRegion marioDead;

    private Animation<TextureRegion> marioRun;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;

    private float stateTime;

    private boolean runningRight;
    private boolean marioIsBig;
    private boolean marioIsDead;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToReDefineMario;
    private boolean marioHasFlower;

    private static int lives = 3;
    private float spawnX;
    private float spawnY;

    private LevelScreen screen;

    public BodyDef bdef;
    private Array<FireBall> fireballs;

    public Mario(LevelScreen screen, float spawnX, float spawnY) {
        this.world = screen.getWorld();
        this.screen = screen;

        this.spawnX = spawnX;
        this.spawnY = spawnY;

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTime = 0;
        runningRight = true;
        marioHasFlower = false;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(Assets.atlas.findRegion("little_mario"), (i * 16), 0, 16, 16));
        }
        marioRun = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(Assets.atlas.findRegion("big_mario"), (i * 16), 0, 16, 32));
        }
        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();

        frames.add(new TextureRegion(Assets.atlas.findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(Assets.atlas.findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(Assets.atlas.findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(Assets.atlas.findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<TextureRegion>(0.2f, frames);

        marioJump = new TextureRegion(Assets.atlas.findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(Assets.atlas.findRegion("big_mario"), 80, 0, 16, 32);

        marioStand = new TextureRegion(Assets.atlas.findRegion("little_mario"), 0,0, 16, 16);
        bigMarioStand = new TextureRegion(Assets.atlas.findRegion("big_mario"), 0, 0, 16, 32);

        marioDead = new TextureRegion(Assets.atlas.findRegion("little_mario"), 96, 0, 16, 16);

        defineMario();

        setBounds(0, 0, 16 / MarioGame.PPM, 16 / MarioGame.PPM);
        setRegion(marioStand);

        fireballs = new Array<FireBall>();
    }

    public void handleInput(float dt) {
        if (currentState != Mario.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) && (currentState != Mario.State.JUMPING && currentState != Mario.State.FALLING)) {
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
                if (marioIsBig) {
                    Assets.super_jump.play();
                } else {
                    Assets.little_jump.play();
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) && b2body.getLinearVelocity().x <= 2) {
                b2body.applyLinearImpulse(new Vector2(0.075f, 0), b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) && b2body.getLinearVelocity().x >= -2) {
                b2body.applyLinearImpulse(new Vector2(-0.075f, 0), b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) && b2body.getLinearVelocity().x >= -2) {
                b2body.applyLinearImpulse(new Vector2(-0.075f, 0), b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && marioHasFlower == true) {
                fireAttack();
            }
        }
    }

    public int getLives() {
        return lives;
    }

    public static void addLives() {
        lives++;
    }

    public static void loseLives() {
        lives--;
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch(currentState) {
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTime);
                if (growMario.isAnimationFinished(stateTime)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTime, true) : marioRun.getKeyFrame(stateTime, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }

        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if (marioIsDead) {
            return State.DEAD;
        } else if (runGrowAnimation) {
            return State.GROWING;
        } else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
            return State.JUMPING;
        } else if (b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else {
            return State.STANDING;
        }
    }

    public void fireAttack() {
        fireballs.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false));
    }

    public void grow() {
        if (!marioIsBig) {
            runGrowAnimation = true;
            marioIsBig = true;
            Hud.addScore(1000);
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
            Assets.power_up.play();
        }
    }

    public void marioDie() {
        Assets.music.stop();
        Assets.mario_dies.play();
        marioIsDead = true;
        loseLives(); // Lose 1 life after hit
        Filter filter = new Filter();
        filter.maskBits = MarioGame.NOTHING_BIT;
        for (Fixture fixture : b2body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
    }

    public void flowerUp() {
        marioHasFlower = true;
        Hud.addScore(1000);
        Assets.power_up.play();
    }

    public boolean isRunningRight() {
        return runningRight;
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public boolean isDead() {
        return marioIsDead;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle)enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle)enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else {
            if(marioIsBig) {
                marioIsBig = false;
                marioHasFlower = false;
                timeToReDefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                Assets.power_down.play();
            } else {
                marioDie();
            }
        }
    }

    public void update(float dt) {
        if (marioIsBig) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 5 / MarioGame.PPM);
        } else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 + 1 / MarioGame.PPM);
        }
        setRegion(getFrame(dt));
        if (timeToDefineBigMario) {
            defineBigMario();
        }
        if (timeToReDefineMario) {
            reDefineMario();
        }
        for(FireBall  ball : fireballs) {
            if (!ball.isDestroyed()) {
                ball.update(dt);
            }
        }
    }

    public void defineBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MarioGame.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioGame.PPM);
        fdef.filter.categoryBits = MarioGame.MARIO_BIT;
        fdef.filter.maskBits = MarioGame.GROUND_BIT | MarioGame.COIN_BIT | MarioGame.BRICK_BIT | MarioGame.ENEMY_BIT | MarioGame.OBJECT_BIT | MarioGame.ENEMY_HEAD_BIT | MarioGame.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioGame.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioGame.PPM, 7 / MarioGame.PPM), new Vector2(2 / MarioGame.PPM, 7 / MarioGame.PPM));
        fdef.filter.categoryBits = MarioGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(spawnX / MarioGame.PPM, spawnY / MarioGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioGame.PPM);
        fdef.filter.categoryBits = MarioGame.MARIO_BIT;
        fdef.filter.maskBits = MarioGame.GROUND_BIT | MarioGame.COIN_BIT | MarioGame.BRICK_BIT | MarioGame.ENEMY_BIT | MarioGame.OBJECT_BIT | MarioGame.ENEMY_HEAD_BIT | MarioGame.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioGame.PPM, 7 / MarioGame.PPM), new Vector2(2 / MarioGame.PPM, 7 / MarioGame.PPM));
        fdef.filter.categoryBits = MarioGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public void reDefineMario() {
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioGame.PPM);
        fdef.filter.categoryBits = MarioGame.MARIO_BIT;
        fdef.filter.maskBits = MarioGame.GROUND_BIT | MarioGame.COIN_BIT | MarioGame.BRICK_BIT | MarioGame.ENEMY_BIT | MarioGame.OBJECT_BIT | MarioGame.ENEMY_HEAD_BIT | MarioGame.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioGame.PPM, 7 / MarioGame.PPM), new Vector2(2 / MarioGame.PPM, 7 / MarioGame.PPM));
        fdef.filter.categoryBits = MarioGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToReDefineMario = false;
    }

    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs) {
            ball.draw(batch);
        }
    }

}
