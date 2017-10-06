package com.example.doublei.GraphLibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

public class CharterYLabels extends CharterLabelsBase {
    public CharterYLabels(Context context) {
        this(context, null);
    }

    public CharterYLabels(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CharterYLabels(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (values == null || values.length == 0) {
            return;
        }

        final int valuesLength = values.length;

        final float height = getMeasuredHeight();
        final float width = getMeasuredWidth();

        final float gap = height / (truecount - 1);

        int yNumCount=0;
        float x;
        float y;

        for (int i = 0; i < valuesLength; i++) {
            String value = allCaps ? values[i].toUpperCase() : values[i];

            if (visibilityPattern[i]) {
                Rect textBounds = new Rect();
                paintLabel.getTextBounds(value, 0, value.length(), textBounds);
                int textHeight = 2 * textBounds.bottom - textBounds.top;
                float textWidth = textBounds.right;

                switch (horizontalGravity) {
                    default:
                        // HORIZONTAL_GRAVITY_LEFT
                        x = 0;
                        break;

                    case HORIZONTAL_GRAVITY_CENTER:
                        x = (width - textWidth) / 2;
                        break;

                    case HORIZONTAL_GRAVITY_RIGHT:
                        x = width - textWidth;
                        break;
                }

                if (yNumCount == 0) {
                    y = height;
                    yNumCount++;
                } else if (yNumCount == truecount-1) {
                    y = textHeight;
                    yNumCount++;
                } else {
                    y = gap * yNumCount + (textHeight / 2);
                    yNumCount++;
                }
                canvas.drawText(value, x, y, paintLabel);
            }
        }
    }
}
