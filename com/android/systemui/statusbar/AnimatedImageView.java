package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews.RemoteView;
import com.android.systemui.R.styleable;

@RemoteViews.RemoteView
public class AnimatedImageView
  extends ImageView
{
  AnimationDrawable mAnim;
  boolean mAttached;
  int mDrawableId;
  private final boolean mHasOverlappingRendering;
  
  public AnimatedImageView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AnimatedImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.AnimatedImageView, 0, 0);
    try
    {
      this.mHasOverlappingRendering = paramContext.getBoolean(0, true);
      return;
    }
    finally
    {
      paramContext.recycle();
    }
  }
  
  private void updateAnim()
  {
    Drawable localDrawable = getDrawable();
    if ((this.mAttached) && (this.mAnim != null)) {
      this.mAnim.stop();
    }
    if ((localDrawable instanceof AnimationDrawable))
    {
      this.mAnim = ((AnimationDrawable)localDrawable);
      if (isShown()) {
        this.mAnim.start();
      }
      return;
    }
    this.mAnim = null;
  }
  
  public boolean hasOverlappingRendering()
  {
    return this.mHasOverlappingRendering;
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mAttached = true;
    updateAnim();
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mAnim != null) {
      this.mAnim.stop();
    }
    this.mAttached = false;
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if (this.mAnim != null)
    {
      if (isShown()) {
        this.mAnim.start();
      }
    }
    else {
      return;
    }
    this.mAnim.stop();
  }
  
  public void setImageDrawable(Drawable paramDrawable)
  {
    if (paramDrawable != null) {
      if (this.mDrawableId == paramDrawable.hashCode()) {
        return;
      }
    }
    for (this.mDrawableId = paramDrawable.hashCode();; this.mDrawableId = 0)
    {
      super.setImageDrawable(paramDrawable);
      updateAnim();
      return;
    }
  }
  
  @RemotableViewMethod
  public void setImageResource(int paramInt)
  {
    if (this.mDrawableId == paramInt) {
      return;
    }
    this.mDrawableId = paramInt;
    super.setImageResource(paramInt);
    updateAnim();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\AnimatedImageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */