package android.support.v17.leanback.widget;

import android.os.Build.VERSION;

final class StaticShadowHelper
{
  static final StaticShadowHelper sInstance = new StaticShadowHelper();
  ShadowHelperVersionImpl mImpl;
  boolean mSupportsShadow;
  
  private StaticShadowHelper()
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.mSupportsShadow = true;
      this.mImpl = new ShadowHelperJbmr2Impl();
      return;
    }
    this.mSupportsShadow = false;
    this.mImpl = new ShadowHelperStubImpl();
  }
  
  public static StaticShadowHelper getInstance()
  {
    return sInstance;
  }
  
  public boolean supportsShadow()
  {
    return this.mSupportsShadow;
  }
  
  private static final class ShadowHelperJbmr2Impl
    implements StaticShadowHelper.ShadowHelperVersionImpl
  {}
  
  private static final class ShadowHelperStubImpl
    implements StaticShadowHelper.ShadowHelperVersionImpl
  {}
  
  static abstract interface ShadowHelperVersionImpl {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\StaticShadowHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */