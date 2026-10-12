
package com.example.mad3new;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DiceView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int number = 1;
    private int dieColor = Color.WHITE;

    public DiceView(Context context) {
        super(context);
        init();
    }

    public DiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
    }

    public void setDieColor(int color) {
        dieColor = color;
        invalidate();
    }

    public void setNumber(int number) {
        this.number = Math.max(1, Math.min(6, number));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float size = Math.min(getWidth(), getHeight()) * 0.82f;
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        float half = size / 2f;
        float radius = size * 0.09f;

        RectF rect = new RectF(
                centerX - half,
                centerY - half,
                centerX + half,
                centerY + half
        );

        // Colored dice face.
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(dieColor);
        paint.setShadowLayer(5f, 0f, 3f, Color.GRAY);
        canvas.drawRoundRect(rect, radius, radius, paint);
        paint.clearShadowLayer();

        // Border.
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Math.max(2f, size * 0.025f));
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(rect, radius, radius, paint);

        // Dots.
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);

        float offset = size * 0.27f;
        float dotRadius = size * 0.065f;

        float left = centerX - offset;
        float right = centerX + offset;
        float top = centerY - offset;
        float bottom = centerY + offset;

        switch (number) {
            case 1:
                drawDot(canvas, centerX, centerY, dotRadius);
                break;

            case 2:
                drawDot(canvas, left, top, dotRadius);
                drawDot(canvas, right, bottom, dotRadius);
                break;

            case 3:
                drawDot(canvas, left, top, dotRadius);
                drawDot(canvas, centerX, centerY, dotRadius);
                drawDot(canvas, right, bottom, dotRadius);
                break;

            case 4:
                drawDot(canvas, left, top, dotRadius);
                drawDot(canvas, right, top, dotRadius);
                drawDot(canvas, left, bottom, dotRadius);
                drawDot(canvas, right, bottom, dotRadius);
                break;

            case 5:
                drawDot(canvas, left, top, dotRadius);
                drawDot(canvas, right, top, dotRadius);
                drawDot(canvas, centerX, centerY, dotRadius);
                drawDot(canvas, left, bottom, dotRadius);
                drawDot(canvas, right, bottom, dotRadius);
                break;

            case 6:
                drawDot(canvas, left, top, dotRadius);
                drawDot(canvas, right, top, dotRadius);
                drawDot(canvas, left, centerY, dotRadius);
                drawDot(canvas, right, centerY, dotRadius);
                drawDot(canvas, left, bottom, dotRadius);
                drawDot(canvas, right, bottom, dotRadius);
                break;
        }
    }

    private void drawDot(
            Canvas canvas, float x, float y, float radius) {
        canvas.drawCircle(x, y, radius, paint);
    }
}
