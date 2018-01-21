package com.android.systemui.qs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import android.widget.Switch;

public class QSTileBaseView
  extends LinearLayout
{
  private String mAccessibilityClass;
  private boolean mCollapsedView;
  private final H mHandler = new H();
  private QSIconView mIcon;
  private RippleDrawable mRipple;
  private Drawable mTileBackground;
  private boolean mTileState;
  
  public QSTileBaseView(Context paramContext, QSIconView paramQSIconView, boolean paramBoolean)
  {
    super(paramContext);
    this.mIcon = paramQSIconView;
    addView(this.mIcon);
    this.mTileBackground = newTileBackground();
    if ((this.mTileBackground instanceof RippleDrawable))
    {
      ((RippleDrawable)this.mTileBackground).setColor(ColorStateList.valueOf(paramContext.getColor(2131493102)));
      setRipple((RippleDrawable)this.mTileBackground);
    }
    setImportantForAccessibility(1);
    setBackground(this.mTileBackground);
    int i = paramContext.getResources().getDimensionPixelSize(2131755412);
    setPadding(0, i, 0, i);
    setClipChildren(false);
    setClipToPadding(false);
    this.mCollapsedView = paramBoolean;
    setFocusable(true);
  }
  
  private Drawable newTileBackground()
  {
    TypedArray localTypedArray = this.mContext.obtainStyledAttributes(new int[] { 16843868 });
    Drawable localDrawable = localTypedArray.getDrawable(0);
    localTypedArray.recycle();
    return localDrawable;
  }
  
  private void setRipple(RippleDrawable paramRippleDrawable)
  {
    this.mRipple = paramRippleDrawable;
    if (getWidth() != 0) {
      updateRippleSize(getWidth(), getHeight());
    }
  }
  
  private void updateRippleSize(int paramInt1, int paramInt2)
  {
    paramInt1 /= 2;
    paramInt2 /= 2;
    int i = (int)(this.mIcon.getHeight() * 0.85F);
    this.mRipple.setHotspotBounds(paramInt1 - i, paramInt2 - i, paramInt1 + i, paramInt2 + i);
  }
  
  public QSIconView getIcon()
  {
    return this.mIcon;
  }
  
  protected void handleStateChanged(QSTile.State paramState)
  {
    this.mIcon.setIcon(paramState);
    if ((!this.mCollapsedView) || (TextUtils.isEmpty(paramState.minimalContentDescription)))
    {
      setContentDescription(paramState.contentDescription);
      if (!this.mCollapsedView) {
        break label78;
      }
    }
    label78:
    for (this.mAccessibilityClass = paramState.minimalAccessibilityClassName;; this.mAccessibilityClass = paramState.expandedAccessibilityClassName)
    {
      if ((paramState instanceof QSTile.BooleanState)) {
        this.mTileState = ((QSTile.BooleanState)paramState).value;
      }
      return;
      setContentDescription(paramState.minimalContentDescription);
      break;
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void init(View.OnClickListener paramOnClickListener, View.OnLongClickListener paramOnLongClickListener)
  {
    setClickable(true);
    setOnClickListener(paramOnClickListener);
    setOnLongClickListener(paramOnLongClickListener);
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    int i;
    if (!TextUtils.isEmpty(this.mAccessibilityClass))
    {
      paramAccessibilityEvent.setClassName(this.mAccessibilityClass);
      if (Switch.class.getName().equals(this.mAccessibilityClass))
      {
        Resources localResources = getResources();
        if (this.mTileState) {
          break label79;
        }
        i = 2131690582;
        paramAccessibilityEvent.setContentDescription(localResources.getString(i));
        if (!this.mTileState) {
          break label85;
        }
      }
    }
    label79:
    label85:
    for (boolean bool = false;; bool = true)
    {
      paramAccessibilityEvent.setChecked(bool);
      return;
      i = 2131690583;
      break;
    }
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    Resources localResources;
    if (!TextUtils.isEmpty(this.mAccessibilityClass))
    {
      paramAccessibilityNodeInfo.setClassName(this.mAccessibilityClass);
      if (Switch.class.getName().equals(this.mAccessibilityClass))
      {
        localResources = getResources();
        if (!this.mTileState) {
          break label76;
        }
      }
    }
    label76:
    for (int i = 2131690582;; i = 2131690583)
    {
      paramAccessibilityNodeInfo.setText(localResources.getString(i));
      paramAccessibilityNodeInfo.setChecked(this.mTileState);
      paramAccessibilityNodeInfo.setCheckable(true);
      return;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    paramInt1 = getMeasuredWidth();
    paramInt2 = getMeasuredHeight();
    if (this.mRipple != null) {
      updateRippleSize(paramInt1, paramInt2);
    }
  }
  
  public void onStateChanged(QSTile.State paramState)
  {
    this.mHandler.obtainMessage(1, paramState).sendToTarget();
  }
  
  public View updateAccessibilityOrder(View paramView)
  {
    setAccessibilityTraversalAfter(paramView.getId());
    return this;
  }
  
  private class H
    extends Handler
  {
    public H()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (paramMessage.what == 1) {
        QSTileBaseView.this.handleStateChanged((QSTile.State)paramMessage.obj);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QSTileBaseView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */