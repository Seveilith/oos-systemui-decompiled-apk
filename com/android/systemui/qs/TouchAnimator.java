package com.android.systemui.qs;

import android.util.FloatProperty;
import android.util.MathUtils;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.List;

public class TouchAnimator
{
  private static final FloatProperty<TouchAnimator> POSITION = new FloatProperty("position")
  {
    public Float get(TouchAnimator paramAnonymousTouchAnimator)
    {
      return Float.valueOf(TouchAnimator.-get1(paramAnonymousTouchAnimator));
    }
    
    public void setValue(TouchAnimator paramAnonymousTouchAnimator, float paramAnonymousFloat)
    {
      paramAnonymousTouchAnimator.setPosition(paramAnonymousFloat);
    }
  };
  private final float mEndDelay;
  private final Interpolator mInterpolator;
  private final KeyframeSet[] mKeyframeSets;
  private float mLastT = -1.0F;
  private final Listener mListener;
  private final float mSpan;
  private final float mStartDelay;
  private final Object[] mTargets;
  
  private TouchAnimator(Object[] paramArrayOfObject, KeyframeSet[] paramArrayOfKeyframeSet, float paramFloat1, float paramFloat2, Interpolator paramInterpolator, Listener paramListener)
  {
    this.mTargets = paramArrayOfObject;
    this.mKeyframeSets = paramArrayOfKeyframeSet;
    this.mStartDelay = paramFloat1;
    this.mEndDelay = paramFloat2;
    this.mSpan = (1.0F - this.mEndDelay - this.mStartDelay);
    this.mInterpolator = paramInterpolator;
    this.mListener = paramListener;
  }
  
  public void setPosition(float paramFloat)
  {
    float f = MathUtils.constrain((paramFloat - this.mStartDelay) / this.mSpan, 0.0F, 1.0F);
    paramFloat = f;
    if (this.mInterpolator != null) {
      paramFloat = this.mInterpolator.getInterpolation(f);
    }
    if (paramFloat == this.mLastT) {
      return;
    }
    if (this.mListener != null)
    {
      if (paramFloat != 1.0F) {
        break label108;
      }
      this.mListener.onAnimationAtEnd();
    }
    for (;;)
    {
      this.mLastT = paramFloat;
      int i = 0;
      while (i < this.mTargets.length)
      {
        this.mKeyframeSets[i].setValue(paramFloat, this.mTargets[i]);
        i += 1;
      }
      label108:
      if (paramFloat == 0.0F) {
        this.mListener.onAnimationAtStart();
      } else if ((this.mLastT <= 0.0F) || (this.mLastT == 1.0F)) {
        this.mListener.onAnimationStarted();
      }
    }
  }
  
  public static class Builder
  {
    private float mEndDelay;
    private Interpolator mInterpolator;
    private TouchAnimator.Listener mListener;
    private float mStartDelay;
    private List<Object> mTargets = new ArrayList();
    private List<TouchAnimator.KeyframeSet> mValues = new ArrayList();
    
    private void add(Object paramObject, TouchAnimator.KeyframeSet paramKeyframeSet)
    {
      this.mTargets.add(paramObject);
      this.mValues.add(paramKeyframeSet);
    }
    
    private static Property getProperty(Object paramObject, String paramString, Class<?> paramClass)
    {
      if ((paramObject instanceof View))
      {
        if (paramString.equals("translationX")) {
          return View.TRANSLATION_X;
        }
        if (paramString.equals("translationY")) {
          return View.TRANSLATION_Y;
        }
        if (paramString.equals("translationZ")) {
          return View.TRANSLATION_Z;
        }
        if (paramString.equals("alpha")) {
          return View.ALPHA;
        }
        if (paramString.equals("rotation")) {
          return View.ROTATION;
        }
        if (paramString.equals("x")) {
          return View.X;
        }
        if (paramString.equals("y")) {
          return View.Y;
        }
        if (paramString.equals("scaleX")) {
          return View.SCALE_X;
        }
        if (paramString.equals("scaleY")) {
          return View.SCALE_Y;
        }
      }
      if (((paramObject instanceof TouchAnimator)) && ("position".equals(paramString))) {
        return TouchAnimator.-get0();
      }
      return Property.of(paramObject.getClass(), paramClass, paramString);
    }
    
    public Builder addFloat(Object paramObject, String paramString, float... paramVarArgs)
    {
      add(paramObject, TouchAnimator.KeyframeSet.ofFloat(getProperty(paramObject, paramString, Float.TYPE), paramVarArgs));
      return this;
    }
    
    public TouchAnimator build()
    {
      return new TouchAnimator(this.mTargets.toArray(new Object[this.mTargets.size()]), (TouchAnimator.KeyframeSet[])this.mValues.toArray(new TouchAnimator.KeyframeSet[this.mValues.size()]), this.mStartDelay, this.mEndDelay, this.mInterpolator, this.mListener, null);
    }
    
    public Builder setEndDelay(float paramFloat)
    {
      this.mEndDelay = paramFloat;
      return this;
    }
    
    public Builder setInterpolator(Interpolator paramInterpolator)
    {
      this.mInterpolator = paramInterpolator;
      return this;
    }
    
    public Builder setListener(TouchAnimator.Listener paramListener)
    {
      this.mListener = paramListener;
      return this;
    }
    
    public Builder setStartDelay(float paramFloat)
    {
      this.mStartDelay = paramFloat;
      return this;
    }
  }
  
  private static class FloatKeyframeSet<T>
    extends TouchAnimator.KeyframeSet
  {
    private final Property<T, Float> mProperty;
    private final float[] mValues;
    
    public FloatKeyframeSet(Property<T, Float> paramProperty, float[] paramArrayOfFloat)
    {
      super();
      this.mProperty = paramProperty;
      this.mValues = paramArrayOfFloat;
    }
    
    protected void interpolate(int paramInt, float paramFloat, Object paramObject)
    {
      float f1 = this.mValues[(paramInt - 1)];
      float f2 = this.mValues[paramInt];
      this.mProperty.set(paramObject, Float.valueOf((f2 - f1) * paramFloat + f1));
    }
  }
  
  private static abstract class KeyframeSet
  {
    private final float mFrameWidth;
    private final int mSize;
    
    public KeyframeSet(int paramInt)
    {
      this.mSize = paramInt;
      this.mFrameWidth = (1.0F / (paramInt - 1));
    }
    
    public static KeyframeSet ofFloat(Property paramProperty, float... paramVarArgs)
    {
      return new TouchAnimator.FloatKeyframeSet(paramProperty, paramVarArgs);
    }
    
    protected abstract void interpolate(int paramInt, float paramFloat, Object paramObject);
    
    void setValue(float paramFloat, Object paramObject)
    {
      int i = 1;
      while ((i < this.mSize - 1) && (paramFloat > this.mFrameWidth)) {
        i += 1;
      }
      interpolate(i, paramFloat / this.mFrameWidth, paramObject);
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onAnimationAtEnd();
    
    public abstract void onAnimationAtStart();
    
    public abstract void onAnimationStarted();
  }
  
  public static class ListenerAdapter
    implements TouchAnimator.Listener
  {
    public void onAnimationAtEnd() {}
    
    public void onAnimationAtStart() {}
    
    public void onAnimationStarted() {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\TouchAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */