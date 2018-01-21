package com.android.keyguard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.OpFeatures;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.internal.widget.TextViewInputDisabler;
import java.util.Iterator;
import java.util.List;

public class KeyguardPasswordView
  extends KeyguardAbsKeyInputView
  implements KeyguardSecurityView, TextView.OnEditorActionListener, TextWatcher
{
  private final String TAG = "KeyguardPasswordView";
  private final int mDisappearYTranslation;
  private Interpolator mFastOutLinearInInterpolator;
  InputMethodManager mImm;
  private boolean mIsResume = false;
  private Interpolator mLinearOutSlowInInterpolator;
  private TextView mPasswordEntry;
  private TextViewInputDisabler mPasswordEntryDisabler;
  private final boolean mShowImeAtScreenOn;
  private View mSwitchImeButton;
  
  public KeyguardPasswordView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardPasswordView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mShowImeAtScreenOn = paramContext.getResources().getBoolean(R.bool.kg_show_ime_at_screen_on);
    this.mDisappearYTranslation = getResources().getDimensionPixelSize(R.dimen.disappear_y_translation);
    this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(paramContext, 17563662);
    this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(paramContext, 17563663);
  }
  
  private boolean hasMultipleEnabledIMEsOrSubtypes(InputMethodManager paramInputMethodManager, boolean paramBoolean)
  {
    Object localObject1 = paramInputMethodManager.getEnabledInputMethodList();
    int i = 0;
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (InputMethodInfo)((Iterator)localObject1).next();
      if (i > 1) {
        return true;
      }
      localObject2 = paramInputMethodManager.getEnabledInputMethodSubtypeList((InputMethodInfo)localObject2, true);
      if (((List)localObject2).isEmpty())
      {
        i += 1;
      }
      else
      {
        int j = 0;
        Iterator localIterator = ((Iterable)localObject2).iterator();
        while (localIterator.hasNext()) {
          if (((InputMethodSubtype)localIterator.next()).isAuxiliary()) {
            j += 1;
          }
        }
        if ((((List)localObject2).size() - j > 0) || ((paramBoolean) && (j > 1))) {
          i += 1;
        }
      }
    }
    return (i > 1) || (paramInputMethodManager.getEnabledInputMethodSubtypeList(null, false).size() > 1);
  }
  
  private void requestShowIME()
  {
    Log.i("KeyguardPasswordView", "requestShowIME: mIsResume:" + this.mIsResume + " isShown:" + isShown() + " input Enable:" + this.mPasswordEntry.isEnabled());
    if ((this.mIsResume) && (this.mPasswordEntry.isEnabled()))
    {
      Log.i("KeyguardPasswordView", "request IME show");
      this.mImm.showSoftInput(this.mPasswordEntry, 1);
    }
  }
  
  private void setGoogleLatinImeWhenDirectBoot()
  {
    int i;
    if (!((UserManager)this.mContext.getSystemService(UserManager.class)).isUserUnlocked(KeyguardUpdateMonitor.getCurrentUser()))
    {
      List localList = this.mImm.getInputMethodList();
      localObject2 = null;
      boolean bool = OpFeatures.isSupport(new int[] { 1 });
      Object localObject1 = localObject2;
      if (localList != null)
      {
        int j = localList.size();
        i = 0;
        localObject1 = localObject2;
        if (i < j)
        {
          localObject1 = (InputMethodInfo)localList.get(i);
          if (!"com.android.inputmethod.latin.LatinIME".equals(((InputMethodInfo)localObject1).getServiceName())) {
            break label171;
          }
          localObject1 = ((InputMethodInfo)localObject1).getId();
        }
      }
      Log.d("KeyguardPasswordView", "start latin ime: " + (String)localObject1 + ", global:" + bool);
      localObject2 = localObject1;
      if (localObject1 == null) {
        if (!bool) {
          break label178;
        }
      }
    }
    for (Object localObject2 = "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME";; localObject2 = "com.android.inputmethod.latin/.LatinIME")
    {
      try
      {
        this.mImm.setInputMethod(getWindowToken(), (String)localObject2);
        return;
      }
      catch (Exception localException)
      {
        label171:
        label178:
        Log.i("KeyguardPasswordView", "set Google IME fail " + localException);
      }
      i += 1;
      break;
    }
  }
  
  private void updateSwitchImeButton()
  {
    boolean bool1;
    Object localObject;
    if (this.mSwitchImeButton.getVisibility() == 0)
    {
      bool1 = true;
      boolean bool2 = hasMultipleEnabledIMEsOrSubtypes(this.mImm, false);
      if (bool1 != bool2)
      {
        localObject = this.mSwitchImeButton;
        if (!bool2) {
          break label96;
        }
      }
    }
    label96:
    for (int i = 0;; i = 8)
    {
      ((View)localObject).setVisibility(i);
      if (this.mSwitchImeButton.getVisibility() != 0)
      {
        localObject = this.mPasswordEntry.getLayoutParams();
        if ((localObject instanceof ViewGroup.MarginLayoutParams))
        {
          ((ViewGroup.MarginLayoutParams)localObject).setMarginStart(0);
          this.mPasswordEntry.setLayoutParams((ViewGroup.LayoutParams)localObject);
        }
      }
      return;
      bool1 = false;
      break;
    }
  }
  
  public void afterTextChanged(Editable paramEditable)
  {
    if (!TextUtils.isEmpty(paramEditable)) {
      onUserInput();
    }
  }
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.mCallback != null) {
      this.mCallback.userActivity();
    }
  }
  
  protected String getPasswordText()
  {
    return this.mPasswordEntry.getText().toString();
  }
  
  protected int getPasswordTextViewId()
  {
    return R.id.passwordEntry;
  }
  
  protected int getPromtReasonStringRes(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return R.string.kg_prompt_reason_timeout_password;
    case 1: 
      return R.string.kg_prompt_reason_restart_password;
    case 2: 
      return R.string.kg_prompt_reason_timeout_password;
    case 3: 
      return R.string.kg_prompt_reason_device_admin;
    case 4: 
      return R.string.kg_prompt_reason_user_request;
    }
    return 0;
  }
  
  public int getWrongPasswordStringId()
  {
    int i = KeyguardUpdateMonitor.getCurrentUser();
    i = KeyguardUpdateMonitor.getInstance(this.mContext).getFailedUnlockAttempts(i);
    if (i % 5 == 3) {
      return R.string.kg_wrong_pin_warning;
    }
    if (i % 5 == 4) {
      return R.string.kg_wrong_pin_warning_one;
    }
    return R.string.kg_wrong_password;
  }
  
  public boolean isCheckingPassword()
  {
    return super.isCheckingPassword();
  }
  
  public boolean needsInput()
  {
    return true;
  }
  
  public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
  {
    int i;
    if (paramKeyEvent == null) {
      if ((paramInt == 0) || (paramInt == 6))
      {
        paramInt = 1;
        if ((paramKeyEvent == null) || (!KeyEvent.isConfirmKey(paramKeyEvent.getKeyCode()))) {
          break label81;
        }
        if (paramKeyEvent.getAction() != 0) {
          break label75;
        }
        i = 1;
      }
    }
    for (;;)
    {
      if ((paramInt == 0) && (i == 0)) {
        break label87;
      }
      verifyPasswordAndUnlock();
      return true;
      if (paramInt == 5)
      {
        paramInt = 1;
        break;
      }
      paramInt = 0;
      break;
      paramInt = 0;
      break;
      label75:
      i = 0;
      continue;
      label81:
      i = 0;
    }
    label87:
    return false;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mImm = ((InputMethodManager)getContext().getSystemService("input_method"));
    this.mPasswordEntry = ((TextView)findViewById(getPasswordTextViewId()));
    this.mPasswordEntryDisabler = new TextViewInputDisabler(this.mPasswordEntry);
    this.mPasswordEntry.setKeyListener(TextKeyListener.getInstance());
    this.mPasswordEntry.setInputType(129);
    this.mPasswordEntry.setOnEditorActionListener(this);
    this.mPasswordEntry.addTextChangedListener(this);
    this.mPasswordEntry.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        KeyguardPasswordView.this.mCallback.userActivity();
      }
    });
    this.mPasswordEntry.setSelected(true);
    this.mPasswordEntry.requestFocus();
    this.mSwitchImeButton = findViewById(R.id.switch_ime_button);
    this.mSwitchImeButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        KeyguardPasswordView.this.mCallback.userActivity();
        KeyguardPasswordView.this.mImm.showInputMethodPicker(false);
      }
    });
    updateSwitchImeButton();
    postDelayed(new Runnable()
    {
      public void run()
      {
        KeyguardPasswordView.-wrap1(KeyguardPasswordView.this);
      }
    }, 500L);
    setGoogleLatinImeWhenDirectBoot();
  }
  
  public void onPause()
  {
    super.onPause();
    this.mIsResume = false;
    this.mImm.hideSoftInputFromWindow(getWindowToken(), 0);
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    return this.mPasswordEntry.requestFocus(paramInt, paramRect);
  }
  
  public void onResume(final int paramInt)
  {
    super.onResume(paramInt);
    this.mIsResume = true;
    post(new Runnable()
    {
      public void run()
      {
        if ((KeyguardPasswordView.this.isShown()) && (KeyguardPasswordView.-get0(KeyguardPasswordView.this).isEnabled()))
        {
          KeyguardPasswordView.-get0(KeyguardPasswordView.this).requestFocus();
          Log.i("KeyguardPasswordView", " onResume reason:" + paramInt);
          if ((paramInt != 1) || (KeyguardPasswordView.-get1(KeyguardPasswordView.this))) {
            KeyguardPasswordView.-wrap0(KeyguardPasswordView.this);
          }
        }
      }
    });
  }
  
  public void onScreenStateChanged(int paramInt)
  {
    super.onScreenStateChanged(paramInt);
    Log.i("KeyguardPasswordView", "onScreenStateChanged: screenState:" + paramInt);
    if (paramInt == 1) {
      requestShowIME();
    }
  }
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    Log.i("KeyguardPasswordView", "onWindowFocusChanged: hasWindowFocus:" + paramBoolean);
    if ((paramBoolean) && (this.mPasswordEntry != null) && (this.mPasswordEntry.isEnabled())) {
      requestShowIME();
    }
  }
  
  public void reset()
  {
    super.reset();
    this.mPasswordEntry.requestFocus();
  }
  
  protected void resetPasswordText(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mPasswordEntry.setText("");
  }
  
  protected void resetState()
  {
    this.mSecurityMessageDisplay.setTimeout(0);
    this.mSecurityMessageDisplay.setMessage(R.string.kg_password_instructions, true);
    boolean bool = this.mPasswordEntry.isEnabled();
    setPasswordEntryEnabled(true);
    setPasswordEntryInputEnabled(true);
    Log.i("KeyguardPasswordView", " resetState wasDisabled:" + bool);
    if (bool) {
      requestShowIME();
    }
  }
  
  protected void setPasswordEntryEnabled(boolean paramBoolean)
  {
    this.mPasswordEntry.setEnabled(paramBoolean);
  }
  
  protected void setPasswordEntryInputEnabled(boolean paramBoolean)
  {
    this.mPasswordEntryDisabler.setInputEnabled(paramBoolean);
  }
  
  public void startAppearAnimation()
  {
    setAlpha(0.0F);
    setTranslationY(0.0F);
    animate().alpha(1.0F).withLayer().setDuration(300L).setInterpolator(this.mLinearOutSlowInInterpolator);
  }
  
  public boolean startDisappearAnimation(Runnable paramRunnable)
  {
    animate().alpha(0.0F).translationY(this.mDisappearYTranslation).setInterpolator(this.mFastOutLinearInInterpolator).setDuration(100L).withEndAction(paramRunnable);
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardPasswordView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */