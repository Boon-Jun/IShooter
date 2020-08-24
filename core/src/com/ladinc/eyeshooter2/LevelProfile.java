package com.ladinc.eyeshooter2;


public class LevelProfile {

    public static void loadlevel(EyeShooter context,int level) {
        context.requiredscore = 600 * level + (level - 1) * 100;
        context.minbullet = 1 + (level / 13 > 2 ? 2 : level / 13);
        context.maxbullet = 1 + (level / 3 > 2 ? 2 : level / 3);
        context.minrotate = 6 - (level / 2 > 3 ? 3 : level / 2);
        context.maxrotate = 8 - (level / 7 > 4 ? 4 : level / 7);
        context.minshoot = 5 - (level / 5 > 2 ? 2 : level / 5);
        context.maxshoot = 7 - (level / 11 > 4 ? 4 : level / 11);
        context.eyeballchances = (level*0.01>0.35?0.35f:level*0.01f);
        context.eyechaserchances = (level*0.01>0.20?0.20f:level*0.01f);
        context.confusionscore = 45000 - (level*100<35000?level*100:35000);
        if(level >= 10){
            context.actionResolver.unlockAchievement(1);
        }else{
            return;
        }
        if(level>= 30){
            context.actionResolver.unlockAchievement(2);
        }else{
            return;
        }
        if(level>= 50){
            context.actionResolver.unlockAchievement(3);
        }else{
            return;
        }
        if(level>= 70){
            context.actionResolver.unlockAchievement(4);
        }else{
            return;
        }
        if(level>= 100){
            context.actionResolver.unlockAchievement(5);
        }

    }
}
