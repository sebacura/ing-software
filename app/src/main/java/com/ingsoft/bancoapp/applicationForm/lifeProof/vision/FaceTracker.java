/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ingsoft.bancoapp.applicationForm.lifeProof.vision;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.ingsoft.bancoapp.applicationForm.lifeProof.EyesActivity;
import com.ingsoft.bancoapp.applicationForm.lifeProof.vision.messages.*;

import java.util.HashMap;
import java.util.Map;



/**
 * Tracks the eye positions and state over time, managing an underlying graphic which renders googly
 * eyes over the source video.<p>
 * <p>
 * To improve eye tracking performance, it also helps to keep track of the previous landmark
 * proportions relative to the detected face and to interpolate landmark positions for future
 * updates if the landmarks are missing.  This helps to compensate for intermediate frames where the
 * face was detected but one or both of the eyes were not detected.  Missing landmarks can happen
 * during quick movements due to camera image blurring.
 */
public class FaceTracker extends Tracker<Face> {
    private static final float EYE_CLOSED_THRESHOLD = 0.4f;

    private EyesActivity eyesActivity;
    private GraphicOverlay mOverlay;
    private TimerMgmt mTimerMgmt;
    private String status;  //Possible values are: Initial, Secondary, Steps, StepNumbers

    // messages
    private Step1_LookAtCameraMessage mStep1LookAtCameraMessage;
    private Step2_InstructionsMessage mStep2InstructionsMessage;
    private Step3_IndividualInstructions mStep3IndividualInstructions;
    private Step3_NumberInstruction mStep3NumberInstruction;

    // Record the previously seen proportions of the landmark locations relative to the bounding box
    // of the face.  These proportions can be used to approximate where the landmarks are within the
    // face bounding box if the eye landmark is missing in a future update.
    @SuppressLint("UseSparseArrays")
    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();

    // Similarly, keep track of the previous eye open state so that it can be reused for
    // intermediate frames which lack eye landmarks and corresponding eye state.
    private boolean mPreviousIsLeftOpen = true;
    private boolean mPreviousIsRightOpen = true;

    //This variable is used to determine when no face has been detected for a long time.
    private boolean faceWasSeen;
    private boolean livingPersonRecognized = false;

    //Blink detection from start
    private boolean eyesCurrentlyOpen;
    private int amountOfBlinksSinceStart;
    private final int acceptableAmountOfBlinks = 3;

    //Steps
    private int currentInstruction;
    private String[] instructions;
    private final int amountOfInstructions = 3;
    private boolean stepsTimerDone = false;
    private boolean couldNotTakePhoto = false;

    //==============================================================================================
    // Methods
    //==============================================================================================

    // @RequiresApi(api = Build.VERSION_CODES.O)
    public FaceTracker(EyesActivity ea, GraphicOverlay overlay) {
        eyesActivity = ea;
        mOverlay = overlay;
        status = "Initial";
        faceWasSeen = false;
        eyesCurrentlyOpen = true;
        amountOfBlinksSinceStart = 0;
        mTimerMgmt = new TimerMgmt(this, eyesActivity);
    }

    /**
     * Resets the underlying googly eyes graphic and associated physics state.
     */
    @Override
    public void onNewItem(int id, Face face) {
        //mEyesGraphics = new EyesGraphics(mOverlay);
    }

    /**
     * Updates the positions and state of eyes to the underlying graphic, according to the most
     * recent face detection results.  The graphic will render the eyes and simulate the motion of
     * the iris based upon these changes over time.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        faceWasSeen = true;
        mTimerMgmt.stopNoFaceTimer();

        boolean eyesOpen[] = updateEyeStatus(face);
        boolean isLeftOpen = eyesOpen[0];
        boolean isRightOpen = eyesOpen[1];

        if(status == "Initial" || status == "Secondary"){
            updateOverlayPrimarySteps(status);
            if(eyesCurrentlyOpen){
                if(!isLeftOpen && !isRightOpen){
                    eyesCurrentlyOpen = false;
                }
            } else {
                if(isLeftOpen && isRightOpen){
                    eyesCurrentlyOpen = true;
                    amountOfBlinksSinceStart ++;
                    System.out.println("Amount of blinks: " + amountOfBlinksSinceStart);
                }
            }
        } else if (status == "Steps") {
           // if (mStep3IndividualInstructions == null) {
                System.out.println("Actualizando que se reconocio persona: " + amountOfBlinksSinceStart + "accept: "+ acceptableAmountOfBlinks);
                if (amountOfBlinksSinceStart >= acceptableAmountOfBlinks) {
                    livingPersonRecognized = true;
                }
               // updateStep();
           // }
            //Check if the step is fulfilled
            if (mStep3IndividualInstructions.getEye() == "Left") {
                if (!isLeftOpen && isRightOpen) {
                    stepFulfilled();
                }
            } else if (mStep3IndividualInstructions.getEye() == "Right") {
                if (isLeftOpen && !isRightOpen) {
                    stepFulfilled();
                }
            }
        } else if(status == "StepNumbers" && mStep3NumberInstruction==null){
            if(isLeftOpen && isRightOpen) {
                eyesActivity.saveCurrentImage();
            }else{
                couldNotTakePhoto = true;
            }
            currentInstruction = 0;
            instructions = generateInstructions();
            updateStep();
        }
        if(couldNotTakePhoto){
            if(isLeftOpen && isRightOpen){
                eyesActivity.saveCurrentImage();
                couldNotTakePhoto = false;
            }
        }
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        if (status == "Initial" && mStep1LookAtCameraMessage==null){
            mStep1LookAtCameraMessage = new Step1_LookAtCameraMessage(mOverlay);
            mOverlay.add(mStep1LookAtCameraMessage);
            mTimerMgmt.startTimer(status);
        } else if (faceWasSeen) {
            faceWasSeen = false;
            mTimerMgmt.startNoFaceTimer();
        }

        //mOverlay.remove(mEyesGraphics);
        //mOverlay.add(mLookAtCameraMessage);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the googly eyes graphic from
     * the overlay.
     */
    @Override
    public void onDone() {
        //mOverlay.remove(mEyesGraphics);
        //mOverlay.add(mLookAtCameraMessage);
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    private void updatePreviousProsportions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    /**
     * Finds a specific landmark position, or approximates the position based on past observations
     * if it is not present.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF prop = mPreviousProportions.get(landmarkId);
        if (prop == null) {
            return null;
        }

        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }

    private boolean[] updateEyeStatus(Face face){
        boolean isOpen[] = new boolean[2];
        updatePreviousProsportions(face);

        PointF leftPosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
        PointF rightPosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);

        float leftOpenScore = face.getIsRightEyeOpenProbability();

        if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isOpen[0] = mPreviousIsLeftOpen;
        } else {
            isOpen[0] = (leftOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsLeftOpen = isOpen[0];
        }
        float rightOpenScore = face.getIsLeftEyeOpenProbability();
        if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isOpen[1] = mPreviousIsRightOpen;
        } else {
            isOpen[1] = (rightOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsRightOpen = isOpen[1];
        }
        return isOpen;
    }

    private void stepFulfilled(){
        boolean last = mStep3IndividualInstructions.completeTask();
        if(stepsTimerDone) {
            if (last) {
                int resultCode;
                System.out.println("En last, living person recognized: " + livingPersonRecognized);
                if(livingPersonRecognized){
                    resultCode = 1;
                } else {
                    resultCode = 2;
                }
                //System.out.println("Get image" + eyesActivity.getImage());

                Intent intent = new Intent();
                // intent.putExtra("IMAGE", eyesActivity.getImage());
                eyesActivity.setResult(resultCode, intent);
                eyesActivity.finish();
            } else {
                status = "StepNumbers";
                stepsTimerDone = false;
                currentInstruction++;
                updateStep();
            }
        }
    }

    private String[] generateInstructions(){
        String[] instructions = new String[amountOfInstructions];
        for(int i=0; i<instructions.length; i++){
            if(Math.random()<0.5){
                instructions[i] = "Left";
            } else {
                instructions[i] = "Right";
            }
        }
        return instructions;
    }

    private void updateOverlayPrimarySteps(String status){
        if(status == "Initial" && mStep1LookAtCameraMessage==null){
            mStep1LookAtCameraMessage = new Step1_LookAtCameraMessage(mOverlay);
            mOverlay.add(mStep1LookAtCameraMessage);
            mTimerMgmt.startTimer(status);
        } else if (status == "Secondary" && mStep2InstructionsMessage == null) {
            mOverlay.remove(mStep1LookAtCameraMessage);
            mStep2InstructionsMessage = new Step2_InstructionsMessage(mOverlay);
            mOverlay.add(mStep2InstructionsMessage);
            mTimerMgmt.startTimer(status);
        }
    }

    private void updateStep(){
        mOverlay.clear();
        mStep3NumberInstruction = new Step3_NumberInstruction(mOverlay, currentInstruction+1);
        mOverlay.add(mStep3NumberInstruction);
        mTimerMgmt.startTimer(status);
    }

    public void updateStatus(String newStatus) {
        status = newStatus;
    }

    public void stepsTimerDone(){
        stepsTimerDone = true;
    }

    public void stepsNumberTimerDone(){
        status = "Steps";
        mOverlay.clear();
        mStep3IndividualInstructions = new Step3_IndividualInstructions(mOverlay, instructions[currentInstruction], currentInstruction+1,currentInstruction==amountOfInstructions-1);
        mOverlay.add(mStep3IndividualInstructions);
        mTimerMgmt.startTimer(status);
       // stepsTimerDone = false;

    }
}