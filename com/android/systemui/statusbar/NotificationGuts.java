package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.android.internal.R.styleable;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Interpolators;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.android.systemui.util.MdmLogger;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class NotificationGuts
  extends LinearLayout
  implements TunerService.Tunable
{
  private static final List<String> ALLOW_SYSTEM_NOTIFICATION_BE_BLOCKED_PKGS = Arrays.asList(new String[] { "net.oneplus.push" });
  private float mActiveSliderAlpha = 1.0F;
  private ColorStateList mActiveSliderTint;
  private int mActualHeight;
  private boolean mAuto;
  private ImageView mAutoButton;
  private Drawable mBackground;
  private RadioButton mBlock;
  private int mClipTopAmount;
  private boolean mExposed;
  private Runnable mFalsingCheck;
  private Handler mHandler;
  private INotificationManager mINotificationManager;
  private TextView mImportanceSummary;
  private TextView mImportanceTitle;
  private float mInactiveSliderAlpha;
  private ColorStateList mInactiveSliderTint;
  private OnGutsClosedListener mListener;
  private boolean mNeedsFalsingProtection;
  private int mNotificationImportance;
  private RadioButton mReset;
  private SeekBar mSeekBar;
  private boolean mShowSlider;
  private RadioButton mSilent;
  private int mStartingUserImportance;
  
  public NotificationGuts(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setWillNotDraw(false);
    this.mHandler = new Handler();
    this.mFalsingCheck = new Runnable()
    {
      public void run()
      {
        if ((NotificationGuts.-get3(NotificationGuts.this)) && (NotificationGuts.-get2(NotificationGuts.this))) {
          NotificationGuts.this.closeControls(-1, -1, true);
        }
      }
    };
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Theme, 0, 0);
    this.mInactiveSliderAlpha = paramContext.getFloat(3, 0.5F);
    paramContext.recycle();
  }
  
  private void applyAuto()
  {
    Object localObject = this.mSeekBar;
    boolean bool;
    if (this.mAuto)
    {
      bool = false;
      ((SeekBar)localObject).setEnabled(bool);
      if (!this.mAuto) {
        break label134;
      }
      localObject = this.mActiveSliderTint;
      label31:
      if (!this.mAuto) {
        break label142;
      }
    }
    label134:
    label142:
    for (float f = this.mInactiveSliderAlpha;; f = this.mActiveSliderAlpha)
    {
      Drawable localDrawable = this.mAutoButton.getDrawable().mutate();
      localDrawable.setTintList((ColorStateList)localObject);
      this.mAutoButton.setImageDrawable(localDrawable);
      this.mSeekBar.setAlpha(f);
      if (!this.mAuto) {
        break label150;
      }
      this.mSeekBar.setProgress(this.mNotificationImportance);
      this.mImportanceSummary.setText(this.mContext.getString(2131690514));
      this.mImportanceTitle.setText(this.mContext.getString(2131690507));
      return;
      bool = true;
      break;
      localObject = this.mInactiveSliderTint;
      break label31;
    }
    label150:
    updateTitleAndSummary(this.mSeekBar.getProgress());
  }
  
  private void bindSlider(View paramView, boolean paramBoolean)
  {
    this.mActiveSliderTint = ColorStateList.valueOf(com.android.settingslib.Utils.getColorAccent(this.mContext));
    this.mInactiveSliderTint = loadColorStateList(2131493032);
    this.mImportanceSummary = ((TextView)paramView.findViewById(2131952108));
    this.mImportanceTitle = ((TextView)paramView.findViewById(2131951748));
    this.mSeekBar = ((SeekBar)paramView.findViewById(2131952088));
    final int i;
    if (paramBoolean)
    {
      i = 1;
      this.mSeekBar.setMax(5);
      this.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
      {
        public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
        {
          NotificationGuts.this.resetFalsingCheck();
          int i = paramAnonymousInt;
          if (paramAnonymousInt < i)
          {
            paramAnonymousSeekBar.setProgress(i);
            i = i;
          }
          NotificationGuts.-wrap1(NotificationGuts.this, i);
          if (paramAnonymousBoolean) {
            MetricsLogger.action(NotificationGuts.-get1(NotificationGuts.this), 290);
          }
        }
        
        public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar)
        {
          NotificationGuts.this.resetFalsingCheck();
        }
        
        public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar) {}
      });
      this.mSeekBar.setProgress(this.mNotificationImportance);
      this.mAutoButton = ((ImageView)paramView.findViewById(2131952109));
      this.mAutoButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = NotificationGuts.this;
          if (NotificationGuts.-get0(NotificationGuts.this)) {}
          for (boolean bool = false;; bool = true)
          {
            NotificationGuts.-set0(paramAnonymousView, bool);
            NotificationGuts.-wrap0(NotificationGuts.this);
            return;
          }
        }
      });
      if (this.mStartingUserImportance != 64536) {
        break label159;
      }
    }
    label159:
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.mAuto = paramBoolean;
      applyAuto();
      return;
      i = 0;
      break;
    }
  }
  
  private void bindToggles(View paramView, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    ((RadioGroup)paramView).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
    {
      public void onCheckedChanged(RadioGroup paramAnonymousRadioGroup, int paramAnonymousInt)
      {
        NotificationGuts.this.resetFalsingCheck();
      }
    });
    this.mBlock = ((RadioButton)paramView.findViewById(2131952105));
    this.mSilent = ((RadioButton)paramView.findViewById(2131952104));
    this.mReset = ((RadioButton)paramView.findViewById(2131952103));
    if (paramBoolean)
    {
      this.mBlock.setVisibility(8);
      this.mReset.setText(this.mContext.getString(2131690016));
    }
    for (;;)
    {
      this.mBlock.setText(this.mContext.getString(2131689931));
      this.mSilent.setText(this.mContext.getString(2131690017));
      if (paramInt2 != 1) {
        break;
      }
      this.mSilent.setChecked(true);
      return;
      this.mReset.setText(this.mContext.getString(2131690016));
    }
    this.mReset.setChecked(true);
  }
  
  private void draw(Canvas paramCanvas, Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      paramDrawable.setBounds(0, this.mClipTopAmount, getWidth(), this.mActualHeight);
      paramDrawable.draw(paramCanvas);
    }
  }
  
  private void drawableStateChanged(Drawable paramDrawable)
  {
    if ((paramDrawable != null) && (paramDrawable.isStateful())) {
      paramDrawable.setState(getDrawableState());
    }
  }
  
  private int getSelectedImportance()
  {
    if ((this.mSeekBar != null) && (this.mSeekBar.isShown()))
    {
      if (this.mSeekBar.isEnabled()) {
        return this.mSeekBar.getProgress();
      }
      return 64536;
    }
    if ((this.mBlock != null) && (this.mBlock.isChecked())) {
      return 0;
    }
    if ((this.mSilent != null) && (this.mSilent.isChecked())) {
      return 2;
    }
    return 64536;
  }
  
  private ColorStateList loadColorStateList(int paramInt)
  {
    return ColorStateList.valueOf(this.mContext.getColor(paramInt));
  }
  
  private void updateTitleAndSummary(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      this.mImportanceSummary.setText(this.mContext.getString(2131690515));
      this.mImportanceTitle.setText(this.mContext.getString(2131690508));
      return;
    case 1: 
      this.mImportanceSummary.setText(this.mContext.getString(2131690516));
      this.mImportanceTitle.setText(this.mContext.getString(2131690509));
      return;
    case 2: 
      this.mImportanceSummary.setText(this.mContext.getString(2131690517));
      this.mImportanceTitle.setText(this.mContext.getString(2131690510));
      return;
    case 3: 
      this.mImportanceSummary.setText(this.mContext.getString(2131690518));
      this.mImportanceTitle.setText(this.mContext.getString(2131690511));
      return;
    case 4: 
      this.mImportanceSummary.setText(this.mContext.getString(2131690519));
      this.mImportanceTitle.setText(this.mContext.getString(2131690512));
      return;
    }
    this.mImportanceSummary.setText(this.mContext.getString(2131690520));
    this.mImportanceTitle.setText(this.mContext.getString(2131690513));
  }
  
  public boolean areGutsExposed()
  {
    return this.mExposed;
  }
  
  void bindImportance(PackageManager paramPackageManager, StatusBarNotification paramStatusBarNotification, Set<String> paramSet, int paramInt)
  {
    int i = 0;
    this.mINotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    this.mStartingUserImportance = 64536;
    try
    {
      this.mStartingUserImportance = this.mINotificationManager.getImportance(paramStatusBarNotification.getPackageName(), paramStatusBarNotification.getUid());
      int j = this.mINotificationManager.getOPLevel(paramStatusBarNotification.getPackageName(), paramStatusBarNotification.getUid());
      i = j;
    }
    catch (RemoteException localRemoteException)
    {
      View localView1;
      View localView2;
      View localView3;
      boolean bool1;
      for (;;) {}
    }
    this.mNotificationImportance = paramInt;
    localView1 = findViewById(2131952107);
    localView2 = findViewById(2131952102);
    localView3 = findViewById(2131952106);
    if (paramSet != null) {}
    for (bool1 = paramSet.contains(paramStatusBarNotification.getPackageName()); bool1; bool1 = false)
    {
      localView2.setVisibility(8);
      localView1.setVisibility(8);
      localView3.setVisibility(0);
      return;
    }
    localView3.setVisibility(8);
    bool1 = false;
    try
    {
      paramSet = paramPackageManager.getPackageInfo(paramStatusBarNotification.getPackageName(), 64);
      boolean bool2 = com.android.settingslib.Utils.isSystemPackage(getResources(), paramPackageManager, paramSet);
      bool1 = bool2;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager)
    {
      for (;;) {}
    }
    if (ALLOW_SYSTEM_NOTIFICATION_BE_BLOCKED_PKGS.contains(paramStatusBarNotification.getPackageName())) {
      bool1 = false;
    }
    if (this.mShowSlider)
    {
      bindSlider(localView1, bool1);
      localView1.setVisibility(0);
      localView2.setVisibility(8);
      return;
    }
    bindToggles(localView2, this.mStartingUserImportance, bool1, i);
    localView2.setVisibility(0);
    localView1.setVisibility(8);
  }
  
  public void closeControls(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (getWindowToken() == null)
    {
      if ((paramBoolean) && (this.mListener != null)) {
        this.mListener.onGutsClosed(this);
      }
      return;
    }
    int i;
    if (paramInt1 != -1)
    {
      i = paramInt1;
      paramInt1 = paramInt2;
      if (paramInt2 != -1) {}
    }
    else
    {
      i = (getLeft() + getRight()) / 2;
      paramInt1 = getTop() + getHeight() / 2;
    }
    Animator localAnimator = ViewAnimationUtils.createCircularReveal(this, i, paramInt1, (float)Math.hypot(Math.max(getWidth() - i, i), Math.max(getHeight() - paramInt1, paramInt1)), 0.0F);
    localAnimator.setDuration(360L);
    localAnimator.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
    localAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        super.onAnimationEnd(paramAnonymousAnimator);
        NotificationGuts.this.setVisibility(8);
      }
    });
    localAnimator.start();
    setExposed(false, this.mNeedsFalsingProtection);
    if ((paramBoolean) && (this.mListener != null)) {
      this.mListener.onGutsClosed(this);
    }
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    if (this.mBackground != null) {
      this.mBackground.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    drawableStateChanged(this.mBackground);
  }
  
  public int getActualHeight()
  {
    return this.mActualHeight;
  }
  
  public boolean hasImportanceChanged()
  {
    return this.mStartingUserImportance != getSelectedImportance();
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    TunerService.get(this.mContext).addTunable(this, new String[] { "show_importance_slider" });
  }
  
  protected void onDetachedFromWindow()
  {
    TunerService.get(this.mContext).removeTunable(this);
    super.onDetachedFromWindow();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    draw(paramCanvas, this.mBackground);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mBackground = this.mContext.getDrawable(2130838069);
    if (this.mBackground != null) {
      this.mBackground.setCallback(this);
    }
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    boolean bool2 = false;
    if ("show_importance_slider".equals(paramString1))
    {
      boolean bool1 = bool2;
      if (paramString2 != null)
      {
        bool1 = bool2;
        if (Integer.parseInt(paramString2) != 0) {
          bool1 = true;
        }
      }
      this.mShowSlider = bool1;
    }
  }
  
  public void resetFalsingCheck()
  {
    this.mHandler.removeCallbacks(this.mFalsingCheck);
    if ((this.mNeedsFalsingProtection) && (this.mExposed)) {
      this.mHandler.postDelayed(this.mFalsingCheck, 8000L);
    }
  }
  
  void saveImportance(StatusBarNotification paramStatusBarNotification)
  {
    int i = getSelectedImportance();
    MetricsLogger.action(this.mContext, 291, i - this.mStartingUserImportance);
    MdmLogger.log("notification_level", "setting_finish", "1");
    try
    {
      if (!this.mShowSlider)
      {
        i = 0;
        if ((this.mBlock != null) && (this.mBlock.isChecked()))
        {
          i = 2;
          MdmLogger.log("block_all", "choice_block", paramStatusBarNotification.getPackageName());
        }
        for (;;)
        {
          if (com.android.systemui.util.Utils.DEBUG_ONEPLUS) {
            Log.d("NotificationGuts", "set pkg=" + paramStatusBarNotification.getPackageName() + " oplevel to " + NotificationListenerService.Ranking.opLevelToString(i));
          }
          this.mINotificationManager.setOPLevel(paramStatusBarNotification.getPackageName(), paramStatusBarNotification.getUid(), i);
          return;
          if ((this.mSilent != null) && (this.mSilent.isChecked()))
          {
            i = 1;
            MdmLogger.log("denoise_notification", "choice_denoise", paramStatusBarNotification.getPackageName());
          }
          else
          {
            MdmLogger.log("allow", "choice_permit", paramStatusBarNotification.getPackageName());
          }
        }
      }
      this.mINotificationManager.setImportance(paramStatusBarNotification.getPackageName(), paramStatusBarNotification.getUid(), i);
      return;
    }
    catch (RemoteException paramStatusBarNotification) {}
  }
  
  public void setActualHeight(int paramInt)
  {
    this.mActualHeight = paramInt;
    invalidate();
  }
  
  public void setClipTopAmount(int paramInt)
  {
    this.mClipTopAmount = paramInt;
    invalidate();
  }
  
  public void setClosedListener(OnGutsClosedListener paramOnGutsClosedListener)
  {
    this.mListener = paramOnGutsClosedListener;
  }
  
  public void setExposed(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mExposed = paramBoolean1;
    this.mNeedsFalsingProtection = paramBoolean2;
    if ((this.mExposed) && (this.mNeedsFalsingProtection))
    {
      resetFalsingCheck();
      return;
    }
    this.mHandler.removeCallbacks(this.mFalsingCheck);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mBackground);
  }
  
  public static abstract interface OnGutsClosedListener
  {
    public abstract void onGutsClosed(NotificationGuts paramNotificationGuts);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\NotificationGuts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */