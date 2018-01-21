package com.android.systemui;

import android.graphics.Rect;
import android.view.Display;
import java.util.List;

public abstract interface RecentsComponent
{
  public abstract void cancelPreloadingRecents();
  
  public abstract boolean dockTopTask(int paramInt1, int paramInt2, Rect paramRect, int paramInt3);
  
  public abstract List<String> getLockedPackageList();
  
  public abstract void hideRecents(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void onDraggingInRecents(float paramFloat);
  
  public abstract void onDraggingInRecentsEnded(float paramFloat);
  
  public abstract void preloadRecents();
  
  public abstract void showNextAffiliatedTask();
  
  public abstract void showPrevAffiliatedTask();
  
  public abstract void showRecents(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void toggleRecents(Display paramDisplay);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\RecentsComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */