package com.android.systemui.statusbar.policy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Notification.Action;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.ExpandableView;
import com.android.systemui.statusbar.NotificationData.Entry;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.stack.ScrollContainer;

public class RemoteInputView
  extends LinearLayout
  implements View.OnClickListener, TextWatcher
{
  public static final Object VIEW_TAG = new Object();
  private RemoteInputController mController;
  private RemoteEditText mEditText;
  private NotificationData.Entry mEntry;
  private PendingIntent mPendingIntent;
  private ProgressBar mProgressBar;
  private RemoteInput mRemoteInput;
  private RemoteInput[] mRemoteInputs;
  private boolean mRemoved;
  private boolean mResetting;
  private int mRevealCx;
  private int mRevealCy;
  private int mRevealR;
  private ScrollContainer mScrollContainer;
  private View mScrollContainerChild;
  private ImageButton mSendButton;
  public final Object mToken = new Object();
  
  public RemoteInputView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void findScrollContainer()
  {
    if (this.mScrollContainer == null) {
      this.mScrollContainerChild = null;
    }
    for (Object localObject = this;; localObject = ((ViewParent)localObject).getParent())
    {
      if (localObject != null)
      {
        if ((this.mScrollContainerChild == null) && ((localObject instanceof ExpandableView))) {
          this.mScrollContainerChild = ((View)localObject);
        }
        if (!(((ViewParent)localObject).getParent() instanceof ScrollContainer)) {
          continue;
        }
        this.mScrollContainer = ((ScrollContainer)((ViewParent)localObject).getParent());
        if (this.mScrollContainerChild == null) {
          this.mScrollContainerChild = ((View)localObject);
        }
      }
      return;
    }
  }
  
  public static RemoteInputView inflate(Context paramContext, ViewGroup paramViewGroup, NotificationData.Entry paramEntry, RemoteInputController paramRemoteInputController)
  {
    paramContext = (RemoteInputView)LayoutInflater.from(paramContext).inflate(2130968802, paramViewGroup, false);
    paramContext.mController = paramRemoteInputController;
    paramContext.mEntry = paramEntry;
    paramContext.setTag(VIEW_TAG);
    return paramContext;
  }
  
  private void onDefocus(boolean paramBoolean)
  {
    this.mController.removeRemoteInput(this.mEntry, this.mToken);
    this.mEntry.remoteInputText = this.mEditText.getText();
    if (!this.mRemoved)
    {
      if ((!paramBoolean) || (this.mRevealR <= 0)) {
        break label118;
      }
      Animator localAnimator = ViewAnimationUtils.createCircularReveal(this, this.mRevealCx, this.mRevealCy, this.mRevealR, 0.0F);
      localAnimator.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
      localAnimator.setDuration(150L);
      localAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          RemoteInputView.this.setVisibility(4);
        }
      });
      localAnimator.start();
    }
    for (;;)
    {
      MetricsLogger.action(this.mContext, 400, this.mEntry.notification.getPackageName());
      return;
      label118:
      setVisibility(4);
    }
  }
  
  private void reset()
  {
    this.mResetting = true;
    this.mEditText.getText().clear();
    this.mEditText.setEnabled(true);
    this.mSendButton.setVisibility(0);
    this.mProgressBar.setVisibility(4);
    this.mController.removeSpinning(this.mEntry.key, this.mToken);
    updateSendButton();
    onDefocus(false);
    this.mResetting = false;
  }
  
  private void sendRemoteInput()
  {
    Bundle localBundle = new Bundle();
    localBundle.putString(this.mRemoteInput.getResultKey(), this.mEditText.getText().toString());
    Intent localIntent = new Intent().addFlags(268435456);
    RemoteInput.addResultsToIntent(this.mRemoteInputs, localIntent, localBundle);
    this.mEditText.setEnabled(false);
    this.mSendButton.setVisibility(4);
    this.mProgressBar.setVisibility(0);
    this.mEntry.remoteInputText = this.mEditText.getText();
    this.mController.addSpinning(this.mEntry.key, this.mToken);
    this.mController.removeRemoteInput(this.mEntry, this.mToken);
    this.mEditText.mShowImeOnInputConnection = false;
    this.mController.remoteInputSent(this.mEntry);
    ((ShortcutManager)getContext().getSystemService(ShortcutManager.class)).onApplicationActive(this.mEntry.notification.getPackageName(), this.mEntry.notification.getUser().getIdentifier());
    MetricsLogger.action(this.mContext, 398, this.mEntry.notification.getPackageName());
    try
    {
      this.mPendingIntent.send(this.mContext, 0, localIntent);
      return;
    }
    catch (PendingIntent.CanceledException localCanceledException)
    {
      Log.i("RemoteInput", "Unable to send remote input result", localCanceledException);
      MetricsLogger.action(this.mContext, 399, this.mEntry.notification.getPackageName());
    }
  }
  
  private void updateSendButton()
  {
    boolean bool = false;
    ImageButton localImageButton = this.mSendButton;
    if (this.mEditText.getText().length() != 0) {
      bool = true;
    }
    localImageButton.setEnabled(bool);
  }
  
  public void afterTextChanged(Editable paramEditable)
  {
    updateSendButton();
  }
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  public void close()
  {
    RemoteEditText.-wrap0(this.mEditText, false);
  }
  
  public void dispatchFinishTemporaryDetach()
  {
    if (isAttachedToWindow()) {
      attachViewToParent(this.mEditText, 0, this.mEditText.getLayoutParams());
    }
    for (;;)
    {
      super.dispatchFinishTemporaryDetach();
      return;
      removeDetachedView(this.mEditText, false);
    }
  }
  
  public void dispatchStartTemporaryDetach()
  {
    super.dispatchStartTemporaryDetach();
    detachViewFromParent(this.mEditText);
  }
  
  public void focus()
  {
    MetricsLogger.action(this.mContext, 397, this.mEntry.notification.getPackageName());
    setVisibility(0);
    this.mController.addRemoteInput(this.mEntry, this.mToken);
    this.mEditText.setInnerFocusable(true);
    this.mEditText.mShowImeOnInputConnection = true;
    this.mEditText.setText(this.mEntry.remoteInputText);
    this.mEditText.setSelection(this.mEditText.getText().length());
    this.mEditText.requestFocus();
    updateSendButton();
  }
  
  public void focusAnimated()
  {
    if (getVisibility() != 0)
    {
      Animator localAnimator = ViewAnimationUtils.createCircularReveal(this, this.mRevealCx, this.mRevealCy, 0.0F, this.mRevealR);
      localAnimator.setDuration(360L);
      localAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
      localAnimator.start();
    }
    focus();
  }
  
  public PendingIntent getPendingIntent()
  {
    return this.mPendingIntent;
  }
  
  public boolean isActive()
  {
    if (this.mEditText.isFocused()) {
      return this.mEditText.isEnabled();
    }
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if ((this.mEntry.row.isChangingPosition()) && (getVisibility() == 0) && (this.mEditText.isFocusable())) {
      this.mEditText.requestFocus();
    }
  }
  
  public void onClick(View paramView)
  {
    if (paramView == this.mSendButton) {
      sendRemoteInput();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if ((this.mEntry.row.isChangingPosition()) || (isTemporarilyDetached())) {
      return;
    }
    this.mController.removeRemoteInput(this.mEntry, this.mToken);
    this.mController.removeSpinning(this.mEntry.key, this.mToken);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mProgressBar = ((ProgressBar)findViewById(2131952215));
    this.mSendButton = ((ImageButton)findViewById(2131952214));
    this.mSendButton.setOnClickListener(this);
    this.mEditText = ((RemoteEditText)getChildAt(0));
    this.mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        int i;
        if (paramAnonymousKeyEvent == null) {
          if ((paramAnonymousInt == 6) || (paramAnonymousInt == 5))
          {
            paramAnonymousInt = 1;
            if ((paramAnonymousKeyEvent == null) || (!KeyEvent.isConfirmKey(paramAnonymousKeyEvent.getKeyCode()))) {
              break label98;
            }
            if (paramAnonymousKeyEvent.getAction() != 0) {
              break label92;
            }
            i = 1;
          }
        }
        for (;;)
        {
          if ((paramAnonymousInt == 0) && (i == 0)) {
            break label104;
          }
          if (RemoteInputView.-get0(RemoteInputView.this).length() > 0) {
            RemoteInputView.-wrap1(RemoteInputView.this);
          }
          return true;
          if (paramAnonymousInt == 4)
          {
            paramAnonymousInt = 1;
            break;
          }
          paramAnonymousInt = 0;
          break;
          paramAnonymousInt = 0;
          break;
          label92:
          i = 0;
          continue;
          label98:
          i = 0;
        }
        label104:
        return false;
      }
    });
    this.mEditText.addTextChangedListener(this);
    this.mEditText.setInnerFocusable(false);
    RemoteEditText.-set0(this.mEditText, this);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 0)
    {
      findScrollContainer();
      if (this.mScrollContainer != null)
      {
        this.mScrollContainer.requestDisallowLongPress();
        this.mScrollContainer.requestDisallowDismiss();
      }
    }
    return super.onInterceptTouchEvent(paramMotionEvent);
  }
  
  public void onNotificationUpdateOrReset()
  {
    int i = 0;
    if (this.mProgressBar.getVisibility() == 0) {
      i = 1;
    }
    if (i != 0) {
      reset();
    }
  }
  
  public boolean onRequestSendAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    if ((this.mResetting) && (paramView == this.mEditText)) {
      return false;
    }
    return super.onRequestSendAccessibilityEvent(paramView, paramAccessibilityEvent);
  }
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    super.onTouchEvent(paramMotionEvent);
    return true;
  }
  
  public boolean requestScrollTo()
  {
    findScrollContainer();
    this.mScrollContainer.lockScrollTo(this.mScrollContainerChild);
    return true;
  }
  
  public void setPendingIntent(PendingIntent paramPendingIntent)
  {
    this.mPendingIntent = paramPendingIntent;
  }
  
  public void setRemoteInput(RemoteInput[] paramArrayOfRemoteInput, RemoteInput paramRemoteInput)
  {
    this.mRemoteInputs = paramArrayOfRemoteInput;
    this.mRemoteInput = paramRemoteInput;
    this.mEditText.setHint(this.mRemoteInput.getLabel());
  }
  
  public void setRemoved()
  {
    this.mRemoved = true;
  }
  
  public void setRevealParameters(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mRevealCx = paramInt1;
    this.mRevealCy = paramInt2;
    this.mRevealR = paramInt3;
  }
  
  public void stealFocusFrom(RemoteInputView paramRemoteInputView)
  {
    paramRemoteInputView.close();
    setPendingIntent(paramRemoteInputView.mPendingIntent);
    setRemoteInput(paramRemoteInputView.mRemoteInputs, paramRemoteInputView.mRemoteInput);
    setRevealParameters(paramRemoteInputView.mRevealCx, paramRemoteInputView.mRevealCy, paramRemoteInputView.mRevealR);
    focus();
  }
  
  public boolean updatePendingIntentFromActions(Notification.Action[] paramArrayOfAction)
  {
    if ((this.mPendingIntent == null) || (paramArrayOfAction == null)) {
      return false;
    }
    Intent localIntent = this.mPendingIntent.getIntent();
    if (localIntent == null) {
      return false;
    }
    int k = paramArrayOfAction.length;
    int i = 0;
    if (i < k)
    {
      Notification.Action localAction = paramArrayOfAction[i];
      RemoteInput[] arrayOfRemoteInput = localAction.getRemoteInputs();
      if ((localAction.actionIntent == null) || (arrayOfRemoteInput == null)) {}
      Object localObject;
      do
      {
        do
        {
          i += 1;
          break;
        } while (!localIntent.filterEquals(localAction.actionIntent.getIntent()));
        localObject = null;
        int m = arrayOfRemoteInput.length;
        int j = 0;
        while (j < m)
        {
          RemoteInput localRemoteInput = arrayOfRemoteInput[j];
          if (localRemoteInput.getAllowFreeFormInput()) {
            localObject = localRemoteInput;
          }
          j += 1;
        }
      } while (localObject == null);
      setPendingIntent(localAction.actionIntent);
      setRemoteInput(arrayOfRemoteInput, (RemoteInput)localObject);
      return true;
    }
    return false;
  }
  
  public static class RemoteEditText
    extends EditText
  {
    private final Drawable mBackground = getBackground();
    private RemoteInputView mRemoteInputView;
    boolean mShowImeOnInputConnection;
    
    public RemoteEditText(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    private void defocusIfNeeded(boolean paramBoolean)
    {
      if (((this.mRemoteInputView != null) && (RemoteInputView.-get1(this.mRemoteInputView).row.isChangingPosition())) || (isTemporarilyDetached()))
      {
        if ((isTemporarilyDetached()) && (this.mRemoteInputView != null)) {
          RemoteInputView.-get1(this.mRemoteInputView).remoteInputText = getText();
        }
        return;
      }
      if ((isFocusable()) && (isEnabled()))
      {
        setInnerFocusable(false);
        if (this.mRemoteInputView != null) {
          RemoteInputView.-wrap0(this.mRemoteInputView, paramBoolean);
        }
        this.mShowImeOnInputConnection = false;
      }
    }
    
    public void getFocusedRect(Rect paramRect)
    {
      super.getFocusedRect(paramRect);
      paramRect.top = this.mScrollY;
      paramRect.bottom = (this.mScrollY + (this.mBottom - this.mTop));
    }
    
    public boolean onCheckIsTextEditor()
    {
      boolean bool2 = false;
      if (this.mRemoteInputView != null) {}
      for (boolean bool1 = RemoteInputView.-get2(this.mRemoteInputView);; bool1 = false)
      {
        if (!bool1) {
          bool2 = super.onCheckIsTextEditor();
        }
        return bool2;
      }
    }
    
    public void onCommitCompletion(CompletionInfo paramCompletionInfo)
    {
      clearComposingText();
      setText(paramCompletionInfo.getText());
      setSelection(getText().length());
    }
    
    public InputConnection onCreateInputConnection(EditorInfo paramEditorInfo)
    {
      paramEditorInfo = super.onCreateInputConnection(paramEditorInfo);
      if ((this.mShowImeOnInputConnection) && (paramEditorInfo != null))
      {
        final InputMethodManager localInputMethodManager = InputMethodManager.getInstance();
        if (localInputMethodManager != null) {
          post(new Runnable()
          {
            public void run()
            {
              localInputMethodManager.viewClicked(RemoteInputView.RemoteEditText.this);
              localInputMethodManager.showSoftInput(RemoteInputView.RemoteEditText.this, 0);
            }
          });
        }
      }
      return paramEditorInfo;
    }
    
    protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
    {
      super.onFocusChanged(paramBoolean, paramInt, paramRect);
      if (!paramBoolean) {
        defocusIfNeeded(true);
      }
    }
    
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
    {
      if (paramInt == 4) {
        return true;
      }
      return super.onKeyDown(paramInt, paramKeyEvent);
    }
    
    public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
    {
      if (paramInt == 4)
      {
        defocusIfNeeded(true);
        return true;
      }
      return super.onKeyUp(paramInt, paramKeyEvent);
    }
    
    protected void onVisibilityChanged(View paramView, int paramInt)
    {
      super.onVisibilityChanged(paramView, paramInt);
      if (!isShown()) {
        defocusIfNeeded(false);
      }
    }
    
    public boolean requestRectangleOnScreen(Rect paramRect)
    {
      return this.mRemoteInputView.requestScrollTo();
    }
    
    void setInnerFocusable(boolean paramBoolean)
    {
      setFocusableInTouchMode(paramBoolean);
      setFocusable(paramBoolean);
      setCursorVisible(paramBoolean);
      if (paramBoolean)
      {
        requestFocus();
        setBackground(this.mBackground);
        return;
      }
      setBackground(null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\RemoteInputView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */