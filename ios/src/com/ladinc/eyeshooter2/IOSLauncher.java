package com.ladinc.eyeshooter2;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

public class IOSLauncher extends IOSApplication.Delegate implements EyeShooter.ActionResolver {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(new EyeShooter(this), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    @Override
    public void signIn() {

    }

    @Override
    public void signOut() {

    }

    @Override
    public void rateGame() {

    }

    @Override
    public void unlockAchievement(int number) {

    }

    @Override
    public void submitScore(int highScore) {

    }

    @Override
    public void showAchievement() {

    }

    @Override
    public void showScore() {

    }

    @Override
    public boolean isSignedIn() {
        return false;
    }
}