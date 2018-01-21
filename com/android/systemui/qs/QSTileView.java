package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.MathUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.FontSizeUtils;
import libcore.util.Objects;

public class QSTileView
  extends QSTileBaseView
{
  public static int sTextColor;
  protected final Context mContext;
  protected TextView mLabel;
  private ImageView mPadLock;
  private int mTilePaddingTopPx;
  private final int mTileSpacingPx;
  
  public QSTileView(Context paramContext, QSIconView paramQSIconView)
  {
    this(paramContext, paramQSIconView, false);
  }
  
  public QSTileView(Context paramContext, QSIconView paramQSIconView, boolean paramBoolean)
  {
    super(paramContext, paramQSIconView, paramBoolean);
    this.mContext = paramContext;
    this.mTileSpacingPx = paramContext.getResources().getDimensionPixelSize(2131755431);
    setClipChildren(false);
    setClickable(true);
    updateTopPadding();
    setId(View.generateViewId());
    createLabel();
    setOrientation(1);
    setGravity(17);
  }
  
  public static void updateThemeColor(int paramInt)
  {
    sTextColor = paramInt;
  }
  
  private void updateTopPadding()
  {
    Resources localResources = getResources();
    int i = localResources.getDimensionPixelSize(2131755427);
    int j = localResources.getDimensionPixelSize(2131755428);
    float f = (MathUtils.constrain(getResources().getConfiguration().fontScale, 1.0F, 1.3F) - 1.0F) / 0.29999995F;
    this.mTilePaddingTopPx = Math.round((1.0F - f) * i + j * f);
    setPadding(this.mTileSpacingPx, this.mTilePaddingTopPx + this.mTileSpacingPx, this.mTileSpacingPx, 0);
    requestLayout();
  }
  
  protected void createLabel()
  {
    View localView = LayoutInflater.from(this.mContext).inflate(2130968780, null);
    this.mLabel = ((TextView)localView.findViewById(2131952160));
    this.mLabel.setTextColor(sTextColor);
    this.mPadLock = ((ImageView)localView.findViewById(2131952161));
    addView(localView);
  }
  
  TextView getLabel()
  {
    return this.mLabel;
  }
  
  protected void handleStateChanged(QSTile.State paramState)
  {
    int i = 0;
    super.handleStateChanged(paramState);
    if (!Objects.equal(this.mLabel.getText(), paramState.label)) {
      this.mLabel.setText(paramState.label);
    }
    Object localObject = this.mLabel;
    boolean bool;
    if (paramState.disabledByPolicy)
    {
      bool = false;
      ((TextView)localObject).setEnabled(bool);
      localObject = this.mPadLock;
      if (!paramState.disabledByPolicy) {
        break label81;
      }
    }
    for (;;)
    {
      ((ImageView)localObject).setVisibility(i);
      return;
      bool = true;
      break;
      label81:
      i = 8;
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    updateTopPadding();
    FontSizeUtils.updateFontSize(this.mLabel, 2131755421);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QSTileView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */