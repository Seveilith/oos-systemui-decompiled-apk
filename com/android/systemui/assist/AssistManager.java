package com.android.systemui.assist;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.android.internal.app.AssistUtils;
import com.android.internal.app.IVoiceInteractionSessionListener.Stub;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractionSessionShowCallback.Stub;
import com.android.systemui.statusbar.BaseStatusBar;

public class AssistManager
{
  private final AssistDisclosure mAssistDisclosure;
  protected final AssistUtils mAssistUtils;
  private final BaseStatusBar mBar;
  protected final Context mContext;
  private Runnable mHideRunnable = new Runnable()
  {
    public void run()
    {
      AssistManager.-get1(AssistManager.this).removeCallbacks(this);
      AssistManager.-get1(AssistManager.this).show(false, true);
    }
  };
  private IVoiceInteractionSessionShowCallback mShowCallback = new IVoiceInteractionSessionShowCallback.Stub()
  {
    public void onFailed()
      throws RemoteException
    {
      AssistManager.-get1(AssistManager.this).post(AssistManager.-get0(AssistManager.this));
    }
    
    public void onShown()
      throws RemoteException
    {
      AssistManager.-get1(AssistManager.this).post(AssistManager.-get0(AssistManager.this));
    }
  };
  private AssistOrbContainer mView;
  private final WindowManager mWindowManager;
  
  public AssistManager(BaseStatusBar paramBaseStatusBar, Context paramContext)
  {
    this.mContext = paramContext;
    this.mBar = paramBaseStatusBar;
    this.mWindowManager = ((WindowManager)this.mContext.getSystemService("window"));
    this.mAssistUtils = new AssistUtils(paramContext);
    this.mAssistDisclosure = new AssistDisclosure(paramContext, new Handler());
    registerVoiceInteractionSessionListener();
  }
  
  private ComponentName getAssistInfo()
  {
    return this.mAssistUtils.getAssistComponentForUser(-2);
  }
  
  private WindowManager.LayoutParams getLayoutParams()
  {
    WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(-1, this.mContext.getResources().getDimensionPixelSize(2131755517), 2033, 280, -3);
    if (ActivityManager.isHighEndGfx()) {
      localLayoutParams.flags |= 0x1000000;
    }
    localLayoutParams.gravity = 8388691;
    localLayoutParams.setTitle("AssistPreviewPanel");
    localLayoutParams.softInputMode = 49;
    return localLayoutParams;
  }
  
  private boolean isVoiceSessionRunning()
  {
    return this.mAssistUtils.isSessionRunning();
  }
  
  private void maybeSwapSearchIcon(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (this.mView == null) {
      onConfigurationChanged();
    }
    replaceDrawable(this.mView.getOrb().getLogo(), paramComponentName, "com.android.systemui.action_assist_icon", paramBoolean);
  }
  
  private void showOrb(ComponentName paramComponentName, boolean paramBoolean)
  {
    maybeSwapSearchIcon(paramComponentName, paramBoolean);
    this.mView.show(true, true);
  }
  
  private void startAssistActivity(final Bundle paramBundle, ComponentName paramComponentName)
  {
    if (!this.mBar.isDeviceProvisioned()) {
      return;
    }
    this.mBar.animateCollapsePanels(3);
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "assist_structure_enabled", 1, -2) != 0) {}
    final Intent localIntent;
    for (boolean bool = true;; bool = false)
    {
      localIntent = ((SearchManager)this.mContext.getSystemService("search")).getAssistIntent(bool);
      if (localIntent != null) {
        break;
      }
      return;
    }
    localIntent.setComponent(paramComponentName);
    localIntent.putExtras(paramBundle);
    if (bool) {
      showDisclosure();
    }
    try
    {
      paramBundle = ActivityOptions.makeCustomAnimation(this.mContext, 2131034296, 2131034297);
      localIntent.addFlags(268435456);
      AsyncTask.execute(new Runnable()
      {
        public void run()
        {
          AssistManager.this.mContext.startActivityAsUser(localIntent, paramBundle.toBundle(), new UserHandle(-2));
        }
      });
      return;
    }
    catch (ActivityNotFoundException paramBundle)
    {
      Log.w("AssistManager", "Activity not found for " + localIntent.getAction());
    }
  }
  
  private void startAssistInternal(Bundle paramBundle, ComponentName paramComponentName, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      startVoiceInteractor(paramBundle);
      return;
    }
    startAssistActivity(paramBundle, paramComponentName);
  }
  
  private void startVoiceInteractor(Bundle paramBundle)
  {
    this.mAssistUtils.showSessionForActiveService(paramBundle, 4, this.mShowCallback, null);
  }
  
  public boolean canVoiceAssistBeLaunchedFromKeyguard()
  {
    return this.mAssistUtils.activeServiceSupportsLaunchFromKeyguard();
  }
  
  public void destroy()
  {
    this.mWindowManager.removeViewImmediate(this.mView);
  }
  
  public ComponentName getVoiceInteractorComponentName()
  {
    return this.mAssistUtils.getActiveServiceComponentName();
  }
  
  public void hideAssist()
  {
    this.mAssistUtils.hideCurrentSession();
  }
  
  public void launchVoiceAssistFromKeyguard()
  {
    this.mAssistUtils.launchVoiceAssistFromKeyguard();
  }
  
  public void onConfigurationChanged()
  {
    boolean bool = false;
    if (this.mView != null)
    {
      bool = this.mView.isShowing();
      this.mWindowManager.removeView(this.mView);
    }
    this.mView = ((AssistOrbContainer)LayoutInflater.from(this.mContext).inflate(2130968603, null));
    this.mView.setVisibility(8);
    this.mView.setSystemUiVisibility(1792);
    WindowManager.LayoutParams localLayoutParams = getLayoutParams();
    this.mWindowManager.addView(this.mView, localLayoutParams);
    if (bool) {
      this.mView.show(true, false);
    }
  }
  
  public void onLockscreenShown()
  {
    this.mAssistUtils.onLockscreenShown();
  }
  
  protected void registerVoiceInteractionSessionListener()
  {
    this.mAssistUtils.registerVoiceInteractionSessionListener(new IVoiceInteractionSessionListener.Stub()
    {
      public void onVoiceSessionHidden()
        throws RemoteException
      {
        Log.v("AssistManager", "Voice closed");
      }
      
      public void onVoiceSessionShown()
        throws RemoteException
      {
        Log.v("AssistManager", "Voice open");
      }
    });
  }
  
  /* Error */
  public void replaceDrawable(android.widget.ImageView paramImageView, ComponentName paramComponentName, String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_2
    //   1: ifnull +116 -> 117
    //   4: aload_0
    //   5: getfield 50	com/android/systemui/assist/AssistManager:mContext	Landroid/content/Context;
    //   8: invokevirtual 334	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   11: astore 7
    //   13: iload 4
    //   15: ifeq +54 -> 69
    //   18: aload 7
    //   20: aload_2
    //   21: sipush 128
    //   24: invokevirtual 340	android/content/pm/PackageManager:getServiceInfo	(Landroid/content/ComponentName;I)Landroid/content/pm/ServiceInfo;
    //   27: getfield 346	android/content/pm/ServiceInfo:metaData	Landroid/os/Bundle;
    //   30: astore 6
    //   32: aload 6
    //   34: ifnull +83 -> 117
    //   37: aload 6
    //   39: aload_3
    //   40: invokevirtual 352	android/os/Bundle:getInt	(Ljava/lang/String;)I
    //   43: istore 5
    //   45: iload 5
    //   47: ifeq +70 -> 117
    //   50: aload_1
    //   51: aload 7
    //   53: aload_2
    //   54: invokevirtual 357	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   57: invokevirtual 361	android/content/pm/PackageManager:getResourcesForApplication	(Ljava/lang/String;)Landroid/content/res/Resources;
    //   60: iload 5
    //   62: invokevirtual 365	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
    //   65: invokevirtual 371	android/widget/ImageView:setImageDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   68: return
    //   69: aload 7
    //   71: aload_2
    //   72: sipush 128
    //   75: invokevirtual 375	android/content/pm/PackageManager:getActivityInfo	(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;
    //   78: getfield 378	android/content/pm/ActivityInfo:metaData	Landroid/os/Bundle;
    //   81: astore 6
    //   83: goto -51 -> 32
    //   86: astore_3
    //   87: ldc -20
    //   89: new 238	java/lang/StringBuilder
    //   92: dup
    //   93: invokespecial 239	java/lang/StringBuilder:<init>	()V
    //   96: ldc_w 380
    //   99: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   102: aload_2
    //   103: invokevirtual 383	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   106: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   109: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   112: aload_3
    //   113: invokestatic 386	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   116: pop
    //   117: aload_1
    //   118: aconst_null
    //   119: invokevirtual 371	android/widget/ImageView:setImageDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   122: return
    //   123: astore_3
    //   124: ldc -20
    //   126: new 238	java/lang/StringBuilder
    //   129: dup
    //   130: invokespecial 239	java/lang/StringBuilder:<init>	()V
    //   133: ldc_w 388
    //   136: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: aload_2
    //   140: invokevirtual 383	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   143: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: ldc_w 390
    //   149: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   155: invokestatic 393	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   158: pop
    //   159: goto -42 -> 117
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	162	0	this	AssistManager
    //   0	162	1	paramImageView	android.widget.ImageView
    //   0	162	2	paramComponentName	ComponentName
    //   0	162	3	paramString	String
    //   0	162	4	paramBoolean	boolean
    //   43	18	5	i	int
    //   30	52	6	localBundle	Bundle
    //   11	59	7	localPackageManager	android.content.pm.PackageManager
    // Exception table:
    //   from	to	target	type
    //   4	13	86	android/content/res/Resources$NotFoundException
    //   18	32	86	android/content/res/Resources$NotFoundException
    //   37	45	86	android/content/res/Resources$NotFoundException
    //   50	68	86	android/content/res/Resources$NotFoundException
    //   69	83	86	android/content/res/Resources$NotFoundException
    //   4	13	123	android/content/pm/PackageManager$NameNotFoundException
    //   18	32	123	android/content/pm/PackageManager$NameNotFoundException
    //   37	45	123	android/content/pm/PackageManager$NameNotFoundException
    //   50	68	123	android/content/pm/PackageManager$NameNotFoundException
    //   69	83	123	android/content/pm/PackageManager$NameNotFoundException
  }
  
  protected boolean shouldShowOrb()
  {
    return true;
  }
  
  public void showDisclosure()
  {
    this.mAssistDisclosure.postShow();
  }
  
  public void startAssist(Bundle paramBundle)
  {
    ComponentName localComponentName = getAssistInfo();
    if (localComponentName == null) {
      return;
    }
    boolean bool = localComponentName.equals(getVoiceInteractorComponentName());
    AssistOrbContainer localAssistOrbContainer;
    Runnable localRunnable;
    if ((!bool) || ((!isVoiceSessionRunning()) && (shouldShowOrb())))
    {
      showOrb(localComponentName, bool);
      localAssistOrbContainer = this.mView;
      localRunnable = this.mHideRunnable;
      if (!bool) {
        break label85;
      }
    }
    label85:
    for (long l = 2500L;; l = 1000L)
    {
      localAssistOrbContainer.postDelayed(localRunnable, l);
      startAssistInternal(paramBundle, localComponentName, bool);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\assist\AssistManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */