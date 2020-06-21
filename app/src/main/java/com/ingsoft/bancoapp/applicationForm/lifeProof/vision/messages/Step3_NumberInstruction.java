package com.ingsoft.bancoapp.applicationForm.lifeProof.vision.messages;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.ingsoft.bancoapp.applicationForm.lifeProof.vision.GraphicOverlay;

public class Step3_NumberInstruction extends GraphicOverlay.Graphic {
    private Integer number;

    public Step3_NumberInstruction(GraphicOverlay overlay, Integer number) {
        super(overlay);
        this.number=number;
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
        int frameHeight = (int) viewHeight * 3 / 4;

        int top = center - (frameHeight / 2);
        int bottom = center + (frameHeight / 2 );

        RectF innerRectangle = new RectF(left, top, right, bottom);
        canvas.drawRect(innerRectangle, paint);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(5);
        paint.setColor(Color.WHITE);
        paint.setTextSize(150);

        String text = number.toString();
        Utils.drawText(text, canvas, paint, width, (left+right)/2-30, center);

    }
}
