package com.android.systemui.statusbar.policy;

public abstract interface RotationLockController
{
  public abstract void addRotationLockControllerCallback(RotationLockControllerCallback paramRotationLockControllerCallback);
  
  public abstract int getRotationLockOrientation();
  
  public abstract boolean isRotationLocked();
  
  public abstract void removeRotationLockControllerCallback(RotationLockControllerCallback paramRotationLockControllerCallback);
  
  public abstract void setRotationLocked(boolean paramBoolean);
  
  public static abstract interface RotationLockControllerCallback
  {
    public abstract void onRotationLockStateChanged(boolean paramBoolean1, boolean paramBoolean2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\RotationLockController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */