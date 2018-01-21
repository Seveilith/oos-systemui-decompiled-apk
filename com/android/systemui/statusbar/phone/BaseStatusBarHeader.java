package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.qs.QSPanel.Callback;
import com.android.systemui.statusbar.policy.NetworkController.EmergencyListener;

public abstract class BaseStatusBarHeader
  extends RelativeLayout
  implements NetworkController.EmergencyListener
{
  public BaseStatusBarHeader(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public abstract int getCollapsedHeight();
  
  public abstract void setActivityStarter(ActivityStarter paramActivityStarter);
  
  public abstract void setCallback(QSPanel.Callback paramCallback);
  
  public abstract void setExpanded(boolean paramBoolean);
  
  public abstract void setExpansion(float paramFloat);
  
  public abstract void setListening(boolean paramBoolean);
  
  public abstract void setQSPanel(QSPanel paramQSPanel);
  
  public abstract void updateEverything();
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\BaseStatusBarHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */