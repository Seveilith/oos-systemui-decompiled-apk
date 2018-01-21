package android.support.v4.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View.BaseSavedState;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import java.util.ArrayList;

public class FragmentTabHost
  extends TabHost
  implements TabHost.OnTabChangeListener
{
  private boolean mAttached;
  private int mContainerId;
  private Context mContext;
  private FragmentManager mFragmentManager;
  private TabInfo mLastTab;
  private TabHost.OnTabChangeListener mOnTabChangeListener;
  private final ArrayList<TabInfo> mTabs = new ArrayList();
  
  public FragmentTabHost(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initFragmentTabHost(paramContext, paramAttributeSet);
  }
  
  @Nullable
  private FragmentTransaction doTabChanged(@Nullable String paramString, @Nullable FragmentTransaction paramFragmentTransaction)
  {
    TabInfo localTabInfo = getTabInfoForTag(paramString);
    paramString = paramFragmentTransaction;
    if (this.mLastTab != localTabInfo)
    {
      paramString = paramFragmentTransaction;
      if (paramFragmentTransaction == null) {
        paramString = this.mFragmentManager.beginTransaction();
      }
      if ((this.mLastTab != null) && (this.mLastTab.fragment != null)) {
        paramString.detach(this.mLastTab.fragment);
      }
      if (localTabInfo != null)
      {
        if (localTabInfo.fragment != null) {
          break label116;
        }
        localTabInfo.fragment = Fragment.instantiate(this.mContext, localTabInfo.clss.getName(), localTabInfo.args);
        paramString.add(this.mContainerId, localTabInfo.fragment, localTabInfo.tag);
      }
    }
    for (;;)
    {
      this.mLastTab = localTabInfo;
      return paramString;
      label116:
      paramString.attach(localTabInfo.fragment);
    }
  }
  
  @Nullable
  private TabInfo getTabInfoForTag(String paramString)
  {
    int i = 0;
    int j = this.mTabs.size();
    while (i < j)
    {
      TabInfo localTabInfo = (TabInfo)this.mTabs.get(i);
      if (localTabInfo.tag.equals(paramString)) {
        return localTabInfo;
      }
      i += 1;
    }
    return null;
  }
  
  private void initFragmentTabHost(Context paramContext, AttributeSet paramAttributeSet)
  {
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, new int[] { 16842995 }, 0, 0);
    this.mContainerId = paramContext.getResourceId(0, 0);
    paramContext.recycle();
    super.setOnTabChangedListener(this);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    String str = getCurrentTabTag();
    Object localObject1 = null;
    int i = 0;
    int j = this.mTabs.size();
    if (i < j)
    {
      TabInfo localTabInfo = (TabInfo)this.mTabs.get(i);
      localTabInfo.fragment = this.mFragmentManager.findFragmentByTag(localTabInfo.tag);
      Object localObject2 = localObject1;
      if (localTabInfo.fragment != null)
      {
        if (!localTabInfo.fragment.isDetached()) {
          break label92;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        label92:
        if (localTabInfo.tag.equals(str))
        {
          this.mLastTab = localTabInfo;
          localObject2 = localObject1;
        }
        else
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = this.mFragmentManager.beginTransaction();
          }
          ((FragmentTransaction)localObject2).detach(localTabInfo.fragment);
        }
      }
    }
    this.mAttached = true;
    localObject1 = doTabChanged(str, (FragmentTransaction)localObject1);
    if (localObject1 != null)
    {
      ((FragmentTransaction)localObject1).commit();
      this.mFragmentManager.executePendingTransactions();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mAttached = false;
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    setCurrentTabByTag(paramParcelable.curTab);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.curTab = getCurrentTabTag();
    return localSavedState;
  }
  
  public void onTabChanged(String paramString)
  {
    if (this.mAttached)
    {
      FragmentTransaction localFragmentTransaction = doTabChanged(paramString, null);
      if (localFragmentTransaction != null) {
        localFragmentTransaction.commit();
      }
    }
    if (this.mOnTabChangeListener != null) {
      this.mOnTabChangeListener.onTabChanged(paramString);
    }
  }
  
  public void setOnTabChangedListener(TabHost.OnTabChangeListener paramOnTabChangeListener)
  {
    this.mOnTabChangeListener = paramOnTabChangeListener;
  }
  
  @Deprecated
  public void setup()
  {
    throw new IllegalStateException("Must call setup() that takes a Context and FragmentManager");
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public FragmentTabHost.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new FragmentTabHost.SavedState(paramAnonymousParcel);
      }
      
      public FragmentTabHost.SavedState[] newArray(int paramAnonymousInt)
      {
        return new FragmentTabHost.SavedState[paramAnonymousInt];
      }
    };
    String curTab;
    
    SavedState(Parcel paramParcel)
    {
      super();
      this.curTab = paramParcel.readString();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "FragmentTabHost.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " curTab=" + this.curTab + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeString(this.curTab);
    }
  }
  
  static final class TabInfo
  {
    @Nullable
    final Bundle args;
    @NonNull
    final Class<?> clss;
    Fragment fragment;
    @NonNull
    final String tag;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\app\FragmentTabHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */