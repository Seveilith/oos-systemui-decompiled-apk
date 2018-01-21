package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Space;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.statusbar.phone.QSTileHost;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class QuickQSPanel
  extends QSPanel
{
  private QSPanel mFullPanel;
  private View mHeader;
  private int mMaxTiles;
  private final TunerService.Tunable mNumTiles = new TunerService.Tunable()
  {
    public void onTuningChanged(String paramAnonymousString1, String paramAnonymousString2)
    {
      QuickQSPanel.this.setMaxTiles(QuickQSPanel.this.getNumQuickTiles(QuickQSPanel.this.mContext));
    }
  };
  
  public QuickQSPanel(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (this.mTileLayout != null)
    {
      int i = 0;
      while (i < this.mRecords.size())
      {
        this.mTileLayout.removeTile((QSPanel.TileRecord)this.mRecords.get(i));
        i += 1;
      }
      removeView((View)this.mTileLayout);
    }
    this.mTileLayout = new HeaderTileLayout(paramContext);
    this.mTileLayout.setListening(this.mListening);
    addView((View)this.mTileLayout, 1);
  }
  
  protected QSTileBaseView createTileView(QSTile<?> paramQSTile, boolean paramBoolean)
  {
    return new QSTileBaseView(this.mContext, paramQSTile.createTileView(this.mContext), paramBoolean);
  }
  
  protected void drawTile(QSPanel.TileRecord paramTileRecord, QSTile.State paramState)
  {
    QSTile.State localState = paramState;
    if ((paramState instanceof QSTile.SignalState))
    {
      localState = paramTileRecord.tile.newTileState();
      paramState.copyTo(localState);
      ((QSTile.SignalState)localState).activityIn = false;
      ((QSTile.SignalState)localState).activityOut = false;
    }
    super.drawTile(paramTileRecord, localState);
  }
  
  public int getNumQuickTiles(Context paramContext)
  {
    return TunerService.get(paramContext).getValue("sysui_qqs_count", 5);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    TunerService.get(this.mContext).addTunable(this.mNumTiles, new String[] { "sysui_qqs_count" });
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    TunerService.get(this.mContext).removeTunable(this.mNumTiles);
  }
  
  protected void onTileClick(QSTile<?> paramQSTile)
  {
    paramQSTile.secondaryClick();
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if (paramString1.equals("qs_show_brightness")) {
      super.onTuningChanged(paramString1, "0");
    }
  }
  
  public void setHost(QSTileHost paramQSTileHost, QSCustomizer paramQSCustomizer)
  {
    super.setHost(paramQSTileHost, paramQSCustomizer);
    setTiles(this.mHost.getTiles());
  }
  
  public void setMaxTiles(int paramInt)
  {
    this.mMaxTiles = paramInt;
    if (this.mHost != null) {
      setTiles(this.mHost.getTiles());
    }
  }
  
  public void setQSPanelAndHeader(QSPanel paramQSPanel, View paramView)
  {
    this.mFullPanel = paramQSPanel;
    this.mHeader = paramView;
  }
  
  public void setTiles(Collection<QSTile<?>> paramCollection)
  {
    ArrayList localArrayList = new ArrayList();
    paramCollection = paramCollection.iterator();
    do
    {
      if (!paramCollection.hasNext()) {
        break;
      }
      localArrayList.add((QSTile)paramCollection.next());
    } while (localArrayList.size() != this.mMaxTiles);
    super.setTiles(localArrayList, true);
  }
  
  protected boolean shouldShowDetail()
  {
    return !this.mExpanded;
  }
  
  private static class HeaderTileLayout
    extends LinearLayout
    implements QSPanel.QSTileLayout
  {
    private final Space mEndSpacer;
    private boolean mListening;
    protected final ArrayList<QSPanel.TileRecord> mRecords = new ArrayList();
    
    public HeaderTileLayout(Context paramContext)
    {
      super();
      setClipChildren(false);
      setClipToPadding(false);
      setGravity(16);
      setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
      this.mEndSpacer = new Space(paramContext);
      this.mEndSpacer.setLayoutParams(generateEmptyParams());
      addView(this.mEndSpacer);
      setOrientation(0);
    }
    
    private LinearLayout.LayoutParams generateEmptyParams()
    {
      LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(0, 0);
      localLayoutParams.gravity = 17;
      return localLayoutParams;
    }
    
    private LinearLayout.LayoutParams generateLayoutParams()
    {
      int i = this.mContext.getResources().getDimensionPixelSize(2131755411);
      LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(i, i);
      localLayoutParams.gravity = 17;
      return localLayoutParams;
    }
    
    private LinearLayout.LayoutParams generateSpaceParams()
    {
      LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(0, this.mContext.getResources().getDimensionPixelSize(2131755411));
      localLayoutParams.weight = 1.0F;
      localLayoutParams.gravity = 17;
      return localLayoutParams;
    }
    
    private int getChildIndex(QSTileBaseView paramQSTileBaseView)
    {
      int j = getChildCount();
      int i = 0;
      while (i < j)
      {
        if (getChildAt(i) == paramQSTileBaseView) {
          return i;
        }
        i += 1;
      }
      return -1;
    }
    
    public void addTile(QSPanel.TileRecord paramTileRecord)
    {
      this.mRecords.add(paramTileRecord);
      addView(paramTileRecord.tileView, getChildCount() - 1, generateLayoutParams());
      if (this.mRecords.size() == TunerService.get(this.mContext).getValue("sysui_qqs_count", 5)) {
        addView(new Space(this.mContext), getChildCount() - 1, generateEmptyParams());
      }
      for (;;)
      {
        paramTileRecord.tile.setListening(this, this.mListening);
        return;
        addView(new Space(this.mContext), getChildCount() - 1, generateSpaceParams());
      }
    }
    
    public int getOffsetTop(QSPanel.TileRecord paramTileRecord)
    {
      return 0;
    }
    
    public boolean hasOverlappingRendering()
    {
      return false;
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      if ((this.mRecords != null) && (this.mRecords.size() > 0))
      {
        Object localObject = this;
        Iterator localIterator = this.mRecords.iterator();
        while (localIterator.hasNext())
        {
          QSPanel.TileRecord localTileRecord = (QSPanel.TileRecord)localIterator.next();
          if (localTileRecord.tileView.getVisibility() != 8) {
            localObject = localTileRecord.tileView.updateAccessibilityOrder((View)localObject);
          }
        }
        ((QSPanel.TileRecord)this.mRecords.get(0)).tileView.setAccessibilityTraversalAfter(2131952269);
        ((QSPanel.TileRecord)this.mRecords.get(this.mRecords.size() - 1)).tileView.setAccessibilityTraversalBefore(2131952174);
      }
    }
    
    public void removeTile(QSPanel.TileRecord paramTileRecord)
    {
      int i = getChildIndex(paramTileRecord.tileView);
      removeViewAt(i);
      removeViewAt(i);
      this.mRecords.remove(paramTileRecord);
      paramTileRecord.tile.setListening(this, false);
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (this.mListening == paramBoolean) {
        return;
      }
      this.mListening = paramBoolean;
      Iterator localIterator = this.mRecords.iterator();
      while (localIterator.hasNext()) {
        ((QSPanel.TileRecord)localIterator.next()).tile.setListening(this, this.mListening);
      }
    }
    
    public boolean updateResources()
    {
      return false;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QuickQSPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */