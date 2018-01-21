package com.android.keyguard;

import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Point;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.os.Bundle;
import android.util.Slog;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.InvalidDisplayException;

public class KeyguardDisplayManager
{
  private static boolean DEBUG = false;
  private Context mContext;
  private MediaRouter mMediaRouter;
  private final MediaRouter.SimpleCallback mMediaRouterCallback = new MediaRouter.SimpleCallback()
  {
    public void onRoutePresentationDisplayChanged(MediaRouter paramAnonymousMediaRouter, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      if (KeyguardDisplayManager.-get0()) {
        Slog.d("KeyguardDisplayManager", "onRoutePresentationDisplayChanged: info=" + paramAnonymousRouteInfo);
      }
      KeyguardDisplayManager.this.updateDisplays(KeyguardDisplayManager.-get1(KeyguardDisplayManager.this));
    }
    
    public void onRouteSelected(MediaRouter paramAnonymousMediaRouter, int paramAnonymousInt, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      if (KeyguardDisplayManager.-get0()) {
        Slog.d("KeyguardDisplayManager", "onRouteSelected: type=" + paramAnonymousInt + ", info=" + paramAnonymousRouteInfo);
      }
      KeyguardDisplayManager.this.updateDisplays(KeyguardDisplayManager.-get1(KeyguardDisplayManager.this));
    }
    
    public void onRouteUnselected(MediaRouter paramAnonymousMediaRouter, int paramAnonymousInt, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      if (KeyguardDisplayManager.-get0()) {
        Slog.d("KeyguardDisplayManager", "onRouteUnselected: type=" + paramAnonymousInt + ", info=" + paramAnonymousRouteInfo);
      }
      KeyguardDisplayManager.this.updateDisplays(KeyguardDisplayManager.-get1(KeyguardDisplayManager.this));
    }
  };
  private DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener()
  {
    public void onDismiss(DialogInterface paramAnonymousDialogInterface)
    {
      KeyguardDisplayManager.this.mPresentation = null;
    }
  };
  Presentation mPresentation;
  private boolean mShowing;
  
  public KeyguardDisplayManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mMediaRouter = ((MediaRouter)this.mContext.getSystemService("media_router"));
  }
  
  public void hide()
  {
    if (this.mShowing)
    {
      if (DEBUG) {
        Slog.v("KeyguardDisplayManager", "hide");
      }
      this.mMediaRouter.removeCallback(this.mMediaRouterCallback);
      updateDisplays(false);
    }
    this.mShowing = false;
  }
  
  public void show()
  {
    if (!this.mShowing)
    {
      if (DEBUG) {
        Slog.v("KeyguardDisplayManager", "show");
      }
      this.mMediaRouter.addCallback(4, this.mMediaRouterCallback, 8);
      updateDisplays(true);
    }
    this.mShowing = true;
  }
  
  protected void updateDisplays(boolean paramBoolean)
  {
    int i = 1;
    if (paramBoolean)
    {
      localObject = this.mMediaRouter.getSelectedRoute(4);
      if (localObject != null) {
        if (((MediaRouter.RouteInfo)localObject).getPlaybackType() == 1)
        {
          if (i == 0) {
            break label193;
          }
          localObject = ((MediaRouter.RouteInfo)localObject).getPresentationDisplay();
          if ((this.mPresentation != null) && (this.mPresentation.getDisplay() != localObject))
          {
            if (DEBUG) {
              Slog.v("KeyguardDisplayManager", "Display gone: " + this.mPresentation.getDisplay());
            }
            this.mPresentation.dismiss();
            this.mPresentation = null;
          }
          if ((this.mPresentation == null) && (localObject != null))
          {
            if (DEBUG) {
              Slog.i("KeyguardDisplayManager", "Keyguard enabled on display: " + localObject);
            }
            this.mPresentation = new KeyguardPresentation(this.mContext, (Display)localObject, R.style.keyguard_presentation_theme);
            this.mPresentation.setOnDismissListener(this.mOnDismissListener);
          }
        }
      }
    }
    label193:
    while (this.mPresentation == null) {
      for (;;)
      {
        try
        {
          this.mPresentation.show();
          return;
        }
        catch (WindowManager.InvalidDisplayException localInvalidDisplayException)
        {
          Object localObject;
          Slog.w("KeyguardDisplayManager", "Invalid display:", localInvalidDisplayException);
          this.mPresentation = null;
          return;
        }
        i = 0;
        continue;
        i = 0;
        continue;
        localObject = null;
      }
    }
    this.mPresentation.dismiss();
    this.mPresentation = null;
  }
  
  private static final class KeyguardPresentation
    extends Presentation
  {
    private View mClock;
    private int mMarginLeft;
    private int mMarginTop;
    Runnable mMoveTextRunnable = new Runnable()
    {
      public void run()
      {
        int i = KeyguardDisplayManager.KeyguardPresentation.-get1(KeyguardDisplayManager.KeyguardPresentation.this);
        int j = (int)(Math.random() * (KeyguardDisplayManager.KeyguardPresentation.-get4(KeyguardDisplayManager.KeyguardPresentation.this) - KeyguardDisplayManager.KeyguardPresentation.-get0(KeyguardDisplayManager.KeyguardPresentation.this).getWidth()));
        int k = KeyguardDisplayManager.KeyguardPresentation.-get2(KeyguardDisplayManager.KeyguardPresentation.this);
        int m = (int)(Math.random() * (KeyguardDisplayManager.KeyguardPresentation.-get3(KeyguardDisplayManager.KeyguardPresentation.this) - KeyguardDisplayManager.KeyguardPresentation.-get0(KeyguardDisplayManager.KeyguardPresentation.this).getHeight()));
        KeyguardDisplayManager.KeyguardPresentation.-get0(KeyguardDisplayManager.KeyguardPresentation.this).setTranslationX(i + j);
        KeyguardDisplayManager.KeyguardPresentation.-get0(KeyguardDisplayManager.KeyguardPresentation.this).setTranslationY(k + m);
        KeyguardDisplayManager.KeyguardPresentation.-get0(KeyguardDisplayManager.KeyguardPresentation.this).postDelayed(KeyguardDisplayManager.KeyguardPresentation.this.mMoveTextRunnable, 10000L);
      }
    };
    private int mUsableHeight;
    private int mUsableWidth;
    
    public KeyguardPresentation(Context paramContext, Display paramDisplay, int paramInt)
    {
      super(paramDisplay, paramInt);
      getWindow().setType(2009);
    }
    
    protected void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      paramBundle = new Point();
      getDisplay().getSize(paramBundle);
      this.mUsableWidth = (paramBundle.x * 80 / 100);
      this.mUsableHeight = (paramBundle.y * 80 / 100);
      this.mMarginLeft = (paramBundle.x * 20 / 200);
      this.mMarginTop = (paramBundle.y * 20 / 200);
      setContentView(R.layout.keyguard_presentation);
      this.mClock = findViewById(R.id.clock);
      this.mClock.post(this.mMoveTextRunnable);
    }
    
    public void onDetachedFromWindow()
    {
      this.mClock.removeCallbacks(this.mMoveTextRunnable);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardDisplayManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */