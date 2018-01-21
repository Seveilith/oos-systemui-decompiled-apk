package android.support.v17.leanback.widget.picker;

import android.content.res.Resources;
import android.support.v17.leanback.R.string;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

class PickerConstant
{
  public final String[] ampm;
  public final String dateSeparator;
  public final String[] days;
  public final String[] hours12;
  public final String[] hours24;
  public final Locale locale;
  public final String[] minutes;
  public final String[] months;
  public final String timeSeparator;
  
  public PickerConstant(Locale paramLocale, Resources paramResources)
  {
    this.locale = paramLocale;
    DateFormatSymbols localDateFormatSymbols = DateFormatSymbols.getInstance(paramLocale);
    this.months = localDateFormatSymbols.getShortMonths();
    paramLocale = Calendar.getInstance(paramLocale);
    this.days = createStringIntArrays(paramLocale.getMinimum(5), paramLocale.getMaximum(5), "%02d");
    this.hours12 = createStringIntArrays(1, 12, "%02d");
    this.hours24 = createStringIntArrays(0, 23, "%02d");
    this.minutes = createStringIntArrays(0, 59, "%02d");
    this.ampm = localDateFormatSymbols.getAmPmStrings();
    this.dateSeparator = paramResources.getString(R.string.lb_date_separator);
    this.timeSeparator = paramResources.getString(R.string.lb_time_separator);
  }
  
  public static String[] createStringIntArrays(int paramInt1, int paramInt2, String paramString)
  {
    String[] arrayOfString = new String[paramInt2 - paramInt1 + 1];
    int i = paramInt1;
    if (i <= paramInt2)
    {
      if (paramString != null) {
        arrayOfString[(i - paramInt1)] = String.format(paramString, new Object[] { Integer.valueOf(i) });
      }
      for (;;)
      {
        i += 1;
        break;
        arrayOfString[(i - paramInt1)] = String.valueOf(i);
      }
    }
    return arrayOfString;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\picker\PickerConstant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */