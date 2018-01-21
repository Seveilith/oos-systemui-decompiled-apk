package com.android.systemui.screenshot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings.System;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import com.android.systemui.SystemUI;

class GlobalScreenshot
{
  private static Bitmap sScreenBitmap;
  private ImageView mBackgroundView;
  private float mBgPadding;
  private float mBgPaddingScale;
  private MediaActionSound mCameraSound;
  private Context mContext;
  private Display mDisplay;
  private Matrix mDisplayMatrix;
  private DisplayMetrics mDisplayMetrics;
  private int mNotificationIconSize;
  private NotificationManager mNotificationManager;
  private final int mPreviewHeight;
  private final int mPreviewWidth;
  private AsyncTask<Void, Void, Void> mSaveInBgTask;
  private Bitmap mScreenBitmap;
  private AnimatorSet mScreenshotAnimation;
  private ImageView mScreenshotFlash;
  private View mScreenshotLayout;
  private ScreenshotSelectorView mScreenshotSelectorView;
  private ImageView mScreenshotView;
  private WindowManager.LayoutParams mWindowLayoutParams;
  private WindowManager mWindowManager;
  
  public GlobalScreenshot(Context paramContext)
  {
    Resources localResources = paramContext.getResources();
    this.mContext = paramContext;
    LayoutInflater localLayoutInflater = (LayoutInflater)paramContext.getSystemService("layout_inflater");
    this.mDisplayMatrix = new Matrix();
    this.mScreenshotLayout = localLayoutInflater.inflate(2130968627, null);
    this.mBackgroundView = ((ImageView)this.mScreenshotLayout.findViewById(2131951834));
    this.mScreenshotView = ((ImageView)this.mScreenshotLayout.findViewById(2131951835));
    this.mScreenshotFlash = ((ImageView)this.mScreenshotLayout.findViewById(2131951836));
    this.mScreenshotSelectorView = ((ScreenshotSelectorView)this.mScreenshotLayout.findViewById(2131951837));
    this.mScreenshotLayout.setFocusable(true);
    this.mScreenshotSelectorView.setFocusable(true);
    this.mScreenshotSelectorView.setFocusableInTouchMode(true);
    this.mScreenshotLayout.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.mWindowLayoutParams = new WindowManager.LayoutParams(-1, -1, 0, 0, 2036, 17302784, -3);
    this.mWindowLayoutParams.setTitle("ScreenshotAnimation");
    this.mWindowManager = ((WindowManager)paramContext.getSystemService("window"));
    this.mNotificationManager = ((NotificationManager)paramContext.getSystemService("notification"));
    this.mDisplay = this.mWindowManager.getDefaultDisplay();
    this.mDisplayMetrics = new DisplayMetrics();
    this.mDisplay.getRealMetrics(this.mDisplayMetrics);
    this.mNotificationIconSize = localResources.getDimensionPixelSize(17104902);
    this.mBgPadding = localResources.getDimensionPixelSize(2131755381);
    this.mBgPaddingScale = (this.mBgPadding / this.mDisplayMetrics.widthPixels);
    int i = 0;
    try
    {
      j = localResources.getDimensionPixelSize(2131755401);
      i = j;
    }
    catch (Resources.NotFoundException paramContext)
    {
      int j;
      for (;;) {}
    }
    j = i;
    if (i <= 0) {
      j = this.mDisplayMetrics.widthPixels;
    }
    this.mPreviewWidth = j;
    this.mPreviewHeight = localResources.getDimensionPixelSize(2131755371);
    this.mCameraSound = new MediaActionSound();
    this.mCameraSound.load(0);
  }
  
  private ValueAnimator createScreenshotDropInAnimation()
  {
    final Interpolator local6 = new Interpolator()
    {
      public float getInterpolation(float paramAnonymousFloat)
      {
        if (paramAnonymousFloat <= 0.60465115F) {
          return (float)Math.sin(paramAnonymousFloat / 0.60465115F * 3.141592653589793D);
        }
        return 0.0F;
      }
    };
    final Interpolator local7 = new Interpolator()
    {
      public float getInterpolation(float paramAnonymousFloat)
      {
        if (paramAnonymousFloat < 0.30232558F) {
          return 0.0F;
        }
        return (paramAnonymousFloat - 0.60465115F) / 0.39534885F;
      }
    };
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    localValueAnimator.setDuration(430L);
    localValueAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        GlobalScreenshot.-get6(GlobalScreenshot.this).setVisibility(8);
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        GlobalScreenshot.-get0(GlobalScreenshot.this).setAlpha(0.0F);
        GlobalScreenshot.-get0(GlobalScreenshot.this).setVisibility(0);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setAlpha(0.0F);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setTranslationX(0.0F);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setTranslationY(0.0F);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setScaleX(GlobalScreenshot.-get1(GlobalScreenshot.this) + 1.0F);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setScaleY(GlobalScreenshot.-get1(GlobalScreenshot.this) + 1.0F);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setVisibility(0);
        GlobalScreenshot.-get6(GlobalScreenshot.this).setAlpha(0.0F);
        GlobalScreenshot.-get6(GlobalScreenshot.this).setVisibility(0);
      }
    });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f1 = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        float f2 = GlobalScreenshot.-get1(GlobalScreenshot.this) + 1.0F - local7.getInterpolation(f1) * 0.27499998F;
        GlobalScreenshot.-get0(GlobalScreenshot.this).setAlpha(local7.getInterpolation(f1) * 0.5F);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setAlpha(f1);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setScaleX(f2);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setScaleY(f2);
        GlobalScreenshot.-get6(GlobalScreenshot.this).setAlpha(local6.getInterpolation(f1));
      }
    });
    return localValueAnimator;
  }
  
  private ValueAnimator createScreenshotDropOutAnimation(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    localValueAnimator.setStartDelay(500L);
    localValueAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        GlobalScreenshot.-get0(GlobalScreenshot.this).setVisibility(8);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setVisibility(8);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setLayerType(0, null);
      }
    });
    if ((paramBoolean1) && (paramBoolean2))
    {
      final Interpolator local12 = new Interpolator()
      {
        public float getInterpolation(float paramAnonymousFloat)
        {
          if (paramAnonymousFloat < 0.8604651F) {
            return (float)(1.0D - Math.pow(1.0F - paramAnonymousFloat / 0.8604651F, 2.0D));
          }
          return 1.0F;
        }
      };
      float f1 = (paramInt1 - this.mBgPadding * 2.0F) / 2.0F;
      float f2 = (paramInt2 - this.mBgPadding * 2.0F) / 2.0F;
      final PointF localPointF = new PointF(-f1 + 0.45F * f1, -f2 + 0.45F * f2);
      localValueAnimator.setDuration(430L);
      localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          float f1 = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
          float f2 = GlobalScreenshot.-get1(GlobalScreenshot.this) + 0.725F - local12.getInterpolation(f1) * 0.27500004F;
          GlobalScreenshot.-get0(GlobalScreenshot.this).setAlpha((1.0F - f1) * 0.5F);
          GlobalScreenshot.-get9(GlobalScreenshot.this).setAlpha(1.0F - local12.getInterpolation(f1));
          GlobalScreenshot.-get9(GlobalScreenshot.this).setScaleX(f2);
          GlobalScreenshot.-get9(GlobalScreenshot.this).setScaleY(f2);
          GlobalScreenshot.-get9(GlobalScreenshot.this).setTranslationX(localPointF.x * f1);
          GlobalScreenshot.-get9(GlobalScreenshot.this).setTranslationY(localPointF.y * f1);
        }
      });
      return localValueAnimator;
    }
    localValueAnimator.setDuration(320L);
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f1 = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        float f2 = GlobalScreenshot.-get1(GlobalScreenshot.this) + 0.725F - 0.125F * f1;
        GlobalScreenshot.-get0(GlobalScreenshot.this).setAlpha((1.0F - f1) * 0.5F);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setAlpha(1.0F - f1);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setScaleX(f2);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setScaleY(f2);
      }
    });
    return localValueAnimator;
  }
  
  private float getDegreesForRotation(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0.0F;
    case 1: 
      return 270.0F;
    case 2: 
      return 180.0F;
    }
    return 90.0F;
  }
  
  static void notifyScreenshotError(Context paramContext, NotificationManager paramNotificationManager, int paramInt)
  {
    Object localObject = paramContext.getResources();
    String str = ((Resources)localObject).getString(paramInt);
    localObject = new Notification.Builder(paramContext).setTicker(((Resources)localObject).getString(2131690076)).setContentTitle(((Resources)localObject).getString(2131690076)).setContentText(str).setSmallIcon(2130838386).setWhen(System.currentTimeMillis()).setVisibility(1).setCategory("err").setAutoCancel(true).setColor(paramContext.getColor(17170523));
    SystemUI.overrideNotificationAppName(paramContext, (Notification.Builder)localObject);
    paramNotificationManager.notify(2131951669, new Notification.BigTextStyle((Notification.Builder)localObject).bigText(str).build());
  }
  
  private void startAnimation(final Runnable paramRunnable, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mScreenshotView.setImageBitmap(this.mScreenBitmap);
    this.mScreenshotLayout.requestFocus();
    if (this.mScreenshotAnimation != null)
    {
      if (this.mScreenshotAnimation.isStarted()) {
        this.mScreenshotAnimation.end();
      }
      this.mScreenshotAnimation.removeAllListeners();
    }
    this.mWindowManager.addView(this.mScreenshotLayout, this.mWindowLayoutParams);
    ValueAnimator localValueAnimator1 = createScreenshotDropInAnimation();
    ValueAnimator localValueAnimator2 = createScreenshotDropOutAnimation(paramInt1, paramInt2, paramBoolean1, paramBoolean2);
    this.mScreenshotAnimation = new AnimatorSet();
    this.mScreenshotAnimation.playSequentially(new Animator[] { localValueAnimator1, localValueAnimator2 });
    this.mScreenshotAnimation.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        GlobalScreenshot.-set2(GlobalScreenshot.-get4(GlobalScreenshot.this));
        paramAnonymousAnimator = new Intent(GlobalScreenshot.-get3(GlobalScreenshot.this), TakeScreenshotService.class);
        GlobalScreenshot.-get3(GlobalScreenshot.this).startService(paramAnonymousAnimator);
        GlobalScreenshot.-get10(GlobalScreenshot.this).removeView(GlobalScreenshot.-get7(GlobalScreenshot.this));
        GlobalScreenshot.-set0(GlobalScreenshot.this, null);
        GlobalScreenshot.-get9(GlobalScreenshot.this).setImageBitmap(null);
        GlobalScreenshot.-set1(GlobalScreenshot.this, null);
        paramRunnable.run();
      }
    });
    this.mScreenshotLayout.post(new Runnable()
    {
      public void run()
      {
        if (Settings.System.getInt(GlobalScreenshot.-get3(GlobalScreenshot.this).getContentResolver(), "oem_screenshot_sound_enable", 1) == 1) {
          GlobalScreenshot.-get2(GlobalScreenshot.this).play(0);
        }
        GlobalScreenshot.-get9(GlobalScreenshot.this).setLayerType(2, null);
        GlobalScreenshot.-get9(GlobalScreenshot.this).buildLayer();
        GlobalScreenshot.-get5(GlobalScreenshot.this).start();
      }
    });
  }
  
  public void saveScreenshot()
  {
    SaveImageInBackgroundData localSaveImageInBackgroundData = new SaveImageInBackgroundData();
    localSaveImageInBackgroundData.context = this.mContext;
    localSaveImageInBackgroundData.image = sScreenBitmap;
    localSaveImageInBackgroundData.iconSize = this.mNotificationIconSize;
    localSaveImageInBackgroundData.previewWidth = this.mPreviewWidth;
    localSaveImageInBackgroundData.previewheight = this.mPreviewHeight;
    this.mSaveInBgTask = new SaveImageInBackgroundTask(this.mContext, localSaveImageInBackgroundData, this.mNotificationManager).execute(new Void[0]);
  }
  
  void stopScreenshot()
  {
    if (this.mScreenshotSelectorView.getSelectionRect() != null)
    {
      this.mWindowManager.removeView(this.mScreenshotLayout);
      this.mScreenshotSelectorView.stopSelection();
    }
  }
  
  void takeScreenshot(Runnable paramRunnable, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mDisplay.getRealMetrics(this.mDisplayMetrics);
    takeScreenshot(paramRunnable, paramBoolean1, paramBoolean2, 0, 0, this.mDisplayMetrics.widthPixels, this.mDisplayMetrics.heightPixels);
  }
  
  void takeScreenshot(Runnable paramRunnable, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mScreenshotAnimation != null)
    {
      paramRunnable.run();
      return;
    }
    this.mDisplay.getRealMetrics(this.mDisplayMetrics);
    Object localObject = new float[2];
    localObject[0] = this.mDisplayMetrics.widthPixels;
    localObject[1] = this.mDisplayMetrics.heightPixels;
    float f = getDegreesForRotation(this.mDisplay.getRotation());
    if (f > 0.0F) {}
    for (int i = 1;; i = 0)
    {
      if (i != 0)
      {
        this.mDisplayMatrix.reset();
        this.mDisplayMatrix.preRotate(-f);
        this.mDisplayMatrix.mapPoints((float[])localObject);
        localObject[0] = Math.abs(localObject[0]);
        localObject[1] = Math.abs(localObject[1]);
      }
      this.mScreenBitmap = SurfaceControl.screenshot((int)localObject[0], (int)localObject[1]);
      if (this.mScreenBitmap != null) {
        break;
      }
      Log.d("GlobalScreenshot", "mScreenBitmap == null");
      notifyScreenshotError(this.mContext, this.mNotificationManager, 2131690079);
      paramRunnable.run();
      return;
    }
    if (i != 0)
    {
      Bitmap localBitmap = Bitmap.createBitmap(this.mDisplayMetrics.widthPixels, this.mDisplayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas(localBitmap);
      localCanvas.translate(localBitmap.getWidth() / 2, localBitmap.getHeight() / 2);
      localCanvas.rotate(f);
      localCanvas.translate(-localObject[0] / 2.0F, -localObject[1] / 2.0F);
      localCanvas.drawBitmap(this.mScreenBitmap, 0.0F, 0.0F, null);
      localCanvas.setBitmap(null);
      this.mScreenBitmap.recycle();
      this.mScreenBitmap = localBitmap;
    }
    if ((paramInt3 != this.mDisplayMetrics.widthPixels) || (paramInt4 != this.mDisplayMetrics.heightPixels))
    {
      localObject = Bitmap.createBitmap(this.mScreenBitmap, paramInt1, paramInt2, paramInt3, paramInt4);
      this.mScreenBitmap.recycle();
      this.mScreenBitmap = ((Bitmap)localObject);
    }
    this.mScreenBitmap.setHasAlpha(false);
    this.mScreenBitmap.prepareToDraw();
    startAnimation(paramRunnable, this.mDisplayMetrics.widthPixels, this.mDisplayMetrics.heightPixels, paramBoolean1, paramBoolean2);
  }
  
  void takeScreenshotPartial(final Runnable paramRunnable, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    this.mWindowManager.addView(this.mScreenshotLayout, this.mWindowLayoutParams);
    this.mScreenshotSelectorView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, final MotionEvent paramAnonymousMotionEvent)
      {
        paramAnonymousView = (ScreenshotSelectorView)paramAnonymousView;
        switch (paramAnonymousMotionEvent.getAction())
        {
        default: 
          return false;
        case 0: 
          paramAnonymousView.startSelection((int)paramAnonymousMotionEvent.getX(), (int)paramAnonymousMotionEvent.getY());
          return true;
        case 2: 
          paramAnonymousView.updateSelection((int)paramAnonymousMotionEvent.getX(), (int)paramAnonymousMotionEvent.getY());
          return true;
        }
        paramAnonymousView.setVisibility(8);
        GlobalScreenshot.-get10(GlobalScreenshot.this).removeView(GlobalScreenshot.-get7(GlobalScreenshot.this));
        paramAnonymousMotionEvent = paramAnonymousView.getSelectionRect();
        if ((paramAnonymousMotionEvent != null) && (paramAnonymousMotionEvent.width() != 0) && (paramAnonymousMotionEvent.height() != 0)) {
          GlobalScreenshot.-get7(GlobalScreenshot.this).post(new Runnable()
          {
            public void run()
            {
              GlobalScreenshot.this.takeScreenshot(this.val$finisher, this.val$statusBarVisible, this.val$navBarVisible, paramAnonymousMotionEvent.left, paramAnonymousMotionEvent.top, paramAnonymousMotionEvent.width(), paramAnonymousMotionEvent.height());
            }
          });
        }
        paramAnonymousView.stopSelection();
        return true;
      }
    });
    this.mScreenshotLayout.post(new Runnable()
    {
      public void run()
      {
        GlobalScreenshot.-get8(GlobalScreenshot.this).setVisibility(0);
        GlobalScreenshot.-get8(GlobalScreenshot.this).requestFocus();
      }
    });
  }
  
  public static class DeleteScreenshotReceiver
    extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (!paramIntent.hasExtra("android:screenshot_uri_id")) {
        return;
      }
      NotificationManager localNotificationManager = (NotificationManager)paramContext.getSystemService("notification");
      paramIntent = Uri.parse(paramIntent.getStringExtra("android:screenshot_uri_id"));
      localNotificationManager.cancel(2131951669);
      new DeleteImageInBackgroundTask(paramContext).execute(new Uri[] { paramIntent });
    }
  }
  
  public static class TargetChosenReceiver
    extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      ((NotificationManager)paramContext.getSystemService("notification")).cancel(2131951669);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\screenshot\GlobalScreenshot.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */