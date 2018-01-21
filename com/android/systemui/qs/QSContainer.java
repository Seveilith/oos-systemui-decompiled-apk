package com.android.systemui.qs;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.systemui.Interpolators;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.statusbar.phone.BaseStatusBarHeader;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import com.android.systemui.statusbar.phone.QSTileHost;
import com.android.systemui.util.ThemeColorUtils;

public class QSContainer
  extends FrameLayout
{
  private final Animator.AnimatorListener mAnimateHeaderSlidingInListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      QSContainer.-set0(QSContainer.this, false);
      QSContainer.-wrap0(QSContainer.this);
    }
  };
  private long mDelay;
  protected BaseStatusBarHeader mHeader;
  private boolean mHeaderAnimating;
  private int mHeightOverride = -1;
  private boolean mKeyguardShowing;
  private boolean mListening;
  private int mOrientation = 0;
  private ViewOutlineProvider mOutline;
  private NotificationPanelView mPanelView;
  private QSAnimator mQSAnimator;
  private QSCustomizer mQSCustomizer;
  private QSDetail mQSDetail;
  protected QSPanel mQSPanel;
  private final Rect mQsBounds = new Rect();
  private boolean mQsExpanded;
  protected float mQsExpansion;
  private final Point mSizePoint = new Point();
  ViewOutlineProvider mSmallOutline = new ViewOutlineProvider()
  {
    private int mDiff = 12;
    
    public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
    {
      paramAnonymousOutline.setRect(QSContainer.this.getLeft(), QSContainer.this.getTop(), QSContainer.this.getRight(), QSContainer.this.getBottom() - this.mDiff);
    }
  };
  private boolean mStackScrollerOverscrolling;
  private final ViewTreeObserver.OnPreDrawListener mStartHeaderSlidingIn = new ViewTreeObserver.OnPreDrawListener()
  {
    public boolean onPreDraw()
    {
      QSContainer.this.getViewTreeObserver().removeOnPreDrawListener(this);
      QSContainer.this.animate().translationY(0.0F).setStartDelay(QSContainer.-get1(QSContainer.this)).setDuration(448L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setListener(QSContainer.-get0(QSContainer.this)).start();
      QSContainer.this.setY(-QSContainer.this.mHeader.getHeight());
      return true;
    }
  };
  
  public QSContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void updateBottom()
  {
    int i = calculateContainerHeight();
    setBottom(getTop() + i);
    this.mQSDetail.setBottom(getTop() + i);
  }
  
  private void updateQsState()
  {
    int j = 0;
    boolean bool2;
    Object localObject;
    label73:
    boolean bool1;
    if ((!this.mQsExpanded) && (!this.mStackScrollerOverscrolling))
    {
      bool2 = this.mHeaderAnimating;
      this.mQSPanel.setExpanded(this.mQsExpanded);
      this.mQSDetail.setExpanded(this.mQsExpanded);
      localObject = this.mHeader;
      if ((!this.mQsExpanded) && (this.mKeyguardShowing) && (!this.mHeaderAnimating)) {
        break label147;
      }
      i = 0;
      ((BaseStatusBarHeader)localObject).setVisibility(i);
      localObject = this.mHeader;
      if ((this.mKeyguardShowing) && (!this.mHeaderAnimating)) {
        break label152;
      }
      if ((this.mQsExpanded) && (!this.mStackScrollerOverscrolling)) {
        break label157;
      }
      bool1 = false;
      label115:
      ((BaseStatusBarHeader)localObject).setExpanded(bool1);
      localObject = this.mQSPanel;
      if (!bool2) {
        break label162;
      }
    }
    label147:
    label152:
    label157:
    label162:
    for (int i = j;; i = 4)
    {
      ((QSPanel)localObject).setVisibility(i);
      return;
      bool2 = true;
      break;
      i = 4;
      break label73;
      bool1 = true;
      break label115;
      bool1 = true;
      break label115;
    }
  }
  
  public void animateHeaderSlidingIn(long paramLong)
  {
    if (!this.mQsExpanded)
    {
      this.mHeaderAnimating = true;
      this.mDelay = paramLong;
      getViewTreeObserver().addOnPreDrawListener(this.mStartHeaderSlidingIn);
    }
  }
  
  public void animateHeaderSlidingOut()
  {
    this.mHeaderAnimating = true;
    animate().y(-this.mHeader.getHeight()).setStartDelay(0L).setDuration(360L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        QSContainer.this.animate().setListener(null);
        QSContainer.-set0(QSContainer.this, false);
        QSContainer.-wrap0(QSContainer.this);
      }
    }).start();
  }
  
  protected int calculateContainerHeight()
  {
    if (this.mHeightOverride != -1) {}
    for (int i = this.mHeightOverride; this.mQSCustomizer.isCustomizing(); i = getMeasuredHeight()) {
      return this.mQSCustomizer.getHeight();
    }
    return (int)(this.mQsExpansion * (i - this.mHeader.getCollapsedHeight())) + this.mHeader.getCollapsedHeight();
  }
  
  public QSCustomizer getCustomizer()
  {
    return this.mQSCustomizer;
  }
  
  public int getDesiredHeight()
  {
    if (isCustomizing()) {
      return getHeight();
    }
    if (this.mQSDetail.isClosingDetail())
    {
      int i = ((FrameLayout.LayoutParams)this.mQSPanel.getLayoutParams()).topMargin;
      int j = this.mQSPanel.getMeasuredHeight();
      return getPaddingBottom() + (i + j);
    }
    return getMeasuredHeight();
  }
  
  public BaseStatusBarHeader getHeader()
  {
    return this.mHeader;
  }
  
  public int getQsMinExpansionHeight()
  {
    return this.mHeader.getHeight();
  }
  
  public QSPanel getQsPanel()
  {
    return this.mQSPanel;
  }
  
  public void hideImmediately()
  {
    animate().cancel();
    setY(-this.mHeader.getHeight());
  }
  
  public boolean isCustomizing()
  {
    return this.mQSCustomizer.isCustomizing();
  }
  
  public boolean isShowingDetail()
  {
    if (!this.mQSPanel.isShowingCustomize()) {
      return this.mQSDetail.isShowingDetail();
    }
    return true;
  }
  
  public void notifyCustomizeChanged()
  {
    int j = 0;
    updateBottom();
    Object localObject = this.mQSPanel;
    if (!this.mQSCustomizer.isCustomizing())
    {
      i = 0;
      ((QSPanel)localObject).setVisibility(i);
      localObject = this.mHeader;
      if (this.mQSCustomizer.isCustomizing()) {
        break label63;
      }
    }
    label63:
    for (int i = j;; i = 4)
    {
      ((BaseStatusBarHeader)localObject).setVisibility(i);
      this.mPanelView.onQsHeightChanged();
      return;
      i = 4;
      break;
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.mOrientation != paramConfiguration.orientation)
    {
      this.mOrientation = paramConfiguration.orientation;
      getDisplay().getRealSize(this.mSizePoint);
    }
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mQSPanel = ((QSPanel)findViewById(2131952157));
    this.mQSDetail = ((QSDetail)findViewById(2131952158));
    this.mHeader = ((BaseStatusBarHeader)findViewById(2131952168));
    this.mQSDetail.setQsPanel(this.mQSPanel, this.mHeader);
    this.mQSAnimator = new QSAnimator(this, (QuickQSPanel)this.mHeader.findViewById(2131952177), this.mQSPanel);
    this.mQSCustomizer = ((QSCustomizer)findViewById(2131952159));
    this.mQSCustomizer.setQsContainer(this);
    updateThemeColor();
    this.mOutline = getOutlineProvider();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    updateBottom();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.mQSPanel.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt2), 0));
    paramInt2 = this.mQSPanel.getMeasuredWidth();
    int i = ((FrameLayout.LayoutParams)this.mQSPanel.getLayoutParams()).topMargin;
    int j = this.mQSPanel.getMeasuredHeight();
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(i + j, 1073741824));
    if (this.mSizePoint.y == 0) {
      getDisplay().getRealSize(this.mSizePoint);
    }
    this.mQSCustomizer.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(this.mSizePoint.y, 1073741824));
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    this.mQSAnimator.onRtlChanged();
  }
  
  public void setExpanded(boolean paramBoolean)
  {
    this.mQsExpanded = paramBoolean;
    QSPanel localQSPanel = this.mQSPanel;
    if (this.mListening) {}
    for (paramBoolean = this.mQsExpanded;; paramBoolean = false)
    {
      localQSPanel.setListening(paramBoolean);
      updateQsState();
      return;
    }
  }
  
  public void setHeaderClickable(boolean paramBoolean)
  {
    this.mHeader.setClickable(paramBoolean);
  }
  
  public void setHeaderListening(boolean paramBoolean)
  {
    this.mHeader.setListening(paramBoolean);
  }
  
  public void setHeightOverride(int paramInt)
  {
    this.mHeightOverride = paramInt;
    updateBottom();
  }
  
  public void setHost(QSTileHost paramQSTileHost)
  {
    this.mQSPanel.setHost(paramQSTileHost, this.mQSCustomizer);
    this.mHeader.setQSPanel(this.mQSPanel);
    this.mQSDetail.setHost(paramQSTileHost);
    this.mQSAnimator.setHost(paramQSTileHost);
  }
  
  public void setKeyguardShowing(boolean paramBoolean)
  {
    this.mKeyguardShowing = paramBoolean;
    this.mQSAnimator.setOnKeyguard(paramBoolean);
    updateQsState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    this.mListening = paramBoolean;
    this.mHeader.setListening(paramBoolean);
    QSPanel localQSPanel = this.mQSPanel;
    if (this.mListening) {}
    for (paramBoolean = this.mQsExpanded;; paramBoolean = false)
    {
      localQSPanel.setListening(paramBoolean);
      return;
    }
  }
  
  public void setOverscrolling(boolean paramBoolean)
  {
    this.mStackScrollerOverscrolling = paramBoolean;
    updateQsState();
  }
  
  public void setPanelView(NotificationPanelView paramNotificationPanelView)
  {
    this.mPanelView = paramNotificationPanelView;
  }
  
  public void setQsExpansion(float paramFloat1, float paramFloat2)
  {
    this.mQsExpansion = paramFloat1;
    float f = paramFloat1 - 1.0F;
    if (!this.mHeaderAnimating)
    {
      if (this.mKeyguardShowing) {
        paramFloat2 = f * this.mHeader.getHeight();
      }
      setTranslationY(paramFloat2);
    }
    Object localObject = this.mHeader;
    if (this.mKeyguardShowing)
    {
      paramFloat2 = 1.0F;
      ((BaseStatusBarHeader)localObject).setExpansion(paramFloat2);
      this.mQSPanel.setTranslationY(this.mQSPanel.getHeight() * f);
      localObject = this.mQSDetail;
      if (paramFloat1 != 1.0F) {
        break label176;
      }
    }
    label176:
    for (boolean bool = true;; bool = false)
    {
      ((QSDetail)localObject).setFullyExpanded(bool);
      this.mQSAnimator.setPosition(paramFloat1);
      updateBottom();
      this.mQsBounds.top = ((int)((1.0F - paramFloat1) * this.mQSPanel.getHeight()));
      this.mQsBounds.right = this.mQSPanel.getWidth();
      this.mQsBounds.bottom = this.mQSPanel.getHeight();
      this.mQSPanel.setClipBounds(this.mQsBounds);
      return;
      paramFloat2 = paramFloat1;
      break;
    }
  }
  
  public void setShadow(boolean paramBoolean)
  {
    if ((!paramBoolean) && (this.mPanelView.hasNotification()))
    {
      setOutlineProvider(this.mSmallOutline);
      return;
    }
    setOutlineProvider(this.mOutline);
  }
  
  protected void updateThemeColor()
  {
    setBackgroundTintList(ColorStateList.valueOf(ThemeColorUtils.getColor(ThemeColorUtils.QS_SYSTEM_PRIMARY)));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QSContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */