package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.SparseArray;

class TelephonyIcons
{
  static final MobileSignalController.MobileIconGroup CARRIER_NETWORK_CHANGE;
  static final int[][] DATA_1X;
  static final int[][] DATA_3G;
  static final int[][] DATA_4G;
  static final int[][] DATA_4G_PLUS;
  static final MobileSignalController.MobileIconGroup DATA_DISABLED = new MobileSignalController.MobileIconGroup("DataDisabled", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690160, 2130838415, false, 2130837788);
  static final int[][] DATA_E;
  static final int[][] DATA_G;
  static final int[][] DATA_H;
  static final int[][] DATA_LTE;
  static final MobileSignalController.MobileIconGroup E;
  static final MobileSignalController.MobileIconGroup FOUR_G;
  static final MobileSignalController.MobileIconGroup FOUR_G_LTE;
  static final MobileSignalController.MobileIconGroup FOUR_G_PLUS;
  static final MobileSignalController.MobileIconGroup G;
  static final MobileSignalController.MobileIconGroup H;
  static final MobileSignalController.MobileIconGroup H_plus;
  static final MobileSignalController.MobileIconGroup LTE;
  static final MobileSignalController.MobileIconGroup LTE_PLUS;
  static final int[] ONEPLUS_TELEPHONY_SIGNAL_STRENGTH_ROAMING;
  static final MobileSignalController.MobileIconGroup ONE_X;
  static final int[][] QS_TELEPHONY_CARRIER_NETWORK_CHANGE;
  static final int[][] QS_TELEPHONY_SIGNAL_STRENGTH;
  static final MobileSignalController.MobileIconGroup ROAMING;
  static final int[][] STACKED_DATA_ICONS;
  static final int[][] STACKED_ICONS;
  static final int[][] TELEPHONY_CARRIER_NETWORK_CHANGE;
  static final int[][] TELEPHONY_SIGNAL_STRENGTH;
  static final int[][] TELEPHONY_SIGNAL_STRENGTH_ROAMING;
  static final int[][] TELEPHONY_SIGNAL_STRENGTH_ROAMING_R;
  static final MobileSignalController.MobileIconGroup THREE_G;
  static final MobileSignalController.MobileIconGroup THREE_G_PLUS;
  static final MobileSignalController.MobileIconGroup UNKNOWN;
  static final MobileSignalController.MobileIconGroup WFC;
  private static boolean isInitiated;
  static String[] mDataActivityArray;
  static String[] mDataDisconnectedArray;
  static int[] mDataDisconnectedTypeIcon;
  static String[] mDataTypeArray;
  static String[] mDataTypeDescriptionArray;
  static String[] mDataTypeGenerationArray;
  static String[] mDataTypeGenerationDescArray;
  static String[] mForbiddenDataArray;
  static int[] mForbiddenDataTypeIcon;
  private static Resources mRes;
  static int[] mSelectedDataActivityIndex;
  static String[] mSelectedDataTypeDesc;
  static int[] mSelectedDataTypeIcon;
  static int[] mSelectedQSDataTypeIcon;
  static int[] mSelectedSignalStreagthIndex;
  static String[] mSignalNullArray;
  static String[] mSignalStrengthArray;
  static String[] mSignalStrengthDesc;
  static String[] mSignalStrengthRoamingArray;
  static SparseArray<Integer> mStacked2SingleIconLookup;
  
  static
  {
    int[] arrayOfInt = { 2130838615, 2130838687, 2130838763, 2130838839, 2130838915 };
    TELEPHONY_SIGNAL_STRENGTH = new int[][] { { 2130838580, 2130838652, 2130838728, 2130838804, 2130838880 }, arrayOfInt };
    QS_TELEPHONY_SIGNAL_STRENGTH = new int[][] { { 2130837812, 2130837813, 2130837815, 2130837817, 2130837820 }, { 2130837828, 2130837829, 2130837830, 2130837832, 2130837833 } };
    arrayOfInt = new int[] { 2130838615, 2130838687, 2130838763, 2130838839, 2130838915 };
    TELEPHONY_SIGNAL_STRENGTH_ROAMING = new int[][] { { 2130838580, 2130838652, 2130838728, 2130838804, 2130838880 }, arrayOfInt };
    TELEPHONY_SIGNAL_STRENGTH_ROAMING_R = new int[][] { { 2130838610, 2130838682, 2130838758, 2130838834, 2130838910 }, { 2130838609, 2130838681, 2130838757, 2130838833, 2130838909 } };
    ONEPLUS_TELEPHONY_SIGNAL_STRENGTH_ROAMING = new int[] { 2130838632, 2130838704, 2130838780, 2130838856, 2130838932 };
    TELEPHONY_CARRIER_NETWORK_CHANGE = new int[][] { { 2130838961, 2130838961, 2130838961, 2130838961, 2130838961 }, { 2130838961, 2130838961, 2130838961, 2130838961, 2130838961 } };
    QS_TELEPHONY_CARRIER_NETWORK_CHANGE = new int[][] { { 2130837825, 2130837825, 2130837825, 2130837825, 2130837825 }, { 2130837825, 2130837825, 2130837825, 2130837825, 2130837825 } };
    DATA_G = new int[][] { { 2130838423, 2130838423, 2130838423, 2130838423 }, { 2130838423, 2130838423, 2130838423, 2130838423 } };
    DATA_3G = new int[][] { { 2130838417, 2130838417, 2130838417, 2130838417 }, { 2130838417, 2130838417, 2130838417, 2130838417 } };
    DATA_E = new int[][] { { 2130838422, 2130838422, 2130838422, 2130838422 }, { 2130838422, 2130838422, 2130838422, 2130838422 } };
    arrayOfInt = new int[] { 2130838424, 2130838424, 2130838424, 2130838424 };
    DATA_H = new int[][] { { 2130838424, 2130838424, 2130838424, 2130838424 }, arrayOfInt };
    DATA_1X = new int[][] { { 2130838416, 2130838416, 2130838416, 2130838416 }, { 2130838416, 2130838416, 2130838416, 2130838416 } };
    DATA_4G = new int[][] { { 2130838419, 2130838419, 2130838419, 2130838419 }, { 2130838419, 2130838419, 2130838419, 2130838419 } };
    DATA_4G_PLUS = new int[][] { { 2130838421, 2130838421, 2130838421, 2130838421 }, { 2130838421, 2130838421, 2130838421, 2130838421 } };
    DATA_LTE = new int[][] { { 2130838427, 2130838427, 2130838427, 2130838427 }, { 2130838427, 2130838427, 2130838427, 2130838427 } };
    STACKED_ICONS = new int[][] { { 2130838644, 2130838716, 2130838792, 2130838868, 2130838944 }, { 2130838633, 2130838705, 2130838781, 2130838857, 2130838933 } };
    STACKED_DATA_ICONS = new int[][] { { 2130838647, 2130838719, 2130838795, 2130838871, 2130838947 }, { 2130838646, 2130838718, 2130838794, 2130838870, 2130838946 }, { 2130838645, 2130838717, 2130838793, 2130838869, 2130838945 }, { 2130838649, 2130838721, 2130838797, 2130838873, 2130838949 }, { 2130838648, 2130838720, 2130838796, 2130838872, 2130838948 }, { 2130838650, 2130838722, 2130838798, 2130838874, 2130838950 }, { 2130838651, 2130838723, 2130838799, 2130838875, 2130838951 } };
    isInitiated = false;
    CARRIER_NETWORK_CHANGE = new MobileSignalController.MobileIconGroup("CARRIER_NETWORK_CHANGE", TELEPHONY_CARRIER_NETWORK_CHANGE, QS_TELEPHONY_CARRIER_NETWORK_CHANGE, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838961, 2130837825, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690164, 0, false, 0);
    THREE_G = new MobileSignalController.MobileIconGroup("3G", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690144, 2130838417, true, 2130837818);
    THREE_G_PLUS = new MobileSignalController.MobileIconGroup("3G+", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690145, 2130838418, true, 2130837819);
    WFC = new MobileSignalController.MobileIconGroup("WFC", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 0, 0, false, 0);
    UNKNOWN = new MobileSignalController.MobileIconGroup("Unknown", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 0, 0, false, 0);
    E = new MobileSignalController.MobileIconGroup("E", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690155, 2130838422, false, 2130837827);
    ONE_X = new MobileSignalController.MobileIconGroup("1X", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690153, 2130838416, true, 2130837814);
    G = new MobileSignalController.MobileIconGroup("G", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690141, 2130838423, false, 2130837835);
    H = new MobileSignalController.MobileIconGroup("H", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690146, 2130838424, false, 2130837836);
    H_plus = new MobileSignalController.MobileIconGroup("H+", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690147, 2130838425, false, 2130837837);
    FOUR_G = new MobileSignalController.MobileIconGroup("4G", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690148, 2130838419, true, 2130837821);
    FOUR_G_LTE = new MobileSignalController.MobileIconGroup("4GLTE", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690149, 2130838420, true, 2130837822);
    FOUR_G_PLUS = new MobileSignalController.MobileIconGroup("4G+", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690150, 2130838421, true, 2130837823);
    LTE = new MobileSignalController.MobileIconGroup("LTE", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690151, 2130838427, true, 2130837840);
    LTE_PLUS = new MobileSignalController.MobileIconGroup("LTE+", TELEPHONY_SIGNAL_STRENGTH, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690152, 2130838429, true, 2130837841);
    ROAMING = new MobileSignalController.MobileIconGroup("Roaming", TELEPHONY_SIGNAL_STRENGTH_ROAMING, QS_TELEPHONY_SIGNAL_STRENGTH, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 2130838983, 2130837842, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 2131690154, 2130838431, false, 2130837844);
  }
  
  static int convertMobileStrengthIcon(int paramInt)
  {
    if (mStacked2SingleIconLookup == null) {
      return paramInt;
    }
    if (mStacked2SingleIconLookup.indexOfKey(paramInt) >= 0) {
      return ((Integer)mStacked2SingleIconLookup.get(paramInt)).intValue();
    }
    return paramInt;
  }
  
  static int getDataActivity(int paramInt1, int paramInt2)
  {
    log("TelephonyIcons", String.format("getDataActivity, slot=%d, activity=%d", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }));
    String[] arrayOfString = mRes.getStringArray(mRes.getIdentifier(mDataActivityArray[paramInt1], null, "com.android.systemui"));
    arrayOfString = mRes.getStringArray(mRes.getIdentifier(arrayOfString[mSelectedDataActivityIndex[paramInt1]], null, "com.android.systemui"));
    return mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
  }
  
  public static int getDataDisconnectedIcon(int paramInt)
  {
    log("TelephonyIcons", "getDisconnectedDataIcon " + String.format("sub=%d", new Object[] { Integer.valueOf(paramInt) }));
    return mDataDisconnectedTypeIcon[paramInt];
  }
  
  static int getDataTypeDesc(int paramInt)
  {
    return mRes.getIdentifier(mSelectedDataTypeDesc[paramInt], null, "com.android.systemui");
  }
  
  static int getDataTypeIcon(int paramInt)
  {
    log("TelephonyIcons", "getDataTypeIcon " + String.format("sub=%d", new Object[] { Integer.valueOf(paramInt) }));
    return mSelectedDataTypeIcon[paramInt];
  }
  
  public static int getForbiddenDataIcon(int paramInt)
  {
    log("TelephonyIcons", "getForbiddenDataIcon " + String.format("sub=%d", new Object[] { Integer.valueOf(paramInt) }));
    return mForbiddenDataTypeIcon[paramInt];
  }
  
  static int getOneplusRoamingSignalIconId(int paramInt)
  {
    return ONEPLUS_TELEPHONY_SIGNAL_STRENGTH_ROAMING[paramInt];
  }
  
  static int getQSDataTypeIcon(int paramInt)
  {
    return mSelectedQSDataTypeIcon[paramInt];
  }
  
  static int getRoamingSignalIconId(int paramInt1, int paramInt2)
  {
    return TELEPHONY_SIGNAL_STRENGTH_ROAMING_R[paramInt2][paramInt1];
  }
  
  static int getSignalNullIcon(int paramInt)
  {
    if (mSignalNullArray == null) {
      return 0;
    }
    String str = mSignalNullArray[paramInt];
    log("TelephonyIcons", "null signal icon name: " + str);
    return mRes.getIdentifier(str, null, "com.android.systemui");
  }
  
  static int[] getSignalStrengthDes(int paramInt)
  {
    int[] arrayOfInt = new int[5];
    paramInt = 0;
    while (paramInt < 5)
    {
      arrayOfInt[paramInt] = mRes.getIdentifier(mSignalStrengthDesc[paramInt], null, "com.android.systemui");
      paramInt += 1;
    }
    return arrayOfInt;
  }
  
  static int getSignalStrengthIcon(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    log("TelephonyIcons", "getSignalStrengthIcon: " + String.format("slot=%d, inetCondition=%d, level=%d, roaming=%b", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(paramBoolean) }));
    Resources localResources1 = mRes;
    Resources localResources2 = mRes;
    if (!paramBoolean) {}
    for (Object localObject = mSignalStrengthArray[paramInt1];; localObject = mSignalStrengthRoamingArray[paramInt1])
    {
      localObject = localResources1.getStringArray(localResources2.getIdentifier((String)localObject, null, "com.android.systemui"));
      log("TelephonyIcons", String.format("signalStrengthArray.length=%d", new Object[] { Integer.valueOf(localObject.length) }));
      localObject = mRes.getStringArray(mRes.getIdentifier(localObject[mSelectedSignalStreagthIndex[paramInt1]], null, "com.android.systemui"));
      log("TelephonyIcons", String.format("selectedTypeArray.length=%d", new Object[] { Integer.valueOf(localObject.length) }));
      localObject = mRes.getStringArray(mRes.getIdentifier(localObject[paramInt2], null, "com.android.systemui"));
      log("TelephonyIcons", String.format("inetArray.length=%d", new Object[] { Integer.valueOf(localObject.length) }));
      return mRes.getIdentifier(localObject[paramInt3], null, "com.android.systemui");
    }
  }
  
  static int getStackedDataIcon(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    switch (paramInt1)
    {
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 15: 
    case 16: 
    case 18: 
    default: 
      paramInt2 = STACKED_DATA_ICONS[3][paramInt2];
      Log.w("TelephonyIcons", "Unknow network type:" + paramInt1);
      return paramInt2;
    case 13: 
      if (paramBoolean) {
        return STACKED_DATA_ICONS[5][paramInt2];
      }
      return STACKED_DATA_ICONS[0][paramInt2];
    case 19: 
      if (paramBoolean) {
        return STACKED_DATA_ICONS[6][paramInt2];
      }
      return STACKED_DATA_ICONS[4][paramInt2];
    case 3: 
    case 5: 
    case 6: 
    case 12: 
    case 14: 
    case 17: 
      return STACKED_DATA_ICONS[1][paramInt2];
    }
    return STACKED_DATA_ICONS[2][paramInt2];
  }
  
  static int getStackedVoiceIcon(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      return STACKED_ICONS[1][paramInt];
    }
    return STACKED_ICONS[0][paramInt];
  }
  
  static void initStacked2SingleIconLookup()
  {
    mStacked2SingleIconLookup = new SparseArray();
    TypedArray localTypedArray1 = mRes.obtainTypedArray(2131427594);
    TypedArray localTypedArray2 = mRes.obtainTypedArray(2131427595);
    mStacked2SingleIconLookup.clear();
    int i = 0;
    while ((i < localTypedArray1.length()) && (i < localTypedArray2.length()))
    {
      mStacked2SingleIconLookup.put(localTypedArray1.getResourceId(i, 0), Integer.valueOf(localTypedArray2.getResourceId(i, 0)));
      i += 1;
    }
    localTypedArray1.recycle();
    localTypedArray2.recycle();
    log("TelephonyIcons", "initStacked2SingleIconLookup: size=" + mStacked2SingleIconLookup.size());
  }
  
  private static void log(String paramString1, String paramString2)
  {
    Log.d(paramString1, paramString2);
  }
  
  static void readIconsFromXml(Context paramContext)
  {
    if (isInitiated)
    {
      log("TelephonyIcons", "readIconsFromXml, already read!");
      return;
    }
    mRes = paramContext.getResources();
    try
    {
      mForbiddenDataArray = mRes.getStringArray(2131427417);
      mDataDisconnectedArray = mRes.getStringArray(2131427419);
      mDataTypeArray = mRes.getStringArray(2131427375);
      mDataTypeDescriptionArray = mRes.getStringArray(2131427380);
      mDataTypeGenerationArray = mRes.getStringArray(2131427379);
      mDataTypeGenerationDescArray = mRes.getStringArray(2131427381);
      mDataActivityArray = mRes.getStringArray(2131427382);
      mSignalStrengthArray = mRes.getStringArray(2131427421);
      mSignalStrengthRoamingArray = mRes.getStringArray(2131427422);
      mSignalNullArray = mRes.getStringArray(2131427484);
      mSignalStrengthDesc = mRes.getStringArray(2131427483);
      initStacked2SingleIconLookup();
      if ((mSelectedDataTypeIcon == null) && (mDataTypeArray.length != 0)) {
        mSelectedDataTypeIcon = new int[mDataTypeArray.length];
      }
      if ((mForbiddenDataTypeIcon == null) && (mForbiddenDataArray.length != 0)) {
        mForbiddenDataTypeIcon = new int[mForbiddenDataArray.length];
      }
      if ((mDataDisconnectedTypeIcon == null) && (mDataDisconnectedArray.length != 0)) {
        mDataDisconnectedTypeIcon = new int[mDataDisconnectedArray.length];
      }
      if ((mSelectedQSDataTypeIcon == null) && (mDataTypeArray.length != 0)) {
        mSelectedQSDataTypeIcon = new int[mDataTypeArray.length];
      }
      if ((mSelectedDataTypeDesc == null) && (mDataTypeArray.length != 0)) {
        mSelectedDataTypeDesc = new String[mDataTypeArray.length];
      }
      if ((mSelectedDataActivityIndex == null) && (mDataActivityArray.length != 0)) {
        mSelectedDataActivityIndex = new int[mDataActivityArray.length];
      }
      if ((mSelectedSignalStreagthIndex == null) && (mSignalStrengthArray.length != 0)) {
        mSelectedSignalStreagthIndex = new int[mSignalStrengthArray.length];
      }
      isInitiated = true;
      return;
    }
    catch (Resources.NotFoundException paramContext)
    {
      isInitiated = false;
      log("TelephonyIcons", "readIconsFromXml, exception happened: " + paramContext);
    }
  }
  
  private static void setDataDisconnectedResource(int paramInt1, int paramInt2, String[] paramArrayOfString)
  {
    mDataDisconnectedTypeIcon[paramInt1] = mRes.getIdentifier(paramArrayOfString[paramInt2], null, "com.android.systemui");
  }
  
  private static void setForbiddenResource(int paramInt1, int paramInt2, String[] paramArrayOfString)
  {
    mForbiddenDataTypeIcon[paramInt1] = mRes.getIdentifier(paramArrayOfString[paramInt2], null, "com.android.systemui");
  }
  
  static void updateDataType(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt3)
  {
    log("TelephonyIcons", "updateDataType " + String.format("slot=%d, type=%d, inetCondition=%d", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) }) + " showAtLeast3G=" + String.valueOf(paramBoolean1) + " show4GforLte=" + String.valueOf(paramBoolean2) + " hspaDistinguishable=" + String.valueOf(paramBoolean3));
    String str = mDataTypeArray[paramInt1];
    paramInt3 = mRes.getIdentifier(str, null, "com.android.systemui");
    String[] arrayOfString = mRes.getStringArray(paramInt3);
    if (mRes.getBoolean(2131558448))
    {
      Object localObject1 = mForbiddenDataArray[paramInt1];
      Object localObject2 = mDataDisconnectedArray[paramInt1];
      int i = mRes.getIdentifier((String)localObject1, null, "com.android.systemui");
      int j = mRes.getIdentifier((String)localObject2, null, "com.android.systemui");
      localObject1 = mRes.getStringArray(i);
      localObject2 = mRes.getStringArray(j);
      setForbiddenResource(paramInt1, paramInt2, (String[])localObject1);
      setDataDisconnectedResource(paramInt1, paramInt2, (String[])localObject2);
    }
    log("TelephonyIcons", "data type item name: " + str + " id:" + paramInt3);
    switch (paramInt2)
    {
    case 11: 
    case 18: 
    default: 
      mSelectedDataActivityIndex[paramInt1] = 0;
      mSelectedDataTypeIcon[paramInt1] = 0;
      mSelectedQSDataTypeIcon[paramInt1] = 0;
      mSelectedDataTypeDesc[paramInt1] = "";
      mSelectedSignalStreagthIndex[paramInt1] = 0;
    }
    for (;;)
    {
      log("TelephonyIcons", "updateDataType " + String.format("mSelectedDataTypeIcon[%d]=%d, mSelectedDataActivityIndex=%d", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(mSelectedDataTypeIcon[paramInt1]), Integer.valueOf(mSelectedDataActivityIndex[paramInt1]) }));
      return;
      if (!paramBoolean1)
      {
        mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
        mSelectedQSDataTypeIcon[paramInt1] = 0;
        mSelectedDataTypeDesc[paramInt1] = mDataTypeDescriptionArray[paramInt2];
        mSelectedDataActivityIndex[paramInt1] = 0;
        mSelectedSignalStreagthIndex[paramInt1] = 0;
      }
      else if (!paramBoolean1)
      {
        mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
        mSelectedQSDataTypeIcon[paramInt1] = 2130837827;
        mSelectedDataTypeDesc[paramInt1] = mDataTypeDescriptionArray[paramInt2];
        mSelectedDataActivityIndex[paramInt1] = 2;
        mSelectedSignalStreagthIndex[paramInt1] = 1;
      }
      else
      {
        mSelectedDataActivityIndex[paramInt1] = 4;
        mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
        mSelectedQSDataTypeIcon[paramInt1] = 2130837818;
        mSelectedDataTypeDesc[paramInt1] = mDataTypeDescriptionArray[paramInt2];
        mSelectedSignalStreagthIndex[paramInt1] = 8;
        continue;
        if ((paramBoolean3) || (MobileSignalController.isCarrierOneSupported()))
        {
          mSelectedDataTypeDesc[paramInt1] = mDataTypeDescriptionArray[paramInt2];
          if ((paramInt2 == 10) && (MobileSignalController.isCarrierOneSupported()))
          {
            mSelectedDataActivityIndex[paramInt1] = 7;
            mSelectedDataTypeIcon[paramInt1] = 2130838425;
            mSelectedQSDataTypeIcon[paramInt1] = 2130837837;
            mSelectedSignalStreagthIndex[paramInt1] = 5;
          }
          else
          {
            mSelectedDataActivityIndex[paramInt1] = 6;
            mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
            mSelectedQSDataTypeIcon[paramInt1] = 2130837836;
            mSelectedSignalStreagthIndex[paramInt1] = 4;
          }
        }
        else
        {
          mSelectedDataActivityIndex[paramInt1] = 4;
          mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(mDataTypeGenerationArray[0], null, "com.android.systemui");
          mSelectedQSDataTypeIcon[paramInt1] = 2130837818;
          mSelectedDataTypeDesc[paramInt1] = mDataTypeGenerationDescArray[0];
          mSelectedSignalStreagthIndex[paramInt1] = 2;
          continue;
          if ((paramBoolean3) || (MobileSignalController.isCarrierOneSupported()))
          {
            mSelectedDataActivityIndex[paramInt1] = 7;
            mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
            mSelectedQSDataTypeIcon[paramInt1] = 2130837836;
            mSelectedDataTypeDesc[paramInt1] = mDataTypeDescriptionArray[paramInt2];
            mSelectedSignalStreagthIndex[paramInt1] = 5;
          }
          else
          {
            mSelectedDataActivityIndex[paramInt1] = 4;
            mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(mDataTypeGenerationArray[0], null, "com.android.systemui");
            mSelectedQSDataTypeIcon[paramInt1] = 2130837818;
            if ((mRes.getBoolean(2131558438)) || (mRes.getBoolean(2131558439)))
            {
              mSelectedDataActivityIndex[paramInt1] = 5;
              mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(mDataTypeGenerationArray[3], null, "com.android.systemui");
              mSelectedQSDataTypeIcon[paramInt1] = 2130837821;
            }
            mSelectedDataTypeDesc[paramInt1] = mDataTypeGenerationDescArray[0];
            mSelectedSignalStreagthIndex[paramInt1] = 2;
            continue;
            if (!paramBoolean1)
            {
              mSelectedDataActivityIndex[paramInt1] = 8;
              mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
              mSelectedQSDataTypeIcon[paramInt1] = 2130837814;
              mSelectedDataTypeDesc[paramInt1] = mDataTypeDescriptionArray[paramInt2];
              mSelectedSignalStreagthIndex[paramInt1] = 7;
            }
            else if (!paramBoolean1)
            {
              mSelectedDataActivityIndex[paramInt1] = 8;
              mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
              mSelectedQSDataTypeIcon[paramInt1] = 2130837814;
              mSelectedDataTypeDesc[paramInt1] = mDataTypeDescriptionArray[paramInt2];
              mSelectedSignalStreagthIndex[paramInt1] = 6;
            }
            else
            {
              mSelectedDataActivityIndex[paramInt1] = 4;
              mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
              mSelectedQSDataTypeIcon[paramInt1] = 2130837818;
              mSelectedDataTypeDesc[paramInt1] = mDataTypeDescriptionArray[paramInt2];
              mSelectedSignalStreagthIndex[paramInt1] = 2;
              continue;
              if ((!paramBoolean2) || (MobileSignalController.isCarrierOneSupported()))
              {
                mSelectedDataActivityIndex[paramInt1] = 9;
                mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
                if (paramInt2 == 19) {
                  mSelectedQSDataTypeIcon[paramInt1] = 2130837841;
                }
                for (;;)
                {
                  mSelectedDataTypeDesc[paramInt1] = mDataTypeDescriptionArray[paramInt2];
                  mSelectedSignalStreagthIndex[paramInt1] = 3;
                  break;
                  mSelectedQSDataTypeIcon[paramInt1] = 2130837840;
                }
              }
              if (paramInt2 == 19)
              {
                mSelectedDataActivityIndex[paramInt1] = 10;
                mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(mDataTypeGenerationArray[2], null, "com.android.systemui");
                mSelectedQSDataTypeIcon[paramInt1] = 2130837823;
              }
              for (;;)
              {
                mSelectedDataTypeDesc[paramInt1] = mDataTypeGenerationDescArray[1];
                mSelectedSignalStreagthIndex[paramInt1] = 3;
                break;
                mSelectedDataActivityIndex[paramInt1] = 5;
                mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(mDataTypeGenerationArray[1], null, "com.android.systemui");
                mSelectedQSDataTypeIcon[paramInt1] = 2130837821;
              }
              if (!paramBoolean1)
              {
                mSelectedDataActivityIndex[paramInt1] = 1;
                mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(arrayOfString[paramInt2], null, "com.android.systemui");
                mSelectedQSDataTypeIcon[paramInt1] = 2130837835;
                mSelectedDataTypeDesc[paramInt1] = mDataTypeDescriptionArray[paramInt2];
                mSelectedSignalStreagthIndex[paramInt1] = 0;
              }
              else
              {
                mSelectedDataActivityIndex[paramInt1] = 4;
                mSelectedDataTypeIcon[paramInt1] = mRes.getIdentifier(mDataTypeGenerationArray[0], null, "com.android.systemui");
                mSelectedQSDataTypeIcon[paramInt1] = 2130837818;
                mSelectedDataTypeDesc[paramInt1] = mDataTypeGenerationDescArray[0];
                mSelectedSignalStreagthIndex[paramInt1] = 2;
              }
            }
          }
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\TelephonyIcons.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */