package com.ladinc.eyeshooter2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;


public class EyeShooter extends ApplicationAdapter implements InputProcessor {
    private final int _VERSIONCODE = 2; ///update as needed
	public SpriteBatch batch;
	private Preferences prefs;
    private int misscount,level,lives,consecutivehit;
	private long highscore,timetorotate,timetoshoot,readytimer = 1300;;
    public long requiredscore,currentscore,confusionscore,confaddup,currenttimemillis;
    private EnemyEye enemyeye;
    private Ship ship;
    public int touchpointer = -1,minshoot,minrotate,maxshoot,maxrotate,minbullet,maxbullet,confusion;
    public float camerawidth,cameraheight,eyeballchances,eyechaserchances;
    private Vector2 achievementposition,pauseposition,soundposition,leaderboardposition,gameboundposition,livesposition,hiscoreposition,currentscoreposition,
                    levelposition,titleposition,shootbuttonposition,leftbuttonposition,rightbuttonposition,hiscorenumber,currentscorenumber,levelnumber,
                    eyeborderposition,startmessageposition,tryagainmessageposition,endscoremessageposition,continuemessageposition,confusioncenterposition;
    public OrthographicCamera camera;
    private Rectangle pauserect,soundrect,leaderboardrect,shootrectangle,leftrectangle,rightrectangle,achievementrect;
    private Pool<ShipBullet> shipbulletpool;
    private Pool<EyeBullet> eyebulletpool;
    public Pool<EyeBall> eyeballpool;
    public GameState gamestate;
    private TextureRegion shoot,left,right;
    private GlyphLayout scoreglyph,hiscoreglyph,levelglyph;
    public static Array<ShipBullet> activesbullet;
    public static Array<EyeBullet> activeeyebullet;
    public static Array<EyeBall> activeeyeball;
    private EyeChaser eyechaser;
    public boolean playconfusion,generatinganimation;
    private boolean playsound = true;
    public Rectangle gameboundrectangle;
    private ConfusionStats cnfs;
    public ActionResolver actionResolver;
    public EyeShooter(ActionResolver actionResolver){
        this.actionResolver = actionResolver;
    }

	@Override
	public void create () {
        Assets.load();
		batch = new SpriteBatch(20);
        camera = new OrthographicCamera();
        activesbullet = new Array<ShipBullet>();
        activeeyebullet = new Array<EyeBullet>();
        activeeyeball = new Array<EyeBall>();
        shootrectangle = new Rectangle();
        leftrectangle = new Rectangle();
        rightrectangle = new Rectangle();
        pauserect = new Rectangle();
        soundrect = new Rectangle();
        achievementrect = new Rectangle();
        leaderboardrect = new Rectangle();
        prefs = Gdx.app.getPreferences("com.ladinc.eyeshooter.eyeshootersprefs");
        if(prefs.getInteger("com.ladinc.eyeshooter.main._gamestate",0) == -1 && prefs.getInteger("com.ladinc.eyeshooter._VERSIONCODE") == _VERSIONCODE) {
            gamestate = GameState.paused;
            memoryLoad();
        }else{
            ship = new Ship(this);
            enemyeye = new EnemyEye(this);
            eyechaser = new EyeChaser(this,ship,enemyeye);
            highscore = prefs.getLong("com.ladinc.eyeshooter.main._highscore",0);
            currentscore = 0;
            level = 1;
            lives = 3;
            gamestate = GameState.ready;
            confusion = 1;
            cnfs = new ConfusionStats(this);
        }
        shoot = Assets.shootbutton;
        left = Assets.leftbutton;
        right = Assets.rightbutton;
        scoreglyph = new GlyphLayout(Assets.numberfont, String.valueOf(currentscore));
        hiscoreglyph = new GlyphLayout(Assets.numberfont, String.valueOf(highscore));
        levelglyph = new GlyphLayout(Assets.numberfont, level < 10 ? "0" + String.valueOf(level) : String.valueOf(level));
        LevelProfile.loadlevel(this,level);
        shipbulletpool = new Pool<ShipBullet>(0, 1) {
            @Override
            protected ShipBullet newObject() {
                return new ShipBullet(ship, enemyeye);
            }
        };
        eyebulletpool = new Pool<EyeBullet>(0, 3) {
            @Override
            protected EyeBullet newObject() {
                return new EyeBullet(enemyeye, ship);
            }
        };
        eyeballpool = new Pool<EyeBall>(0, 2) {
            @Override
            protected EyeBall newObject() {
                return new EyeBall(EyeShooter.this,enemyeye, ship);
            }
        };
        Gdx.input.setInputProcessor(this);
        currenttimemillis = System.currentTimeMillis();
	}
	@Override
	public void resize(int width, int height){
        camerawidth = 1080;
        cameraheight = (float) height / (float) width * 1080;
        camera = new OrthographicCamera(camerawidth,cameraheight);
        camera.position.set(camerawidth / 2, cameraheight / 2, 0);
        camera.update();
        enemyeye.setPosition(camerawidth/2,cameraheight/2);
        enemyeye.setRotatestate();
        ship.afterResized();
        batch.setProjectionMatrix(camera.combined);
        determineUI(camerawidth,cameraheight);
    }

    @Override
    public void pause() {
        super.pause();
        if(gamestate == GameState.started || gamestate == GameState.paused) {
            gamestate = GameState.paused;
            prefs.putInteger("com.ladinc.eyeshooter.main._gamestate",-1);
            memorySave();
        }else{
            prefs.putInteger("com.ladinc.eyeshooter.main._gamestate",0);
            prefs.flush();
        }
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void render() {
        if(readytimer>0){
            long nexttime = System.currentTimeMillis();
            long difference = nexttime-currenttimemillis;
            readytimer -= difference;
            currenttimemillis = nexttime;
        }
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(Assets.gamebound,gameboundposition.x,gameboundposition.y);
        drawGameBoundInternals(delta);
        drawUI();
        if(playconfusion){
            cnfs.drawConfusionAssets();
        }
        batch.end();
        if(gamestate == GameState.paused) {
            return;
        }else if(playconfusion){
            cnfs.playConfusionAnimation(delta);
        }else{
            ammoMaster();
            aiMaster();
            updatebullets(delta);
        }
    }

    @Override
	public void dispose () {
		batch.dispose();
        Assets.dispose();
	}
	private void updatebullets(float delta){
        eyechaser.updateInfo(delta);
        for (ShipBullet bullet: activesbullet){
            bullet.updateposition(delta);
        }
        for (EyeBullet bullet: activeeyebullet){
            if(bullet.outofBounds()){
                eyebulletpool.free(bullet);
                activeeyebullet.removeValue(bullet,true);
            }
            bullet.updateposition(delta);
        }
        try{
            activeeyeball.get(0);
        }catch(IndexOutOfBoundsException e){
            generatinganimation = false;
            return;      /////worst cheat ever;
        }
        generatinganimation = true;
        for (EyeBall ball: activeeyeball){
            ball.updateBallInfo(delta);
        }
    }
    private void drawbullets(){
        eyechaser.drawEyeChaser(batch);
        for (ShipBullet bullet:activesbullet){
            bullet.drawBullet(batch);
        }
        for (EyeBullet bullet:activeeyebullet){
            bullet.drawBullet(batch);
        }
        for (EyeBall ball:activeeyeball){
            ball.drawBall(batch);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (readytimer > 0) {
            return false;
        }
        if (touchpointer == -1) {
            touchpointer = pointer;
            Vector3 touchpoint = new Vector3();
            camera.unproject(touchpoint.set(screenX, screenY, 0));
            if (soundrect.contains(touchpoint.x, touchpoint.y)) {
                if(playsound){
                    playsound = false;
                    Assets.enemyshoot.stop();
                    Assets.confusionsound.stop();
                    Assets.deathsound.stop();
                    Assets.movesound.stop();
                    Assets.shipshoot.stop();
                }else{
                    Assets.movesound.setVolume(Assets.movesound.play(),0.3f);
                    playsound = true;
                }
            } else if (leaderboardrect.contains(touchpoint.x, touchpoint.y)) {
                if(playsound) {
                    Assets.movesound.setVolume(Assets.movesound.play(),0.3f);
                }
                actionResolver.showScore();
            } else if (pauserect.contains(touchpoint.x, touchpoint.y)) {
                if(playsound) {
                    Assets.movesound.setVolume(Assets.movesound.play(),0.3f);
                }
                if(gamestate == GameState.started){
                    pause();
                }else if(gamestate == GameState.paused){
                    pauseTouch();
                }
            } else if(achievementrect.contains(touchpoint.x, touchpoint.y)){
                if(playsound) {
                    Assets.movesound.setVolume(Assets.movesound.play(),0.3f);
                }
                actionResolver.showAchievement();
            }
            switch (gamestate) {
                case ready:
                    if (leftrectangle.contains(touchpoint.x, touchpoint.y)) {
                        left = Assets.leftbuttonp;
                        readyTouch();
                    } else if (rightrectangle.contains(touchpoint.x, touchpoint.y)) {
                        right = Assets.rightbuttonp;
                        readyTouch();
                    } else if (shootrectangle.contains(touchpoint.x, touchpoint.y)) {
                        shoot = Assets.shootbuttonp;
                        readyTouch();
                    }
                    break;
                case started:
                    if((ship.deathanimateended)){
                    if (leftrectangle.contains(touchpoint.x, touchpoint.y)) {
                        left = Assets.leftbuttonp;
                        if (playconfusion) {
                            return false;
                        }
                        ship.setState(ship.state - 1 * confusion);
                        if(playsound){
                            Assets.movesound.setVolume(Assets.movesound.play(),0.3f);
                        }
                    } else if (rightrectangle.contains(touchpoint.x, touchpoint.y)) {
                        right = Assets.rightbuttonp;
                        if (playconfusion) {
                            return false;
                        }
                        ship.setState(ship.state + 1 * confusion);
                        if (playsound) {
                            Assets.movesound.setVolume(Assets.movesound.play(),0.3f);
                        }
                    } else if (shootrectangle.contains(touchpoint.x, touchpoint.y)) {
                        shoot = Assets.shootbuttonp;
                        if (playconfusion) {
                            return false;
                        }
                        try {
                            activesbullet.get(0);
                        } catch (IndexOutOfBoundsException e) {
                            ShipBullet bullet = shipbulletpool.obtain();
                            bullet.releasebullet();
                            activesbullet.add(bullet);
                            if(playsound){
                                Assets.shipshoot.setVolume(Assets.shipshoot.play(),0.3f);
                            }
                        }
                    }
                }
                    break;
                case paused:
                    if (leftrectangle.contains(touchpoint.x, touchpoint.y)) {
                        left = Assets.leftbuttonp;
                        pauseTouch();
                    } else if (rightrectangle.contains(touchpoint.x, touchpoint.y)) {
                        right = Assets.rightbuttonp;
                        pauseTouch();
                    } else if (shootrectangle.contains(touchpoint.x, touchpoint.y)) {
                        shoot = Assets.shootbuttonp;
                        pauseTouch();
                    }
                    break;
                case ended:
                    if (leftrectangle.contains(touchpoint.x, touchpoint.y)) {
                        left = Assets.leftbuttonp;
                        endTouch();
                    } else if (rightrectangle.contains(touchpoint.x, touchpoint.y)) {
                        right = Assets.rightbuttonp;
                        endTouch();
                    } else if (shootrectangle.contains(touchpoint.x, touchpoint.y)) {
                        shoot = Assets.shootbuttonp;
                        endTouch();

                    }
                    return true;
            }
        }
        return false;
    }
    private void pauseTouch(){
        if(playsound) {
            Assets.movesound.setVolume(Assets.movesound.play(),0.3f);
        }
        currenttimemillis = System.currentTimeMillis();
        gamestate = GameState.started;
    }
    private void readyTouch(){
        if(playsound){
            Assets.movesound.setVolume(Assets.movesound.play(),0.3f);
        }
        timetoshoot = MathUtils.random(minshoot, maxshoot) * 1000;
        timetorotate = MathUtils.random(minrotate, maxrotate) * 1000;
        currenttimemillis = System.currentTimeMillis();
        gamestate = GameState.started;
    }
    private void endTouch(){
        if(playsound){
            Assets.movesound.setVolume(Assets.movesound.play(),0.3f);
        }
        currentscore = 0;
        level = 1;
        lives = 3;
        LevelProfile.loadlevel(this,level);
        scoreglyph.setText(Assets.numberfont, String.valueOf(currentscore));
        hiscoreglyph.setText(Assets.numberfont, String.valueOf(highscore));
        levelglyph.setText(Assets.numberfont, level < 10 ? "0" + String.valueOf(level) : String.valueOf(level));
        timetoshoot = MathUtils.random(minshoot, maxshoot) * 1000;
        timetorotate = MathUtils.random(minrotate, maxrotate) * 1000;
        ship.reset();
        enemyeye.reset();
        confusion = 1;
        confaddup = 0;
        cnfs.reset();
        freeAmmo();
        Assets.tint.setAlpha(0);
        currenttimemillis = System.currentTimeMillis();
        gamestate = GameState.started;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(touchpointer == pointer){
            resetButtons();
            touchpointer = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    private void determineUI(float camerawidth,float cameraheight){
        gameboundposition = new Vector2(camerawidth/2-Assets.gamebound.getRegionWidth()/2,cameraheight/2-Assets.gamebound.getRegionHeight()/2);
        gameboundrectangle = new Rectangle(gameboundposition.x,gameboundposition.y,Assets.gamebound.getRegionWidth(),Assets.gamebound.getRegionHeight());
        eyeborderposition = new Vector2(gameboundposition.x - (Assets.border.getRegionWidth()/2-Assets.gamebound.getRegionWidth()/2),
                                        gameboundposition.y - (Assets.border.getRegionHeight()/2-Assets.gamebound.getRegionHeight()/2));
        startmessageposition = new Vector2(gameboundposition.x + (Assets.gamebound.getRegionWidth()/2 - Assets.startmessage.getRegionWidth()/2),
                gameboundposition.y +(Assets.gamebound.getRegionWidth()/2 - Assets.startmessage.getRegionHeight()/2));
        tryagainmessageposition = new Vector2(gameboundposition.x +(Assets.gamebound.getRegionWidth()/2 - Assets.tryagainmessage.getRegionWidth()/2),
                gameboundposition.y + Assets.gamebound.getRegionHeight()- Assets.tryagainmessage.getRegionHeight() - 80);
        endscoremessageposition = new Vector2(tryagainmessageposition.x,tryagainmessageposition.y - 30 - Assets.endscoremessage.getRegionHeight());
        continuemessageposition = new Vector2(gameboundposition.x + (Assets.gamebound.getRegionWidth()/2-Assets.continuemessage.getRegionWidth()/2),
                                                (cameraheight/2 + Assets.enemy.getWidth()/2)+50);
        confusioncenterposition = new Vector2(gameboundposition.x+Assets.gamebound.getRegionWidth()/2,gameboundposition.y+Assets.gamebound.getRegionHeight()/2);
        leaderboardposition = new Vector2((gameboundposition.x - Assets.leaderboard.getRegionWidth())/2,
                                            gameboundposition.y + Assets.gamebound.getRegionHeight() - 20 - Assets.leaderboard.getRegionHeight());
        leaderboardrect.set(leaderboardposition.x,leaderboardposition.y,Assets.leaderboard.getRegionWidth(),Assets.leaderboard.getRegionHeight());
        achievementposition = new Vector2(leaderboardposition.x,leaderboardposition.y - 30 - Assets.achievement.getRegionHeight());
        achievementrect.set(achievementposition.x,achievementposition.y,Assets.achievement.getRegionWidth(),Assets.achievement.getRegionHeight());
        pauseposition = new Vector2(gameboundposition.x + Assets.gamebound.getRegionWidth() +
                                    (camerawidth - (gameboundposition.x + Assets.gamebound.getRegionWidth()) - Assets.pausebutton.getRegionWidth())/2,
                                    leaderboardposition.y);
        pauserect.set(pauseposition.x,pauseposition.y,Assets.pausebutton.getRegionWidth(),Assets.pausebutton.getRegionHeight());
        soundposition = new Vector2(pauseposition.x,pauseposition.y - 30 - Assets.mutesound.getRegionHeight());
        soundrect.set(soundposition.x,soundposition.y,Assets.mutesound.getRegionWidth(),Assets.mutesound.getRegionHeight());
        Assets.tint.setCenter(confusioncenterposition.x,confusioncenterposition.y);
        Assets.confusion1.setCenter(confusioncenterposition.x,confusioncenterposition.y);
        Assets.confusion2.setCenter(confusioncenterposition.x,confusioncenterposition.y);
        float excess = gameboundposition.y;
        float buttonsspace = (Assets.gamebound.getRegionWidth() - Assets.shootbutton.getRegionWidth() - Assets.leftbutton.getRegionWidth() - Assets.rightbutton.getRegionWidth())/2;
        leftbuttonposition = new Vector2(gameboundposition.x, (excess - Assets.leftbutton.getRegionHeight())/2);
        shootbuttonposition = new Vector2(leftbuttonposition.x + Assets.leftbutton.getRegionWidth()+buttonsspace,leftbuttonposition.y);
        rightbuttonposition = new Vector2(shootbuttonposition.x + Assets.shootbutton.getRegionWidth() + buttonsspace,leftbuttonposition.y);
        leftrectangle.set(leftbuttonposition.x,leftbuttonposition.y,Assets.leftbutton.getRegionWidth(),Assets.leftbutton.getRegionHeight());
        shootrectangle.set(shootbuttonposition.x,shootbuttonposition.y,Assets.shootbutton.getRegionWidth(),Assets.shootbutton.getRegionHeight());
        rightrectangle.set(rightbuttonposition.x,rightbuttonposition.y,Assets.rightbutton.getRegionWidth(),Assets.rightbutton.getRegionHeight());
        float titleheight = excess/2 - (Assets.title.getRegionHeight() + Assets.level.getRegionHeight() + 100)/2;
        titleposition = new Vector2(100,cameraheight-titleheight-Assets.title.getRegionHeight());
        levelposition = new Vector2(titleposition.x,titleposition.y - 50-Assets.level.getRegionHeight());
        levelnumber = new Vector2(levelposition.x,levelposition.y - 20);//flushed to left
        boolean hiscoreposboolean = ((cameraheight-(excess - Assets.hiscore.getRegionHeight() - Assets.score.getRegionHeight() - 100)/2)+Assets.hiscore.getRegionHeight())<cameraheight?true:false;
        hiscoreposition = new Vector2(camerawidth - 100 - Assets.hiscore.getRegionWidth(),
                hiscoreposboolean?(cameraheight-(excess - Assets.hiscore.getRegionHeight() - Assets.score.getRegionHeight() - 100)/2):cameraheight - Assets.score.getRegionHeight());
        hiscorenumber = new Vector2(hiscoreposition.x + Assets.hiscore.getRegionWidth(),hiscoreposition.y - (hiscoreposboolean?20:10));//flushed to right
        currentscoreposition = new Vector2(camerawidth - 100 - Assets.score.getRegionWidth(),hiscoreposition.y - (hiscoreposboolean?100:50) - Assets.score.getRegionHeight());
        currentscorenumber = new Vector2(currentscoreposition.x + Assets.score.getRegionWidth(),currentscoreposition.y - (hiscoreposboolean?20:10));//flushed to right
        livesposition = new Vector2((camerawidth - 3*Assets.lives.getRegionWidth() - 40)/2,cameraheight - excess);
    }
    private void drawUI(){
        batch.draw(Assets.border,eyeborderposition.x,eyeborderposition.y);
        batch.draw(left,leftbuttonposition.x,leftbuttonposition.y);
        batch.draw(shoot,shootbuttonposition.x,shootbuttonposition.y);
        batch.draw(right,rightbuttonposition.x,rightbuttonposition.y);
        batch.draw(Assets.title,titleposition.x,titleposition.y);
        batch.draw(Assets.level,levelposition.x,levelposition.y);
        batch.draw(Assets.hiscore,hiscoreposition.x,hiscoreposition.y);
        batch.draw(Assets.score,currentscoreposition.x,currentscoreposition.y);
        batch.draw(Assets.leaderboard,leaderboardposition.x,leaderboardposition.y);
        batch.draw(Assets.achievement,achievementposition.x,achievementposition.y);
        batch.draw(Assets.pausebutton,pauseposition.x,pauseposition.y);
        if(playsound){
            batch.draw(Assets.playsound,soundposition.x,soundposition.y);
        }else{
            batch.draw(Assets.mutesound,soundposition.x,soundposition.y);
        }
        Assets.tint.draw(batch);
        for(int x=0;x<lives;x++){
            batch.draw(Assets.lives,livesposition.x + x*(Assets.lives.getRegionWidth()+20),livesposition.y);
        }
        Assets.numberfont.draw(batch,scoreglyph,currentscorenumber.x - scoreglyph.width,currentscorenumber.y);
        Assets.numberfont.draw(batch,hiscoreglyph,hiscorenumber.x - hiscoreglyph.width,hiscorenumber.y);
        Assets.numberfont.draw(batch,levelglyph,levelnumber.x+(Assets.level.getRegionWidth() - levelglyph.width)/2,levelnumber.y);
    }

    private void ammoMaster(){
        for (ShipBullet bullet: activesbullet){
            if(bullet.impacted()){
                activesbullet.removeValue(bullet,true);
                shipbulletpool.free(bullet);
                if(bullet.state == enemyeye.rotatestate){
                    enemyeye.hit = 1;
                    currentscore += 100 + consecutivehit*4;
                    consecutivehit += 1;
                    confaddup += 100;
                    misscount = (misscount-2)>0?misscount-2:0;
                    scoreglyph.setText(Assets.numberfont,String.valueOf(currentscore));
                    if(currentscore>=requiredscore){
                        level += 1;
                        levelglyph.setText(Assets.numberfont,level>=10?String.valueOf(level):"0"+String.valueOf(level));
                        LevelProfile.loadlevel(this,level);
                    }
                    if(MathUtils.random() <= (float)confaddup/confusionscore){
                        confaddup = 0;
                        applyConfusion();
                        if(playsound){
                            Assets.confusionsound.play();
                        }
                    }
                    if(currentscore > highscore){
                        highscore = currentscore;
                        hiscoreglyph.setText(Assets.numberfont,String.valueOf(highscore));
                    }
                }else{
                    misscount += 1;
                    if(misscount >= 2){
                        consecutivehit = 0;
                    }
                    currentscore -= 10*misscount;
                    if(currentscore < 0){
                        currentscore = 0;
                    }
                    scoreglyph.setText(Assets.numberfont,String.valueOf(currentscore));
                }
            }
        }
        for (EyeBullet bullet: activeeyebullet){
            if(bullet.impacted()){
                activeeyebullet.removeValue(bullet,true);
                eyebulletpool.free(bullet);
                determineLives();
            }
        }
        for (EyeBall ball: activeeyeball){
            if(ball.impacted()){
                activeeyeball.removeValue(ball,true);
                eyeballpool.free(ball);
                determineLives();
            }
        }
        if(eyechaser.impacted()){
            eyechaser.reset();
            determineLives();
        }
    }

    private void applyConfusion(){
        cnfs.setPlayingAssets();
        playconfusion = true;
    }

    private void determineLives(){
        if((ship.invulnerability <= 0 && ship.deathanimateended)) {
            if (lives > 0) {
                lives -= 1;
                ship.startDeathAnimate();
            } else {
                gamestate = GameState.ended;
                ship.startDeathAnimate();
                readytimer = 1800;
                currenttimemillis = System.currentTimeMillis();
                prefs.putLong("com.ladinc.eyeshooter.main._highscore", highscore);
                actionResolver.submitScore((int)(currentscore));
                prefs.flush();
            }
            if(playsound) {
                Assets.deathsound.play();
            }
            }
    }
    private void aiMaster(){
        if(gamestate != GameState.started || generatinganimation){
            return;
        }
        long nexttime = System.currentTimeMillis();
        timetoshoot -= nexttime - currenttimemillis;
        timetorotate -= nexttime - currenttimemillis;
        currenttimemillis = nexttime;
        if(timetoshoot<=0) {
            if(!eyechaser.loaded && MathUtils.random() < eyechaserchances){
                eyechaser.startEyeChaserAnimation();
            }else if(MathUtils.random() < eyeballchances){
                int numberofeyeballs;
                if(level>50 && !eyechaser.loaded){
                    numberofeyeballs = MathUtils.random(1, 3);
                }else{
                    numberofeyeballs = MathUtils.random(1, 2);
                }
                int state = MathUtils.random(0, 3);
                for (int x = 0; x < numberofeyeballs; x++) {
                    EyeBall eyeball = eyeballpool.obtain();
                    activeeyeball.add(eyeball);
                    eyeball.releaseBall(state);
                    if(numberofeyeballs == 3){
                        state += 1;
                    }else {
                        state += MathUtils.random(1, 2);
                    }
                    state %= 4;
                    ////play eyeballsound?
                }
            }else{
                int numberofbullets;
                if(eyechaser.loaded && maxbullet == 3){
                    if(minbullet != 1){
                        numberofbullets = 2;
                    }else{
                        numberofbullets = MathUtils.random(minbullet, 2);
                    }
                }else{
                        numberofbullets = MathUtils.random(minbullet, maxbullet);
                    }
                int state = MathUtils.random(0, 3);
            for (int x = 0; x < numberofbullets; x++) {
                EyeBullet bullet = eyebulletpool.obtain();
                activeeyebullet.add(bullet);
                bullet.releasebullet(state);
                state += 1;
                state %= 4;
                if(playsound) {
                    Assets.enemyshoot.setVolume(Assets.enemyshoot.play(),0.3f);
                }
            }
        }
            timetoshoot = MathUtils.random(minshoot,maxshoot)*1000;
        }
        if(timetorotate<=0){
            enemyeye.setRotatestate();
            timetorotate = MathUtils.random(minrotate,maxrotate)*1000;
        }
    }
    private void resetButtons(){
        shoot = Assets.shootbutton;
        left = Assets.leftbutton;
        right = Assets.rightbutton;
    }
    private void drawGameBoundInternals(float delta){
        switch(gamestate){
            case ready:
                batch.draw(Assets.startmessage,startmessageposition.x,startmessageposition.y);
                break;
            case paused:
                drawbullets();
                ship.drawship(delta);
                enemyeye.drawenemyeye(delta);
                batch.draw(Assets.continuemessage,continuemessageposition.x,continuemessageposition.y);
                break;
            case started:
                drawbullets();
                ship.drawship(delta);
                enemyeye.drawenemyeye(delta);
                break;
            case ended:
                drawbullets();
                ship.drawship(delta);
                enemyeye.drawenemyeye(delta);
                if(readytimer<1500) batch.draw(Assets.tryagainmessage,tryagainmessageposition.x,tryagainmessageposition.y);
                if(readytimer<1000){
                    batch.draw(Assets.endscoremessage,endscoremessageposition.x,endscoremessageposition.y);
                    Assets.numberfont.draw(batch,scoreglyph,endscoremessageposition.x + Assets.endscoremessage.getRegionWidth() + 10,
                            endscoremessageposition.y + scoreglyph.height);
                }
                break;
            default: break;
        }
        Assets.tint.draw(batch);
    }
    private void memoryLoad(){
        playconfusion = prefs.getBoolean("com.ladinc.eyeshooter.main._confusiongamestate");
        generatinganimation = prefs.getBoolean("com.ladinc.eyeshooter.main._generatinganimation");
        playsound = prefs.getBoolean("com.ladinc.eyeshooter.main._playsound");
        highscore = prefs.getLong("com.ladinc.eyeshooter.main._highscore");
        currentscore = prefs.getLong("com.ladinc.eyeshooter.main._currentscore");
        level = prefs.getInteger("com.ladinc.eyeshooter.main._level");
        timetoshoot = prefs.getLong("com.ladinc.eyeshooter.main._timetoshoot");
        timetorotate = prefs.getLong("com.ladinc.eyeshooter.main._timetorotate");
        lives = prefs.getInteger("com.ladinc.eyeshooter.main._lives");
        confusion = prefs.getInteger("com.ladinc.eyeshooter.main._confusion");
        confusionscore = prefs.getLong("com.ladinc.eyeshooter.main._confusionscore");
        confaddup = prefs.getLong("com.ladinc.eyeshooter.main._confaddup");
        misscount = prefs.getInteger("com.ladinc.eyeshooter.main._misscount");
        consecutivehit = prefs.getInteger("com.ladinc.eyeshooter.main._consecutivehit");
        ship = Ship.shipMemoryload(prefs,this);
        enemyeye = EnemyEye.enemyLoad(prefs,this);
        int bulletcount = prefs.getInteger("com.ladinc.eyeshooter.main._bulletcount");
        cnfs = ConfusionStats.confusionStatsload(prefs,this);
        for(int x=0;x<bulletcount;x++){
            activesbullet.add(ShipBullet.shipBulletload(prefs,ship,enemyeye,x));
        }
        bulletcount = prefs.getInteger("com.ladinc.eyeshooter.main._enemybulletcount");
        for(int x=0;x<bulletcount;x++){
            activeeyebullet.add(EyeBullet.eyeBulletload(prefs,ship,enemyeye,x));
        }
        bulletcount = prefs.getInteger("com.ladinc.eyeshooter.main._eyeballcount");
        for(int x=0;x<bulletcount;x++){
            activeeyeball.add(EyeBall.eyeBallLoad(prefs,this,x,enemyeye,ship));
        }
        eyechaser = EyeChaser.eyeChaserLoad(prefs,this,ship,enemyeye);
    }
    private void memorySave(){
        prefs.putInteger("com.ladinc.eyeshooter._VERSIONCODE",_VERSIONCODE);
        prefs.putInteger("com.ladinc.eyeshooter.main._gamestate",-1);
        prefs.putBoolean("com.ladinc.eyeshooter.main._confusiongamestate",playconfusion);
        prefs.putBoolean("com.ladinc.eyeshooter.main._generatinganimation",generatinganimation);
        prefs.putBoolean("com.ladinc.eyeshooter.main._playsound",playsound);
        prefs.putLong("com.ladinc.eyeshooter.main._highscore", highscore);
        prefs.putLong("com.ladinc.eyeshooter.main._currentscore", currentscore);
        prefs.putInteger("com.ladinc.eyeshooter.main._level", level);
        prefs.putLong("com.ladinc.eyeshooter.main._timetoshoot",timetoshoot);
        prefs.putLong("com.ladinc.eyeshooter.main._timetorotate",timetorotate);
        prefs.putInteger("com.ladinc.eyeshooter.main._lives",lives);
        prefs.putInteger("com.ladinc.eyeshooter.main._confusion",confusion);
        prefs.putInteger("com.ladinc.eyeshooter.main._misscount",misscount);
        prefs.putInteger("com.ladinc.eyeshooter.main._consecutivehit",consecutivehit);
        prefs.putLong("com.ladinc.eyeshooter.main._confaddup",confaddup);
        ship.shipMemoryPut(prefs);
        enemyeye.enemySave(prefs);
        cnfs.confusionStatssave(prefs);
        int bulletcount = 0;
        for(ShipBullet bullet:activesbullet){
            ShipBullet.shipBulletsave(prefs,bullet,bulletcount);
            bulletcount += 1;
        }
        prefs.putInteger("com.ladinc.eyeshooter.main._bulletcount",bulletcount);
        bulletcount = 0;
        for(EyeBullet bullet:activeeyebullet){
            EyeBullet.eyeBulletsave(prefs,bullet,bulletcount);
            bulletcount += 1;
        }
        prefs.putInteger("com.ladinc.eyeshooter.main._enemybulletcount",bulletcount);
        bulletcount = 0;
        for(EyeBall eyeball:activeeyeball){
            EyeBall.eyeBallSave(prefs,eyeball,bulletcount);
            bulletcount += 1;
        }
        prefs.putInteger("com.ladinc.eyeshooter.main._eyeballcount",bulletcount);
        eyechaser.eyeChaserSave(prefs);
        prefs.flush();
    }
    public enum GameState{
        ready,started,ended,paused
    }

    public void freeAmmo(){
        eyebulletpool.freeAll(activeeyebullet);
        eyebulletpool.clear();
        eyeballpool.freeAll(activeeyeball);
        activeeyeball.clear();
        eyechaser.reset();
    }

    public interface ActionResolver {
        public void signIn();
        public void signOut();
        public void rateGame();
        public void unlockAchievement(int number);
        public void submitScore(int highScore);
        public void showAchievement();
        public void showScore();
        public boolean isSignedIn();
    }
}
