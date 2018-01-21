package com.android.systemui.qs;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.settings.BrightnessController;
import com.android.systemui.settings.SimSwitchController;
import com.android.systemui.settings.ToggleSeekBar;
import com.android.systemui.settings.ToggleSlider;
import com.android.systemui.statusbar.phone.QSTileHost;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import com.android.systemui.statusbar.policy.MobileSignalController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.android.systemui.util.ThemeColorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class QSPanel
  extends LinearLayout
  implements TunerService.Tunable, QSTile.Host.Callback
{
  private BrightnessController mBrightnessController;
  private View mBrightnessMirror;
  private BrightnessMirrorController mBrightnessMirrorController;
  private int mBrightnessPaddingTop;
  protected final View mBrightnessView;
  private Callback mCallback;
  protected final Context mContext;
  private QSCustomizer mCustomizePanel;
  private Record mDetailRecord;
  protected boolean mExpanded;
  private boolean mGridContentVisible = true;
  private final H mHandler = new H(null);
  protected QSTileHost mHost;
  protected boolean mListening;
  private int mPanelPaddingBottom;
  protected final ArrayList<TileRecord> mRecords = new ArrayList();
  private SimSwitchController mSimSwitchController;
  protected View mSimSwitcherView = null;
  protected QSTileLayout mTileLayout;
  
  public QSPanel(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public QSPanel(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    setOrientation(1);
    if (MobileSignalController.isCarrierOneSupported())
    {
      this.mSimSwitcherView = LayoutInflater.from(paramContext).inflate(2130968818, this, false);
      addView(this.mSimSwitcherView);
      this.mSimSwitchController = new SimSwitchController(getContext(), this.mSimSwitcherView, this);
    }
    setupTileLayout();
    this.mBrightnessView = LayoutInflater.from(paramContext).inflate(2130968784, this, false);
    addView(this.mBrightnessView);
    updateResources();
    this.mBrightnessController = new BrightnessController(getContext(), (ImageView)findViewById(2131952165), (ToggleSlider)findViewById(2131952163));
  }
  
  private void fireScanStateChanged(boolean paramBoolean)
  {
    if (this.mCallback != null) {
      this.mCallback.onScanStateChanged(paramBoolean);
    }
  }
  
  private void fireShowingDetail(QSTile.DetailAdapter paramDetailAdapter, int paramInt1, int paramInt2)
  {
    if (this.mCallback != null) {
      this.mCallback.onShowingDetail(paramDetailAdapter, paramInt1, paramInt2);
    }
  }
  
  private void fireToggleStateChanged(boolean paramBoolean)
  {
    if (this.mCallback != null) {
      this.mCallback.onToggleStateChanged(paramBoolean);
    }
  }
  
  private QSTile<?> getTile(String paramString)
  {
    int i = 0;
    while (i < this.mRecords.size())
    {
      if (paramString.equals(((TileRecord)this.mRecords.get(i)).tile.getTileSpec())) {
        return ((TileRecord)this.mRecords.get(i)).tile;
      }
      i += 1;
    }
    return this.mHost.createTile(paramString);
  }
  
  private void handleShowDetailImpl(Record paramRecord, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    Object localObject2 = null;
    if (paramBoolean) {}
    for (Object localObject1 = paramRecord;; localObject1 = null)
    {
      setDetailRecord((Record)localObject1);
      localObject1 = localObject2;
      if (paramBoolean) {
        localObject1 = paramRecord.detailAdapter;
      }
      fireShowingDetail((QSTile.DetailAdapter)localObject1, paramInt1, paramInt2);
      return;
    }
  }
  
  private void handleShowDetailTile(TileRecord paramTileRecord, boolean paramBoolean)
  {
    if (this.mDetailRecord != null) {}
    for (boolean bool = true; (bool == paramBoolean) && (this.mDetailRecord == paramTileRecord); bool = false) {
      return;
    }
    if (paramBoolean)
    {
      paramTileRecord.detailAdapter = paramTileRecord.tile.getDetailAdapter();
      if (paramTileRecord.detailAdapter == null) {
        return;
      }
    }
    paramTileRecord.tile.setDetailListening(paramBoolean);
    handleShowDetailImpl(paramTileRecord, paramBoolean, paramTileRecord.tileView.getLeft() + paramTileRecord.tileView.getWidth() / 2, paramTileRecord.tileView.getTop() + this.mTileLayout.getOffsetTop(paramTileRecord) + paramTileRecord.tileView.getHeight() / 2 + getTop());
  }
  
  private void logTiles()
  {
    int i = 0;
    while (i < this.mRecords.size())
    {
      TileRecord localTileRecord = (TileRecord)this.mRecords.get(i);
      MetricsLogger.visible(this.mContext, localTileRecord.tile.getMetricsCategory());
      i += 1;
    }
  }
  
  private void setDetailRecord(Record paramRecord)
  {
    if (paramRecord == this.mDetailRecord) {
      return;
    }
    this.mDetailRecord = paramRecord;
    if ((this.mDetailRecord instanceof TileRecord)) {}
    for (boolean bool = ((TileRecord)this.mDetailRecord).scanState;; bool = false)
    {
      fireScanStateChanged(bool);
      return;
    }
  }
  
  protected void addTile(QSTile<?> paramQSTile, boolean paramBoolean)
  {
    final TileRecord localTileRecord = new TileRecord();
    localTileRecord.tile = paramQSTile;
    localTileRecord.tileView = createTileView(paramQSTile, paramBoolean);
    paramQSTile = new QSTile.Callback()
    {
      public void onAnnouncementRequested(CharSequence paramAnonymousCharSequence)
      {
        if (paramAnonymousCharSequence != null) {
          QSPanel.-get2(QSPanel.this).obtainMessage(3, paramAnonymousCharSequence).sendToTarget();
        }
      }
      
      public void onScanStateChanged(boolean paramAnonymousBoolean)
      {
        localTileRecord.scanState = paramAnonymousBoolean;
        if (QSPanel.-get1(QSPanel.this) == localTileRecord) {
          QSPanel.-wrap0(QSPanel.this, localTileRecord.scanState);
        }
      }
      
      public void onShowDetail(boolean paramAnonymousBoolean)
      {
        if (QSPanel.this.shouldShowDetail()) {
          QSPanel.this.showDetail(paramAnonymousBoolean, localTileRecord);
        }
      }
      
      public void onStateChanged(QSTile.State paramAnonymousState)
      {
        QSPanel.this.drawTile(localTileRecord, paramAnonymousState);
      }
      
      public void onToggleStateChanged(boolean paramAnonymousBoolean)
      {
        if (QSPanel.-get1(QSPanel.this) == localTileRecord) {
          QSPanel.-wrap1(QSPanel.this, paramAnonymousBoolean);
        }
      }
    };
    localTileRecord.tile.addCallback(paramQSTile);
    localTileRecord.callback = paramQSTile;
    paramQSTile = new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        QSPanel.this.onTileClick(localTileRecord.tile);
      }
    };
    View.OnLongClickListener local3 = new View.OnLongClickListener()
    {
      public boolean onLongClick(View paramAnonymousView)
      {
        localTileRecord.tile.longClick();
        return true;
      }
    };
    localTileRecord.tileView.init(paramQSTile, local3);
    localTileRecord.tile.refreshState();
    this.mRecords.add(localTileRecord);
    if (this.mTileLayout != null) {
      this.mTileLayout.addTile(localTileRecord);
    }
  }
  
  public void clickTile(ComponentName paramComponentName)
  {
    paramComponentName = CustomTile.toSpec(paramComponentName);
    int j = this.mRecords.size();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        if (((TileRecord)this.mRecords.get(i)).tile.getTileSpec().equals(paramComponentName)) {
          ((TileRecord)this.mRecords.get(i)).tile.click();
        }
      }
      else {
        return;
      }
      i += 1;
    }
  }
  
  public void closeDetail()
  {
    if ((this.mCustomizePanel != null) && (this.mCustomizePanel.isShown()))
    {
      this.mCustomizePanel.hideNoAnimation();
      return;
    }
    showDetail(false, this.mDetailRecord);
  }
  
  protected QSTileBaseView createTileView(QSTile<?> paramQSTile, boolean paramBoolean)
  {
    return new QSTileView(this.mContext, paramQSTile.createTileView(this.mContext), paramBoolean);
  }
  
  protected void drawTile(TileRecord paramTileRecord, QSTile.State paramState)
  {
    paramTileRecord.tileView.onStateChanged(paramState);
  }
  
  public View getBrightnessView()
  {
    return this.mBrightnessView;
  }
  
  public QSTileHost getHost()
  {
    return this.mHost;
  }
  
  QSTileLayout getTileLayout()
  {
    return this.mTileLayout;
  }
  
  QSTileBaseView getTileView(QSTile<?> paramQSTile)
  {
    Iterator localIterator = this.mRecords.iterator();
    while (localIterator.hasNext())
    {
      TileRecord localTileRecord = (TileRecord)localIterator.next();
      if (localTileRecord.tile == paramQSTile) {
        return localTileRecord.tileView;
      }
    }
    return null;
  }
  
  protected void handleShowDetail(Record paramRecord, boolean paramBoolean)
  {
    if ((paramRecord instanceof TileRecord))
    {
      handleShowDetailTile((TileRecord)paramRecord, paramBoolean);
      return;
    }
    int i = 0;
    int j = 0;
    if (paramRecord != null)
    {
      i = paramRecord.x;
      j = paramRecord.y;
    }
    handleShowDetailImpl(paramRecord, paramBoolean, i, j);
  }
  
  public boolean isShowingCustomize()
  {
    if (this.mCustomizePanel != null) {
      return this.mCustomizePanel.isCustomizing();
    }
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    TunerService.get(this.mContext).addTunable(this, new String[] { "qs_show_brightness" });
    if (this.mHost != null) {
      setTiles(this.mHost.getTiles());
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.mBrightnessMirrorController != null) {
      setBrightnessMirror(this.mBrightnessMirrorController);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    TunerService.get(this.mContext).removeTunable(this);
    this.mHost.removeCallback(this);
    Iterator localIterator = this.mRecords.iterator();
    while (localIterator.hasNext()) {
      ((TileRecord)localIterator.next()).tile.removeCallbacks();
    }
    super.onDetachedFromWindow();
  }
  
  protected void onTileClick(QSTile<?> paramQSTile)
  {
    paramQSTile.click();
  }
  
  public void onTilesChanged()
  {
    setTiles(this.mHost.getTiles());
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    int j = 0;
    if ("qs_show_brightness".equals(paramString1))
    {
      paramString1 = this.mBrightnessView;
      i = j;
      if (paramString2 != null) {
        if (Integer.parseInt(paramString2) == 0) {
          break label41;
        }
      }
    }
    label41:
    for (int i = j;; i = 8)
    {
      paramString1.setVisibility(i);
      return;
    }
  }
  
  public void openDetails(String paramString)
  {
    showDetailAdapter(true, getTile(paramString).getDetailAdapter(), new int[] { getWidth() / 2, 0 });
  }
  
  public void refreshAllTiles()
  {
    Iterator localIterator = this.mRecords.iterator();
    while (localIterator.hasNext()) {
      ((TileRecord)localIterator.next()).tile.refreshState();
    }
  }
  
  public void setBrightnessMirror(BrightnessMirrorController paramBrightnessMirrorController)
  {
    this.mBrightnessMirrorController = paramBrightnessMirrorController;
    ToggleSlider localToggleSlider = (ToggleSlider)findViewById(2131952163);
    localToggleSlider.setMirror((ToggleSlider)paramBrightnessMirrorController.getMirror().findViewById(2131952163));
    localToggleSlider.setMirrorController(paramBrightnessMirrorController);
    this.mBrightnessMirror = paramBrightnessMirrorController.getMirror();
    this.mBrightnessController.setMirrorView(this.mBrightnessMirror);
    updateThemeColor();
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public void setExpanded(boolean paramBoolean)
  {
    if (this.mExpanded == paramBoolean) {
      return;
    }
    this.mExpanded = paramBoolean;
    if ((!this.mExpanded) && ((this.mTileLayout instanceof PagedTileLayout))) {
      ((PagedTileLayout)this.mTileLayout).setCurrentItem(0, false);
    }
    MetricsLogger.visibility(this.mContext, 111, this.mExpanded);
    if (!this.mExpanded)
    {
      closeDetail();
      return;
    }
    logTiles();
  }
  
  void setGridContentVisibility(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      setVisibility(i);
      if (this.mGridContentVisible != paramBoolean) {
        MetricsLogger.visibility(this.mContext, 111, i);
      }
      this.mGridContentVisible = paramBoolean;
      return;
    }
  }
  
  public void setHost(QSTileHost paramQSTileHost, QSCustomizer paramQSCustomizer)
  {
    this.mHost = paramQSTileHost;
    this.mHost.addCallback(this);
    setTiles(this.mHost.getTiles());
    this.mCustomizePanel = paramQSCustomizer;
    if (this.mCustomizePanel != null) {
      this.mCustomizePanel.setHost(this.mHost);
    }
    this.mBrightnessController.setBackgroundLooper(paramQSTileHost.getLooper());
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mListening == paramBoolean) {
      return;
    }
    this.mListening = paramBoolean;
    if (this.mTileLayout != null) {
      this.mTileLayout.setListening(paramBoolean);
    }
    if (this.mListening) {
      refreshAllTiles();
    }
    if (this.mBrightnessView.getVisibility() == 0)
    {
      if (paramBoolean) {
        this.mBrightnessController.registerCallbacks();
      }
    }
    else {
      return;
    }
    this.mBrightnessController.unregisterCallbacks();
  }
  
  public void setTiles(Collection<QSTile<?>> paramCollection)
  {
    setTiles(paramCollection, false);
  }
  
  public void setTiles(Collection<QSTile<?>> paramCollection, boolean paramBoolean)
  {
    Iterator localIterator = this.mRecords.iterator();
    while (localIterator.hasNext())
    {
      TileRecord localTileRecord = (TileRecord)localIterator.next();
      this.mTileLayout.removeTile(localTileRecord);
      localTileRecord.tile.removeCallback(localTileRecord.callback);
    }
    this.mRecords.clear();
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      addTile((QSTile)paramCollection.next(), paramBoolean);
    }
  }
  
  protected void setupTileLayout()
  {
    this.mTileLayout = ((QSTileLayout)LayoutInflater.from(this.mContext).inflate(2130968778, this, false));
    this.mTileLayout.setListening(this.mListening);
    addView((View)this.mTileLayout);
  }
  
  protected boolean shouldShowDetail()
  {
    return this.mExpanded;
  }
  
  protected void showDetail(boolean paramBoolean, Record paramRecord)
  {
    H localH = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localH.obtainMessage(1, i, 0, paramRecord).sendToTarget();
      return;
    }
  }
  
  public void showDetailAdapter(boolean paramBoolean, QSTile.DetailAdapter paramDetailAdapter, int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt[0];
    int j = paramArrayOfInt[1];
    ((View)getParent()).getLocationInWindow(paramArrayOfInt);
    Record localRecord = new Record();
    localRecord.detailAdapter = paramDetailAdapter;
    localRecord.x = (i - paramArrayOfInt[0]);
    localRecord.y = (j - paramArrayOfInt[1]);
    paramArrayOfInt[0] = i;
    paramArrayOfInt[1] = j;
    showDetail(paramBoolean, localRecord);
  }
  
  public void showEdit(final View paramView)
  {
    paramView.post(new Runnable()
    {
      public void run()
      {
        if ((QSPanel.-get0(QSPanel.this) != null) && (!QSPanel.-get0(QSPanel.this).isCustomizing()))
        {
          int[] arrayOfInt = new int[2];
          paramView.getLocationInWindow(arrayOfInt);
          int i = arrayOfInt[0];
          int j = arrayOfInt[1];
          QSPanel.-get0(QSPanel.this).show(i, j);
        }
      }
    });
  }
  
  public void updateResources()
  {
    Object localObject = this.mContext.getResources();
    this.mPanelPaddingBottom = ((Resources)localObject).getDimensionPixelSize(2131755432);
    this.mBrightnessPaddingTop = ((Resources)localObject).getDimensionPixelSize(2131755434);
    setPadding(0, this.mBrightnessPaddingTop, 0, this.mPanelPaddingBottom);
    localObject = this.mRecords.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((TileRecord)((Iterator)localObject).next()).tile.clearState();
    }
    if (this.mListening) {
      refreshAllTiles();
    }
    if (this.mTileLayout != null) {
      this.mTileLayout.updateResources();
    }
  }
  
  protected void updateThemeColor()
  {
    int i = ThemeColorUtils.getColor(ThemeColorUtils.QS_SYSTEM_PRIMARY);
    int j = ThemeColorUtils.getColor(ThemeColorUtils.PROGRESS);
    int k = ThemeColorUtils.getColor(ThemeColorUtils.PROGRESS_BACKGROUND);
    int m = ThemeColorUtils.getColor(ThemeColorUtils.QS_ICON_ACTIVE);
    int n = ThemeColorUtils.getColor(ThemeColorUtils.QS_ICON_INACTIVE);
    ((ImageButton)this.mBrightnessView.findViewById(2131952165)).setImageTintList(ColorStateList.valueOf(m));
    ((ImageView)this.mBrightnessView.findViewById(2131952162)).setImageTintList(ColorStateList.valueOf(n));
    ((ImageView)this.mBrightnessView.findViewById(2131952164)).setImageTintList(ColorStateList.valueOf(n));
    ((ToggleSeekBar)this.mBrightnessView.findViewById(2131952298)).setThumbTintList(ColorStateList.valueOf(j));
    ((ToggleSeekBar)this.mBrightnessView.findViewById(2131952298)).setProgressTintList(ColorStateList.valueOf(j));
    ((ToggleSeekBar)this.mBrightnessView.findViewById(2131952298)).setProgressBackgroundTintList(ColorStateList.valueOf(k));
    if (this.mBrightnessMirror != null)
    {
      ((FrameLayout)this.mBrightnessMirror.findViewById(2131951798)).setBackgroundTintList(ColorStateList.valueOf(i));
      ((ImageButton)this.mBrightnessMirror.findViewById(2131952165)).setImageTintList(ColorStateList.valueOf(m));
      ((ImageView)this.mBrightnessMirror.findViewById(2131952162)).setImageTintList(ColorStateList.valueOf(n));
      ((ImageView)this.mBrightnessMirror.findViewById(2131952164)).setImageTintList(ColorStateList.valueOf(n));
      ((ToggleSeekBar)this.mBrightnessMirror.findViewById(2131952298)).setThumbTintList(ColorStateList.valueOf(j));
      ((ToggleSeekBar)this.mBrightnessMirror.findViewById(2131952298)).setProgressTintList(ColorStateList.valueOf(j));
      ((ToggleSeekBar)this.mBrightnessMirror.findViewById(2131952298)).setProgressBackgroundTintList(ColorStateList.valueOf(k));
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onScanStateChanged(boolean paramBoolean);
    
    public abstract void onShowingDetail(QSTile.DetailAdapter paramDetailAdapter, int paramInt1, int paramInt2);
    
    public abstract void onToggleStateChanged(boolean paramBoolean);
  }
  
  private class H
    extends Handler
  {
    private H() {}
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = true;
      if (paramMessage.what == 1)
      {
        localQSPanel = QSPanel.this;
        localRecord = (QSPanel.Record)paramMessage.obj;
        if (paramMessage.arg1 != 0) {
          localQSPanel.handleShowDetail(localRecord, bool);
        }
      }
      while (paramMessage.what != 3) {
        for (;;)
        {
          QSPanel localQSPanel;
          QSPanel.Record localRecord;
          return;
          bool = false;
        }
      }
      QSPanel.this.announceForAccessibility((CharSequence)paramMessage.obj);
    }
  }
  
  public static abstract interface QSTileLayout
  {
    public abstract void addTile(QSPanel.TileRecord paramTileRecord);
    
    public abstract int getOffsetTop(QSPanel.TileRecord paramTileRecord);
    
    public abstract void removeTile(QSPanel.TileRecord paramTileRecord);
    
    public abstract void setListening(boolean paramBoolean);
    
    public abstract boolean updateResources();
  }
  
  protected static class Record
  {
    QSTile.DetailAdapter detailAdapter;
    int x;
    int y;
  }
  
  public static final class TileRecord
    extends QSPanel.Record
  {
    public QSTile.Callback callback;
    public boolean scanState;
    public QSTile<?> tile;
    public QSTileBaseView tileView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QSPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */