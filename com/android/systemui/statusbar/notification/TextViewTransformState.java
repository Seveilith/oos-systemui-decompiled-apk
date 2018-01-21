package com.android.systemui.statusbar.notification;

import android.text.Layout;
import android.text.TextUtils;
import android.util.Pools.SimplePool;
import android.view.View;
import android.widget.TextView;

public class TextViewTransformState
  extends TransformState
{
  private static Pools.SimplePool<TextViewTransformState> sInstancePool = new Pools.SimplePool(40);
  private TextView mText;
  
  private int getEllipsisCount()
  {
    Layout localLayout = this.mText.getLayout();
    if ((localLayout != null) && (localLayout.getLineCount() > 0)) {
      return localLayout.getEllipsisCount(0);
    }
    return 0;
  }
  
  private int getInnerHeight(TextView paramTextView)
  {
    return paramTextView.getHeight() - paramTextView.getPaddingTop() - paramTextView.getPaddingBottom();
  }
  
  public static TextViewTransformState obtain()
  {
    TextViewTransformState localTextViewTransformState = (TextViewTransformState)sInstancePool.acquire();
    if (localTextViewTransformState != null) {
      return localTextViewTransformState;
    }
    return new TextViewTransformState();
  }
  
  public void initFrom(View paramView)
  {
    super.initFrom(paramView);
    if ((paramView instanceof TextView)) {
      this.mText = ((TextView)paramView);
    }
  }
  
  public void recycle()
  {
    super.recycle();
    sInstancePool.release(this);
  }
  
  protected void reset()
  {
    super.reset();
    this.mText = null;
  }
  
  protected boolean sameAs(TransformState paramTransformState)
  {
    boolean bool2 = false;
    if ((paramTransformState instanceof TextViewTransformState))
    {
      TextViewTransformState localTextViewTransformState = (TextViewTransformState)paramTransformState;
      if (TextUtils.equals(localTextViewTransformState.mText.getText(), this.mText.getText()))
      {
        boolean bool1 = bool2;
        if (getEllipsisCount() == localTextViewTransformState.getEllipsisCount())
        {
          bool1 = bool2;
          if (getInnerHeight(this.mText) == getInnerHeight(localTextViewTransformState.mText)) {
            bool1 = true;
          }
        }
        return bool1;
      }
    }
    return super.sameAs(paramTransformState);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\TextViewTransformState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */