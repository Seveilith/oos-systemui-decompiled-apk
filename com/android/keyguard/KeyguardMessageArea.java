package com.android.keyguard;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import java.lang.ref.WeakReference;

class KeyguardMessageArea
  extends TextView
  implements SecurityMessageDisplay
{
  private static final Object ANNOUNCE_TOKEN = new Object();
  private final Runnable mClearMessageRunnable = new Runnable()
  {
    public void run()
    {
      KeyguardMessageArea.this.mMessage = null;
      KeyguardMessageArea.-wrap0(KeyguardMessageArea.this);
    }
  };
  private final int mDefaultColor;
  private final Handler mHandler;
  private KeyguardUpdateMonitorCallback mInfoCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onFinishedGoingToSleep(int paramAnonymousInt)
    {
      KeyguardMessageArea.this.setSelected(false);
    }
    
    public void onStartedWakingUp()
    {
      KeyguardMessageArea.this.setSelected(true);
    }
  };
  CharSequence mMessage;
  private int mNextMessageColor = -1;
  long mTimeout = 5000L;
  private final KeyguardUpdateMonitor mUpdateMonitor;
  
  public KeyguardMessageArea(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardMessageArea(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setLayerType(2, null);
    this.mUpdateMonitor = KeyguardUpdateMonitor.getInstance(getContext());
    this.mUpdateMonitor.registerCallback(this.mInfoCallback);
    this.mHandler = new Handler(Looper.myLooper());
    this.mDefaultColor = getCurrentTextColor();
    update();
  }
  
  private void clearMessage()
  {
    this.mHandler.removeCallbacks(this.mClearMessageRunnable);
    this.mHandler.post(this.mClearMessageRunnable);
  }
  
  public static SecurityMessageDisplay findSecurityMessageDisplay(View paramView)
  {
    KeyguardMessageArea localKeyguardMessageArea = (KeyguardMessageArea)paramView.findViewById(R.id.keyguard_message_area);
    if (localKeyguardMessageArea == null) {
      throw new RuntimeException("Can't find keyguard_message_area in " + paramView.getClass());
    }
    return localKeyguardMessageArea;
  }
  
  private void securityMessageChanged(CharSequence paramCharSequence)
  {
    this.mMessage = paramCharSequence;
    update();
    this.mHandler.removeCallbacks(this.mClearMessageRunnable);
    if (!KeyguardUpdateMonitor.getInstance(this.mContext).isUserUnlocked()) {
      this.mTimeout = 0L;
    }
    if (this.mTimeout > 0L) {
      this.mHandler.postDelayed(this.mClearMessageRunnable, this.mTimeout);
    }
    this.mHandler.removeCallbacksAndMessages(ANNOUNCE_TOKEN);
    this.mHandler.postAtTime(new AnnounceRunnable(this, getText()), ANNOUNCE_TOKEN, SystemClock.uptimeMillis() + 250L);
  }
  
  private void update()
  {
    CharSequence localCharSequence = this.mMessage;
    if (TextUtils.isEmpty(localCharSequence)) {}
    for (int i = 4;; i = 0)
    {
      setVisibility(i);
      setText(localCharSequence);
      i = this.mDefaultColor;
      if (this.mNextMessageColor != -1)
      {
        i = this.mNextMessageColor;
        this.mNextMessageColor = -1;
      }
      setTextColor(i);
      return;
    }
  }
  
  protected void onFinishInflate()
  {
    setSelected(KeyguardUpdateMonitor.getInstance(this.mContext).isDeviceInteractive());
  }
  
  public void setMessage(int paramInt, boolean paramBoolean)
  {
    if ((paramInt != 0) && (paramBoolean))
    {
      securityMessageChanged(getContext().getResources().getText(paramInt));
      return;
    }
    clearMessage();
  }
  
  public void setMessage(int paramInt, boolean paramBoolean, Object... paramVarArgs)
  {
    if ((paramInt != 0) && (paramBoolean))
    {
      securityMessageChanged(getContext().getString(paramInt, paramVarArgs));
      return;
    }
    clearMessage();
  }
  
  public void setMessage(CharSequence paramCharSequence, boolean paramBoolean)
  {
    if ((!TextUtils.isEmpty(paramCharSequence)) && (paramBoolean))
    {
      securityMessageChanged(paramCharSequence);
      return;
    }
    clearMessage();
  }
  
  public void setNextMessageColor(int paramInt)
  {
    this.mNextMessageColor = paramInt;
  }
  
  public void setTimeout(int paramInt)
  {
    this.mTimeout = paramInt;
  }
  
  private static class AnnounceRunnable
    implements Runnable
  {
    private final WeakReference<View> mHost;
    private final CharSequence mTextToAnnounce;
    
    AnnounceRunnable(View paramView, CharSequence paramCharSequence)
    {
      this.mHost = new WeakReference(paramView);
      this.mTextToAnnounce = paramCharSequence;
    }
    
    public void run()
    {
      View localView = (View)this.mHost.get();
      if (localView != null) {
        localView.announceForAccessibility(this.mTextToAnnounce);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardMessageArea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */