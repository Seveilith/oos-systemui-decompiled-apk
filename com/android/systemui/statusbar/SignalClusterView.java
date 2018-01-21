package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallbackExtended;
import com.android.systemui.statusbar.policy.NetworkControllerImpl;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.SecurityController.SecurityControllerCallback;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.android.systemui.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SignalClusterView
  extends LinearLayout
  implements NetworkController.SignalCallbackExtended, SecurityController.SecurityControllerCallback, TunerService.Tunable
{
  static final boolean DEBUG = Log.isLoggable("SignalClusterView", 3);
  ImageView mAirplane;
  private String mAirplaneContentDescription;
  private int mAirplaneIconId = 0;
  private boolean mBlockAirplane;
  private boolean mBlockEthernet;
  private boolean mBlockMobile;
  private boolean mBlockWifi;
  private float mDarkIntensity;
  private final int mEndPadding;
  private final int mEndPaddingNothingVisible;
  ImageView mEthernet;
  ImageView mEthernetDark;
  private String mEthernetDescription;
  ViewGroup mEthernetGroup;
  private int mEthernetIconId = 0;
  private boolean mEthernetVisible = false;
  private final float mIconScaleFactor;
  private int mIconTint = -1;
  private boolean mImsOverWifi = false;
  ImageView mImsOverWifiImageView;
  private boolean mIsAirplaneMode = false;
  private int mLastAirplaneIconId = -1;
  private int mLastEthernetIconId = -1;
  private int mLastVpnIconId = -1;
  private int mLastWifiActivityId = -1;
  private int mLastWifiStrengthId = -1;
  private final int mMobileDataIconStartPadding;
  LinearLayout mMobileSignalGroup;
  private final int mMobileSignalGroupEndPadding;
  NetworkControllerImpl mNC;
  ImageView mNoSims;
  View mNoSimsCombo;
  ImageView mNoSimsDark;
  private int mNoSimsIcon;
  private boolean mNoSimsVisible = false;
  private ArrayList<PhoneState> mPhoneStates = new ArrayList();
  SecurityController mSC;
  private final int mSecondaryTelephonyPadding;
  private TelephonyManager mTelephonyManager;
  private final Rect mTintArea = new Rect();
  ImageView mVpn;
  private int mVpnIconId = 0;
  private boolean mVpnVisible = false;
  private final int mWideTypeIconStartPadding;
  ImageView mWifi;
  ImageView mWifiActivity;
  private int mWifiActivityId = 0;
  View mWifiAirplaneSpacer;
  ImageView mWifiDark;
  private String mWifiDescription;
  ViewGroup mWifiGroup;
  View mWifiSignalSpacer;
  private int mWifiStrengthId = 0;
  private boolean mWifiVisible = false;
  
  public SignalClusterView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SignalClusterView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SignalClusterView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramAttributeSet = getResources();
    this.mMobileSignalGroupEndPadding = paramAttributeSet.getDimensionPixelSize(2131755531);
    this.mMobileDataIconStartPadding = paramAttributeSet.getDimensionPixelSize(2131755532);
    this.mWideTypeIconStartPadding = paramAttributeSet.getDimensionPixelSize(2131755533);
    this.mSecondaryTelephonyPadding = paramAttributeSet.getDimensionPixelSize(2131755534);
    this.mEndPadding = paramAttributeSet.getDimensionPixelSize(2131755540);
    this.mEndPaddingNothingVisible = paramAttributeSet.getDimensionPixelSize(2131755541);
    TypedValue localTypedValue = new TypedValue();
    paramAttributeSet.getValue(2131755367, localTypedValue, true);
    this.mIconScaleFactor = localTypedValue.getFloat();
    this.mTelephonyManager = ((TelephonyManager)paramContext.getSystemService("phone"));
  }
  
  private void apply()
  {
    boolean bool4 = true;
    if (this.mWifiGroup == null) {
      return;
    }
    Object localObject = this.mVpn;
    if (this.mVpnVisible)
    {
      i = 0;
      ((ImageView)localObject).setVisibility(i);
      if (!this.mVpnVisible) {
        break label530;
      }
      if (this.mLastVpnIconId != this.mVpnIconId)
      {
        setIconForView(this.mVpn, this.mVpnIconId);
        this.mLastVpnIconId = this.mVpnIconId;
      }
      this.mVpn.setVisibility(0);
      label78:
      if (DEBUG)
      {
        if (!this.mVpnVisible) {
          break label542;
        }
        localObject = "VISIBLE";
        label96:
        Log.d("SignalClusterView", String.format("vpn: %s", new Object[] { localObject }));
      }
      if (!this.mEthernetVisible) {
        break label550;
      }
      if (this.mLastEthernetIconId != this.mEthernetIconId)
      {
        setIconForView(this.mEthernet, this.mEthernetIconId);
        setIconForView(this.mEthernetDark, this.mEthernetIconId);
        this.mLastEthernetIconId = this.mEthernetIconId;
      }
      this.mEthernetGroup.setContentDescription(this.mEthernetDescription);
      this.mEthernetGroup.setVisibility(0);
      label186:
      if (DEBUG)
      {
        if (!this.mEthernetVisible) {
          break label562;
        }
        localObject = "VISIBLE";
        label204:
        Log.d("SignalClusterView", String.format("ethernet: %s", new Object[] { localObject }));
      }
      if (!this.mWifiVisible) {
        break label570;
      }
      if (this.mWifiStrengthId != this.mLastWifiStrengthId)
      {
        setIconForView(this.mWifi, this.mWifiStrengthId);
        setIconForView(this.mWifiDark, this.mWifiStrengthId);
        this.mLastWifiStrengthId = this.mWifiStrengthId;
      }
      if (this.mWifiActivityId != this.mLastWifiActivityId)
      {
        this.mWifiActivity.setImageResource(this.mWifiActivityId);
        this.mLastWifiActivityId = this.mWifiActivityId;
      }
      this.mWifiGroup.setContentDescription(this.mWifiDescription);
      this.mWifiGroup.setVisibility(0);
      label324:
      if (DEBUG) {
        if (!this.mWifiVisible) {
          break label582;
        }
      }
    }
    boolean bool1;
    int k;
    boolean bool2;
    label530:
    label542:
    label550:
    label562:
    label570:
    label582:
    for (localObject = "VISIBLE";; localObject = "GONE")
    {
      Log.d("SignalClusterView", String.format("wifi: %s sig=%d act=%d", new Object[] { localObject, Integer.valueOf(this.mWifiStrengthId), Integer.valueOf(this.mWifiActivityId) }));
      bool1 = false;
      i = 0;
      k = 0;
      localObject = this.mPhoneStates.iterator();
      while (((Iterator)localObject).hasNext())
      {
        PhoneState localPhoneState = (PhoneState)((Iterator)localObject).next();
        bool2 = bool1;
        int j = i;
        if (localPhoneState.apply(bool1))
        {
          bool2 = bool1;
          j = i;
          if (!bool1)
          {
            j = PhoneState.-get5(localPhoneState);
            bool2 = true;
          }
        }
        boolean bool3 = bool2;
        if (!bool2) {
          bool3 = true;
        }
        if (PhoneState.-get5(localPhoneState) != 0) {
          j = PhoneState.-get5(localPhoneState);
        }
        bool1 = bool3;
        i = j;
        if (PhoneState.-get6(localPhoneState) != 0)
        {
          bool1 = bool3;
          i = j;
          if (PhoneState.-get7(localPhoneState) != 0)
          {
            k = 1;
            bool1 = bool3;
            i = j;
          }
        }
      }
      i = 8;
      break;
      this.mVpn.setVisibility(8);
      break label78;
      localObject = "GONE";
      break label96;
      this.mEthernetGroup.setVisibility(8);
      break label186;
      localObject = "GONE";
      break label204;
      this.mWifiGroup.setVisibility(8);
      break label324;
    }
    if (this.mIsAirplaneMode)
    {
      if (this.mLastAirplaneIconId != this.mAirplaneIconId)
      {
        setIconForView(this.mAirplane, this.mAirplaneIconId);
        this.mLastAirplaneIconId = this.mAirplaneIconId;
      }
      this.mAirplane.setContentDescription(this.mAirplaneContentDescription);
      this.mAirplane.setVisibility(0);
      if (!this.mImsOverWifi) {
        break label884;
      }
      this.mImsOverWifiImageView.setVisibility(0);
      label662:
      if ((!this.mIsAirplaneMode) || (!this.mWifiVisible)) {
        break label896;
      }
      this.mWifiAirplaneSpacer.setVisibility(0);
      label684:
      if ((k == 0) && ((!bool1) || (i == 0))) {
        break label908;
      }
      label697:
      if ((this.mWifiVisible) && (!this.mIsAirplaneMode)) {
        break label918;
      }
      label711:
      this.mWifiSignalSpacer.setVisibility(8);
      label720:
      if ((this.mNoSimsVisible) && (this.mNoSims != null) && (this.mNoSimsDark != null))
      {
        if (this.mNoSimsIcon == 0) {
          this.mNoSimsIcon = getNoSimIcon();
        }
        if (this.mNoSimsIcon != 0)
        {
          this.mNoSims.setImageResource(this.mNoSimsIcon);
          this.mNoSimsDark.setImageResource(this.mNoSimsIcon);
        }
      }
      this.mNoSimsCombo.setVisibility(8);
      bool2 = bool4;
      if (!this.mNoSimsVisible)
      {
        bool2 = bool4;
        if (!this.mWifiVisible)
        {
          bool2 = bool4;
          if (!this.mIsAirplaneMode)
          {
            bool2 = bool4;
            if (!bool1)
            {
              bool2 = bool4;
              if (!this.mVpnVisible) {
                bool2 = this.mEthernetVisible;
              }
            }
          }
        }
      }
      if (!bool2) {
        break label929;
      }
    }
    label884:
    label896:
    label908:
    label918:
    label929:
    for (int i = this.mEndPadding;; i = this.mEndPaddingNothingVisible)
    {
      setPaddingRelative(0, 0, i, 0);
      return;
      this.mAirplane.setVisibility(8);
      break;
      this.mImsOverWifiImageView.setVisibility(8);
      break label662;
      this.mWifiAirplaneSpacer.setVisibility(8);
      break label684;
      if (!this.mNoSimsVisible) {
        break label711;
      }
      break label697;
      this.mWifiSignalSpacer.setVisibility(0);
      break label720;
    }
  }
  
  private void applyDarkIntensity(float paramFloat, View paramView1, View paramView2)
  {
    paramView1.setAlpha(1.0F - paramFloat);
    paramView2.setAlpha(paramFloat);
  }
  
  private void applyIconTint()
  {
    setTint(this.mVpn, StatusBarIconController.getTint(this.mTintArea, this.mVpn, this.mIconTint));
    setTint(this.mAirplane, StatusBarIconController.getTint(this.mTintArea, this.mAirplane, this.mIconTint));
    applyDarkIntensity(StatusBarIconController.getDarkIntensity(this.mTintArea, this.mNoSims, this.mDarkIntensity), this.mNoSims, this.mNoSimsDark);
    setTint(this.mWifiActivity, StatusBarIconController.getTint(this.mTintArea, this.mWifiActivity, this.mIconTint));
    setTint(this.mWifi, StatusBarIconController.getTint(this.mTintArea, this.mWifi, this.mIconTint));
    setTint(this.mWifiDark, StatusBarIconController.getTint(this.mTintArea, this.mWifiDark, this.mIconTint));
    applyDarkIntensity(StatusBarIconController.getDarkIntensity(this.mTintArea, this.mEthernet, this.mDarkIntensity), this.mEthernet, this.mEthernetDark);
    int i = 0;
    while (i < this.mPhoneStates.size())
    {
      ((PhoneState)this.mPhoneStates.get(i)).setIconTint(this.mIconTint, this.mDarkIntensity, this.mTintArea);
      i += 1;
    }
  }
  
  private int currentVpnIconId(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 2130838394;
    }
    return 2130839003;
  }
  
  private boolean getImsOverWifiStatus(int paramInt)
  {
    TelephonyManager localTelephonyManager = (TelephonyManager)this.mContext.getSystemService("phone");
    return (localTelephonyManager != null) && ((localTelephonyManager.isVoWifiCallingAvailableForSubscriber(paramInt)) || (localTelephonyManager.isVideoTelephonyWifiCallingAvailableForSubscriber(paramInt)));
  }
  
  private int getInOutIndicator(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int j = 0;
    if (paramBoolean1) {
      j = 1;
    }
    int i = j;
    if (paramBoolean2) {
      i = j + 2;
    }
    j = i;
    if (paramBoolean3) {
      j = i + 4;
    }
    switch (j)
    {
    case 0: 
    default: 
      return 2130838995;
    case 1: 
      return 2130838993;
    case 2: 
      return 2130838996;
    case 3: 
      return 2130838994;
    case 4: 
      return 2130838995;
    case 5: 
      return 2130838993;
    case 6: 
      return 2130838996;
    }
    return 2130838994;
  }
  
  private int getNoSimIcon()
  {
    Resources localResources = getContext().getResources();
    if (!localResources.getBoolean(2131558425)) {
      return 0;
    }
    try
    {
      localObject = localResources.getStringArray(2131427485);
      if (localObject == null) {
        return 0;
      }
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      return 0;
    }
    Object localObject = localObject[0];
    int i = localNotFoundException.getIdentifier((String)localObject, null, getContext().getPackageName());
    if (DEBUG) {
      Log.d("SignalClusterView", "getNoSimIcon resId = " + i + " resName = " + (String)localObject);
    }
    return i;
  }
  
  private PhoneState getState(int paramInt)
  {
    Iterator localIterator = this.mPhoneStates.iterator();
    while (localIterator.hasNext())
    {
      PhoneState localPhoneState = (PhoneState)localIterator.next();
      if (PhoneState.-get8(localPhoneState) == paramInt) {
        return localPhoneState;
      }
    }
    Log.e("SignalClusterView", "Unexpected subscription " + paramInt);
    return null;
  }
  
  private int getWifiActivityId(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 0;
    if (paramBoolean1) {
      i = 1;
    }
    int j = i;
    if (paramBoolean2) {
      j = i + 2;
    }
    switch (j)
    {
    case 0: 
    default: 
      return 2130838982;
    case 1: 
      return 2130838978;
    case 2: 
      return 2130838987;
    }
    return 2130838980;
  }
  
  private boolean hasCorrectSubs(List<SubscriptionInfo> paramList)
  {
    int j = paramList.size();
    if (j != this.mPhoneStates.size()) {
      return false;
    }
    if (paramList.size() == 0) {
      return false;
    }
    int i = 0;
    while (i < j)
    {
      if (PhoneState.-get8((PhoneState)this.mPhoneStates.get(i)) != ((SubscriptionInfo)paramList.get(i)).getSubscriptionId()) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private PhoneState inflatePhoneState(int paramInt1, int paramInt2)
  {
    PhoneState localPhoneState = new PhoneState(paramInt1, paramInt2, this.mContext);
    if (this.mMobileSignalGroup != null) {
      this.mMobileSignalGroup.addView(PhoneState.-get2(localPhoneState));
    }
    this.mPhoneStates.add(localPhoneState);
    return localPhoneState;
  }
  
  private void maybeScaleVpnAndNoSimsIcons()
  {
    if (this.mIconScaleFactor == 1.0F) {
      return;
    }
    this.mVpn.setImageDrawable(new ScalingDrawableWrapper(this.mVpn.getDrawable(), this.mIconScaleFactor));
    this.mNoSims.setImageDrawable(new ScalingDrawableWrapper(this.mNoSims.getDrawable(), this.mIconScaleFactor));
    this.mNoSimsDark.setImageDrawable(new ScalingDrawableWrapper(this.mNoSimsDark.getDrawable(), this.mIconScaleFactor));
  }
  
  private void setIconForView(ImageView paramImageView, int paramInt)
  {
    Drawable localDrawable = paramImageView.getContext().getDrawable(paramInt);
    if (this.mIconScaleFactor == 1.0F)
    {
      paramImageView.setImageDrawable(localDrawable);
      return;
    }
    paramImageView.setImageDrawable(new ScalingDrawableWrapper(localDrawable, this.mIconScaleFactor));
  }
  
  private void setTint(ImageView paramImageView, int paramInt)
  {
    paramImageView.setImageTintList(ColorStateList.valueOf(paramInt));
  }
  
  public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    if ((this.mEthernetVisible) && (this.mEthernetGroup != null) && (this.mEthernetGroup.getContentDescription() != null)) {
      paramAccessibilityEvent.getText().add(this.mEthernetGroup.getContentDescription());
    }
    if ((this.mWifiVisible) && (this.mWifiGroup != null) && (this.mWifiGroup.getContentDescription() != null)) {
      paramAccessibilityEvent.getText().add(this.mWifiGroup.getContentDescription());
    }
    Iterator localIterator = this.mPhoneStates.iterator();
    while (localIterator.hasNext()) {
      ((PhoneState)localIterator.next()).populateAccessibilityEvent(paramAccessibilityEvent);
    }
    return super.dispatchPopulateAccessibilityEventInternal(paramAccessibilityEvent);
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    Iterator localIterator = this.mPhoneStates.iterator();
    while (localIterator.hasNext())
    {
      PhoneState localPhoneState = (PhoneState)localIterator.next();
      this.mMobileSignalGroup.addView(PhoneState.-get2(localPhoneState));
    }
    if (this.mMobileSignalGroup.getChildCount() > 0) {}
    for (int i = this.mMobileSignalGroupEndPadding;; i = 0)
    {
      this.mMobileSignalGroup.setPaddingRelative(0, 0, i, 0);
      TunerService.get(this.mContext).addTunable(this, new String[] { "icon_blacklist" });
      apply();
      applyIconTint();
      this.mNC.addSignalCallback(this);
      return;
    }
  }
  
  protected void onDetachedFromWindow()
  {
    this.mVpn = null;
    this.mEthernetGroup = null;
    this.mEthernet = null;
    this.mWifiGroup = null;
    this.mWifi = null;
    this.mWifiActivity = null;
    this.mAirplane = null;
    this.mImsOverWifiImageView = null;
    this.mMobileSignalGroup.removeAllViews();
    TunerService.get(this.mContext).removeTunable(this);
    this.mSC.removeCallback(this);
    this.mNC.removeSignalCallback(this);
    super.onDetachedFromWindow();
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mVpn = ((ImageView)findViewById(2131952235));
    this.mEthernetGroup = ((ViewGroup)findViewById(2131952236));
    this.mEthernet = ((ImageView)findViewById(2131952237));
    this.mEthernetDark = ((ImageView)findViewById(2131952238));
    this.mWifiGroup = ((ViewGroup)findViewById(2131952240));
    this.mWifi = ((ImageView)findViewById(2131952241));
    this.mWifiDark = ((ImageView)findViewById(2131952242));
    this.mWifiActivity = ((ImageView)findViewById(2131952243));
    this.mAirplane = ((ImageView)findViewById(2131952250));
    this.mNoSims = ((ImageView)findViewById(2131952247));
    this.mNoSimsDark = ((ImageView)findViewById(2131952248));
    this.mImsOverWifiImageView = ((ImageView)findViewById(2131952239));
    this.mNoSimsCombo = findViewById(2131952246);
    this.mWifiAirplaneSpacer = findViewById(2131952249);
    this.mWifiSignalSpacer = findViewById(2131952244);
    this.mMobileSignalGroup = ((LinearLayout)findViewById(2131952245));
    maybeScaleVpnAndNoSimsIcons();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    applyIconTint();
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    if (this.mEthernet != null)
    {
      this.mEthernet.setImageDrawable(null);
      this.mEthernetDark.setImageDrawable(null);
      this.mLastEthernetIconId = -1;
    }
    if (this.mWifi != null)
    {
      this.mWifi.setImageDrawable(null);
      this.mWifiDark.setImageDrawable(null);
      this.mLastWifiStrengthId = -1;
    }
    if (this.mWifiActivity != null)
    {
      this.mWifiActivity.setImageDrawable(null);
      this.mLastWifiActivityId = -1;
    }
    Iterator localIterator = this.mPhoneStates.iterator();
    while (localIterator.hasNext())
    {
      PhoneState localPhoneState = (PhoneState)localIterator.next();
      if (PhoneState.-get0(localPhoneState) != null)
      {
        PhoneState.-wrap0(localPhoneState, PhoneState.-get0(localPhoneState));
        PhoneState.-get0(localPhoneState).setImageDrawable(null);
        PhoneState.-set4(localPhoneState, -1);
      }
      if (PhoneState.-get1(localPhoneState) != null)
      {
        PhoneState.-wrap0(localPhoneState, PhoneState.-get1(localPhoneState));
        PhoneState.-get1(localPhoneState).setImageDrawable(null);
        PhoneState.-set4(localPhoneState, -1);
      }
      if (PhoneState.-get4(localPhoneState) != null)
      {
        PhoneState.-get4(localPhoneState).setImageDrawable(null);
        PhoneState.-set5(localPhoneState, -1);
      }
      if (PhoneState.-get3(localPhoneState) != null) {
        PhoneState.-get3(localPhoneState).setImageDrawable(null);
      }
    }
    if (this.mAirplane != null)
    {
      this.mAirplane.setImageDrawable(null);
      this.mLastAirplaneIconId = -1;
    }
    apply();
  }
  
  public void onStateChanged()
  {
    post(new Runnable()
    {
      public void run()
      {
        SignalClusterView.-set2(SignalClusterView.this, SignalClusterView.this.mSC.isVpnEnabled());
        SignalClusterView.-set1(SignalClusterView.this, SignalClusterView.-wrap0(SignalClusterView.this, SignalClusterView.this.mSC.isVpnBranded()));
        SignalClusterView.-wrap3(SignalClusterView.this);
      }
    });
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if (!"icon_blacklist".equals(paramString1)) {
      return;
    }
    paramString1 = StatusBarIconController.getIconBlacklist(paramString2);
    boolean bool1 = paramString1.contains("airplane");
    boolean bool2 = paramString1.contains("mobile");
    boolean bool3 = paramString1.contains("wifi");
    boolean bool4 = paramString1.contains("ethernet");
    if ((bool1 != this.mBlockAirplane) || (bool2 != this.mBlockMobile)) {
      break label107;
    }
    for (;;)
    {
      this.mBlockAirplane = bool1;
      this.mBlockMobile = bool2;
      this.mBlockEthernet = bool4;
      this.mBlockWifi = bool3;
      this.mNC.removeSignalCallback(this);
      this.mNC.addSignalCallback(this);
      label107:
      return;
      if (bool4 == this.mBlockEthernet) {
        if (bool3 == this.mBlockWifi) {
          break;
        }
      }
    }
  }
  
  public void setEthernetIndicators(NetworkController.IconState paramIconState)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramIconState.visible) {
      if (!this.mBlockEthernet) {
        break label46;
      }
    }
    label46:
    for (bool1 = bool2;; bool1 = true)
    {
      this.mEthernetVisible = bool1;
      this.mEthernetIconId = paramIconState.icon;
      this.mEthernetDescription = paramIconState.contentDescription;
      apply();
      return;
    }
  }
  
  public void setIconTint(int paramInt, float paramFloat, Rect paramRect)
  {
    int i;
    if ((paramInt != this.mIconTint) || (paramFloat != this.mDarkIntensity)) {
      i = 1;
    }
    for (;;)
    {
      this.mIconTint = paramInt;
      this.mDarkIntensity = paramFloat;
      this.mTintArea.set(paramRect);
      if ((i != 0) && (isAttachedToWindow())) {
        applyIconTint();
      }
      return;
      if (this.mTintArea.equals(paramRect)) {
        i = 0;
      } else {
        i = 1;
      }
    }
  }
  
  public void setIsAirplaneMode(NetworkController.IconState paramIconState)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramIconState.visible) {
      if (!this.mBlockAirplane) {
        break label46;
      }
    }
    label46:
    for (bool1 = bool2;; bool1 = true)
    {
      this.mIsAirplaneMode = bool1;
      this.mAirplaneIconId = paramIconState.icon;
      this.mAirplaneContentDescription = paramIconState.contentDescription;
      apply();
      return;
    }
  }
  
  public void setMobileDataEnabled(boolean paramBoolean) {}
  
  public void setMobileDataIndicators(NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString1, String paramString2, boolean paramBoolean3, int paramInt7, int paramInt8, int paramInt9, int paramInt10, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6)
  {
    PhoneState localPhoneState = getState(paramInt7);
    if (localPhoneState == null) {
      return;
    }
    PhoneState.-set1(localPhoneState, paramInt8);
    PhoneState.-set9(localPhoneState, paramInt9);
    PhoneState.-set10(localPhoneState, paramInt10);
    this.mImsOverWifi = paramBoolean4;
    setMobileDataIndicators(paramIconState1, paramIconState2, paramInt1, paramInt2, paramBoolean1, paramBoolean2, paramInt3, paramInt4, paramInt5, paramInt6, paramString1, paramString2, paramBoolean3, paramInt7, paramBoolean5, paramBoolean6);
  }
  
  public void setMobileDataIndicators(NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString1, String paramString2, boolean paramBoolean3, int paramInt7, boolean paramBoolean4, boolean paramBoolean5)
  {
    paramIconState2 = getState(paramInt7);
    if (paramIconState2 == null) {
      return;
    }
    boolean bool;
    if ((!paramIconState1.visible) || (this.mBlockMobile))
    {
      bool = false;
      PhoneState.-set16(paramIconState2, bool);
      PhoneState.-set13(paramIconState2, paramIconState1.icon);
      PhoneState.-set15(paramIconState2, paramInt1);
      PhoneState.-set8(paramIconState2, paramIconState1.contentDescription);
      PhoneState.-set14(paramIconState2, paramString1);
      if (paramInt1 == 0) {
        break label153;
      }
      label71:
      PhoneState.-set3(paramIconState2, paramBoolean3);
      PhoneState.-set0(paramIconState2, paramInt3);
      PhoneState.-set6(paramIconState2, paramInt4);
      PhoneState.-set17(paramIconState2, paramInt5);
      PhoneState.-set18(paramIconState2, paramInt6);
      PhoneState.-set11(paramIconState2, paramBoolean1);
      PhoneState.-set12(paramIconState2, paramBoolean2);
      PhoneState.-set7(paramIconState2, paramBoolean4);
      if (!paramBoolean5) {
        break label159;
      }
    }
    label153:
    label159:
    for (paramBoolean1 = false;; paramBoolean1 = true)
    {
      PhoneState.-set2(paramIconState2, paramBoolean1);
      apply();
      return;
      bool = true;
      break;
      paramBoolean3 = false;
      break label71;
    }
  }
  
  public void setNetworkController(NetworkControllerImpl paramNetworkControllerImpl)
  {
    if (DEBUG) {
      Log.d("SignalClusterView", "NetworkController=" + paramNetworkControllerImpl);
    }
    this.mNC = paramNetworkControllerImpl;
  }
  
  public void setNoSims(boolean paramBoolean)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramBoolean) {
      if (!this.mBlockMobile) {
        break label27;
      }
    }
    label27:
    for (bool1 = bool2;; bool1 = true)
    {
      this.mNoSimsVisible = bool1;
      apply();
      return;
    }
  }
  
  public void setSecurityController(SecurityController paramSecurityController)
  {
    if (DEBUG) {
      Log.d("SignalClusterView", "SecurityController=" + paramSecurityController);
    }
    this.mSC = paramSecurityController;
    this.mSC.addCallback(this);
    this.mVpnVisible = this.mSC.isVpnEnabled();
    this.mVpnIconId = currentVpnIconId(this.mSC.isVpnBranded());
  }
  
  public void setSubs(List<SubscriptionInfo> paramList)
  {
    if (hasCorrectSubs(paramList)) {
      return;
    }
    Iterator localIterator = this.mPhoneStates.iterator();
    while (localIterator.hasNext())
    {
      PhoneState localPhoneState = (PhoneState)localIterator.next();
      if (PhoneState.-get0(localPhoneState) != null) {
        PhoneState.-wrap0(localPhoneState, PhoneState.-get0(localPhoneState));
      }
      if (PhoneState.-get1(localPhoneState) != null) {
        PhoneState.-wrap0(localPhoneState, PhoneState.-get1(localPhoneState));
      }
    }
    this.mPhoneStates.clear();
    if (this.mMobileSignalGroup != null) {
      this.mMobileSignalGroup.removeAllViews();
    }
    int k = paramList.size();
    int i = TelephonyManager.getDefault().getPhoneCount();
    int j = 0;
    if (i == 1)
    {
      i = j;
      if (k == 0)
      {
        inflatePhoneState(-1, 0);
        i = j;
      }
    }
    for (;;)
    {
      j = 0;
      while (j < k)
      {
        int m = ((SubscriptionInfo)paramList.get(j)).getSimSlotIndex();
        if ((i != 0) && (m == 1)) {
          inflatePhoneState(-1, 0);
        }
        inflatePhoneState(((SubscriptionInfo)paramList.get(j)).getSubscriptionId(), m);
        if ((i != 0) && (m == 0)) {
          inflatePhoneState(-1, 1);
        }
        j += 1;
      }
      if (k == 0)
      {
        inflatePhoneState(-1, 0);
        inflatePhoneState(-2, 1);
        i = j;
      }
      else
      {
        i = j;
        if (k == 1)
        {
          i = j;
          if (Utils.hasCtaFeature(this.mContext)) {
            i = 1;
          }
        }
      }
    }
    boolean bool = false;
    i = 0;
    while (i < k)
    {
      bool |= getImsOverWifiStatus(((SubscriptionInfo)paramList.get(i)).getSubscriptionId());
      i += 1;
    }
    this.mImsOverWifi = bool;
    if (isAttachedToWindow()) {
      applyIconTint();
    }
  }
  
  public void setWifiIndicators(boolean paramBoolean1, NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, boolean paramBoolean2, boolean paramBoolean3, String paramString)
  {
    boolean bool = false;
    paramBoolean1 = bool;
    if (paramIconState1.visible) {
      if (!this.mBlockWifi) {
        break label61;
      }
    }
    label61:
    for (paramBoolean1 = bool;; paramBoolean1 = true)
    {
      this.mWifiVisible = paramBoolean1;
      this.mWifiStrengthId = paramIconState1.icon;
      this.mWifiActivityId = getWifiActivityId(paramBoolean2, paramBoolean3);
      this.mWifiDescription = paramIconState1.contentDescription;
      apply();
      return;
    }
  }
  
  private class PhoneState
  {
    private ImageView mDataActivity;
    private int mDataActivityId = 0;
    private ImageView mDataNetworkTypeInRoaming;
    private int mDataNetworkTypeInRoamingId = 0;
    private boolean mDisabled = false;
    private boolean mIsMobileTypeIconWide;
    private int mLastMobileStrengthId = -1;
    private int mLastMobileTypeId = -1;
    private ImageView mMobile;
    private ImageView mMobileActivity;
    private int mMobileActivityId = 0;
    private ImageView mMobileDark;
    private boolean mMobileDataConnected;
    private String mMobileDescription;
    private ImageView mMobileEmbms;
    private int mMobileEmbmsId = 0;
    private ViewGroup mMobileGroup;
    private ImageView mMobileIms;
    private int mMobileImsId = 0;
    private boolean mMobileIn;
    private ImageView mMobileInOut;
    private boolean mMobileOut;
    private ViewGroup mMobileSingleGroup;
    private ViewGroup mMobileStackedGroup;
    private int mMobileStrengthId = 0;
    private ImageView mMobileType;
    private String mMobileTypeDescription;
    private int mMobileTypeId = 0;
    private boolean mMobileVisible = false;
    private ImageView mRoaming;
    private final int mSlotIndex = -1;
    private ImageView mStackedData;
    private int mStackedDataId = 0;
    private ImageView mStackedVoice;
    private int mStackedVoiceId = 0;
    private final int mSubId;
    
    public PhoneState(int paramInt1, int paramInt2, Context paramContext)
    {
      setViews((ViewGroup)LayoutInflater.from(paramContext).inflate(2130968718, null));
      this.mSubId = paramInt1;
      if (paramInt1 < 0)
      {
        this.mMobileStrengthId = 2130838475;
        if (!SignalClusterView.-get0(SignalClusterView.this)) {
          break label129;
        }
      }
      for (;;)
      {
        this.mMobileVisible = bool;
        return;
        label129:
        bool = true;
      }
    }
    
    private void maybeStartAnimatableDrawable(ImageView paramImageView)
    {
      Drawable localDrawable = paramImageView.getDrawable();
      paramImageView = localDrawable;
      if ((localDrawable instanceof ScalingDrawableWrapper)) {
        paramImageView = ((ScalingDrawableWrapper)localDrawable).getDrawable();
      }
      if ((paramImageView instanceof Animatable))
      {
        paramImageView = (Animatable)paramImageView;
        if ((paramImageView instanceof AnimatedVectorDrawable)) {
          ((AnimatedVectorDrawable)paramImageView).forceAnimationOnUI();
        }
        if (!paramImageView.isRunning()) {
          paramImageView.start();
        }
      }
    }
    
    private void maybeStopAnimatableDrawable(ImageView paramImageView)
    {
      Drawable localDrawable = paramImageView.getDrawable();
      paramImageView = localDrawable;
      if ((localDrawable instanceof ScalingDrawableWrapper)) {
        paramImageView = ((ScalingDrawableWrapper)localDrawable).getDrawable();
      }
      if ((paramImageView instanceof Animatable))
      {
        paramImageView = (Animatable)paramImageView;
        if (paramImageView.isRunning()) {
          paramImageView.stop();
        }
      }
    }
    
    private void updateAnimatableIcon(ImageView paramImageView, int paramInt)
    {
      maybeStopAnimatableDrawable(paramImageView);
      SignalClusterView.-wrap4(SignalClusterView.this, paramImageView, paramInt);
      maybeStartAnimatableDrawable(paramImageView);
    }
    
    public boolean apply(boolean paramBoolean)
    {
      int j = 0;
      boolean bool;
      Object localObject;
      if ((this.mStackedDataId != 0) && (this.mStackedVoiceId != 0))
      {
        bool = true;
        if ((this.mMobileVisible) && (!SignalClusterView.-get1(SignalClusterView.this))) {
          break label397;
        }
        this.mMobileGroup.setVisibility(8);
        localObject = this.mMobileGroup;
        if (!paramBoolean) {
          break label855;
        }
        i = SignalClusterView.-get4(SignalClusterView.this);
        label63:
        ((ViewGroup)localObject).setPaddingRelative(i, 0, 0, 0);
        localObject = this.mMobile;
        if (!this.mIsMobileTypeIconWide) {
          break label860;
        }
        i = SignalClusterView.-get5(SignalClusterView.this);
        label93:
        ((ImageView)localObject).setPaddingRelative(i, 0, 0, 0);
        localObject = this.mMobileDark;
        if (!this.mIsMobileTypeIconWide) {
          break label871;
        }
        i = SignalClusterView.-get5(SignalClusterView.this);
        label123:
        ((ImageView)localObject).setPaddingRelative(i, 0, 0, 0);
        if (SignalClusterView.DEBUG)
        {
          if (!this.mMobileVisible) {
            break label882;
          }
          localObject = "VISIBLE";
          label149:
          Log.d("SignalClusterView", String.format("mobile: %s sig=%d typ=%d", new Object[] { localObject, Integer.valueOf(this.mMobileStrengthId), Integer.valueOf(this.mMobileTypeId) }));
        }
        localObject = this.mMobileType;
        if (this.mMobileTypeId == 0) {
          break label890;
        }
        i = 0;
        label204:
        ((ImageView)localObject).setVisibility(i);
        localObject = this.mDataActivity;
        if (this.mDataActivityId == 0) {
          break label896;
        }
        i = 0;
        label225:
        ((ImageView)localObject).setVisibility(i);
        localObject = this.mMobileActivity;
        if (this.mMobileActivityId == 0) {
          break label902;
        }
        i = 0;
        label246:
        ((ImageView)localObject).setVisibility(i);
        localObject = this.mDataNetworkTypeInRoaming;
        if (this.mDataNetworkTypeInRoamingId == 0) {
          break label908;
        }
        i = 0;
        label267:
        ((ImageView)localObject).setVisibility(i);
        localObject = this.mMobileEmbms;
        if (this.mMobileEmbmsId == 0) {
          break label914;
        }
        i = 0;
        label288:
        ((ImageView)localObject).setVisibility(i);
        localObject = this.mMobileIms;
        if (this.mMobileImsId == 0) {
          break label920;
        }
        i = 0;
        label309:
        ((ImageView)localObject).setVisibility(i);
        localObject = this.mMobileType;
        if ((bool) || (this.mMobileTypeId == 0)) {
          break label926;
        }
        i = 0;
        label335:
        ((ImageView)localObject).setVisibility(i);
        this.mMobileInOut.setImageResource(SignalClusterView.-wrap1(SignalClusterView.this, this.mMobileIn, this.mMobileOut, bool));
        localObject = this.mMobileInOut;
        if (!this.mMobileDataConnected) {
          break label932;
        }
      }
      label397:
      label855:
      label860:
      label871:
      label882:
      label890:
      label896:
      label902:
      label908:
      label914:
      label920:
      label926:
      label932:
      for (int i = j;; i = 8)
      {
        ((ImageView)localObject).setVisibility(i);
        return this.mMobileVisible;
        bool = false;
        break;
        if (!this.mDisabled)
        {
          if (this.mLastMobileStrengthId != this.mMobileStrengthId)
          {
            updateAnimatableIcon(this.mMobile, this.mMobileStrengthId);
            updateAnimatableIcon(this.mMobileDark, this.mMobileStrengthId);
            this.mLastMobileStrengthId = this.mMobileStrengthId;
          }
          if (this.mLastMobileTypeId != this.mMobileTypeId)
          {
            this.mMobileType.setImageResource(this.mMobileTypeId);
            this.mLastMobileTypeId = this.mMobileTypeId;
          }
          this.mDataActivity.setImageResource(this.mDataActivityId);
          localObject = this.mDataActivity.getDrawable();
          if ((localObject instanceof Animatable))
          {
            localObject = (Animatable)localObject;
            if (!((Animatable)localObject).isRunning()) {
              ((Animatable)localObject).start();
            }
          }
          this.mMobileActivity.setImageResource(this.mMobileActivityId);
          localObject = this.mMobileActivity.getDrawable();
          if ((localObject instanceof Animatable))
          {
            localObject = (Animatable)localObject;
            if (!((Animatable)localObject).isRunning()) {
              ((Animatable)localObject).start();
            }
          }
          this.mMobileEmbms.setImageResource(this.mMobileEmbmsId);
          this.mDataNetworkTypeInRoaming.setImageResource(this.mDataNetworkTypeInRoamingId);
          this.mMobileIms.setImageResource(this.mMobileImsId);
          if ((this.mStackedDataId != 0) && (this.mStackedVoiceId != 0))
          {
            this.mStackedData.setImageResource(this.mStackedDataId);
            this.mStackedVoice.setImageResource(this.mStackedVoiceId);
            this.mMobileSingleGroup.setVisibility(8);
            this.mMobileStackedGroup.setVisibility(0);
          }
          for (;;)
          {
            this.mMobileGroup.setContentDescription(this.mMobileTypeDescription + " " + this.mMobileDescription);
            this.mMobileGroup.setVisibility(0);
            break;
            this.mStackedData.setImageResource(0);
            this.mStackedVoice.setImageResource(0);
            this.mMobileSingleGroup.setVisibility(0);
            this.mMobileStackedGroup.setVisibility(8);
          }
        }
        if (SignalClusterView.-get3(SignalClusterView.this) == 0) {
          SignalClusterView.-set0(SignalClusterView.this, SignalClusterView.-wrap2(SignalClusterView.this));
        }
        if (SignalClusterView.-get3(SignalClusterView.this) != 0)
        {
          this.mMobile.setImageResource(SignalClusterView.-get3(SignalClusterView.this));
          this.mMobileDark.setImageResource(SignalClusterView.-get3(SignalClusterView.this));
        }
        for (;;)
        {
          this.mMobileSingleGroup.setVisibility(0);
          this.mMobileStackedGroup.setVisibility(8);
          break;
          this.mMobile.setImageResource(2130838475);
          this.mMobileDark.setImageResource(2130838475);
        }
        i = 0;
        break label63;
        i = SignalClusterView.-get2(SignalClusterView.this);
        break label93;
        i = SignalClusterView.-get2(SignalClusterView.this);
        break label123;
        localObject = "GONE";
        break label149;
        i = 8;
        break label204;
        i = 8;
        break label225;
        i = 8;
        break label246;
        i = 8;
        break label267;
        i = 8;
        break label288;
        i = 8;
        break label309;
        i = 8;
        break label335;
      }
    }
    
    public void populateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      if ((this.mMobileVisible) && (this.mMobileGroup != null) && (this.mMobileGroup.getContentDescription() != null)) {
        paramAccessibilityEvent.getText().add(this.mMobileGroup.getContentDescription());
      }
    }
    
    public void setIconTint(int paramInt, float paramFloat, Rect paramRect)
    {
      SignalClusterView.-wrap5(SignalClusterView.this, this.mMobileType, StatusBarIconController.getTint(paramRect, this.mMobileType, paramInt));
      SignalClusterView.-wrap5(SignalClusterView.this, this.mMobile, StatusBarIconController.getTint(paramRect, this.mMobile, paramInt));
      SignalClusterView.-wrap5(SignalClusterView.this, this.mMobileInOut, StatusBarIconController.getTint(paramRect, this.mMobileInOut, paramInt));
      SignalClusterView.-wrap5(SignalClusterView.this, this.mStackedData, StatusBarIconController.getTint(paramRect, this.mStackedData, paramInt));
      SignalClusterView.-wrap5(SignalClusterView.this, this.mStackedVoice, StatusBarIconController.getTint(paramRect, this.mStackedVoice, paramInt));
    }
    
    public void setViews(ViewGroup paramViewGroup)
    {
      this.mMobileGroup = paramViewGroup;
      this.mMobile = ((ImageView)paramViewGroup.findViewById(2131952075));
      this.mMobileDark = ((ImageView)paramViewGroup.findViewById(2131952076));
      this.mMobileType = ((ImageView)paramViewGroup.findViewById(2131952077));
      this.mMobileActivity = ((ImageView)paramViewGroup.findViewById(2131952072));
      this.mMobileIms = ((ImageView)paramViewGroup.findViewById(2131952069));
      this.mMobileInOut = ((ImageView)paramViewGroup.findViewById(2131952072));
      this.mDataNetworkTypeInRoaming = ((ImageView)paramViewGroup.findViewById(2131952070));
      this.mMobileEmbms = ((ImageView)paramViewGroup.findViewById(2131952068));
      this.mDataActivity = ((ImageView)paramViewGroup.findViewById(2131952071));
      this.mStackedData = ((ImageView)paramViewGroup.findViewById(2131952079));
      this.mStackedVoice = ((ImageView)paramViewGroup.findViewById(2131952080));
      this.mMobileSingleGroup = ((ViewGroup)paramViewGroup.findViewById(2131952074));
      this.mMobileStackedGroup = ((ViewGroup)paramViewGroup.findViewById(2131952078));
      this.mRoaming = ((ImageView)paramViewGroup.findViewById(2131952073));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\SignalClusterView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */