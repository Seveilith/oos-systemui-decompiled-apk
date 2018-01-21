package com.android.systemui.screenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

class SaveImageInBackgroundData
{
  Context context;
  int errorMsgResId;
  Runnable finisher;
  int iconSize;
  Bitmap image;
  Uri imageUri;
  int previewWidth;
  int previewheight;
  
  void clearContext()
  {
    this.context = null;
  }
  
  void clearImage()
  {
    this.image = null;
    this.imageUri = null;
    this.iconSize = 0;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\screenshot\SaveImageInBackgroundData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */