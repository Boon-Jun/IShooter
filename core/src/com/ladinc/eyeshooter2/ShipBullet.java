package com.ladinc.eyeshooter2;


import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class ShipBullet implements Pool.Poolable{
    private Vector2 position;
    private Ship context;
    private Sprite image;
    private Rectangle bulletRectangle;
    private EnemyEye eye;
    private float shortend,longend,rotation;
    private final float defaultveloc = 200;
    public int state;
    public ShipBullet(Ship context, EnemyEye eye){
        this.context = context;
        image = Assets.eyeshooteratlas.createSprite("shipbullet");
        image.setOrigin(0,image.getHeight()/2);
        position = new Vector2();
        this.eye = eye;
        shortend = image.getHeight();
        longend = image.getWidth();
        bulletRectangle = new Rectangle();
    }
    public ShipBullet(int state,Vector2 position,Ship context, EnemyEye eye){
        this(context,eye);
        this.position = position;
        this.state = state;
        teleportBullet();
    }
    public void releasebullet(){
        this.state = context.state;
        position.set(context.position.x,context.position.y);
        image.setPosition(context.position.x,context.position.y - shortend/2);
        switch(state){
            case 0:
                image.setRotation(0);
                bulletRectangle.set(position.x,position.y - shortend/2,longend,shortend);
                break;
            case 1:
                image.setRotation(90);
                bulletRectangle.set(position.x - shortend/2,position.y,shortend,longend);
                break;
            case 2:
                image.setRotation(180);
                bulletRectangle.set(position.x - longend,position.y - shortend/2,longend,shortend);
                break;
            case 3:
                image.setRotation(270);
                bulletRectangle.set(position.x - shortend/2,position.y - longend,shortend,longend);
                break;
            default:
                break;

        }
    }
    public boolean impacted() {
        return bulletRectangle.overlaps(eye.eyerectangle);
    }
    public void updateposition(float delta){
        switch(state){
            case 0:
                position.add(-defaultveloc*delta,0);
                image.translateX(-defaultveloc*delta);
                bulletRectangle.setX(bulletRectangle.x - defaultveloc*delta);
                break;
            case 1:
                position.add(0,-defaultveloc*delta);
                image.translateY(-defaultveloc*delta);
                bulletRectangle.setY(bulletRectangle.y - defaultveloc*delta);
                break;
            case 2:
                position.add(defaultveloc*delta,0);
                image.translateX(defaultveloc*delta);
                bulletRectangle.setX(bulletRectangle.x + defaultveloc*delta);
                break;
            case 3:
                position.add(0,defaultveloc*delta);
                image.translateY(defaultveloc*delta);
                bulletRectangle.setY(bulletRectangle.y + defaultveloc*delta);
                break;
            default:break;
        }
    }
    private void teleportBullet(){
        image.setPosition(position.x,position.y - shortend/2);
        switch(state){
            case 0:
                image.setRotation(0);
                bulletRectangle.set(position.x,position.y - shortend/2,longend,shortend);
                break;
            case 1:
                image.setRotation(90);
                bulletRectangle.set(position.x - shortend/2,position.y,shortend,longend);
                break;
            case 2:
                image.setRotation(180);
                bulletRectangle.set(position.x - longend,position.y - shortend/2,longend,shortend);
                break;
            case 3:
                image.setRotation(270);
                bulletRectangle.set(position.x - shortend/2,position.y - longend,shortend,longend);
                break;
            default:
                break;
        }
    }
    public void drawBullet(SpriteBatch batch){
        image.draw(batch);
    }

    @Override
    public void reset() {

    }

    public static ShipBullet shipBulletload(Preferences prefs,Ship ship, EnemyEye eye,int bulletcount){
        int state = prefs.getInteger("com.ladinc.eyeshooter._shipbulletstate"+bulletcount,3);
        Vector2 position = new Vector2(prefs.getFloat("com.ladinc.eyeshooter._shipbulletposition"+bulletcount+".x"),prefs.getFloat("com.ladinc.eyeshooter._shipbulletposition"+bulletcount+".y"));
        return new ShipBullet(state,position,ship,eye);
    }
    public static void shipBulletsave(Preferences prefs,ShipBullet bullet,int bulletcount){
        prefs.putInteger("com.ladinc.eyeshooter._shipbulletstate"+bulletcount,bullet.state);
        prefs.putFloat("com.ladinc.eyeshooter._shipbulletposition"+bulletcount+".x",bullet.position.x);
        prefs.putFloat("com.ladinc.eyeshooter._shipbulletposition"+bulletcount+".y",bullet.position.y);
    }
}
