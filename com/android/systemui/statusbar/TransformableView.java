package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.TransformState;

public abstract interface TransformableView
{
  public abstract TransformState getCurrentState(int paramInt);
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract void transformFrom(TransformableView paramTransformableView);
  
  public abstract void transformFrom(TransformableView paramTransformableView, float paramFloat);
  
  public abstract void transformTo(TransformableView paramTransformableView, float paramFloat);
  
  public abstract void transformTo(TransformableView paramTransformableView, Runnable paramRunnable);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\TransformableView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */