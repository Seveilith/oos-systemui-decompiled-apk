package com.android.systemui.statusbar.stack;

import android.view.View;
import com.android.systemui.statusbar.ActivatableNotificationView;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.ArrayList;

public class AmbientState
{
  private ActivatableNotificationView mActivatedChild;
  private boolean mDark;
  private boolean mDimmed;
  private boolean mDismissAllInProgress;
  private ArrayList<View> mDraggedViews = new ArrayList();
  private HeadsUpManager mHeadsUpManager;
  private boolean mHideSensitive;
  private int mLayoutHeight;
  private int mLayoutMinHeight;
  private float mMaxHeadsUpTranslation;
  private float mOverScrollBottomAmount;
  private float mOverScrollTopAmount;
  private int mScrollY;
  private boolean mShadeExpanded;
  private int mSpeedBumpIndex = -1;
  private float mStackTranslation;
  private int mTopPadding;
  
  public ActivatableNotificationView getActivatedChild()
  {
    return this.mActivatedChild;
  }
  
  public ArrayList<View> getDraggedViews()
  {
    return this.mDraggedViews;
  }
  
  public int getInnerHeight()
  {
    return Math.max(this.mLayoutHeight - this.mTopPadding, this.mLayoutMinHeight);
  }
  
  public float getMaxHeadsUpTranslation()
  {
    return this.mMaxHeadsUpTranslation;
  }
  
  public float getOverScrollAmount(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.mOverScrollTopAmount;
    }
    return this.mOverScrollBottomAmount;
  }
  
  public int getScrollY()
  {
    return this.mScrollY;
  }
  
  public int getSpeedBumpIndex()
  {
    return this.mSpeedBumpIndex;
  }
  
  public float getStackTranslation()
  {
    return this.mStackTranslation;
  }
  
  public float getTopPadding()
  {
    return this.mTopPadding;
  }
  
  public boolean isDark()
  {
    return this.mDark;
  }
  
  public boolean isDimmed()
  {
    return this.mDimmed;
  }
  
  public boolean isHideSensitive()
  {
    return this.mHideSensitive;
  }
  
  public boolean isShadeExpanded()
  {
    return this.mShadeExpanded;
  }
  
  public void onBeginDrag(View paramView)
  {
    this.mDraggedViews.add(paramView);
  }
  
  public void onDragFinished(View paramView)
  {
    this.mDraggedViews.remove(paramView);
  }
  
  public void setActivatedChild(ActivatableNotificationView paramActivatableNotificationView)
  {
    this.mActivatedChild = paramActivatableNotificationView;
  }
  
  public void setDark(boolean paramBoolean)
  {
    this.mDark = paramBoolean;
  }
  
  public void setDimmed(boolean paramBoolean)
  {
    this.mDimmed = paramBoolean;
  }
  
  public void setDismissAllInProgress(boolean paramBoolean)
  {
    this.mDismissAllInProgress = paramBoolean;
  }
  
  public void setHeadsUpManager(HeadsUpManager paramHeadsUpManager)
  {
    this.mHeadsUpManager = paramHeadsUpManager;
  }
  
  public void setHideSensitive(boolean paramBoolean)
  {
    this.mHideSensitive = paramBoolean;
  }
  
  public void setLayoutHeight(int paramInt)
  {
    this.mLayoutHeight = paramInt;
  }
  
  public void setLayoutMinHeight(int paramInt)
  {
    this.mLayoutMinHeight = paramInt;
  }
  
  public void setMaxHeadsUpTranslation(float paramFloat)
  {
    this.mMaxHeadsUpTranslation = paramFloat;
  }
  
  public void setOverScrollAmount(float paramFloat, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mOverScrollTopAmount = paramFloat;
      return;
    }
    this.mOverScrollBottomAmount = paramFloat;
  }
  
  public void setScrollY(int paramInt)
  {
    this.mScrollY = paramInt;
  }
  
  public void setShadeExpanded(boolean paramBoolean)
  {
    this.mShadeExpanded = paramBoolean;
  }
  
  public void setSpeedBumpIndex(int paramInt)
  {
    this.mSpeedBumpIndex = paramInt;
  }
  
  public void setStackTranslation(float paramFloat)
  {
    this.mStackTranslation = paramFloat;
  }
  
  public void setTopPadding(int paramInt)
  {
    this.mTopPadding = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\AmbientState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */