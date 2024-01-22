package com.drivepdfviewer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PageView extends View {
    private Bitmap bitmap;

    private Paint paint;

    public PageView(Context context) {
        super(context);
        init();
    }

    public PageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize Paint for smooth rendering
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        // Initialize your bitmap here, for example, loading it from resources
        // bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.your_image);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate(); // Trigger a redraw when the bitmap is set
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the bitmap if it exists
        if (bitmap != null) {
            // Calculate the destination rectangle for the bitmap
            float left = (getWidth() - bitmap.getWidth()) / 2f;
            float top = (getHeight() - bitmap.getHeight()) / 2f;

            // Draw the bitmap with the Paint settings
            canvas.drawBitmap(bitmap, left, top, paint);
        }
    }
}