package com.ladinc.eyeshooter2;


import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class EyeBall implements Pool.Poolable{
    private EyeShooter context;
    private Vector2 position,velocity;
    private Rectangle eyeballrectangle;
    private Sprite image;
    private EnemyEye eye;
    private Ship ship;
    private int bounces,rotation;
    private float animationtime;
    private boolean endanimation;
    public float pausetime;
    public EyeBall(EyeShooter context,EnemyEye enemy,Ship ship){
        this.context = context;
        eye = enemy;
        this.ship = ship;
        image = Assets.eyeshooteratlas.createSprite("bouncyballfinal");
        eyeballrectangle = new Rectangle();
        eyeballrectangle.setSize(image.getWidth()/(float)Math.sqrt(2));
        position = new Vector2();
        velocity = new Vector2(-1,0).setLength2((Assets.gamebound.getRegionWidth()*Assets.gamebound.getRegionWidth() +
                                                Assets.gamebound.getRegionHeight()*Assets.gamebound.getRegionWidth())/8);
        rotation = MathUtils.randomSign();
    }
    public EyeBall(EyeShooter context,EnemyEye enemy,Ship ship,int bounces, int rotation, Vector2 position, Vector2 velocity,float animationtime,
                   float pausetime, boolean endanimation){
        this(context,enemy,ship);
        this.bounces = bounces;
        this.rotation = rotation;
        this.position = position;
        this.velocity = velocity;
        this.animationtime = animationtime;
        this.pausetime = pausetime;
        this.endanimation = endanimation;
        if(pausetime > 0.5){
            image.setCenter(position.x,position.y);
        }
    }

    @Override
    public void reset() {
        rotation = MathUtils.randomSign();
        bounces = 0;
        animationtime = 0;
        endanimation = false;
        pausetime = 0;
        velocity.setAngle(180);
    }

    public void releaseBall(int state){
        switch(state){
            case 0:
                position.set(eye.position.x - eye.longend /2 - image.getRegionWidth()/2,eye.position.y);
                velocity.setAngle(180);
                break;
            case 1:
                position.set(eye.position.x,eye.position.y - eye.longend /2 - image.getRegionWidth()/2);
                velocity.setAngle(270);
                break;
            case 2:
                position.set(eye.position.x + eye.longend /2 + image.getRegionWidth()/2,eye.position.y);
                velocity.setAngle(0);
                break;
            case 3:
                position.set(eye.position.x,eye.position.y + eye.longend /2 + image.getRegionWidth()/2);
                velocity.setAngle(90);
                break;
            default:
                break;

        }
        eyeballrectangle.setPosition(position);
    }

    public void updatevelocity() {
        if (bounces == 0) {
            velocity.rotate(rotation * 135);
            bounces += 1;
        } else if (bounces <= 2) {
            velocity.rotate90(rotation);
            bounces += 1;
        }else if (bounces == 3){
            startEndAnimation();
        }
    }

    public void startEndAnimation(){
        endanimation = true;
        animationtime = 0;
    }

    public void updateposition(float delta){
        Vector2 tempvec = new Vector2(position);
        position.x += velocity.x*delta;
        position.y += velocity.y*delta;
        image.setCenter(position.x,position.y);
        eyeballrectangle.setCenter(position);
        if(!context.gameboundrectangle.contains(eyeballrectangle)){
            position = tempvec;
            image.setCenter(position.x,position.y);
            eyeballrectangle.setCenter(position);
            updatevelocity();
        }
    }

    public void drawBall(SpriteBatch batch){
        if(endanimation == false) {
            if (animationtime < Assets.eyeball.getAnimationDuration()) {
                TextureRegion keyframe = Assets.eyeball.getKeyFrame(animationtime);
                context.batch.draw(keyframe, position.x - keyframe.getRegionWidth() / 2, position.y - keyframe.getRegionHeight() / 2);
                return;
            }
            image.draw(batch);
        }else{
            if (animationtime < Assets.reverseeyeball.getAnimationDuration()) {
                TextureRegion keyframe = Assets.reverseeyeball.getKeyFrame(animationtime);
                context.batch.draw(keyframe, position.x - keyframe.getRegionWidth() / 2, position.y - keyframe.getRegionHeight() / 2);
                return;
            }else{
                endanimation = false;
                EyeShooter.activeeyeball.removeValue(this, true);
                context.eyeballpool.free(this);
            }
        }
    }

    public boolean impacted(){
        if((ship.invulnerability <= 0 && ship.deathanimateended)) {
            return ship.shiprectangle.overlaps(eyeballrectangle);
        }else{
            return false;
        }
    }

    public void updateBallInfo(float delta) {
        if (animationtime < Assets.eyeball.getAnimationDuration()) {
            animationtime += delta;
            if(animationtime > Assets.eyeball.getAnimationDuration()){
                image.setCenter(position.x,position.y);
            }
            return;
        }
        if (pausetime < 0.5) {
            pausetime += delta;
            if(pausetime > 0.5){
                context.currenttimemillis = System.currentTimeMillis();
            }
            return;
        }
        if(endanimation){
            return;
        }
        updateposition(delta);
    }
    public static void eyeBallSave(Preferences prefs,EyeBall eyeball,int ballcount){
        prefs.putInteger("com.ladinc.eyeshooter.eyeball._eyeballbounces"+ballcount,eyeball.bounces);
        prefs.putInteger("com.ladinc.eyeshooter.eyeball._eyeballrotation"+ballcount,eyeball.rotation);
        prefs.putFloat("com.ladinc.eyeshooter.eyeball._eyeballposition"+ballcount+".x",eyeball.position.x);
        prefs.putFloat("com.ladinc.eyeshooter.eyeball._eyeballposition"+ballcount+".y",eyeball.position.y);
        prefs.putFloat("com.ladinc.eyeshooter.eyeball._eyeballvelocity"+ballcount+".x",eyeball.velocity.x);
        prefs.putFloat("com.ladinc.eyeshooter.eyeball._eyeballvelocity"+ballcount+".y",eyeball.velocity.y);
        prefs.putFloat("com.ladinc.eyeshooter.eyeball._animationtime"+ballcount,eyeball.animationtime);
        prefs.putFloat("com.ladinc.eyeshooter.eyeball._pausetime"+ballcount,eyeball.pausetime);
        prefs.putBoolean("com.ladinc.eyeshooter.eyeball._endanimation"+ballcount,eyeball.endanimation);
    }

    public static EyeBall eyeBallLoad(Preferences prefs,EyeShooter context,int ballcount, EnemyEye enemy,Ship ship){
        int bounces = prefs.getInteger("com.ladinc.eyeshooter.eyeball._eyeballbounces"+ballcount);
        int rotation = prefs.getInteger("com.ladinc.eyeshooter.eyeball._eyeballrotation"+ballcount);
        Vector2 position = new Vector2(prefs.getFloat("com.ladinc.eyeshooter.eyeball._eyeballposition"+ballcount+".x"),
                                        prefs.getFloat("com.ladinc.eyeshooter.eyeball._eyeballposition"+ballcount+".y"));
        Vector2 velocity = new Vector2(prefs.getFloat("com.ladinc.eyeshooter.eyeball._eyeballvelocity"+ballcount+".x"),
                                        prefs.getFloat("com.ladinc.eyeshooter.eyeball._eyeballvelocity"+ballcount+".y"));
        float animationtime = prefs.getFloat("com.ladinc.eyeshooter.eyeball._animationtime"+ballcount);
        float pausetime = prefs.getFloat("com.ladinc.eyeshooter.eyeball._pausetime"+ballcount);
        boolean endanimation = prefs.getBoolean("com.ladinc.eyeshooter.eyeball._endanimation"+ballcount);
        return new EyeBall(context,enemy,ship,bounces,rotation,position,velocity,animationtime,pausetime,endanimation);
    }
}
