package android.support.v4.view;

import android.content.res.ColorStateList;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.BuildCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class ViewCompat
{
  static final ViewCompatImpl IMPL = new BaseViewCompatImpl();
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (BuildCompat.isAtLeastN())
    {
      IMPL = new Api24ViewCompatImpl();
      return;
    }
    if (i >= 23)
    {
      IMPL = new MarshmallowViewCompatImpl();
      return;
    }
    if (i >= 21)
    {
      IMPL = new LollipopViewCompatImpl();
      return;
    }
    if (i >= 19)
    {
      IMPL = new KitKatViewCompatImpl();
      return;
    }
    if (i >= 18)
    {
      IMPL = new JbMr2ViewCompatImpl();
      return;
    }
    if (i >= 17)
    {
      IMPL = new JbMr1ViewCompatImpl();
      return;
    }
    if (i >= 16)
    {
      IMPL = new JBViewCompatImpl();
      return;
    }
    if (i >= 15)
    {
      IMPL = new ICSMr1ViewCompatImpl();
      return;
    }
    if (i >= 14)
    {
      IMPL = new ICSViewCompatImpl();
      return;
    }
    if (i >= 11)
    {
      IMPL = new HCViewCompatImpl();
      return;
    }
  }
  
  public static ViewPropertyAnimatorCompat animate(View paramView)
  {
    return IMPL.animate(paramView);
  }
  
  public static boolean canScrollHorizontally(View paramView, int paramInt)
  {
    return IMPL.canScrollHorizontally(paramView, paramInt);
  }
  
  public static boolean canScrollVertically(View paramView, int paramInt)
  {
    return IMPL.canScrollVertically(paramView, paramInt);
  }
  
  public static WindowInsetsCompat dispatchApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
  {
    return IMPL.dispatchApplyWindowInsets(paramView, paramWindowInsetsCompat);
  }
  
  public static float getAlpha(View paramView)
  {
    return IMPL.getAlpha(paramView);
  }
  
  public static ColorStateList getBackgroundTintList(View paramView)
  {
    return IMPL.getBackgroundTintList(paramView);
  }
  
  public static PorterDuff.Mode getBackgroundTintMode(View paramView)
  {
    return IMPL.getBackgroundTintMode(paramView);
  }
  
  public static Display getDisplay(@NonNull View paramView)
  {
    return IMPL.getDisplay(paramView);
  }
  
  public static float getElevation(View paramView)
  {
    return IMPL.getElevation(paramView);
  }
  
  public static boolean getFitsSystemWindows(View paramView)
  {
    return IMPL.getFitsSystemWindows(paramView);
  }
  
  public static int getImportantForAccessibility(View paramView)
  {
    return IMPL.getImportantForAccessibility(paramView);
  }
  
  public static int getLayerType(View paramView)
  {
    return IMPL.getLayerType(paramView);
  }
  
  public static int getLayoutDirection(View paramView)
  {
    return IMPL.getLayoutDirection(paramView);
  }
  
  @Nullable
  public static Matrix getMatrix(View paramView)
  {
    return IMPL.getMatrix(paramView);
  }
  
  public static int getMeasuredState(View paramView)
  {
    return IMPL.getMeasuredState(paramView);
  }
  
  public static int getMeasuredWidthAndState(View paramView)
  {
    return IMPL.getMeasuredWidthAndState(paramView);
  }
  
  public static int getMinimumHeight(View paramView)
  {
    return IMPL.getMinimumHeight(paramView);
  }
  
  public static int getMinimumWidth(View paramView)
  {
    return IMPL.getMinimumWidth(paramView);
  }
  
  public static ViewParent getParentForAccessibility(View paramView)
  {
    return IMPL.getParentForAccessibility(paramView);
  }
  
  public static float getScaleX(View paramView)
  {
    return IMPL.getScaleX(paramView);
  }
  
  public static float getTranslationX(View paramView)
  {
    return IMPL.getTranslationX(paramView);
  }
  
  public static float getTranslationY(View paramView)
  {
    return IMPL.getTranslationY(paramView);
  }
  
  public static int getWindowSystemUiVisibility(View paramView)
  {
    return IMPL.getWindowSystemUiVisibility(paramView);
  }
  
  public static float getY(View paramView)
  {
    return IMPL.getY(paramView);
  }
  
  public static boolean hasAccessibilityDelegate(View paramView)
  {
    return IMPL.hasAccessibilityDelegate(paramView);
  }
  
  public static boolean hasOverlappingRendering(View paramView)
  {
    return IMPL.hasOverlappingRendering(paramView);
  }
  
  public static boolean hasTransientState(View paramView)
  {
    return IMPL.hasTransientState(paramView);
  }
  
  public static boolean isAttachedToWindow(View paramView)
  {
    return IMPL.isAttachedToWindow(paramView);
  }
  
  public static boolean isLaidOut(View paramView)
  {
    return IMPL.isLaidOut(paramView);
  }
  
  public static boolean isNestedScrollingEnabled(View paramView)
  {
    return IMPL.isNestedScrollingEnabled(paramView);
  }
  
  public static void jumpDrawablesToCurrentState(View paramView)
  {
    IMPL.jumpDrawablesToCurrentState(paramView);
  }
  
  public static void offsetLeftAndRight(View paramView, int paramInt)
  {
    IMPL.offsetLeftAndRight(paramView, paramInt);
  }
  
  public static void offsetTopAndBottom(View paramView, int paramInt)
  {
    IMPL.offsetTopAndBottom(paramView, paramInt);
  }
  
  public static WindowInsetsCompat onApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
  {
    return IMPL.onApplyWindowInsets(paramView, paramWindowInsetsCompat);
  }
  
  public static void postInvalidateOnAnimation(View paramView)
  {
    IMPL.postInvalidateOnAnimation(paramView);
  }
  
  public static void postInvalidateOnAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    IMPL.postInvalidateOnAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static void postOnAnimation(View paramView, Runnable paramRunnable)
  {
    IMPL.postOnAnimation(paramView, paramRunnable);
  }
  
  public static void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong)
  {
    IMPL.postOnAnimationDelayed(paramView, paramRunnable, paramLong);
  }
  
  public static void requestApplyInsets(View paramView)
  {
    IMPL.requestApplyInsets(paramView);
  }
  
  public static int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3)
  {
    return IMPL.resolveSizeAndState(paramInt1, paramInt2, paramInt3);
  }
  
  public static void setAccessibilityDelegate(View paramView, AccessibilityDelegateCompat paramAccessibilityDelegateCompat)
  {
    IMPL.setAccessibilityDelegate(paramView, paramAccessibilityDelegateCompat);
  }
  
  public static void setActivated(View paramView, boolean paramBoolean)
  {
    IMPL.setActivated(paramView, paramBoolean);
  }
  
  public static void setAlpha(View paramView, @FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    IMPL.setAlpha(paramView, paramFloat);
  }
  
  public static void setBackground(View paramView, Drawable paramDrawable)
  {
    IMPL.setBackground(paramView, paramDrawable);
  }
  
  public static void setBackgroundTintList(View paramView, ColorStateList paramColorStateList)
  {
    IMPL.setBackgroundTintList(paramView, paramColorStateList);
  }
  
  public static void setBackgroundTintMode(View paramView, PorterDuff.Mode paramMode)
  {
    IMPL.setBackgroundTintMode(paramView, paramMode);
  }
  
  public static void setChildrenDrawingOrderEnabled(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    IMPL.setChildrenDrawingOrderEnabled(paramViewGroup, paramBoolean);
  }
  
  public static void setElevation(View paramView, float paramFloat)
  {
    IMPL.setElevation(paramView, paramFloat);
  }
  
  public static void setImportantForAccessibility(View paramView, int paramInt)
  {
    IMPL.setImportantForAccessibility(paramView, paramInt);
  }
  
  public static void setLayerPaint(View paramView, Paint paramPaint)
  {
    IMPL.setLayerPaint(paramView, paramPaint);
  }
  
  public static void setLayerType(View paramView, int paramInt, Paint paramPaint)
  {
    IMPL.setLayerType(paramView, paramInt, paramPaint);
  }
  
  public static void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener)
  {
    IMPL.setOnApplyWindowInsetsListener(paramView, paramOnApplyWindowInsetsListener);
  }
  
  public static void setSaveFromParentEnabled(View paramView, boolean paramBoolean)
  {
    IMPL.setSaveFromParentEnabled(paramView, paramBoolean);
  }
  
  public static void setScaleX(View paramView, float paramFloat)
  {
    IMPL.setScaleX(paramView, paramFloat);
  }
  
  public static void setScaleY(View paramView, float paramFloat)
  {
    IMPL.setScaleY(paramView, paramFloat);
  }
  
  public static void setTranslationX(View paramView, float paramFloat)
  {
    IMPL.setTranslationX(paramView, paramFloat);
  }
  
  public static void setTranslationY(View paramView, float paramFloat)
  {
    IMPL.setTranslationY(paramView, paramFloat);
  }
  
  public static void stopNestedScroll(View paramView)
  {
    IMPL.stopNestedScroll(paramView);
  }
  
  static class Api24ViewCompatImpl
    extends ViewCompat.MarshmallowViewCompatImpl
  {}
  
  static class BaseViewCompatImpl
    implements ViewCompat.ViewCompatImpl
  {
    private static Method sChildrenDrawingOrderMethod;
    WeakHashMap<View, ViewPropertyAnimatorCompat> mViewPropertyAnimatorCompatMap = null;
    
    private boolean canScrollingViewScrollHorizontally(ScrollingView paramScrollingView, int paramInt)
    {
      int i = paramScrollingView.computeHorizontalScrollOffset();
      int j = paramScrollingView.computeHorizontalScrollRange() - paramScrollingView.computeHorizontalScrollExtent();
      if (j == 0) {
        return false;
      }
      if (paramInt < 0) {
        return i > 0;
      }
      return i < j - 1;
    }
    
    private boolean canScrollingViewScrollVertically(ScrollingView paramScrollingView, int paramInt)
    {
      int i = paramScrollingView.computeVerticalScrollOffset();
      int j = paramScrollingView.computeVerticalScrollRange() - paramScrollingView.computeVerticalScrollExtent();
      if (j == 0) {
        return false;
      }
      if (paramInt < 0) {
        return i > 0;
      }
      return i < j - 1;
    }
    
    public ViewPropertyAnimatorCompat animate(View paramView)
    {
      return new ViewPropertyAnimatorCompat(paramView);
    }
    
    public boolean canScrollHorizontally(View paramView, int paramInt)
    {
      if ((paramView instanceof ScrollingView)) {
        return canScrollingViewScrollHorizontally((ScrollingView)paramView, paramInt);
      }
      return false;
    }
    
    public boolean canScrollVertically(View paramView, int paramInt)
    {
      if ((paramView instanceof ScrollingView)) {
        return canScrollingViewScrollVertically((ScrollingView)paramView, paramInt);
      }
      return false;
    }
    
    public WindowInsetsCompat dispatchApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
    {
      return paramWindowInsetsCompat;
    }
    
    public float getAlpha(View paramView)
    {
      return 1.0F;
    }
    
    public ColorStateList getBackgroundTintList(View paramView)
    {
      return ViewCompatBase.getBackgroundTintList(paramView);
    }
    
    public PorterDuff.Mode getBackgroundTintMode(View paramView)
    {
      return ViewCompatBase.getBackgroundTintMode(paramView);
    }
    
    public Display getDisplay(View paramView)
    {
      return ViewCompatBase.getDisplay(paramView);
    }
    
    public float getElevation(View paramView)
    {
      return 0.0F;
    }
    
    public boolean getFitsSystemWindows(View paramView)
    {
      return false;
    }
    
    long getFrameTime()
    {
      return 10L;
    }
    
    public int getImportantForAccessibility(View paramView)
    {
      return 0;
    }
    
    public int getLayerType(View paramView)
    {
      return 0;
    }
    
    public int getLayoutDirection(View paramView)
    {
      return 0;
    }
    
    public Matrix getMatrix(View paramView)
    {
      return null;
    }
    
    public int getMeasuredState(View paramView)
    {
      return 0;
    }
    
    public int getMeasuredWidthAndState(View paramView)
    {
      return paramView.getMeasuredWidth();
    }
    
    public int getMinimumHeight(View paramView)
    {
      return ViewCompatBase.getMinimumHeight(paramView);
    }
    
    public int getMinimumWidth(View paramView)
    {
      return ViewCompatBase.getMinimumWidth(paramView);
    }
    
    public ViewParent getParentForAccessibility(View paramView)
    {
      return paramView.getParent();
    }
    
    public float getScaleX(View paramView)
    {
      return 0.0F;
    }
    
    public float getTranslationX(View paramView)
    {
      return 0.0F;
    }
    
    public float getTranslationY(View paramView)
    {
      return 0.0F;
    }
    
    public int getWindowSystemUiVisibility(View paramView)
    {
      return 0;
    }
    
    public float getY(View paramView)
    {
      return paramView.getTop();
    }
    
    public boolean hasAccessibilityDelegate(View paramView)
    {
      return false;
    }
    
    public boolean hasOverlappingRendering(View paramView)
    {
      return true;
    }
    
    public boolean hasTransientState(View paramView)
    {
      return false;
    }
    
    public boolean isAttachedToWindow(View paramView)
    {
      return ViewCompatBase.isAttachedToWindow(paramView);
    }
    
    public boolean isLaidOut(View paramView)
    {
      return ViewCompatBase.isLaidOut(paramView);
    }
    
    public boolean isNestedScrollingEnabled(View paramView)
    {
      if ((paramView instanceof NestedScrollingChild)) {
        return ((NestedScrollingChild)paramView).isNestedScrollingEnabled();
      }
      return false;
    }
    
    public void jumpDrawablesToCurrentState(View paramView) {}
    
    public void offsetLeftAndRight(View paramView, int paramInt)
    {
      ViewCompatBase.offsetLeftAndRight(paramView, paramInt);
    }
    
    public void offsetTopAndBottom(View paramView, int paramInt)
    {
      ViewCompatBase.offsetTopAndBottom(paramView, paramInt);
    }
    
    public WindowInsetsCompat onApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
    {
      return paramWindowInsetsCompat;
    }
    
    public void postInvalidateOnAnimation(View paramView)
    {
      paramView.invalidate();
    }
    
    public void postInvalidateOnAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramView.invalidate(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void postOnAnimation(View paramView, Runnable paramRunnable)
    {
      paramView.postDelayed(paramRunnable, getFrameTime());
    }
    
    public void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong)
    {
      paramView.postDelayed(paramRunnable, getFrameTime() + paramLong);
    }
    
    public void requestApplyInsets(View paramView) {}
    
    public int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3)
    {
      return View.resolveSize(paramInt1, paramInt2);
    }
    
    public void setAccessibilityDelegate(View paramView, AccessibilityDelegateCompat paramAccessibilityDelegateCompat) {}
    
    public void setActivated(View paramView, boolean paramBoolean) {}
    
    public void setAlpha(View paramView, float paramFloat) {}
    
    public void setBackground(View paramView, Drawable paramDrawable)
    {
      paramView.setBackgroundDrawable(paramDrawable);
    }
    
    public void setBackgroundTintList(View paramView, ColorStateList paramColorStateList)
    {
      ViewCompatBase.setBackgroundTintList(paramView, paramColorStateList);
    }
    
    public void setBackgroundTintMode(View paramView, PorterDuff.Mode paramMode)
    {
      ViewCompatBase.setBackgroundTintMode(paramView, paramMode);
    }
    
    public void setChildrenDrawingOrderEnabled(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      if (sChildrenDrawingOrderMethod == null) {}
      try
      {
        sChildrenDrawingOrderMethod = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", new Class[] { Boolean.TYPE });
        sChildrenDrawingOrderMethod.setAccessible(true);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;)
        {
          try
          {
            sChildrenDrawingOrderMethod.invoke(paramViewGroup, new Object[] { Boolean.valueOf(paramBoolean) });
            return;
          }
          catch (InvocationTargetException paramViewGroup)
          {
            Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", paramViewGroup);
            return;
          }
          catch (IllegalArgumentException paramViewGroup)
          {
            Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", paramViewGroup);
            return;
          }
          catch (IllegalAccessException paramViewGroup)
          {
            Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", paramViewGroup);
          }
          localNoSuchMethodException = localNoSuchMethodException;
          Log.e("ViewCompat", "Unable to find childrenDrawingOrderEnabled", localNoSuchMethodException);
        }
      }
    }
    
    public void setElevation(View paramView, float paramFloat) {}
    
    public void setImportantForAccessibility(View paramView, int paramInt) {}
    
    public void setLayerPaint(View paramView, Paint paramPaint) {}
    
    public void setLayerType(View paramView, int paramInt, Paint paramPaint) {}
    
    public void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener) {}
    
    public void setSaveFromParentEnabled(View paramView, boolean paramBoolean) {}
    
    public void setScaleX(View paramView, float paramFloat) {}
    
    public void setScaleY(View paramView, float paramFloat) {}
    
    public void setTranslationX(View paramView, float paramFloat) {}
    
    public void setTranslationY(View paramView, float paramFloat) {}
    
    public void stopNestedScroll(View paramView)
    {
      if ((paramView instanceof NestedScrollingChild)) {
        ((NestedScrollingChild)paramView).stopNestedScroll();
      }
    }
  }
  
  static class HCViewCompatImpl
    extends ViewCompat.BaseViewCompatImpl
  {
    public float getAlpha(View paramView)
    {
      return ViewCompatHC.getAlpha(paramView);
    }
    
    long getFrameTime()
    {
      return ViewCompatHC.getFrameTime();
    }
    
    public int getLayerType(View paramView)
    {
      return ViewCompatHC.getLayerType(paramView);
    }
    
    public Matrix getMatrix(View paramView)
    {
      return ViewCompatHC.getMatrix(paramView);
    }
    
    public int getMeasuredState(View paramView)
    {
      return ViewCompatHC.getMeasuredState(paramView);
    }
    
    public int getMeasuredWidthAndState(View paramView)
    {
      return ViewCompatHC.getMeasuredWidthAndState(paramView);
    }
    
    public float getScaleX(View paramView)
    {
      return ViewCompatHC.getScaleX(paramView);
    }
    
    public float getTranslationX(View paramView)
    {
      return ViewCompatHC.getTranslationX(paramView);
    }
    
    public float getTranslationY(View paramView)
    {
      return ViewCompatHC.getTranslationY(paramView);
    }
    
    public float getY(View paramView)
    {
      return ViewCompatHC.getY(paramView);
    }
    
    public void jumpDrawablesToCurrentState(View paramView)
    {
      ViewCompatHC.jumpDrawablesToCurrentState(paramView);
    }
    
    public void offsetLeftAndRight(View paramView, int paramInt)
    {
      ViewCompatHC.offsetLeftAndRight(paramView, paramInt);
    }
    
    public void offsetTopAndBottom(View paramView, int paramInt)
    {
      ViewCompatHC.offsetTopAndBottom(paramView, paramInt);
    }
    
    public int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3)
    {
      return ViewCompatHC.resolveSizeAndState(paramInt1, paramInt2, paramInt3);
    }
    
    public void setActivated(View paramView, boolean paramBoolean)
    {
      ViewCompatHC.setActivated(paramView, paramBoolean);
    }
    
    public void setAlpha(View paramView, float paramFloat)
    {
      ViewCompatHC.setAlpha(paramView, paramFloat);
    }
    
    public void setLayerPaint(View paramView, Paint paramPaint)
    {
      setLayerType(paramView, getLayerType(paramView), paramPaint);
      paramView.invalidate();
    }
    
    public void setLayerType(View paramView, int paramInt, Paint paramPaint)
    {
      ViewCompatHC.setLayerType(paramView, paramInt, paramPaint);
    }
    
    public void setSaveFromParentEnabled(View paramView, boolean paramBoolean)
    {
      ViewCompatHC.setSaveFromParentEnabled(paramView, paramBoolean);
    }
    
    public void setScaleX(View paramView, float paramFloat)
    {
      ViewCompatHC.setScaleX(paramView, paramFloat);
    }
    
    public void setScaleY(View paramView, float paramFloat)
    {
      ViewCompatHC.setScaleY(paramView, paramFloat);
    }
    
    public void setTranslationX(View paramView, float paramFloat)
    {
      ViewCompatHC.setTranslationX(paramView, paramFloat);
    }
    
    public void setTranslationY(View paramView, float paramFloat)
    {
      ViewCompatHC.setTranslationY(paramView, paramFloat);
    }
  }
  
  static class ICSMr1ViewCompatImpl
    extends ViewCompat.ICSViewCompatImpl
  {}
  
  static class ICSViewCompatImpl
    extends ViewCompat.HCViewCompatImpl
  {
    static boolean accessibilityDelegateCheckFailed = false;
    static Field mAccessibilityDelegateField;
    
    public ViewPropertyAnimatorCompat animate(View paramView)
    {
      if (this.mViewPropertyAnimatorCompatMap == null) {
        this.mViewPropertyAnimatorCompatMap = new WeakHashMap();
      }
      ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat2 = (ViewPropertyAnimatorCompat)this.mViewPropertyAnimatorCompatMap.get(paramView);
      ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat1 = localViewPropertyAnimatorCompat2;
      if (localViewPropertyAnimatorCompat2 == null)
      {
        localViewPropertyAnimatorCompat1 = new ViewPropertyAnimatorCompat(paramView);
        this.mViewPropertyAnimatorCompatMap.put(paramView, localViewPropertyAnimatorCompat1);
      }
      return localViewPropertyAnimatorCompat1;
    }
    
    public boolean canScrollHorizontally(View paramView, int paramInt)
    {
      return ViewCompatICS.canScrollHorizontally(paramView, paramInt);
    }
    
    public boolean canScrollVertically(View paramView, int paramInt)
    {
      return ViewCompatICS.canScrollVertically(paramView, paramInt);
    }
    
    /* Error */
    public boolean hasAccessibilityDelegate(View paramView)
    {
      // Byte code:
      //   0: getstatic 15	android/support/v4/view/ViewCompat$ICSViewCompatImpl:accessibilityDelegateCheckFailed	Z
      //   3: ifeq +5 -> 8
      //   6: iconst_0
      //   7: ireturn
      //   8: getstatic 56	android/support/v4/view/ViewCompat$ICSViewCompatImpl:mAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   11: ifnonnull +20 -> 31
      //   14: ldc 58
      //   16: ldc 60
      //   18: invokevirtual 66	java/lang/Class:getDeclaredField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
      //   21: putstatic 56	android/support/v4/view/ViewCompat$ICSViewCompatImpl:mAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   24: getstatic 56	android/support/v4/view/ViewCompat$ICSViewCompatImpl:mAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   27: iconst_1
      //   28: invokevirtual 72	java/lang/reflect/Field:setAccessible	(Z)V
      //   31: getstatic 56	android/support/v4/view/ViewCompat$ICSViewCompatImpl:mAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   34: aload_1
      //   35: invokevirtual 73	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   38: astore_1
      //   39: aload_1
      //   40: ifnull +12 -> 52
      //   43: iconst_1
      //   44: ireturn
      //   45: astore_1
      //   46: iconst_1
      //   47: putstatic 15	android/support/v4/view/ViewCompat$ICSViewCompatImpl:accessibilityDelegateCheckFailed	Z
      //   50: iconst_0
      //   51: ireturn
      //   52: iconst_0
      //   53: ireturn
      //   54: astore_1
      //   55: iconst_1
      //   56: putstatic 15	android/support/v4/view/ViewCompat$ICSViewCompatImpl:accessibilityDelegateCheckFailed	Z
      //   59: iconst_0
      //   60: ireturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	61	0	this	ICSViewCompatImpl
      //   0	61	1	paramView	View
      // Exception table:
      //   from	to	target	type
      //   14	31	45	java/lang/Throwable
      //   31	39	54	java/lang/Throwable
    }
    
    public void setAccessibilityDelegate(View paramView, @Nullable AccessibilityDelegateCompat paramAccessibilityDelegateCompat)
    {
      Object localObject = null;
      if (paramAccessibilityDelegateCompat == null) {}
      for (paramAccessibilityDelegateCompat = (AccessibilityDelegateCompat)localObject;; paramAccessibilityDelegateCompat = paramAccessibilityDelegateCompat.getBridge())
      {
        ViewCompatICS.setAccessibilityDelegate(paramView, paramAccessibilityDelegateCompat);
        return;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ImportantForAccessibility {}
  
  static class JBViewCompatImpl
    extends ViewCompat.ICSMr1ViewCompatImpl
  {
    public boolean getFitsSystemWindows(View paramView)
    {
      return ViewCompatJB.getFitsSystemWindows(paramView);
    }
    
    public int getImportantForAccessibility(View paramView)
    {
      return ViewCompatJB.getImportantForAccessibility(paramView);
    }
    
    public int getMinimumHeight(View paramView)
    {
      return ViewCompatJB.getMinimumHeight(paramView);
    }
    
    public int getMinimumWidth(View paramView)
    {
      return ViewCompatJB.getMinimumWidth(paramView);
    }
    
    public ViewParent getParentForAccessibility(View paramView)
    {
      return ViewCompatJB.getParentForAccessibility(paramView);
    }
    
    public boolean hasOverlappingRendering(View paramView)
    {
      return ViewCompatJB.hasOverlappingRendering(paramView);
    }
    
    public boolean hasTransientState(View paramView)
    {
      return ViewCompatJB.hasTransientState(paramView);
    }
    
    public void postInvalidateOnAnimation(View paramView)
    {
      ViewCompatJB.postInvalidateOnAnimation(paramView);
    }
    
    public void postInvalidateOnAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      ViewCompatJB.postInvalidateOnAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void postOnAnimation(View paramView, Runnable paramRunnable)
    {
      ViewCompatJB.postOnAnimation(paramView, paramRunnable);
    }
    
    public void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong)
    {
      ViewCompatJB.postOnAnimationDelayed(paramView, paramRunnable, paramLong);
    }
    
    public void requestApplyInsets(View paramView)
    {
      ViewCompatJB.requestApplyInsets(paramView);
    }
    
    public void setBackground(View paramView, Drawable paramDrawable)
    {
      ViewCompatJB.setBackground(paramView, paramDrawable);
    }
    
    public void setImportantForAccessibility(View paramView, int paramInt)
    {
      int i = paramInt;
      if (paramInt == 4) {
        i = 2;
      }
      ViewCompatJB.setImportantForAccessibility(paramView, i);
    }
  }
  
  static class JbMr1ViewCompatImpl
    extends ViewCompat.JBViewCompatImpl
  {
    public Display getDisplay(View paramView)
    {
      return ViewCompatJellybeanMr1.getDisplay(paramView);
    }
    
    public int getLayoutDirection(View paramView)
    {
      return ViewCompatJellybeanMr1.getLayoutDirection(paramView);
    }
    
    public int getWindowSystemUiVisibility(View paramView)
    {
      return ViewCompatJellybeanMr1.getWindowSystemUiVisibility(paramView);
    }
    
    public void setLayerPaint(View paramView, Paint paramPaint)
    {
      ViewCompatJellybeanMr1.setLayerPaint(paramView, paramPaint);
    }
  }
  
  static class JbMr2ViewCompatImpl
    extends ViewCompat.JbMr1ViewCompatImpl
  {}
  
  static class KitKatViewCompatImpl
    extends ViewCompat.JbMr2ViewCompatImpl
  {
    public boolean isAttachedToWindow(View paramView)
    {
      return ViewCompatKitKat.isAttachedToWindow(paramView);
    }
    
    public boolean isLaidOut(View paramView)
    {
      return ViewCompatKitKat.isLaidOut(paramView);
    }
    
    public void setImportantForAccessibility(View paramView, int paramInt)
    {
      ViewCompatJB.setImportantForAccessibility(paramView, paramInt);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface LayerType {}
  
  static class LollipopViewCompatImpl
    extends ViewCompat.KitKatViewCompatImpl
  {
    public WindowInsetsCompat dispatchApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
    {
      return WindowInsetsCompat.wrap(ViewCompatLollipop.dispatchApplyWindowInsets(paramView, WindowInsetsCompat.unwrap(paramWindowInsetsCompat)));
    }
    
    public ColorStateList getBackgroundTintList(View paramView)
    {
      return ViewCompatLollipop.getBackgroundTintList(paramView);
    }
    
    public PorterDuff.Mode getBackgroundTintMode(View paramView)
    {
      return ViewCompatLollipop.getBackgroundTintMode(paramView);
    }
    
    public float getElevation(View paramView)
    {
      return ViewCompatLollipop.getElevation(paramView);
    }
    
    public boolean isNestedScrollingEnabled(View paramView)
    {
      return ViewCompatLollipop.isNestedScrollingEnabled(paramView);
    }
    
    public void offsetLeftAndRight(View paramView, int paramInt)
    {
      ViewCompatLollipop.offsetLeftAndRight(paramView, paramInt);
    }
    
    public void offsetTopAndBottom(View paramView, int paramInt)
    {
      ViewCompatLollipop.offsetTopAndBottom(paramView, paramInt);
    }
    
    public WindowInsetsCompat onApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
    {
      return WindowInsetsCompat.wrap(ViewCompatLollipop.onApplyWindowInsets(paramView, WindowInsetsCompat.unwrap(paramWindowInsetsCompat)));
    }
    
    public void requestApplyInsets(View paramView)
    {
      ViewCompatLollipop.requestApplyInsets(paramView);
    }
    
    public void setBackgroundTintList(View paramView, ColorStateList paramColorStateList)
    {
      ViewCompatLollipop.setBackgroundTintList(paramView, paramColorStateList);
    }
    
    public void setBackgroundTintMode(View paramView, PorterDuff.Mode paramMode)
    {
      ViewCompatLollipop.setBackgroundTintMode(paramView, paramMode);
    }
    
    public void setElevation(View paramView, float paramFloat)
    {
      ViewCompatLollipop.setElevation(paramView, paramFloat);
    }
    
    public void setOnApplyWindowInsetsListener(View paramView, final OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener)
    {
      if (paramOnApplyWindowInsetsListener == null)
      {
        ViewCompatLollipop.setOnApplyWindowInsetsListener(paramView, null);
        return;
      }
      ViewCompatLollipop.setOnApplyWindowInsetsListener(paramView, new ViewCompatLollipop.OnApplyWindowInsetsListenerBridge()
      {
        public Object onApplyWindowInsets(View paramAnonymousView, Object paramAnonymousObject)
        {
          paramAnonymousObject = WindowInsetsCompat.wrap(paramAnonymousObject);
          return WindowInsetsCompat.unwrap(paramOnApplyWindowInsetsListener.onApplyWindowInsets(paramAnonymousView, (WindowInsetsCompat)paramAnonymousObject));
        }
      });
    }
    
    public void stopNestedScroll(View paramView)
    {
      ViewCompatLollipop.stopNestedScroll(paramView);
    }
  }
  
  static class MarshmallowViewCompatImpl
    extends ViewCompat.LollipopViewCompatImpl
  {
    public void offsetLeftAndRight(View paramView, int paramInt)
    {
      ViewCompatMarshmallow.offsetLeftAndRight(paramView, paramInt);
    }
    
    public void offsetTopAndBottom(View paramView, int paramInt)
    {
      ViewCompatMarshmallow.offsetTopAndBottom(paramView, paramInt);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ResolvedLayoutDirectionMode {}
  
  static abstract interface ViewCompatImpl
  {
    public abstract ViewPropertyAnimatorCompat animate(View paramView);
    
    public abstract boolean canScrollHorizontally(View paramView, int paramInt);
    
    public abstract boolean canScrollVertically(View paramView, int paramInt);
    
    public abstract WindowInsetsCompat dispatchApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat);
    
    public abstract float getAlpha(View paramView);
    
    public abstract ColorStateList getBackgroundTintList(View paramView);
    
    public abstract PorterDuff.Mode getBackgroundTintMode(View paramView);
    
    public abstract Display getDisplay(View paramView);
    
    public abstract float getElevation(View paramView);
    
    public abstract boolean getFitsSystemWindows(View paramView);
    
    public abstract int getImportantForAccessibility(View paramView);
    
    public abstract int getLayerType(View paramView);
    
    public abstract int getLayoutDirection(View paramView);
    
    @Nullable
    public abstract Matrix getMatrix(View paramView);
    
    public abstract int getMeasuredState(View paramView);
    
    public abstract int getMeasuredWidthAndState(View paramView);
    
    public abstract int getMinimumHeight(View paramView);
    
    public abstract int getMinimumWidth(View paramView);
    
    public abstract ViewParent getParentForAccessibility(View paramView);
    
    public abstract float getScaleX(View paramView);
    
    public abstract float getTranslationX(View paramView);
    
    public abstract float getTranslationY(View paramView);
    
    public abstract int getWindowSystemUiVisibility(View paramView);
    
    public abstract float getY(View paramView);
    
    public abstract boolean hasAccessibilityDelegate(View paramView);
    
    public abstract boolean hasOverlappingRendering(View paramView);
    
    public abstract boolean hasTransientState(View paramView);
    
    public abstract boolean isAttachedToWindow(View paramView);
    
    public abstract boolean isLaidOut(View paramView);
    
    public abstract boolean isNestedScrollingEnabled(View paramView);
    
    public abstract void jumpDrawablesToCurrentState(View paramView);
    
    public abstract void offsetLeftAndRight(View paramView, int paramInt);
    
    public abstract void offsetTopAndBottom(View paramView, int paramInt);
    
    public abstract WindowInsetsCompat onApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat);
    
    public abstract void postInvalidateOnAnimation(View paramView);
    
    public abstract void postInvalidateOnAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract void postOnAnimation(View paramView, Runnable paramRunnable);
    
    public abstract void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong);
    
    public abstract void requestApplyInsets(View paramView);
    
    public abstract int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void setAccessibilityDelegate(View paramView, @Nullable AccessibilityDelegateCompat paramAccessibilityDelegateCompat);
    
    public abstract void setActivated(View paramView, boolean paramBoolean);
    
    public abstract void setAlpha(View paramView, float paramFloat);
    
    public abstract void setBackground(View paramView, Drawable paramDrawable);
    
    public abstract void setBackgroundTintList(View paramView, ColorStateList paramColorStateList);
    
    public abstract void setBackgroundTintMode(View paramView, PorterDuff.Mode paramMode);
    
    public abstract void setChildrenDrawingOrderEnabled(ViewGroup paramViewGroup, boolean paramBoolean);
    
    public abstract void setElevation(View paramView, float paramFloat);
    
    public abstract void setImportantForAccessibility(View paramView, int paramInt);
    
    public abstract void setLayerPaint(View paramView, Paint paramPaint);
    
    public abstract void setLayerType(View paramView, int paramInt, Paint paramPaint);
    
    public abstract void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener);
    
    public abstract void setSaveFromParentEnabled(View paramView, boolean paramBoolean);
    
    public abstract void setScaleX(View paramView, float paramFloat);
    
    public abstract void setScaleY(View paramView, float paramFloat);
    
    public abstract void setTranslationX(View paramView, float paramFloat);
    
    public abstract void setTranslationY(View paramView, float paramFloat);
    
    public abstract void stopNestedScroll(View paramView);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\ViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */