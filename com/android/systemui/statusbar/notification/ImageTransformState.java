package com.android.systemui.statusbar.notification;

import android.graphics.drawable.Icon;
import android.util.Pools.SimplePool;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;

public class ImageTransformState
  extends TransformState
{
  private static Pools.SimplePool<ImageTransformState> sInstancePool = new Pools.SimplePool(40);
  private Icon mIcon;
  
  private static float mapToDuration(float paramFloat)
  {
    return Math.max(Math.min((360.0F * paramFloat - 150.0F) / 210.0F, 1.0F), 0.0F);
  }
  
  public static ImageTransformState obtain()
  {
    ImageTransformState localImageTransformState = (ImageTransformState)sInstancePool.acquire();
    if (localImageTransformState != null) {
      return localImageTransformState;
    }
    return new ImageTransformState();
  }
  
  public void appear(float paramFloat, TransformableView paramTransformableView)
  {
    if ((paramTransformableView instanceof HybridNotificationView))
    {
      if (paramFloat == 0.0F)
      {
        this.mTransformedView.setPivotY(0.0F);
        this.mTransformedView.setPivotX(this.mTransformedView.getWidth() / 2);
        prepareFadeIn();
      }
      paramFloat = mapToDuration(paramFloat);
      CrossFadeHelper.fadeIn(this.mTransformedView, paramFloat, false);
      paramFloat = Interpolators.LINEAR_OUT_SLOW_IN.getInterpolation(paramFloat);
      this.mTransformedView.setScaleX(paramFloat);
      this.mTransformedView.setScaleY(paramFloat);
      return;
    }
    super.appear(paramFloat, paramTransformableView);
  }
  
  public void disappear(float paramFloat, TransformableView paramTransformableView)
  {
    if ((paramTransformableView instanceof HybridNotificationView))
    {
      if (paramFloat == 0.0F)
      {
        this.mTransformedView.setPivotY(0.0F);
        this.mTransformedView.setPivotX(this.mTransformedView.getWidth() / 2);
      }
      paramFloat = mapToDuration(1.0F - paramFloat);
      CrossFadeHelper.fadeOut(this.mTransformedView, 1.0F - paramFloat, false);
      paramFloat = Interpolators.LINEAR_OUT_SLOW_IN.getInterpolation(paramFloat);
      this.mTransformedView.setScaleX(paramFloat);
      this.mTransformedView.setScaleY(paramFloat);
      return;
    }
    super.disappear(paramFloat, paramTransformableView);
  }
  
  public Icon getIcon()
  {
    return this.mIcon;
  }
  
  public void initFrom(View paramView)
  {
    super.initFrom(paramView);
    if ((paramView instanceof ImageView)) {
      this.mIcon = ((Icon)paramView.getTag(2131951682));
    }
  }
  
  public void recycle()
  {
    super.recycle();
    sInstancePool.release(this);
  }
  
  protected void reset()
  {
    super.reset();
    this.mIcon = null;
  }
  
  protected boolean sameAs(TransformState paramTransformState)
  {
    if ((paramTransformState instanceof ImageTransformState))
    {
      if (this.mIcon != null) {
        return this.mIcon.sameAs(((ImageTransformState)paramTransformState).getIcon());
      }
      return false;
    }
    return super.sameAs(paramTransformState);
  }
  
  protected boolean transformScale()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\ImageTransformState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */