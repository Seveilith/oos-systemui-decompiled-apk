package com.android.systemui;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.service.dreams.Sandman;

public class Somnambulator
  extends Activity
{
  public void onStart()
  {
    super.onStart();
    Intent localIntent1 = getIntent();
    if ("android.intent.action.CREATE_SHORTCUT".equals(localIntent1.getAction()))
    {
      localIntent1 = new Intent(this, Somnambulator.class);
      localIntent1.setFlags(276824064);
      Intent localIntent2 = new Intent();
      localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(this, 2130903041));
      localIntent2.putExtra("android.intent.extra.shortcut.INTENT", localIntent1);
      localIntent2.putExtra("android.intent.extra.shortcut.NAME", getString(2131690262));
      setResult(-1, localIntent2);
    }
    for (;;)
    {
      finish();
      return;
      if (localIntent1.hasCategory("android.intent.category.DESK_DOCK")) {
        Sandman.startDreamWhenDockedIfAppropriate(this);
      } else {
        Sandman.startDreamByUserRequest(this);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\Somnambulator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */