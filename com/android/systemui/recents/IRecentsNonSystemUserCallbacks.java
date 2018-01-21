package com.android.systemui.recents;

import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IRecentsNonSystemUserCallbacks
  extends IInterface
{
  public abstract void cancelPreloadingRecents()
    throws RemoteException;
  
  public abstract void dockTopTask(int paramInt1, int paramInt2, int paramInt3, Rect paramRect)
    throws RemoteException;
  
  public abstract void hideRecents(boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void onConfigurationChanged()
    throws RemoteException;
  
  public abstract void onDraggingInRecents(float paramFloat)
    throws RemoteException;
  
  public abstract void onDraggingInRecentsEnded(float paramFloat)
    throws RemoteException;
  
  public abstract void preloadRecents()
    throws RemoteException;
  
  public abstract void showCurrentUserToast(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void showRecents(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, int paramInt)
    throws RemoteException;
  
  public abstract void toggleRecents(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRecentsNonSystemUserCallbacks
  {
    public Stub()
    {
      attachInterface(this, "com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
    }
    
    public static IRecentsNonSystemUserCallbacks asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
      if ((localIInterface != null) && ((localIInterface instanceof IRecentsNonSystemUserCallbacks))) {
        return (IRecentsNonSystemUserCallbacks)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      boolean bool1;
      boolean bool2;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
        preloadRecents();
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
        cancelPreloadingRecents();
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
        boolean bool3;
        boolean bool4;
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label224;
          }
          bool2 = true;
          if (paramParcel1.readInt() == 0) {
            break label230;
          }
          bool3 = true;
          if (paramParcel1.readInt() == 0) {
            break label236;
          }
          bool4 = true;
          if (paramParcel1.readInt() == 0) {
            break label242;
          }
        }
        for (boolean bool5 = true;; bool5 = false)
        {
          showRecents(bool1, bool2, bool3, bool4, bool5, paramParcel1.readInt());
          return true;
          bool1 = false;
          break;
          bool2 = false;
          break label168;
          bool3 = false;
          break label178;
          bool4 = false;
          break label188;
        }
      case 4: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label290;
          }
        }
        for (bool2 = true;; bool2 = false)
        {
          hideRecents(bool1, bool2);
          return true;
          bool1 = false;
          break;
        }
      case 5: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
        toggleRecents(paramParcel1.readInt());
        return true;
      case 6: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
        onConfigurationChanged();
        return true;
      case 7: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        int i = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          dockTopTask(paramInt1, paramInt2, i, paramParcel1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
        onDraggingInRecents(paramParcel1.readFloat());
        return true;
      case 9: 
        label168:
        label178:
        label188:
        label224:
        label230:
        label236:
        label242:
        label290:
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
        onDraggingInRecentsEnded(paramParcel1.readFloat());
        return true;
      }
      paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
      showCurrentUserToast(paramParcel1.readInt(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IRecentsNonSystemUserCallbacks
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void cancelPreloadingRecents()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void dockTopTask(int paramInt1, int paramInt2, int paramInt3, Rect paramRect)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: aload 5
        //   7: ldc 33
        //   9: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 5
        //   14: iload_1
        //   15: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   18: aload 5
        //   20: iload_2
        //   21: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   24: aload 5
        //   26: iload_3
        //   27: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   30: aload 4
        //   32: ifnull +39 -> 71
        //   35: aload 5
        //   37: iconst_1
        //   38: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   41: aload 4
        //   43: aload 5
        //   45: iconst_0
        //   46: invokevirtual 59	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   49: aload_0
        //   50: getfield 19	com/android/systemui/recents/IRecentsNonSystemUserCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   53: bipush 7
        //   55: aload 5
        //   57: aconst_null
        //   58: iconst_1
        //   59: invokeinterface 43 5 0
        //   64: pop
        //   65: aload 5
        //   67: invokevirtual 46	android/os/Parcel:recycle	()V
        //   70: return
        //   71: aload 5
        //   73: iconst_0
        //   74: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   77: goto -28 -> 49
        //   80: astore 4
        //   82: aload 5
        //   84: invokevirtual 46	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	90	0	this	Proxy
        //   0	90	1	paramInt1	int
        //   0	90	2	paramInt2	int
        //   0	90	3	paramInt3	int
        //   0	90	4	paramRect	Rect
        //   3	80	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	30	80	finally
        //   35	49	80	finally
        //   49	65	80	finally
        //   71	77	80	finally
      }
      
      /* Error */
      public void hideRecents(boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: iload_1
        //   16: ifeq +45 -> 61
        //   19: iconst_1
        //   20: istore_3
        //   21: aload 5
        //   23: iload_3
        //   24: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   27: iload_2
        //   28: ifeq +38 -> 66
        //   31: iload 4
        //   33: istore_3
        //   34: aload 5
        //   36: iload_3
        //   37: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   40: aload_0
        //   41: getfield 19	com/android/systemui/recents/IRecentsNonSystemUserCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   44: iconst_4
        //   45: aload 5
        //   47: aconst_null
        //   48: iconst_1
        //   49: invokeinterface 43 5 0
        //   54: pop
        //   55: aload 5
        //   57: invokevirtual 46	android/os/Parcel:recycle	()V
        //   60: return
        //   61: iconst_0
        //   62: istore_3
        //   63: goto -42 -> 21
        //   66: iconst_0
        //   67: istore_3
        //   68: goto -34 -> 34
        //   71: astore 6
        //   73: aload 5
        //   75: invokevirtual 46	android/os/Parcel:recycle	()V
        //   78: aload 6
        //   80: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	81	0	this	Proxy
        //   0	81	1	paramBoolean1	boolean
        //   0	81	2	paramBoolean2	boolean
        //   20	48	3	i	int
        //   1	31	4	j	int
        //   6	68	5	localParcel	Parcel
        //   71	8	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   8	15	71	finally
        //   21	27	71	finally
        //   34	55	71	finally
      }
      
      public void onConfigurationChanged()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onDraggingInRecents(float paramFloat)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
          localParcel.writeFloat(paramFloat);
          this.mRemote.transact(8, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onDraggingInRecentsEnded(float paramFloat)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
          localParcel.writeFloat(paramFloat);
          this.mRemote.transact(9, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void preloadRecents()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void showCurrentUserToast(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(10, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void showRecents(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 8
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 9
        //   8: aload 9
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: iload_1
        //   16: ifeq +100 -> 116
        //   19: iconst_1
        //   20: istore 7
        //   22: aload 9
        //   24: iload 7
        //   26: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   29: iload_2
        //   30: ifeq +92 -> 122
        //   33: iconst_1
        //   34: istore 7
        //   36: aload 9
        //   38: iload 7
        //   40: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   43: iload_3
        //   44: ifeq +84 -> 128
        //   47: iconst_1
        //   48: istore 7
        //   50: aload 9
        //   52: iload 7
        //   54: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   57: iload 4
        //   59: ifeq +75 -> 134
        //   62: iconst_1
        //   63: istore 7
        //   65: aload 9
        //   67: iload 7
        //   69: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   72: iload 5
        //   74: ifeq +66 -> 140
        //   77: iload 8
        //   79: istore 7
        //   81: aload 9
        //   83: iload 7
        //   85: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   88: aload 9
        //   90: iload 6
        //   92: invokevirtual 53	android/os/Parcel:writeInt	(I)V
        //   95: aload_0
        //   96: getfield 19	com/android/systemui/recents/IRecentsNonSystemUserCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   99: iconst_3
        //   100: aload 9
        //   102: aconst_null
        //   103: iconst_1
        //   104: invokeinterface 43 5 0
        //   109: pop
        //   110: aload 9
        //   112: invokevirtual 46	android/os/Parcel:recycle	()V
        //   115: return
        //   116: iconst_0
        //   117: istore 7
        //   119: goto -97 -> 22
        //   122: iconst_0
        //   123: istore 7
        //   125: goto -89 -> 36
        //   128: iconst_0
        //   129: istore 7
        //   131: goto -81 -> 50
        //   134: iconst_0
        //   135: istore 7
        //   137: goto -72 -> 65
        //   140: iconst_0
        //   141: istore 7
        //   143: goto -62 -> 81
        //   146: astore 10
        //   148: aload 9
        //   150: invokevirtual 46	android/os/Parcel:recycle	()V
        //   153: aload 10
        //   155: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	156	0	this	Proxy
        //   0	156	1	paramBoolean1	boolean
        //   0	156	2	paramBoolean2	boolean
        //   0	156	3	paramBoolean3	boolean
        //   0	156	4	paramBoolean4	boolean
        //   0	156	5	paramBoolean5	boolean
        //   0	156	6	paramInt	int
        //   20	122	7	i	int
        //   1	77	8	j	int
        //   6	143	9	localParcel	Parcel
        //   146	8	10	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   8	15	146	finally
        //   22	29	146	finally
        //   36	43	146	finally
        //   50	57	146	finally
        //   65	72	146	finally
        //   81	110	146	finally
      }
      
      public void toggleRecents(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsNonSystemUserCallbacks");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\IRecentsNonSystemUserCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */