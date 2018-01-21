package com.android.systemui.recents.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Property;
import android.view.View;
import com.android.systemui.recents.misc.Utilities;
import java.util.ArrayList;

public class TaskViewTransform
{
  public static final Property<View, Rect> LTRB = new Property(Rect.class, "leftTopRightBottom")
  {
    private Rect mTmpRect = new Rect();
    
    public Rect get(View paramAnonymousView)
    {
      this.mTmpRect.set(paramAnonymousView.getLeft(), paramAnonymousView.getTop(), paramAnonymousView.getRight(), paramAnonymousView.getBottom());
      return this.mTmpRect;
    }
    
    public void set(View paramAnonymousView, Rect paramAnonymousRect)
    {
      paramAnonymousView.setLeftTopRightBottom(paramAnonymousRect.left, paramAnonymousRect.top, paramAnonymousRect.right, paramAnonymousRect.bottom);
    }
  };
  public float alpha = 1.0F;
  public float dimAlpha = 0.0F;
  public RectF rect = new RectF();
  public float scale = 1.0F;
  public float translationZ = 0.0F;
  public float viewOutlineAlpha = 0.0F;
  public boolean visible = false;
  
  public static void reset(TaskView paramTaskView)
  {
    paramTaskView.setTranslationX(0.0F);
    paramTaskView.setTranslationY(0.0F);
    paramTaskView.setTranslationZ(0.0F);
    paramTaskView.setScaleX(1.0F);
    paramTaskView.setScaleY(1.0F);
    paramTaskView.setAlpha(1.0F);
    paramTaskView.getViewBounds().setClipBottom(0);
    paramTaskView.setLeftTopRightBottom(0, 0, 0, 0);
  }
  
  public void applyToTaskView(TaskView paramTaskView, ArrayList<Animator> paramArrayList, AnimationProps paramAnimationProps, boolean paramBoolean)
  {
    if (!this.visible) {
      return;
    }
    if (paramAnimationProps.isImmediate())
    {
      if ((paramBoolean) && (hasTranslationZChangedFrom(paramTaskView.getTranslationZ()))) {
        paramTaskView.setTranslationZ(this.translationZ);
      }
      if (hasScaleChangedFrom(paramTaskView.getScaleX()))
      {
        paramTaskView.setScaleX(this.scale);
        paramTaskView.setScaleY(this.scale);
      }
      if (hasAlphaChangedFrom(paramTaskView.getAlpha())) {
        paramTaskView.setAlpha(this.alpha);
      }
      if (hasRectChangedFrom(paramTaskView)) {
        paramTaskView.setLeftTopRightBottom((int)this.rect.left, (int)this.rect.top, (int)this.rect.right, (int)this.rect.bottom);
      }
    }
    do
    {
      return;
      if ((paramBoolean) && (hasTranslationZChangedFrom(paramTaskView.getTranslationZ()))) {
        paramArrayList.add(paramAnimationProps.apply(3, ObjectAnimator.ofFloat(paramTaskView, View.TRANSLATION_Z, new float[] { paramTaskView.getTranslationZ(), this.translationZ })));
      }
      if (hasScaleChangedFrom(paramTaskView.getScaleX())) {
        paramArrayList.add(paramAnimationProps.apply(5, ObjectAnimator.ofPropertyValuesHolder(paramTaskView, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat(View.SCALE_X, new float[] { paramTaskView.getScaleX(), this.scale }), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[] { paramTaskView.getScaleX(), this.scale }) })));
      }
      if (hasAlphaChangedFrom(paramTaskView.getAlpha())) {
        paramArrayList.add(paramAnimationProps.apply(4, ObjectAnimator.ofFloat(paramTaskView, View.ALPHA, new float[] { paramTaskView.getAlpha(), this.alpha })));
      }
    } while (!hasRectChangedFrom(paramTaskView));
    Rect localRect1 = new Rect(paramTaskView.getLeft(), paramTaskView.getTop(), paramTaskView.getRight(), paramTaskView.getBottom());
    Rect localRect2 = new Rect();
    this.rect.round(localRect2);
    paramArrayList.add(paramAnimationProps.apply(6, ObjectAnimator.ofPropertyValuesHolder(paramTaskView, new PropertyValuesHolder[] { PropertyValuesHolder.ofObject(LTRB, Utilities.RECT_EVALUATOR, new Rect[] { localRect1, localRect2 }) })));
  }
  
  public void copyFrom(TaskViewTransform paramTaskViewTransform)
  {
    this.translationZ = paramTaskViewTransform.translationZ;
    this.scale = paramTaskViewTransform.scale;
    this.alpha = paramTaskViewTransform.alpha;
    this.visible = paramTaskViewTransform.visible;
    this.dimAlpha = paramTaskViewTransform.dimAlpha;
    this.viewOutlineAlpha = paramTaskViewTransform.viewOutlineAlpha;
    this.rect.set(paramTaskViewTransform.rect);
  }
  
  public void fillIn(TaskView paramTaskView)
  {
    this.translationZ = paramTaskView.getTranslationZ();
    this.scale = paramTaskView.getScaleX();
    this.alpha = paramTaskView.getAlpha();
    this.visible = true;
    this.dimAlpha = paramTaskView.getDimAlpha();
    this.viewOutlineAlpha = paramTaskView.getViewBounds().getAlpha();
    this.rect.set(paramTaskView.getLeft(), paramTaskView.getTop(), paramTaskView.getRight(), paramTaskView.getBottom());
  }
  
  public boolean hasAlphaChangedFrom(float paramFloat)
  {
    boolean bool = false;
    if (Float.compare(this.alpha, paramFloat) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasRectChangedFrom(View paramView)
  {
    if (((int)this.rect.left != paramView.getLeft()) || ((int)this.rect.right != paramView.getRight())) {}
    while (((int)this.rect.top != paramView.getTop()) || ((int)this.rect.bottom != paramView.getBottom())) {
      return true;
    }
    return false;
  }
  
  public boolean hasScaleChangedFrom(float paramFloat)
  {
    boolean bool = false;
    if (Float.compare(this.scale, paramFloat) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasTranslationZChangedFrom(float paramFloat)
  {
    boolean bool = false;
    if (Float.compare(this.translationZ, paramFloat) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSame(TaskViewTransform paramTaskViewTransform)
  {
    if ((this.translationZ == paramTaskViewTransform.translationZ) && (this.scale == paramTaskViewTransform.scale) && (paramTaskViewTransform.alpha == this.alpha) && (this.dimAlpha == paramTaskViewTransform.dimAlpha) && (this.visible == paramTaskViewTransform.visible)) {
      return this.rect.equals(paramTaskViewTransform.rect);
    }
    return false;
  }
  
  public void reset()
  {
    this.translationZ = 0.0F;
    this.scale = 1.0F;
    this.alpha = 1.0F;
    this.dimAlpha = 0.0F;
    this.viewOutlineAlpha = 0.0F;
    this.visible = false;
    this.rect.setEmpty();
  }
  
  public String toString()
  {
    return "R: " + this.rect + " V: " + this.visible;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\TaskViewTransform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */