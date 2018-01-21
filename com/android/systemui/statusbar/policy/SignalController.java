package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import java.io.PrintWriter;
import java.util.BitSet;

public abstract class SignalController<T extends State, I extends IconGroup>
{
  protected static final boolean CHATTY = NetworkControllerImpl.CHATTY;
  protected static final boolean DEBUG = NetworkControllerImpl.DEBUG;
  protected final CallbackHandler mCallbackHandler;
  protected final Context mContext;
  protected final T mCurrentState;
  private final State[] mHistory;
  private int mHistoryIndex;
  protected final T mLastState;
  protected final NetworkControllerImpl mNetworkController;
  protected final String mTag;
  protected final int mTransportType;
  
  public SignalController(String paramString, Context paramContext, int paramInt, CallbackHandler paramCallbackHandler, NetworkControllerImpl paramNetworkControllerImpl)
  {
    this.mTag = ("NetworkController." + paramString);
    this.mNetworkController = paramNetworkControllerImpl;
    this.mTransportType = paramInt;
    this.mContext = paramContext;
    this.mCallbackHandler = paramCallbackHandler;
    this.mCurrentState = cleanState();
    this.mLastState = cleanState();
    this.mHistory = new State[64];
    paramInt = 0;
    while (paramInt < 64)
    {
      this.mHistory[paramInt] = cleanState();
      paramInt += 1;
    }
  }
  
  protected abstract T cleanState();
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("  - " + this.mTag + " -----");
    paramPrintWriter.println("  Current State: " + this.mCurrentState);
    int i = 0;
    int j = 0;
    while (j < 64)
    {
      int k = i;
      if (this.mHistory[j].time != 0L) {
        k = i + 1;
      }
      j += 1;
      i = k;
    }
    j = this.mHistoryIndex + 64 - 1;
    while (j >= this.mHistoryIndex + 64 - i)
    {
      paramPrintWriter.println("  Previous State(" + (this.mHistoryIndex + 64 - j) + "): " + this.mHistory[(j & 0x3F)]);
      j -= 1;
    }
  }
  
  public int getContentDescription()
  {
    if (this.mCurrentState.connected) {
      return getIcons().mContentDesc[this.mCurrentState.level];
    }
    return getIcons().mDiscContentDesc;
  }
  
  public int getCurrentIconId()
  {
    if (this.mCurrentState.connected) {
      return getIcons().mSbIcons[this.mCurrentState.inetCondition][this.mCurrentState.level];
    }
    if (this.mCurrentState.enabled) {
      return getIcons().mSbDiscState;
    }
    return getIcons().mSbNullState;
  }
  
  protected I getIcons()
  {
    return this.mCurrentState.iconGroup;
  }
  
  public int getQsCurrentIconId()
  {
    if (this.mCurrentState.connected) {
      return getIcons().mQsIcons[this.mCurrentState.inetCondition][this.mCurrentState.level];
    }
    if (this.mCurrentState.enabled) {
      return getIcons().mQsDiscState;
    }
    return getIcons().mQsNullState;
  }
  
  public T getState()
  {
    return this.mCurrentState;
  }
  
  protected String getStringIfExists(int paramInt)
  {
    if (paramInt != 0) {
      return this.mContext.getString(paramInt);
    }
    return "";
  }
  
  public boolean isDirty()
  {
    if (!this.mLastState.equals(this.mCurrentState))
    {
      if (DEBUG) {
        Log.d(this.mTag, "Change in state from: " + this.mLastState + "\n" + "\tto: " + this.mCurrentState);
      }
      return true;
    }
    return false;
  }
  
  public final void notifyListeners()
  {
    notifyListeners(this.mCallbackHandler);
  }
  
  public abstract void notifyListeners(NetworkController.SignalCallback paramSignalCallback);
  
  public void notifyListenersIfNecessary()
  {
    if (isDirty())
    {
      saveLastState();
      notifyListeners();
    }
  }
  
  protected void recordLastState()
  {
    State[] arrayOfState = this.mHistory;
    int i = this.mHistoryIndex;
    this.mHistoryIndex = (i + 1);
    arrayOfState[(i & 0x3F)].copyFrom(this.mLastState);
  }
  
  public void resetLastState()
  {
    this.mCurrentState.copyFrom(this.mLastState);
  }
  
  public void saveLastState()
  {
    recordLastState();
    this.mCurrentState.time = System.currentTimeMillis();
    this.mLastState.copyFrom(this.mCurrentState);
  }
  
  public void updateConnectivity(BitSet paramBitSet1, BitSet paramBitSet2)
  {
    paramBitSet1 = this.mCurrentState;
    if (paramBitSet2.get(this.mTransportType)) {}
    for (int i = 1;; i = 0)
    {
      paramBitSet1.inetCondition = i;
      notifyListenersIfNecessary();
      return;
    }
  }
  
  static class IconGroup
  {
    final int[] mContentDesc;
    final int mDiscContentDesc;
    final String mName;
    final int mQsDiscState;
    final int[][] mQsIcons;
    final int mQsNullState;
    final int mSbDiscState;
    final int[][] mSbIcons;
    final int mSbNullState;
    
    public IconGroup(String paramString, int[][] paramArrayOfInt1, int[][] paramArrayOfInt2, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.mName = paramString;
      this.mSbIcons = paramArrayOfInt1;
      this.mQsIcons = paramArrayOfInt2;
      this.mContentDesc = paramArrayOfInt;
      this.mSbNullState = paramInt1;
      this.mQsNullState = paramInt2;
      this.mSbDiscState = paramInt3;
      this.mQsDiscState = paramInt4;
      this.mDiscContentDesc = paramInt5;
    }
    
    public String toString()
    {
      return "IconGroup(" + this.mName + ")";
    }
  }
  
  static class State
  {
    boolean activityIn;
    boolean activityOut;
    boolean connected;
    boolean enabled;
    SignalController.IconGroup iconGroup;
    int inetCondition;
    int level;
    int rssi;
    long time;
    
    public void copyFrom(State paramState)
    {
      this.connected = paramState.connected;
      this.enabled = paramState.enabled;
      this.level = paramState.level;
      this.iconGroup = paramState.iconGroup;
      this.inetCondition = paramState.inetCondition;
      this.activityIn = paramState.activityIn;
      this.activityOut = paramState.activityOut;
      this.rssi = paramState.rssi;
      this.time = paramState.time;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!paramObject.getClass().equals(getClass())) {
        return false;
      }
      paramObject = (State)paramObject;
      boolean bool1 = bool2;
      if (((State)paramObject).connected == this.connected)
      {
        bool1 = bool2;
        if (((State)paramObject).enabled == this.enabled)
        {
          bool1 = bool2;
          if (((State)paramObject).level == this.level)
          {
            bool1 = bool2;
            if (((State)paramObject).inetCondition == this.inetCondition)
            {
              bool1 = bool2;
              if (((State)paramObject).iconGroup == this.iconGroup)
              {
                bool1 = bool2;
                if (((State)paramObject).activityIn == this.activityIn)
                {
                  bool1 = bool2;
                  if (((State)paramObject).activityOut == this.activityOut)
                  {
                    bool1 = bool2;
                    if (((State)paramObject).rssi == this.rssi) {
                      bool1 = true;
                    }
                  }
                }
              }
            }
          }
        }
      }
      return bool1;
    }
    
    public String toString()
    {
      if (this.time != 0L)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        toString(localStringBuilder);
        return localStringBuilder.toString();
      }
      return "Empty " + getClass().getSimpleName();
    }
    
    protected void toString(StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append("connected=").append(this.connected).append(',').append("enabled=").append(this.enabled).append(',').append("level=").append(this.level).append(',').append("inetCondition=").append(this.inetCondition).append(',').append("iconGroup=").append(this.iconGroup).append(',').append("activityIn=").append(this.activityIn).append(',').append("activityOut=").append(this.activityOut).append(',').append("rssi=").append(this.rssi).append(',').append("lastModified=").append(DateFormat.format("MM-dd hh:mm:ss", this.time));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\SignalController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */