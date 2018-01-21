package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.internal.view.RotationPolicy;
import com.android.internal.view.RotationPolicy.RotationPolicyListener;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RotationLockControllerImpl
  implements RotationLockController
{
  private final CopyOnWriteArrayList<RotationLockController.RotationLockControllerCallback> mCallbacks = new CopyOnWriteArrayList();
  private final Context mContext;
  private final RotationPolicy.RotationPolicyListener mRotationPolicyListener = new RotationPolicy.RotationPolicyListener()
  {
    public void onChange()
    {
      RotationLockControllerImpl.-wrap0(RotationLockControllerImpl.this);
    }
  };
  
  public RotationLockControllerImpl(Context paramContext)
  {
    this.mContext = paramContext;
    setListening(true);
  }
  
  private void notifyChanged()
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      notifyChanged((RotationLockController.RotationLockControllerCallback)localIterator.next());
    }
  }
  
  private void notifyChanged(RotationLockController.RotationLockControllerCallback paramRotationLockControllerCallback)
  {
    paramRotationLockControllerCallback.onRotationLockStateChanged(RotationPolicy.isRotationLocked(this.mContext), RotationPolicy.isRotationLockToggleVisible(this.mContext));
  }
  
  public void addRotationLockControllerCallback(RotationLockController.RotationLockControllerCallback paramRotationLockControllerCallback)
  {
    this.mCallbacks.add(paramRotationLockControllerCallback);
    notifyChanged(paramRotationLockControllerCallback);
  }
  
  public int getRotationLockOrientation()
  {
    return RotationPolicy.getRotationLockOrientation(this.mContext);
  }
  
  public boolean isRotationLocked()
  {
    return RotationPolicy.isRotationLocked(this.mContext);
  }
  
  public void removeRotationLockControllerCallback(RotationLockController.RotationLockControllerCallback paramRotationLockControllerCallback)
  {
    this.mCallbacks.remove(paramRotationLockControllerCallback);
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      RotationPolicy.registerRotationPolicyListener(this.mContext, this.mRotationPolicyListener, -1);
      return;
    }
    RotationPolicy.unregisterRotationPolicyListener(this.mContext, this.mRotationPolicyListener);
  }
  
  public void setRotationLocked(boolean paramBoolean)
  {
    RotationPolicy.setRotationLock(this.mContext, paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\RotationLockControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */