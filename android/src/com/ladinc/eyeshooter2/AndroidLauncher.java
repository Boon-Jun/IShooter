package com.ladinc.eyeshooter2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.games.Games;
import com.ladinc.eyeshooter2.basegameutils.GameHelper;

public class AndroidLauncher extends AndroidApplication implements com.ladinc.eyeshooter2.EyeShooter.ActionResolver,GameHelper.GameHelperListener {
	private GameHelper gameHelper;
	private final static int requestCode = 1;
    private RelativeLayout layout;
    private AdView adview;
    private View gameView;

    @Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.hideStatusBar = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.enableDebugLog(false);
        gameHelper.setup(this);
        layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        MobileAds.initialize(this,getString(R.string.my_adid));
        createAdView();
        createGameView(config);
        layout.addView(adview);
        layout.addView(gameView);
        setContentView(layout);
	}

    private AdView createAdView(){
        adview = new AdView(this);
        adview.setAdSize(AdSize.SMART_BANNER);
        adview.setAdUnitId(getString(R.string.banadid));
        adview.setId(R.id.adViewId);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        adview.setLayoutParams(params);
        adview.loadAd(new AdRequest.Builder().build());
        return adview;
    }

    private View createGameView(AndroidApplicationConfiguration cfg){
        gameView = initializeForView(new com.ladinc.eyeshooter2.EyeShooter(this), cfg);
        RelativeLayout.LayoutParams lpm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpm.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE );
        lpm.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        lpm.addRule(RelativeLayout.BELOW, adview.getId());
        gameView.setLayoutParams(lpm);
        return gameView;
    }
    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHelper.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void signIn() {
        try
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    gameHelper.beginUserInitiatedSignIn();
                }
            });
        }
        catch (Exception e)
        {
        }
    }

    @Override
    public void signOut() {
        try
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    gameHelper.signOut();
                }
            });
        }
        catch (Exception e)
        {
        }
    }


    @Override
    public void rateGame() {

    }

    @Override
    public void unlockAchievement(int number) {
        if (isSignedIn() == true) {
            switch (number) {
                case 1:
                    Games.Achievements.unlock(gameHelper.getApiClient(), getString(R.string.achievement1));
                    break;
                case 2:
                    Games.Achievements.unlock(gameHelper.getApiClient(), getString(R.string.achievement2));
                    break;
                case 3:
                    Games.Achievements.unlock(gameHelper.getApiClient(), getString(R.string.achievement3));
                    break;
                case 4:
                    Games.Achievements.unlock(gameHelper.getApiClient(), getString(R.string.achievement4));
                    break;
                case 5:
                    Games.Achievements.unlock(gameHelper.getApiClient(), getString(R.string.achievement5));
                    break;
                default:
                    break;

            }
        }
    }

    @Override
    public void submitScore(int highScore) {
        if (isSignedIn() == true)
        {
            Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                    getString(R.string.highscores), highScore);
        }
    }

    @Override
    public void showAchievement() {
        if (isSignedIn() == true)
        {
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
        }
        else
        {
            signIn();
        }
    }

    @Override
    public void showScore() {
        if (isSignedIn() == true)
        {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                    getString(R.string.highscores)), requestCode);
        }
        else
        {
            signIn();
        };
    }

    @Override
    public boolean isSignedIn() {
        return gameHelper.isSignedIn();
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }
}
