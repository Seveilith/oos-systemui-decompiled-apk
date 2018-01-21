package android.support.v4.app;

import android.os.Bundle;
import android.support.v4.content.Loader;

public abstract class LoaderManager
{
  public static abstract interface LoaderCallbacks<D>
  {
    public abstract Loader<D> onCreateLoader(int paramInt, Bundle paramBundle);
    
    public abstract void onLoadFinished(Loader<D> paramLoader, D paramD);
    
    public abstract void onLoaderReset(Loader<D> paramLoader);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\app\LoaderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */