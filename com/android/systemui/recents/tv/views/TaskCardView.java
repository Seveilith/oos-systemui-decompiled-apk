package com.android.systemui.recents.tv.views;

import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.tv.RecentsTvActivity;
import com.android.systemui.recents.tv.animations.DismissAnimationsHolder;
import com.android.systemui.recents.tv.animations.RecentsRowFocusAnimationHolder;
import com.android.systemui.recents.tv.animations.ViewFocusAnimator;

public class TaskCardView
  extends LinearLayout
{
  private ImageView mBadgeView;
  private int mCornerRadius;
  private DismissAnimationsHolder mDismissAnimationsHolder;
  private View mDismissIconView;
  private boolean mDismissState = false;
  private View mInfoFieldView;
  private RecentsRowFocusAnimationHolder mRecentsRowFocusAnimationHolder;
  private Task mTask;
  private View mThumbnailView;
  private TextView mTitleTextView;
  private boolean mTouchExplorationEnabled;
  private ViewFocusAnimator mViewFocusAnimator;
  
  public TaskCardView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TaskCardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TaskCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setLayoutDirection(getResources().getConfiguration().getLayoutDirection());
  }
  
  public static int getNumberOfVisibleTasks(Context paramContext)
  {
    Resources localResources = paramContext.getResources();
    paramContext = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
    Point localPoint = new Point();
    paramContext.getSize(localPoint);
    int i = localPoint.x;
    int j = localResources.getDimensionPixelSize(2131755642);
    int k = localResources.getDimensionPixelSize(2131755656);
    return (int)(Math.ceil(i / (j + k * 2.0D)) + 1.0D);
  }
  
  public static Rect getStartingCardThumbnailRect(Context paramContext, boolean paramBoolean, int paramInt)
  {
    if (paramInt > 1) {
      return getStartingCardThumbnailRectForStartPosition(paramContext, paramBoolean);
    }
    return getStartingCardThumbnailRectForFocusedPosition(paramContext, paramBoolean);
  }
  
  private static Rect getStartingCardThumbnailRectForFocusedPosition(Context paramContext, boolean paramBoolean)
  {
    Object localObject = paramContext.getResources();
    TypedValue localTypedValue = new TypedValue();
    ((Resources)localObject).getValue(2131624036, localTypedValue, true);
    if (paramBoolean) {}
    for (float f = localTypedValue.getFloat();; f = 1.0F)
    {
      int i = ((Resources)localObject).getDimensionPixelOffset(2131755642);
      int j = (int)(i * f - i);
      int k = ((Resources)localObject).getDimensionPixelOffset(2131755643);
      int i1 = (int)(k * f - k);
      int m = ((Resources)localObject).getDimensionPixelOffset(2131755654);
      int n = ((Resources)localObject).getDimensionPixelOffset(2131755644) + ((Resources)localObject).getDimensionPixelOffset(2131755650);
      int i2 = (int)(n * f - n);
      int i3 = ((Resources)localObject).getDimensionPixelOffset(2131755664) + ((Resources)localObject).getDimensionPixelOffset(2131755665) + ((Resources)localObject).getDimensionPixelOffset(2131755663) + ((Resources)localObject).getDimensionPixelOffset(2131755666);
      i1 = i1 + i2 + (int)(i3 * f - i3);
      paramContext = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
      localObject = new Point();
      paramContext.getSize((Point)localObject);
      i2 = ((Point)localObject).x;
      return new Rect(i2 / 2 - i / 2 - j / 2, m - i1 / 2 + (int)(n * f), i2 / 2 + i / 2 + j / 2, m - i1 / 2 + (int)(n * f) + (int)(k * f));
    }
  }
  
  private static Rect getStartingCardThumbnailRectForStartPosition(Context paramContext, boolean paramBoolean)
  {
    Object localObject = paramContext.getResources();
    int k = ((Resources)localObject).getDimensionPixelOffset(2131755642);
    int j = ((Resources)localObject).getDimensionPixelOffset(2131755656) * 2;
    int i = j;
    if (paramBoolean) {
      i = j + ((Resources)localObject).getDimensionPixelOffset(2131755657);
    }
    j = ((Resources)localObject).getDimensionPixelOffset(2131755643);
    int m = ((Resources)localObject).getDimensionPixelOffset(2131755654);
    int n = ((Resources)localObject).getDimensionPixelOffset(2131755644) + ((Resources)localObject).getDimensionPixelOffset(2131755650);
    paramContext = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
    localObject = new Point();
    paramContext.getSize((Point)localObject);
    int i1 = ((Point)localObject).x;
    return new Rect(i1 / 2 + k / 2 + i, m + n, i1 / 2 + k / 2 + i + k, m + n + j);
  }
  
  private void setAsBannerView(Drawable paramDrawable, ImageView paramImageView)
  {
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)paramImageView.getLayoutParams();
    localLayoutParams.width = getResources().getDimensionPixelSize(2131755645);
    localLayoutParams.height = getResources().getDimensionPixelSize(2131755646);
    paramImageView.setLayoutParams(localLayoutParams);
    paramImageView.setImageDrawable(paramDrawable);
  }
  
  private void setAsIconView(Drawable paramDrawable, ImageView paramImageView)
  {
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)paramImageView.getLayoutParams();
    localLayoutParams.width = getResources().getDimensionPixelSize(2131755647);
    localLayoutParams.height = getResources().getDimensionPixelSize(2131755648);
    paramImageView.setLayoutParams(localLayoutParams);
    paramImageView.setImageDrawable(paramDrawable);
  }
  
  private void setAsScreenShotView(Bitmap paramBitmap, ImageView paramImageView)
  {
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)paramImageView.getLayoutParams();
    localLayoutParams.width = -1;
    localLayoutParams.height = -1;
    paramImageView.setLayoutParams(localLayoutParams);
    paramImageView.setClipToOutline(true);
    paramImageView.setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), TaskCardView.-get0(TaskCardView.this));
      }
    });
    paramImageView.setImageBitmap(paramBitmap);
  }
  
  private void setDismissState(boolean paramBoolean)
  {
    if (this.mDismissState != paramBoolean)
    {
      this.mDismissState = paramBoolean;
      if (!this.mTouchExplorationEnabled)
      {
        if (!paramBoolean) {
          break label32;
        }
        this.mDismissAnimationsHolder.startEnterAnimation();
      }
    }
    return;
    label32:
    this.mDismissAnimationsHolder.startExitAnimation();
  }
  
  private void setThumbnailView()
  {
    ImageView localImageView = (ImageView)findViewById(2131952208);
    PackageManager localPackageManager = getContext().getPackageManager();
    if (this.mTask.thumbnail != null)
    {
      setAsScreenShotView(this.mTask.thumbnail, localImageView);
      return;
    }
    Drawable localDrawable = null;
    try
    {
      if (this.mTask.key != null) {
        localDrawable = localPackageManager.getActivityBanner(this.mTask.key.baseIntent);
      }
      if (localDrawable != null)
      {
        setAsBannerView(localDrawable, localImageView);
        return;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e("TaskCardView", "Package not found : " + localNameNotFoundException);
      setAsIconView(this.mTask.icon, localImageView);
      return;
    }
    setAsIconView(this.mTask.icon, localImageView);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    switch (paramKeyEvent.getKeyCode())
    {
    }
    do
    {
      do
      {
        return super.dispatchKeyEvent(paramKeyEvent);
      } while ((isInDismissState()) || (paramKeyEvent.getAction() != 0));
      setDismissState(true);
      return true;
      if (paramKeyEvent.getAction() == 0)
      {
        if (isInDismissState()) {
          setDismissState(false);
        }
      }
      else {
        return true;
      }
      ((RecentsTvActivity)getContext()).requestPipControlsFocus();
      return true;
    } while (!isInDismissState());
    return true;
  }
  
  public View getDismissIconView()
  {
    return this.mDismissIconView;
  }
  
  public void getFocusedRect(Rect paramRect)
  {
    this.mThumbnailView.getFocusedRect(paramRect);
  }
  
  public Rect getFocusedThumbnailRect()
  {
    Rect localRect = new Rect();
    this.mThumbnailView.getGlobalVisibleRect(localRect);
    return localRect;
  }
  
  public View getInfoFieldView()
  {
    return this.mInfoFieldView;
  }
  
  public RecentsRowFocusAnimationHolder getRecentsRowFocusAnimationHolder()
  {
    return this.mRecentsRowFocusAnimationHolder;
  }
  
  public Task getTask()
  {
    return this.mTask;
  }
  
  public View getThumbnailView()
  {
    return this.mThumbnailView;
  }
  
  public ViewFocusAnimator getViewFocusAnimator()
  {
    return this.mViewFocusAnimator;
  }
  
  public void init(Task paramTask)
  {
    this.mTask = paramTask;
    this.mTitleTextView.setText(paramTask.title);
    this.mBadgeView.setImageDrawable(paramTask.icon);
    setThumbnailView();
    setContentDescription(paramTask.titleDescription);
    this.mDismissState = false;
    this.mDismissAnimationsHolder.reset();
    this.mRecentsRowFocusAnimationHolder.reset();
  }
  
  public boolean isInDismissState()
  {
    return this.mDismissState;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mThumbnailView = findViewById(2131952207);
    this.mInfoFieldView = findViewById(2131952204);
    this.mTitleTextView = ((TextView)findViewById(2131952206));
    this.mBadgeView = ((ImageView)findViewById(2131952205));
    this.mDismissIconView = findViewById(2131952209);
    this.mDismissAnimationsHolder = new DismissAnimationsHolder(this);
    this.mCornerRadius = getResources().getDimensionPixelSize(2131755614);
    this.mRecentsRowFocusAnimationHolder = new RecentsRowFocusAnimationHolder(this, this.mInfoFieldView);
    this.mTouchExplorationEnabled = Recents.getSystemServices().isTouchExplorationEnabled();
    if (!this.mTouchExplorationEnabled) {
      this.mDismissIconView.setVisibility(0);
    }
    for (;;)
    {
      this.mViewFocusAnimator = new ViewFocusAnimator(this);
      return;
      this.mDismissIconView.setVisibility(8);
    }
  }
  
  public void startDismissTaskAnimation(Animator.AnimatorListener paramAnimatorListener)
  {
    this.mDismissState = false;
    this.mDismissAnimationsHolder.startDismissAnimation(paramAnimatorListener);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\views\TaskCardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */