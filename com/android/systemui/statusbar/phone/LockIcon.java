package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.util.Utils;

public class LockIcon
  extends KeyguardAffordanceView
{
  private AccessibilityController mAccessibilityController;
  private int mDensity;
  private boolean mDeviceInteractive;
  private Animation mFacelockAnimationSet;
  private Animation mFacelockFailAnimationSet;
  private int mFacelockRunningType = 0;
  private boolean mHasFingerPrintIcon;
  private boolean mLastDeviceInteractive;
  private boolean mLastScreenOn;
  private int mLastState = 0;
  private boolean mScreenOn;
  private boolean mTransientFpError;
  private TrustDrawable mTrustDrawable;
  private final UnlockMethodCache mUnlockMethodCache;
  
  public LockIcon(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mTrustDrawable = new TrustDrawable(paramContext);
    setBackground(this.mTrustDrawable);
    this.mUnlockMethodCache = UnlockMethodCache.getInstance(paramContext);
    this.mFacelockAnimationSet = AnimationUtils.loadAnimation(this.mContext, 2131034141);
    this.mFacelockFailAnimationSet = AnimationUtils.loadAnimation(this.mContext, 2131034142);
  }
  
  private int getAnimationResForTransition(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    if (KeyguardUpdateMonitor.getInstance(this.mContext).shouldShowFacelockIcon()) {
      return -1;
    }
    if ((paramInt1 == 3) && (paramInt2 == 4)) {
      return 2130838027;
    }
    if ((paramInt1 == 1) && (paramInt2 == 4)) {
      return 2130839025;
    }
    if ((paramInt1 == 4) && (paramInt2 == 1)) {
      return 2130837666;
    }
    if ((paramInt1 == 4) && (paramInt2 == 3)) {
      return 2130838025;
    }
    if ((paramInt1 != 3) || (paramInt2 != 1) || (this.mUnlockMethodCache.isTrusted())) {
      if (paramInt2 == 3)
      {
        if ((!paramBoolean3) && (paramBoolean4) && (paramBoolean2)) {
          break label126;
        }
        if ((paramBoolean4) && (!paramBoolean1)) {
          break label121;
        }
      }
    }
    label121:
    while (!paramBoolean2)
    {
      return -1;
      return 2130838021;
    }
    label126:
    return 2130838023;
  }
  
  private int getIconForState(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    switch (paramInt)
    {
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    default: 
      throw new IllegalArgumentException();
    case 11: 
      return 2130837671;
    case 0: 
      return 2130837749;
    case 1: 
      return 2130837750;
    case 2: 
      return 17302247;
    case 3: 
      if ((paramBoolean1) && (paramBoolean2)) {
        return 2130837717;
      }
      return 2130838023;
    }
    return 2130837718;
  }
  
  private int getState()
  {
    KeyguardUpdateMonitor localKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
    boolean bool1 = localKeyguardUpdateMonitor.isFingerprintDetectionRunning();
    boolean bool2 = localKeyguardUpdateMonitor.isUnlockingWithFingerprintAllowed();
    boolean bool3 = localKeyguardUpdateMonitor.isFingerprintLockout();
    if (KeyguardUpdateMonitor.getInstance(this.mContext).shouldShowFacelockIcon()) {
      return 11;
    }
    if (this.mTransientFpError) {
      return 4;
    }
    if (this.mUnlockMethodCache.canSkipBouncer()) {
      return 1;
    }
    if (this.mUnlockMethodCache.isFaceUnlockRunning()) {
      return 2;
    }
    if ((!bool1) || (!bool2) || (bool3)) {
      return 0;
    }
    return 3;
  }
  
  private void updateClickability()
  {
    boolean bool3 = true;
    if (this.mAccessibilityController == null) {
      return;
    }
    boolean bool2 = this.mAccessibilityController.isTouchExplorationEnabled();
    int i;
    int j;
    label58:
    boolean bool1;
    if (this.mUnlockMethodCache.isTrustManaged()) {
      if (this.mAccessibilityController.isAccessibilityEnabled())
      {
        i = 0;
        if (!this.mUnlockMethodCache.isTrustManaged()) {
          break label131;
        }
        if (i == 0) {
          break label126;
        }
        j = 0;
        bool1 = KeyguardUpdateMonitor.getInstance(this.mContext).isFacelockAvailable();
        if ((i != 0) || (bool2)) {
          break label136;
        }
      }
    }
    label126:
    label131:
    label136:
    for (bool2 = bool1;; bool2 = true)
    {
      setClickable(bool2);
      bool2 = bool3;
      if (j == 0) {
        bool2 = bool1;
      }
      setLongClickable(bool2);
      setFocusable(this.mAccessibilityController.isAccessibilityEnabled());
      return;
      i = 1;
      break;
      i = 0;
      break;
      j = 1;
      break label58;
      j = 0;
      break label58;
    }
  }
  
  private void updateIconAnimation(final View paramView)
  {
    if ((this.mFacelockAnimationSet == null) || (this.mFacelockFailAnimationSet == null)) {
      return;
    }
    if (!KeyguardUpdateMonitor.getInstance(this.mContext).isFacelockRecognizing())
    {
      if (Utils.DEBUG_ONEPLUS) {
        Log.d("LockIcon", "stop recog anim");
      }
      paramView.clearAnimation();
      this.mFacelockAnimationSet.setAnimationListener(null);
      if (KeyguardUpdateMonitor.getInstance(this.mContext).shouldPlayFacelockFailAnim())
      {
        if (Utils.DEBUG_ONEPLUS) {
          Log.d("LockIcon", "play fail anim");
        }
        paramView.startAnimation(this.mFacelockFailAnimationSet);
      }
      return;
    }
    this.mFacelockAnimationSet.setAnimationListener(new Animation.AnimationListener()
    {
      public void onAnimationEnd(Animation paramAnonymousAnimation)
      {
        if ((!KeyguardUpdateMonitor.getInstance(LockIcon.-get0(LockIcon.this)).isFacelockRecognizing()) || (LockIcon.-get1(LockIcon.this) == null)) {
          return;
        }
        if (Utils.DEBUG_ONEPLUS) {
          Log.d("LockIcon", "start recog anim again");
        }
        LockIcon.-get1(LockIcon.this).setAnimationListener(this);
        paramView.startAnimation(LockIcon.-get1(LockIcon.this));
      }
      
      public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
      
      public void onAnimationStart(Animation paramAnonymousAnimation) {}
    });
    if (Utils.DEBUG_ONEPLUS) {
      Log.d("LockIcon", "start anim");
    }
    paramView.startAnimation(this.mFacelockAnimationSet);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    int i = paramConfiguration.densityDpi;
    if (i != this.mDensity)
    {
      this.mDensity = i;
      this.mTrustDrawable.stop();
      this.mTrustDrawable = new TrustDrawable(getContext());
      setBackground(this.mTrustDrawable);
      update();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mTrustDrawable.stop();
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    if (this.mHasFingerPrintIcon)
    {
      paramAccessibilityNodeInfo.setClassName(LockIcon.class.getName());
      paramAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, getContext().getString(2131690094)));
    }
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if (isShown())
    {
      this.mTrustDrawable.start();
      return;
    }
    this.mTrustDrawable.stop();
  }
  
  public void setAccessibilityController(AccessibilityController paramAccessibilityController)
  {
    this.mAccessibilityController = paramAccessibilityController;
  }
  
  public void setDeviceInteractive(boolean paramBoolean)
  {
    this.mDeviceInteractive = paramBoolean;
    update();
  }
  
  public void setFacelockRunning(int paramInt, boolean paramBoolean)
  {
    if (this.mFacelockRunningType == paramInt) {
      return;
    }
    Log.d("LockIcon", "setFacelockRunning , type:" + paramInt + ", updateIcon:" + paramBoolean);
    this.mFacelockRunningType = paramInt;
    if (paramBoolean) {
      update(true);
    }
  }
  
  public void setScreenOn(boolean paramBoolean)
  {
    this.mScreenOn = paramBoolean;
    update();
  }
  
  public void setTransientFpError(boolean paramBoolean)
  {
    this.mTransientFpError = paramBoolean;
    update();
  }
  
  public void update()
  {
    update(false);
  }
  
  public void update(boolean paramBoolean)
  {
    boolean bool1;
    label31:
    int m;
    label52:
    boolean bool3;
    boolean bool2;
    label80:
    int k;
    label125:
    int j;
    AnimatedVectorDrawable localAnimatedVectorDrawable;
    label179:
    label268:
    float f;
    label284:
    label311:
    label377:
    boolean bool4;
    if (isShown())
    {
      bool1 = KeyguardUpdateMonitor.getInstance(this.mContext).isDeviceInteractive();
      if (!bool1) {
        break label443;
      }
      this.mTrustDrawable.start();
      m = getState();
      if ((m != 3) && (m != 4)) {
        break label453;
      }
      bool1 = true;
      bool3 = bool1;
      bool2 = bool1;
      if ((m == this.mLastState) && (this.mDeviceInteractive == this.mLastDeviceInteractive)) {
        break label459;
      }
      int i = 1;
      k = getAnimationResForTransition(this.mLastState, m, this.mLastDeviceInteractive, this.mDeviceInteractive, this.mLastScreenOn, this.mScreenOn);
      if (k != 2130838021) {
        break label481;
      }
      bool1 = true;
      bool2 = true;
      paramBoolean = true;
      j = k;
      if (k == -1)
      {
        j = getIconForState(m, this.mScreenOn, this.mDeviceInteractive);
        i = 0;
      }
      Drawable localDrawable = this.mContext.getDrawable(j);
      if (!(localDrawable instanceof AnimatedVectorDrawable)) {
        break label524;
      }
      localAnimatedVectorDrawable = (AnimatedVectorDrawable)localDrawable;
      j = getResources().getDimensionPixelSize(2131755521);
      k = getResources().getDimensionPixelSize(2131755522);
      Object localObject = localDrawable;
      if (!bool1) {
        if (localDrawable.getIntrinsicHeight() == j)
        {
          localObject = localDrawable;
          if (localDrawable.getIntrinsicWidth() == k) {}
        }
        else
        {
          localObject = new IntrinsicSizeDrawable(localDrawable, k, j);
        }
      }
      if (!bool2) {
        break label530;
      }
      j = getResources().getDimensionPixelSize(2131755552);
      setPaddingRelative(0, 0, 0, j);
      if (!bool1) {
        break label536;
      }
      f = 1.0F;
      setRestingAlpha(f);
      setImageDrawable((Drawable)localObject);
      localObject = getResources();
      if (!bool1) {
        break label543;
      }
      j = 2131690093;
      setContentDescription(((Resources)localObject).getString(j));
      this.mHasFingerPrintIcon = bool1;
      if ((localAnimatedVectorDrawable != null) && (i != 0))
      {
        localAnimatedVectorDrawable.forceAnimationOnUI();
        localAnimatedVectorDrawable.start();
      }
      if ((this.mLastState == 11) || (m != 11)) {
        break label551;
      }
      setBackground(this.mContext.getDrawable(2130837672));
      updateIconAnimation(this);
      this.mLastState = m;
      this.mLastDeviceInteractive = this.mDeviceInteractive;
      this.mLastScreenOn = this.mScreenOn;
      bool4 = paramBoolean;
      label407:
      if ((this.mUnlockMethodCache.isTrustManaged()) && (!bool4)) {
        break label569;
      }
    }
    label443:
    label453:
    label459:
    label481:
    label524:
    label530:
    label536:
    label543:
    label551:
    label569:
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mTrustDrawable.setTrustManaged(paramBoolean);
      updateClickability();
      return;
      bool1 = false;
      break;
      this.mTrustDrawable.stop();
      break label31;
      bool1 = false;
      break label52;
      if (this.mScreenOn != this.mLastScreenOn) {
        break label80;
      }
      bool4 = bool2;
      if (!paramBoolean) {
        break label407;
      }
      break label80;
      if (k == 2130839025)
      {
        bool1 = true;
        bool2 = false;
        paramBoolean = true;
        break label125;
      }
      paramBoolean = bool2;
      bool2 = bool3;
      if (k != 2130837666) {
        break label125;
      }
      bool1 = true;
      bool2 = false;
      paramBoolean = false;
      break label125;
      localAnimatedVectorDrawable = null;
      break label179;
      j = 0;
      break label268;
      f = 0.5F;
      break label284;
      j = 2131690092;
      break label311;
      if (m == 11) {
        break label377;
      }
      setBackground(this.mTrustDrawable);
      break label377;
    }
  }
  
  private static class IntrinsicSizeDrawable
    extends InsetDrawable
  {
    private final int mIntrinsicHeight;
    private final int mIntrinsicWidth;
    
    public IntrinsicSizeDrawable(Drawable paramDrawable, int paramInt1, int paramInt2)
    {
      super(0);
      this.mIntrinsicWidth = paramInt1;
      this.mIntrinsicHeight = paramInt2;
    }
    
    public int getIntrinsicHeight()
    {
      return this.mIntrinsicHeight;
    }
    
    public int getIntrinsicWidth()
    {
      return this.mIntrinsicWidth;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\LockIcon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */