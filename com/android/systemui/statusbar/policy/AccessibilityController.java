package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener;
import java.util.ArrayList;

public class AccessibilityController
  implements AccessibilityManager.AccessibilityStateChangeListener, AccessibilityManager.TouchExplorationStateChangeListener
{
  private boolean mAccessibilityEnabled;
  private final ArrayList<AccessibilityStateChangedCallback> mChangeCallbacks = new ArrayList();
  private boolean mTouchExplorationEnabled;
  
  public AccessibilityController(Context paramContext)
  {
    paramContext = (AccessibilityManager)paramContext.getSystemService("accessibility");
    paramContext.addTouchExplorationStateChangeListener(this);
    paramContext.addAccessibilityStateChangeListener(this);
    this.mAccessibilityEnabled = paramContext.isEnabled();
    this.mTouchExplorationEnabled = paramContext.isTouchExplorationEnabled();
  }
  
  private void fireChanged()
  {
    int j = this.mChangeCallbacks.size();
    int i = 0;
    while (i < j)
    {
      ((AccessibilityStateChangedCallback)this.mChangeCallbacks.get(i)).onStateChanged(this.mAccessibilityEnabled, this.mTouchExplorationEnabled);
      i += 1;
    }
  }
  
  public void addStateChangedCallback(AccessibilityStateChangedCallback paramAccessibilityStateChangedCallback)
  {
    this.mChangeCallbacks.add(paramAccessibilityStateChangedCallback);
    paramAccessibilityStateChangedCallback.onStateChanged(this.mAccessibilityEnabled, this.mTouchExplorationEnabled);
  }
  
  public boolean isAccessibilityEnabled()
  {
    return this.mAccessibilityEnabled;
  }
  
  public boolean isTouchExplorationEnabled()
  {
    return this.mTouchExplorationEnabled;
  }
  
  public void onAccessibilityStateChanged(boolean paramBoolean)
  {
    this.mAccessibilityEnabled = paramBoolean;
    fireChanged();
  }
  
  public void onTouchExplorationStateChanged(boolean paramBoolean)
  {
    this.mTouchExplorationEnabled = paramBoolean;
    fireChanged();
  }
  
  public static abstract interface AccessibilityStateChangedCallback
  {
    public abstract void onStateChanged(boolean paramBoolean1, boolean paramBoolean2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\AccessibilityController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */