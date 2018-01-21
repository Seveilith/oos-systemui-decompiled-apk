package android.support.v7.widget;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.layout;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class ActionBarContextView
  extends AbsActionBarView
{
  private View mClose;
  private int mCloseItemLayout;
  private View mCustomView;
  private int mSubtitleStyleRes;
  private CharSequence mTitle;
  private LinearLayout mTitleLayout;
  private boolean mTitleOptional;
  private int mTitleStyleRes;
  
  public ActionBarContextView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionBarContextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.actionModeStyle);
  }
  
  public ActionBarContextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.ActionMode, paramInt, 0);
    ViewCompat.setBackground(this, paramContext.getDrawable(R.styleable.ActionMode_background));
    this.mTitleStyleRes = paramContext.getResourceId(R.styleable.ActionMode_titleTextStyle, 0);
    this.mSubtitleStyleRes = paramContext.getResourceId(R.styleable.ActionMode_subtitleTextStyle, 0);
    this.mContentHeight = paramContext.getLayoutDimension(R.styleable.ActionMode_height, 0);
    this.mCloseItemLayout = paramContext.getResourceId(R.styleable.ActionMode_closeItemLayout, R.layout.abc_action_mode_close_item_material);
    paramContext.recycle();
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new ViewGroup.MarginLayoutParams(-1, -2);
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new ViewGroup.MarginLayoutParams(getContext(), paramAttributeSet);
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mActionMenuPresenter != null)
    {
      this.mActionMenuPresenter.hideOverflowMenu();
      this.mActionMenuPresenter.hideSubMenus();
    }
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      if (paramAccessibilityEvent.getEventType() == 32)
      {
        paramAccessibilityEvent.setSource(this);
        paramAccessibilityEvent.setClassName(getClass().getName());
        paramAccessibilityEvent.setPackageName(getContext().getPackageName());
        paramAccessibilityEvent.setContentDescription(this.mTitle);
      }
    }
    else {
      return;
    }
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramBoolean = ViewUtils.isLayoutRtl(this);
    int i;
    int j;
    int k;
    Object localObject;
    if (paramBoolean)
    {
      i = paramInt3 - paramInt1 - getPaddingRight();
      j = getPaddingTop();
      k = paramInt4 - paramInt2 - getPaddingTop() - getPaddingBottom();
      paramInt2 = i;
      if (this.mClose != null)
      {
        paramInt2 = i;
        if (this.mClose.getVisibility() != 8)
        {
          localObject = (ViewGroup.MarginLayoutParams)this.mClose.getLayoutParams();
          if (!paramBoolean) {
            break label258;
          }
          paramInt2 = ((ViewGroup.MarginLayoutParams)localObject).rightMargin;
          label89:
          if (!paramBoolean) {
            break label267;
          }
          paramInt4 = ((ViewGroup.MarginLayoutParams)localObject).leftMargin;
          label100:
          paramInt2 = next(i, paramInt2, paramBoolean);
          paramInt2 = next(paramInt2 + positionChild(this.mClose, paramInt2, j, k, paramBoolean), paramInt4, paramBoolean);
        }
      }
      paramInt4 = paramInt2;
      if (this.mTitleLayout != null)
      {
        paramInt4 = paramInt2;
        if (this.mCustomView == null)
        {
          paramInt4 = paramInt2;
          if (this.mTitleLayout.getVisibility() != 8) {
            paramInt4 = paramInt2 + positionChild(this.mTitleLayout, paramInt2, j, k, paramBoolean);
          }
        }
      }
      if (this.mCustomView != null) {
        positionChild(this.mCustomView, paramInt4, j, k, paramBoolean);
      }
      if (!paramBoolean) {
        break label277;
      }
      paramInt1 = getPaddingLeft();
      label216:
      if (this.mMenuView != null)
      {
        localObject = this.mMenuView;
        if (!paramBoolean) {
          break label290;
        }
      }
    }
    label258:
    label267:
    label277:
    label290:
    for (paramBoolean = false;; paramBoolean = true)
    {
      positionChild((View)localObject, paramInt1, j, k, paramBoolean);
      return;
      i = getPaddingLeft();
      break;
      paramInt2 = ((ViewGroup.MarginLayoutParams)localObject).leftMargin;
      break label89;
      paramInt4 = ((ViewGroup.MarginLayoutParams)localObject).rightMargin;
      break label100;
      paramInt1 = paramInt3 - paramInt1 - getPaddingRight();
      break label216;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (View.MeasureSpec.getMode(paramInt1) != 1073741824) {
      throw new IllegalStateException(getClass().getSimpleName() + " can only be used " + "with android:layout_width=\"match_parent\" (or fill_parent)");
    }
    if (View.MeasureSpec.getMode(paramInt2) == 0) {
      throw new IllegalStateException(getClass().getSimpleName() + " can only be used " + "with android:layout_height=\"wrap_content\"");
    }
    int m = View.MeasureSpec.getSize(paramInt1);
    int i;
    int n;
    int k;
    int j;
    if (this.mContentHeight > 0)
    {
      i = this.mContentHeight;
      n = getPaddingTop() + getPaddingBottom();
      paramInt1 = m - getPaddingLeft() - getPaddingRight();
      k = i - n;
      j = View.MeasureSpec.makeMeasureSpec(k, Integer.MIN_VALUE);
      paramInt2 = paramInt1;
      Object localObject;
      if (this.mClose != null)
      {
        paramInt1 = measureChildView(this.mClose, paramInt1, j, 0);
        localObject = (ViewGroup.MarginLayoutParams)this.mClose.getLayoutParams();
        paramInt2 = paramInt1 - (((ViewGroup.MarginLayoutParams)localObject).leftMargin + ((ViewGroup.MarginLayoutParams)localObject).rightMargin);
      }
      paramInt1 = paramInt2;
      if (this.mMenuView != null)
      {
        paramInt1 = paramInt2;
        if (this.mMenuView.getParent() == this) {
          paramInt1 = measureChildView(this.mMenuView, paramInt2, j, 0);
        }
      }
      paramInt2 = paramInt1;
      if (this.mTitleLayout != null)
      {
        paramInt2 = paramInt1;
        if (this.mCustomView == null)
        {
          if (!this.mTitleOptional) {
            break label503;
          }
          paramInt2 = View.MeasureSpec.makeMeasureSpec(0, 0);
          this.mTitleLayout.measure(paramInt2, j);
          int i1 = this.mTitleLayout.getMeasuredWidth();
          if (i1 > paramInt1) {
            break label491;
          }
          j = 1;
          label296:
          paramInt2 = paramInt1;
          if (j != 0) {
            paramInt2 = paramInt1 - i1;
          }
          localObject = this.mTitleLayout;
          if (j == 0) {
            break label497;
          }
          paramInt1 = 0;
          label321:
          ((LinearLayout)localObject).setVisibility(paramInt1);
        }
      }
      label327:
      if (this.mCustomView != null)
      {
        localObject = this.mCustomView.getLayoutParams();
        if (((ViewGroup.LayoutParams)localObject).width == -2) {
          break label519;
        }
        paramInt1 = 1073741824;
        label356:
        if (((ViewGroup.LayoutParams)localObject).width < 0) {
          break label526;
        }
        paramInt2 = Math.min(((ViewGroup.LayoutParams)localObject).width, paramInt2);
        label374:
        if (((ViewGroup.LayoutParams)localObject).height == -2) {
          break label529;
        }
        j = 1073741824;
        label388:
        if (((ViewGroup.LayoutParams)localObject).height < 0) {
          break label537;
        }
        k = Math.min(((ViewGroup.LayoutParams)localObject).height, k);
      }
    }
    label491:
    label497:
    label503:
    label519:
    label526:
    label529:
    label537:
    for (;;)
    {
      this.mCustomView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, paramInt1), View.MeasureSpec.makeMeasureSpec(k, j));
      if (this.mContentHeight > 0) {
        break label548;
      }
      paramInt2 = 0;
      k = getChildCount();
      paramInt1 = 0;
      while (paramInt1 < k)
      {
        j = getChildAt(paramInt1).getMeasuredHeight() + n;
        i = paramInt2;
        if (j > paramInt2) {
          i = j;
        }
        paramInt1 += 1;
        paramInt2 = i;
      }
      i = View.MeasureSpec.getSize(paramInt2);
      break;
      j = 0;
      break label296;
      paramInt1 = 8;
      break label321;
      paramInt2 = measureChildView(this.mTitleLayout, paramInt1, j, 0);
      break label327;
      paramInt1 = Integer.MIN_VALUE;
      break label356;
      break label374;
      j = Integer.MIN_VALUE;
      break label388;
    }
    setMeasuredDimension(m, paramInt2);
    return;
    label548:
    setMeasuredDimension(m, i);
  }
  
  public void setContentHeight(int paramInt)
  {
    this.mContentHeight = paramInt;
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\ActionBarContextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */