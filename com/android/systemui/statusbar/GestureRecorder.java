package com.android.systemui.statusbar;

import android.os.Handler;
import android.os.SystemClock;
import java.util.HashSet;
import java.util.LinkedList;

public class GestureRecorder
{
  public static final String TAG = GestureRecorder.class.getSimpleName();
  private Gesture mCurrentGesture;
  private LinkedList<Gesture> mGestures;
  private Handler mHandler;
  
  public void saveLater()
  {
    this.mHandler.removeMessages(6351);
    this.mHandler.sendEmptyMessageDelayed(6351, 5000L);
  }
  
  public void tag(long paramLong, String paramString1, String paramString2)
  {
    synchronized (this.mGestures)
    {
      if (this.mCurrentGesture == null)
      {
        this.mCurrentGesture = new Gesture();
        this.mGestures.add(this.mCurrentGesture);
      }
      this.mCurrentGesture.tag(paramLong, paramString1, paramString2);
      saveLater();
      return;
    }
  }
  
  public void tag(String paramString1, String paramString2)
  {
    tag(SystemClock.uptimeMillis(), paramString1, paramString2);
  }
  
  public class Gesture
  {
    boolean mComplete = false;
    long mDownTime = -1L;
    private LinkedList<Record> mRecords = new LinkedList();
    private HashSet<String> mTags = new HashSet();
    
    public Gesture() {}
    
    public void tag(long paramLong, String paramString1, String paramString2)
    {
      this.mRecords.add(new TagRecord(paramLong, paramString1, paramString2));
      this.mTags.add(paramString1);
    }
    
    public abstract class Record
    {
      long time;
      
      public Record() {}
    }
    
    public class TagRecord
      extends GestureRecorder.Gesture.Record
    {
      public String info;
      public String tag;
      
      public TagRecord(long paramLong, String paramString1, String paramString2)
      {
        super();
        this.time = paramLong;
        this.tag = paramString1;
        this.info = paramString2;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\GestureRecorder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */