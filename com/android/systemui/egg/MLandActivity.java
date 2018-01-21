package com.android.systemui.egg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class MLandActivity
  extends Activity
{
  MLand mLand;
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968716);
    this.mLand = ((MLand)findViewById(2131952057));
    this.mLand.setScoreFieldHolder((ViewGroup)findViewById(2131952064));
    paramBundle = findViewById(2131952058);
    this.mLand.setSplash(paramBundle);
    int i = this.mLand.getGameControllers().size();
    if (i > 0) {
      this.mLand.setupPlayers(i);
    }
  }
  
  public void onPause()
  {
    this.mLand.stop();
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    this.mLand.onAttachedToWindow();
    updateSplashPlayers();
    this.mLand.showSplash();
  }
  
  public void playerMinus(View paramView)
  {
    this.mLand.removePlayer();
    updateSplashPlayers();
  }
  
  public void playerPlus(View paramView)
  {
    this.mLand.addPlayer();
    updateSplashPlayers();
  }
  
  public void startButtonPressed(View paramView)
  {
    findViewById(2131952063).setVisibility(4);
    findViewById(2131952065).setVisibility(4);
    this.mLand.start(true);
  }
  
  public void updateSplashPlayers()
  {
    int i = this.mLand.getNumPlayers();
    View localView1 = findViewById(2131952063);
    View localView2 = findViewById(2131952065);
    if (i == 1)
    {
      localView1.setVisibility(4);
      localView2.setVisibility(0);
      localView2.requestFocus();
      return;
    }
    if (i == 6)
    {
      localView1.setVisibility(0);
      localView2.setVisibility(4);
      localView1.requestFocus();
      return;
    }
    localView1.setVisibility(0);
    localView2.setVisibility(0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\egg\MLandActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */