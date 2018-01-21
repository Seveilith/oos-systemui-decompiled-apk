package com.android.systemui.statusbar.stack;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.ExpandableView;
import com.android.systemui.statusbar.notification.NotificationUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class StackScrollAlgorithm
{
  private StackIndentationFunctor mBottomStackIndentationFunctor;
  private int mBottomStackPeekSize;
  private int mBottomStackSlowDownLength;
  private int mCollapsedSize;
  private int mIncreasedPaddingBetweenElements;
  private boolean mIsExpanded;
  private int mPaddingBetweenElements;
  private StackScrollAlgorithmState mTempAlgorithmState = new StackScrollAlgorithmState();
  private int mZBasicHeight;
  private int mZDistanceBetweenElements;
  
  public StackScrollAlgorithm(Context paramContext)
  {
    initView(paramContext);
  }
  
  public static boolean canChildBeDismissed(View paramView)
  {
    if (!(paramView instanceof ExpandableNotificationRow)) {
      return false;
    }
    paramView = (ExpandableNotificationRow)paramView;
    if (paramView.areGutsExposed()) {
      return false;
    }
    return paramView.canViewBeDismissed();
  }
  
  private void clampHunToMaxTranslation(AmbientState paramAmbientState, ExpandableNotificationRow paramExpandableNotificationRow, StackViewState paramStackViewState)
  {
    float f1 = paramAmbientState.getMaxHeadsUpTranslation();
    float f2 = paramExpandableNotificationRow.getCollapsedHeight();
    f1 = Math.min(paramStackViewState.yTranslation, f1 - f2);
    paramStackViewState.height = ((int)Math.max(paramStackViewState.height - (paramStackViewState.yTranslation - f1), paramExpandableNotificationRow.getCollapsedHeight()));
    paramStackViewState.yTranslation = f1;
  }
  
  private void clampHunToTop(AmbientState paramAmbientState, ExpandableNotificationRow paramExpandableNotificationRow, StackViewState paramStackViewState)
  {
    float f = Math.max(paramAmbientState.getTopPadding() + paramAmbientState.getStackTranslation(), paramStackViewState.yTranslation);
    paramStackViewState.height = ((int)Math.max(paramStackViewState.height - (f - paramStackViewState.yTranslation), paramExpandableNotificationRow.getCollapsedHeight()));
    paramStackViewState.yTranslation = f;
  }
  
  private void clampPositionToBottomStackStart(StackViewState paramStackViewState, int paramInt1, int paramInt2, AmbientState paramAmbientState)
  {
    int i = paramAmbientState.getInnerHeight() - this.mBottomStackPeekSize - this.mBottomStackSlowDownLength;
    if (i - paramInt1 < paramStackViewState.yTranslation)
    {
      float f2 = i - paramStackViewState.yTranslation;
      float f1 = f2;
      if (f2 < paramInt2)
      {
        f1 = paramInt2;
        paramStackViewState.yTranslation = (i - paramInt2);
      }
      paramStackViewState.height = ((int)f1);
    }
  }
  
  private int getMaxAllowedChildHeight(View paramView)
  {
    if ((paramView instanceof ExpandableView)) {
      return ((ExpandableView)paramView).getIntrinsicHeight();
    }
    if (paramView == null) {
      return this.mCollapsedSize;
    }
    return paramView.getHeight();
  }
  
  private void getNotificationChildrenStates(StackScrollState paramStackScrollState, StackScrollAlgorithmState paramStackScrollAlgorithmState)
  {
    int j = paramStackScrollAlgorithmState.visibleChildren.size();
    int i = 0;
    while (i < j)
    {
      ExpandableView localExpandableView = (ExpandableView)paramStackScrollAlgorithmState.visibleChildren.get(i);
      if ((localExpandableView instanceof ExpandableNotificationRow)) {
        ((ExpandableNotificationRow)localExpandableView).getChildrenStates(paramStackScrollState);
      }
      i += 1;
    }
  }
  
  private int getPaddingAfterChild(StackScrollAlgorithmState paramStackScrollAlgorithmState, ExpandableView paramExpandableView)
  {
    paramStackScrollAlgorithmState = (Float)paramStackScrollAlgorithmState.increasedPaddingMap.get(paramExpandableView);
    if (paramStackScrollAlgorithmState == null) {
      return this.mPaddingBetweenElements;
    }
    return (int)NotificationUtils.interpolate(this.mPaddingBetweenElements, this.mIncreasedPaddingBetweenElements, paramStackScrollAlgorithmState.floatValue());
  }
  
  private void handleDraggedViews(AmbientState paramAmbientState, StackScrollState paramStackScrollState, StackScrollAlgorithmState paramStackScrollAlgorithmState)
  {
    ArrayList localArrayList = paramAmbientState.getDraggedViews();
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      View localView = (View)localIterator.next();
      int i = paramStackScrollAlgorithmState.visibleChildren.indexOf(localView);
      if ((i >= 0) && (i < paramStackScrollAlgorithmState.visibleChildren.size() - 1))
      {
        Object localObject = (View)paramStackScrollAlgorithmState.visibleChildren.get(i + 1);
        if (!localArrayList.contains(localObject))
        {
          localObject = paramStackScrollState.getViewStateForView((View)localObject);
          if (paramAmbientState.isShadeExpanded())
          {
            ((StackViewState)localObject).shadowAlpha = 1.0F;
            ((StackViewState)localObject).hidden = false;
          }
        }
        paramStackScrollState.getViewStateForView(localView).alpha = localView.getAlpha();
      }
    }
  }
  
  private void initAlgorithmState(StackScrollState paramStackScrollState, StackScrollAlgorithmState paramStackScrollAlgorithmState, AmbientState paramAmbientState)
  {
    paramStackScrollAlgorithmState.itemsInBottomStack = 0.0F;
    paramStackScrollAlgorithmState.partialInBottom = 0.0F;
    float f = paramAmbientState.getOverScrollAmount(false);
    paramStackScrollAlgorithmState.scrollY = ((int)(Math.max(0, paramAmbientState.getScrollY()) + f));
    ViewGroup localViewGroup = paramStackScrollState.getHostView();
    int m = localViewGroup.getChildCount();
    paramStackScrollAlgorithmState.visibleChildren.clear();
    paramStackScrollAlgorithmState.visibleChildren.ensureCapacity(m);
    paramStackScrollAlgorithmState.increasedPaddingMap.clear();
    int i = 0;
    paramAmbientState = null;
    int k = 0;
    while (k < m)
    {
      ExpandableView localExpandableView = (ExpandableView)localViewGroup.getChildAt(k);
      Object localObject = paramAmbientState;
      int j = i;
      if (localExpandableView.getVisibility() != 8)
      {
        j = updateNotGoneIndex(paramStackScrollState, paramStackScrollAlgorithmState, i, localExpandableView);
        f = localExpandableView.getIncreasedPaddingAmount();
        if (f != 0.0F)
        {
          paramStackScrollAlgorithmState.increasedPaddingMap.put(localExpandableView, Float.valueOf(f));
          if (paramAmbientState != null)
          {
            localObject = (Float)paramStackScrollAlgorithmState.increasedPaddingMap.get(paramAmbientState);
            if (localObject == null) {
              break label309;
            }
            f = Math.max(((Float)localObject).floatValue(), f);
          }
        }
        label309:
        for (;;)
        {
          paramStackScrollAlgorithmState.increasedPaddingMap.put(paramAmbientState, Float.valueOf(f));
          i = j;
          if (!(localExpandableView instanceof ExpandableNotificationRow)) {
            break;
          }
          paramAmbientState = (ExpandableNotificationRow)localExpandableView;
          localObject = paramAmbientState.getNotificationChildren();
          i = j;
          if (!paramAmbientState.isSummaryWithChildren()) {
            break;
          }
          i = j;
          if (localObject == null) {
            break;
          }
          paramAmbientState = ((Iterable)localObject).iterator();
          for (;;)
          {
            i = j;
            if (!paramAmbientState.hasNext()) {
              break;
            }
            localObject = (ExpandableNotificationRow)paramAmbientState.next();
            if (((ExpandableNotificationRow)localObject).getVisibility() != 8)
            {
              paramStackScrollState.getViewStateForView((View)localObject).notGoneIndex = j;
              j += 1;
            }
          }
        }
        localObject = localExpandableView;
        j = i;
      }
      k += 1;
      paramAmbientState = (AmbientState)localObject;
      i = j;
    }
  }
  
  private void initConstants(Context paramContext)
  {
    this.mPaddingBetweenElements = Math.max(1, paramContext.getResources().getDimensionPixelSize(2131755461));
    this.mIncreasedPaddingBetweenElements = paramContext.getResources().getDimensionPixelSize(2131755463);
    this.mCollapsedSize = paramContext.getResources().getDimensionPixelSize(2131755369);
    this.mBottomStackPeekSize = paramContext.getResources().getDimensionPixelSize(2131755457);
    this.mZDistanceBetweenElements = Math.max(1, paramContext.getResources().getDimensionPixelSize(2131755460));
    this.mZBasicHeight = (this.mZDistanceBetweenElements * 4);
    this.mBottomStackSlowDownLength = paramContext.getResources().getDimensionPixelSize(2131755459);
    this.mBottomStackIndentationFunctor = new PiecewiseLinearIndentationFunctor(3, this.mBottomStackPeekSize, getBottomStackSlowDownLength(), 0.5F);
  }
  
  private void updateClipping(StackScrollState paramStackScrollState, StackScrollAlgorithmState paramStackScrollAlgorithmState, AmbientState paramAmbientState)
  {
    float f5 = paramAmbientState.getTopPadding() + paramAmbientState.getStackTranslation();
    float f4 = 0.0F;
    float f2 = 0.0F;
    int j = paramStackScrollAlgorithmState.visibleChildren.size();
    int i = 0;
    if (i < j)
    {
      ExpandableView localExpandableView = (ExpandableView)paramStackScrollAlgorithmState.visibleChildren.get(i);
      StackViewState localStackViewState = paramStackScrollState.getViewStateForView(localExpandableView);
      float f3 = f4;
      float f1 = f2;
      if (!localExpandableView.mustStayOnScreen())
      {
        f3 = Math.max(f5, f4);
        f1 = Math.max(f5, f2);
      }
      f2 = localStackViewState.yTranslation;
      f4 = localStackViewState.height;
      boolean bool;
      if ((localExpandableView instanceof ExpandableNotificationRow))
      {
        bool = ((ExpandableNotificationRow)localExpandableView).isPinned();
        label125:
        if ((f2 >= f3) || ((bool) && (!paramAmbientState.isShadeExpanded()))) {
          break label198;
        }
      }
      label198:
      for (localStackViewState.clipTopAmount = ((int)(f3 - f2));; localStackViewState.clipTopAmount = 0)
      {
        if (!localExpandableView.isTransparent())
        {
          f3 = f2 + f4;
          f1 = f2;
        }
        i += 1;
        f4 = f3;
        f2 = f1;
        break;
        bool = false;
        break label125;
      }
    }
  }
  
  private void updateDimmedActivatedHideSensitive(AmbientState paramAmbientState, StackScrollState paramStackScrollState, StackScrollAlgorithmState paramStackScrollAlgorithmState)
  {
    boolean bool3 = paramAmbientState.isDimmed();
    boolean bool4 = paramAmbientState.isDark();
    boolean bool2 = paramAmbientState.isHideSensitive();
    paramAmbientState = paramAmbientState.getActivatedChild();
    int k = paramStackScrollAlgorithmState.visibleChildren.size();
    int i = 0;
    if (i < k)
    {
      View localView = (View)paramStackScrollAlgorithmState.visibleChildren.get(i);
      StackViewState localStackViewState = paramStackScrollState.getViewStateForView(localView);
      localStackViewState.dimmed = bool3;
      localStackViewState.dark = bool4;
      boolean bool1;
      if (!localStackViewState.hideSensitive)
      {
        bool1 = bool2;
        label90:
        localStackViewState.hideSensitive = bool1;
        if (paramAmbientState != localView) {
          break label149;
        }
      }
      label149:
      for (int j = 1;; j = 0)
      {
        if ((bool3) && (j != 0)) {
          localStackViewState.zTranslation += this.mZDistanceBetweenElements * 2.0F;
        }
        i += 1;
        break;
        bool1 = true;
        break label90;
      }
    }
  }
  
  private void updateFirstChildHeight(ExpandableView paramExpandableView, StackViewState paramStackViewState, int paramInt, AmbientState paramAmbientState)
  {
    paramStackViewState.height = ((int)Math.max(Math.min(paramAmbientState.getInnerHeight() - this.mBottomStackPeekSize - this.mBottomStackSlowDownLength + paramAmbientState.getScrollY(), paramInt), paramExpandableView.getCollapsedHeight()));
  }
  
  private void updateHeadsUpStates(StackScrollState paramStackScrollState, StackScrollAlgorithmState paramStackScrollAlgorithmState, AmbientState paramAmbientState)
  {
    int k = paramStackScrollAlgorithmState.visibleChildren.size();
    Object localObject1 = null;
    int i = 0;
    if (i < k)
    {
      localObject2 = (View)paramStackScrollAlgorithmState.visibleChildren.get(i);
      if ((localObject2 instanceof ExpandableNotificationRow)) {
        break label45;
      }
    }
    label45:
    ExpandableNotificationRow localExpandableNotificationRow;
    do
    {
      return;
      localExpandableNotificationRow = (ExpandableNotificationRow)localObject2;
    } while (!localExpandableNotificationRow.isHeadsUp());
    StackViewState localStackViewState = paramStackScrollState.getViewStateForView(localExpandableNotificationRow);
    Object localObject2 = localObject1;
    if (localObject1 == null)
    {
      localObject2 = localExpandableNotificationRow;
      localStackViewState.location = 1;
    }
    if (localObject2 == localExpandableNotificationRow) {}
    for (int j = 1;; j = 0)
    {
      float f1 = localStackViewState.yTranslation;
      float f2 = localStackViewState.height;
      if (this.mIsExpanded)
      {
        clampHunToTop(paramAmbientState, localExpandableNotificationRow, localStackViewState);
        clampHunToMaxTranslation(paramAmbientState, localExpandableNotificationRow, localStackViewState);
      }
      if (localExpandableNotificationRow.isPinned())
      {
        localStackViewState.yTranslation = Math.max(localStackViewState.yTranslation, 0.0F);
        localStackViewState.height = Math.max(localExpandableNotificationRow.getIntrinsicHeight(), localStackViewState.height);
        localObject1 = paramStackScrollState.getViewStateForView((View)localObject2);
        if ((j == 0) && ((!this.mIsExpanded) || (f1 + f2 < ((StackViewState)localObject1).yTranslation + ((StackViewState)localObject1).height)))
        {
          localStackViewState.height = localExpandableNotificationRow.getIntrinsicHeight();
          localStackViewState.yTranslation = (((StackViewState)localObject1).yTranslation + ((StackViewState)localObject1).height - localStackViewState.height);
        }
      }
      i += 1;
      localObject1 = localObject2;
      break;
    }
  }
  
  private int updateNotGoneIndex(StackScrollState paramStackScrollState, StackScrollAlgorithmState paramStackScrollAlgorithmState, int paramInt, ExpandableView paramExpandableView)
  {
    paramStackScrollState.getViewStateForView(paramExpandableView).notGoneIndex = paramInt;
    paramStackScrollAlgorithmState.visibleChildren.add(paramExpandableView);
    return paramInt + 1;
  }
  
  private void updatePositionsForState(StackScrollState paramStackScrollState, StackScrollAlgorithmState paramStackScrollAlgorithmState, AmbientState paramAmbientState)
  {
    float f2 = paramAmbientState.getInnerHeight() - this.mBottomStackPeekSize - this.mBottomStackSlowDownLength;
    float f1 = -paramStackScrollAlgorithmState.scrollY;
    int j = paramStackScrollAlgorithmState.visibleChildren.size();
    int i = 0;
    if (i < j)
    {
      ExpandableView localExpandableView = (ExpandableView)paramStackScrollAlgorithmState.visibleChildren.get(i);
      StackViewState localStackViewState = paramStackScrollState.getViewStateForView(localExpandableView);
      localStackViewState.location = 0;
      int k = getPaddingAfterChild(paramStackScrollAlgorithmState, localExpandableView);
      int m = getMaxAllowedChildHeight(localExpandableView);
      int n = localExpandableView.getCollapsedHeight();
      localStackViewState.yTranslation = f1;
      if (i == 0) {
        updateFirstChildHeight(localExpandableView, localStackViewState, m, paramAmbientState);
      }
      if (m + f1 + k >= f2) {
        if (f1 >= f2) {
          updateStateForChildFullyInBottomStack(paramStackScrollAlgorithmState, f2, localStackViewState, n, paramAmbientState, localExpandableView);
        }
      }
      for (;;)
      {
        if ((i == 0) && (paramAmbientState.getScrollY() <= 0)) {
          localStackViewState.yTranslation = Math.max(0.0F, localStackViewState.yTranslation);
        }
        f1 = localStackViewState.yTranslation + m + k;
        if (f1 <= 0.0F) {
          localStackViewState.location = 2;
        }
        if (localStackViewState.location == 0) {
          Log.wtf("StackScrollAlgorithm", "Failed to assign location for child " + i);
        }
        localStackViewState.yTranslation += paramAmbientState.getTopPadding() + paramAmbientState.getStackTranslation();
        i += 1;
        break;
        updateStateForChildTransitioningInBottom(paramStackScrollAlgorithmState, f2, localExpandableView, f1, localStackViewState, m);
        continue;
        localStackViewState.location = 4;
        clampPositionToBottomStackStart(localStackViewState, localStackViewState.height, m, paramAmbientState);
      }
    }
  }
  
  private void updateSpeedBumpState(StackScrollState paramStackScrollState, StackScrollAlgorithmState paramStackScrollAlgorithmState, int paramInt)
  {
    int j = paramStackScrollAlgorithmState.visibleChildren.size();
    int i = 0;
    if (i < j)
    {
      StackViewState localStackViewState = paramStackScrollState.getViewStateForView((View)paramStackScrollAlgorithmState.visibleChildren.get(i));
      if ((paramInt != -1) && (i >= paramInt)) {}
      for (boolean bool = true;; bool = false)
      {
        localStackViewState.belowSpeedBump = bool;
        i += 1;
        break;
      }
    }
  }
  
  private void updateStateForChildFullyInBottomStack(StackScrollAlgorithmState paramStackScrollAlgorithmState, float paramFloat, StackViewState paramStackViewState, int paramInt, AmbientState paramAmbientState, ExpandableView paramExpandableView)
  {
    paramStackScrollAlgorithmState.itemsInBottomStack += 1.0F;
    if (paramStackScrollAlgorithmState.itemsInBottomStack < 3.0F)
    {
      paramFloat = this.mBottomStackIndentationFunctor.getValue(paramStackScrollAlgorithmState.itemsInBottomStack) + paramFloat - getPaddingAfterChild(paramStackScrollAlgorithmState, paramExpandableView);
      paramStackViewState.location = 8;
      paramStackViewState.height = paramInt;
      paramStackViewState.yTranslation = (paramFloat - paramInt);
      return;
    }
    if (paramStackScrollAlgorithmState.itemsInBottomStack > 5.0F)
    {
      paramStackViewState.hidden = true;
      paramStackViewState.shadowAlpha = 0.0F;
    }
    for (;;)
    {
      paramStackViewState.location = 16;
      paramFloat = paramAmbientState.getInnerHeight();
      break;
      if (paramStackScrollAlgorithmState.itemsInBottomStack > 4.0F) {
        paramStackViewState.shadowAlpha = (1.0F - paramStackScrollAlgorithmState.partialInBottom);
      }
    }
  }
  
  private void updateStateForChildTransitioningInBottom(StackScrollAlgorithmState paramStackScrollAlgorithmState, float paramFloat1, ExpandableView paramExpandableView, float paramFloat2, StackViewState paramStackViewState, int paramInt)
  {
    paramStackScrollAlgorithmState.partialInBottom = (1.0F - (paramFloat1 - paramFloat2) / (getPaddingAfterChild(paramStackScrollAlgorithmState, paramExpandableView) + paramInt));
    float f = this.mBottomStackIndentationFunctor.getValue(paramStackScrollAlgorithmState.partialInBottom);
    paramStackScrollAlgorithmState.itemsInBottomStack += paramStackScrollAlgorithmState.partialInBottom;
    int i = paramInt;
    if (paramInt > paramExpandableView.getCollapsedHeight())
    {
      i = (int)Math.max(Math.min(paramFloat1 + f - getPaddingAfterChild(paramStackScrollAlgorithmState, paramExpandableView) - paramFloat2, paramInt), paramExpandableView.getCollapsedHeight());
      paramStackViewState.height = i;
    }
    paramStackViewState.yTranslation = (paramFloat1 + f - i - getPaddingAfterChild(paramStackScrollAlgorithmState, paramExpandableView));
    paramStackViewState.location = 4;
  }
  
  private void updateZValuesForState(StackScrollState paramStackScrollState, StackScrollAlgorithmState paramStackScrollAlgorithmState, AmbientState paramAmbientState)
  {
    int j = paramStackScrollAlgorithmState.visibleChildren.size();
    float f2 = 0.0F;
    int i = j - 1;
    if (i >= 0)
    {
      ExpandableView localExpandableView = (ExpandableView)paramStackScrollAlgorithmState.visibleChildren.get(i);
      StackViewState localStackViewState = paramStackScrollState.getViewStateForView(localExpandableView);
      float f1;
      if (i > j - 1 - paramStackScrollAlgorithmState.itemsInBottomStack)
      {
        f1 = i - (j - 1 - paramStackScrollAlgorithmState.itemsInBottomStack);
        if (f1 <= 1.0F) {
          if (f1 <= 0.2F)
          {
            f1 = 0.1F * f1 * 5.0F;
            label106:
            localStackViewState.zTranslation = (this.mZBasicHeight - f1);
            f1 = f2;
          }
        }
      }
      for (;;)
      {
        i -= 1;
        f2 = f1;
        break;
        f1 = 0.1F + (f1 - 0.2F) * (1.0F / 0.8F) * (this.mZDistanceBetweenElements - 0.1F);
        break label106;
        f1 *= this.mZDistanceBetweenElements;
        break label106;
        if ((localExpandableView.mustStayOnScreen()) && (localStackViewState.yTranslation < paramAmbientState.getTopPadding() + paramAmbientState.getStackTranslation()))
        {
          if (f2 != 0.0F) {}
          for (f1 = f2 + 1.0F;; f1 = f2 + Math.min(1.0F, (paramAmbientState.getTopPadding() + paramAmbientState.getStackTranslation() - localStackViewState.yTranslation) / localStackViewState.height))
          {
            localStackViewState.zTranslation = (this.mZBasicHeight + this.mZDistanceBetweenElements * f1);
            break;
          }
        }
        localStackViewState.zTranslation = this.mZBasicHeight;
        f1 = f2;
      }
    }
  }
  
  public int getBottomStackSlowDownLength()
  {
    return this.mBottomStackSlowDownLength + this.mPaddingBetweenElements;
  }
  
  public void getStackScrollState(AmbientState paramAmbientState, StackScrollState paramStackScrollState)
  {
    StackScrollAlgorithmState localStackScrollAlgorithmState = this.mTempAlgorithmState;
    paramStackScrollState.resetViewStates();
    initAlgorithmState(paramStackScrollState, localStackScrollAlgorithmState, paramAmbientState);
    updatePositionsForState(paramStackScrollState, localStackScrollAlgorithmState, paramAmbientState);
    updateZValuesForState(paramStackScrollState, localStackScrollAlgorithmState, paramAmbientState);
    updateHeadsUpStates(paramStackScrollState, localStackScrollAlgorithmState, paramAmbientState);
    handleDraggedViews(paramAmbientState, paramStackScrollState, localStackScrollAlgorithmState);
    updateDimmedActivatedHideSensitive(paramAmbientState, paramStackScrollState, localStackScrollAlgorithmState);
    updateClipping(paramStackScrollState, localStackScrollAlgorithmState, paramAmbientState);
    updateSpeedBumpState(paramStackScrollState, localStackScrollAlgorithmState, paramAmbientState.getSpeedBumpIndex());
    getNotificationChildrenStates(paramStackScrollState, localStackScrollAlgorithmState);
  }
  
  public void initView(Context paramContext)
  {
    initConstants(paramContext);
  }
  
  public void setIsExpanded(boolean paramBoolean)
  {
    this.mIsExpanded = paramBoolean;
  }
  
  class StackScrollAlgorithmState
  {
    public final HashMap<ExpandableView, Float> increasedPaddingMap = new HashMap();
    public float itemsInBottomStack;
    public float partialInBottom;
    public int scrollY;
    public final ArrayList<ExpandableView> visibleChildren = new ArrayList();
    
    StackScrollAlgorithmState() {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\StackScrollAlgorithm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */