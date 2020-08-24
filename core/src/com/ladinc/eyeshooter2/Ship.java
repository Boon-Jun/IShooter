package com.ladinc.eyeshooter2;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Ship {
    public int state = 3;
    private Sprite shipsprite;
    private EyeShooter context;
    private int shipstate = 1;
    private float shipinvultimer,gameboundwidth;
    public float longend,shortend,deathtime;
    public Vector2 position,deathspot;
    public Rectangle shiprectangle;
    public float invulnerability;
    public boolean deathanimateended;


    public Ship(EyeShooter context){
        shipsprite = Assets.ship;
        this.context = context;
        position = new Vector2();
        shortend = shipsprite.getHeight();
        longend = shipsprite.getWidth();
        shiprectangle = new Rectangle();
        gameboundwidth = Assets.gamebound.getRegionWidth();
        deathspot = new Vector2();
        deathanimateended = true;
    }
    public Ship(EyeShooter context,int state,float invulnerability,int shipstate,boolean deathanimateended,float deathtime) {
        this(context);
        this.state = state;
        this.invulnerability = invulnerability;
        this.shipstate = shipstate;
        this.deathanimateended = deathanimateended;
        this.deathtime = deathtime;
    }
    public void afterResized(){
        setState(state);
        if(!deathanimateended){
            startDeathAnimate();
        }
    }
    public void setState(int wantedstate){
        if(wantedstate < 0){
            this.state = 3;
        }else {
            this.state = (wantedstate) % 4;
        }
        setPosition(state);
        setRotation(state);
    }
    public void drawship(float delta){
        if((invulnerability <= 0 && deathanimateended) && context.gamestate != EyeShooter.GameState.ended){
            this.shipsprite.draw(context.batch);
        }else if(invulnerability>0){
            playinvulnanimation(delta);
        }else if(!deathanimateended){
            playDeathAnimate(delta);
        }
    }

    public void setPosition(int state) {
        switch(state){
            case 0:
                position.set(context.camerawidth/2 + gameboundwidth/2 - longend - 20,context.cameraheight/2);
                shipsprite.setPosition(position.x,position.y - shortend/2);
                shiprectangle.set(position.x, position.y - shortend/2,longend,shortend);
                break;
            case 1:
                position.set(context.camerawidth/2,context.cameraheight/2 + gameboundwidth/2 -longend - 20);
                shipsprite.setPosition(position.x,position.y - shortend/2);
                shiprectangle.set(position.x - shortend/2, position.y,shortend,longend);
                break;
            case 2:
                position.set(context.camerawidth/2 - gameboundwidth/2 +20 + longend ,context.cameraheight/2);
                shipsprite.setPosition(position.x,position.y - shortend/2);
                shiprectangle.set(position.x - longend, position.y - shortend/2,longend,shortend);
                break;
            case 3:
                position.set(context.camerawidth/2,context.cameraheight/2 - gameboundwidth/2 + 20 + longend);
                shipsprite.setPosition(position.x,position.y - shortend/2);
                shiprectangle.set(position.x - shortend/2, position.y - longend,shortend,longend);
                break;
            default:
                break;
        }
    }
    public void setRotation(int state){
        switch(state){
            case 0:
                shipsprite.setRotation(0);
                break;
            case 1:
                shipsprite.setRotation(90);
                break;
            case 2:
                shipsprite.setRotation(180);
                break;
            case 3:
                shipsprite.setRotation(270);
                break;
            default:
                break;
        }
    }
    public void revive(){
        setState(3);
        invulnerability = 2;
    }
    public void playinvulnanimation(float delta){
        if(context.gamestate != EyeShooter.GameState.paused && context.gamestate != EyeShooter.GameState.ended) {
            shipinvultimer += delta;
            invulnerability -= delta;
            if(shipinvultimer> 0.2){
                shipinvultimer -= 0.2;
                shipstate *= -1;
            }
        }
        if(shipstate>0){
            this.shipsprite.draw(context.batch);
        }
    }
    public void reset(){
        setState(3);
        deathanimateended = true;
    }
    public static Ship shipMemoryload(Preferences prefs,EyeShooter context){
        int state = prefs.getInteger("com.ladinc.eyeshooter.ship._state");
        float invulnerability = prefs.getFloat("com.ladinc.eyeshooter.ship._invulnerability");
        int shipstate = prefs.getInteger("com.ladinc.eyeshooter.ship._shipstate");
        boolean deathanimateended = prefs.getBoolean("com.ladinc.eyeshooter.ship._deathanimateended");
        float deathtime = prefs.getFloat("com.ladinc.eyeshooter.ship._deathtime");
        return new Ship(context,state,invulnerability,shipstate,deathanimateended,deathtime);
    }
    public void shipMemoryPut(Preferences prefs){
        prefs.putInteger("com.ladinc.eyeshooter.ship._state",state);
        prefs.putInteger("com.ladinc.eyeshooter.ship._shipstate",shipstate);
        prefs.putFloat("com.ladinc.eyeshooter.ship._invulnerability", invulnerability);
        prefs.putBoolean("com.ladinc.eyeshooter.ship._deathanimateended",deathanimateended);
        prefs.putFloat("com.ladinc.eyeshooter.ship._deathtime",deathtime);
    }
    public void startDeathAnimate(){
        deathanimateended = false;
        shiprectangle.setPosition(0,0);
        switch(state){
            case 0:
                this.deathspot.set(position.x+longend/2,position.y);
                break;
            case 1:
                this.deathspot.set(position.x,position.y + longend/2);
                break;
            case 2:
                this.deathspot.set(position.x-longend/2,position.y);
                break;
            case 3:
                this.deathspot.set(position.x,position.y-longend/2);
                break;
            default:
                break;
        }
    }
    public void playDeathAnimate(float delta){
            if(context.gamestate != EyeShooter.GameState.paused) {
                deathtime += delta;
            }
            TextureRegion keyframe = Assets.explosion.getKeyFrame(deathtime);
            if(deathtime>Assets.explosion.getAnimationDuration()){
                deathtime = 0;
                deathspot.set(0,0);
                deathanimateended = true;
                if(context.gamestate != EyeShooter.GameState.ended) revive();
            }

            context.batch.draw(keyframe,deathspot.x -keyframe.getRegionWidth()/2,deathspot.y-keyframe.getRegionHeight()/2);
    }

}
