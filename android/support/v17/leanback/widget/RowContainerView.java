package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

final class RowContainerView
  extends LinearLayout
{
  private Drawable mForeground;
  private boolean mForegroundBoundsChanged = true;
  private ViewGroup mHeaderDock;
  
  public RowContainerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public RowContainerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setOrientation(1);
    LayoutInflater.from(paramContext).inflate(R.layout.lb_row_container, this);
    this.mHeaderDock = ((ViewGroup)findViewById(R.id.lb_row_container_header_dock));
    setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if (this.mForeground != null)
    {
      if (this.mForegroundBoundsChanged)
      {
        this.mForegroundBoundsChanged = false;
        this.mForeground.setBounds(0, 0, getWidth(), getHeight());
      }
      this.mForeground.draw(paramCanvas);
    }
  }
  
  public Drawable getForeground()
  {
    return this.mForeground;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mForegroundBoundsChanged = true;
  }
  
  public void setForeground(Drawable paramDrawable)
  {
    this.mForeground = paramDrawable;
    if (this.mForeground == null) {}
    for (boolean bool = true;; bool = false)
    {
      setWillNotDraw(bool);
      invalidate();
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\RowContainerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */