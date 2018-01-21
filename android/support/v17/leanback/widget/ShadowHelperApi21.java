package android.support.v17.leanback.widget;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

class ShadowHelperApi21
{
  static final ViewOutlineProvider sOutlineProvider = new ViewOutlineProvider()
  {
    public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
    {
      paramAnonymousOutline.setRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight());
      paramAnonymousOutline.setAlpha(1.0F);
    }
  };
  
  public static void setZ(View paramView, float paramFloat)
  {
    paramView.setZ(paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\ShadowHelperApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */