package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.R.style;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ImageCardView
  extends BaseCardView
{
  private boolean mAttachedToWindow;
  private ImageView mBadgeImage;
  private TextView mContentView;
  private ImageView mImageView;
  private ViewGroup mInfoArea;
  private TextView mTitleView;
  
  public ImageCardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.imageCardViewStyle);
  }
  
  public ImageCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    buildImageCardView(paramAttributeSet, paramInt, R.style.Widget_Leanback_ImageCardView);
  }
  
  private void buildImageCardView(AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    setFocusable(true);
    setFocusableInTouchMode(true);
    Object localObject = LayoutInflater.from(getContext());
    ((LayoutInflater)localObject).inflate(R.layout.lb_image_card_view, this);
    paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.lbImageCardView, paramInt1, paramInt2);
    int k = paramAttributeSet.getInt(R.styleable.lbImageCardView_lbImageCardViewType, 0);
    label70:
    int i;
    label81:
    int j;
    if (k == 0)
    {
      paramInt2 = 1;
      if ((k & 0x1) != 1) {
        break label178;
      }
      paramInt1 = 1;
      if ((k & 0x2) != 2) {
        break label183;
      }
      i = 1;
      if ((k & 0x4) != 4) {
        break label189;
      }
      j = 1;
      label92:
      if ((j != 0) || ((k & 0x8) != 8)) {
        break label195;
      }
    }
    label178:
    label183:
    label189:
    label195:
    for (k = 1;; k = 0)
    {
      this.mImageView = ((ImageView)findViewById(R.id.main_image));
      if (this.mImageView.getDrawable() == null) {
        this.mImageView.setVisibility(4);
      }
      this.mInfoArea = ((ViewGroup)findViewById(R.id.info_field));
      if (paramInt2 == 0) {
        break label201;
      }
      removeView(this.mInfoArea);
      paramAttributeSet.recycle();
      return;
      paramInt2 = 0;
      break;
      paramInt1 = 0;
      break label70;
      i = 0;
      break label81;
      j = 0;
      break label92;
    }
    label201:
    if (paramInt1 != 0)
    {
      this.mTitleView = ((TextView)((LayoutInflater)localObject).inflate(R.layout.lb_image_card_view_themed_title, this.mInfoArea, false));
      this.mInfoArea.addView(this.mTitleView);
    }
    if (i != 0)
    {
      this.mContentView = ((TextView)((LayoutInflater)localObject).inflate(R.layout.lb_image_card_view_themed_content, this.mInfoArea, false));
      this.mInfoArea.addView(this.mContentView);
    }
    if ((j != 0) || (k != 0))
    {
      paramInt2 = R.layout.lb_image_card_view_themed_badge_right;
      if (k != 0) {
        paramInt2 = R.layout.lb_image_card_view_themed_badge_left;
      }
      this.mBadgeImage = ((ImageView)((LayoutInflater)localObject).inflate(paramInt2, this.mInfoArea, false));
      this.mInfoArea.addView(this.mBadgeImage);
    }
    if ((paramInt1 == 0) || (i != 0))
    {
      if (i != 0)
      {
        localObject = (RelativeLayout.LayoutParams)this.mContentView.getLayoutParams();
        if (paramInt1 == 0) {
          ((RelativeLayout.LayoutParams)localObject).addRule(10);
        }
        if (k != 0)
        {
          ((RelativeLayout.LayoutParams)localObject).removeRule(16);
          ((RelativeLayout.LayoutParams)localObject).removeRule(20);
          ((RelativeLayout.LayoutParams)localObject).addRule(17, this.mBadgeImage.getId());
        }
        this.mContentView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
      if (this.mBadgeImage != null)
      {
        localObject = (RelativeLayout.LayoutParams)this.mBadgeImage.getLayoutParams();
        if (i == 0) {
          break label568;
        }
        ((RelativeLayout.LayoutParams)localObject).addRule(8, this.mContentView.getId());
      }
    }
    for (;;)
    {
      this.mBadgeImage.setLayoutParams((ViewGroup.LayoutParams)localObject);
      localObject = paramAttributeSet.getDrawable(R.styleable.lbImageCardView_infoAreaBackground);
      if (localObject != null) {
        setInfoAreaBackground((Drawable)localObject);
      }
      if ((this.mBadgeImage != null) && (this.mBadgeImage.getDrawable() == null)) {
        this.mBadgeImage.setVisibility(8);
      }
      paramAttributeSet.recycle();
      return;
      if (this.mBadgeImage == null) {
        break;
      }
      localObject = (RelativeLayout.LayoutParams)this.mTitleView.getLayoutParams();
      if (k != 0) {
        ((RelativeLayout.LayoutParams)localObject).addRule(17, this.mBadgeImage.getId());
      }
      for (;;)
      {
        this.mTitleView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        break;
        ((RelativeLayout.LayoutParams)localObject).addRule(16, this.mBadgeImage.getId());
      }
      label568:
      if (paramInt1 != 0) {
        ((RelativeLayout.LayoutParams)localObject).addRule(8, this.mTitleView.getId());
      }
    }
  }
  
  private void fadeIn()
  {
    this.mImageView.setAlpha(0.0F);
    if (this.mAttachedToWindow) {
      this.mImageView.animate().alpha(1.0F).setDuration(this.mImageView.getResources().getInteger(17694720));
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mAttachedToWindow = true;
    if (this.mImageView.getAlpha() == 0.0F) {
      fadeIn();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    this.mAttachedToWindow = false;
    this.mImageView.animate().cancel();
    this.mImageView.setAlpha(1.0F);
    super.onDetachedFromWindow();
  }
  
  public void setInfoAreaBackground(Drawable paramDrawable)
  {
    if (this.mInfoArea != null) {
      this.mInfoArea.setBackground(paramDrawable);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\ImageCardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */