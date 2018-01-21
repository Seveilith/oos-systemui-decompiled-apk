package com.android.systemui.statusbar.stack;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Pair;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.animation.AnimationUtils;
import android.widget.OverScroller;
import android.widget.ScrollView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.ExpandHelper;
import com.android.systemui.ExpandHelper.Callback;
import com.android.systemui.Interpolators;
import com.android.systemui.SwipeHelper;
import com.android.systemui.SwipeHelper.Callback;
import com.android.systemui.SwipeHelper.LongPressListener;
import com.android.systemui.classifier.FalsingManager;
import com.android.systemui.statusbar.ActivatableNotificationView;
import com.android.systemui.statusbar.DismissView;
import com.android.systemui.statusbar.EmptyShadeView;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.ExpandableView;
import com.android.systemui.statusbar.ExpandableView.OnHeightChangedListener;
import com.android.systemui.statusbar.NotificationData.Entry;
import com.android.systemui.statusbar.NotificationGuts;
import com.android.systemui.statusbar.NotificationOverflowContainer;
import com.android.systemui.statusbar.NotificationSettingsIconRow;
import com.android.systemui.statusbar.NotificationSettingsIconRow.SettingsIconRowListener;
import com.android.systemui.statusbar.StackScrollerDecorView;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager.NotificationGroup;
import com.android.systemui.statusbar.phone.NotificationGroupManager.OnGroupChangeListener;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.HeadsUpManager.HeadsUpEntry;
import com.android.systemui.statusbar.policy.ScrollAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class NotificationStackScrollLayout
  extends ViewGroup
  implements SwipeHelper.Callback, ExpandHelper.Callback, ScrollAdapter, ExpandableView.OnHeightChangedListener, NotificationGroupManager.OnGroupChangeListener, NotificationSettingsIconRow.SettingsIconRowListener, ScrollContainer
{
  private static final Property<NotificationStackScrollLayout, Float> BACKGROUND_FADE = new FloatProperty("backgroundFade")
  {
    public Float get(NotificationStackScrollLayout paramAnonymousNotificationStackScrollLayout)
    {
      return Float.valueOf(paramAnonymousNotificationStackScrollLayout.getBackgroundFadeAmount());
    }
    
    public void setValue(NotificationStackScrollLayout paramAnonymousNotificationStackScrollLayout, float paramAnonymousFloat)
    {
      NotificationStackScrollLayout.-wrap3(paramAnonymousNotificationStackScrollLayout, paramAnonymousFloat);
    }
  };
  private boolean mActivateNeedsAnimation;
  private int mActivePointerId = -1;
  private ArrayList<View> mAddedHeadsUpChildren = new ArrayList();
  private AmbientState mAmbientState = new AmbientState();
  private boolean mAnimateNextBackgroundBottom;
  private boolean mAnimateNextBackgroundTop;
  private ArrayList<AnimationEvent> mAnimationEvents = new ArrayList();
  private HashSet<Runnable> mAnimationFinishedRunnables = new HashSet();
  private boolean mAnimationRunning;
  private boolean mAnimationsEnabled;
  private Rect mBackgroundBounds = new Rect();
  private float mBackgroundFadeAmount = 1.0F;
  private final Paint mBackgroundPaint = new Paint();
  private ViewTreeObserver.OnPreDrawListener mBackgroundUpdater = new ViewTreeObserver.OnPreDrawListener()
  {
    public boolean onPreDraw()
    {
      if ((NotificationStackScrollLayout.-get8(NotificationStackScrollLayout.this)) || (NotificationStackScrollLayout.-get0(NotificationStackScrollLayout.this))) {}
      for (;;)
      {
        return true;
        NotificationStackScrollLayout.-wrap6(NotificationStackScrollLayout.this);
      }
    }
  };
  private boolean mBackwardScrollable;
  private int mBgColor;
  private ObjectAnimator mBottomAnimator = null;
  private int mBottomInset = 0;
  private int mBottomStackPeekSize;
  private int mBottomStackSlowDownHeight;
  private boolean mChangePositionInProgress;
  private boolean mChildTransferInProgress;
  private ArrayList<View> mChildrenChangingPositions = new ArrayList();
  private HashSet<View> mChildrenToAddAnimated = new HashSet();
  private ArrayList<View> mChildrenToRemoveAnimated = new ArrayList();
  private boolean mChildrenUpdateRequested;
  private ViewTreeObserver.OnPreDrawListener mChildrenUpdater = new ViewTreeObserver.OnPreDrawListener()
  {
    public boolean onPreDraw()
    {
      NotificationStackScrollLayout.-get11(NotificationStackScrollLayout.this).updateSensitivity();
      NotificationStackScrollLayout.-wrap9(NotificationStackScrollLayout.this);
      NotificationStackScrollLayout.-wrap7(NotificationStackScrollLayout.this);
      NotificationStackScrollLayout.-set1(NotificationStackScrollLayout.this, false);
      NotificationStackScrollLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
      return true;
    }
  };
  private HashSet<View> mClearOverlayViewsWhenFinished = new HashSet();
  private int mCollapsedSize;
  private int mContentHeight;
  private boolean mContinuousShadowUpdate;
  private NotificationSettingsIconRow mCurrIconRow;
  private Rect mCurrentBounds = new Rect(-1, -1, -1, -1);
  private int mCurrentStackHeight = Integer.MAX_VALUE;
  private StackScrollState mCurrentStackScrollState = new StackScrollState(this);
  private int mDarkAnimationOriginIndex;
  private boolean mDarkNeedsAnimation;
  private float mDimAmount;
  private ValueAnimator mDimAnimator;
  private Animator.AnimatorListener mDimEndListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      NotificationStackScrollLayout.-set3(NotificationStackScrollLayout.this, null);
    }
  };
  private ValueAnimator.AnimatorUpdateListener mDimUpdateListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      NotificationStackScrollLayout.-wrap4(NotificationStackScrollLayout.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
    }
  };
  private boolean mDimmedNeedsAnimation;
  private boolean mDisallowDismissInThisMotion;
  private boolean mDisallowScrollingInThisMotion;
  private boolean mDismissAllInProgress;
  private DismissView mDismissView;
  private boolean mDontClampNextScroll;
  private boolean mDontReportNextOverScroll;
  private int mDownX;
  private ArrayList<View> mDragAnimPendingChildren = new ArrayList();
  private boolean mDrawBackgroundAsSrc;
  private EmptyShadeView mEmptyShadeView;
  private Rect mEndAnimationRect = new Rect();
  private boolean mEverythingNeedsAnimation;
  private ExpandHelper mExpandHelper;
  private View mExpandedGroupView;
  private float mExpandedHeight;
  private boolean mExpandedInThisMotion;
  private boolean mExpandingNotification;
  private boolean mFadingOut;
  private FalsingManager mFalsingManager;
  private Runnable mFinishScrollingCallback;
  private ActivatableNotificationView mFirstVisibleBackgroundChild = null;
  private boolean mForceNoOverlappingRendering;
  private View mForcedScroll;
  private boolean mForwardScrollable;
  private HashSet<View> mFromMoreCardAdditions = new HashSet();
  private View mGearExposedView;
  private boolean mGenerateChildOrderChangedEvent;
  private long mGoToFullShadeDelay;
  private boolean mGoToFullShadeNeedsAnimation;
  private boolean mGroupExpandedForMeasure;
  private NotificationGroupManager mGroupManager;
  private HashSet<Pair<ExpandableNotificationRow, Boolean>> mHeadsUpChangeAnimations = new HashSet();
  private HeadsUpManager mHeadsUpManager;
  private boolean mHideSensitiveNeedsAnimation;
  private int mIncreasedPaddingBetweenElements;
  private float mInitialTouchX;
  private float mInitialTouchY;
  private int mIntrinsicPadding;
  private boolean mIsBeingDragged;
  private boolean mIsExpanded = true;
  private boolean mIsExpansionChanging;
  private int mLastMotionY;
  private ActivatableNotificationView mLastVisibleBackgroundChild = null;
  private OnChildLocationsChangedListener mListener;
  private SwipeHelper.LongPressListener mLongPressListener;
  private int mMaxLayoutHeight;
  private float mMaxOverScroll;
  private int mMaxScrollAfterExpand;
  private int mMaximumVelocity;
  private float mMinTopOverScrollToEscape;
  private int mMinimumVelocity;
  private boolean mNeedViewResizeAnimation;
  private boolean mNeedsAnimation;
  private OnEmptySpaceClickListener mOnEmptySpaceClickListener;
  private ExpandableView.OnHeightChangedListener mOnHeightChangedListener;
  private boolean mOnlyScrollingInThisMotion;
  private float mOverScrolledBottomPixels;
  private float mOverScrolledTopPixels;
  private int mOverflingDistance;
  private NotificationOverflowContainer mOverflowContainer;
  private OnOverscrollTopChangedListener mOverscrollTopChangedListener;
  private int mOwnScrollY;
  private int mPaddingBetweenElements;
  private boolean mPanelTracking;
  private boolean mParentFadingOut;
  private PhoneStatusBar mPhoneStatusBar;
  private boolean mPulsing;
  protected ViewGroup mQsContainer;
  private boolean mQsExpanded;
  private Runnable mReclamp = new Runnable()
  {
    public void run()
    {
      int i = NotificationStackScrollLayout.-wrap0(NotificationStackScrollLayout.this);
      NotificationStackScrollLayout.-get13(NotificationStackScrollLayout.this).startScroll(NotificationStackScrollLayout.-get12(NotificationStackScrollLayout.this), NotificationStackScrollLayout.-get10(NotificationStackScrollLayout.this), 0, i - NotificationStackScrollLayout.-get10(NotificationStackScrollLayout.this));
      NotificationStackScrollLayout.-set5(NotificationStackScrollLayout.this, true);
      NotificationStackScrollLayout.-set4(NotificationStackScrollLayout.this, true);
      NotificationStackScrollLayout.this.postInvalidateOnAnimation();
    }
  };
  private ScrimController mScrimController;
  private boolean mScrollable;
  private boolean mScrolledToTopOnFirstDown;
  private OverScroller mScroller;
  private boolean mScrollingEnabled;
  private ViewTreeObserver.OnPreDrawListener mShadowUpdater = new ViewTreeObserver.OnPreDrawListener()
  {
    public boolean onPreDraw()
    {
      NotificationStackScrollLayout.-wrap10(NotificationStackScrollLayout.this);
      return true;
    }
  };
  private ArrayList<View> mSnappedBackChildren = new ArrayList();
  private PorterDuffXfermode mSrcMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
  private final StackScrollAlgorithm mStackScrollAlgorithm;
  private float mStackTranslation;
  private Rect mStartAnimationRect = new Rect();
  private final StackStateAnimator mStateAnimator = new StackStateAnimator(this);
  private NotificationSwipeHelper mSwipeHelper;
  private ArrayList<View> mSwipedOutViews = new ArrayList();
  private boolean mSwipingInProgress;
  private int[] mTempInt2 = new int[2];
  private final ArrayList<Pair<ExpandableNotificationRow, Boolean>> mTmpList = new ArrayList();
  private ArrayList<ExpandableView> mTmpSortedChildren = new ArrayList();
  private ObjectAnimator mTopAnimator = null;
  private int mTopPadding;
  private boolean mTopPaddingNeedsAnimation;
  private float mTopPaddingOverflow;
  private boolean mTouchIsClick;
  private int mTouchSlop;
  private boolean mTrackingHeadsUp;
  private View mTranslatingParentView;
  private VelocityTracker mVelocityTracker;
  private Comparator<ExpandableView> mViewPositionComparator = new Comparator()
  {
    public int compare(ExpandableView paramAnonymousExpandableView1, ExpandableView paramAnonymousExpandableView2)
    {
      float f1 = paramAnonymousExpandableView1.getTranslationY() + paramAnonymousExpandableView1.getActualHeight();
      float f2 = paramAnonymousExpandableView2.getTranslationY() + paramAnonymousExpandableView2.getActualHeight();
      if (f1 < f2) {
        return -1;
      }
      if (f1 > f2) {
        return 1;
      }
      return 0;
    }
  };
  
  public NotificationStackScrollLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NotificationStackScrollLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NotificationStackScrollLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public NotificationStackScrollLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mBgColor = paramContext.getColor(2131493026);
    paramInt1 = getResources().getDimensionPixelSize(2131755369);
    paramInt2 = getResources().getDimensionPixelSize(2131755371);
    this.mExpandHelper = new ExpandHelper(getContext(), this, paramInt1, paramInt2);
    this.mExpandHelper.setEventSource(this);
    this.mExpandHelper.setScrollAdapter(this);
    this.mSwipeHelper = new NotificationSwipeHelper(0, this, getContext());
    this.mSwipeHelper.setLongPressListener(this.mLongPressListener);
    this.mStackScrollAlgorithm = new StackScrollAlgorithm(paramContext);
    initView(paramContext);
    setWillNotDraw(false);
    this.mFalsingManager = FalsingManager.getInstance(paramContext);
  }
  
  private void abortBackgroundAnimators()
  {
    if (this.mBottomAnimator != null) {
      this.mBottomAnimator.cancel();
    }
    if (this.mTopAnimator != null) {
      this.mTopAnimator.cancel();
    }
  }
  
  private void animateDimmed(boolean paramBoolean)
  {
    if (this.mDimAnimator != null) {
      this.mDimAnimator.cancel();
    }
    if (paramBoolean) {}
    for (float f = 1.0F; f == this.mDimAmount; f = 0.0F) {
      return;
    }
    this.mDimAnimator = TimeAnimator.ofFloat(new float[] { this.mDimAmount, f });
    this.mDimAnimator.setDuration(220L);
    this.mDimAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    this.mDimAnimator.addListener(this.mDimEndListener);
    this.mDimAnimator.addUpdateListener(this.mDimUpdateListener);
    this.mDimAnimator.start();
  }
  
  private void applyCurrentBackgroundBounds()
  {
    ScrimController localScrimController = this.mScrimController;
    if ((this.mFadingOut) || (this.mParentFadingOut) || (this.mAmbientState.isDark())) {}
    for (Rect localRect = null;; localRect = this.mCurrentBounds)
    {
      localScrimController.setExcludedBackgroundArea(localRect);
      invalidate();
      return;
    }
  }
  
  private void applyCurrentState()
  {
    this.mCurrentStackScrollState.apply();
    if (this.mListener != null) {
      this.mListener.onChildLocationsChanged(this);
    }
    runAnimationFinishedRunnables();
    setAnimationRunning(false);
    updateBackground();
    updateViewShadows();
  }
  
  private boolean areBoundsAnimating()
  {
    return (this.mBottomAnimator != null) || (this.mTopAnimator != null);
  }
  
  private int clampPadding(int paramInt)
  {
    return Math.max(paramInt, this.mIntrinsicPadding);
  }
  
  private void clampScrollPosition()
  {
    int i = getScrollRange();
    if (i < this.mOwnScrollY) {
      setOwnScrollY(i);
    }
  }
  
  private void clearHeadsUpDisappearRunning()
  {
    int i = 0;
    while (i < getChildCount())
    {
      Object localObject = getChildAt(i);
      if ((localObject instanceof ExpandableNotificationRow))
      {
        localObject = (ExpandableNotificationRow)localObject;
        ((ExpandableNotificationRow)localObject).setHeadsupDisappearRunning(false);
        if (((ExpandableNotificationRow)localObject).isSummaryWithChildren())
        {
          localObject = ((ExpandableNotificationRow)localObject).getNotificationChildren().iterator();
          while (((Iterator)localObject).hasNext()) {
            ((ExpandableNotificationRow)((Iterator)localObject).next()).setHeadsupDisappearRunning(false);
          }
        }
      }
      i += 1;
    }
  }
  
  private void clearTemporaryViews(ViewGroup paramViewGroup)
  {
    while ((paramViewGroup != null) && (paramViewGroup.getTransientViewCount() != 0)) {
      paramViewGroup.removeTransientView(paramViewGroup.getTransientView(0));
    }
    if (paramViewGroup != null) {
      paramViewGroup.getOverlay().clear();
    }
  }
  
  private void clearViewOverlays()
  {
    Iterator localIterator = this.mClearOverlayViewsWhenFinished.iterator();
    while (localIterator.hasNext()) {
      StackStateAnimator.removeFromOverlay((View)localIterator.next());
    }
  }
  
  private void customScrollTo(int paramInt)
  {
    setOwnScrollY(paramInt);
    updateChildren();
  }
  
  private void dispatchDownEventToScroller(MotionEvent paramMotionEvent)
  {
    paramMotionEvent = MotionEvent.obtain(paramMotionEvent);
    paramMotionEvent.setAction(0);
    onScrollTouch(paramMotionEvent);
    paramMotionEvent.recycle();
  }
  
  private void endDrag()
  {
    setIsBeingDragged(false);
    recycleVelocityTracker();
    if (getCurrentOverScrollAmount(true) > 0.0F) {
      setOverScrollAmount(0.0F, true, true);
    }
    if (getCurrentOverScrollAmount(false) > 0.0F) {
      setOverScrollAmount(0.0F, false, true);
    }
  }
  
  private int findDarkAnimationOriginIndex(PointF paramPointF)
  {
    if ((paramPointF == null) || (paramPointF.y < this.mTopPadding)) {
      return -1;
    }
    if (paramPointF.y > getBottomMostNotificationBottom()) {
      return -2;
    }
    paramPointF = getClosestChildAtRawPosition(paramPointF.x, paramPointF.y);
    if (paramPointF != null) {
      return getNotGoneIndex(paramPointF);
    }
    return -1;
  }
  
  private void fling(int paramInt)
  {
    int i;
    float f2;
    OverScroller localOverScroller;
    int k;
    int m;
    if (getChildCount() > 0)
    {
      i = getScrollRange();
      float f1 = getCurrentOverScrollAmount(true);
      f2 = getCurrentOverScrollAmount(false);
      if ((paramInt >= 0) || (f1 <= 0.0F)) {
        break label174;
      }
      setOwnScrollY(this.mOwnScrollY - (int)f1);
      this.mDontReportNextOverScroll = true;
      setOverScrollAmount(0.0F, true, false);
      this.mMaxOverScroll = (Math.abs(paramInt) / 1000.0F * getRubberBandFactor(true) * this.mOverflingDistance + f1);
      j = Math.max(0, i);
      i = j;
      if (this.mExpandedInThisMotion) {
        i = Math.min(j, this.mMaxScrollAfterExpand);
      }
      localOverScroller = this.mScroller;
      k = this.mScrollX;
      m = this.mOwnScrollY;
      if ((!this.mExpandedInThisMotion) || (this.mOwnScrollY < 0)) {
        break label241;
      }
    }
    label174:
    label241:
    for (int j = 0;; j = 1073741823)
    {
      localOverScroller.fling(k, m, 1, paramInt, 0, 0, 0, i, 0, j);
      postInvalidateOnAnimation();
      return;
      if ((paramInt > 0) && (f2 > 0.0F))
      {
        setOwnScrollY((int)(this.mOwnScrollY + f2));
        setOverScrollAmount(0.0F, false, false);
        this.mMaxOverScroll = (Math.abs(paramInt) / 1000.0F * getRubberBandFactor(false) * this.mOverflingDistance + f2);
        break;
      }
      this.mMaxOverScroll = 0.0F;
      break;
    }
  }
  
  private void focusNextViewIfFocused(View paramView)
  {
    View localView1;
    if ((paramView instanceof ExpandableNotificationRow))
    {
      ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)paramView;
      if (localExpandableNotificationRow.shouldRefocusOnDismiss())
      {
        View localView2 = localExpandableNotificationRow.getChildAfterViewWhenDismissed();
        localView1 = localView2;
        if (localView2 == null)
        {
          localView1 = localExpandableNotificationRow.getGroupParentWhenDismissed();
          if (localView1 == null) {
            break label67;
          }
        }
      }
    }
    label67:
    for (float f = localView1.getTranslationY();; f = paramView.getTranslationY())
    {
      localView1 = getFirstChildBelowTranlsationY(f);
      if (localView1 != null) {
        localView1.requestAccessibilityFocus();
      }
      return;
    }
  }
  
  private void generateActivateEvent()
  {
    if (this.mActivateNeedsAnimation) {
      this.mAnimationEvents.add(new AnimationEvent(null, 6));
    }
    this.mActivateNeedsAnimation = false;
  }
  
  private void generateAnimateEverythingEvent()
  {
    if (this.mEverythingNeedsAnimation) {
      this.mAnimationEvents.add(new AnimationEvent(null, 18));
    }
    this.mEverythingNeedsAnimation = false;
  }
  
  private void generateChildAdditionEvents()
  {
    Iterator localIterator = this.mChildrenToAddAnimated.iterator();
    while (localIterator.hasNext())
    {
      View localView = (View)localIterator.next();
      if (this.mFromMoreCardAdditions.contains(localView)) {
        this.mAnimationEvents.add(new AnimationEvent(localView, 0, 360L));
      } else {
        this.mAnimationEvents.add(new AnimationEvent(localView, 0));
      }
    }
    this.mChildrenToAddAnimated.clear();
    this.mFromMoreCardAdditions.clear();
  }
  
  private void generateChildHierarchyEvents()
  {
    generateHeadsUpAnimationEvents();
    generateChildRemovalEvents();
    generateChildAdditionEvents();
    generatePositionChangeEvents();
    generateSnapBackEvents();
    generateDragEvents();
    generateTopPaddingEvent();
    generateActivateEvent();
    generateDimmedEvent();
    generateHideSensitiveEvent();
    generateDarkEvent();
    generateGoToFullShadeEvent();
    generateViewResizeEvent();
    generateGroupExpansionEvent();
    generateAnimateEverythingEvent();
    this.mNeedsAnimation = false;
  }
  
  private void generateChildRemovalEvents()
  {
    Iterator localIterator = this.mChildrenToRemoveAnimated.iterator();
    if (localIterator.hasNext())
    {
      View localView = (View)localIterator.next();
      if (this.mSwipedOutViews.contains(localView)) {}
      for (int i = 2;; i = 1)
      {
        AnimationEvent localAnimationEvent = new AnimationEvent(localView, i);
        localAnimationEvent.viewAfterChangingView = getFirstChildBelowTranlsationY(localView.getTranslationY());
        this.mAnimationEvents.add(localAnimationEvent);
        this.mSwipedOutViews.remove(localView);
        break;
      }
    }
    this.mChildrenToRemoveAnimated.clear();
  }
  
  private void generateDarkEvent()
  {
    if (this.mDarkNeedsAnimation)
    {
      AnimationEvent localAnimationEvent = new AnimationEvent(null, 9);
      localAnimationEvent.darkAnimationOriginIndex = this.mDarkAnimationOriginIndex;
      this.mAnimationEvents.add(localAnimationEvent);
      startBackgroundFadeIn();
    }
    this.mDarkNeedsAnimation = false;
  }
  
  private void generateDimmedEvent()
  {
    if (this.mDimmedNeedsAnimation) {
      this.mAnimationEvents.add(new AnimationEvent(null, 7));
    }
    this.mDimmedNeedsAnimation = false;
  }
  
  private void generateDragEvents()
  {
    Iterator localIterator = this.mDragAnimPendingChildren.iterator();
    while (localIterator.hasNext())
    {
      View localView = (View)localIterator.next();
      this.mAnimationEvents.add(new AnimationEvent(localView, 4));
    }
    this.mDragAnimPendingChildren.clear();
  }
  
  private void generateGoToFullShadeEvent()
  {
    if (this.mGoToFullShadeNeedsAnimation) {
      this.mAnimationEvents.add(new AnimationEvent(null, 10));
    }
    this.mGoToFullShadeNeedsAnimation = false;
  }
  
  private void generateGroupExpansionEvent()
  {
    if (this.mExpandedGroupView != null)
    {
      this.mAnimationEvents.add(new AnimationEvent(this.mExpandedGroupView, 13));
      this.mExpandedGroupView = null;
    }
  }
  
  private void generateHeadsUpAnimationEvents()
  {
    Iterator localIterator = this.mHeadsUpChangeAnimations.iterator();
    while (localIterator.hasNext())
    {
      Object localObject2 = (Pair)localIterator.next();
      Object localObject1 = (ExpandableNotificationRow)((Pair)localObject2).first;
      boolean bool3 = ((Boolean)((Pair)localObject2).second).booleanValue();
      int k = 17;
      boolean bool2 = false;
      int j;
      label79:
      boolean bool1;
      int i;
      if ((!((ExpandableNotificationRow)localObject1).isPinned()) || (this.mIsExpanded))
      {
        j = 0;
        if ((!this.mIsExpanded) && (!bool3)) {
          break label200;
        }
        localObject2 = this.mCurrentStackScrollState.getViewStateForView((View)localObject1);
        if (localObject2 == null) {
          continue;
        }
        bool1 = bool2;
        i = k;
        if (bool3) {
          if (!this.mAddedHeadsUpChildren.contains(localObject1))
          {
            bool1 = bool2;
            i = k;
            if (j == 0) {}
          }
          else
          {
            if ((j == 0) && (!shouldHunAppearFromBottom((StackViewState)localObject2))) {
              break label246;
            }
            i = 14;
            label156:
            if (j == 0) {
              break label251;
            }
            bool1 = false;
          }
        }
      }
      for (;;)
      {
        localObject1 = new AnimationEvent((View)localObject1, i);
        ((AnimationEvent)localObject1).headsUpFromBottom = bool1;
        this.mAnimationEvents.add(localObject1);
        break;
        j = 1;
        break label79;
        label200:
        if (((ExpandableNotificationRow)localObject1).wasJustClicked()) {}
        for (j = 16;; j = 15)
        {
          bool1 = bool2;
          i = j;
          if (!((ExpandableNotificationRow)localObject1).isChildInGroup()) {
            break;
          }
          ((ExpandableNotificationRow)localObject1).setHeadsupDisappearRunning(false);
          bool1 = bool2;
          i = j;
          break;
        }
        label246:
        i = 0;
        break label156;
        label251:
        bool1 = true;
      }
    }
    this.mHeadsUpChangeAnimations.clear();
    this.mAddedHeadsUpChildren.clear();
  }
  
  private void generateHideSensitiveEvent()
  {
    if (this.mHideSensitiveNeedsAnimation) {
      this.mAnimationEvents.add(new AnimationEvent(null, 11));
    }
    this.mHideSensitiveNeedsAnimation = false;
  }
  
  private void generatePositionChangeEvents()
  {
    Iterator localIterator = this.mChildrenChangingPositions.iterator();
    while (localIterator.hasNext())
    {
      View localView = (View)localIterator.next();
      this.mAnimationEvents.add(new AnimationEvent(localView, 8));
    }
    this.mChildrenChangingPositions.clear();
    if (this.mGenerateChildOrderChangedEvent)
    {
      this.mAnimationEvents.add(new AnimationEvent(null, 8));
      this.mGenerateChildOrderChangedEvent = false;
    }
  }
  
  private boolean generateRemoveAnimation(View paramView)
  {
    if (removeRemovedChildFromHeadsUpChangeAnimations(paramView))
    {
      this.mAddedHeadsUpChildren.remove(paramView);
      return false;
    }
    if (isClickedHeadsUp(paramView))
    {
      this.mClearOverlayViewsWhenFinished.add(paramView);
      return true;
    }
    if ((!this.mIsExpanded) || (!this.mAnimationsEnabled) || (isChildInInvisibleGroup(paramView))) {
      return false;
    }
    if (!this.mChildrenToAddAnimated.contains(paramView))
    {
      this.mChildrenToRemoveAnimated.add(paramView);
      this.mNeedsAnimation = true;
      return true;
    }
    this.mChildrenToAddAnimated.remove(paramView);
    this.mFromMoreCardAdditions.remove(paramView);
    return false;
  }
  
  private void generateSnapBackEvents()
  {
    Iterator localIterator = this.mSnappedBackChildren.iterator();
    while (localIterator.hasNext())
    {
      View localView = (View)localIterator.next();
      this.mAnimationEvents.add(new AnimationEvent(localView, 5));
    }
    this.mSnappedBackChildren.clear();
  }
  
  private void generateTopPaddingEvent()
  {
    if (this.mTopPaddingNeedsAnimation) {
      this.mAnimationEvents.add(new AnimationEvent(null, 3));
    }
    this.mTopPaddingNeedsAnimation = false;
  }
  
  private void generateViewResizeEvent()
  {
    if (this.mNeedViewResizeAnimation) {
      this.mAnimationEvents.add(new AnimationEvent(null, 12));
    }
    this.mNeedViewResizeAnimation = false;
  }
  
  private float getAppearEndPosition()
  {
    int i;
    if ((this.mTrackingHeadsUp) || (this.mHeadsUpManager.hasPinnedHeadsUp()))
    {
      i = this.mHeadsUpManager.getTopHeadsUpPinnedHeight() + this.mBottomStackPeekSize + this.mBottomStackSlowDownHeight;
      if (!onKeyguard()) {
        break label60;
      }
    }
    label60:
    for (int j = this.mTopPadding;; j = this.mIntrinsicPadding)
    {
      return j + i;
      i = getLayoutMinHeight();
      break;
    }
  }
  
  private float getAppearStartPosition()
  {
    if (this.mTrackingHeadsUp) {}
    for (int i = this.mHeadsUpManager.getTopHeadsUpPinnedHeight();; i = 0) {
      return i;
    }
  }
  
  private float getExpandTranslationStart()
  {
    int j = 0;
    int i = j;
    if (!this.mTrackingHeadsUp) {
      if (!this.mHeadsUpManager.hasPinnedHeadsUp()) {
        break label31;
      }
    }
    label31:
    for (i = j;; i = -Math.min(getFirstChildIntrinsicHeight(), this.mMaxLayoutHeight - this.mIntrinsicPadding - this.mBottomStackSlowDownHeight - this.mBottomStackPeekSize)) {
      return i - this.mTopPadding;
    }
  }
  
  private View getFirstChildBelowTranlsationY(float paramFloat)
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if ((localView.getVisibility() != 8) && (localView.getTranslationY() >= paramFloat)) {
        return localView;
      }
      i += 1;
    }
    return null;
  }
  
  private ActivatableNotificationView getFirstChildWithBackground()
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if ((localView.getVisibility() != 8) && ((localView instanceof ActivatableNotificationView))) {
        return (ActivatableNotificationView)localView;
      }
      i += 1;
    }
    return null;
  }
  
  private int getImeInset()
  {
    return Math.max(0, this.mBottomInset - (getRootView().getHeight() - getHeight()));
  }
  
  private int getIntrinsicHeight(View paramView)
  {
    if ((paramView instanceof ExpandableView)) {
      return ((ExpandableView)paramView).getIntrinsicHeight();
    }
    return paramView.getHeight();
  }
  
  private ActivatableNotificationView getLastChildWithBackground()
  {
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      View localView = getChildAt(i);
      if ((localView.getVisibility() != 8) && ((localView instanceof ActivatableNotificationView))) {
        return (ActivatableNotificationView)localView;
      }
      i -= 1;
    }
    return null;
  }
  
  private int getLayoutHeight()
  {
    return Math.min(this.mMaxLayoutHeight, this.mCurrentStackHeight);
  }
  
  private int getNotGoneIndex(View paramView)
  {
    int m = getChildCount();
    int j = 0;
    int i = 0;
    while (i < m)
    {
      View localView = getChildAt(i);
      if (paramView == localView) {
        return j;
      }
      int k = j;
      if (localView.getVisibility() != 8) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return -1;
  }
  
  private int getPositionInLinearLayout(View paramView)
  {
    ExpandableNotificationRow localExpandableNotificationRow1 = null;
    ExpandableNotificationRow localExpandableNotificationRow2 = null;
    Object localObject = paramView;
    if (isChildInGroup(paramView))
    {
      localExpandableNotificationRow1 = (ExpandableNotificationRow)paramView;
      localExpandableNotificationRow2 = localExpandableNotificationRow1.getNotificationParent();
      localObject = localExpandableNotificationRow2;
    }
    int i = 0;
    float f1 = 0.0F;
    int k = 0;
    while (k < getChildCount())
    {
      paramView = (ExpandableView)getChildAt(k);
      if (paramView.getVisibility() != 8) {}
      int j;
      float f2;
      for (int m = 1;; m = 0)
      {
        j = i;
        f2 = f1;
        if (m != 0)
        {
          f2 = paramView.getIncreasedPaddingAmount();
          j = i;
          if (i != 0) {
            j = i + (int)NotificationUtils.interpolate(this.mPaddingBetweenElements, this.mIncreasedPaddingBetweenElements, Math.max(f1, f2));
          }
        }
        if (paramView != localObject) {
          break;
        }
        i = j;
        if (localExpandableNotificationRow2 != null) {
          i = j + localExpandableNotificationRow2.getPositionOfChild(localExpandableNotificationRow1);
        }
        return i;
      }
      i = j;
      if (m != 0) {
        i = j + getIntrinsicHeight(paramView);
      }
      k += 1;
      f1 = f2;
    }
    return 0;
  }
  
  private float getRubberBandFactor(boolean paramBoolean)
  {
    if (!paramBoolean) {
      return 0.35F;
    }
    if (this.mExpandedInThisMotion) {
      return 0.15F;
    }
    if ((this.mIsExpansionChanging) || (this.mPanelTracking)) {
      return 0.21F;
    }
    if (this.mScrolledToTopOnFirstDown) {
      return 1.0F;
    }
    return 0.35F;
  }
  
  private int getScrollRange()
  {
    int i = Math.max(0, getContentHeight() - this.mMaxLayoutHeight + this.mBottomStackPeekSize + this.mBottomStackSlowDownHeight);
    int j = getImeInset();
    return i + Math.min(j, Math.max(0, getContentHeight() - (getHeight() - j)));
  }
  
  private int getStackEndPosition()
  {
    return this.mMaxLayoutHeight - this.mBottomStackPeekSize - this.mBottomStackSlowDownHeight + this.mPaddingBetweenElements + (int)this.mStackTranslation;
  }
  
  private void handleChildDismissed(View paramView)
  {
    if (this.mDismissAllInProgress) {
      return;
    }
    setSwipingInProgress(false);
    if (this.mDragAnimPendingChildren.contains(paramView)) {
      this.mDragAnimPendingChildren.remove(paramView);
    }
    this.mSwipedOutViews.add(paramView);
    this.mAmbientState.onDragFinished(paramView);
    updateContinuousShadowDrawing();
    if ((paramView instanceof ExpandableNotificationRow))
    {
      ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)paramView;
      if (localExpandableNotificationRow.isHeadsUp()) {
        this.mHeadsUpManager.addSwipedOutNotification(localExpandableNotificationRow.getStatusBarNotification().getKey());
      }
    }
    performDismiss(paramView, this.mGroupManager, false);
    this.mFalsingManager.onNotificationDismissed();
    if (this.mFalsingManager.shouldEnforceBouncer()) {
      this.mPhoneStatusBar.executeRunnableDismissingKeyguard(null, null, false, true, false);
    }
  }
  
  private void handleDismissAllClipping()
  {
    int j = getChildCount();
    boolean bool = false;
    int i = 0;
    while (i < j)
    {
      ExpandableView localExpandableView = (ExpandableView)getChildAt(i);
      if (localExpandableView.getVisibility() == 8)
      {
        i += 1;
      }
      else
      {
        if ((this.mDismissAllInProgress) && (bool)) {
          localExpandableView.setMinClipTopAmount(localExpandableView.getClipTopAmount());
        }
        for (;;)
        {
          bool = canChildBeDismissed(localExpandableView);
          break;
          localExpandableView.setMinClipTopAmount(0);
        }
      }
    }
  }
  
  private void handleEmptySpaceClick(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    }
    do
    {
      do
      {
        return;
      } while ((!this.mTouchIsClick) || ((Math.abs(paramMotionEvent.getY() - this.mInitialTouchY) <= this.mTouchSlop) && (Math.abs(paramMotionEvent.getX() - this.mInitialTouchX) <= this.mTouchSlop)));
      this.mTouchIsClick = false;
      return;
    } while ((this.mPhoneStatusBar.getBarState() == 1) || (!this.mTouchIsClick) || (!isBelowLastNotification(this.mInitialTouchX, this.mInitialTouchY)));
    this.mOnEmptySpaceClickListener.onEmptySpaceClicked(this.mInitialTouchX, this.mInitialTouchY);
  }
  
  private void initDownStates(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 0)
    {
      this.mExpandedInThisMotion = false;
      if (!this.mScroller.isFinished()) {
        break label61;
      }
    }
    label61:
    for (boolean bool = false;; bool = true)
    {
      this.mOnlyScrollingInThisMotion = bool;
      this.mDisallowScrollingInThisMotion = false;
      this.mDisallowDismissInThisMotion = false;
      this.mTouchIsClick = true;
      this.mInitialTouchX = paramMotionEvent.getX();
      this.mInitialTouchY = paramMotionEvent.getY();
      return;
    }
  }
  
  private void initOrResetVelocityTracker()
  {
    if (this.mVelocityTracker == null)
    {
      this.mVelocityTracker = VelocityTracker.obtain();
      return;
    }
    this.mVelocityTracker.clear();
  }
  
  private void initVelocityTrackerIfNotExists()
  {
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
  }
  
  private void initView(Context paramContext)
  {
    this.mScroller = new OverScroller(getContext());
    setDescendantFocusability(262144);
    setClipChildren(false);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(paramContext);
    this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
    this.mMinimumVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
    this.mOverflingDistance = localViewConfiguration.getScaledOverflingDistance();
    this.mCollapsedSize = paramContext.getResources().getDimensionPixelSize(2131755369);
    this.mBottomStackPeekSize = paramContext.getResources().getDimensionPixelSize(2131755457);
    this.mStackScrollAlgorithm.initView(paramContext);
    this.mPaddingBetweenElements = Math.max(1, paramContext.getResources().getDimensionPixelSize(2131755461));
    this.mIncreasedPaddingBetweenElements = paramContext.getResources().getDimensionPixelSize(2131755463);
    this.mBottomStackSlowDownHeight = this.mStackScrollAlgorithm.getBottomStackSlowDownLength();
    this.mMinTopOverScrollToEscape = getResources().getDimensionPixelSize(2131755464);
  }
  
  private boolean isChildInGroup(View paramView)
  {
    if ((paramView instanceof ExpandableNotificationRow)) {
      return this.mGroupManager.isChildInGroupWithSummary(((ExpandableNotificationRow)paramView).getStatusBarNotification());
    }
    return false;
  }
  
  private boolean isChildInInvisibleGroup(View paramView)
  {
    boolean bool = false;
    if ((paramView instanceof ExpandableNotificationRow))
    {
      paramView = (ExpandableNotificationRow)paramView;
      ExpandableNotificationRow localExpandableNotificationRow = this.mGroupManager.getGroupSummary(paramView.getStatusBarNotification());
      if ((localExpandableNotificationRow != null) && (localExpandableNotificationRow != paramView))
      {
        if (paramView.getVisibility() == 4) {
          bool = true;
        }
        return bool;
      }
    }
    return false;
  }
  
  private boolean isClickedHeadsUp(View paramView)
  {
    return HeadsUpManager.isClickedHeadsUpNotification(paramView);
  }
  
  private boolean isCurrentlyAnimating()
  {
    return this.mStateAnimator.isRunning();
  }
  
  private boolean isHeadsUp(View paramView)
  {
    if ((paramView instanceof ExpandableNotificationRow)) {
      return ((ExpandableNotificationRow)paramView).isHeadsUp();
    }
    return false;
  }
  
  private boolean isInContentBounds(MotionEvent paramMotionEvent)
  {
    return isInContentBounds(paramMotionEvent.getY());
  }
  
  public static boolean isPinnedHeadsUp(View paramView)
  {
    boolean bool = false;
    if ((paramView instanceof ExpandableNotificationRow))
    {
      paramView = (ExpandableNotificationRow)paramView;
      if (paramView.isHeadsUp()) {
        bool = paramView.isPinned();
      }
      return bool;
    }
    return false;
  }
  
  private boolean isRubberbanded(boolean paramBoolean)
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (paramBoolean)
    {
      bool1 = bool2;
      if (!this.mExpandedInThisMotion)
      {
        bool1 = bool2;
        if (!this.mIsExpansionChanging)
        {
          bool1 = bool2;
          if (!this.mPanelTracking)
          {
            bool1 = bool2;
            if (this.mScrolledToTopOnFirstDown) {
              bool1 = false;
            }
          }
        }
      }
    }
    return bool1;
  }
  
  private boolean isScrollingEnabled()
  {
    return this.mScrollingEnabled;
  }
  
  private void notifyHeightChangeListener(ExpandableView paramExpandableView)
  {
    if (this.mOnHeightChangedListener != null) {
      this.mOnHeightChangedListener.onHeightChanged(paramExpandableView, false);
    }
  }
  
  private void notifyOverscrollTopListener(float paramFloat, boolean paramBoolean)
  {
    ExpandHelper localExpandHelper = this.mExpandHelper;
    if (paramFloat > 1.0F) {}
    for (boolean bool = true;; bool = false)
    {
      localExpandHelper.onlyObserveMovements(bool);
      if (!this.mDontReportNextOverScroll) {
        break;
      }
      this.mDontReportNextOverScroll = false;
      return;
    }
    if (this.mOverscrollTopChangedListener != null) {
      this.mOverscrollTopChangedListener.onOverscrollTopChanged(paramFloat, paramBoolean);
    }
  }
  
  private boolean onInterceptTouchEventScroll(MotionEvent paramMotionEvent)
  {
    if (!isScrollingEnabled()) {
      return false;
    }
    int i = paramMotionEvent.getAction();
    if ((i == 2) && (this.mIsBeingDragged)) {
      return true;
    }
    switch (i & 0xFF)
    {
    }
    for (;;)
    {
      return this.mIsBeingDragged;
      i = this.mActivePointerId;
      if (i != -1)
      {
        int j = paramMotionEvent.findPointerIndex(i);
        if (j == -1)
        {
          Log.e("StackScroller", "Invalid pointerId=" + i + " in onInterceptTouchEvent");
        }
        else
        {
          i = (int)paramMotionEvent.getY(j);
          j = (int)paramMotionEvent.getX(j);
          int k = Math.abs(i - this.mLastMotionY);
          int m = Math.abs(j - this.mDownX);
          if ((k > this.mTouchSlop) && (k > m))
          {
            setIsBeingDragged(true);
            this.mLastMotionY = i;
            this.mDownX = j;
            initVelocityTrackerIfNotExists();
            this.mVelocityTracker.addMovement(paramMotionEvent);
            continue;
            i = (int)paramMotionEvent.getY();
            this.mScrolledToTopOnFirstDown = isScrolledToTop();
            if (getChildAtPosition(paramMotionEvent.getX(), i) == null)
            {
              setIsBeingDragged(false);
              recycleVelocityTracker();
            }
            else
            {
              this.mLastMotionY = i;
              this.mDownX = ((int)paramMotionEvent.getX());
              this.mActivePointerId = paramMotionEvent.getPointerId(0);
              initOrResetVelocityTracker();
              this.mVelocityTracker.addMovement(paramMotionEvent);
              if (this.mScroller.isFinished()) {}
              for (boolean bool = false;; bool = true)
              {
                setIsBeingDragged(bool);
                break;
              }
              setIsBeingDragged(false);
              this.mActivePointerId = -1;
              recycleVelocityTracker();
              if (this.mScroller.springBack(this.mScrollX, this.mOwnScrollY, 0, 0, 0, getScrollRange()))
              {
                postInvalidateOnAnimation();
                continue;
                onSecondaryPointerUp(paramMotionEvent);
              }
            }
          }
        }
      }
    }
  }
  
  private boolean onKeyguard()
  {
    return this.mPhoneStatusBar.getBarState() == 1;
  }
  
  private void onOverScrollFling(boolean paramBoolean, int paramInt)
  {
    if (this.mOverscrollTopChangedListener != null) {
      this.mOverscrollTopChangedListener.flingTopOverscroll(paramInt, paramBoolean);
    }
    this.mDontReportNextOverScroll = true;
    setOverScrollAmount(0.0F, true, false);
  }
  
  private boolean onScrollTouch(MotionEvent paramMotionEvent)
  {
    if (!isScrollingEnabled()) {
      return false;
    }
    if ((paramMotionEvent.getY() >= this.mQsContainer.getBottom()) || (this.mIsBeingDragged))
    {
      this.mForcedScroll = null;
      initVelocityTrackerIfNotExists();
      this.mVelocityTracker.addMovement(paramMotionEvent);
      switch (paramMotionEvent.getAction() & 0xFF)
      {
      }
    }
    for (;;)
    {
      return true;
      return false;
      if ((getChildCount() != 0) && (isInContentBounds(paramMotionEvent))) {
        if (!this.mScroller.isFinished()) {
          break label188;
        }
      }
      label188:
      for (boolean bool = false;; bool = true)
      {
        setIsBeingDragged(bool);
        if (!this.mScroller.isFinished()) {
          this.mScroller.forceFinished(true);
        }
        this.mLastMotionY = ((int)paramMotionEvent.getY());
        this.mDownX = ((int)paramMotionEvent.getX());
        this.mActivePointerId = paramMotionEvent.getPointerId(0);
        break;
        return false;
      }
      int i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
      if (i == -1)
      {
        Log.e("StackScroller", "Invalid pointerId=" + this.mActivePointerId + " in onTouchEvent");
      }
      else
      {
        int k = (int)paramMotionEvent.getY(i);
        i = (int)paramMotionEvent.getX(i);
        int j = this.mLastMotionY - k;
        int m = Math.abs(i - this.mDownX);
        int n = Math.abs(j);
        i = j;
        if (!this.mIsBeingDragged)
        {
          i = j;
          if (n > this.mTouchSlop)
          {
            i = j;
            if (n > m)
            {
              setIsBeingDragged(true);
              if (j <= 0) {
                break label423;
              }
              i = j - this.mTouchSlop;
            }
          }
        }
        label339:
        if (this.mIsBeingDragged)
        {
          this.mLastMotionY = k;
          k = getScrollRange();
          j = k;
          if (this.mExpandedInThisMotion) {
            j = Math.min(k, this.mMaxScrollAfterExpand);
          }
          if (i < 0) {}
          for (float f = overScrollDown(i); f != 0.0F; f = overScrollUp(i, j))
          {
            overScrollBy(0, (int)f, 0, this.mOwnScrollY, 0, j, 0, getHeight() / 2, true);
            break;
            label423:
            i = j + this.mTouchSlop;
            break label339;
          }
          if (this.mIsBeingDragged)
          {
            paramMotionEvent = this.mVelocityTracker;
            paramMotionEvent.computeCurrentVelocity(1000, this.mMaximumVelocity);
            i = (int)paramMotionEvent.getYVelocity(this.mActivePointerId);
            if (shouldOverScrollFling(i)) {
              onOverScrollFling(true, i);
            }
            for (;;)
            {
              this.mActivePointerId = -1;
              endDrag();
              break;
              if (getChildCount() > 0) {
                if (Math.abs(i) > this.mMinimumVelocity)
                {
                  if ((getCurrentOverScrollAmount(true) == 0.0F) || (i > 0)) {
                    fling(-i);
                  } else {
                    onOverScrollFling(false, i);
                  }
                }
                else if (this.mScroller.springBack(this.mScrollX, this.mOwnScrollY, 0, 0, 0, getScrollRange())) {
                  postInvalidateOnAnimation();
                }
              }
            }
            if ((this.mIsBeingDragged) && (getChildCount() > 0))
            {
              if (this.mScroller.springBack(this.mScrollX, this.mOwnScrollY, 0, 0, 0, getScrollRange())) {
                postInvalidateOnAnimation();
              }
              this.mActivePointerId = -1;
              endDrag();
              continue;
              i = paramMotionEvent.getActionIndex();
              this.mLastMotionY = ((int)paramMotionEvent.getY(i));
              this.mDownX = ((int)paramMotionEvent.getX(i));
              this.mActivePointerId = paramMotionEvent.getPointerId(i);
              continue;
              onSecondaryPointerUp(paramMotionEvent);
              try
              {
                this.mLastMotionY = ((int)paramMotionEvent.getY(paramMotionEvent.findPointerIndex(this.mActivePointerId)));
                this.mDownX = ((int)paramMotionEvent.getX(paramMotionEvent.findPointerIndex(this.mActivePointerId)));
              }
              catch (IllegalArgumentException paramMotionEvent)
              {
                Log.e("StackScroller", "pointerIndex out of range when onScrollTouch pointer up");
              }
            }
          }
        }
      }
    }
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = 0;
    int j = (paramMotionEvent.getAction() & 0xFF00) >> 8;
    if (paramMotionEvent.getPointerId(j) == this.mActivePointerId)
    {
      if (j == 0) {
        i = 1;
      }
      this.mLastMotionY = ((int)paramMotionEvent.getY(i));
      this.mActivePointerId = paramMotionEvent.getPointerId(i);
      if (this.mVelocityTracker != null) {
        this.mVelocityTracker.clear();
      }
    }
  }
  
  private void onViewAddedInternal(View paramView)
  {
    updateHideSensitiveForChild(paramView);
    ((ExpandableView)paramView).setOnHeightChangedListener(this);
    generateAddAnimation(paramView, false);
    updateAnimationState(paramView);
    updateChronometerForChild(paramView);
  }
  
  private void onViewRemovedInternal(View paramView, ViewGroup paramViewGroup)
  {
    if (this.mChangePositionInProgress) {
      return;
    }
    ExpandableView localExpandableView = (ExpandableView)paramView;
    localExpandableView.setOnHeightChangedListener(null);
    this.mCurrentStackScrollState.removeViewStateForView(paramView);
    updateScrollStateForRemovedChild(localExpandableView);
    if (generateRemoveAnimation(paramView)) {
      if (!this.mSwipedOutViews.contains(paramView)) {
        paramViewGroup.getOverlay().add(paramView);
      }
    }
    for (;;)
    {
      updateAnimationState(false, paramView);
      localExpandableView.setClipTopAmount(0);
      focusNextViewIfFocused(paramView);
      return;
      if (Math.abs(localExpandableView.getTranslation()) != localExpandableView.getWidth())
      {
        paramViewGroup.addTransientView(paramView, 0);
        localExpandableView.setTransientContainer(paramViewGroup);
        continue;
        this.mSwipedOutViews.remove(paramView);
      }
    }
  }
  
  private float overScrollDown(int paramInt)
  {
    paramInt = Math.min(paramInt, 0);
    float f2 = getCurrentOverScrollAmount(false);
    float f1 = f2 + paramInt;
    if (f2 > 0.0F) {
      setOverScrollAmount(f1, false, false);
    }
    if (f1 < 0.0F) {}
    for (;;)
    {
      f2 = this.mOwnScrollY + f1;
      if (f2 < 0.0F)
      {
        setOverScrolledPixels(getCurrentOverScrolledPixels(true) - f2, true, false);
        setOwnScrollY(0);
        f1 = 0.0F;
      }
      return f1;
      f1 = 0.0F;
    }
  }
  
  private float overScrollUp(int paramInt1, int paramInt2)
  {
    paramInt1 = Math.max(paramInt1, 0);
    float f1 = getCurrentOverScrollAmount(true);
    float f2 = f1 - paramInt1;
    if (f1 > 0.0F) {
      setOverScrollAmount(f2, true, false);
    }
    if (f2 < 0.0F) {}
    for (f1 = -f2;; f1 = 0.0F)
    {
      f2 = this.mOwnScrollY + f1;
      if (f2 > paramInt2)
      {
        if (!this.mExpandedInThisMotion) {
          setOverScrolledPixels(getCurrentOverScrolledPixels(false) + f2 - paramInt2, false, false);
        }
        setOwnScrollY(paramInt2);
        f1 = 0.0F;
      }
      return f1;
    }
  }
  
  public static void performDismiss(View paramView, NotificationGroupManager paramNotificationGroupManager, boolean paramBoolean)
  {
    if (!(paramView instanceof ExpandableNotificationRow)) {
      return;
    }
    paramView = (ExpandableNotificationRow)paramView;
    if (paramNotificationGroupManager.isOnlyChildInGroup(paramView.getStatusBarNotification()))
    {
      ExpandableNotificationRow localExpandableNotificationRow = paramNotificationGroupManager.getLogicalGroupSummary(paramView.getStatusBarNotification());
      if (localExpandableNotificationRow.isClearable()) {
        performDismiss(localExpandableNotificationRow, paramNotificationGroupManager, paramBoolean);
      }
    }
    paramView.setDismissed(true, paramBoolean);
    if (paramView.isClearable()) {
      paramView.performDismiss();
    }
  }
  
  private void recycleVelocityTracker()
  {
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  private boolean removeRemovedChildFromHeadsUpChangeAnimations(View paramView)
  {
    boolean bool1 = false;
    Iterator localIterator = this.mHeadsUpChangeAnimations.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)localPair.first;
      boolean bool2 = ((Boolean)localPair.second).booleanValue();
      if (paramView == localExpandableNotificationRow)
      {
        this.mTmpList.add(localPair);
        bool1 |= bool2;
      }
    }
    if (bool1)
    {
      this.mHeadsUpChangeAnimations.removeAll(this.mTmpList);
      ((ExpandableNotificationRow)paramView).setHeadsupDisappearRunning(false);
    }
    this.mTmpList.clear();
    return bool1;
  }
  
  private void requestAnimateEverything()
  {
    if ((this.mIsExpanded) && (this.mAnimationsEnabled))
    {
      this.mEverythingNeedsAnimation = true;
      this.mNeedsAnimation = true;
      requestChildrenUpdate();
    }
  }
  
  private void requestAnimationOnViewResize(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    if ((this.mAnimationsEnabled) && ((this.mIsExpanded) || ((paramExpandableNotificationRow != null) && (paramExpandableNotificationRow.isPinned()))))
    {
      this.mNeedViewResizeAnimation = true;
      this.mNeedsAnimation = true;
    }
  }
  
  private void requestChildrenUpdate()
  {
    if (!this.mChildrenUpdateRequested)
    {
      getViewTreeObserver().addOnPreDrawListener(this.mChildrenUpdater);
      this.mChildrenUpdateRequested = true;
      invalidate();
    }
  }
  
  private void runAnimationFinishedRunnables()
  {
    Iterator localIterator = this.mAnimationFinishedRunnables.iterator();
    while (localIterator.hasNext()) {
      ((Runnable)localIterator.next()).run();
    }
    this.mAnimationFinishedRunnables.clear();
  }
  
  private void setBackgroundFadeAmount(float paramFloat)
  {
    this.mBackgroundFadeAmount = paramFloat;
    updateBackgroundDimming();
  }
  
  private void setBackgroundTop(int paramInt)
  {
    this.mCurrentBounds.top = paramInt;
    applyCurrentBackgroundBounds();
  }
  
  private void setDimAmount(float paramFloat)
  {
    this.mDimAmount = paramFloat;
    updateBackgroundDimming();
  }
  
  private void setIsBeingDragged(boolean paramBoolean)
  {
    this.mIsBeingDragged = paramBoolean;
    if (paramBoolean)
    {
      requestDisallowInterceptTouchEvent(true);
      removeLongPressCallback();
    }
  }
  
  private void setIsExpanded(boolean paramBoolean)
  {
    if (paramBoolean != this.mIsExpanded) {}
    for (int i = 1;; i = 0)
    {
      this.mIsExpanded = paramBoolean;
      this.mStackScrollAlgorithm.setIsExpanded(paramBoolean);
      if (i != 0)
      {
        if (!this.mIsExpanded) {
          this.mGroupManager.collapseAllGroups();
        }
        updateNotificationAnimationStates();
        updateChronometers();
      }
      return;
    }
  }
  
  private void setMaxLayoutHeight(int paramInt)
  {
    this.mMaxLayoutHeight = paramInt;
    updateAlgorithmHeightAndPadding();
  }
  
  private void setOverScrollAmountInternal(float paramFloat, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    paramFloat = Math.max(0.0F, paramFloat);
    if (paramBoolean2)
    {
      this.mStateAnimator.animateOverScrollToAmount(paramFloat, paramBoolean1, paramBoolean3);
      return;
    }
    setOverScrolledPixels(paramFloat / getRubberBandFactor(paramBoolean1), paramBoolean1);
    this.mAmbientState.setOverScrollAmount(paramFloat, paramBoolean1);
    if (paramBoolean1) {
      notifyOverscrollTopListener(paramFloat, paramBoolean3);
    }
    requestChildrenUpdate();
  }
  
  private void setOverScrolledPixels(float paramFloat, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mOverScrolledTopPixels = paramFloat;
      return;
    }
    this.mOverScrolledBottomPixels = paramFloat;
  }
  
  private void setStackTranslation(float paramFloat)
  {
    if (paramFloat != this.mStackTranslation)
    {
      this.mStackTranslation = paramFloat;
      this.mAmbientState.setStackTranslation(paramFloat);
      requestChildrenUpdate();
    }
  }
  
  private void setSwipingInProgress(boolean paramBoolean)
  {
    this.mSwipingInProgress = paramBoolean;
    if (paramBoolean) {
      requestDisallowInterceptTouchEvent(true);
    }
  }
  
  private void setTopPadding(int paramInt, boolean paramBoolean)
  {
    if (this.mTopPadding != paramInt)
    {
      this.mTopPadding = paramInt;
      updateAlgorithmHeightAndPadding();
      updateContentHeight();
      if ((paramBoolean) && (this.mAnimationsEnabled) && (this.mIsExpanded))
      {
        this.mTopPaddingNeedsAnimation = true;
        this.mNeedsAnimation = true;
      }
      requestChildrenUpdate();
      notifyHeightChangeListener(null);
    }
  }
  
  private boolean shouldHunAppearFromBottom(StackViewState paramStackViewState)
  {
    return paramStackViewState.yTranslation + paramStackViewState.height >= this.mAmbientState.getMaxHeadsUpTranslation();
  }
  
  private boolean shouldOverScrollFling(int paramInt)
  {
    boolean bool = true;
    float f = getCurrentOverScrollAmount(true);
    if ((!this.mScrolledToTopOnFirstDown) || (this.mExpandedInThisMotion)) {
      bool = false;
    }
    do
    {
      return bool;
      if (f <= this.mMinTopOverScrollToEscape) {
        break;
      }
    } while (paramInt > 0);
    return false;
  }
  
  private void springBack()
  {
    int k = getScrollRange();
    int i;
    int j;
    label26:
    boolean bool;
    float f;
    if (this.mOwnScrollY <= 0)
    {
      i = 1;
      if (this.mOwnScrollY < k) {
        break label88;
      }
      j = 1;
      if ((i != 0) || (j != 0))
      {
        if (i == 0) {
          break label93;
        }
        bool = true;
        f = -this.mOwnScrollY;
        setOwnScrollY(0);
        this.mDontReportNextOverScroll = true;
      }
    }
    for (;;)
    {
      setOverScrollAmount(f, bool, false);
      setOverScrollAmount(0.0F, bool, true);
      this.mScroller.forceFinished(true);
      return;
      i = 0;
      break;
      label88:
      j = 0;
      break label26;
      label93:
      bool = false;
      f = this.mOwnScrollY - k;
      setOwnScrollY(k);
    }
  }
  
  private void startAnimationToState()
  {
    if (this.mNeedsAnimation)
    {
      generateChildHierarchyEvents();
      this.mNeedsAnimation = false;
    }
    if ((!this.mAnimationEvents.isEmpty()) || (isCurrentlyAnimating()))
    {
      setAnimationRunning(true);
      this.mStateAnimator.startAnimationForEvents(this.mAnimationEvents, this.mCurrentStackScrollState, this.mGoToFullShadeDelay);
      this.mAnimationEvents.clear();
      updateBackground();
      updateViewShadows();
    }
    for (;;)
    {
      this.mGoToFullShadeDelay = 0L;
      return;
      applyCurrentState();
    }
  }
  
  private void startBackgroundAnimation()
  {
    this.mCurrentBounds.left = this.mBackgroundBounds.left;
    this.mCurrentBounds.right = this.mBackgroundBounds.right;
    startBottomAnimation();
    startTopAnimation();
  }
  
  private void startBackgroundFadeIn()
  {
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this, BACKGROUND_FADE, new float[] { 0.0F, 1.0F });
    if ((this.mDarkAnimationOriginIndex == -1) || (this.mDarkAnimationOriginIndex == -2)) {}
    for (int i = getNotGoneChildCount() - 1;; i = Math.max(this.mDarkAnimationOriginIndex, getNotGoneChildCount() - this.mDarkAnimationOriginIndex - 1))
    {
      localObjectAnimator.setStartDelay(Math.max(0, i) * 24);
      localObjectAnimator.setDuration(360L);
      localObjectAnimator.setInterpolator(Interpolators.ALPHA_IN);
      localObjectAnimator.start();
      return;
    }
  }
  
  private void startBottomAnimation()
  {
    int i = this.mStartAnimationRect.bottom;
    int j = this.mEndAnimationRect.bottom;
    int k = this.mBackgroundBounds.bottom;
    ObjectAnimator localObjectAnimator = this.mBottomAnimator;
    if ((localObjectAnimator != null) && (j == k)) {
      return;
    }
    if (!this.mAnimateNextBackgroundBottom)
    {
      if (localObjectAnimator != null)
      {
        localObjectAnimator.getValues()[0].setIntValues(new int[] { i, k });
        this.mStartAnimationRect.bottom = i;
        this.mEndAnimationRect.bottom = k;
        localObjectAnimator.setCurrentPlayTime(localObjectAnimator.getCurrentPlayTime());
        return;
      }
      setBackgroundBottom(k);
      return;
    }
    if (localObjectAnimator != null) {
      localObjectAnimator.cancel();
    }
    localObjectAnimator = ObjectAnimator.ofInt(this, "backgroundBottom", new int[] { this.mCurrentBounds.bottom, k });
    localObjectAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    localObjectAnimator.setDuration(360L);
    localObjectAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        NotificationStackScrollLayout.-get14(NotificationStackScrollLayout.this).bottom = -1;
        NotificationStackScrollLayout.-get5(NotificationStackScrollLayout.this).bottom = -1;
        NotificationStackScrollLayout.-set0(NotificationStackScrollLayout.this, null);
      }
    });
    localObjectAnimator.start();
    this.mStartAnimationRect.bottom = this.mCurrentBounds.bottom;
    this.mEndAnimationRect.bottom = k;
    this.mBottomAnimator = localObjectAnimator;
  }
  
  private void startTopAnimation()
  {
    int j = this.mEndAnimationRect.top;
    int i = this.mBackgroundBounds.top;
    ObjectAnimator localObjectAnimator = this.mTopAnimator;
    if ((localObjectAnimator != null) && (j == i)) {
      return;
    }
    if (!this.mAnimateNextBackgroundTop)
    {
      if (localObjectAnimator != null)
      {
        j = this.mStartAnimationRect.top;
        localObjectAnimator.getValues()[0].setIntValues(new int[] { j, i });
        this.mStartAnimationRect.top = j;
        this.mEndAnimationRect.top = i;
        localObjectAnimator.setCurrentPlayTime(localObjectAnimator.getCurrentPlayTime());
        return;
      }
      setBackgroundTop(i);
      return;
    }
    if (localObjectAnimator != null) {
      localObjectAnimator.cancel();
    }
    localObjectAnimator = ObjectAnimator.ofInt(this, "backgroundTop", new int[] { this.mCurrentBounds.top, i });
    localObjectAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    localObjectAnimator.setDuration(360L);
    localObjectAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        NotificationStackScrollLayout.-get14(NotificationStackScrollLayout.this).top = -1;
        NotificationStackScrollLayout.-get5(NotificationStackScrollLayout.this).top = -1;
        NotificationStackScrollLayout.-set7(NotificationStackScrollLayout.this, null);
      }
    });
    localObjectAnimator.start();
    this.mStartAnimationRect.top = this.mCurrentBounds.top;
    this.mEndAnimationRect.top = i;
    this.mTopAnimator = localObjectAnimator;
  }
  
  private int targetScrollForView(ExpandableView paramExpandableView, int paramInt)
  {
    return paramExpandableView.getIntrinsicHeight() + paramInt + getImeInset() - getHeight() + getTopPadding();
  }
  
  private void updateAlgorithmHeightAndPadding()
  {
    this.mAmbientState.setLayoutHeight(getLayoutHeight());
    updateAlgorithmLayoutMinHeight();
    this.mAmbientState.setTopPadding(this.mTopPadding);
  }
  
  private void updateAlgorithmLayoutMinHeight()
  {
    AmbientState localAmbientState = this.mAmbientState;
    if ((!this.mQsExpanded) || (onKeyguard())) {}
    for (int i = 0;; i = getLayoutMinHeight())
    {
      localAmbientState.setLayoutMinHeight(i);
      return;
    }
  }
  
  private void updateAnimationState(View paramView)
  {
    boolean bool;
    if ((this.mAnimationsEnabled) || (this.mPulsing)) {
      if (!this.mIsExpanded) {
        bool = isPinnedHeadsUp(paramView);
      }
    }
    for (;;)
    {
      updateAnimationState(bool, paramView);
      return;
      bool = true;
      continue;
      bool = false;
    }
  }
  
  private void updateAnimationState(boolean paramBoolean, View paramView)
  {
    if ((paramView instanceof ExpandableNotificationRow)) {
      ((ExpandableNotificationRow)paramView).setIconAnimationRunning(paramBoolean);
    }
  }
  
  private void updateBackground()
  {
    if (this.mAmbientState.isDark()) {
      return;
    }
    updateBackgroundBounds();
    boolean bool;
    if (!this.mCurrentBounds.equals(this.mBackgroundBounds)) {
      if ((!this.mAnimateNextBackgroundTop) && (!this.mAnimateNextBackgroundBottom))
      {
        bool = areBoundsAnimating();
        if (!isExpanded())
        {
          abortBackgroundAnimators();
          bool = false;
        }
        if (!bool) {
          break label85;
        }
        startBackgroundAnimation();
      }
    }
    for (;;)
    {
      this.mAnimateNextBackgroundBottom = false;
      this.mAnimateNextBackgroundTop = false;
      return;
      bool = true;
      break;
      label85:
      this.mCurrentBounds.set(this.mBackgroundBounds);
      applyCurrentBackgroundBounds();
      continue;
      abortBackgroundAnimators();
    }
  }
  
  private void updateBackgroundBounds()
  {
    this.mBackgroundBounds.left = ((int)getX());
    this.mBackgroundBounds.right = ((int)(getX() + getWidth()));
    if (!this.mIsExpanded)
    {
      this.mBackgroundBounds.top = 0;
      this.mBackgroundBounds.bottom = 0;
      return;
    }
    ActivatableNotificationView localActivatableNotificationView = this.mFirstVisibleBackgroundChild;
    int i = 0;
    int j;
    if (localActivatableNotificationView != null)
    {
      i = (int)StackStateAnimator.getFinalTranslationY(localActivatableNotificationView);
      if ((!this.mAnimateNextBackgroundTop) && ((this.mTopAnimator != null) || (this.mCurrentBounds.top != i))) {}
    }
    else
    {
      localActivatableNotificationView = this.mLastVisibleBackgroundChild;
      if (localActivatableNotificationView == null) {
        break label271;
      }
      j = Math.min((int)StackStateAnimator.getFinalTranslationY(localActivatableNotificationView) + StackStateAnimator.getFinalActualHeight(localActivatableNotificationView), getHeight());
      if ((!this.mAnimateNextBackgroundBottom) && ((this.mBottomAnimator != null) || (this.mCurrentBounds.bottom != j))) {
        break label231;
      }
      label148:
      if (this.mPhoneStatusBar.getBarState() == 1) {
        break label281;
      }
    }
    label231:
    label271:
    label281:
    for (i = (int)Math.max(this.mTopPadding + this.mStackTranslation, i);; i = Math.max(0, i))
    {
      this.mBackgroundBounds.top = i;
      this.mBackgroundBounds.bottom = Math.min(getHeight(), Math.max(j, i));
      return;
      if ((this.mTopAnimator != null) && (this.mEndAnimationRect.top == i)) {
        break;
      }
      i = (int)localActivatableNotificationView.getTranslationY();
      break;
      if ((this.mBottomAnimator != null) && (this.mEndAnimationRect.bottom == j)) {
        break label148;
      }
      j = Math.min((int)(localActivatableNotificationView.getTranslationY() + localActivatableNotificationView.getActualHeight()), getHeight());
      break label148;
      i = this.mTopPadding;
      j = i;
      break label148;
    }
  }
  
  private void updateBackgroundDimming()
  {
    float f1 = (0.7F + (1.0F - this.mDimAmount) * 0.3F) * this.mBackgroundFadeAmount;
    int i = this.mScrimController.getScrimBehindColor();
    float f2 = 1.0F - f1;
    i = Color.argb((int)(255.0F * f1 + Color.alpha(i) * f2), (int)(this.mBackgroundFadeAmount * Color.red(this.mBgColor) + Color.red(i) * f2), (int)(this.mBackgroundFadeAmount * Color.green(this.mBgColor) + Color.green(i) * f2), (int)(this.mBackgroundFadeAmount * Color.blue(this.mBgColor) + Color.blue(i) * f2));
    this.mBackgroundPaint.setColor(i);
    invalidate();
  }
  
  private void updateChildren()
  {
    updateScrollStateForAddedChildren();
    this.mAmbientState.setScrollY(this.mOwnScrollY);
    this.mStackScrollAlgorithm.getStackScrollState(this.mAmbientState, this.mCurrentStackScrollState);
    if ((isCurrentlyAnimating()) || (this.mNeedsAnimation))
    {
      startAnimationToState();
      return;
    }
    applyCurrentState();
  }
  
  private void updateChronometerForChild(View paramView)
  {
    if ((paramView instanceof ExpandableNotificationRow)) {
      ((ExpandableNotificationRow)paramView).setChronometerRunning(this.mIsExpanded);
    }
  }
  
  private void updateChronometers()
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      updateChronometerForChild(getChildAt(i));
      i += 1;
    }
  }
  
  private void updateContentHeight()
  {
    int i = 0;
    float f1 = 0.0F;
    int j = 0;
    while (j < getChildCount())
    {
      ExpandableView localExpandableView = (ExpandableView)getChildAt(j);
      int k = i;
      float f2 = f1;
      if (localExpandableView.getVisibility() != 8)
      {
        f2 = localExpandableView.getIncreasedPaddingAmount();
        k = i;
        if (i != 0) {
          k = i + (int)NotificationUtils.interpolate(this.mPaddingBetweenElements, this.mIncreasedPaddingBetweenElements, Math.max(f1, f2));
        }
        k += localExpandableView.getIntrinsicHeight();
      }
      j += 1;
      i = k;
      f1 = f2;
    }
    this.mContentHeight = (this.mTopPadding + i);
    updateScrollability();
  }
  
  private void updateContinuousShadowDrawing()
  {
    boolean bool;
    if (!this.mAnimationRunning)
    {
      if (!this.mAmbientState.getDraggedViews().isEmpty()) {
        break label56;
      }
      bool = false;
      if (bool != this.mContinuousShadowUpdate)
      {
        if (!bool) {
          break label61;
        }
        getViewTreeObserver().addOnPreDrawListener(this.mShadowUpdater);
      }
    }
    for (;;)
    {
      this.mContinuousShadowUpdate = bool;
      return;
      bool = true;
      break;
      label56:
      bool = true;
      break;
      label61:
      getViewTreeObserver().removeOnPreDrawListener(this.mShadowUpdater);
    }
  }
  
  private void updateFadingState()
  {
    applyCurrentBackgroundBounds();
    updateSrcDrawing();
  }
  
  private void updateFirstAndLastBackgroundViews()
  {
    boolean bool2 = true;
    ActivatableNotificationView localActivatableNotificationView1 = getFirstChildWithBackground();
    ActivatableNotificationView localActivatableNotificationView2 = getLastChildWithBackground();
    boolean bool1;
    if ((this.mAnimationsEnabled) && (this.mIsExpanded)) {
      if (localActivatableNotificationView1 != this.mFirstVisibleBackgroundChild)
      {
        bool1 = true;
        this.mAnimateNextBackgroundTop = bool1;
        if (localActivatableNotificationView2 == this.mLastVisibleBackgroundChild) {
          break label75;
        }
        bool1 = bool2;
      }
    }
    label53:
    for (this.mAnimateNextBackgroundBottom = bool1;; this.mAnimateNextBackgroundBottom = false)
    {
      this.mFirstVisibleBackgroundChild = localActivatableNotificationView1;
      this.mLastVisibleBackgroundChild = localActivatableNotificationView2;
      return;
      bool1 = false;
      break;
      label75:
      bool1 = false;
      break label53;
      this.mAnimateNextBackgroundTop = false;
    }
  }
  
  private void updateForcedScroll()
  {
    if ((this.mForcedScroll == null) || ((this.mForcedScroll.hasFocus()) && (this.mForcedScroll.isAttachedToWindow()))) {}
    for (;;)
    {
      if (this.mForcedScroll != null)
      {
        ExpandableView localExpandableView = (ExpandableView)this.mForcedScroll;
        int i = getPositionInLinearLayout(localExpandableView);
        int k = targetScrollForView(localExpandableView, i);
        int j = localExpandableView.getIntrinsicHeight();
        k = Math.max(0, Math.min(k, getScrollRange()));
        if ((this.mOwnScrollY < k) || (i + j < this.mOwnScrollY)) {
          setOwnScrollY(k);
        }
      }
      return;
      this.mForcedScroll = null;
    }
  }
  
  private void updateForwardAndBackwardScrollability()
  {
    boolean bool1;
    boolean bool2;
    label36:
    int i;
    if ((this.mScrollable) && (this.mOwnScrollY < getScrollRange()))
    {
      bool1 = true;
      if ((!this.mScrollable) || (this.mOwnScrollY <= 0)) {
        break label81;
      }
      bool2 = true;
      if (bool1 != this.mForwardScrollable) {
        break label86;
      }
      if (bool2 == this.mBackwardScrollable) {
        break label91;
      }
      i = 1;
    }
    for (;;)
    {
      this.mForwardScrollable = bool1;
      this.mBackwardScrollable = bool2;
      if (i != 0) {
        sendAccessibilityEvent(2048);
      }
      return;
      bool1 = false;
      break;
      label81:
      bool2 = false;
      break label36;
      label86:
      i = 1;
      continue;
      label91:
      i = 0;
    }
  }
  
  private void updateHideSensitiveForChild(View paramView)
  {
    if ((paramView instanceof ExpandableView)) {
      ((ExpandableView)paramView).setHideSensitiveForIntrinsicHeight(this.mAmbientState.isHideSensitive());
    }
  }
  
  private void updateNotificationAnimationStates()
  {
    boolean bool1;
    int i;
    label19:
    View localView;
    if (!this.mAnimationsEnabled)
    {
      bool1 = this.mPulsing;
      int j = getChildCount();
      i = 0;
      if (i >= j) {
        return;
      }
      localView = getChildAt(i);
      if (this.mIsExpanded) {
        break label69;
      }
    }
    label69:
    for (boolean bool2 = isPinnedHeadsUp(localView);; bool2 = true)
    {
      bool1 &= bool2;
      updateAnimationState(bool1, localView);
      i += 1;
      break label19;
      bool1 = true;
      break;
    }
  }
  
  private void updateScrollPositionOnExpandInBottom(ExpandableView paramExpandableView)
  {
    if ((paramExpandableView instanceof ExpandableNotificationRow))
    {
      paramExpandableView = (ExpandableNotificationRow)paramExpandableView;
      if ((paramExpandableView.isUserLocked()) && (paramExpandableView != getFirstChildNotGone()))
      {
        if (paramExpandableView.isSummaryWithChildren()) {
          return;
        }
        float f2 = paramExpandableView.getTranslationY() + paramExpandableView.getActualHeight();
        float f1 = f2;
        if (paramExpandableView.isChildInGroup()) {
          f1 = f2 + paramExpandableView.getNotificationParent().getTranslationY();
        }
        int i = getStackEndPosition();
        if (f1 > i)
        {
          setOwnScrollY((int)(this.mOwnScrollY + f1 - i));
          this.mDisallowScrollingInThisMotion = true;
        }
      }
    }
  }
  
  private void updateScrollStateForAddedChildren()
  {
    if (this.mChildrenToAddAnimated.isEmpty()) {
      return;
    }
    int i = 0;
    if (i < getChildCount())
    {
      ExpandableView localExpandableView = (ExpandableView)getChildAt(i);
      int k;
      if (this.mChildrenToAddAnimated.contains(localExpandableView))
      {
        k = getPositionInLinearLayout(localExpandableView);
        if (localExpandableView.getIncreasedPaddingAmount() != 1.0F) {
          break label101;
        }
      }
      label101:
      for (int j = this.mIncreasedPaddingBetweenElements;; j = this.mPaddingBetweenElements)
      {
        int m = getIntrinsicHeight(localExpandableView);
        if (k < this.mOwnScrollY) {
          setOwnScrollY(this.mOwnScrollY + (m + j));
        }
        i += 1;
        break;
      }
    }
    clampScrollPosition();
  }
  
  private void updateScrollStateForRemovedChild(ExpandableView paramExpandableView)
  {
    int i = getPositionInLinearLayout(paramExpandableView);
    int j = (int)NotificationUtils.interpolate(this.mPaddingBetweenElements, this.mIncreasedPaddingBetweenElements, paramExpandableView.getIncreasedPaddingAmount());
    j = getIntrinsicHeight(paramExpandableView) + j;
    if (i + j <= this.mOwnScrollY) {
      setOwnScrollY(this.mOwnScrollY - j);
    }
    while (i >= this.mOwnScrollY) {
      return;
    }
    setOwnScrollY(i);
  }
  
  private void updateScrollability()
  {
    if (getScrollRange() > 0) {}
    for (boolean bool = true;; bool = false)
    {
      if (bool != this.mScrollable)
      {
        this.mScrollable = bool;
        setFocusable(bool);
        updateForwardAndBackwardScrollability();
      }
      return;
    }
  }
  
  private void updateSrcDrawing()
  {
    Paint localPaint = this.mBackgroundPaint;
    if ((!this.mDrawBackgroundAsSrc) || (this.mFadingOut) || (this.mParentFadingOut)) {}
    for (Object localObject = null;; localObject = this.mSrcMode)
    {
      localPaint.setXfermode((Xfermode)localObject);
      invalidate();
      return;
    }
  }
  
  private void updateViewShadows()
  {
    int i = 0;
    while (i < getChildCount())
    {
      localObject = (ExpandableView)getChildAt(i);
      if (((ExpandableView)localObject).getVisibility() != 8) {
        this.mTmpSortedChildren.add(localObject);
      }
      i += 1;
    }
    Collections.sort(this.mTmpSortedChildren, this.mViewPositionComparator);
    Object localObject = null;
    i = 0;
    if (i < this.mTmpSortedChildren.size())
    {
      ExpandableView localExpandableView = (ExpandableView)this.mTmpSortedChildren.get(i);
      float f2 = localExpandableView.getTranslationZ();
      float f1;
      if (localObject == null)
      {
        f1 = f2;
        label108:
        f1 -= f2;
        if ((f1 > 0.0F) && (f1 < 0.1F)) {
          break label157;
        }
        localExpandableView.setFakeShadowIntensity(0.0F, 0.0F, 0, 0);
      }
      for (;;)
      {
        localObject = localExpandableView;
        i += 1;
        break;
        f1 = ((ExpandableView)localObject).getTranslationZ();
        break label108;
        label157:
        f2 = ((ExpandableView)localObject).getTranslationY();
        float f3 = ((ExpandableView)localObject).getActualHeight();
        float f4 = localExpandableView.getTranslationY();
        float f5 = ((ExpandableView)localObject).getExtraBottomPadding();
        localExpandableView.setFakeShadowIntensity(f1 / 0.1F, ((ExpandableView)localObject).getOutlineAlpha(), (int)(f2 + f3 - f4 - f5), ((ExpandableView)localObject).getOutlineTranslation());
      }
    }
    this.mTmpSortedChildren.clear();
  }
  
  public boolean canChildBeDismissed(View paramView)
  {
    return StackScrollAlgorithm.canChildBeDismissed(paramView);
  }
  
  public boolean canChildBeExpanded(View paramView)
  {
    if ((!(paramView instanceof ExpandableNotificationRow)) || (!((ExpandableNotificationRow)paramView).isExpandable()) || (((ExpandableNotificationRow)paramView).areGutsExposed())) {
      return false;
    }
    return (this.mIsExpanded) || (!((ExpandableNotificationRow)paramView).isPinned());
  }
  
  public void cancelExpandHelper()
  {
    this.mExpandHelper.cancel();
  }
  
  public void cancelOverScroll(boolean paramBoolean)
  {
    this.mStateAnimator.cancelOverScrollAnimators(paramBoolean);
    setOverScrollAmount(0.0F, paramBoolean, false, false, false);
  }
  
  public void changeViewPosition(View paramView, int paramInt)
  {
    int i = indexOfChild(paramView);
    if ((paramView != null) && (paramView.getParent() == this) && (i != paramInt))
    {
      this.mChangePositionInProgress = true;
      ((ExpandableView)paramView).setChangingPosition(true);
      removeView(paramView);
      addView(paramView, paramInt);
      ((ExpandableView)paramView).setChangingPosition(false);
      this.mChangePositionInProgress = false;
      if ((this.mIsExpanded) && (this.mAnimationsEnabled) && (paramView.getVisibility() != 8))
      {
        this.mChildrenChangingPositions.add(paramView);
        this.mNeedsAnimation = true;
      }
    }
  }
  
  public void clearChildFocus(View paramView)
  {
    super.clearChildFocus(paramView);
    if (this.mForcedScroll == paramView) {
      this.mForcedScroll = null;
    }
  }
  
  public void closeControlsIfOutsideTouch(MotionEvent paramMotionEvent)
  {
    this.mSwipeHelper.closeControlsIfOutsideTouch(paramMotionEvent);
  }
  
  public void computeScroll()
  {
    int k;
    int m;
    int n;
    int i1;
    int j;
    if (this.mScroller.computeScrollOffset())
    {
      k = this.mScrollX;
      m = this.mOwnScrollY;
      n = this.mScroller.getCurrX();
      i1 = this.mScroller.getCurrY();
      if ((k != n) || (m != i1))
      {
        j = getScrollRange();
        if ((i1 >= 0) || (m < 0)) {
          break label170;
        }
        float f = this.mScroller.getCurrVelocity();
        if (f >= this.mMinimumVelocity) {
          this.mMaxOverScroll = (Math.abs(f) / 1000.0F * this.mOverflingDistance);
        }
      }
    }
    label170:
    do
    {
      do
      {
        int i = j;
        if (this.mDontClampNextScroll) {
          i = Math.max(j, m);
        }
        overScrollBy(n - k, i1 - m, k, m, 0, i, 0, (int)this.mMaxOverScroll, false);
        onScrollChanged(this.mScrollX, this.mOwnScrollY, k, m);
        postInvalidateOnAnimation();
        return;
      } while ((i1 <= j) || (m > j));
      break;
      this.mDontClampNextScroll = false;
    } while (this.mFinishScrollingCallback == null);
    this.mFinishScrollingCallback.run();
  }
  
  public void dismissViewAnimated(View paramView, Runnable paramRunnable, int paramInt, long paramLong)
  {
    this.mSwipeHelper.dismissChild(paramView, 0.0F, paramRunnable, paramInt, true, paramLong, true);
  }
  
  public void expansionStateChanged(boolean paramBoolean)
  {
    this.mExpandingNotification = paramBoolean;
    if (!this.mExpandedInThisMotion)
    {
      this.mMaxScrollAfterExpand = this.mOwnScrollY;
      this.mExpandedInThisMotion = true;
    }
  }
  
  public void forceNoOverlappingRendering(boolean paramBoolean)
  {
    this.mForceNoOverlappingRendering = paramBoolean;
  }
  
  public void generateAddAnimation(View paramView, boolean paramBoolean)
  {
    if ((!this.mIsExpanded) || (!this.mAnimationsEnabled) || (this.mChangePositionInProgress)) {}
    while ((!isHeadsUp(paramView)) || (this.mChangePositionInProgress))
    {
      return;
      this.mChildrenToAddAnimated.add(paramView);
      if (paramBoolean) {
        this.mFromMoreCardAdditions.add(paramView);
      }
      this.mNeedsAnimation = true;
    }
    this.mAddedHeadsUpChildren.add(paramView);
    this.mChildrenToAddAnimated.remove(paramView);
  }
  
  public void generateChildOrderChangedEvent()
  {
    if ((this.mIsExpanded) && (this.mAnimationsEnabled))
    {
      this.mGenerateChildOrderChangedEvent = true;
      this.mNeedsAnimation = true;
      requestChildrenUpdate();
    }
  }
  
  public void generateHeadsUpAnimation(ExpandableNotificationRow paramExpandableNotificationRow, boolean paramBoolean)
  {
    if (this.mAnimationsEnabled)
    {
      this.mHeadsUpChangeAnimations.add(new Pair(paramExpandableNotificationRow, Boolean.valueOf(paramBoolean)));
      this.mNeedsAnimation = true;
      if ((!this.mIsExpanded) && (!paramBoolean)) {
        break label48;
      }
    }
    for (;;)
    {
      requestChildrenUpdate();
      return;
      label48:
      paramExpandableNotificationRow.setHeadsupDisappearRunning(true);
    }
  }
  
  public ActivatableNotificationView getActivatedChild()
  {
    return this.mAmbientState.getActivatedChild();
  }
  
  public float getAppearFraction(float paramFloat)
  {
    float f1 = getAppearEndPosition();
    float f2 = getAppearStartPosition();
    return (paramFloat - f2) / (f1 - f2);
  }
  
  public float getBackgroundFadeAmount()
  {
    return this.mBackgroundFadeAmount;
  }
  
  public float getBottomMostNotificationBottom()
  {
    int j = getChildCount();
    float f1 = 0.0F;
    int i = 0;
    if (i < j)
    {
      ExpandableView localExpandableView = (ExpandableView)getChildAt(i);
      float f2;
      if (localExpandableView.getVisibility() == 8) {
        f2 = f1;
      }
      for (;;)
      {
        i += 1;
        f1 = f2;
        break;
        float f3 = localExpandableView.getTranslationY() + localExpandableView.getActualHeight();
        f2 = f1;
        if (f3 > f1) {
          f2 = f3;
        }
      }
    }
    return getStackTranslation() + f1;
  }
  
  public int getBottomStackPeekSize()
  {
    return this.mBottomStackPeekSize;
  }
  
  public int getBottomStackSlowDownHeight()
  {
    return this.mBottomStackSlowDownHeight;
  }
  
  public View getChildAtPosition(MotionEvent paramMotionEvent)
  {
    ExpandableView localExpandableView = getChildAtPosition(paramMotionEvent.getX(), paramMotionEvent.getY());
    paramMotionEvent = localExpandableView;
    ExpandableNotificationRow localExpandableNotificationRow;
    if ((localExpandableView instanceof ExpandableNotificationRow))
    {
      localExpandableNotificationRow = ((ExpandableNotificationRow)localExpandableView).getNotificationParent();
      paramMotionEvent = localExpandableView;
      if (localExpandableNotificationRow != null)
      {
        paramMotionEvent = localExpandableView;
        if (localExpandableNotificationRow.areChildrenExpanded()) {
          if ((!localExpandableNotificationRow.areGutsExposed()) && (this.mGearExposedView != localExpandableNotificationRow)) {
            break label64;
          }
        }
      }
    }
    for (;;)
    {
      paramMotionEvent = localExpandableNotificationRow;
      label64:
      do
      {
        do
        {
          return paramMotionEvent;
          paramMotionEvent = localExpandableView;
        } while (localExpandableNotificationRow.getNotificationChildren().size() != 1);
        paramMotionEvent = localExpandableView;
      } while (!localExpandableNotificationRow.isClearable());
    }
  }
  
  public ExpandableView getChildAtPosition(float paramFloat1, float paramFloat2)
  {
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      Object localObject = (ExpandableView)getChildAt(i);
      if ((((ExpandableView)localObject).getVisibility() == 8) || ((localObject instanceof StackScrollerDecorView))) {}
      float f1;
      do
      {
        float f2;
        float f3;
        int k;
        do
        {
          i += 1;
          break;
          f1 = ((ExpandableView)localObject).getTranslationY();
          f2 = ((ExpandableView)localObject).getClipTopAmount();
          f3 = ((ExpandableView)localObject).getActualHeight();
          k = getWidth();
        } while ((paramFloat2 < f1 + f2) || (paramFloat2 > f1 + f3) || (paramFloat1 < 0.0F) || (paramFloat1 > k));
        if (!(localObject instanceof ExpandableNotificationRow)) {
          break label207;
        }
        localObject = (ExpandableNotificationRow)localObject;
      } while ((!this.mIsExpanded) && (((ExpandableNotificationRow)localObject).isHeadsUp()) && (((ExpandableNotificationRow)localObject).isPinned()) && (this.mHeadsUpManager.getTopEntry().entry.row != localObject) && (this.mGroupManager.getGroupSummary(this.mHeadsUpManager.getTopEntry().entry.row.getStatusBarNotification()) != localObject));
      return ((ExpandableNotificationRow)localObject).getViewAtPosition(paramFloat2 - f1);
      label207:
      return (ExpandableView)localObject;
    }
    return null;
  }
  
  public ExpandableView getChildAtRawPosition(float paramFloat1, float paramFloat2)
  {
    getLocationOnScreen(this.mTempInt2);
    return getChildAtPosition(paramFloat1 - this.mTempInt2[0], paramFloat2 - this.mTempInt2[1]);
  }
  
  public int getChildLocation(View paramView)
  {
    paramView = this.mCurrentStackScrollState.getViewStateForView(paramView);
    if (paramView == null) {
      return 0;
    }
    if (paramView.gone) {
      return 64;
    }
    return paramView.location;
  }
  
  public ExpandableView getClosestChildAtRawPosition(float paramFloat1, float paramFloat2)
  {
    getLocationOnScreen(this.mTempInt2);
    float f2 = paramFloat2 - this.mTempInt2[1];
    Object localObject1 = null;
    paramFloat1 = Float.MAX_VALUE;
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      ExpandableView localExpandableView = (ExpandableView)getChildAt(i);
      Object localObject2 = localObject1;
      paramFloat2 = paramFloat1;
      if (localExpandableView.getVisibility() != 8)
      {
        if (!(localExpandableView instanceof StackScrollerDecorView)) {
          break label98;
        }
        paramFloat2 = paramFloat1;
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        paramFloat1 = paramFloat2;
        break;
        label98:
        paramFloat2 = localExpandableView.getTranslationY();
        float f1 = localExpandableView.getClipTopAmount();
        float f3 = localExpandableView.getActualHeight();
        f1 = Math.min(Math.abs(paramFloat2 + f1 - f2), Math.abs(paramFloat2 + f3 - f2));
        localObject2 = localObject1;
        paramFloat2 = paramFloat1;
        if (f1 < paramFloat1)
        {
          localObject2 = localExpandableView;
          paramFloat2 = f1;
        }
      }
    }
    return (ExpandableView)localObject1;
  }
  
  public int getContentHeight()
  {
    return this.mContentHeight;
  }
  
  public float getCurrentOverScrollAmount(boolean paramBoolean)
  {
    return this.mAmbientState.getOverScrollAmount(paramBoolean);
  }
  
  public float getCurrentOverScrolledPixels(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.mOverScrolledTopPixels;
    }
    return this.mOverScrolledBottomPixels;
  }
  
  public int getDismissViewHeight()
  {
    return this.mDismissView.getHeight() + this.mPaddingBetweenElements;
  }
  
  public int getEmptyBottomMargin()
  {
    return Math.max(this.mMaxLayoutHeight - this.mContentHeight - this.mBottomStackPeekSize - this.mBottomStackSlowDownHeight, 0);
  }
  
  public int getEmptyShadeViewHeight()
  {
    return this.mEmptyShadeView.getHeight();
  }
  
  public float getFalsingThresholdFactor()
  {
    if (this.mPhoneStatusBar.isWakeUpComingFromTouch()) {
      return 1.5F;
    }
    return 1.0F;
  }
  
  public int getFirstChildIntrinsicHeight()
  {
    ExpandableView localExpandableView = getFirstChildNotGone();
    int i;
    if (localExpandableView != null) {
      i = localExpandableView.getIntrinsicHeight();
    }
    for (;;)
    {
      int j = i;
      if (this.mOwnScrollY > 0) {
        j = Math.max(i - this.mOwnScrollY, this.mCollapsedSize);
      }
      return j;
      if (this.mEmptyShadeView != null) {
        i = this.mEmptyShadeView.getIntrinsicHeight();
      } else {
        i = this.mCollapsedSize;
      }
    }
  }
  
  public ExpandableView getFirstChildNotGone()
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() != 8) {
        return (ExpandableView)localView;
      }
      i += 1;
    }
    return null;
  }
  
  public int getFirstItemMinHeight()
  {
    ExpandableView localExpandableView = getFirstChildNotGone();
    if (localExpandableView != null) {
      return localExpandableView.getMinHeight();
    }
    return this.mCollapsedSize;
  }
  
  public View getHostView()
  {
    return this;
  }
  
  public int getIntrinsicPadding()
  {
    return this.mIntrinsicPadding;
  }
  
  public View getLastChildNotGone()
  {
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() != 8) {
        return localView;
      }
      i -= 1;
    }
    return null;
  }
  
  public int getLayoutMinHeight()
  {
    int i = getFirstChildIntrinsicHeight();
    return Math.min(Math.min(this.mBottomStackPeekSize + i + this.mBottomStackSlowDownHeight, this.mMaxLayoutHeight - this.mIntrinsicPadding), getHeight() - this.mPaddingBottom - this.mTopPadding - this.mPaddingTop - this.mBottomStackPeekSize - this.mBottomStackSlowDownHeight);
  }
  
  public int getMaxExpandHeight(ExpandableView paramExpandableView)
  {
    int i = paramExpandableView.getMaxContentHeight();
    if ((paramExpandableView.isSummaryWithChildren()) && (paramExpandableView.getParent() == this))
    {
      this.mGroupExpandedForMeasure = true;
      ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)paramExpandableView;
      this.mGroupManager.toggleGroupExpansion(localExpandableNotificationRow.getStatusBarNotification());
      localExpandableNotificationRow.setForceUnlocked(true);
      this.mAmbientState.setLayoutHeight(this.mMaxLayoutHeight);
      this.mStackScrollAlgorithm.getStackScrollState(this.mAmbientState, this.mCurrentStackScrollState);
      this.mAmbientState.setLayoutHeight(getLayoutHeight());
      this.mGroupManager.toggleGroupExpansion(localExpandableNotificationRow.getStatusBarNotification());
      this.mGroupExpandedForMeasure = false;
      localExpandableNotificationRow.setForceUnlocked(false);
      paramExpandableView = this.mCurrentStackScrollState.getViewStateForView(paramExpandableView);
      if (paramExpandableView != null) {
        return Math.min(paramExpandableView.height, i);
      }
    }
    return i;
  }
  
  public int getNotGoneChildCount()
  {
    int m = getChildCount();
    int j = 0;
    int i = 0;
    if (i < m)
    {
      ExpandableView localExpandableView = (ExpandableView)getChildAt(i);
      int k = j;
      if (localExpandableView.getVisibility() != 8) {
        if (!localExpandableView.willBeGone()) {
          break label57;
        }
      }
      label57:
      for (k = j;; k = j + 1)
      {
        i += 1;
        j = k;
        break;
      }
    }
    return j;
  }
  
  public float getNotificationsTopY()
  {
    return this.mTopPadding + getStackTranslation();
  }
  
  public int getPeekHeight()
  {
    ExpandableView localExpandableView = getFirstChildNotGone();
    if (localExpandableView != null) {}
    for (int i = localExpandableView.getCollapsedHeight();; i = this.mCollapsedSize) {
      return this.mIntrinsicPadding + i + this.mBottomStackPeekSize + this.mBottomStackSlowDownHeight;
    }
  }
  
  public float getStackTranslation()
  {
    return this.mStackTranslation;
  }
  
  public int getTopPadding()
  {
    return this.mTopPadding;
  }
  
  public float getTopPaddingOverflow()
  {
    return this.mTopPaddingOverflow;
  }
  
  public void goToFullShade(long paramLong)
  {
    this.mDismissView.setInvisible();
    this.mEmptyShadeView.setInvisible();
    this.mGoToFullShadeNeedsAnimation = true;
    this.mGoToFullShadeDelay = paramLong;
    this.mNeedsAnimation = true;
    requestChildrenUpdate();
  }
  
  public boolean hasOverlappingRendering()
  {
    if (!this.mForceNoOverlappingRendering) {
      return super.hasOverlappingRendering();
    }
    return false;
  }
  
  public boolean isAddOrRemoveAnimationPending()
  {
    boolean bool2 = true;
    if (this.mNeedsAnimation)
    {
      boolean bool1 = bool2;
      if (this.mChildrenToAddAnimated.isEmpty())
      {
        bool1 = bool2;
        if (this.mChildrenToRemoveAnimated.isEmpty()) {
          bool1 = false;
        }
      }
      return bool1;
    }
    return false;
  }
  
  public boolean isAntiFalsingNeeded()
  {
    return onKeyguard();
  }
  
  public boolean isBelowLastNotification(float paramFloat1, float paramFloat2)
  {
    int i = getChildCount() - 1;
    if (i >= 0)
    {
      ExpandableView localExpandableView = (ExpandableView)getChildAt(i);
      int j;
      if (localExpandableView.getVisibility() != 8)
      {
        float f = localExpandableView.getY();
        if (f > paramFloat2) {
          return false;
        }
        if (paramFloat2 <= localExpandableView.getActualHeight() + f) {
          break label109;
        }
        j = 1;
        label64:
        if (localExpandableView != this.mDismissView) {
          break label117;
        }
        if ((j == 0) && (!this.mDismissView.isOnEmptySpace(paramFloat1 - this.mDismissView.getX(), paramFloat2 - f))) {
          break label115;
        }
      }
      label109:
      label115:
      label117:
      do
      {
        i -= 1;
        break;
        j = 0;
        break label64;
        return false;
        if (localExpandableView == this.mEmptyShadeView) {
          return true;
        }
      } while (j != 0);
      return false;
    }
    return paramFloat2 > this.mTopPadding + this.mStackTranslation;
  }
  
  public boolean isDismissViewNotGone()
  {
    return (this.mDismissView.getVisibility() != 8) && (!this.mDismissView.willBeGone());
  }
  
  public boolean isDismissViewVisible()
  {
    return this.mDismissView.isVisible();
  }
  
  public boolean isExpanded()
  {
    return this.mIsExpanded;
  }
  
  public boolean isInContentBounds(float paramFloat)
  {
    return paramFloat < getHeight() - getEmptyBottomMargin();
  }
  
  public boolean isOverScrollRunning(boolean paramBoolean)
  {
    return this.mStateAnimator.isOverScrollAnimatorsRunning(paramBoolean);
  }
  
  public boolean isScrolledToBottom()
  {
    return this.mOwnScrollY >= getScrollRange();
  }
  
  public boolean isScrolledToTop()
  {
    boolean bool = false;
    if (this.mOwnScrollY == 0) {
      bool = true;
    }
    return bool;
  }
  
  public void lockScrollTo(View paramView)
  {
    if (this.mForcedScroll == paramView) {
      return;
    }
    this.mForcedScroll = paramView;
    scrollTo(paramView);
  }
  
  public void notifyGroupChildAdded(View paramView)
  {
    onViewAddedInternal(paramView);
  }
  
  public void notifyGroupChildRemoved(View paramView, ViewGroup paramViewGroup)
  {
    onViewRemovedInternal(paramView, paramViewGroup);
  }
  
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    this.mBottomInset = paramWindowInsets.getSystemWindowInsetBottom();
    int i = getScrollRange();
    if (this.mOwnScrollY > i)
    {
      removeCallbacks(this.mReclamp);
      postDelayed(this.mReclamp, 50L);
    }
    while (this.mForcedScroll == null) {
      return paramWindowInsets;
    }
    scrollTo(this.mForcedScroll);
    return paramWindowInsets;
  }
  
  public void onBeginDrag(View paramView)
  {
    this.mFalsingManager.onNotificatonStartDismissing();
    setSwipingInProgress(true);
    this.mAmbientState.onBeginDrag(paramView);
    updateContinuousShadowDrawing();
    if ((!this.mAnimationsEnabled) || ((!this.mIsExpanded) && (isPinnedHeadsUp(paramView)))) {}
    for (;;)
    {
      requestChildrenUpdate();
      return;
      this.mDragAnimPendingChildren.add(paramView);
      this.mNeedsAnimation = true;
    }
  }
  
  public void onChildAnimationFinished()
  {
    setAnimationRunning(false);
    requestChildrenUpdate();
    runAnimationFinishedRunnables();
    clearViewOverlays();
    clearHeadsUpDisappearRunning();
  }
  
  public void onChildDismissed(View paramView)
  {
    Object localObject = (ExpandableNotificationRow)paramView;
    if (!((ExpandableNotificationRow)localObject).isDismissed()) {
      handleChildDismissed(paramView);
    }
    localObject = ((ExpandableNotificationRow)localObject).getTransientContainer();
    if (localObject != null) {}
    try
    {
      ((ViewGroup)localObject).removeTransientView(paramView);
      return;
    }
    catch (IllegalArgumentException paramView) {}
  }
  
  public void onChildSnappedBack(View paramView, float paramFloat)
  {
    this.mAmbientState.onDragFinished(paramView);
    updateContinuousShadowDrawing();
    if (!this.mDragAnimPendingChildren.contains(paramView))
    {
      if (this.mAnimationsEnabled)
      {
        this.mSnappedBackChildren.add(paramView);
        this.mNeedsAnimation = true;
      }
      requestChildrenUpdate();
    }
    for (;;)
    {
      if ((this.mCurrIconRow != null) && (paramFloat == 0.0F))
      {
        this.mCurrIconRow.resetState();
        this.mCurrIconRow = null;
      }
      return;
      this.mDragAnimPendingChildren.remove(paramView);
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    float f = getResources().getDisplayMetrics().density;
    this.mSwipeHelper.setDensityScale(f);
    f = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();
    this.mSwipeHelper.setPagingTouchSlop(f);
    initView(getContext());
  }
  
  public void onDragCancelled(View paramView)
  {
    this.mFalsingManager.onNotificatonStopDismissing();
    setSwipingInProgress(false);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.mCurrentBounds.top < this.mCurrentBounds.bottom) {
      paramCanvas.drawRect(0.0F, this.mCurrentBounds.top, getWidth(), this.mCurrentBounds.bottom, this.mBackgroundPaint);
    }
  }
  
  public void onExpansionStarted()
  {
    this.mIsExpansionChanging = true;
  }
  
  public void onExpansionStopped()
  {
    this.mIsExpansionChanging = false;
    if (!this.mIsExpanded)
    {
      setOwnScrollY(0);
      this.mPhoneStatusBar.resetUserExpandedStates();
      clearTemporaryViews(this);
      int i = 0;
      while (i < getChildCount())
      {
        ExpandableView localExpandableView = (ExpandableView)getChildAt(i);
        if ((localExpandableView instanceof ExpandableNotificationRow)) {
          clearTemporaryViews(((ExpandableNotificationRow)localExpandableView).getChildrenContainer());
        }
        i += 1;
      }
    }
  }
  
  public void onGearTouched(ExpandableNotificationRow paramExpandableNotificationRow, int paramInt1, int paramInt2)
  {
    if (this.mLongPressListener != null)
    {
      MetricsLogger.action(this.mContext, 333, paramExpandableNotificationRow.getStatusBarNotification().getPackageName());
      com.android.systemui.util.MdmLogger.sTouchGear = true;
      this.mLongPressListener.onLongPress(paramExpandableNotificationRow, paramInt1, paramInt2);
    }
  }
  
  public void onGoToKeyguard()
  {
    requestAnimateEverything();
  }
  
  public void onGroupCreatedFromChildren(NotificationGroupManager.NotificationGroup paramNotificationGroup)
  {
    this.mPhoneStatusBar.requestNotificationUpdate();
  }
  
  public void onGroupExpansionChanged(final ExpandableNotificationRow paramExpandableNotificationRow, boolean paramBoolean)
  {
    boolean bool;
    if ((!this.mGroupExpandedForMeasure) && (this.mAnimationsEnabled)) {
      if (!this.mIsExpanded) {
        bool = paramExpandableNotificationRow.isPinned();
      }
    }
    for (;;)
    {
      if (bool)
      {
        this.mExpandedGroupView = paramExpandableNotificationRow;
        this.mNeedsAnimation = true;
      }
      paramExpandableNotificationRow.setChildrenExpanded(paramBoolean, bool);
      if (!this.mGroupExpandedForMeasure) {
        onHeightChanged(paramExpandableNotificationRow, false);
      }
      runAfterAnimationFinished(new Runnable()
      {
        public void run()
        {
          paramExpandableNotificationRow.onFinishedExpansionChange();
        }
      });
      return;
      bool = true;
      continue;
      bool = false;
    }
  }
  
  public void onGroupsChanged()
  {
    this.mPhoneStatusBar.requestNotificationUpdate();
  }
  
  public void onHeightChanged(ExpandableView paramExpandableView, boolean paramBoolean)
  {
    ExpandableNotificationRow localExpandableNotificationRow = null;
    updateContentHeight();
    updateScrollPositionOnExpandInBottom(paramExpandableView);
    clampScrollPosition();
    notifyHeightChangeListener(paramExpandableView);
    if ((paramExpandableView instanceof ExpandableNotificationRow)) {
      localExpandableNotificationRow = (ExpandableNotificationRow)paramExpandableView;
    }
    if ((localExpandableNotificationRow != null) && ((localExpandableNotificationRow == this.mFirstVisibleBackgroundChild) || (localExpandableNotificationRow.getNotificationParent() == this.mFirstVisibleBackgroundChild))) {
      updateAlgorithmLayoutMinHeight();
    }
    if (paramBoolean) {
      requestAnimationOnViewResize(localExpandableNotificationRow);
    }
    requestChildrenUpdate();
  }
  
  public void onInitializeAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    paramAccessibilityEvent.setScrollable(this.mScrollable);
    paramAccessibilityEvent.setScrollX(this.mScrollX);
    paramAccessibilityEvent.setScrollY(this.mOwnScrollY);
    paramAccessibilityEvent.setMaxScrollX(this.mScrollX);
    paramAccessibilityEvent.setMaxScrollY(getScrollRange());
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (this.mScrollable)
    {
      paramAccessibilityNodeInfo.setScrollable(true);
      if (this.mBackwardScrollable)
      {
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP);
      }
      if (this.mForwardScrollable)
      {
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN);
      }
    }
    paramAccessibilityNodeInfo.setClassName(ScrollView.class.getName());
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    initDownStates(paramMotionEvent);
    handleEmptySpaceClick(paramMotionEvent);
    boolean bool2 = false;
    boolean bool1 = bool2;
    boolean bool3;
    label53:
    boolean bool4;
    if (!this.mSwipingInProgress)
    {
      if (this.mOnlyScrollingInThisMotion) {
        bool1 = bool2;
      }
    }
    else
    {
      bool3 = false;
      bool2 = bool3;
      if (!this.mSwipingInProgress)
      {
        if (!this.mExpandingNotification) {
          break label109;
        }
        bool2 = bool3;
      }
      bool4 = false;
      bool3 = bool4;
      if (!this.mIsBeingDragged)
      {
        if (!this.mExpandingNotification) {
          break label118;
        }
        bool3 = bool4;
      }
    }
    for (;;)
    {
      if ((bool3) || (bool2) || (bool1)) {
        break label164;
      }
      return super.onInterceptTouchEvent(paramMotionEvent);
      bool1 = this.mExpandHelper.onInterceptTouchEvent(paramMotionEvent);
      break;
      label109:
      bool2 = onInterceptTouchEventScroll(paramMotionEvent);
      break label53;
      label118:
      bool3 = bool4;
      if (!this.mExpandedInThisMotion)
      {
        bool3 = bool4;
        if (!this.mOnlyScrollingInThisMotion)
        {
          bool3 = bool4;
          if (!this.mDisallowDismissInThisMotion) {
            bool3 = this.mSwipeHelper.onInterceptTouchEvent(paramMotionEvent);
          }
        }
      }
    }
    label164:
    return true;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    float f1 = getWidth() / 2.0F;
    paramInt1 = 0;
    while (paramInt1 < getChildCount())
    {
      View localView = getChildAt(paramInt1);
      float f2 = localView.getMeasuredWidth();
      float f3 = localView.getMeasuredHeight();
      localView.layout((int)(f1 - f2 / 2.0F), 0, (int)(f2 / 2.0F + f1), (int)f3);
      paramInt1 += 1;
    }
    setMaxLayoutHeight(getHeight());
    updateContentHeight();
    clampScrollPosition();
    requestChildrenUpdate();
    updateFirstAndLastBackgroundViews();
    updateAlgorithmLayoutMinHeight();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      measureChild(getChildAt(i), paramInt1, paramInt2);
      i += 1;
    }
  }
  
  protected void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!this.mScroller.isFinished())
    {
      int i = this.mScrollX;
      int j = this.mOwnScrollY;
      this.mScrollX = paramInt1;
      setOwnScrollY(paramInt2);
      if (paramBoolean2)
      {
        springBack();
        return;
      }
      onScrollChanged(this.mScrollX, this.mOwnScrollY, i, j);
      invalidateParentIfNeeded();
      updateChildren();
      float f = getCurrentOverScrollAmount(true);
      if (this.mOwnScrollY < 0)
      {
        notifyOverscrollTopListener(-this.mOwnScrollY, isRubberbanded(true));
        return;
      }
      notifyOverscrollTopListener(f, isRubberbanded(true));
      return;
    }
    customScrollTo(paramInt2);
    scrollTo(paramInt1, this.mScrollY);
  }
  
  public void onPanelTrackingStarted()
  {
    this.mPanelTracking = true;
  }
  
  public void onPanelTrackingStopped()
  {
    this.mPanelTracking = false;
  }
  
  public void onReset(ExpandableView paramExpandableView)
  {
    updateAnimationState(paramExpandableView);
    updateChronometerForChild(paramExpandableView);
  }
  
  public void onSettingsIconRowReset(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    if ((this.mTranslatingParentView != null) && (paramExpandableNotificationRow == this.mTranslatingParentView))
    {
      NotificationSwipeHelper.-wrap2(this.mSwipeHelper, false);
      this.mGearExposedView = null;
      this.mTranslatingParentView = null;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool5 = true;
    int i;
    boolean bool2;
    boolean bool1;
    label49:
    boolean bool3;
    label74:
    boolean bool4;
    if (paramMotionEvent.getActionMasked() != 3)
    {
      if (paramMotionEvent.getActionMasked() != 1) {
        break label140;
      }
      i = 1;
      handleEmptySpaceClick(paramMotionEvent);
      bool2 = false;
      bool1 = bool2;
      if (this.mIsExpanded)
      {
        if (!this.mSwipingInProgress) {
          break label145;
        }
        bool1 = bool2;
      }
      bool3 = false;
      bool2 = bool3;
      if (this.mIsExpanded)
      {
        if (!this.mSwipingInProgress) {
          break label232;
        }
        bool2 = bool3;
      }
      bool4 = false;
      bool3 = bool4;
      if (!this.mIsBeingDragged)
      {
        if (!this.mExpandingNotification) {
          break label264;
        }
        bool3 = bool4;
      }
    }
    for (;;)
    {
      bool4 = bool5;
      if (!bool3)
      {
        bool4 = bool5;
        if (!bool2)
        {
          bool4 = bool5;
          if (!bool1) {
            bool4 = super.onTouchEvent(paramMotionEvent);
          }
        }
      }
      return bool4;
      i = 1;
      break;
      label140:
      i = 0;
      break;
      label145:
      bool1 = bool2;
      if (this.mOnlyScrollingInThisMotion) {
        break label49;
      }
      if (i != 0) {
        this.mExpandHelper.onlyObserveMovements(false);
      }
      bool3 = this.mExpandingNotification;
      bool2 = this.mExpandHelper.onTouchEvent(paramMotionEvent);
      bool1 = bool2;
      if (!this.mExpandedInThisMotion) {
        break label49;
      }
      bool1 = bool2;
      if (this.mExpandingNotification) {
        break label49;
      }
      bool1 = bool2;
      if (!bool3) {
        break label49;
      }
      bool1 = bool2;
      if (this.mDisallowScrollingInThisMotion) {
        break label49;
      }
      dispatchDownEventToScroller(paramMotionEvent);
      bool1 = bool2;
      break label49;
      label232:
      bool2 = bool3;
      if (this.mExpandingNotification) {
        break label74;
      }
      bool2 = bool3;
      if (this.mDisallowScrollingInThisMotion) {
        break label74;
      }
      bool2 = onScrollTouch(paramMotionEvent);
      break label74;
      label264:
      bool3 = bool4;
      if (!this.mExpandedInThisMotion)
      {
        bool3 = bool4;
        if (!this.mOnlyScrollingInThisMotion)
        {
          bool3 = bool4;
          if (!this.mDisallowDismissInThisMotion) {
            bool3 = this.mSwipeHelper.onTouchEvent(paramMotionEvent);
          }
        }
      }
    }
  }
  
  public void onViewAdded(View paramView)
  {
    super.onViewAdded(paramView);
    onViewAddedInternal(paramView);
  }
  
  public void onViewRemoved(View paramView)
  {
    super.onViewRemoved(paramView);
    if (!this.mChildTransferInProgress) {
      onViewRemovedInternal(paramView, this);
    }
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    if (!paramBoolean) {
      removeLongPressCallback();
    }
  }
  
  protected boolean overScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
  {
    paramInt2 = paramInt4 + paramInt2;
    paramInt3 = -paramInt8;
    paramInt1 = paramInt8 + paramInt6;
    paramBoolean = false;
    if (paramInt2 > paramInt1) {
      paramBoolean = true;
    }
    for (;;)
    {
      onOverScrolled(0, paramInt1, false, paramBoolean);
      return paramBoolean;
      paramInt1 = paramInt2;
      if (paramInt2 < paramInt3)
      {
        paramInt1 = paramInt3;
        paramBoolean = true;
      }
    }
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityActionInternal(paramInt, paramBundle)) {
      return true;
    }
    if (!isEnabled()) {
      return false;
    }
    int i = -1;
    switch (paramInt)
    {
    }
    do
    {
      return false;
      i = 1;
      paramInt = getHeight();
      int j = this.mPaddingBottom;
      int k = this.mTopPadding;
      int m = this.mPaddingTop;
      int n = this.mBottomStackPeekSize;
      int i1 = this.mBottomStackSlowDownHeight;
      paramInt = Math.max(0, Math.min(this.mOwnScrollY + i * (paramInt - j - k - m - n - i1), getScrollRange()));
    } while (paramInt == this.mOwnScrollY);
    this.mScroller.startScroll(this.mScrollX, this.mOwnScrollY, 0, paramInt - this.mOwnScrollY);
    postInvalidateOnAnimation();
    return true;
  }
  
  public void removeLongPressCallback()
  {
    this.mSwipeHelper.removeLongPressCallback();
  }
  
  public void removeViewStateForView(View paramView)
  {
    this.mCurrentStackScrollState.removeViewStateForView(paramView);
  }
  
  public void requestDisallowDismiss()
  {
    this.mDisallowDismissInThisMotion = true;
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    super.requestDisallowInterceptTouchEvent(paramBoolean);
    if (paramBoolean) {
      this.mSwipeHelper.removeLongPressCallback();
    }
  }
  
  public void requestDisallowLongPress()
  {
    removeLongPressCallback();
  }
  
  public void resetExposedGearView(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mSwipeHelper.resetExposedGearView(paramBoolean1, paramBoolean2);
  }
  
  public void resetScrollPosition()
  {
    this.mScroller.abortAnimation();
    setOwnScrollY(0);
  }
  
  public void runAfterAnimationFinished(Runnable paramRunnable)
  {
    this.mAnimationFinishedRunnables.add(paramRunnable);
  }
  
  public boolean scrollTo(View paramView)
  {
    ExpandableView localExpandableView = (ExpandableView)paramView;
    int i = getPositionInLinearLayout(paramView);
    int j = targetScrollForView(localExpandableView, i);
    int k = localExpandableView.getIntrinsicHeight();
    if ((this.mOwnScrollY < j) || (i + k < this.mOwnScrollY))
    {
      this.mScroller.startScroll(this.mScrollX, this.mOwnScrollY, 0, j - this.mOwnScrollY);
      this.mDontReportNextOverScroll = true;
      postInvalidateOnAnimation();
      return true;
    }
    return false;
  }
  
  public void setActivatedChild(ActivatableNotificationView paramActivatableNotificationView)
  {
    this.mAmbientState.setActivatedChild(paramActivatableNotificationView);
    if (this.mAnimationsEnabled)
    {
      this.mActivateNeedsAnimation = true;
      this.mNeedsAnimation = true;
    }
    requestChildrenUpdate();
  }
  
  public void setAlpha(float paramFloat)
  {
    super.setAlpha(paramFloat);
    if (paramFloat != 1.0F) {}
    for (boolean bool = true;; bool = false)
    {
      setFadingOut(bool);
      return;
    }
  }
  
  public void setAnimationRunning(boolean paramBoolean)
  {
    if (paramBoolean != this.mAnimationRunning)
    {
      if (!paramBoolean) {
        break label33;
      }
      getViewTreeObserver().addOnPreDrawListener(this.mBackgroundUpdater);
    }
    for (;;)
    {
      this.mAnimationRunning = paramBoolean;
      updateContinuousShadowDrawing();
      return;
      label33:
      getViewTreeObserver().removeOnPreDrawListener(this.mBackgroundUpdater);
    }
  }
  
  public void setAnimationsEnabled(boolean paramBoolean)
  {
    this.mAnimationsEnabled = paramBoolean;
    updateNotificationAnimationStates();
  }
  
  public void setBackgroundBottom(int paramInt)
  {
    this.mCurrentBounds.bottom = paramInt;
    applyCurrentBackgroundBounds();
  }
  
  public void setChildLocationsChangedListener(OnChildLocationsChangedListener paramOnChildLocationsChangedListener)
  {
    this.mListener = paramOnChildLocationsChangedListener;
  }
  
  public void setChildTransferInProgress(boolean paramBoolean)
  {
    this.mChildTransferInProgress = paramBoolean;
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, PointF paramPointF)
  {
    this.mAmbientState.setDark(paramBoolean1);
    if ((paramBoolean2) && (this.mAnimationsEnabled))
    {
      this.mDarkNeedsAnimation = true;
      this.mDarkAnimationOriginIndex = findDarkAnimationOriginIndex(paramPointF);
      this.mNeedsAnimation = true;
      setBackgroundFadeAmount(0.0F);
    }
    for (;;)
    {
      requestChildrenUpdate();
      if (!paramBoolean1) {
        break;
      }
      setWillNotDraw(true);
      this.mScrimController.setExcludedBackgroundArea(null);
      return;
      if (!paramBoolean1) {
        setBackgroundFadeAmount(1.0F);
      }
    }
    updateBackground();
    setWillNotDraw(false);
  }
  
  public void setDimmed(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mAmbientState.setDimmed(paramBoolean1);
    if ((paramBoolean2) && (this.mAnimationsEnabled))
    {
      this.mDimmedNeedsAnimation = true;
      this.mNeedsAnimation = true;
      animateDimmed(paramBoolean1);
      requestChildrenUpdate();
      return;
    }
    if (paramBoolean1) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      setDimAmount(f);
      break;
    }
  }
  
  public void setDismissAllInProgress(boolean paramBoolean)
  {
    this.mDismissAllInProgress = paramBoolean;
    this.mAmbientState.setDismissAllInProgress(paramBoolean);
    handleDismissAllClipping();
  }
  
  public void setDismissView(DismissView paramDismissView)
  {
    int i = -1;
    if (this.mDismissView != null)
    {
      i = indexOfChild(this.mDismissView);
      removeView(this.mDismissView);
    }
    this.mDismissView = paramDismissView;
    addView(this.mDismissView, i);
  }
  
  public void setDrawBackgroundAsSrc(boolean paramBoolean)
  {
    this.mDrawBackgroundAsSrc = paramBoolean;
    updateSrcDrawing();
  }
  
  public void setEmptyShadeView(EmptyShadeView paramEmptyShadeView)
  {
    int i = -1;
    if (this.mEmptyShadeView != null)
    {
      i = indexOfChild(this.mEmptyShadeView);
      removeView(this.mEmptyShadeView);
    }
    this.mEmptyShadeView = paramEmptyShadeView;
    addView(this.mEmptyShadeView, i);
  }
  
  public void setExpandedHeight(float paramFloat)
  {
    this.mExpandedHeight = paramFloat;
    if (paramFloat > 0.0F) {}
    int i;
    for (boolean bool = true;; bool = false)
    {
      setIsExpanded(bool);
      f2 = getAppearEndPosition();
      f1 = getAppearStartPosition();
      if (paramFloat < f2) {
        break;
      }
      f1 = 0.0F;
      i = (int)paramFloat;
      if (i != this.mCurrentStackHeight)
      {
        this.mCurrentStackHeight = i;
        updateAlgorithmHeightAndPadding();
        requestChildrenUpdate();
      }
      setStackTranslation(f1);
      return;
    }
    float f2 = getAppearFraction(paramFloat);
    if (f2 >= 0.0F) {}
    for (float f1 = NotificationUtils.interpolate(getExpandTranslationStart(), 0.0F, f2);; f1 = paramFloat - f1 + getExpandTranslationStart())
    {
      i = (int)(paramFloat - f1);
      break;
    }
  }
  
  public void setExpandingEnabled(boolean paramBoolean)
  {
    this.mExpandHelper.setEnabled(paramBoolean);
  }
  
  public void setExpansionCancelled(View paramView)
  {
    if ((paramView instanceof ExpandableNotificationRow)) {
      ((ExpandableNotificationRow)paramView).setGroupExpansionChanging(false);
    }
  }
  
  public void setFadingOut(boolean paramBoolean)
  {
    if (paramBoolean != this.mFadingOut)
    {
      this.mFadingOut = paramBoolean;
      updateFadingState();
    }
  }
  
  public void setFinishScrollingCallback(Runnable paramRunnable)
  {
    this.mFinishScrollingCallback = paramRunnable;
  }
  
  public void setGroupManager(NotificationGroupManager paramNotificationGroupManager)
  {
    this.mGroupManager = paramNotificationGroupManager;
  }
  
  public void setHeadsUpBoundaries(int paramInt1, int paramInt2)
  {
    this.mAmbientState.setMaxHeadsUpTranslation(paramInt1 - paramInt2);
    this.mStateAnimator.setHeadsUpAppearHeightBottom(paramInt1);
    requestChildrenUpdate();
  }
  
  public void setHeadsUpManager(HeadsUpManager paramHeadsUpManager)
  {
    this.mHeadsUpManager = paramHeadsUpManager;
    this.mAmbientState.setHeadsUpManager(paramHeadsUpManager);
  }
  
  public void setHideSensitive(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 != this.mAmbientState.isHideSensitive())
    {
      int j = getChildCount();
      int i = 0;
      while (i < j)
      {
        ((ExpandableView)getChildAt(i)).setHideSensitiveForIntrinsicHeight(paramBoolean1);
        i += 1;
      }
      this.mAmbientState.setHideSensitive(paramBoolean1);
      if ((paramBoolean2) && (this.mAnimationsEnabled))
      {
        this.mHideSensitiveNeedsAnimation = true;
        this.mNeedsAnimation = true;
      }
      requestChildrenUpdate();
    }
  }
  
  public void setIntrinsicPadding(int paramInt)
  {
    this.mIntrinsicPadding = paramInt;
  }
  
  public void setLongPressListener(SwipeHelper.LongPressListener paramLongPressListener)
  {
    this.mSwipeHelper.setLongPressListener(paramLongPressListener);
    this.mLongPressListener = paramLongPressListener;
  }
  
  public void setOnEmptySpaceClickListener(OnEmptySpaceClickListener paramOnEmptySpaceClickListener)
  {
    this.mOnEmptySpaceClickListener = paramOnEmptySpaceClickListener;
  }
  
  public void setOnHeightChangedListener(ExpandableView.OnHeightChangedListener paramOnHeightChangedListener)
  {
    this.mOnHeightChangedListener = paramOnHeightChangedListener;
  }
  
  public void setOverScrollAmount(float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    setOverScrollAmount(paramFloat, paramBoolean1, paramBoolean2, true);
  }
  
  public void setOverScrollAmount(float paramFloat, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    setOverScrollAmount(paramFloat, paramBoolean1, paramBoolean2, paramBoolean3, isRubberbanded(paramBoolean1));
  }
  
  public void setOverScrollAmount(float paramFloat, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    if (paramBoolean3) {
      this.mStateAnimator.cancelOverScrollAnimators(paramBoolean1);
    }
    setOverScrollAmountInternal(paramFloat, paramBoolean1, paramBoolean2, paramBoolean4);
  }
  
  public void setOverScrolledPixels(float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    setOverScrollAmount(getRubberBandFactor(paramBoolean1) * paramFloat, paramBoolean1, paramBoolean2, true);
  }
  
  public void setOverflowContainer(NotificationOverflowContainer paramNotificationOverflowContainer)
  {
    int i = -1;
    if (this.mOverflowContainer != null)
    {
      i = indexOfChild(this.mOverflowContainer);
      removeView(this.mOverflowContainer);
    }
    this.mOverflowContainer = paramNotificationOverflowContainer;
    addView(this.mOverflowContainer, i);
  }
  
  public void setOverscrollTopChangedListener(OnOverscrollTopChangedListener paramOnOverscrollTopChangedListener)
  {
    this.mOverscrollTopChangedListener = paramOnOverscrollTopChangedListener;
  }
  
  public void setOwnScrollY(int paramInt)
  {
    if (paramInt != this.mOwnScrollY)
    {
      this.mOwnScrollY = paramInt;
      updateForwardAndBackwardScrollability();
    }
  }
  
  public void setParentFadingOut(boolean paramBoolean)
  {
    if (paramBoolean != this.mParentFadingOut)
    {
      this.mParentFadingOut = paramBoolean;
      updateFadingState();
    }
  }
  
  public void setPhoneStatusBar(PhoneStatusBar paramPhoneStatusBar)
  {
    this.mPhoneStatusBar = paramPhoneStatusBar;
  }
  
  public void setPulsing(boolean paramBoolean)
  {
    this.mPulsing = paramBoolean;
    updateNotificationAnimationStates();
  }
  
  public void setQsContainer(ViewGroup paramViewGroup)
  {
    this.mQsContainer = paramViewGroup;
  }
  
  public void setQsExpanded(boolean paramBoolean)
  {
    this.mQsExpanded = paramBoolean;
    updateAlgorithmLayoutMinHeight();
  }
  
  public void setScrimController(ScrimController paramScrimController)
  {
    this.mScrimController = paramScrimController;
    this.mScrimController.setScrimBehindChangeRunnable(new Runnable()
    {
      public void run()
      {
        NotificationStackScrollLayout.-wrap5(NotificationStackScrollLayout.this);
      }
    });
  }
  
  public void setScrollingEnabled(boolean paramBoolean)
  {
    this.mScrollingEnabled = paramBoolean;
  }
  
  public void setShadeExpanded(boolean paramBoolean)
  {
    this.mAmbientState.setShadeExpanded(paramBoolean);
    this.mStateAnimator.setShadeExpanded(paramBoolean);
  }
  
  public void setTrackingHeadsUp(boolean paramBoolean)
  {
    this.mTrackingHeadsUp = paramBoolean;
  }
  
  public void setUserExpandedChild(View paramView, boolean paramBoolean)
  {
    if ((paramView instanceof ExpandableNotificationRow))
    {
      paramView = (ExpandableNotificationRow)paramView;
      paramView.setUserExpanded(paramBoolean, true);
      paramView.onExpandedByGesture(paramBoolean);
    }
  }
  
  public void setUserLockedChild(View paramView, boolean paramBoolean)
  {
    if ((paramView instanceof ExpandableNotificationRow)) {
      ((ExpandableNotificationRow)paramView).setUserLocked(paramBoolean);
    }
    removeLongPressCallback();
    requestDisallowInterceptTouchEvent(true);
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return true;
  }
  
  public void snapViewIfNeeded(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    boolean bool;
    if (!this.mIsExpanded)
    {
      bool = isPinnedHeadsUp(paramExpandableNotificationRow);
      if (!paramExpandableNotificationRow.getSettingsRow().isVisible()) {
        break label43;
      }
    }
    label43:
    for (float f = paramExpandableNotificationRow.getTranslation();; f = 0.0F)
    {
      this.mSwipeHelper.snapChildIfNeeded(paramExpandableNotificationRow, bool, f);
      return;
      bool = true;
      break;
    }
  }
  
  public void updateEmptyShadeView(boolean paramBoolean)
  {
    int i;
    int j;
    if (this.mEmptyShadeView.willBeGone())
    {
      i = 8;
      if (!paramBoolean) {
        break label87;
      }
      j = 0;
      label19:
      if (i != j)
      {
        if (j == 8) {
          break label103;
        }
        if (!this.mEmptyShadeView.willBeGone()) {
          break label93;
        }
        this.mEmptyShadeView.cancelAnimation();
      }
    }
    for (;;)
    {
      this.mEmptyShadeView.setVisibility(j);
      this.mEmptyShadeView.setWillBeGone(false);
      updateContentHeight();
      notifyHeightChangeListener(this.mEmptyShadeView);
      return;
      i = this.mEmptyShadeView.getVisibility();
      break;
      label87:
      j = 8;
      break label19;
      label93:
      this.mEmptyShadeView.setInvisible();
    }
    label103:
    Runnable local11 = new Runnable()
    {
      public void run()
      {
        NotificationStackScrollLayout.-get4(NotificationStackScrollLayout.this).setVisibility(8);
        NotificationStackScrollLayout.-get4(NotificationStackScrollLayout.this).setWillBeGone(false);
        NotificationStackScrollLayout.-wrap8(NotificationStackScrollLayout.this);
        NotificationStackScrollLayout.-wrap2(NotificationStackScrollLayout.this, NotificationStackScrollLayout.-get4(NotificationStackScrollLayout.this));
      }
    };
    if ((this.mAnimationsEnabled) && (this.mIsExpanded))
    {
      this.mEmptyShadeView.setWillBeGone(true);
      this.mEmptyShadeView.performVisibilityAnimation(false, local11);
      return;
    }
    this.mEmptyShadeView.setInvisible();
    local11.run();
  }
  
  public void updateOverflowContainerVisibility(boolean paramBoolean)
  {
    int i;
    if (this.mOverflowContainer.willBeGone())
    {
      i = 8;
      if (!paramBoolean) {
        break label106;
      }
    }
    Runnable local12;
    label106:
    for (final int j = 0;; j = 8)
    {
      if (i != j)
      {
        local12 = new Runnable()
        {
          public void run()
          {
            NotificationStackScrollLayout.-get9(NotificationStackScrollLayout.this).setVisibility(j);
            NotificationStackScrollLayout.-get9(NotificationStackScrollLayout.this).setWillBeGone(false);
            NotificationStackScrollLayout.-wrap8(NotificationStackScrollLayout.this);
            NotificationStackScrollLayout.-wrap2(NotificationStackScrollLayout.this, NotificationStackScrollLayout.-get9(NotificationStackScrollLayout.this));
          }
        };
        if ((!this.mAnimationsEnabled) || (!this.mIsExpanded)) {
          break label112;
        }
        if (j == 8) {
          break label127;
        }
        this.mOverflowContainer.performAddAnimation(0L, 360L);
        this.mOverflowContainer.setVisibility(j);
        this.mOverflowContainer.setWillBeGone(false);
        updateContentHeight();
        notifyHeightChangeListener(this.mOverflowContainer);
      }
      return;
      i = this.mOverflowContainer.getVisibility();
      break;
    }
    label112:
    this.mOverflowContainer.cancelAppearDrawing();
    local12.run();
    return;
    label127:
    this.mOverflowContainer.performRemoveAnimation(360L, 0.0F, local12);
    this.mOverflowContainer.setWillBeGone(true);
  }
  
  public void updateSensitive(View paramView, boolean paramBoolean)
  {
    paramView = this.mCurrentStackScrollState.getViewStateForView(paramView);
    if (paramView != null) {
      paramView.hideSensitive = paramBoolean;
    }
  }
  
  public void updateSpeedBumpIndex(int paramInt)
  {
    this.mAmbientState.setSpeedBumpIndex(paramInt);
  }
  
  public boolean updateSwipeProgress(View paramView, boolean paramBoolean, float paramFloat)
  {
    if ((!this.mIsExpanded) && (isPinnedHeadsUp(paramView)) && (canChildBeDismissed(paramView))) {
      this.mScrimController.setTopHeadsUpDragAmount(paramView, Math.min(Math.abs(paramFloat / 2.0F - 1.0F), 1.0F));
    }
    return true;
  }
  
  public void updateTopPadding(float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = (int)paramFloat;
    int j = getLayoutMinHeight();
    if ((i + j > getHeight()) && (getNotGoneChildCount() > 0))
    {
      this.mTopPaddingOverflow = (i + j - getHeight());
      if (!paramBoolean2) {
        break label72;
      }
    }
    for (;;)
    {
      setTopPadding(i, paramBoolean1);
      setExpandedHeight(this.mExpandedHeight);
      return;
      this.mTopPaddingOverflow = 0.0F;
      break;
      label72:
      i = clampPadding(i);
    }
  }
  
  static class AnimationEvent
  {
    static AnimationFilter[] FILTERS = { new AnimationFilter().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateZ().hasDelays(), new AnimationFilter().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateZ().hasDelays(), new AnimationFilter().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateZ().hasDelays(), new AnimationFilter().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateDimmed().animateZ(), new AnimationFilter().animateShadowAlpha(), new AnimationFilter().animateShadowAlpha().animateHeight(), new AnimationFilter().animateZ(), new AnimationFilter().animateDimmed(), new AnimationFilter().animateAlpha().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateDark().hasDelays(), new AnimationFilter().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateDimmed().animateZ().hasDelays(), new AnimationFilter().animateHideSensitive(), new AnimationFilter().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateAlpha().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateZ().hasDelays(), new AnimationFilter().animateShadowAlpha().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateAlpha().animateShadowAlpha().animateDark().animateDimmed().animateHideSensitive().animateHeight().animateTopInset().animateY().animateZ() };
    static int[] LENGTHS = { 464, 464, 360, 360, 360, 360, 220, 220, 360, 360, 448, 360, 360, 360, 650, 230, 230, 360, 360 };
    final int animationType;
    final View changingView;
    int darkAnimationOriginIndex;
    final long eventStartTime = AnimationUtils.currentAnimationTimeMillis();
    final AnimationFilter filter;
    boolean headsUpFromBottom;
    final long length;
    View viewAfterChangingView;
    
    AnimationEvent(View paramView, int paramInt)
    {
      this(paramView, paramInt, LENGTHS[paramInt]);
    }
    
    AnimationEvent(View paramView, int paramInt, long paramLong)
    {
      this.changingView = paramView;
      this.animationType = paramInt;
      this.filter = FILTERS[paramInt];
      this.length = paramLong;
    }
    
    static long combineLength(ArrayList<AnimationEvent> paramArrayList)
    {
      long l = 0L;
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        AnimationEvent localAnimationEvent = (AnimationEvent)paramArrayList.get(i);
        l = Math.max(l, localAnimationEvent.length);
        if (localAnimationEvent.animationType == 10) {
          return localAnimationEvent.length;
        }
        i += 1;
      }
      return l;
    }
  }
  
  private class NotificationSwipeHelper
    extends SwipeHelper
  {
    private CheckForDrag mCheckForDrag;
    private Runnable mFalsingCheck = new Runnable()
    {
      public void run()
      {
        NotificationStackScrollLayout.NotificationSwipeHelper.this.resetExposedGearView(true, true);
      }
    };
    private boolean mGearSnappedOnLeft;
    private boolean mGearSnappedTo;
    private Handler mHandler = new Handler();
    
    public NotificationSwipeHelper(int paramInt, SwipeHelper.Callback paramCallback, Context paramContext)
    {
      super(paramCallback, paramContext);
    }
    
    private void cancelCheckForDrag()
    {
      if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this) != null) {
        NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).cancelFadeAnimator();
      }
      this.mHandler.removeCallbacks(this.mCheckForDrag);
    }
    
    private void checkForDrag()
    {
      if ((this.mCheckForDrag != null) && (this.mHandler.hasCallbacks(this.mCheckForDrag))) {
        return;
      }
      this.mCheckForDrag = new CheckForDrag(null);
      this.mHandler.postDelayed(this.mCheckForDrag, 60L);
    }
    
    private void dismissOrSnapBack(View paramView, float paramFloat, MotionEvent paramMotionEvent)
    {
      if (isDismissGesture(paramMotionEvent))
      {
        if (swipedFastEnough()) {}
        for (boolean bool = false;; bool = true)
        {
          dismissChild(paramView, paramFloat, bool);
          return;
        }
      }
      snapChild(paramView, 0.0F, paramFloat);
    }
    
    private float getSpaceForGear(View paramView)
    {
      if ((paramView instanceof ExpandableNotificationRow)) {
        return ((ExpandableNotificationRow)paramView).getSpaceForGear();
      }
      return 0.0F;
    }
    
    private void handleGearCoveredOrDismissed()
    {
      cancelCheckForDrag();
      setSnappedToGear(false);
      if ((NotificationStackScrollLayout.-get6(NotificationStackScrollLayout.this) != null) && (NotificationStackScrollLayout.-get6(NotificationStackScrollLayout.this) == NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this))) {
        NotificationStackScrollLayout.-set6(NotificationStackScrollLayout.this, null);
      }
    }
    
    private boolean isTowardsGear(float paramFloat, boolean paramBoolean)
    {
      if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this) == null) {
        return false;
      }
      if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isVisible())
      {
        if ((paramBoolean) && (paramFloat <= 0.0F)) {}
        while ((!paramBoolean) && (paramFloat >= 0.0F)) {
          return true;
        }
        return false;
      }
      return false;
    }
    
    private void setSnappedToGear(boolean paramBoolean)
    {
      boolean bool2 = false;
      if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this) != null) {}
      for (boolean bool1 = NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isIconOnLeft();; bool1 = false)
      {
        this.mGearSnappedOnLeft = bool1;
        bool1 = bool2;
        if (paramBoolean)
        {
          bool1 = bool2;
          if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this) != null) {
            bool1 = true;
          }
        }
        this.mGearSnappedTo = bool1;
        return;
      }
    }
    
    private void snapToGear(View paramView, float paramFloat)
    {
      float f = getSpaceForGear(paramView);
      if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isIconOnLeft()) {}
      for (;;)
      {
        NotificationStackScrollLayout.-set6(NotificationStackScrollLayout.this, NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this));
        if ((paramView instanceof ExpandableNotificationRow)) {
          MetricsLogger.action(NotificationStackScrollLayout.-get1(NotificationStackScrollLayout.this), 332, ((ExpandableNotificationRow)paramView).getStatusBarNotification().getPackageName());
        }
        if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this) != null)
        {
          NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).setSnapping(true);
          setSnappedToGear(true);
        }
        NotificationStackScrollLayout.this.onDragCancelled(paramView);
        if (NotificationStackScrollLayout.this.isAntiFalsingNeeded())
        {
          this.mHandler.removeCallbacks(this.mFalsingCheck);
          this.mHandler.postDelayed(this.mFalsingCheck, 4000L);
        }
        super.snapChild(paramView, f, paramFloat);
        return;
        f = -f;
      }
    }
    
    private boolean swipedEnoughToShowGear(View paramView)
    {
      if (NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this) == null) {
        return false;
      }
      float f1;
      float f2;
      if (NotificationStackScrollLayout.this.canChildBeDismissed(paramView))
      {
        f1 = 0.4F;
        f1 = getSpaceForGear(paramView) * f1;
        f2 = getTranslation(paramView);
        if ((swipedFarEnough()) || (!NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isVisible())) {
          break label98;
        }
        if (!NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isIconOnLeft()) {
          break label89;
        }
        if (f2 <= f1) {
          break label87;
        }
      }
      label87:
      label89:
      while (f2 < -f1)
      {
        return true;
        f1 = 0.2F;
        break;
        return false;
      }
      return false;
      label98:
      return false;
    }
    
    public void closeControlsIfOutsideTouch(MotionEvent paramMotionEvent)
    {
      NotificationGuts localNotificationGuts = NotificationStackScrollLayout.-get11(NotificationStackScrollLayout.this).getExposedGuts();
      Object localObject2 = null;
      int j = 0;
      Object localObject1;
      int i;
      if (localNotificationGuts != null)
      {
        localObject1 = localNotificationGuts;
        i = localNotificationGuts.getActualHeight();
      }
      for (;;)
      {
        if (localObject1 != null)
        {
          j = (int)paramMotionEvent.getRawX();
          int k = (int)paramMotionEvent.getRawY();
          ((View)localObject1).getLocationOnScreen(NotificationStackScrollLayout.-get15(NotificationStackScrollLayout.this));
          int m = NotificationStackScrollLayout.-get15(NotificationStackScrollLayout.this)[0];
          int n = NotificationStackScrollLayout.-get15(NotificationStackScrollLayout.this)[1];
          if (!new Rect(m, n, ((View)localObject1).getWidth() + m, n + i).contains(j, k)) {
            NotificationStackScrollLayout.-get11(NotificationStackScrollLayout.this).dismissPopups(-1, -1, true, true);
          }
        }
        return;
        i = j;
        localObject1 = localObject2;
        if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this) != null)
        {
          i = j;
          localObject1 = localObject2;
          if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isVisible())
          {
            i = j;
            localObject1 = localObject2;
            if (NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this) != null)
            {
              localObject1 = NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this);
              i = ((ExpandableView)NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this)).getActualHeight();
            }
          }
        }
      }
    }
    
    public void dismissChild(View paramView, float paramFloat, boolean paramBoolean)
    {
      super.dismissChild(paramView, paramFloat, paramBoolean);
      if (NotificationStackScrollLayout.-get7(NotificationStackScrollLayout.this)) {
        NotificationStackScrollLayout.-wrap1(NotificationStackScrollLayout.this, paramView);
      }
      handleGearCoveredOrDismissed();
    }
    
    public float getTranslation(View paramView)
    {
      return ((ExpandableView)paramView).getTranslation();
    }
    
    public Animator getViewTranslationAnimator(View paramView, float paramFloat, ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener)
    {
      if ((paramView instanceof ExpandableNotificationRow)) {
        return ((ExpandableNotificationRow)paramView).getTranslateViewAnimator(paramFloat, paramAnimatorUpdateListener);
      }
      return super.getViewTranslationAnimator(paramView, paramFloat, paramAnimatorUpdateListener);
    }
    
    public boolean handleUpEvent(MotionEvent paramMotionEvent, View paramView, float paramFloat1, float paramFloat2)
    {
      if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this) == null)
      {
        cancelCheckForDrag();
        return false;
      }
      boolean bool = isTowardsGear(paramFloat1, NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isIconOnLeft());
      int j;
      int i;
      if (Math.abs(paramFloat1) > getEscapeVelocity())
      {
        j = 1;
        double d = paramMotionEvent.getEventTime() - paramMotionEvent.getDownTime();
        if (NotificationStackScrollLayout.this.canChildBeDismissed(paramView)) {
          break label175;
        }
        if (d < 200.0D) {
          break label169;
        }
        i = 1;
        label83:
        if ((!this.mGearSnappedTo) || (!NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isVisible())) {
          break label275;
        }
        if (this.mGearSnappedOnLeft != NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isIconOnLeft()) {
          break label231;
        }
        if (Math.abs(getTranslation(paramView)) > getSpaceForGear(paramView) * 0.6F) {
          break label181;
        }
        i = 1;
        label144:
        if ((!bool) && (i == 0)) {
          break label187;
        }
        snapChild(paramView, 0.0F, paramFloat1);
      }
      for (;;)
      {
        return true;
        j = 0;
        break;
        label169:
        i = 0;
        break label83;
        label175:
        i = 0;
        break label83;
        label181:
        i = 0;
        break label144;
        label187:
        if (isDismissGesture(paramMotionEvent))
        {
          if (swipedFastEnough()) {}
          for (bool = false;; bool = true)
          {
            dismissChild(paramView, paramFloat1, bool);
            break;
          }
        }
        snapToGear(paramView, paramFloat1);
        continue;
        label231:
        if (((j != 0) || (!swipedEnoughToShowGear(paramView))) && ((!bool) || (swipedFarEnough())))
        {
          dismissOrSnapBack(paramView, paramFloat1, paramMotionEvent);
        }
        else
        {
          snapToGear(paramView, paramFloat1);
          continue;
          label275:
          if (((j != 0) && (i == 0)) || ((swipedEnoughToShowGear(paramView)) || (bool))) {
            snapToGear(paramView, paramFloat1);
          } else {
            dismissOrSnapBack(paramView, paramFloat1, paramMotionEvent);
          }
        }
      }
    }
    
    public void onDownUpdate(View paramView)
    {
      NotificationStackScrollLayout.-set8(NotificationStackScrollLayout.this, paramView);
      cancelCheckForDrag();
      if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this) != null) {
        NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).setSnapping(false);
      }
      this.mCheckForDrag = null;
      NotificationStackScrollLayout.-set2(NotificationStackScrollLayout.this, null);
      this.mHandler.removeCallbacks(this.mFalsingCheck);
      resetExposedGearView(true, false);
      if ((paramView instanceof ExpandableNotificationRow))
      {
        NotificationStackScrollLayout.-set2(NotificationStackScrollLayout.this, ((ExpandableNotificationRow)paramView).getSettingsRow());
        NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).setGearListener(NotificationStackScrollLayout.this);
      }
    }
    
    public void onMoveUpdate(View paramView, float paramFloat1, float paramFloat2)
    {
      boolean bool2 = false;
      this.mHandler.removeCallbacks(this.mFalsingCheck);
      if (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this) != null)
      {
        NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).setSnapping(false);
        if (!this.mGearSnappedTo) {
          break label119;
        }
        bool1 = this.mGearSnappedOnLeft;
        if (!isTowardsGear(paramFloat1, bool1)) {
          break label134;
        }
        bool1 = false;
        label61:
        if (bool1)
        {
          setSnappedToGear(false);
          if (this.mHandler.hasCallbacks(this.mCheckForDrag)) {
            break label150;
          }
          this.mCheckForDrag = null;
        }
      }
      label90:
      if ((paramView instanceof ExpandableNotificationRow)) {}
      for (boolean bool1 = ((ExpandableNotificationRow)paramView).areGutsExposed();; bool1 = false)
      {
        if ((!NotificationStackScrollLayout.isPinnedHeadsUp(paramView)) && (!bool1)) {
          break label199;
        }
        return;
        label119:
        bool1 = NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isIconOnLeft();
        break;
        label134:
        bool1 = NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isIconLocationChange(paramFloat1);
        break label61;
        label150:
        NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).setGearAlpha(0.0F);
        NotificationSettingsIconRow localNotificationSettingsIconRow = NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this);
        bool1 = bool2;
        if (paramFloat1 > 0.0F) {
          bool1 = true;
        }
        localNotificationSettingsIconRow.setIconLocation(bool1);
        break label90;
      }
      label199:
      checkForDrag();
    }
    
    public void resetExposedGearView(boolean paramBoolean1, boolean paramBoolean2)
    {
      if ((NotificationStackScrollLayout.-get6(NotificationStackScrollLayout.this) == null) || ((!paramBoolean2) && (NotificationStackScrollLayout.-get6(NotificationStackScrollLayout.this) == NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this)))) {
        return;
      }
      Object localObject = NotificationStackScrollLayout.-get6(NotificationStackScrollLayout.this);
      if (paramBoolean1)
      {
        localObject = getViewTranslationAnimator((View)localObject, 0.0F, null);
        if (localObject != null) {
          ((Animator)localObject).start();
        }
      }
      for (;;)
      {
        NotificationStackScrollLayout.-set6(NotificationStackScrollLayout.this, null);
        this.mGearSnappedTo = false;
        return;
        if ((NotificationStackScrollLayout.-get6(NotificationStackScrollLayout.this) instanceof ExpandableNotificationRow)) {
          ((ExpandableNotificationRow)NotificationStackScrollLayout.-get6(NotificationStackScrollLayout.this)).resetTranslation();
        }
      }
    }
    
    public void setTranslation(View paramView, float paramFloat)
    {
      ((ExpandableView)paramView).setTranslation(paramFloat);
    }
    
    public void snapChild(View paramView, float paramFloat1, float paramFloat2)
    {
      super.snapChild(paramView, paramFloat1, paramFloat2);
      NotificationStackScrollLayout.this.onDragCancelled(paramView);
      if (paramFloat1 == 0.0F) {
        handleGearCoveredOrDismissed();
      }
    }
    
    private final class CheckForDrag
      implements Runnable
    {
      private CheckForDrag() {}
      
      public void run()
      {
        if (NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this) == null) {
          return;
        }
        float f1 = NotificationStackScrollLayout.NotificationSwipeHelper.this.getTranslation(NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this));
        float f2 = Math.abs(f1);
        float f3 = NotificationStackScrollLayout.NotificationSwipeHelper.-wrap1(NotificationStackScrollLayout.NotificationSwipeHelper.this, NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this));
        float f4 = NotificationStackScrollLayout.NotificationSwipeHelper.-wrap0(NotificationStackScrollLayout.NotificationSwipeHelper.this, NotificationStackScrollLayout.-get16(NotificationStackScrollLayout.this)) * 0.4F;
        NotificationSettingsIconRow localNotificationSettingsIconRow;
        if ((NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this) != null) && ((!NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isVisible()) || (NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this).isIconLocationChange(f1))) && (f2 >= f3 * 0.4D) && (f2 < f4))
        {
          localNotificationSettingsIconRow = NotificationStackScrollLayout.-get2(NotificationStackScrollLayout.this);
          if (f1 <= 0.0F) {
            break label174;
          }
        }
        label174:
        for (boolean bool = true;; bool = false)
        {
          localNotificationSettingsIconRow.fadeInSettings(bool, f1, f4);
          return;
        }
      }
    }
  }
  
  public static abstract interface OnChildLocationsChangedListener
  {
    public abstract void onChildLocationsChanged(NotificationStackScrollLayout paramNotificationStackScrollLayout);
  }
  
  public static abstract interface OnEmptySpaceClickListener
  {
    public abstract void onEmptySpaceClicked(float paramFloat1, float paramFloat2);
  }
  
  public static abstract interface OnOverscrollTopChangedListener
  {
    public abstract void flingTopOverscroll(float paramFloat, boolean paramBoolean);
    
    public abstract void onOverscrollTopChanged(float paramFloat, boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\NotificationStackScrollLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */