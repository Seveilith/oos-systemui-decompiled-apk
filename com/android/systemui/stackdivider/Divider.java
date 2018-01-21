package com.android.systemui.stackdivider;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.RemoteException;
import android.util.Log;
import android.view.IDockedStackListener.Stub;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.android.systemui.SystemUI;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class Divider
  extends SystemUI
{
  private boolean mAdjustedForIme = false;
  private final DividerState mDividerState = new DividerState();
  private DockDividerVisibilityListener mDockDividerVisibilityListener;
  private ForcedResizableInfoActivityController mForcedResizableController;
  private boolean mMinimized = false;
  private DividerView mView;
  private boolean mVisible = false;
  private DividerWindowManager mWindowManager;
  
  private void addDivider(Configuration paramConfiguration)
  {
    this.mView = ((DividerView)LayoutInflater.from(this.mContext).inflate(2130968617, null));
    Object localObject = this.mView;
    int i;
    int k;
    label65:
    int j;
    if (this.mVisible)
    {
      i = 0;
      ((DividerView)localObject).setVisibility(i);
      i = this.mContext.getResources().getDimensionPixelSize(17104931);
      if (paramConfiguration.orientation != 2) {
        break label183;
      }
      k = 1;
      if (k == 0) {
        break label189;
      }
      j = i;
      label72:
      if (k == 0) {
        break label194;
      }
      i = -1;
    }
    label183:
    label189:
    label194:
    for (;;)
    {
      this.mWindowManager.add(this.mView, j, i);
      this.mView.injectDependencies(this.mWindowManager, this.mDividerState);
      if (Utils.DEBUG_ONEPLUS)
      {
        localObject = this.mView.findViewById(2131951828);
        i = 0;
        if (localObject != null) {
          i = ((View)localObject).getLayoutParams().width;
        }
        Log.d("Divider", "orientation is " + paramConfiguration.orientation + " divider handle view height is " + i);
      }
      return;
      i = 4;
      break;
      k = 0;
      break label65;
      j = -1;
      break label72;
    }
  }
  
  private void notifyDockedStackExistsChanged(final boolean paramBoolean)
  {
    this.mView.post(new Runnable()
    {
      public void run()
      {
        Divider.-get1(Divider.this).notifyDockedStackExistsChanged(paramBoolean);
      }
    });
  }
  
  private void removeDivider()
  {
    this.mWindowManager.remove();
  }
  
  private void update(Configuration paramConfiguration)
  {
    removeDivider();
    addDivider(paramConfiguration);
    if (this.mMinimized)
    {
      this.mView.setMinimizedDockStack(true);
      updateTouchable();
    }
  }
  
  private void updateMinimizedDockedStack(final boolean paramBoolean, final long paramLong)
  {
    this.mView.post(new Runnable()
    {
      public void run()
      {
        if (Divider.-get2(Divider.this) != paramBoolean)
        {
          Divider.-set1(Divider.this, paramBoolean);
          Divider.-wrap2(Divider.this);
          if (paramLong > 0L) {
            Divider.-get3(Divider.this).setMinimizedDockStack(paramBoolean, paramLong);
          }
        }
        else
        {
          return;
        }
        Divider.-get3(Divider.this).setMinimizedDockStack(paramBoolean);
      }
    });
  }
  
  private void updateTouchable()
  {
    boolean bool2 = false;
    DividerWindowManager localDividerWindowManager = this.mWindowManager;
    boolean bool1 = bool2;
    if (!this.mMinimized) {
      if (!this.mAdjustedForIme) {
        break label31;
      }
    }
    label31:
    for (bool1 = bool2;; bool1 = true)
    {
      localDividerWindowManager.setTouchable(bool1);
      return;
    }
  }
  
  private void updateVisibility(final boolean paramBoolean)
  {
    this.mView.post(new Runnable()
    {
      public void run()
      {
        DividerView localDividerView;
        if (Divider.-get4(Divider.this) != paramBoolean)
        {
          Divider.-set2(Divider.this, paramBoolean);
          localDividerView = Divider.-get3(Divider.this);
          if (!paramBoolean) {
            break label66;
          }
        }
        label66:
        for (int i = 0;; i = 4)
        {
          localDividerView.setVisibility(i);
          Divider.-get3(Divider.this).setMinimizedDockStack(Divider.-get2(Divider.this));
          return;
        }
      }
    });
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print("  mVisible=");
    paramPrintWriter.println(this.mVisible);
    paramPrintWriter.print("  mMinimized=");
    paramPrintWriter.println(this.mMinimized);
    paramPrintWriter.print("  mAdjustedForIme=");
    paramPrintWriter.println(this.mAdjustedForIme);
  }
  
  public DividerView getView()
  {
    return this.mView;
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    update(paramConfiguration);
  }
  
  public void start()
  {
    this.mWindowManager = new DividerWindowManager(this.mContext);
    update(this.mContext.getResources().getConfiguration());
    putComponent(Divider.class, this);
    this.mDockDividerVisibilityListener = new DockDividerVisibilityListener();
    Recents.getSystemServices().registerDockedStackListener(this.mDockDividerVisibilityListener);
    this.mForcedResizableController = new ForcedResizableInfoActivityController(this.mContext);
  }
  
  class DockDividerVisibilityListener
    extends IDockedStackListener.Stub
  {
    DockDividerVisibilityListener() {}
    
    public void onAdjustedForImeChanged(boolean paramBoolean, long paramLong)
      throws RemoteException
    {
      Divider.-get3(Divider.this).post(new -void_onAdjustedForImeChanged_boolean_adjustedForIme_long_animDuration_LambdaImpl0(paramBoolean, paramLong));
    }
    
    public void onDividerVisibilityChanged(boolean paramBoolean)
      throws RemoteException
    {
      Divider.-wrap3(Divider.this, paramBoolean);
    }
    
    public void onDockSideChanged(int paramInt)
      throws RemoteException
    {
      Divider.-get3(Divider.this).post(new -void_onDockSideChanged_int_newDockSide_LambdaImpl0(paramInt));
    }
    
    public void onDockedStackExistsChanged(boolean paramBoolean)
      throws RemoteException
    {
      Divider.-wrap0(Divider.this, paramBoolean);
    }
    
    public void onDockedStackMinimizedChanged(boolean paramBoolean, long paramLong)
      throws RemoteException
    {
      Divider.-wrap1(Divider.this, paramBoolean, paramLong);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\stackdivider\Divider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */