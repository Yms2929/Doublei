package com.example.doublei.GraphLibrary;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.util.AttributeSet;
import android.view.View;

import com.example.doublei.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class CharterLabelsBase extends View {
    public static final int VERTICAL_GRAVITY_TOP = 0;
    public static final int VERTICAL_GRAVITY_CENTER = 1;
    public static final int VERTICAL_GRAVITY_BOTTOM = 2;
    public static final int HORIZONTAL_GRAVITY_LEFT = 0;
    public static final int HORIZONTAL_GRAVITY_CENTER = 1;
    public static final int HORIZONTAL_GRAVITY_RIGHT = 2;
    private static final int DEFAULT_VERTICAL_GRAVITY = VERTICAL_GRAVITY_CENTER;
    private static final int DEFAULT_HORIZONTAL_GRAVITY = HORIZONTAL_GRAVITY_LEFT;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VERTICAL_GRAVITY_TOP, VERTICAL_GRAVITY_CENTER, VERTICAL_GRAVITY_BOTTOM})
    public @interface VerticalGravity {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HORIZONTAL_GRAVITY_LEFT, HORIZONTAL_GRAVITY_CENTER, HORIZONTAL_GRAVITY_RIGHT})
    public @interface HorizontalGravity {
    }

    Paint paintLabel;
    boolean[] visibilityPattern;
    int verticalGravity;
    int horizontalGravity;
    String[] values;
    boolean stickyEdges;
    boolean allCaps;
    int truecount=1;

    protected CharterLabelsBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Charter);
        Resources res = getResources();
        stickyEdges = typedArray.getBoolean(R.styleable.Charter_c_stickyEdges,
                res.getBoolean(R.bool.default_stickyEdges));
        verticalGravity =
                typedArray.getInt(R.styleable.Charter_c_verticalGravity, DEFAULT_VERTICAL_GRAVITY);
        horizontalGravity =
                typedArray.getInt(R.styleable.Charter_c_horizontalGravity, DEFAULT_HORIZONTAL_GRAVITY);
        int paintLabelColor = typedArray.getColor(R.styleable.Charter_c_labelColor,
                res.getColor(R.color.default_labelColor));
        float paintLabelSize = typedArray.getDimension(R.styleable.Charter_c_labelSize,
                getResources().getDimension(R.dimen.default_labelSize));
        allCaps = typedArray.getBoolean(R.styleable.Charter_c_labelAllCaps,
                res.getBoolean(R.bool.default_labelAllCaps));
        typedArray.recycle();

        paintLabel = new Paint();
        paintLabel.setAntiAlias(true);
        paintLabel.setColor(paintLabelColor);
        paintLabel.setTextSize(paintLabelSize);

        visibilityPattern = new boolean[]{true, true, true, true, true, true, true};
        truecount=1;
    }

    public void setStickyEdges(boolean stickyEdges) {
        this.stickyEdges = stickyEdges;
        invalidate();
    }

    public void setValues(String[] values) {
        if (values == null || values.length == 0) {
            return;
        }

        this.values = values;
        invalidate();
    }

    public void setValues(float[] values, boolean summarize) {
        if (summarize) {
            values = summarize(values);
        }
        setValues(floatArrayToStringArray(values));
    }

    private String[] floatArrayToStringArray(float[] values) {
        if (values == null) {
            return new String[]{};
        }

        String[] stringArray = new String[values.length];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = String.valueOf((int) values[i]);
        }
        return stringArray;
    }

    private float[] summarize(float[] values) {
        if (values == null) {
            return new float[]{};
        }
        boolean[] visibilityPatterns = new boolean[]{true, true, true, true, true, true, true};
        int big;
        float temp;
        boolean same = true;
        truecount=1;
        for (int i = 0; i < 7; i++) {
            big = 0;
            for (int j = 1; j < 7 - i; j++) {
                if (values[big] < values[j]) {
                    big = j;
                }
            }
            if (big != 6 - i) {
                temp = values[6 - i];
                values[6 - i] = values[big];
                values[big] = temp;
            }
        }

        for (int i = 1; i < 7; i++) {
            if (values[i] == values[6]){
                for(int j=i;j<6;j++) {
                    visibilityPatterns[i] = false;
                }
                visibilityPatterns[6] = true;
                truecount++;
                break;
            }
            if (values[i - 1] == values[i]) {
                same = false;
            }
            if (!same) {
                visibilityPatterns[i] = same;
                same=true;
            } else {
                visibilityPatterns[i] = same;
                truecount++;
            }
        }
        if (values[6] == 0)
            return new float[]{0};
        visibilityPattern[0] = visibilityPatterns[0];
        visibilityPattern[1] = visibilityPatterns[5];
        visibilityPattern[2] = visibilityPatterns[4];
        visibilityPattern[3] = visibilityPatterns[3];
        visibilityPattern[4] = visibilityPatterns[2];
        visibilityPattern[5] = visibilityPatterns[1];
        visibilityPattern[6] = visibilityPatterns[6];
        return new float[]{values[0], values[5], values[4], values[3], values[2], values[1], values[6]};

//        return new float[]{0, (diff / 5) * 4, diff / 2, diff / 5, max};
//        return new float[]{0, diff / 5, diff / 2, (diff / 5) * 4, max};//1 4 3 2 5
    }
}
