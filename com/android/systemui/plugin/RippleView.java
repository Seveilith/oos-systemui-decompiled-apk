package com.android.systemui.plugin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import java.util.Random;

public class RippleView
  extends View
{
  public static int MESSAGE_DELAY = 20;
  private int COLOR = Color.parseColor("#888888");
  private float DURATION = 1000.0F;
  private float END_RADIUS_FISRT = 120.0F;
  private float END_RADIUS_SECOND = 150.0F;
  private float START_RADIUS_FIRST = 30.0F;
  private float START_RADIUS_SECOND = 50.0F;
  private int STROKE_WIDTH_FIRST = 4;
  private int STROKE_WIDTH_SECOUND = 2;
  private final String TAG = "RippleView";
  private Handler handler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      RippleView.this.invalidate();
      if (RippleView.-get1(RippleView.this))
      {
        paramAnonymousMessage = RippleView.this;
        RippleView.-set0(paramAnonymousMessage, RippleView.-get2(paramAnonymousMessage) + 1);
        if (RippleView.-get2(RippleView.this) > RippleView.-get0(RippleView.this) / RippleView.MESSAGE_DELAY) {
          RippleView.-set0(RippleView.this, 0);
        }
        paramAnonymousMessage = RippleView.this;
        RippleView.-set1(paramAnonymousMessage, RippleView.-get3(paramAnonymousMessage) + 1);
        if (RippleView.-get3(RippleView.this) > RippleView.-get0(RippleView.this) / RippleView.MESSAGE_DELAY) {
          RippleView.-set1(RippleView.this, 0);
        }
        sendEmptyMessageDelayed(0, RippleView.MESSAGE_DELAY);
      }
    }
  };
  private boolean isStartRipple;
  private int mClickCount = 0;
  private float mOffsetFirst;
  private float mOffsetSecond;
  private int mPositionX;
  private int mPositionY;
  private Paint mRipplePaintFirst = new Paint();
  private Paint mRipplePaintSecond = new Paint();
  private int mScreenHeight;
  private int mScreenWidth;
  private int rippleFirstRadius = -5;
  private int rippleSecendRadius = 0;
  
  public RippleView(Context paramContext)
  {
    super(paramContext);
    init(paramContext);
  }
  
  public RippleView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
  }
  
  public RippleView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext);
  }
  
  private void init(Context paramContext)
  {
    this.mRipplePaintFirst.setAntiAlias(true);
    this.mRipplePaintFirst.setStyle(Paint.Style.STROKE);
    this.mRipplePaintSecond.setAntiAlias(true);
    this.mRipplePaintSecond.setStyle(Paint.Style.STROKE);
    paramContext = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
    this.mScreenWidth = paramContext.getWidth();
    this.mScreenHeight = paramContext.getHeight();
  }
  
  public void generatePosition()
  {
    Random localRandom = new Random();
    int i = (int)(this.mScreenWidth - this.END_RADIUS_FISRT);
    i = (int)this.END_RADIUS_FISRT;
    i = (int)(this.mScreenHeight - this.END_RADIUS_FISRT);
    i = (int)this.END_RADIUS_FISRT;
    int k;
    int j;
    int m;
    if (this.mClickCount == 0)
    {
      i = (int)(this.mScreenWidth / 2 - this.END_RADIUS_FISRT);
      k = (int)this.END_RADIUS_FISRT;
      j = (int)(this.mScreenHeight / 2 - this.END_RADIUS_FISRT);
      m = (int)this.END_RADIUS_FISRT;
      this.mPositionX = ((int)(localRandom.nextInt(i - k) + this.END_RADIUS_FISRT));
      this.mPositionY = ((int)(localRandom.nextInt(j - m) + this.END_RADIUS_FISRT));
    }
    for (;;)
    {
      Log.d("RippleView", "generatePosition : click = " + this.mClickCount + ", " + k + " < x < " + i + ", " + m + " < y < " + j);
      Log.d("RippleView", "generatePosition: (" + this.mPositionX + ", " + this.mPositionY + ")");
      return;
      if (this.mClickCount == 1)
      {
        i = (int)(this.mScreenWidth - this.END_RADIUS_FISRT);
        k = (int)(this.mScreenWidth / 2 + this.END_RADIUS_FISRT);
        j = (int)(this.mScreenHeight / 2 - this.END_RADIUS_FISRT);
        m = (int)this.END_RADIUS_FISRT;
        this.mPositionX = ((int)(localRandom.nextInt(i - k) + this.mScreenWidth / 2 + this.END_RADIUS_FISRT));
        this.mPositionY = ((int)(localRandom.nextInt(j - m) + this.END_RADIUS_FISRT));
      }
      else if (this.mClickCount == 2)
      {
        i = (int)(this.mScreenWidth - this.END_RADIUS_FISRT);
        k = (int)(this.mScreenWidth / 2 + this.END_RADIUS_FISRT);
        j = (int)(this.mScreenHeight - this.END_RADIUS_FISRT);
        m = (int)(this.mScreenHeight / 2 + this.END_RADIUS_FISRT);
        this.mPositionX = ((int)(localRandom.nextInt(i - k) + this.mScreenWidth / 2 + this.END_RADIUS_FISRT));
        this.mPositionY = ((int)(localRandom.nextInt(j - m) + this.mScreenHeight / 2 + this.END_RADIUS_FISRT));
      }
      else
      {
        i = (int)(this.mScreenWidth / 2 - this.END_RADIUS_FISRT);
        k = (int)this.END_RADIUS_FISRT;
        j = (int)(this.mScreenHeight - this.END_RADIUS_FISRT);
        m = (int)(this.mScreenHeight / 2 + this.END_RADIUS_FISRT);
        this.mPositionX = ((int)(localRandom.nextInt(i - k) + this.END_RADIUS_FISRT));
        this.mPositionY = ((int)(localRandom.nextInt(j - m) + this.mScreenHeight / 2 + this.END_RADIUS_FISRT));
      }
    }
  }
  
  public boolean isValidPosition(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() != 0) {
      return false;
    }
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    Log.d("RippleView", "isValidPosition: (" + f1 + ", " + f2 + ")");
    if ((f1 <= this.mPositionX + this.END_RADIUS_SECOND) && (f1 >= this.mPositionX - this.END_RADIUS_SECOND) && (f2 <= this.mPositionY + this.END_RADIUS_SECOND) && (f2 > this.mPositionY - this.END_RADIUS_SECOND))
    {
      Log.d("RippleView", "isValidPosition: true");
      return true;
    }
    return false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.isStartRipple)
    {
      int i;
      if (this.rippleFirstRadius >= 0)
      {
        i = (int)(Math.sin(MESSAGE_DELAY * 3.1416D / this.DURATION * this.rippleFirstRadius) * 255.0D);
        this.mRipplePaintFirst.setAlpha(i);
        paramCanvas.drawCircle(this.mPositionX, this.mPositionY, this.START_RADIUS_FIRST + this.mOffsetFirst * this.rippleFirstRadius, this.mRipplePaintFirst);
      }
      if (this.rippleSecendRadius >= 0)
      {
        i = (int)(Math.sin(MESSAGE_DELAY * 3.1416D / this.DURATION * this.rippleSecendRadius) * 255.0D);
        this.mRipplePaintSecond.setAlpha(i);
        paramCanvas.drawCircle(this.mPositionX, this.mPositionY, this.START_RADIUS_SECOND + this.mOffsetSecond * this.rippleSecendRadius, this.mRipplePaintSecond);
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = isValidPosition(paramMotionEvent);
    Log.d("RippleView", "onTouchEvent: isValid = " + bool);
    if (bool)
    {
      if (this.mClickCount != 3) {
        break label59;
      }
      LSState.getInstance().getPreventModeCtrl().disableProximitySensor();
    }
    for (;;)
    {
      return super.onTouchEvent(paramMotionEvent);
      label59:
      this.mClickCount += 1;
      stopRipple();
      startRipple();
    }
  }
  
  public void prepare()
  {
    this.mClickCount = 0;
    generatePosition();
    this.mOffsetFirst = ((this.END_RADIUS_FISRT - this.START_RADIUS_FIRST) * MESSAGE_DELAY / this.DURATION);
    this.mOffsetSecond = ((this.END_RADIUS_SECOND - this.START_RADIUS_FIRST) * MESSAGE_DELAY / this.DURATION);
    this.mRipplePaintFirst.setStrokeWidth(this.STROKE_WIDTH_FIRST);
    this.mRipplePaintFirst.setColor(this.COLOR);
    this.mRipplePaintSecond.setStrokeWidth(this.STROKE_WIDTH_SECOUND);
    this.mRipplePaintSecond.setColor(this.COLOR);
  }
  
  public void startRipple()
  {
    generatePosition();
    startRipple(0);
  }
  
  public void startRipple(int paramInt)
  {
    this.isStartRipple = true;
    this.handler.sendEmptyMessageDelayed(0, paramInt);
  }
  
  public void stopRipple()
  {
    this.isStartRipple = false;
    this.handler.removeMessages(0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\plugin\RippleView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */