package com.android.systemui;

import android.service.dreams.DreamService;

public class DessertCaseDream
  extends DreamService
{
  private DessertCaseView.RescalingContainer mContainer;
  private DessertCaseView mView;
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    setInteractive(false);
    this.mView = new DessertCaseView(this);
    this.mContainer = new DessertCaseView.RescalingContainer(this);
    this.mContainer.setView(this.mView);
    setContentView(this.mContainer);
  }
  
  public void onDreamingStarted()
  {
    super.onDreamingStarted();
    this.mView.postDelayed(new Runnable()
    {
      public void run()
      {
        DessertCaseDream.-get0(DessertCaseDream.this).start();
      }
    }, 1000L);
  }
  
  public void onDreamingStopped()
  {
    super.onDreamingStopped();
    this.mView.stop();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\DessertCaseDream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */