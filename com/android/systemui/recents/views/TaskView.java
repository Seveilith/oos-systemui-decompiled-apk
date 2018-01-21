package com.android.systemui.recents.views;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityManager.TaskThumbnailInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.LaunchTaskEvent;
import com.android.systemui.recents.events.ui.DismissTaskViewEvent;
import com.android.systemui.recents.events.ui.TaskViewDismissedEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragEndCancelledEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragEndEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragStartEvent;
import com.android.systemui.recents.misc.ReferenceCountedTrigger;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.Utilities;
import com.android.systemui.recents.model.RecentsTaskLoader;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskCallbacks;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.model.TaskStack.DockState;
import com.android.systemui.recents.model.ThumbnailData;
import java.util.ArrayList;

public class TaskView
  extends FixedSizeFrameLayout
  implements Task.TaskCallbacks, View.OnClickListener, View.OnLongClickListener
{
  public static final Property<TaskView, Float> DIM_ALPHA = new FloatProperty("dimAlpha")
  {
    public Float get(TaskView paramAnonymousTaskView)
    {
      return Float.valueOf(paramAnonymousTaskView.getDimAlpha());
    }
    
    public void setValue(TaskView paramAnonymousTaskView, float paramAnonymousFloat)
    {
      paramAnonymousTaskView.setDimAlpha(paramAnonymousFloat);
    }
  };
  public static final Property<TaskView, Float> DIM_ALPHA_WITHOUT_HEADER = new FloatProperty("dimAlphaWithoutHeader")
  {
    public Float get(TaskView paramAnonymousTaskView)
    {
      return Float.valueOf(paramAnonymousTaskView.getDimAlpha());
    }
    
    public void setValue(TaskView paramAnonymousTaskView, float paramAnonymousFloat)
    {
      paramAnonymousTaskView.setDimAlphaWithoutHeader(paramAnonymousFloat);
    }
  };
  public static final Property<TaskView, Float> VIEW_OUTLINE_ALPHA = new FloatProperty("viewOutlineAlpha")
  {
    public Float get(TaskView paramAnonymousTaskView)
    {
      return Float.valueOf(paramAnonymousTaskView.getViewBounds().getAlpha());
    }
    
    public void setValue(TaskView paramAnonymousTaskView, float paramAnonymousFloat)
    {
      paramAnonymousTaskView.getViewBounds().setAlpha(paramAnonymousFloat);
    }
  };
  private float mActionButtonTranslationZ;
  private View mActionButtonView;
  private TaskViewCallbacks mCb;
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mClipViewInStack = true;
  @ViewDebug.ExportedProperty(category="recents")
  private float mDimAlpha;
  private ObjectAnimator mDimAnimator;
  private Toast mDisabledAppToast;
  @ViewDebug.ExportedProperty(category="recents")
  private Point mDownTouchPos = new Point();
  @ViewDebug.ExportedProperty(deepExport=true, prefix="header_")
  TaskViewHeader mHeaderView;
  private View mIncompatibleAppToastView;
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mIsDisabledInSafeMode;
  private ObjectAnimator mOutlineAnimator;
  private final TaskViewTransform mTargetAnimationTransform = new TaskViewTransform();
  @ViewDebug.ExportedProperty(deepExport=true, prefix="task_")
  private Task mTask;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="thumbnail_")
  TaskViewThumbnail mThumbnailView;
  private ArrayList<Animator> mTmpAnimators = new ArrayList();
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mTouchExplorationEnabled;
  private AnimatorSet mTransformAnimation;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="view_bounds_")
  private AnimateableViewBounds mViewBounds;
  
  public TaskView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TaskView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TaskView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public TaskView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramAttributeSet = Recents.getConfiguration();
    paramContext = paramContext.getResources();
    this.mViewBounds = new AnimateableViewBounds(this, paramContext.getDimensionPixelSize(2131755615));
    if (paramAttributeSet.fakeShadows) {
      setBackground(new FakeShadowDrawable(paramContext, paramAttributeSet));
    }
    setOutlineProvider(this.mViewBounds);
    setOnLongClickListener(this);
  }
  
  public void cancelTransformAnimation()
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    if (localSystemServicesProxy.isDeepCleaning())
    {
      Log.d("TaskView", "reset deepCleaning");
      localSystemServicesProxy.setDeepCleaning(false);
      if (this.mCb != null) {
        this.mCb.showDismissAllButton();
      }
    }
    Utilities.cancelAnimationWithoutCallbacks(this.mTransformAnimation);
    Utilities.cancelAnimationWithoutCallbacks(this.mDimAnimator);
    Utilities.cancelAnimationWithoutCallbacks(this.mOutlineAnimator);
  }
  
  void dismissTask()
  {
    DismissTaskViewEvent localDismissTaskViewEvent = new DismissTaskViewEvent(this);
    localDismissTaskViewEvent.addPostAnimationCallback(new Runnable()
    {
      public void run()
      {
        EventBus.getDefault().send(new TaskViewDismissedEvent(TaskView.-get1(TaskView.this), jdField_this, new AnimationProps(200, Interpolators.FAST_OUT_SLOW_IN)));
      }
    });
    EventBus.getDefault().send(localDismissTaskViewEvent);
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    super.dispatchDraw(paramCanvas);
    if (this.mTask.key.isTopAppLocked)
    {
      Object localObject = Recents.getTaskLoader();
      ActivityManager.TaskThumbnailInfo localTaskThumbnailInfo = Recents.getSystemServices().getTaskThumbnail(this.mTask.key.id, true).thumbnailInfo;
      if (localObject != null) {
        this.mThumbnailView.setThumbnail(((RecentsTaskLoader)localObject).getDefaultThumbnail(), localTaskThumbnailInfo);
      }
      localObject = getResources().getDrawable(2130837861);
      int i = (getMeasuredWidth() - 120) / 2;
      int j = (getMeasuredHeight() - 120) / 2;
      ((Drawable)localObject).setBounds(i, j, i + 120, j + 120);
      ((Drawable)localObject).draw(paramCanvas);
    }
  }
  
  public float getDimAlpha()
  {
    return this.mDimAlpha;
  }
  
  public TaskViewHeader getHeaderView()
  {
    return this.mHeaderView;
  }
  
  public Task getTask()
  {
    return this.mTask;
  }
  
  AnimateableViewBounds getViewBounds()
  {
    return this.mViewBounds;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void hideActionButton(boolean paramBoolean1, int paramInt, boolean paramBoolean2, final Animator.AnimatorListener paramAnimatorListener)
  {
    if ((paramBoolean1) && (this.mActionButtonView.getAlpha() > 0.0F))
    {
      if (paramBoolean2) {
        this.mActionButtonView.animate().scaleX(0.9F).scaleY(0.9F);
      }
      this.mActionButtonView.animate().alpha(0.0F).setDuration(paramInt).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new Runnable()
      {
        public void run()
        {
          if (paramAnimatorListener != null) {
            paramAnimatorListener.onAnimationEnd(null);
          }
          TaskView.-get0(TaskView.this).setVisibility(4);
        }
      }).start();
    }
    do
    {
      return;
      this.mActionButtonView.setAlpha(0.0F);
      this.mActionButtonView.setVisibility(4);
    } while (paramAnimatorListener == null);
    paramAnimatorListener.onAnimationEnd(null);
  }
  
  boolean isAnimatingTo(TaskViewTransform paramTaskViewTransform)
  {
    if ((this.mTransformAnimation != null) && (this.mTransformAnimation.isStarted())) {
      return this.mTargetAnimationTransform.isSame(paramTaskViewTransform);
    }
    return false;
  }
  
  public boolean isLocked()
  {
    if (this.mTask != null) {
      return this.mTask.isLocked;
    }
    return false;
  }
  
  protected void measureContents(int paramInt1, int paramInt2)
  {
    int i = this.mPaddingLeft;
    int j = this.mPaddingRight;
    int k = this.mPaddingTop;
    int m = this.mPaddingBottom;
    measureChildren(View.MeasureSpec.makeMeasureSpec(paramInt1 - i - j, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2 - k - m, 1073741824));
    setMeasuredDimension(paramInt1, paramInt2);
  }
  
  public final void onBusEvent(DragEndCancelledEvent paramDragEndCancelledEvent)
  {
    paramDragEndCancelledEvent.addPostAnimationCallback(new -void_onBusEvent_com_android_systemui_recents_events_ui_dragndrop_DragEndCancelledEvent_event_LambdaImpl0());
  }
  
  public final void onBusEvent(DragEndEvent paramDragEndEvent)
  {
    if (!(paramDragEndEvent.dropTarget instanceof TaskStack.DockState)) {
      paramDragEndEvent.addPostAnimationCallback(new -void_onBusEvent_com_android_systemui_recents_events_ui_dragndrop_DragEndEvent_event_LambdaImpl0());
    }
    EventBus.getDefault().unregister(this);
  }
  
  public void onClick(View paramView)
  {
    if (this.mIsDisabledInSafeMode)
    {
      paramView = getContext();
      String str = paramView.getString(2131690330, new Object[] { this.mTask.title });
      if (this.mDisabledAppToast != null) {
        this.mDisabledAppToast.cancel();
      }
      this.mDisabledAppToast = Toast.makeText(paramView, str, 0);
      this.mDisabledAppToast.show();
      return;
    }
    boolean bool = false;
    if (paramView == this.mActionButtonView)
    {
      this.mActionButtonView.setTranslationZ(0.0F);
      bool = true;
    }
    Log.d("TaskView", "onClick to " + this.mTask.key.id + ", " + this.mTask.key.getComponent().toString());
    EventBus.getDefault().send(new LaunchTaskEvent(this, this.mTask, null, -1, bool));
    MetricsLogger.action(paramView.getContext(), 277, this.mTask.key.getComponent().toString());
  }
  
  void onConfigurationChanged()
  {
    this.mHeaderView.onConfigurationChanged();
  }
  
  protected void onFinishInflate()
  {
    this.mHeaderView = ((TaskViewHeader)findViewById(2131952193));
    this.mThumbnailView = ((TaskViewThumbnail)findViewById(2131952189));
    this.mThumbnailView.updateClipToTaskBar(this.mHeaderView);
    this.mActionButtonView = findViewById(2131952190);
    this.mActionButtonView.setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setOval(0, 0, TaskView.-get0(TaskView.this).getWidth(), TaskView.-get0(TaskView.this).getHeight());
        paramAnonymousOutline.setAlpha(0.35F);
      }
    });
    this.mActionButtonView.setOnClickListener(this);
    this.mActionButtonTranslationZ = this.mActionButtonView.getTranslationZ();
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 0) {
      this.mDownTouchPos.set((int)(paramMotionEvent.getX() * getScaleX()), (int)(paramMotionEvent.getY() * getScaleY()));
    }
    return super.onInterceptTouchEvent(paramMotionEvent);
  }
  
  public boolean onLongClick(View paramView)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    Rect localRect = new Rect(this.mViewBounds.mClipBounds);
    localRect.scale(getScaleX());
    boolean bool = localRect.contains(this.mDownTouchPos.x, this.mDownTouchPos.y);
    if ((paramView != this) || (!bool) || (localSystemServicesProxy.hasDockedTask())) {
      return false;
    }
    setClipViewInStack(false);
    paramView = this.mDownTouchPos;
    paramView.x = ((int)(paramView.x + (1.0F - getScaleX()) * getWidth() / 2.0F));
    paramView = this.mDownTouchPos;
    paramView.y = ((int)(paramView.y + (1.0F - getScaleY()) * getHeight() / 2.0F));
    EventBus.getDefault().register(this, 3);
    EventBus.getDefault().send(new DragStartEvent(this.mTask, this, this.mDownTouchPos));
    return true;
  }
  
  public void onPrepareLaunchTargetForEnterAnimation()
  {
    setDimAlphaWithoutHeader(0.0F);
    this.mActionButtonView.setAlpha(0.0F);
    if ((this.mIncompatibleAppToastView != null) && (this.mIncompatibleAppToastView.getVisibility() == 0)) {
      this.mIncompatibleAppToastView.setAlpha(0.0F);
    }
  }
  
  void onReload(boolean paramBoolean)
  {
    if (!paramBoolean) {
      resetViewProperties();
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramInt1 > 0) && (paramInt2 > 0))
    {
      this.mHeaderView.onTaskViewSizeChanged(paramInt1, paramInt2);
      this.mThumbnailView.onTaskViewSizeChanged(paramInt1, paramInt2);
      this.mActionButtonView.setTranslationX(paramInt1 - getMeasuredWidth());
      this.mActionButtonView.setTranslationY(paramInt2 - getMeasuredHeight());
    }
  }
  
  public void onStartFrontTaskEnterAnimation(boolean paramBoolean)
  {
    if (paramBoolean) {
      showActionButton(false, 0);
    }
  }
  
  public void onStartLaunchTargetEnterAnimation(TaskViewTransform paramTaskViewTransform, int paramInt, boolean paramBoolean, ReferenceCountedTrigger paramReferenceCountedTrigger)
  {
    Utilities.cancelAnimationWithoutCallbacks(this.mDimAnimator);
    paramReferenceCountedTrigger.increment();
    this.mDimAnimator = ((ObjectAnimator)new AnimationProps(paramInt, Interpolators.ALPHA_OUT).apply(7, ObjectAnimator.ofFloat(this, DIM_ALPHA_WITHOUT_HEADER, new float[] { getDimAlpha(), paramTaskViewTransform.dimAlpha })));
    this.mDimAnimator.addListener(paramReferenceCountedTrigger.decrementOnAnimationEnd());
    this.mDimAnimator.start();
    if (paramBoolean) {
      showActionButton(true, paramInt);
    }
    if ((this.mIncompatibleAppToastView != null) && (this.mIncompatibleAppToastView.getVisibility() == 0)) {
      this.mIncompatibleAppToastView.animate().alpha(1.0F).setDuration(paramInt).setInterpolator(Interpolators.ALPHA_IN).start();
    }
  }
  
  public void onStartLaunchTargetLaunchAnimation(int paramInt, boolean paramBoolean, ReferenceCountedTrigger paramReferenceCountedTrigger)
  {
    Utilities.cancelAnimationWithoutCallbacks(this.mDimAnimator);
    this.mDimAnimator = ((ObjectAnimator)new AnimationProps(paramInt, Interpolators.ALPHA_OUT).apply(7, ObjectAnimator.ofFloat(this, DIM_ALPHA, new float[] { getDimAlpha(), 0.0F })));
    this.mDimAnimator.start();
    paramReferenceCountedTrigger.increment();
    if (paramBoolean) {}
    for (paramBoolean = false;; paramBoolean = true)
    {
      hideActionButton(true, paramInt, paramBoolean, paramReferenceCountedTrigger.decrementOnAnimationEnd());
      return;
    }
  }
  
  public void onTaskBound(Task paramTask, boolean paramBoolean, int paramInt, Rect paramRect)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    this.mTouchExplorationEnabled = paramBoolean;
    this.mTask = paramTask;
    this.mTask.addCallback(this);
    if (!this.mTask.isSystemApp)
    {
      paramBoolean = localSystemServicesProxy.isInSafeMode();
      this.mIsDisabledInSafeMode = paramBoolean;
      this.mThumbnailView.bindToTask(this.mTask, this.mIsDisabledInSafeMode, paramInt, paramRect);
      this.mHeaderView.bindToTask(this.mTask, this.mTouchExplorationEnabled, this.mIsDisabledInSafeMode);
      if ((paramTask.isDockable) || (!localSystemServicesProxy.hasDockedTask())) {
        break label153;
      }
      if (this.mIncompatibleAppToastView == null)
      {
        this.mIncompatibleAppToastView = Utilities.findViewStubById(this, 2131952191).inflate();
        paramTask = (TextView)findViewById(16908299);
        if (paramTask != null) {
          paramTask.setText(2131690332);
        }
      }
      this.mIncompatibleAppToastView.setVisibility(0);
    }
    label153:
    while (this.mIncompatibleAppToastView == null)
    {
      return;
      paramBoolean = false;
      break;
    }
    this.mIncompatibleAppToastView.setVisibility(4);
  }
  
  public void onTaskDataLoaded(Task paramTask, ActivityManager.TaskThumbnailInfo paramTaskThumbnailInfo)
  {
    this.mThumbnailView.onTaskDataLoaded(paramTaskThumbnailInfo);
    this.mHeaderView.onTaskDataLoaded();
  }
  
  public void onTaskDataUnloaded()
  {
    this.mTask.removeCallback(this);
    this.mThumbnailView.unbindFromTask();
    this.mHeaderView.unbindFromTask(this.mTouchExplorationEnabled);
  }
  
  public void onTaskStackIdChanged()
  {
    this.mHeaderView.bindToTask(this.mTask, this.mTouchExplorationEnabled, this.mIsDisabledInSafeMode);
    this.mHeaderView.onTaskDataLoaded();
  }
  
  void resetViewProperties()
  {
    cancelTransformAnimation();
    setDimAlpha(0.0F);
    setVisibility(0);
    getViewBounds().reset();
    getHeaderView().reset();
    TaskViewTransform.reset(this);
    this.mActionButtonView.setScaleX(1.0F);
    this.mActionButtonView.setScaleY(1.0F);
    this.mActionButtonView.setAlpha(0.0F);
    this.mActionButtonView.setTranslationX(0.0F);
    this.mActionButtonView.setTranslationY(0.0F);
    this.mActionButtonView.setTranslationZ(this.mActionButtonTranslationZ);
    if (this.mIncompatibleAppToastView != null) {
      this.mIncompatibleAppToastView.setVisibility(4);
    }
  }
  
  void setCallbacks(TaskViewCallbacks paramTaskViewCallbacks)
  {
    this.mCb = paramTaskViewCallbacks;
  }
  
  void setClipViewInStack(boolean paramBoolean)
  {
    if (paramBoolean != this.mClipViewInStack)
    {
      this.mClipViewInStack = paramBoolean;
      if (this.mCb != null) {
        this.mCb.onTaskViewClipStateChanged(this);
      }
    }
  }
  
  public void setDimAlpha(float paramFloat)
  {
    this.mDimAlpha = paramFloat;
    this.mThumbnailView.setDimAlpha(paramFloat);
    this.mHeaderView.setDimAlpha(paramFloat);
  }
  
  public void setDimAlphaWithoutHeader(float paramFloat)
  {
    this.mDimAlpha = paramFloat;
    this.mThumbnailView.setDimAlpha(paramFloat);
  }
  
  public void setFocusedState(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {
      if ((paramBoolean2) && (!isFocused())) {}
    }
    while ((!isAccessibilityFocused()) || (!this.mTouchExplorationEnabled))
    {
      return;
      requestFocus();
      return;
    }
    clearAccessibilityFocus();
  }
  
  void setTouchEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (TaskView localTaskView = this;; localTaskView = null)
    {
      setOnClickListener(localTaskView);
      return;
    }
  }
  
  boolean shouldClipViewInStack()
  {
    if ((this.mTask.isFreeformTask()) || (getVisibility() != 0)) {
      return false;
    }
    return this.mClipViewInStack;
  }
  
  public void showActionButton(boolean paramBoolean, int paramInt)
  {
    this.mActionButtonView.setVisibility(0);
    if ((paramBoolean) && (this.mActionButtonView.getAlpha() < 1.0F))
    {
      this.mActionButtonView.animate().alpha(1.0F).scaleX(1.0F).scaleY(1.0F).setDuration(paramInt).setInterpolator(Interpolators.ALPHA_IN).start();
      return;
    }
    this.mActionButtonView.setScaleX(1.0F);
    this.mActionButtonView.setScaleY(1.0F);
    this.mActionButtonView.setAlpha(1.0F);
    this.mActionButtonView.setTranslationZ(this.mActionButtonTranslationZ);
  }
  
  void startNoUserInteractionAnimation()
  {
    this.mHeaderView.startNoUserInteractionAnimation();
  }
  
  void updateViewPropertiesToTaskTransform(TaskViewTransform paramTaskViewTransform, AnimationProps paramAnimationProps, ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    Object localObject = Recents.getConfiguration();
    cancelTransformAnimation();
    this.mTmpAnimators.clear();
    ArrayList localArrayList = this.mTmpAnimators;
    if (((RecentsConfiguration)localObject).fakeShadows) {}
    for (boolean bool = false;; bool = true)
    {
      paramTaskViewTransform.applyToTaskView(this, localArrayList, paramAnimationProps, bool);
      if (!paramAnimationProps.isImmediate()) {
        break;
      }
      if (Float.compare(getDimAlpha(), paramTaskViewTransform.dimAlpha) != 0) {
        setDimAlpha(paramTaskViewTransform.dimAlpha);
      }
      if (Float.compare(this.mViewBounds.getAlpha(), paramTaskViewTransform.viewOutlineAlpha) != 0) {
        this.mViewBounds.setAlpha(paramTaskViewTransform.viewOutlineAlpha);
      }
      if (paramAnimationProps.getListener() != null) {
        paramAnimationProps.getListener().onAnimationEnd(null);
      }
      if (paramAnimatorUpdateListener != null) {
        paramAnimatorUpdateListener.onAnimationUpdate(null);
      }
      return;
    }
    if (Float.compare(getDimAlpha(), paramTaskViewTransform.dimAlpha) != 0)
    {
      this.mDimAnimator = ObjectAnimator.ofFloat(this, DIM_ALPHA, new float[] { getDimAlpha(), paramTaskViewTransform.dimAlpha });
      this.mTmpAnimators.add(paramAnimationProps.apply(6, this.mDimAnimator));
    }
    if (Float.compare(this.mViewBounds.getAlpha(), paramTaskViewTransform.viewOutlineAlpha) != 0)
    {
      this.mOutlineAnimator = ObjectAnimator.ofFloat(this, VIEW_OUTLINE_ALPHA, new float[] { this.mViewBounds.getAlpha(), paramTaskViewTransform.viewOutlineAlpha });
      this.mTmpAnimators.add(paramAnimationProps.apply(6, this.mOutlineAnimator));
    }
    if (paramAnimatorUpdateListener != null)
    {
      localObject = ValueAnimator.ofInt(new int[] { 0, 1 });
      ((ValueAnimator)localObject).addUpdateListener(paramAnimatorUpdateListener);
      this.mTmpAnimators.add(paramAnimationProps.apply(6, (ValueAnimator)localObject));
    }
    this.mTransformAnimation = paramAnimationProps.createAnimator(this.mTmpAnimators);
    this.mTransformAnimation.start();
    this.mTargetAnimationTransform.copyFrom(paramTaskViewTransform);
  }
  
  static abstract interface TaskViewCallbacks
  {
    public abstract void onTaskViewClipStateChanged(TaskView paramTaskView);
    
    public abstract void showDismissAllButton();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\TaskView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */