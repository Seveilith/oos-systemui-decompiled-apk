package com.android.keyguard;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PasswordTextViewForPin
  extends View
{
  private Interpolator mAppearInterpolator;
  private int mCharPadding;
  private Stack<CharState> mCharPool = new Stack();
  private Interpolator mDisappearInterpolator;
  private int mDotSize;
  private final Paint mDrawAlphaPaint1 = new Paint();
  private final Paint mDrawAlphaPaint2 = new Paint();
  private final Paint mDrawAlphaPaint3 = new Paint();
  private final Paint mDrawAlphaPaint4 = new Paint();
  private final Paint mDrawEmptyCirclePaint = new Paint();
  private final Paint mDrawPaint = new Paint();
  private Interpolator mFastOutSlowInInterpolator;
  private onTextChangedListerner mOnTextChangeListerner;
  private PowerManager mPM;
  private boolean mShowPassword;
  private String mText = "";
  private ArrayList<CharState> mTextChars = new ArrayList();
  private final int mTextHeightRaw;
  private UserActivityListener mUserActivityListener;
  
  public PasswordTextViewForPin(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PasswordTextViewForPin(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public PasswordTextViewForPin(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PasswordTextViewForPin(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setFocusableInTouchMode(true);
    setFocusable(true);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.PasswordTextView);
    for (;;)
    {
      try
      {
        this.mTextHeightRaw = paramContext.getInt(R.styleable.PasswordTextView_scaledTextSize, 0);
        paramContext.recycle();
        this.mDrawPaint.setFlags(129);
        this.mDrawPaint.setTextAlign(Paint.Align.CENTER);
        this.mDrawPaint.setColor(-1);
        this.mDotSize = getContext().getResources().getDimensionPixelSize(R.dimen.password_dot_size);
        this.mCharPadding = getContext().getResources().getDimensionPixelSize(R.dimen.password_char_padding);
        if (Settings.System.getInt(this.mContext.getContentResolver(), "show_password", 1) == 1)
        {
          this.mShowPassword = bool;
          this.mAppearInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563662);
          this.mDisappearInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563663);
          this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563661);
          this.mPM = ((PowerManager)this.mContext.getSystemService("power"));
          return;
        }
      }
      finally
      {
        paramContext.recycle();
      }
      bool = false;
    }
  }
  
  private Rect getCharBounds()
  {
    float f1 = this.mTextHeightRaw;
    float f2 = getResources().getDisplayMetrics().scaledDensity;
    this.mDrawPaint.setTextSize(f1 * f2);
    Rect localRect = new Rect();
    this.mDrawPaint.getTextBounds("0", 0, 1, localRect);
    return localRect;
  }
  
  private float getDrawingWidth()
  {
    int i = 0;
    int m = this.mTextChars.size();
    Object localObject = getCharBounds();
    int n = ((Rect)localObject).right;
    int i1 = ((Rect)localObject).left;
    int j = 0;
    while (j < m)
    {
      localObject = (CharState)this.mTextChars.get(j);
      int k = i;
      if (j != 0) {
        k = (int)(i + this.mCharPadding * ((CharState)localObject).currentWidthFactor);
      }
      i = (int)(k + (n - i1) * ((CharState)localObject).currentWidthFactor);
      j += 1;
    }
    return i;
  }
  
  private CharState obtainCharState(char paramChar)
  {
    CharState localCharState;
    if (this.mCharPool.isEmpty()) {
      localCharState = new CharState(null);
    }
    for (;;)
    {
      localCharState.whichChar = paramChar;
      return localCharState;
      localCharState = (CharState)this.mCharPool.pop();
      localCharState.reset();
    }
  }
  
  private boolean shouldSpeakPasswordsForAccessibility()
  {
    return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "speak_password", 0, -3) == 1;
  }
  
  private void userActivity()
  {
    this.mPM.userActivity(SystemClock.uptimeMillis(), false);
    if (this.mUserActivityListener != null) {
      this.mUserActivityListener.onUserActivity();
    }
  }
  
  public void append(char paramChar)
  {
    int i = this.mTextChars.size();
    String str = this.mText;
    this.mText += paramChar;
    int j = this.mText.length();
    CharState localCharState;
    if (j > i)
    {
      localCharState = obtainCharState(paramChar);
      this.mTextChars.add(localCharState);
    }
    for (;;)
    {
      localCharState.startAppearAnimation();
      if (j > 1)
      {
        localCharState = (CharState)this.mTextChars.get(j - 2);
        if (localCharState.isDotSwapPending) {
          localCharState.swapToDotWhenAppearFinished();
        }
      }
      if (j == 16) {
        new Handler().post(new Runnable()
        {
          public void run()
          {
            PasswordTextViewForPin.-get8(PasswordTextViewForPin.this).onCheckPasswordAndUnlock();
          }
        });
      }
      if (this.mOnTextChangeListerner != null) {
        this.mOnTextChangeListerner.onTextChanged(this.mText);
      }
      userActivity();
      sendAccessibilityEventTypeViewTextChanged(str, str.length(), 0, 1);
      return;
      localCharState = (CharState)this.mTextChars.get(j - 1);
      localCharState.whichChar = paramChar;
    }
  }
  
  public void deleteLastChar()
  {
    int i = this.mText.length();
    String str = this.mText;
    if (i > 0)
    {
      this.mText = this.mText.substring(0, i - 1);
      ((CharState)this.mTextChars.get(i - 1)).startRemoveAnimation(0L, 0L);
    }
    if (this.mOnTextChangeListerner != null) {
      this.mOnTextChangeListerner.onTextChanged(this.mText);
    }
    userActivity();
    sendAccessibilityEventTypeViewTextChanged(str, str.length() - 1, 1, 0);
  }
  
  public String getText()
  {
    return this.mText;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    float f1 = getDrawingWidth();
    f1 = getWidth() / 2 - f1 / 2.0F;
    int j = this.mTextChars.size();
    Rect localRect = getCharBounds();
    int k = localRect.bottom;
    int m = localRect.top;
    float f2 = getHeight() / 2;
    float f3 = localRect.right - localRect.left;
    int i = 0;
    while (i < j)
    {
      f1 += ((CharState)this.mTextChars.get(i)).draw(paramCanvas, f1, k - m, f2, f3);
      i += 1;
    }
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(PasswordTextViewForPin.class.getName());
    paramAccessibilityEvent.setPassword(true);
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(PasswordTextViewForPin.class.getName());
    paramAccessibilityNodeInfo.setPassword(true);
    if (shouldSpeakPasswordsForAccessibility()) {
      paramAccessibilityNodeInfo.setText(this.mText);
    }
    paramAccessibilityNodeInfo.setEditable(true);
    paramAccessibilityNodeInfo.setInputType(16);
  }
  
  public void onPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onPopulateAccessibilityEvent(paramAccessibilityEvent);
    if (shouldSpeakPasswordsForAccessibility())
    {
      String str = this.mText;
      if (!TextUtils.isEmpty(str)) {
        paramAccessibilityEvent.getText().add(str);
      }
    }
  }
  
  public void reset(boolean paramBoolean1, boolean paramBoolean2)
  {
    String str = this.mText;
    this.mText = "";
    int k = this.mTextChars.size();
    int m = (k - 1) / 2;
    int i = 0;
    if (i < k)
    {
      CharState localCharState = (CharState)this.mTextChars.get(i);
      int j;
      if (paramBoolean1) {
        if (i <= m)
        {
          j = i * 2;
          label65:
          localCharState.startRemoveAnimation(Math.min(j * 40L, 200L), Math.min(40L * (k - 1), 200L) + 160L);
          CharState.-wrap2(localCharState);
        }
      }
      for (;;)
      {
        i += 1;
        break;
        j = k - 1 - (i - m - 1) * 2;
        break label65;
        this.mCharPool.push(localCharState);
      }
    }
    if (!paramBoolean1) {
      this.mTextChars.clear();
    }
    if (this.mOnTextChangeListerner != null) {
      this.mOnTextChangeListerner.onTextChanged(this.mText);
    }
    if (paramBoolean2) {
      sendAccessibilityEventTypeViewTextChanged(str, 0, str.length(), 0);
    }
  }
  
  void sendAccessibilityEventTypeViewTextChanged(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((AccessibilityManager.getInstance(this.mContext).isEnabled()) && ((isFocused()) || ((isSelected()) && (isShown()))))
    {
      if (!shouldSpeakPasswordsForAccessibility()) {
        paramString = null;
      }
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(16);
      localAccessibilityEvent.setFromIndex(paramInt1);
      localAccessibilityEvent.setRemovedCount(paramInt2);
      localAccessibilityEvent.setAddedCount(paramInt3);
      localAccessibilityEvent.setBeforeText(paramString);
      localAccessibilityEvent.setPassword(true);
      sendAccessibilityEventUnchecked(localAccessibilityEvent);
    }
  }
  
  public void setTextChangeListener(onTextChangedListerner paramonTextChangedListerner)
  {
    this.mOnTextChangeListerner = paramonTextChangedListerner;
  }
  
  public void setUserActivityListener(UserActivityListener paramUserActivityListener)
  {
    this.mUserActivityListener = paramUserActivityListener;
  }
  
  private class CharState
  {
    float currentDotSizeFactor;
    float currentEmptyCircleSizeFactor = 1.0F;
    float currentTextSizeFactor;
    float currentTextTranslationY = 1.0F;
    float currentWidthFactor;
    boolean dotAnimationIsGrowing;
    Animator dotAnimator;
    Animator.AnimatorListener dotFinishListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        PasswordTextViewForPin.CharState.this.dotAnimator = null;
      }
    };
    private ValueAnimator.AnimatorUpdateListener dotSizeUpdater = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        PasswordTextViewForPin.CharState.this.currentDotSizeFactor = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        PasswordTextViewForPin.this.invalidate();
      }
    };
    private Runnable dotSwapperRunnable = new Runnable()
    {
      public void run()
      {
        PasswordTextViewForPin.CharState.-wrap1(PasswordTextViewForPin.CharState.this);
        PasswordTextViewForPin.CharState.this.isDotSwapPending = false;
      }
    };
    boolean isDotSwapPending;
    Animator.AnimatorListener removeEndListener = new AnimatorListenerAdapter()
    {
      private boolean mCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (!this.mCancelled)
        {
          PasswordTextViewForPin.-get7(PasswordTextViewForPin.this).remove(PasswordTextViewForPin.CharState.this);
          PasswordTextViewForPin.-get2(PasswordTextViewForPin.this).push(PasswordTextViewForPin.CharState.this);
          PasswordTextViewForPin.CharState.this.reset();
          PasswordTextViewForPin.CharState.-wrap0(PasswordTextViewForPin.CharState.this, PasswordTextViewForPin.CharState.this.textTranslateAnimator);
          PasswordTextViewForPin.CharState.this.textTranslateAnimator = null;
        }
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        this.mCancelled = false;
      }
    };
    boolean textAnimationIsGrowing;
    ValueAnimator textAnimator;
    Animator.AnimatorListener textFinishListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        PasswordTextViewForPin.CharState.this.textAnimator = null;
      }
    };
    private ValueAnimator.AnimatorUpdateListener textSizeUpdater = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        PasswordTextViewForPin.CharState.this.currentTextSizeFactor = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        PasswordTextViewForPin.this.invalidate();
      }
    };
    ValueAnimator textTranslateAnimator;
    Animator.AnimatorListener textTranslateFinishListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        PasswordTextViewForPin.CharState.this.textTranslateAnimator = null;
      }
    };
    private ValueAnimator.AnimatorUpdateListener textTranslationUpdater = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        PasswordTextViewForPin.CharState.this.currentTextTranslationY = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        PasswordTextViewForPin.this.invalidate();
      }
    };
    char whichChar;
    boolean widthAnimationIsGrowing;
    ValueAnimator widthAnimator;
    Animator.AnimatorListener widthFinishListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        PasswordTextViewForPin.CharState.this.widthAnimator = null;
      }
    };
    private ValueAnimator.AnimatorUpdateListener widthUpdater = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        PasswordTextViewForPin.CharState.this.currentWidthFactor = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        PasswordTextViewForPin.this.invalidate();
      }
    };
    
    private CharState() {}
    
    private void cancelAnimator(Animator paramAnimator)
    {
      if (paramAnimator != null) {
        paramAnimator.cancel();
      }
    }
    
    private void performSwap()
    {
      startTextDisappearAnimation(0L);
      startDotAppearAnimation(30L);
    }
    
    private void postDotSwap(long paramLong)
    {
      removeDotSwapCallbacks();
      PasswordTextViewForPin.this.postDelayed(this.dotSwapperRunnable, paramLong);
      this.isDotSwapPending = true;
    }
    
    private void removeDotSwapCallbacks()
    {
      PasswordTextViewForPin.this.removeCallbacks(this.dotSwapperRunnable);
      this.isDotSwapPending = false;
    }
    
    private void startDotAppearAnimation(long paramLong)
    {
      cancelAnimator(this.dotAnimator);
      ValueAnimator localValueAnimator1;
      AnimatorSet localAnimatorSet;
      if (!PasswordTextViewForPin.-get6(PasswordTextViewForPin.this))
      {
        localValueAnimator1 = ValueAnimator.ofFloat(new float[] { this.currentDotSizeFactor, 1.5F });
        localValueAnimator1.addUpdateListener(this.dotSizeUpdater);
        localValueAnimator1.setInterpolator(PasswordTextViewForPin.-get0(PasswordTextViewForPin.this));
        localValueAnimator1.setDuration(160L);
        ValueAnimator localValueAnimator2 = ValueAnimator.ofFloat(new float[] { 1.5F, 1.0F });
        localValueAnimator2.addUpdateListener(this.dotSizeUpdater);
        localValueAnimator2.setDuration(160L);
        localValueAnimator2.addListener(this.dotFinishListener);
        localAnimatorSet = new AnimatorSet();
        localAnimatorSet.playSequentially(new Animator[] { localValueAnimator1, localValueAnimator2 });
        localAnimatorSet.setStartDelay(paramLong);
        localAnimatorSet.start();
      }
      for (this.dotAnimator = localAnimatorSet;; this.dotAnimator = localValueAnimator1)
      {
        this.dotAnimationIsGrowing = true;
        return;
        localValueAnimator1 = ValueAnimator.ofFloat(new float[] { this.currentDotSizeFactor, 1.0F });
        localValueAnimator1.addUpdateListener(this.dotSizeUpdater);
        localValueAnimator1.setDuration(((1.0F - this.currentDotSizeFactor) * 160.0F));
        localValueAnimator1.addListener(this.dotFinishListener);
        localValueAnimator1.setStartDelay(paramLong);
        localValueAnimator1.start();
      }
    }
    
    private void startDotDisappearAnimation(long paramLong)
    {
      cancelAnimator(this.dotAnimator);
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { this.currentDotSizeFactor, 0.0F });
      localValueAnimator.addUpdateListener(this.dotSizeUpdater);
      localValueAnimator.addListener(this.dotFinishListener);
      localValueAnimator.setInterpolator(PasswordTextViewForPin.-get3(PasswordTextViewForPin.this));
      localValueAnimator.setDuration((Math.min(this.currentDotSizeFactor, 1.0F) * 160.0F));
      localValueAnimator.setStartDelay(paramLong);
      localValueAnimator.start();
      this.dotAnimator = localValueAnimator;
      this.dotAnimationIsGrowing = false;
    }
    
    private void startTextAppearAnimation()
    {
      cancelAnimator(this.textAnimator);
      this.textAnimator = ValueAnimator.ofFloat(new float[] { this.currentTextSizeFactor, 1.0F });
      this.textAnimator.addUpdateListener(this.textSizeUpdater);
      this.textAnimator.addListener(this.textFinishListener);
      this.textAnimator.setInterpolator(PasswordTextViewForPin.-get0(PasswordTextViewForPin.this));
      this.textAnimator.setDuration(((1.0F - this.currentTextSizeFactor) * 160.0F));
      this.textAnimator.start();
      this.textAnimationIsGrowing = true;
      if (this.textTranslateAnimator == null)
      {
        this.textTranslateAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.0F });
        this.textTranslateAnimator.addUpdateListener(this.textTranslationUpdater);
        this.textTranslateAnimator.addListener(this.textTranslateFinishListener);
        this.textTranslateAnimator.setInterpolator(PasswordTextViewForPin.-get0(PasswordTextViewForPin.this));
        this.textTranslateAnimator.setDuration(160L);
        this.textTranslateAnimator.start();
      }
    }
    
    private void startTextDisappearAnimation(long paramLong)
    {
      cancelAnimator(this.textAnimator);
      this.textAnimator = ValueAnimator.ofFloat(new float[] { this.currentTextSizeFactor, 0.0F });
      this.textAnimator.addUpdateListener(this.textSizeUpdater);
      this.textAnimator.addListener(this.textFinishListener);
      this.textAnimator.setInterpolator(PasswordTextViewForPin.-get3(PasswordTextViewForPin.this));
      this.textAnimator.setDuration((this.currentTextSizeFactor * 160.0F));
      this.textAnimator.setStartDelay(paramLong);
      this.textAnimator.start();
      this.textAnimationIsGrowing = false;
    }
    
    private void startWidthAppearAnimation()
    {
      cancelAnimator(this.widthAnimator);
      this.widthAnimator = ValueAnimator.ofFloat(new float[] { this.currentWidthFactor, 1.0F });
      this.widthAnimator.addUpdateListener(this.widthUpdater);
      this.widthAnimator.addListener(this.widthFinishListener);
      this.widthAnimator.setDuration(((1.0F - this.currentWidthFactor) * 160.0F));
      this.widthAnimator.start();
      this.widthAnimationIsGrowing = true;
    }
    
    private void startWidthDisappearAnimation(long paramLong)
    {
      cancelAnimator(this.widthAnimator);
      this.widthAnimator = ValueAnimator.ofFloat(new float[] { this.currentWidthFactor, 0.0F });
      this.widthAnimator.addUpdateListener(this.widthUpdater);
      this.widthAnimator.addListener(this.widthFinishListener);
      this.widthAnimator.addListener(this.removeEndListener);
      this.widthAnimator.setDuration((this.currentWidthFactor * 160.0F));
      this.widthAnimator.setStartDelay(paramLong);
      this.widthAnimator.start();
      this.widthAnimationIsGrowing = false;
    }
    
    public float draw(Canvas paramCanvas, float paramFloat1, int paramInt, float paramFloat2, float paramFloat3)
    {
      int i;
      if (this.currentTextSizeFactor > 0.0F)
      {
        i = 1;
        if (this.currentDotSizeFactor <= 0.0F) {
          break label205;
        }
      }
      label205:
      for (int j = 1;; j = 0)
      {
        paramFloat3 *= this.currentWidthFactor;
        if (i != 0)
        {
          float f1 = paramInt / 2.0F;
          float f2 = this.currentTextSizeFactor;
          float f3 = paramInt;
          float f4 = this.currentTextTranslationY;
          paramCanvas.save();
          paramCanvas.translate(paramFloat1 + paramFloat3 / 2.0F, f1 * f2 + paramFloat2 + f3 * f4 * 0.8F);
          paramCanvas.scale(this.currentTextSizeFactor, this.currentTextSizeFactor);
          paramCanvas.drawText(Character.toString(this.whichChar), 0.0F, 0.0F, PasswordTextViewForPin.-get5(PasswordTextViewForPin.this));
          paramCanvas.restore();
        }
        if (j != 0)
        {
          paramCanvas.save();
          paramCanvas.translate(paramFloat1 + paramFloat3 / 2.0F, paramFloat2);
          paramCanvas.drawCircle(0.0F, 0.0F, PasswordTextViewForPin.-get4(PasswordTextViewForPin.this) / 2 * this.currentDotSizeFactor, PasswordTextViewForPin.-get5(PasswordTextViewForPin.this));
          paramCanvas.restore();
        }
        return PasswordTextViewForPin.-get1(PasswordTextViewForPin.this) * this.currentWidthFactor + paramFloat3;
        i = 0;
        break;
      }
    }
    
    void reset()
    {
      this.whichChar = '\000';
      this.currentTextSizeFactor = 0.0F;
      this.currentDotSizeFactor = 0.0F;
      this.currentWidthFactor = 0.0F;
      cancelAnimator(this.textAnimator);
      this.textAnimator = null;
      cancelAnimator(this.dotAnimator);
      this.dotAnimator = null;
      cancelAnimator(this.widthAnimator);
      this.widthAnimator = null;
      this.currentTextTranslationY = 1.0F;
      removeDotSwapCallbacks();
    }
    
    void startAppearAnimation()
    {
      int i;
      int j;
      if (!PasswordTextViewForPin.-get6(PasswordTextViewForPin.this)) {
        if ((this.dotAnimator != null) && (this.dotAnimationIsGrowing))
        {
          i = 0;
          if (!PasswordTextViewForPin.-get6(PasswordTextViewForPin.this)) {
            break label126;
          }
          if ((this.textAnimator == null) || (!this.textAnimationIsGrowing)) {
            break label121;
          }
          j = 0;
          label52:
          if ((this.widthAnimator == null) || (!this.widthAnimationIsGrowing)) {
            break label131;
          }
        }
      }
      label121:
      label126:
      label131:
      for (int k = 0;; k = 1)
      {
        if (i != 0) {
          startDotAppearAnimation(0L);
        }
        if (j != 0) {
          startTextAppearAnimation();
        }
        if (k != 0) {
          startWidthAppearAnimation();
        }
        if (PasswordTextViewForPin.-get6(PasswordTextViewForPin.this)) {
          postDotSwap(250L);
        }
        return;
        i = 1;
        break;
        i = 0;
        break;
        j = 1;
        break label52;
        j = 0;
        break label52;
      }
    }
    
    void startRemoveAnimation(long paramLong1, long paramLong2)
    {
      boolean bool1;
      boolean bool2;
      label38:
      boolean bool3;
      if ((this.currentDotSizeFactor > 0.0F) && (this.dotAnimator == null))
      {
        bool1 = true;
        if ((this.currentTextSizeFactor <= 0.0F) || (this.textAnimator != null)) {
          break label110;
        }
        bool2 = true;
        if ((this.currentWidthFactor <= 0.0F) || (this.widthAnimator != null)) {
          break label132;
        }
        bool3 = true;
      }
      for (;;)
      {
        if (bool1) {
          startDotDisappearAnimation(paramLong1);
        }
        if (bool2) {
          startTextDisappearAnimation(paramLong1);
        }
        if (bool3) {
          startWidthDisappearAnimation(paramLong2);
        }
        return;
        if (this.dotAnimator != null)
        {
          bool1 = this.dotAnimationIsGrowing;
          break;
        }
        bool1 = false;
        break;
        label110:
        if (this.textAnimator != null)
        {
          bool2 = this.textAnimationIsGrowing;
          break label38;
        }
        bool2 = false;
        break label38;
        label132:
        if (this.widthAnimator != null) {
          bool3 = this.widthAnimationIsGrowing;
        } else {
          bool3 = false;
        }
      }
    }
    
    void swapToDotWhenAppearFinished()
    {
      removeDotSwapCallbacks();
      if (this.textAnimator != null)
      {
        postDotSwap(100L + (this.textAnimator.getDuration() - this.textAnimator.getCurrentPlayTime()));
        return;
      }
      performSwap();
    }
  }
  
  public static abstract interface UserActivityListener
  {
    public abstract void onCheckPasswordAndUnlock();
    
    public abstract void onUserActivity();
  }
  
  public static abstract interface onTextChangedListerner
  {
    public abstract void onTextChanged(String paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\PasswordTextViewForPin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */