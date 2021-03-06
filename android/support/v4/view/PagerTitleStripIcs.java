package android.support.v4.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.TextView;
import java.util.Locale;

class PagerTitleStripIcs
{
  public static void setSingleLineAllCaps(TextView paramTextView)
  {
    paramTextView.setTransformationMethod(new SingleLineAllCapsTransform(paramTextView.getContext()));
  }
  
  private static class SingleLineAllCapsTransform
    extends SingleLineTransformationMethod
  {
    private Locale mLocale;
    
    public SingleLineAllCapsTransform(Context paramContext)
    {
      this.mLocale = paramContext.getResources().getConfiguration().locale;
    }
    
    public CharSequence getTransformation(CharSequence paramCharSequence, View paramView)
    {
      Object localObject = null;
      paramView = super.getTransformation(paramCharSequence, paramView);
      paramCharSequence = (CharSequence)localObject;
      if (paramView != null) {
        paramCharSequence = paramView.toString().toUpperCase(this.mLocale);
      }
      return paramCharSequence;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\PagerTitleStripIcs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */