package com.android.systemui.tv.pip;

import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaController.Callback;
import android.media.session.MediaController.TransportControls;
import android.media.session.PlaybackState;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.LinearLayout;

public class PipControlsView
  extends LinearLayout
{
  private PipControlButtonView mCloseButtonView;
  private final View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener()
  {
    public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean) {
        PipControlsView.-set0(PipControlsView.this, (PipControlButtonView)paramAnonymousView);
      }
      while (PipControlsView.-get0(PipControlsView.this) != paramAnonymousView) {
        return;
      }
      PipControlsView.-set0(PipControlsView.this, null);
    }
  };
  private PipControlButtonView mFocusedChild;
  private PipControlButtonView mFullButtonView;
  Listener mListener;
  private MediaController mMediaController;
  private MediaController.Callback mMediaControllerCallback = new MediaController.Callback()
  {
    public void onPlaybackStateChanged(PlaybackState paramAnonymousPlaybackState)
    {
      PipControlsView.-wrap1(PipControlsView.this);
    }
  };
  final PipManager mPipManager = PipManager.getInstance();
  private final PipManager.MediaListener mPipMediaListener = new PipManager.MediaListener()
  {
    public void onMediaControllerChanged()
    {
      PipControlsView.-wrap0(PipControlsView.this);
    }
  };
  private PipControlButtonView mPlayPauseButtonView;
  
  public PipControlsView(Context paramContext)
  {
    this(paramContext, null, 0, 0);
  }
  
  public PipControlsView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0, 0);
  }
  
  public PipControlsView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PipControlsView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130968835, this);
    setOrientation(0);
    setGravity(49);
  }
  
  private void updateMediaController()
  {
    MediaController localMediaController = this.mPipManager.getMediaController();
    if (this.mMediaController == localMediaController) {
      return;
    }
    if (this.mMediaController != null) {
      this.mMediaController.unregisterCallback(this.mMediaControllerCallback);
    }
    this.mMediaController = localMediaController;
    if (this.mMediaController != null) {
      this.mMediaController.registerCallback(this.mMediaControllerCallback);
    }
    updatePlayPauseView();
  }
  
  private void updatePlayPauseView()
  {
    int i = this.mPipManager.getPlaybackState();
    if (i == 2)
    {
      this.mPlayPauseButtonView.setVisibility(8);
      return;
    }
    this.mPlayPauseButtonView.setVisibility(0);
    if (i == 0)
    {
      this.mPlayPauseButtonView.setImageResource(2130837762);
      this.mPlayPauseButtonView.setText(2131690651);
      return;
    }
    this.mPlayPauseButtonView.setImageResource(2130837765);
    this.mPlayPauseButtonView.setText(2131690650);
  }
  
  PipControlButtonView getFocusedButton()
  {
    return this.mFocusedChild;
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    updateMediaController();
    this.mPipManager.addMediaListener(this.mPipMediaListener);
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mPipManager.removeMediaListener(this.mPipMediaListener);
    if (this.mMediaController != null) {
      this.mMediaController.unregisterCallback(this.mMediaControllerCallback);
    }
  }
  
  public void onFinishInflate()
  {
    super.onFinishInflate();
    this.mFullButtonView = ((PipControlButtonView)findViewById(2131952321));
    this.mFullButtonView.setOnFocusChangeListener(this.mFocusChangeListener);
    this.mFullButtonView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PipControlsView.this.mPipManager.movePipToFullscreen();
      }
    });
    this.mCloseButtonView = ((PipControlButtonView)findViewById(2131952322));
    this.mCloseButtonView.setOnFocusChangeListener(this.mFocusChangeListener);
    this.mCloseButtonView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PipControlsView.this.mPipManager.closePip();
        if (PipControlsView.this.mListener != null) {
          PipControlsView.this.mListener.onClosed();
        }
      }
    });
    this.mPlayPauseButtonView = ((PipControlButtonView)findViewById(2131952323));
    this.mPlayPauseButtonView.setOnFocusChangeListener(this.mFocusChangeListener);
    this.mPlayPauseButtonView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((PipControlsView.-get1(PipControlsView.this) == null) || (PipControlsView.-get1(PipControlsView.this).getPlaybackState() == null)) {
          return;
        }
        PipControlsView.-get1(PipControlsView.this).getPlaybackState().getActions();
        PipControlsView.-get1(PipControlsView.this).getPlaybackState().getState();
        if (PipControlsView.this.mPipManager.getPlaybackState() == 1) {
          PipControlsView.-get1(PipControlsView.this).getTransportControls().play();
        }
        while (PipControlsView.this.mPipManager.getPlaybackState() != 0) {
          return;
        }
        PipControlsView.-get1(PipControlsView.this).getTransportControls().pause();
      }
    });
  }
  
  public void reset()
  {
    this.mFullButtonView.reset();
    this.mCloseButtonView.reset();
    this.mPlayPauseButtonView.reset();
    this.mFullButtonView.requestFocus();
  }
  
  public void setListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }
  
  public static abstract interface Listener
  {
    public abstract void onClosed();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tv\pip\PipControlsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */