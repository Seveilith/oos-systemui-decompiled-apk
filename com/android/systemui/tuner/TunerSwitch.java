package com.android.systemui.tuner;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.provider.Settings.Secure;
import android.support.v14.preference.SwitchPreference;
import android.util.AttributeSet;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R.styleable;

public class TunerSwitch
  extends SwitchPreference
  implements TunerService.Tunable
{
  private final int mAction;
  private final boolean mDefault;
  
  public TunerSwitch(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TunerSwitch);
    this.mDefault = paramContext.getBoolean(0, false);
    this.mAction = paramContext.getInt(1, -1);
  }
  
  public void onAttached()
  {
    super.onAttached();
    TunerService.get(getContext()).addTunable(this, getKey().split(","));
  }
  
  protected void onClick()
  {
    super.onClick();
    if (this.mAction != -1) {
      MetricsLogger.action(getContext(), this.mAction, isChecked());
    }
  }
  
  public void onDetached()
  {
    TunerService.get(getContext()).removeTunable(this);
    super.onDetached();
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    boolean bool = false;
    if (paramString2 != null) {
      if (Integer.parseInt(paramString2) == 0) {}
    }
    for (bool = true;; bool = this.mDefault)
    {
      setChecked(bool);
      return;
    }
  }
  
  protected boolean persistBoolean(boolean paramBoolean)
  {
    String[] arrayOfString = getKey().split(",");
    int j = arrayOfString.length;
    int i = 0;
    if (i < j)
    {
      String str2 = arrayOfString[i];
      ContentResolver localContentResolver = getContext().getContentResolver();
      if (paramBoolean) {}
      for (String str1 = "1";; str1 = "0")
      {
        Settings.Secure.putString(localContentResolver, str2, str1);
        i += 1;
        break;
      }
    }
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\TunerSwitch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */