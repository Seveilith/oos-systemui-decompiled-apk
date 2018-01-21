package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.session.MediaSessionLegacyHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.BoostFramework;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ActionMode.Callback2;
import android.view.InputQueue.Callback;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder.Callback2;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.internal.view.FloatingActionMode;
import com.android.internal.widget.FloatingToolbar;
import com.android.systemui.R.styleable;
import com.android.systemui.classifier.FalsingManager;
import com.android.systemui.plugin.LSState;
import com.android.systemui.plugin.PreventModeCtrl;
import com.android.systemui.statusbar.DragDownHelper;
import com.android.systemui.statusbar.stack.NotificationStackScrollLayout;
import com.android.systemui.util.Utils;

public class StatusBarWindowView
  extends FrameLayout
{
  int[] lBoostCpuParamVal;
  int[] lBoostPackParamVal;
  int lBoostTimeOut = 0;
  int lDisPackTimeOut = 0;
  private View mBrightnessMirror;
  private DragDownHelper mDragDownHelper;
  private boolean mDragDownTracking = false;
  private Window mFakeWindow = new Window(this.mContext)
  {
    public void addContentView(View paramAnonymousView, ViewGroup.LayoutParams paramAnonymousLayoutParams) {}
    
    public void alwaysReadCloseOnTouchAttr() {}
    
    public void clearContentView() {}
    
    public void closeAllPanels() {}
    
    public void closePanel(int paramAnonymousInt) {}
    
    public View getCurrentFocus()
    {
      return null;
    }
    
    public View getDecorView()
    {
      return StatusBarWindowView.this;
    }
    
    public LayoutInflater getLayoutInflater()
    {
      return null;
    }
    
    public int getNavigationBarColor()
    {
      return 0;
    }
    
    public int getStatusBarColor()
    {
      return 0;
    }
    
    public int getVolumeControlStream()
    {
      return 0;
    }
    
    public void invalidatePanelMenu(int paramAnonymousInt) {}
    
    public boolean isFloating()
    {
      return false;
    }
    
    public boolean isShortcutKey(int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      return false;
    }
    
    protected void onActive() {}
    
    public void onConfigurationChanged(Configuration paramAnonymousConfiguration) {}
    
    public void onMultiWindowModeChanged() {}
    
    public void openPanel(int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent) {}
    
    public View peekDecorView()
    {
      return null;
    }
    
    public boolean performContextMenuIdentifierAction(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      return false;
    }
    
    public boolean performPanelIdentifierAction(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      return false;
    }
    
    public boolean performPanelShortcut(int paramAnonymousInt1, int paramAnonymousInt2, KeyEvent paramAnonymousKeyEvent, int paramAnonymousInt3)
    {
      return false;
    }
    
    public void reportActivityRelaunched() {}
    
    public void restoreHierarchyState(Bundle paramAnonymousBundle) {}
    
    public Bundle saveHierarchyState()
    {
      return null;
    }
    
    public void setBackgroundDrawable(Drawable paramAnonymousDrawable) {}
    
    public void setChildDrawable(int paramAnonymousInt, Drawable paramAnonymousDrawable) {}
    
    public void setChildInt(int paramAnonymousInt1, int paramAnonymousInt2) {}
    
    public void setContentView(int paramAnonymousInt) {}
    
    public void setContentView(View paramAnonymousView) {}
    
    public void setContentView(View paramAnonymousView, ViewGroup.LayoutParams paramAnonymousLayoutParams) {}
    
    public void setDecorCaptionShade(int paramAnonymousInt) {}
    
    public void setFeatureDrawable(int paramAnonymousInt, Drawable paramAnonymousDrawable) {}
    
    public void setFeatureDrawableAlpha(int paramAnonymousInt1, int paramAnonymousInt2) {}
    
    public void setFeatureDrawableResource(int paramAnonymousInt1, int paramAnonymousInt2) {}
    
    public void setFeatureDrawableUri(int paramAnonymousInt, Uri paramAnonymousUri) {}
    
    public void setFeatureInt(int paramAnonymousInt1, int paramAnonymousInt2) {}
    
    public void setNavigationBarColor(int paramAnonymousInt) {}
    
    public void setResizingCaptionDrawable(Drawable paramAnonymousDrawable) {}
    
    public void setStatusBarColor(int paramAnonymousInt) {}
    
    public void setTitle(CharSequence paramAnonymousCharSequence) {}
    
    public void setTitleColor(int paramAnonymousInt) {}
    
    public void setVolumeControlStream(int paramAnonymousInt) {}
    
    public boolean superDispatchGenericMotionEvent(MotionEvent paramAnonymousMotionEvent)
    {
      return false;
    }
    
    public boolean superDispatchKeyEvent(KeyEvent paramAnonymousKeyEvent)
    {
      return false;
    }
    
    public boolean superDispatchKeyShortcutEvent(KeyEvent paramAnonymousKeyEvent)
    {
      return false;
    }
    
    public boolean superDispatchTouchEvent(MotionEvent paramAnonymousMotionEvent)
    {
      return false;
    }
    
    public boolean superDispatchTrackballEvent(MotionEvent paramAnonymousMotionEvent)
    {
      return false;
    }
    
    public void takeInputQueue(InputQueue.Callback paramAnonymousCallback) {}
    
    public void takeKeyEvents(boolean paramAnonymousBoolean) {}
    
    public void takeSurface(SurfaceHolder.Callback2 paramAnonymousCallback2) {}
    
    public void togglePanel(int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent) {}
  };
  private FalsingManager mFalsingManager;
  private ActionMode mFloatingActionMode;
  private View mFloatingActionModeOriginatingView;
  private FloatingToolbar mFloatingToolbar;
  private ViewTreeObserver.OnPreDrawListener mFloatingToolbarPreDrawListener;
  boolean mIsPerfBoostEnabled = false;
  boolean mIsperfDisablepackingEnable = false;
  private int mLeftInset = 0;
  private NotificationPanelView mNotificationPanel;
  BoostFramework mPerfBoost = null;
  BoostFramework mPerfIop = null;
  BoostFramework mPerfPack = null;
  private int mRightInset = 0;
  private PhoneStatusBar mService;
  private NotificationStackScrollLayout mStackScrollLayout;
  private final Paint mTransparentSrcPaint = new Paint();
  
  public StatusBarWindowView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setMotionEventSplittingEnabled(false);
    this.mTransparentSrcPaint.setColor(0);
    this.mTransparentSrcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    this.mFalsingManager = FalsingManager.getInstance(paramContext);
    paramContext = paramContext.getResources();
    this.mIsPerfBoostEnabled = paramContext.getBoolean(17957046);
    this.mIsperfDisablepackingEnable = paramContext.getBoolean(17957049);
    if (this.mIsPerfBoostEnabled)
    {
      this.lBoostTimeOut = paramContext.getInteger(17694890);
      this.lBoostCpuParamVal = paramContext.getIntArray(17236043);
    }
    if (this.mIsperfDisablepackingEnable)
    {
      this.lDisPackTimeOut = paramContext.getInteger(17694893);
      this.lBoostPackParamVal = paramContext.getIntArray(17236047);
    }
  }
  
  private void applyMargins()
  {
    this.mService.mScrimController.setLeftInset(this.mLeftInset);
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if ((localView.getLayoutParams() instanceof LayoutParams))
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if ((!localLayoutParams.ignoreRightInset) && ((localLayoutParams.rightMargin != this.mRightInset) || (localLayoutParams.leftMargin != this.mLeftInset)))
        {
          localLayoutParams.rightMargin = this.mRightInset;
          localLayoutParams.leftMargin = this.mLeftInset;
          localView.requestLayout();
        }
      }
      i += 1;
    }
  }
  
  private void cleanupFloatingActionModeViews()
  {
    if (this.mFloatingToolbar != null)
    {
      this.mFloatingToolbar.dismiss();
      this.mFloatingToolbar = null;
    }
    if (this.mFloatingActionModeOriginatingView != null)
    {
      if (this.mFloatingToolbarPreDrawListener != null)
      {
        this.mFloatingActionModeOriginatingView.getViewTreeObserver().removeOnPreDrawListener(this.mFloatingToolbarPreDrawListener);
        this.mFloatingToolbarPreDrawListener = null;
      }
      this.mFloatingActionModeOriginatingView = null;
    }
  }
  
  private ActionMode createFloatingActionMode(View paramView, final ActionMode.Callback2 paramCallback2)
  {
    if (this.mFloatingActionMode != null) {
      this.mFloatingActionMode.finish();
    }
    cleanupFloatingActionModeViews();
    paramCallback2 = new FloatingActionMode(this.mContext, paramCallback2, paramView);
    this.mFloatingActionModeOriginatingView = paramView;
    this.mFloatingToolbarPreDrawListener = new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        paramCallback2.updateViewLocationInWindow();
        return true;
      }
    };
    return paramCallback2;
  }
  
  private void setHandledFloatingActionMode(ActionMode paramActionMode)
  {
    this.mFloatingActionMode = paramActionMode;
    this.mFloatingToolbar = new FloatingToolbar(this.mContext, this.mFakeWindow);
    ((FloatingActionMode)this.mFloatingActionMode).setFloatingToolbar(this.mFloatingToolbar);
    this.mFloatingActionMode.invalidate();
    this.mFloatingActionModeOriginatingView.getViewTreeObserver().addOnPreDrawListener(this.mFloatingToolbarPreDrawListener);
  }
  
  private ActionMode startActionMode(View paramView, ActionMode.Callback paramCallback, int paramInt)
  {
    paramCallback = new ActionModeCallback2Wrapper(paramCallback);
    paramView = createFloatingActionMode(paramView, paramCallback);
    if ((paramView != null) && (paramCallback.onCreateActionMode(paramView, paramView.getMenu())))
    {
      setHandledFloatingActionMode(paramView);
      return paramView;
    }
    return null;
  }
  
  void acquirePerfLock(String paramString, int paramInt)
  {
    if ((this.mIsperfDisablepackingEnable) && (this.mPerfPack == null)) {
      this.mPerfPack = new BoostFramework();
    }
    if (this.mPerfPack != null) {
      this.mPerfPack.perfLockAcquire(this.lDisPackTimeOut, this.lBoostPackParamVal);
    }
    if ((this.mIsPerfBoostEnabled) && (this.mPerfBoost == null)) {
      this.mPerfBoost = new BoostFramework();
    }
    if (this.mPerfBoost != null) {
      this.mPerfBoost.perfLockAcquire(paramInt, this.lBoostCpuParamVal);
    }
    if (this.mPerfIop == null) {
      this.mPerfIop = new BoostFramework();
    }
    if (this.mPerfIop != null) {
      this.mPerfIop.perfIOPrefetchStart(-1, paramString);
    }
  }
  
  public void cancelExpandHelper()
  {
    if (this.mStackScrollLayout != null) {
      this.mStackScrollLayout.cancelExpandHelper();
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (this.mService.interceptMediaKey(paramKeyEvent)) {
      return true;
    }
    if (super.dispatchKeyEvent(paramKeyEvent)) {
      return true;
    }
    int i;
    if (paramKeyEvent.getAction() == 0) {
      i = 1;
    }
    switch (paramKeyEvent.getKeyCode())
    {
    default: 
    case 4: 
    case 82: 
    case 62: 
      do
      {
        return false;
        i = 0;
        break;
        if (i == 0)
        {
          this.mService.onBackPressed();
          return true;
        }
        this.mService.onBackKeyDown();
        return true;
        if (i == 0) {
          return this.mService.onMenuPressed();
        }
      } while (i != 0);
      return this.mService.onSpacePressed();
    }
    if (this.mService.isDozing())
    {
      MediaSessionLegacyHelper.getHelper(this.mContext).sendVolumeKeyEvent(paramKeyEvent, true);
      return true;
    }
    this.mService.onVolumeKeyPressed(paramKeyEvent);
    return false;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mFalsingManager.onTouchEvent(paramMotionEvent, getWidth(), getHeight());
    if ((this.mBrightnessMirror != null) && (this.mBrightnessMirror.getVisibility() == 0) && (paramMotionEvent.getActionMasked() == 5)) {
      return false;
    }
    if ((paramMotionEvent.getAction() == 0) && (this.mService.getBarState() != 0)) {
      acquirePerfLock("com.android.systemui", 3000);
    }
    if (paramMotionEvent.getActionMasked() == 0) {
      this.mStackScrollLayout.closeControlsIfOutsideTouch(paramMotionEvent);
    }
    if ((Utils.DEBUG_ONEPLUS) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 1))) {
      Log.i("StatusBarWindowView", "dispatchTouchEvent ev:" + paramMotionEvent);
    }
    return super.dispatchTouchEvent(paramMotionEvent);
  }
  
  protected boolean fitSystemWindows(Rect paramRect)
  {
    int j = 1;
    if (getFitsSystemWindows())
    {
      if (paramRect.top == getPaddingTop())
      {
        if (paramRect.bottom == getPaddingBottom()) {
          break label109;
        }
        i = 1;
      }
      for (;;)
      {
        if ((paramRect.right != this.mRightInset) || (paramRect.left != this.mLeftInset))
        {
          this.mRightInset = paramRect.right;
          this.mLeftInset = paramRect.left;
          applyMargins();
        }
        if (i != 0) {
          setPadding(0, 0, 0, 0);
        }
        paramRect.left = 0;
        paramRect.top = 0;
        paramRect.right = 0;
        return false;
        i = 1;
        continue;
        label109:
        i = 0;
      }
    }
    if ((this.mRightInset != 0) || (this.mLeftInset != 0))
    {
      this.mRightInset = 0;
      this.mLeftInset = 0;
      applyMargins();
    }
    int i = j;
    if (getPaddingLeft() == 0)
    {
      if (getPaddingRight() == 0) {
        break label179;
      }
      i = j;
    }
    for (;;)
    {
      if (i != 0) {
        setPadding(0, 0, 0, 0);
      }
      paramRect.top = 0;
      return false;
      label179:
      i = j;
      if (getPaddingTop() == 0)
      {
        i = j;
        if (getPaddingBottom() == 0) {
          i = 0;
        }
      }
    }
  }
  
  protected FrameLayout.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -1);
  }
  
  public FrameLayout.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mService.isScrimSrcModeEnabled())
    {
      IBinder localIBinder = getWindowToken();
      WindowManager.LayoutParams localLayoutParams = (WindowManager.LayoutParams)getLayoutParams();
      localLayoutParams.token = localIBinder;
      setLayoutParams(localLayoutParams);
      WindowManagerGlobal.getInstance().changeCanvasOpacity(localIBinder, true);
      setWillNotDraw(false);
      return;
    }
    setWillNotDraw(true);
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.mService.isScrimSrcModeEnabled())
    {
      int i = getHeight() - getPaddingBottom();
      int j = getWidth();
      int k = getPaddingRight();
      if (getPaddingTop() != 0) {
        paramCanvas.drawRect(0.0F, 0.0F, getWidth(), getPaddingTop(), this.mTransparentSrcPaint);
      }
      if (getPaddingBottom() != 0) {
        paramCanvas.drawRect(0.0F, i, getWidth(), getHeight(), this.mTransparentSrcPaint);
      }
      if (getPaddingLeft() != 0) {
        paramCanvas.drawRect(0.0F, getPaddingTop(), getPaddingLeft(), i, this.mTransparentSrcPaint);
      }
      if (getPaddingRight() != 0) {
        paramCanvas.drawRect(j - k, getPaddingTop(), getWidth(), i, this.mTransparentSrcPaint);
      }
    }
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mStackScrollLayout = ((NotificationStackScrollLayout)findViewById(2131952277));
    this.mNotificationPanel = ((NotificationPanelView)findViewById(2131952273));
    this.mBrightnessMirror = findViewById(2131951797);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (LSState.getInstance().getPreventModeCtrl().isPreventModeActive())
    {
      LSState.getInstance().getPreventModeCtrl().disPatchTouchEvent(paramMotionEvent);
      return true;
    }
    if (LSState.getInstance().getPhoneStatusBar().getFacelockController().isLighModeEnabled()) {
      return true;
    }
    if (LSState.getInstance().getFingerprintUnlockControl().getMode() == 5) {
      return true;
    }
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mNotificationPanel.isFullyExpanded())
    {
      bool1 = bool2;
      if (this.mStackScrollLayout.getVisibility() == 0)
      {
        bool1 = bool2;
        if (this.mService.getBarState() == 1)
        {
          if (!this.mService.isBouncerShowing()) {
            break label155;
          }
          bool1 = bool2;
        }
      }
    }
    for (;;)
    {
      if (!bool1) {
        super.onInterceptTouchEvent(paramMotionEvent);
      }
      if (bool1)
      {
        paramMotionEvent = MotionEvent.obtain(paramMotionEvent);
        paramMotionEvent.setAction(3);
        this.mStackScrollLayout.onInterceptTouchEvent(paramMotionEvent);
        this.mNotificationPanel.onInterceptTouchEvent(paramMotionEvent);
        paramMotionEvent.recycle();
      }
      return bool1;
      label155:
      bool2 = this.mDragDownHelper.onInterceptTouchEvent(paramMotionEvent);
      bool1 = bool2;
      if (paramMotionEvent.getActionMasked() == 0)
      {
        this.mService.wakeUpIfDozing(paramMotionEvent.getEventTime(), paramMotionEvent);
        bool1 = bool2;
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (LSState.getInstance().getPhoneStatusBar().getFacelockController().isLighModeEnabled()) {
      LSState.getInstance().getPhoneStatusBar().getFacelockController().disPatchTouchEvent(paramMotionEvent);
    }
    boolean bool1 = false;
    if (this.mService.getBarState() == 1) {
      bool1 = this.mDragDownHelper.onTouchEvent(paramMotionEvent);
    }
    boolean bool2 = bool1;
    if (!bool1) {
      bool2 = super.onTouchEvent(paramMotionEvent);
    }
    int i = paramMotionEvent.getAction();
    if ((!bool2) && ((i == 1) || (i == 3))) {
      this.mService.setInteracting(1, false);
    }
    return bool2;
  }
  
  public void onViewAdded(View paramView)
  {
    super.onViewAdded(paramView);
    if (paramView.getId() == 2131951797) {
      this.mBrightnessMirror = paramView;
    }
  }
  
  public void setService(PhoneStatusBar paramPhoneStatusBar)
  {
    this.mService = paramPhoneStatusBar;
    this.mDragDownHelper = new DragDownHelper(getContext(), this, this.mStackScrollLayout, this.mService);
  }
  
  public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback, int paramInt)
  {
    if (paramInt == 1) {
      return startActionMode(paramView, paramCallback, paramInt);
    }
    return super.startActionModeForChild(paramView, paramCallback, paramInt);
  }
  
  private class ActionModeCallback2Wrapper
    extends ActionMode.Callback2
  {
    private final ActionMode.Callback mWrapped;
    
    public ActionModeCallback2Wrapper(ActionMode.Callback paramCallback)
    {
      this.mWrapped = paramCallback;
    }
    
    public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
    {
      return this.mWrapped.onActionItemClicked(paramActionMode, paramMenuItem);
    }
    
    public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return this.mWrapped.onCreateActionMode(paramActionMode, paramMenu);
    }
    
    public void onDestroyActionMode(ActionMode paramActionMode)
    {
      this.mWrapped.onDestroyActionMode(paramActionMode);
      if (paramActionMode == StatusBarWindowView.-get0(StatusBarWindowView.this))
      {
        StatusBarWindowView.-wrap0(StatusBarWindowView.this);
        StatusBarWindowView.-set0(StatusBarWindowView.this, null);
      }
      StatusBarWindowView.this.requestFitSystemWindows();
    }
    
    public void onGetContentRect(ActionMode paramActionMode, View paramView, Rect paramRect)
    {
      if ((this.mWrapped instanceof ActionMode.Callback2))
      {
        ((ActionMode.Callback2)this.mWrapped).onGetContentRect(paramActionMode, paramView, paramRect);
        return;
      }
      super.onGetContentRect(paramActionMode, paramView, paramRect);
    }
    
    public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      StatusBarWindowView.this.requestFitSystemWindows();
      return this.mWrapped.onPrepareActionMode(paramActionMode, paramMenu);
    }
  }
  
  public class LayoutParams
    extends FrameLayout.LayoutParams
  {
    public boolean ignoreRightInset;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      this$1 = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.StatusBarWindowView_Layout);
      this.ignoreRightInset = StatusBarWindowView.this.getBoolean(0, false);
      StatusBarWindowView.this.recycle();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\StatusBarWindowView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */