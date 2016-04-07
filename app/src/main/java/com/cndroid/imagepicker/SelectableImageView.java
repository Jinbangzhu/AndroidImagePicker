package com.cndroid.imagepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by jinbangzhu on 1/18/16.
 */
public class SelectableImageView extends ImageView {

    private boolean isSelected, touched;
    private int selectedColor;

    public SelectableImageView(Context context) {
        super(context);
        initial(context, null);
    }

    public SelectableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial(context, attrs);
    }

    public SelectableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial(context, attrs);
    }

    private void initial(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SelectableImageView, 0, 0);
        try {
            selectedColor = typedArray.getColor(R.styleable.SelectableImageView_pi_selectedColor, getResources().getColor(R.color.pickup_image_selected_image_bg));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            typedArray.recycle();
        }
    }

    public void isSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (touched || isSelected) canvas.drawColor(selectedColor);
    }

    public void setTapListener(ITapListener iTapListener) {
        this.tapListener = iTapListener;
    }

    private ITapListener tapListener;

    public interface ITapListener {
        void onTaped();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d("onTouchEvent", " event = " + event.getAction());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touched = true;
            invalidate();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            touched = false;
            invalidate();
            if (null != tapListener)
                tapListener.onTaped();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            touched = false;
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
