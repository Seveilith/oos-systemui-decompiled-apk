package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatProperty;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.view.ViewStub.OnInflateListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.Chronometer;
import android.widget.ImageView;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.NotificationColorUtil;
import com.android.systemui.classifier.FalsingManager;
import com.android.systemui.plugin.LSState;
import com.android.systemui.statusbar.notification.HybridNotificationView;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.stack.NotificationChildrenContainer;
import com.android.systemui.statusbar.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.stack.StackScrollState;
import com.android.systemui.statusbar.stack.StackStateAnimator;
import com.android.systemui.statusbar.stack.StackViewState;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExpandableNotificationRow
  extends ActivatableNotificationView
{
  private static final Property<ExpandableNotificationRow, Float> TRANSLATE_CONTENT = new FloatProperty("translate")
  {
    public Float get(ExpandableNotificationRow paramAnonymousExpandableNotificationRow)
    {
      return Float.valueOf(paramAnonymousExpandableNotificationRow.getTranslation());
    }
    
    public void setValue(ExpandableNotificationRow paramAnonymousExpandableNotificationRow, float paramAnonymousFloat)
    {
      paramAnonymousExpandableNotificationRow.setTranslation(paramAnonymousFloat);
    }
  };
  private String mAppName;
  private View mChildAfterViewWhenDismissed;
  private NotificationChildrenContainer mChildrenContainer;
  private ViewStub mChildrenContainerStub;
  private boolean mChildrenExpanded;
  private boolean mDismissed;
  private NotificationData.Entry mEntry;
  private View.OnClickListener mExpandClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if ((!ExpandableNotificationRow.-get10(ExpandableNotificationRow.this)) && (ExpandableNotificationRow.-get5(ExpandableNotificationRow.this).isSummaryOfGroup(ExpandableNotificationRow.-get11(ExpandableNotificationRow.this))))
      {
        ExpandableNotificationRow.-set2(ExpandableNotificationRow.this, true);
        bool1 = ExpandableNotificationRow.-get5(ExpandableNotificationRow.this).isGroupExpanded(ExpandableNotificationRow.-get11(ExpandableNotificationRow.this));
        boolean bool2 = ExpandableNotificationRow.-get5(ExpandableNotificationRow.this).toggleGroupExpansion(ExpandableNotificationRow.-get11(ExpandableNotificationRow.this));
        ExpandableNotificationRow.-get7(ExpandableNotificationRow.this).onExpandClicked(ExpandableNotificationRow.-get3(ExpandableNotificationRow.this), bool2);
        MetricsLogger.action(ExpandableNotificationRow.-get2(ExpandableNotificationRow.this), 408, bool2);
        ExpandableNotificationRow.-wrap0(ExpandableNotificationRow.this, true, bool1);
        return;
      }
      if (paramAnonymousView.isAccessibilityFocused()) {
        ExpandableNotificationRow.-get8(ExpandableNotificationRow.this).setFocusOnVisibilityChange();
      }
      if (ExpandableNotificationRow.this.isPinned())
      {
        if (ExpandableNotificationRow.-get4(ExpandableNotificationRow.this)) {}
        for (bool1 = false;; bool1 = true)
        {
          ExpandableNotificationRow.-set1(ExpandableNotificationRow.this, bool1);
          ExpandableNotificationRow.this.notifyHeightChanged(true);
          ExpandableNotificationRow.-get7(ExpandableNotificationRow.this).onExpandClicked(ExpandableNotificationRow.-get3(ExpandableNotificationRow.this), bool1);
          MetricsLogger.action(ExpandableNotificationRow.-get2(ExpandableNotificationRow.this), 407, bool1);
          return;
        }
      }
      if (ExpandableNotificationRow.this.isExpanded()) {}
      for (boolean bool1 = false;; bool1 = true)
      {
        ExpandableNotificationRow.this.setUserExpanded(bool1);
        break;
      }
    }
  };
  private boolean mExpandable;
  private boolean mExpandedWhenPinned;
  private FalsingManager mFalsingManager;
  private boolean mForceUnlocked;
  private boolean mGroupExpansionChanging;
  private NotificationGroupManager mGroupManager;
  private View mGroupParentWhenDismissed;
  private NotificationGuts mGuts;
  private ViewStub mGutsStub;
  private boolean mHasUserChangedExpansion;
  private int mHeadsUpHeight;
  private HeadsUpManager mHeadsUpManager;
  private boolean mHeadsupDisappearRunning;
  private boolean mHideSensitiveForIntrinsicHeight;
  private boolean mIconAnimationRunning;
  private int mIncreasedPaddingBetweenElements;
  private boolean mIsHeadsUp;
  private boolean mIsPinned;
  private boolean mIsSummaryWithChildren;
  private boolean mIsSystemChildExpanded;
  private boolean mIsSystemExpanded;
  private boolean mJustClicked;
  private boolean mKeepInParent;
  private boolean mLastChronometerRunning = true;
  private ExpansionLogger mLogger;
  private String mLoggingKey;
  private int mMaxExpandHeight;
  private int mMaxHeadsUpHeight;
  private int mMaxHeadsUpHeightLegacy;
  private int mNotificationColor;
  private int mNotificationMaxHeight;
  private int mNotificationMinHeight;
  private int mNotificationMinHeightLegacy;
  private ExpandableNotificationRow mNotificationParent;
  private View.OnClickListener mOnClickListener;
  private OnExpandClickListener mOnExpandClickListener;
  private boolean mOnKeyguard;
  private NotificationContentView mPrivateLayout;
  private NotificationContentView mPublicLayout;
  private boolean mRefocusOnDismiss;
  private boolean mRemoved;
  private boolean mSensitive;
  private boolean mSensitiveHiddenInGeneral;
  private NotificationSettingsIconRow mSettingsIconRow;
  private ViewStub mSettingsIconRowStub;
  private boolean mShowNoBackground;
  private boolean mShowingPublic;
  private boolean mShowingPublicInitialized;
  private StatusBarNotification mStatusBarNotification;
  private Animator mTranslateAnim;
  private ArrayList<View> mTranslateableViews;
  private boolean mUserExpanded;
  private boolean mUserLocked;
  private View mVetoButton;
  
  public ExpandableNotificationRow(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mFalsingManager = FalsingManager.getInstance(paramContext);
    initDimens();
  }
  
  private void animateShowingPublic(long paramLong1, long paramLong2)
  {
    Object localObject1;
    final View[] arrayOfView;
    Object localObject2;
    if (this.mIsSummaryWithChildren)
    {
      localObject1 = new View[1];
      localObject1[0] = this.mChildrenContainer;
      arrayOfView = new View[1];
      arrayOfView[0] = this.mPublicLayout;
      if (!this.mShowingPublic) {
        break label150;
      }
      localObject2 = localObject1;
      label46:
      if (!this.mShowingPublic) {
        break label157;
      }
      localObject1 = arrayOfView;
    }
    label150:
    label157:
    for (;;)
    {
      i = 0;
      j = localObject2.length;
      while (i < j)
      {
        arrayOfView = localObject2[i];
        arrayOfView.setVisibility(0);
        arrayOfView.animate().cancel();
        arrayOfView.animate().alpha(0.0F).setStartDelay(paramLong1).setDuration(paramLong2).withEndAction(new Runnable()
        {
          public void run()
          {
            arrayOfView.setVisibility(4);
          }
        });
        i += 1;
      }
      localObject1 = new View[1];
      localObject1[0] = this.mPrivateLayout;
      break;
      localObject2 = arrayOfView;
      break label46;
    }
    int i = 0;
    int j = localObject1.length;
    while (i < j)
    {
      localObject2 = localObject1[i];
      ((View)localObject2).setVisibility(0);
      ((View)localObject2).setAlpha(0.0F);
      ((View)localObject2).animate().cancel();
      ((View)localObject2).animate().alpha(1.0F).setStartDelay(paramLong1).setDuration(paramLong2);
      i += 1;
    }
  }
  
  private int getFontScaledHeight(int paramInt)
  {
    paramInt = getResources().getDimensionPixelSize(paramInt);
    float f = Math.max(1.0F, getResources().getDisplayMetrics().scaledDensity / getResources().getDisplayMetrics().density);
    return (int)(paramInt * f);
  }
  
  private NotificationHeaderView getVisibleNotificationHeader()
  {
    if ((!this.mIsSummaryWithChildren) || (this.mShowingPublic)) {
      return getShowingLayout().getVisibleNotificationHeader();
    }
    return this.mChildrenContainer.getHeaderView();
  }
  
  private void initDimens()
  {
    this.mNotificationMinHeightLegacy = getFontScaledHeight(2131755370);
    this.mNotificationMinHeight = getFontScaledHeight(2131755369);
    this.mNotificationMaxHeight = getFontScaledHeight(2131755371);
    this.mMaxHeadsUpHeightLegacy = getFontScaledHeight(2131755372);
    this.mMaxHeadsUpHeight = getFontScaledHeight(2131755373);
    this.mIncreasedPaddingBetweenElements = getResources().getDimensionPixelSize(2131755463);
  }
  
  private boolean isSystemChildExpanded()
  {
    return this.mIsSystemChildExpanded;
  }
  
  private void logExpansionEvent(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = isExpanded();
    if (this.mIsSummaryWithChildren) {
      bool = this.mGroupManager.isGroupExpanded(this.mStatusBarNotification);
    }
    if ((paramBoolean2 != bool) && (this.mLogger != null)) {
      this.mLogger.logNotificationExpansion(this.mLoggingKey, paramBoolean1, bool);
    }
  }
  
  private void onChildrenCountChanged()
  {
    boolean bool;
    if ((BaseStatusBar.ENABLE_CHILD_NOTIFICATIONS) && (this.mChildrenContainer != null)) {
      if (this.mChildrenContainer.getNotificationChildCount() > 0) {
        bool = true;
      }
    }
    for (;;)
    {
      this.mIsSummaryWithChildren = bool;
      if ((this.mIsSummaryWithChildren) && (this.mChildrenContainer.getHeaderView() == null)) {
        this.mChildrenContainer.recreateNotificationHeader(this.mExpandClickListener, this.mEntry.notification);
      }
      getShowingLayout().updateBackgroundColor(false);
      this.mPrivateLayout.updateExpandButtons(isExpandable());
      updateChildrenHeaderAppearance();
      updateChildrenVisibility();
      return;
      bool = false;
      continue;
      bool = false;
    }
  }
  
  private void setChronometerRunning(boolean paramBoolean, NotificationContentView paramNotificationContentView)
  {
    if (paramNotificationContentView != null) {
      if (paramBoolean) {
        break label49;
      }
    }
    label49:
    for (paramBoolean = isPinned();; paramBoolean = true)
    {
      View localView1 = paramNotificationContentView.getContractedChild();
      View localView2 = paramNotificationContentView.getExpandedChild();
      paramNotificationContentView = paramNotificationContentView.getHeadsUpChild();
      setChronometerRunningForChild(paramBoolean, localView1);
      setChronometerRunningForChild(paramBoolean, localView2);
      setChronometerRunningForChild(paramBoolean, paramNotificationContentView);
      return;
    }
  }
  
  private void setChronometerRunningForChild(boolean paramBoolean, View paramView)
  {
    if (paramView != null)
    {
      paramView = paramView.findViewById(16909239);
      if ((paramView instanceof Chronometer)) {
        ((Chronometer)paramView).setStarted(paramBoolean);
      }
    }
  }
  
  private void setIconAnimationRunning(boolean paramBoolean, NotificationContentView paramNotificationContentView)
  {
    if (paramNotificationContentView != null)
    {
      View localView1 = paramNotificationContentView.getContractedChild();
      View localView2 = paramNotificationContentView.getExpandedChild();
      paramNotificationContentView = paramNotificationContentView.getHeadsUpChild();
      setIconAnimationRunningForChild(paramBoolean, localView1);
      setIconAnimationRunningForChild(paramBoolean, localView2);
      setIconAnimationRunningForChild(paramBoolean, paramNotificationContentView);
    }
  }
  
  private void setIconAnimationRunningForChild(boolean paramBoolean, View paramView)
  {
    if (paramView != null)
    {
      setIconRunning((ImageView)paramView.findViewById(16908294), paramBoolean);
      setIconRunning((ImageView)paramView.findViewById(16908356), paramBoolean);
    }
  }
  
  private void setIconRunning(ImageView paramImageView, boolean paramBoolean)
  {
    if (paramImageView != null)
    {
      paramImageView = paramImageView.getDrawable();
      if (!(paramImageView instanceof AnimationDrawable)) {
        break label35;
      }
      paramImageView = (AnimationDrawable)paramImageView;
      if (!paramBoolean) {
        break label30;
      }
      paramImageView.start();
    }
    label30:
    label35:
    while (!(paramImageView instanceof AnimatedVectorDrawable))
    {
      return;
      paramImageView.stop();
      return;
    }
    paramImageView = (AnimatedVectorDrawable)paramImageView;
    if (paramBoolean)
    {
      paramImageView.start();
      return;
    }
    paramImageView.stop();
  }
  
  private void updateChildrenVisibility()
  {
    int j = 0;
    Object localObject = this.mPrivateLayout;
    if ((this.mShowingPublic) || (this.mIsSummaryWithChildren))
    {
      i = 4;
      ((NotificationContentView)localObject).setVisibility(i);
      if (this.mChildrenContainer != null)
      {
        localObject = this.mChildrenContainer;
        if ((this.mShowingPublic) || (!this.mIsSummaryWithChildren)) {
          break label97;
        }
        i = 0;
        label56:
        ((NotificationChildrenContainer)localObject).setVisibility(i);
        localObject = this.mChildrenContainer;
        if ((this.mShowingPublic) || (!this.mIsSummaryWithChildren)) {
          break label102;
        }
      }
    }
    label97:
    label102:
    for (int i = j;; i = 4)
    {
      ((NotificationChildrenContainer)localObject).updateHeaderVisibility(i);
      updateLimits();
      return;
      i = 0;
      break;
      i = 4;
      break label56;
    }
  }
  
  private void updateClickAndFocus()
  {
    boolean bool1;
    if (isChildInGroup())
    {
      bool1 = isGroupExpanded();
      if (this.mOnClickListener == null) {
        break label53;
      }
    }
    label53:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      if (isFocusable() != bool1) {
        setFocusable(bool1);
      }
      if (isClickable() != bool2) {
        setClickable(bool2);
      }
      return;
      bool1 = true;
      break;
    }
  }
  
  private void updateLimits()
  {
    updateLimitsForView(this.mPrivateLayout);
    updateLimitsForView(this.mPublicLayout);
  }
  
  private void updateLimitsForView(NotificationContentView paramNotificationContentView)
  {
    int m = 0;
    int j;
    if (paramNotificationContentView.getContractedChild().getId() != 16909242)
    {
      j = 1;
      if (this.mEntry.targetSdk >= 24) {
        break label113;
      }
      i = 1;
      label32:
      if ((j != 0) && (i != 0) && (!this.mIsSummaryWithChildren)) {
        break label118;
      }
      j = this.mNotificationMinHeight;
      label52:
      int k = m;
      if (paramNotificationContentView.getHeadsUpChild() != null)
      {
        k = m;
        if (paramNotificationContentView.getHeadsUpChild().getId() != 16909242) {
          k = 1;
        }
      }
      if ((k == 0) || (i == 0)) {
        break label126;
      }
    }
    label113:
    label118:
    label126:
    for (int i = this.mMaxHeadsUpHeightLegacy;; i = this.mMaxHeadsUpHeight)
    {
      paramNotificationContentView.setHeights(j, i, this.mNotificationMaxHeight);
      return;
      j = 0;
      break;
      i = 0;
      break label32;
      j = this.mNotificationMinHeightLegacy;
      break label52;
    }
  }
  
  private void updateMaxHeights()
  {
    int j = getIntrinsicHeight();
    Object localObject2 = this.mPrivateLayout.getExpandedChild();
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = this.mPrivateLayout.getContractedChild();
    }
    this.mMaxExpandHeight = ((View)localObject1).getHeight();
    localObject2 = this.mPrivateLayout.getHeadsUpChild();
    localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = this.mPrivateLayout.getContractedChild();
    }
    boolean bool = false;
    localObject2 = LSState.getInstance().getPhoneStatusBar().getNotificationData();
    if (localObject2 != null) {
      bool = ((NotificationData)localObject2).isLock(this.mStatusBarNotification.getKey());
    }
    if (bool) {}
    for (int i = this.mPublicLayout.getMinHeight();; i = ((View)localObject1).getHeight())
    {
      this.mHeadsUpHeight = i;
      if (j != getIntrinsicHeight()) {
        notifyHeightChanged(true);
      }
      return;
    }
  }
  
  private void updateNotificationColor()
  {
    this.mNotificationColor = NotificationColorUtil.resolveContrastColor(this.mContext, getStatusBarNotification().getNotification().color);
  }
  
  public void addChildNotification(ExpandableNotificationRow paramExpandableNotificationRow, int paramInt)
  {
    if (this.mChildrenContainer == null) {
      this.mChildrenContainerStub.inflate();
    }
    this.mChildrenContainer.addNotification(paramExpandableNotificationRow, paramInt);
    onChildrenCountChanged();
    paramExpandableNotificationRow.setIsChildInGroup(true, this);
  }
  
  public void animateTranslateNotification(float paramFloat)
  {
    if (this.mTranslateAnim != null) {
      this.mTranslateAnim.cancel();
    }
    this.mTranslateAnim = getTranslateViewAnimator(paramFloat, null);
    if (this.mTranslateAnim != null) {
      this.mTranslateAnim.start();
    }
  }
  
  public boolean applyChildOrder(List<ExpandableNotificationRow> paramList)
  {
    if (this.mChildrenContainer != null) {
      return this.mChildrenContainer.applyChildOrder(paramList);
    }
    return false;
  }
  
  public void applyChildrenState(StackScrollState paramStackScrollState)
  {
    if (this.mIsSummaryWithChildren) {
      this.mChildrenContainer.applyState(paramStackScrollState);
    }
  }
  
  public boolean areChildrenExpanded()
  {
    return this.mChildrenExpanded;
  }
  
  public boolean areGutsExposed()
  {
    if (this.mGuts != null) {
      return this.mGuts.areGutsExposed();
    }
    return false;
  }
  
  public boolean canViewBeDismissed()
  {
    boolean bool2 = true;
    boolean bool1;
    if (isClearable())
    {
      bool1 = bool2;
      if (this.mShowingPublic)
      {
        bool1 = bool2;
        if (!this.mSensitiveHiddenInGeneral) {}
      }
    }
    else
    {
      bool1 = false;
    }
    return bool1;
  }
  
  public void closeRemoteInput()
  {
    this.mPrivateLayout.closeRemoteInput();
    this.mPublicLayout.closeRemoteInput();
  }
  
  protected boolean disallowSingleClick(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    NotificationHeaderView localNotificationHeaderView = getVisibleNotificationHeader();
    if (localNotificationHeaderView != null) {
      return localNotificationHeaderView.isInTouchRect(f1 - getTranslation(), f2);
    }
    return super.disallowSingleClick(paramMotionEvent);
  }
  
  public View getChildAfterViewWhenDismissed()
  {
    return this.mChildAfterViewWhenDismissed;
  }
  
  public NotificationChildrenContainer getChildrenContainer()
  {
    return this.mChildrenContainer;
  }
  
  public void getChildrenStates(StackScrollState paramStackScrollState)
  {
    if (this.mIsSummaryWithChildren)
    {
      StackViewState localStackViewState = paramStackScrollState.getViewStateForView(this);
      this.mChildrenContainer.getState(paramStackScrollState, localStackViewState);
    }
  }
  
  public int getCollapsedHeight()
  {
    if ((!this.mIsSummaryWithChildren) || (this.mShowingPublic)) {
      return getMinHeight();
    }
    return this.mChildrenContainer.getCollapsedHeight();
  }
  
  protected View getContentView()
  {
    if ((!this.mIsSummaryWithChildren) || (this.mShowingPublic)) {
      return getShowingLayout();
    }
    return this.mChildrenContainer;
  }
  
  public int getExtraBottomPadding()
  {
    if ((this.mIsSummaryWithChildren) && (isGroupExpanded())) {
      return this.mIncreasedPaddingBetweenElements;
    }
    return 0;
  }
  
  public View getGroupParentWhenDismissed()
  {
    return this.mGroupParentWhenDismissed;
  }
  
  public NotificationGuts getGuts()
  {
    return this.mGuts;
  }
  
  public float getIncreasedPaddingAmount()
  {
    if (this.mIsSummaryWithChildren)
    {
      if (isGroupExpanded()) {
        return 1.0F;
      }
      if (isUserLocked()) {
        return this.mChildrenContainer.getGroupExpandFraction();
      }
    }
    return 0.0F;
  }
  
  public int getIntrinsicHeight()
  {
    if (isUserLocked()) {
      return getActualHeight();
    }
    if ((this.mGuts != null) && (this.mGuts.areGutsExposed())) {
      return this.mGuts.getHeight();
    }
    if ((!isChildInGroup()) || (isGroupExpanded()))
    {
      if ((this.mSensitive) && (this.mHideSensitiveForIntrinsicHeight)) {
        return getMinHeight();
      }
    }
    else {
      return this.mPrivateLayout.getMinHeight();
    }
    if ((!this.mIsSummaryWithChildren) || (this.mOnKeyguard))
    {
      if ((!this.mIsHeadsUp) && (!this.mHeadsupDisappearRunning)) {
        break label165;
      }
      if ((isPinned()) || (this.mHeadsupDisappearRunning)) {
        return getPinnedHeadsUpHeight(true);
      }
    }
    else
    {
      return this.mChildrenContainer.getIntrinsicHeight();
    }
    if (isExpanded()) {
      return Math.max(getMaxExpandHeight(), this.mHeadsUpHeight);
    }
    return Math.max(getCollapsedHeight(), this.mHeadsUpHeight);
    label165:
    if (isExpanded()) {
      return getMaxExpandHeight();
    }
    return getCollapsedHeight();
  }
  
  public int getMaxContentHeight()
  {
    if ((!this.mIsSummaryWithChildren) || (this.mShowingPublic)) {
      return getShowingLayout().getMaxHeight();
    }
    return this.mChildrenContainer.getMaxContentHeight();
  }
  
  public int getMaxExpandHeight()
  {
    return this.mMaxExpandHeight;
  }
  
  public int getMinHeight()
  {
    if ((this.mIsHeadsUp) && (this.mHeadsUpManager.isTrackingHeadsUp())) {
      return getPinnedHeadsUpHeight(false);
    }
    if ((!this.mIsSummaryWithChildren) || (isGroupExpanded())) {}
    while (this.mIsHeadsUp)
    {
      return this.mHeadsUpHeight;
      if (!this.mShowingPublic) {
        return this.mChildrenContainer.getMinHeight();
      }
    }
    return getShowingLayout().getMinHeight();
  }
  
  public List<ExpandableNotificationRow> getNotificationChildren()
  {
    if (this.mChildrenContainer == null) {
      return null;
    }
    return this.mChildrenContainer.getNotificationChildren();
  }
  
  public int getNotificationColor()
  {
    return this.mNotificationColor;
  }
  
  public NotificationHeaderView getNotificationHeader()
  {
    if (this.mIsSummaryWithChildren) {
      return this.mChildrenContainer.getHeaderView();
    }
    return this.mPrivateLayout.getNotificationHeader();
  }
  
  public ExpandableNotificationRow getNotificationParent()
  {
    return this.mNotificationParent;
  }
  
  public int getPinnedHeadsUpHeight(boolean paramBoolean)
  {
    if (this.mIsSummaryWithChildren) {
      return this.mChildrenContainer.getIntrinsicHeight();
    }
    if (this.mExpandedWhenPinned) {
      return Math.max(getMaxExpandHeight(), this.mHeadsUpHeight);
    }
    if (paramBoolean) {
      return Math.max(getCollapsedHeight(), this.mHeadsUpHeight);
    }
    return this.mHeadsUpHeight;
  }
  
  public int getPositionOfChild(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    if (this.mIsSummaryWithChildren) {
      return this.mChildrenContainer.getPositionInLinearLayout(paramExpandableNotificationRow);
    }
    return 0;
  }
  
  public NotificationContentView getPrivateLayout()
  {
    return this.mPrivateLayout;
  }
  
  public NotificationContentView getPublicLayout()
  {
    return this.mPublicLayout;
  }
  
  public NotificationSettingsIconRow getSettingsRow()
  {
    if (this.mSettingsIconRow == null) {
      this.mSettingsIconRowStub.inflate();
    }
    return this.mSettingsIconRow;
  }
  
  public NotificationContentView getShowingLayout()
  {
    if (this.mShowingPublic) {
      return this.mPublicLayout;
    }
    return this.mPrivateLayout;
  }
  
  public HybridNotificationView getSingleLineView()
  {
    return this.mPrivateLayout.getSingleLineView();
  }
  
  public float getSpaceForGear()
  {
    if (this.mSettingsIconRow != null) {
      return this.mSettingsIconRow.getSpaceForGear();
    }
    return 0.0F;
  }
  
  public StatusBarNotification getStatusBarNotification()
  {
    return this.mStatusBarNotification;
  }
  
  public Animator getTranslateViewAnimator(final float paramFloat, ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    if (this.mTranslateAnim != null) {
      this.mTranslateAnim.cancel();
    }
    if (areGutsExposed()) {
      return null;
    }
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this, TRANSLATE_CONTENT, new float[] { paramFloat });
    if (paramAnimatorUpdateListener != null) {
      localObjectAnimator.addUpdateListener(paramAnimatorUpdateListener);
    }
    localObjectAnimator.addListener(new AnimatorListenerAdapter()
    {
      boolean cancelled = false;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.cancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if ((!this.cancelled) && (ExpandableNotificationRow.-get9(ExpandableNotificationRow.this) != null) && (paramFloat == 0.0F))
        {
          ExpandableNotificationRow.-get9(ExpandableNotificationRow.this).resetState();
          ExpandableNotificationRow.-set6(ExpandableNotificationRow.this, null);
        }
      }
    });
    this.mTranslateAnim = localObjectAnimator;
    return localObjectAnimator;
  }
  
  public float getTranslation()
  {
    if ((this.mTranslateableViews != null) && (this.mTranslateableViews.size() > 0)) {
      return ((View)this.mTranslateableViews.get(0)).getTranslationX();
    }
    return 0.0F;
  }
  
  public ExpandableNotificationRow getViewAtPosition(float paramFloat)
  {
    ExpandableNotificationRow localExpandableNotificationRow;
    if ((this.mIsSummaryWithChildren) && (this.mChildrenExpanded))
    {
      localExpandableNotificationRow = this.mChildrenContainer.getViewAtPosition(paramFloat);
      if (localExpandableNotificationRow == null) {
        return this;
      }
    }
    else
    {
      return this;
    }
    return localExpandableNotificationRow;
  }
  
  protected boolean handleSlideBack()
  {
    if ((this.mSettingsIconRow != null) && (this.mSettingsIconRow.isVisible()))
    {
      animateTranslateNotification(0.0F);
      return true;
    }
    return false;
  }
  
  public boolean hasUserChangedExpansion()
  {
    return this.mHasUserChangedExpansion;
  }
  
  public void inflateGuts()
  {
    if (this.mGuts == null) {
      this.mGutsStub.inflate();
    }
  }
  
  public boolean isChildInGroup()
  {
    return this.mNotificationParent != null;
  }
  
  public boolean isClearable()
  {
    List localList;
    int i;
    if ((this.mStatusBarNotification != null) && (this.mStatusBarNotification.isClearable()))
    {
      if (this.mIsSummaryWithChildren)
      {
        localList = this.mChildrenContainer.getNotificationChildren();
        i = 0;
      }
    }
    else {
      while (i < localList.size())
      {
        if (!((ExpandableNotificationRow)localList.get(i)).isClearable())
        {
          return false;
          return false;
        }
        i += 1;
      }
    }
    return true;
  }
  
  public boolean isContentExpandable()
  {
    return getShowingLayout().isContentExpandable();
  }
  
  public boolean isDismissed()
  {
    return this.mDismissed;
  }
  
  public boolean isExpandable()
  {
    if ((!this.mIsSummaryWithChildren) || (this.mShowingPublic)) {
      return this.mExpandable;
    }
    return !this.mChildrenExpanded;
  }
  
  public boolean isExpanded()
  {
    return isExpanded(false);
  }
  
  public boolean isExpanded(boolean paramBoolean)
  {
    if ((!this.mOnKeyguard) || (paramBoolean))
    {
      if ((hasUserChangedExpansion()) || ((!isSystemExpanded()) && (!isSystemChildExpanded()))) {
        return isUserExpanded();
      }
      return true;
    }
    return false;
  }
  
  public boolean isGroupExpanded()
  {
    return this.mGroupManager.isGroupExpanded(this.mStatusBarNotification);
  }
  
  public boolean isGroupExpansionChanging()
  {
    if (isChildInGroup()) {
      return this.mNotificationParent.isGroupExpansionChanging();
    }
    return this.mGroupExpansionChanging;
  }
  
  public boolean isHeadsUp()
  {
    return this.mIsHeadsUp;
  }
  
  public boolean isOnKeyguard()
  {
    return this.mOnKeyguard;
  }
  
  public boolean isPinned()
  {
    return this.mIsPinned;
  }
  
  public boolean isRemoved()
  {
    return this.mRemoved;
  }
  
  public boolean isSummaryWithChildren()
  {
    return this.mIsSummaryWithChildren;
  }
  
  public boolean isSystemExpanded()
  {
    return this.mIsSystemExpanded;
  }
  
  public boolean isUserExpanded()
  {
    return this.mUserExpanded;
  }
  
  public boolean isUserLocked()
  {
    return (this.mUserLocked) && (!this.mForceUnlocked);
  }
  
  public boolean keepInParent()
  {
    return this.mKeepInParent;
  }
  
  public void makeActionsVisibile()
  {
    setUserExpanded(true, true);
    if (isChildInGroup()) {
      this.mGroupManager.setGroupExpanded(this.mStatusBarNotification, true);
    }
    notifyHeightChanged(false);
  }
  
  public boolean mustStayOnScreen()
  {
    return this.mIsHeadsUp;
  }
  
  public void notifyHeightChanged(boolean paramBoolean)
  {
    super.notifyHeightChanged(paramBoolean);
    NotificationContentView localNotificationContentView = getShowingLayout();
    if (!paramBoolean) {}
    for (paramBoolean = isUserLocked();; paramBoolean = true)
    {
      localNotificationContentView.requestSelectLayout(paramBoolean);
      return;
    }
  }
  
  protected void onAppearAnimationFinished(boolean paramBoolean)
  {
    super.onAppearAnimationFinished(paramBoolean);
    if (paramBoolean)
    {
      if (this.mChildrenContainer != null)
      {
        this.mChildrenContainer.setAlpha(1.0F);
        this.mChildrenContainer.setLayerType(0, null);
      }
      this.mPrivateLayout.setAlpha(1.0F);
      this.mPrivateLayout.setLayerType(0, null);
      this.mPublicLayout.setAlpha(1.0F);
      this.mPublicLayout.setLayerType(0, null);
    }
  }
  
  public void onExpandedByGesture(boolean paramBoolean)
  {
    int i = 409;
    if (this.mGroupManager.isSummaryOfGroup(getStatusBarNotification())) {
      i = 410;
    }
    MetricsLogger.action(this.mContext, i, paramBoolean);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mPublicLayout = ((NotificationContentView)findViewById(2131952292));
    this.mPublicLayout.setContainingNotification(this);
    this.mPrivateLayout = ((NotificationContentView)findViewById(2131952291));
    this.mPrivateLayout.setExpandClickListener(this.mExpandClickListener);
    this.mPrivateLayout.setContainingNotification(this);
    this.mPublicLayout.setExpandClickListener(this.mExpandClickListener);
    this.mSettingsIconRowStub = ((ViewStub)findViewById(2131952289));
    this.mSettingsIconRowStub.setOnInflateListener(new ViewStub.OnInflateListener()
    {
      public void onInflate(ViewStub paramAnonymousViewStub, View paramAnonymousView)
      {
        ExpandableNotificationRow.-set5(ExpandableNotificationRow.this, (NotificationSettingsIconRow)paramAnonymousView);
        ExpandableNotificationRow.-get9(ExpandableNotificationRow.this).setNotificationRowParent(ExpandableNotificationRow.this);
        ExpandableNotificationRow.-get9(ExpandableNotificationRow.this).setAppName(ExpandableNotificationRow.-get0(ExpandableNotificationRow.this));
      }
    });
    this.mGutsStub = ((ViewStub)findViewById(2131952296));
    this.mGutsStub.setOnInflateListener(new ViewStub.OnInflateListener()
    {
      public void onInflate(ViewStub paramAnonymousViewStub, View paramAnonymousView)
      {
        ExpandableNotificationRow.-set3(ExpandableNotificationRow.this, (NotificationGuts)paramAnonymousView);
        ExpandableNotificationRow.-get6(ExpandableNotificationRow.this).setClipTopAmount(ExpandableNotificationRow.this.getClipTopAmount());
        ExpandableNotificationRow.-get6(ExpandableNotificationRow.this).setActualHeight(ExpandableNotificationRow.this.getActualHeight());
        ExpandableNotificationRow.-set4(ExpandableNotificationRow.this, null);
      }
    });
    this.mChildrenContainerStub = ((ViewStub)findViewById(2131952294));
    this.mChildrenContainerStub.setOnInflateListener(new ViewStub.OnInflateListener()
    {
      public void onInflate(ViewStub paramAnonymousViewStub, View paramAnonymousView)
      {
        ExpandableNotificationRow.-set0(ExpandableNotificationRow.this, (NotificationChildrenContainer)paramAnonymousView);
        ExpandableNotificationRow.-get1(ExpandableNotificationRow.this).setNotificationParent(ExpandableNotificationRow.this);
        ExpandableNotificationRow.-get1(ExpandableNotificationRow.this).onNotificationUpdated();
        ExpandableNotificationRow.-get12(ExpandableNotificationRow.this).add(ExpandableNotificationRow.-get1(ExpandableNotificationRow.this));
      }
    });
    this.mVetoButton = findViewById(2131952293);
    this.mVetoButton.setImportantForAccessibility(2);
    this.mVetoButton.setContentDescription(this.mContext.getString(2131690170));
    this.mTranslateableViews = new ArrayList();
    int i = 0;
    while (i < getChildCount())
    {
      this.mTranslateableViews.add(getChildAt(i));
      i += 1;
    }
    this.mTranslateableViews.remove(this.mVetoButton);
    this.mTranslateableViews.remove(this.mSettingsIconRowStub);
    this.mTranslateableViews.remove(this.mChildrenContainerStub);
    this.mTranslateableViews.remove(this.mGutsStub);
  }
  
  public void onFinishedExpansionChange()
  {
    this.mGroupExpansionChanging = false;
    updateBackgroundForGroupState();
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (canViewBeDismissed()) {
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_DISMISS);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    updateMaxHeights();
    if (this.mSettingsIconRow != null) {
      this.mSettingsIconRow.updateVerticalLocation();
    }
  }
  
  public void onNotificationUpdated(NotificationData.Entry paramEntry)
  {
    this.mEntry = paramEntry;
    this.mStatusBarNotification = paramEntry.notification;
    this.mPrivateLayout.onNotificationUpdated(paramEntry);
    this.mPublicLayout.onNotificationUpdated(paramEntry);
    this.mShowingPublicInitialized = false;
    updateNotificationColor();
    if (this.mIsSummaryWithChildren)
    {
      this.mChildrenContainer.recreateNotificationHeader(this.mExpandClickListener, this.mEntry.notification);
      this.mChildrenContainer.onNotificationUpdated();
    }
    if (this.mIconAnimationRunning) {
      setIconAnimationRunning(true);
    }
    if (this.mNotificationParent != null) {
      this.mNotificationParent.updateChildrenHeaderAppearance();
    }
    onChildrenCountChanged();
    this.mPublicLayout.updateExpandButtons(true);
    updateLimits();
  }
  
  public boolean onRequestSendAccessibilityEventInternal(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    if (super.onRequestSendAccessibilityEventInternal(paramView, paramAccessibilityEvent))
    {
      paramView = AccessibilityEvent.obtain();
      onInitializeAccessibilityEvent(paramView);
      dispatchPopulateAccessibilityEvent(paramView);
      paramAccessibilityEvent.appendRecord(paramView);
      return true;
    }
    return false;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getActionMasked() != 0) || (!isChildInGroup()) || (isGroupExpanded())) {
      return super.onTouchEvent(paramMotionEvent);
    }
    return false;
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityActionInternal(paramInt, paramBundle)) {
      return true;
    }
    switch (paramInt)
    {
    default: 
      return false;
    }
    NotificationStackScrollLayout.performDismiss(this, this.mGroupManager, true);
    return true;
  }
  
  public void performDismiss()
  {
    this.mVetoButton.performClick();
  }
  
  public void prepareExpansionChanged(StackScrollState paramStackScrollState)
  {
    if (this.mIsSummaryWithChildren) {
      this.mChildrenContainer.prepareExpansionChanged(paramStackScrollState);
    }
  }
  
  public void reInflateViews()
  {
    initDimens();
    if ((this.mIsSummaryWithChildren) && (this.mChildrenContainer != null)) {
      this.mChildrenContainer.reInflateViews(this.mExpandClickListener, this.mEntry.notification);
    }
    Object localObject;
    int i;
    if (this.mGuts != null)
    {
      localObject = this.mGuts;
      i = indexOfChild((View)localObject);
      removeView((View)localObject);
      this.mGuts = ((NotificationGuts)LayoutInflater.from(this.mContext).inflate(2130968733, this, false));
      this.mGuts.setVisibility(((View)localObject).getVisibility());
      addView(this.mGuts, i);
    }
    if (this.mSettingsIconRow != null)
    {
      localObject = this.mSettingsIconRow;
      i = indexOfChild((View)localObject);
      removeView((View)localObject);
      this.mSettingsIconRow = ((NotificationSettingsIconRow)LayoutInflater.from(this.mContext).inflate(2130968737, this, false));
      this.mSettingsIconRow.setNotificationRowParent(this);
      this.mSettingsIconRow.setAppName(this.mAppName);
      this.mSettingsIconRow.setVisibility(((View)localObject).getVisibility());
      addView(this.mSettingsIconRow, i);
    }
    this.mPrivateLayout.reInflateViews();
    this.mPublicLayout.reInflateViews();
  }
  
  public void removeAllChildren()
  {
    ArrayList localArrayList = new ArrayList(this.mChildrenContainer.getNotificationChildren());
    int i = 0;
    if (i < localArrayList.size())
    {
      ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)localArrayList.get(i);
      if (localExpandableNotificationRow.keepInParent()) {}
      for (;;)
      {
        i += 1;
        break;
        this.mChildrenContainer.removeNotification(localExpandableNotificationRow);
        localExpandableNotificationRow.setIsChildInGroup(false, null);
      }
    }
    onChildrenCountChanged();
  }
  
  public void removeChildNotification(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    if (this.mChildrenContainer != null) {
      this.mChildrenContainer.removeNotification(paramExpandableNotificationRow);
    }
    onChildrenCountChanged();
    paramExpandableNotificationRow.setIsChildInGroup(false, null);
  }
  
  public void reset()
  {
    super.reset();
    boolean bool = isExpanded();
    this.mExpandable = false;
    this.mHasUserChangedExpansion = false;
    this.mUserLocked = false;
    this.mShowingPublic = false;
    this.mSensitive = false;
    this.mShowingPublicInitialized = false;
    this.mIsSystemExpanded = false;
    this.mOnKeyguard = false;
    this.mPublicLayout.reset();
    this.mPrivateLayout.reset();
    resetHeight();
    resetTranslation();
    logExpansionEvent(false, bool);
  }
  
  public void resetHeight()
  {
    onHeightReset();
    requestLayout();
  }
  
  public void resetTranslation()
  {
    if (this.mTranslateAnim != null) {
      this.mTranslateAnim.cancel();
    }
    if (this.mTranslateableViews != null)
    {
      int i = 0;
      while (i < this.mTranslateableViews.size())
      {
        ((View)this.mTranslateableViews.get(i)).setTranslationX(0.0F);
        i += 1;
      }
    }
    invalidateOutline();
    if (this.mSettingsIconRow != null) {
      this.mSettingsIconRow.resetState();
    }
  }
  
  public void resetUserExpansion()
  {
    this.mHasUserChangedExpansion = false;
    this.mUserExpanded = false;
  }
  
  public void setActualHeight(int paramInt, boolean paramBoolean)
  {
    super.setActualHeight(paramInt, paramBoolean);
    if ((this.mGuts != null) && (this.mGuts.areGutsExposed()))
    {
      this.mGuts.setActualHeight(paramInt);
      return;
    }
    int i = Math.max(getMinHeight(), paramInt);
    this.mPrivateLayout.setContentHeight(i);
    this.mPublicLayout.setContentHeight(i);
    if (this.mIsSummaryWithChildren) {
      this.mChildrenContainer.setActualHeight(paramInt);
    }
    if (this.mGuts != null) {
      this.mGuts.setActualHeight(paramInt);
    }
  }
  
  public void setActualHeightAnimating(boolean paramBoolean)
  {
    if (this.mPrivateLayout != null) {
      this.mPrivateLayout.setContentHeightAnimating(paramBoolean);
    }
  }
  
  public void setAppName(String paramString)
  {
    this.mAppName = paramString;
    if (this.mSettingsIconRow != null) {
      this.mSettingsIconRow.setAppName(this.mAppName);
    }
  }
  
  public void setChildrenExpanded(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mChildrenExpanded = paramBoolean1;
    if (this.mChildrenContainer != null) {
      this.mChildrenContainer.setChildrenExpanded(paramBoolean1);
    }
    updateBackgroundForGroupState();
    updateClickAndFocus();
  }
  
  public void setChronometerRunning(boolean paramBoolean)
  {
    this.mLastChronometerRunning = paramBoolean;
    setChronometerRunning(paramBoolean, this.mPrivateLayout);
    setChronometerRunning(paramBoolean, this.mPublicLayout);
    if (this.mChildrenContainer != null)
    {
      List localList = this.mChildrenContainer.getNotificationChildren();
      int i = 0;
      while (i < localList.size())
      {
        ((ExpandableNotificationRow)localList.get(i)).setChronometerRunning(paramBoolean);
        i += 1;
      }
    }
  }
  
  public void setClipToActualHeight(boolean paramBoolean)
  {
    boolean bool2 = true;
    if (!paramBoolean) {}
    for (boolean bool1 = isUserLocked();; bool1 = true)
    {
      super.setClipToActualHeight(bool1);
      NotificationContentView localNotificationContentView = getShowingLayout();
      bool1 = bool2;
      if (!paramBoolean) {
        bool1 = isUserLocked();
      }
      localNotificationContentView.setClipToActualHeight(bool1);
      return;
    }
  }
  
  public void setClipTopAmount(int paramInt)
  {
    super.setClipTopAmount(paramInt);
    this.mPrivateLayout.setClipTopAmount(paramInt);
    this.mPublicLayout.setClipTopAmount(paramInt);
    if (this.mGuts != null) {
      this.mGuts.setClipTopAmount(paramInt);
    }
  }
  
  public void setContentBackground(int paramInt, boolean paramBoolean, NotificationContentView paramNotificationContentView)
  {
    if (getShowingLayout() == paramNotificationContentView) {
      setTintColor(paramInt, paramBoolean);
    }
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    super.setDark(paramBoolean1, paramBoolean2, paramLong);
    NotificationContentView localNotificationContentView = getShowingLayout();
    if (localNotificationContentView != null) {
      localNotificationContentView.setDark(paramBoolean1, paramBoolean2, paramLong);
    }
    if (this.mIsSummaryWithChildren) {
      this.mChildrenContainer.setDark(paramBoolean1, paramBoolean2, paramLong);
    }
  }
  
  public void setDismissed(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mDismissed = paramBoolean1;
    this.mGroupParentWhenDismissed = this.mNotificationParent;
    this.mRefocusOnDismiss = paramBoolean2;
    this.mChildAfterViewWhenDismissed = null;
    if (isChildInGroup())
    {
      List localList = this.mNotificationParent.getNotificationChildren();
      int i = localList.indexOf(this);
      if ((i != -1) && (i < localList.size() - 1)) {
        this.mChildAfterViewWhenDismissed = ((View)localList.get(i + 1));
      }
    }
  }
  
  public void setExpandable(boolean paramBoolean)
  {
    this.mExpandable = paramBoolean;
    this.mPrivateLayout.updateExpandButtons(isExpandable());
  }
  
  public void setExpansionLogger(ExpansionLogger paramExpansionLogger, String paramString)
  {
    this.mLogger = paramExpansionLogger;
    this.mLoggingKey = paramString;
  }
  
  public void setForceUnlocked(boolean paramBoolean)
  {
    this.mForceUnlocked = paramBoolean;
    if (this.mIsSummaryWithChildren)
    {
      Iterator localIterator = getNotificationChildren().iterator();
      while (localIterator.hasNext()) {
        ((ExpandableNotificationRow)localIterator.next()).setForceUnlocked(paramBoolean);
      }
    }
  }
  
  public void setGroupExpansionChanging(boolean paramBoolean)
  {
    this.mGroupExpansionChanging = paramBoolean;
  }
  
  public void setGroupManager(NotificationGroupManager paramNotificationGroupManager)
  {
    this.mGroupManager = paramNotificationGroupManager;
    this.mPrivateLayout.setGroupManager(paramNotificationGroupManager);
  }
  
  public void setHeadsUp(boolean paramBoolean)
  {
    int i = getIntrinsicHeight();
    this.mIsHeadsUp = paramBoolean;
    this.mPrivateLayout.setHeadsUp(paramBoolean);
    if (this.mIsSummaryWithChildren) {
      this.mChildrenContainer.updateGroupOverflow();
    }
    if (i != getIntrinsicHeight()) {
      notifyHeightChanged(false);
    }
  }
  
  public void setHeadsUpManager(HeadsUpManager paramHeadsUpManager)
  {
    this.mHeadsUpManager = paramHeadsUpManager;
  }
  
  public void setHeadsupDisappearRunning(boolean paramBoolean)
  {
    this.mHeadsupDisappearRunning = paramBoolean;
    this.mPrivateLayout.setHeadsupDisappearRunning(paramBoolean);
  }
  
  public void setHideSensitive(boolean paramBoolean1, boolean paramBoolean2, long paramLong1, long paramLong2)
  {
    int i = 0;
    boolean bool = this.mShowingPublic;
    if (this.mSensitive) {}
    for (;;)
    {
      this.mShowingPublic = paramBoolean1;
      if ((!this.mShowingPublicInitialized) || (this.mShowingPublic != bool)) {
        break;
      }
      return;
      paramBoolean1 = false;
    }
    if (this.mPublicLayout.getChildCount() == 0) {
      return;
    }
    if (!paramBoolean2)
    {
      this.mPublicLayout.animate().cancel();
      this.mPrivateLayout.animate().cancel();
      if (this.mChildrenContainer != null)
      {
        this.mChildrenContainer.animate().cancel();
        this.mChildrenContainer.setAlpha(1.0F);
      }
      this.mPublicLayout.setAlpha(1.0F);
      this.mPrivateLayout.setAlpha(1.0F);
      NotificationContentView localNotificationContentView = this.mPublicLayout;
      if (this.mShowingPublic)
      {
        localNotificationContentView.setVisibility(i);
        updateChildrenVisibility();
      }
    }
    for (;;)
    {
      getShowingLayout().updateBackgroundColor(paramBoolean2);
      this.mPrivateLayout.updateExpandButtons(isExpandable());
      this.mShowingPublicInitialized = true;
      return;
      i = 4;
      break;
      animateShowingPublic(paramLong1, paramLong2);
    }
  }
  
  public void setHideSensitiveForIntrinsicHeight(boolean paramBoolean)
  {
    this.mHideSensitiveForIntrinsicHeight = paramBoolean;
    if (this.mIsSummaryWithChildren)
    {
      List localList = this.mChildrenContainer.getNotificationChildren();
      int i = 0;
      while (i < localList.size())
      {
        ((ExpandableNotificationRow)localList.get(i)).setHideSensitiveForIntrinsicHeight(paramBoolean);
        i += 1;
      }
    }
  }
  
  public void setIconAnimationRunning(boolean paramBoolean)
  {
    setIconAnimationRunning(paramBoolean, this.mPublicLayout);
    setIconAnimationRunning(paramBoolean, this.mPrivateLayout);
    if (this.mIsSummaryWithChildren)
    {
      setIconAnimationRunningForChild(paramBoolean, this.mChildrenContainer.getHeaderView());
      List localList = this.mChildrenContainer.getNotificationChildren();
      int i = 0;
      while (i < localList.size())
      {
        ((ExpandableNotificationRow)localList.get(i)).setIconAnimationRunning(paramBoolean);
        i += 1;
      }
    }
    this.mIconAnimationRunning = paramBoolean;
  }
  
  public void setIsChildInGroup(boolean paramBoolean, ExpandableNotificationRow paramExpandableNotificationRow)
  {
    if (BaseStatusBar.ENABLE_CHILD_NOTIFICATIONS) {
      if (!paramBoolean) {
        break label55;
      }
    }
    for (;;)
    {
      this.mNotificationParent = paramExpandableNotificationRow;
      this.mPrivateLayout.setIsChildInGroup(paramBoolean);
      resetBackgroundAlpha();
      updateBackgroundForGroupState();
      updateClickAndFocus();
      if (this.mNotificationParent != null) {
        this.mNotificationParent.updateBackgroundForGroupState();
      }
      return;
      paramBoolean = false;
      break;
      label55:
      paramExpandableNotificationRow = null;
    }
  }
  
  public void setJustClicked(boolean paramBoolean)
  {
    this.mJustClicked = paramBoolean;
  }
  
  public void setKeepInParent(boolean paramBoolean)
  {
    this.mKeepInParent = paramBoolean;
  }
  
  public void setOnClickListener(View.OnClickListener paramOnClickListener)
  {
    super.setOnClickListener(paramOnClickListener);
    this.mOnClickListener = paramOnClickListener;
    updateClickAndFocus();
  }
  
  public void setOnDismissListener(View.OnClickListener paramOnClickListener)
  {
    this.mVetoButton.setOnClickListener(paramOnClickListener);
  }
  
  public void setOnExpandClickListener(OnExpandClickListener paramOnExpandClickListener)
  {
    this.mOnExpandClickListener = paramOnExpandClickListener;
  }
  
  public void setOnKeyguard(boolean paramBoolean)
  {
    if (paramBoolean != this.mOnKeyguard)
    {
      boolean bool = isExpanded();
      this.mOnKeyguard = paramBoolean;
      logExpansionEvent(false, bool);
      if (bool != isExpanded())
      {
        if (this.mIsSummaryWithChildren) {
          this.mChildrenContainer.updateGroupOverflow();
        }
        notifyHeightChanged(false);
      }
    }
  }
  
  public void setPinned(boolean paramBoolean)
  {
    int i = getIntrinsicHeight();
    this.mIsPinned = paramBoolean;
    if (i != getIntrinsicHeight()) {
      notifyHeightChanged(false);
    }
    if (paramBoolean)
    {
      setIconAnimationRunning(true);
      this.mExpandedWhenPinned = false;
    }
    for (;;)
    {
      setChronometerRunning(this.mLastChronometerRunning);
      return;
      if (this.mExpandedWhenPinned) {
        setUserExpanded(true);
      }
    }
  }
  
  public void setRemoteInputController(RemoteInputController paramRemoteInputController)
  {
    this.mPrivateLayout.setRemoteInputController(paramRemoteInputController);
  }
  
  public void setRemoved()
  {
    this.mRemoved = true;
    this.mPrivateLayout.setRemoved();
  }
  
  public void setSensitive(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mSensitive = paramBoolean1;
    this.mSensitiveHiddenInGeneral = paramBoolean2;
  }
  
  public void setShowingLegacyBackground(boolean paramBoolean)
  {
    super.setShowingLegacyBackground(paramBoolean);
    this.mPrivateLayout.setShowingLegacyBackground(paramBoolean);
    this.mPublicLayout.setShowingLegacyBackground(paramBoolean);
  }
  
  public void setSingleLineWidthIndention(int paramInt)
  {
    this.mPrivateLayout.setSingleLineWidthIndention(paramInt);
  }
  
  public void setSystemChildExpanded(boolean paramBoolean)
  {
    this.mIsSystemChildExpanded = paramBoolean;
  }
  
  public void setSystemExpanded(boolean paramBoolean)
  {
    if (paramBoolean != this.mIsSystemExpanded)
    {
      boolean bool = isExpanded();
      this.mIsSystemExpanded = paramBoolean;
      notifyHeightChanged(false);
      logExpansionEvent(false, bool);
      if (this.mIsSummaryWithChildren) {
        this.mChildrenContainer.updateGroupOverflow();
      }
    }
  }
  
  public void setTranslation(float paramFloat)
  {
    if (areGutsExposed()) {
      return;
    }
    int i = 0;
    while (i < this.mTranslateableViews.size())
    {
      if (this.mTranslateableViews.get(i) != null) {
        ((View)this.mTranslateableViews.get(i)).setTranslationX(paramFloat);
      }
      i += 1;
    }
    invalidateOutline();
    if (this.mSettingsIconRow != null) {
      this.mSettingsIconRow.updateSettingsIcons(paramFloat, getMeasuredWidth());
    }
  }
  
  public void setUserExpanded(boolean paramBoolean)
  {
    setUserExpanded(paramBoolean, false);
  }
  
  public void setUserExpanded(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mFalsingManager.setNotificationExpanded();
    if ((!this.mIsSummaryWithChildren) || (this.mShowingPublic)) {}
    while ((!paramBoolean1) || (this.mExpandable))
    {
      paramBoolean2 = isExpanded();
      this.mHasUserChangedExpansion = true;
      this.mUserExpanded = paramBoolean1;
      logExpansionEvent(true, paramBoolean2);
      return;
      if (paramBoolean2)
      {
        paramBoolean2 = this.mGroupManager.isGroupExpanded(this.mStatusBarNotification);
        this.mGroupManager.setGroupExpanded(this.mStatusBarNotification, paramBoolean1);
        logExpansionEvent(true, paramBoolean2);
        return;
      }
    }
  }
  
  public void setUserLocked(boolean paramBoolean)
  {
    this.mUserLocked = paramBoolean;
    this.mPrivateLayout.setUserExpanding(paramBoolean);
    if (this.mIsSummaryWithChildren)
    {
      this.mChildrenContainer.setUserLocked(paramBoolean);
      if ((paramBoolean) || (!isGroupExpanded())) {}
    }
    else
    {
      return;
    }
    updateBackgroundForGroupState();
  }
  
  protected boolean shouldHideBackground()
  {
    if (!super.shouldHideBackground()) {
      return this.mShowNoBackground;
    }
    return true;
  }
  
  public boolean shouldRefocusOnDismiss()
  {
    if (!this.mRefocusOnDismiss) {
      return isAccessibilityFocused();
    }
    return true;
  }
  
  public void startChildAnimation(StackScrollState paramStackScrollState, StackStateAnimator paramStackStateAnimator, long paramLong1, long paramLong2)
  {
    if (this.mIsSummaryWithChildren) {
      this.mChildrenContainer.startAnimationToState(paramStackScrollState, paramStackStateAnimator, paramLong1, paramLong2);
    }
  }
  
  public void updateBackgroundForGroupState()
  {
    boolean bool1 = false;
    boolean bool2 = false;
    int i;
    if (this.mIsSummaryWithChildren)
    {
      bool1 = bool2;
      if (isGroupExpanded())
      {
        if (!isGroupExpansionChanging()) {
          break label88;
        }
        bool1 = bool2;
      }
      for (;;)
      {
        this.mShowNoBackground = bool1;
        this.mChildrenContainer.updateHeaderForExpansion(this.mShowNoBackground);
        List localList = this.mChildrenContainer.getNotificationChildren();
        i = 0;
        while (i < localList.size())
        {
          ((ExpandableNotificationRow)localList.get(i)).updateBackgroundForGroupState();
          i += 1;
        }
        label88:
        bool1 = bool2;
        if (!isUserLocked()) {
          bool1 = true;
        }
      }
    }
    if (isChildInGroup())
    {
      i = getShowingLayout().getBackgroundColorForExpansionState();
      if (!isGroupExpanded())
      {
        if ((!this.mNotificationParent.isGroupExpansionChanging()) && (!this.mNotificationParent.isUserLocked())) {
          break label178;
        }
        if (i == 0) {
          break label173;
        }
        i = 1;
        if (i == 0) {
          break label183;
        }
      }
    }
    label154:
    for (this.mShowNoBackground = bool1;; this.mShowNoBackground = false)
    {
      updateOutline();
      updateBackground();
      return;
      i = 1;
      break;
      label173:
      i = 0;
      break;
      label178:
      i = 0;
      break;
      label183:
      bool1 = true;
      break label154;
    }
  }
  
  protected void updateBackgroundTint()
  {
    super.updateBackgroundTint();
    updateBackgroundForGroupState();
    if (this.mIsSummaryWithChildren)
    {
      List localList = this.mChildrenContainer.getNotificationChildren();
      int i = 0;
      while (i < localList.size())
      {
        ((ExpandableNotificationRow)localList.get(i)).updateBackgroundForGroupState();
        i += 1;
      }
    }
  }
  
  public void updateChildrenHeaderAppearance()
  {
    if (this.mIsSummaryWithChildren) {
      this.mChildrenContainer.updateChildrenHeaderAppearance();
    }
  }
  
  public boolean wasJustClicked()
  {
    return this.mJustClicked;
  }
  
  public static abstract interface ExpansionLogger
  {
    public abstract void logNotificationExpansion(String paramString, boolean paramBoolean1, boolean paramBoolean2);
  }
  
  public static abstract interface OnExpandClickListener
  {
    public abstract void onExpandClicked(NotificationData.Entry paramEntry, boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\ExpandableNotificationRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */