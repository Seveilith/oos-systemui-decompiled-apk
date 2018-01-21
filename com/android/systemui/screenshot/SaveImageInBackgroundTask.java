package com.android.systemui.screenshot;

import android.app.Notification.Action.Builder;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Process;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import com.android.systemui.SystemUI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class SaveImageInBackgroundTask
  extends AsyncTask<Void, Void, Void>
{
  private static boolean mTickerAddSpace;
  private final String mImageFileName;
  private final String mImageFilePath;
  private final int mImageHeight;
  private final long mImageTime;
  private final int mImageWidth;
  private final Notification.Builder mNotificationBuilder;
  private final NotificationManager mNotificationManager;
  private final Notification.BigPictureStyle mNotificationStyle;
  private final SaveImageInBackgroundData mParams;
  private final Notification.Builder mPublicNotificationBuilder;
  private final File mScreenshotDir;
  
  SaveImageInBackgroundTask(Context paramContext, SaveImageInBackgroundData paramSaveImageInBackgroundData, NotificationManager paramNotificationManager)
  {
    Resources localResources = paramContext.getResources();
    this.mParams = paramSaveImageInBackgroundData;
    this.mImageTime = System.currentTimeMillis();
    this.mImageFileName = String.format("Screenshot_%s.png", new Object[] { new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(this.mImageTime)) });
    this.mScreenshotDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Screenshots");
    this.mImageFilePath = new File(this.mScreenshotDir, this.mImageFileName).getAbsolutePath();
    this.mImageWidth = paramSaveImageInBackgroundData.image.getWidth();
    this.mImageHeight = paramSaveImageInBackgroundData.image.getHeight();
    int i = paramSaveImageInBackgroundData.iconSize;
    int j = paramSaveImageInBackgroundData.previewWidth;
    int k = paramSaveImageInBackgroundData.previewheight;
    Object localObject2 = new Canvas();
    Paint localPaint = new Paint();
    Object localObject1 = new ColorMatrix();
    ((ColorMatrix)localObject1).setSaturation(0.25F);
    localPaint.setColorFilter(new ColorMatrixColorFilter((ColorMatrix)localObject1));
    Matrix localMatrix = new Matrix();
    Bitmap localBitmap = Bitmap.createBitmap(j, k, paramSaveImageInBackgroundData.image.getConfig());
    localMatrix.setTranslate((j - this.mImageWidth) / 2, (k - this.mImageHeight) / 2);
    ((Canvas)localObject2).setBitmap(localBitmap);
    ((Canvas)localObject2).drawBitmap(paramSaveImageInBackgroundData.image, localMatrix, localPaint);
    ((Canvas)localObject2).drawColor(1090519039);
    ((Canvas)localObject2).setBitmap(null);
    float f = i / Math.min(this.mImageWidth, this.mImageHeight);
    localObject1 = Bitmap.createBitmap(i, i, paramSaveImageInBackgroundData.image.getConfig());
    localMatrix.setScale(f, f);
    localMatrix.postTranslate((i - this.mImageWidth * f) / 2.0F, (i - this.mImageHeight * f) / 2.0F);
    ((Canvas)localObject2).setBitmap((Bitmap)localObject1);
    ((Canvas)localObject2).drawBitmap(paramSaveImageInBackgroundData.image, localMatrix, localPaint);
    ((Canvas)localObject2).drawColor(1090519039);
    ((Canvas)localObject2).setBitmap(null);
    boolean bool;
    long l;
    if (mTickerAddSpace)
    {
      bool = false;
      mTickerAddSpace = bool;
      this.mNotificationManager = paramNotificationManager;
      l = System.currentTimeMillis();
      this.mNotificationStyle = new Notification.BigPictureStyle().bigPicture(localBitmap.createAshmemBitmap());
      this.mPublicNotificationBuilder = new Notification.Builder(paramContext).setContentTitle(localResources.getString(2131690072)).setContentText(localResources.getString(2131690073)).setSmallIcon(2130838388).setCategory("progress").setWhen(l).setShowWhen(true).setColor(localResources.getColor(17170523));
      SystemUI.overrideNotificationAppName(paramContext, this.mPublicNotificationBuilder);
      paramNotificationManager = new Notification.Builder(paramContext);
      localObject2 = new StringBuilder().append(localResources.getString(2131690071));
      if (!mTickerAddSpace) {
        break label681;
      }
    }
    label681:
    for (paramSaveImageInBackgroundData = " ";; paramSaveImageInBackgroundData = "")
    {
      this.mNotificationBuilder = paramNotificationManager.setTicker(paramSaveImageInBackgroundData).setContentTitle(localResources.getString(2131690072)).setContentText(localResources.getString(2131690073)).setSmallIcon(2130838388).setWhen(l).setShowWhen(true).setColor(localResources.getColor(17170523)).setStyle(this.mNotificationStyle).setPublicVersion(this.mPublicNotificationBuilder.build());
      this.mNotificationBuilder.setFlag(32, true);
      SystemUI.overrideNotificationAppName(paramContext, this.mNotificationBuilder);
      ((Service)paramContext).startForeground(2131951669, this.mNotificationBuilder.build());
      this.mNotificationBuilder.setLargeIcon(((Bitmap)localObject1).createAshmemBitmap());
      this.mNotificationStyle.bigLargeIcon((Bitmap)null);
      return;
      bool = true;
      break;
    }
  }
  
  protected Void doInBackground(Void... paramVarArgs)
  {
    if (isCancelled()) {
      return null;
    }
    Process.setThreadPriority(-2);
    Object localObject1 = this.mParams.context;
    paramVarArgs = this.mParams.image;
    Resources localResources = ((Context)localObject1).getResources();
    long l;
    Object localObject2;
    try
    {
      this.mScreenshotDir.mkdirs();
      l = this.mImageTime / 1000L;
      localObject2 = new FileOutputStream(this.mImageFilePath);
      boolean bool = paramVarArgs.compress(Bitmap.CompressFormat.PNG, 100, (OutputStream)localObject2);
      ((OutputStream)localObject2).flush();
      ((OutputStream)localObject2).close();
      if (!bool)
      {
        Log.d("TAG", " compress screenshop fail. ");
        throw new Exception();
      }
    }
    catch (Exception localException)
    {
      this.mParams.clearImage();
      this.mParams.errorMsgResId = 2131690078;
      new File(this.mImageFilePath).delete();
      Log.d("SaveImageInBackgroundTask", "screenshop exception :" + localException);
    }
    for (;;)
    {
      if (paramVarArgs != null) {
        paramVarArgs.recycle();
      }
      return null;
      localObject2 = new ContentValues();
      Object localObject3 = ((Context)localObject1).getContentResolver();
      ((ContentValues)localObject2).put("_data", this.mImageFilePath);
      ((ContentValues)localObject2).put("title", this.mImageFileName);
      ((ContentValues)localObject2).put("_display_name", this.mImageFileName);
      ((ContentValues)localObject2).put("datetaken", Long.valueOf(this.mImageTime));
      ((ContentValues)localObject2).put("date_added", Long.valueOf(l));
      ((ContentValues)localObject2).put("date_modified", Long.valueOf(l));
      ((ContentValues)localObject2).put("mime_type", "image/png");
      ((ContentValues)localObject2).put("width", Integer.valueOf(this.mImageWidth));
      ((ContentValues)localObject2).put("height", Integer.valueOf(this.mImageHeight));
      ((ContentValues)localObject2).put("_size", Long.valueOf(new File(this.mImageFilePath).length()));
      localObject2 = ((ContentResolver)localObject3).insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, (ContentValues)localObject2);
      localObject3 = String.format("Screenshot (%s)", new Object[] { DateFormat.getDateTimeInstance().format(new Date(this.mImageTime)) });
      Intent localIntent = new Intent("android.intent.action.SEND");
      localIntent.setType("image/png");
      localIntent.putExtra("android.intent.extra.STREAM", (Parcelable)localObject2);
      localIntent.putExtra("android.intent.extra.SUBJECT", (String)localObject3);
      localObject3 = PendingIntent.getActivity((Context)localObject1, 0, Intent.createChooser(localIntent, null, PendingIntent.getBroadcast((Context)localObject1, 0, new Intent((Context)localObject1, GlobalScreenshot.TargetChosenReceiver.class), 1342177280).getIntentSender()).addFlags(268468224), 268435456);
      localObject3 = new Notification.Action.Builder(2130837863, localException.getString(17040549), (PendingIntent)localObject3);
      this.mNotificationBuilder.addAction(((Notification.Action.Builder)localObject3).build());
      localObject1 = PendingIntent.getBroadcast((Context)localObject1, 0, new Intent((Context)localObject1, GlobalScreenshot.DeleteScreenshotReceiver.class).putExtra("android:screenshot_uri_id", ((Uri)localObject2).toString()), 1342177280);
      Notification.Action.Builder localBuilder = new Notification.Action.Builder(2130837862, localException.getString(17040249), (PendingIntent)localObject1);
      this.mNotificationBuilder.addAction(localBuilder.build());
      this.mParams.imageUri = ((Uri)localObject2);
      this.mParams.image = null;
      this.mParams.errorMsgResId = 0;
    }
  }
  
  protected void onCancelled(Void paramVoid)
  {
    this.mParams.finisher.run();
    this.mParams.clearImage();
    this.mParams.clearContext();
    this.mNotificationManager.cancel(2131951669);
  }
  
  protected void onPostExecute(Void paramVoid)
  {
    paramVoid = null;
    if (this.mParams.errorMsgResId != 0) {
      GlobalScreenshot.notifyScreenshotError(this.mParams.context, this.mNotificationManager, this.mParams.errorMsgResId);
    }
    for (;;)
    {
      this.mParams.clearContext();
      if (paramVoid != null) {
        ((Service)paramVoid).stopForeground(false);
      }
      return;
      paramVoid = this.mParams.context;
      Resources localResources = paramVoid.getResources();
      Intent localIntent = new Intent("android.intent.action.VIEW");
      localIntent.setDataAndType(this.mParams.imageUri, "image/png");
      localIntent.setFlags(268435456);
      localIntent.putExtra("load_media_set", true);
      long l = System.currentTimeMillis();
      this.mPublicNotificationBuilder.setContentTitle(localResources.getString(2131690074)).setContentText(localResources.getString(2131690075)).setContentIntent(PendingIntent.getActivity(this.mParams.context, 0, localIntent, 0)).setWhen(l).setAutoCancel(true).setColor(paramVoid.getColor(17170523));
      this.mNotificationBuilder.setContentTitle(localResources.getString(2131690074)).setContentText(localResources.getString(2131690075)).setContentIntent(PendingIntent.getActivity(this.mParams.context, 0, localIntent, 0)).setWhen(l).setAutoCancel(true).setColor(paramVoid.getColor(17170523)).setPublicVersion(this.mPublicNotificationBuilder.build()).setFlag(32, false);
      this.mNotificationManager.notify(2131951669, this.mNotificationBuilder.build());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\screenshot\SaveImageInBackgroundTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */