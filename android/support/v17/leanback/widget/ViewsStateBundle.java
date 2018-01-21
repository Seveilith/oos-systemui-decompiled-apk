package android.support.v17.leanback.widget;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.LruCache;
import android.util.SparseArray;
import android.view.View;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class ViewsStateBundle
{
  private LruCache<String, SparseArray<Parcelable>> mChildStates;
  private int mLimitNumber = 100;
  private int mSavePolicy = 0;
  
  static String getSaveStatesKey(int paramInt)
  {
    return Integer.toString(paramInt);
  }
  
  public void clear()
  {
    if (this.mChildStates != null) {
      this.mChildStates.evictAll();
    }
  }
  
  public final void loadFromBundle(Bundle paramBundle)
  {
    if ((this.mChildStates != null) && (paramBundle != null))
    {
      this.mChildStates.evictAll();
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        this.mChildStates.put(str, paramBundle.getSparseParcelableArray(str));
      }
    }
  }
  
  public final void loadView(View paramView, int paramInt)
  {
    if (this.mChildStates != null)
    {
      Object localObject = getSaveStatesKey(paramInt);
      localObject = (SparseArray)this.mChildStates.remove(localObject);
      if (localObject != null) {
        paramView.restoreHierarchyState((SparseArray)localObject);
      }
    }
  }
  
  public void remove(int paramInt)
  {
    if ((this.mChildStates != null) && (this.mChildStates.size() != 0)) {
      this.mChildStates.remove(getSaveStatesKey(paramInt));
    }
  }
  
  public final Bundle saveAsBundle()
  {
    if ((this.mChildStates == null) || (this.mChildStates.size() == 0)) {
      return null;
    }
    Object localObject = this.mChildStates.snapshot();
    Bundle localBundle = new Bundle();
    localObject = ((Map)localObject).entrySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      localBundle.putSparseParcelableArray((String)localEntry.getKey(), (SparseArray)localEntry.getValue());
    }
    return localBundle;
  }
  
  public final void saveOffscreenView(View paramView, int paramInt)
  {
    switch (this.mSavePolicy)
    {
    default: 
      return;
    case 2: 
    case 3: 
      saveViewUnchecked(paramView, paramInt);
      return;
    }
    remove(paramInt);
  }
  
  public final Bundle saveOnScreenView(Bundle paramBundle, View paramView, int paramInt)
  {
    Object localObject = paramBundle;
    if (this.mSavePolicy != 0)
    {
      localObject = getSaveStatesKey(paramInt);
      SparseArray localSparseArray = new SparseArray();
      paramView.saveHierarchyState(localSparseArray);
      paramView = paramBundle;
      if (paramBundle == null) {
        paramView = new Bundle();
      }
      paramView.putSparseParcelableArray((String)localObject, localSparseArray);
      localObject = paramView;
    }
    return (Bundle)localObject;
  }
  
  protected final void saveViewUnchecked(View paramView, int paramInt)
  {
    if (this.mChildStates != null)
    {
      String str = getSaveStatesKey(paramInt);
      SparseArray localSparseArray = new SparseArray();
      paramView.saveHierarchyState(localSparseArray);
      this.mChildStates.put(str, localSparseArray);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\ViewsStateBundle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */