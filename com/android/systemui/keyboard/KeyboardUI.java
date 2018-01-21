package com.android.systemui.keyboard;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanFilter.Builder;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanSettings.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.OnTabletModeChangedListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.BluetoothEventManager;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.bluetooth.Utils;
import com.android.settingslib.bluetooth.Utils.ErrorListener;
import com.android.systemui.SystemUI;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class KeyboardUI
  extends SystemUI
  implements InputManager.OnTabletModeChangedListener
{
  private boolean mBootCompleted;
  private long mBootCompletedTime;
  private CachedBluetoothDeviceManager mCachedDeviceManager;
  protected volatile Context mContext;
  private BluetoothDialog mDialog;
  private boolean mEnabled;
  private volatile KeyboardHandler mHandler;
  private int mInTabletMode = -1;
  private String mKeyboardName;
  private LocalBluetoothAdapter mLocalBluetoothAdapter;
  private LocalBluetoothProfileManager mProfileManager;
  private int mScanAttempt = 0;
  private ScanCallback mScanCallback;
  private int mState;
  private volatile KeyboardUIHandler mUIHandler;
  
  private void bleAbortScanInternal(int paramInt)
  {
    if ((this.mState == 3) && (paramInt == this.mScanAttempt))
    {
      stopScanning();
      this.mState = 9;
    }
  }
  
  private CachedBluetoothDevice getCachedBluetoothDevice(BluetoothDevice paramBluetoothDevice)
  {
    CachedBluetoothDevice localCachedBluetoothDevice2 = this.mCachedDeviceManager.findDevice(paramBluetoothDevice);
    CachedBluetoothDevice localCachedBluetoothDevice1 = localCachedBluetoothDevice2;
    if (localCachedBluetoothDevice2 == null) {
      localCachedBluetoothDevice1 = this.mCachedDeviceManager.addDevice(this.mLocalBluetoothAdapter, this.mProfileManager, paramBluetoothDevice);
    }
    return localCachedBluetoothDevice1;
  }
  
  private CachedBluetoothDevice getDiscoveredKeyboard()
  {
    Iterator localIterator = this.mCachedDeviceManager.getCachedDevicesCopy().iterator();
    while (localIterator.hasNext())
    {
      CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice)localIterator.next();
      if (localCachedBluetoothDevice.getName().equals(this.mKeyboardName)) {
        return localCachedBluetoothDevice;
      }
    }
    return null;
  }
  
  private CachedBluetoothDevice getPairedKeyboard()
  {
    Iterator localIterator = this.mLocalBluetoothAdapter.getBondedDevices().iterator();
    while (localIterator.hasNext())
    {
      BluetoothDevice localBluetoothDevice = (BluetoothDevice)localIterator.next();
      if (this.mKeyboardName.equals(localBluetoothDevice.getName())) {
        return getCachedBluetoothDevice(localBluetoothDevice);
      }
    }
    return null;
  }
  
  private void init()
  {
    Object localObject = this.mContext;
    this.mKeyboardName = ((Context)localObject).getString(17039471);
    if (TextUtils.isEmpty(this.mKeyboardName)) {
      return;
    }
    LocalBluetoothManager localLocalBluetoothManager = LocalBluetoothManager.getInstance((Context)localObject, null);
    if (localLocalBluetoothManager == null) {
      return;
    }
    this.mEnabled = true;
    this.mCachedDeviceManager = localLocalBluetoothManager.getCachedDeviceManager();
    this.mLocalBluetoothAdapter = localLocalBluetoothManager.getBluetoothAdapter();
    this.mProfileManager = localLocalBluetoothManager.getProfileManager();
    localLocalBluetoothManager.getEventManager().registerCallback(new BluetoothCallbackHandler(null));
    Utils.setErrorListener(new BluetoothErrorListener(null));
    localObject = (InputManager)((Context)localObject).getSystemService(InputManager.class);
    ((InputManager)localObject).registerOnTabletModeChangedListener(this, this.mHandler);
    this.mInTabletMode = ((InputManager)localObject).isInTabletMode();
    processKeyboardState();
    this.mUIHandler = new KeyboardUIHandler();
  }
  
  private boolean isUserSetupComplete()
  {
    boolean bool = false;
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "user_setup_complete", 0, -2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void onBleScanFailedInternal()
  {
    this.mScanCallback = null;
    if (this.mState == 3) {
      this.mState = 9;
    }
  }
  
  private void onBluetoothStateChangedInternal(int paramInt)
  {
    if ((paramInt == 12) && (this.mState == 4)) {
      processKeyboardState();
    }
  }
  
  private void onDeviceAddedInternal(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    if ((this.mState == 3) && (paramCachedBluetoothDevice.getName().equals(this.mKeyboardName)))
    {
      stopScanning();
      paramCachedBluetoothDevice.startPairing();
      this.mState = 5;
    }
  }
  
  private void onDeviceBondStateChangedInternal(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
  {
    if ((this.mState == 5) && (paramCachedBluetoothDevice.getName().equals(this.mKeyboardName)))
    {
      if (paramInt != 12) {
        break label35;
      }
      this.mState = 6;
    }
    label35:
    while (paramInt != 10) {
      return;
    }
    this.mState = 7;
  }
  
  private void onShowErrorInternal(Context paramContext, String paramString, int paramInt)
  {
    if (((this.mState == 5) || (this.mState == 7)) && (this.mKeyboardName.equals(paramString))) {
      Toast.makeText(paramContext, paramContext.getString(paramInt, new Object[] { paramString }), 0).show();
    }
  }
  
  private void processKeyboardState()
  {
    this.mHandler.removeMessages(2);
    if (!this.mEnabled)
    {
      this.mState = -1;
      return;
    }
    if (!this.mBootCompleted)
    {
      this.mState = 1;
      return;
    }
    if (this.mInTabletMode != 0)
    {
      if (this.mState == 3) {
        stopScanning();
      }
      for (;;)
      {
        this.mState = 2;
        return;
        if (this.mState == 4) {
          this.mUIHandler.sendEmptyMessage(9);
        }
      }
    }
    int i = this.mLocalBluetoothAdapter.getState();
    if (((i == 11) || (i == 12)) && (this.mState == 4)) {
      this.mUIHandler.sendEmptyMessage(9);
    }
    if (i == 11)
    {
      this.mState = 4;
      return;
    }
    if (i != 12)
    {
      this.mState = 4;
      showBluetoothDialog();
      return;
    }
    CachedBluetoothDevice localCachedBluetoothDevice = getPairedKeyboard();
    if ((this.mState == 2) || (this.mState == 4))
    {
      if (localCachedBluetoothDevice != null)
      {
        this.mState = 6;
        localCachedBluetoothDevice.connect(false);
        return;
      }
      this.mCachedDeviceManager.clearNonBondedDevices();
    }
    localCachedBluetoothDevice = getDiscoveredKeyboard();
    if (localCachedBluetoothDevice != null)
    {
      this.mState = 5;
      localCachedBluetoothDevice.startPairing();
      return;
    }
    this.mState = 3;
    startScanning();
  }
  
  private void showBluetoothDialog()
  {
    if (isUserSetupComplete())
    {
      long l1 = SystemClock.uptimeMillis();
      long l2 = this.mBootCompletedTime + 10000L;
      if (l2 < l1)
      {
        this.mUIHandler.sendEmptyMessage(8);
        return;
      }
      this.mHandler.sendEmptyMessageAtTime(2, l2);
      return;
    }
    this.mLocalBluetoothAdapter.enable();
  }
  
  private void startScanning()
  {
    Object localObject = this.mLocalBluetoothAdapter.getBluetoothLeScanner();
    ScanFilter localScanFilter = new ScanFilter.Builder().setDeviceName(this.mKeyboardName).build();
    ScanSettings localScanSettings = new ScanSettings.Builder().setCallbackType(1).setNumOfMatches(1).setScanMode(2).setReportDelay(0L).build();
    this.mScanCallback = new KeyboardScanCallback(null);
    ((BluetoothLeScanner)localObject).startScan(Arrays.asList(new ScanFilter[] { localScanFilter }), localScanSettings, this.mScanCallback);
    localObject = this.mHandler;
    int i = this.mScanAttempt + 1;
    this.mScanAttempt = i;
    localObject = ((KeyboardHandler)localObject).obtainMessage(10, i, 0);
    this.mHandler.sendMessageDelayed((Message)localObject, 30000L);
  }
  
  private static String stateToString(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    default: 
      return "STATE_UNKNOWN (" + paramInt + ")";
    case -1: 
      return "STATE_NOT_ENABLED";
    case 1: 
      return "STATE_WAITING_FOR_BOOT_COMPLETED";
    case 2: 
      return "STATE_WAITING_FOR_TABLET_MODE_EXIT";
    case 3: 
      return "STATE_WAITING_FOR_DEVICE_DISCOVERY";
    case 4: 
      return "STATE_WAITING_FOR_BLUETOOTH";
    case 5: 
      return "STATE_PAIRING";
    case 6: 
      return "STATE_PAIRED";
    case 7: 
      return "STATE_PAIRING_FAILED";
    case 8: 
      return "STATE_USER_CANCELLED";
    }
    return "STATE_DEVICE_NOT_FOUND";
  }
  
  private void stopScanning()
  {
    if (this.mScanCallback != null)
    {
      BluetoothLeScanner localBluetoothLeScanner = this.mLocalBluetoothAdapter.getBluetoothLeScanner();
      if (localBluetoothLeScanner != null) {
        localBluetoothLeScanner.stopScan(this.mScanCallback);
      }
      this.mScanCallback = null;
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("KeyboardUI:");
    paramPrintWriter.println("  mEnabled=" + this.mEnabled);
    paramPrintWriter.println("  mBootCompleted=" + this.mEnabled);
    paramPrintWriter.println("  mBootCompletedTime=" + this.mBootCompletedTime);
    paramPrintWriter.println("  mKeyboardName=" + this.mKeyboardName);
    paramPrintWriter.println("  mInTabletMode=" + this.mInTabletMode);
    paramPrintWriter.println("  mState=" + stateToString(this.mState));
  }
  
  protected void onBootCompleted()
  {
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void onBootCompletedInternal()
  {
    this.mBootCompleted = true;
    this.mBootCompletedTime = SystemClock.uptimeMillis();
    if (this.mState == 1) {
      processKeyboardState();
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onTabletModeChanged(long paramLong, boolean paramBoolean)
  {
    int i = 1;
    if ((paramBoolean) && (this.mInTabletMode != 1)) {
      if (!paramBoolean) {
        break label44;
      }
    }
    for (;;)
    {
      this.mInTabletMode = i;
      processKeyboardState();
      do
      {
        return;
      } while ((paramBoolean) || (this.mInTabletMode == 0));
      break;
      label44:
      i = 0;
    }
  }
  
  public void start()
  {
    this.mContext = this.mContext;
    HandlerThread localHandlerThread = new HandlerThread("Keyboard", 10);
    localHandlerThread.start();
    this.mHandler = new KeyboardHandler(localHandlerThread.getLooper());
    this.mHandler.sendEmptyMessage(0);
  }
  
  private final class BluetoothCallbackHandler
    implements BluetoothCallback
  {
    private BluetoothCallbackHandler() {}
    
    public void onBluetoothStateChanged(int paramInt)
    {
      KeyboardUI.-get1(KeyboardUI.this).obtainMessage(4, paramInt, 0).sendToTarget();
    }
    
    public void onConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt) {}
    
    public void onDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice) {}
    
    public void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
    {
      KeyboardUI.-get1(KeyboardUI.this).obtainMessage(5, paramInt, 0, paramCachedBluetoothDevice).sendToTarget();
    }
    
    public void onDeviceDeleted(CachedBluetoothDevice paramCachedBluetoothDevice) {}
    
    public void onScanningStateChanged(boolean paramBoolean) {}
  }
  
  private final class BluetoothDialogClickListener
    implements DialogInterface.OnClickListener
  {
    private BluetoothDialogClickListener() {}
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (-1 == paramInt) {}
      for (paramInt = 1;; paramInt = 0)
      {
        KeyboardUI.-get1(KeyboardUI.this).obtainMessage(3, paramInt, 0).sendToTarget();
        KeyboardUI.-set0(KeyboardUI.this, null);
        return;
      }
    }
  }
  
  private final class BluetoothDialogDismissListener
    implements DialogInterface.OnDismissListener
  {
    private BluetoothDialogDismissListener() {}
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      KeyboardUI.-set0(KeyboardUI.this, null);
    }
  }
  
  private final class BluetoothErrorListener
    implements Utils.ErrorListener
  {
    private BluetoothErrorListener() {}
    
    public void onShowError(Context paramContext, String paramString, int paramInt)
    {
      KeyboardUI.-get1(KeyboardUI.this).obtainMessage(11, paramInt, 0, new Pair(paramContext, paramString)).sendToTarget();
    }
  }
  
  private final class KeyboardHandler
    extends Handler
  {
    public KeyboardHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = 1;
      switch (paramMessage.what)
      {
      case 8: 
      case 9: 
      default: 
        return;
      case 0: 
        KeyboardUI.-wrap2(KeyboardUI.this);
        return;
      case 1: 
        KeyboardUI.this.onBootCompletedInternal();
        return;
      case 2: 
        KeyboardUI.-wrap8(KeyboardUI.this);
        return;
      case 3: 
        if (paramMessage.arg1 == 1) {}
        while (i != 0)
        {
          KeyboardUI.-get2(KeyboardUI.this).enable();
          return;
          i = 0;
        }
        KeyboardUI.-set1(KeyboardUI.this, 8);
        return;
      case 10: 
        i = paramMessage.arg1;
        KeyboardUI.-wrap1(KeyboardUI.this, i);
        return;
      case 4: 
        i = paramMessage.arg1;
        KeyboardUI.-wrap4(KeyboardUI.this, i);
        return;
      case 5: 
        localObject = (CachedBluetoothDevice)paramMessage.obj;
        i = paramMessage.arg1;
        KeyboardUI.-wrap6(KeyboardUI.this, (CachedBluetoothDevice)localObject, i);
        return;
      case 6: 
        paramMessage = (BluetoothDevice)paramMessage.obj;
        paramMessage = KeyboardUI.-wrap0(KeyboardUI.this, paramMessage);
        KeyboardUI.-wrap5(KeyboardUI.this, paramMessage);
        return;
      case 7: 
        KeyboardUI.-wrap3(KeyboardUI.this);
        return;
      }
      Object localObject = (Pair)paramMessage.obj;
      KeyboardUI.-wrap7(KeyboardUI.this, (Context)((Pair)localObject).first, (String)((Pair)localObject).second, paramMessage.arg1);
    }
  }
  
  private final class KeyboardScanCallback
    extends ScanCallback
  {
    private KeyboardScanCallback() {}
    
    private boolean isDeviceDiscoverable(ScanResult paramScanResult)
    {
      boolean bool = false;
      if ((paramScanResult.getScanRecord().getAdvertiseFlags() & 0x3) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public void onBatchScanResults(List<ScanResult> paramList)
    {
      ScanResult localScanResult = null;
      int i = Integer.MIN_VALUE;
      Iterator localIterator = paramList.iterator();
      paramList = localScanResult;
      while (localIterator.hasNext())
      {
        localScanResult = (ScanResult)localIterator.next();
        if ((isDeviceDiscoverable(localScanResult)) && (localScanResult.getRssi() > i))
        {
          paramList = localScanResult.getDevice();
          i = localScanResult.getRssi();
        }
      }
      if (paramList != null) {
        KeyboardUI.-get1(KeyboardUI.this).obtainMessage(6, paramList).sendToTarget();
      }
    }
    
    public void onScanFailed(int paramInt)
    {
      KeyboardUI.-get1(KeyboardUI.this).obtainMessage(7).sendToTarget();
    }
    
    public void onScanResult(int paramInt, ScanResult paramScanResult)
    {
      if (isDeviceDiscoverable(paramScanResult)) {
        KeyboardUI.-get1(KeyboardUI.this).obtainMessage(6, paramScanResult.getDevice()).sendToTarget();
      }
    }
  }
  
  private final class KeyboardUIHandler
    extends Handler
  {
    public KeyboardUIHandler()
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      do
      {
        do
        {
          return;
        } while (KeyboardUI.-get0(KeyboardUI.this) != null);
        paramMessage = new KeyboardUI.BluetoothDialogClickListener(KeyboardUI.this, null);
        KeyboardUI.BluetoothDialogDismissListener localBluetoothDialogDismissListener = new KeyboardUI.BluetoothDialogDismissListener(KeyboardUI.this, null);
        KeyboardUI.-set0(KeyboardUI.this, new BluetoothDialog(KeyboardUI.this.mContext));
        KeyboardUI.-get0(KeyboardUI.this).setTitle(2131690496);
        KeyboardUI.-get0(KeyboardUI.this).setMessage(2131690497);
        KeyboardUI.-get0(KeyboardUI.this).setPositiveButton(2131690498, paramMessage);
        KeyboardUI.-get0(KeyboardUI.this).setNegativeButton(17039360, paramMessage);
        KeyboardUI.-get0(KeyboardUI.this).setOnDismissListener(localBluetoothDialogDismissListener);
        KeyboardUI.-get0(KeyboardUI.this).show();
        return;
      } while (KeyboardUI.-get0(KeyboardUI.this) == null);
      KeyboardUI.-get0(KeyboardUI.this).dismiss();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\keyboard\KeyboardUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */