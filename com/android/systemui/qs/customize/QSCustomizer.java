package com.android.systemui.qs.customize;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.Toolbar.OnMenuItemClickListener;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.qs.QSContainer;
import com.android.systemui.qs.QSDetailClipper;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.SystemSetting;
import com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.phone.QSTileHost;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.KeyguardMonitor.Callback;
import com.android.systemui.util.ThemeColorUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QSCustomizer
  extends LinearLayout
  implements Toolbar.OnMenuItemClickListener
{
  private boolean isShown;
  private ImageView mBackButton;
  private final QSDetailClipper mClipper = new QSDetailClipper(this);
  private final Animator.AnimatorListener mCollapseAnimationListener = new AnimatorListenerAdapter()
  {
    public void onAnimationCancel(Animator paramAnonymousAnimator)
    {
      if (!QSCustomizer.-get0(QSCustomizer.this)) {
        QSCustomizer.this.setVisibility(8);
      }
      QSCustomizer.-wrap0(QSCustomizer.this, false);
      QSCustomizer.-get3(QSCustomizer.this).setCustomizerAnimating(false);
    }
    
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      if (!QSCustomizer.-get0(QSCustomizer.this)) {
        QSCustomizer.this.setVisibility(8);
      }
      QSCustomizer.-wrap0(QSCustomizer.this, false);
      QSCustomizer.-get3(QSCustomizer.this).setCustomizerAnimating(false);
      QSCustomizer.-get5(QSCustomizer.this).setAdapter(QSCustomizer.-get6(QSCustomizer.this));
      QSCustomizer.-get4(QSCustomizer.this).setShadow(true);
    }
  };
  private boolean mCustomizing;
  private final Animator.AnimatorListener mExpandAnimationListener = new AnimatorListenerAdapter()
  {
    public void onAnimationCancel(Animator paramAnonymousAnimator)
    {
      QSCustomizer.-get3(QSCustomizer.this).setCustomizerAnimating(false);
    }
    
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      if (QSCustomizer.-get0(QSCustomizer.this)) {
        QSCustomizer.-wrap0(QSCustomizer.this, true);
      }
      QSCustomizer.-get3(QSCustomizer.this).setCustomizerAnimating(false);
    }
    
    public void onAnimationStart(Animator paramAnonymousAnimator)
    {
      if (QSCustomizer.-get0(QSCustomizer.this)) {
        QSCustomizer.this.setVisibility(0);
      }
    }
  };
  private boolean mHasNavBar;
  private QSTileHost mHost;
  private final KeyguardMonitor.Callback mKeyguardCallback = new KeyguardMonitor.Callback()
  {
    public void onKeyguardChanged()
    {
      if (QSCustomizer.-get2(QSCustomizer.this).getKeyguardMonitor().isShowing()) {
        QSCustomizer.this.hide(0, 0);
      }
    }
  };
  private final SystemSetting mNavBarSetting;
  private NotificationsQuickSettingsContainer mNotifQsContainer;
  private PhoneStatusBar mPhoneStatusBar;
  private TextView mQSTitle;
  private QSContainer mQsContainer;
  private RecyclerView mRecyclerView;
  private TileAdapter mTileAdapter;
  private Toolbar mToolbar;
  private View mToolbarBackground;
  
  public QSCustomizer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(new ContextThemeWrapper(paramContext, 2131821109), paramAttributeSet);
    LayoutInflater.from(getContext()).inflate(2130968769, this);
    this.mToolbar = ((Toolbar)findViewById(16909309));
    paramContext = new TypedValue();
    this.mContext.getTheme().resolveAttribute(16843531, paramContext, true);
    this.mBackButton = ((ImageView)findViewById(2131951793));
    this.mBackButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        QSCustomizer.this.hide((int)paramAnonymousView.getX() + paramAnonymousView.getWidth() / 2, (int)paramAnonymousView.getY() + paramAnonymousView.getHeight() / 2);
      }
    });
    this.mQSTitle = ((TextView)findViewById(2131952150));
    this.mQSTitle.setText(this.mContext.getString(2131689984));
    FontSizeUtils.updateFontSize(this.mQSTitle, 2131755717);
    this.mToolbarBackground = findViewById(2131952149);
    this.mToolbar.setOnMenuItemClickListener(this);
    this.mRecyclerView = ((RecyclerView)findViewById(16908298));
    this.mTileAdapter = new TileAdapter(getContext());
    this.mRecyclerView.setAdapter(this.mTileAdapter);
    int i = this.mContext.getResources().getDimensionPixelSize(2131755712);
    this.mRecyclerView.setPadding(i, i, i, 0);
    this.mTileAdapter.getItemTouchHelper().attachToRecyclerView(this.mRecyclerView);
    paramContext = new GridLayoutManager(getContext(), 3);
    paramContext.setSpanSizeLookup(this.mTileAdapter.getSizeLookup());
    this.mRecyclerView.setLayoutManager(paramContext);
    this.mRecyclerView.addItemDecoration(this.mTileAdapter.getItemDecoration());
    paramContext = new DefaultItemAnimator();
    paramContext.setMoveDuration(150L);
    this.mRecyclerView.setItemAnimator(paramContext);
    this.mNavBarSetting = new SystemSetting(this.mContext, null, "buttons_show_on_screen_navkeys")
    {
      protected void handleValueChanged(int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        paramAnonymousBoolean = true;
        QSCustomizer localQSCustomizer = QSCustomizer.this;
        if (paramAnonymousInt == 1) {}
        for (;;)
        {
          QSCustomizer.-set0(localQSCustomizer, paramAnonymousBoolean);
          QSCustomizer.-wrap1(QSCustomizer.this, QSCustomizer.-get1(QSCustomizer.this));
          return;
          paramAnonymousBoolean = false;
        }
      }
    };
    this.mNavBarSetting.setListening(true);
    if (this.mNavBarSetting.getValue(0) == 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mHasNavBar = bool;
      updateNavBackdrop(this.mHasNavBar);
      return;
    }
  }
  
  private void reset()
  {
    ArrayList localArrayList = new ArrayList();
    String[] arrayOfString = this.mContext.getString(2131689907).split(",");
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      localArrayList.add(arrayOfString[i]);
      i += 1;
    }
    this.mTileAdapter.setTileSpecs(localArrayList);
  }
  
  private void save()
  {
    this.mTileAdapter.saveSpecs(this.mHost);
  }
  
  private void setCustomizing(boolean paramBoolean)
  {
    this.mCustomizing = paramBoolean;
    this.mQsContainer.notifyCustomizeChanged();
  }
  
  private void setTileSpecs()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mHost.getTiles().iterator();
    while (localIterator.hasNext()) {
      localArrayList.add(((QSTile)localIterator.next()).getTileSpec());
    }
    this.mTileAdapter.setTileSpecs(localArrayList);
    this.mRecyclerView.setAdapter(this.mTileAdapter);
  }
  
  private void updateNavBackdrop(boolean paramBoolean)
  {
    View localView = findViewById(2131952151);
    if (localView != null) {
      if (!paramBoolean) {
        break label24;
      }
    }
    label24:
    for (int i = 0;; i = 8)
    {
      localView.setVisibility(i);
      return;
    }
  }
  
  public void hide(int paramInt1, int paramInt2)
  {
    if (this.isShown)
    {
      MetricsLogger.hidden(getContext(), 358);
      this.isShown = false;
      this.mToolbar.dismissPopupMenus();
      setCustomizing(false);
      save();
      this.mClipper.animateCircularClip(paramInt1, paramInt2, false, this.mCollapseAnimationListener, 0);
      this.mNotifQsContainer.setCustomizerAnimating(true);
      this.mNotifQsContainer.setCustomizerShowing(false);
      announceForAccessibility(this.mContext.getString(2131690186));
      this.mHost.getKeyguardMonitor().removeCallback(this.mKeyguardCallback);
    }
  }
  
  public void hideNoAnimation()
  {
    if (this.isShown)
    {
      MetricsLogger.hidden(getContext(), 358);
      this.isShown = false;
      this.mToolbar.dismissPopupMenus();
      setCustomizing(false);
      save();
      setVisibility(8);
      this.mHost.getKeyguardMonitor().removeCallback(this.mKeyguardCallback);
      this.mRecyclerView.setAdapter(this.mTileAdapter);
    }
  }
  
  public boolean isCustomizing()
  {
    return this.mCustomizing;
  }
  
  public boolean isShown()
  {
    return this.isShown;
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    boolean bool2 = false;
    super.onConfigurationChanged(paramConfiguration);
    boolean bool1 = bool2;
    if (this.mHasNavBar)
    {
      bool1 = bool2;
      if (paramConfiguration.orientation != 2) {
        bool1 = true;
      }
    }
    updateNavBackdrop(bool1);
    FontSizeUtils.updateFontSize(this.mQSTitle, 2131755717);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mNavBarSetting.setListening(false);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    updateThemeColor();
    this.mToolbar.getMenu().add(0, 1, 0, this.mContext.getString(17040533));
  }
  
  public boolean onMenuItemClick(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    }
    for (;;)
    {
      return false;
      MetricsLogger.action(getContext(), 359);
      reset();
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    ImageView localImageView = this.mBackButton;
    if (paramInt == 1) {}
    for (paramInt = 180;; paramInt = 0)
    {
      localImageView.setRotation(paramInt);
      return;
    }
  }
  
  public void setContainer(NotificationsQuickSettingsContainer paramNotificationsQuickSettingsContainer)
  {
    this.mNotifQsContainer = paramNotificationsQuickSettingsContainer;
  }
  
  public void setHost(QSTileHost paramQSTileHost)
  {
    this.mHost = paramQSTileHost;
    this.mPhoneStatusBar = paramQSTileHost.getPhoneStatusBar();
    this.mTileAdapter.setHost(paramQSTileHost);
  }
  
  public void setQsContainer(QSContainer paramQSContainer)
  {
    this.mQsContainer = paramQSContainer;
  }
  
  public void show(int paramInt1, int paramInt2)
  {
    if (!this.isShown)
    {
      MetricsLogger.visible(getContext(), 358);
      this.isShown = true;
      this.mQsContainer.setShadow(false);
      setTileSpecs();
      this.mClipper.animateCircularClip(paramInt1, paramInt2, true, this.mExpandAnimationListener, 100);
      new TileQueryHelper(this.mContext, this.mHost).setListener(this.mTileAdapter);
      this.mNotifQsContainer.setCustomizerAnimating(true);
      this.mNotifQsContainer.setCustomizerShowing(true);
      announceForAccessibility(this.mContext.getString(2131690626));
      this.mHost.getKeyguardMonitor().addCallback(this.mKeyguardCallback);
    }
  }
  
  protected void updateThemeColor()
  {
    int i = ThemeColorUtils.getColor(ThemeColorUtils.QS_SYSTEM_PRIMARY);
    int j = ThemeColorUtils.getColor(ThemeColorUtils.QS_PRIMARY_TEXT);
    int k = ThemeColorUtils.getColor(ThemeColorUtils.QS_EDIT_BOTTOM);
    int m = ThemeColorUtils.getColor(ThemeColorUtils.QS_EDIT_TEXT_BOTTOM);
    int n = ThemeColorUtils.getColor(ThemeColorUtils.PROGRESS);
    int i1 = ThemeColorUtils.getColor(ThemeColorUtils.QS_EDIT_TILE_BACKGROUND);
    int i2 = ThemeColorUtils.getColor(ThemeColorUtils.QS_ICON_ACTIVE);
    this.mQSTitle.setTextColor(j);
    this.mBackButton.setImageTintList(ColorStateList.valueOf(i2));
    this.mToolbar.setBackgroundColor(i);
    this.mToolbarBackground.setBackgroundColor(i);
    this.mRecyclerView.setBackgroundColor(i);
    this.mTileAdapter.setThemeColor(k, m, i1, n);
    this.mToolbar.setPopupTheme(ThemeColorUtils.getPopTheme());
    this.mToolbar.getOverflowIcon().setTint(i2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\customize\QSCustomizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */