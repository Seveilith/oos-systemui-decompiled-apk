package com.android.systemui.recents.misc;

import android.animation.TypeEvaluator;
import android.graphics.RectF;

public class RectFEvaluator
  implements TypeEvaluator<RectF>
{
  private RectF mRect = new RectF();
  
  public RectF evaluate(float paramFloat, RectF paramRectF1, RectF paramRectF2)
  {
    float f1 = paramRectF1.left;
    float f2 = paramRectF2.left;
    float f3 = paramRectF1.left;
    float f4 = paramRectF1.top;
    float f5 = paramRectF2.top;
    float f6 = paramRectF1.top;
    float f7 = paramRectF1.right;
    float f8 = paramRectF2.right;
    float f9 = paramRectF1.right;
    float f10 = paramRectF1.bottom;
    float f11 = paramRectF2.bottom;
    float f12 = paramRectF1.bottom;
    this.mRect.set(f1 + (f2 - f3) * paramFloat, f4 + (f5 - f6) * paramFloat, f7 + (f8 - f9) * paramFloat, f10 + (f11 - f12) * paramFloat);
    return this.mRect;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\misc\RectFEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */