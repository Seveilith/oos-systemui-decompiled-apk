package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.AnimationIcon;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.Icon;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.RotationLockController.RotationLockControllerCallback;

public class RotationLockTile
  extends QSTile<QSTile.BooleanState>
{
  private final QSTile<QSTile.BooleanState>.AnimationIcon mAutoToLandscape = new QSTile.AnimationIcon(this, 2130837746, 2130837747);
  private final QSTile<QSTile.BooleanState>.AnimationIcon mAutoToPortrait = new QSTile.AnimationIcon(this, 2130837767, 2130837768);
  private final RotationLockController.RotationLockControllerCallback mCallback = new RotationLockController.RotationLockControllerCallback()
  {
    public void onRotationLockStateChanged(boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
    {
      RotationLockTile.-wrap0(RotationLockTile.this, Boolean.valueOf(paramAnonymousBoolean1));
    }
  };
  private final RotationLockController mController;
  private final QSTile<QSTile.BooleanState>.AnimationIcon mLandscapeToAuto = new QSTile.AnimationIcon(this, 2130837748, 2130837745);
  private final QSTile<QSTile.BooleanState>.AnimationIcon mPortraitToAuto = new QSTile.AnimationIcon(this, 2130837769, 2130837766);
  
  public RotationLockTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mController = paramHost.getRotationLockController();
  }
  
  private String getAccessibilityString(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      StringBuilder localStringBuilder = new StringBuilder().append(this.mContext.getString(2131690274)).append(",");
      Context localContext = this.mContext;
      if (isCurrentOrientationLockPortrait(this.mController, this.mContext)) {}
      for (String str = this.mContext.getString(2131690277);; str = this.mContext.getString(2131690278)) {
        return localContext.getString(2131690275, new Object[] { str });
      }
    }
    return this.mContext.getString(2131690274);
  }
  
  public static boolean isCurrentOrientationLockPortrait(RotationLockController paramRotationLockController, Context paramContext)
  {
    int i = paramRotationLockController.getRotationLockOrientation();
    if (i == 0) {
      return paramContext.getResources().getConfiguration().orientation != 2;
    }
    return i != 2;
  }
  
  protected String composeChangeAnnouncement()
  {
    return getAccessibilityString(((QSTile.BooleanState)this.mState).value);
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.DISPLAY_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 123;
  }
  
  public CharSequence getTileLabel()
  {
    return ((QSTile.BooleanState)getState()).label;
  }
  
  protected void handleClick()
  {
    if (this.mController == null) {
      return;
    }
    Context localContext = this.mContext;
    int i = getMetricsCategory();
    if (((QSTile.BooleanState)this.mState).value)
    {
      bool = false;
      MetricsLogger.action(localContext, i, bool);
      if (!this.mController.isRotationLocked()) {
        break label77;
      }
    }
    label77:
    for (boolean bool = false;; bool = true)
    {
      this.mController.setRotationLocked(bool);
      refreshState(Boolean.valueOf(bool));
      return;
      bool = true;
      break;
    }
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    boolean bool2 = true;
    if (this.mController == null) {
      return;
    }
    boolean bool1;
    int i;
    if (paramObject != null)
    {
      bool1 = ((Boolean)paramObject).booleanValue();
      paramBooleanState.noDisableColor = true;
      if (bool1) {
        bool2 = false;
      }
      paramBooleanState.value = bool2;
      bool2 = isCurrentOrientationLockPortrait(this.mController, this.mContext);
      if (!bool1) {
        break label151;
      }
      if (!bool2) {
        break label137;
      }
      i = 2131690277;
      label69:
      paramBooleanState.label = this.mContext.getString(i);
      if (!bool2) {
        break label143;
      }
    }
    label137:
    label143:
    for (paramObject = this.mAutoToPortrait;; paramObject = this.mAutoToLandscape)
    {
      paramBooleanState.icon = ((QSTile.Icon)paramObject);
      paramBooleanState.contentDescription = getAccessibilityString(bool1);
      paramObject = Switch.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      return;
      bool1 = this.mController.isRotationLocked();
      break;
      i = 2131690278;
      break label69;
    }
    label151:
    paramBooleanState.label = this.mContext.getString(2131690273);
    if (bool2) {}
    for (paramObject = this.mPortraitToAuto;; paramObject = this.mLandscapeToAuto)
    {
      paramBooleanState.icon = ((QSTile.Icon)paramObject);
      break;
    }
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mController == null) {
      return;
    }
    if (paramBoolean)
    {
      this.mController.addRotationLockControllerCallback(this.mCallback);
      return;
    }
    this.mController.removeRotationLockControllerCallback(this.mCallback);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\RotationLockTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */