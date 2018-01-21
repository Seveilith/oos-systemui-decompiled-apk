package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;

public abstract class DialogPreference
  extends Preference
{
  private Drawable mDialogIcon;
  private int mDialogLayoutResId;
  private CharSequence mDialogMessage;
  private CharSequence mDialogTitle;
  private CharSequence mNegativeButtonText;
  private CharSequence mPositiveButtonText;
  
  public DialogPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.dialogPreferenceStyle, 16842897));
  }
  
  public DialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public DialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.DialogPreference, paramInt1, paramInt2);
    this.mDialogTitle = TypedArrayUtils.getString(paramContext, R.styleable.DialogPreference_dialogTitle, R.styleable.DialogPreference_android_dialogTitle);
    if (this.mDialogTitle == null) {
      this.mDialogTitle = getTitle();
    }
    this.mDialogMessage = TypedArrayUtils.getString(paramContext, R.styleable.DialogPreference_dialogMessage, R.styleable.DialogPreference_android_dialogMessage);
    this.mDialogIcon = TypedArrayUtils.getDrawable(paramContext, R.styleable.DialogPreference_dialogIcon, R.styleable.DialogPreference_android_dialogIcon);
    this.mPositiveButtonText = TypedArrayUtils.getString(paramContext, R.styleable.DialogPreference_positiveButtonText, R.styleable.DialogPreference_android_positiveButtonText);
    this.mNegativeButtonText = TypedArrayUtils.getString(paramContext, R.styleable.DialogPreference_negativeButtonText, R.styleable.DialogPreference_android_negativeButtonText);
    this.mDialogLayoutResId = TypedArrayUtils.getResourceId(paramContext, R.styleable.DialogPreference_dialogLayout, R.styleable.DialogPreference_android_dialogLayout, 0);
    paramContext.recycle();
  }
  
  public Drawable getDialogIcon()
  {
    return this.mDialogIcon;
  }
  
  public int getDialogLayoutResource()
  {
    return this.mDialogLayoutResId;
  }
  
  public CharSequence getDialogMessage()
  {
    return this.mDialogMessage;
  }
  
  public CharSequence getDialogTitle()
  {
    return this.mDialogTitle;
  }
  
  public CharSequence getNegativeButtonText()
  {
    return this.mNegativeButtonText;
  }
  
  public CharSequence getPositiveButtonText()
  {
    return this.mPositiveButtonText;
  }
  
  protected void onClick()
  {
    getPreferenceManager().showDialog(this);
  }
  
  public static abstract interface TargetFragment
  {
    public abstract Preference findPreference(CharSequence paramCharSequence);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\preference\DialogPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */