package com.android.systemui.statusbar.phone;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import java.util.ArrayList;

public class ButtonDispatcher
{
  private Integer mAlpha;
  private View.OnClickListener mClickListener;
  private View mCurrentView;
  private final int mId;
  private Drawable mImageDrawable;
  private int mImageResource = -1;
  private View.OnLongClickListener mLongClickListener;
  private Boolean mLongClickable;
  private View.OnTouchListener mTouchListener;
  private final ArrayList<View> mViews = new ArrayList();
  private Integer mVisibility = Integer.valueOf(-1);
  
  public ButtonDispatcher(int paramInt)
  {
    this.mId = paramInt;
  }
  
  public void abortCurrentGesture()
  {
    int j = this.mViews.size();
    int i = 0;
    while (i < j)
    {
      ((ButtonInterface)this.mViews.get(i)).abortCurrentGesture();
      i += 1;
    }
  }
  
  void addView(View paramView)
  {
    this.mViews.add(paramView);
    paramView.setOnClickListener(this.mClickListener);
    paramView.setOnTouchListener(this.mTouchListener);
    paramView.setOnLongClickListener(this.mLongClickListener);
    if (this.mLongClickable != null) {
      paramView.setLongClickable(this.mLongClickable.booleanValue());
    }
    if (this.mAlpha != null) {
      paramView.setAlpha(this.mAlpha.intValue());
    }
    if (this.mVisibility != null) {
      paramView.setVisibility(this.mVisibility.intValue());
    }
    if (this.mImageResource > 0) {
      ((ButtonInterface)paramView).setImageResource(this.mImageResource);
    }
    while (this.mImageDrawable == null) {
      return;
    }
    ((ButtonInterface)paramView).setImageDrawable(this.mImageDrawable);
  }
  
  void addView(View paramView, boolean paramBoolean)
  {
    addView(paramView);
    if ((paramView instanceof ButtonInterface)) {
      ((ButtonInterface)paramView).setLandscape(paramBoolean);
    }
  }
  
  void clear()
  {
    this.mViews.clear();
  }
  
  public float getAlpha()
  {
    if (this.mAlpha != null) {}
    for (int i = this.mAlpha.intValue();; i = 1) {
      return i;
    }
  }
  
  public View getCurrentView()
  {
    return this.mCurrentView;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public ArrayList<View> getViews()
  {
    return this.mViews;
  }
  
  public int getVisibility()
  {
    if (this.mVisibility != null) {
      return this.mVisibility.intValue();
    }
    return 0;
  }
  
  public void setAlpha(int paramInt)
  {
    this.mAlpha = Integer.valueOf(paramInt);
    int j = this.mViews.size();
    int i = 0;
    while (i < j)
    {
      ((View)this.mViews.get(i)).setAlpha(paramInt);
      i += 1;
    }
  }
  
  public void setCarMode(boolean paramBoolean)
  {
    int j = this.mViews.size();
    int i = 0;
    while (i < j)
    {
      View localView = (View)this.mViews.get(i);
      if ((localView instanceof ButtonInterface)) {
        ((ButtonInterface)localView).setCarMode(paramBoolean);
      }
      i += 1;
    }
  }
  
  public void setCurrentView(View paramView)
  {
    this.mCurrentView = paramView.findViewById(this.mId);
  }
  
  public void setImageDrawable(Drawable paramDrawable)
  {
    this.mImageDrawable = paramDrawable;
    this.mImageResource = -1;
    int j = this.mViews.size();
    int i = 0;
    while (i < j)
    {
      ((ButtonInterface)this.mViews.get(i)).setImageDrawable(this.mImageDrawable);
      i += 1;
    }
  }
  
  public void setImageResource(int paramInt)
  {
    this.mImageResource = paramInt;
    this.mImageDrawable = null;
    int i = this.mViews.size();
    paramInt = 0;
    while (paramInt < i)
    {
      ((ButtonInterface)this.mViews.get(paramInt)).setImageResource(this.mImageResource);
      paramInt += 1;
    }
  }
  
  public void setLongClickable(boolean paramBoolean)
  {
    this.mLongClickable = Boolean.valueOf(paramBoolean);
    int j = this.mViews.size();
    int i = 0;
    while (i < j)
    {
      ((View)this.mViews.get(i)).setLongClickable(this.mLongClickable.booleanValue());
      i += 1;
    }
  }
  
  public void setOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mClickListener = paramOnClickListener;
    int j = this.mViews.size();
    int i = 0;
    while (i < j)
    {
      ((View)this.mViews.get(i)).setOnClickListener(this.mClickListener);
      i += 1;
    }
  }
  
  public void setOnLongClickListener(View.OnLongClickListener paramOnLongClickListener)
  {
    this.mLongClickListener = paramOnLongClickListener;
    int j = this.mViews.size();
    int i = 0;
    while (i < j)
    {
      ((View)this.mViews.get(i)).setOnLongClickListener(this.mLongClickListener);
      i += 1;
    }
  }
  
  public void setOnTouchListener(View.OnTouchListener paramOnTouchListener)
  {
    this.mTouchListener = paramOnTouchListener;
    int j = this.mViews.size();
    int i = 0;
    while (i < j)
    {
      ((View)this.mViews.get(i)).setOnTouchListener(this.mTouchListener);
      i += 1;
    }
  }
  
  public void setVisibility(int paramInt)
  {
    if (this.mVisibility.intValue() == paramInt) {
      return;
    }
    this.mVisibility = Integer.valueOf(paramInt);
    int i = this.mViews.size();
    paramInt = 0;
    while (paramInt < i)
    {
      ((View)this.mViews.get(paramInt)).setVisibility(this.mVisibility.intValue());
      paramInt += 1;
    }
  }
  
  public static abstract interface ButtonInterface
  {
    public abstract void abortCurrentGesture();
    
    public abstract void setCarMode(boolean paramBoolean);
    
    public abstract void setImageDrawable(Drawable paramDrawable);
    
    public abstract void setImageResource(int paramInt);
    
    public abstract void setLandscape(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\ButtonDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */