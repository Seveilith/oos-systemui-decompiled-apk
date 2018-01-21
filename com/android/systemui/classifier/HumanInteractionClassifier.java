package com.android.systemui.classifier;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.provider.Settings.Global;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import com.android.systemui.util.Utils;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class HumanInteractionClassifier
  extends Classifier
{
  private static HumanInteractionClassifier sInstance = null;
  private final ArrayDeque<MotionEvent> mBufferedEvents = new ArrayDeque();
  private final Context mContext;
  private int mCurrentType = 7;
  private final float mDpi;
  private boolean mEnableClassifier = false;
  private final GestureClassifier[] mGestureClassifiers;
  private final Handler mHandler = new Handler();
  private final HistoryEvaluator mHistoryEvaluator;
  protected final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      HumanInteractionClassifier.-wrap0(HumanInteractionClassifier.this);
    }
  };
  private final StrokeClassifier[] mStrokeClassifiers;
  
  private HumanInteractionClassifier(Context paramContext)
  {
    this.mContext = paramContext;
    paramContext = this.mContext.getResources().getDisplayMetrics();
    this.mDpi = ((paramContext.xdpi + paramContext.ydpi) / 2.0F);
    this.mClassifierData = new ClassifierData(this.mDpi);
    this.mHistoryEvaluator = new HistoryEvaluator();
    this.mStrokeClassifiers = new StrokeClassifier[] { new AnglesClassifier(this.mClassifierData), new SpeedClassifier(this.mClassifierData), new DurationCountClassifier(this.mClassifierData), new EndPointRatioClassifier(this.mClassifierData), new EndPointLengthClassifier(this.mClassifierData), new AccelerationClassifier(this.mClassifierData), new SpeedAnglesClassifier(this.mClassifierData), new LengthCountClassifier(this.mClassifierData), new DirectionClassifier(this.mClassifierData) };
    this.mGestureClassifiers = new GestureClassifier[] { new PointerCountClassifier(this.mClassifierData), new ProximityClassifier(this.mClassifierData) };
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("HIC_enable"), false, this.mSettingsObserver, -1);
    updateConfiguration();
  }
  
  private void addTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mClassifierData.update(paramMotionEvent);
    Object localObject1 = this.mStrokeClassifiers;
    int i = 0;
    int j = localObject1.length;
    while (i < j)
    {
      localObject1[i].onTouchEvent(paramMotionEvent);
      i += 1;
    }
    localObject1 = this.mGestureClassifiers;
    i = 0;
    j = localObject1.length;
    while (i < j)
    {
      localObject1[i].onTouchEvent(paramMotionEvent);
      i += 1;
    }
    boolean bool;
    int k;
    if (!FalsingLog.ENABLED)
    {
      bool = Utils.DEBUG_ONEPLUS;
      k = this.mClassifierData.getEndingStrokes().size();
      i = 0;
    }
    Object localObject2;
    float f1;
    label154:
    Object localObject3;
    label168:
    String str;
    float f2;
    for (;;)
    {
      if (i >= k) {
        break label306;
      }
      localObject2 = (Stroke)this.mClassifierData.getEndingStrokes().get(i);
      f1 = 0.0F;
      StringBuilder localStringBuilder;
      if (bool)
      {
        localObject1 = new StringBuilder("stroke");
        localObject3 = this.mStrokeClassifiers;
        j = 0;
        int m = localObject3.length;
        if (j >= m) {
          break label273;
        }
        str = localObject3[j];
        f2 = str.getFalseTouchEvaluation(this.mCurrentType, (Stroke)localObject2);
        if (bool)
        {
          str = str.getTag();
          localStringBuilder = ((StringBuilder)localObject1).append(" ");
          if (f2 < 1.0F) {
            break label263;
          }
        }
      }
      for (;;)
      {
        localStringBuilder.append(str).append("=").append(f2);
        f1 += f2;
        j += 1;
        break label168;
        bool = true;
        break;
        localObject1 = null;
        break label154;
        label263:
        str = str.toLowerCase();
      }
      label273:
      if (bool) {
        Log.i(" addTouchEvent", ((StringBuilder)localObject1).toString());
      }
      this.mHistoryEvaluator.addStroke(f1);
      i += 1;
    }
    label306:
    i = paramMotionEvent.getActionMasked();
    if ((i == 1) || (i == 3))
    {
      f1 = 0.0F;
      if (bool)
      {
        localObject1 = new StringBuilder("gesture");
        localObject2 = this.mGestureClassifiers;
        i = 0;
        j = localObject2.length;
        label356:
        if (i >= j) {
          break label453;
        }
        str = localObject2[i];
        f2 = str.getFalseTouchEvaluation(this.mCurrentType);
        if (bool)
        {
          str = str.getTag();
          localObject3 = ((StringBuilder)localObject1).append(" ");
          if (f2 < 1.0F) {
            break label443;
          }
        }
      }
      for (;;)
      {
        ((StringBuilder)localObject3).append(str).append("=").append(f2);
        f1 += f2;
        i += 1;
        break label356;
        localObject1 = null;
        break;
        label443:
        str = str.toLowerCase();
      }
      label453:
      if (bool) {
        Log.i(" addTouchEvent", ((StringBuilder)localObject1).toString());
      }
      this.mHistoryEvaluator.addGesture(f1);
      setType(7);
    }
    this.mClassifierData.cleanUp(paramMotionEvent);
  }
  
  public static HumanInteractionClassifier getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new HumanInteractionClassifier(paramContext);
    }
    return sInstance;
  }
  
  private void updateConfiguration()
  {
    boolean bool = true;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "HIC_enable", 1) != 0) {}
    for (;;)
    {
      this.mEnableClassifier = bool;
      return;
      bool = false;
    }
  }
  
  public String getTag()
  {
    return "HIC";
  }
  
  public boolean isEnabled()
  {
    return this.mEnableClassifier;
  }
  
  public boolean isFalseTouch()
  {
    int i = 0;
    if (this.mEnableClassifier)
    {
      float f = this.mHistoryEvaluator.getEvaluation();
      if (f >= 5.0F) {}
      for (boolean bool = true;; bool = false)
      {
        if (FalsingLog.ENABLED)
        {
          StringBuilder localStringBuilder = new StringBuilder().append("eval=").append(f).append(" result=");
          if (bool) {
            i = 1;
          }
          FalsingLog.i("isFalseTouch", i);
        }
        return bool;
      }
    }
    return false;
  }
  
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    int j = 0;
    Object localObject = this.mStrokeClassifiers;
    int k = localObject.length;
    int i = 0;
    while (i < k)
    {
      localObject[i].onSensorChanged(paramSensorEvent);
      i += 1;
    }
    localObject = this.mGestureClassifiers;
    k = localObject.length;
    i = j;
    while (i < k)
    {
      localObject[i].onSensorChanged(paramSensorEvent);
      i += 1;
    }
  }
  
  public void onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mEnableClassifier) {
      return;
    }
    if (this.mCurrentType == 2)
    {
      this.mBufferedEvents.add(MotionEvent.obtain(paramMotionEvent));
      Point localPoint = new Point(paramMotionEvent.getX() / this.mDpi, paramMotionEvent.getY() / this.mDpi);
      while (localPoint.dist(new Point(((MotionEvent)this.mBufferedEvents.getFirst()).getX() / this.mDpi, ((MotionEvent)this.mBufferedEvents.getFirst()).getY() / this.mDpi)) > 0.1F)
      {
        addTouchEvent((MotionEvent)this.mBufferedEvents.getFirst());
        this.mBufferedEvents.remove();
      }
      if (paramMotionEvent.getActionMasked() == 1)
      {
        ((MotionEvent)this.mBufferedEvents.getFirst()).setAction(1);
        addTouchEvent((MotionEvent)this.mBufferedEvents.getFirst());
        this.mBufferedEvents.clear();
      }
      return;
    }
    addTouchEvent(paramMotionEvent);
  }
  
  public void setType(int paramInt)
  {
    this.mCurrentType = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\HumanInteractionClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */