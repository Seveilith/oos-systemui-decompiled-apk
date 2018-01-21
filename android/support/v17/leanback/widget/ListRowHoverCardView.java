package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class ListRowHoverCardView
  extends LinearLayout
{
  private final TextView mDescriptionView;
  private final TextView mTitleView;
  
  public ListRowHoverCardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ListRowHoverCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    LayoutInflater.from(paramContext).inflate(R.layout.lb_list_row_hovercard, this);
    this.mTitleView = ((TextView)findViewById(R.id.title));
    this.mDescriptionView = ((TextView)findViewById(R.id.description));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\ListRowHoverCardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */