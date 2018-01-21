package com.android.systemui.volume;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.Objects;

public class SegmentedButtons
  extends LinearLayout
{
  private static final Typeface MEDIUM = Typeface.create("sans-serif-medium", 0);
  private static final Typeface REGULAR = Typeface.create("sans-serif", 0);
  private Callback mCallback;
  private final View.OnClickListener mClick = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      SegmentedButtons.this.setSelectedValue(paramAnonymousView.getTag(), true);
    }
  };
  private final Context mContext;
  protected final LayoutInflater mInflater;
  protected Object mSelectedValue;
  private final SpTexts mSpTexts;
  
  public SegmentedButtons(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.mInflater = LayoutInflater.from(this.mContext);
    setOrientation(0);
    this.mSpTexts = new SpTexts(this.mContext);
  }
  
  private void fireInteraction()
  {
    if (this.mCallback != null) {
      this.mCallback.onInteraction();
    }
  }
  
  private void fireOnSelected(boolean paramBoolean)
  {
    if (this.mCallback != null) {
      this.mCallback.onSelected(this.mSelectedValue, paramBoolean);
    }
  }
  
  public void addButton(int paramInt1, int paramInt2, Object paramObject)
  {
    Button localButton = inflateButton();
    localButton.setTag(2131951937, Integer.valueOf(paramInt1));
    localButton.setText(paramInt1);
    localButton.setContentDescription(getResources().getString(paramInt2));
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localButton.getLayoutParams();
    if (getChildCount() == 0)
    {
      localLayoutParams.rightMargin = 0;
      localLayoutParams.leftMargin = 0;
    }
    localButton.setLayoutParams(localLayoutParams);
    addView(localButton);
    localButton.setTag(paramObject);
    localButton.setOnClickListener(this.mClick);
    Interaction.register(localButton, new Interaction.Callback()
    {
      public void onInteraction()
      {
        SegmentedButtons.-wrap0(SegmentedButtons.this);
      }
    });
    this.mSpTexts.add(localButton);
  }
  
  public Object getSelectedValue()
  {
    return this.mSelectedValue;
  }
  
  public Button inflateButton()
  {
    return (Button)this.mInflater.inflate(2130968811, this, false);
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  protected void setSelectedStyle(TextView paramTextView, boolean paramBoolean) {}
  
  public void setSelectedValue(Object paramObject, boolean paramBoolean)
  {
    if (Objects.equals(paramObject, this.mSelectedValue)) {
      return;
    }
    this.mSelectedValue = paramObject;
    int i = 0;
    while (i < getChildCount())
    {
      paramObject = (TextView)getChildAt(i);
      Object localObject = ((TextView)paramObject).getTag();
      boolean bool = Objects.equals(this.mSelectedValue, localObject);
      ((TextView)paramObject).setSelected(bool);
      setSelectedStyle((TextView)paramObject, bool);
      i += 1;
    }
    fireOnSelected(paramBoolean);
  }
  
  public void updateLocale()
  {
    int i = 0;
    while (i < getChildCount())
    {
      Button localButton = (Button)getChildAt(i);
      localButton.setText(((Integer)localButton.getTag(2131951937)).intValue());
      i += 1;
    }
  }
  
  public static abstract interface Callback
    extends Interaction.Callback
  {
    public abstract void onSelected(Object paramObject, boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\SegmentedButtons.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */