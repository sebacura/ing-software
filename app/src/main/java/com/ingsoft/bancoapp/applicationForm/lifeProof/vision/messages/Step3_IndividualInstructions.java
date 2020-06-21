package com.ingsoft.bancoapp.applicationForm.lifeProof.vision.messages;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.ingsoft.bancoapp.applicationForm.lifeProof.vision.GraphicOverlay;

public class Step3_IndividualInstructions extends GraphicOverlay.Graphic {
    private String eye;
    private boolean taskCompleted;
    private boolean lastInstruction;
    private int step;

    public Step3_IndividualInstructions(GraphicOverlay overlay, String eye, int step, boolean lastInstruction) {
        super(overlay);
        this.eye = eye;
        taskCompleted = false;
        this.step = step;
        this.lastInstruction = lastInstruction;
    }

    public String getEye() {
        return eye;
    }

    public boolean completeTask(){
        taskCompleted = true;
        if(lastInstruction){
            return true;
        }else{
            return false;
        }
    }

    public boolean taskCompleted(){
        return taskCompleted;
    }

    @Override
    public void draw(Canvas canvas) {
        int viewHeight = canvas.getHeight();

        RectF outerRectangle = new RectF(0, 0, canvas.getWidth(), viewHeight);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(0, 0, 0, 0));
        canvas.drawRect(outerRectangle, paint);

        paint.setColor(Color.argb(100, 0, 0, 0));

        int sizeInPixel = 130;

        int center = viewHeight / 2;

        int left = sizeInPixel;
        int right = canvas.getWidth() - sizeInPixel;
        int width = right - left;
       int frameHeight = (int) viewHeight / 4;

        int top = center + (frameHeight / 2);
        int bottom = center + (frameHeight * 3 / 2 );

        RectF innerRectangle = new RectF(left, top, right, bottom);
        canvas.drawRect(innerRectangle, paint);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(5);
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);

        String text = "Paso: " + step + "\nPestañee con el ojo " + eyeInSpanish(eye) + ".";
        Utils.drawText(text, canvas, paint, width, left, (top+bottom)/2);
    }

    private String eyeInSpanish(String eye){
        String ojo = null;
        if(eye=="Left"){
            ojo = "izquierdo";
        } else if(eye == "Right") {
            ojo = "derecho";
        }
        return ojo;
    }
}
