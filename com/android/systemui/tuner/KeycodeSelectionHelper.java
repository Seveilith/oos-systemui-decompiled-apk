package com.android.systemui.tuner;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class KeycodeSelectionHelper
{
  private static final ArrayList<String> mKeycodeStrings = new ArrayList();
  private static final ArrayList<Integer> mKeycodes = new ArrayList();
  
  static
  {
    Field[] arrayOfField = KeyEvent.class.getDeclaredFields();
    int j = arrayOfField.length;
    int i = 0;
    for (;;)
    {
      Field localField;
      if (i < j)
      {
        localField = arrayOfField[i];
        if ((!Modifier.isStatic(localField.getModifiers())) || (!localField.getName().startsWith("KEYCODE_")) || (!localField.getType().equals(Integer.TYPE))) {}
      }
      try
      {
        mKeycodeStrings.add(formatString(localField.getName()));
        mKeycodes.add((Integer)localField.get(null));
        i += 1;
        continue;
        return;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        for (;;) {}
      }
    }
  }
  
  private static String formatString(String paramString)
  {
    paramString = new StringBuilder(paramString.replace("KEYCODE_", "").replace("_", " ").toLowerCase());
    int i = 0;
    while (i < paramString.length())
    {
      if ((i == 0) || (paramString.charAt(i - 1) == ' ')) {
        paramString.setCharAt(i, Character.toUpperCase(paramString.charAt(i)));
      }
      i += 1;
    }
    return paramString.toString();
  }
  
  public static Intent getSelectImageIntent()
  {
    return new Intent("android.intent.action.OPEN_DOCUMENT").addCategory("android.intent.category.OPENABLE").setType("image/*");
  }
  
  public static void showKeycodeSelect(Context paramContext, OnSelectionComplete paramOnSelectionComplete)
  {
    new AlertDialog.Builder(paramContext).setTitle(2131690602).setItems((CharSequence[])mKeycodeStrings.toArray(new String[0]), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        this.val$listener.onSelectionComplete(((Integer)KeycodeSelectionHelper.-get0().get(paramAnonymousInt)).intValue());
      }
    }).show();
  }
  
  public static abstract interface OnSelectionComplete
  {
    public abstract void onSelectionComplete(int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\KeycodeSelectionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */