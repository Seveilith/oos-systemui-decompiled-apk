package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class TintTypedArray
{
  private final Context mContext;
  private final TypedArray mWrapped;
  
  private TintTypedArray(Context paramContext, TypedArray paramTypedArray)
  {
    this.mContext = paramContext;
    this.mWrapped = paramTypedArray;
  }
  
  public static TintTypedArray obtainStyledAttributes(Context paramContext, int paramInt, int[] paramArrayOfInt)
  {
    return new TintTypedArray(paramContext, paramContext.obtainStyledAttributes(paramInt, paramArrayOfInt));
  }
  
  public static TintTypedArray obtainStyledAttributes(Context paramContext, AttributeSet paramAttributeSet, int[] paramArrayOfInt)
  {
    return new TintTypedArray(paramContext, paramContext.obtainStyledAttributes(paramAttributeSet, paramArrayOfInt));
  }
  
  public static TintTypedArray obtainStyledAttributes(Context paramContext, AttributeSet paramAttributeSet, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    return new TintTypedArray(paramContext, paramContext.obtainStyledAttributes(paramAttributeSet, paramArrayOfInt, paramInt1, paramInt2));
  }
  
  public boolean getBoolean(int paramInt, boolean paramBoolean)
  {
    return this.mWrapped.getBoolean(paramInt, paramBoolean);
  }
  
  public int getColor(int paramInt1, int paramInt2)
  {
    return this.mWrapped.getColor(paramInt1, paramInt2);
  }
  
  public ColorStateList getColorStateList(int paramInt)
  {
    if (this.mWrapped.hasValue(paramInt))
    {
      int i = this.mWrapped.getResourceId(paramInt, 0);
      if (i != 0)
      {
        ColorStateList localColorStateList = AppCompatResources.getColorStateList(this.mContext, i);
        if (localColorStateList != null) {
          return localColorStateList;
        }
      }
    }
    return this.mWrapped.getColorStateList(paramInt);
  }
  
  public int getDimensionPixelOffset(int paramInt1, int paramInt2)
  {
    return this.mWrapped.getDimensionPixelOffset(paramInt1, paramInt2);
  }
  
  public int getDimensionPixelSize(int paramInt1, int paramInt2)
  {
    return this.mWrapped.getDimensionPixelSize(paramInt1, paramInt2);
  }
  
  public Drawable getDrawable(int paramInt)
  {
    if (this.mWrapped.hasValue(paramInt))
    {
      int i = this.mWrapped.getResourceId(paramInt, 0);
      if (i != 0) {
        return AppCompatResources.getDrawable(this.mContext, i);
      }
    }
    return this.mWrapped.getDrawable(paramInt);
  }
  
  public Drawable getDrawableIfKnown(int paramInt)
  {
    if (this.mWrapped.hasValue(paramInt))
    {
      paramInt = this.mWrapped.getResourceId(paramInt, 0);
      if (paramInt != 0) {
        return AppCompatDrawableManager.get().getDrawable(this.mContext, paramInt, true);
      }
    }
    return null;
  }
  
  public float getFloat(int paramInt, float paramFloat)
  {
    return this.mWrapped.getFloat(paramInt, paramFloat);
  }
  
  public int getInt(int paramInt1, int paramInt2)
  {
    return this.mWrapped.getInt(paramInt1, paramInt2);
  }
  
  public int getInteger(int paramInt1, int paramInt2)
  {
    return this.mWrapped.getInteger(paramInt1, paramInt2);
  }
  
  public int getLayoutDimension(int paramInt1, int paramInt2)
  {
    return this.mWrapped.getLayoutDimension(paramInt1, paramInt2);
  }
  
  public int getResourceId(int paramInt1, int paramInt2)
  {
    return this.mWrapped.getResourceId(paramInt1, paramInt2);
  }
  
  public String getString(int paramInt)
  {
    return this.mWrapped.getString(paramInt);
  }
  
  public CharSequence getText(int paramInt)
  {
    return this.mWrapped.getText(paramInt);
  }
  
  public CharSequence[] getTextArray(int paramInt)
  {
    return this.mWrapped.getTextArray(paramInt);
  }
  
  public boolean hasValue(int paramInt)
  {
    return this.mWrapped.hasValue(paramInt);
  }
  
  public void recycle()
  {
    this.mWrapped.recycle();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\TintTypedArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */