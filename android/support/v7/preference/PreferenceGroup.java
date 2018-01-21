package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PreferenceGroup
  extends Preference
{
  private boolean mAttachedToHierarchy = false;
  private final Runnable mClearRecycleCacheRunnable = new Runnable()
  {
    public void run()
    {
      try
      {
        PreferenceGroup.-get0(PreferenceGroup.this).clear();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  };
  private int mCurrentPreferenceOrder = 0;
  private final Handler mHandler = new Handler();
  private final SimpleArrayMap<String, Long> mIdRecycleCache = new SimpleArrayMap();
  private boolean mOrderingAsAdded = true;
  private List<Preference> mPreferenceList = new ArrayList();
  
  public PreferenceGroup(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public PreferenceGroup(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PreferenceGroup(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.PreferenceGroup, paramInt1, paramInt2);
    this.mOrderingAsAdded = TypedArrayUtils.getBoolean(paramContext, R.styleable.PreferenceGroup_orderingFromXml, R.styleable.PreferenceGroup_orderingFromXml, true);
    paramContext.recycle();
  }
  
  public void addItemFromInflater(Preference paramPreference)
  {
    addPreference(paramPreference);
  }
  
  public boolean addPreference(Preference paramPreference)
  {
    if (this.mPreferenceList.contains(paramPreference)) {
      return true;
    }
    if (paramPreference.getOrder() == Integer.MAX_VALUE)
    {
      if (this.mOrderingAsAdded)
      {
        i = this.mCurrentPreferenceOrder;
        this.mCurrentPreferenceOrder = (i + 1);
        paramPreference.setOrder(i);
      }
      if ((paramPreference instanceof PreferenceGroup)) {
        ((PreferenceGroup)paramPreference).setOrderingAsAdded(this.mOrderingAsAdded);
      }
    }
    int j = Collections.binarySearch(this.mPreferenceList, paramPreference);
    int i = j;
    if (j < 0) {
      i = j * -1 - 1;
    }
    if (!onPrepareAddPreference(paramPreference)) {
      return false;
    }
    for (;;)
    {
      PreferenceManager localPreferenceManager;
      try
      {
        this.mPreferenceList.add(i, paramPreference);
        localPreferenceManager = getPreferenceManager();
        String str = paramPreference.getKey();
        if ((str != null) && (this.mIdRecycleCache.containsKey(str)))
        {
          l = ((Long)this.mIdRecycleCache.get(str)).longValue();
          this.mIdRecycleCache.remove(str);
          paramPreference.onAttachedToHierarchy(localPreferenceManager, l);
          if (this.mAttachedToHierarchy) {
            paramPreference.onAttached();
          }
          notifyHierarchyChanged();
          return true;
        }
      }
      finally {}
      long l = localPreferenceManager.getNextId();
    }
  }
  
  protected void dispatchRestoreInstanceState(Bundle paramBundle)
  {
    super.dispatchRestoreInstanceState(paramBundle);
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      getPreference(i).dispatchRestoreInstanceState(paramBundle);
      i += 1;
    }
  }
  
  protected void dispatchSaveInstanceState(Bundle paramBundle)
  {
    super.dispatchSaveInstanceState(paramBundle);
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      getPreference(i).dispatchSaveInstanceState(paramBundle);
      i += 1;
    }
  }
  
  public Preference findPreference(CharSequence paramCharSequence)
  {
    if (TextUtils.equals(getKey(), paramCharSequence)) {
      return this;
    }
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      Preference localPreference = getPreference(i);
      String str = localPreference.getKey();
      if ((str != null) && (str.equals(paramCharSequence))) {
        return localPreference;
      }
      if ((localPreference instanceof PreferenceGroup))
      {
        localPreference = ((PreferenceGroup)localPreference).findPreference(paramCharSequence);
        if (localPreference != null) {
          return localPreference;
        }
      }
      i += 1;
    }
    return null;
  }
  
  public Preference getPreference(int paramInt)
  {
    return (Preference)this.mPreferenceList.get(paramInt);
  }
  
  public int getPreferenceCount()
  {
    return this.mPreferenceList.size();
  }
  
  protected boolean isOnSameScreenAsChildren()
  {
    return true;
  }
  
  public void notifyDependencyChange(boolean paramBoolean)
  {
    super.notifyDependencyChange(paramBoolean);
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      getPreference(i).onParentChanged(this, paramBoolean);
      i += 1;
    }
  }
  
  public void onAttached()
  {
    super.onAttached();
    this.mAttachedToHierarchy = true;
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      getPreference(i).onAttached();
      i += 1;
    }
  }
  
  public void onDetached()
  {
    super.onDetached();
    this.mAttachedToHierarchy = false;
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      getPreference(i).onDetached();
      i += 1;
    }
  }
  
  protected boolean onPrepareAddPreference(Preference paramPreference)
  {
    paramPreference.onParentChanged(this, shouldDisableDependents());
    return true;
  }
  
  public void setOrderingAsAdded(boolean paramBoolean)
  {
    this.mOrderingAsAdded = paramBoolean;
  }
  
  void sortPreferences()
  {
    try
    {
      Collections.sort(this.mPreferenceList);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\preference\PreferenceGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */