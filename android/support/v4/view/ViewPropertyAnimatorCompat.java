package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public final class ViewPropertyAnimatorCompat
{
  static final ViewPropertyAnimatorCompatImpl IMPL = new BaseViewPropertyAnimatorCompatImpl();
  Runnable mEndAction = null;
  int mOldLayerType = -1;
  Runnable mStartAction = null;
  private WeakReference<View> mView;
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 21)
    {
      IMPL = new LollipopViewPropertyAnimatorCompatImpl();
      return;
    }
    if (i >= 19)
    {
      IMPL = new KitKatViewPropertyAnimatorCompatImpl();
      return;
    }
    if (i >= 18)
    {
      IMPL = new JBMr2ViewPropertyAnimatorCompatImpl();
      return;
    }
    if (i >= 16)
    {
      IMPL = new JBViewPropertyAnimatorCompatImpl();
      return;
    }
    if (i >= 14)
    {
      IMPL = new ICSViewPropertyAnimatorCompatImpl();
      return;
    }
  }
  
  ViewPropertyAnimatorCompat(View paramView)
  {
    this.mView = new WeakReference(paramView);
  }
  
  public ViewPropertyAnimatorCompat alpha(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView != null) {
      IMPL.alpha(this, localView, paramFloat);
    }
    return this;
  }
  
  public void cancel()
  {
    View localView = (View)this.mView.get();
    if (localView != null) {
      IMPL.cancel(this, localView);
    }
  }
  
  public ViewPropertyAnimatorCompat setDuration(long paramLong)
  {
    View localView = (View)this.mView.get();
    if (localView != null) {
      IMPL.setDuration(this, localView, paramLong);
    }
    return this;
  }
  
  public ViewPropertyAnimatorCompat setListener(ViewPropertyAnimatorListener paramViewPropertyAnimatorListener)
  {
    View localView = (View)this.mView.get();
    if (localView != null) {
      IMPL.setListener(this, localView, paramViewPropertyAnimatorListener);
    }
    return this;
  }
  
  public void start()
  {
    View localView = (View)this.mView.get();
    if (localView != null) {
      IMPL.start(this, localView);
    }
  }
  
  public ViewPropertyAnimatorCompat translationX(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView != null) {
      IMPL.translationX(this, localView, paramFloat);
    }
    return this;
  }
  
  public ViewPropertyAnimatorCompat translationY(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView != null) {
      IMPL.translationY(this, localView, paramFloat);
    }
    return this;
  }
  
  static class BaseViewPropertyAnimatorCompatImpl
    implements ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
  {
    WeakHashMap<View, Runnable> mStarterMap = null;
    
    private void postStartMessage(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      Runnable localRunnable = null;
      if (this.mStarterMap != null) {
        localRunnable = (Runnable)this.mStarterMap.get(paramView);
      }
      Object localObject = localRunnable;
      if (localRunnable == null)
      {
        localObject = new Starter(paramViewPropertyAnimatorCompat, paramView);
        if (this.mStarterMap == null) {
          this.mStarterMap = new WeakHashMap();
        }
        this.mStarterMap.put(paramView, localObject);
      }
      paramView.removeCallbacks((Runnable)localObject);
      paramView.post((Runnable)localObject);
    }
    
    private void removeStartMessage(View paramView)
    {
      if (this.mStarterMap != null)
      {
        Runnable localRunnable = (Runnable)this.mStarterMap.get(paramView);
        if (localRunnable != null) {
          paramView.removeCallbacks(localRunnable);
        }
      }
    }
    
    public void alpha(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void cancel(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void setDuration(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, long paramLong) {}
    
    public void setListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorListener paramViewPropertyAnimatorListener)
    {
      paramView.setTag(2113929216, paramViewPropertyAnimatorListener);
    }
    
    public void start(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      removeStartMessage(paramView);
      startAnimation(paramViewPropertyAnimatorCompat, paramView);
    }
    
    void startAnimation(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      Object localObject = paramView.getTag(2113929216);
      ViewPropertyAnimatorListener localViewPropertyAnimatorListener = null;
      if ((localObject instanceof ViewPropertyAnimatorListener)) {
        localViewPropertyAnimatorListener = (ViewPropertyAnimatorListener)localObject;
      }
      localObject = paramViewPropertyAnimatorCompat.mStartAction;
      Runnable localRunnable = paramViewPropertyAnimatorCompat.mEndAction;
      paramViewPropertyAnimatorCompat.mStartAction = null;
      paramViewPropertyAnimatorCompat.mEndAction = null;
      if (localObject != null) {
        ((Runnable)localObject).run();
      }
      if (localViewPropertyAnimatorListener != null)
      {
        localViewPropertyAnimatorListener.onAnimationStart(paramView);
        localViewPropertyAnimatorListener.onAnimationEnd(paramView);
      }
      if (localRunnable != null) {
        localRunnable.run();
      }
      if (this.mStarterMap != null) {
        this.mStarterMap.remove(paramView);
      }
    }
    
    public void translationX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void translationY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    class Starter
      implements Runnable
    {
      WeakReference<View> mViewRef;
      ViewPropertyAnimatorCompat mVpa;
      
      Starter(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
      {
        this.mViewRef = new WeakReference(paramView);
        this.mVpa = paramViewPropertyAnimatorCompat;
      }
      
      public void run()
      {
        View localView = (View)this.mViewRef.get();
        if (localView != null) {
          ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl.this.startAnimation(this.mVpa, localView);
        }
      }
    }
  }
  
  static class ICSViewPropertyAnimatorCompatImpl
    extends ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl
  {
    WeakHashMap<View, Integer> mLayerMap = null;
    
    public void alpha(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.alpha(paramView, paramFloat);
    }
    
    public void cancel(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      ViewPropertyAnimatorCompatICS.cancel(paramView);
    }
    
    public void setDuration(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, long paramLong)
    {
      ViewPropertyAnimatorCompatICS.setDuration(paramView, paramLong);
    }
    
    public void setListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorListener paramViewPropertyAnimatorListener)
    {
      paramView.setTag(2113929216, paramViewPropertyAnimatorListener);
      ViewPropertyAnimatorCompatICS.setListener(paramView, new MyVpaListener(paramViewPropertyAnimatorCompat));
    }
    
    public void start(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      ViewPropertyAnimatorCompatICS.start(paramView);
    }
    
    public void translationX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.translationX(paramView, paramFloat);
    }
    
    public void translationY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.translationY(paramView, paramFloat);
    }
    
    static class MyVpaListener
      implements ViewPropertyAnimatorListener
    {
      boolean mAnimEndCalled;
      ViewPropertyAnimatorCompat mVpa;
      
      MyVpaListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat)
      {
        this.mVpa = paramViewPropertyAnimatorCompat;
      }
      
      public void onAnimationCancel(View paramView)
      {
        Object localObject = paramView.getTag(2113929216);
        ViewPropertyAnimatorListener localViewPropertyAnimatorListener = null;
        if ((localObject instanceof ViewPropertyAnimatorListener)) {
          localViewPropertyAnimatorListener = (ViewPropertyAnimatorListener)localObject;
        }
        if (localViewPropertyAnimatorListener != null) {
          localViewPropertyAnimatorListener.onAnimationCancel(paramView);
        }
      }
      
      public void onAnimationEnd(View paramView)
      {
        if (this.mVpa.mOldLayerType >= 0)
        {
          ViewCompat.setLayerType(paramView, this.mVpa.mOldLayerType, null);
          this.mVpa.mOldLayerType = -1;
        }
        if ((Build.VERSION.SDK_INT < 16) && (this.mAnimEndCalled)) {
          return;
        }
        if (this.mVpa.mEndAction != null)
        {
          localObject1 = this.mVpa.mEndAction;
          this.mVpa.mEndAction = null;
          ((Runnable)localObject1).run();
        }
        Object localObject2 = paramView.getTag(2113929216);
        Object localObject1 = null;
        if ((localObject2 instanceof ViewPropertyAnimatorListener)) {
          localObject1 = (ViewPropertyAnimatorListener)localObject2;
        }
        if (localObject1 != null) {
          ((ViewPropertyAnimatorListener)localObject1).onAnimationEnd(paramView);
        }
        this.mAnimEndCalled = true;
      }
      
      public void onAnimationStart(View paramView)
      {
        this.mAnimEndCalled = false;
        if (this.mVpa.mOldLayerType >= 0) {
          ViewCompat.setLayerType(paramView, 2, null);
        }
        if (this.mVpa.mStartAction != null)
        {
          localObject1 = this.mVpa.mStartAction;
          this.mVpa.mStartAction = null;
          ((Runnable)localObject1).run();
        }
        Object localObject2 = paramView.getTag(2113929216);
        Object localObject1 = null;
        if ((localObject2 instanceof ViewPropertyAnimatorListener)) {
          localObject1 = (ViewPropertyAnimatorListener)localObject2;
        }
        if (localObject1 != null) {
          ((ViewPropertyAnimatorListener)localObject1).onAnimationStart(paramView);
        }
      }
    }
  }
  
  static class JBMr2ViewPropertyAnimatorCompatImpl
    extends ViewPropertyAnimatorCompat.JBViewPropertyAnimatorCompatImpl
  {}
  
  static class JBViewPropertyAnimatorCompatImpl
    extends ViewPropertyAnimatorCompat.ICSViewPropertyAnimatorCompatImpl
  {
    public void setListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorListener paramViewPropertyAnimatorListener)
    {
      ViewPropertyAnimatorCompatJB.setListener(paramView, paramViewPropertyAnimatorListener);
    }
  }
  
  static class KitKatViewPropertyAnimatorCompatImpl
    extends ViewPropertyAnimatorCompat.JBMr2ViewPropertyAnimatorCompatImpl
  {}
  
  static class LollipopViewPropertyAnimatorCompatImpl
    extends ViewPropertyAnimatorCompat.KitKatViewPropertyAnimatorCompatImpl
  {}
  
  static abstract interface ViewPropertyAnimatorCompatImpl
  {
    public abstract void alpha(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void cancel(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView);
    
    public abstract void setDuration(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, long paramLong);
    
    public abstract void setListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorListener paramViewPropertyAnimatorListener);
    
    public abstract void start(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView);
    
    public abstract void translationX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void translationY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\ViewPropertyAnimatorCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */