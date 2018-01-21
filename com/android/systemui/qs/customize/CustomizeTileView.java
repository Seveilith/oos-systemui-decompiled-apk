package com.android.systemui.qs.customize;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.qs.QSIconView;
import com.android.systemui.qs.QSTileView;
import libcore.util.Objects;

public class CustomizeTileView
  extends QSTileView
{
  private TextView mAppLabel;
  private int mLabelMaxLines;
  private int mLabelMinLines;
  
  public CustomizeTileView(Context paramContext, QSIconView paramQSIconView)
  {
    super(paramContext, paramQSIconView);
  }
  
  protected void createLabel()
  {
    super.createLabel();
    this.mLabelMinLines = this.mLabel.getMinLines();
    this.mLabelMaxLines = this.mLabel.getMaxLines();
    View localView = LayoutInflater.from(this.mContext).inflate(2130968780, null);
    this.mAppLabel = ((TextView)localView.findViewById(2131952160));
    this.mAppLabel.setAlpha(0.6F);
    this.mAppLabel.setSingleLine(true);
    this.mAppLabel.setTextColor(sTextColor);
    this.mLabel.setEllipsize(TextUtils.TruncateAt.END);
    this.mAppLabel.setEllipsize(TextUtils.TruncateAt.END);
    addView(localView);
  }
  
  public TextView getAppLabel()
  {
    return this.mAppLabel;
  }
  
  public void setAppLabel(CharSequence paramCharSequence)
  {
    if (!Objects.equal(paramCharSequence, this.mAppLabel.getText())) {
      this.mAppLabel.setText(paramCharSequence);
    }
  }
  
  public void setShowAppLabel(boolean paramBoolean)
  {
    TextView localTextView = this.mAppLabel;
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      localTextView.setVisibility(i);
      this.mLabel.setSingleLine(paramBoolean);
      if (!paramBoolean)
      {
        this.mLabel.setMinLines(this.mLabelMinLines);
        this.mLabel.setMaxLines(this.mLabelMaxLines);
      }
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\customize\CustomizeTileView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */