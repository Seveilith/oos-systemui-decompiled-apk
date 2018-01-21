package com.android.systemui.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.IPowerManager.Stub;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings.System;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.ThemeColorUtils;
import java.util.ArrayList;
import java.util.Iterator;

public class BrightnessController
  implements ToggleSlider.Listener
{
  private volatile boolean mAutomatic;
  private final boolean mAutomaticAvailable;
  private Handler mBackgroundHandler;
  private final BrightnessObserver mBrightnessObserver;
  private ArrayList<BrightnessStateChangeCallback> mChangeCallbacks = new ArrayList();
  private final Context mContext;
  private final ToggleSlider mControl;
  private boolean mExternalChange;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool = true;
      BrightnessController.-set1(BrightnessController.this, true);
      for (;;)
      {
        try
        {
          switch (paramAnonymousMessage.what)
          {
          case 0: 
            super.handleMessage(paramAnonymousMessage);
            label53:
            return;
          }
        }
        finally
        {
          BrightnessController.-set1(BrightnessController.this, false);
        }
        BrightnessController.-wrap0(BrightnessController.this, ((Boolean)paramAnonymousMessage.obj).booleanValue());
        continue;
        BrightnessController.-get6(BrightnessController.this).setMax(paramAnonymousMessage.arg1);
        BrightnessController.-get6(BrightnessController.this).setValue(paramAnonymousMessage.arg2);
      }
      ToggleSlider localToggleSlider = BrightnessController.-get6(BrightnessController.this);
      if (paramAnonymousMessage.arg1 != 0) {}
      for (;;)
      {
        localToggleSlider.setChecked(bool);
        break label53;
        BrightnessController.-get6(BrightnessController.this).setOnChangedListener(BrightnessController.this);
        break label53;
        BrightnessController.-get6(BrightnessController.this).setOnChangedListener(null);
        break;
        break;
        bool = false;
      }
    }
  };
  private final ImageView mIcon;
  private boolean mListening;
  private final int mMaximumBacklight;
  private final int mMinimumBacklight;
  private ImageView mMirrorIcon;
  private boolean mNewController = false;
  private final IPowerManager mPower;
  private final Runnable mStartListeningRunnable = new Runnable()
  {
    public void run()
    {
      BrightnessController.-get3(BrightnessController.this).startObserving();
      BrightnessController.-get13(BrightnessController.this).startTracking();
      BrightnessController.-get11(BrightnessController.this).run();
      BrightnessController.-get12(BrightnessController.this).run();
      BrightnessController.-get7(BrightnessController.this).sendEmptyMessage(3);
    }
  };
  private final Runnable mStopListeningRunnable = new Runnable()
  {
    public void run()
    {
      BrightnessController.-get3(BrightnessController.this).stopObserving();
      BrightnessController.-get13(BrightnessController.this).stopTracking();
      BrightnessController.-get7(BrightnessController.this).sendEmptyMessage(4);
    }
  };
  private final Runnable mUpdateModeRunnable = new Runnable()
  {
    public void run()
    {
      if (BrightnessController.-get1(BrightnessController.this))
      {
        int i = Settings.System.getIntForUser(BrightnessController.-get5(BrightnessController.this).getContentResolver(), "screen_brightness_mode", 0, -2);
        BrightnessController localBrightnessController = BrightnessController.this;
        if (i != 0) {}
        for (boolean bool = true;; bool = false)
        {
          BrightnessController.-set0(localBrightnessController, bool);
          BrightnessController.-get7(BrightnessController.this).obtainMessage(0, Boolean.valueOf(BrightnessController.-get0(BrightnessController.this))).sendToTarget();
          return;
        }
      }
      BrightnessController.-get7(BrightnessController.this).obtainMessage(2, Integer.valueOf(0)).sendToTarget();
      BrightnessController.-get7(BrightnessController.this).obtainMessage(0, Boolean.valueOf(false)).sendToTarget();
    }
  };
  private final Runnable mUpdateSliderRunnable = new Runnable()
  {
    public void run()
    {
      if ((!BrightnessController.-get0(BrightnessController.this)) || (BrightnessController.-get10(BrightnessController.this)))
      {
        int i = Settings.System.getIntForUser(BrightnessController.-get5(BrightnessController.this).getContentResolver(), "screen_brightness", BrightnessController.-get8(BrightnessController.this), -2);
        BrightnessController.-get7(BrightnessController.this).obtainMessage(1, BrightnessController.-get8(BrightnessController.this) - BrightnessController.-get9(BrightnessController.this), i - BrightnessController.-get9(BrightnessController.this)).sendToTarget();
        return;
      }
      float f = Settings.System.getFloatForUser(BrightnessController.-get5(BrightnessController.this).getContentResolver(), "screen_auto_brightness_adj", 0.0F, -2);
      BrightnessController.-get7(BrightnessController.this).obtainMessage(1, 2048, (int)((1.0F + f) * 2048.0F / 2.0F)).sendToTarget();
    }
  };
  private final CurrentUserTracker mUserTracker;
  
  public BrightnessController(Context paramContext, ImageView paramImageView, ToggleSlider paramToggleSlider)
  {
    this.mContext = paramContext;
    this.mIcon = paramImageView;
    this.mIcon.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        BrightnessController.this.onClickAutomiticIcon();
      }
    });
    this.mControl = paramToggleSlider;
    this.mBackgroundHandler = new Handler(Looper.getMainLooper());
    this.mUserTracker = new CurrentUserTracker(this.mContext)
    {
      public void onUserSwitched(int paramAnonymousInt)
      {
        BrightnessController.-get2(BrightnessController.this).post(BrightnessController.-get11(BrightnessController.this));
        BrightnessController.-get2(BrightnessController.this).post(BrightnessController.-get12(BrightnessController.this));
      }
    };
    this.mBrightnessObserver = new BrightnessObserver(this.mHandler);
    paramImageView = (PowerManager)paramContext.getSystemService("power");
    this.mMinimumBacklight = paramImageView.getMinimumScreenBrightnessSetting();
    this.mMaximumBacklight = paramImageView.getMaximumScreenBrightnessSetting();
    this.mAutomaticAvailable = paramContext.getResources().getBoolean(17956900);
    this.mPower = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
    this.mNewController = this.mContext.getPackageManager().hasSystemFeature("oem.autobrightctl.animation.support");
  }
  
  private void setBrightness(int paramInt)
  {
    try
    {
      this.mPower.setTemporaryScreenBrightnessSettingOverride(paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void setBrightnessAdj(float paramFloat)
  {
    try
    {
      this.mPower.setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void setMode(int paramInt)
  {
    Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", paramInt, this.mUserTracker.getCurrentUserId());
  }
  
  private void updateIcon(boolean paramBoolean)
  {
    updateIconInternal(paramBoolean, this.mIcon);
    updateIconInternal(paramBoolean, this.mMirrorIcon);
  }
  
  private void updateIconInternal(boolean paramBoolean, ImageView paramImageView)
  {
    int j;
    if (paramImageView != null)
    {
      if (!paramBoolean) {
        break label45;
      }
      i = 1;
      if (i == 0) {
        break label50;
      }
      j = 2130837781;
      label19:
      paramImageView.setImageResource(j);
      if (i == 0) {
        break label58;
      }
    }
    label45:
    label50:
    label58:
    for (int i = ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_ICON);; i = ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_ICON_DISABLE))
    {
      paramImageView.setImageTintList(ColorStateList.valueOf(i));
      return;
      i = 0;
      break;
      j = 2130837779;
      break label19;
    }
  }
  
  public void onChanged(ToggleSlider paramToggleSlider, boolean paramBoolean1, boolean paramBoolean2, final int paramInt, boolean paramBoolean3)
  {
    updateIcon(this.mAutomatic);
    if (this.mExternalChange) {
      return;
    }
    if (this.mNewController)
    {
      paramInt += this.mMinimumBacklight;
      if (paramBoolean3) {
        MetricsLogger.action(this.mContext, 218, paramInt);
      }
      setBrightnessAdj(paramInt);
      setBrightness(paramInt);
      if (!paramBoolean1) {
        AsyncTask.execute(new Runnable()
        {
          public void run()
          {
            Settings.System.putIntForUser(BrightnessController.-get5(BrightnessController.this).getContentResolver(), "screen_brightness", paramInt, -2);
            Settings.System.putFloatForUser(BrightnessController.-get5(BrightnessController.this).getContentResolver(), "screen_auto_brightness_adj", paramInt, -2);
          }
        });
      }
    }
    for (;;)
    {
      paramToggleSlider = this.mChangeCallbacks.iterator();
      while (paramToggleSlider.hasNext()) {
        ((BrightnessStateChangeCallback)paramToggleSlider.next()).onBrightnessLevelChanged();
      }
      if (!this.mAutomatic)
      {
        paramInt += this.mMinimumBacklight;
        if (paramBoolean3) {
          MetricsLogger.action(this.mContext, 218, paramInt);
        }
        setBrightness(paramInt);
        if (!paramBoolean1) {
          AsyncTask.execute(new Runnable()
          {
            public void run()
            {
              Settings.System.putIntForUser(BrightnessController.-get5(BrightnessController.this).getContentResolver(), "screen_brightness", paramInt, -2);
            }
          });
        }
      }
      else
      {
        final float f = paramInt / 1024.0F - 1.0F;
        if (paramBoolean3) {
          MetricsLogger.action(this.mContext, 219, paramInt);
        }
        setBrightnessAdj(f);
        if (!paramBoolean1) {
          AsyncTask.execute(new Runnable()
          {
            public void run()
            {
              Settings.System.putFloatForUser(BrightnessController.-get5(BrightnessController.this).getContentResolver(), "screen_auto_brightness_adj", f, -2);
            }
          });
        }
      }
    }
  }
  
  public void onClickAutomiticIcon()
  {
    MdmLogger.log("quick_bright", "auto", "1");
    if (this.mAutomatic) {}
    for (int i = 0;; i = 1)
    {
      setMode(i);
      return;
    }
  }
  
  public void onInit(ToggleSlider paramToggleSlider) {}
  
  public void registerCallbacks()
  {
    if (this.mListening) {
      return;
    }
    this.mBackgroundHandler.post(this.mStartListeningRunnable);
    this.mListening = true;
  }
  
  public void setBackgroundLooper(Looper paramLooper)
  {
    this.mBackgroundHandler = new Handler(paramLooper);
  }
  
  public void setMirrorView(View paramView)
  {
    this.mMirrorIcon = ((ImageView)paramView.findViewById(2131952165));
  }
  
  public void unregisterCallbacks()
  {
    if (!this.mListening) {
      return;
    }
    this.mBackgroundHandler.post(this.mStopListeningRunnable);
    this.mListening = false;
  }
  
  private class BrightnessObserver
    extends ContentObserver
  {
    private final Uri BRIGHTNESS_ADJ_URI = Settings.System.getUriFor("screen_auto_brightness_adj");
    private final Uri BRIGHTNESS_MODE_URI = Settings.System.getUriFor("screen_brightness_mode");
    private final Uri BRIGHTNESS_URI = Settings.System.getUriFor("screen_brightness");
    
    public BrightnessObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      onChange(paramBoolean, null);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (paramBoolean) {
        return;
      }
      if (this.BRIGHTNESS_MODE_URI.equals(paramUri))
      {
        BrightnessController.-get2(BrightnessController.this).post(BrightnessController.-get11(BrightnessController.this));
        BrightnessController.-get2(BrightnessController.this).post(BrightnessController.-get12(BrightnessController.this));
      }
      for (;;)
      {
        paramUri = BrightnessController.-get4(BrightnessController.this).iterator();
        while (paramUri.hasNext()) {
          ((BrightnessController.BrightnessStateChangeCallback)paramUri.next()).onBrightnessLevelChanged();
        }
        if ((!this.BRIGHTNESS_URI.equals(paramUri)) || (BrightnessController.-get0(BrightnessController.this)))
        {
          if ((this.BRIGHTNESS_ADJ_URI.equals(paramUri)) && (BrightnessController.-get0(BrightnessController.this))) {
            BrightnessController.-get2(BrightnessController.this).post(BrightnessController.-get12(BrightnessController.this));
          }
        }
        else
        {
          BrightnessController.-get2(BrightnessController.this).post(BrightnessController.-get12(BrightnessController.this));
          continue;
        }
        BrightnessController.-get2(BrightnessController.this).post(BrightnessController.-get11(BrightnessController.this));
        BrightnessController.-get2(BrightnessController.this).post(BrightnessController.-get12(BrightnessController.this));
      }
    }
    
    public void startObserving()
    {
      ContentResolver localContentResolver = BrightnessController.-get5(BrightnessController.this).getContentResolver();
      localContentResolver.unregisterContentObserver(this);
      localContentResolver.registerContentObserver(this.BRIGHTNESS_MODE_URI, false, this, -1);
      localContentResolver.registerContentObserver(this.BRIGHTNESS_URI, false, this, -1);
      localContentResolver.registerContentObserver(this.BRIGHTNESS_ADJ_URI, false, this, -1);
    }
    
    public void stopObserving()
    {
      BrightnessController.-get5(BrightnessController.this).getContentResolver().unregisterContentObserver(this);
    }
  }
  
  public static abstract interface BrightnessStateChangeCallback
  {
    public abstract void onBrightnessLevelChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\settings\BrightnessController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */