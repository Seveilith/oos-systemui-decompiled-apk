package com.android.systemui.qs.customize;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import com.android.systemui.qs.QSIconView;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.DrawableIcon;
import com.android.systemui.qs.QSTile.State;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.statusbar.phone.QSTileHost;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TileQueryHelper
{
  private final Context mContext;
  private TileStateListener mListener;
  private final ArrayList<String> mSpecs = new ArrayList();
  private final ArrayList<TileInfo> mTiles = new ArrayList();
  
  public TileQueryHelper(Context paramContext, QSTileHost paramQSTileHost)
  {
    this.mContext = paramContext;
    addSystemTiles(paramQSTileHost);
  }
  
  private void addSystemTiles(final QSTileHost paramQSTileHost)
  {
    String[] arrayOfString = this.mContext.getString(2131689908).split(",");
    Handler localHandler1 = new Handler(paramQSTileHost.getLooper());
    final Handler localHandler2 = new Handler(Looper.getMainLooper());
    int i = 0;
    while (i < arrayOfString.length)
    {
      final String str = arrayOfString[i];
      final QSTile localQSTile = paramQSTileHost.createTile(str);
      if ((localQSTile != null) && (localQSTile.isAvailable()))
      {
        localQSTile.setListening(this, true);
        localQSTile.clearState();
        localQSTile.refreshState();
        localQSTile.setListening(this, false);
        localHandler1.post(new Runnable()
        {
          public void run()
          {
            final QSTile.State localState = localQSTile.newTileState();
            localQSTile.getState().copyTo(localState);
            localState.label = localQSTile.getTileLabel();
            localHandler2.post(new Runnable()
            {
              public void run()
              {
                TileQueryHelper.-wrap1(TileQueryHelper.this, this.val$spec, null, localState, true);
                TileQueryHelper.-get1(TileQueryHelper.this).onTilesChanged(TileQueryHelper.-get2(TileQueryHelper.this));
              }
            });
          }
        });
      }
      i += 1;
    }
    localHandler1.post(new Runnable()
    {
      public void run()
      {
        localHandler2.post(new Runnable()
        {
          public void run()
          {
            new TileQueryHelper.QueryTilesTask(TileQueryHelper.this, null).execute(new Collection[] { this.val$host.getTiles() });
          }
        });
      }
    });
  }
  
  private void addTile(String paramString, Drawable paramDrawable, CharSequence paramCharSequence1, CharSequence paramCharSequence2, Context paramContext)
  {
    paramContext = new QSTile.State();
    paramContext.label = paramCharSequence1;
    paramContext.contentDescription = paramCharSequence1;
    paramContext.icon = new QSTile.DrawableIcon(paramDrawable);
    addTile(paramString, paramCharSequence2, paramContext, false);
  }
  
  private void addTile(String paramString, CharSequence paramCharSequence, QSTile.State paramState, boolean paramBoolean)
  {
    if (this.mSpecs.contains(paramString)) {
      return;
    }
    TileInfo localTileInfo = new TileInfo();
    localTileInfo.state = paramState;
    paramState = localTileInfo.state;
    String str = Button.class.getName();
    localTileInfo.state.expandedAccessibilityClassName = str;
    paramState.minimalAccessibilityClassName = str;
    localTileInfo.spec = paramString;
    localTileInfo.appLabel = paramCharSequence;
    localTileInfo.isSystem = paramBoolean;
    this.mTiles.add(localTileInfo);
    this.mSpecs.add(paramString);
  }
  
  public void setListener(TileStateListener paramTileStateListener)
  {
    this.mListener = paramTileStateListener;
  }
  
  private class QueryTilesTask
    extends AsyncTask<Collection<QSTile<?>>, Void, Collection<TileQueryHelper.TileInfo>>
  {
    private QueryTilesTask() {}
    
    private QSTile.State getState(Collection<QSTile<?>> paramCollection, String paramString)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        paramCollection = (QSTile)localIterator.next();
        if (paramString.equals(paramCollection.getTileSpec()))
        {
          paramString = paramCollection.newTileState();
          paramCollection.getState().copyTo(paramString);
          return paramString;
        }
      }
      return null;
    }
    
    protected Collection<TileQueryHelper.TileInfo> doInBackground(Collection<QSTile<?>>... paramVarArgs)
    {
      ArrayList localArrayList = new ArrayList();
      PackageManager localPackageManager = TileQueryHelper.-get0(TileQueryHelper.this).getPackageManager();
      Object localObject1 = localPackageManager.queryIntentServicesAsUser(new Intent("android.service.quicksettings.action.QS_TILE"), 0, ActivityManager.getCurrentUser());
      String str = TileQueryHelper.-get0(TileQueryHelper.this).getString(2131689908);
      Iterator localIterator = ((Iterable)localObject1).iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (ResolveInfo)localIterator.next();
        Object localObject2 = new ComponentName(((ResolveInfo)localObject1).serviceInfo.packageName, ((ResolveInfo)localObject1).serviceInfo.name);
        if (!str.contains(((ComponentName)localObject2).flattenToString()))
        {
          CharSequence localCharSequence = ((ResolveInfo)localObject1).serviceInfo.applicationInfo.loadLabel(localPackageManager);
          localObject2 = CustomTile.toSpec((ComponentName)localObject2);
          Object localObject3 = getState(paramVarArgs[0], (String)localObject2);
          if (localObject3 != null)
          {
            TileQueryHelper.-wrap1(TileQueryHelper.this, (String)localObject2, localCharSequence, (QSTile.State)localObject3, false);
          }
          else if ((((ResolveInfo)localObject1).serviceInfo.icon != 0) || (((ResolveInfo)localObject1).serviceInfo.applicationInfo.icon != 0))
          {
            localObject3 = ((ResolveInfo)localObject1).serviceInfo.loadIcon(localPackageManager);
            if (("android.permission.BIND_QUICK_SETTINGS_TILE".equals(((ResolveInfo)localObject1).serviceInfo.permission)) && (localObject3 != null))
            {
              ((Drawable)localObject3).mutate();
              ((Drawable)localObject3).setTint(QSIconView.sCustomDisableIconColor);
              localObject1 = ((ResolveInfo)localObject1).serviceInfo.loadLabel(localPackageManager);
              TileQueryHelper localTileQueryHelper = TileQueryHelper.this;
              if (localObject1 != null) {}
              for (localObject1 = ((CharSequence)localObject1).toString();; localObject1 = "null")
              {
                TileQueryHelper.-wrap0(localTileQueryHelper, (String)localObject2, (Drawable)localObject3, (CharSequence)localObject1, localCharSequence, TileQueryHelper.-get0(TileQueryHelper.this));
                break;
              }
            }
          }
        }
      }
      return localArrayList;
    }
    
    protected void onPostExecute(Collection<TileQueryHelper.TileInfo> paramCollection)
    {
      TileQueryHelper.-get2(TileQueryHelper.this).addAll(paramCollection);
      TileQueryHelper.-get1(TileQueryHelper.this).onTilesChanged(TileQueryHelper.-get2(TileQueryHelper.this));
    }
  }
  
  public static class TileInfo
  {
    public CharSequence appLabel;
    public boolean isSystem;
    public String spec;
    public QSTile.State state;
  }
  
  public static abstract interface TileStateListener
  {
    public abstract void onTilesChanged(List<TileQueryHelper.TileInfo> paramList);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\customize\TileQueryHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */