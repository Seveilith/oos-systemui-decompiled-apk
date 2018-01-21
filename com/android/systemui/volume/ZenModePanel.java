package com.android.systemui.volume;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ZenRule;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Prefs;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.ZenModeController.Callback;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class ZenModePanel
  extends LinearLayout
{
  private static final boolean DEBUG = Log.isLoggable("ZenModePanel", 3);
  private static final int DEFAULT_BUCKET_INDEX;
  private static final int MAX_BUCKET_MINUTES;
  private static final int[] MINUTE_BUCKETS;
  private static final int MIN_BUCKET_MINUTES;
  public static final Intent ZEN_PRIORITY_SETTINGS;
  public static final Intent ZEN_RING_MODE_SETTINGS;
  public static final Intent ZEN_SETTINGS;
  public static final Intent ZEN_SILENT_MODE_SETTINGS;
  private boolean mAttached;
  private int mAttachedZen;
  private int mBucketIndex = -1;
  private Callback mCallback;
  private Condition[] mConditions;
  private final Context mContext;
  private ZenModeController mController;
  private boolean mCountdownConditionSupported;
  private Condition mExitCondition;
  private boolean mExpanded;
  private final Uri mForeverId;
  private final H mHandler = new H(null);
  private boolean mHidden;
  protected final LayoutInflater mInflater;
  private final Interaction.Callback mInteractionCallback = new Interaction.Callback()
  {
    public void onInteraction()
    {
      ZenModePanel.-wrap3(ZenModePanel.this);
    }
  };
  private final ZenPrefs mPrefs;
  private boolean mRequestingConditions;
  private Condition mSessionExitCondition;
  private int mSessionZen;
  private final SpTexts mSpTexts;
  private String mTag = "ZenModePanel/" + Integer.toHexString(System.identityHashCode(this));
  private Condition mTimeCondition;
  private final TransitionHelper mTransitionHelper = new TransitionHelper(null);
  private boolean mVoiceCapable;
  private TextView mZenAlarmWarning;
  protected SegmentedButtons mZenButtons;
  protected final SegmentedButtons.Callback mZenButtonsCallback = new SegmentedButtons.Callback()
  {
    public void onInteraction()
    {
      ZenModePanel.-wrap3(ZenModePanel.this);
    }
    
    public void onSelected(Object paramAnonymousObject, boolean paramAnonymousBoolean)
    {
      if ((paramAnonymousObject != null) && (ZenModePanel.this.mZenButtons.isShown()) && (ZenModePanel.this.isAttachedToWindow()))
      {
        final int i = ((Integer)paramAnonymousObject).intValue();
        if (paramAnonymousBoolean) {
          MetricsLogger.action(ZenModePanel.-get4(ZenModePanel.this), 165, i);
        }
        if (ZenModePanel.-get0()) {
          Log.d(ZenModePanel.-get10(ZenModePanel.this), "mZenButtonsCallback selected=" + i);
        }
        AsyncTask.execute(new Runnable()
        {
          public void run()
          {
            ZenModePanel.-get5(ZenModePanel.this).setZen(i, this.val$realConditionId, "ZenModePanel.selectZen");
            if (i != 0) {
              Prefs.putInt(ZenModePanel.-get4(ZenModePanel.this), "DndFavoriteZen", i);
            }
          }
        });
      }
    }
  };
  private final ZenModeController.Callback mZenCallback = new ZenModeController.Callback()
  {
    public void onManualRuleChanged(ZenModeConfig.ZenRule paramAnonymousZenRule)
    {
      ZenModePanel.-get7(ZenModePanel.this).obtainMessage(2, paramAnonymousZenRule).sendToTarget();
    }
  };
  protected LinearLayout mZenConditions;
  private View mZenIntroduction;
  private View mZenIntroductionConfirm;
  private TextView mZenIntroductionCustomize;
  private TextView mZenIntroductionMessage;
  private RadioGroup mZenRadioGroup;
  private LinearLayout mZenRadioGroupContent;
  
  static
  {
    int[] arrayOfInt;
    if (DEBUG)
    {
      arrayOfInt = new int[12];
      arrayOfInt[0] = 0;
      arrayOfInt[1] = 1;
      arrayOfInt[2] = 2;
      arrayOfInt[3] = 5;
      arrayOfInt[4] = 15;
      arrayOfInt[5] = 30;
      arrayOfInt[6] = 45;
      arrayOfInt[7] = 60;
      arrayOfInt[8] = 120;
      arrayOfInt[9] = '´';
      arrayOfInt[10] = 'ð';
      arrayOfInt[11] = 'Ǡ';
      arrayOfInt;
    }
    for (;;)
    {
      MINUTE_BUCKETS = arrayOfInt;
      MIN_BUCKET_MINUTES = MINUTE_BUCKETS[0];
      MAX_BUCKET_MINUTES = MINUTE_BUCKETS[(MINUTE_BUCKETS.length - 1)];
      DEFAULT_BUCKET_INDEX = Arrays.binarySearch(MINUTE_BUCKETS, 60);
      ZEN_SETTINGS = new Intent("android.settings.ZEN_MODE_SETTINGS");
      ZEN_PRIORITY_SETTINGS = new Intent("android.settings.ZEN_MODE_PRIORITY_SETTINGS");
      ZEN_SILENT_MODE_SETTINGS = new Intent("android.oneplus.ZEN_SILENT_MODE_SETTINGS");
      ZEN_RING_MODE_SETTINGS = new Intent("android.oneplus.ZEN_RING_MODE_SETTINGS");
      return;
      arrayOfInt = ZenModeConfig.MINUTE_BUCKETS;
    }
  }
  
  public ZenModePanel(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.mPrefs = new ZenPrefs(null);
    this.mInflater = LayoutInflater.from(this.mContext.getApplicationContext());
    this.mForeverId = Condition.newId(this.mContext).appendPath("forever").build();
    this.mSpTexts = new SpTexts(this.mContext);
    this.mVoiceCapable = Util.isVoiceCapable(this.mContext);
    if (DEBUG) {
      Log.d(this.mTag, "new ZenModePanel");
    }
  }
  
  private void announceConditionSelection(ConditionTag paramConditionTag)
  {
    String str;
    switch (getSelectedZen(0))
    {
    default: 
      return;
    case 1: 
      str = this.mContext.getString(2131690359);
    }
    for (;;)
    {
      announceForAccessibility(this.mContext.getString(2131690427, new Object[] { str, paramConditionTag.line1.getText() }));
      return;
      str = this.mContext.getString(2131690358);
      continue;
      str = this.mContext.getString(2131690360);
    }
  }
  
  private void bind(Condition paramCondition, final View paramView, final int paramInt)
  {
    if (paramCondition == null) {
      throw new IllegalArgumentException("condition must not be null");
    }
    boolean bool2;
    final ConditionTag localConditionTag;
    label42:
    boolean bool1;
    label59:
    final Object localObject2;
    Object localObject1;
    label300:
    Object localObject3;
    label334:
    float f;
    label359:
    long l;
    if (paramCondition.state == 1)
    {
      bool2 = true;
      if (paramView.getTag() == null) {
        break label616;
      }
      localConditionTag = (ConditionTag)paramView.getTag();
      paramView.setTag(localConditionTag);
      if (localConditionTag.rb != null) {
        break label629;
      }
      bool1 = true;
      if (localConditionTag.rb == null) {
        localConditionTag.rb = ((RadioButton)this.mZenRadioGroup.getChildAt(paramInt));
      }
      localConditionTag.condition = paramCondition;
      localObject2 = getConditionId(localConditionTag.condition);
      if (DEBUG) {
        Log.d(this.mTag, "bind i=" + this.mZenRadioGroupContent.indexOfChild(paramView) + " first=" + bool1 + " condition=" + localObject2);
      }
      localConditionTag.rb.setEnabled(bool2);
      localConditionTag.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          if ((ZenModePanel.-get6(ZenModePanel.this)) && (paramAnonymousBoolean))
          {
            localConditionTag.rb.setChecked(true);
            if (ZenModePanel.-get0()) {
              Log.d(ZenModePanel.-get10(ZenModePanel.this), "onCheckedChanged " + localObject2);
            }
            MetricsLogger.action(ZenModePanel.-get4(ZenModePanel.this), 164);
            ZenModePanel.-wrap6(ZenModePanel.this, localConditionTag.condition);
            ZenModePanel.-wrap1(ZenModePanel.this, localConditionTag);
          }
        }
      });
      if (localConditionTag.lines == null) {
        localConditionTag.lines = paramView.findViewById(16908290);
      }
      if (localConditionTag.line1 == null)
      {
        localConditionTag.line1 = ((TextView)paramView.findViewById(16908308));
        this.mSpTexts.add(localConditionTag.line1);
      }
      if (localConditionTag.line2 == null)
      {
        localConditionTag.line2 = ((TextView)paramView.findViewById(16908309));
        this.mSpTexts.add(localConditionTag.line2);
      }
      if (TextUtils.isEmpty(paramCondition.line1)) {
        break label635;
      }
      localObject1 = paramCondition.line1;
      localObject3 = paramCondition.line2;
      localConditionTag.line1.setText((CharSequence)localObject1);
      if (!TextUtils.isEmpty((CharSequence)localObject3)) {
        break label644;
      }
      localConditionTag.line2.setVisibility(8);
      localConditionTag.lines.setEnabled(bool2);
      localObject1 = localConditionTag.lines;
      if (!bool2) {
        break label666;
      }
      f = 1.0F;
      ((View)localObject1).setAlpha(f);
      localObject1 = (ImageView)paramView.findViewById(16908313);
      ((ImageView)localObject1).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ZenModePanel.-wrap5(ZenModePanel.this, paramView, localConditionTag, false, paramInt);
        }
      });
      localObject3 = (ImageView)paramView.findViewById(16908314);
      ((ImageView)localObject3).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ZenModePanel.-wrap5(ZenModePanel.this, paramView, localConditionTag, true, paramInt);
        }
      });
      localConditionTag.lines.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          localConditionTag.rb.setChecked(true);
        }
      });
      l = ZenModeConfig.tryParseCountdownConditionId((Uri)localObject2);
      if ((paramInt == 2) || (l <= 0L)) {
        break label785;
      }
      ((ImageView)localObject1).setVisibility(0);
      ((ImageView)localObject3).setVisibility(0);
      if (this.mBucketIndex <= -1) {
        break label686;
      }
      if (this.mBucketIndex <= 0) {
        break label674;
      }
      bool2 = true;
      label491:
      ((ImageView)localObject1).setEnabled(bool2);
      if (this.mBucketIndex >= MINUTE_BUCKETS.length - 1) {
        break label680;
      }
      bool2 = true;
      label514:
      ((ImageView)localObject3).setEnabled(bool2);
      if (!((ImageView)localObject1).isEnabled()) {
        break label769;
      }
      f = 1.0F;
      label532:
      ((ImageView)localObject1).setAlpha(f);
      if (!((ImageView)localObject3).isEnabled()) {
        break label777;
      }
      f = 1.0F;
      label550:
      ((ImageView)localObject3).setAlpha(f);
    }
    for (;;)
    {
      if (bool1)
      {
        Interaction.register(localConditionTag.rb, this.mInteractionCallback);
        Interaction.register(localConditionTag.lines, this.mInteractionCallback);
        Interaction.register((View)localObject1, this.mInteractionCallback);
        Interaction.register((View)localObject3, this.mInteractionCallback);
      }
      paramView.setVisibility(0);
      return;
      bool2 = false;
      break;
      label616:
      localConditionTag = new ConditionTag(null);
      break label42;
      label629:
      bool1 = false;
      break label59;
      label635:
      localObject1 = paramCondition.summary;
      break label300;
      label644:
      localConditionTag.line2.setVisibility(0);
      localConditionTag.line2.setText((CharSequence)localObject3);
      break label334;
      label666:
      f = 0.4F;
      break label359;
      label674:
      bool2 = false;
      break label491;
      label680:
      bool2 = false;
      break label514;
      label686:
      if (l - System.currentTimeMillis() > MIN_BUCKET_MINUTES * 60000)
      {
        bool2 = true;
        label707:
        ((ImageView)localObject1).setEnabled(bool2);
        localObject2 = ZenModeConfig.toTimeCondition(this.mContext, MAX_BUCKET_MINUTES, ActivityManager.getCurrentUser());
        if (!Objects.equals(paramCondition.summary, ((Condition)localObject2).summary)) {
          break label763;
        }
      }
      label763:
      for (bool2 = false;; bool2 = true)
      {
        ((ImageView)localObject3).setEnabled(bool2);
        break;
        bool2 = false;
        break label707;
      }
      label769:
      f = 0.5F;
      break label532;
      label777:
      f = 0.5F;
      break label550;
      label785:
      ((ImageView)localObject1).setVisibility(8);
      ((ImageView)localObject3).setVisibility(8);
    }
  }
  
  private void checkForAttachedZenChange()
  {
    int i = getSelectedZen(-1);
    if (DEBUG) {
      Log.d(this.mTag, "selectedZen=" + i);
    }
    if (i != this.mAttachedZen)
    {
      if (DEBUG) {
        Log.d(this.mTag, "attachedZen: " + this.mAttachedZen + " -> " + i);
      }
      if (i == 2) {
        this.mPrefs.trackNoneSelected();
      }
    }
  }
  
  private String computeAlarmWarningText(boolean paramBoolean)
  {
    if (!paramBoolean) {
      return null;
    }
    long l1 = System.currentTimeMillis();
    long l2 = this.mController.getNextAlarm();
    if (l2 < l1) {
      return null;
    }
    int j = 0;
    int i;
    if ((this.mSessionExitCondition == null) || (isForever(this.mSessionExitCondition))) {
      i = 2131690471;
    }
    while (i == 0)
    {
      return null;
      long l3 = ZenModeConfig.tryParseCountdownConditionId(this.mSessionExitCondition.id);
      i = j;
      if (l3 > l1)
      {
        i = j;
        if (l2 < l3) {
          i = 2131690472;
        }
      }
    }
    Object localObject;
    if (l2 - l1 < 86400000L)
    {
      j = 1;
      paramBoolean = DateFormat.is24HourFormat(this.mContext, ActivityManager.getCurrentUser());
      if (j == 0) {
        break label212;
      }
      if (!paramBoolean) {
        break label204;
      }
      localObject = "Hm";
      label139:
      localObject = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), (String)localObject), l2);
      if (j == 0) {
        break label232;
      }
    }
    label204:
    label212:
    label232:
    for (j = 2131690473;; j = 2131690474)
    {
      localObject = getResources().getString(j, new Object[] { localObject });
      return getResources().getString(i, new Object[] { localObject });
      j = 0;
      break;
      localObject = "hma";
      break label139;
      if (paramBoolean)
      {
        localObject = "EEEHm";
        break label139;
      }
      localObject = "EEEhma";
      break label139;
    }
  }
  
  private void confirmZenIntroduction()
  {
    String str = prefKeyForConfirmation(getSelectedZen(0));
    if (str == null) {
      return;
    }
    if (DEBUG) {
      Log.d("ZenModePanel", "confirmZenIntroduction " + str);
    }
    Prefs.putBoolean(this.mContext, str, true);
    this.mHandler.sendEmptyMessage(3);
  }
  
  private static Condition copy(Condition paramCondition)
  {
    if (paramCondition == null) {
      return null;
    }
    return paramCondition.copy();
  }
  
  private void ensureSelection()
  {
    int j = getVisibleConditions();
    if (j == 0) {
      return;
    }
    int i = 0;
    while (i < j)
    {
      localConditionTag = getConditionTagAt(i);
      if ((localConditionTag != null) && (localConditionTag.rb.isChecked()))
      {
        if (DEBUG) {
          Log.d(this.mTag, "Not selecting a default, checked=" + localConditionTag.condition);
        }
        return;
      }
      i += 1;
    }
    ConditionTag localConditionTag = getConditionTagAt(0);
    if (localConditionTag == null) {
      return;
    }
    if (DEBUG) {
      Log.d(this.mTag, "Selecting a default");
    }
    i = this.mPrefs.getMinuteIndex();
    if ((i != -1) && (this.mCountdownConditionSupported))
    {
      this.mTimeCondition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[i], ActivityManager.getCurrentUser());
      this.mBucketIndex = i;
      bind(this.mTimeCondition, this.mZenRadioGroupContent.getChildAt(1), 1);
      getConditionTagAt(1).rb.setChecked(true);
      return;
    }
    localConditionTag.rb.setChecked(true);
  }
  
  private void fireExpanded()
  {
    if (this.mCallback != null) {
      this.mCallback.onExpanded(this.mExpanded);
    }
  }
  
  private void fireInteraction()
  {
    if (this.mCallback != null) {
      this.mCallback.onInteraction();
    }
  }
  
  private Condition forever()
  {
    return new Condition(this.mForeverId, foreverSummary(this.mContext), "", "", 0, 1, 0);
  }
  
  private static String foreverSummary(Context paramContext)
  {
    return paramContext.getString(17040851);
  }
  
  private static Uri getConditionId(Condition paramCondition)
  {
    Uri localUri = null;
    if (paramCondition != null) {
      localUri = paramCondition.id;
    }
    return localUri;
  }
  
  private ConditionTag getConditionTagAt(int paramInt)
  {
    return (ConditionTag)this.mZenRadioGroupContent.getChildAt(paramInt).getTag();
  }
  
  private Uri getRealConditionId(Condition paramCondition)
  {
    if (isForever(paramCondition)) {
      return null;
    }
    return getConditionId(paramCondition);
  }
  
  private Condition getSelectedCondition()
  {
    int j = getVisibleConditions();
    int i = 0;
    while (i < j)
    {
      ConditionTag localConditionTag = getConditionTagAt(i);
      if ((localConditionTag != null) && (localConditionTag.rb.isChecked())) {
        return localConditionTag.condition;
      }
      i += 1;
    }
    return null;
  }
  
  private int getSelectedZen(int paramInt)
  {
    Object localObject = this.mZenButtons.getSelectedValue();
    if (localObject != null) {
      paramInt = ((Integer)localObject).intValue();
    }
    return paramInt;
  }
  
  private Condition getTimeUntilNextAlarmCondition()
  {
    GregorianCalendar localGregorianCalendar1 = new GregorianCalendar();
    long l1 = localGregorianCalendar1.getTimeInMillis();
    setToMidnight(localGregorianCalendar1);
    localGregorianCalendar1.add(5, 6);
    long l2 = this.mController.getNextAlarm();
    if (l2 > 0L)
    {
      GregorianCalendar localGregorianCalendar2 = new GregorianCalendar();
      localGregorianCalendar2.setTimeInMillis(l2);
      setToMidnight(localGregorianCalendar2);
      if (localGregorianCalendar1.compareTo(localGregorianCalendar2) >= 0) {
        return ZenModeConfig.toNextAlarmCondition(this.mContext, l1, l2, ActivityManager.getCurrentUser());
      }
    }
    return null;
  }
  
  private int getVisibleConditions()
  {
    int j = 0;
    int m = this.mZenRadioGroupContent.getChildCount();
    int i = 0;
    if (i < m)
    {
      if (this.mZenRadioGroupContent.getChildAt(i).getVisibility() == 0) {}
      for (int k = 1;; k = 0)
      {
        j += k;
        i += 1;
        break;
      }
    }
    return j;
  }
  
  private void handleExitConditionChanged(Condition paramCondition)
  {
    setExitCondition(paramCondition);
    if (DEBUG) {
      Log.d(this.mTag, "handleExitConditionChanged " + this.mExitCondition);
    }
    int j = getVisibleConditions();
    int i = 0;
    while (i < j)
    {
      ConditionTag localConditionTag = getConditionTagAt(i);
      if ((localConditionTag != null) && (sameConditionId(localConditionTag.condition, this.mExitCondition))) {
        bind(paramCondition, this.mZenRadioGroupContent.getChildAt(i), i);
      }
      i += 1;
    }
  }
  
  private void handleUpdateConditions()
  {
    int j = 0;
    if (this.mTransitionHelper.isTransitioning()) {
      return;
    }
    Object localObject;
    if (this.mConditions == null)
    {
      i = 0;
      if (DEBUG) {
        Log.d(this.mTag, "handleUpdateConditions conditionCount=" + i);
      }
      bind(forever(), this.mZenRadioGroupContent.getChildAt(0), 0);
      if ((this.mCountdownConditionSupported) && (this.mTimeCondition != null)) {
        bind(this.mTimeCondition, this.mZenRadioGroupContent.getChildAt(1), 1);
      }
      if (this.mCountdownConditionSupported)
      {
        localObject = getTimeUntilNextAlarmCondition();
        if (localObject == null) {
          break label205;
        }
        this.mZenRadioGroup.getChildAt(2).setVisibility(0);
        this.mZenRadioGroupContent.getChildAt(2).setVisibility(0);
        bind((Condition)localObject, this.mZenRadioGroupContent.getChildAt(2), 2);
      }
      label158:
      if ((this.mExpanded) && (isShown())) {
        ensureSelection();
      }
      localObject = this.mZenConditions;
      if (this.mSessionZen == 0) {
        break label234;
      }
    }
    label205:
    label234:
    for (int i = j;; i = 8)
    {
      ((LinearLayout)localObject).setVisibility(i);
      return;
      i = this.mConditions.length;
      break;
      this.mZenRadioGroup.getChildAt(2).setVisibility(8);
      this.mZenRadioGroupContent.getChildAt(2).setVisibility(8);
      break label158;
    }
  }
  
  private void handleUpdateManualRule(ZenModeConfig.ZenRule paramZenRule)
  {
    Condition localCondition = null;
    if (paramZenRule != null) {}
    for (int i = paramZenRule.zenMode;; i = 0)
    {
      handleUpdateZen(i);
      if (paramZenRule != null) {
        localCondition = paramZenRule.condition;
      }
      handleExitConditionChanged(localCondition);
      return;
    }
  }
  
  private void handleUpdateZen(int paramInt)
  {
    if ((this.mSessionZen != -1) && (this.mSessionZen != paramInt))
    {
      setExpanded(isShown());
      this.mSessionZen = paramInt;
    }
    this.mZenButtons.setSelectedValue(Integer.valueOf(paramInt), false);
    updateWidgets();
    handleUpdateConditions();
    if (this.mExpanded)
    {
      Condition localCondition = getSelectedCondition();
      if (!Objects.equals(this.mExitCondition, localCondition)) {
        select(localCondition);
      }
    }
  }
  
  private void hideAllConditions()
  {
    int j = this.mZenRadioGroupContent.getChildCount();
    int i = 0;
    while (i < j)
    {
      this.mZenRadioGroupContent.getChildAt(i).setVisibility(8);
      i += 1;
    }
  }
  
  private static boolean isCountdown(Condition paramCondition)
  {
    if (paramCondition != null) {
      return ZenModeConfig.isValidCountdownConditionId(paramCondition.id);
    }
    return false;
  }
  
  private boolean isForever(Condition paramCondition)
  {
    if (paramCondition != null) {
      return this.mForeverId.equals(paramCondition.id);
    }
    return false;
  }
  
  private void onClickTimeButton(View paramView, ConditionTag paramConditionTag, boolean paramBoolean, int paramInt)
  {
    MetricsLogger.action(this.mContext, 163, paramBoolean);
    Object localObject2 = null;
    int k = MINUTE_BUCKETS.length;
    if (this.mBucketIndex == -1)
    {
      long l1 = ZenModeConfig.tryParseCountdownConditionId(getConditionId(paramConditionTag.condition));
      long l2 = System.currentTimeMillis();
      i = 0;
      for (;;)
      {
        Object localObject1 = localObject2;
        int m;
        long l3;
        if (i < k)
        {
          if (!paramBoolean) {
            break label197;
          }
          j = i;
          m = MINUTE_BUCKETS[j];
          l3 = l2 + 60000 * m;
          if ((!paramBoolean) || (l3 <= l1)) {
            break label209;
          }
        }
        label197:
        label209:
        while ((!paramBoolean) && (l3 < l1))
        {
          this.mBucketIndex = j;
          localObject1 = ZenModeConfig.toTimeCondition(this.mContext, l3, m, ActivityManager.getCurrentUser(), false);
          localObject2 = localObject1;
          if (localObject1 == null)
          {
            this.mBucketIndex = DEFAULT_BUCKET_INDEX;
            localObject2 = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[this.mBucketIndex], ActivityManager.getCurrentUser());
          }
          this.mTimeCondition = ((Condition)localObject2);
          bind(this.mTimeCondition, paramView, paramInt);
          paramConditionTag.rb.setChecked(true);
          select(this.mTimeCondition);
          announceConditionSelection(paramConditionTag);
          return;
          j = k - 1 - i;
          break;
        }
        i += 1;
      }
    }
    int j = this.mBucketIndex;
    if (paramBoolean) {}
    for (int i = 1;; i = -1)
    {
      this.mBucketIndex = Math.max(0, Math.min(k - 1, i + j));
      localObject2 = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[this.mBucketIndex], ActivityManager.getCurrentUser());
      break;
    }
  }
  
  private static Condition parseExistingTimeCondition(Context paramContext, Condition paramCondition)
  {
    if (paramCondition == null) {
      return null;
    }
    long l1 = ZenModeConfig.tryParseCountdownConditionId(paramCondition.id);
    if (l1 == 0L) {
      return null;
    }
    long l2 = l1 - System.currentTimeMillis();
    if ((l2 <= 0L) || (l2 > MAX_BUCKET_MINUTES * 60000)) {
      return null;
    }
    return ZenModeConfig.toTimeCondition(paramContext, l1, Math.round((float)l2 / 60000.0F), ActivityManager.getCurrentUser(), false);
  }
  
  private static String prefKeyForConfirmation(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      return "DndConfirmedPriorityIntroduction";
    }
    return "DndConfirmedSilenceIntroduction";
  }
  
  private static boolean sameConditionId(Condition paramCondition1, Condition paramCondition2)
  {
    boolean bool = false;
    if (paramCondition1 == null) {
      if (paramCondition2 == null) {
        bool = true;
      }
    }
    while (paramCondition2 == null) {
      return bool;
    }
    return paramCondition1.id.equals(paramCondition2.id);
  }
  
  private void select(Condition paramCondition)
  {
    if (DEBUG) {
      Log.d(this.mTag, "select " + paramCondition);
    }
    if ((this.mSessionZen == -1) || (this.mSessionZen == 0))
    {
      if (DEBUG) {
        Log.d(this.mTag, "Ignoring condition selection outside of manual zen");
      }
      return;
    }
    final Uri localUri = getRealConditionId(paramCondition);
    if (this.mController != null) {
      AsyncTask.execute(new Runnable()
      {
        public void run()
        {
          ZenModePanel.-get5(ZenModePanel.this).setZen(ZenModePanel.-get9(ZenModePanel.this), localUri, "ZenModePanel.selectCondition");
        }
      });
    }
    setExitCondition(paramCondition);
    if (localUri == null) {
      this.mPrefs.setMinuteIndex(-1);
    }
    for (;;)
    {
      setSessionExitCondition(copy(paramCondition));
      return;
      if ((isCountdown(paramCondition)) && (this.mBucketIndex != -1)) {
        this.mPrefs.setMinuteIndex(this.mBucketIndex);
      }
    }
  }
  
  private void setExitCondition(Condition paramCondition)
  {
    if (Objects.equals(this.mExitCondition, paramCondition)) {
      return;
    }
    this.mExitCondition = paramCondition;
    if (DEBUG) {
      Log.d(this.mTag, "mExitCondition=" + getConditionId(this.mExitCondition));
    }
    updateWidgets();
  }
  
  private void setExpanded(boolean paramBoolean)
  {
    if (paramBoolean == this.mExpanded) {
      return;
    }
    if (DEBUG) {
      Log.d(this.mTag, "setExpanded " + paramBoolean);
    }
    this.mExpanded = paramBoolean;
    if ((this.mExpanded) && (isShown())) {
      ensureSelection();
    }
    updateWidgets();
    fireExpanded();
  }
  
  private void setRequestingConditions(boolean paramBoolean)
  {
    if (this.mRequestingConditions == paramBoolean) {
      return;
    }
    if (DEBUG) {
      Log.d(this.mTag, "setRequestingConditions " + paramBoolean);
    }
    this.mRequestingConditions = paramBoolean;
    if (this.mRequestingConditions)
    {
      this.mTimeCondition = parseExistingTimeCondition(this.mContext, this.mExitCondition);
      if (this.mTimeCondition != null) {
        this.mBucketIndex = -1;
      }
      for (;;)
      {
        if (DEBUG) {
          Log.d(this.mTag, "Initial bucket index: " + this.mBucketIndex);
        }
        this.mConditions = null;
        handleUpdateConditions();
        return;
        this.mBucketIndex = DEFAULT_BUCKET_INDEX;
        this.mTimeCondition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[this.mBucketIndex], ActivityManager.getCurrentUser());
      }
    }
    hideAllConditions();
  }
  
  private void setSessionExitCondition(Condition paramCondition)
  {
    if (Objects.equals(paramCondition, this.mSessionExitCondition)) {
      return;
    }
    if (DEBUG) {
      Log.d(this.mTag, "mSessionExitCondition=" + getConditionId(paramCondition));
    }
    this.mSessionExitCondition = paramCondition;
  }
  
  private void setToMidnight(Calendar paramCalendar)
  {
    paramCalendar.set(11, 0);
    paramCalendar.set(12, 0);
    paramCalendar.set(13, 0);
    paramCalendar.set(14, 0);
  }
  
  private void updateWidgets()
  {
    int m = 0;
    if (this.mTransitionHelper.isTransitioning())
    {
      this.mTransitionHelper.pendingUpdateWidgets();
      return;
    }
    int i = getSelectedZen(0);
    int j;
    boolean bool;
    label42:
    label73:
    Object localObject;
    int k;
    label89:
    label107:
    label131:
    label149:
    TextView localTextView;
    if (i == 1)
    {
      j = 1;
      if (i != 2) {
        break label198;
      }
      bool = true;
      if ((j != 0) && (!ZenPrefs.-get0(this.mPrefs))) {
        break label204;
      }
      if ((bool) && (!ZenPrefs.-get1(this.mPrefs))) {
        break label209;
      }
      i = 0;
      localObject = this.mZenButtons;
      if (!this.mHidden) {
        break label214;
      }
      k = 8;
      ((SegmentedButtons)localObject).setVisibility(k);
      localObject = this.mZenIntroduction;
      if (i == 0) {
        break label219;
      }
      k = 0;
      ((View)localObject).setVisibility(k);
      if (i != 0)
      {
        localObject = this.mZenIntroductionMessage;
        if (j == 0) {
          break label225;
        }
        i = 2131690346;
        ((TextView)localObject).setText(i);
        localObject = this.mZenIntroductionCustomize;
        if (j == 0) {
          break label246;
        }
        i = 0;
        ((TextView)localObject).setVisibility(i);
      }
      localObject = computeAlarmWarningText(bool);
      localTextView = this.mZenAlarmWarning;
      if (localObject == null) {
        break label252;
      }
    }
    label198:
    label204:
    label209:
    label214:
    label219:
    label225:
    label246:
    label252:
    for (i = m;; i = 8)
    {
      localTextView.setVisibility(i);
      this.mZenAlarmWarning.setText((CharSequence)localObject);
      return;
      j = 0;
      break;
      bool = false;
      break label42;
      i = 1;
      break label73;
      i = 1;
      break label73;
      k = 0;
      break label89;
      k = 8;
      break label107;
      if (this.mVoiceCapable)
      {
        i = 2131690348;
        break label131;
      }
      i = 2131690349;
      break label131;
      i = 8;
      break label149;
    }
  }
  
  protected void addZenConditions(int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      View localView = this.mInflater.inflate(2130968847, this, false);
      localView.setId(i);
      this.mZenRadioGroup.addView(localView);
      localView = this.mInflater.inflate(2130968848, this, false);
      localView.setId(i + paramInt);
      this.mZenRadioGroupContent.addView(localView);
      i += 1;
    }
  }
  
  protected void createZenButtons()
  {
    this.mZenButtons = ((SegmentedButtons)findViewById(2131952354));
    this.mZenButtons.addButton(2131690361, 2131690357, Integer.valueOf(2));
    this.mZenButtons.addButton(2131690363, 2131690360, Integer.valueOf(3));
    this.mZenButtons.addButton(2131690362, 2131690359, Integer.valueOf(1));
    this.mZenButtons.setCallback(this.mZenButtonsCallback);
  }
  
  public void init(ZenModeController paramZenModeController)
  {
    this.mController = paramZenModeController;
    this.mCountdownConditionSupported = this.mController.isCountdownConditionSupported();
    if (this.mCountdownConditionSupported) {}
    for (int i = 2;; i = 0)
    {
      addZenConditions(i + 1);
      this.mSessionZen = getSelectedZen(-1);
      handleUpdateManualRule(this.mController.getManualRule());
      if (DEBUG) {
        Log.d(this.mTag, "init mExitCondition=" + this.mExitCondition);
      }
      hideAllConditions();
      return;
    }
  }
  
  protected void onAttachedToWindow()
  {
    boolean bool = true;
    super.onAttachedToWindow();
    if (DEBUG) {
      Log.d(this.mTag, "onAttachedToWindow");
    }
    this.mAttached = true;
    this.mAttachedZen = getSelectedZen(-1);
    this.mSessionZen = this.mAttachedZen;
    this.mTransitionHelper.clear();
    this.mController.addCallback(this.mZenCallback);
    setSessionExitCondition(copy(this.mExitCondition));
    updateWidgets();
    if (this.mHidden) {
      bool = false;
    }
    setRequestingConditions(bool);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.mZenButtons != null) {
      this.mZenButtons.updateLocale();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (DEBUG) {
      Log.d(this.mTag, "onDetachedFromWindow");
    }
    checkForAttachedZenChange();
    this.mAttached = false;
    this.mAttachedZen = -1;
    this.mSessionZen = -1;
    this.mController.removeCallback(this.mZenCallback);
    setSessionExitCondition(null);
    setRequestingConditions(false);
    this.mTransitionHelper.clear();
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    createZenButtons();
    this.mZenIntroduction = findViewById(2131952355);
    this.mZenIntroductionMessage = ((TextView)findViewById(2131952357));
    this.mSpTexts.add(this.mZenIntroductionMessage);
    this.mZenIntroductionConfirm = findViewById(2131952356);
    this.mZenIntroductionConfirm.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ZenModePanel.-wrap2(ZenModePanel.this);
      }
    });
    this.mZenIntroductionCustomize = ((TextView)findViewById(2131952358));
    this.mZenIntroductionCustomize.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ZenModePanel.-wrap2(ZenModePanel.this);
        if (ZenModePanel.-get3(ZenModePanel.this) != null) {
          ZenModePanel.-get3(ZenModePanel.this).onPrioritySettings();
        }
      }
    });
    this.mSpTexts.add(this.mZenIntroductionCustomize);
    this.mZenConditions = ((LinearLayout)findViewById(2131952359));
    this.mZenAlarmWarning = ((TextView)findViewById(2131952362));
    this.mZenRadioGroup = ((RadioGroup)findViewById(2131952360));
    this.mZenRadioGroupContent = ((LinearLayout)findViewById(2131952361));
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public static abstract interface Callback
  {
    public abstract void onExpanded(boolean paramBoolean);
    
    public abstract void onInteraction();
    
    public abstract void onPrioritySettings();
  }
  
  private static class ConditionTag
  {
    Condition condition;
    TextView line1;
    TextView line2;
    View lines;
    RadioButton rb;
  }
  
  private final class H
    extends Handler
  {
    private H()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 2: 
        ZenModePanel.-wrap4(ZenModePanel.this, (ZenModeConfig.ZenRule)paramMessage.obj);
        return;
      }
      ZenModePanel.-wrap7(ZenModePanel.this);
    }
  }
  
  private final class TransitionHelper
    implements LayoutTransition.TransitionListener, Runnable
  {
    private boolean mPendingUpdateWidgets;
    private boolean mTransitioning;
    private final ArraySet<View> mTransitioningViews = new ArraySet();
    
    private TransitionHelper() {}
    
    private void updateTransitioning()
    {
      boolean bool = isTransitioning();
      if (this.mTransitioning == bool) {
        return;
      }
      this.mTransitioning = bool;
      if (ZenModePanel.-get0()) {
        Log.d(ZenModePanel.-get10(ZenModePanel.this), "TransitionHelper mTransitioning=" + this.mTransitioning);
      }
      if (!this.mTransitioning)
      {
        if (this.mPendingUpdateWidgets) {
          ZenModePanel.-get7(ZenModePanel.this).post(this);
        }
      }
      else {
        return;
      }
      this.mPendingUpdateWidgets = false;
    }
    
    public void clear()
    {
      this.mTransitioningViews.clear();
      this.mPendingUpdateWidgets = false;
    }
    
    public void endTransition(LayoutTransition paramLayoutTransition, ViewGroup paramViewGroup, View paramView, int paramInt)
    {
      this.mTransitioningViews.remove(paramView);
      updateTransitioning();
    }
    
    public boolean isTransitioning()
    {
      return !this.mTransitioningViews.isEmpty();
    }
    
    public void pendingUpdateWidgets()
    {
      this.mPendingUpdateWidgets = true;
    }
    
    public void run()
    {
      if (ZenModePanel.-get0()) {
        Log.d(ZenModePanel.-get10(ZenModePanel.this), "TransitionHelper run mPendingUpdateWidgets=" + this.mPendingUpdateWidgets);
      }
      if (this.mPendingUpdateWidgets) {
        ZenModePanel.-wrap7(ZenModePanel.this);
      }
      this.mPendingUpdateWidgets = false;
    }
    
    public void startTransition(LayoutTransition paramLayoutTransition, ViewGroup paramViewGroup, View paramView, int paramInt)
    {
      this.mTransitioningViews.add(paramView);
      updateTransitioning();
    }
  }
  
  private final class ZenPrefs
    implements SharedPreferences.OnSharedPreferenceChangeListener
  {
    private boolean mConfirmedPriorityIntroduction;
    private boolean mConfirmedSilenceIntroduction;
    private int mMinuteIndex;
    private final int mNoneDangerousThreshold = ZenModePanel.-get4(ZenModePanel.this).getResources().getInteger(2131624015);
    private int mNoneSelected;
    
    private ZenPrefs()
    {
      Prefs.registerListener(ZenModePanel.-get4(ZenModePanel.this), this);
      updateMinuteIndex();
      updateNoneSelected();
      updateConfirmedPriorityIntroduction();
      updateConfirmedSilenceIntroduction();
    }
    
    private int clampIndex(int paramInt)
    {
      return MathUtils.constrain(paramInt, -1, ZenModePanel.-get2().length - 1);
    }
    
    private int clampNoneSelected(int paramInt)
    {
      return MathUtils.constrain(paramInt, 0, Integer.MAX_VALUE);
    }
    
    private void updateConfirmedPriorityIntroduction()
    {
      boolean bool = Prefs.getBoolean(ZenModePanel.-get4(ZenModePanel.this), "DndConfirmedPriorityIntroduction", false);
      if (bool == this.mConfirmedPriorityIntroduction) {
        return;
      }
      this.mConfirmedPriorityIntroduction = bool;
      if (ZenModePanel.-get0()) {
        Log.d(ZenModePanel.-get10(ZenModePanel.this), "Confirmed priority introduction: " + this.mConfirmedPriorityIntroduction);
      }
    }
    
    private void updateConfirmedSilenceIntroduction()
    {
      boolean bool = Prefs.getBoolean(ZenModePanel.-get4(ZenModePanel.this), "DndConfirmedSilenceIntroduction", false);
      if (bool == this.mConfirmedSilenceIntroduction) {
        return;
      }
      this.mConfirmedSilenceIntroduction = bool;
      if (ZenModePanel.-get0()) {
        Log.d(ZenModePanel.-get10(ZenModePanel.this), "Confirmed silence introduction: " + this.mConfirmedSilenceIntroduction);
      }
    }
    
    private void updateMinuteIndex()
    {
      this.mMinuteIndex = clampIndex(Prefs.getInt(ZenModePanel.-get4(ZenModePanel.this), "DndCountdownMinuteIndex", ZenModePanel.-get1()));
      if (ZenModePanel.-get0()) {
        Log.d(ZenModePanel.-get10(ZenModePanel.this), "Favorite minute index: " + this.mMinuteIndex);
      }
    }
    
    private void updateNoneSelected()
    {
      this.mNoneSelected = clampNoneSelected(Prefs.getInt(ZenModePanel.-get4(ZenModePanel.this), "DndNoneSelected", 0));
      if (ZenModePanel.-get0()) {
        Log.d(ZenModePanel.-get10(ZenModePanel.this), "None selected: " + this.mNoneSelected);
      }
    }
    
    public int getMinuteIndex()
    {
      return this.mMinuteIndex;
    }
    
    public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString)
    {
      updateMinuteIndex();
      updateNoneSelected();
      updateConfirmedPriorityIntroduction();
      updateConfirmedSilenceIntroduction();
    }
    
    public void setMinuteIndex(int paramInt)
    {
      paramInt = clampIndex(paramInt);
      if (paramInt == this.mMinuteIndex) {
        return;
      }
      this.mMinuteIndex = clampIndex(paramInt);
      if (ZenModePanel.-get0()) {
        Log.d(ZenModePanel.-get10(ZenModePanel.this), "Setting favorite minute index: " + this.mMinuteIndex);
      }
      Prefs.putInt(ZenModePanel.-get4(ZenModePanel.this), "DndCountdownMinuteIndex", this.mMinuteIndex);
    }
    
    public void trackNoneSelected()
    {
      this.mNoneSelected = clampNoneSelected(this.mNoneSelected + 1);
      if (ZenModePanel.-get0()) {
        Log.d(ZenModePanel.-get10(ZenModePanel.this), "Setting none selected: " + this.mNoneSelected + " threshold=" + this.mNoneDangerousThreshold);
      }
      Prefs.putInt(ZenModePanel.-get4(ZenModePanel.this), "DndNoneSelected", this.mNoneSelected);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\ZenModePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */