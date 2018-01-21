package com.android.systemui.statusbar.notification;

import android.util.ArraySet;
import android.util.Pools.SimplePool;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation;

public class TransformState
{
  private static Pools.SimplePool<TransformState> sInstancePool = new Pools.SimplePool(40);
  private int[] mOwnPosition = new int[2];
  private float mTransformationEndX = -1.0F;
  private float mTransformationEndY = -1.0F;
  protected View mTransformedView;
  
  public static TransformState createFrom(View paramView)
  {
    if ((paramView instanceof TextView))
    {
      localObject = TextViewTransformState.obtain();
      ((TextViewTransformState)localObject).initFrom(paramView);
      return (TransformState)localObject;
    }
    if (paramView.getId() == 16909228)
    {
      localObject = ActionListTransformState.obtain();
      ((ActionListTransformState)localObject).initFrom(paramView);
      return (TransformState)localObject;
    }
    if ((paramView instanceof NotificationHeaderView))
    {
      localObject = HeaderTransformState.obtain();
      ((HeaderTransformState)localObject).initFrom(paramView);
      return (TransformState)localObject;
    }
    if ((paramView instanceof ImageView))
    {
      localObject = ImageTransformState.obtain();
      ((ImageTransformState)localObject).initFrom(paramView);
      return (TransformState)localObject;
    }
    if ((paramView instanceof ProgressBar))
    {
      localObject = ProgressTransformState.obtain();
      ((ProgressTransformState)localObject).initFrom(paramView);
      return (TransformState)localObject;
    }
    Object localObject = obtain();
    ((TransformState)localObject).initFrom(paramView);
    return (TransformState)localObject;
  }
  
  public static TransformState obtain()
  {
    TransformState localTransformState = (TransformState)sInstancePool.acquire();
    if (localTransformState != null) {
      return localTransformState;
    }
    return new TransformState();
  }
  
  public static void setClippingDeactivated(View paramView, boolean paramBoolean)
  {
    if (!(paramView.getParent() instanceof ViewGroup)) {
      return;
    }
    Object localObject1 = (ViewGroup)paramView.getParent();
    Object localObject3 = (ArraySet)((ViewGroup)localObject1).getTag(2131951680);
    Object localObject2 = localObject3;
    if (localObject3 == null)
    {
      localObject2 = new ArraySet();
      ((ViewGroup)localObject1).setTag(2131951680, localObject2);
    }
    Object localObject4 = (Boolean)((ViewGroup)localObject1).getTag(2131951679);
    localObject3 = localObject4;
    if (localObject4 == null)
    {
      localObject3 = Boolean.valueOf(((ViewGroup)localObject1).getClipChildren());
      ((ViewGroup)localObject1).setTag(2131951679, localObject3);
    }
    Object localObject5 = (Boolean)((ViewGroup)localObject1).getTag(2131951681);
    localObject4 = localObject5;
    if (localObject5 == null)
    {
      localObject4 = Boolean.valueOf(((ViewGroup)localObject1).getClipToPadding());
      ((ViewGroup)localObject1).setTag(2131951681, localObject4);
    }
    if ((localObject1 instanceof ExpandableNotificationRow))
    {
      localObject5 = (ExpandableNotificationRow)localObject1;
      label140:
      if (paramBoolean) {
        break label232;
      }
      ((ArraySet)localObject2).remove(paramView);
      if (((ArraySet)localObject2).isEmpty())
      {
        ((ViewGroup)localObject1).setClipChildren(((Boolean)localObject3).booleanValue());
        ((ViewGroup)localObject1).setClipToPadding(((Boolean)localObject4).booleanValue());
        ((ViewGroup)localObject1).setTag(2131951680, null);
        if (localObject5 != null) {
          ((ExpandableNotificationRow)localObject5).setClipToActualHeight(true);
        }
      }
    }
    for (;;)
    {
      if ((localObject5 != null) && (!((ExpandableNotificationRow)localObject5).isChildInGroup())) {
        break label270;
      }
      localObject1 = ((ViewGroup)localObject1).getParent();
      if (!(localObject1 instanceof ViewGroup)) {
        return;
      }
      localObject1 = (ViewGroup)localObject1;
      break;
      localObject5 = null;
      break label140;
      label232:
      ((ArraySet)localObject2).add(paramView);
      ((ViewGroup)localObject1).setClipChildren(false);
      ((ViewGroup)localObject1).setClipToPadding(false);
      if ((localObject5 != null) && (((ExpandableNotificationRow)localObject5).isChildInGroup())) {
        ((ExpandableNotificationRow)localObject5).setClipToActualHeight(false);
      }
    }
    label270:
    return;
  }
  
  private void setTransformationStartScaleX(float paramFloat)
  {
    this.mTransformedView.setTag(2131951674, Float.valueOf(paramFloat));
  }
  
  private void setTransformationStartScaleY(float paramFloat)
  {
    this.mTransformedView.setTag(2131951675, Float.valueOf(paramFloat));
  }
  
  private void transformViewFrom(TransformState paramTransformState, int paramInt, ViewTransformationHelper.CustomTransformation paramCustomTransformation, float paramFloat)
  {
    View localView = this.mTransformedView;
    int i;
    label24:
    boolean bool;
    label52:
    int[] arrayOfInt1;
    label65:
    int[] arrayOfInt2;
    if ((paramInt & 0x1) != 0)
    {
      i = 1;
      if ((paramInt & 0x10) == 0) {
        break label245;
      }
      paramInt = 1;
      bool = transformScale();
      if ((paramFloat != 0.0F) && ((i == 0) || (getTransformationStartX() != -1.0F))) {
        break label250;
      }
      if (paramFloat == 0.0F) {
        break label297;
      }
      arrayOfInt1 = paramTransformState.getLaidOutLocationOnScreen();
      arrayOfInt2 = getLaidOutLocationOnScreen();
      if ((paramCustomTransformation == null) || (!paramCustomTransformation.initTransformation(this, paramTransformState))) {
        break label306;
      }
    }
    for (;;)
    {
      if (i == 0) {
        setTransformationStartX(-1.0F);
      }
      if (paramInt == 0) {
        setTransformationStartY(-1.0F);
      }
      if (!bool)
      {
        setTransformationStartScaleX(-1.0F);
        setTransformationStartScaleY(-1.0F);
      }
      setClippingDeactivated(localView, true);
      label245:
      label250:
      do
      {
        paramFloat = Interpolators.FAST_OUT_SLOW_IN.getInterpolation(paramFloat);
        if (i != 0) {
          localView.setTranslationX(NotificationUtils.interpolate(getTransformationStartX(), 0.0F, paramFloat));
        }
        if (paramInt != 0) {
          localView.setTranslationY(NotificationUtils.interpolate(getTransformationStartY(), 0.0F, paramFloat));
        }
        if (bool)
        {
          float f = getTransformationStartScaleX();
          if (f != -1.0F) {
            localView.setScaleX(NotificationUtils.interpolate(f, 1.0F, paramFloat));
          }
          f = getTransformationStartScaleY();
          if (f != -1.0F) {
            localView.setScaleY(NotificationUtils.interpolate(f, 1.0F, paramFloat));
          }
        }
        return;
        i = 0;
        break;
        paramInt = 0;
        break label24;
        if (((paramInt != 0) && (getTransformationStartY() == -1.0F)) || ((bool) && (getTransformationStartScaleX() == -1.0F))) {
          break label52;
        }
      } while ((!bool) || (getTransformationStartScaleY() != -1.0F));
      break label52;
      label297:
      arrayOfInt1 = paramTransformState.getLocationOnScreen();
      break label65;
      label306:
      if (i != 0) {
        setTransformationStartX(arrayOfInt1[0] - arrayOfInt2[0]);
      }
      if (paramInt != 0) {
        setTransformationStartY(arrayOfInt1[1] - arrayOfInt2[1]);
      }
      paramTransformState = paramTransformState.getTransformedView();
      if ((bool) && (paramTransformState.getWidth() != localView.getWidth()))
      {
        setTransformationStartScaleX(paramTransformState.getWidth() * paramTransformState.getScaleX() / localView.getWidth());
        localView.setPivotX(0.0F);
      }
      for (;;)
      {
        if ((!bool) || (paramTransformState.getHeight() == localView.getHeight())) {
          break label448;
        }
        setTransformationStartScaleY(paramTransformState.getHeight() * paramTransformState.getScaleY() / localView.getHeight());
        localView.setPivotY(0.0F);
        break;
        setTransformationStartScaleX(-1.0F);
      }
      label448:
      setTransformationStartScaleY(-1.0F);
    }
  }
  
  private void transformViewTo(TransformState paramTransformState, int paramInt, ViewTransformationHelper.CustomTransformation paramCustomTransformation, float paramFloat)
  {
    View localView = this.mTransformedView;
    int i;
    label24:
    boolean bool;
    float f1;
    label56:
    label80:
    Object localObject;
    if ((paramInt & 0x1) != 0)
    {
      i = 1;
      if ((paramInt & 0x10) == 0) {
        break label400;
      }
      paramInt = 1;
      bool = transformScale();
      if (paramFloat == 0.0F)
      {
        if (i != 0)
        {
          f1 = getTransformationStartX();
          if (f1 == -1.0F) {
            break label405;
          }
          setTransformationStartX(f1);
        }
        if (paramInt != 0)
        {
          f1 = getTransformationStartY();
          if (f1 == -1.0F) {
            break label415;
          }
          setTransformationStartY(f1);
        }
        localObject = paramTransformState.getTransformedView();
        if ((!bool) || (((View)localObject).getWidth() == localView.getWidth())) {
          break label425;
        }
        setTransformationStartScaleX(localView.getScaleX());
        localView.setPivotX(0.0F);
        label125:
        if ((!bool) || (((View)localObject).getHeight() == localView.getHeight())) {
          break label434;
        }
        setTransformationStartScaleY(localView.getScaleY());
        localView.setPivotY(0.0F);
      }
    }
    for (;;)
    {
      setClippingDeactivated(localView, true);
      float f2 = Interpolators.FAST_OUT_SLOW_IN.getInterpolation(paramFloat);
      localObject = paramTransformState.getLaidOutLocationOnScreen();
      int[] arrayOfInt = getLaidOutLocationOnScreen();
      if (i != 0)
      {
        f1 = localObject[0] - arrayOfInt[0];
        paramFloat = f1;
        if (paramCustomTransformation != null)
        {
          paramFloat = f1;
          if (paramCustomTransformation.customTransformTarget(this, paramTransformState)) {
            paramFloat = this.mTransformationEndX;
          }
        }
        localView.setTranslationX(NotificationUtils.interpolate(getTransformationStartX(), paramFloat, f2));
      }
      if (paramInt != 0)
      {
        f1 = localObject[1] - arrayOfInt[1];
        paramFloat = f1;
        if (paramCustomTransformation != null)
        {
          paramFloat = f1;
          if (paramCustomTransformation.customTransformTarget(this, paramTransformState)) {
            paramFloat = this.mTransformationEndY;
          }
        }
        localView.setTranslationY(NotificationUtils.interpolate(getTransformationStartY(), paramFloat, f2));
      }
      if (bool)
      {
        paramTransformState = paramTransformState.getTransformedView();
        paramFloat = getTransformationStartScaleX();
        if (paramFloat != -1.0F) {
          localView.setScaleX(NotificationUtils.interpolate(paramFloat, paramTransformState.getWidth() / localView.getWidth(), f2));
        }
        paramFloat = getTransformationStartScaleY();
        if (paramFloat != -1.0F) {
          localView.setScaleY(NotificationUtils.interpolate(paramFloat, paramTransformState.getHeight() / localView.getHeight(), f2));
        }
      }
      return;
      i = 0;
      break;
      label400:
      paramInt = 0;
      break label24;
      label405:
      f1 = localView.getTranslationX();
      break label56;
      label415:
      f1 = localView.getTranslationY();
      break label80;
      label425:
      setTransformationStartScaleX(-1.0F);
      break label125;
      label434:
      setTransformationStartScaleY(-1.0F);
    }
  }
  
  public void abortTransformation()
  {
    this.mTransformedView.setTag(2131951672, Float.valueOf(-1.0F));
    this.mTransformedView.setTag(2131951673, Float.valueOf(-1.0F));
    this.mTransformedView.setTag(2131951674, Float.valueOf(-1.0F));
    this.mTransformedView.setTag(2131951675, Float.valueOf(-1.0F));
  }
  
  public void appear(float paramFloat, TransformableView paramTransformableView)
  {
    if (paramFloat == 0.0F) {
      prepareFadeIn();
    }
    CrossFadeHelper.fadeIn(this.mTransformedView, paramFloat);
  }
  
  public void disappear(float paramFloat, TransformableView paramTransformableView)
  {
    CrossFadeHelper.fadeOut(this.mTransformedView, paramFloat);
  }
  
  public int[] getLaidOutLocationOnScreen()
  {
    int[] arrayOfInt = getLocationOnScreen();
    arrayOfInt[0] = ((int)(arrayOfInt[0] - this.mTransformedView.getTranslationX()));
    arrayOfInt[1] = ((int)(arrayOfInt[1] - this.mTransformedView.getTranslationY()));
    return arrayOfInt;
  }
  
  public int[] getLocationOnScreen()
  {
    this.mTransformedView.getLocationOnScreen(this.mOwnPosition);
    int[] arrayOfInt = this.mOwnPosition;
    arrayOfInt[0] = ((int)(arrayOfInt[0] - (1.0F - this.mTransformedView.getScaleX()) * this.mTransformedView.getPivotX()));
    arrayOfInt = this.mOwnPosition;
    arrayOfInt[1] = ((int)(arrayOfInt[1] - (1.0F - this.mTransformedView.getScaleY()) * this.mTransformedView.getPivotY()));
    return this.mOwnPosition;
  }
  
  public float getTransformationStartScaleX()
  {
    Object localObject = this.mTransformedView.getTag(2131951674);
    if (localObject == null) {
      return -1.0F;
    }
    return ((Float)localObject).floatValue();
  }
  
  public float getTransformationStartScaleY()
  {
    Object localObject = this.mTransformedView.getTag(2131951675);
    if (localObject == null) {
      return -1.0F;
    }
    return ((Float)localObject).floatValue();
  }
  
  public float getTransformationStartX()
  {
    Object localObject = this.mTransformedView.getTag(2131951672);
    if (localObject == null) {
      return -1.0F;
    }
    return ((Float)localObject).floatValue();
  }
  
  public float getTransformationStartY()
  {
    Object localObject = this.mTransformedView.getTag(2131951673);
    if (localObject == null) {
      return -1.0F;
    }
    return ((Float)localObject).floatValue();
  }
  
  public View getTransformedView()
  {
    return this.mTransformedView;
  }
  
  public void initFrom(View paramView)
  {
    this.mTransformedView = paramView;
  }
  
  public void prepareFadeIn()
  {
    resetTransformedView();
  }
  
  public void recycle()
  {
    reset();
    if (getClass() == TransformState.class) {
      sInstancePool.release(this);
    }
  }
  
  protected void reset()
  {
    this.mTransformedView = null;
    this.mTransformationEndX = -1.0F;
    this.mTransformationEndY = -1.0F;
  }
  
  protected void resetTransformedView()
  {
    this.mTransformedView.setTranslationX(0.0F);
    this.mTransformedView.setTranslationY(0.0F);
    this.mTransformedView.setScaleX(1.0F);
    this.mTransformedView.setScaleY(1.0F);
    setClippingDeactivated(this.mTransformedView, false);
    abortTransformation();
  }
  
  protected boolean sameAs(TransformState paramTransformState)
  {
    return false;
  }
  
  public void setTransformationEndY(float paramFloat)
  {
    this.mTransformationEndY = paramFloat;
  }
  
  public void setTransformationStartX(float paramFloat)
  {
    this.mTransformedView.setTag(2131951672, Float.valueOf(paramFloat));
  }
  
  public void setTransformationStartY(float paramFloat)
  {
    this.mTransformedView.setTag(2131951673, Float.valueOf(paramFloat));
  }
  
  public void setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean2) && (this.mTransformedView.getVisibility() == 8)) {
      return;
    }
    View localView;
    int i;
    if (this.mTransformedView.getVisibility() != 8)
    {
      localView = this.mTransformedView;
      if (paramBoolean1)
      {
        i = 0;
        localView.setVisibility(i);
      }
    }
    else
    {
      this.mTransformedView.animate().cancel();
      localView = this.mTransformedView;
      if (!paramBoolean1) {
        break label88;
      }
    }
    label88:
    for (float f = 1.0F;; f = 0.0F)
    {
      localView.setAlpha(f);
      resetTransformedView();
      return;
      i = 4;
      break;
    }
  }
  
  protected boolean transformScale()
  {
    return false;
  }
  
  public void transformViewFrom(TransformState paramTransformState, float paramFloat)
  {
    this.mTransformedView.animate().cancel();
    if (sameAs(paramTransformState)) {
      if (this.mTransformedView.getVisibility() == 4)
      {
        this.mTransformedView.setAlpha(1.0F);
        this.mTransformedView.setVisibility(0);
      }
    }
    for (;;)
    {
      transformViewFullyFrom(paramTransformState, paramFloat);
      return;
      CrossFadeHelper.fadeIn(this.mTransformedView, paramFloat);
    }
  }
  
  public void transformViewFullyFrom(TransformState paramTransformState, float paramFloat)
  {
    transformViewFrom(paramTransformState, 17, null, paramFloat);
  }
  
  public void transformViewFullyTo(TransformState paramTransformState, float paramFloat)
  {
    transformViewTo(paramTransformState, 17, null, paramFloat);
  }
  
  public boolean transformViewTo(TransformState paramTransformState, float paramFloat)
  {
    this.mTransformedView.animate().cancel();
    if (sameAs(paramTransformState))
    {
      if (this.mTransformedView.getVisibility() == 0)
      {
        this.mTransformedView.setAlpha(0.0F);
        this.mTransformedView.setVisibility(4);
      }
      return false;
    }
    CrossFadeHelper.fadeOut(this.mTransformedView, paramFloat);
    transformViewFullyTo(paramTransformState, paramFloat);
    return true;
  }
  
  public void transformViewVerticalFrom(TransformState paramTransformState, float paramFloat)
  {
    transformViewFrom(paramTransformState, 16, null, paramFloat);
  }
  
  public void transformViewVerticalFrom(TransformState paramTransformState, ViewTransformationHelper.CustomTransformation paramCustomTransformation, float paramFloat)
  {
    transformViewFrom(paramTransformState, 16, paramCustomTransformation, paramFloat);
  }
  
  public void transformViewVerticalTo(TransformState paramTransformState, float paramFloat)
  {
    transformViewTo(paramTransformState, 16, null, paramFloat);
  }
  
  public void transformViewVerticalTo(TransformState paramTransformState, ViewTransformationHelper.CustomTransformation paramCustomTransformation, float paramFloat)
  {
    transformViewTo(paramTransformState, 16, paramCustomTransformation, paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\TransformState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */