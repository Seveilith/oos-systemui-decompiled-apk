package com.android.keyguard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewHierarchyEncoder;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ViewFlipper;
import com.android.internal.widget.LockPatternUtils;

public class KeyguardSecurityViewFlipper
  extends ViewFlipper
  implements KeyguardSecurityView
{
  private Rect mTempRect = new Rect();
  
  public KeyguardSecurityViewFlipper(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardSecurityViewFlipper(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private int makeChildMeasureSpec(int paramInt1, int paramInt2)
  {
    int i;
    switch (paramInt2)
    {
    default: 
      i = 1073741824;
      paramInt2 = Math.min(paramInt1, paramInt2);
      paramInt1 = i;
    }
    for (;;)
    {
      return View.MeasureSpec.makeMeasureSpec(paramInt2, paramInt1);
      i = Integer.MIN_VALUE;
      paramInt2 = paramInt1;
      paramInt1 = i;
      continue;
      i = 1073741824;
      paramInt2 = paramInt1;
      paramInt1 = i;
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  KeyguardSecurityView getSecurityView()
  {
    View localView = getChildAt(getDisplayedChild());
    if ((localView instanceof KeyguardSecurityView)) {
      return (KeyguardSecurityView)localView;
    }
    return null;
  }
  
  public boolean isCheckingPassword()
  {
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView();
    if (localKeyguardSecurityView != null) {
      return localKeyguardSecurityView.isCheckingPassword();
    }
    return false;
  }
  
  public boolean needsInput()
  {
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView();
    if (localKeyguardSecurityView != null) {
      return localKeyguardSecurityView.needsInput();
    }
    return false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i5 = View.MeasureSpec.getMode(paramInt1);
    int i1 = View.MeasureSpec.getMode(paramInt2);
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt2);
    paramInt2 = i;
    paramInt1 = j;
    int n = getChildCount();
    int k = 0;
    Object localObject;
    int m;
    while (k < n)
    {
      localObject = (LayoutParams)getChildAt(k).getLayoutParams();
      m = paramInt2;
      if (((LayoutParams)localObject).maxWidth > 0)
      {
        m = paramInt2;
        if (((LayoutParams)localObject).maxWidth < paramInt2) {
          m = ((LayoutParams)localObject).maxWidth;
        }
      }
      paramInt2 = paramInt1;
      if (((LayoutParams)localObject).maxHeight > 0)
      {
        paramInt2 = paramInt1;
        if (((LayoutParams)localObject).maxHeight < paramInt1) {
          paramInt2 = ((LayoutParams)localObject).maxHeight;
        }
      }
      k += 1;
      paramInt1 = paramInt2;
      paramInt2 = m;
    }
    int i2 = getPaddingLeft() + getPaddingRight();
    int i3 = getPaddingTop() + getPaddingBottom();
    int i4 = Math.max(0, paramInt2 - i2);
    int i6 = Math.max(0, paramInt1 - i3);
    if (i5 == 1073741824)
    {
      paramInt1 = i;
      if (i1 != 1073741824) {
        break label298;
      }
    }
    label298:
    for (paramInt2 = j;; paramInt2 = 0)
    {
      m = 0;
      k = paramInt1;
      paramInt1 = m;
      while (paramInt1 < n)
      {
        localObject = getChildAt(paramInt1);
        LayoutParams localLayoutParams = (LayoutParams)((View)localObject).getLayoutParams();
        ((View)localObject).measure(makeChildMeasureSpec(i4, localLayoutParams.width), makeChildMeasureSpec(i6, localLayoutParams.height));
        k = Math.max(k, Math.min(((View)localObject).getMeasuredWidth(), i - i2));
        paramInt2 = Math.max(paramInt2, Math.min(((View)localObject).getMeasuredHeight(), j - i3));
        paramInt1 += 1;
      }
      paramInt1 = 0;
      break;
    }
    setMeasuredDimension(k + i2, paramInt2 + i3);
  }
  
  public void onPause()
  {
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView();
    if (localKeyguardSecurityView != null) {
      localKeyguardSecurityView.onPause();
    }
  }
  
  public void onResume(int paramInt)
  {
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView();
    if (localKeyguardSecurityView != null) {
      localKeyguardSecurityView.onResume(paramInt);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = super.onTouchEvent(paramMotionEvent);
    this.mTempRect.set(0, 0, 0, 0);
    int i = 0;
    if (i < getChildCount())
    {
      View localView = getChildAt(i);
      boolean bool2 = bool1;
      if (localView.getVisibility() == 0)
      {
        offsetRectIntoDescendantCoords(localView, this.mTempRect);
        paramMotionEvent.offsetLocation(this.mTempRect.left, this.mTempRect.top);
        if (localView.dispatchTouchEvent(paramMotionEvent)) {
          break label119;
        }
      }
      for (;;)
      {
        paramMotionEvent.offsetLocation(-this.mTempRect.left, -this.mTempRect.top);
        bool2 = bool1;
        i += 1;
        bool1 = bool2;
        break;
        label119:
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void setKeyguardCallback(KeyguardSecurityCallback paramKeyguardSecurityCallback)
  {
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView();
    if (localKeyguardSecurityView != null) {
      localKeyguardSecurityView.setKeyguardCallback(paramKeyguardSecurityCallback);
    }
  }
  
  public void setLockPatternUtils(LockPatternUtils paramLockPatternUtils)
  {
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView();
    if (localKeyguardSecurityView != null) {
      localKeyguardSecurityView.setLockPatternUtils(paramLockPatternUtils);
    }
  }
  
  public void showMessage(String paramString, int paramInt)
  {
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView();
    if (localKeyguardSecurityView != null) {
      localKeyguardSecurityView.showMessage(paramString, paramInt);
    }
  }
  
  public void showPromptReason(int paramInt)
  {
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView();
    if (localKeyguardSecurityView != null) {
      localKeyguardSecurityView.showPromptReason(paramInt);
    }
  }
  
  public void startAppearAnimation()
  {
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView();
    if (localKeyguardSecurityView != null) {
      localKeyguardSecurityView.startAppearAnimation();
    }
  }
  
  public boolean startDisappearAnimation(Runnable paramRunnable)
  {
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView();
    if (localKeyguardSecurityView != null) {
      return localKeyguardSecurityView.startDisappearAnimation(paramRunnable);
    }
    return false;
  }
  
  public static class LayoutParams
    extends FrameLayout.LayoutParams
  {
    @ViewDebug.ExportedProperty(category="layout")
    public int maxHeight;
    @ViewDebug.ExportedProperty(category="layout")
    public int maxWidth;
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.KeyguardSecurityViewFlipper_Layout, 0, 0);
      this.maxWidth = paramContext.getDimensionPixelSize(R.styleable.KeyguardSecurityViewFlipper_Layout_layout_maxWidth, 0);
      this.maxHeight = paramContext.getDimensionPixelSize(R.styleable.KeyguardSecurityViewFlipper_Layout_layout_maxHeight, 0);
      paramContext.recycle();
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.maxWidth = paramLayoutParams.maxWidth;
      this.maxHeight = paramLayoutParams.maxHeight;
    }
    
    protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      super.encodeProperties(paramViewHierarchyEncoder);
      paramViewHierarchyEncoder.addProperty("layout:maxWidth", this.maxWidth);
      paramViewHierarchyEncoder.addProperty("layout:maxHeight", this.maxHeight);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardSecurityViewFlipper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */