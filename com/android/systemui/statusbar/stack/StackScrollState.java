package com.android.systemui.statusbar.stack;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.statusbar.DismissView;
import com.android.systemui.statusbar.EmptyShadeView;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.ExpandableView;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

public class StackScrollState
{
  private final int mClearAllTopPadding;
  private final ViewGroup mHostView;
  private WeakHashMap<ExpandableView, StackViewState> mStateMap;
  
  public StackScrollState(ViewGroup paramViewGroup)
  {
    this.mHostView = paramViewGroup;
    this.mStateMap = new WeakHashMap();
    this.mClearAllTopPadding = paramViewGroup.getContext().getResources().getDimensionPixelSize(2131755505);
  }
  
  private void resetViewState(ExpandableView paramExpandableView)
  {
    StackViewState localStackViewState2 = (StackViewState)this.mStateMap.get(paramExpandableView);
    StackViewState localStackViewState1 = localStackViewState2;
    if (localStackViewState2 == null)
    {
      localStackViewState1 = new StackViewState();
      this.mStateMap.put(paramExpandableView, localStackViewState1);
    }
    localStackViewState1.height = paramExpandableView.getIntrinsicHeight();
    if (paramExpandableView.getVisibility() == 8) {}
    for (boolean bool = true;; bool = false)
    {
      localStackViewState1.gone = bool;
      localStackViewState1.alpha = 1.0F;
      localStackViewState1.shadowAlpha = 1.0F;
      localStackViewState1.notGoneIndex = -1;
      localStackViewState1.hidden = false;
      return;
    }
  }
  
  public void apply()
  {
    int k = this.mHostView.getChildCount();
    int i = 0;
    if (i < k)
    {
      Object localObject = (ExpandableView)this.mHostView.getChildAt(i);
      StackViewState localStackViewState = (StackViewState)this.mStateMap.get(localObject);
      if (!applyState((ExpandableView)localObject, localStackViewState)) {}
      int j;
      label89:
      label119:
      do
      {
        i += 1;
        break;
        if ((localObject instanceof DismissView))
        {
          localObject = (DismissView)localObject;
          if (localStackViewState.clipTopAmount < this.mClearAllTopPadding)
          {
            j = 1;
            if ((j != 0) && (!((DismissView)localObject).willBeGone())) {
              break label119;
            }
          }
          for (bool = false;; bool = true)
          {
            ((DismissView)localObject).performVisibilityAnimation(bool);
            break;
            j = 0;
            break label89;
          }
        }
      } while (!(localObject instanceof EmptyShadeView));
      localObject = (EmptyShadeView)localObject;
      if (localStackViewState.clipTopAmount <= 0)
      {
        j = 1;
        label150:
        if ((j != 0) && (!((EmptyShadeView)localObject).willBeGone())) {
          break label180;
        }
      }
      label180:
      for (boolean bool = false;; bool = true)
      {
        ((EmptyShadeView)localObject).performVisibilityAnimation(bool);
        break;
        j = 0;
        break label150;
      }
    }
  }
  
  public boolean applyState(ExpandableView paramExpandableView, StackViewState paramStackViewState)
  {
    if (paramStackViewState == null)
    {
      Log.wtf("StackScrollStateNoSuchChild", "No child state was found when applying this state to the hostView");
      return false;
    }
    if (paramStackViewState.gone) {
      return false;
    }
    applyViewState(paramExpandableView, paramStackViewState);
    int i = paramExpandableView.getActualHeight();
    int j = paramStackViewState.height;
    if (i != j) {
      paramExpandableView.setActualHeight(j, false);
    }
    float f1 = paramExpandableView.getShadowAlpha();
    float f2 = paramStackViewState.shadowAlpha;
    if (f1 != f2) {
      paramExpandableView.setShadowAlpha(f2);
    }
    paramExpandableView.setDimmed(paramStackViewState.dimmed, false);
    paramExpandableView.setHideSensitive(paramStackViewState.hideSensitive, false, 0L, 0L);
    paramExpandableView.setBelowSpeedBump(paramStackViewState.belowSpeedBump);
    paramExpandableView.setDark(paramStackViewState.dark, false, 0L);
    if (paramExpandableView.getClipTopAmount() != paramStackViewState.clipTopAmount) {
      paramExpandableView.setClipTopAmount(paramStackViewState.clipTopAmount);
    }
    if ((paramExpandableView instanceof ExpandableNotificationRow))
    {
      paramExpandableView = (ExpandableNotificationRow)paramExpandableView;
      if (paramStackViewState.isBottomClipped) {
        paramExpandableView.setClipToActualHeight(true);
      }
      paramExpandableView.applyChildrenState(this);
    }
    return true;
  }
  
  public void applyViewState(View paramView, ViewState paramViewState)
  {
    float f1 = paramView.getAlpha();
    float f2 = paramView.getTranslationY();
    float f3 = paramView.getTranslationX();
    float f4 = paramView.getTranslationZ();
    float f5 = paramViewState.alpha;
    float f6 = paramViewState.yTranslation;
    float f7 = paramViewState.zTranslation;
    boolean bool1;
    int i;
    label78:
    boolean bool2;
    if (f5 != 0.0F)
    {
      bool1 = paramViewState.hidden;
      if ((f1 != f5) && (f3 == 0.0F))
      {
        if (f5 != 1.0F) {
          break label198;
        }
        i = 1;
        if ((!bool1) && (i == 0)) {
          break label204;
        }
        bool2 = false;
        label91:
        j = paramView.getLayerType();
        if (!bool2) {
          break label213;
        }
        i = 2;
        label105:
        if (j != i) {
          paramView.setLayerType(i, null);
        }
        paramView.setAlpha(f5);
      }
      int j = paramView.getVisibility();
      if (!bool1) {
        break label219;
      }
      i = 4;
      label139:
      if ((i != j) && ((!(paramView instanceof ExpandableView)) || (!((ExpandableView)paramView).willBeGone()))) {
        break label225;
      }
    }
    for (;;)
    {
      if (f2 != f6) {
        paramView.setTranslationY(f6);
      }
      if (f4 != f7) {
        paramView.setTranslationZ(f7);
      }
      return;
      bool1 = true;
      break;
      label198:
      i = 0;
      break label78;
      label204:
      bool2 = paramView.hasOverlappingRendering();
      break label91;
      label213:
      i = 0;
      break label105;
      label219:
      i = 0;
      break label139;
      label225:
      paramView.setVisibility(i);
    }
  }
  
  public ViewGroup getHostView()
  {
    return this.mHostView;
  }
  
  public StackViewState getViewStateForView(View paramView)
  {
    return (StackViewState)this.mStateMap.get(paramView);
  }
  
  public void removeViewStateForView(View paramView)
  {
    this.mStateMap.remove(paramView);
  }
  
  public void resetViewStates()
  {
    int j = this.mHostView.getChildCount();
    int i = 0;
    while (i < j)
    {
      Object localObject = (ExpandableView)this.mHostView.getChildAt(i);
      resetViewState((ExpandableView)localObject);
      if ((localObject instanceof ExpandableNotificationRow))
      {
        localObject = (ExpandableNotificationRow)localObject;
        List localList = ((ExpandableNotificationRow)localObject).getNotificationChildren();
        if ((((ExpandableNotificationRow)localObject).isSummaryWithChildren()) && (localList != null))
        {
          localObject = localList.iterator();
          while (((Iterator)localObject).hasNext()) {
            resetViewState((ExpandableNotificationRow)((Iterator)localObject).next());
          }
        }
      }
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\StackScrollState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */