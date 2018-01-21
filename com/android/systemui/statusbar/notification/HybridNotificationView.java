package com.android.systemui.statusbar.notification;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.keyguard.AlphaOptimizedLinearLayout;
import com.android.systemui.ViewInvertHelper;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation;

public class HybridNotificationView
  extends AlphaOptimizedLinearLayout
  implements TransformableView
{
  private ViewInvertHelper mInvertHelper;
  protected TextView mTextView;
  protected TextView mTitleView;
  private ViewTransformationHelper mTransformationHelper;
  
  public HybridNotificationView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public HybridNotificationView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public HybridNotificationView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public HybridNotificationView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public void bind(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    this.mTitleView.setText(paramCharSequence1);
    TextView localTextView = this.mTitleView;
    int i;
    if (TextUtils.isEmpty(paramCharSequence1))
    {
      i = 8;
      localTextView.setVisibility(i);
      if (!TextUtils.isEmpty(paramCharSequence2)) {
        break label64;
      }
      this.mTextView.setVisibility(8);
      this.mTextView.setText(null);
    }
    for (;;)
    {
      requestLayout();
      return;
      i = 0;
      break;
      label64:
      this.mTextView.setVisibility(0);
      this.mTextView.setText(paramCharSequence2.toString());
    }
  }
  
  public TransformState getCurrentState(int paramInt)
  {
    return this.mTransformationHelper.getCurrentState(paramInt);
  }
  
  public TextView getTextView()
  {
    return this.mTextView;
  }
  
  public TextView getTitleView()
  {
    return this.mTitleView;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mTitleView = ((TextView)findViewById(2131951842));
    this.mTextView = ((TextView)findViewById(2131951843));
    this.mInvertHelper = new ViewInvertHelper(this, 700L);
    this.mTransformationHelper = new ViewTransformationHelper();
    this.mTransformationHelper.setCustomTransformation(new ViewTransformationHelper.CustomTransformation()
    {
      public boolean transformFrom(TransformState paramAnonymousTransformState, TransformableView paramAnonymousTransformableView, float paramAnonymousFloat)
      {
        paramAnonymousTransformableView = paramAnonymousTransformableView.getCurrentState(1);
        CrossFadeHelper.fadeIn(HybridNotificationView.this.mTextView, paramAnonymousFloat);
        if (paramAnonymousTransformableView != null)
        {
          paramAnonymousTransformState.transformViewVerticalFrom(paramAnonymousTransformableView, paramAnonymousFloat);
          paramAnonymousTransformableView.recycle();
        }
        return true;
      }
      
      public boolean transformTo(TransformState paramAnonymousTransformState, TransformableView paramAnonymousTransformableView, float paramAnonymousFloat)
      {
        paramAnonymousTransformableView = paramAnonymousTransformableView.getCurrentState(1);
        CrossFadeHelper.fadeOut(HybridNotificationView.this.mTextView, paramAnonymousFloat);
        if (paramAnonymousTransformableView != null)
        {
          paramAnonymousTransformState.transformViewVerticalTo(paramAnonymousTransformableView, paramAnonymousFloat);
          paramAnonymousTransformableView.recycle();
        }
        return true;
      }
    }, 2);
    this.mTransformationHelper.addTransformedView(1, this.mTitleView);
    this.mTransformationHelper.addTransformedView(2, this.mTextView);
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    this.mInvertHelper.setInverted(paramBoolean1, paramBoolean2, paramLong);
  }
  
  public void setVisible(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      setVisibility(i);
      this.mTransformationHelper.setVisible(paramBoolean);
      return;
    }
  }
  
  public void transformFrom(TransformableView paramTransformableView)
  {
    this.mTransformationHelper.transformFrom(paramTransformableView);
  }
  
  public void transformFrom(TransformableView paramTransformableView, float paramFloat)
  {
    this.mTransformationHelper.transformFrom(paramTransformableView, paramFloat);
  }
  
  public void transformTo(TransformableView paramTransformableView, float paramFloat)
  {
    this.mTransformationHelper.transformTo(paramTransformableView, paramFloat);
  }
  
  public void transformTo(TransformableView paramTransformableView, Runnable paramRunnable)
  {
    this.mTransformationHelper.transformTo(paramTransformableView, paramRunnable);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\HybridNotificationView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */