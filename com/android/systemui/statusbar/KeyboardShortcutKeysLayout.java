package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public final class KeyboardShortcutKeysLayout
  extends ViewGroup
{
  private final Context mContext;
  private int mLineHeight;
  
  public KeyboardShortcutKeysLayout(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
  }
  
  public KeyboardShortcutKeysLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
  }
  
  private int getHorizontalVerticalSpacing()
  {
    return (int)TypedValue.applyDimension(1, 4.0F, getResources().getDisplayMetrics());
  }
  
  private boolean isRTL()
  {
    return this.mContext.getResources().getConfiguration().getLayoutDirection() == 1;
  }
  
  private void layoutChildrenOnRow(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    int i = paramInt4;
    if (!isRTL()) {
      i = getPaddingLeft() + paramInt3 - paramInt4 + paramInt6;
    }
    paramInt6 = paramInt1;
    paramInt4 = i;
    if (paramInt6 < paramInt2)
    {
      View localView = getChildAt(paramInt6);
      int j = localView.getMeasuredWidth();
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      i = paramInt4;
      if (isRTL())
      {
        i = paramInt4;
        if (paramInt6 == paramInt1) {
          i = paramInt3 - paramInt4 - getPaddingRight() - j - localLayoutParams.mHorizontalSpacing;
        }
      }
      localView.layout(i, paramInt5, i + j, localView.getMeasuredHeight() + paramInt5);
      if (isRTL()) {
        if (paramInt6 < paramInt2 - 1) {
          paramInt4 = getChildAt(paramInt6 + 1).getMeasuredWidth();
        }
      }
      label154:
      for (paramInt4 = i - (localLayoutParams.mHorizontalSpacing + paramInt4);; paramInt4 = i + (localLayoutParams.mHorizontalSpacing + j))
      {
        paramInt6 += 1;
        break;
        paramInt4 = 0;
        break label154;
      }
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    int i = getHorizontalVerticalSpacing();
    return new LayoutParams(i, i);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    int i = getHorizontalVerticalSpacing();
    return new LayoutParams(i, i, paramLayoutParams);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i1 = getChildCount();
    int i2 = paramInt3 - paramInt1;
    int m;
    int i;
    label42:
    Object localObject;
    int j;
    int k;
    int n;
    int i3;
    if (isRTL())
    {
      paramInt4 = i2 - getPaddingRight();
      paramInt3 = getPaddingTop();
      m = 0;
      i = 0;
      paramInt2 = 0;
      if (paramInt2 >= i1) {
        break label285;
      }
      localObject = getChildAt(paramInt2);
      j = i;
      paramInt1 = paramInt4;
      k = paramInt3;
      n = m;
      if (((View)localObject).getVisibility() != 8)
      {
        i3 = ((View)localObject).getMeasuredWidth();
        localObject = (LayoutParams)((View)localObject).getLayoutParams();
        if (!isRTL()) {
          break label242;
        }
        if (paramInt4 - getPaddingLeft() - i3 >= 0) {
          break label237;
        }
        paramInt1 = 1;
        label119:
        j = i;
        n = paramInt4;
        k = paramInt3;
        if (paramInt1 != 0)
        {
          layoutChildrenOnRow(i, paramInt2, i2, paramInt4, paramInt3, m);
          if (!isRTL()) {
            break label262;
          }
          paramInt1 = i2 - getPaddingRight();
          label165:
          k = paramInt3 + this.mLineHeight;
          j = paramInt2;
          n = paramInt1;
        }
        if (!isRTL()) {
          break label270;
        }
      }
    }
    label237:
    label242:
    label262:
    label270:
    for (paramInt1 = n - i3 - ((LayoutParams)localObject).mHorizontalSpacing;; paramInt1 = n + i3 + ((LayoutParams)localObject).mHorizontalSpacing)
    {
      n = ((LayoutParams)localObject).mHorizontalSpacing;
      paramInt2 += 1;
      i = j;
      paramInt4 = paramInt1;
      paramInt3 = k;
      m = n;
      break label42;
      paramInt4 = getPaddingLeft();
      break;
      paramInt1 = 0;
      break label119;
      if (paramInt4 + i3 > i2)
      {
        paramInt1 = 1;
        break label119;
      }
      paramInt1 = 0;
      break label119;
      paramInt1 = getPaddingLeft();
      break label165;
    }
    label285:
    if (i < i1) {
      layoutChildrenOnRow(i, i1, i2, paramInt4, paramInt3, m);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i4 = View.MeasureSpec.getSize(paramInt1) - getPaddingLeft() - getPaddingRight();
    int i5 = getChildCount();
    int i3 = View.MeasureSpec.getSize(paramInt2) - getPaddingTop() - getPaddingBottom();
    int j = 0;
    int i = getPaddingLeft();
    paramInt1 = getPaddingTop();
    if (View.MeasureSpec.getMode(paramInt2) == Integer.MIN_VALUE) {}
    for (int k = View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE);; k = View.MeasureSpec.makeMeasureSpec(0, 0))
    {
      int m = 0;
      while (m < i5)
      {
        View localView = getChildAt(m);
        int i2 = j;
        int i1 = i;
        int n = paramInt1;
        if (localView.getVisibility() != 8)
        {
          LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
          localView.measure(View.MeasureSpec.makeMeasureSpec(i4, Integer.MIN_VALUE), k);
          i1 = localView.getMeasuredWidth();
          i2 = Math.max(j, localView.getMeasuredHeight() + localLayoutParams.mVerticalSpacing);
          n = i;
          j = paramInt1;
          if (i + i1 > i4)
          {
            n = getPaddingLeft();
            j = paramInt1 + i2;
          }
          i1 = n + (localLayoutParams.mHorizontalSpacing + i1);
          n = j;
        }
        m += 1;
        j = i2;
        i = i1;
        paramInt1 = n;
      }
    }
    this.mLineHeight = j;
    if (View.MeasureSpec.getMode(paramInt2) == 0) {
      i = paramInt1 + j;
    }
    for (;;)
    {
      setMeasuredDimension(i4, i);
      return;
      i = i3;
      if (View.MeasureSpec.getMode(paramInt2) == Integer.MIN_VALUE)
      {
        i = i3;
        if (paramInt1 + j < i3) {
          i = paramInt1 + j;
        }
      }
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.LayoutParams
  {
    public final int mHorizontalSpacing;
    public final int mVerticalSpacing;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(0);
      this.mHorizontalSpacing = paramInt1;
      this.mVerticalSpacing = paramInt2;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
      this.mHorizontalSpacing = paramInt1;
      this.mVerticalSpacing = paramInt2;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\KeyboardShortcutKeysLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */