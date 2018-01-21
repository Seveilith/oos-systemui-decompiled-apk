package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewPropertyAnimator;

public class EmergencyCarrierArea
  extends AlphaOptimizedLinearLayout
{
  private CarrierText mCarrierText;
  private EmergencyButton mEmergencyButton;
  
  public EmergencyCarrierArea(Context paramContext)
  {
    super(paramContext);
  }
  
  public EmergencyCarrierArea(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mCarrierText = ((CarrierText)findViewById(R.id.carrier_text));
    this.mEmergencyButton = ((EmergencyButton)findViewById(R.id.emergency_call_button));
    this.mEmergencyButton.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        if (EmergencyCarrierArea.-get0(EmergencyCarrierArea.this).getVisibility() != 0) {
          return false;
        }
        switch (paramAnonymousMotionEvent.getAction())
        {
        default: 
          return false;
        case 0: 
          EmergencyCarrierArea.-get0(EmergencyCarrierArea.this).animate().alpha(0.0F);
          return false;
        }
        EmergencyCarrierArea.-get0(EmergencyCarrierArea.this).animate().alpha(1.0F);
        return false;
      }
    });
  }
  
  public void setCarrierTextVisible(boolean paramBoolean)
  {
    CarrierText localCarrierText = this.mCarrierText;
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      localCarrierText.setVisibility(i);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\EmergencyCarrierArea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */