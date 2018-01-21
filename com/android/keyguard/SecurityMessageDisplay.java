package com.android.keyguard;

public abstract interface SecurityMessageDisplay
{
  public abstract void setMessage(int paramInt, boolean paramBoolean);
  
  public abstract void setMessage(int paramInt, boolean paramBoolean, Object... paramVarArgs);
  
  public abstract void setMessage(CharSequence paramCharSequence, boolean paramBoolean);
  
  public abstract void setNextMessageColor(int paramInt);
  
  public abstract void setTimeout(int paramInt);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\SecurityMessageDisplay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */