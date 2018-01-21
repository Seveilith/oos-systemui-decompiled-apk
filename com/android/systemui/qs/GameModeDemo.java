package com.android.systemui.qs;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings.System;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import com.android.systemui.Prefs;

public class GameModeDemo
{
  Context mContext;
  private ViewGroup mDemo;
  private boolean mIsShow = false;
  private SystemSetting mModeSetting;
  
  public GameModeDemo(Context paramContext)
  {
    this.mContext = paramContext;
    if (isFirstTime(this.mContext))
    {
      Log.d("GameModeDemo", "Never entering game mode before");
      this.mModeSetting = new SystemSetting(this.mContext, new Handler(), "game_mode_status", true)
      {
        protected void handleValueChanged(int paramAnonymousInt, boolean paramAnonymousBoolean)
        {
          if (paramAnonymousInt == 1)
          {
            GameModeDemo.-wrap0(GameModeDemo.this, true);
            return;
          }
          GameModeDemo.-wrap0(GameModeDemo.this, false);
        }
      };
      this.mModeSetting.setListening(true);
    }
  }
  
  public static boolean isFirstTime(Context paramContext)
  {
    return Prefs.getBoolean(paramContext, "QsGameModeFirstTime", true);
  }
  
  private void showDemo(boolean paramBoolean)
  {
    if (this.mIsShow == paramBoolean)
    {
      Log.d("GameModeDemo", "showDemo: the same visibility:" + paramBoolean + ", don't do anything");
      return;
    }
    this.mIsShow = paramBoolean;
    int i;
    if (paramBoolean)
    {
      this.mDemo = ((ViewGroup)LayoutInflater.from(this.mContext).inflate(2130968626, null, false));
      this.mDemo.findViewById(2131951833).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          GameModeDemo.-wrap0(GameModeDemo.this, false);
        }
      });
      WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(2014, 1024, -2);
      localLayoutParams.setTitle("Game Mode Demo");
      TextView localTextView = (TextView)this.mDemo.findViewById(2131951832);
      localTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
      paramBoolean = "1".equals(Settings.System.getStringForUser(this.mContext.getContentResolver(), "game_mode_lock_buttons", -2));
      boolean bool = "1".equals(Settings.System.getStringForUser(this.mContext.getContentResolver(), "game_mode_block_notification", -2));
      if (paramBoolean) {
        if (bool)
        {
          i = 2131690024;
          localTextView.setText(this.mContext.getString(2131690021, new Object[] { this.mContext.getString(i) }));
          ((WindowManager)this.mContext.getSystemService("window")).addView(this.mDemo, localLayoutParams);
          Prefs.putBoolean(this.mContext, "QsGameModeFirstTime", false);
        }
      }
    }
    do
    {
      return;
      i = 2131690023;
      break;
      i = 2131690022;
      break;
      this.mModeSetting.setListening(false);
    } while (this.mDemo == null);
    ((WindowManager)this.mContext.getSystemService("window")).removeView(this.mDemo);
    this.mDemo = null;
  }
  
  public void updateResources()
  {
    if (this.mIsShow)
    {
      showDemo(false);
      showDemo(true);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\GameModeDemo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */