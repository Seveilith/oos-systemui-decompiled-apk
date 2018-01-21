package com.android.systemui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewOverlay;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DessertCaseView
  extends FrameLayout
{
  private static final float[] ALPHA_MASK = { 0.0F, 0.0F, 0.0F, 0.0F, 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 255.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F };
  private static final float[] MASK;
  private static final int NUM_PASTRIES;
  private static final int[] PASTRIES;
  private static final int[] RARE_PASTRIES;
  private static final String TAG = DessertCaseView.class.getSimpleName();
  private static final float[] WHITE_MASK = { 0.0F, 0.0F, 0.0F, 0.0F, 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 255.0F, -1.0F, 0.0F, 0.0F, 0.0F, 255.0F };
  private static final int[] XRARE_PASTRIES;
  private static final int[] XXRARE_PASTRIES;
  float[] hsv = { 0.0F, 1.0F, 0.85F };
  private int mCellSize;
  private View[] mCells;
  private int mColumns;
  private SparseArray<Drawable> mDrawables = new SparseArray(NUM_PASTRIES);
  private final Set<Point> mFreeList = new HashSet();
  private final Handler mHandler = new Handler();
  private int mHeight;
  private final Runnable mJuggle = new Runnable()
  {
    public void run()
    {
      int j = DessertCaseView.this.getChildCount();
      int i = 0;
      while (i < 1)
      {
        View localView = DessertCaseView.this.getChildAt((int)(Math.random() * j));
        DessertCaseView.this.place(localView, true);
        i += 1;
      }
      DessertCaseView.this.fillFreeList();
      if (DessertCaseView.-get2(DessertCaseView.this)) {
        DessertCaseView.-get0(DessertCaseView.this).postDelayed(DessertCaseView.-get1(DessertCaseView.this), 2000L);
      }
    }
  };
  private int mRows;
  private boolean mStarted;
  private int mWidth;
  private final HashSet<View> tmpSet = new HashSet();
  
  static
  {
    PASTRIES = new int[] { 2130837660, 2130837646 };
    RARE_PASTRIES = new int[] { 2130837647, 2130837649, 2130837651, 2130837653, 2130837654, 2130837655, 2130837656, 2130837658 };
    XRARE_PASTRIES = new int[] { 2130837661, 2130837650, 2130837652, 2130837659 };
    XXRARE_PASTRIES = new int[] { 2130837662, 2130837648, 2130837657 };
    NUM_PASTRIES = PASTRIES.length + RARE_PASTRIES.length + XRARE_PASTRIES.length + XXRARE_PASTRIES.length;
    MASK = new float[] { 0.0F, 0.0F, 0.0F, 0.0F, 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 255.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F };
  }
  
  public DessertCaseView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DessertCaseView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public DessertCaseView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramAttributeSet = getResources();
    this.mStarted = false;
    this.mCellSize = paramAttributeSet.getDimensionPixelSize(2131755454);
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    if (this.mCellSize < 512) {
      localOptions.inSampleSize = 2;
    }
    localOptions.inMutable = true;
    paramContext = null;
    int[][] arrayOfInt = new int[4][];
    arrayOfInt[0] = PASTRIES;
    arrayOfInt[1] = RARE_PASTRIES;
    arrayOfInt[2] = XRARE_PASTRIES;
    arrayOfInt[3] = XXRARE_PASTRIES;
    int j = arrayOfInt.length;
    paramInt = 0;
    while (paramInt < j)
    {
      int[] arrayOfInt1 = arrayOfInt[paramInt];
      int i = 0;
      int k = arrayOfInt1.length;
      while (i < k)
      {
        int m = arrayOfInt1[i];
        localOptions.inBitmap = paramContext;
        paramContext = BitmapFactory.decodeResource(paramAttributeSet, m, localOptions);
        BitmapDrawable localBitmapDrawable = new BitmapDrawable(paramAttributeSet, convertToAlphaMask(paramContext));
        localBitmapDrawable.setColorFilter(new ColorMatrixColorFilter(ALPHA_MASK));
        localBitmapDrawable.setBounds(0, 0, this.mCellSize, this.mCellSize);
        this.mDrawables.append(m, localBitmapDrawable);
        i += 1;
      }
      paramInt += 1;
    }
  }
  
  private static Bitmap convertToAlphaMask(Bitmap paramBitmap)
  {
    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ALPHA_8);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint();
    localPaint.setColorFilter(new ColorMatrixColorFilter(MASK));
    localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
    return localBitmap;
  }
  
  static float frand()
  {
    return (float)Math.random();
  }
  
  static float frand(float paramFloat1, float paramFloat2)
  {
    return frand() * (paramFloat2 - paramFloat1) + paramFloat1;
  }
  
  private Point[] getOccupied(View paramView)
  {
    int m = ((Integer)paramView.getTag(33554434)).intValue();
    paramView = (Point)paramView.getTag(33554433);
    if ((paramView == null) || (m == 0)) {
      return new Point[0];
    }
    Point[] arrayOfPoint = new Point[m * m];
    int i = 0;
    int j = 0;
    while (j < m)
    {
      int k = 0;
      while (k < m)
      {
        arrayOfPoint[i] = new Point(paramView.x + j, paramView.y + k);
        k += 1;
        i += 1;
      }
      j += 1;
    }
    return arrayOfPoint;
  }
  
  static int irand(int paramInt1, int paramInt2)
  {
    return (int)frand(paramInt1, paramInt2);
  }
  
  private final Animator.AnimatorListener makeHardwareLayerListener(final View paramView)
  {
    new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramView.setLayerType(0, null);
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        paramView.setLayerType(2, null);
        paramView.buildLayer();
      }
    };
  }
  
  public void fillFreeList()
  {
    fillFreeList(500);
  }
  
  public void fillFreeList(int paramInt)
  {
    for (;;)
    {
      float f;
      try
      {
        Context localContext = getContext();
        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(this.mCellSize, this.mCellSize);
        if (this.mFreeList.isEmpty()) {
          break;
        }
        Point localPoint = (Point)this.mFreeList.iterator().next();
        this.mFreeList.remove(localPoint);
        int i = localPoint.x;
        int j = localPoint.y;
        if (this.mCells[(this.mColumns * j + i)] != null) {
          continue;
        }
        final ImageView localImageView = new ImageView(localContext);
        localImageView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            DessertCaseView.this.place(localImageView, true);
            DessertCaseView.this.postDelayed(new Runnable()
            {
              public void run()
              {
                DessertCaseView.this.fillFreeList();
              }
            }, 250L);
          }
        });
        localImageView.setBackgroundColor(random_color());
        f = frand();
        if (f < 5.0E-4F)
        {
          Drawable localDrawable1 = (Drawable)this.mDrawables.get(pick(XXRARE_PASTRIES));
          if (localDrawable1 != null) {
            localImageView.getOverlay().add(localDrawable1);
          }
          i = this.mCellSize;
          localLayoutParams.height = i;
          localLayoutParams.width = i;
          addView(localImageView, localLayoutParams);
          place(localImageView, localPoint, false);
          if (paramInt <= 0) {
            continue;
          }
          f = ((Integer)localImageView.getTag(33554434)).intValue();
          localImageView.setScaleX(0.5F * f);
          localImageView.setScaleY(0.5F * f);
          localImageView.setAlpha(0.0F);
          localImageView.animate().withLayer().scaleX(f).scaleY(f).alpha(1.0F).setDuration(paramInt);
          continue;
        }
        if (f >= 0.005F) {
          break label324;
        }
      }
      finally {}
      Drawable localDrawable2 = (Drawable)this.mDrawables.get(pick(XRARE_PASTRIES));
      continue;
      label324:
      if (f < 0.5F) {
        localDrawable2 = (Drawable)this.mDrawables.get(pick(RARE_PASTRIES));
      } else if (f < 0.7F) {
        localDrawable2 = (Drawable)this.mDrawables.get(pick(PASTRIES));
      } else {
        localDrawable2 = null;
      }
    }
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    for (;;)
    {
      try
      {
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        if (this.mWidth == paramInt1)
        {
          paramInt3 = this.mHeight;
          if (paramInt3 == paramInt2) {
            return;
          }
        }
        boolean bool = this.mStarted;
        if (bool) {
          stop();
        }
        this.mWidth = paramInt1;
        this.mHeight = paramInt2;
        this.mCells = null;
        removeAllViewsInLayout();
        this.mFreeList.clear();
        this.mRows = (this.mHeight / this.mCellSize);
        this.mColumns = (this.mWidth / this.mCellSize);
        this.mCells = new View[this.mRows * this.mColumns];
        setScaleX(0.25F);
        setScaleY(0.25F);
        setTranslationX((this.mWidth - this.mCellSize * this.mColumns) * 0.5F * 0.25F);
        setTranslationY((this.mHeight - this.mCellSize * this.mRows) * 0.5F * 0.25F);
        paramInt1 = 0;
        if (paramInt1 < this.mRows)
        {
          paramInt2 = 0;
          if (paramInt2 < this.mColumns)
          {
            this.mFreeList.add(new Point(paramInt2, paramInt1));
            paramInt2 += 1;
            continue;
          }
        }
        else
        {
          if (bool) {
            start();
          }
          return;
        }
      }
      finally {}
      paramInt1 += 1;
    }
  }
  
  int pick(int[] paramArrayOfInt)
  {
    return paramArrayOfInt[((int)(Math.random() * paramArrayOfInt.length))];
  }
  
  public void place(View paramView, Point paramPoint, boolean paramBoolean)
  {
    for (;;)
    {
      int k;
      int m;
      Object localObject1;
      int i;
      try
      {
        k = paramPoint.x;
        m = paramPoint.y;
        f = frand();
        if (paramView.getTag(33554433) != null)
        {
          localObject1 = getOccupied(paramView);
          i = 0;
          j = localObject1.length;
          if (i < j)
          {
            localObject2 = localObject1[i];
            this.mFreeList.add(localObject2);
            this.mCells[(localObject2.y * this.mColumns + localObject2.x)] = null;
            i += 1;
            continue;
          }
        }
        j = 1;
        if (f < 0.01F)
        {
          i = j;
          if (k < this.mColumns - 3)
          {
            i = j;
            if (m < this.mRows - 3) {
              i = 4;
            }
          }
          paramView.setTag(33554433, paramPoint);
          paramView.setTag(33554434, Integer.valueOf(i));
          this.tmpSet.clear();
          paramPoint = getOccupied(paramView);
          j = 0;
          n = paramPoint.length;
          if (j < n)
          {
            localObject1 = paramPoint[j];
            localObject1 = this.mCells[(localObject1.y * this.mColumns + localObject1.x)];
            if (localObject1 == null) {
              break label908;
            }
            this.tmpSet.add(localObject1);
            break label908;
          }
        }
        else
        {
          if (f < 0.1F)
          {
            i = j;
            if (k >= this.mColumns - 2) {
              continue;
            }
            i = j;
            if (m >= this.mRows - 2) {
              continue;
            }
            i = 3;
            continue;
          }
          i = j;
          if (f >= 0.33F) {
            continue;
          }
          i = j;
          if (k == this.mColumns - 1) {
            continue;
          }
          i = j;
          if (m == this.mRows - 1) {
            continue;
          }
          i = 2;
          continue;
        }
        localObject1 = this.tmpSet.iterator();
        if (!((Iterator)localObject1).hasNext()) {
          break label531;
        }
        final Object localObject2 = (View)((Iterator)localObject1).next();
        Point[] arrayOfPoint = getOccupied((View)localObject2);
        j = 0;
        n = arrayOfPoint.length;
        if (j < n)
        {
          Point localPoint = arrayOfPoint[j];
          this.mFreeList.add(localPoint);
          this.mCells[(localPoint.y * this.mColumns + localPoint.x)] = null;
          j += 1;
          continue;
        }
        if (localObject2 == paramView) {
          continue;
        }
        ((View)localObject2).setTag(33554433, null);
        if (paramBoolean)
        {
          ((View)localObject2).animate().withLayer().scaleX(0.5F).scaleY(0.5F).alpha(0.0F).setDuration(500L).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener()
          {
            public void onAnimationCancel(Animator paramAnonymousAnimator) {}
            
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              DessertCaseView.this.removeView(localObject2);
            }
            
            public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
            
            public void onAnimationStart(Animator paramAnonymousAnimator) {}
          }).start();
          continue;
        }
        removeView((View)localObject2);
      }
      finally {}
      continue;
      label531:
      int j = 0;
      int n = paramPoint.length;
      while (j < n)
      {
        localObject1 = paramPoint[j];
        this.mCells[(localObject1.y * this.mColumns + localObject1.x)] = paramView;
        this.mFreeList.remove(localObject1);
        j += 1;
      }
      float f = irand(0, 4) * 90.0F;
      if (paramBoolean)
      {
        paramView.bringToFront();
        paramPoint = new AnimatorSet();
        paramPoint.playTogether(new Animator[] { ObjectAnimator.ofFloat(paramView, View.SCALE_X, new float[] { i }), ObjectAnimator.ofFloat(paramView, View.SCALE_Y, new float[] { i }) });
        paramPoint.setInterpolator(new AnticipateOvershootInterpolator());
        paramPoint.setDuration(500L);
        localObject1 = new AnimatorSet();
        ((AnimatorSet)localObject1).playTogether(new Animator[] { ObjectAnimator.ofFloat(paramView, View.ROTATION, new float[] { f }), ObjectAnimator.ofFloat(paramView, View.X, new float[] { this.mCellSize * k + (i - 1) * this.mCellSize / 2 }), ObjectAnimator.ofFloat(paramView, View.Y, new float[] { this.mCellSize * m + (i - 1) * this.mCellSize / 2 }) });
        ((AnimatorSet)localObject1).setInterpolator(new DecelerateInterpolator());
        ((AnimatorSet)localObject1).setDuration(500L);
        paramPoint.addListener(makeHardwareLayerListener(paramView));
        paramPoint.start();
        ((AnimatorSet)localObject1).start();
      }
      for (;;)
      {
        return;
        paramView.setX(this.mCellSize * k + (i - 1) * this.mCellSize / 2);
        paramView.setY(this.mCellSize * m + (i - 1) * this.mCellSize / 2);
        paramView.setScaleX(i);
        paramView.setScaleY(i);
        paramView.setRotation(f);
      }
      label908:
      j += 1;
    }
  }
  
  public void place(View paramView, boolean paramBoolean)
  {
    place(paramView, new Point(irand(0, this.mColumns), irand(0, this.mRows)), paramBoolean);
  }
  
  int random_color()
  {
    this.hsv[0] = (irand(0, 12) * 30.0F);
    return Color.HSVToColor(this.hsv);
  }
  
  public void start()
  {
    if (!this.mStarted)
    {
      this.mStarted = true;
      fillFreeList(2000);
    }
    this.mHandler.postDelayed(this.mJuggle, 5000L);
  }
  
  public void stop()
  {
    this.mStarted = false;
    this.mHandler.removeCallbacks(this.mJuggle);
  }
  
  public static class RescalingContainer
    extends FrameLayout
  {
    private DessertCaseView mView;
    
    public RescalingContainer(Context paramContext)
    {
      super();
      setSystemUiVisibility(5638);
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      float f1 = paramInt3 - paramInt1;
      float f2 = paramInt4 - paramInt2;
      paramInt3 = (int)(f1 / 0.25F / 2.0F);
      paramInt4 = (int)(f2 / 0.25F / 2.0F);
      paramInt1 = (int)(paramInt1 + 0.5F * f1);
      paramInt2 = (int)(paramInt2 + 0.5F * f2);
      this.mView.layout(paramInt1 - paramInt3, paramInt2 - paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
    }
    
    public void setView(DessertCaseView paramDessertCaseView)
    {
      addView(paramDessertCaseView);
      this.mView = paramDessertCaseView;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\DessertCaseView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */