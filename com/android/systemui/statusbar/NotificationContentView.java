package com.android.systemui.statusbar;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.Log;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import com.android.internal.util.NotificationColorUtil;
import com.android.systemui.statusbar.notification.HybridGroupManager;
import com.android.systemui.statusbar.notification.HybridNotificationView;
import com.android.systemui.statusbar.notification.NotificationCustomViewWrapper;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.NotificationViewWrapper;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.RemoteInputView;

public class NotificationContentView
  extends FrameLayout
{
  private boolean mAnimate;
  private int mAnimationStartVisibleType = -1;
  private boolean mBeforeN;
  private RemoteInputView mCachedExpandedRemoteInput;
  private RemoteInputView mCachedHeadsUpRemoteInput;
  private final Rect mClipBounds = new Rect();
  private boolean mClipToActualHeight = true;
  private int mClipTopAmount;
  private ExpandableNotificationRow mContainingNotification;
  private int mContentHeight;
  private int mContentHeightAtAnimationStart = -1;
  private View mContractedChild;
  private NotificationViewWrapper mContractedWrapper;
  private boolean mDark;
  private final ViewTreeObserver.OnPreDrawListener mEnableAnimationPredrawListener = new ViewTreeObserver.OnPreDrawListener()
  {
    public boolean onPreDraw()
    {
      NotificationContentView.this.post(new Runnable()
      {
        public void run()
        {
          NotificationContentView.-set0(NotificationContentView.this, true);
        }
      });
      NotificationContentView.this.getViewTreeObserver().removeOnPreDrawListener(this);
      return true;
    }
  };
  private View.OnClickListener mExpandClickListener;
  private boolean mExpandable;
  private View mExpandedChild;
  private RemoteInputView mExpandedRemoteInput;
  private NotificationViewWrapper mExpandedWrapper;
  private boolean mFocusOnVisibilityChange;
  private boolean mForceSelectNextLayout = true;
  private NotificationGroupManager mGroupManager;
  private View mHeadsUpChild;
  private int mHeadsUpHeight;
  private RemoteInputView mHeadsUpRemoteInput;
  private NotificationViewWrapper mHeadsUpWrapper;
  private boolean mHeadsupDisappearRunning;
  private HybridGroupManager mHybridGroupManager = new HybridGroupManager(getContext(), this);
  private boolean mIsChildInGroup;
  private boolean mIsHeadsUp;
  private final int mMinContractedHeight = getResources().getDimensionPixelSize(2131755375);
  private final int mNotificationContentMarginEnd = getResources().getDimensionPixelSize(17104962);
  private int mNotificationMaxHeight;
  private PendingIntent mPreviousExpandedRemoteInputIntent;
  private PendingIntent mPreviousHeadsUpRemoteInputIntent;
  private RemoteInputController mRemoteInputController;
  private boolean mShowingLegacyBackground;
  private HybridNotificationView mSingleLineView;
  private int mSingleLineWidthIndention;
  private int mSmallHeight;
  private StatusBarNotification mStatusBarNotification;
  private int mTransformationStartVisibleType;
  private boolean mUserExpanding;
  private int mVisibleType = 0;
  
  public NotificationContentView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    reset();
  }
  
  private void animateToVisibleType(int paramInt)
  {
    TransformableView localTransformableView1 = getTransformableViewForVisibleType(paramInt);
    final TransformableView localTransformableView2 = getTransformableViewForVisibleType(this.mVisibleType);
    if ((localTransformableView1 == localTransformableView2) || (localTransformableView2 == null))
    {
      localTransformableView1.setVisible(true);
      return;
    }
    this.mAnimationStartVisibleType = this.mVisibleType;
    localTransformableView1.transformFrom(localTransformableView2);
    getViewForVisibleType(paramInt).setVisibility(0);
    localTransformableView2.transformTo(localTransformableView1, new Runnable()
    {
      public void run()
      {
        if (localTransformableView2 != NotificationContentView.-wrap0(NotificationContentView.this, NotificationContentView.-get0(NotificationContentView.this))) {
          localTransformableView2.setVisible(false);
        }
        NotificationContentView.-set1(NotificationContentView.this, -1);
      }
    });
  }
  
  private RemoteInputView applyRemoteInput(View paramView, NotificationData.Entry paramEntry, boolean paramBoolean, PendingIntent paramPendingIntent, RemoteInputView paramRemoteInputView)
  {
    View localView = paramView.findViewById(16909228);
    if ((localView instanceof FrameLayout))
    {
      Object localObject = (RemoteInputView)paramView.findViewWithTag(RemoteInputView.VIEW_TAG);
      if (localObject != null) {
        ((RemoteInputView)localObject).onNotificationUpdateOrReset();
      }
      paramView = (View)localObject;
      if (localObject == null)
      {
        paramView = (View)localObject;
        if (paramBoolean)
        {
          localObject = (FrameLayout)localView;
          if (paramRemoteInputView != null) {
            break label218;
          }
          paramView = RemoteInputView.inflate(this.mContext, (ViewGroup)localObject, paramEntry, this.mRemoteInputController);
          paramView.setVisibility(4);
          ((ViewGroup)localObject).addView(paramView, new FrameLayout.LayoutParams(-1, -1));
        }
      }
      if (paramBoolean)
      {
        int j = paramEntry.notification.getNotification().color;
        int i = j;
        if (j == 0) {
          i = this.mContext.getColor(2131493058);
        }
        paramView.setBackgroundColor(NotificationColorUtil.ensureTextBackgroundColor(i, this.mContext.getColor(2131493059), this.mContext.getColor(2131493060)));
        if ((paramPendingIntent != null) || (paramView.isActive()))
        {
          paramEntry = paramEntry.notification.getNotification().actions;
          if (paramPendingIntent != null) {
            paramView.setPendingIntent(paramPendingIntent);
          }
          if (!paramView.updatePendingIntentFromActions(paramEntry)) {
            break label242;
          }
          if (!paramView.isActive()) {
            paramView.focus();
          }
        }
      }
      label218:
      label242:
      while (!paramView.isActive())
      {
        return paramView;
        ((ViewGroup)localObject).addView(paramRemoteInputView);
        paramRemoteInputView.dispatchFinishTemporaryDetach();
        paramRemoteInputView.requestFocus();
        paramView = paramRemoteInputView;
        break;
      }
      paramView.close();
      return paramView;
    }
    return null;
  }
  
  private void applyRemoteInput(NotificationData.Entry paramEntry)
  {
    if (this.mRemoteInputController == null) {
      return;
    }
    boolean bool2 = false;
    boolean bool1 = false;
    Object localObject = paramEntry.notification.getNotification().actions;
    if (localObject != null)
    {
      int k = localObject.length;
      int i = 0;
      bool2 = bool1;
      if (i < k)
      {
        RemoteInput[] arrayOfRemoteInput = localObject[i];
        bool2 = bool1;
        int m;
        int j;
        if (arrayOfRemoteInput.getRemoteInputs() != null)
        {
          arrayOfRemoteInput = arrayOfRemoteInput.getRemoteInputs();
          m = arrayOfRemoteInput.length;
          j = 0;
        }
        for (;;)
        {
          bool2 = bool1;
          if (j < m)
          {
            if (arrayOfRemoteInput[j].getAllowFreeFormInput()) {
              bool2 = true;
            }
          }
          else
          {
            i += 1;
            bool1 = bool2;
            break;
          }
          j += 1;
        }
      }
    }
    localObject = this.mExpandedChild;
    if (localObject != null)
    {
      this.mExpandedRemoteInput = applyRemoteInput((View)localObject, paramEntry, bool2, this.mPreviousExpandedRemoteInputIntent, this.mCachedExpandedRemoteInput);
      if ((this.mCachedExpandedRemoteInput != null) && (this.mCachedExpandedRemoteInput != this.mExpandedRemoteInput)) {
        this.mCachedExpandedRemoteInput.dispatchFinishTemporaryDetach();
      }
      this.mCachedExpandedRemoteInput = null;
      localObject = this.mHeadsUpChild;
      if (localObject == null) {
        break label254;
      }
    }
    label254:
    for (this.mHeadsUpRemoteInput = applyRemoteInput((View)localObject, paramEntry, bool2, this.mPreviousHeadsUpRemoteInputIntent, this.mCachedHeadsUpRemoteInput);; this.mHeadsUpRemoteInput = null)
    {
      if ((this.mCachedHeadsUpRemoteInput != null) && (this.mCachedHeadsUpRemoteInput != this.mHeadsUpRemoteInput)) {
        this.mCachedHeadsUpRemoteInput.dispatchFinishTemporaryDetach();
      }
      this.mCachedHeadsUpRemoteInput = null;
      return;
      this.mExpandedRemoteInput = null;
      break;
    }
  }
  
  private float calculateTransformationAmount()
  {
    int j = getViewForVisibleType(this.mTransformationStartVisibleType).getHeight();
    int k = getViewForVisibleType(this.mVisibleType).getHeight();
    int i = Math.abs(this.mContentHeight - j);
    j = Math.abs(k - j);
    return Math.min(1.0F, i / j);
  }
  
  private void focusExpandButtonIfNecessary()
  {
    if (this.mFocusOnVisibilityChange)
    {
      Object localObject = getVisibleNotificationHeader();
      if (localObject != null)
      {
        localObject = ((NotificationHeaderView)localObject).getExpandButton();
        if (localObject != null) {
          ((ImageView)localObject).requestAccessibilityFocus();
        }
      }
      this.mFocusOnVisibilityChange = false;
    }
  }
  
  private void forceUpdateVisibilities()
  {
    int i;
    int j;
    label34:
    int k;
    label52:
    int m;
    if (this.mVisibleType != 0)
    {
      if (this.mTransformationStartVisibleType != 0) {
        break label147;
      }
      i = 1;
      if (this.mVisibleType == 1) {
        break label152;
      }
      if (this.mTransformationStartVisibleType != 1) {
        break label157;
      }
      j = 1;
      if (this.mVisibleType == 2) {
        break label162;
      }
      if (this.mTransformationStartVisibleType != 2) {
        break label167;
      }
      k = 1;
      if (this.mVisibleType == 3) {
        break label172;
      }
      if (this.mTransformationStartVisibleType != 3) {
        break label178;
      }
      m = 1;
      label71:
      if (i != 0) {
        break label184;
      }
      this.mContractedChild.setVisibility(4);
      label83:
      if (this.mExpandedChild != null)
      {
        if (j != 0) {
          break label195;
        }
        this.mExpandedChild.setVisibility(4);
      }
      label102:
      if (this.mHeadsUpChild != null)
      {
        if (k != 0) {
          break label206;
        }
        this.mHeadsUpChild.setVisibility(4);
      }
    }
    for (;;)
    {
      if (this.mSingleLineView != null)
      {
        if (m != 0) {
          break label217;
        }
        this.mSingleLineView.setVisibility(4);
      }
      return;
      i = 1;
      break;
      label147:
      i = 0;
      break;
      label152:
      j = 1;
      break label34;
      label157:
      j = 0;
      break label34;
      label162:
      k = 1;
      break label52;
      label167:
      k = 0;
      break label52;
      label172:
      m = 1;
      break label71;
      label178:
      m = 0;
      break label71;
      label184:
      this.mContractedWrapper.setVisible(true);
      break label83;
      label195:
      this.mExpandedWrapper.setVisible(true);
      break label102;
      label206:
      this.mHeadsUpWrapper.setVisible(true);
    }
    label217:
    this.mSingleLineView.setVisible(true);
  }
  
  private int getMinContentHeightHint()
  {
    if ((this.mIsChildInGroup) && (isVisibleOrTransitioning(3))) {
      return this.mContext.getResources().getDimensionPixelSize(17104965);
    }
    if ((this.mHeadsUpChild != null) && (this.mExpandedChild != null))
    {
      boolean bool2;
      boolean bool1;
      if (!isTransitioningFromTo(2, 1))
      {
        bool2 = isTransitioningFromTo(1, 2);
        if (isVisibleOrTransitioning(0)) {
          break label118;
        }
        if (this.mIsHeadsUp) {
          break label113;
        }
        bool1 = this.mHeadsupDisappearRunning;
      }
      for (;;)
      {
        if ((!bool2) && (!bool1)) {
          break label123;
        }
        return Math.min(this.mHeadsUpChild.getHeight(), this.mExpandedChild.getHeight());
        bool2 = true;
        break;
        label113:
        bool1 = true;
        continue;
        label118:
        bool1 = false;
      }
    }
    label123:
    if ((this.mVisibleType == 1) && (this.mContentHeightAtAnimationStart >= 0) && (this.mExpandedChild != null)) {
      return Math.min(this.mContentHeightAtAnimationStart, this.mExpandedChild.getHeight());
    }
    int i;
    if ((this.mHeadsUpChild != null) && (isVisibleOrTransitioning(2))) {
      i = this.mHeadsUpChild.getHeight();
    }
    for (;;)
    {
      int j = i;
      if (this.mExpandedChild != null)
      {
        j = i;
        if (isVisibleOrTransitioning(1)) {
          j = Math.min(i, this.mExpandedChild.getHeight());
        }
      }
      return j;
      if (this.mExpandedChild != null) {
        i = this.mExpandedChild.getHeight();
      } else {
        i = this.mContractedChild.getHeight() + this.mContext.getResources().getDimensionPixelSize(17104965);
      }
    }
  }
  
  private TransformableView getTransformableViewForVisibleType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return this.mContractedWrapper;
    case 1: 
      return this.mExpandedWrapper;
    case 2: 
      return this.mHeadsUpWrapper;
    }
    return this.mSingleLineView;
  }
  
  private View getViewForVisibleType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return this.mContractedChild;
    case 1: 
      return this.mExpandedChild;
    case 2: 
      return this.mHeadsUpChild;
    }
    return this.mSingleLineView;
  }
  
  private NotificationViewWrapper getVisibleWrapper(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      return this.mExpandedWrapper;
    case 2: 
      return this.mHeadsUpWrapper;
    }
    return this.mContractedWrapper;
  }
  
  private int getVisualTypeForHeight(float paramFloat)
  {
    if (this.mExpandedChild == null) {}
    for (int i = 1; (i == 0) && (paramFloat == this.mExpandedChild.getHeight()); i = 0) {
      return 1;
    }
    if ((this.mUserExpanding) || (!this.mIsChildInGroup) || (isGroupExpanded()))
    {
      if (((!this.mIsHeadsUp) && (!this.mHeadsupDisappearRunning)) || (this.mHeadsUpChild == null)) {
        break label98;
      }
      if ((paramFloat <= this.mHeadsUpChild.getHeight()) || (i != 0)) {
        return 2;
      }
    }
    else
    {
      return 3;
    }
    return 1;
    label98:
    if ((i == 0) && ((paramFloat > this.mContractedChild.getHeight()) || ((this.mIsChildInGroup) && (!isGroupExpanded()) && (this.mContainingNotification.isExpanded(true))))) {
      return 1;
    }
    return 0;
  }
  
  private boolean isGroupExpanded()
  {
    return this.mGroupManager.isGroupExpanded(this.mStatusBarNotification);
  }
  
  private boolean isTransitioningFromTo(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    boolean bool1;
    if (this.mTransformationStartVisibleType != paramInt1)
    {
      bool1 = bool2;
      if (this.mAnimationStartVisibleType != paramInt1) {}
    }
    else
    {
      bool1 = bool2;
      if (this.mVisibleType == paramInt2) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isVisibleOrTransitioning(int paramInt)
  {
    if ((this.mVisibleType == paramInt) || (this.mTransformationStartVisibleType == paramInt)) {}
    while (this.mAnimationStartVisibleType == paramInt) {
      return true;
    }
    return false;
  }
  
  private void selectLayout(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mContractedChild == null) {
      return;
    }
    if (this.mUserExpanding)
    {
      updateContentTransformation();
      return;
    }
    int j = calculateVisibleType();
    int i;
    if (j != this.mVisibleType)
    {
      i = 1;
      label37:
      if ((i == 0) && (!paramBoolean2)) {
        break label142;
      }
      Object localObject = getViewForVisibleType(j);
      if (localObject != null)
      {
        ((View)localObject).setVisibility(0);
        transferRemoteInputFocus(j);
      }
      localObject = getVisibleWrapper(j);
      if (localObject != null) {
        ((NotificationViewWrapper)localObject).setContentHeight(this.mContentHeight, getMinContentHeightHint());
      }
      if (!paramBoolean1) {
        break label175;
      }
      if ((j != 1) || (this.mExpandedChild == null)) {
        break label144;
      }
      label113:
      animateToVisibleType(j);
    }
    for (;;)
    {
      this.mVisibleType = j;
      if (i != 0) {
        focusExpandButtonIfNecessary();
      }
      updateBackgroundColor(paramBoolean1);
      return;
      i = 0;
      break label37;
      label142:
      break;
      label144:
      if (((j == 2) && (this.mHeadsUpChild != null)) || ((j == 3) && (this.mSingleLineView != null)) || (j == 0)) {
        break label113;
      }
      label175:
      updateViewVisibilities(j);
    }
  }
  
  private void setVisible(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      getViewTreeObserver().removeOnPreDrawListener(this.mEnableAnimationPredrawListener);
      getViewTreeObserver().addOnPreDrawListener(this.mEnableAnimationPredrawListener);
      return;
    }
    getViewTreeObserver().removeOnPreDrawListener(this.mEnableAnimationPredrawListener);
    this.mAnimate = false;
  }
  
  private boolean shouldContractedBeFixedSize()
  {
    if (this.mBeforeN) {
      return this.mContractedWrapper instanceof NotificationCustomViewWrapper;
    }
    return false;
  }
  
  private void transferRemoteInputFocus(int paramInt)
  {
    if ((paramInt == 2) && (this.mHeadsUpRemoteInput != null) && (this.mExpandedRemoteInput != null) && (this.mExpandedRemoteInput.isActive())) {
      this.mHeadsUpRemoteInput.stealFocusFrom(this.mExpandedRemoteInput);
    }
    if ((paramInt == 1) && (this.mExpandedRemoteInput != null) && (this.mHeadsUpRemoteInput != null) && (this.mHeadsUpRemoteInput.isActive())) {
      this.mExpandedRemoteInput.stealFocusFrom(this.mHeadsUpRemoteInput);
    }
  }
  
  private void updateBackgroundTransformation(float paramFloat)
  {
    int i = getBackgroundColor(this.mVisibleType);
    int k = getBackgroundColor(this.mTransformationStartVisibleType);
    int j = i;
    if (i != k)
    {
      j = k;
      if (k == 0) {
        j = this.mContainingNotification.getBackgroundColorWithoutTint();
      }
      k = i;
      if (i == 0) {
        k = this.mContainingNotification.getBackgroundColorWithoutTint();
      }
      j = NotificationUtils.interpolateColors(j, k, paramFloat);
    }
    this.mContainingNotification.updateBackgroundAlpha(paramFloat);
    this.mContainingNotification.setContentBackground(j, false, this);
  }
  
  private void updateClipping()
  {
    if (this.mClipToActualHeight)
    {
      this.mClipBounds.set(0, this.mClipTopAmount, getWidth(), this.mContentHeight);
      setClipBounds(this.mClipBounds);
      return;
    }
    setClipBounds(null);
  }
  
  private void updateContentTransformation()
  {
    int i = calculateVisibleType();
    TransformableView localTransformableView1;
    TransformableView localTransformableView2;
    if (i != this.mVisibleType)
    {
      this.mTransformationStartVisibleType = this.mVisibleType;
      localTransformableView1 = getTransformableViewForVisibleType(i);
      localTransformableView2 = getTransformableViewForVisibleType(this.mTransformationStartVisibleType);
      if (localTransformableView1 == null) {
        Log.d("NotificationContentView", "shownView visibleType = " + i);
      }
      if (localTransformableView2 == null) {
        Log.d("NotificationContentView", "hiddenView visibleType = " + this.mTransformationStartVisibleType);
      }
      localTransformableView1.transformFrom(localTransformableView2, 0.0F);
      getViewForVisibleType(i).setVisibility(0);
      localTransformableView2.transformTo(localTransformableView1, 0.0F);
      this.mVisibleType = i;
      updateBackgroundColor(true);
    }
    if (this.mForceSelectNextLayout) {
      forceUpdateVisibilities();
    }
    if ((this.mTransformationStartVisibleType != -1) && (this.mVisibleType != this.mTransformationStartVisibleType) && (getViewForVisibleType(this.mTransformationStartVisibleType) != null))
    {
      localTransformableView1 = getTransformableViewForVisibleType(this.mVisibleType);
      localTransformableView2 = getTransformableViewForVisibleType(this.mTransformationStartVisibleType);
      float f = calculateTransformationAmount();
      localTransformableView1.transformFrom(localTransformableView2, f);
      localTransformableView2.transformTo(localTransformableView1, f);
      updateBackgroundTransformation(f);
      return;
    }
    updateViewVisibilities(i);
    updateBackgroundColor(false);
  }
  
  private boolean updateContractedHeaderWidth()
  {
    NotificationHeaderView localNotificationHeaderView1 = this.mContractedWrapper.getNotificationHeader();
    if (localNotificationHeaderView1 != null)
    {
      int i;
      int j;
      int k;
      if ((this.mExpandedChild != null) && (this.mExpandedWrapper.getNotificationHeader() != null))
      {
        NotificationHeaderView localNotificationHeaderView2 = this.mExpandedWrapper.getNotificationHeader();
        i = localNotificationHeaderView2.getMeasuredWidth() - localNotificationHeaderView2.getPaddingEnd();
        if (i != localNotificationHeaderView1.getMeasuredWidth() - localNotificationHeaderView2.getPaddingEnd())
        {
          i = localNotificationHeaderView1.getMeasuredWidth() - i;
          if (localNotificationHeaderView1.isLayoutRtl()) {}
          for (j = i;; j = localNotificationHeaderView1.getPaddingLeft())
          {
            k = localNotificationHeaderView1.getPaddingTop();
            if (localNotificationHeaderView1.isLayoutRtl()) {
              i = localNotificationHeaderView1.getPaddingLeft();
            }
            localNotificationHeaderView1.setPadding(j, k, i, localNotificationHeaderView1.getPaddingBottom());
            localNotificationHeaderView1.setShowWorkBadgeAtEnd(true);
            return true;
          }
        }
      }
      else
      {
        i = this.mNotificationContentMarginEnd;
        if (localNotificationHeaderView1.getPaddingEnd() != i)
        {
          if (localNotificationHeaderView1.isLayoutRtl()) {}
          for (j = i;; j = localNotificationHeaderView1.getPaddingLeft())
          {
            k = localNotificationHeaderView1.getPaddingTop();
            if (localNotificationHeaderView1.isLayoutRtl()) {
              i = localNotificationHeaderView1.getPaddingLeft();
            }
            localNotificationHeaderView1.setPadding(j, k, i, localNotificationHeaderView1.getPaddingBottom());
            localNotificationHeaderView1.setShowWorkBadgeAtEnd(false);
            return true;
          }
        }
      }
    }
    return false;
  }
  
  private void updateShowingLegacyBackground()
  {
    if (this.mContractedChild != null) {
      this.mContractedWrapper.setShowingLegacyBackground(this.mShowingLegacyBackground);
    }
    if (this.mExpandedChild != null) {
      this.mExpandedWrapper.setShowingLegacyBackground(this.mShowingLegacyBackground);
    }
    if (this.mHeadsUpChild != null) {
      this.mHeadsUpWrapper.setShowingLegacyBackground(this.mShowingLegacyBackground);
    }
  }
  
  private void updateSingleLineView()
  {
    if (this.mIsChildInGroup) {
      this.mSingleLineView = this.mHybridGroupManager.bindFromNotification(this.mSingleLineView, this.mStatusBarNotification.getNotification());
    }
    while (this.mSingleLineView == null) {
      return;
    }
  }
  
  private void updateViewVisibilities(int paramInt)
  {
    if (paramInt == 0)
    {
      bool = true;
      this.mContractedWrapper.setVisible(bool);
      if (this.mExpandedChild != null)
      {
        if (paramInt != 1) {
          break label86;
        }
        bool = true;
        label28:
        this.mExpandedWrapper.setVisible(bool);
      }
      if (this.mHeadsUpChild != null)
      {
        if (paramInt != 2) {
          break label91;
        }
        bool = true;
        label50:
        this.mHeadsUpWrapper.setVisible(bool);
      }
      if (this.mSingleLineView != null) {
        if (paramInt != 3) {
          break label96;
        }
      }
    }
    label86:
    label91:
    label96:
    for (boolean bool = true;; bool = false)
    {
      this.mSingleLineView.setVisible(bool);
      return;
      bool = false;
      break;
      bool = false;
      break label28;
      bool = false;
      break label50;
    }
  }
  
  private void updateVisibility()
  {
    setVisible(isShown());
  }
  
  public int calculateVisibleType()
  {
    if (this.mUserExpanding)
    {
      if ((!this.mIsChildInGroup) || (isGroupExpanded()) || (this.mContainingNotification.isExpanded(true)))
      {
        i = this.mContainingNotification.getMaxContentHeight();
        j = i;
        if (i == 0) {
          j = this.mContentHeight;
        }
        j = getVisualTypeForHeight(j);
        if ((this.mIsChildInGroup) && (!isGroupExpanded())) {
          break label109;
        }
      }
      label109:
      for (i = getVisualTypeForHeight(this.mContainingNotification.getCollapsedHeight());; i = 3)
      {
        if (this.mTransformationStartVisibleType != i) {
          break label114;
        }
        return j;
        i = this.mContainingNotification.getShowingLayout().getMinHeight();
        break;
      }
      label114:
      return i;
    }
    int j = this.mContainingNotification.getIntrinsicHeight();
    int i = this.mContentHeight;
    if (j != 0) {
      i = Math.min(this.mContentHeight, j);
    }
    return getVisualTypeForHeight(i);
  }
  
  public void closeRemoteInput()
  {
    if (this.mHeadsUpRemoteInput != null) {
      this.mHeadsUpRemoteInput.close();
    }
    if (this.mExpandedRemoteInput != null) {
      this.mExpandedRemoteInput.close();
    }
  }
  
  public int getBackgroundColor(int paramInt)
  {
    NotificationViewWrapper localNotificationViewWrapper = getVisibleWrapper(paramInt);
    paramInt = 0;
    if (localNotificationViewWrapper != null) {
      paramInt = localNotificationViewWrapper.getCustomBackgroundColor();
    }
    return paramInt;
  }
  
  public int getBackgroundColorForExpansionState()
  {
    if ((this.mContainingNotification.isGroupExpanded()) || (this.mContainingNotification.isUserLocked())) {}
    for (int i = calculateVisibleType();; i = getVisibleType()) {
      return getBackgroundColor(i);
    }
  }
  
  public View getContractedChild()
  {
    return this.mContractedChild;
  }
  
  public View getExpandedChild()
  {
    return this.mExpandedChild;
  }
  
  public View getHeadsUpChild()
  {
    return this.mHeadsUpChild;
  }
  
  public int getMaxHeight()
  {
    if (this.mExpandedChild != null) {
      return this.mExpandedChild.getHeight();
    }
    if ((this.mIsHeadsUp) && (this.mHeadsUpChild != null)) {
      return this.mHeadsUpChild.getHeight();
    }
    return this.mContractedChild.getHeight();
  }
  
  public int getMinHeight()
  {
    return getMinHeight(false);
  }
  
  public int getMinHeight(boolean paramBoolean)
  {
    if ((paramBoolean) || (!this.mIsChildInGroup) || (isGroupExpanded()))
    {
      if (this.mContractedChild != null) {
        return this.mContractedChild.getHeight();
      }
      Log.w(NotificationContentView.class.getSimpleName(), "mContractedChild is empty!!");
      return 0;
    }
    if (this.mSingleLineView != null) {
      return this.mSingleLineView.getHeight();
    }
    Log.w(NotificationContentView.class.getSimpleName(), "mSingleLineView is empty!!");
    return 0;
  }
  
  public NotificationHeaderView getNotificationHeader()
  {
    Object localObject2 = null;
    if (this.mContractedChild != null) {
      localObject2 = this.mContractedWrapper.getNotificationHeader();
    }
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = localObject2;
      if (this.mExpandedChild != null) {
        localObject1 = this.mExpandedWrapper.getNotificationHeader();
      }
    }
    localObject2 = localObject1;
    if (localObject1 == null)
    {
      localObject2 = localObject1;
      if (this.mHeadsUpChild != null) {
        localObject2 = this.mHeadsUpWrapper.getNotificationHeader();
      }
    }
    return (NotificationHeaderView)localObject2;
  }
  
  public HybridNotificationView getSingleLineView()
  {
    return this.mSingleLineView;
  }
  
  public NotificationHeaderView getVisibleNotificationHeader()
  {
    NotificationViewWrapper localNotificationViewWrapper = getVisibleWrapper(this.mVisibleType);
    if (localNotificationViewWrapper == null) {
      return null;
    }
    return localNotificationViewWrapper.getNotificationHeader();
  }
  
  public int getVisibleType()
  {
    return this.mVisibleType;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public boolean isContentExpandable()
  {
    return this.mExpandedChild != null;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    updateVisibility();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    getViewTreeObserver().removeOnPreDrawListener(this.mEnableAnimationPredrawListener);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 0;
    if (this.mExpandedChild != null) {
      i = this.mExpandedChild.getHeight();
    }
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if ((i != 0) && (this.mExpandedChild.getHeight() != i)) {
      this.mContentHeightAtAnimationStart = i;
    }
    updateClipping();
    invalidateOutline();
    selectLayout(false, this.mForceSelectNextLayout);
    this.mForceSelectNextLayout = false;
    updateExpandButtons(this.mExpandable);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.getMode(paramInt2);
    int k;
    label27:
    int m;
    ViewGroup.LayoutParams localLayoutParams;
    if (j == 1073741824)
    {
      i = 1;
      if (j != Integer.MIN_VALUE) {
        break label470;
      }
      k = 1;
      j = Integer.MAX_VALUE;
      m = View.MeasureSpec.getSize(paramInt1);
      if ((i != 0) || (k != 0)) {
        j = View.MeasureSpec.getSize(paramInt2);
      }
      paramInt2 = 0;
      if (this.mExpandedChild != null)
      {
        paramInt2 = Math.min(j, this.mNotificationMaxHeight);
        localLayoutParams = this.mExpandedChild.getLayoutParams();
        if (localLayoutParams.height >= 0) {
          paramInt2 = Math.min(j, localLayoutParams.height);
        }
        if (paramInt2 != Integer.MAX_VALUE) {
          break label476;
        }
        paramInt2 = View.MeasureSpec.makeMeasureSpec(0, 0);
        label113:
        this.mExpandedChild.measure(paramInt1, paramInt2);
        paramInt2 = Math.max(0, this.mExpandedChild.getMeasuredHeight());
      }
      i = paramInt2;
      if (this.mContractedChild != null)
      {
        i = Math.min(j, this.mSmallHeight);
        if (!shouldContractedBeFixedSize()) {
          break label487;
        }
      }
    }
    label470:
    label476:
    label487:
    for (int i = View.MeasureSpec.makeMeasureSpec(i, 1073741824);; i = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE))
    {
      this.mContractedChild.measure(paramInt1, i);
      k = this.mContractedChild.getMeasuredHeight();
      if (k < this.mMinContractedHeight)
      {
        i = View.MeasureSpec.makeMeasureSpec(this.mMinContractedHeight, 1073741824);
        this.mContractedChild.measure(paramInt1, i);
      }
      paramInt2 = Math.max(paramInt2, k);
      if (updateContractedHeaderWidth()) {
        this.mContractedChild.measure(paramInt1, i);
      }
      i = paramInt2;
      if (this.mExpandedChild != null)
      {
        i = paramInt2;
        if (this.mContractedChild.getMeasuredHeight() > this.mExpandedChild.getMeasuredHeight())
        {
          i = View.MeasureSpec.makeMeasureSpec(this.mContractedChild.getMeasuredHeight(), 1073741824);
          this.mExpandedChild.measure(paramInt1, i);
          i = paramInt2;
        }
      }
      paramInt2 = i;
      if (this.mHeadsUpChild != null)
      {
        k = Math.min(j, this.mHeadsUpHeight);
        localLayoutParams = this.mHeadsUpChild.getLayoutParams();
        paramInt2 = k;
        if (localLayoutParams.height >= 0) {
          paramInt2 = Math.min(k, localLayoutParams.height);
        }
        this.mHeadsUpChild.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE));
        paramInt2 = Math.max(i, this.mHeadsUpChild.getMeasuredHeight());
      }
      i = paramInt2;
      if (this.mSingleLineView != null)
      {
        i = paramInt1;
        k = i;
        if (this.mSingleLineWidthIndention != 0)
        {
          k = i;
          if (View.MeasureSpec.getMode(paramInt1) != 0) {
            k = View.MeasureSpec.makeMeasureSpec(m - this.mSingleLineWidthIndention + this.mSingleLineView.getPaddingEnd(), 1073741824);
          }
        }
        this.mSingleLineView.measure(k, View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE));
        i = Math.max(paramInt2, this.mSingleLineView.getMeasuredHeight());
      }
      setMeasuredDimension(m, Math.min(i, j));
      return;
      i = 0;
      break;
      k = 0;
      break label27;
      paramInt2 = View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE);
      break label113;
    }
  }
  
  public void onNotificationUpdated(NotificationData.Entry paramEntry)
  {
    this.mStatusBarNotification = paramEntry.notification;
    if (paramEntry.targetSdk < 24) {}
    for (boolean bool = true;; bool = false)
    {
      this.mBeforeN = bool;
      updateSingleLineView();
      applyRemoteInput(paramEntry);
      if (this.mContractedChild != null) {
        this.mContractedWrapper.notifyContentUpdated(paramEntry.notification);
      }
      if (this.mExpandedChild != null) {
        this.mExpandedWrapper.notifyContentUpdated(paramEntry.notification);
      }
      if (this.mHeadsUpChild != null) {
        this.mHeadsUpWrapper.notifyContentUpdated(paramEntry.notification);
      }
      updateShowingLegacyBackground();
      this.mForceSelectNextLayout = true;
      setDark(this.mDark, false, 0L);
      this.mPreviousExpandedRemoteInputIntent = null;
      this.mPreviousHeadsUpRemoteInputIntent = null;
      return;
    }
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    updateVisibility();
  }
  
  public void reInflateViews()
  {
    if ((this.mIsChildInGroup) && (this.mSingleLineView != null))
    {
      removeView(this.mSingleLineView);
      this.mSingleLineView = null;
      updateSingleLineView();
    }
  }
  
  public void requestSelectLayout(boolean paramBoolean)
  {
    selectLayout(paramBoolean, false);
  }
  
  public void reset()
  {
    if (this.mContractedChild != null)
    {
      this.mContractedChild.animate().cancel();
      removeView(this.mContractedChild);
    }
    this.mPreviousExpandedRemoteInputIntent = null;
    if (this.mExpandedRemoteInput != null)
    {
      this.mExpandedRemoteInput.onNotificationUpdateOrReset();
      if (this.mExpandedRemoteInput.isActive())
      {
        this.mPreviousExpandedRemoteInputIntent = this.mExpandedRemoteInput.getPendingIntent();
        this.mCachedExpandedRemoteInput = this.mExpandedRemoteInput;
        this.mExpandedRemoteInput.dispatchStartTemporaryDetach();
        ((ViewGroup)this.mExpandedRemoteInput.getParent()).removeView(this.mExpandedRemoteInput);
      }
    }
    if (this.mExpandedChild != null)
    {
      this.mExpandedChild.animate().cancel();
      removeView(this.mExpandedChild);
      this.mExpandedRemoteInput = null;
    }
    this.mPreviousHeadsUpRemoteInputIntent = null;
    if (this.mHeadsUpRemoteInput != null)
    {
      this.mHeadsUpRemoteInput.onNotificationUpdateOrReset();
      if (this.mHeadsUpRemoteInput.isActive())
      {
        this.mPreviousHeadsUpRemoteInputIntent = this.mHeadsUpRemoteInput.getPendingIntent();
        this.mCachedHeadsUpRemoteInput = this.mHeadsUpRemoteInput;
        this.mHeadsUpRemoteInput.dispatchStartTemporaryDetach();
        ((ViewGroup)this.mHeadsUpRemoteInput.getParent()).removeView(this.mHeadsUpRemoteInput);
      }
    }
    if (this.mHeadsUpChild != null)
    {
      this.mHeadsUpChild.animate().cancel();
      removeView(this.mHeadsUpChild);
      this.mHeadsUpRemoteInput = null;
    }
    this.mContractedChild = null;
    this.mExpandedChild = null;
    this.mHeadsUpChild = null;
  }
  
  public void setClipToActualHeight(boolean paramBoolean)
  {
    this.mClipToActualHeight = paramBoolean;
    updateClipping();
  }
  
  public void setClipTopAmount(int paramInt)
  {
    this.mClipTopAmount = paramInt;
    updateClipping();
  }
  
  public void setContainingNotification(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    this.mContainingNotification = paramExpandableNotificationRow;
  }
  
  public void setContentHeight(int paramInt)
  {
    this.mContentHeight = Math.max(Math.min(paramInt, getHeight()), getMinHeight());
    selectLayout(this.mAnimate, false);
    paramInt = getMinContentHeightHint();
    NotificationViewWrapper localNotificationViewWrapper = getVisibleWrapper(this.mVisibleType);
    if (localNotificationViewWrapper != null) {
      localNotificationViewWrapper.setContentHeight(this.mContentHeight, paramInt);
    }
    localNotificationViewWrapper = getVisibleWrapper(this.mTransformationStartVisibleType);
    if (localNotificationViewWrapper != null) {
      localNotificationViewWrapper.setContentHeight(this.mContentHeight, paramInt);
    }
    updateClipping();
    invalidateOutline();
  }
  
  public void setContentHeightAnimating(boolean paramBoolean)
  {
    if (!paramBoolean) {
      this.mContentHeightAtAnimationStart = -1;
    }
  }
  
  public void setContractedChild(View paramView)
  {
    if (this.mContractedChild != null)
    {
      this.mContractedChild.animate().cancel();
      removeView(this.mContractedChild);
    }
    addView(paramView);
    this.mContractedChild = paramView;
    this.mContractedWrapper = NotificationViewWrapper.wrap(getContext(), paramView, this.mContainingNotification);
    this.mContractedWrapper.setDark(this.mDark, false, 0L);
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    if (this.mContractedChild == null) {
      return;
    }
    this.mDark = paramBoolean1;
    if ((this.mVisibleType != 0) && (paramBoolean1))
    {
      if ((this.mVisibleType == 1) || ((this.mExpandedChild != null) && (!paramBoolean1))) {
        break label95;
      }
      label43:
      if ((this.mVisibleType == 2) || ((this.mHeadsUpChild != null) && (!paramBoolean1))) {
        break label108;
      }
    }
    for (;;)
    {
      if ((this.mSingleLineView != null) && ((this.mVisibleType == 3) || (!paramBoolean1))) {
        break label121;
      }
      return;
      this.mContractedWrapper.setDark(paramBoolean1, paramBoolean2, paramLong);
      break;
      label95:
      this.mExpandedWrapper.setDark(paramBoolean1, paramBoolean2, paramLong);
      break label43;
      label108:
      this.mHeadsUpWrapper.setDark(paramBoolean1, paramBoolean2, paramLong);
    }
    label121:
    this.mSingleLineView.setDark(paramBoolean1, paramBoolean2, paramLong);
  }
  
  public void setExpandClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mExpandClickListener = paramOnClickListener;
  }
  
  public void setExpandedChild(View paramView)
  {
    if (this.mExpandedChild != null)
    {
      this.mExpandedChild.animate().cancel();
      removeView(this.mExpandedChild);
    }
    addView(paramView);
    this.mExpandedChild = paramView;
    this.mExpandedWrapper = NotificationViewWrapper.wrap(getContext(), paramView, this.mContainingNotification);
  }
  
  public void setFocusOnVisibilityChange()
  {
    this.mFocusOnVisibilityChange = true;
  }
  
  public void setGroupManager(NotificationGroupManager paramNotificationGroupManager)
  {
    this.mGroupManager = paramNotificationGroupManager;
  }
  
  public void setHeadsUp(boolean paramBoolean)
  {
    this.mIsHeadsUp = paramBoolean;
    selectLayout(false, true);
    updateExpandButtons(this.mExpandable);
  }
  
  public void setHeadsUpChild(View paramView)
  {
    if (this.mHeadsUpChild != null)
    {
      this.mHeadsUpChild.animate().cancel();
      removeView(this.mHeadsUpChild);
    }
    addView(paramView);
    this.mHeadsUpChild = paramView;
    this.mHeadsUpWrapper = NotificationViewWrapper.wrap(getContext(), paramView, this.mContainingNotification);
  }
  
  public void setHeadsupDisappearRunning(boolean paramBoolean)
  {
    this.mHeadsupDisappearRunning = paramBoolean;
    selectLayout(false, true);
  }
  
  public void setHeights(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mSmallHeight = paramInt1;
    this.mHeadsUpHeight = paramInt2;
    this.mNotificationMaxHeight = paramInt3;
  }
  
  public void setIsChildInGroup(boolean paramBoolean)
  {
    this.mIsChildInGroup = paramBoolean;
    updateSingleLineView();
  }
  
  public void setRemoteInputController(RemoteInputController paramRemoteInputController)
  {
    this.mRemoteInputController = paramRemoteInputController;
  }
  
  public void setRemoved()
  {
    if (this.mExpandedRemoteInput != null) {
      this.mExpandedRemoteInput.setRemoved();
    }
    if (this.mHeadsUpRemoteInput != null) {
      this.mHeadsUpRemoteInput.setRemoved();
    }
  }
  
  public void setShowingLegacyBackground(boolean paramBoolean)
  {
    this.mShowingLegacyBackground = paramBoolean;
    updateShowingLegacyBackground();
  }
  
  public void setSingleLineWidthIndention(int paramInt)
  {
    if (paramInt != this.mSingleLineWidthIndention)
    {
      this.mSingleLineWidthIndention = paramInt;
      this.mContainingNotification.forceLayout();
      forceLayout();
    }
  }
  
  public void setUserExpanding(boolean paramBoolean)
  {
    this.mUserExpanding = paramBoolean;
    if (paramBoolean)
    {
      this.mTransformationStartVisibleType = this.mVisibleType;
      return;
    }
    this.mTransformationStartVisibleType = -1;
    this.mVisibleType = calculateVisibleType();
    updateViewVisibilities(this.mVisibleType);
    updateBackgroundColor(false);
  }
  
  public void updateBackgroundColor(boolean paramBoolean)
  {
    int i = getBackgroundColor(this.mVisibleType);
    this.mContainingNotification.resetBackgroundAlpha();
    this.mContainingNotification.setContentBackground(i, paramBoolean, this);
  }
  
  public void updateExpandButtons(boolean paramBoolean)
  {
    this.mExpandable = paramBoolean;
    boolean bool = paramBoolean;
    if (this.mExpandedChild != null)
    {
      bool = paramBoolean;
      if (this.mExpandedChild.getHeight() != 0)
      {
        if ((this.mIsHeadsUp) && (this.mHeadsUpChild != null)) {
          break label119;
        }
        bool = paramBoolean;
        if (this.mExpandedChild.getHeight() == this.mContractedChild.getHeight()) {
          bool = false;
        }
      }
    }
    for (;;)
    {
      if (this.mExpandedChild != null) {
        this.mExpandedWrapper.updateExpandability(bool, this.mExpandClickListener);
      }
      if (this.mContractedChild != null) {
        this.mContractedWrapper.updateExpandability(bool, this.mExpandClickListener);
      }
      if (this.mHeadsUpChild != null) {
        this.mHeadsUpWrapper.updateExpandability(bool, this.mExpandClickListener);
      }
      return;
      label119:
      bool = paramBoolean;
      if (this.mExpandedChild.getHeight() == this.mHeadsUpChild.getHeight()) {
        bool = false;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\NotificationContentView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */