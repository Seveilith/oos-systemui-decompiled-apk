package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v17.leanback.R.dimen;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class ShadowOverlayContainer
  extends FrameLayout
{
  private static final Rect sTempRect = new Rect();
  private float mFocusedZ;
  private boolean mInitialized;
  private int mOverlayColor;
  private Paint mOverlayPaint;
  private int mShadowType = 1;
  private float mUnfocusedZ;
  private View mWrappedView;
  
  public ShadowOverlayContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ShadowOverlayContainer(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    useStaticShadow();
    useDynamicShadow();
  }
  
  public static boolean supportsDynamicShadow()
  {
    return ShadowHelper.getInstance().supportsDynamicShadow();
  }
  
  public static boolean supportsShadow()
  {
    return StaticShadowHelper.getInstance().supportsShadow();
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if ((this.mOverlayPaint != null) && (this.mOverlayColor != 0)) {
      paramCanvas.drawRect(this.mWrappedView.getLeft(), this.mWrappedView.getTop(), this.mWrappedView.getRight(), this.mWrappedView.getBottom(), this.mOverlayPaint);
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramBoolean) && (this.mWrappedView != null))
    {
      sTempRect.left = ((int)this.mWrappedView.getPivotX());
      sTempRect.top = ((int)this.mWrappedView.getPivotY());
      offsetDescendantRectToMyCoords(this.mWrappedView, sTempRect);
      setPivotX(sTempRect.left);
      setPivotY(sTempRect.top);
    }
  }
  
  public void useDynamicShadow()
  {
    useDynamicShadow(getResources().getDimension(R.dimen.lb_material_shadow_normal_z), getResources().getDimension(R.dimen.lb_material_shadow_focused_z));
  }
  
  public void useDynamicShadow(float paramFloat1, float paramFloat2)
  {
    if (this.mInitialized) {
      throw new IllegalStateException("Already initialized");
    }
    if (supportsDynamicShadow())
    {
      this.mShadowType = 3;
      this.mUnfocusedZ = paramFloat1;
      this.mFocusedZ = paramFloat2;
    }
  }
  
  public void useStaticShadow()
  {
    if (this.mInitialized) {
      throw new IllegalStateException("Already initialized");
    }
    if (supportsShadow()) {
      this.mShadowType = 2;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\ShadowOverlayContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */