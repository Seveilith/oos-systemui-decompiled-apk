package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class TitleView
  extends FrameLayout
{
  private int flags = 6;
  private ImageView mBadgeView;
  private boolean mHasSearchListener = false;
  private SearchOrbView mSearchOrbView;
  private TextView mTextView;
  private final TitleViewAdapter mTitleViewAdapter = new TitleViewAdapter() {};
  
  public TitleView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TitleView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.browseTitleViewStyle);
  }
  
  public TitleView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = LayoutInflater.from(paramContext).inflate(R.layout.lb_title_view, this);
    this.mBadgeView = ((ImageView)paramContext.findViewById(R.id.title_badge));
    this.mTextView = ((TextView)paramContext.findViewById(R.id.title_text));
    this.mSearchOrbView = ((SearchOrbView)paramContext.findViewById(R.id.title_orb));
    setClipToPadding(false);
    setClipChildren(false);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\TitleView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */