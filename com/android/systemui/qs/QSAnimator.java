package com.android.systemui.qs;

import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.TextView;
import com.android.systemui.statusbar.phone.QSTileHost;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.util.ArrayList;
import java.util.Iterator;

public class QSAnimator
  implements QSTile.Host.Callback, PagedTileLayout.PageListener, TouchAnimator.Listener, View.OnLayoutChangeListener, View.OnAttachStateChangeListener, TunerService.Tunable
{
  private final ArrayList<View> mAllViews = new ArrayList();
  private boolean mAllowFancy;
  private TouchAnimator mAlphaAnimator;
  private TouchAnimator mFirstPageAnimator;
  private TouchAnimator mFirstPageDelayedAnimator;
  private boolean mFullRows;
  private QSTileHost mHost;
  private float mLastPosition;
  private TouchAnimator mLastRowAnimator;
  private final TouchAnimator.Listener mNonFirstPageListener = new TouchAnimator.ListenerAdapter()
  {
    public void onAnimationStarted()
    {
      QSAnimator.-get1(QSAnimator.this).setVisibility(0);
    }
  };
  private TouchAnimator mNonfirstPageAnimator;
  private int mNumQuickTiles;
  private boolean mOnFirstPage = true;
  private boolean mOnKeyguard;
  private PagedTileLayout mPagedLayout;
  private final QSContainer mQsContainer;
  private final QSPanel mQsPanel;
  private final QuickQSPanel mQuickQsPanel;
  private final ArrayList<View> mTopFiveQs = new ArrayList();
  private TouchAnimator mTranslationXAnimator;
  private TouchAnimator mTranslationYAnimator;
  private Runnable mUpdateAnimators = new Runnable()
  {
    public void run()
    {
      QSAnimator.-wrap0(QSAnimator.this);
      QSAnimator.this.setPosition(QSAnimator.-get0(QSAnimator.this));
    }
  };
  
  public QSAnimator(QSContainer paramQSContainer, QuickQSPanel paramQuickQSPanel, QSPanel paramQSPanel)
  {
    this.mQsContainer = paramQSContainer;
    this.mQuickQsPanel = paramQuickQSPanel;
    this.mQsPanel = paramQSPanel;
    this.mQsPanel.addOnAttachStateChangeListener(this);
    paramQSContainer.addOnLayoutChangeListener(this);
    paramQSContainer = this.mQsPanel.getTileLayout();
    if ((paramQSContainer instanceof PagedTileLayout))
    {
      this.mPagedLayout = ((PagedTileLayout)paramQSContainer);
      this.mPagedLayout.setPageListener(this);
      return;
    }
    Log.w("QSAnimator", "QS Not using page layout");
  }
  
  private void clearAnimationState()
  {
    int j = this.mAllViews.size();
    this.mQuickQsPanel.setAlpha(0.0F);
    this.mQsPanel.getBrightnessView().setAlpha(1.0F);
    int i = 0;
    while (i < j)
    {
      View localView = (View)this.mAllViews.get(i);
      localView.setAlpha(1.0F);
      localView.setTranslationX(0.0F);
      localView.setTranslationY(0.0F);
      i += 1;
    }
    j = this.mTopFiveQs.size();
    i = 0;
    while (i < j)
    {
      ((View)this.mTopFiveQs.get(i)).setVisibility(0);
      i += 1;
    }
  }
  
  private void getRelativePosition(int[] paramArrayOfInt, View paramView1, View paramView2)
  {
    paramArrayOfInt[0] = (paramView1.getWidth() / 2 + 0);
    paramArrayOfInt[1] = 0;
    getRelativePositionInt(paramArrayOfInt, paramView1, paramView2);
  }
  
  private void getRelativePositionInt(int[] paramArrayOfInt, View paramView1, View paramView2)
  {
    if ((paramView1 == paramView2) || (paramView1 == null)) {
      return;
    }
    if (!(paramView1 instanceof PagedTileLayout.TilePage))
    {
      paramArrayOfInt[0] += paramView1.getLeft();
      paramArrayOfInt[1] += paramView1.getTop();
    }
    getRelativePositionInt(paramArrayOfInt, (View)paramView1.getParent(), paramView2);
  }
  
  private boolean isIconInAnimatedRow(int paramInt)
  {
    boolean bool = false;
    if (this.mPagedLayout == null) {
      return false;
    }
    int i = this.mPagedLayout.getColumnCount();
    if (paramInt < (this.mNumQuickTiles + i - 1) / i * i) {
      bool = true;
    }
    return bool;
  }
  
  private void updateAnimators()
  {
    Object localObject1 = new TouchAnimator.Builder();
    TouchAnimator.Builder localBuilder1 = new TouchAnimator.Builder();
    TouchAnimator.Builder localBuilder2 = new TouchAnimator.Builder();
    TouchAnimator.Builder localBuilder3 = new TouchAnimator.Builder();
    TouchAnimator.Builder localBuilder4 = new TouchAnimator.Builder();
    if (this.mQsPanel.getHost() == null) {
      return;
    }
    Object localObject2 = this.mQsPanel.getHost().getTiles();
    int i = 0;
    int[] arrayOfInt1 = new int[2];
    int[] arrayOfInt2 = new int[2];
    int j = 0;
    clearAnimationState();
    this.mAllViews.clear();
    this.mTopFiveQs.clear();
    this.mAllViews.add((View)this.mQsPanel.getTileLayout());
    localObject2 = ((Iterable)localObject2).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      QSTile localQSTile = (QSTile)((Iterator)localObject2).next();
      QSTileBaseView localQSTileBaseView1 = this.mQsPanel.getTileView(localQSTile);
      if (localQSTileBaseView1 == null)
      {
        Log.e("QSAnimator", "tileView is null " + localQSTile.getTileSpec());
      }
      else
      {
        TextView localTextView = ((QSTileView)localQSTileBaseView1).getLabel();
        View localView = localQSTileBaseView1.getIcon().getIconView();
        int k;
        int m;
        if ((i < this.mNumQuickTiles) && (this.mAllowFancy))
        {
          QSTileBaseView localQSTileBaseView2 = this.mQuickQsPanel.getTileView(localQSTile);
          if (localQSTileBaseView2 == null)
          {
            Log.e("QSAnimator", "quickTileView is null " + localQSTile.getTileSpec());
          }
          else
          {
            j = arrayOfInt1[0];
            getRelativePosition(arrayOfInt1, localQSTileBaseView2.getIcon(), this.mQsContainer);
            getRelativePosition(arrayOfInt2, localView, this.mQsContainer);
            k = arrayOfInt2[0] - arrayOfInt1[0];
            m = arrayOfInt2[1] - arrayOfInt1[1];
            j = arrayOfInt1[0] - j;
            localBuilder2.addFloat(localQSTileBaseView2, "translationX", new float[] { 0.0F, k });
            localBuilder3.addFloat(localQSTileBaseView2, "translationY", new float[] { 0.0F, m });
            ((TouchAnimator.Builder)localObject1).addFloat(localQSTileBaseView1, "translationY", new float[] { this.mQsPanel.getHeight(), 0.0F });
            localBuilder2.addFloat(localTextView, "translationX", new float[] { -k, 0.0F });
            localBuilder3.addFloat(localTextView, "translationY", new float[] { -m, 0.0F });
            this.mTopFiveQs.add(localView);
            this.mAllViews.add(localView);
            this.mAllViews.add(localQSTileBaseView2);
          }
        }
        else
        {
          for (;;)
          {
            this.mAllViews.add(localQSTileBaseView1);
            this.mAllViews.add(localTextView);
            i += 1;
            break;
            if ((this.mFullRows) && (isIconInAnimatedRow(i)))
            {
              arrayOfInt1[0] += j;
              getRelativePosition(arrayOfInt2, localView, this.mQsContainer);
              k = arrayOfInt2[0];
              m = arrayOfInt1[0];
              int n = arrayOfInt2[1] - arrayOfInt1[1];
              ((TouchAnimator.Builder)localObject1).addFloat(localQSTileBaseView1, "translationY", new float[] { this.mQsPanel.getHeight(), 0.0F });
              localBuilder2.addFloat(localQSTileBaseView1, "translationX", new float[] { -(k - m), 0.0F });
              localBuilder3.addFloat(localTextView, "translationY", new float[] { -n, 0.0F });
              localBuilder3.addFloat(localView, "translationY", new float[] { -n, 0.0F });
              this.mAllViews.add(localView);
            }
            else
            {
              localBuilder4.addFloat(localQSTileBaseView1, "alpha", new float[] { 0.0F, 1.0F });
            }
          }
        }
      }
    }
    localBuilder1.addFloat(this.mQsPanel.getBrightnessView(), "alpha", new float[] { 0.0F, 1.0F }).setStartDelay(0.5F);
    if (this.mAllowFancy)
    {
      this.mFirstPageAnimator = ((TouchAnimator.Builder)localObject1).setListener(this).build();
      this.mFirstPageDelayedAnimator = new TouchAnimator.Builder().setStartDelay(0.7F).addFloat(this.mQsPanel.getTileLayout(), "alpha", new float[] { 0.0F, 1.0F }).build();
      this.mLastRowAnimator = localBuilder4.setStartDelay(0.86F).build();
      localObject1 = new Path();
      ((Path)localObject1).moveTo(0.0F, 0.0F);
      ((Path)localObject1).cubicTo(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      localObject1 = new PathInterpolatorBuilder(0.0F, 0.0F, 0.0F, 1.0F);
      localBuilder2.setInterpolator(((PathInterpolatorBuilder)localObject1).getXInterpolator());
      localBuilder3.setInterpolator(((PathInterpolatorBuilder)localObject1).getYInterpolator());
      localBuilder1.setInterpolator(((PathInterpolatorBuilder)localObject1).getYInterpolator());
      this.mTranslationXAnimator = localBuilder2.build();
      this.mTranslationYAnimator = localBuilder3.build();
      this.mAlphaAnimator = localBuilder1.build();
    }
    this.mNonfirstPageAnimator = new TouchAnimator.Builder().addFloat(this.mQuickQsPanel, "alpha", new float[] { 1.0F, 0.0F }).setListener(this.mNonFirstPageListener).setEndDelay(0.5F).build();
  }
  
  public void onAnimationAtEnd()
  {
    this.mQuickQsPanel.setVisibility(4);
    int j = this.mTopFiveQs.size();
    int i = 0;
    while (i < j)
    {
      ((View)this.mTopFiveQs.get(i)).setVisibility(0);
      i += 1;
    }
  }
  
  public void onAnimationAtStart()
  {
    this.mQuickQsPanel.setVisibility(0);
  }
  
  public void onAnimationStarted()
  {
    QuickQSPanel localQuickQSPanel = this.mQuickQsPanel;
    if (this.mOnKeyguard) {}
    for (int i = 4;; i = 0)
    {
      localQuickQSPanel.setVisibility(i);
      if (!this.mOnFirstPage) {
        break;
      }
      int j = this.mTopFiveQs.size();
      i = 0;
      while (i < j)
      {
        ((View)this.mTopFiveQs.get(i)).setVisibility(4);
        i += 1;
      }
    }
  }
  
  public void onLayoutChange(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    this.mQsPanel.post(this.mUpdateAnimators);
  }
  
  public void onPageChanged(boolean paramBoolean)
  {
    if (this.mOnFirstPage == paramBoolean) {
      return;
    }
    if (!paramBoolean) {
      clearAnimationState();
    }
    this.mOnFirstPage = paramBoolean;
  }
  
  public void onRtlChanged()
  {
    updateAnimators();
  }
  
  public void onTilesChanged()
  {
    this.mQsPanel.post(this.mUpdateAnimators);
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    boolean bool3 = true;
    boolean bool2 = true;
    boolean bool1;
    if ("sysui_qs_fancy_anim".equals(paramString1))
    {
      bool1 = bool2;
      if (paramString2 != null)
      {
        if (Integer.parseInt(paramString2) != 0) {
          bool1 = bool2;
        }
      }
      else
      {
        this.mAllowFancy = bool1;
        if (!this.mAllowFancy) {
          clearAnimationState();
        }
      }
    }
    for (;;)
    {
      updateAnimators();
      return;
      bool1 = false;
      break;
      if ("sysui_qs_move_whole_rows".equals(paramString1))
      {
        bool1 = bool3;
        if (paramString2 != null) {
          if (Integer.parseInt(paramString2) == 0) {
            break label94;
          }
        }
        label94:
        for (bool1 = bool3;; bool1 = false)
        {
          this.mFullRows = bool1;
          break;
        }
      }
      if ("sysui_qqs_count".equals(paramString1))
      {
        this.mNumQuickTiles = this.mQuickQsPanel.getNumQuickTiles(this.mQsContainer.getContext());
        clearAnimationState();
      }
    }
  }
  
  public void onViewAttachedToWindow(View paramView)
  {
    TunerService.get(this.mQsContainer.getContext()).addTunable(this, new String[] { "sysui_qs_fancy_anim", "sysui_qs_move_whole_rows", "sysui_qqs_count" });
  }
  
  public void onViewDetachedFromWindow(View paramView)
  {
    if (this.mHost != null) {
      this.mHost.removeCallback(this);
    }
    TunerService.get(this.mQsContainer.getContext()).removeTunable(this);
  }
  
  public void setHost(QSTileHost paramQSTileHost)
  {
    this.mHost = paramQSTileHost;
    paramQSTileHost.addCallback(this);
    updateAnimators();
  }
  
  public void setOnKeyguard(boolean paramBoolean)
  {
    this.mOnKeyguard = paramBoolean;
    QuickQSPanel localQuickQSPanel = this.mQuickQsPanel;
    if (this.mOnKeyguard) {}
    for (int i = 4;; i = 0)
    {
      localQuickQSPanel.setVisibility(i);
      if (this.mOnKeyguard) {
        clearAnimationState();
      }
      return;
    }
  }
  
  public void setPosition(float paramFloat)
  {
    if (this.mFirstPageAnimator == null) {
      return;
    }
    if (this.mOnKeyguard) {
      return;
    }
    this.mLastPosition = paramFloat;
    if ((this.mOnFirstPage) && (this.mAllowFancy))
    {
      this.mQuickQsPanel.setAlpha(1.0F);
      this.mFirstPageAnimator.setPosition(paramFloat);
      this.mFirstPageDelayedAnimator.setPosition(paramFloat);
      this.mTranslationXAnimator.setPosition(paramFloat);
      this.mTranslationYAnimator.setPosition(paramFloat);
      this.mLastRowAnimator.setPosition(paramFloat);
      this.mAlphaAnimator.setPosition(paramFloat);
      return;
    }
    this.mNonfirstPageAnimator.setPosition(paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QSAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */