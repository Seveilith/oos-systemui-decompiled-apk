package com.android.keyguard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

public abstract class KeyguardPinBasedInputViewForPin
  extends KeyguardAbsKeyInputView
  implements View.OnKeyListener
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
  private TextView mDeleteButton;
  private View mOkButton;
  protected PasswordTextViewForPin mPasswordEntry;
  
  public KeyguardPinBasedInputViewForPin(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardPinBasedInputViewForPin(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void performClick(View paramView)
  {
    if (paramView != null) {
      paramView.performClick();
    }
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
    this.mPasswordEntry = ((PasswordTextViewForPin)findViewById(getPasswordTextViewId()));
    this.mPasswordEntry.setOnKeyListener(this);
    this.mPasswordEntry.setSelected(true);
    this.mPasswordEntry.setUserActivityListener(new PasswordTextViewForPin.UserActivityListener()
    {
      public void onCheckPasswordAndUnlock()
      {
        KeyguardPinBasedInputViewForPin.this.doHapticKeyClick();
        if (KeyguardPinBasedInputViewForPin.this.mPasswordEntry.isEnabled()) {
          KeyguardPinBasedInputViewForPin.this.verifyPasswordAndUnlock();
        }
      }
      
      public void onUserActivity()
      {
        KeyguardPinBasedInputViewForPin.this.onUserInput();
      }
    });
    this.mOkButton = findViewById(R.id.key_enter);
    if (this.mOkButton != null)
    {
      this.mOkButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          KeyguardPinBasedInputViewForPin.this.doHapticKeyClick();
          if (KeyguardPinBasedInputViewForPin.this.mPasswordEntry.isEnabled()) {
            KeyguardPinBasedInputViewForPin.this.verifyPasswordAndUnlock();
          }
        }
      });
      this.mOkButton.setOnHoverListener(new LiftToActivateListener(getContext()));
    }
    this.mDeleteButton = ((TextView)findViewById(R.id.deleteOrCancel));
    this.mDeleteButton.setText(getContext().getResources().getString(R.string.aosp_remove_string));
    this.mDeleteButton.setVisibility(0);
    this.mDeleteButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (KeyguardPinBasedInputViewForPin.this.mPasswordEntry.isEnabled()) {
          KeyguardPinBasedInputViewForPin.this.mPasswordEntry.deleteLastChar();
        }
        KeyguardPinBasedInputViewForPin.this.doHapticKeyClick();
      }
    });
    this.mDeleteButton.setOnLongClickListener(new View.OnLongClickListener()
    {
      public boolean onLongClick(View paramAnonymousView)
      {
        if (KeyguardPinBasedInputViewForPin.this.mPasswordEntry.isEnabled()) {
          KeyguardPinBasedInputViewForPin.this.resetPasswordText(true, true);
        }
        KeyguardPinBasedInputViewForPin.this.doHapticKeyClick();
        return true;
      }
    });
    this.mPasswordEntry.setTextChangeListener(new PasswordTextViewForPin.onTextChangedListerner()
    {
      public void onTextChanged(String paramAnonymousString) {}
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
    if (paramKeyEvent.getAction() == 0)
    {
      onKeyDown(paramInt, paramKeyEvent);
      return true;
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
    this.mPasswordEntry.setEnabled(true);
  }
  
  protected void setPasswordEntryEnabled(boolean paramBoolean)
  {
    this.mPasswordEntry.setEnabled(paramBoolean);
  }
  
  protected void setPasswordEntryInputEnabled(boolean paramBoolean)
  {
    this.mPasswordEntry.setEnabled(paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardPinBasedInputViewForPin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */