package com.android.systemui.statusbar;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

public abstract class ExpandableOutlineView
  extends ExpandableView
{
  private boolean mCustomOutline;
  private float mOutlineAlpha = -1.0F;
  private final Rect mOutlineRect = new Rect();
  ViewOutlineProvider mProvider = new ViewOutlineProvider()
  {
    public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
    {
      int i = (int)ExpandableOutlineView.this.getTranslation();
      if (!ExpandableOutlineView.-get0(ExpandableOutlineView.this)) {
        paramAnonymousOutline.setRect(i, ExpandableOutlineView.this.mClipTopAmount, ExpandableOutlineView.this.getWidth() + i, Math.max(ExpandableOutlineView.this.getActualHeight(), ExpandableOutlineView.this.mClipTopAmount));
      }
      for (;;)
      {
        paramAnonymousOutline.setAlpha(ExpandableOutlineView.-get1(ExpandableOutlineView.this));
        return;
        paramAnonymousOutline.setRect(ExpandableOutlineView.-get2(ExpandableOutlineView.this));
      }
    }
  };
  
  public ExpandableOutlineView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setOutlineProvider(this.mProvider);
  }
  
  public float getOutlineAlpha()
  {
    return this.mOutlineAlpha;
  }
  
  public int getOutlineTranslation()
  {
    if (this.mCustomOutline) {
      return this.mOutlineRect.left;
    }
    return (int)getTranslation();
  }
  
  public void setActualHeight(int paramInt, boolean paramBoolean)
  {
    super.setActualHeight(paramInt, paramBoolean);
    invalidateOutline();
  }
  
  public void setClipTopAmount(int paramInt)
  {
    super.setClipTopAmount(paramInt);
    invalidateOutline();
  }
  
  protected void setOutlineAlpha(float paramFloat)
  {
    if (paramFloat != this.mOutlineAlpha)
    {
      this.mOutlineAlpha = paramFloat;
      invalidateOutline();
    }
  }
  
  protected void setOutlineRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.mCustomOutline = true;
    setClipToOutline(true);
    this.mOutlineRect.set((int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4);
    this.mOutlineRect.bottom = ((int)Math.max(paramFloat2, this.mOutlineRect.bottom));
    this.mOutlineRect.right = ((int)Math.max(paramFloat1, this.mOutlineRect.right));
    invalidateOutline();
  }
  
  protected void setOutlineRect(RectF paramRectF)
  {
    if (paramRectF != null)
    {
      setOutlineRect(paramRectF.left, paramRectF.top, paramRectF.right, paramRectF.bottom);
      return;
    }
    this.mCustomOutline = false;
    setClipToOutline(false);
    invalidateOutline();
  }
  
  public void updateOutline()
  {
    if (this.mCustomOutline) {
      return;
    }
    boolean bool = true;
    if (isChildInGroup()) {
      if ((!isGroupExpanded()) || (isGroupExpansionChanging()))
      {
        bool = false;
        if (!bool) {
          break label80;
        }
      }
    }
    label80:
    for (ViewOutlineProvider localViewOutlineProvider = this.mProvider;; localViewOutlineProvider = null)
    {
      setOutlineProvider(localViewOutlineProvider);
      return;
      bool = true;
      break;
      if (!isSummaryWithChildren()) {
        break;
      }
      if (isGroupExpanded())
      {
        bool = isGroupExpansionChanging();
        break;
      }
      bool = true;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\ExpandableOutlineView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */