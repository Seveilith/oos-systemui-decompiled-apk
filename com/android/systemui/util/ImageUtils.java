package com.android.systemui.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class ImageUtils
{
  private static String TAG = "ImageUtils";
  
  public static Bitmap computeCustomBackgroundBounds(Context paramContext, Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return paramBitmap;
    }
    Object localObject = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    ((Display)localObject).getRealMetrics(localDisplayMetrics);
    int j = localDisplayMetrics.widthPixels;
    int i = localDisplayMetrics.heightPixels;
    if (paramContext.getResources().getConfiguration().orientation == 2)
    {
      j = localDisplayMetrics.heightPixels;
      i = localDisplayMetrics.widthPixels;
    }
    int k = paramBitmap.getWidth();
    int m = paramBitmap.getHeight();
    if ((k == j) && (m == i))
    {
      Log.d(TAG, "bitmapWidth:" + k + ", bitmapHeight:" + m);
      return paramBitmap;
    }
    localObject = paramBitmap;
    if (paramBitmap != null)
    {
      localObject = paramBitmap;
      if (paramContext.getResources() != null) {
        localObject = new BitmapDrawable(paramContext.getResources(), createCenterCroppedBitmap(paramBitmap, j, i)).getBitmap();
      }
    }
    return (Bitmap)localObject;
  }
  
  public static Bitmap createCenterCroppedBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    if (paramBitmap == null) {
      return null;
    }
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    localCanvas.drawColor(-1);
    float f1 = paramBitmap.getWidth() / paramBitmap.getHeight();
    float f2 = paramInt1 / paramInt2;
    Log.w(TAG, "src aspectSrc:" + f1 + ", srcWidth:" + i + ", srcHeight:" + j);
    Log.w(TAG, "dst aspectDst:" + f2 + ", dstWidth:" + paramInt1 + ", dstHeight:" + paramInt2);
    j = paramBitmap.getWidth();
    i = paramBitmap.getHeight();
    if (f1 > f2) {
      j = (int)(i * f2);
    }
    for (;;)
    {
      localCanvas.drawBitmap(paramBitmap, new Rect((paramBitmap.getWidth() - j) / 2, (paramBitmap.getHeight() - i) / 2, (paramBitmap.getWidth() + j) / 2, (paramBitmap.getHeight() + i) / 2), new Rect(0, 0, paramInt1, paramInt2), new Paint());
      return localBitmap;
      i = (int)(j / f2);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\util\ImageUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */