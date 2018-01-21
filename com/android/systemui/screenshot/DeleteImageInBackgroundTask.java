package com.android.systemui.screenshot;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

class DeleteImageInBackgroundTask
  extends AsyncTask<Uri, Void, Void>
{
  private Context mContext;
  
  DeleteImageInBackgroundTask(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  protected Void doInBackground(Uri... paramVarArgs)
  {
    if (paramVarArgs.length != 1) {
      return null;
    }
    paramVarArgs = paramVarArgs[0];
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    try
    {
      localContentResolver.delete(paramVarArgs, null, null);
      return null;
    }
    catch (UnsupportedOperationException paramVarArgs)
    {
      paramVarArgs.printStackTrace();
    }
    return null;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\screenshot\DeleteImageInBackgroundTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */