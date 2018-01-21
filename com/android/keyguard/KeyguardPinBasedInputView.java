package com.android.keyguard;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

public abstract class KeyguardPinBasedInputView
  extends KeyguardAbsKeyInputView
  implements View.OnKeyListener, View.OnTouchListener
{
  private View mButton0;
  private View mButton1;
  private View mButton2;
  private View mButton3;
  private View mButton4;
  private View mButton5;
  private View mButton6;
  private View mButton7;
  private View mButton8;
  private View mButton9;
  private View mDeleteButton;
  private View mOkButton;
  protected PasswordTextView mPasswordEntry;
  
  public KeyguardPinBasedInputView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardPinBasedInputView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void performClick(View paramView)
  {
    paramView.performClick();
  }
  
  private void performNumberClick(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      performClick(this.mButton0);
      return;
    case 1: 
      performClick(this.mButton1);
      return;
    case 2: 
      performClick(this.mButton2);
      return;
    case 3: 
      performClick(this.mButton3);
      return;
    case 4: 
      performClick(this.mButton4);
      return;
    case 5: 
      performClick(this.mButton5);
      return;
    case 6: 
      performClick(this.mButton6);
      return;
    case 7: 
      performClick(this.mButton7);
      return;
    case 8: 
      performClick(this.mButton8);
      return;
    }
    performClick(this.mButton9);
  }
  
  protected String getPasswordText()
  {
    return this.mPasswordEntry.getText();
  }
  
  protected int getPromtReasonStringRes(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return R.string.kg_prompt_reason_timeout_pin;
    case 1: 
      return R.string.kg_prompt_reason_restart_pin;
    case 2: 
      return R.string.kg_prompt_reason_timeout_pin;
    case 3: 
      return R.string.kg_prompt_reason_device_admin;
    case 4: 
      return R.string.kg_prompt_reason_user_request;
    }
    return 0;
  }
  
  protected void onFinishInflate()
  {
    this.mPasswordEntry = ((PasswordTextView)findViewById(getPasswordTextViewId()));
    this.mPasswordEntry.setOnKeyListener(this);
    this.mPasswordEntry.setSelected(true);
    this.mPasswordEntry.setUserActivityListener(new PasswordTextView.UserActivityListener()
    {
      public void onUserActivity()
      {
        KeyguardPinBasedInputView.this.onUserInput();
      }
    });
    this.mOkButton = findViewById(R.id.key_enter);
    if (this.mOkButton != null)
    {
      this.mOkButton.setOnTouchListener(this);
      this.mOkButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (KeyguardPinBasedInputView.this.mPasswordEntry.isEnabled()) {
            KeyguardPinBasedInputView.this.verifyPasswordAndUnlock();
          }
        }
      });
      this.mOkButton.setOnHoverListener(new LiftToActivateListener(getContext()));
    }
    this.mDeleteButton = findViewById(R.id.delete_button);
    this.mDeleteButton.setVisibility(0);
    this.mDeleteButton.setOnTouchListener(this);
    this.mDeleteButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (KeyguardPinBasedInputView.this.mPasswordEntry.isEnabled()) {
          KeyguardPinBasedInputView.this.mPasswordEntry.deleteLastChar();
        }
      }
    });
    this.mDeleteButton.setOnLongClickListener(new View.OnLongClickListener()
    {
      public boolean onLongClick(View paramAnonymousView)
      {
        if (KeyguardPinBasedInputView.this.mPasswordEntry.isEnabled()) {
          KeyguardPinBasedInputView.this.resetPasswordText(true, true);
        }
        KeyguardPinBasedInputView.this.doHapticKeyClick();
        return true;
      }
    });
    this.mButton0 = findViewById(R.id.key0);
    this.mButton1 = findViewById(R.id.key1);
    this.mButton2 = findViewById(R.id.key2);
    this.mButton3 = findViewById(R.id.key3);
    this.mButton4 = findViewById(R.id.key4);
    this.mButton5 = findViewById(R.id.key5);
    this.mButton6 = findViewById(R.id.key6);
    this.mButton7 = findViewById(R.id.key7);
    this.mButton8 = findViewById(R.id.key8);
    this.mButton9 = findViewById(R.id.key9);
    this.mPasswordEntry.requestFocus();
    super.onFinishInflate();
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() == 0) {
      return onKeyDown(paramInt, paramKeyEvent);
    }
    return false;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (KeyEvent.isConfirmKey(paramInt))
    {
      performClick(this.mOkButton);
      return true;
    }
    if (paramInt == 67)
    {
      performClick(this.mDeleteButton);
      return true;
    }
    if ((paramInt >= 7) && (paramInt <= 16))
    {
      performNumberClick(paramInt - 7);
      return true;
    }
    if ((paramInt >= 144) && (paramInt <= 153))
    {
      performNumberClick(paramInt - 144);
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    return this.mPasswordEntry.requestFocus(paramInt, paramRect);
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getActionMasked() == 0) {
      doHapticKeyClick();
    }
    return false;
  }
  
  public void reset()
  {
    this.mPasswordEntry.requestFocus();
    super.reset();
  }
  
  protected void resetPasswordText(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mPasswordEntry.reset(paramBoolean1, paramBoolean2);
  }
  
  protected void resetState()
  {
    setPasswordEntryEnabled(true);
  }
  
  protected void setPasswordEntryEnabled(boolean paramBoolean)
  {
    this.mPasswordEntry.setEnabled(paramBoolean);
    this.mOkButton.setEnabled(paramBoolean);
  }
  
  protected void setPasswordEntryInputEnabled(boolean paramBoolean)
  {
    this.mPasswordEntry.setEnabled(paramBoolean);
    this.mOkButton.setEnabled(paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardPinBasedInputView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */