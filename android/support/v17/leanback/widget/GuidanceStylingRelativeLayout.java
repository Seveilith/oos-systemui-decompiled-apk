package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

class GuidanceStylingRelativeLayout
  extends RelativeLayout
{
  private float mTitleKeylinePercent;
  
  public GuidanceStylingRelativeLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public GuidanceStylingRelativeLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public GuidanceStylingRelativeLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }
  
  private void init()
  {
    TypedArray localTypedArray = getContext().getTheme().obtainStyledAttributes(R.styleable.LeanbackGuidedStepTheme);
    this.mTitleKeylinePercent = localTypedArray.getFloat(R.styleable.LeanbackGuidedStepTheme_guidedStepKeyline, 40.0F);
    localTypedArray.recycle();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    TextView localTextView1 = (TextView)getRootView().findViewById(R.id.guidance_title);
    TextView localTextView2 = (TextView)getRootView().findViewById(R.id.guidance_breadcrumb);
    TextView localTextView3 = (TextView)getRootView().findViewById(R.id.guidance_description);
    ImageView localImageView = (ImageView)getRootView().findViewById(R.id.guidance_icon);
    paramInt1 = (int)(getMeasuredHeight() * this.mTitleKeylinePercent / 100.0F);
    if ((localTextView1 != null) && (localTextView1.getParent() == this))
    {
      paramInt2 = paramInt1 - -localTextView1.getPaint().getFontMetricsInt().top - localTextView2.getMeasuredHeight() - localTextView1.getPaddingTop() - localTextView2.getTop();
      if ((localTextView2 != null) && (localTextView2.getParent() == this)) {
        localTextView2.offsetTopAndBottom(paramInt2);
      }
      localTextView1.offsetTopAndBottom(paramInt2);
      if ((localTextView3 != null) && (localTextView3.getParent() == this)) {
        localTextView3.offsetTopAndBottom(paramInt2);
      }
    }
    if ((localImageView != null) && (localImageView.getParent() == this) && (localImageView.getDrawable() != null)) {
      localImageView.offsetTopAndBottom(paramInt1 - localImageView.getMeasuredHeight() / 2);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\GuidanceStylingRelativeLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */