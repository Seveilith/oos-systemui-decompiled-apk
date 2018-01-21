package com.android.systemui.statusbar.stack;

import android.app.Notification.Builder;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.systemui.ViewInvertHelper;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.NotificationContentView;
import com.android.systemui.statusbar.NotificationHeaderUtil;
import com.android.systemui.statusbar.notification.HybridGroupManager;
import com.android.systemui.statusbar.notification.HybridNotificationView;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.NotificationViewWrapper;
import java.util.ArrayList;
import java.util.List;

public class NotificationChildrenContainer
  extends ViewGroup
{
  private int mActualHeight;
  private int mChildPadding;
  private final List<ExpandableNotificationRow> mChildren = new ArrayList();
  private boolean mChildrenExpanded;
  private float mCollapsedBottompadding;
  private int mDividerHeight;
  private final List<View> mDividers = new ArrayList();
  private ViewState mGroupOverFlowState;
  private int mHeaderHeight;
  private NotificationHeaderUtil mHeaderUtil;
  private ViewState mHeaderViewState;
  private final HybridGroupManager mHybridGroupManager;
  private int mMaxNotificationHeight;
  private boolean mNeverAppliedGroupState;
  private NotificationHeaderView mNotificationHeader;
  private int mNotificationHeaderMargin;
  private NotificationViewWrapper mNotificationHeaderWrapper;
  private ExpandableNotificationRow mNotificationParent;
  private int mNotificatonTopPadding;
  private ViewInvertHelper mOverflowInvertHelper;
  private TextView mOverflowNumber;
  private int mRealHeight;
  private boolean mUserLocked;
  
  public NotificationChildrenContainer(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NotificationChildrenContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NotificationChildrenContainer(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public NotificationChildrenContainer(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    initDimens();
    this.mHybridGroupManager = new HybridGroupManager(getContext(), this);
  }
  
  private int getIntrinsicHeight(float paramFloat)
  {
    int i = this.mNotificationHeaderMargin;
    int j = 0;
    int i1 = this.mChildren.size();
    int m = 1;
    float f = 0.0F;
    if (this.mUserLocked) {
      f = getGroupExpandFraction();
    }
    int k = 0;
    if ((k >= i1) || (j >= paramFloat))
    {
      if (!this.mUserLocked) {
        break label240;
      }
      j = (int)(i + NotificationUtils.interpolate(this.mCollapsedBottompadding, 0.0F, f));
    }
    label208:
    label240:
    do
    {
      return j;
      if (m == 0) {
        if (this.mUserLocked) {
          i = (int)(i + NotificationUtils.interpolate(this.mChildPadding, this.mDividerHeight, f));
        }
      }
      for (;;)
      {
        i += ((ExpandableNotificationRow)this.mChildren.get(k)).getIntrinsicHeight();
        j += 1;
        k += 1;
        break;
        if (this.mChildrenExpanded) {}
        for (int n = this.mDividerHeight;; n = this.mChildPadding)
        {
          i += n;
          break;
        }
        if (!this.mUserLocked) {
          break label208;
        }
        i = (int)(i + NotificationUtils.interpolate(0.0F, this.mNotificatonTopPadding + this.mDividerHeight, f));
        m = 0;
      }
      if (this.mChildrenExpanded) {}
      for (m = this.mNotificatonTopPadding + this.mDividerHeight;; m = 0)
      {
        i += m;
        break;
      }
      j = i;
    } while (this.mChildrenExpanded);
    return (int)(i + this.mCollapsedBottompadding);
  }
  
  private int getMaxAllowedVisibleChildren()
  {
    return getMaxAllowedVisibleChildren(false);
  }
  
  private int getMaxAllowedVisibleChildren(boolean paramBoolean)
  {
    if ((!paramBoolean) && ((this.mChildrenExpanded) || (this.mNotificationParent.isUserLocked()))) {
      return 8;
    }
    if ((!this.mNotificationParent.isOnKeyguard()) && ((this.mNotificationParent.isExpanded()) || (this.mNotificationParent.isHeadsUp()))) {
      return 5;
    }
    return 2;
  }
  
  private int getMinHeight(int paramInt)
  {
    int i = this.mNotificationHeaderMargin;
    int j = 0;
    int m = 1;
    int n = this.mChildren.size();
    int k = 0;
    if ((k >= n) || (j >= paramInt)) {
      return (int)(i + this.mCollapsedBottompadding);
    }
    if (m == 0) {
      i += this.mChildPadding;
    }
    for (;;)
    {
      i += ((ExpandableNotificationRow)this.mChildren.get(k)).getSingleLineView().getHeight();
      j += 1;
      k += 1;
      break;
      m = 0;
    }
  }
  
  private int getVisibleChildrenExpandHeight()
  {
    int k = this.mNotificationHeaderMargin + this.mNotificatonTopPadding + this.mDividerHeight;
    int i = 0;
    int n = this.mChildren.size();
    int i1 = getMaxAllowedVisibleChildren(true);
    int j = 0;
    if ((j >= n) || (i >= i1)) {
      return k;
    }
    ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)this.mChildren.get(j);
    if (localExpandableNotificationRow.isExpanded(true)) {}
    for (int m = localExpandableNotificationRow.getMaxExpandHeight();; m = localExpandableNotificationRow.getShowingLayout().getMinHeight(true))
    {
      float f = m;
      k = (int)(k + f);
      i += 1;
      j += 1;
      break;
    }
  }
  
  private View inflateDivider()
  {
    return LayoutInflater.from(this.mContext).inflate(2130968732, this, false);
  }
  
  private void initDimens()
  {
    this.mChildPadding = getResources().getDimensionPixelSize(2131755482);
    this.mDividerHeight = Math.max(1, getResources().getDimensionPixelSize(2131755461));
    this.mHeaderHeight = getResources().getDimensionPixelSize(2131755462);
    this.mMaxNotificationHeight = getResources().getDimensionPixelSize(2131755371);
    this.mNotificationHeaderMargin = getResources().getDimensionPixelSize(17104966);
    this.mNotificatonTopPadding = getResources().getDimensionPixelSize(2131755483);
    this.mCollapsedBottompadding = getResources().getDimensionPixelSize(17104967);
  }
  
  private boolean updateChildStateForExpandedGroup(ExpandableNotificationRow paramExpandableNotificationRow, int paramInt1, StackViewState paramStackViewState, int paramInt2)
  {
    int j = paramInt2 + paramExpandableNotificationRow.getClipTopAmount();
    int i = paramExpandableNotificationRow.getIntrinsicHeight();
    paramInt2 = i;
    if (j + i >= paramInt1) {
      paramInt2 = Math.max(paramInt1 - j, 0);
    }
    if (paramInt2 == 0) {}
    for (boolean bool = true;; bool = false)
    {
      paramStackViewState.hidden = bool;
      paramStackViewState.height = paramInt2;
      if ((paramStackViewState.height != i) && (!paramStackViewState.hidden)) {
        break;
      }
      return false;
    }
    return true;
  }
  
  private void updateExpansionStates()
  {
    if ((this.mChildrenExpanded) || (this.mUserLocked)) {
      return;
    }
    int j = this.mChildren.size();
    int i = 0;
    if (i < j)
    {
      ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)this.mChildren.get(i);
      if ((i == 0) && (j == 1)) {}
      for (boolean bool = true;; bool = false)
      {
        localExpandableNotificationRow.setSystemChildExpanded(bool);
        i += 1;
        break;
      }
    }
  }
  
  public void addNotification(ExpandableNotificationRow paramExpandableNotificationRow, int paramInt)
  {
    if (paramInt < 0) {
      paramInt = this.mChildren.size();
    }
    for (;;)
    {
      this.mChildren.add(paramInt, paramExpandableNotificationRow);
      ViewGroup localViewGroup = (ViewGroup)paramExpandableNotificationRow.getParent();
      if (localViewGroup != null) {
        localViewGroup.removeView(paramExpandableNotificationRow);
      }
      addView(paramExpandableNotificationRow);
      paramExpandableNotificationRow.setUserLocked(this.mUserLocked);
      paramExpandableNotificationRow = inflateDivider();
      addView(paramExpandableNotificationRow);
      this.mDividers.add(paramInt, paramExpandableNotificationRow);
      updateGroupOverflow();
      return;
    }
  }
  
  public boolean applyChildOrder(List<ExpandableNotificationRow> paramList)
  {
    if (paramList == null) {
      return false;
    }
    boolean bool = false;
    int i = 0;
    while ((i < this.mChildren.size()) && (i < paramList.size()))
    {
      ExpandableNotificationRow localExpandableNotificationRow1 = (ExpandableNotificationRow)this.mChildren.get(i);
      ExpandableNotificationRow localExpandableNotificationRow2 = (ExpandableNotificationRow)paramList.get(i);
      if (localExpandableNotificationRow1 != localExpandableNotificationRow2)
      {
        this.mChildren.remove(localExpandableNotificationRow2);
        this.mChildren.add(i, localExpandableNotificationRow2);
        bool = true;
      }
      i += 1;
    }
    updateExpansionStates();
    return bool;
  }
  
  public void applyState(StackScrollState paramStackScrollState)
  {
    int j = this.mChildren.size();
    ViewState localViewState = new ViewState();
    float f2 = 0.0F;
    if (this.mUserLocked) {
      f2 = getGroupExpandFraction();
    }
    boolean bool1;
    int i;
    label53:
    ExpandableNotificationRow localExpandableNotificationRow;
    View localView;
    float f1;
    label153:
    float f3;
    if (!this.mUserLocked)
    {
      bool1 = this.mNotificationParent.isGroupExpansionChanging();
      i = 0;
      if (i >= j) {
        break label259;
      }
      localExpandableNotificationRow = (ExpandableNotificationRow)this.mChildren.get(i);
      StackViewState localStackViewState = paramStackScrollState.getViewStateForView(localExpandableNotificationRow);
      paramStackScrollState.applyState(localExpandableNotificationRow, localStackViewState);
      localView = (View)this.mDividers.get(i);
      localViewState.initFrom(localView);
      localViewState.yTranslation = (localStackViewState.yTranslation - this.mDividerHeight);
      if ((!this.mChildrenExpanded) || (localStackViewState.alpha == 0.0F)) {
        break label248;
      }
      f1 = 0.5F;
      f3 = f1;
      if (this.mUserLocked)
      {
        f3 = f1;
        if (localStackViewState.alpha != 0.0F) {
          f3 = NotificationUtils.interpolate(0.0F, 0.5F, Math.min(localStackViewState.alpha, f2));
        }
      }
      if (!bool1) {
        break label253;
      }
    }
    label248:
    label253:
    for (boolean bool2 = false;; bool2 = true)
    {
      localViewState.hidden = bool2;
      localViewState.alpha = f3;
      paramStackScrollState.applyViewState(localView, localViewState);
      localExpandableNotificationRow.setFakeShadowIntensity(0.0F, 0.0F, 0, 0);
      i += 1;
      break label53;
      bool1 = true;
      break;
      f1 = 0.0F;
      break label153;
    }
    label259:
    if (this.mOverflowNumber != null)
    {
      paramStackScrollState.applyViewState(this.mOverflowNumber, this.mGroupOverFlowState);
      this.mNeverAppliedGroupState = false;
    }
    if (this.mNotificationHeader != null) {
      paramStackScrollState.applyViewState(this.mNotificationHeader, this.mHeaderViewState);
    }
  }
  
  public int getCollapsedHeight()
  {
    return getMinHeight(getMaxAllowedVisibleChildren(true));
  }
  
  public float getGroupExpandFraction()
  {
    int i = getVisibleChildrenExpandHeight();
    int j = getCollapsedHeight();
    return Math.max(0.0F, Math.min(1.0F, (this.mActualHeight - j) / (i - j)));
  }
  
  public NotificationHeaderView getHeaderView()
  {
    return this.mNotificationHeader;
  }
  
  public int getIntrinsicHeight()
  {
    return getIntrinsicHeight(getMaxAllowedVisibleChildren());
  }
  
  public int getMaxContentHeight()
  {
    int i = this.mNotificationHeaderMargin + this.mNotificatonTopPadding;
    int j = 0;
    int n = this.mChildren.size();
    int k = 0;
    if ((k >= n) || (j >= 8))
    {
      k = i;
      if (j > 0) {
        k = i + this.mDividerHeight * j;
      }
      return k;
    }
    ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)this.mChildren.get(k);
    if (localExpandableNotificationRow.isExpanded(true)) {}
    for (int m = localExpandableNotificationRow.getMaxExpandHeight();; m = localExpandableNotificationRow.getShowingLayout().getMinHeight(true))
    {
      float f = m;
      i = (int)(i + f);
      j += 1;
      k += 1;
      break;
    }
  }
  
  public int getMinHeight()
  {
    return getMinHeight(2);
  }
  
  public int getNotificationChildCount()
  {
    return this.mChildren.size();
  }
  
  public List<ExpandableNotificationRow> getNotificationChildren()
  {
    return this.mChildren;
  }
  
  public int getPositionInLinearLayout(View paramView)
  {
    int i = this.mNotificationHeaderMargin + this.mNotificatonTopPadding;
    int k = 0;
    while (k < this.mChildren.size())
    {
      ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)this.mChildren.get(k);
      if (localExpandableNotificationRow.getVisibility() != 8) {}
      int j;
      for (int m = 1;; m = 0)
      {
        j = i;
        if (m != 0) {
          j = i + this.mDividerHeight;
        }
        if (localExpandableNotificationRow != paramView) {
          break;
        }
        return j;
      }
      i = j;
      if (m != 0) {
        i = j + localExpandableNotificationRow.getIntrinsicHeight();
      }
      k += 1;
    }
    return 0;
  }
  
  public void getState(StackScrollState paramStackScrollState, StackViewState paramStackViewState)
  {
    int i1 = this.mChildren.size();
    int i = this.mNotificationHeaderMargin;
    int m = 1;
    int i2 = getMaxAllowedVisibleChildren() - 1;
    int j = i2 + 1;
    float f1 = 0.0F;
    if (this.mUserLocked)
    {
      f1 = getGroupExpandFraction();
      j = getMaxAllowedVisibleChildren(true);
    }
    boolean bool;
    int k;
    label80:
    Object localObject1;
    label136:
    Object localObject2;
    int n;
    label177:
    float f2;
    if (!this.mNotificationParent.isGroupExpansionChanging())
    {
      bool = this.mChildrenExpanded;
      int i3 = paramStackViewState.height;
      k = 0;
      if (k >= i1) {
        break label484;
      }
      localObject1 = (ExpandableNotificationRow)this.mChildren.get(k);
      if (m != 0) {
        break label330;
      }
      if (!this.mUserLocked) {
        break label298;
      }
      i = (int)(i + NotificationUtils.interpolate(this.mChildPadding, this.mDividerHeight, f1));
      localObject2 = paramStackScrollState.getViewStateForView((View)localObject1);
      n = ((ExpandableNotificationRow)localObject1).getIntrinsicHeight();
      if (!bool) {
        break label399;
      }
      if (updateChildStateForExpandedGroup((ExpandableNotificationRow)localObject1, i3, (StackViewState)localObject2, i)) {
        ((StackViewState)localObject2).isBottomClipped = true;
      }
      ((StackViewState)localObject2).yTranslation = i;
      if (!bool) {
        break label421;
      }
      f2 = this.mNotificationParent.getTranslationZ();
      label199:
      ((StackViewState)localObject2).zTranslation = f2;
      ((StackViewState)localObject2).dimmed = paramStackViewState.dimmed;
      ((StackViewState)localObject2).dark = paramStackViewState.dark;
      ((StackViewState)localObject2).hideSensitive = paramStackViewState.hideSensitive;
      ((StackViewState)localObject2).belowSpeedBump = paramStackViewState.belowSpeedBump;
      ((StackViewState)localObject2).clipTopAmount = 0;
      ((StackViewState)localObject2).alpha = 0.0F;
      if (k >= j) {
        break label427;
      }
    }
    for (((StackViewState)localObject2).alpha = 1.0F;; ((StackViewState)localObject2).alpha = Math.max(0.0F, Math.min(1.0F, ((StackViewState)localObject2).alpha)))
    {
      label298:
      label330:
      label399:
      label421:
      label427:
      do
      {
        ((StackViewState)localObject2).location = paramStackViewState.location;
        i += n;
        k += 1;
        break label80;
        bool = false;
        break;
        if (this.mChildrenExpanded) {}
        for (n = this.mDividerHeight;; n = this.mChildPadding)
        {
          i += n;
          break;
        }
        if (this.mUserLocked)
        {
          i = (int)(i + NotificationUtils.interpolate(0.0F, this.mNotificatonTopPadding + this.mDividerHeight, f1));
          m = 0;
          break label136;
        }
        if (this.mChildrenExpanded) {}
        for (m = this.mNotificatonTopPadding + this.mDividerHeight;; m = 0)
        {
          i += m;
          break;
        }
        ((StackViewState)localObject2).hidden = false;
        ((StackViewState)localObject2).height = n;
        ((StackViewState)localObject2).isBottomClipped = false;
        break label177;
        f2 = 0.0F;
        break label199;
      } while ((f1 != 1.0F) || (k > i2));
      ((StackViewState)localObject2).alpha = ((this.mActualHeight - ((StackViewState)localObject2).yTranslation) / ((StackViewState)localObject2).height);
    }
    label484:
    if (this.mOverflowNumber != null)
    {
      localObject2 = (ExpandableNotificationRow)this.mChildren.get(Math.min(getMaxAllowedVisibleChildren(true), i1) - 1);
      this.mGroupOverFlowState.copyFrom(paramStackScrollState.getViewStateForView((View)localObject2));
      if (this.mChildrenExpanded) {
        break label679;
      }
      if (this.mUserLocked)
      {
        localObject1 = ((ExpandableNotificationRow)localObject2).getSingleLineView();
        paramStackViewState = ((HybridNotificationView)localObject1).getTextView();
        paramStackScrollState = paramStackViewState;
        if (paramStackViewState.getVisibility() == 8) {
          paramStackScrollState = ((HybridNotificationView)localObject1).getTitleView();
        }
        paramStackViewState = paramStackScrollState;
        if (paramStackScrollState.getVisibility() == 8) {
          paramStackViewState = (StackViewState)localObject1;
        }
        paramStackScrollState = this.mGroupOverFlowState;
        paramStackScrollState.yTranslation += NotificationUtils.getRelativeYOffset(paramStackViewState, (View)localObject2);
        this.mGroupOverFlowState.alpha = paramStackViewState.getAlpha();
      }
    }
    if (this.mNotificationHeader != null)
    {
      if (this.mHeaderViewState == null) {
        this.mHeaderViewState = new ViewState();
      }
      this.mHeaderViewState.initFrom(this.mNotificationHeader);
      paramStackScrollState = this.mHeaderViewState;
      if (!bool) {
        break label709;
      }
    }
    label679:
    label709:
    for (f1 = this.mNotificationParent.getTranslationZ();; f1 = 0.0F)
    {
      paramStackScrollState.zTranslation = f1;
      return;
      paramStackScrollState = this.mGroupOverFlowState;
      paramStackScrollState.yTranslation += this.mNotificationHeaderMargin;
      this.mGroupOverFlowState.alpha = 0.0F;
      break;
    }
  }
  
  public ExpandableNotificationRow getViewAtPosition(float paramFloat)
  {
    int j = this.mChildren.size();
    int i = 0;
    while (i < j)
    {
      ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)this.mChildren.get(i);
      float f1 = localExpandableNotificationRow.getTranslationY();
      float f2 = localExpandableNotificationRow.getClipTopAmount();
      float f3 = localExpandableNotificationRow.getActualHeight();
      if ((paramFloat >= f1 + f2) && (paramFloat <= f1 + f3)) {
        return localExpandableNotificationRow;
      }
      i += 1;
    }
    return null;
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    updateGroupOverflow();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = 1;
    paramInt3 = Math.min(this.mChildren.size(), 8);
    paramInt1 = 0;
    while (paramInt1 < paramInt3)
    {
      View localView = (View)this.mChildren.get(paramInt1);
      localView.layout(0, 0, localView.getMeasuredWidth(), localView.getMeasuredHeight());
      ((View)this.mDividers.get(paramInt1)).layout(0, 0, getWidth(), this.mDividerHeight);
      paramInt1 += 1;
    }
    if (this.mOverflowNumber != null)
    {
      if (getLayoutDirection() != 1) {
        break label172;
      }
      paramInt1 = paramInt2;
      if (paramInt1 == 0) {
        break label177;
      }
    }
    label172:
    label177:
    for (paramInt1 = 0;; paramInt1 = getWidth() - this.mOverflowNumber.getMeasuredWidth())
    {
      paramInt2 = this.mOverflowNumber.getMeasuredWidth();
      this.mOverflowNumber.layout(paramInt1, 0, paramInt1 + paramInt2, this.mOverflowNumber.getMeasuredHeight());
      if (this.mNotificationHeader != null) {
        this.mNotificationHeader.layout(0, 0, this.mNotificationHeader.getMeasuredWidth(), this.mNotificationHeader.getMeasuredHeight());
      }
      return;
      paramInt1 = 0;
      break;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int k = this.mMaxNotificationHeight;
    int m = View.MeasureSpec.getMode(paramInt2);
    int j;
    label33:
    int n;
    int i2;
    int i1;
    int i3;
    label152:
    label155:
    ExpandableNotificationRow localExpandableNotificationRow;
    if (m == 1073741824)
    {
      i = 1;
      if (m != Integer.MIN_VALUE) {
        break label287;
      }
      j = 1;
      n = View.MeasureSpec.getSize(paramInt2);
      if (i == 0)
      {
        paramInt2 = k;
        if (j == 0) {}
      }
      else
      {
        paramInt2 = Math.min(k, n);
      }
      i2 = View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE);
      i1 = View.MeasureSpec.getSize(paramInt1);
      if (this.mOverflowNumber != null) {
        this.mOverflowNumber.measure(View.MeasureSpec.makeMeasureSpec(i1, Integer.MIN_VALUE), i2);
      }
      i3 = View.MeasureSpec.makeMeasureSpec(this.mDividerHeight, 1073741824);
      paramInt2 = this.mNotificationHeaderMargin + this.mNotificatonTopPadding;
      int i4 = Math.min(this.mChildren.size(), 8);
      i = getMaxAllowedVisibleChildren(true);
      if (i4 <= i) {
        break label293;
      }
      i -= 1;
      j = 0;
      if (j >= i4) {
        break label310;
      }
      localExpandableNotificationRow = (ExpandableNotificationRow)this.mChildren.get(j);
      if (j != i) {
        break label298;
      }
      k = 1;
      label187:
      if ((k == 0) || (this.mOverflowNumber == null)) {
        break label304;
      }
    }
    label287:
    label293:
    label298:
    label304:
    for (k = this.mOverflowNumber.getMeasuredWidth();; k = 0)
    {
      localExpandableNotificationRow.setSingleLineWidthIndention(k);
      localExpandableNotificationRow.measure(paramInt1, i2);
      ((View)this.mDividers.get(j)).measure(paramInt1, i3);
      k = paramInt2;
      if (localExpandableNotificationRow.getVisibility() != 8) {
        k = paramInt2 + (localExpandableNotificationRow.getMeasuredHeight() + this.mDividerHeight);
      }
      j += 1;
      paramInt2 = k;
      break label155;
      i = 0;
      break;
      j = 0;
      break label33;
      i = -1;
      break label152;
      k = 0;
      break label187;
    }
    label310:
    this.mRealHeight = paramInt2;
    int i = paramInt2;
    if (m != 0) {
      i = Math.min(paramInt2, n);
    }
    if (this.mNotificationHeader != null)
    {
      paramInt2 = View.MeasureSpec.makeMeasureSpec(this.mHeaderHeight, 1073741824);
      this.mNotificationHeader.measure(paramInt1, paramInt2);
    }
    setMeasuredDimension(i1, i);
  }
  
  public void onNotificationUpdated()
  {
    this.mHybridGroupManager.setOverflowNumberColor(this.mOverflowNumber, this.mNotificationParent.getNotificationColor());
  }
  
  public boolean pointInView(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramFloat1 >= -paramFloat3)
    {
      bool1 = bool2;
      if (paramFloat2 >= -paramFloat3)
      {
        bool1 = bool2;
        if (paramFloat1 < this.mRight - this.mLeft + paramFloat3)
        {
          bool1 = bool2;
          if (paramFloat2 < this.mRealHeight + paramFloat3) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public void prepareExpansionChanged(StackScrollState paramStackScrollState) {}
  
  public void reInflateViews(View.OnClickListener paramOnClickListener, StatusBarNotification paramStatusBarNotification)
  {
    removeView(this.mNotificationHeader);
    this.mNotificationHeader = null;
    recreateNotificationHeader(paramOnClickListener, paramStatusBarNotification);
    initDimens();
    int i = 0;
    while (i < this.mDividers.size())
    {
      paramOnClickListener = (View)this.mDividers.get(i);
      int j = indexOfChild(paramOnClickListener);
      removeView(paramOnClickListener);
      paramOnClickListener = inflateDivider();
      addView(paramOnClickListener, j);
      this.mDividers.set(i, paramOnClickListener);
      i += 1;
    }
    removeView(this.mOverflowNumber);
    this.mOverflowNumber = null;
    this.mOverflowInvertHelper = null;
    this.mGroupOverFlowState = null;
    updateGroupOverflow();
  }
  
  public void recreateNotificationHeader(View.OnClickListener paramOnClickListener, StatusBarNotification paramStatusBarNotification)
  {
    RemoteViews localRemoteViews = Notification.Builder.recoverBuilder(getContext(), this.mNotificationParent.getStatusBarNotification().getNotification()).makeNotificationHeader();
    if (this.mNotificationHeader == null)
    {
      this.mNotificationHeader = ((NotificationHeaderView)localRemoteViews.apply(getContext(), this));
      this.mNotificationHeader.findViewById(16909240).setVisibility(0);
      this.mNotificationHeader.setOnClickListener(paramOnClickListener);
      this.mNotificationHeaderWrapper = NotificationViewWrapper.wrap(getContext(), this.mNotificationHeader, this.mNotificationParent);
      addView(this.mNotificationHeader, 0);
      invalidate();
    }
    for (;;)
    {
      updateChildrenHeaderAppearance();
      return;
      localRemoteViews.reapply(getContext(), this.mNotificationHeader);
      this.mNotificationHeaderWrapper.notifyContentUpdated(paramStatusBarNotification);
    }
  }
  
  public void removeNotification(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    int i = this.mChildren.indexOf(paramExpandableNotificationRow);
    this.mChildren.remove(paramExpandableNotificationRow);
    removeView(paramExpandableNotificationRow);
    final View localView = (View)this.mDividers.remove(i);
    removeView(localView);
    getOverlay().add(localView);
    CrossFadeHelper.fadeOut(localView, new Runnable()
    {
      public void run()
      {
        NotificationChildrenContainer.this.getOverlay().remove(localView);
      }
    });
    paramExpandableNotificationRow.setSystemChildExpanded(false);
    paramExpandableNotificationRow.setUserLocked(false);
    updateGroupOverflow();
    if (!paramExpandableNotificationRow.isRemoved()) {
      this.mHeaderUtil.restoreNotificationHeader(paramExpandableNotificationRow);
    }
  }
  
  public void setActualHeight(int paramInt)
  {
    if (!this.mUserLocked) {
      return;
    }
    this.mActualHeight = paramInt;
    float f1 = getGroupExpandFraction();
    int j = getMaxAllowedVisibleChildren(true);
    int k = this.mChildren.size();
    paramInt = 0;
    if (paramInt < k)
    {
      ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)this.mChildren.get(paramInt);
      int i;
      label75:
      float f2;
      if (localExpandableNotificationRow.isExpanded(true))
      {
        i = localExpandableNotificationRow.getMaxExpandHeight();
        f2 = i;
        if (paramInt >= j) {
          break label128;
        }
        localExpandableNotificationRow.setActualHeight((int)NotificationUtils.interpolate(localExpandableNotificationRow.getShowingLayout().getMinHeight(false), f2, f1), false);
      }
      for (;;)
      {
        paramInt += 1;
        break;
        i = localExpandableNotificationRow.getShowingLayout().getMinHeight(true);
        break label75;
        label128:
        localExpandableNotificationRow.setActualHeight((int)f2, false);
      }
    }
  }
  
  public void setChildrenExpanded(boolean paramBoolean)
  {
    this.mChildrenExpanded = paramBoolean;
    updateExpansionStates();
    if (this.mNotificationHeader != null) {
      this.mNotificationHeader.setExpanded(paramBoolean);
    }
    int j = this.mChildren.size();
    int i = 0;
    while (i < j)
    {
      ((ExpandableNotificationRow)this.mChildren.get(i)).setChildrenExpanded(paramBoolean, false);
      i += 1;
    }
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    if (this.mOverflowNumber != null) {
      this.mOverflowInvertHelper.setInverted(paramBoolean1, paramBoolean2, paramLong);
    }
    this.mNotificationHeaderWrapper.setDark(paramBoolean1, paramBoolean2, paramLong);
  }
  
  public void setNotificationParent(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    this.mNotificationParent = paramExpandableNotificationRow;
    this.mHeaderUtil = new NotificationHeaderUtil(this.mNotificationParent);
  }
  
  public void setUserLocked(boolean paramBoolean)
  {
    this.mUserLocked = paramBoolean;
    int j = this.mChildren.size();
    int i = 0;
    while (i < j)
    {
      ((ExpandableNotificationRow)this.mChildren.get(i)).setUserLocked(paramBoolean);
      i += 1;
    }
  }
  
  public void startAnimationToState(StackScrollState paramStackScrollState, StackStateAnimator paramStackStateAnimator, long paramLong1, long paramLong2)
  {
    int i = this.mChildren.size();
    ViewState localViewState = new ViewState();
    float f3 = getGroupExpandFraction();
    boolean bool1;
    label48:
    ExpandableNotificationRow localExpandableNotificationRow;
    View localView;
    float f1;
    label149:
    float f2;
    if (!this.mUserLocked)
    {
      bool1 = this.mNotificationParent.isGroupExpansionChanging();
      i -= 1;
      if (i < 0) {
        break label262;
      }
      localExpandableNotificationRow = (ExpandableNotificationRow)this.mChildren.get(i);
      StackViewState localStackViewState = paramStackScrollState.getViewStateForView(localExpandableNotificationRow);
      paramStackStateAnimator.startStackAnimations(localExpandableNotificationRow, localStackViewState, paramStackScrollState, -1, paramLong1);
      localView = (View)this.mDividers.get(i);
      localViewState.initFrom(localView);
      localViewState.yTranslation = (localStackViewState.yTranslation - this.mDividerHeight);
      if ((!this.mChildrenExpanded) || (localStackViewState.alpha == 0.0F)) {
        break label250;
      }
      f1 = 0.5F;
      f2 = f1;
      if (this.mUserLocked)
      {
        f2 = f1;
        if (localStackViewState.alpha != 0.0F) {
          f2 = NotificationUtils.interpolate(0.0F, 0.5F, Math.min(localStackViewState.alpha, f3));
        }
      }
      if (!bool1) {
        break label256;
      }
    }
    label250:
    label256:
    for (boolean bool2 = false;; bool2 = true)
    {
      localViewState.hidden = bool2;
      localViewState.alpha = f2;
      paramStackStateAnimator.startViewAnimations(localView, localViewState, paramLong1, paramLong2);
      localExpandableNotificationRow.setFakeShadowIntensity(0.0F, 0.0F, 0, 0);
      i -= 1;
      break label48;
      bool1 = true;
      break;
      f1 = 0.0F;
      break label149;
    }
    label262:
    if (this.mOverflowNumber != null)
    {
      if (this.mNeverAppliedGroupState)
      {
        f1 = this.mGroupOverFlowState.alpha;
        this.mGroupOverFlowState.alpha = 0.0F;
        paramStackScrollState.applyViewState(this.mOverflowNumber, this.mGroupOverFlowState);
        this.mGroupOverFlowState.alpha = f1;
        this.mNeverAppliedGroupState = false;
      }
      paramStackStateAnimator.startViewAnimations(this.mOverflowNumber, this.mGroupOverFlowState, paramLong1, paramLong2);
    }
    if (this.mNotificationHeader != null) {
      paramStackScrollState.applyViewState(this.mNotificationHeader, this.mHeaderViewState);
    }
  }
  
  public void updateChildrenHeaderAppearance()
  {
    this.mHeaderUtil.updateChildrenHeaderAppearance();
  }
  
  public void updateGroupOverflow()
  {
    int i = this.mChildren.size();
    int j = getMaxAllowedVisibleChildren(true);
    if (i > j)
    {
      this.mOverflowNumber = this.mHybridGroupManager.bindOverflowNumber(this.mOverflowNumber, i - j);
      if (this.mOverflowInvertHelper == null) {
        this.mOverflowInvertHelper = new ViewInvertHelper(this.mOverflowNumber, 700L);
      }
      if (this.mGroupOverFlowState == null)
      {
        this.mGroupOverFlowState = new ViewState();
        this.mNeverAppliedGroupState = true;
      }
    }
    while (this.mOverflowNumber == null) {
      return;
    }
    removeView(this.mOverflowNumber);
    if (isShown())
    {
      final TextView localTextView = this.mOverflowNumber;
      addTransientView(localTextView, getTransientViewCount());
      CrossFadeHelper.fadeOut(localTextView, new Runnable()
      {
        public void run()
        {
          NotificationChildrenContainer.this.removeTransientView(localTextView);
        }
      });
    }
    this.mOverflowNumber = null;
    this.mOverflowInvertHelper = null;
    this.mGroupOverFlowState = null;
  }
  
  public void updateHeaderForExpansion(boolean paramBoolean)
  {
    if (this.mNotificationHeader != null)
    {
      if (paramBoolean)
      {
        ColorDrawable localColorDrawable = new ColorDrawable();
        localColorDrawable.setColor(this.mNotificationParent.calculateBgColor());
        this.mNotificationHeader.setHeaderBackgroundDrawable(localColorDrawable);
      }
    }
    else {
      return;
    }
    this.mNotificationHeader.setHeaderBackgroundDrawable(null);
  }
  
  public void updateHeaderVisibility(int paramInt)
  {
    if (this.mNotificationHeader != null) {
      this.mNotificationHeader.setVisibility(paramInt);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\NotificationChildrenContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */