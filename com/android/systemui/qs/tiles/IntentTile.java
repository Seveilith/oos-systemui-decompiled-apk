package com.android.systemui.qs.tiles;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.Icon;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.qs.QSTile.State;
import java.util.Arrays;
import java.util.Objects;

public class IntentTile
  extends QSTile<QSTile.State>
{
  private int mCurrentUserId;
  private String mIntentPackage;
  private Intent mLastIntent;
  private PendingIntent mOnClick;
  private String mOnClickUri;
  private PendingIntent mOnLaunch;
  private String mOnLaunchUri;
  private PendingIntent mOnLongClick;
  private String mOnLongClickUri;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      IntentTile.-wrap0(IntentTile.this, paramAnonymousIntent);
    }
  };
  
  private IntentTile(QSTile.Host paramHost, String paramString)
  {
    super(paramHost);
    this.mContext.registerReceiver(this.mReceiver, new IntentFilter(paramString));
  }
  
  public static QSTile<?> create(QSTile.Host paramHost, String paramString)
  {
    if ((paramString != null) && (paramString.startsWith("intent(")) && (paramString.endsWith(")")))
    {
      paramString = paramString.substring("intent(".length(), paramString.length() - 1);
      if (paramString.isEmpty()) {
        throw new IllegalArgumentException("Empty intent tile spec action");
      }
    }
    else
    {
      throw new IllegalArgumentException("Bad intent tile spec: " + paramString);
    }
    return new IntentTile(paramHost, paramString);
  }
  
  private void sendIntent(String paramString1, PendingIntent paramPendingIntent, String paramString2)
  {
    if (paramPendingIntent != null) {
      try
      {
        if (paramPendingIntent.isActivity())
        {
          getHost().startActivityDismissingKeyguard(paramPendingIntent);
          return;
        }
        paramPendingIntent.send();
        return;
      }
      catch (Throwable paramPendingIntent)
      {
        Log.w(this.TAG, "Error sending " + paramString1 + " intent", paramPendingIntent);
        return;
      }
    }
    if (paramString2 != null)
    {
      paramPendingIntent = Intent.parseUri(paramString2, 1);
      this.mContext.sendBroadcastAsUser(paramPendingIntent, new UserHandle(this.mCurrentUserId));
    }
  }
  
  public Intent getLongClickIntent()
  {
    return null;
  }
  
  public int getMetricsCategory()
  {
    return 121;
  }
  
  public CharSequence getTileLabel()
  {
    return getState().label;
  }
  
  protected void handleClick()
  {
    MetricsLogger.action(this.mContext, getMetricsCategory(), this.mIntentPackage);
    sendIntent("click", this.mOnClick, this.mOnClickUri);
  }
  
  protected void handleDestroy()
  {
    super.handleDestroy();
    this.mContext.unregisterReceiver(this.mReceiver);
  }
  
  protected void handleLongClick()
  {
    sendIntent("long-click", this.mOnLongClick, this.mOnLongClickUri);
  }
  
  protected void handleUpdateState(QSTile.State paramState, Object paramObject)
  {
    Object localObject = (Intent)paramObject;
    paramObject = localObject;
    if (localObject == null)
    {
      if (this.mLastIntent == null) {
        return;
      }
      paramObject = this.mLastIntent;
    }
    this.mLastIntent = ((Intent)paramObject);
    paramState.contentDescription = ((Intent)paramObject).getStringExtra("contentDescription");
    paramState.label = ((Intent)paramObject).getStringExtra("label");
    paramState.icon = null;
    localObject = ((Intent)paramObject).getByteArrayExtra("iconBitmap");
    if (localObject != null) {}
    for (;;)
    {
      try
      {
        paramState.icon = new BytesIcon((byte[])localObject);
        this.mOnClick = ((PendingIntent)((Intent)paramObject).getParcelableExtra("onClick"));
        this.mOnClickUri = ((Intent)paramObject).getStringExtra("onClickUri");
        this.mOnLongClick = ((PendingIntent)((Intent)paramObject).getParcelableExtra("onLongClick"));
        this.mOnLongClickUri = ((Intent)paramObject).getStringExtra("onLongClickUri");
        this.mOnLaunch = ((PendingIntent)((Intent)paramObject).getParcelableExtra("onLaunch"));
        this.mOnLaunchUri = ((Intent)paramObject).getStringExtra("onLaunchUri");
        this.mIntentPackage = ((Intent)paramObject).getStringExtra("package");
        if (this.mIntentPackage != null) {
          break label275;
        }
        paramState = "";
        this.mIntentPackage = paramState;
        return;
      }
      catch (Throwable paramState)
      {
        Log.w(this.TAG, "Error loading icon bitmap, length " + localObject.length, paramState);
        continue;
      }
      int i = ((Intent)paramObject).getIntExtra("iconId", 0);
      if (i != 0)
      {
        localObject = ((Intent)paramObject).getStringExtra("iconPackage");
        if (!TextUtils.isEmpty((CharSequence)localObject))
        {
          paramState.icon = new PackageDrawableIcon((String)localObject, i);
        }
        else
        {
          paramState.icon = QSTile.ResourceIcon.get(i);
          continue;
          label275:
          paramState = this.mIntentPackage;
        }
      }
    }
  }
  
  protected void handleUserSwitch(int paramInt)
  {
    super.handleUserSwitch(paramInt);
    this.mCurrentUserId = paramInt;
  }
  
  public QSTile.State newTileState()
  {
    return new QSTile.State();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean) {
      sendIntent("onLaunch", this.mOnLaunch, this.mOnLaunchUri);
    }
  }
  
  private static class BytesIcon
    extends QSTile.Icon
  {
    private final byte[] mBytes;
    
    public BytesIcon(byte[] paramArrayOfByte)
    {
      this.mBytes = paramArrayOfByte;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof BytesIcon)) {
        return Arrays.equals(((BytesIcon)paramObject).mBytes, this.mBytes);
      }
      return false;
    }
    
    public Drawable getDrawable(Context paramContext)
    {
      Bitmap localBitmap = BitmapFactory.decodeByteArray(this.mBytes, 0, this.mBytes.length);
      return new BitmapDrawable(paramContext.getResources(), localBitmap);
    }
    
    public String toString()
    {
      return String.format("BytesIcon[len=%s]", new Object[] { Integer.valueOf(this.mBytes.length) });
    }
  }
  
  private class PackageDrawableIcon
    extends QSTile.Icon
  {
    private final String mPackage;
    private final int mResId;
    
    public PackageDrawableIcon(String paramString, int paramInt)
    {
      this.mPackage = paramString;
      this.mResId = paramInt;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof PackageDrawableIcon)) {
        return false;
      }
      paramObject = (PackageDrawableIcon)paramObject;
      boolean bool1 = bool2;
      if (Objects.equals(((PackageDrawableIcon)paramObject).mPackage, this.mPackage))
      {
        bool1 = bool2;
        if (((PackageDrawableIcon)paramObject).mResId == this.mResId) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public Drawable getDrawable(Context paramContext)
    {
      try
      {
        paramContext = paramContext.createPackageContext(this.mPackage, 0).getDrawable(this.mResId);
        return paramContext;
      }
      catch (Throwable paramContext)
      {
        Log.w(IntentTile.-get0(IntentTile.this), "Error loading package drawable pkg=" + this.mPackage + " id=" + this.mResId, paramContext);
      }
      return null;
    }
    
    public String toString()
    {
      return String.format("PackageDrawableIcon[pkg=%s,id=0x%08x]", new Object[] { this.mPackage, Integer.valueOf(this.mResId) });
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\IntentTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */