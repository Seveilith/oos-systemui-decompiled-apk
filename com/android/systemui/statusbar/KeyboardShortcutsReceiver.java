package com.android.systemui.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KeyboardShortcutsReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ("android.intent.action.SHOW_KEYBOARD_SHORTCUTS".equals(paramIntent.getAction())) {
      KeyboardShortcuts.show(paramContext, -1);
    }
    while (!"android.intent.action.DISMISS_KEYBOARD_SHORTCUTS".equals(paramIntent.getAction())) {
      return;
    }
    KeyboardShortcuts.dismiss();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\KeyboardShortcutsReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */