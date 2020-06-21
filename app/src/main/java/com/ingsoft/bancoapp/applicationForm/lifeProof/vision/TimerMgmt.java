package com.ingsoft.bancoapp.applicationForm.lifeProof.vision;

import com.ingsoft.bancoapp.applicationForm.lifeProof.EyesActivity;

import java.util.Timer;
import java.util.TimerTask;



public class TimerMgmt {
    private EyesActivity eyesActivity;
    private FaceTracker faceTracker;

    private final int secondsFirstMessage = 3;
    private Timer mTimerStep1;
    private final int secondsSecondMessage = 8;
    private Timer mTimerStep2;
    private final int secondsNumberMessage = 1;
    private Timer mTimerStep3Number;
    private boolean showedNumber = false;
    private final int secondsStepsMessage = 2;
    private Timer mTimerStep3;
    private final int secondsNoFaceFail = 10;
    private Timer mNoFaceTimer;

    public TimerMgmt(FaceTracker ft, EyesActivity ea){
        faceTracker = ft;
        eyesActivity = ea;
    }

    public void startTimer(String status){
        if(status=="Initial"){
            mTimerStep1 = new Timer();
            mTimerStep1.schedule(new FirstMessageUpTask(), secondsFirstMessage*1000);
        } else if(status == "Secondary"){
            mTimerStep2 = new Timer();
            mTimerStep2.schedule(new SecondMessageUpTask(), secondsSecondMessage*1000);
        } else if(status == "Steps"){
            mTimerStep3 = new Timer();
            mTimerStep3.schedule(new StepsMessageUpTask(), secondsStepsMessage * 1000);
        } else if(status == "StepNumbers"){
            mTimerStep3Number = new Timer();
            mTimerStep3Number.schedule(new StepsNumberMessageUpTask(), secondsNumberMessage * 1000);
            showedNumber = true;
        }
    }

    public void startNoFaceTimer(){
        mNoFaceTimer = null;
        mNoFaceTimer = new Timer();
        mNoFaceTimer.schedule(new NoFaceTask(), secondsNoFaceFail*1000);
    }

    public void stopNoFaceTimer(){
        if(mNoFaceTimer != null){
            mNoFaceTimer.cancel();
        }
    }

    class FirstMessageUpTask extends TimerTask {
        public void run(){
            mTimerStep1.cancel();
            faceTracker.updateStatus("Secondary");
            startNoFaceTimer();
        }
    }
    class SecondMessageUpTask extends TimerTask {
        public void run(){
            mTimerStep2.cancel();
            faceTracker.updateStatus("StepNumbers");
        }
    }
    class StepsMessageUpTask extends TimerTask {
        public void run(){
            mTimerStep3.cancel();
            faceTracker.stepsTimerDone();
        }
    }
    class StepsNumberMessageUpTask extends TimerTask {
        public void run(){
            mTimerStep3Number.cancel();
            faceTracker.stepsNumberTimerDone();
        }
    }
    class NoFaceTask extends TimerTask {
        public void run(){
            mNoFaceTimer.cancel();
            eyesActivity.finish();
        }
    }

}
