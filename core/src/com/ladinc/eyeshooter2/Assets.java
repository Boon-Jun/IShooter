package com.ladinc.eyeshooter2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Assets {
    public static TextureAtlas eyeshooteratlas;
    public static Sprite ship,enemy,enemyhit,confusion1,confusion2,tint,eyechaser;
    public static TextureRegion achievement,leaderboard,pausebutton,mutesound,playsound,startmessage,tryagainmessage,endscoremessage,border,gamebound,title,hiscore,score,level,shootbutton,shootbuttonp,leftbuttonp,
            leftbutton,rightbuttonp,rightbutton,lives,continuemessage;
    public static BitmapFont numberfont;
    public static Sound deathsound,confusionsound,movesound,shipshoot,enemyshoot;
    public static Animation<TextureRegion> reverseeyeball,explosion,eyeball;

    public static void load(){
        eyeshooteratlas = new TextureAtlas(Gdx.files.internal("eyeshootertextures.atlas"));
        ship = eyeshooteratlas.createSprite("ship");
        ship.setOrigin(0,ship.getHeight()/2);
        enemy = eyeshooteratlas.createSprite("enemy");
        enemy.setOriginCenter();
        enemyhit = eyeshooteratlas.createSprite("enemyhit");
        enemyhit.setOriginCenter();
        title = eyeshooteratlas.findRegion("title");
        hiscore = eyeshooteratlas.findRegion("hiscore");
        level = eyeshooteratlas.findRegion("level");
        score = eyeshooteratlas.findRegion("score");
        shootbutton = eyeshooteratlas.findRegion("shootbutton");
        shootbuttonp = eyeshooteratlas.findRegion("shootbuttonp");
        leftbutton = eyeshooteratlas.findRegion("leftbutton");
        leftbuttonp = eyeshooteratlas.findRegion("leftbuttonp");
        rightbutton = eyeshooteratlas.findRegion("rightbutton");
        rightbuttonp = eyeshooteratlas.findRegion("rightbuttonp");
        lives = eyeshooteratlas.findRegion("lives");
        gamebound = eyeshooteratlas.findRegion("gamebound");
        border = eyeshooteratlas.findRegion("borders");
        startmessage = eyeshooteratlas.findRegion("startmessage");
        endscoremessage = eyeshooteratlas.findRegion("endscoremessage");
        tryagainmessage = eyeshooteratlas.findRegion("tryagainmessage");
        continuemessage = eyeshooteratlas.findRegion("continuemessage");
        leaderboard = eyeshooteratlas.findRegion("leaderboard");
        achievement = eyeshooteratlas.findRegion("achievement");
        pausebutton = eyeshooteratlas.findRegion("pausebutton");
        mutesound = eyeshooteratlas.findRegion("mutesound");
        playsound = eyeshooteratlas.findRegion("playsound");
        eyechaser = eyeshooteratlas.createSprite("eyechaser");
        eyechaser.setOriginCenter();
        confusion1 = eyeshooteratlas.createSprite("confusion1");
        confusion1.setOriginCenter();
        confusion2 = eyeshooteratlas.createSprite("confusion2");
        confusion2.setOriginCenter();
        tint = eyeshooteratlas.createSprite("tint");
        tint.setAlpha(0);
        explosion = new Animation<TextureRegion>(0.2f,eyeshooteratlas.findRegions("explosion"), Animation.PlayMode.NORMAL);
        eyeball = new Animation<TextureRegion>(0.233f,eyeshooteratlas.findRegions("bouncyball"), Animation.PlayMode.NORMAL);
        reverseeyeball = new Animation<TextureRegion>(0.233f,eyeshooteratlas.findRegions("bouncyball"), Animation.PlayMode.REVERSED);
        //eyechaser = new Animation<TextureRegion>(0.333f,eyeshooteratlas.findRegions("eyechaser"), Animation.PlayMode.LOOP);////to be updated
        movesound = Gdx.audio.newSound(Gdx.files.internal("switchstate.wav"));
        shipshoot = Gdx.audio.newSound(Gdx.files.internal("shipshoot.wav"));
        enemyshoot = Gdx.audio.newSound(Gdx.files.internal("enemyshoot.wav"));
        confusionsound = Gdx.audio.newSound(Gdx.files.internal("confusion.wav"));
        deathsound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("atari full.ttf"));
        parameter.size = 30;
        parameter.color = Color.BLACK;
        numberfont = generator.generateFont(parameter);

    }
    public static void dispose(){
        eyeshooteratlas.dispose();
        movesound.dispose();
        shipshoot.dispose();
        enemyshoot.dispose();
        confusionsound.dispose();
    }
}
