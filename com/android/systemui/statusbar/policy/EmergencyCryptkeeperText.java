package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

public class EmergencyCryptkeeperText
  extends TextView
{
  private KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onPhoneStateChanged(int paramAnonymousInt) {}
  };
  private KeyguardUpdateMonitor mKeyguardUpdateMonitor;
  
  public EmergencyCryptkeeperText(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setVisibility(8);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
    this.mKeyguardUpdateMonitor.registerCallback(this.mCallback);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mKeyguardUpdateMonitor != null) {
      this.mKeyguardUpdateMonitor.removeCallback(this.mCallback);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\EmergencyCryptkeeperText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */