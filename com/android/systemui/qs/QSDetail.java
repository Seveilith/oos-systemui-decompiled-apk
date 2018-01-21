package com.android.systemui.qs;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.statusbar.phone.BaseStatusBarHeader;
import com.android.systemui.statusbar.phone.QSTileHost;
import com.android.systemui.util.ThemeColorUtils;

public class QSDetail
  extends LinearLayout
{
  private boolean mAnimatingOpen;
  private QSDetailClipper mClipper;
  private boolean mClosingDetail;
  private QSTile.DetailAdapter mDetailAdapter;
  private ViewGroup mDetailContent;
  private TextView mDetailDoneButton;
  private TextView mDetailSettingsButton;
  private final SparseArray<View> mDetailViews = new SparseArray();
  private boolean mFullyExpanded;
  private BaseStatusBarHeader mHeader;
  private final AnimatorListenerAdapter mHideGridContentWhenDone = new AnimatorListenerAdapter()
  {
    public void onAnimationCancel(Animator paramAnonymousAnimator)
    {
      paramAnonymousAnimator.removeListener(this);
      QSDetail.-set0(QSDetail.this, false);
      QSDetail.-wrap0(QSDetail.this);
    }
    
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      if (QSDetail.-get1(QSDetail.this) != null)
      {
        QSDetail.-get6(QSDetail.this).setGridContentVisibility(false);
        QSDetail.-get3(QSDetail.this).setVisibility(4);
      }
      QSDetail.-set0(QSDetail.this, false);
      QSDetail.-wrap0(QSDetail.this);
    }
  };
  private QSTileHost mHost;
  private int mOpenX;
  private int mOpenY;
  private View mQsDetailHeader;
  private ImageView mQsDetailHeaderBack;
  private ImageView mQsDetailHeaderProgress;
  private Switch mQsDetailHeaderSwitch;
  private TextView mQsDetailHeaderTitle;
  private QSPanel mQsPanel;
  private final QSPanel.Callback mQsPanelCallback = new QSPanel.Callback()
  {
    public void onScanStateChanged(final boolean paramAnonymousBoolean)
    {
      QSDetail.this.post(new Runnable()
      {
        public void run()
        {
          QSDetail.-wrap1(QSDetail.this, paramAnonymousBoolean);
        }
      });
    }
    
    public void onShowingDetail(final QSTile.DetailAdapter paramAnonymousDetailAdapter, final int paramAnonymousInt1, final int paramAnonymousInt2)
    {
      QSDetail.this.post(new Runnable()
      {
        public void run()
        {
          QSDetail.-wrap2(QSDetail.this, paramAnonymousDetailAdapter, paramAnonymousInt1, paramAnonymousInt2);
        }
      });
    }
    
    public void onToggleStateChanged(final boolean paramAnonymousBoolean)
    {
      QSDetail.this.post(new Runnable()
      {
        public void run()
        {
          QSDetail localQSDetail = QSDetail.this;
          boolean bool2 = paramAnonymousBoolean;
          if (QSDetail.-get1(QSDetail.this) != null) {}
          for (boolean bool1 = QSDetail.-get1(QSDetail.this).getToggleEnabled();; bool1 = false)
          {
            QSDetail.-wrap3(localQSDetail, bool2, bool1);
            return;
          }
        }
      });
    }
  };
  private boolean mScanState;
  private boolean mSwitchState;
  private final AnimatorListenerAdapter mTeardownDetailWhenDone = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      QSDetail.-get2(QSDetail.this).removeAllViews();
      QSDetail.this.setVisibility(4);
      QSDetail.-set1(QSDetail.this, false);
    }
  };
  private boolean mTriggeredExpand;
  
  public QSDetail(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void checkPendingAnimations()
  {
    boolean bool2 = this.mSwitchState;
    if (this.mDetailAdapter != null) {}
    for (boolean bool1 = this.mDetailAdapter.getToggleEnabled();; bool1 = false)
    {
      handleToggleStateChanged(bool2, bool1);
      return;
    }
  }
  
  private void handleScanStateChanged(boolean paramBoolean)
  {
    if (this.mScanState == paramBoolean) {
      return;
    }
    this.mScanState = paramBoolean;
    Animatable localAnimatable = (Animatable)this.mQsDetailHeaderProgress.getDrawable();
    if (paramBoolean)
    {
      this.mQsDetailHeaderProgress.animate().alpha(1.0F);
      localAnimatable.start();
      return;
    }
    this.mQsDetailHeaderProgress.animate().alpha(0.0F);
    localAnimatable.stop();
  }
  
  private void handleShowingDetail(final QSTile.DetailAdapter paramDetailAdapter, int paramInt1, int paramInt2)
  {
    Object localObject;
    label86:
    label105:
    label115:
    label125:
    int j;
    if (paramDetailAdapter != null)
    {
      bool = true;
      setClickable(bool);
      Log.d("QSDetail", "handleShowingDetail: " + bool);
      if (!bool) {
        break label220;
      }
      this.mQsDetailHeaderTitle.setText(paramDetailAdapter.getTitle());
      localObject = paramDetailAdapter.getToggleState();
      if (localObject != null) {
        break label162;
      }
      this.mQsDetailHeaderSwitch.setVisibility(4);
      this.mQsDetailHeader.setClickable(false);
      if (this.mFullyExpanded) {
        break label212;
      }
      this.mTriggeredExpand = true;
      this.mHost.animateToggleQSExpansion();
      this.mOpenX = paramInt1;
      this.mOpenY = paramInt2;
      if (this.mDetailAdapter == null) {
        break label266;
      }
      i = 1;
      if (paramDetailAdapter == null) {
        break label272;
      }
      j = 1;
      label132:
      if (i == j) {
        break label278;
      }
    }
    label162:
    label212:
    label220:
    label266:
    label272:
    label278:
    for (int i = 1;; i = 0)
    {
      if ((i != 0) || (this.mDetailAdapter != paramDetailAdapter)) {
        break label284;
      }
      return;
      bool = false;
      break;
      this.mQsDetailHeaderSwitch.setVisibility(0);
      handleToggleStateChanged(((Boolean)localObject).booleanValue(), paramDetailAdapter.getToggleEnabled());
      this.mQsDetailHeader.setClickable(true);
      this.mQsDetailHeader.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (QSDetail.-get5(QSDetail.this).isChecked()) {}
          for (boolean bool = false;; bool = true)
          {
            QSDetail.-get5(QSDetail.this).setChecked(bool);
            paramDetailAdapter.setToggleState(bool);
            return;
          }
        }
      });
      break label86;
      this.mTriggeredExpand = false;
      break label105;
      i = this.mOpenX;
      j = this.mOpenY;
      paramInt1 = i;
      paramInt2 = j;
      if (!this.mTriggeredExpand) {
        break label115;
      }
      this.mHost.animateToggleQSExpansion();
      this.mTriggeredExpand = false;
      paramInt1 = i;
      paramInt2 = j;
      break label115;
      i = 0;
      break label125;
      j = 0;
      break label132;
    }
    label284:
    if (paramDetailAdapter != null)
    {
      int k = paramDetailAdapter.getMetricsCategory();
      localObject = paramDetailAdapter.createDetailView(this.mContext, (View)this.mDetailViews.get(k), this.mDetailContent);
      if (localObject == null) {
        throw new IllegalStateException("Must return detail view");
      }
      final Intent localIntent = paramDetailAdapter.getSettingsIntent();
      TextView localTextView = this.mDetailSettingsButton;
      if (localIntent != null)
      {
        j = 0;
        localTextView.setVisibility(j);
        this.mDetailSettingsButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            QSDetail.-get4(QSDetail.this).startActivityDismissingKeyguard(localIntent);
          }
        });
        this.mDetailContent.removeAllViews();
        this.mDetailContent.addView((View)localObject);
        this.mDetailViews.put(k, localObject);
        MetricsLogger.visible(this.mContext, paramDetailAdapter.getMetricsCategory());
        announceForAccessibility(this.mContext.getString(2131690475, new Object[] { paramDetailAdapter.getTitle() }));
        this.mDetailAdapter = paramDetailAdapter;
        localObject = this.mHideGridContentWhenDone;
        setVisibility(0);
        label469:
        sendAccessibilityEvent(32);
        if (i != 0)
        {
          if (paramDetailAdapter == null) {
            break label614;
          }
          bool = true;
          label487:
          this.mAnimatingOpen = bool;
          if ((!this.mFullyExpanded) && (this.mDetailAdapter == null)) {
            break label626;
          }
          setAlpha(1.0F);
          paramDetailAdapter = this.mClipper;
          if (this.mDetailAdapter == null) {
            break label620;
          }
        }
      }
    }
    label614:
    label620:
    for (boolean bool = true;; bool = false)
    {
      paramDetailAdapter.animateCircularClip(paramInt1, paramInt2, bool, (Animator.AnimatorListener)localObject, 0);
      return;
      j = 8;
      break;
      if (this.mDetailAdapter != null) {
        MetricsLogger.hidden(this.mContext, this.mDetailAdapter.getMetricsCategory());
      }
      this.mClosingDetail = true;
      this.mDetailAdapter = null;
      localObject = this.mTeardownDetailWhenDone;
      this.mHeader.setVisibility(0);
      this.mQsPanel.setGridContentVisibility(true);
      this.mQsPanelCallback.onScanStateChanged(false);
      break label469;
      bool = false;
      break label487;
    }
    label626:
    animate().alpha(0.0F).setDuration(300L).setListener((Animator.AnimatorListener)localObject).start();
  }
  
  private void handleToggleStateChanged(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mSwitchState = paramBoolean1;
    if (this.mAnimatingOpen) {
      return;
    }
    this.mQsDetailHeaderSwitch.setChecked(paramBoolean1);
    this.mQsDetailHeader.setEnabled(paramBoolean2);
    this.mQsDetailHeaderSwitch.setEnabled(paramBoolean2);
  }
  
  private void updateDetailText()
  {
    this.mDetailDoneButton.setText(2131690306);
    this.mDetailSettingsButton.setText(2131690305);
  }
  
  public boolean isClosingDetail()
  {
    return this.mClosingDetail;
  }
  
  public boolean isShowingDetail()
  {
    return this.mDetailAdapter != null;
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    FontSizeUtils.updateFontSize(this.mDetailDoneButton, 2131755436);
    FontSizeUtils.updateFontSize(this.mDetailSettingsButton, 2131755436);
    int i = 0;
    while (i < this.mDetailViews.size())
    {
      ((View)this.mDetailViews.valueAt(i)).dispatchConfigurationChanged(paramConfiguration);
      i += 1;
    }
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mDetailContent = ((ViewGroup)findViewById(16908290));
    this.mDetailSettingsButton = ((TextView)findViewById(16908314));
    this.mDetailDoneButton = ((TextView)findViewById(16908313));
    this.mQsDetailHeader = findViewById(2131952152);
    this.mQsDetailHeaderBack = ((ImageView)this.mQsDetailHeader.findViewById(16908363));
    this.mQsDetailHeaderTitle = ((TextView)this.mQsDetailHeader.findViewById(16908310));
    this.mQsDetailHeaderSwitch = ((Switch)this.mQsDetailHeader.findViewById(16908311));
    this.mQsDetailHeaderProgress = ((ImageView)findViewById(2131952153));
    updateDetailText();
    this.mClipper = new QSDetailClipper(this, 1356);
    this.mClipper.setInterpolator(new DecelerateInterpolator(1.0F));
    View.OnClickListener local4 = new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        QSDetail.this.announceForAccessibility(QSDetail.-get0(QSDetail.this).getString(2131690186));
        QSDetail.-get6(QSDetail.this).closeDetail();
      }
    };
    this.mQsDetailHeaderBack.setOnClickListener(local4);
    this.mDetailDoneButton.setOnClickListener(local4);
    updateThemeColor();
  }
  
  public void setExpanded(boolean paramBoolean)
  {
    if (!paramBoolean) {
      this.mTriggeredExpand = false;
    }
  }
  
  public void setFullyExpanded(boolean paramBoolean)
  {
    this.mFullyExpanded = paramBoolean;
  }
  
  public void setHost(QSTileHost paramQSTileHost)
  {
    this.mHost = paramQSTileHost;
  }
  
  public void setQsPanel(QSPanel paramQSPanel, BaseStatusBarHeader paramBaseStatusBarHeader)
  {
    this.mQsPanel = paramQSPanel;
    this.mHeader = paramBaseStatusBarHeader;
    this.mHeader.setCallback(this.mQsPanelCallback);
    this.mQsPanel.setCallback(this.mQsPanelCallback);
  }
  
  protected void updateThemeColor()
  {
    int i = ThemeColorUtils.getColor(ThemeColorUtils.QS_SYSTEM_PRIMARY);
    int j = ThemeColorUtils.getColor(ThemeColorUtils.QS_PRIMARY_TEXT);
    ThemeColorUtils.getColor(ThemeColorUtils.QS_PRIMARY_TEXT);
    int k = ThemeColorUtils.getColor(ThemeColorUtils.PROGRESS);
    ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_ICON_DISABLE);
    int m = ThemeColorUtils.getColor(ThemeColorUtils.QS_SWITCH_THUMB_DISABLED);
    int n = ThemeColorUtils.getColor(ThemeColorUtils.QS_SWITCH_TRACK_DISABLED);
    int i1 = ThemeColorUtils.getColor(ThemeColorUtils.QS_SWITCH_THUMB_OFF);
    int i2 = ThemeColorUtils.getColor(ThemeColorUtils.QS_SWITCH_TRACK_OFF);
    int i3 = ThemeColorUtils.getColor(ThemeColorUtils.QS_ICON_ACTIVE);
    Object localObject = new int[0];
    localObject = new ColorStateList(new int[][] { { -16842910 }, { 16842912 }, localObject }, new int[] { m, k, i1 });
    ColorStateList localColorStateList = new ColorStateList(new int[][] { { -16842910 }, { 16842912 }, new int[0] }, new int[] { n, k, i2 });
    setBackgroundTintList(ColorStateList.valueOf(i));
    this.mDetailSettingsButton.setTextColor(k);
    this.mDetailDoneButton.setTextColor(k);
    this.mQsDetailHeaderBack.setImageTintList(ColorStateList.valueOf(i3));
    this.mQsDetailHeaderSwitch.setThumbTintList((ColorStateList)localObject);
    this.mQsDetailHeaderSwitch.setTrackTintList(localColorStateList);
    this.mQsDetailHeaderTitle.setTextColor(j);
    this.mQsDetailHeaderProgress.setColorFilter(k);
    this.mQsDetailHeaderProgress.setBackgroundColor(k & 0x80FFFFFF);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QSDetail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */