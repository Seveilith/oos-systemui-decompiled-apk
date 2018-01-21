package com.android.systemui.recents.misc;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.RectEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.ArraySet;
import android.util.IntProperty;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.views.TaskViewTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utilities
{
  public static final Property<Drawable, Integer> DRAWABLE_ALPHA = new IntProperty("drawableAlpha")
  {
    public Integer get(Drawable paramAnonymousDrawable)
    {
      return Integer.valueOf(paramAnonymousDrawable.getAlpha());
    }
    
    public void setValue(Drawable paramAnonymousDrawable, int paramAnonymousInt)
    {
      paramAnonymousDrawable.setAlpha(paramAnonymousInt);
    }
  };
  public static final Property<Drawable, Rect> DRAWABLE_RECT = new Property(Rect.class, "drawableBounds")
  {
    public Rect get(Drawable paramAnonymousDrawable)
    {
      return paramAnonymousDrawable.getBounds();
    }
    
    public void set(Drawable paramAnonymousDrawable, Rect paramAnonymousRect)
    {
      paramAnonymousDrawable.setBounds(paramAnonymousRect);
    }
  };
  public static final Rect EMPTY_RECT = new Rect();
  public static final RectFEvaluator RECTF_EVALUATOR = new RectFEvaluator();
  public static final RectEvaluator RECT_EVALUATOR = new RectEvaluator(new Rect());
  
  public static <T> ArraySet<T> arrayToSet(T[] paramArrayOfT, ArraySet<T> paramArraySet)
  {
    paramArraySet.clear();
    if (paramArrayOfT != null) {
      Collections.addAll(paramArraySet, paramArrayOfT);
    }
    return paramArraySet;
  }
  
  public static void cancelAnimationWithoutCallbacks(Animator paramAnimator)
  {
    if ((paramAnimator != null) && (paramAnimator.isStarted()))
    {
      removeAnimationListenersRecursive(paramAnimator);
      paramAnimator.cancel();
    }
  }
  
  public static float clamp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return Math.max(paramFloat2, Math.min(paramFloat3, paramFloat1));
  }
  
  public static int clamp(int paramInt1, int paramInt2, int paramInt3)
  {
    return Math.max(paramInt2, Math.min(paramInt3, paramInt1));
  }
  
  public static float clamp01(float paramFloat)
  {
    return Math.max(0.0F, Math.min(1.0F, paramFloat));
  }
  
  public static float computeContrastBetweenColors(int paramInt1, int paramInt2)
  {
    float f1 = Color.red(paramInt1) / 255.0F;
    float f2 = Color.green(paramInt1) / 255.0F;
    float f3 = Color.blue(paramInt1) / 255.0F;
    label52:
    label67:
    float f4;
    float f5;
    float f6;
    if (f1 < 0.03928F)
    {
      f1 /= 12.92F;
      if (f2 >= 0.03928F) {
        break label204;
      }
      f2 /= 12.92F;
      if (f3 >= 0.03928F) {
        break label223;
      }
      f3 /= 12.92F;
      f4 = Color.red(paramInt2) / 255.0F;
      f5 = Color.green(paramInt2) / 255.0F;
      f6 = Color.blue(paramInt2) / 255.0F;
      if (f4 >= 0.03928F) {
        break label244;
      }
      f4 /= 12.92F;
      label112:
      if (f5 >= 0.03928F) {
        break label265;
      }
      f5 /= 12.92F;
      label127:
      if (f6 >= 0.03928F) {
        break label286;
      }
    }
    label204:
    label223:
    label244:
    label265:
    label286:
    for (f6 /= 12.92F;; f6 = (float)Math.pow((0.055F + f6) / 1.055F, 2.4000000953674316D))
    {
      return Math.abs((0.05F + (0.2126F * f4 + 0.7152F * f5 + 0.0722F * f6)) / (0.05F + (0.2126F * f1 + 0.7152F * f2 + 0.0722F * f3)));
      f1 = (float)Math.pow((0.055F + f1) / 1.055F, 2.4000000953674316D);
      break;
      f2 = (float)Math.pow((0.055F + f2) / 1.055F, 2.4000000953674316D);
      break label52;
      f3 = (float)Math.pow((0.055F + f3) / 1.055F, 2.4000000953674316D);
      break label67;
      f4 = (float)Math.pow((0.055F + f4) / 1.055F, 2.4000000953674316D);
      break label112;
      f5 = (float)Math.pow((0.055F + f5) / 1.055F, 2.4000000953674316D);
      break label127;
    }
  }
  
  public static String dumpRect(Rect paramRect)
  {
    if (paramRect == null) {
      return "N:0,0-0,0";
    }
    return paramRect.left + "," + paramRect.top + "-" + paramRect.right + "," + paramRect.bottom;
  }
  
  public static <T extends View> T findParent(View paramView, Class<T> paramClass)
  {
    for (paramView = paramView.getParent(); paramView != null; paramView = paramView.getParent()) {
      if (paramView.getClass().equals(paramClass)) {
        return (View)paramView;
      }
    }
    return null;
  }
  
  public static ViewStub findViewStubById(Activity paramActivity, int paramInt)
  {
    return (ViewStub)paramActivity.findViewById(paramInt);
  }
  
  public static ViewStub findViewStubById(View paramView, int paramInt)
  {
    return (ViewStub)paramView.findViewById(paramInt);
  }
  
  public static Configuration getAppConfiguration(Context paramContext)
  {
    return paramContext.getApplicationContext().getResources().getConfiguration();
  }
  
  public static int getColorWithOverlay(int paramInt1, int paramInt2, float paramFloat)
  {
    return Color.rgb((int)(Color.red(paramInt1) * paramFloat + (1.0F - paramFloat) * Color.red(paramInt2)), (int)(Color.green(paramInt1) * paramFloat + (1.0F - paramFloat) * Color.green(paramInt2)), (int)(Color.blue(paramInt1) * paramFloat + (1.0F - paramFloat) * Color.blue(paramInt2)));
  }
  
  public static boolean isDescendentAccessibilityFocused(View paramView)
  {
    if (paramView.isAccessibilityFocused()) {
      return true;
    }
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      int j = paramView.getChildCount();
      int i = 0;
      while (i < j)
      {
        if (isDescendentAccessibilityFocused(paramView.getChildAt(i))) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public static float mapRange(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (paramFloat3 - paramFloat2) * paramFloat1 + paramFloat2;
  }
  
  public static void matchTaskListSize(List<Task> paramList, List<TaskViewTransform> paramList1)
  {
    int i = paramList1.size();
    int j = paramList.size();
    if (i < j) {
      while (i < j)
      {
        paramList1.add(new TaskViewTransform());
        i += 1;
      }
    }
    if (i > j) {
      paramList1.subList(j, i).clear();
    }
  }
  
  public static void removeAnimationListenersRecursive(Animator paramAnimator)
  {
    if ((paramAnimator instanceof AnimatorSet))
    {
      ArrayList localArrayList = ((AnimatorSet)paramAnimator).getChildAnimations();
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        removeAnimationListenersRecursive((Animator)localArrayList.get(i));
        i -= 1;
      }
    }
    paramAnimator.removeAllListeners();
  }
  
  public static void scaleRectAboutCenter(RectF paramRectF, float paramFloat)
  {
    if (paramFloat != 1.0F)
    {
      float f1 = paramRectF.centerX();
      float f2 = paramRectF.centerY();
      paramRectF.offset(-f1, -f2);
      paramRectF.left *= paramFloat;
      paramRectF.top *= paramFloat;
      paramRectF.right *= paramFloat;
      paramRectF.bottom *= paramFloat;
      paramRectF.offset(f1, f2);
    }
  }
  
  public static void setViewFrameFromTranslation(View paramView)
  {
    RectF localRectF = new RectF(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    localRectF.offset(paramView.getTranslationX(), paramView.getTranslationY());
    paramView.setTranslationX(0.0F);
    paramView.setTranslationY(0.0F);
    paramView.setLeftTopRightBottom((int)localRectF.left, (int)localRectF.top, (int)localRectF.right, (int)localRectF.bottom);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\misc\Utilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */