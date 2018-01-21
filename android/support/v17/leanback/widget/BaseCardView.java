package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.integer;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.util.ArrayList;

public class BaseCardView
  extends FrameLayout
{
  private static final int[] LB_PRESSED_STATE_SET = { 16842919 };
  private final int mActivatedAnimDuration;
  private Animation mAnim;
  private final Runnable mAnimationTrigger = new Runnable()
  {
    public void run()
    {
      BaseCardView.this.animateInfoOffset(true);
    }
  };
  private int mCardType;
  private boolean mDelaySelectedAnim;
  ArrayList<View> mExtraViewList;
  private int mExtraVisibility;
  float mInfoAlpha = 1.0F;
  float mInfoOffset;
  ArrayList<View> mInfoViewList;
  float mInfoVisFraction;
  private int mInfoVisibility;
  private ArrayList<View> mMainViewList;
  private int mMeasuredHeight;
  private int mMeasuredWidth;
  private final int mSelectedAnimDuration;
  private int mSelectedAnimationDelay;
  
  public BaseCardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.baseCardViewStyle);
  }
  
  public BaseCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbBaseCardView, paramInt, 0);
    try
    {
      this.mCardType = paramContext.getInteger(R.styleable.lbBaseCardView_cardType, 0);
      paramAttributeSet = paramContext.getDrawable(R.styleable.lbBaseCardView_cardForeground);
      if (paramAttributeSet != null) {
        setForeground(paramAttributeSet);
      }
      paramAttributeSet = paramContext.getDrawable(R.styleable.lbBaseCardView_cardBackground);
      if (paramAttributeSet != null) {
        setBackground(paramAttributeSet);
      }
      this.mInfoVisibility = paramContext.getInteger(R.styleable.lbBaseCardView_infoVisibility, 1);
      this.mExtraVisibility = paramContext.getInteger(R.styleable.lbBaseCardView_extraVisibility, 2);
      if (this.mExtraVisibility < this.mInfoVisibility) {
        this.mExtraVisibility = this.mInfoVisibility;
      }
      this.mSelectedAnimationDelay = paramContext.getInteger(R.styleable.lbBaseCardView_selectedAnimationDelay, getResources().getInteger(R.integer.lb_card_selected_animation_delay));
      this.mSelectedAnimDuration = paramContext.getInteger(R.styleable.lbBaseCardView_selectedAnimationDuration, getResources().getInteger(R.integer.lb_card_selected_animation_duration));
      this.mActivatedAnimDuration = paramContext.getInteger(R.styleable.lbBaseCardView_activatedAnimationDuration, getResources().getInteger(R.integer.lb_card_activated_animation_duration));
      paramContext.recycle();
      this.mDelaySelectedAnim = true;
      this.mMainViewList = new ArrayList();
      this.mInfoViewList = new ArrayList();
      this.mExtraViewList = new ArrayList();
      this.mInfoOffset = 0.0F;
      this.mInfoVisFraction = 0.0F;
      return;
    }
    finally
    {
      paramContext.recycle();
    }
  }
  
  private void animateInfoAlpha(boolean paramBoolean)
  {
    cancelAnimations();
    if (paramBoolean)
    {
      int i = 0;
      while (i < this.mInfoViewList.size())
      {
        ((View)this.mInfoViewList.get(i)).setVisibility(0);
        i += 1;
      }
    }
    float f2 = this.mInfoAlpha;
    if (paramBoolean) {}
    for (float f1 = 1.0F;; f1 = 0.0F)
    {
      this.mAnim = new InfoAlphaAnimation(f2, f1);
      this.mAnim.setDuration(this.mActivatedAnimDuration);
      this.mAnim.setInterpolator(new DecelerateInterpolator());
      this.mAnim.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          if (BaseCardView.this.mInfoAlpha == 0.0D)
          {
            int i = 0;
            while (i < BaseCardView.this.mInfoViewList.size())
            {
              ((View)BaseCardView.this.mInfoViewList.get(i)).setVisibility(8);
              i += 1;
            }
          }
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation) {}
      });
      startAnimation(this.mAnim);
      return;
    }
  }
  
  private void animateInfoHeight(boolean paramBoolean)
  {
    cancelAnimations();
    int j = 0;
    if (paramBoolean)
    {
      int k = View.MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
      int m = View.MeasureSpec.makeMeasureSpec(0, 0);
      int i = 0;
      while (i < this.mExtraViewList.size())
      {
        View localView = (View)this.mExtraViewList.get(i);
        localView.setVisibility(0);
        localView.measure(k, m);
        j = Math.max(j, localView.getMeasuredHeight());
        i += 1;
      }
    }
    float f2 = this.mInfoVisFraction;
    if (paramBoolean) {}
    for (float f1 = 1.0F;; f1 = 0.0F)
    {
      this.mAnim = new InfoHeightAnimation(f2, f1);
      this.mAnim.setDuration(this.mSelectedAnimDuration);
      this.mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
      this.mAnim.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          if (BaseCardView.this.mInfoOffset == 0.0F)
          {
            int i = 0;
            while (i < BaseCardView.this.mExtraViewList.size())
            {
              ((View)BaseCardView.this.mExtraViewList.get(i)).setVisibility(8);
              i += 1;
            }
          }
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation) {}
      });
      startAnimation(this.mAnim);
      return;
    }
  }
  
  private void applyActiveState(boolean paramBoolean)
  {
    if ((hasInfoRegion()) && (this.mInfoVisibility <= 1)) {
      setInfoViewVisibility(paramBoolean);
    }
    if ((hasExtraRegion()) && (this.mExtraVisibility <= 1)) {}
  }
  
  private void applySelectedState(boolean paramBoolean)
  {
    removeCallbacks(this.mAnimationTrigger);
    if (this.mCardType == 3) {
      if (paramBoolean) {
        if (!this.mDelaySelectedAnim)
        {
          post(this.mAnimationTrigger);
          this.mDelaySelectedAnim = true;
        }
      }
    }
    while (this.mInfoVisibility != 2)
    {
      return;
      postDelayed(this.mAnimationTrigger, this.mSelectedAnimationDelay);
      return;
      animateInfoOffset(false);
      return;
    }
    setInfoViewVisibility(paramBoolean);
  }
  
  private void cancelAnimations()
  {
    if (this.mAnim != null)
    {
      this.mAnim.cancel();
      this.mAnim = null;
    }
  }
  
  private void findChildrenViews()
  {
    this.mMainViewList.clear();
    this.mInfoViewList.clear();
    this.mExtraViewList.clear();
    int m = getChildCount();
    boolean bool2 = isRegionVisible(this.mInfoVisibility);
    int i;
    boolean bool1;
    label96:
    int j;
    label98:
    View localView;
    if ((hasExtraRegion()) && (this.mInfoOffset > 0.0F))
    {
      i = 1;
      bool1 = bool2;
      if (this.mCardType == 2)
      {
        bool1 = bool2;
        if (this.mInfoVisibility == 2)
        {
          if ((!bool2) || (this.mInfoVisFraction <= 0.0F)) {
            break label128;
          }
          bool1 = true;
        }
      }
      j = 0;
      if (j >= m) {
        return;
      }
      localView = getChildAt(j);
      if (localView != null) {
        break label134;
      }
    }
    for (;;)
    {
      j += 1;
      break label98;
      i = 0;
      break;
      label128:
      bool1 = false;
      break label96;
      label134:
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      int k;
      if (localLayoutParams.viewType == 1)
      {
        this.mInfoViewList.add(localView);
        if (bool1) {}
        for (k = 0;; k = 8)
        {
          localView.setVisibility(k);
          break;
        }
      }
      if (localLayoutParams.viewType == 2)
      {
        this.mExtraViewList.add(localView);
        if (i != 0) {}
        for (k = 0;; k = 8)
        {
          localView.setVisibility(k);
          break;
        }
      }
      this.mMainViewList.add(localView);
      localView.setVisibility(0);
    }
  }
  
  private boolean hasExtraRegion()
  {
    return this.mCardType == 3;
  }
  
  private boolean hasInfoRegion()
  {
    boolean bool = false;
    if (this.mCardType != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isRegionVisible(int paramInt)
  {
    boolean bool = false;
    switch (paramInt)
    {
    default: 
      return false;
    case 0: 
      return true;
    case 1: 
      return isActivated();
    }
    if (isActivated()) {
      bool = isSelected();
    }
    return bool;
  }
  
  private void setInfoViewVisibility(boolean paramBoolean)
  {
    int i;
    if (this.mCardType == 3)
    {
      if (paramBoolean)
      {
        i = 0;
        while (i < this.mInfoViewList.size())
        {
          ((View)this.mInfoViewList.get(i)).setVisibility(0);
          i += 1;
        }
      }
      i = 0;
      while (i < this.mInfoViewList.size())
      {
        ((View)this.mInfoViewList.get(i)).setVisibility(8);
        i += 1;
      }
      i = 0;
      while (i < this.mExtraViewList.size())
      {
        ((View)this.mExtraViewList.get(i)).setVisibility(8);
        i += 1;
      }
      this.mInfoOffset = 0.0F;
    }
    label149:
    label192:
    do
    {
      return;
      if (this.mCardType == 2)
      {
        if (this.mInfoVisibility == 2)
        {
          animateInfoHeight(paramBoolean);
          return;
        }
        i = 0;
        View localView;
        if (i < this.mInfoViewList.size())
        {
          localView = (View)this.mInfoViewList.get(i);
          if (!paramBoolean) {
            break label192;
          }
        }
        for (int j = 0;; j = 8)
        {
          localView.setVisibility(j);
          i += 1;
          break label149;
          break;
        }
      }
    } while (this.mCardType != 1);
    animateInfoAlpha(paramBoolean);
  }
  
  void animateInfoOffset(boolean paramBoolean)
  {
    cancelAnimations();
    int j = 0;
    int i = 0;
    if (paramBoolean)
    {
      int m = View.MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
      int n = View.MeasureSpec.makeMeasureSpec(0, 0);
      int k = 0;
      for (;;)
      {
        j = i;
        if (k >= this.mExtraViewList.size()) {
          break;
        }
        View localView = (View)this.mExtraViewList.get(k);
        localView.setVisibility(0);
        localView.measure(m, n);
        i = Math.max(i, localView.getMeasuredHeight());
        k += 1;
      }
    }
    float f = this.mInfoOffset;
    if (paramBoolean) {}
    for (;;)
    {
      this.mAnim = new InfoOffsetAnimation(f, j);
      this.mAnim.setDuration(this.mSelectedAnimDuration);
      this.mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
      this.mAnim.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          if (BaseCardView.this.mInfoOffset == 0.0F)
          {
            int i = 0;
            while (i < BaseCardView.this.mExtraViewList.size())
            {
              ((View)BaseCardView.this.mExtraViewList.get(i)).setVisibility(8);
              i += 1;
            }
          }
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation) {}
      });
      startAnimation(this.mAnim);
      return;
      j = 0;
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt);
    int k = arrayOfInt.length;
    int j = 0;
    int i = 0;
    paramInt = 0;
    while (paramInt < k)
    {
      if (arrayOfInt[paramInt] == 16842919) {
        j = 1;
      }
      if (arrayOfInt[paramInt] == 16842910) {
        i = 1;
      }
      paramInt += 1;
    }
    if ((j != 0) && (i != 0)) {
      return View.PRESSED_ENABLED_STATE_SET;
    }
    if (j != 0) {
      return LB_PRESSED_STATE_SET;
    }
    if (i != 0) {
      return View.ENABLED_STATE_SET;
    }
    return View.EMPTY_STATE_SET;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    removeCallbacks(this.mAnimationTrigger);
    cancelAnimations();
    this.mInfoOffset = 0.0F;
    this.mInfoVisFraction = 0.0F;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    float f2 = getPaddingTop();
    int i = 0;
    View localView;
    float f1;
    while (i < this.mMainViewList.size())
    {
      localView = (View)this.mMainViewList.get(i);
      f1 = f2;
      if (localView.getVisibility() != 8)
      {
        localView.layout(getPaddingLeft(), (int)f2, this.mMeasuredWidth + getPaddingLeft(), (int)(localView.getMeasuredHeight() + f2));
        f1 = f2 + localView.getMeasuredHeight();
      }
      i += 1;
      f2 = f1;
    }
    if (hasInfoRegion())
    {
      float f3 = 0.0F;
      i = 0;
      while (i < this.mInfoViewList.size())
      {
        f3 += ((View)this.mInfoViewList.get(i)).getMeasuredHeight();
        i += 1;
      }
      float f4;
      if (this.mCardType == 1)
      {
        f2 -= f3;
        f1 = f2;
        f4 = f3;
        if (f2 < 0.0F)
        {
          f1 = 0.0F;
          f4 = f3;
        }
        i = 0;
      }
      for (;;)
      {
        f2 = f1;
        if (i < this.mInfoViewList.size())
        {
          localView = (View)this.mInfoViewList.get(i);
          f2 = f1;
          f3 = f4;
          if (localView.getVisibility() != 8)
          {
            int k = localView.getMeasuredHeight();
            int j = k;
            if (k > f4) {
              j = (int)f4;
            }
            localView.layout(getPaddingLeft(), (int)f1, this.mMeasuredWidth + getPaddingLeft(), (int)(j + f1));
            f1 += j;
            f4 -= j;
            f2 = f1;
            f3 = f4;
            if (f4 <= 0.0F) {
              f2 = f1;
            }
          }
        }
        else
        {
          if (!hasExtraRegion()) {
            break label514;
          }
          i = 0;
          while (i < this.mExtraViewList.size())
          {
            localView = (View)this.mExtraViewList.get(i);
            f1 = f2;
            if (localView.getVisibility() != 8)
            {
              localView.layout(getPaddingLeft(), (int)f2, this.mMeasuredWidth + getPaddingLeft(), (int)(localView.getMeasuredHeight() + f2));
              f1 = f2 + localView.getMeasuredHeight();
            }
            i += 1;
            f2 = f1;
          }
          if (this.mCardType == 2)
          {
            f1 = f2;
            f4 = f3;
            if (this.mInfoVisibility != 2) {
              break;
            }
            f4 = f3 * this.mInfoVisFraction;
            f1 = f2;
            break;
          }
          f1 = f2 - this.mInfoOffset;
          f4 = f3;
          break;
        }
        i += 1;
        f1 = f2;
        f4 = f3;
      }
    }
    label514:
    onSizeChanged(0, 0, paramInt3 - paramInt1, paramInt4 - paramInt2);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.mMeasuredWidth = 0;
    this.mMeasuredHeight = 0;
    int i = 0;
    int n = 0;
    int i2 = 0;
    int i5 = 0;
    int i3 = 0;
    int i4 = 0;
    findChildrenViews();
    int i6 = View.MeasureSpec.makeMeasureSpec(0, 0);
    int j = 0;
    View localView;
    int m;
    while (j < this.mMainViewList.size())
    {
      localView = (View)this.mMainViewList.get(j);
      m = n;
      k = i;
      if (localView.getVisibility() != 8)
      {
        measureChild(localView, i6, i6);
        this.mMeasuredWidth = Math.max(this.mMeasuredWidth, localView.getMeasuredWidth());
        m = n + localView.getMeasuredHeight();
        k = View.combineMeasuredStates(i, localView.getMeasuredState());
      }
      j += 1;
      n = m;
      i = k;
    }
    setPivotX(this.mMeasuredWidth / 2);
    setPivotY(n / 2);
    int i7 = View.MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
    int i1 = i3;
    int k = i;
    if (hasInfoRegion())
    {
      m = 0;
      j = i;
      i = i5;
      while (m < this.mInfoViewList.size())
      {
        localView = (View)this.mInfoViewList.get(m);
        k = i;
        i1 = j;
        if (localView.getVisibility() != 8)
        {
          measureChild(localView, i7, i6);
          k = i;
          if (this.mCardType != 1) {
            k = i + localView.getMeasuredHeight();
          }
          i1 = View.combineMeasuredStates(j, localView.getMeasuredState());
        }
        m += 1;
        i = k;
        j = i1;
      }
      i1 = i3;
      i2 = i;
      k = j;
      if (hasExtraRegion())
      {
        i3 = 0;
        m = i4;
        for (;;)
        {
          i1 = m;
          i2 = i;
          k = j;
          if (i3 >= this.mExtraViewList.size()) {
            break;
          }
          localView = (View)this.mExtraViewList.get(i3);
          i1 = m;
          k = j;
          if (localView.getVisibility() != 8)
          {
            measureChild(localView, i7, i6);
            i1 = m + localView.getMeasuredHeight();
            k = View.combineMeasuredStates(j, localView.getMeasuredState());
          }
          i3 += 1;
          m = i1;
          j = k;
        }
      }
    }
    float f3;
    float f1;
    label481:
    float f4;
    if ((hasInfoRegion()) && (this.mInfoVisibility == 2))
    {
      i = 1;
      f3 = n;
      if (i == 0) {
        break label563;
      }
      f1 = i2 * this.mInfoVisFraction;
      f4 = i1;
      if (i == 0) {
        break label570;
      }
    }
    label563:
    label570:
    for (float f2 = 0.0F;; f2 = this.mInfoOffset)
    {
      this.mMeasuredHeight = ((int)(f4 + (f1 + f3) - f2));
      setMeasuredDimension(View.resolveSizeAndState(this.mMeasuredWidth + getPaddingLeft() + getPaddingRight(), paramInt1, k), View.resolveSizeAndState(this.mMeasuredHeight + getPaddingTop() + getPaddingBottom(), paramInt2, k << 16));
      return;
      i = 0;
      break;
      f1 = i2;
      break label481;
    }
  }
  
  public void setActivated(boolean paramBoolean)
  {
    if (paramBoolean != isActivated())
    {
      super.setActivated(paramBoolean);
      applyActiveState(isActivated());
    }
  }
  
  public void setSelected(boolean paramBoolean)
  {
    if (paramBoolean != isSelected())
    {
      super.setSelected(paramBoolean);
      applySelectedState(isSelected());
    }
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  public String toString()
  {
    return super.toString();
  }
  
  private class InfoAlphaAnimation
    extends Animation
  {
    private float mDelta;
    private float mStartValue;
    
    public InfoAlphaAnimation(float paramFloat1, float paramFloat2)
    {
      this.mStartValue = paramFloat1;
      this.mDelta = (paramFloat2 - paramFloat1);
    }
    
    protected void applyTransformation(float paramFloat, Transformation paramTransformation)
    {
      BaseCardView.this.mInfoAlpha = (this.mStartValue + this.mDelta * paramFloat);
      int i = 0;
      while (i < BaseCardView.this.mInfoViewList.size())
      {
        ((View)BaseCardView.this.mInfoViewList.get(i)).setAlpha(BaseCardView.this.mInfoAlpha);
        i += 1;
      }
    }
  }
  
  private class InfoHeightAnimation
    extends Animation
  {
    private float mDelta;
    private float mStartValue;
    
    public InfoHeightAnimation(float paramFloat1, float paramFloat2)
    {
      this.mStartValue = paramFloat1;
      this.mDelta = (paramFloat2 - paramFloat1);
    }
    
    protected void applyTransformation(float paramFloat, Transformation paramTransformation)
    {
      BaseCardView.this.mInfoVisFraction = (this.mStartValue + this.mDelta * paramFloat);
      BaseCardView.this.requestLayout();
    }
  }
  
  private class InfoOffsetAnimation
    extends Animation
  {
    private float mDelta;
    private float mStartValue;
    
    public InfoOffsetAnimation(float paramFloat1, float paramFloat2)
    {
      this.mStartValue = paramFloat1;
      this.mDelta = (paramFloat2 - paramFloat1);
    }
    
    protected void applyTransformation(float paramFloat, Transformation paramTransformation)
    {
      BaseCardView.this.mInfoOffset = (this.mStartValue + this.mDelta * paramFloat);
      BaseCardView.this.requestLayout();
    }
  }
  
  public static class LayoutParams
    extends FrameLayout.LayoutParams
  {
    @ViewDebug.ExportedProperty(category="layout", mapping={@android.view.ViewDebug.IntToString(from=0, to="MAIN"), @android.view.ViewDebug.IntToString(from=1, to="INFO"), @android.view.ViewDebug.IntToString(from=2, to="EXTRA")})
    public int viewType = 0;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbBaseCardView_Layout);
      this.viewType = paramContext.getInt(R.styleable.lbBaseCardView_Layout_layout_viewType, 0);
      paramContext.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.viewType = paramLayoutParams.viewType;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\BaseCardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */