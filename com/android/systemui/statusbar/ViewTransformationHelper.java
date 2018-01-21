package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.TransformState;
import java.util.Iterator;
import java.util.Stack;

public class ViewTransformationHelper
  implements TransformableView
{
  private ArrayMap<Integer, CustomTransformation> mCustomTransformations = new ArrayMap();
  private ArrayMap<Integer, View> mTransformedViews = new ArrayMap();
  private ValueAnimator mViewTransformationAnimation;
  
  private void abortTransformations()
  {
    Iterator localIterator = this.mTransformedViews.keySet().iterator();
    while (localIterator.hasNext())
    {
      TransformState localTransformState = getCurrentState(((Integer)localIterator.next()).intValue());
      if (localTransformState != null)
      {
        localTransformState.abortTransformation();
        localTransformState.recycle();
      }
    }
  }
  
  public void addRemainingTransformTypes(View paramView)
  {
    int j = this.mTransformedViews.size();
    int i = 0;
    while (i < j)
    {
      for (localObject = (View)this.mTransformedViews.valueAt(i); localObject != paramView.getParent(); localObject = (View)((View)localObject).getParent()) {
        ((View)localObject).setTag(2131951683, Boolean.valueOf(true));
      }
      i += 1;
    }
    Object localObject = new Stack();
    ((Stack)localObject).push(paramView);
    while (!((Stack)localObject).isEmpty())
    {
      paramView = (View)((Stack)localObject).pop();
      if (paramView.getVisibility() != 8)
      {
        if ((Boolean)paramView.getTag(2131951683) == null)
        {
          i = paramView.getId();
          if (i != -1)
          {
            addTransformedView(i, paramView);
            continue;
          }
        }
        paramView.setTag(2131951683, null);
        if (((paramView instanceof ViewGroup)) && (!this.mTransformedViews.containsValue(paramView)))
        {
          paramView = (ViewGroup)paramView;
          i = 0;
          while (i < paramView.getChildCount())
          {
            ((Stack)localObject).push(paramView.getChildAt(i));
            i += 1;
          }
        }
      }
    }
  }
  
  public void addTransformedView(int paramInt, View paramView)
  {
    this.mTransformedViews.put(Integer.valueOf(paramInt), paramView);
  }
  
  public ArraySet<View> getAllTransformingViews()
  {
    return new ArraySet(this.mTransformedViews.values());
  }
  
  public TransformState getCurrentState(int paramInt)
  {
    View localView = (View)this.mTransformedViews.get(Integer.valueOf(paramInt));
    if ((localView != null) && (localView.getVisibility() != 8)) {
      return TransformState.createFrom(localView);
    }
    return null;
  }
  
  public void reset()
  {
    this.mTransformedViews.clear();
  }
  
  public void resetTransformedView(View paramView)
  {
    paramView = TransformState.createFrom(paramView);
    paramView.setVisible(true, true);
    paramView.recycle();
  }
  
  public void setCustomTransformation(CustomTransformation paramCustomTransformation, int paramInt)
  {
    this.mCustomTransformations.put(Integer.valueOf(paramInt), paramCustomTransformation);
  }
  
  public void setVisible(boolean paramBoolean)
  {
    if (this.mViewTransformationAnimation != null) {
      this.mViewTransformationAnimation.cancel();
    }
    Iterator localIterator = this.mTransformedViews.keySet().iterator();
    while (localIterator.hasNext())
    {
      TransformState localTransformState = getCurrentState(((Integer)localIterator.next()).intValue());
      if (localTransformState != null)
      {
        localTransformState.setVisible(paramBoolean, false);
        localTransformState.recycle();
      }
    }
  }
  
  public void transformFrom(final TransformableView paramTransformableView)
  {
    if (this.mViewTransformationAnimation != null) {
      this.mViewTransformationAnimation.cancel();
    }
    this.mViewTransformationAnimation = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    this.mViewTransformationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        ViewTransformationHelper.this.transformFrom(paramTransformableView, paramAnonymousValueAnimator.getAnimatedFraction());
      }
    });
    this.mViewTransformationAnimation.addListener(new AnimatorListenerAdapter()
    {
      public boolean mCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (!this.mCancelled)
        {
          ViewTransformationHelper.this.setVisible(true);
          return;
        }
        ViewTransformationHelper.-wrap0(ViewTransformationHelper.this);
      }
    });
    this.mViewTransformationAnimation.setInterpolator(Interpolators.LINEAR);
    this.mViewTransformationAnimation.setDuration(360L);
    this.mViewTransformationAnimation.start();
  }
  
  public void transformFrom(TransformableView paramTransformableView, float paramFloat)
  {
    Iterator localIterator = this.mTransformedViews.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (Integer)localIterator.next();
      TransformState localTransformState = getCurrentState(((Integer)localObject).intValue());
      if (localTransformState != null)
      {
        CustomTransformation localCustomTransformation = (CustomTransformation)this.mCustomTransformations.get(localObject);
        if ((localCustomTransformation != null) && (localCustomTransformation.transformFrom(localTransformState, paramTransformableView, paramFloat)))
        {
          localTransformState.recycle();
        }
        else
        {
          localObject = paramTransformableView.getCurrentState(((Integer)localObject).intValue());
          if (localObject != null)
          {
            localTransformState.transformViewFrom((TransformState)localObject, paramFloat);
            ((TransformState)localObject).recycle();
          }
          for (;;)
          {
            localTransformState.recycle();
            break;
            localTransformState.appear(paramFloat, paramTransformableView);
          }
        }
      }
    }
  }
  
  public void transformTo(TransformableView paramTransformableView, float paramFloat)
  {
    Iterator localIterator = this.mTransformedViews.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (Integer)localIterator.next();
      TransformState localTransformState = getCurrentState(((Integer)localObject).intValue());
      if (localTransformState != null)
      {
        CustomTransformation localCustomTransformation = (CustomTransformation)this.mCustomTransformations.get(localObject);
        if ((localCustomTransformation != null) && (localCustomTransformation.transformTo(localTransformState, paramTransformableView, paramFloat)))
        {
          localTransformState.recycle();
        }
        else
        {
          localObject = paramTransformableView.getCurrentState(((Integer)localObject).intValue());
          if (localObject != null)
          {
            localTransformState.transformViewTo((TransformState)localObject, paramFloat);
            ((TransformState)localObject).recycle();
          }
          for (;;)
          {
            localTransformState.recycle();
            break;
            localTransformState.disappear(paramFloat, paramTransformableView);
          }
        }
      }
    }
  }
  
  public void transformTo(final TransformableView paramTransformableView, final Runnable paramRunnable)
  {
    if (this.mViewTransformationAnimation != null) {
      this.mViewTransformationAnimation.cancel();
    }
    this.mViewTransformationAnimation = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    this.mViewTransformationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        ViewTransformationHelper.this.transformTo(paramTransformableView, paramAnonymousValueAnimator.getAnimatedFraction());
      }
    });
    this.mViewTransformationAnimation.setInterpolator(Interpolators.LINEAR);
    this.mViewTransformationAnimation.setDuration(360L);
    this.mViewTransformationAnimation.addListener(new AnimatorListenerAdapter()
    {
      public boolean mCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (!this.mCancelled)
        {
          if (paramRunnable != null) {
            paramRunnable.run();
          }
          ViewTransformationHelper.this.setVisible(false);
          return;
        }
        ViewTransformationHelper.-wrap0(ViewTransformationHelper.this);
      }
    });
    this.mViewTransformationAnimation.start();
  }
  
  public static abstract class CustomTransformation
  {
    public boolean customTransformTarget(TransformState paramTransformState1, TransformState paramTransformState2)
    {
      return false;
    }
    
    public boolean initTransformation(TransformState paramTransformState1, TransformState paramTransformState2)
    {
      return false;
    }
    
    public abstract boolean transformFrom(TransformState paramTransformState, TransformableView paramTransformableView, float paramFloat);
    
    public abstract boolean transformTo(TransformState paramTransformState, TransformableView paramTransformableView, float paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\ViewTransformationHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */