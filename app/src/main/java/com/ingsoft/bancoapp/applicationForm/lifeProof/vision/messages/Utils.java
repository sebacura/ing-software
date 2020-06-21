package com.ingsoft.bancoapp.applicationForm.lifeProof.vision.messages;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static List<String> getLines(String text, Paint paint, int width){
        List<String> substrings = new ArrayList<String>();
        while(!text.isEmpty()) {
            if(text.charAt(0) == ' '){
                text=text.substring(1);
            }
            int amountOfChars = paint.breakText(text, true, width - 40, null);
            while(amountOfChars < text.length() && text.charAt(amountOfChars) != ' '){
                amountOfChars--;
            }
            substrings.add(text.substring(0,amountOfChars));
            text = text.substring(amountOfChars);
            System.out.println(text);
        }
        return substrings;
    }

    public static void drawText(String text, Canvas canvas, Paint paint, int width, int left, int center){
        List<String> substrings = getLines(text,paint, width);
        float lineHeight = paint.getFontMetrics().descent - paint.getFontMetrics().ascent;
        float totalTextHeight = lineHeight * substrings.size();
        for(int i=0; i<substrings.size(); i++){
            canvas.drawText(substrings.get(i), left + 20, center-(totalTextHeight/2) + i*lineHeight, paint);
        }
    }
}
