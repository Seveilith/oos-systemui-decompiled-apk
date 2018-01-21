package android.support.v17.leanback.widget;

import android.os.Build.VERSION;
import android.view.View;

final class ShadowHelper
{
  static final ShadowHelper sInstance = new ShadowHelper();
  ShadowHelperVersionImpl mImpl;
  boolean mSupportsDynamicShadow;
  
  private ShadowHelper()
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.mSupportsDynamicShadow = true;
      this.mImpl = new ShadowHelperApi21Impl();
      return;
    }
    this.mImpl = new ShadowHelperStubImpl();
  }
  
  public static ShadowHelper getInstance()
  {
    return sInstance;
  }
  
  public void setZ(View paramView, float paramFloat)
  {
    this.mImpl.setZ(paramView, paramFloat);
  }
  
  public boolean supportsDynamicShadow()
  {
    return this.mSupportsDynamicShadow;
  }
  
  private static final class ShadowHelperApi21Impl
    implements ShadowHelper.ShadowHelperVersionImpl
  {
    public void setZ(View paramView, float paramFloat)
    {
      ShadowHelperApi21.setZ(paramView, paramFloat);
    }
  }
  
  private static final class ShadowHelperStubImpl
    implements ShadowHelper.ShadowHelperVersionImpl
  {
    public void setZ(View paramView, float paramFloat) {}
  }
  
  static abstract interface ShadowHelperVersionImpl
  {
    public abstract void setZ(View paramView, float paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\ShadowHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */