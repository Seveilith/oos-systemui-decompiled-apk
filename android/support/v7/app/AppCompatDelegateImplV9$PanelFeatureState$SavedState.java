package android.support.v7.app;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

class AppCompatDelegateImplV9$PanelFeatureState$SavedState
  implements Parcelable
{
  public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
  {
    public AppCompatDelegateImplV9.PanelFeatureState.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
    {
      return AppCompatDelegateImplV9.PanelFeatureState.SavedState.readFromParcel(paramAnonymousParcel, paramAnonymousClassLoader);
    }
    
    public AppCompatDelegateImplV9.PanelFeatureState.SavedState[] newArray(int paramAnonymousInt)
    {
      return new AppCompatDelegateImplV9.PanelFeatureState.SavedState[paramAnonymousInt];
    }
  });
  int featureId;
  boolean isOpen;
  Bundle menuState;
  
  static SavedState readFromParcel(Parcel paramParcel, ClassLoader paramClassLoader)
  {
    boolean bool = true;
    SavedState localSavedState = new SavedState();
    localSavedState.featureId = paramParcel.readInt();
    if (paramParcel.readInt() == 1) {}
    for (;;)
    {
      localSavedState.isOpen = bool;
      if (localSavedState.isOpen) {
        localSavedState.menuState = paramParcel.readBundle(paramClassLoader);
      }
      return localSavedState;
      bool = false;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.featureId);
    if (this.isOpen) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      if (this.isOpen) {
        paramParcel.writeBundle(this.menuState);
      }
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\app\AppCompatDelegateImplV9$PanelFeatureState$SavedState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */