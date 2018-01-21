package android.support.v4.app;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader.OnLoadCanceledListener;
import android.support.v4.content.Loader.OnLoadCompleteListener;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;

class LoaderManagerImpl
  extends LoaderManager
{
  static boolean DEBUG = false;
  FragmentHostCallback mHost;
  final SparseArrayCompat<LoaderInfo> mInactiveLoaders = new SparseArrayCompat();
  final SparseArrayCompat<LoaderInfo> mLoaders = new SparseArrayCompat();
  boolean mRetaining;
  boolean mStarted;
  final String mWho;
  
  LoaderManagerImpl(String paramString, FragmentHostCallback paramFragmentHostCallback, boolean paramBoolean)
  {
    this.mWho = paramString;
    this.mHost = paramFragmentHostCallback;
    this.mStarted = paramBoolean;
  }
  
  void doDestroy()
  {
    if (!this.mRetaining)
    {
      if (DEBUG) {
        Log.v("LoaderManager", "Destroying Active in " + this);
      }
      i = this.mLoaders.size() - 1;
      while (i >= 0)
      {
        ((LoaderInfo)this.mLoaders.valueAt(i)).destroy();
        i -= 1;
      }
      this.mLoaders.clear();
    }
    if (DEBUG) {
      Log.v("LoaderManager", "Destroying Inactive in " + this);
    }
    int i = this.mInactiveLoaders.size() - 1;
    while (i >= 0)
    {
      ((LoaderInfo)this.mInactiveLoaders.valueAt(i)).destroy();
      i -= 1;
    }
    this.mInactiveLoaders.clear();
  }
  
  void doReportNextStart()
  {
    int i = this.mLoaders.size() - 1;
    while (i >= 0)
    {
      ((LoaderInfo)this.mLoaders.valueAt(i)).mReportNextStart = true;
      i -= 1;
    }
  }
  
  void doReportStart()
  {
    int i = this.mLoaders.size() - 1;
    while (i >= 0)
    {
      ((LoaderInfo)this.mLoaders.valueAt(i)).reportStart();
      i -= 1;
    }
  }
  
  void doRetain()
  {
    if (DEBUG) {
      Log.v("LoaderManager", "Retaining in " + this);
    }
    if (!this.mStarted)
    {
      RuntimeException localRuntimeException = new RuntimeException("here");
      localRuntimeException.fillInStackTrace();
      Log.w("LoaderManager", "Called doRetain when not started: " + this, localRuntimeException);
      return;
    }
    this.mRetaining = true;
    this.mStarted = false;
    int i = this.mLoaders.size() - 1;
    while (i >= 0)
    {
      ((LoaderInfo)this.mLoaders.valueAt(i)).retain();
      i -= 1;
    }
  }
  
  void doStart()
  {
    if (DEBUG) {
      Log.v("LoaderManager", "Starting in " + this);
    }
    if (this.mStarted)
    {
      RuntimeException localRuntimeException = new RuntimeException("here");
      localRuntimeException.fillInStackTrace();
      Log.w("LoaderManager", "Called doStart when already started: " + this, localRuntimeException);
      return;
    }
    this.mStarted = true;
    int i = this.mLoaders.size() - 1;
    while (i >= 0)
    {
      ((LoaderInfo)this.mLoaders.valueAt(i)).start();
      i -= 1;
    }
  }
  
  void doStop()
  {
    if (DEBUG) {
      Log.v("LoaderManager", "Stopping in " + this);
    }
    if (!this.mStarted)
    {
      RuntimeException localRuntimeException = new RuntimeException("here");
      localRuntimeException.fillInStackTrace();
      Log.w("LoaderManager", "Called doStop when not started: " + this, localRuntimeException);
      return;
    }
    int i = this.mLoaders.size() - 1;
    while (i >= 0)
    {
      ((LoaderInfo)this.mLoaders.valueAt(i)).stop();
      i -= 1;
    }
    this.mStarted = false;
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str;
    int i;
    LoaderInfo localLoaderInfo;
    if (this.mLoaders.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Active Loaders:");
      str = paramString + "    ";
      i = 0;
      while (i < this.mLoaders.size())
      {
        localLoaderInfo = (LoaderInfo)this.mLoaders.valueAt(i);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  #");
        paramPrintWriter.print(this.mLoaders.keyAt(i));
        paramPrintWriter.print(": ");
        paramPrintWriter.println(localLoaderInfo.toString());
        localLoaderInfo.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        i += 1;
      }
    }
    if (this.mInactiveLoaders.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Inactive Loaders:");
      str = paramString + "    ";
      i = 0;
      while (i < this.mInactiveLoaders.size())
      {
        localLoaderInfo = (LoaderInfo)this.mInactiveLoaders.valueAt(i);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  #");
        paramPrintWriter.print(this.mInactiveLoaders.keyAt(i));
        paramPrintWriter.print(": ");
        paramPrintWriter.println(localLoaderInfo.toString());
        localLoaderInfo.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        i += 1;
      }
    }
  }
  
  void finishRetain()
  {
    if (this.mRetaining)
    {
      if (DEBUG) {
        Log.v("LoaderManager", "Finished Retaining in " + this);
      }
      this.mRetaining = false;
      int i = this.mLoaders.size() - 1;
      while (i >= 0)
      {
        ((LoaderInfo)this.mLoaders.valueAt(i)).finishRetain();
        i -= 1;
      }
    }
  }
  
  public boolean hasRunningLoaders()
  {
    boolean bool2 = false;
    int j = this.mLoaders.size();
    int i = 0;
    if (i < j)
    {
      LoaderInfo localLoaderInfo = (LoaderInfo)this.mLoaders.valueAt(i);
      if ((!localLoaderInfo.mStarted) || (localLoaderInfo.mDeliveredData)) {}
      for (boolean bool1 = false;; bool1 = true)
      {
        bool2 |= bool1;
        i += 1;
        break;
      }
    }
    return bool2;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("LoaderManager{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" in ");
    DebugUtils.buildShortClassTag(this.mHost, localStringBuilder);
    localStringBuilder.append("}}");
    return localStringBuilder.toString();
  }
  
  void updateHostController(FragmentHostCallback paramFragmentHostCallback)
  {
    this.mHost = paramFragmentHostCallback;
  }
  
  final class LoaderInfo
    implements Loader.OnLoadCompleteListener<Object>, Loader.OnLoadCanceledListener<Object>
  {
    final Bundle mArgs;
    LoaderManager.LoaderCallbacks<Object> mCallbacks;
    Object mData;
    boolean mDeliveredData;
    boolean mDestroyed;
    boolean mHaveData;
    final int mId;
    boolean mListenerRegistered;
    Loader<Object> mLoader;
    LoaderInfo mPendingLoader;
    boolean mReportNextStart;
    boolean mRetaining;
    boolean mRetainingStarted;
    boolean mStarted;
    
    void callOnLoadFinished(Loader<Object> paramLoader, Object paramObject)
    {
      String str;
      if (this.mCallbacks != null)
      {
        str = null;
        if (this.this$0.mHost != null)
        {
          str = this.this$0.mHost.mFragmentManager.mNoTransactionsBecause;
          this.this$0.mHost.mFragmentManager.mNoTransactionsBecause = "onLoadFinished";
        }
      }
      try
      {
        if (LoaderManagerImpl.DEBUG) {
          Log.v("LoaderManager", "  onLoadFinished in " + paramLoader + ": " + paramLoader.dataToString(paramObject));
        }
        this.mCallbacks.onLoadFinished(paramLoader, paramObject);
        if (this.this$0.mHost != null) {
          this.this$0.mHost.mFragmentManager.mNoTransactionsBecause = str;
        }
        this.mDeliveredData = true;
        return;
      }
      finally
      {
        if (this.this$0.mHost != null) {
          this.this$0.mHost.mFragmentManager.mNoTransactionsBecause = str;
        }
      }
    }
    
    void destroy()
    {
      if (LoaderManagerImpl.DEBUG) {
        Log.v("LoaderManager", "  Destroying: " + this);
      }
      this.mDestroyed = true;
      boolean bool = this.mDeliveredData;
      this.mDeliveredData = false;
      String str;
      if ((this.mCallbacks != null) && (this.mLoader != null) && (this.mHaveData) && (bool))
      {
        if (LoaderManagerImpl.DEBUG) {
          Log.v("LoaderManager", "  Resetting: " + this);
        }
        str = null;
        if (this.this$0.mHost != null)
        {
          str = this.this$0.mHost.mFragmentManager.mNoTransactionsBecause;
          this.this$0.mHost.mFragmentManager.mNoTransactionsBecause = "onLoaderReset";
        }
      }
      try
      {
        this.mCallbacks.onLoaderReset(this.mLoader);
        if (this.this$0.mHost != null) {
          this.this$0.mHost.mFragmentManager.mNoTransactionsBecause = str;
        }
        this.mCallbacks = null;
        this.mData = null;
        this.mHaveData = false;
        if (this.mLoader != null)
        {
          if (this.mListenerRegistered)
          {
            this.mListenerRegistered = false;
            this.mLoader.unregisterListener(this);
            this.mLoader.unregisterOnLoadCanceledListener(this);
          }
          this.mLoader.reset();
        }
        if (this.mPendingLoader != null) {
          this.mPendingLoader.destroy();
        }
        return;
      }
      finally
      {
        if (this.this$0.mHost != null) {
          this.this$0.mHost.mFragmentManager.mNoTransactionsBecause = str;
        }
      }
    }
    
    public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mId=");
      paramPrintWriter.print(this.mId);
      paramPrintWriter.print(" mArgs=");
      paramPrintWriter.println(this.mArgs);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCallbacks=");
      paramPrintWriter.println(this.mCallbacks);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mLoader=");
      paramPrintWriter.println(this.mLoader);
      if (this.mLoader != null) {
        this.mLoader.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      }
      if ((this.mHaveData) || (this.mDeliveredData))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mHaveData=");
        paramPrintWriter.print(this.mHaveData);
        paramPrintWriter.print("  mDeliveredData=");
        paramPrintWriter.println(this.mDeliveredData);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mData=");
        paramPrintWriter.println(this.mData);
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mStarted=");
      paramPrintWriter.print(this.mStarted);
      paramPrintWriter.print(" mReportNextStart=");
      paramPrintWriter.print(this.mReportNextStart);
      paramPrintWriter.print(" mDestroyed=");
      paramPrintWriter.println(this.mDestroyed);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mRetaining=");
      paramPrintWriter.print(this.mRetaining);
      paramPrintWriter.print(" mRetainingStarted=");
      paramPrintWriter.print(this.mRetainingStarted);
      paramPrintWriter.print(" mListenerRegistered=");
      paramPrintWriter.println(this.mListenerRegistered);
      if (this.mPendingLoader != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Pending Loader ");
        paramPrintWriter.print(this.mPendingLoader);
        paramPrintWriter.println(":");
        this.mPendingLoader.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      }
    }
    
    void finishRetain()
    {
      if (this.mRetaining)
      {
        if (LoaderManagerImpl.DEBUG) {
          Log.v("LoaderManager", "  Finished Retaining: " + this);
        }
        this.mRetaining = false;
        if ((this.mStarted != this.mRetainingStarted) && (!this.mStarted)) {
          stop();
        }
      }
      if ((!this.mStarted) || (!this.mHaveData) || (this.mReportNextStart)) {
        return;
      }
      callOnLoadFinished(this.mLoader, this.mData);
    }
    
    void reportStart()
    {
      if ((this.mStarted) && (this.mReportNextStart))
      {
        this.mReportNextStart = false;
        if ((this.mHaveData) && (!this.mRetaining)) {}
      }
      else
      {
        return;
      }
      callOnLoadFinished(this.mLoader, this.mData);
    }
    
    void retain()
    {
      if (LoaderManagerImpl.DEBUG) {
        Log.v("LoaderManager", "  Retaining: " + this);
      }
      this.mRetaining = true;
      this.mRetainingStarted = this.mStarted;
      this.mStarted = false;
      this.mCallbacks = null;
    }
    
    void start()
    {
      if ((this.mRetaining) && (this.mRetainingStarted))
      {
        this.mStarted = true;
        return;
      }
      if (this.mStarted) {
        return;
      }
      this.mStarted = true;
      if (LoaderManagerImpl.DEBUG) {
        Log.v("LoaderManager", "  Starting: " + this);
      }
      if ((this.mLoader == null) && (this.mCallbacks != null)) {
        this.mLoader = this.mCallbacks.onCreateLoader(this.mId, this.mArgs);
      }
      if (this.mLoader != null)
      {
        if ((!this.mLoader.getClass().isMemberClass()) || (Modifier.isStatic(this.mLoader.getClass().getModifiers())))
        {
          if (!this.mListenerRegistered)
          {
            this.mLoader.registerListener(this.mId, this);
            this.mLoader.registerOnLoadCanceledListener(this);
            this.mListenerRegistered = true;
          }
          this.mLoader.startLoading();
        }
      }
      else {
        return;
      }
      throw new IllegalArgumentException("Object returned from onCreateLoader must not be a non-static inner member class: " + this.mLoader);
    }
    
    void stop()
    {
      if (LoaderManagerImpl.DEBUG) {
        Log.v("LoaderManager", "  Stopping: " + this);
      }
      this.mStarted = false;
      if ((!this.mRetaining) && (this.mLoader != null) && (this.mListenerRegistered))
      {
        this.mListenerRegistered = false;
        this.mLoader.unregisterListener(this);
        this.mLoader.unregisterOnLoadCanceledListener(this);
        this.mLoader.stopLoading();
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(64);
      localStringBuilder.append("LoaderInfo{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" #");
      localStringBuilder.append(this.mId);
      localStringBuilder.append(" : ");
      DebugUtils.buildShortClassTag(this.mLoader, localStringBuilder);
      localStringBuilder.append("}}");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\app\LoaderManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */