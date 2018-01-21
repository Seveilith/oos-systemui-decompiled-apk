package com.android.keyguard;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;

public class KeyguardPINView
  extends KeyguardPinBasedInputViewForPin
{
  private final AppearAnimationUtils mAppearAnimationUtils;
  private ViewGroup mContainer;
  private final DisappearAnimationUtils mDisappearAnimationUtils;
  private final DisappearAnimationUtils mDisappearAnimationUtilsLocked;
  private int mDisappearYTranslation;
  private View mFingerprintIcon;
  private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
  private ViewGroup mRow0;
  private ViewGroup mRow1;
  private ViewGroup mRow2;
  private ViewGroup mRow3;
  private View[][] mViews;
  
  public KeyguardPINView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardPINView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mAppearAnimationUtils = new AppearAnimationUtils(paramContext);
    this.mDisappearAnimationUtils = new DisappearAnimationUtils(paramContext, 125L, 0.6F, 0.45F, AnimationUtils.loadInterpolator(this.mContext, 17563663));
    this.mDisappearAnimationUtilsLocked = new DisappearAnimationUtils(paramContext, 187L, 0.6F, 0.45F, AnimationUtils.loadInterpolator(this.mContext, 17563663));
    this.mDisappearYTranslation = getResources().getDimensionPixelSize(R.dimen.disappear_y_translation);
    this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(paramContext);
  }
  
  private void enableClipping(boolean paramBoolean)
  {
    this.mContainer.setClipToPadding(paramBoolean);
    this.mContainer.setClipChildren(paramBoolean);
    this.mRow1.setClipToPadding(paramBoolean);
    this.mRow2.setClipToPadding(paramBoolean);
    this.mRow3.setClipToPadding(paramBoolean);
    setClipChildren(paramBoolean);
  }
  
  protected int getPasswordTextViewId()
  {
    return R.id.pinEntry;
  }
  
  public int getWrongPasswordStringId()
  {
    int i = KeyguardUpdateMonitor.getInstance(this.mContext).getFailedUnlockAttempts(KeyguardUpdateMonitor.getCurrentUser());
    if (i % 5 == 3) {
      return R.string.kg_wrong_pin_warning;
    }
    if (i % 5 == 4) {
      return R.string.kg_wrong_pin_warning_one;
    }
    return R.string.kg_wrong_pin;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mContainer = ((ViewGroup)findViewById(R.id.container));
    this.mRow0 = ((ViewGroup)findViewById(R.id.row0));
    this.mRow1 = ((ViewGroup)findViewById(R.id.row1));
    this.mRow2 = ((ViewGroup)findViewById(R.id.row2));
    this.mRow3 = ((ViewGroup)findViewById(R.id.row3));
    View localView1 = findViewById(R.id.keyguard_message_area);
    ViewGroup localViewGroup = this.mRow0;
    View localView2 = findViewById(R.id.key1);
    View localView3 = findViewById(R.id.key2);
    View localView4 = findViewById(R.id.key3);
    View localView5 = findViewById(R.id.key4);
    View localView6 = findViewById(R.id.key5);
    View localView7 = findViewById(R.id.key6);
    View[] arrayOfView1 = { findViewById(R.id.key7), findViewById(R.id.key8), findViewById(R.id.key9) };
    View[] arrayOfView2 = { findViewById(R.id.deleteOrCancel), findViewById(R.id.key0), findViewById(R.id.key_enter) };
    View localView8 = this.mEcaView;
    this.mViews = new View[][] { { null, localView1, null }, { localViewGroup, null, null }, { localView2, localView3, localView4 }, { localView5, localView6, localView7 }, arrayOfView1, arrayOfView2, { null, localView8, null } };
    this.mFingerprintIcon = findViewById(R.id.fingerprint_icon);
  }
  
  protected void resetState()
  {
    super.resetState();
    this.mSecurityMessageDisplay.setTimeout(0);
    this.mSecurityMessageDisplay.setMessage(getMessageWithCount(R.string.kg_pin_instructions), true);
  }
  
  public void startAppearAnimation()
  {
    enableClipping(false);
    setAlpha(1.0F);
    setTranslationY(this.mAppearAnimationUtils.getStartTranslation());
    AppearAnimationUtils.startTranslationYAnimation(this, 0L, 500L, 0.0F, this.mAppearAnimationUtils.getInterpolator());
    this.mAppearAnimationUtils.startAnimation2d(this.mViews, new Runnable()
    {
      public void run()
      {
        KeyguardPINView.-wrap0(KeyguardPINView.this, true);
      }
    });
  }
  
  public boolean startDisappearAnimation(final Runnable paramRunnable)
  {
    enableClipping(false);
    setTranslationY(0.0F);
    AppearAnimationUtils.startTranslationYAnimation(this, 0L, 500L, this.mDisappearYTranslation, this.mDisappearAnimationUtils.getInterpolator());
    if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {}
    for (DisappearAnimationUtils localDisappearAnimationUtils = this.mDisappearAnimationUtilsLocked;; localDisappearAnimationUtils = this.mDisappearAnimationUtils)
    {
      localDisappearAnimationUtils.startAnimation2d(this.mViews, new Runnable()
      {
        public void run()
        {
          KeyguardPINView.-wrap0(KeyguardPINView.this, true);
          if (paramRunnable != null) {
            paramRunnable.run();
          }
        }
      });
      if (this.mFingerprintIcon.getVisibility() == 0) {
        this.mDisappearAnimationUtils.createAnimation(this.mFingerprintIcon, 0L, 200L, -this.mDisappearAnimationUtils.getStartTranslation() * 3.0F, false, this.mDisappearAnimationUtils.getInterpolator(), null);
      }
      return true;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardPINView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */