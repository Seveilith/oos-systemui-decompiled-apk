package com.android.systemui.tuner;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.Prefs;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.volume.ZenModePanel;
import com.android.systemui.volume.ZenModePanel.Callback;

public class TunerZenModePanel
  extends LinearLayout
  implements View.OnClickListener
{
  private View mButtons;
  private ZenModePanel.Callback mCallback;
  private ZenModeController mController;
  private View mDone;
  private View.OnClickListener mDoneListener;
  private boolean mEditing;
  private View mHeaderSwitch;
  private View mMoreSettings;
  private final Runnable mUpdate = new Runnable()
  {
    public void run()
    {
      TunerZenModePanel.-wrap0(TunerZenModePanel.this);
    }
  };
  private int mZenMode;
  private ZenModePanel mZenModePanel;
  
  public TunerZenModePanel(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void postUpdatePanel()
  {
    removeCallbacks(this.mUpdate);
    postDelayed(this.mUpdate, 40L);
  }
  
  private void updatePanel()
  {
    int j = 0;
    boolean bool;
    Object localObject;
    if (this.mZenMode != 0)
    {
      bool = true;
      ((Checkable)this.mHeaderSwitch.findViewById(16908311)).setChecked(bool);
      localObject = this.mZenModePanel;
      if (!bool) {
        break label71;
      }
      i = 0;
      label41:
      ((ZenModePanel)localObject).setVisibility(i);
      localObject = this.mButtons;
      if (!bool) {
        break label77;
      }
    }
    label71:
    label77:
    for (int i = j;; i = 8)
    {
      ((View)localObject).setVisibility(i);
      return;
      bool = false;
      break;
      i = 8;
      break label41;
    }
  }
  
  public void init(ZenModeController paramZenModeController)
  {
    this.mController = paramZenModeController;
    this.mHeaderSwitch = findViewById(2131952318);
    this.mHeaderSwitch.setVisibility(0);
    this.mHeaderSwitch.setOnClickListener(this);
    ((TextView)this.mHeaderSwitch.findViewById(16908310)).setText(2131690264);
    this.mZenModePanel = ((ZenModePanel)findViewById(2131952353));
    this.mZenModePanel.init(paramZenModeController);
    this.mButtons = findViewById(2131952319);
    this.mMoreSettings = this.mButtons.findViewById(16908314);
    this.mMoreSettings.setOnClickListener(this);
    ((TextView)this.mMoreSettings).setText(2131690305);
    this.mDone = this.mButtons.findViewById(16908313);
    this.mDone.setOnClickListener(this);
    ((TextView)this.mDone).setText(2131690306);
  }
  
  public void onClick(View paramView)
  {
    if (paramView == this.mHeaderSwitch)
    {
      this.mEditing = true;
      if (this.mZenMode == 0)
      {
        this.mZenMode = Prefs.getInt(this.mContext, "DndFavoriteZen", 3);
        this.mController.setZen(this.mZenMode, null, "TunerZenModePanel");
        postUpdatePanel();
      }
    }
    do
    {
      return;
      this.mZenMode = 0;
      this.mController.setZen(0, null, "TunerZenModePanel");
      postUpdatePanel();
      return;
      if (paramView == this.mMoreSettings)
      {
        paramView = new Intent("android.settings.ZEN_MODE_SETTINGS");
        paramView.addFlags(268435456);
        getContext().startActivity(paramView);
        return;
      }
    } while (paramView != this.mDone);
    this.mEditing = false;
    setVisibility(8);
    this.mDoneListener.onClick(paramView);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mEditing = false;
  }
  
  public void setCallback(ZenModePanel.Callback paramCallback)
  {
    this.mCallback = paramCallback;
    this.mZenModePanel.setCallback(paramCallback);
  }
  
  public void setDoneListener(View.OnClickListener paramOnClickListener)
  {
    this.mDoneListener = paramOnClickListener;
  }
  
  public void setZenState(int paramInt)
  {
    this.mZenMode = paramInt;
    postUpdatePanel();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\TunerZenModePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */