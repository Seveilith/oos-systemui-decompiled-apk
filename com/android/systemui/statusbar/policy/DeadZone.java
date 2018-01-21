package com.android.systemui.statusbar.policy;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.MotionEvent;
import android.view.View;
import com.android.systemui.R.styleable;

public class DeadZone
  extends View
{
  private final Runnable mDebugFlash = new Runnable()
  {
    public void run()
    {
      ObjectAnimator.ofFloat(DeadZone.this, "flash", new float[] { 1.0F, 0.0F }).setDuration(150L).start();
    }
  };
  private int mDecay;
  private float mFlashFrac = 0.0F;
  private int mHold;
  private long mLastPokeTime;
  private boolean mShouldFlash;
  private int mSizeMax;
  private int mSizeMin;
  private boolean mVertical;
  
  public DeadZone(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public DeadZone(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.DeadZone, paramInt, 0);
    this.mHold = paramAttributeSet.getInteger(2, 0);
    this.mDecay = paramAttributeSet.getInteger(3, 0);
    this.mSizeMin = paramAttributeSet.getDimensionPixelSize(0, 0);
    this.mSizeMax = paramAttributeSet.getDimensionPixelSize(1, 0);
    if (paramAttributeSet.getInt(4, -1) == 1) {}
    for (;;)
    {
      this.mVertical = bool;
      setFlashOnTouchCapture(paramContext.getResources().getBoolean(2131558417));
      return;
      bool = false;
    }
  }
  
  private float getSize(long paramLong)
  {
    if (this.mSizeMax == 0) {
      return 0.0F;
    }
    paramLong -= this.mLastPokeTime;
    if (paramLong > this.mHold + this.mDecay) {
      return this.mSizeMin;
    }
    if (paramLong < this.mHold) {
      return this.mSizeMax;
    }
    return (int)lerp(this.mSizeMax, this.mSizeMin, (float)(paramLong - this.mHold) / this.mDecay);
  }
  
  static float lerp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (paramFloat2 - paramFloat1) * paramFloat3 + paramFloat1;
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    if ((!this.mShouldFlash) || (this.mFlashFrac <= 0.0F)) {
      return;
    }
    int i = (int)getSize(SystemClock.uptimeMillis());
    if (this.mVertical) {}
    for (int j = i;; j = paramCanvas.getWidth())
    {
      if (this.mVertical) {
        i = paramCanvas.getHeight();
      }
      paramCanvas.clipRect(0, 0, j, i);
      paramCanvas.drawARGB((int)(255.0F * this.mFlashFrac), 221, 238, 170);
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = 1;
    if (paramMotionEvent.getToolType(0) == 3) {
      return false;
    }
    int j = paramMotionEvent.getAction();
    if (j == 4) {
      poke(paramMotionEvent);
    }
    for (;;)
    {
      return false;
      if (j == 0)
      {
        j = (int)getSize(paramMotionEvent.getEventTime());
        if (this.mVertical) {
          if (paramMotionEvent.getX() >= j) {}
        }
        while (i != 0)
        {
          Slog.v("DeadZone", "consuming errant click: (" + paramMotionEvent.getX() + "," + paramMotionEvent.getY() + ")");
          if (this.mShouldFlash)
          {
            post(this.mDebugFlash);
            postInvalidate();
          }
          return false;
          do
          {
            i = 0;
            break;
          } while (paramMotionEvent.getY() >= j);
        }
      }
    }
  }
  
  public void poke(MotionEvent paramMotionEvent)
  {
    this.mLastPokeTime = paramMotionEvent.getEventTime();
    if (this.mShouldFlash) {
      postInvalidate();
    }
  }
  
  public void setFlashOnTouchCapture(boolean paramBoolean)
  {
    this.mShouldFlash = paramBoolean;
    this.mFlashFrac = 0.0F;
    postInvalidate();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\DeadZone.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */