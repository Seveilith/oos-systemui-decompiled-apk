package com.android.systemui.volume;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnTouchListener;

public class Interaction
{
  public static void register(View paramView, Callback paramCallback)
  {
    paramView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        this.val$callback.onInteraction();
        return false;
      }
    });
    paramView.setOnGenericMotionListener(new View.OnGenericMotionListener()
    {
      public boolean onGenericMotion(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        this.val$callback.onInteraction();
        return false;
      }
    });
  }
  
  public static abstract interface Callback
  {
    public abstract void onInteraction();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\Interaction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */