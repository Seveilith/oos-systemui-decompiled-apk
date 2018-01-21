package com.android.systemui.tuner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View.DragShadowBuilder;
import android.widget.ImageView;

public class ClipboardView
  extends ImageView
  implements ClipboardManager.OnPrimaryClipChangedListener
{
  private final ClipboardManager mClipboardManager;
  private ClipData mCurrentClip;
  
  public ClipboardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mClipboardManager = ((ClipboardManager)paramContext.getSystemService(ClipboardManager.class));
  }
  
  private void setBackgroundDragTarget(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1308622847;; i = 0)
    {
      setBackgroundColor(i);
      return;
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    startListening();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    stopListening();
  }
  
  public boolean onDragEvent(DragEvent paramDragEvent)
  {
    switch (paramDragEvent.getAction())
    {
    default: 
      return true;
    case 5: 
      setBackgroundDragTarget(true);
      return true;
    case 3: 
      this.mClipboardManager.setPrimaryClip(paramDragEvent.getClipData());
    }
    setBackgroundDragTarget(false);
    return true;
  }
  
  public void onPrimaryClipChanged()
  {
    this.mCurrentClip = this.mClipboardManager.getPrimaryClip();
    if (this.mCurrentClip != null) {}
    for (int i = 2130837598;; i = 2130837597)
    {
      setImageResource(i);
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getActionMasked() == 0) && (this.mCurrentClip != null)) {
      startPocketDrag();
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void startListening()
  {
    this.mClipboardManager.addPrimaryClipChangedListener(this);
    onPrimaryClipChanged();
  }
  
  public void startPocketDrag()
  {
    startDragAndDrop(this.mCurrentClip, new View.DragShadowBuilder(this), null, 256);
  }
  
  public void stopListening()
  {
    this.mClipboardManager.removePrimaryClipChangedListener(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\ClipboardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */