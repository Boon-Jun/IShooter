package com.ladinc.eyeshooter2;


import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class EnemyEye {
    private EyeShooter context;
    private Sprite enemysprite,enemyhitsprite,drawnsprite;
    public float longend, shortend;
    public Vector2 position;
    public int rotatestate = 1,hit;
    public Rectangle eyerectangle;
    private float animatetime;
    private int blinkcount;
    public EnemyEye(EyeShooter context){
        this.context = context;
        enemysprite = Assets.enemy;
        enemyhitsprite = Assets.enemyhit;
        longend = enemysprite.getWidth();
        shortend = enemysprite.getHeight();
        drawnsprite = enemysprite;
        this.eyerectangle = new Rectangle();
        position = new Vector2();
    }
    public EnemyEye(EyeShooter context,int state, int blinkcount, float animatetime){
        this(context);
        this.rotatestate = state;
        this.blinkcount = blinkcount;
        this.animatetime = animatetime;
    }
    public void drawenemyeye(float delta){
        if(hit == 0){
            drawnsprite.draw(context.batch);
        }else{
            playhitanimation(delta);
            drawnsprite.draw(context.batch);
        }
    }
    public void setPosition(float x,float y){
        position.set(x,y);
        enemysprite.setCenter(x,y);
        enemyhitsprite.setCenter(x,y);
    }
    public void setRotatestate(){
        if(context.gamestate == EyeShooter.GameState.paused || context.gamestate == EyeShooter.GameState.ended){
            setSpriterotation(rotatestate);
            return;
        }
        int wantedrotatestate = MathUtils.random(0,3);
        rotatestate = wantedrotatestate;
        setSpriterotation(rotatestate);
    }
    public void setSpriterotation(int state){
        switch(state){
            case 0:
                enemysprite.setRotation(0);
                enemyhitsprite.setRotation(0);
                eyerectangle.set(position.x - longend/2, position.y - shortend/2,longend,shortend);
                break;
            case 1:
                enemysprite.setRotation(90);
                enemyhitsprite.setRotation(90);
                eyerectangle.set(position.x - shortend/2, position.y - longend/2,shortend,longend);
                break;
            case 2:
                enemysprite.setRotation(180);
                enemyhitsprite.setRotation(180);
                eyerectangle.set(position.x - longend/2, position.y - shortend/2,longend,shortend);
                break;
            case 3:
                enemysprite.setRotation(270);
                enemyhitsprite.setRotation(270);
                eyerectangle.set(position.x - shortend/2, position.y - longend/2,shortend,longend);
                break;
            default:
                break;
        }
    }
    public void playhitanimation(float delta){
        animatetime += delta;
        if(animatetime > 0.1){
            animatetime -=0.1;
            blinkcount += 1;
        }
        if(blinkcount%2 != 1){
            drawnsprite = enemyhitsprite;
        }
        if(blinkcount >= 3){
            drawnsprite = enemysprite;
            animatetime = 0;
            blinkcount = 0;
            hit = 0;
        }
    }
    public void reset(){
        this.rotatestate = 0;
        setSpriterotation(rotatestate);
        this.blinkcount = 0;
        this.animatetime = 0;
    }
    public void enemySave(Preferences prefs){
        prefs.putInteger("com.ladinc.eyeshooter.enemyeye._state",rotatestate);
        prefs.putFloat("com.ladinc.eyeshooter.enemyeye._animatetime",animatetime);
        prefs.putInteger("com.ladinc.eyeshooter.enemyeye._blinkcount",blinkcount);
    }
    public static EnemyEye enemyLoad(Preferences prefs,EyeShooter context){
        int rotatestate = prefs.getInteger("com.ladinc.eyeshooter.enemyeye._state");
        float animatetime = prefs.getFloat("com.ladinc.eyeshooter.enemyeye._animatetime");
        int blinkcount = prefs.getInteger("com.ladinc.eyeshooter.enemyeye._blinkcount");
        return new EnemyEye(context,rotatestate,blinkcount,animatetime);
    }
}
