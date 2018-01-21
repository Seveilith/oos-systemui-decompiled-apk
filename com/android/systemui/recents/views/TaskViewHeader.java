package com.android.systemui.recents.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions.OnAnimationStartedListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.CountDownTimer;
import android.support.v4.graphics.ColorUtils;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.LockStateController;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsDebugFlags;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.DockedFirstAnimationFrameEvent;
import com.android.systemui.recents.events.activity.LaunchTaskEvent;
import com.android.systemui.recents.events.ui.ShowApplicationInfoEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.Utilities;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.util.MdmLogger;

public class TaskViewHeader
  extends FrameLayout
  implements View.OnClickListener, View.OnLongClickListener
{
  ImageView mAppIconView;
  ImageView mAppInfoView;
  FrameLayout mAppOverlayView;
  TextView mAppTitleView;
  private HighlightColorDrawable mBackground;
  int mCornerRadius;
  Drawable mDarkDismissDrawable;
  Drawable mDarkDockDrawable;
  Drawable mDarkFreeformIcon;
  Drawable mDarkFullscreenIcon;
  Drawable mDarkInfoIcon;
  Drawable mDarkLockOffDrawable;
  Drawable mDarkLockOnDrawable;
  @ViewDebug.ExportedProperty(category="recents")
  float mDimAlpha;
  private Paint mDimLayerPaint = new Paint();
  int mDisabledTaskBarBackgroundColor;
  ImageView mDismissButton;
  ImageView mDockButton;
  private CountDownTimer mFocusTimerCountDown;
  ProgressBar mFocusTimerIndicator;
  int mHeaderBarHeight;
  int mHeaderButtonPadding;
  int mHighlightHeight;
  ImageView mIconView;
  Drawable mLightDismissDrawable;
  Drawable mLightDockDrawable;
  Drawable mLightFreeformIcon;
  Drawable mLightFullscreenIcon;
  Drawable mLightInfoIcon;
  Drawable mLightLockOffDrawable;
  Drawable mLightLockOnDrawable;
  ImageView mLockTaskButton;
  ImageView mMoveTaskButton;
  int mMoveTaskTargetStackId = -1;
  private HighlightColorDrawable mOverlayBackground;
  Task mTask;
  int mTaskBarViewDarkTextColor;
  int mTaskBarViewLightTextColor;
  @ViewDebug.ExportedProperty(category="recents")
  Rect mTaskViewRect = new Rect();
  TextView mTitleView;
  private float[] mTmpHSL = new float[3];
  
  public TaskViewHeader(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TaskViewHeader(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TaskViewHeader(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public TaskViewHeader(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setWillNotDraw(false);
    paramAttributeSet = paramContext.getResources();
    this.mLightDismissDrawable = paramContext.getDrawable(2130838105);
    this.mDarkDismissDrawable = paramContext.getDrawable(2130838104);
    this.mCornerRadius = paramAttributeSet.getDimensionPixelSize(2131755614);
    this.mHighlightHeight = paramAttributeSet.getDimensionPixelSize(2131755616);
    this.mTaskBarViewLightTextColor = paramContext.getColor(2131493014);
    this.mTaskBarViewDarkTextColor = paramContext.getColor(2131493015);
    this.mLightFreeformIcon = paramContext.getDrawable(2130838120);
    this.mDarkFreeformIcon = paramContext.getDrawable(2130838119);
    this.mLightFullscreenIcon = paramContext.getDrawable(2130838122);
    this.mDarkFullscreenIcon = paramContext.getDrawable(2130838121);
    this.mLightInfoIcon = paramContext.getDrawable(2130838111);
    this.mDarkInfoIcon = paramContext.getDrawable(2130838110);
    this.mDisabledTaskBarBackgroundColor = paramContext.getColor(2131493011);
    this.mLightLockOnDrawable = paramContext.getDrawable(2130838115);
    this.mDarkLockOnDrawable = paramContext.getDrawable(2130838114);
    this.mLightLockOffDrawable = paramContext.getDrawable(2130838113);
    this.mDarkLockOffDrawable = paramContext.getDrawable(2130838112);
    this.mLightDockDrawable = paramContext.getDrawable(2130838108);
    this.mDarkDockDrawable = paramContext.getDrawable(2130838107);
    this.mBackground = new HighlightColorDrawable();
    this.mBackground.setColorAndDim(Color.argb(255, 0, 0, 0), 0.0F);
    setBackground(this.mBackground);
    this.mOverlayBackground = new HighlightColorDrawable();
    this.mDimLayerPaint.setColor(Color.argb(255, 0, 0, 0));
    this.mDimLayerPaint.setAntiAlias(true);
  }
  
  private void hideAppOverlay(boolean paramBoolean)
  {
    if (this.mAppOverlayView == null) {
      return;
    }
    if (paramBoolean)
    {
      this.mAppOverlayView.setVisibility(8);
      return;
    }
    int i = this.mIconView.getLeft();
    int j = this.mIconView.getWidth() / 2;
    int k = this.mIconView.getTop();
    int m = this.mIconView.getHeight() / 2;
    Animator localAnimator = ViewAnimationUtils.createCircularReveal(this.mAppOverlayView, i + j, k + m, getWidth(), 0.0F);
    localAnimator.setDuration(250L);
    localAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
    localAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        TaskViewHeader.this.mAppOverlayView.setVisibility(8);
      }
    });
    localAnimator.start();
  }
  
  private boolean isSupportingDockButton()
  {
    return false;
  }
  
  private void setLockState(Task paramTask)
  {
    if (paramTask.isLocked)
    {
      localImageView = this.mLockTaskButton;
      if (paramTask.useLightOnPrimaryColor) {}
      for (paramTask = this.mLightLockOnDrawable;; paramTask = this.mDarkLockOnDrawable)
      {
        localImageView.setImageDrawable(paramTask);
        return;
      }
    }
    ImageView localImageView = this.mLockTaskButton;
    if (paramTask.useLightOnPrimaryColor) {}
    for (paramTask = this.mLightLockOffDrawable;; paramTask = this.mDarkLockOffDrawable)
    {
      localImageView.setImageDrawable(paramTask);
      return;
    }
  }
  
  private void showAppOverlay()
  {
    Object localObject1 = Recents.getSystemServices();
    Object localObject2 = this.mTask.key.getComponent();
    int j = this.mTask.key.userId;
    localObject2 = ((SystemServicesProxy)localObject1).getActivityInfo((ComponentName)localObject2, j);
    if (localObject2 == null) {
      return;
    }
    if (this.mAppOverlayView == null)
    {
      this.mAppOverlayView = ((FrameLayout)Utilities.findViewStubById(this, 2131952200).inflate());
      this.mAppOverlayView.setBackground(this.mOverlayBackground);
      this.mAppIconView = ((ImageView)this.mAppOverlayView.findViewById(2131952099));
      this.mAppIconView.setOnClickListener(this);
      this.mAppIconView.setOnLongClickListener(this);
      this.mAppInfoView = ((ImageView)this.mAppOverlayView.findViewById(2131952203));
      this.mAppInfoView.setOnClickListener(this);
      this.mAppTitleView = ((TextView)this.mAppOverlayView.findViewById(2131952202));
      updateLayoutParams(this.mAppIconView, this.mAppTitleView, null, this.mAppInfoView);
    }
    this.mAppTitleView.setText(((SystemServicesProxy)localObject1).getBadgedApplicationLabel(((ActivityInfo)localObject2).applicationInfo, j));
    TextView localTextView = this.mAppTitleView;
    int i;
    if (this.mTask.useLightOnPrimaryColor)
    {
      i = this.mTaskBarViewLightTextColor;
      localTextView.setTextColor(i);
      this.mAppIconView.setImageDrawable(((SystemServicesProxy)localObject1).getBadgedApplicationIcon(((ActivityInfo)localObject2).applicationInfo, j));
      localObject2 = this.mAppInfoView;
      if (!this.mTask.useLightOnPrimaryColor) {
        break label361;
      }
    }
    label361:
    for (localObject1 = this.mLightInfoIcon;; localObject1 = this.mDarkInfoIcon)
    {
      ((ImageView)localObject2).setImageDrawable((Drawable)localObject1);
      this.mAppOverlayView.setVisibility(0);
      i = this.mIconView.getLeft();
      j = this.mIconView.getWidth() / 2;
      int k = this.mIconView.getTop();
      int m = this.mIconView.getHeight() / 2;
      localObject1 = ViewAnimationUtils.createCircularReveal(this.mAppOverlayView, i + j, k + m, 0.0F, getWidth());
      ((Animator)localObject1).setDuration(250L);
      ((Animator)localObject1).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
      ((Animator)localObject1).start();
      return;
      i = this.mTaskBarViewDarkTextColor;
      break;
    }
  }
  
  private void updateBackgroundColor(int paramInt, float paramFloat)
  {
    if (this.mTask != null)
    {
      this.mBackground.setColorAndDim(paramInt, paramFloat);
      ColorUtils.colorToHSL(paramInt, this.mTmpHSL);
      this.mTmpHSL[2] = Math.min(1.0F, this.mTmpHSL[2] + (1.0F - paramFloat) * -0.0625F);
      this.mOverlayBackground.setColorAndDim(ColorUtils.HSLToColor(this.mTmpHSL), paramFloat);
      this.mDimLayerPaint.setAlpha((int)(255.0F * paramFloat));
      invalidate();
    }
  }
  
  private void updateLayoutParams(View paramView1, View paramView2, View paramView3, View paramView4)
  {
    setLayoutParams(new FrameLayout.LayoutParams(-1, this.mHeaderBarHeight, 48));
    paramView1.setLayoutParams(new FrameLayout.LayoutParams(this.mHeaderBarHeight, this.mHeaderBarHeight, 8388611));
    paramView1 = new FrameLayout.LayoutParams(-1, -2, 8388627);
    paramView1.setMarginStart(this.mHeaderBarHeight);
    if (this.mMoveTaskButton != null) {}
    for (int i = this.mHeaderBarHeight * 2;; i = this.mHeaderBarHeight)
    {
      paramView1.setMarginEnd(i);
      paramView2.setLayoutParams(paramView1);
      if (paramView3 != null)
      {
        paramView1 = new FrameLayout.LayoutParams(this.mHeaderBarHeight, this.mHeaderBarHeight, 8388613);
        paramView1.setMarginEnd(this.mHeaderBarHeight);
        paramView3.setLayoutParams(paramView1);
        paramView3.setPadding(this.mHeaderButtonPadding, this.mHeaderButtonPadding, this.mHeaderButtonPadding, this.mHeaderButtonPadding);
      }
      paramView4.setLayoutParams(new FrameLayout.LayoutParams(this.mHeaderBarHeight, this.mHeaderBarHeight, 8388613));
      paramView4.setPadding(this.mHeaderButtonPadding, this.mHeaderButtonPadding, this.mHeaderButtonPadding, this.mHeaderButtonPadding);
      return;
    }
  }
  
  public void bindToTask(Task paramTask, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mTask = paramTask;
    int i;
    if (paramBoolean2)
    {
      i = this.mDisabledTaskBarBackgroundColor;
      if (this.mBackground.getColor() != i) {
        updateBackgroundColor(i, this.mDimAlpha);
      }
      if (!this.mTitleView.getText().toString().equals(paramTask.title)) {
        this.mTitleView.setText(paramTask.title);
      }
      this.mTitleView.setContentDescription(paramTask.titleDescription);
      localObject = this.mTitleView;
      if (!paramTask.useLightOnPrimaryColor) {
        break label380;
      }
      i = this.mTaskBarViewLightTextColor;
      label100:
      ((TextView)localObject).setTextColor(i);
      setLockState(paramTask);
      this.mLockTaskButton.setOnClickListener(this);
      ((RippleDrawable)this.mLockTaskButton.getBackground()).setForceSoftware(true);
      this.mDockButton.setOnClickListener(this);
      localImageView = this.mDockButton;
      if (!paramTask.useLightOnPrimaryColor) {
        break label389;
      }
      localObject = this.mLightDockDrawable;
      label161:
      localImageView.setImageDrawable((Drawable)localObject);
      ((RippleDrawable)this.mDockButton.getBackground()).setForceSoftware(true);
      if (isSupportingDockButton()) {
        break label398;
      }
      this.mDockButton.setVisibility(8);
      this.mDockButton.setClickable(false);
      label206:
      if (this.mMoveTaskButton != null)
      {
        if (!paramTask.isFreeformTask()) {
          break label434;
        }
        this.mMoveTaskTargetStackId = 1;
        localImageView = this.mMoveTaskButton;
        if (!paramTask.useLightOnPrimaryColor) {
          break label425;
        }
      }
    }
    label380:
    label389:
    label398:
    label425:
    for (Object localObject = this.mLightFullscreenIcon;; localObject = this.mDarkFullscreenIcon)
    {
      localImageView.setImageDrawable((Drawable)localObject);
      this.mMoveTaskButton.setOnClickListener(this);
      this.mMoveTaskButton.setClickable(false);
      ((RippleDrawable)this.mMoveTaskButton.getBackground()).setForceSoftware(true);
      if (Recents.getDebugFlags().isFastToggleRecentsEnabled())
      {
        if (this.mFocusTimerIndicator == null) {
          this.mFocusTimerIndicator = ((ProgressBar)Utilities.findViewStubById(this, 2131952198).inflate());
        }
        this.mFocusTimerIndicator.getProgressDrawable().setColorFilter(getSecondaryColor(paramTask.colorPrimary, paramTask.useLightOnPrimaryColor), PorterDuff.Mode.SRC_IN);
      }
      if (paramBoolean1)
      {
        this.mIconView.setContentDescription(paramTask.appInfoDescription);
        this.mIconView.setOnClickListener(this);
        this.mIconView.setClickable(true);
      }
      return;
      i = paramTask.colorPrimary;
      break;
      i = this.mTaskBarViewDarkTextColor;
      break label100;
      localObject = this.mDarkDockDrawable;
      break label161;
      this.mDockButton.setVisibility(0);
      this.mDockButton.setAlpha(1.0F);
      this.mDockButton.setClickable(true);
      break label206;
    }
    label434:
    this.mMoveTaskTargetStackId = 2;
    ImageView localImageView = this.mMoveTaskButton;
    if (paramTask.useLightOnPrimaryColor) {}
    for (localObject = this.mLightFreeformIcon;; localObject = this.mDarkFreeformIcon)
    {
      localImageView.setImageDrawable((Drawable)localObject);
      break;
    }
  }
  
  public void cancelFocusTimerIndicator()
  {
    if (this.mFocusTimerIndicator == null) {
      return;
    }
    if (this.mFocusTimerCountDown != null)
    {
      this.mFocusTimerCountDown.cancel();
      this.mFocusTimerIndicator.setProgress(0);
      this.mFocusTimerIndicator.setVisibility(4);
    }
  }
  
  public ImageView getIconView()
  {
    return this.mIconView;
  }
  
  int getSecondaryColor(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = -1;; i = -16777216) {
      return Utilities.getColorWithOverlay(paramInt, i, 0.8F);
    }
  }
  
  public void onClick(View paramView)
  {
    boolean bool = false;
    if (paramView == this.mIconView) {
      EventBus.getDefault().send(new ShowApplicationInfoEvent(this.mTask));
    }
    label301:
    do
    {
      return;
      if (paramView == this.mDismissButton)
      {
        ((TaskView)Utilities.findParent(this, TaskView.class)).dismissTask();
        MetricsLogger.histogram(getContext(), "overview_task_dismissed_source", 2);
        return;
      }
      if (paramView == this.mMoveTaskButton)
      {
        TaskView localTaskView = (TaskView)Utilities.findParent(this, TaskView.class);
        if (this.mMoveTaskTargetStackId == 2) {}
        for (paramView = new Rect(this.mTaskViewRect);; paramView = new Rect())
        {
          EventBus.getDefault().send(new LaunchTaskEvent(localTaskView, this.mTask, paramView, this.mMoveTaskTargetStackId, false));
          return;
        }
      }
      if (paramView == this.mAppInfoView)
      {
        EventBus.getDefault().send(new ShowApplicationInfoEvent(this.mTask));
        return;
      }
      if (paramView == this.mAppIconView)
      {
        hideAppOverlay(false);
        return;
      }
      if (paramView == this.mLockTaskButton)
      {
        if (this.mTask == null) {
          return;
        }
        if (this.mTask.isLocked)
        {
          MdmLogger.log("recent_unlock", "", "1");
          paramView = this.mTask;
          if (!this.mTask.isLocked) {
            break label301;
          }
        }
        for (;;)
        {
          paramView.isLocked = bool;
          LockStateController.getInstance(getContext()).setLockState(this.mTask.key.baseIntent.getComponent().toShortString(), this.mTask.isLocked, this.mTask.key.userId);
          setLockState(this.mTask);
          return;
          MdmLogger.log("recent_lock", "", "1");
          break;
          bool = true;
        }
      }
    } while (paramView != this.mDockButton);
    if (Recents.getSystemServices().startTaskInDockedMode(this.mTask.key.id, 0))
    {
      new ActivityOptions.OnAnimationStartedListener()
      {
        public void onAnimationStarted()
        {
          EventBus.getDefault().send(new DockedFirstAnimationFrameEvent());
          if ((this.val$tv != null) && (this.val$tv.getStack() != null)) {
            this.val$tv.getStack().removeTask(TaskViewHeader.this.mTask, null, true);
          }
        }
      };
      return;
    }
    Log.d("RecentsView", "fail to enter dock by button");
  }
  
  public void onConfigurationChanged()
  {
    getResources();
    int i = TaskStackLayoutAlgorithm.getDimensionForDevice(getContext(), 2131755610, 2131755610, 2131755610, 2131755611, 2131755610, 2131755611);
    int j = TaskStackLayoutAlgorithm.getDimensionForDevice(getContext(), 2131755612, 2131755612, 2131755612, 2131755613, 2131755612, 2131755613);
    if ((i != this.mHeaderBarHeight) || (j != this.mHeaderButtonPadding))
    {
      this.mHeaderBarHeight = i;
      this.mHeaderButtonPadding = j;
      updateLayoutParams(this.mIconView, this.mTitleView, this.mDockButton, this.mLockTaskButton);
      if (this.mAppOverlayView != null) {
        updateLayoutParams(this.mAppIconView, this.mAppTitleView, null, this.mAppInfoView);
      }
    }
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    return new int[0];
  }
  
  public void onDrawForeground(Canvas paramCanvas)
  {
    super.onDrawForeground(paramCanvas);
    paramCanvas.drawRoundRect(0.0F, 0.0F, this.mTaskViewRect.width(), getHeight() + this.mCornerRadius, this.mCornerRadius, this.mCornerRadius, this.mDimLayerPaint);
  }
  
  protected void onFinishInflate()
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    this.mIconView = ((ImageView)findViewById(2131951747));
    this.mIconView.setOnLongClickListener(this);
    this.mTitleView = ((TextView)findViewById(2131951748));
    this.mLockTaskButton = ((ImageView)findViewById(2131952197));
    this.mDockButton = ((ImageView)findViewById(2131952195));
    if (localSystemServicesProxy.hasFreeformWorkspaceSupport()) {
      this.mMoveTaskButton = ((ImageView)findViewById(2131952194));
    }
    setNoUserInteractionState();
    onConfigurationChanged();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    onTaskViewSizeChanged(this.mTaskViewRect.width(), this.mTaskViewRect.height());
  }
  
  public boolean onLongClick(View paramView)
  {
    if (paramView == this.mIconView)
    {
      showAppOverlay();
      return true;
    }
    if (paramView == this.mAppIconView)
    {
      hideAppOverlay(false);
      return true;
    }
    return false;
  }
  
  public void onTaskDataLoaded()
  {
    if (this.mTask.icon != null) {
      this.mIconView.setImageDrawable(this.mTask.icon);
    }
  }
  
  public void onTaskViewSizeChanged(int paramInt1, int paramInt2)
  {
    this.mTaskViewRect.set(0, 0, paramInt1, paramInt2);
    int k = 1;
    int m = 1;
    int i1 = paramInt1 - getMeasuredWidth();
    int n = 1;
    ImageView localImageView = this.mLockTaskButton;
    int j = m;
    paramInt2 = n;
    int i = k;
    if (this.mTask != null)
    {
      j = m;
      paramInt2 = n;
      i = k;
      if (this.mTask.isFreeformTask())
      {
        this.mTitleView.setTextSize(2, 16.0F);
        k = this.mIconView.getMeasuredWidth();
        i = (int)this.mTitleView.getPaint().measureText(this.mTask.title);
        m = localImageView.getMeasuredWidth();
        if (this.mMoveTaskButton == null) {
          break label258;
        }
        paramInt2 = this.mMoveTaskButton.getMeasuredWidth();
        if (paramInt1 < k + m + paramInt2 + i) {
          break label263;
        }
        i = 1;
        label147:
        if (paramInt1 < k + m + paramInt2) {
          break label268;
        }
        j = 1;
        label161:
        if (paramInt1 < k + m) {
          break label274;
        }
        paramInt2 = 1;
      }
    }
    label172:
    Object localObject = this.mTitleView;
    if (i != 0)
    {
      i = 0;
      label184:
      ((TextView)localObject).setVisibility(i);
      if (this.mMoveTaskButton != null)
      {
        localObject = this.mMoveTaskButton;
        if (j == 0) {
          break label284;
        }
        i = 0;
        label210:
        ((ImageView)localObject).setVisibility(i);
        this.mMoveTaskButton.setTranslationX(i1);
      }
      if (paramInt2 == 0) {
        break label289;
      }
    }
    label258:
    label263:
    label268:
    label274:
    label284:
    label289:
    for (paramInt2 = 0;; paramInt2 = 4)
    {
      localImageView.setVisibility(paramInt2);
      localImageView.setTranslationX(i1);
      setLeftTopRightBottom(0, 0, paramInt1, getMeasuredHeight());
      return;
      paramInt2 = 0;
      break;
      i = 0;
      break label147;
      j = 0;
      break label161;
      paramInt2 = 0;
      break label172;
      i = 4;
      break label184;
      i = 4;
      break label210;
    }
  }
  
  public void reset()
  {
    hideAppOverlay(true);
  }
  
  public void setDimAlpha(float paramFloat)
  {
    if (Float.compare(this.mDimAlpha, paramFloat) != 0)
    {
      this.mDimAlpha = paramFloat;
      this.mTitleView.setAlpha(1.0F - paramFloat);
      updateBackgroundColor(this.mBackground.getColor(), paramFloat);
    }
  }
  
  void setNoUserInteractionState()
  {
    this.mLockTaskButton.setVisibility(0);
    this.mLockTaskButton.animate().cancel();
    this.mLockTaskButton.setAlpha(1.0F);
    this.mLockTaskButton.setClickable(true);
    if (isSupportingDockButton())
    {
      this.mDockButton.setVisibility(0);
      this.mDockButton.animate().cancel();
      this.mDockButton.setAlpha(1.0F);
      this.mDockButton.setClickable(true);
    }
    for (;;)
    {
      if (this.mMoveTaskButton != null)
      {
        this.mMoveTaskButton.setVisibility(0);
        this.mMoveTaskButton.animate().cancel();
        this.mMoveTaskButton.setAlpha(1.0F);
        this.mMoveTaskButton.setClickable(true);
      }
      return;
      this.mDockButton.setVisibility(8);
      this.mDockButton.animate().cancel();
      this.mDockButton.setAlpha(0.0F);
      this.mDockButton.setClickable(false);
    }
  }
  
  public void startFocusTimerIndicator(int paramInt)
  {
    if (this.mFocusTimerIndicator == null) {
      return;
    }
    this.mFocusTimerIndicator.setVisibility(0);
    this.mFocusTimerIndicator.setMax(paramInt);
    this.mFocusTimerIndicator.setProgress(paramInt);
    if (this.mFocusTimerCountDown != null) {
      this.mFocusTimerCountDown.cancel();
    }
    this.mFocusTimerCountDown = new CountDownTimer(paramInt, 30L)
    {
      public void onFinish() {}
      
      public void onTick(long paramAnonymousLong)
      {
        TaskViewHeader.this.mFocusTimerIndicator.setProgress((int)paramAnonymousLong);
      }
    }.start();
  }
  
  void startNoUserInteractionAnimation()
  {
    int i = getResources().getInteger(2131623992);
    this.mLockTaskButton.setVisibility(0);
    this.mLockTaskButton.setClickable(true);
    if (this.mLockTaskButton.getVisibility() == 0)
    {
      this.mLockTaskButton.animate().alpha(1.0F).setInterpolator(Interpolators.FAST_OUT_LINEAR_IN).setDuration(i).start();
      if (!isSupportingDockButton()) {
        break label201;
      }
      this.mDockButton.setVisibility(0);
      this.mDockButton.setClickable(true);
      if (this.mDockButton.getVisibility() != 0) {
        break label190;
      }
      this.mDockButton.animate().alpha(1.0F).setInterpolator(Interpolators.FAST_OUT_LINEAR_IN).setDuration(i).start();
    }
    for (;;)
    {
      if (this.mMoveTaskButton != null)
      {
        if (this.mMoveTaskButton.getVisibility() != 0) {
          break label221;
        }
        this.mMoveTaskButton.setVisibility(0);
        this.mMoveTaskButton.setClickable(true);
        this.mMoveTaskButton.animate().alpha(1.0F).setInterpolator(Interpolators.FAST_OUT_LINEAR_IN).setDuration(i).start();
      }
      return;
      this.mLockTaskButton.setAlpha(1.0F);
      break;
      label190:
      this.mDockButton.setAlpha(1.0F);
      continue;
      label201:
      this.mDockButton.setVisibility(8);
      this.mDockButton.setClickable(false);
    }
    label221:
    this.mMoveTaskButton.setAlpha(1.0F);
  }
  
  void unbindFromTask(boolean paramBoolean)
  {
    this.mTask = null;
    this.mIconView.setImageDrawable(null);
    if (paramBoolean) {
      this.mIconView.setClickable(false);
    }
  }
  
  public void updateHeaderDockButton()
  {
    if (isSupportingDockButton())
    {
      this.mDockButton.setVisibility(0);
      this.mDockButton.setClickable(true);
      this.mDockButton.setAlpha(1.0F);
      return;
    }
    this.mDockButton.setVisibility(8);
    this.mDockButton.setClickable(false);
    this.mDockButton.setAlpha(0.0F);
  }
  
  private class HighlightColorDrawable
    extends Drawable
  {
    private Paint mBackgroundPaint = new Paint();
    private int mColor;
    private float mDimAlpha;
    private Paint mHighlightPaint = new Paint();
    
    public HighlightColorDrawable()
    {
      this.mBackgroundPaint.setColor(Color.argb(255, 0, 0, 0));
      this.mBackgroundPaint.setAntiAlias(true);
      this.mHighlightPaint.setColor(Color.argb(255, 255, 255, 255));
      this.mHighlightPaint.setAntiAlias(true);
    }
    
    public void draw(Canvas paramCanvas)
    {
      paramCanvas.drawRoundRect(0.0F, 0.0F, TaskViewHeader.this.mTaskViewRect.width(), Math.max(TaskViewHeader.this.mHighlightHeight, TaskViewHeader.this.mCornerRadius) * 2, TaskViewHeader.this.mCornerRadius, TaskViewHeader.this.mCornerRadius, this.mHighlightPaint);
      paramCanvas.drawRoundRect(0.0F, TaskViewHeader.this.mHighlightHeight, TaskViewHeader.this.mTaskViewRect.width(), TaskViewHeader.this.getHeight() + TaskViewHeader.this.mCornerRadius, TaskViewHeader.this.mCornerRadius, TaskViewHeader.this.mCornerRadius, this.mBackgroundPaint);
    }
    
    public int getColor()
    {
      return this.mColor;
    }
    
    public int getOpacity()
    {
      return -1;
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorAndDim(int paramInt, float paramFloat)
    {
      if ((this.mColor != paramInt) || (Float.compare(this.mDimAlpha, paramFloat) != 0))
      {
        this.mColor = paramInt;
        this.mDimAlpha = paramFloat;
        this.mBackgroundPaint.setColor(paramInt);
        ColorUtils.colorToHSL(paramInt, TaskViewHeader.-get0(TaskViewHeader.this));
        TaskViewHeader.-get0(TaskViewHeader.this)[2] = Math.min(1.0F, TaskViewHeader.-get0(TaskViewHeader.this)[2] + (1.0F - paramFloat) * 0.075F);
        this.mHighlightPaint.setColor(ColorUtils.HSLToColor(TaskViewHeader.-get0(TaskViewHeader.this)));
        invalidateSelf();
      }
    }
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\TaskViewHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */