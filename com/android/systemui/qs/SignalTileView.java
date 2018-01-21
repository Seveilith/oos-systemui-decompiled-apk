package com.android.systemui.qs;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public final class SignalTileView
  extends QSIconView
{
  private static final long DEFAULT_DURATION = new ValueAnimator().getDuration();
  private static final long SHORT_DURATION = DEFAULT_DURATION / 3L;
  private FrameLayout mIconFrame;
  private ImageView mIn = addTrafficView(2130837839);
  private ImageView mOut = addTrafficView(2130837843);
  private ImageView mOverlay;
  private int mOverlayStartPadding;
  private ImageView mRoaming;
  private ImageView mSignal;
  private int mWideOverlayIconStartPadding;
  
  public SignalTileView(Context paramContext)
  {
    super(paramContext);
    this.mWideOverlayIconStartPadding = paramContext.getResources().getDimensionPixelSize(2131755535);
    this.mOverlayStartPadding = paramContext.getResources().getDimensionPixelSize(2131755713);
  }
  
  private ImageView addTrafficView(int paramInt)
  {
    ImageView localImageView = new ImageView(this.mContext);
    localImageView.setImageResource(paramInt);
    localImageView.setAlpha(0.0F);
    addView(localImageView);
    return localImageView;
  }
  
  private void layoutIndicator(View paramView)
  {
    int i = 1;
    int j;
    if (getLayoutDirection() == 1)
    {
      if (i == 0) {
        break label60;
      }
      j = this.mIconFrame.getLeft();
      i = j - paramView.getMeasuredWidth();
    }
    for (;;)
    {
      paramView.layout(i, this.mIconFrame.getBottom() - paramView.getMeasuredHeight(), j, this.mIconFrame.getBottom());
      return;
      i = 0;
      break;
      label60:
      i = this.mIconFrame.getRight();
      j = i + paramView.getMeasuredWidth();
    }
  }
  
  private void setVisibility(View paramView, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (paramBoolean2)) {}
    float f;
    for (int i = 1;; i = 0)
    {
      f = i;
      if (paramView.getAlpha() != f) {
        break;
      }
      return;
    }
    if (paramBoolean1)
    {
      paramView = paramView.animate();
      if (paramBoolean2) {}
      for (long l = SHORT_DURATION;; l = DEFAULT_DURATION)
      {
        paramView.setDuration(l).alpha(f).start();
        return;
      }
    }
    paramView.setAlpha(f);
  }
  
  protected View createIcon()
  {
    this.mIconFrame = new FrameLayout(this.mContext);
    this.mSignal = new ImageView(this.mContext);
    this.mIconFrame.addView(this.mSignal);
    this.mOverlay = new ImageView(this.mContext);
    LinearLayout localLinearLayout;
    if ((getContext().getResources().getBoolean(2131558440)) || (this.mStyle == 4))
    {
      this.mRoaming = new ImageView(this.mContext);
      this.mRoaming.setImageResource(2130837844);
      this.mRoaming.setVisibility(8);
      localLinearLayout = new LinearLayout(this.mContext);
      if (getContext().getResources().getBoolean(2131558440))
      {
        localLinearLayout.addView(this.mRoaming, -2, -2);
        localLinearLayout.addView(this.mOverlay, -2, -2);
        this.mIconFrame.addView(localLinearLayout, -2, -2);
      }
    }
    for (;;)
    {
      return this.mIconFrame;
      localLinearLayout.addView(this.mOverlay, -2, -2);
      localLinearLayout.addView(this.mRoaming, -2, -2);
      break;
      this.mIconFrame.addView(this.mOverlay, -2, -2);
    }
  }
  
  protected int getIconMeasureMode()
  {
    return Integer.MIN_VALUE;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    layoutIndicator(this.mIn);
    layoutIndicator(this.mOut);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    paramInt1 = View.MeasureSpec.makeMeasureSpec(this.mIconFrame.getMeasuredHeight(), 1073741824);
    paramInt2 = View.MeasureSpec.makeMeasureSpec(this.mIconFrame.getMeasuredHeight(), Integer.MIN_VALUE);
    this.mIn.measure(paramInt2, paramInt1);
    this.mOut.measure(paramInt2, paramInt1);
  }
  
  public void setIcon(QSTile.State paramState)
  {
    int j = 0;
    QSTile.SignalState localSignalState = (QSTile.SignalState)paramState;
    setIcon(this.mSignal, localSignalState);
    label76:
    Object localObject;
    int i;
    if (localSignalState.overlayIconId > 0)
    {
      this.mOverlay.setVisibility(0);
      this.mOverlay.setImageResource(localSignalState.overlayIconId);
      if ((localSignalState.overlayIconId <= 0) || (!localSignalState.isOverlayIconWide)) {
        break label287;
      }
      this.mOverlay.setPaddingRelative(this.mWideOverlayIconStartPadding, 0, 0, 0);
      localObject = this.mSignal.getDrawable();
      i = sIconColor;
      if (localObject != null)
      {
        if (paramState.autoMirrorDrawable) {
          ((Drawable)localObject).setAutoMirrored(true);
        }
        ((Drawable)localObject).setTintList(null);
        if (!localSignalState.colored) {
          break label304;
        }
        i = sIconColor;
        label125:
        ((Drawable)localObject).setTint(i);
      }
      boolean bool = isShown();
      setVisibility(this.mIn, bool, localSignalState.activityIn);
      setVisibility(this.mOut, bool, localSignalState.activityOut);
      if (((this.mRoaming != null) && (getContext().getResources().getBoolean(2131558440))) || (this.mStyle == 4))
      {
        paramState = (TelephonyManager)this.mContext.getSystemService("phone");
        localObject = this.mRoaming;
        if ((!paramState.isNetworkRoaming(localSignalState.subId)) || (!localSignalState.isShowRoaming)) {
          break label311;
        }
      }
    }
    for (;;)
    {
      ((ImageView)localObject).setVisibility(j);
      this.mRoaming.setColorFilter(i);
      this.mOverlay.setColorFilter(i);
      this.mIn.setColorFilter(i);
      this.mOut.setColorFilter(i);
      return;
      this.mOverlay.setVisibility(8);
      break;
      label287:
      this.mOverlay.setPaddingRelative(this.mOverlayStartPadding, 0, 0, 0);
      break label76;
      label304:
      i = sDisableIconColor;
      break label125;
      label311:
      j = 8;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\SignalTileView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */