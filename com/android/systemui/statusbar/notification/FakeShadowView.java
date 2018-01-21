package com.android.systemui.statusbar.notification;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout.LayoutParams;
import com.android.systemui.statusbar.AlphaOptimizedFrameLayout;

public class FakeShadowView
  extends AlphaOptimizedFrameLayout
{
  private View mFakeShadow;
  private float mOutlineAlpha;
  private final int mShadowMinHeight;
  
  public FakeShadowView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public FakeShadowView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public FakeShadowView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public FakeShadowView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mFakeShadow = new View(paramContext);
    this.mFakeShadow.setVisibility(4);
    this.mFakeShadow.setLayoutParams(new LinearLayout.LayoutParams(-1, (int)(getResources().getDisplayMetrics().density * 48.0F)));
    this.mFakeShadow.setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setRect(0, 0, FakeShadowView.this.getWidth(), FakeShadowView.-get0(FakeShadowView.this).getHeight());
        paramAnonymousOutline.setAlpha(FakeShadowView.-get1(FakeShadowView.this));
      }
    });
    addView(this.mFakeShadow);
    this.mShadowMinHeight = Math.max(1, paramContext.getResources().getDimensionPixelSize(2131755461));
  }
  
  public void setFakeShadowTranslationZ(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
  {
    if (paramFloat1 == 0.0F) {
      this.mFakeShadow.setVisibility(4);
    }
    do
    {
      return;
      this.mFakeShadow.setVisibility(0);
      paramFloat1 = Math.max(this.mShadowMinHeight, paramFloat1);
      this.mFakeShadow.setTranslationZ(paramFloat1);
      this.mFakeShadow.setTranslationX(paramInt2);
      this.mFakeShadow.setTranslationY(paramInt1 - this.mFakeShadow.getHeight());
    } while (paramFloat2 == this.mOutlineAlpha);
    this.mOutlineAlpha = paramFloat2;
    this.mFakeShadow.invalidateOutline();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\FakeShadowView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */