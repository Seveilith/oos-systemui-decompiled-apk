package com.android.systemui.volume;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.widget.TextView;

public class SpTexts
{
  private final Context mContext;
  private final ArrayMap<TextView, Integer> mTexts = new ArrayMap();
  private final Runnable mUpdateAll = new Runnable()
  {
    public void run()
    {
      int i = 0;
      while (i < SpTexts.-get0(SpTexts.this).size())
      {
        SpTexts.-wrap0(SpTexts.this, (TextView)SpTexts.-get0(SpTexts.this).keyAt(i), ((Integer)SpTexts.-get0(SpTexts.this).valueAt(i)).intValue());
        i += 1;
      }
    }
  };
  
  public SpTexts(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private void setTextSizeH(TextView paramTextView, int paramInt)
  {
    paramTextView.setTextSize(2, paramInt);
  }
  
  public int add(final TextView paramTextView)
  {
    if (paramTextView == null) {
      return 0;
    }
    Resources localResources = this.mContext.getResources();
    float f1 = localResources.getConfiguration().fontScale;
    float f2 = localResources.getDisplayMetrics().density;
    final int i = (int)(paramTextView.getTextSize() / f1 / f2);
    this.mTexts.put(paramTextView, Integer.valueOf(i));
    paramTextView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
    {
      public void onViewAttachedToWindow(View paramAnonymousView)
      {
        SpTexts.-wrap0(SpTexts.this, paramTextView, i);
      }
      
      public void onViewDetachedFromWindow(View paramAnonymousView) {}
    });
    return i;
  }
  
  public void update()
  {
    if (this.mTexts.isEmpty()) {
      return;
    }
    ((TextView)this.mTexts.keyAt(0)).post(this.mUpdateAll);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\SpTexts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */