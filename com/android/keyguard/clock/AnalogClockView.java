package com.android.keyguard.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.R.drawable;
import com.android.keyguard.R.styleable;
import java.util.TimeZone;

public class AnalogClockView
  extends View
{
  private boolean mAttached;
  private Time mCalendar;
  private KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onTimeChanged()
    {
      AnalogClockView.-wrap0(AnalogClockView.this);
      AnalogClockView.this.invalidate();
    }
  };
  private boolean mChanged;
  private final Runnable mClockTick = new Runnable()
  {
    public void run()
    {
      AnalogClockView.-wrap0(AnalogClockView.this);
      AnalogClockView.this.invalidate();
      AnalogClockView.this.postDelayed(AnalogClockView.-get0(AnalogClockView.this), 1000L);
    }
  };
  private final Context mContext;
  private final Drawable mDial;
  private final int mDialHeight;
  private final int mDialWidth;
  private Paint mDotPaint;
  private final float mDotRadius;
  private final Handler mHandler = new Handler();
  private float mHour;
  private final Drawable mHourHand;
  private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.intent.action.TIMEZONE_CHANGED"))
      {
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("time-zone");
        AnalogClockView.-set0(AnalogClockView.this, new Time(TimeZone.getTimeZone(paramAnonymousContext).getID()));
      }
      AnalogClockView.-wrap0(AnalogClockView.this);
      AnalogClockView.this.invalidate();
    }
  };
  private KeyguardUpdateMonitor mKeyguardUpdateMonitor;
  private final Drawable mMinuteHand;
  private float mMinutes;
  private final Drawable mSecondHand;
  private float mSeconds;
  private String mTimeZoneId;
  
  public AnalogClockView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AnalogClockView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AnalogClockView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mContext = paramContext;
    Resources localResources = this.mContext.getResources();
    this.mDial = localResources.getDrawable(R.drawable.clock_analog_dial_mipmap);
    this.mHourHand = localResources.getDrawable(R.drawable.clock_analog_hour_mipmap);
    this.mMinuteHand = localResources.getDrawable(R.drawable.clock_analog_minute_mipmap);
    this.mSecondHand = localResources.getDrawable(R.drawable.clock_analog_second_mipmap);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AnalogClockView);
    this.mDotRadius = paramContext.getDimension(R.styleable.AnalogClockView_jewelRadius, 0.0F);
    paramInt = paramContext.getColor(R.styleable.AnalogClockView_jewelColor, -1);
    if (paramInt != 0)
    {
      this.mDotPaint = new Paint(1);
      this.mDotPaint.setColor(paramInt);
    }
    this.mCalendar = new Time();
    this.mDialWidth = this.mDial.getIntrinsicWidth();
    this.mDialHeight = this.mDial.getIntrinsicHeight();
    this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
  }
  
  private void drawHand(Canvas paramCanvas, Drawable paramDrawable, int paramInt1, int paramInt2, float paramFloat, boolean paramBoolean)
  {
    paramCanvas.save();
    paramCanvas.rotate(paramFloat, paramInt1, paramInt2);
    if (paramBoolean)
    {
      int i = paramDrawable.getIntrinsicWidth();
      int j = paramDrawable.getIntrinsicHeight();
      paramDrawable.setBounds(paramInt1 - i / 2, paramInt2 - j / 2, i / 2 + paramInt1, j / 2 + paramInt2);
    }
    paramDrawable.draw(paramCanvas);
    paramCanvas.restore();
  }
  
  private void onTimeChanged()
  {
    this.mCalendar.setToNow();
    if (this.mTimeZoneId != null) {
      this.mCalendar.switchTimezone(this.mTimeZoneId);
    }
    int i = this.mCalendar.hour;
    int j = this.mCalendar.minute;
    int k = this.mCalendar.second;
    this.mSeconds = k;
    this.mMinutes = (j + k / 60.0F);
    this.mHour = (i + this.mMinutes / 60.0F);
    this.mChanged = true;
    updateContentDescription(this.mCalendar);
  }
  
  private void updateContentDescription(Time paramTime)
  {
    setContentDescription(DateUtils.formatDateTime(this.mContext, paramTime.toMillis(false), 129));
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (!this.mAttached)
    {
      this.mAttached = true;
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
      getContext().getApplicationContext().registerReceiver(this.mIntentReceiver, localIntentFilter, null, this.mHandler);
    }
    this.mCalendar = new Time();
    onTimeChanged();
    this.mKeyguardUpdateMonitor.registerCallback(this.mCallback);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mAttached = false;
    try
    {
      getContext().getApplicationContext().unregisterReceiver(this.mIntentReceiver);
      this.mKeyguardUpdateMonitor.removeCallback(this.mCallback);
      return;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    boolean bool = this.mChanged;
    if (bool) {
      this.mChanged = false;
    }
    int j = getWidth();
    int k = getHeight();
    int m = j / 2;
    int n = k / 2;
    Drawable localDrawable = this.mDial;
    int i1 = localDrawable.getIntrinsicWidth();
    int i2 = localDrawable.getIntrinsicHeight();
    int i = 0;
    if ((j < i1) || (k < i2))
    {
      i = 1;
      float f = Math.min(j / i1, k / i2);
      paramCanvas.save();
      paramCanvas.scale(f, f, m, n);
    }
    if (bool) {
      localDrawable.setBounds(m - i1 / 2, n - i2 / 2, i1 / 2 + m, i2 / 2 + n);
    }
    localDrawable.draw(paramCanvas);
    if ((this.mDotRadius > 0.0F) && (this.mDotPaint != null)) {}
    drawHand(paramCanvas, this.mHourHand, m, n, this.mHour / 12.0F * 360.0F, bool);
    drawHand(paramCanvas, this.mMinuteHand, m, n, this.mMinutes / 60.0F * 360.0F, bool);
    if (i != 0) {
      paramCanvas.restore();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt1);
    int k = View.MeasureSpec.getMode(paramInt2);
    int m = View.MeasureSpec.getSize(paramInt2);
    float f;
    if ((i != 0) && (j < this.mDialWidth)) {
      f = j / this.mDialWidth;
    }
    if ((k != 0) && (m < this.mDialHeight)) {
      f = m / this.mDialHeight;
    }
    setMeasuredDimension(resolveSizeAndState((int)(this.mDialWidth * 0.54F), paramInt1, 0), resolveSizeAndState((int)(this.mDialHeight * 0.54F), paramInt2, 0));
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mChanged = true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\clock\AnalogClockView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */