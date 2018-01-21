package com.android.systemui.egg;

import android.animation.LayoutTransition;
import android.animation.TimeAnimator;
import android.animation.TimeAnimator.TimeListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import java.util.ArrayList;
import java.util.Iterator;

public class MLand
  extends FrameLayout
{
  static final int[] ANTENNAE;
  static final int[] CACTI = { 2130837592, 2130837593, 2130837594 };
  public static final boolean DEBUG = Log.isLoggable("MLand", 3);
  public static final boolean DEBUG_IDDQD = Log.isLoggable("MLand.iddqd", 3);
  static final int[] EYES;
  static final int[] MOUNTAINS = { 2130838056, 2130838057, 2130838058 };
  static final int[] MOUTHS;
  private static Params PARAMS;
  private static final int[][] SKIES;
  private static float dp;
  static final float[] hsv;
  static final Rect sTmpRect;
  private float dt;
  private TimeAnimator mAnim;
  private boolean mAnimating;
  private final AudioAttributes mAudioAttrs = new AudioAttributes.Builder().setUsage(14).build();
  private AudioManager mAudioManager;
  private int mCountdown = 0;
  private int mCurrentPipeId;
  private boolean mFlipped;
  private boolean mFrozen;
  private ArrayList<Integer> mGameControllers = new ArrayList();
  private int mHeight;
  private float mLastPipeTime;
  private ArrayList<Obstacle> mObstaclesInPlay = new ArrayList();
  private Paint mPlayerTracePaint;
  private ArrayList<Player> mPlayers = new ArrayList();
  private boolean mPlaying;
  private int mScene;
  private ViewGroup mScoreFields;
  private View mSplash;
  private int mTaps;
  private int mTimeOfDay;
  private Paint mTouchPaint;
  private Vibrator mVibrator;
  private int mWidth;
  private float t;
  
  static
  {
    int[] arrayOfInt1 = { -4144897, -6250241 };
    int[] arrayOfInt2 = { -16777152, -16777200 };
    int[] arrayOfInt3 = { -6258656, -14663552 };
    SKIES = new int[][] { arrayOfInt1, { -16777200, -16777216 }, arrayOfInt2, arrayOfInt3 };
    dp = 1.0F;
    hsv = new float[] { 0.0F, 0.0F, 0.0F };
    sTmpRect = new Rect();
    ANTENNAE = new int[] { 2130838046, 2130838047 };
    EYES = new int[] { 2130838048, 2130838049 };
    MOUTHS = new int[] { 2130838051, 2130838052, 2130838053, 2130838054 };
  }
  
  public MLand(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public MLand(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public MLand(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mVibrator = ((Vibrator)paramContext.getSystemService("vibrator"));
    this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
    setFocusable(true);
    PARAMS = new Params(getResources());
    this.mTimeOfDay = irand(0, SKIES.length - 1);
    this.mScene = irand(0, 3);
    this.mTouchPaint = new Paint(1);
    this.mTouchPaint.setColor(-2130706433);
    this.mTouchPaint.setStyle(Paint.Style.FILL);
    this.mPlayerTracePaint = new Paint(1);
    this.mPlayerTracePaint.setColor(-2130706433);
    this.mPlayerTracePaint.setStyle(Paint.Style.STROKE);
    this.mPlayerTracePaint.setStrokeWidth(dp * 2.0F);
    setLayoutDirection(0);
    setupPlayers(1);
    MetricsLogger.count(getContext(), "egg_mland_create", 1);
  }
  
  public static void L(String paramString, Object... paramVarArgs)
  {
    if (DEBUG) {
      if (paramVarArgs.length != 0) {
        break label19;
      }
    }
    for (;;)
    {
      Log.d("MLand", paramString);
      return;
      label19:
      paramString = String.format(paramString, paramVarArgs);
    }
  }
  
  private int addPlayerInternal(Player paramPlayer)
  {
    this.mPlayers.add(paramPlayer);
    realignPlayers();
    TextView localTextView = (TextView)LayoutInflater.from(getContext()).inflate(2130968717, null);
    if (this.mScoreFields != null) {
      this.mScoreFields.addView(localTextView, new ViewGroup.MarginLayoutParams(-2, -1));
    }
    paramPlayer.setScoreField(localTextView);
    return this.mPlayers.size() - 1;
  }
  
  public static final float clamp(float paramFloat)
  {
    float f;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    do
    {
      return f;
      f = paramFloat;
    } while (paramFloat <= 1.0F);
    return 1.0F;
  }
  
  private void clearPlayers()
  {
    while (this.mPlayers.size() > 0) {
      removePlayerInternal((Player)this.mPlayers.get(0));
    }
  }
  
  public static final float frand()
  {
    return (float)Math.random();
  }
  
  public static final float frand(float paramFloat1, float paramFloat2)
  {
    return lerp(frand(), paramFloat1, paramFloat2);
  }
  
  public static final int irand(int paramInt1, int paramInt2)
  {
    return Math.round(frand(paramInt1, paramInt2));
  }
  
  public static boolean isGamePad(InputDevice paramInputDevice)
  {
    int i = paramInputDevice.getSources();
    return ((i & 0x401) == 1025) || ((i & 0x1000010) == 16777232);
  }
  
  public static final float lerp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (paramFloat3 - paramFloat2) * paramFloat1 + paramFloat2;
  }
  
  private static float luma(int paramInt)
  {
    return (0xFF0000 & paramInt) * 0.2126F / 1.671168E7F + (0xFF00 & paramInt) * 0.7152F / 65280.0F + (paramInt & 0xFF) * 0.0722F / 255.0F;
  }
  
  public static int pick(int[] paramArrayOfInt)
  {
    return paramArrayOfInt[irand(0, paramArrayOfInt.length - 1)];
  }
  
  private void poke(int paramInt)
  {
    poke(paramInt, -1.0F, -1.0F);
  }
  
  private void poke(int paramInt, float paramFloat1, float paramFloat2)
  {
    L("poke(%d)", new Object[] { Integer.valueOf(paramInt) });
    if (this.mFrozen) {
      return;
    }
    if (!this.mAnimating) {
      reset();
    }
    if (!this.mPlaying) {
      start(true);
    }
    Player localPlayer;
    do
    {
      return;
      localPlayer = getPlayer(paramInt);
      if (localPlayer == null) {
        return;
      }
      localPlayer.boost(paramFloat1, paramFloat2);
      this.mTaps += 1;
    } while (!DEBUG);
    localPlayer.dv *= 0.5F;
    localPlayer.animate().setDuration(400L);
  }
  
  private void realignPlayers()
  {
    int j = this.mPlayers.size();
    float f = (this.mWidth - (j - 1) * PARAMS.PLAYER_SIZE) / 2;
    int i = 0;
    while (i < j)
    {
      ((Player)this.mPlayers.get(i)).setX(f);
      f += PARAMS.PLAYER_SIZE;
      i += 1;
    }
  }
  
  private void removePlayerInternal(Player paramPlayer)
  {
    if (this.mPlayers.remove(paramPlayer))
    {
      removeView(paramPlayer);
      this.mScoreFields.removeView(Player.-get2(paramPlayer));
      realignPlayers();
    }
  }
  
  public static final float rlerp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (paramFloat1 - paramFloat2) / (paramFloat3 - paramFloat2);
  }
  
  private void step(long paramLong1, long paramLong2)
  {
    this.t = ((float)paramLong1 / 1000.0F);
    this.dt = ((float)paramLong2 / 1000.0F);
    if (DEBUG)
    {
      this.t *= 0.5F;
      this.dt *= 0.5F;
    }
    int j = getChildCount();
    int i = 0;
    Object localObject1;
    while (i < j)
    {
      localObject1 = getChildAt(i);
      if ((localObject1 instanceof GameView)) {
        ((GameView)localObject1).step(paramLong1, paramLong2, this.t, this.dt);
      }
      i += 1;
    }
    int k;
    label194:
    Object localObject2;
    int m;
    if (this.mPlaying)
    {
      k = 0;
      j = 0;
      while (j < this.mPlayers.size())
      {
        localObject1 = getPlayer(j);
        if (Player.-get0((Player)localObject1))
        {
          int n;
          int i1;
          if (((Player)localObject1).below(this.mHeight))
          {
            if (DEBUG_IDDQD)
            {
              poke(j);
              unpoke(j);
            }
          }
          else
          {
            i = 0;
            n = this.mObstaclesInPlay.size();
            i1 = n - 1;
            if (n <= 0) {
              break label353;
            }
            localObject2 = (Obstacle)this.mObstaclesInPlay.get(i1);
            if ((((Obstacle)localObject2).intersects((Player)localObject1)) && (!DEBUG_IDDQD)) {
              break label322;
            }
            m = i;
            if (((Obstacle)localObject2).cleared((Player)localObject1))
            {
              m = i;
              if (!(localObject2 instanceof Stem)) {}
            }
          }
          for (m = Math.max(i, ((Stem)localObject2).id);; m = i)
          {
            n = i1;
            i = m;
            break label194;
            L("player %d hit the floor", new Object[] { Integer.valueOf(j) });
            thump(j, 80L);
            ((Player)localObject1).die();
            break;
            label322:
            L("player hit an obstacle", new Object[0]);
            thump(j, 80L);
            ((Player)localObject1).die();
          }
          label353:
          if (i > Player.-get1((Player)localObject1)) {
            Player.-wrap0((Player)localObject1, 1);
          }
        }
        i = k;
        if (Player.-get0((Player)localObject1)) {
          i = k + 1;
        }
        j += 1;
        k = i;
      }
      i = j;
      if (k == 0)
      {
        stop();
        MetricsLogger.count(getContext(), "egg_mland_taps", this.mTaps);
        this.mTaps = 0;
        m = this.mPlayers.size();
        k = 0;
        for (;;)
        {
          i = j;
          if (k >= m) {
            break;
          }
          localObject1 = (Player)this.mPlayers.get(k);
          MetricsLogger.histogram(getContext(), "egg_mland_score", ((Player)localObject1).getScore());
          k += 1;
        }
        j = i - 1;
        if (i <= 0) {
          break label621;
        }
        localObject1 = getChildAt(j);
        if (!(localObject1 instanceof Obstacle)) {
          break label564;
        }
        i = j;
        if (((View)localObject1).getTranslationX() + ((View)localObject1).getWidth() < 0.0F)
        {
          removeViewAt(j);
          this.mObstaclesInPlay.remove(localObject1);
          i = j;
        }
      }
    }
    for (;;)
    {
      break;
      label564:
      i = j;
      if ((localObject1 instanceof Scenery))
      {
        localObject2 = (Scenery)localObject1;
        i = j;
        if (((View)localObject1).getTranslationX() + ((Scenery)localObject2).w < 0.0F)
        {
          ((View)localObject1).setTranslationX(getWidth());
          i = j;
        }
      }
    }
    label621:
    if ((this.mPlaying) && (this.t - this.mLastPipeTime > PARAMS.OBSTACLE_PERIOD))
    {
      this.mLastPipeTime = this.t;
      this.mCurrentPipeId += 1;
      i = (int)(frand() * (this.mHeight - PARAMS.OBSTACLE_MIN * 2 - PARAMS.OBSTACLE_GAP)) + PARAMS.OBSTACLE_MIN;
      j = (PARAMS.OBSTACLE_WIDTH - PARAMS.OBSTACLE_STEM_WIDTH) / 2;
      k = PARAMS.OBSTACLE_WIDTH / 2;
      m = irand(0, 250);
      localObject1 = new Stem(getContext(), i - k, false);
      addView((View)localObject1, new FrameLayout.LayoutParams(PARAMS.OBSTACLE_STEM_WIDTH, (int)((Obstacle)localObject1).h, 51));
      ((Obstacle)localObject1).setTranslationX(this.mWidth + j);
      ((Obstacle)localObject1).setTranslationY(-((Obstacle)localObject1).h - k);
      ((Obstacle)localObject1).setTranslationZ(PARAMS.OBSTACLE_Z * 0.75F);
      ((Obstacle)localObject1).animate().translationY(0.0F).setStartDelay(m).setDuration(250L);
      this.mObstaclesInPlay.add(localObject1);
      localObject2 = new Pop(getContext(), PARAMS.OBSTACLE_WIDTH);
      addView((View)localObject2, new FrameLayout.LayoutParams(PARAMS.OBSTACLE_WIDTH, PARAMS.OBSTACLE_WIDTH, 51));
      ((Obstacle)localObject2).setTranslationX(this.mWidth);
      ((Obstacle)localObject2).setTranslationY(-PARAMS.OBSTACLE_WIDTH);
      ((Obstacle)localObject2).setTranslationZ(PARAMS.OBSTACLE_Z);
      ((Obstacle)localObject2).setScaleX(0.25F);
      ((Obstacle)localObject2).setScaleY(-0.25F);
      ((Obstacle)localObject2).animate().translationY(((Obstacle)localObject1).h - j).scaleX(1.0F).scaleY(-1.0F).setStartDelay(m).setDuration(250L);
      this.mObstaclesInPlay.add(localObject2);
      m = irand(0, 250);
      localObject1 = new Stem(getContext(), this.mHeight - i - PARAMS.OBSTACLE_GAP - k, true);
      addView((View)localObject1, new FrameLayout.LayoutParams(PARAMS.OBSTACLE_STEM_WIDTH, (int)((Obstacle)localObject1).h, 51));
      ((Obstacle)localObject1).setTranslationX(this.mWidth + j);
      ((Obstacle)localObject1).setTranslationY(this.mHeight + k);
      ((Obstacle)localObject1).setTranslationZ(PARAMS.OBSTACLE_Z * 0.75F);
      ((Obstacle)localObject1).animate().translationY(this.mHeight - ((Obstacle)localObject1).h).setStartDelay(m).setDuration(400L);
      this.mObstaclesInPlay.add(localObject1);
      localObject2 = new Pop(getContext(), PARAMS.OBSTACLE_WIDTH);
      addView((View)localObject2, new FrameLayout.LayoutParams(PARAMS.OBSTACLE_WIDTH, PARAMS.OBSTACLE_WIDTH, 51));
      ((Obstacle)localObject2).setTranslationX(this.mWidth);
      ((Obstacle)localObject2).setTranslationY(this.mHeight);
      ((Obstacle)localObject2).setTranslationZ(PARAMS.OBSTACLE_Z);
      ((Obstacle)localObject2).setScaleX(0.25F);
      ((Obstacle)localObject2).setScaleY(0.25F);
      ((Obstacle)localObject2).animate().translationY(this.mHeight - ((Obstacle)localObject1).h - k).scaleX(1.0F).scaleY(1.0F).setStartDelay(m).setDuration(400L);
      this.mObstaclesInPlay.add(localObject2);
    }
    invalidate();
  }
  
  private void thump(int paramInt, long paramLong)
  {
    if (this.mAudioManager.getRingerMode() == 0) {
      return;
    }
    if (paramInt < this.mGameControllers.size())
    {
      InputDevice localInputDevice = InputDevice.getDevice(((Integer)this.mGameControllers.get(paramInt)).intValue());
      if ((localInputDevice != null) && (localInputDevice.getVibrator().hasVibrator()))
      {
        localInputDevice.getVibrator().vibrate(((float)paramLong * 2.0F), this.mAudioAttrs);
        return;
      }
    }
    this.mVibrator.vibrate(paramLong, this.mAudioAttrs);
  }
  
  private void unpoke(int paramInt)
  {
    L("unboost(%d)", new Object[] { Integer.valueOf(paramInt) });
    Player localPlayer;
    if ((!this.mFrozen) && (this.mAnimating) && (this.mPlaying))
    {
      localPlayer = getPlayer(paramInt);
      if (localPlayer != null) {}
    }
    else
    {
      return;
    }
    localPlayer.unboost();
  }
  
  public void addPlayer()
  {
    if (getNumPlayers() == 6) {
      return;
    }
    addPlayerInternal(Player.create(this));
  }
  
  public int getControllerPlayer(int paramInt)
  {
    paramInt = this.mGameControllers.indexOf(Integer.valueOf(paramInt));
    if ((paramInt < 0) || (paramInt >= this.mPlayers.size())) {
      return 0;
    }
    return paramInt;
  }
  
  public ArrayList getGameControllers()
  {
    this.mGameControllers.clear();
    int[] arrayOfInt = InputDevice.getDeviceIds();
    int i = 0;
    int j = arrayOfInt.length;
    while (i < j)
    {
      int k = arrayOfInt[i];
      if ((isGamePad(InputDevice.getDevice(k))) && (!this.mGameControllers.contains(Integer.valueOf(k)))) {
        this.mGameControllers.add(Integer.valueOf(k));
      }
      i += 1;
    }
    return this.mGameControllers;
  }
  
  public float getGameTime()
  {
    return this.t;
  }
  
  public int getNumPlayers()
  {
    return this.mPlayers.size();
  }
  
  public Player getPlayer(int paramInt)
  {
    if (paramInt < this.mPlayers.size()) {
      return (Player)this.mPlayers.get(paramInt);
    }
    return null;
  }
  
  public void hideSplash()
  {
    if ((this.mSplash != null) && (this.mSplash.getVisibility() == 0))
    {
      this.mSplash.setClickable(false);
      this.mSplash.animate().alpha(0.0F).translationZ(0.0F).setDuration(300L).withEndAction(new Runnable()
      {
        public void run()
        {
          MLand.-get4(MLand.this).setVisibility(8);
        }
      });
    }
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    dp = getResources().getDisplayMetrics().density;
    reset();
    start(false);
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    Iterator localIterator = this.mPlayers.iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      if (Player.-get3(localPlayer) > 0.0F)
      {
        this.mTouchPaint.setColor(localPlayer.color & 0x80FFFFFF);
        this.mPlayerTracePaint.setColor(localPlayer.color & 0x80FFFFFF);
        float f1 = Player.-get3(localPlayer);
        float f2 = Player.-get4(localPlayer);
        paramCanvas.drawCircle(f1, f2, 100.0F, this.mTouchPaint);
        float f3 = localPlayer.getX() + localPlayer.getPivotX();
        float f4 = localPlayer.getY() + localPlayer.getPivotY();
        float f5 = 1.5707964F - (float)Math.atan2(f3 - f1, f4 - f2);
        paramCanvas.drawLine((float)(f1 + Math.cos(f5) * 100.0D), (float)(f2 + Math.sin(f5) * 100.0D), f3, f4, this.mPlayerTracePaint);
      }
    }
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    L("generic: %s", new Object[] { paramMotionEvent });
    return false;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    L("keyDown: %d", new Object[] { Integer.valueOf(paramInt) });
    switch (paramInt)
    {
    default: 
      return false;
    }
    poke(getControllerPlayer(paramKeyEvent.getDeviceId()));
    return true;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    L("keyDown: %d", new Object[] { Integer.valueOf(paramInt) });
    switch (paramInt)
    {
    default: 
      return false;
    }
    unpoke(getControllerPlayer(paramKeyEvent.getDeviceId()));
    return true;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    dp = getResources().getDisplayMetrics().density;
    stop();
    reset();
    start(false);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    L("touch: %s", new Object[] { paramMotionEvent });
    int i = paramMotionEvent.getActionIndex();
    float f1 = paramMotionEvent.getX(i);
    float f2 = paramMotionEvent.getY(i);
    int j = (int)(getNumPlayers() * (f1 / getWidth()));
    i = j;
    if (this.mFlipped) {
      i = getNumPlayers() - 1 - j;
    }
    switch (paramMotionEvent.getActionMasked())
    {
    case 2: 
    case 3: 
    case 4: 
    default: 
      return false;
    case 0: 
    case 5: 
      poke(i, f1, f2);
      return true;
    }
    unpoke(i);
    return true;
  }
  
  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    L("trackball: %s", new Object[] { paramMotionEvent });
    switch (paramMotionEvent.getAction())
    {
    default: 
      return false;
    case 0: 
      poke(0);
      return true;
    }
    unpoke(0);
    return true;
  }
  
  public void removePlayer()
  {
    if (getNumPlayers() == 1) {
      return;
    }
    removePlayerInternal((Player)this.mPlayers.get(this.mPlayers.size() - 1));
  }
  
  public void reset()
  {
    L("reset", new Object[0]);
    Object localObject1 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, SKIES[this.mTimeOfDay]);
    ((Drawable)localObject1).setDither(true);
    setBackground((Drawable)localObject1);
    boolean bool;
    if (frand() > 0.5F)
    {
      bool = true;
      this.mFlipped = bool;
      if (!this.mFlipped) {
        break label116;
      }
    }
    int j;
    label116:
    for (int i = -1;; i = 1)
    {
      setScaleX(i);
      for (i = getChildCount();; i = j)
      {
        j = i - 1;
        if (i <= 0) {
          break;
        }
        if ((getChildAt(j) instanceof GameView)) {
          removeViewAt(j);
        }
      }
      bool = false;
      break;
    }
    this.mObstaclesInPlay.clear();
    this.mCurrentPipeId = 0;
    this.mWidth = getWidth();
    this.mHeight = getHeight();
    label266:
    label303:
    float f;
    label319:
    Object localObject2;
    label356:
    label375:
    label462:
    int k;
    if ((this.mTimeOfDay == 0) || (this.mTimeOfDay == 3)) {
      if (frand() > 0.25D)
      {
        i = 1;
        if (i != 0)
        {
          localObject1 = new Star(getContext());
          ((Star)localObject1).setBackgroundResource(2130839021);
          j = getResources().getDimensionPixelSize(2131755686);
          ((Star)localObject1).setTranslationX(frand(j, this.mWidth - j));
          if (this.mTimeOfDay != 0) {
            break label611;
          }
          ((Star)localObject1).setTranslationY(frand(j, this.mHeight * 0.66F));
          ((Star)localObject1).getBackground().setTint(0);
          addView((View)localObject1, new FrameLayout.LayoutParams(j, j));
        }
        if (i == 0)
        {
          if ((this.mTimeOfDay != 1) && (this.mTimeOfDay != 2)) {
            break label660;
          }
          i = 1;
          f = frand();
          if ((i == 0) || (f >= 0.75F)) {
            break label665;
          }
          localObject1 = new Star(getContext());
          ((Star)localObject1).setBackgroundResource(2130838055);
          localObject2 = ((Star)localObject1).getBackground();
          if (i == 0) {
            break label676;
          }
          i = 255;
          ((Drawable)localObject2).setAlpha(i);
          if (frand() <= 0.5D) {
            break label683;
          }
          i = -1;
          ((Star)localObject1).setScaleX(i);
          ((Star)localObject1).setRotation(((Star)localObject1).getScaleX() * frand(5.0F, 30.0F));
          i = getResources().getDimensionPixelSize(2131755686);
          ((Star)localObject1).setTranslationX(frand(i, this.mWidth - i));
          ((Star)localObject1).setTranslationY(frand(i, this.mHeight - i));
          addView((View)localObject1, new FrameLayout.LayoutParams(i, i));
        }
        k = this.mHeight / 6;
        if (frand() >= 0.25D) {
          break label688;
        }
        i = 1;
        label484:
        j = 0;
        label486:
        if (j >= 20) {
          break label971;
        }
        f = frand();
        if ((f >= 0.3D) || (this.mTimeOfDay == 0)) {
          break label693;
        }
        localObject2 = new Star(getContext());
        localObject1 = new FrameLayout.LayoutParams(((Scenery)localObject2).w, ((Scenery)localObject2).h);
        if (!(localObject2 instanceof Building)) {
          break label905;
        }
        ((FrameLayout.LayoutParams)localObject1).gravity = 80;
      }
    }
    for (;;)
    {
      addView((View)localObject2, (ViewGroup.LayoutParams)localObject1);
      ((Scenery)localObject2).setTranslationX(frand(-((FrameLayout.LayoutParams)localObject1).width, this.mWidth + ((FrameLayout.LayoutParams)localObject1).width));
      j += 1;
      break label486;
      i = 0;
      break;
      i = 0;
      break;
      label611:
      ((Star)localObject1).setTranslationY(frand(this.mHeight * 0.66F, this.mHeight - j));
      ((Star)localObject1).getBackground().setTintMode(PorterDuff.Mode.SRC_ATOP);
      ((Star)localObject1).getBackground().setTint(-1056997376);
      break label266;
      label660:
      i = 0;
      break label303;
      label665:
      if (f >= 0.5F) {
        break label462;
      }
      break label319;
      label676:
      i = 128;
      break label356;
      label683:
      i = 1;
      break label375;
      label688:
      i = 0;
      break label484;
      label693:
      if ((f >= 0.6D) || (i != 0)) {
        switch (this.mScene)
        {
        default: 
          localObject1 = new Building(getContext());
        }
      }
      for (;;)
      {
        ((Scenery)localObject1).z = (j / 20.0F);
        ((Scenery)localObject1).v = (((Scenery)localObject1).z * 0.85F);
        if (this.mScene == 0)
        {
          ((Scenery)localObject1).setBackgroundColor(-7829368);
          ((Scenery)localObject1).h = irand(PARAMS.BUILDING_HEIGHT_MIN, k);
        }
        int m = (int)(((Scenery)localObject1).z * 255.0F);
        Drawable localDrawable = ((Scenery)localObject1).getBackground();
        localObject2 = localObject1;
        if (localDrawable == null) {
          break;
        }
        localDrawable.setColorFilter(Color.rgb(m, m, m), PorterDuff.Mode.MULTIPLY);
        localObject2 = localObject1;
        break;
        localObject2 = new Cloud(getContext());
        break;
        localObject1 = new Mountain(getContext());
        continue;
        localObject1 = new Cactus(getContext());
      }
      label905:
      ((FrameLayout.LayoutParams)localObject1).gravity = 48;
      f = frand();
      if ((localObject2 instanceof Star)) {
        ((FrameLayout.LayoutParams)localObject1).topMargin = ((int)(f * f * this.mHeight));
      } else {
        ((FrameLayout.LayoutParams)localObject1).topMargin = ((int)(1.0F - f * f * this.mHeight / 2.0F) + this.mHeight / 2);
      }
    }
    label971:
    localObject1 = this.mPlayers.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Player)((Iterator)localObject1).next();
      addView((View)localObject2);
      ((Player)localObject2).reset();
    }
    realignPlayers();
    if (this.mAnim != null) {
      this.mAnim.cancel();
    }
    this.mAnim = new TimeAnimator();
    this.mAnim.setTimeListener(new TimeAnimator.TimeListener()
    {
      public void onTimeUpdate(TimeAnimator paramAnonymousTimeAnimator, long paramAnonymousLong1, long paramAnonymousLong2)
      {
        MLand.-wrap1(MLand.this, paramAnonymousLong1, paramAnonymousLong2);
      }
    });
  }
  
  public void setScoreFieldHolder(ViewGroup paramViewGroup)
  {
    this.mScoreFields = paramViewGroup;
    if (paramViewGroup != null)
    {
      paramViewGroup = new LayoutTransition();
      paramViewGroup.setDuration(250L);
      this.mScoreFields.setLayoutTransition(paramViewGroup);
    }
    paramViewGroup = this.mPlayers.iterator();
    while (paramViewGroup.hasNext())
    {
      Player localPlayer = (Player)paramViewGroup.next();
      this.mScoreFields.addView(Player.-get2(localPlayer), new ViewGroup.MarginLayoutParams(-2, -1));
    }
  }
  
  public void setSplash(View paramView)
  {
    this.mSplash = paramView;
  }
  
  public void setupPlayers(int paramInt)
  {
    clearPlayers();
    int i = 0;
    while (i < paramInt)
    {
      addPlayerInternal(Player.create(this));
      i += 1;
    }
  }
  
  public void showSplash()
  {
    if ((this.mSplash != null) && (this.mSplash.getVisibility() != 0))
    {
      this.mSplash.setClickable(true);
      this.mSplash.setAlpha(0.0F);
      this.mSplash.setVisibility(0);
      this.mSplash.animate().alpha(1.0F).setDuration(1000L);
      this.mSplash.findViewById(2131952060).setAlpha(1.0F);
      this.mSplash.findViewById(2131952061).setAlpha(0.0F);
      this.mSplash.findViewById(2131952059).setEnabled(true);
      this.mSplash.findViewById(2131952059).requestFocus();
    }
  }
  
  public void start(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (Object localObject = "true";; localObject = "false")
    {
      L("start(startPlaying=%s)", new Object[] { localObject });
      if ((paramBoolean) && (this.mCountdown <= 0))
      {
        showSplash();
        this.mSplash.findViewById(2131952059).setEnabled(false);
        localObject = this.mSplash.findViewById(2131952060);
        final TextView localTextView = (TextView)this.mSplash.findViewById(2131952061);
        ((View)localObject).animate().alpha(0.0F);
        localTextView.animate().alpha(1.0F);
        this.mCountdown = 3;
        post(new Runnable()
        {
          public void run()
          {
            if (MLand.-get1(MLand.this) == 0) {
              MLand.this.startPlaying();
            }
            for (;;)
            {
              localTextView.setText(String.valueOf(MLand.-get1(MLand.this)));
              MLand localMLand = MLand.this;
              MLand.-set0(localMLand, MLand.-get1(localMLand) - 1);
              return;
              MLand.this.postDelayed(this, 500L);
            }
          }
        });
      }
      localObject = this.mPlayers.iterator();
      while (((Iterator)localObject).hasNext()) {
        ((Player)((Iterator)localObject).next()).setVisibility(4);
      }
    }
    if (!this.mAnimating)
    {
      this.mAnim.start();
      this.mAnimating = true;
    }
  }
  
  public void startPlaying()
  {
    this.mPlaying = true;
    this.t = 0.0F;
    this.mLastPipeTime = (getGameTime() - PARAMS.OBSTACLE_PERIOD);
    hideSplash();
    realignPlayers();
    this.mTaps = 0;
    int j = this.mPlayers.size();
    MetricsLogger.histogram(getContext(), "egg_mland_players", j);
    int i = 0;
    while (i < j)
    {
      Player localPlayer = (Player)this.mPlayers.get(i);
      localPlayer.setVisibility(0);
      localPlayer.reset();
      localPlayer.start();
      localPlayer.boost(-1.0F, -1.0F);
      localPlayer.unboost();
      i += 1;
    }
  }
  
  public void stop()
  {
    if (this.mAnimating)
    {
      this.mAnim.cancel();
      this.mAnim = null;
      this.mAnimating = false;
      this.mPlaying = false;
      this.mTimeOfDay = irand(0, SKIES.length - 1);
      this.mScene = irand(0, 3);
      this.mFrozen = true;
      Iterator localIterator = this.mPlayers.iterator();
      while (localIterator.hasNext()) {
        ((Player)localIterator.next()).die();
      }
      postDelayed(new Runnable()
      {
        public void run()
        {
          MLand.-set1(MLand.this, false);
        }
      }, 250L);
    }
  }
  
  public boolean willNotDraw()
  {
    return !DEBUG;
  }
  
  private class Building
    extends MLand.Scenery
  {
    public Building(Context paramContext)
    {
      super(paramContext);
      this.w = MLand.irand(MLand.-get0().BUILDING_WIDTH_MIN, MLand.-get0().BUILDING_WIDTH_MAX);
      this.h = 0;
    }
  }
  
  private class Cactus
    extends MLand.Building
  {
    public Cactus(Context paramContext)
    {
      super(paramContext);
      setBackgroundResource(MLand.pick(MLand.CACTI));
      int i = MLand.irand(MLand.-get0().BUILDING_WIDTH_MAX / 4, MLand.-get0().BUILDING_WIDTH_MAX / 2);
      this.h = i;
      this.w = i;
    }
  }
  
  private class Cloud
    extends MLand.Scenery
  {
    public Cloud(Context paramContext)
    {
      super(paramContext);
      if (MLand.frand() < 0.01F) {}
      for (int i = 2130837608;; i = 2130837607)
      {
        setBackgroundResource(i);
        getBackground().setAlpha(64);
        i = MLand.irand(MLand.-get0().CLOUD_SIZE_MIN, MLand.-get0().CLOUD_SIZE_MAX);
        this.h = i;
        this.w = i;
        this.z = 0.0F;
        this.v = MLand.frand(0.15F, 0.5F);
        return;
      }
    }
  }
  
  private static abstract interface GameView
  {
    public abstract void step(long paramLong1, long paramLong2, float paramFloat1, float paramFloat2);
  }
  
  private class Mountain
    extends MLand.Building
  {
    public Mountain(Context paramContext)
    {
      super(paramContext);
      setBackgroundResource(MLand.pick(MLand.MOUNTAINS));
      int i = MLand.irand(MLand.-get0().BUILDING_WIDTH_MAX / 2, MLand.-get0().BUILDING_WIDTH_MAX);
      this.h = i;
      this.w = i;
      this.z = 0.0F;
    }
  }
  
  private class Obstacle
    extends View
    implements MLand.GameView
  {
    public float h;
    public final Rect hitRect = new Rect();
    
    public Obstacle(Context paramContext, float paramFloat)
    {
      super();
      setBackgroundColor(-65536);
      this.h = paramFloat;
    }
    
    public boolean cleared(MLand.Player paramPlayer)
    {
      int j = paramPlayer.corners.length / 2;
      int i = 0;
      while (i < j)
      {
        int k = (int)paramPlayer.corners[(i * 2)];
        if (this.hitRect.right >= k) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    
    public boolean intersects(MLand.Player paramPlayer)
    {
      int j = paramPlayer.corners.length / 2;
      int i = 0;
      while (i < j)
      {
        int k = (int)paramPlayer.corners[(i * 2)];
        int m = (int)paramPlayer.corners[(i * 2 + 1)];
        if (this.hitRect.contains(k, m)) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public void step(long paramLong1, long paramLong2, float paramFloat1, float paramFloat2)
    {
      setTranslationX(getTranslationX() - MLand.-get0().TRANSLATION_PER_SEC * paramFloat2);
      getHitRect(this.hitRect);
    }
  }
  
  private static class Params
  {
    public int BOOST_DV;
    public int BUILDING_HEIGHT_MIN;
    public int BUILDING_WIDTH_MAX;
    public int BUILDING_WIDTH_MIN;
    public int CLOUD_SIZE_MAX;
    public int CLOUD_SIZE_MIN;
    public int G;
    public float HUD_Z;
    public int MAX_V;
    public int OBSTACLE_GAP;
    public int OBSTACLE_MIN;
    public int OBSTACLE_PERIOD;
    public int OBSTACLE_SPACING;
    public int OBSTACLE_STEM_WIDTH;
    public int OBSTACLE_WIDTH;
    public float OBSTACLE_Z;
    public int PLAYER_HIT_SIZE;
    public int PLAYER_SIZE;
    public float PLAYER_Z;
    public float PLAYER_Z_BOOST;
    public float SCENERY_Z;
    public int STAR_SIZE_MAX;
    public int STAR_SIZE_MIN;
    public float TRANSLATION_PER_SEC;
    
    public Params(Resources paramResources)
    {
      this.TRANSLATION_PER_SEC = paramResources.getDimension(2131755673);
      this.OBSTACLE_SPACING = paramResources.getDimensionPixelSize(2131755672);
      this.OBSTACLE_PERIOD = ((int)(this.OBSTACLE_SPACING / this.TRANSLATION_PER_SEC));
      this.BOOST_DV = paramResources.getDimensionPixelSize(2131755674);
      this.PLAYER_HIT_SIZE = paramResources.getDimensionPixelSize(2131755675);
      this.PLAYER_SIZE = paramResources.getDimensionPixelSize(2131755676);
      this.OBSTACLE_WIDTH = paramResources.getDimensionPixelSize(2131755677);
      this.OBSTACLE_STEM_WIDTH = paramResources.getDimensionPixelSize(2131755678);
      this.OBSTACLE_GAP = paramResources.getDimensionPixelSize(2131755679);
      this.OBSTACLE_MIN = paramResources.getDimensionPixelSize(2131755680);
      this.BUILDING_HEIGHT_MIN = paramResources.getDimensionPixelSize(2131755683);
      this.BUILDING_WIDTH_MIN = paramResources.getDimensionPixelSize(2131755681);
      this.BUILDING_WIDTH_MAX = paramResources.getDimensionPixelSize(2131755682);
      this.CLOUD_SIZE_MIN = paramResources.getDimensionPixelSize(2131755684);
      this.CLOUD_SIZE_MAX = paramResources.getDimensionPixelSize(2131755685);
      this.STAR_SIZE_MIN = paramResources.getDimensionPixelSize(2131755688);
      this.STAR_SIZE_MAX = paramResources.getDimensionPixelSize(2131755689);
      this.G = paramResources.getDimensionPixelSize(2131755690);
      this.MAX_V = paramResources.getDimensionPixelSize(2131755691);
      this.SCENERY_Z = paramResources.getDimensionPixelSize(2131755692);
      this.OBSTACLE_Z = paramResources.getDimensionPixelSize(2131755693);
      this.PLAYER_Z = paramResources.getDimensionPixelSize(2131755694);
      this.PLAYER_Z_BOOST = paramResources.getDimensionPixelSize(2131755695);
      this.HUD_Z = paramResources.getDimensionPixelSize(2131755696);
      if (this.OBSTACLE_MIN <= this.OBSTACLE_WIDTH / 2)
      {
        MLand.L("error: obstacles might be too short, adjusting", new Object[0]);
        this.OBSTACLE_MIN = (this.OBSTACLE_WIDTH / 2 + 1);
      }
    }
  }
  
  private static class Player
    extends ImageView
    implements MLand.GameView
  {
    static int sNextColor = 0;
    public int color;
    public final float[] corners = new float[this.sHull.length];
    public float dv;
    private boolean mAlive;
    private boolean mBoosting;
    private MLand mLand;
    private int mScore;
    private TextView mScoreField;
    private float mTouchX = -1.0F;
    private float mTouchY = -1.0F;
    private final int[] sColors = { -2407369, -12879641, -740352, -15753896, -8710016, -6381922 };
    private final float[] sHull = { 0.3F, 0.0F, 0.7F, 0.0F, 0.92F, 0.33F, 0.92F, 0.75F, 0.6F, 1.0F, 0.4F, 1.0F, 0.08F, 0.75F, 0.08F, 0.33F };
    
    public Player(Context paramContext)
    {
      super();
      setBackgroundResource(2130837587);
      getBackground().setTintMode(PorterDuff.Mode.SRC_ATOP);
      paramContext = this.sColors;
      int i = sNextColor;
      sNextColor = i + 1;
      this.color = paramContext[(i % this.sColors.length)];
      getBackground().setTint(this.color);
      setOutlineProvider(new ViewOutlineProvider()
      {
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          int i = paramAnonymousView.getWidth();
          int j = paramAnonymousView.getHeight();
          int k = (int)(i * 0.3F);
          int m = (int)(j * 0.2F);
          paramAnonymousOutline.setRect(k, m, i - k, j - m);
        }
      });
    }
    
    private void addScore(int paramInt)
    {
      setScore(this.mScore + paramInt);
    }
    
    public static Player create(MLand paramMLand)
    {
      Player localPlayer = new Player(paramMLand.getContext());
      localPlayer.mLand = paramMLand;
      localPlayer.reset();
      localPlayer.setVisibility(4);
      paramMLand.addView(localPlayer, new FrameLayout.LayoutParams(MLand.-get0().PLAYER_SIZE, MLand.-get0().PLAYER_SIZE));
      return localPlayer;
    }
    
    private void setScore(int paramInt)
    {
      this.mScore = paramInt;
      TextView localTextView;
      if (this.mScoreField != null)
      {
        localTextView = this.mScoreField;
        if (!MLand.DEBUG_IDDQD) {
          break label32;
        }
      }
      label32:
      for (String str = "??";; str = String.valueOf(paramInt))
      {
        localTextView.setText(str);
        return;
      }
    }
    
    public boolean below(int paramInt)
    {
      int j = this.corners.length / 2;
      int i = 0;
      while (i < j)
      {
        if ((int)this.corners[(i * 2 + 1)] >= paramInt) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public void boost()
    {
      this.mBoosting = true;
      this.dv = (-MLand.-get0().BOOST_DV);
      animate().cancel();
      animate().scaleX(1.25F).scaleY(1.25F).translationZ(MLand.-get0().PLAYER_Z_BOOST).setDuration(100L);
      setScaleX(1.25F);
      setScaleY(1.25F);
    }
    
    public void boost(float paramFloat1, float paramFloat2)
    {
      this.mTouchX = paramFloat1;
      this.mTouchY = paramFloat2;
      boost();
    }
    
    public void die()
    {
      this.mAlive = false;
      if (this.mScoreField != null) {}
    }
    
    public int getScore()
    {
      return this.mScore;
    }
    
    public void prepareCheckIntersections()
    {
      int j = (MLand.-get0().PLAYER_SIZE - MLand.-get0().PLAYER_HIT_SIZE) / 2;
      int k = MLand.-get0().PLAYER_HIT_SIZE;
      int m = this.sHull.length / 2;
      int i = 0;
      while (i < m)
      {
        this.corners[(i * 2)] = (k * this.sHull[(i * 2)] + j);
        this.corners[(i * 2 + 1)] = (k * this.sHull[(i * 2 + 1)] + j);
        i += 1;
      }
      getMatrix().mapPoints(this.corners);
    }
    
    public void reset()
    {
      setY(MLand.-get3(this.mLand) / 2 + (int)(Math.random() * MLand.-get0().PLAYER_SIZE) - MLand.-get0().PLAYER_SIZE / 2);
      setScore(0);
      setScoreField(this.mScoreField);
      this.mBoosting = false;
      this.dv = 0.0F;
    }
    
    public void setScoreField(TextView paramTextView)
    {
      this.mScoreField = paramTextView;
      if (paramTextView != null)
      {
        setScore(this.mScore);
        this.mScoreField.getBackground().setColorFilter(this.color, PorterDuff.Mode.SRC_ATOP);
        paramTextView = this.mScoreField;
        if (MLand.-wrap0(this.color) <= 0.7F) {
          break label62;
        }
      }
      label62:
      for (int i = -16777216;; i = -1)
      {
        paramTextView.setTextColor(i);
        return;
      }
    }
    
    public void start()
    {
      this.mAlive = true;
    }
    
    public void step(long paramLong1, long paramLong2, float paramFloat1, float paramFloat2)
    {
      if (!this.mAlive)
      {
        setTranslationX(getTranslationX() - MLand.-get0().TRANSLATION_PER_SEC * paramFloat2);
        return;
      }
      if (this.mBoosting)
      {
        this.dv = (-MLand.-get0().BOOST_DV);
        if (this.dv >= -MLand.-get0().MAX_V) {
          break label174;
        }
        this.dv = (-MLand.-get0().MAX_V);
      }
      for (;;)
      {
        paramFloat2 = getTranslationY() + this.dv * paramFloat2;
        paramFloat1 = paramFloat2;
        if (paramFloat2 < 0.0F) {
          paramFloat1 = 0.0F;
        }
        setTranslationY(paramFloat1);
        setRotation(MLand.lerp(MLand.clamp(MLand.rlerp(this.dv, MLand.-get0().MAX_V, MLand.-get0().MAX_V * -1)), 90.0F, -90.0F) + 90.0F);
        prepareCheckIntersections();
        return;
        this.dv += MLand.-get0().G;
        break;
        label174:
        if (this.dv > MLand.-get0().MAX_V) {
          this.dv = MLand.-get0().MAX_V;
        }
      }
    }
    
    public void unboost()
    {
      this.mBoosting = false;
      this.mTouchY = -1.0F;
      this.mTouchX = -1.0F;
      animate().cancel();
      animate().scaleX(1.0F).scaleY(1.0F).translationZ(MLand.-get0().PLAYER_Z).setDuration(200L);
    }
  }
  
  private class Pop
    extends MLand.Obstacle
  {
    Drawable antenna;
    int cx;
    int cy;
    Drawable eyes;
    int mRotate;
    Drawable mouth;
    int r;
    
    public Pop(Context paramContext, float paramFloat)
    {
      super(paramContext, paramFloat);
      setBackgroundResource(2130838050);
      this.antenna = paramContext.getDrawable(MLand.pick(MLand.ANTENNAE));
      if (MLand.frand() > 0.5F)
      {
        this.eyes = paramContext.getDrawable(MLand.pick(MLand.EYES));
        if (MLand.frand() > 0.8F) {
          this.mouth = paramContext.getDrawable(MLand.pick(MLand.MOUTHS));
        }
      }
      setOutlineProvider(new ViewOutlineProvider()
      {
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          int i = (int)(MLand.Pop.this.getWidth() * 1.0F / 6.0F);
          paramAnonymousOutline.setOval(i, i, MLand.Pop.this.getWidth() - i, MLand.Pop.this.getHeight() - i);
        }
      });
    }
    
    public boolean intersects(MLand.Player paramPlayer)
    {
      int j = paramPlayer.corners.length / 2;
      int i = 0;
      while (i < j)
      {
        int k = (int)paramPlayer.corners[(i * 2)];
        int m = (int)paramPlayer.corners[(i * 2 + 1)];
        if (Math.hypot(k - this.cx, m - this.cy) <= this.r) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public void onDraw(Canvas paramCanvas)
    {
      super.onDraw(paramCanvas);
      if (this.antenna != null)
      {
        this.antenna.setBounds(0, 0, paramCanvas.getWidth(), paramCanvas.getHeight());
        this.antenna.draw(paramCanvas);
      }
      if (this.eyes != null)
      {
        this.eyes.setBounds(0, 0, paramCanvas.getWidth(), paramCanvas.getHeight());
        this.eyes.draw(paramCanvas);
      }
      if (this.mouth != null)
      {
        this.mouth.setBounds(0, 0, paramCanvas.getWidth(), paramCanvas.getHeight());
        this.mouth.draw(paramCanvas);
      }
    }
    
    public void step(long paramLong1, long paramLong2, float paramFloat1, float paramFloat2)
    {
      super.step(paramLong1, paramLong2, paramFloat1, paramFloat2);
      if (this.mRotate != 0) {
        setRotation(getRotation() + 45.0F * paramFloat2 * this.mRotate);
      }
      this.cx = ((this.hitRect.left + this.hitRect.right) / 2);
      this.cy = ((this.hitRect.top + this.hitRect.bottom) / 2);
      this.r = (getWidth() / 3);
    }
  }
  
  private class Scenery
    extends FrameLayout
    implements MLand.GameView
  {
    public int h;
    public float v;
    public int w;
    public float z;
    
    public Scenery(Context paramContext)
    {
      super();
    }
    
    public void step(long paramLong1, long paramLong2, float paramFloat1, float paramFloat2)
    {
      setTranslationX(getTranslationX() - MLand.-get0().TRANSLATION_PER_SEC * paramFloat2 * this.v);
    }
  }
  
  private class Star
    extends MLand.Scenery
  {
    public Star(Context paramContext)
    {
      super(paramContext);
      setBackgroundResource(2130838384);
      int i = MLand.irand(MLand.-get0().STAR_SIZE_MIN, MLand.-get0().STAR_SIZE_MAX);
      this.h = i;
      this.w = i;
      this.z = 0.0F;
      this.v = 0.0F;
    }
  }
  
  private class Stem
    extends MLand.Obstacle
  {
    int id = MLand.-get2(MLand.this);
    boolean mDrawShadow;
    GradientDrawable mGradient = new GradientDrawable();
    Path mJandystripe;
    Paint mPaint = new Paint();
    Paint mPaint2;
    Path mShadow = new Path();
    
    public Stem(Context paramContext, float paramFloat, boolean paramBoolean)
    {
      super(paramContext, paramFloat);
      this.mDrawShadow = paramBoolean;
      setBackground(null);
      this.mGradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
      this.mPaint.setColor(-16777216);
      this.mPaint.setColorFilter(new PorterDuffColorFilter(570425344, PorterDuff.Mode.MULTIPLY));
      if (MLand.frand() < 0.01F)
      {
        this.mGradient.setColors(new int[] { -1, -2236963 });
        this.mJandystripe = new Path();
        this.mPaint2 = new Paint();
        this.mPaint2.setColor(-65536);
        this.mPaint2.setColorFilter(new PorterDuffColorFilter(-65536, PorterDuff.Mode.MULTIPLY));
        return;
      }
      this.mGradient.setColors(new int[] { -4412764, -6190977 });
    }
    
    public void onAttachedToWindow()
    {
      super.onAttachedToWindow();
      setWillNotDraw(false);
      setOutlineProvider(new ViewOutlineProvider()
      {
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          paramAnonymousOutline.setRect(0, 0, MLand.Stem.this.getWidth(), MLand.Stem.this.getHeight());
        }
      });
    }
    
    public void onDraw(Canvas paramCanvas)
    {
      int j = paramCanvas.getWidth();
      int k = paramCanvas.getHeight();
      this.mGradient.setGradientCenter(j * 0.75F, 0.0F);
      this.mGradient.setBounds(0, 0, j, k);
      this.mGradient.draw(paramCanvas);
      if (this.mJandystripe != null)
      {
        this.mJandystripe.reset();
        this.mJandystripe.moveTo(0.0F, j);
        this.mJandystripe.lineTo(j, 0.0F);
        this.mJandystripe.lineTo(j, j * 2);
        this.mJandystripe.lineTo(0.0F, j * 3);
        this.mJandystripe.close();
        int i = 0;
        while (i < k)
        {
          paramCanvas.drawPath(this.mJandystripe, this.mPaint2);
          this.mJandystripe.offset(0.0F, j * 4);
          i += j * 4;
        }
      }
      if (!this.mDrawShadow) {
        return;
      }
      this.mShadow.reset();
      this.mShadow.moveTo(0.0F, 0.0F);
      this.mShadow.lineTo(j, 0.0F);
      this.mShadow.lineTo(j, MLand.-get0().OBSTACLE_WIDTH * 0.4F + j * 1.5F);
      this.mShadow.lineTo(0.0F, MLand.-get0().OBSTACLE_WIDTH * 0.4F);
      this.mShadow.close();
      paramCanvas.drawPath(this.mShadow, this.mPaint);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\egg\MLand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */