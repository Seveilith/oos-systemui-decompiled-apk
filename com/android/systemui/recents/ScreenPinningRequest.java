package com.android.systemui.recents;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.util.Utils;
import java.util.ArrayList;

public class ScreenPinningRequest
  implements View.OnClickListener
{
  private final AccessibilityManager mAccessibilityService;
  private final Context mContext;
  private RequestWindowView mRequestWindow;
  private final WindowManager mWindowManager;
  private int taskId;
  
  public ScreenPinningRequest(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAccessibilityService = ((AccessibilityManager)this.mContext.getSystemService("accessibility"));
    this.mWindowManager = ((WindowManager)this.mContext.getSystemService("window"));
  }
  
  private WindowManager.LayoutParams getWindowLayoutParams()
  {
    WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(-1, -1, 2024, 16777480, -3);
    localLayoutParams.privateFlags |= 0x10;
    localLayoutParams.setTitle("ScreenPinningConfirmation");
    localLayoutParams.gravity = 119;
    return localLayoutParams;
  }
  
  public void clearPrompt()
  {
    if (this.mRequestWindow != null)
    {
      this.mWindowManager.removeView(this.mRequestWindow);
      this.mRequestWindow = null;
    }
  }
  
  public FrameLayout.LayoutParams getRequestLayoutParams(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 21;; i = 81) {
      return new FrameLayout.LayoutParams(-2, -2, i);
    }
  }
  
  public void onClick(View paramView)
  {
    if ((paramView.getId() == 2131952227) || (this.mRequestWindow == paramView)) {}
    try
    {
      ActivityManagerNative.getDefault().startSystemLockTaskMode(this.taskId);
      clearPrompt();
      return;
    }
    catch (RemoteException paramView)
    {
      for (;;) {}
    }
  }
  
  public void onConfigurationChanged()
  {
    if (this.mRequestWindow != null) {
      this.mRequestWindow.onConfigurationChanged();
    }
  }
  
  public void showPrompt(int paramInt, boolean paramBoolean)
  {
    try
    {
      clearPrompt();
      this.taskId = paramInt;
      this.mRequestWindow = new RequestWindowView(this.mContext, paramBoolean);
      this.mRequestWindow.setSystemUiVisibility(256);
      WindowManager.LayoutParams localLayoutParams = getWindowLayoutParams();
      this.mWindowManager.addView(this.mRequestWindow, localLayoutParams);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
  
  private class RequestWindowView
    extends FrameLayout
  {
    private final ColorDrawable mColor = new ColorDrawable(0);
    private ValueAnimator mColorAnim;
    private ViewGroup mLayout;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (paramAnonymousIntent.getAction().equals("android.intent.action.CONFIGURATION_CHANGED")) {
          ScreenPinningRequest.RequestWindowView.this.post(ScreenPinningRequest.RequestWindowView.-get3(ScreenPinningRequest.RequestWindowView.this));
        }
        while ((!paramAnonymousIntent.getAction().equals("android.intent.action.USER_SWITCHED")) && (!paramAnonymousIntent.getAction().equals("android.intent.action.SCREEN_OFF"))) {
          return;
        }
        ScreenPinningRequest.this.clearPrompt();
      }
    };
    private boolean mShowCancel;
    private final Runnable mUpdateLayoutRunnable = new Runnable()
    {
      public void run()
      {
        if ((ScreenPinningRequest.RequestWindowView.-get2(ScreenPinningRequest.RequestWindowView.this) != null) && (ScreenPinningRequest.RequestWindowView.-get2(ScreenPinningRequest.RequestWindowView.this).getParent() != null)) {
          ScreenPinningRequest.RequestWindowView.-get2(ScreenPinningRequest.RequestWindowView.this).setLayoutParams(ScreenPinningRequest.this.getRequestLayoutParams(ScreenPinningRequest.RequestWindowView.-wrap0(ScreenPinningRequest.RequestWindowView.this, ScreenPinningRequest.RequestWindowView.-get1(ScreenPinningRequest.RequestWindowView.this))));
        }
      }
    };
    
    public RequestWindowView(Context paramContext, boolean paramBoolean)
    {
      super();
      setClickable(true);
      setOnClickListener(ScreenPinningRequest.this);
      setBackground(this.mColor);
      this.mShowCancel = paramBoolean;
    }
    
    private void inflateView(boolean paramBoolean)
    {
      Object localObject = getContext();
      if (paramBoolean)
      {
        i = 2130968809;
        this.mLayout = ((ViewGroup)View.inflate((Context)localObject, i, null));
        this.mLayout.setClickable(true);
        this.mLayout.setLayoutDirection(0);
        this.mLayout.findViewById(2131952224).setLayoutDirection(3);
        localObject = this.mLayout.findViewById(2131952218);
        if (!Recents.getSystemServices().hasSoftNavigationBar()) {
          break label227;
        }
        if (!Utils.isBackKeyRight(getContext())) {
          break label219;
        }
        ((View)localObject).setLayoutDirection(1);
        label88:
        swapChildrenIfRtlAndVertical((View)localObject);
        label93:
        ((Button)this.mLayout.findViewById(2131952227)).setOnClickListener(ScreenPinningRequest.this);
        if (!this.mShowCancel) {
          break label236;
        }
        ((Button)this.mLayout.findViewById(2131952228)).setOnClickListener(ScreenPinningRequest.this);
        label138:
        ((TextView)this.mLayout.findViewById(2131952226)).setText(2131690432);
        if (!ScreenPinningRequest.-get0(ScreenPinningRequest.this).isEnabled()) {
          break label255;
        }
      }
      label219:
      label227:
      label236:
      label255:
      for (int i = 4;; i = 0)
      {
        this.mLayout.findViewById(2131952221).setVisibility(i);
        this.mLayout.findViewById(2131952220).setVisibility(i);
        addView(this.mLayout, ScreenPinningRequest.this.getRequestLayoutParams(paramBoolean));
        return;
        i = 2130968806;
        break;
        ((View)localObject).setLayoutDirection(0);
        break label88;
        ((View)localObject).setVisibility(8);
        break label93;
        ((Button)this.mLayout.findViewById(2131952228)).setVisibility(4);
        break label138;
      }
    }
    
    private boolean isLandscapePhone(Context paramContext)
    {
      boolean bool2 = false;
      paramContext = this.mContext.getResources().getConfiguration();
      boolean bool1 = bool2;
      if (paramContext.orientation == 2)
      {
        bool1 = bool2;
        if (paramContext.smallestScreenWidthDp < 600) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    private void swapChildrenIfRtlAndVertical(View paramView)
    {
      if (this.mContext.getResources().getConfiguration().getLayoutDirection() != 1) {
        return;
      }
      paramView = (LinearLayout)paramView;
      if (paramView.getOrientation() == 1)
      {
        int j = paramView.getChildCount();
        ArrayList localArrayList = new ArrayList(j);
        int i = 0;
        while (i < j)
        {
          localArrayList.add(paramView.getChildAt(i));
          i += 1;
        }
        paramView.removeAllViews();
        i = j - 1;
        while (i >= 0)
        {
          paramView.addView((View)localArrayList.get(i));
          i -= 1;
        }
      }
    }
    
    public void onAttachedToWindow()
    {
      Object localObject = new DisplayMetrics();
      ScreenPinningRequest.-get1(ScreenPinningRequest.this).getDefaultDisplay().getMetrics((DisplayMetrics)localObject);
      float f = ((DisplayMetrics)localObject).density;
      boolean bool = isLandscapePhone(this.mContext);
      inflateView(bool);
      int i = this.mContext.getColor(2131493041);
      if (ActivityManager.isHighEndGfx())
      {
        this.mLayout.setAlpha(0.0F);
        if (bool)
        {
          this.mLayout.setTranslationX(96.0F * f);
          this.mLayout.animate().alpha(1.0F).translationX(0.0F).translationY(0.0F).setDuration(300L).setInterpolator(new DecelerateInterpolator()).start();
          this.mColorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), new Object[] { Integer.valueOf(0), Integer.valueOf(i) });
          this.mColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
          {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
            {
              int i = ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue();
              ScreenPinningRequest.RequestWindowView.-get0(ScreenPinningRequest.RequestWindowView.this).setColor(i);
            }
          });
          this.mColorAnim.setDuration(1000L);
          this.mColorAnim.start();
        }
      }
      for (;;)
      {
        localObject = new IntentFilter("android.intent.action.CONFIGURATION_CHANGED");
        ((IntentFilter)localObject).addAction("android.intent.action.USER_SWITCHED");
        ((IntentFilter)localObject).addAction("android.intent.action.SCREEN_OFF");
        this.mContext.registerReceiver(this.mReceiver, (IntentFilter)localObject);
        return;
        this.mLayout.setTranslationY(96.0F * f);
        break;
        this.mColor.setColor(i);
      }
    }
    
    protected void onConfigurationChanged()
    {
      removeAllViews();
      inflateView(isLandscapePhone(this.mContext));
    }
    
    public void onDetachedFromWindow()
    {
      this.mContext.unregisterReceiver(this.mReceiver);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\ScreenPinningRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */