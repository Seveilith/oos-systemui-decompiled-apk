package android.support.v4.app;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class FragmentManager
{
  public abstract FragmentTransaction beginTransaction();
  
  public abstract void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
  
  public abstract boolean executePendingTransactions();
  
  public abstract Fragment findFragmentByTag(String paramString);
  
  public abstract boolean popBackStackImmediate();
  
  public static abstract interface OnBackStackChangedListener
  {
    public abstract void onBackStackChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\app\FragmentManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */