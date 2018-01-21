package com.android.systemui.tv.pip;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PipControlButtonView
  extends RelativeLayout
{
  private Animator mButtonFocusGainAnimator;
  private Animator mButtonFocusLossAnimator;
  ImageView mButtonImageView;
  private TextView mDescriptionTextView;
  private View.OnFocusChangeListener mFocusChangeListener;
  private ImageView mIconImageView;
  private final View.OnFocusChangeListener mInternalFocusChangeListener = new View.OnFocusChangeListener()
  {
    public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean) {
        PipControlButtonView.this.startFocusGainAnimation();
      }
      for (;;)
      {
        if (PipControlButtonView.-get0(PipControlButtonView.this) != null) {
          PipControlButtonView.-get0(PipControlButtonView.this).onFocusChange(PipControlButtonView.this, paramAnonymousBoolean);
        }
        return;
        PipControlButtonView.this.startFocusLossAnimation();
      }
    }
  };
  private Animator mTextFocusGainAnimator;
  private Animator mTextFocusLossAnimator;
  
  public PipControlButtonView(Context paramContext)
  {
    this(paramContext, null, 0, 0);
  }
  
  public PipControlButtonView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0, 0);
  }
  
  public PipControlButtonView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PipControlButtonView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130968834, this);
    this.mIconImageView = ((ImageView)findViewById(2131951747));
    this.mButtonImageView = ((ImageView)findViewById(2131951936));
    this.mDescriptionTextView = ((TextView)findViewById(2131952320));
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, new int[] { 16843033, 16843087 }, paramInt1, paramInt2);
    setImageResource(paramContext.getResourceId(0, 0));
    setText(paramContext.getResourceId(1, 0));
    paramContext.recycle();
  }
  
  private static void cancelAnimator(Animator paramAnimator)
  {
    if (paramAnimator.isStarted()) {
      paramAnimator.cancel();
    }
  }
  
  public void onFinishInflate()
  {
    super.onFinishInflate();
    this.mButtonImageView.setOnFocusChangeListener(this.mInternalFocusChangeListener);
    this.mTextFocusGainAnimator = AnimatorInflater.loadAnimator(getContext(), 2131034310);
    this.mTextFocusGainAnimator.setTarget(this.mDescriptionTextView);
    this.mButtonFocusGainAnimator = AnimatorInflater.loadAnimator(getContext(), 2131034310);
    this.mButtonFocusGainAnimator.setTarget(this.mButtonImageView);
    this.mTextFocusLossAnimator = AnimatorInflater.loadAnimator(getContext(), 2131034311);
    this.mTextFocusLossAnimator.setTarget(this.mDescriptionTextView);
    this.mButtonFocusLossAnimator = AnimatorInflater.loadAnimator(getContext(), 2131034311);
    this.mButtonFocusLossAnimator.setTarget(this.mButtonImageView);
  }
  
  public void reset()
  {
    float f = 1.0F;
    cancelAnimator(this.mButtonFocusGainAnimator);
    cancelAnimator(this.mTextFocusGainAnimator);
    cancelAnimator(this.mButtonFocusLossAnimator);
    cancelAnimator(this.mTextFocusLossAnimator);
    this.mButtonImageView.setAlpha(1.0F);
    TextView localTextView = this.mDescriptionTextView;
    if (this.mButtonImageView.hasFocus()) {}
    for (;;)
    {
      localTextView.setAlpha(f);
      return;
      f = 0.0F;
    }
  }
  
  public void setImageResource(int paramInt)
  {
    this.mIconImageView.setImageResource(paramInt);
  }
  
  public void setOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mButtonImageView.setOnClickListener(paramOnClickListener);
  }
  
  public void setOnFocusChangeListener(View.OnFocusChangeListener paramOnFocusChangeListener)
  {
    this.mFocusChangeListener = paramOnFocusChangeListener;
  }
  
  public void setText(int paramInt)
  {
    this.mButtonImageView.setContentDescription(getContext().getString(paramInt));
    this.mDescriptionTextView.setText(paramInt);
  }
  
  public void startFocusGainAnimation()
  {
    cancelAnimator(this.mButtonFocusLossAnimator);
    cancelAnimator(this.mTextFocusLossAnimator);
    this.mTextFocusGainAnimator.start();
    if (this.mButtonImageView.getAlpha() < 1.0F) {
      this.mButtonFocusGainAnimator.start();
    }
  }
  
  public void startFocusLossAnimation()
  {
    cancelAnimator(this.mButtonFocusGainAnimator);
    cancelAnimator(this.mTextFocusGainAnimator);
    this.mTextFocusLossAnimator.start();
    if (this.mButtonImageView.hasFocus()) {
      this.mButtonFocusLossAnimator.start();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tv\pip\PipControlButtonView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */