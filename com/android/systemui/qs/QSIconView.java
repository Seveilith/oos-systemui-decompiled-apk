package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.android.systemui.statusbar.policy.MobileSignalController;
import java.util.Objects;

public class QSIconView
  extends ViewGroup
{
  public static int sCustomDisableIconColor;
  public static int sDisableIconColor;
  public static int sIconColor;
  private final int STATUS_BAR_STYLE_ANDROID_DEFAULT = 0;
  protected final int STATUS_BAR_STYLE_EXTENDED = 4;
  protected final String TAG = "QSIconView";
  private boolean mAnimationEnabled = true;
  protected final View mIcon;
  protected final int mIconSizePx;
  protected int mStyle = 0;
  protected final int mTilePaddingBelowIconPx;
  
  public QSIconView(Context paramContext)
  {
    super(paramContext);
    if (MobileSignalController.isCarrierOneSupported()) {}
    for (this.mStyle = 4;; this.mStyle = this.mContext.getResources().getInteger(2131623986))
    {
      paramContext = paramContext.getResources();
      this.mIconSizePx = paramContext.getDimensionPixelSize(2131755420);
      this.mTilePaddingBelowIconPx = paramContext.getDimensionPixelSize(2131755429);
      this.mIcon = createIcon();
      addView(this.mIcon);
      return;
    }
  }
  
  public static void updateThemeColor(int paramInt1, int paramInt2)
  {
    sIconColor = paramInt1;
    sDisableIconColor = paramInt2;
    sCustomDisableIconColor = 0x4DFFFFFF & paramInt2;
  }
  
  protected View createIcon()
  {
    ImageView localImageView = new ImageView(this.mContext);
    localImageView.setId(16908294);
    localImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    return localImageView;
  }
  
  public void disableAnimation()
  {
    this.mAnimationEnabled = false;
  }
  
  protected final int exactly(int paramInt)
  {
    return View.MeasureSpec.makeMeasureSpec(paramInt, 1073741824);
  }
  
  protected int getIconMeasureMode()
  {
    return 1073741824;
  }
  
  public View getIconView()
  {
    return this.mIcon;
  }
  
  protected final void layout(View paramView, int paramInt1, int paramInt2)
  {
    paramView.layout(paramInt1, paramInt2, paramView.getMeasuredWidth() + paramInt1, paramView.getMeasuredHeight() + paramInt2);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt1 = getMeasuredWidth();
    getMeasuredHeight();
    paramInt1 = (paramInt1 - this.mIcon.getMeasuredWidth()) / 2;
    layout(this.mIcon, paramInt1, 0);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = exactly(this.mIconSizePx);
    this.mIcon.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, getIconMeasureMode()), paramInt2);
    setMeasuredDimension(paramInt1, this.mIcon.getMeasuredHeight() + this.mTilePaddingBelowIconPx);
  }
  
  protected void setIcon(ImageView paramImageView, QSTile.State paramState)
  {
    QSTile.Icon localIcon = paramState.icon;
    Drawable localDrawable;
    int i;
    if (!Objects.equals(localIcon, paramImageView.getTag(2131951663)))
    {
      if (paramState.icon != localIcon) {
        Log.d("QSIconView", "icon is changed during setIcon 1, view=" + this);
      }
      if (localIcon == null) {
        break label239;
      }
      if ((!paramImageView.isShown()) || (!this.mAnimationEnabled)) {
        break label225;
      }
      localDrawable = localIcon.getDrawable(this.mContext);
      if (localIcon == null) {
        break label245;
      }
      i = localIcon.getPadding();
      label95:
      if (localDrawable != null)
      {
        if (paramState.autoMirrorDrawable) {
          localDrawable.setAutoMirrored(true);
        }
        if (!paramState.noDisableColor) {
          break label250;
        }
        localDrawable.setTint(sIconColor);
      }
    }
    for (;;)
    {
      paramImageView.setImageDrawable(localDrawable);
      if (paramState.icon != localIcon) {
        Log.d("QSIconView", "icon is changed during setIcon 2, view=" + this);
      }
      paramImageView.setTag(2131951663, localIcon);
      paramImageView.setPadding(0, i, 0, i);
      if (((localDrawable instanceof Animatable)) && (paramImageView.isShown()))
      {
        paramState = (Animatable)localDrawable;
        paramState.start();
        if (!paramImageView.isShown()) {
          paramState.stop();
        }
      }
      return;
      label225:
      localDrawable = localIcon.getInvisibleDrawable(this.mContext);
      break;
      label239:
      localDrawable = null;
      break;
      label245:
      i = 0;
      break label95;
      label250:
      if ((paramState instanceof QSTile.SignalState))
      {
        if (((QSTile.SignalState)paramState).colored) {
          localDrawable.setTint(sIconColor);
        } else {
          localDrawable.setTint(sDisableIconColor);
        }
      }
      else if ((paramState instanceof QSTile.AirplaneBooleanState))
      {
        QSTile.AirplaneBooleanState localAirplaneBooleanState = (QSTile.AirplaneBooleanState)paramState;
        if ((!localAirplaneBooleanState.value) || (localAirplaneBooleanState.isAirplaneMode)) {
          localDrawable.setTint(sDisableIconColor);
        } else {
          localDrawable.setTint(sIconColor);
        }
      }
      else if ((paramState instanceof QSTile.BooleanState))
      {
        if (((QSTile.BooleanState)paramState).value) {
          localDrawable.setTint(sIconColor);
        } else {
          localDrawable.setTint(sDisableIconColor);
        }
      }
    }
  }
  
  public void setIcon(QSTile.State paramState)
  {
    setIcon((ImageView)this.mIcon, paramState);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QSIconView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */