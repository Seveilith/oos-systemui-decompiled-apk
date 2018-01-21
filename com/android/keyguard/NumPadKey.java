package com.android.keyguard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;

public class NumPadKey
  extends ViewGroup
{
  static String[] sKlondike;
  private int mDigit = -1;
  private TextView mDigitText;
  private boolean mEnableHaptics;
  private TextView mKlondikeText;
  private View.OnClickListener mListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if ((NumPadKey.-get2(NumPadKey.this) == null) && (NumPadKey.-get1(NumPadKey.this) == null) && (NumPadKey.-get3(NumPadKey.this) > 0))
      {
        paramAnonymousView = NumPadKey.this.getRootView().findViewById(NumPadKey.-get3(NumPadKey.this));
        if ((paramAnonymousView != null) && ((paramAnonymousView instanceof PasswordTextView))) {
          NumPadKey.-set0(NumPadKey.this, (PasswordTextView)paramAnonymousView);
        }
      }
      else
      {
        if ((NumPadKey.-get1(NumPadKey.this) == null) || (!NumPadKey.-get1(NumPadKey.this).isEnabled())) {
          break label150;
        }
        NumPadKey.-get1(NumPadKey.this).append(Character.forDigit(NumPadKey.-get0(NumPadKey.this), 10));
      }
      for (;;)
      {
        NumPadKey.this.userActivity();
        return;
        if ((paramAnonymousView == null) || (!(paramAnonymousView instanceof PasswordTextViewForPin))) {
          break;
        }
        NumPadKey.-set1(NumPadKey.this, (PasswordTextViewForPin)paramAnonymousView);
        break;
        label150:
        if ((NumPadKey.-get2(NumPadKey.this) != null) && (NumPadKey.-get2(NumPadKey.this).isEnabled())) {
          NumPadKey.-get2(NumPadKey.this).append(Character.forDigit(NumPadKey.-get0(NumPadKey.this), 10));
        }
      }
    }
  };
  private PowerManager mPM;
  private PasswordTextView mTextView;
  private PasswordTextViewForPin mTextViewForPin;
  private int mTextViewResId;
  
  public NumPadKey(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NumPadKey(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NumPadKey(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, R.layout.keyguard_num_pad_key);
  }
  
  protected NumPadKey(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1);
    setFocusable(true);
    Object localObject = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.NumPadKey);
    for (;;)
    {
      try
      {
        this.mDigit = ((TypedArray)localObject).getInt(R.styleable.NumPadKey_digit, this.mDigit);
        this.mTextViewResId = ((TypedArray)localObject).getResourceId(R.styleable.NumPadKey_textView, 0);
        ((TypedArray)localObject).recycle();
        setOnClickListener(this.mListener);
        setOnHoverListener(new LiftToActivateListener(paramContext));
        setAccessibilityDelegate(new ObscureSpeechDelegate(paramContext));
        this.mEnableHaptics = new LockPatternUtils(paramContext).isTactileFeedbackEnabled();
        this.mPM = ((PowerManager)this.mContext.getSystemService("power"));
        ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(paramInt2, this, true);
        this.mDigitText = ((TextView)findViewById(R.id.digit_text));
        this.mDigitText.setText(Integer.toString(this.mDigit));
        this.mKlondikeText = ((TextView)findViewById(R.id.klondike_text));
        if (this.mDigit >= 0)
        {
          if (sKlondike == null) {
            sKlondike = getResources().getStringArray(R.array.lockscreen_num_pad_klondike);
          }
          if ((sKlondike != null) && (sKlondike.length > this.mDigit))
          {
            localObject = sKlondike[this.mDigit];
            if (((String)localObject).length() <= 0) {
              break label329;
            }
            this.mKlondikeText.setText((CharSequence)localObject);
          }
        }
        paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, android.R.styleable.View);
        if (!paramContext.hasValueOrEmpty(13)) {
          setBackground(this.mContext.getDrawable(R.drawable.ripple_drawable));
        }
        paramContext.recycle();
        setContentDescription(this.mDigitText.getText().toString());
        return;
      }
      finally
      {
        ((TypedArray)localObject).recycle();
      }
      label329:
      this.mKlondikeText.setVisibility(4);
    }
  }
  
  public void doHapticKeyClick()
  {
    if (this.mEnableHaptics) {
      performHapticFeedback(1, 3);
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    ObscureSpeechDelegate.sAnnouncedHeadset = false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.mDigitText.getMeasuredHeight();
    paramInt1 = this.mKlondikeText.getMeasuredHeight();
    paramInt3 = getHeight() / 2 - (i + paramInt1) / 2;
    paramInt2 = getWidth() / 2;
    paramInt4 = paramInt2 - this.mDigitText.getMeasuredWidth() / 2;
    i = paramInt3 + i;
    this.mDigitText.layout(paramInt4, paramInt3, this.mDigitText.getMeasuredWidth() + paramInt4, i);
    paramInt3 = (int)(i - paramInt1 * 0.35F);
    paramInt2 -= this.mKlondikeText.getMeasuredWidth() / 2;
    this.mKlondikeText.layout(paramInt2, paramInt3, this.mKlondikeText.getMeasuredWidth() + paramInt2, paramInt3 + paramInt1);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    measureChildren(paramInt1, paramInt2);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getActionMasked() == 0) {
      doHapticKeyClick();
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void userActivity()
  {
    this.mPM.userActivity(SystemClock.uptimeMillis(), false);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\NumPadKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */