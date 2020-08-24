package com.ladinc.eyeshooter2;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class EyeBullet implements Pool.Poolable{
    private Vector2 position;
    private Sprite image;
    private EnemyEye eye;
    private Ship ship;
    private Rectangle bulletrectangle;
    private int state;
    private float shortend,longend, distancetravelled;
    private final float maxdistancetravelled;
    private final float defaultveloc = 100;

    public EyeBullet(EnemyEye eye,Ship ship){
        this.image = Assets.eyeshooteratlas.createSprite("enemybullet");
        image.setOrigin(0,image.getHeight()/2);
        this.eye = eye;
        this.ship = ship;
        shortend = image.getHeight();
        longend = image.getWidth();
        bulletrectangle = new Rectangle();
        position = new Vector2();
        maxdistancetravelled = Assets.border.getRegionWidth()/2 - eye.longend/2;

    }

    public EyeBullet(int state, Vector2 position, Ship ship, EnemyEye eye,float distancetravelled) {
        this(eye,ship);
        this.position = position;
        this.state = state;
        this.distancetravelled = distancetravelled;
        teleportBullet();
    }

    public void releasebullet(int state){
        this.state = state;
        switch(state){
            case 0:
                position.set(eye.position.x - eye.longend /2,eye.position.y);
                image.setRotation(0);
                bulletrectangle.set(position.x,position.y - shortend/2,longend,shortend);
                break;
            case 1:
                position.set(eye.position.x,eye.position.y - eye.longend /2);
                image.setRotation(90);
                bulletrectangle.set(position.x - shortend/2,position.y,shortend,longend);
                break;
            case 2:
                position.set(eye.position.x + eye.longend /2,eye.position.y);
                image.setRotation(180);
                bulletrectangle.set(position.x - longend,position.y - shortend/2,longend,shortend);
                break;
            case 3:
                position.set(eye.position.x,eye.position.y + eye.longend /2);
                image.setRotation(270);
                bulletrectangle.set(position.x - shortend/2,position.y - longend,shortend,longend);
                break;
            default:
                break;
        }
        image.setPosition(position.x,position.y - shortend/2);//might not work. If it doesnt work put it within switch statement.
        // if it works order does not matter.//update** it works.
    }

    public boolean impacted() {
        if((ship.invulnerability <= 0 && ship.deathanimateended)){
            return bulletrectangle.overlaps(ship.shiprectangle);
        }else{
            return false;
        }
    }

    public boolean outofBounds() {
        return distancetravelled > maxdistancetravelled;
    }

    public void updateposition(float delta){
        distancetravelled += delta*defaultveloc;
        switch(state){
            case 0:
                position.add(-defaultveloc * delta, 0);
                image.translateX(-defaultveloc * delta);
                bulletrectangle.setX(bulletrectangle.x - defaultveloc * delta);
                break;
            case 1:
                position.add(0,-defaultveloc*delta);
                image.translateY(-defaultveloc*delta);
                bulletrectangle.setY(bulletrectangle.y - defaultveloc*delta);
                break;
            case 2:
                position.add(defaultveloc*delta,0);
                image.translateX(defaultveloc*delta);
                bulletrectangle.setX(bulletrectangle.x + defaultveloc*delta);
                break;
            case 3:
                position.add(0,defaultveloc*delta);
                image.translateY(defaultveloc*delta);
                bulletrectangle.setY(bulletrectangle.y + defaultveloc*delta);
                break;
            default:
                break;
        }
    }

    private void teleportBullet(){
        switch(state){
            case 0:
                image.setRotation(0);
                bulletrectangle.set(position.x,position.y - shortend/2,longend,shortend);
                break;
            case 1:
                image.setRotation(90);
                bulletrectangle.set(position.x - shortend/2,position.y,shortend,longend);
                break;
            case 2:
                image.setRotation(180);
                bulletrectangle.set(position.x - longend,position.y - shortend/2,longend,shortend);
                break;
            case 3:
                image.setRotation(270);
                bulletrectangle.set(position.x - shortend/2,position.y - longend,shortend,longend);
                break;
            default:
                break;
        }
        image.setPosition(position.x,position.y - shortend/2);
    }
    public void drawBullet(SpriteBatch batch){
        image.draw(batch);
    }

    @Override
    public void reset() {
        distancetravelled = 0;
    }

    public static EyeBullet eyeBulletload(Preferences prefs, Ship ship, EnemyEye eye, int bulletcount){
        int state = prefs.getInteger("com.ladinc.eyeshooter.eyebullet._eyebulletstate"+bulletcount);
        float distancetravelled = prefs.getFloat("com.ladinc.eyeshooter.eyebullet._eyebulletdistancetravelled"+bulletcount);
        Vector2 position = new Vector2(prefs.getFloat("com.ladinc.eyeshooter.eyebullet._eyebulletposition"+bulletcount+".x"),prefs.getFloat("com.ladinc.eyeshooter.eyebullet._eyebulletposition"+bulletcount+".y"));
        return new EyeBullet(state,position,ship,eye,distancetravelled);
    }
    public static void eyeBulletsave(Preferences prefs,EyeBullet bullet,int bulletcount){
        prefs.putInteger("com.ladinc.eyeshooter.eyebullet._eyebulletstate"+bulletcount,bullet.state);
        prefs.putFloat("com.ladinc.eyeshooter.eyebullet._eyebulletposition"+bulletcount+".x",bullet.position.x);
        prefs.putFloat("com.ladinc.eyeshooter.eyebullet._eyebulletposition"+bulletcount+".y",bullet.position.y);
        prefs.putFloat("com.ladinc.eyeshooter.eyebullet._eyebulletdistancetravelled"+bulletcount,bullet.distancetravelled);
    }
}
