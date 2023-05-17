package com.mariogame.Tools;

import com.badlogic.gdx.physics.box2d.*;
import com.mariogame.game.MarioGame;
import com.mariogame.game.Sprites.Enemies.Enemy;
import com.mariogame.game.Sprites.FireBall;
import com.mariogame.game.Sprites.Items.Item;
import com.mariogame.game.Sprites.Mario;
import com.mariogame.game.Sprites.TileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(cDef) {
            case MarioGame.MARIO_HEAD_BIT | MarioGame.BRICK_BIT:
            case MarioGame.MARIO_HEAD_BIT | MarioGame.COIN_BIT:
                if (fixA.getFilterData().categoryBits == MarioGame.MARIO_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            case MarioGame.ENEMY_HEAD_BIT | MarioGame.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioGame.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else
                    ((Enemy)fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;
            case MarioGame.ENEMY_BIT | MarioGame.OBJECT_BIT:
            case MarioGame.ENEMY_BIT | MarioGame.GROUND_BIT:
                if (fixA.getFilterData().categoryBits == MarioGame.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioGame.MARIO_BIT | MarioGame.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == MarioGame.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit((Enemy)fixB.getUserData());
                else
                    ((Mario) fixB.getUserData()).hit((Enemy)fixA.getUserData());
                break;
            case MarioGame.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).onEnemyHit((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).onEnemyHit((Enemy)fixA.getUserData());
                break;
            case MarioGame.ITEM_BIT | MarioGame.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioGame.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioGame.ITEM_BIT | MarioGame.MARIO_BIT: // Check if Mario and an Item collided
                if (fixA.getFilterData().categoryBits == MarioGame.ITEM_BIT) // Check which fixture is the item
                    ((Item)fixA.getUserData()).useItem((Mario) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).useItem((Mario) fixA.getUserData());
                break;
            case MarioGame.PROJECTILE_BIT | MarioGame.ENEMY_BIT:
            case MarioGame.PROJECTILE_BIT | MarioGame.ENEMY_HEAD_BIT:
                if (fixA.getFilterData().categoryBits == MarioGame.PROJECTILE_BIT) {
                    ((FireBall) fixA.getUserData()).setToDestroy();
                    ((Enemy) fixB.getUserData()).projectileHit((FireBall) fixA.getUserData());
                } else {
                    ((FireBall) fixB.getUserData()).setToDestroy();
                    ((Enemy) fixA.getUserData()).projectileHit((FireBall) fixB.getUserData());
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
