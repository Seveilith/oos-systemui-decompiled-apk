package com.android.systemui.statusbar.policy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.widget.FrameLayout;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.qs.tiles.UserDetailItemView;
import com.android.systemui.statusbar.phone.KeyguardStatusBarView;
import com.android.systemui.statusbar.phone.NotificationPanelView;

public class KeyguardUserSwitcher
{
  private final Adapter mAdapter;
  private boolean mAnimating;
  private final AppearAnimationUtils mAppearAnimationUtils;
  private final KeyguardUserSwitcherScrim mBackground;
  private ObjectAnimator mBgAnimator;
  public final DataSetObserver mDataSetObserver = new DataSetObserver()
  {
    public void onChanged()
    {
      KeyguardUserSwitcher.-wrap0(KeyguardUserSwitcher.this);
    }
  };
  private final KeyguardStatusBarView mStatusBarView;
  private ViewGroup mUserSwitcher;
  private final Container mUserSwitcherContainer;
  private UserSwitcherController mUserSwitcherController;
  
  public KeyguardUserSwitcher(Context paramContext, ViewStub paramViewStub, KeyguardStatusBarView paramKeyguardStatusBarView, NotificationPanelView paramNotificationPanelView, UserSwitcherController paramUserSwitcherController)
  {
    if (!paramContext.getResources().getBoolean(2131558429)) {}
    for (int i = 0; (paramUserSwitcherController != null) && (i != 0); i = 1)
    {
      this.mUserSwitcherContainer = ((Container)paramViewStub.inflate());
      this.mBackground = new KeyguardUserSwitcherScrim(paramContext);
      reinflateViews();
      this.mStatusBarView = paramKeyguardStatusBarView;
      this.mStatusBarView.setKeyguardUserSwitcher(this);
      paramNotificationPanelView.setKeyguardUserSwitcher(this);
      this.mAdapter = new Adapter(paramContext, paramUserSwitcherController, this);
      this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
      this.mUserSwitcherController = paramUserSwitcherController;
      this.mAppearAnimationUtils = new AppearAnimationUtils(paramContext, 400L, -0.5F, 0.5F, Interpolators.FAST_OUT_SLOW_IN);
      this.mUserSwitcherContainer.setKeyguardUserSwitcher(this);
      return;
    }
    this.mUserSwitcherContainer = null;
    this.mStatusBarView = null;
    this.mAdapter = null;
    this.mAppearAnimationUtils = null;
    this.mBackground = null;
  }
  
  private void cancelAnimations()
  {
    int j = this.mUserSwitcher.getChildCount();
    int i = 0;
    while (i < j)
    {
      this.mUserSwitcher.getChildAt(i).animate().cancel();
      i += 1;
    }
    if (this.mBgAnimator != null) {
      this.mBgAnimator.cancel();
    }
    this.mUserSwitcher.animate().cancel();
    this.mAnimating = false;
  }
  
  private void hide(boolean paramBoolean)
  {
    if ((this.mUserSwitcher != null) && (this.mUserSwitcherContainer.getVisibility() == 0))
    {
      cancelAnimations();
      if (!paramBoolean) {
        break label39;
      }
      startDisappearAnimation();
    }
    for (;;)
    {
      this.mStatusBarView.setKeyguardUserSwitcherShowing(false, paramBoolean);
      return;
      label39:
      this.mUserSwitcherContainer.setVisibility(8);
    }
  }
  
  private void refresh()
  {
    int j = this.mUserSwitcher.getChildCount();
    int k = this.mAdapter.getCount();
    int m = Math.max(j, k);
    int i = 0;
    if (i < m)
    {
      View localView1;
      View localView2;
      if (i < k)
      {
        localView1 = null;
        if (i < j) {
          localView1 = this.mUserSwitcher.getChildAt(i);
        }
        localView2 = this.mAdapter.getView(i, localView1, this.mUserSwitcher);
        if (localView1 == null) {
          this.mUserSwitcher.addView(localView2);
        }
      }
      for (;;)
      {
        i += 1;
        break;
        if (localView1 != localView2)
        {
          this.mUserSwitcher.removeViewAt(i);
          this.mUserSwitcher.addView(localView2, i);
          continue;
          int n = this.mUserSwitcher.getChildCount();
          this.mUserSwitcher.removeViewAt(n - 1);
        }
      }
    }
  }
  
  private void reinflateViews()
  {
    if (this.mUserSwitcher != null)
    {
      this.mUserSwitcher.setBackground(null);
      this.mUserSwitcher.removeOnLayoutChangeListener(this.mBackground);
    }
    this.mUserSwitcherContainer.removeAllViews();
    LayoutInflater.from(this.mUserSwitcherContainer.getContext()).inflate(2130968658, this.mUserSwitcherContainer);
    this.mUserSwitcher = ((ViewGroup)this.mUserSwitcherContainer.findViewById(2131951926));
    this.mUserSwitcher.addOnLayoutChangeListener(this.mBackground);
    this.mUserSwitcher.setBackground(this.mBackground);
  }
  
  private boolean shouldExpandByDefault()
  {
    if (this.mUserSwitcherController != null) {
      return this.mUserSwitcherController.isSimpleUserSwitcher();
    }
    return false;
  }
  
  private void startAppearAnimation()
  {
    int j = this.mUserSwitcher.getChildCount();
    View[] arrayOfView = new View[j];
    int i = 0;
    while (i < j)
    {
      arrayOfView[i] = this.mUserSwitcher.getChildAt(i);
      i += 1;
    }
    this.mUserSwitcher.setClipChildren(false);
    this.mUserSwitcher.setClipToPadding(false);
    this.mAppearAnimationUtils.startAnimation(arrayOfView, new Runnable()
    {
      public void run()
      {
        KeyguardUserSwitcher.-get0(KeyguardUserSwitcher.this).setClipChildren(true);
        KeyguardUserSwitcher.-get0(KeyguardUserSwitcher.this).setClipToPadding(true);
      }
    });
    this.mAnimating = true;
    this.mBgAnimator = ObjectAnimator.ofInt(this.mBackground, "alpha", new int[] { 0, 255 });
    this.mBgAnimator.setDuration(400L);
    this.mBgAnimator.setInterpolator(Interpolators.ALPHA_IN);
    this.mBgAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        KeyguardUserSwitcher.-set1(KeyguardUserSwitcher.this, null);
        KeyguardUserSwitcher.-set0(KeyguardUserSwitcher.this, false);
      }
    });
    this.mBgAnimator.start();
  }
  
  private void startDisappearAnimation()
  {
    this.mAnimating = true;
    this.mUserSwitcher.animate().alpha(0.0F).setDuration(300L).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new Runnable()
    {
      public void run()
      {
        KeyguardUserSwitcher.-get1(KeyguardUserSwitcher.this).setVisibility(8);
        KeyguardUserSwitcher.-get0(KeyguardUserSwitcher.this).setAlpha(1.0F);
        KeyguardUserSwitcher.-set0(KeyguardUserSwitcher.this, false);
      }
    });
  }
  
  public void hideIfNotSimple(boolean paramBoolean)
  {
    if ((this.mUserSwitcherContainer == null) || (this.mUserSwitcherController.isSimpleUserSwitcher())) {
      return;
    }
    hide(paramBoolean);
  }
  
  boolean isAnimating()
  {
    return this.mAnimating;
  }
  
  public void onDensityOrFontScaleChanged()
  {
    if (this.mUserSwitcherContainer != null)
    {
      reinflateViews();
      refresh();
    }
  }
  
  public void setKeyguard(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mUserSwitcher != null)
    {
      if ((paramBoolean1) && (shouldExpandByDefault())) {
        show(paramBoolean2);
      }
    }
    else {
      return;
    }
    hide(paramBoolean2);
  }
  
  public void show(boolean paramBoolean)
  {
    if ((this.mUserSwitcher != null) && (this.mUserSwitcherContainer.getVisibility() != 0))
    {
      cancelAnimations();
      this.mAdapter.refresh();
      this.mUserSwitcherContainer.setVisibility(0);
      this.mStatusBarView.setKeyguardUserSwitcherShowing(true, paramBoolean);
      if (paramBoolean) {
        startAppearAnimation();
      }
    }
  }
  
  public static class Adapter
    extends UserSwitcherController.BaseUserAdapter
    implements View.OnClickListener
  {
    private Context mContext;
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    
    public Adapter(Context paramContext, UserSwitcherController paramUserSwitcherController, KeyguardUserSwitcher paramKeyguardUserSwitcher)
    {
      super();
      this.mContext = paramContext;
      this.mKeyguardUserSwitcher = paramKeyguardUserSwitcher;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      UserSwitcherController.UserRecord localUserRecord = getItem(paramInt);
      String str;
      if (((paramView instanceof UserDetailItemView)) && ((paramView.getTag() instanceof UserSwitcherController.UserRecord)))
      {
        paramViewGroup = (UserDetailItemView)paramView;
        str = getName(this.mContext, localUserRecord);
        if (localUserRecord.picture != null) {
          break label122;
        }
        paramViewGroup.bind(str, getDrawable(this.mContext, localUserRecord).mutate(), localUserRecord.resolveId());
      }
      for (;;)
      {
        paramViewGroup.setAvatarEnabled(localUserRecord.isSwitchToEnabled);
        paramView.setActivated(localUserRecord.isCurrent);
        paramView.setTag(localUserRecord);
        return paramView;
        paramView = LayoutInflater.from(this.mContext).inflate(2130968659, paramViewGroup, false);
        paramView.setOnClickListener(this);
        break;
        label122:
        paramViewGroup.bind(str, localUserRecord.picture, localUserRecord.info.id);
      }
    }
    
    public void onClick(View paramView)
    {
      paramView = (UserSwitcherController.UserRecord)paramView.getTag();
      if ((!paramView.isCurrent) || (paramView.isGuest))
      {
        if (paramView.isSwitchToEnabled) {
          switchTo(paramView);
        }
        return;
      }
      this.mKeyguardUserSwitcher.hideIfNotSimple(true);
    }
  }
  
  public static class Container
    extends FrameLayout
  {
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    
    public Container(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      setClipChildren(false);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if ((this.mKeyguardUserSwitcher == null) || (this.mKeyguardUserSwitcher.isAnimating())) {}
      for (;;)
      {
        return false;
        this.mKeyguardUserSwitcher.hideIfNotSimple(true);
      }
    }
    
    public void setKeyguardUserSwitcher(KeyguardUserSwitcher paramKeyguardUserSwitcher)
    {
      this.mKeyguardUserSwitcher = paramKeyguardUserSwitcher;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\KeyguardUserSwitcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */