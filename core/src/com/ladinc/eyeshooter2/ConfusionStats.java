package com.ladinc.eyeshooter2;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ConfusionStats {
    private float initvel, initialx, maxalpha,
            maxsize, ac, tinttime, conftime, currentsize, dissapeartime;
    public float tintalpha;
    private EyeShooter context;
    private Sprite playingSprite;
    private int confusionchangestate;

    public ConfusionStats(EyeShooter context) {
        this.context = context;
        maxsize = Assets.gamebound.getRegionWidth();
        initialx = Assets.confusion1.getWidth();
        maxalpha = (float) 0.2;
        initvel = (float) (0.8 * maxsize + 40);//initvel must be larger than maxwantedsize for negative ac;
        ac = (float) (0.8 * maxsize - initvel) * 2;
    }

    public ConfusionStats(EyeShooter context, float tinttime, float tintalpha, float conftime, int confusionchangestate,float dissapeartime) {
        this(context);
        this.tinttime = tinttime;
        this.tintalpha = tintalpha;
        this.conftime = conftime;
        this.confusionchangestate = confusionchangestate;
        this.dissapeartime = dissapeartime;
        setPlayingAssets();
        if(tintalpha > maxalpha){
            tintalpha = maxalpha;
        }else if(tintalpha < 0){
            tintalpha = 0;
        }////failsafe
        Assets.tint.setAlpha(tintalpha);
        playingSprite.setScale((initvel * conftime + ac * conftime * conftime / 2)/initialx);
        playingSprite.setAlpha(1- (dissapeartime * 1));
    }

    public void playConfusionAnimation(float delta) {
        if (tinttime < 1) {
            tinttime += delta;
            tintalpha += confusionchangestate * delta * maxalpha;
            if(tintalpha > maxalpha){
                tintalpha = maxalpha;
            }else if(tintalpha < 0){
                tintalpha = 0;
            }////failsafe
            Assets.tint.setAlpha(tintalpha);
            return;
        }
        if (conftime < 1){
            conftime += delta;
        currentsize = initvel * conftime + ac * conftime * conftime / 2;
        playingSprite.setScale(currentsize / initialx);
        return;
        }
        if(dissapeartime < 1){
            dissapeartime += delta;
            playingSprite.setAlpha(1- (dissapeartime * 1));
        if (dissapeartime >= 1) {
            tinttime = 0;
            conftime = 0;
            dissapeartime = 0;
            context.playconfusion = false;
            context.confusion *= -1;
            Assets.confusionsound.stop();
            context.currenttimemillis = System.currentTimeMillis();
        }
    }

}

    public void drawConfusionAssets(){
        if(conftime>0) playingSprite.draw(context.batch);
    }

    public void setPlayingAssets(){
        if(context.confusion == 1){
            playingSprite = Assets.confusion1;
            confusionchangestate = 1;
        }else{
            playingSprite = Assets.confusion2;
            confusionchangestate = -1;
        }
        playingSprite.setScale(1);
        playingSprite.setAlpha(1);
    }
    public void reset(){
        tinttime = 0;
        tintalpha = 0;
        conftime = 0;
        dissapeartime = 0;
    }

    public static ConfusionStats confusionStatsload(Preferences prefs, EyeShooter context){
        float tinttime = prefs.getFloat("com.ladinc.eyeshooter.confusionstats._tinttime");
        float tintalpha = prefs.getFloat("com.ladinc.eyeshooter.confusionstats._tintalpha");
        int confusionchangestate = prefs.getInteger("com.ladinc.eyeshooter.confusionstats._confusionchangestate");
        float conftime = prefs.getFloat("com.ladinc.eyeshooter.confusionstats._conftime");
        float dissapeartime = prefs.getFloat("com.ladinc.eyeshooter.confusionstats._dissapeartime");
        return new ConfusionStats(context,tinttime,tintalpha,conftime,confusionchangestate,dissapeartime);
    }

    public void confusionStatssave(Preferences prefs){
        prefs.putFloat("com.ladinc.eyeshooter.confusionstats._tinttime",tinttime);
        prefs.putFloat("com.ladinc.eyeshooter.confusionstats._tintalpha",tintalpha);
        prefs.putInteger("com.ladinc.eyeshooter.confusionstats._confusionchangestate",confusionchangestate);
        prefs.putFloat("com.ladinc.eyeshooter.confusionstats._conftime",conftime);
        prefs.putFloat("com.ladinc.eyeshooter.confusionstats._dissapeartime",dissapeartime);
    }
}
