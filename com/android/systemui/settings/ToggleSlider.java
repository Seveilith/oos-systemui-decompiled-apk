package com.android.systemui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.android.systemui.R.styleable;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;

public class ToggleSlider
  extends RelativeLayout
{
  private final CompoundButton.OnCheckedChangeListener mCheckListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      paramAnonymousCompoundButton = ToggleSlider.-get3(ToggleSlider.this);
      if (paramAnonymousBoolean) {}
      for (boolean bool = false;; bool = true)
      {
        paramAnonymousCompoundButton.setEnabled(bool);
        if (ToggleSlider.-get0(ToggleSlider.this) != null) {
          ToggleSlider.-get0(ToggleSlider.this).onChanged(ToggleSlider.this, ToggleSlider.-get5(ToggleSlider.this), paramAnonymousBoolean, ToggleSlider.-get3(ToggleSlider.this).getProgress(), false);
        }
        if (ToggleSlider.-get1(ToggleSlider.this) != null) {
          ToggleSlider.-get4(ToggleSlider.-get1(ToggleSlider.this)).setChecked(paramAnonymousBoolean);
        }
        return;
      }
    }
  };
  private TextView mLabel;
  private Listener mListener;
  private ToggleSlider mMirror;
  private BrightnessMirrorController mMirrorController;
  private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener()
  {
    public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      if (ToggleSlider.-get0(ToggleSlider.this) != null) {
        ToggleSlider.-get0(ToggleSlider.this).onChanged(ToggleSlider.this, ToggleSlider.-get5(ToggleSlider.this), ToggleSlider.-get4(ToggleSlider.this).isChecked(), paramAnonymousInt, false);
      }
    }
    
    public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar)
    {
      ToggleSlider.-set0(ToggleSlider.this, true);
      if (ToggleSlider.-get0(ToggleSlider.this) != null) {
        ToggleSlider.-get0(ToggleSlider.this).onChanged(ToggleSlider.this, ToggleSlider.-get5(ToggleSlider.this), ToggleSlider.-get4(ToggleSlider.this).isChecked(), ToggleSlider.-get3(ToggleSlider.this).getProgress(), false);
      }
      ToggleSlider.-get4(ToggleSlider.this).setChecked(false);
      if (ToggleSlider.-get2(ToggleSlider.this) != null)
      {
        ToggleSlider.-get2(ToggleSlider.this).showMirror();
        ToggleSlider.-get2(ToggleSlider.this).setLocation((View)ToggleSlider.this.getParent());
      }
    }
    
    public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar)
    {
      ToggleSlider.-set0(ToggleSlider.this, false);
      if (ToggleSlider.-get0(ToggleSlider.this) != null) {
        ToggleSlider.-get0(ToggleSlider.this).onChanged(ToggleSlider.this, ToggleSlider.-get5(ToggleSlider.this), ToggleSlider.-get4(ToggleSlider.this).isChecked(), ToggleSlider.-get3(ToggleSlider.this).getProgress(), true);
      }
      if (ToggleSlider.-get2(ToggleSlider.this) != null) {
        ToggleSlider.-get2(ToggleSlider.this).hideMirror();
      }
    }
  };
  private ToggleSeekBar mSlider;
  private CompoundButton mToggle;
  private boolean mTracking;
  
  public ToggleSlider(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ToggleSlider(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ToggleSlider(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    View.inflate(paramContext, 2130968828, this);
    paramContext.getResources();
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ToggleSlider, paramInt, 0);
    this.mToggle = ((CompoundButton)findViewById(2131952297));
    this.mToggle.setOnCheckedChangeListener(this.mCheckListener);
    this.mSlider = ((ToggleSeekBar)findViewById(2131952298));
    this.mSlider.setOnSeekBarChangeListener(this.mSeekListener);
    this.mLabel = ((TextView)findViewById(2131951937));
    this.mLabel.setText(paramContext.getString(0));
    this.mSlider.setAccessibilityLabel(getContentDescription().toString());
    paramContext.recycle();
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mMirror != null)
    {
      MotionEvent localMotionEvent = paramMotionEvent.copy();
      this.mMirror.dispatchTouchEvent(localMotionEvent);
      localMotionEvent.recycle();
    }
    return super.dispatchTouchEvent(paramMotionEvent);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mListener != null) {
      this.mListener.onInit(this);
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.mToggle.setChecked(paramBoolean);
  }
  
  public void setMax(int paramInt)
  {
    this.mSlider.setMax(paramInt);
    if (this.mMirror != null) {
      this.mMirror.setMax(paramInt);
    }
  }
  
  public void setMirror(ToggleSlider paramToggleSlider)
  {
    this.mMirror = paramToggleSlider;
    if (this.mMirror != null)
    {
      this.mMirror.setChecked(this.mToggle.isChecked());
      this.mMirror.setMax(this.mSlider.getMax());
      this.mMirror.setValue(this.mSlider.getProgress());
    }
  }
  
  public void setMirrorController(BrightnessMirrorController paramBrightnessMirrorController)
  {
    this.mMirrorController = paramBrightnessMirrorController;
  }
  
  public void setOnChangedListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }
  
  public void setValue(int paramInt)
  {
    this.mSlider.setProgress(paramInt);
    if (this.mMirror != null) {
      this.mMirror.setValue(paramInt);
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onChanged(ToggleSlider paramToggleSlider, boolean paramBoolean1, boolean paramBoolean2, int paramInt, boolean paramBoolean3);
    
    public abstract void onInit(ToggleSlider paramToggleSlider);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\settings\ToggleSlider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */