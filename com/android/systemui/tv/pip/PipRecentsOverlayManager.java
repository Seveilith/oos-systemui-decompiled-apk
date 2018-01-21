package com.android.systemui.tv.pip;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.android.systemui.recents.misc.SystemServicesProxy;

public class PipRecentsOverlayManager
{
  private Callback mCallback;
  private boolean mHasFocusableInRecents;
  private boolean mIsPipFocusedInRecent;
  private boolean mIsPipRecentsOverlayShown;
  private boolean mIsRecentsShown;
  private View mOverlayView;
  private PipRecentsControlsView mPipControlsView;
  private PipRecentsControlsView.Listener mPipControlsViewListener = new PipRecentsControlsView.Listener()
  {
    public void onBackPressed()
    {
      if (PipRecentsOverlayManager.-get0(PipRecentsOverlayManager.this) != null) {
        PipRecentsOverlayManager.-get0(PipRecentsOverlayManager.this).onBackPressed();
      }
    }
    
    public void onClosed()
    {
      if (PipRecentsOverlayManager.-get0(PipRecentsOverlayManager.this) != null) {
        PipRecentsOverlayManager.-get0(PipRecentsOverlayManager.this).onClosed();
      }
    }
  };
  private final PipManager mPipManager = PipManager.getInstance();
  private WindowManager.LayoutParams mPipRecentsControlsViewFocusedLayoutParams;
  private WindowManager.LayoutParams mPipRecentsControlsViewLayoutParams;
  private View mRecentsView;
  private final SystemServicesProxy mSystemServicesProxy;
  private boolean mTalkBackEnabled;
  private final WindowManager mWindowManager;
  
  PipRecentsOverlayManager(Context paramContext)
  {
    this.mWindowManager = ((WindowManager)paramContext.getSystemService(WindowManager.class));
    this.mSystemServicesProxy = SystemServicesProxy.getInstance(paramContext);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mOverlayView = ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(2130968839, null);
    this.mPipControlsView = ((PipRecentsControlsView)this.mOverlayView.findViewById(2131952324));
    this.mRecentsView = this.mOverlayView.findViewById(2131952330);
    this.mRecentsView.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean) {
          PipRecentsOverlayManager.this.clearFocus();
        }
      }
    });
    this.mOverlayView.measure(0, 0);
    this.mPipRecentsControlsViewLayoutParams = new WindowManager.LayoutParams(this.mOverlayView.getMeasuredWidth(), this.mOverlayView.getMeasuredHeight(), 2008, 24, -3);
    this.mPipRecentsControlsViewLayoutParams.gravity = 49;
    this.mPipRecentsControlsViewFocusedLayoutParams = new WindowManager.LayoutParams(this.mOverlayView.getMeasuredWidth(), this.mOverlayView.getMeasuredHeight(), 2008, 0, -3);
    this.mPipRecentsControlsViewFocusedLayoutParams.gravity = 49;
  }
  
  void addPipRecentsOverlayView()
  {
    if (this.mIsPipRecentsOverlayShown) {
      return;
    }
    this.mTalkBackEnabled = this.mSystemServicesProxy.isTouchExplorationEnabled();
    View localView = this.mRecentsView;
    if (this.mTalkBackEnabled) {}
    for (int i = 0;; i = 8)
    {
      localView.setVisibility(i);
      this.mIsPipRecentsOverlayShown = true;
      this.mIsPipFocusedInRecent = true;
      this.mWindowManager.addView(this.mOverlayView, this.mPipRecentsControlsViewFocusedLayoutParams);
      return;
    }
  }
  
  public void clearFocus()
  {
    if ((this.mIsPipRecentsOverlayShown) && (this.mIsRecentsShown) && (this.mIsPipFocusedInRecent) && (this.mPipManager.isPipShown()) && (this.mHasFocusableInRecents))
    {
      this.mIsPipFocusedInRecent = false;
      this.mPipControlsView.startFocusLossAnimation();
      this.mWindowManager.updateViewLayout(this.mOverlayView, this.mPipRecentsControlsViewLayoutParams);
      this.mPipManager.resizePinnedStack(3);
      if (this.mCallback != null) {
        this.mCallback.onRecentsFocused();
      }
      return;
    }
  }
  
  boolean isRecentsShown()
  {
    return this.mIsRecentsShown;
  }
  
  void onConfigurationChanged(Context paramContext)
  {
    if (this.mIsRecentsShown) {
      Log.w("PipRecentsOverlayManager", "Configuration is changed while Recents is shown");
    }
    initViews(paramContext);
  }
  
  public void onRecentsPaused()
  {
    this.mIsRecentsShown = false;
    this.mIsPipFocusedInRecent = false;
    removePipRecentsOverlayView();
    if (this.mPipManager.isPipShown()) {
      this.mPipManager.resizePinnedStack(1);
    }
  }
  
  public void onRecentsResumed()
  {
    if (!this.mPipManager.isPipShown()) {
      return;
    }
    this.mIsRecentsShown = true;
    this.mIsPipFocusedInRecent = true;
    this.mPipManager.resizePinnedStack(4);
  }
  
  public void removePipRecentsOverlayView()
  {
    if (!this.mIsPipRecentsOverlayShown) {
      return;
    }
    this.mWindowManager.removeView(this.mOverlayView);
    this.mPipControlsView.reset();
    this.mIsPipRecentsOverlayShown = false;
  }
  
  public void requestFocus(boolean paramBoolean)
  {
    this.mHasFocusableInRecents = paramBoolean;
    if ((this.mIsPipRecentsOverlayShown) && (this.mIsRecentsShown) && (!this.mIsPipFocusedInRecent) && (this.mPipManager.isPipShown()))
    {
      this.mIsPipFocusedInRecent = true;
      this.mPipControlsView.startFocusGainAnimation();
      this.mWindowManager.updateViewLayout(this.mOverlayView, this.mPipRecentsControlsViewFocusedLayoutParams);
      this.mPipManager.resizePinnedStack(4);
      if (this.mTalkBackEnabled)
      {
        this.mPipControlsView.requestFocus();
        this.mPipControlsView.sendAccessibilityEvent(8);
      }
      return;
    }
  }
  
  public void setCallback(Callback paramCallback)
  {
    Object localObject = null;
    this.mCallback = paramCallback;
    PipRecentsControlsView localPipRecentsControlsView = this.mPipControlsView;
    paramCallback = (Callback)localObject;
    if (this.mCallback != null) {
      paramCallback = this.mPipControlsViewListener;
    }
    localPipRecentsControlsView.setListener(paramCallback);
  }
  
  public static abstract interface Callback
  {
    public abstract void onBackPressed();
    
    public abstract void onClosed();
    
    public abstract void onRecentsFocused();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tv\pip\PipRecentsOverlayManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */