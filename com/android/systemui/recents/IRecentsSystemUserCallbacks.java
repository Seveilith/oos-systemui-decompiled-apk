package com.android.systemui.recents;

import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IRecentsSystemUserCallbacks
  extends IInterface
{
  public abstract void registerNonSystemUserCallbacks(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void sendDockingTopTaskEvent(int paramInt, Rect paramRect)
    throws RemoteException;
  
  public abstract void sendLaunchRecentsEvent()
    throws RemoteException;
  
  public abstract void sendRecentsDrawnEvent()
    throws RemoteException;
  
  public abstract void startScreenPinning(int paramInt)
    throws RemoteException;
  
  public abstract void updateRecentsVisibility(boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRecentsSystemUserCallbacks
  {
    public Stub()
    {
      attachInterface(this, "com.android.systemui.recents.IRecentsSystemUserCallbacks");
    }
    
    public static IRecentsSystemUserCallbacks asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.android.systemui.recents.IRecentsSystemUserCallbacks");
      if ((localIInterface != null) && ((localIInterface instanceof IRecentsSystemUserCallbacks))) {
        return (IRecentsSystemUserCallbacks)localIInterface;
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
      boolean bool = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.android.systemui.recents.IRecentsSystemUserCallbacks");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsSystemUserCallbacks");
        registerNonSystemUserCallbacks(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsSystemUserCallbacks");
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        updateRecentsVisibility(bool);
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsSystemUserCallbacks");
        startScreenPinning(paramParcel1.readInt());
        return true;
      case 4: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsSystemUserCallbacks");
        sendRecentsDrawnEvent();
        return true;
      case 5: 
        paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsSystemUserCallbacks");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          sendDockingTopTaskEvent(paramInt1, paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("com.android.systemui.recents.IRecentsSystemUserCallbacks");
      sendLaunchRecentsEvent();
      return true;
    }
    
    private static class Proxy
      implements IRecentsSystemUserCallbacks
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
      
      public void registerNonSystemUserCallbacks(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsSystemUserCallbacks");
          localParcel.writeStrongBinder(paramIBinder);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void sendDockingTopTaskEvent(int paramInt, Rect paramRect)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: iload_1
        //   12: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   15: aload_2
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 63	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	com/android/systemui/recents/IRecentsSystemUserCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_5
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 51 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 54	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_2
        //   58: aload_3
        //   59: invokevirtual 54	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramInt	int
        //   0	64	2	paramRect	Rect
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	57	finally
        //   19	30	57	finally
        //   30	44	57	finally
        //   49	54	57	finally
      }
      
      public void sendLaunchRecentsEvent()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsSystemUserCallbacks");
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void sendRecentsDrawnEvent()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsSystemUserCallbacks");
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void startScreenPinning(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.android.systemui.recents.IRecentsSystemUserCallbacks");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void updateRecentsVisibility(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: aload_3
        //   7: ldc 34
        //   9: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: iload_1
        //   13: ifeq +27 -> 40
        //   16: aload_3
        //   17: iload_2
        //   18: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   21: aload_0
        //   22: getfield 19	com/android/systemui/recents/IRecentsSystemUserCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   25: iconst_2
        //   26: aload_3
        //   27: aconst_null
        //   28: iconst_1
        //   29: invokeinterface 51 5 0
        //   34: pop
        //   35: aload_3
        //   36: invokevirtual 54	android/os/Parcel:recycle	()V
        //   39: return
        //   40: iconst_0
        //   41: istore_2
        //   42: goto -26 -> 16
        //   45: astore 4
        //   47: aload_3
        //   48: invokevirtual 54	android/os/Parcel:recycle	()V
        //   51: aload 4
        //   53: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	54	0	this	Proxy
        //   0	54	1	paramBoolean	boolean
        //   1	41	2	i	int
        //   5	43	3	localParcel	Parcel
        //   45	7	4	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   6	12	45	finally
        //   16	35	45	finally
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\IRecentsSystemUserCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */