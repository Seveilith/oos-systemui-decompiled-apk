package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.ImageView;
import com.android.systemui.R.styleable;
import com.android.systemui.statusbar.phone.ButtonDispatcher.ButtonInterface;
import com.android.systemui.util.Utils;

public class KeyButtonView
  extends ImageView
  implements ButtonDispatcher.ButtonInterface
{
  private static final boolean DEBUG = Build.DEBUG_ONEPLUS;
  private static final boolean IS_17801_DEVICE = Utils.is17801Device();
  private static final String TAG = KeyButtonView.class.getSimpleName();
  private AudioManager mAudioManager;
  private final Runnable mCheckLongPress = new Runnable()
  {
    public void run()
    {
      if (KeyButtonView.this.isPressed())
      {
        if (!KeyButtonView.this.isLongClickable()) {
          break label38;
        }
        KeyButtonView.this.performLongClick();
        KeyButtonView.-set0(KeyButtonView.this, true);
      }
      label38:
      while (!KeyButtonView.-get4(KeyButtonView.this)) {
        return;
      }
      if (KeyButtonView.-get0()) {
        Log.i(KeyButtonView.-get1(), "Logpress:mCode = " + KeyButtonView.-get2(KeyButtonView.this));
      }
      KeyButtonView.this.sendEvent(0, 128);
      KeyButtonView.this.sendAccessibilityEvent(2);
      KeyButtonView.-set0(KeyButtonView.this, true);
    }
  };
  private int mCode;
  private int mContentDescriptionRes;
  private long mDownTime;
  private boolean mGestureAborted;
  private int mKey = 3;
  private boolean mLongClicked;
  private View.OnClickListener mOnClickListener;
  private KeyButtonRipple mRippleDrawable = null;
  private boolean mSupportsLongpress = true;
  private int mThemeColor = 0;
  private int mTouchSlop;
  
  public KeyButtonView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public KeyButtonView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.KeyButtonView, paramInt, 0);
    this.mCode = paramAttributeSet.getInteger(1, 0);
    this.mSupportsLongpress = paramAttributeSet.getBoolean(2, true);
    TypedValue localTypedValue = new TypedValue();
    if (paramAttributeSet.getValue(0, localTypedValue)) {
      this.mContentDescriptionRes = localTypedValue.resourceId;
    }
    paramAttributeSet.recycle();
    setClickable(true);
    this.mTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
    this.mRippleDrawable = new KeyButtonRipple(paramContext, this);
    setBackground(this.mRippleDrawable);
    this.mKey = determineKey();
    if ((!IS_17801_DEVICE) && (this.mCode == 187)) {
      this.mCode = 0;
    }
  }
  
  private void updateThemeColorInternal()
  {
    setImageTintList(ColorStateList.valueOf(this.mThemeColor));
    postInvalidate();
  }
  
  public void abortCurrentGesture()
  {
    setPressed(false);
    this.mGestureAborted = true;
  }
  
  public int determineKey()
  {
    switch (getId())
    {
    default: 
      return 3;
    case 2131951793: 
      return 0;
    case 2131951635: 
      return 1;
    }
    return 2;
  }
  
  public boolean isLongClickable()
  {
    if ((IS_17801_DEVICE) && (this.mKey != 3)) {
      return false;
    }
    return super.isLongClickable();
  }
  
  public void loadAsync(String paramString)
  {
    new AsyncTask()
    {
      protected Drawable doInBackground(String... paramAnonymousVarArgs)
      {
        return Icon.createWithContentUri(paramAnonymousVarArgs[0]).loadDrawable(KeyButtonView.-get3(KeyButtonView.this));
      }
      
      protected void onPostExecute(Drawable paramAnonymousDrawable)
      {
        KeyButtonView.this.setImageDrawable(paramAnonymousDrawable);
      }
    }.execute(new String[] { paramString });
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.mContentDescriptionRes != 0) {
      setContentDescription(this.mContext.getString(this.mContentDescriptionRes));
    }
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    if (this.mCode != 0)
    {
      paramAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, null));
      if ((this.mSupportsLongpress) || (isLongClickable())) {
        paramAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, null));
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = false;
    int i = paramMotionEvent.getAction();
    if (i == 0) {
      this.mGestureAborted = false;
    }
    if (this.mGestureAborted) {
      return false;
    }
    switch (i)
    {
    default: 
      return true;
    case 0: 
      this.mDownTime = SystemClock.uptimeMillis();
      this.mLongClicked = false;
      setPressed(true);
      if (this.mCode != 0)
      {
        if (DEBUG) {
          Log.i(TAG, "ACTION_DOWN :mCode = " + this.mCode);
        }
        sendEvent(0, 0, this.mDownTime);
      }
      for (;;)
      {
        playSoundEffect(0);
        removeCallbacks(this.mCheckLongPress);
        postDelayed(this.mCheckLongPress, ViewConfiguration.getLongPressTimeout());
        return true;
        performHapticFeedback(1);
      }
    case 2: 
      i = (int)paramMotionEvent.getX();
      int j = (int)paramMotionEvent.getY();
      boolean bool1 = bool2;
      if (i >= -this.mTouchSlop)
      {
        bool1 = bool2;
        if (i < getWidth() + this.mTouchSlop)
        {
          bool1 = bool2;
          if (j >= -this.mTouchSlop)
          {
            bool1 = bool2;
            if (j < getHeight() + this.mTouchSlop) {
              bool1 = true;
            }
          }
        }
      }
      setPressed(bool1);
      return true;
    case 3: 
      setPressed(false);
      if (this.mCode != 0) {
        sendEvent(1, 32);
      }
      removeCallbacks(this.mCheckLongPress);
      return true;
    }
    if ((!isPressed()) || (this.mLongClicked))
    {
      i = 0;
      setPressed(false);
      if (this.mCode == 0) {
        break label384;
      }
      if (i == 0) {
        break label374;
      }
      if (DEBUG) {
        Log.i(TAG, "ACTION_UP :mCode = " + this.mCode);
      }
      sendEvent(1, 0);
      sendAccessibilityEvent(1);
    }
    for (;;)
    {
      removeCallbacks(this.mCheckLongPress);
      return true;
      i = 1;
      break;
      label374:
      sendEvent(1, 32);
      continue;
      label384:
      if ((i != 0) && (this.mOnClickListener != null))
      {
        this.mOnClickListener.onClick(this);
        sendAccessibilityEvent(1);
      }
    }
  }
  
  protected void onWindowVisibilityChanged(int paramInt)
  {
    super.onWindowVisibilityChanged(paramInt);
    if (paramInt != 0) {
      jumpDrawablesToCurrentState();
    }
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if ((paramInt == 16) && (this.mCode != 0))
    {
      sendEvent(0, 0, SystemClock.uptimeMillis());
      sendEvent(1, 0);
      sendAccessibilityEvent(1);
      playSoundEffect(0);
      return true;
    }
    if ((paramInt == 32) && (this.mCode != 0))
    {
      sendEvent(0, 128);
      sendEvent(1, 0);
      sendAccessibilityEvent(2);
      return true;
    }
    return super.performAccessibilityActionInternal(paramInt, paramBundle);
  }
  
  public void playSoundEffect(int paramInt)
  {
    this.mAudioManager.playSoundEffect(paramInt, ActivityManager.getCurrentUser());
  }
  
  public void sendEvent(int paramInt1, int paramInt2)
  {
    sendEvent(paramInt1, paramInt2, SystemClock.uptimeMillis());
  }
  
  void sendEvent(int paramInt1, int paramInt2, long paramLong)
  {
    if ((paramInt2 & 0x80) != 0) {}
    for (int i = 1;; i = 0)
    {
      KeyEvent localKeyEvent = new KeyEvent(this.mDownTime, paramLong, paramInt1, this.mCode, i, 0, -1, 0, paramInt2 | 0x8 | 0x40, 257);
      InputManager.getInstance().injectInputEvent(localKeyEvent, 0);
      return;
    }
  }
  
  public void setCarMode(boolean paramBoolean) {}
  
  public void setCode(int paramInt)
  {
    this.mCode = paramInt;
  }
  
  public void setImageDrawable(Drawable paramDrawable)
  {
    super.setImageDrawable(paramDrawable);
    if (Utils.isSupportHideNavBar()) {
      updateThemeColorInternal();
    }
  }
  
  public void setImageResource(int paramInt)
  {
    super.setImageResource(paramInt);
    if (Utils.isSupportHideNavBar()) {
      updateThemeColorInternal();
    }
  }
  
  public void setLandscape(boolean paramBoolean) {}
  
  public void setOnClickListener(View.OnClickListener paramOnClickListener)
  {
    super.setOnClickListener(paramOnClickListener);
    this.mOnClickListener = paramOnClickListener;
  }
  
  public void setRippleColor(int paramInt)
  {
    if (this.mRippleDrawable != null) {
      this.mRippleDrawable.setColor(paramInt);
    }
  }
  
  public void updateThemeColor(int paramInt)
  {
    this.mThemeColor = paramInt;
    updateThemeColorInternal();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\KeyButtonView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */