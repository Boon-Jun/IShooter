package com.ladinc.eyeshooter2;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class EyeChaser {
    private Vector2 currentposition,currentdirection,generatingvelocity;
    private float angularvelocity,clock,radius,angleleft,alpha = 1;
    private movingState movingstate;
    public boolean loaded;
    private Ship ship;
    private EnemyEye enemyEye;
    private EyeShooter context;
    private int positionstate,movements;
    private Rectangle eyechaserRectangle;

    public EyeChaser(EyeShooter context,Ship ship,EnemyEye enemyEye) {
        this.ship = ship;
        this.enemyEye = enemyEye;
        this.context = context;
        movingstate = movingstate.nonexistent;
        eyechaserRectangle = new Rectangle();
        eyechaserRectangle.setSize(Assets.eyechaser.getRegionWidth(),Assets.eyechaser.getRegionWidth());
        generatingvelocity = new Vector2();
        currentposition = new Vector2();
        currentdirection = new Vector2();
        alpha = 1;
        radius = Assets.gamebound.getRegionWidth()/2 - Assets.ship.getRegionWidth()/2 - enemyEye.longend/2 - 30;
    }

    public EyeChaser(EyeShooter context, Ship ship, EnemyEye enemyEye, int positionstate, boolean loaded,
                     float angularvelocity, float clock, float angleleft, Vector2 currentposition,
                     Vector2 currentdirection, Vector2 generatingvelocity,int movingstateint,int movements,float alpha) {
        this(context,ship,enemyEye);
        this.angularvelocity = angularvelocity;
        this.clock = clock;
        this.loaded = loaded;
        this.positionstate = positionstate;
        this.angleleft = angleleft;
        this.currentposition = currentposition;
        this.currentdirection = currentdirection;
        this.generatingvelocity = generatingvelocity;
        this.movements = movements;
        this.alpha = alpha;
        switch(movingstateint){
            case 1:
                movingstate = movingState.generating;
                break;
            case 2:
                movingstate = movingState.paused;
                break;
            case 3:
                movingstate = movingState.moving;
            default:
                break;
        }
    }

    public void updateInfo(float delta){
        if((context.gamestate != EyeShooter.GameState.started || context.gamestate != EyeShooter.GameState.ended) && !loaded) return;
        switch(movingstate){
            case generating:
                if(clock > radius/100){
                    clock = 0;
                    movingstate = movingstate.paused;
                }
                clock += delta;
                currentposition.x += generatingvelocity.x*delta;
                currentposition.y += generatingvelocity.y*delta;
                eyechaserRectangle.setCenter(currentposition);
                break;
            case paused:
                if(clock>2){
                    clock = 0;
                    movingstate = movingState.moving;
                    int direction;
                    if((Math.abs(positionstate - ship.state))%2 == 0){
                        angleleft = 180;
                        direction = MathUtils.randomSign();
                    }else{
                        angleleft = 90;
                        if(ship.state == 3 && positionstate == 0) {
                            direction = -1;
                        }else if(ship.state == 0 && positionstate == 3){
                            direction = 1;
                        }else{
                            if(ship.state > positionstate){
                                direction = 1;
                            }else{
                                direction = -1;
                            }
                        }
                    }

                    positionstate = ship.state;
                    movements += 1;
                    angularvelocity = direction*180/3.0f;
                }
                clock += delta;
                break;
            case moving:
                if(movements >= 7){
                    if(alpha == 0){
                        reset();
                        return;
                    }
                    float change = delta*1/((1/(Math.abs(angularvelocity)/180))/2);
                    alpha -= change;
                    if(alpha <= 0){
                        alpha = 0;
                    }
                }
                if(angleleft <= 0){
                    movingstate = movingState.paused;
                }
                float wantedangle = angularvelocity * delta;
                if(Math.abs(wantedangle) < angleleft){
                    angleleft -= Math.abs(wantedangle);
                }else{
                    wantedangle = wantedangle/Math.abs(wantedangle)*angleleft;
                    angleleft = 0;
                }
                currentdirection.rotate(wantedangle);
                currentposition = currentposition.set(enemyEye.position).add(currentdirection);
                eyechaserRectangle.setCenter(currentposition);
                break;
            default:
                break;
        }
    }

    public boolean impacted(){
        if(!loaded || !(ship.invulnerability <= 0 && ship.deathanimateended))return false;
        return eyechaserRectangle.overlaps(ship.shiprectangle);
    }

    public void drawEyeChaser(SpriteBatch batch){
        float rotatedangle;
        Assets.eyechaser.setCenter(currentposition.x,currentposition.y);
        switch(movingstate){
            case generating:
                Assets.eyechaser.setRotation(currentdirection.angle());
                Assets.eyechaser.draw(batch);
                break;
            case paused:
                if(angularvelocity == 0){
                    rotatedangle = currentdirection.angle();
                }else {
                    rotatedangle = currentdirection.angle() + (angularvelocity >= 0 ? 90 : -90);
                }
                Assets.eyechaser.setRotation(rotatedangle);
                Assets.eyechaser.draw(batch);
                break;
            case moving:
                rotatedangle = currentdirection.angle()+ (angularvelocity >= 0?90:-90);
                Assets.eyechaser.setRotation(rotatedangle);
                Assets.eyechaser.setAlpha(alpha);
                Assets.eyechaser.draw(batch);
                break;
            default:
                break;
        }
    }

    public void reset(){
        movingstate = movingState.nonexistent;
        clock = 0;
        angularvelocity = 0;
        movements = 0;
        loaded = false;
        Assets.eyechaser.setRotation(0);

    }
    public void startEyeChaserAnimation(){
        alpha = 1;
        Assets.eyechaser.setAlpha(alpha);
        loaded = true;
        movingstate = movingState.generating;
        positionstate = MathUtils.random(0,3);
        switch(positionstate){
            case 0:
                generatingvelocity.set(1,0).setLength(100);
                currentposition.set(enemyEye.position.x + enemyEye.longend /2,enemyEye.position.y);
                break;
            case 1:
                generatingvelocity = new Vector2(0,1).setLength(100);
                currentposition.set(enemyEye.position.x,enemyEye.position.y + enemyEye.longend /2);
                break;
            case 2:
                generatingvelocity = new Vector2(-1,0).setLength(100);
                currentposition.set(enemyEye.position.x - enemyEye.longend /2,enemyEye.position.y );
                break;
            case 3:
                generatingvelocity = new Vector2(0,-1).setLength(100);
                currentposition.set(enemyEye.position.x,enemyEye.position.y - enemyEye.longend /2);
                break;
            default:
                break;
        }
        currentdirection.set(generatingvelocity).setLength(radius + enemyEye.longend/2);
        eyechaserRectangle.setCenter(currentposition);
    }
    public enum movingState{
        nonexistent,generating,paused, moving
    }

    public static EyeChaser eyeChaserLoad(Preferences prefs,EyeShooter context,Ship ship, EnemyEye enemyEye){
        int movingstateint = prefs.getInteger("com.ladinc.eyeshooter.eyechaser._movingstate");
        if(movingstateint == 0){
            return new EyeChaser(context,ship,enemyEye);
        }
        int positionstate = prefs.getInteger("com.ladinc.eyeshooter.eyechaser._positionstate");
        boolean loaded = prefs.getBoolean("com.ladinc.eyeshooter.eyechaser._loaded");
        float angularvelocity = prefs.getFloat("com.ladinc.eyeshooter.eyechaser._angularvelocity");
        float clock = prefs.getFloat("com.ladinc.eyeshooter.eyechaser._clock");
        float angleleft = prefs.getFloat("com.ladinc.eyeshooter.eyechaser._angleleft");
        Vector2 currentposition = new Vector2(prefs.getFloat("com.ladinc.eyeshooter.eyechaser._currentposition.x"),
                prefs.getFloat("com.ladinc.eyeshooter.eyechaser._currentposition.y"));
        Vector2 currentdirection = new Vector2(prefs.getFloat("com.ladinc.eyeshooter.eyechaser._currentdirection.x"),
                prefs.getFloat("com.ladinc.eyeshooter.eyechaser._currentdirection.y"));
        Vector2 generatingvelocity = new Vector2(prefs.getFloat("com.ladinc.eyeshooter.eyechaser._generatingvelocity.x"),
                prefs.getFloat("com.ladinc.eyeshooter.eyechaser._generatingvelocity.y"));
        int movements = prefs.getInteger("com.ladinc.eyeshooter.eyechaser._movements");
        float alpha = prefs.getFloat("com.ladinc.eyeshooter.eyechaser._alpha");
        return new EyeChaser(context,ship,enemyEye,positionstate,loaded,angularvelocity,clock,angleleft,currentposition,currentdirection,generatingvelocity,
                            movingstateint,movements,alpha);

    }

    public void eyeChaserSave(Preferences prefs){
        int movingstateint = 0;
        switch(movingstate){
            case nonexistent:
                break;
            case generating:
                movingstateint = 1;
                break;
            case paused:
                movingstateint = 2;
                break;
            case moving:
                movingstateint = 3;
                break;
            default:
                break;
        }
        prefs.putInteger("com.ladinc.eyeshooter.eyechaser._movingstate",movingstateint);
        if(movingstateint == 0){
            return;
        }
        prefs.putInteger("com.ladinc.eyeshooter.eyechaser._positionstate",positionstate);
        prefs.putBoolean("com.ladinc.eyeshooter.eyechaser._loaded",loaded);
        prefs.putFloat("com.ladinc.eyeshooter.eyechaser._angularvelocity",angularvelocity);
        prefs.putFloat("com.ladinc.eyeshooter.eyechaser._clock",clock);
        prefs.putFloat("com.ladinc.eyeshooter.eyechaser._angleleft",angleleft);
        prefs.putFloat("com.ladinc.eyeshooter.eyechaser._currentposition.x",currentposition.x);
        prefs.putFloat("com.ladinc.eyeshooter.eyechaser._currentposition.y",currentposition.y);
        prefs.putFloat("com.ladinc.eyeshooter.eyechaser._currentdirection.x",currentdirection.x);
        prefs.putFloat("com.ladinc.eyeshooter.eyechaser._currentdirection.y",currentdirection.y);
        prefs.putFloat("com.ladinc.eyeshooter.eyechaser._generatingvelocity.x",generatingvelocity.x);
        prefs.putFloat("com.ladinc.eyeshooter.eyechaser._generatingvelocity.y",generatingvelocity.y);
        prefs.putInteger("com.ladinc.eyeshooter.eyechaser._movements",movements);
        prefs.putFloat("com.ladinc.eyeshooter.eyechaser._alpha",alpha);
    }
}
