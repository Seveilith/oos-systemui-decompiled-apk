package com.android.systemui.statusbar;

import android.app.AlertDialog.Builder;
import android.app.AppGlobals;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyboardShortcutGroup;
import android.view.KeyboardShortcutInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager.KeyboardShortcutsReceiver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.android.internal.app.AssistUtils;
import com.android.settingslib.Utils;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.misc.SystemServicesProxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class KeyboardShortcuts
{
  private static final String TAG = KeyboardShortcuts.class.getSimpleName();
  private static KeyboardShortcuts sInstance;
  private static final Object sLock = new Object();
  private final Comparator<KeyboardShortcutInfo> mApplicationItemsComparator = new Comparator()
  {
    public int compare(KeyboardShortcutInfo paramAnonymousKeyboardShortcutInfo1, KeyboardShortcutInfo paramAnonymousKeyboardShortcutInfo2)
    {
      boolean bool1;
      if (paramAnonymousKeyboardShortcutInfo1.getLabel() != null)
      {
        bool1 = paramAnonymousKeyboardShortcutInfo1.getLabel().toString().isEmpty();
        if (paramAnonymousKeyboardShortcutInfo2.getLabel() == null) {
          break label57;
        }
      }
      label57:
      for (boolean bool2 = paramAnonymousKeyboardShortcutInfo2.getLabel().toString().isEmpty();; bool2 = true)
      {
        if ((!bool1) || (!bool2)) {
          break label63;
        }
        return 0;
        bool1 = true;
        break;
      }
      label63:
      if (bool1) {
        return 1;
      }
      if (bool2) {
        return -1;
      }
      return paramAnonymousKeyboardShortcutInfo1.getLabel().toString().compareToIgnoreCase(paramAnonymousKeyboardShortcutInfo2.getLabel().toString());
    }
  };
  private final Context mContext;
  private final DialogInterface.OnClickListener mDialogCloseListener = new DialogInterface.OnClickListener()
  {
    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    {
      KeyboardShortcuts.-wrap2(KeyboardShortcuts.this);
    }
  };
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  private KeyCharacterMap mKeyCharacterMap;
  private Dialog mKeyboardShortcutsDialog;
  private final SparseArray<Drawable> mModifierDrawables = new SparseArray();
  private final SparseArray<String> mModifierNames = new SparseArray();
  private final IPackageManager mPackageManager;
  private final SparseArray<Drawable> mSpecialCharacterDrawables = new SparseArray();
  private final SparseArray<String> mSpecialCharacterNames = new SparseArray();
  
  private KeyboardShortcuts(Context paramContext)
  {
    this.mContext = new ContextThemeWrapper(paramContext, 16974123);
    this.mPackageManager = AppGlobals.getPackageManager();
    loadResources(paramContext);
  }
  
  public static void dismiss()
  {
    synchronized (sLock)
    {
      if (sInstance != null)
      {
        sInstance.dismissKeyboardShortcuts();
        sInstance = null;
      }
      return;
    }
  }
  
  private void dismissKeyboardShortcuts()
  {
    if (this.mKeyboardShortcutsDialog != null)
    {
      this.mKeyboardShortcutsDialog.dismiss();
      this.mKeyboardShortcutsDialog = null;
    }
  }
  
  private KeyboardShortcutGroup getDefaultApplicationShortcuts()
  {
    int i = this.mContext.getUserId();
    ArrayList localArrayList = new ArrayList();
    ComponentName localComponentName = new AssistUtils(this.mContext).getAssistComponentForUser(i);
    Object localObject3 = null;
    Object localObject1 = localObject3;
    if (localComponentName != null) {}
    try
    {
      localObject1 = this.mPackageManager.getPackageInfo(localComponentName.getPackageName(), 0, i);
      if (localObject1 != null)
      {
        localObject1 = Icon.createWithResource(((PackageInfo)localObject1).applicationInfo.packageName, ((PackageInfo)localObject1).applicationInfo.icon);
        localArrayList.add(new KeyboardShortcutInfo(this.mContext.getString(2131690562), (Icon)localObject1, 0, 65536));
      }
      localObject1 = getIconForIntentCategory("android.intent.category.APP_BROWSER", i);
      if (localObject1 != null) {
        localArrayList.add(new KeyboardShortcutInfo(this.mContext.getString(2131690563), (Icon)localObject1, 30, 65536));
      }
      localObject1 = getIconForIntentCategory("android.intent.category.APP_CONTACTS", i);
      if (localObject1 != null) {
        localArrayList.add(new KeyboardShortcutInfo(this.mContext.getString(2131690564), (Icon)localObject1, 31, 65536));
      }
      localObject1 = getIconForIntentCategory("android.intent.category.APP_EMAIL", i);
      if (localObject1 != null) {
        localArrayList.add(new KeyboardShortcutInfo(this.mContext.getString(2131690565), (Icon)localObject1, 33, 65536));
      }
      localObject1 = getIconForIntentCategory("android.intent.category.APP_MESSAGING", i);
      if (localObject1 != null) {
        localArrayList.add(new KeyboardShortcutInfo(this.mContext.getString(2131690566), (Icon)localObject1, 48, 65536));
      }
      localObject1 = getIconForIntentCategory("android.intent.category.APP_MUSIC", i);
      if (localObject1 != null) {
        localArrayList.add(new KeyboardShortcutInfo(this.mContext.getString(2131690567), (Icon)localObject1, 44, 65536));
      }
      localObject1 = getIconForIntentCategory("android.intent.category.APP_CALENDAR", i);
      if (localObject1 != null) {
        localArrayList.add(new KeyboardShortcutInfo(this.mContext.getString(2131690569), (Icon)localObject1, 40, 65536));
      }
      if (localArrayList.size() == 0) {
        return null;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e(TAG, "PackageManagerService is dead");
        Object localObject2 = localObject3;
      }
      Collections.sort(localArrayList, this.mApplicationItemsComparator);
    }
    return new KeyboardShortcutGroup(this.mContext.getString(2131690561), localArrayList, true);
  }
  
  private List<StringDrawableContainer> getHumanReadableModifiers(KeyboardShortcutInfo paramKeyboardShortcutInfo)
  {
    ArrayList localArrayList = new ArrayList();
    int j = paramKeyboardShortcutInfo.getModifiers();
    if (j == 0) {
      return localArrayList;
    }
    int i = 0;
    while (i < this.mModifierNames.size())
    {
      int m = this.mModifierNames.keyAt(i);
      int k = j;
      if ((j & m) != 0)
      {
        localArrayList.add(new StringDrawableContainer((String)this.mModifierNames.get(m), (Drawable)this.mModifierDrawables.get(m)));
        k = j & m;
      }
      i += 1;
      j = k;
    }
    if (j != 0) {
      return null;
    }
    return localArrayList;
  }
  
  private List<StringDrawableContainer> getHumanReadableShortcutKeys(KeyboardShortcutInfo paramKeyboardShortcutInfo)
  {
    List localList = getHumanReadableModifiers(paramKeyboardShortcutInfo);
    if (localList == null) {
      return null;
    }
    Drawable localDrawable = null;
    if (paramKeyboardShortcutInfo.getBaseCharacter() > 0) {
      paramKeyboardShortcutInfo = String.valueOf(paramKeyboardShortcutInfo.getBaseCharacter());
    }
    while (paramKeyboardShortcutInfo != null)
    {
      localList.add(new StringDrawableContainer(paramKeyboardShortcutInfo, localDrawable));
      return localList;
      if (this.mSpecialCharacterDrawables.get(paramKeyboardShortcutInfo.getKeycode()) != null)
      {
        localDrawable = (Drawable)this.mSpecialCharacterDrawables.get(paramKeyboardShortcutInfo.getKeycode());
        paramKeyboardShortcutInfo = (String)this.mSpecialCharacterNames.get(paramKeyboardShortcutInfo.getKeycode());
      }
      else if (this.mSpecialCharacterNames.get(paramKeyboardShortcutInfo.getKeycode()) != null)
      {
        paramKeyboardShortcutInfo = (String)this.mSpecialCharacterNames.get(paramKeyboardShortcutInfo.getKeycode());
      }
      else
      {
        if (paramKeyboardShortcutInfo.getKeycode() == 0) {
          return localList;
        }
        char c = this.mKeyCharacterMap.getDisplayLabel(paramKeyboardShortcutInfo.getKeycode());
        if (c != 0) {
          paramKeyboardShortcutInfo = String.valueOf(c);
        } else {
          return null;
        }
      }
    }
    Log.w(TAG, "Keyboard Shortcut does not have a text representation, skipping.");
    return localList;
  }
  
  private Icon getIconForIntentCategory(String paramString, int paramInt)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory(paramString);
    paramString = getPackageInfoForIntent(localIntent, paramInt);
    if ((paramString != null) && (paramString.applicationInfo.icon != 0)) {
      return Icon.createWithResource(paramString.applicationInfo.packageName, paramString.applicationInfo.icon);
    }
    return null;
  }
  
  private static KeyboardShortcuts getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new KeyboardShortcuts(paramContext);
    }
    return sInstance;
  }
  
  private PackageInfo getPackageInfoForIntent(Intent paramIntent, int paramInt)
  {
    try
    {
      paramIntent = this.mPackageManager.resolveIntent(paramIntent, paramIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 0, paramInt);
      if (paramIntent != null)
      {
        if (paramIntent.activityInfo == null) {
          return null;
        }
        paramIntent = this.mPackageManager.getPackageInfo(paramIntent.activityInfo.packageName, 0, paramInt);
        return paramIntent;
      }
    }
    catch (RemoteException paramIntent)
    {
      Log.e(TAG, "PackageManagerService is dead", paramIntent);
      return null;
    }
    return null;
  }
  
  private KeyboardShortcutGroup getSystemShortcuts()
  {
    KeyboardShortcutGroup localKeyboardShortcutGroup = new KeyboardShortcutGroup(this.mContext.getString(2131690554), true);
    localKeyboardShortcutGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(2131690555), 66, 65536));
    localKeyboardShortcutGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(2131690557), 67, 65536));
    localKeyboardShortcutGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(2131690556), 61, 2));
    localKeyboardShortcutGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(2131690558), 42, 65536));
    localKeyboardShortcutGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(2131690559), 76, 65536));
    localKeyboardShortcutGroup.addItem(new KeyboardShortcutInfo(this.mContext.getString(2131690560), 62, 65536));
    return localKeyboardShortcutGroup;
  }
  
  private void handleShowKeyboardShortcuts(List<KeyboardShortcutGroup> paramList)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.mContext);
    View localView = ((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(2130968639, null);
    populateKeyboardShortcuts((LinearLayout)localView.findViewById(2131951848), paramList);
    localBuilder.setView(localView);
    localBuilder.setPositiveButton(2131690306, this.mDialogCloseListener);
    this.mKeyboardShortcutsDialog = localBuilder.create();
    this.mKeyboardShortcutsDialog.setCanceledOnTouchOutside(true);
    this.mKeyboardShortcutsDialog.getWindow().setType(2008);
    this.mKeyboardShortcutsDialog.show();
  }
  
  private static boolean isShowing()
  {
    if ((sInstance != null) && (sInstance.mKeyboardShortcutsDialog != null)) {
      return sInstance.mKeyboardShortcutsDialog.isShowing();
    }
    return false;
  }
  
  private void loadResources(Context paramContext)
  {
    this.mSpecialCharacterNames.put(3, paramContext.getString(2131690529));
    this.mSpecialCharacterNames.put(4, paramContext.getString(2131690530));
    this.mSpecialCharacterNames.put(19, paramContext.getString(2131690531));
    this.mSpecialCharacterNames.put(20, paramContext.getString(2131690532));
    this.mSpecialCharacterNames.put(21, paramContext.getString(2131690533));
    this.mSpecialCharacterNames.put(22, paramContext.getString(2131690534));
    this.mSpecialCharacterNames.put(23, paramContext.getString(2131690535));
    this.mSpecialCharacterNames.put(56, ".");
    this.mSpecialCharacterNames.put(61, paramContext.getString(2131690536));
    this.mSpecialCharacterNames.put(62, paramContext.getString(2131690537));
    this.mSpecialCharacterNames.put(66, paramContext.getString(2131690538));
    this.mSpecialCharacterNames.put(67, paramContext.getString(2131690539));
    this.mSpecialCharacterNames.put(85, paramContext.getString(2131690540));
    this.mSpecialCharacterNames.put(86, paramContext.getString(2131690541));
    this.mSpecialCharacterNames.put(87, paramContext.getString(2131690542));
    this.mSpecialCharacterNames.put(88, paramContext.getString(2131690543));
    this.mSpecialCharacterNames.put(89, paramContext.getString(2131690544));
    this.mSpecialCharacterNames.put(90, paramContext.getString(2131690545));
    this.mSpecialCharacterNames.put(92, paramContext.getString(2131690546));
    this.mSpecialCharacterNames.put(93, paramContext.getString(2131690547));
    this.mSpecialCharacterNames.put(96, paramContext.getString(2131690528, new Object[] { "A" }));
    this.mSpecialCharacterNames.put(97, paramContext.getString(2131690528, new Object[] { "B" }));
    this.mSpecialCharacterNames.put(98, paramContext.getString(2131690528, new Object[] { "C" }));
    this.mSpecialCharacterNames.put(99, paramContext.getString(2131690528, new Object[] { "X" }));
    this.mSpecialCharacterNames.put(100, paramContext.getString(2131690528, new Object[] { "Y" }));
    this.mSpecialCharacterNames.put(101, paramContext.getString(2131690528, new Object[] { "Z" }));
    this.mSpecialCharacterNames.put(102, paramContext.getString(2131690528, new Object[] { "L1" }));
    this.mSpecialCharacterNames.put(103, paramContext.getString(2131690528, new Object[] { "R1" }));
    this.mSpecialCharacterNames.put(104, paramContext.getString(2131690528, new Object[] { "L2" }));
    this.mSpecialCharacterNames.put(105, paramContext.getString(2131690528, new Object[] { "R2" }));
    this.mSpecialCharacterNames.put(108, paramContext.getString(2131690528, new Object[] { "Start" }));
    this.mSpecialCharacterNames.put(109, paramContext.getString(2131690528, new Object[] { "Select" }));
    this.mSpecialCharacterNames.put(110, paramContext.getString(2131690528, new Object[] { "Mode" }));
    this.mSpecialCharacterNames.put(112, paramContext.getString(2131690548));
    this.mSpecialCharacterNames.put(111, "Esc");
    this.mSpecialCharacterNames.put(120, "SysRq");
    this.mSpecialCharacterNames.put(121, "Break");
    this.mSpecialCharacterNames.put(116, "Scroll Lock");
    this.mSpecialCharacterNames.put(122, paramContext.getString(2131690549));
    this.mSpecialCharacterNames.put(123, paramContext.getString(2131690550));
    this.mSpecialCharacterNames.put(124, paramContext.getString(2131690551));
    this.mSpecialCharacterNames.put(131, "F1");
    this.mSpecialCharacterNames.put(132, "F2");
    this.mSpecialCharacterNames.put(133, "F3");
    this.mSpecialCharacterNames.put(134, "F4");
    this.mSpecialCharacterNames.put(135, "F5");
    this.mSpecialCharacterNames.put(136, "F6");
    this.mSpecialCharacterNames.put(137, "F7");
    this.mSpecialCharacterNames.put(138, "F8");
    this.mSpecialCharacterNames.put(139, "F9");
    this.mSpecialCharacterNames.put(140, "F10");
    this.mSpecialCharacterNames.put(141, "F11");
    this.mSpecialCharacterNames.put(142, "F12");
    this.mSpecialCharacterNames.put(143, paramContext.getString(2131690552));
    this.mSpecialCharacterNames.put(144, paramContext.getString(2131690553, new Object[] { "0" }));
    this.mSpecialCharacterNames.put(145, paramContext.getString(2131690553, new Object[] { "1" }));
    this.mSpecialCharacterNames.put(146, paramContext.getString(2131690553, new Object[] { "2" }));
    this.mSpecialCharacterNames.put(147, paramContext.getString(2131690553, new Object[] { "3" }));
    this.mSpecialCharacterNames.put(148, paramContext.getString(2131690553, new Object[] { "4" }));
    this.mSpecialCharacterNames.put(149, paramContext.getString(2131690553, new Object[] { "5" }));
    this.mSpecialCharacterNames.put(150, paramContext.getString(2131690553, new Object[] { "6" }));
    this.mSpecialCharacterNames.put(151, paramContext.getString(2131690553, new Object[] { "7" }));
    this.mSpecialCharacterNames.put(152, paramContext.getString(2131690553, new Object[] { "8" }));
    this.mSpecialCharacterNames.put(153, paramContext.getString(2131690553, new Object[] { "9" }));
    this.mSpecialCharacterNames.put(154, paramContext.getString(2131690553, new Object[] { "/" }));
    this.mSpecialCharacterNames.put(155, paramContext.getString(2131690553, new Object[] { "*" }));
    this.mSpecialCharacterNames.put(156, paramContext.getString(2131690553, new Object[] { "-" }));
    this.mSpecialCharacterNames.put(157, paramContext.getString(2131690553, new Object[] { "+" }));
    this.mSpecialCharacterNames.put(158, paramContext.getString(2131690553, new Object[] { "." }));
    this.mSpecialCharacterNames.put(159, paramContext.getString(2131690553, new Object[] { "," }));
    this.mSpecialCharacterNames.put(160, paramContext.getString(2131690553, new Object[] { paramContext.getString(2131690538) }));
    this.mSpecialCharacterNames.put(161, paramContext.getString(2131690553, new Object[] { "=" }));
    this.mSpecialCharacterNames.put(162, paramContext.getString(2131690553, new Object[] { "(" }));
    this.mSpecialCharacterNames.put(163, paramContext.getString(2131690553, new Object[] { ")" }));
    this.mSpecialCharacterNames.put(211, "半角/全角");
    this.mSpecialCharacterNames.put(212, "英数");
    this.mSpecialCharacterNames.put(213, "無変換");
    this.mSpecialCharacterNames.put(214, "変換");
    this.mSpecialCharacterNames.put(215, "かな");
    this.mModifierNames.put(65536, "Meta");
    this.mModifierNames.put(4096, "Ctrl");
    this.mModifierNames.put(2, "Alt");
    this.mModifierNames.put(1, "Shift");
    this.mModifierNames.put(4, "Sym");
    this.mModifierNames.put(8, "Fn");
    this.mSpecialCharacterDrawables.put(67, paramContext.getDrawable(2130837738));
    this.mSpecialCharacterDrawables.put(66, paramContext.getDrawable(2130837740));
    this.mSpecialCharacterDrawables.put(19, paramContext.getDrawable(2130837744));
    this.mSpecialCharacterDrawables.put(22, paramContext.getDrawable(2130837743));
    this.mSpecialCharacterDrawables.put(20, paramContext.getDrawable(2130837739));
    this.mSpecialCharacterDrawables.put(21, paramContext.getDrawable(2130837741));
    this.mModifierDrawables.put(65536, paramContext.getDrawable(2130837742));
  }
  
  private void populateKeyboardShortcuts(LinearLayout paramLinearLayout, List<KeyboardShortcutGroup> paramList)
  {
    LayoutInflater localLayoutInflater = LayoutInflater.from(this.mContext);
    int m = paramList.size();
    Object localObject1 = (TextView)localLayoutInflater.inflate(2130968638, null, false);
    ((TextView)localObject1).measure(0, 0);
    int n = ((TextView)localObject1).getMeasuredHeight();
    int i1 = ((TextView)localObject1).getMeasuredHeight() - ((TextView)localObject1).getPaddingTop() - ((TextView)localObject1).getPaddingBottom();
    int i = 0;
    while (i < m)
    {
      localObject1 = (KeyboardShortcutGroup)paramList.get(i);
      Object localObject2 = (TextView)localLayoutInflater.inflate(2130968635, paramLinearLayout, false);
      ((TextView)localObject2).setText(((KeyboardShortcutGroup)localObject1).getLabel());
      int j;
      label170:
      Object localObject3;
      List localList;
      if (((KeyboardShortcutGroup)localObject1).isSystemGroup())
      {
        j = Utils.getColorAccent(this.mContext);
        ((TextView)localObject2).setTextColor(j);
        paramLinearLayout.addView((View)localObject2);
        localObject2 = (LinearLayout)localLayoutInflater.inflate(2130968636, paramLinearLayout, false);
        int i2 = ((KeyboardShortcutGroup)localObject1).getItems().size();
        j = 0;
        if (j >= i2) {
          break label595;
        }
        localObject3 = (KeyboardShortcutInfo)((KeyboardShortcutGroup)localObject1).getItems().get(j);
        localList = getHumanReadableShortcutKeys((KeyboardShortcutInfo)localObject3);
        if (localList != null) {
          break label241;
        }
        Log.w(TAG, "Keyboard Shortcut contains unsupported keys, skipping.");
      }
      for (;;)
      {
        j += 1;
        break label170;
        j = this.mContext.getColor(2131493065);
        break;
        label241:
        View localView = localLayoutInflater.inflate(2130968633, (ViewGroup)localObject2, false);
        if (((KeyboardShortcutInfo)localObject3).getIcon() != null)
        {
          localObject4 = (ImageView)localView.findViewById(2131951844);
          ((ImageView)localObject4).setImageIcon(((KeyboardShortcutInfo)localObject3).getIcon());
          ((ImageView)localObject4).setVisibility(0);
        }
        Object localObject4 = (TextView)localView.findViewById(2131951845);
        ((TextView)localObject4).setText(((KeyboardShortcutInfo)localObject3).getLabel());
        if (((KeyboardShortcutInfo)localObject3).getIcon() != null)
        {
          localObject3 = (RelativeLayout.LayoutParams)((TextView)localObject4).getLayoutParams();
          ((RelativeLayout.LayoutParams)localObject3).removeRule(20);
          ((TextView)localObject4).setLayoutParams((ViewGroup.LayoutParams)localObject3);
        }
        localObject3 = (ViewGroup)localView.findViewById(2131951846);
        int i3 = localList.size();
        int k = 0;
        if (k < i3)
        {
          localObject4 = (StringDrawableContainer)localList.get(k);
          Object localObject5;
          if (((StringDrawableContainer)localObject4).mDrawable != null)
          {
            localObject5 = (ImageView)localLayoutInflater.inflate(2130968637, (ViewGroup)localObject3, false);
            Bitmap localBitmap = Bitmap.createBitmap(i1, i1, Bitmap.Config.ARGB_8888);
            Canvas localCanvas = new Canvas(localBitmap);
            ((StringDrawableContainer)localObject4).mDrawable.setBounds(0, 0, localCanvas.getWidth(), localCanvas.getHeight());
            ((StringDrawableContainer)localObject4).mDrawable.draw(localCanvas);
            ((ImageView)localObject5).setImageBitmap(localBitmap);
            ((ImageView)localObject5).setImportantForAccessibility(1);
            ((ImageView)localObject5).setAccessibilityDelegate(new ShortcutKeyAccessibilityDelegate(((StringDrawableContainer)localObject4).mString));
            ((ViewGroup)localObject3).addView((View)localObject5);
          }
          for (;;)
          {
            k += 1;
            break;
            if (((StringDrawableContainer)localObject4).mString != null)
            {
              localObject5 = (TextView)localLayoutInflater.inflate(2130968638, (ViewGroup)localObject3, false);
              ((TextView)localObject5).setMinimumWidth(n);
              ((TextView)localObject5).setText(((StringDrawableContainer)localObject4).mString);
              ((TextView)localObject5).setAccessibilityDelegate(new ShortcutKeyAccessibilityDelegate(((StringDrawableContainer)localObject4).mString));
              ((ViewGroup)localObject3).addView((View)localObject5);
            }
          }
        }
        ((LinearLayout)localObject2).addView(localView);
      }
      label595:
      paramLinearLayout.addView((View)localObject2);
      if (i < m - 1) {
        paramLinearLayout.addView(localLayoutInflater.inflate(2130968634, paramLinearLayout, false));
      }
      i += 1;
    }
  }
  
  private void retrieveKeyCharacterMap(int paramInt)
  {
    InputManager localInputManager = InputManager.getInstance();
    if (paramInt != -1)
    {
      localObject = localInputManager.getInputDevice(paramInt);
      if (localObject != null)
      {
        this.mKeyCharacterMap = ((InputDevice)localObject).getKeyCharacterMap();
        return;
      }
    }
    Object localObject = localInputManager.getInputDeviceIds();
    paramInt = 0;
    while (paramInt < localObject.length)
    {
      InputDevice localInputDevice = localInputManager.getInputDevice(localObject[paramInt]);
      if ((localInputDevice.getId() != -1) && (localInputDevice.isFullKeyboard()))
      {
        this.mKeyCharacterMap = localInputDevice.getKeyCharacterMap();
        return;
      }
      paramInt += 1;
    }
    this.mKeyCharacterMap = localInputManager.getInputDevice(-1).getKeyCharacterMap();
  }
  
  public static void show(Context paramContext, int paramInt)
  {
    synchronized (sLock)
    {
      if ((sInstance == null) || (sInstance.mContext.equals(paramContext)))
      {
        getInstance(paramContext).showKeyboardShortcuts(paramInt);
        return;
      }
      dismiss();
    }
  }
  
  private void showKeyboardShortcuts(int paramInt)
  {
    retrieveKeyCharacterMap(paramInt);
    Recents.getSystemServices().requestKeyboardShortcuts(this.mContext, new WindowManager.KeyboardShortcutsReceiver()
    {
      public void onKeyboardShortcutsReceived(List<KeyboardShortcutGroup> paramAnonymousList)
      {
        paramAnonymousList.add(KeyboardShortcuts.-wrap1(KeyboardShortcuts.this));
        KeyboardShortcutGroup localKeyboardShortcutGroup = KeyboardShortcuts.-wrap0(KeyboardShortcuts.this);
        if (localKeyboardShortcutGroup != null) {
          paramAnonymousList.add(localKeyboardShortcutGroup);
        }
        KeyboardShortcuts.-wrap4(KeyboardShortcuts.this, paramAnonymousList);
      }
    }, paramInt);
  }
  
  private void showKeyboardShortcutsDialog(final List<KeyboardShortcutGroup> paramList)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        KeyboardShortcuts.-wrap3(KeyboardShortcuts.this, paramList);
      }
    });
  }
  
  public static void toggle(Context paramContext, int paramInt)
  {
    synchronized (sLock)
    {
      if (isShowing())
      {
        dismiss();
        return;
      }
      show(paramContext, paramInt);
    }
  }
  
  private final class ShortcutKeyAccessibilityDelegate
    extends View.AccessibilityDelegate
  {
    private String mContentDescription;
    
    ShortcutKeyAccessibilityDelegate(String paramString)
    {
      this.mContentDescription = paramString;
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfo);
      if (this.mContentDescription != null) {
        paramAccessibilityNodeInfo.setContentDescription(this.mContentDescription.toLowerCase());
      }
    }
  }
  
  private static final class StringDrawableContainer
  {
    public Drawable mDrawable;
    public String mString;
    
    StringDrawableContainer(String paramString, Drawable paramDrawable)
    {
      this.mString = paramString;
      this.mDrawable = paramDrawable;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\KeyboardShortcuts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */