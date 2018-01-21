package com.android.systemui.qs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.FontSizeUtils;

public class QSDetailItems
  extends FrameLayout
{
  private static final boolean DEBUG = Log.isLoggable("QSDetailItems", 3);
  public static int sIconColor;
  public static int sLightColor;
  public static int sNormalColor;
  private final Adapter mAdapter = new Adapter(null);
  private Callback mCallback;
  private final Context mContext;
  private View mEmpty;
  private ImageView mEmptyIcon;
  private TextView mEmptyText;
  private final H mHandler = new H();
  private AutoSizingList mItemList;
  private Item[] mItems;
  private boolean mItemsVisible = true;
  private String mTag;
  
  public QSDetailItems(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.mTag = "QSDetailItems";
  }
  
  public static QSDetailItems convertOrInflate(Context paramContext, View paramView, ViewGroup paramViewGroup)
  {
    if ((paramView instanceof QSDetailItems)) {
      return (QSDetailItems)paramView;
    }
    return (QSDetailItems)LayoutInflater.from(paramContext).inflate(2130968776, paramViewGroup, false);
  }
  
  private void handleSetCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  private void handleSetItems(Item[] paramArrayOfItem)
  {
    int k = 8;
    Object localObject;
    int j;
    if (paramArrayOfItem != null)
    {
      i = paramArrayOfItem.length;
      localObject = this.mEmpty;
      if (i != 0) {
        break label66;
      }
      j = 0;
      label23:
      ((View)localObject).setVisibility(j);
      localObject = this.mItemList;
      if (i != 0) {
        break label72;
      }
    }
    label66:
    label72:
    for (int i = k;; i = 0)
    {
      ((AutoSizingList)localObject).setVisibility(i);
      this.mItems = paramArrayOfItem;
      this.mAdapter.notifyDataSetChanged();
      return;
      i = 0;
      break;
      j = 8;
      break label23;
    }
  }
  
  private void handleSetItemsVisible(boolean paramBoolean)
  {
    if (this.mItemsVisible == paramBoolean) {
      return;
    }
    this.mItemsVisible = paramBoolean;
    int i = 0;
    if (i < this.mItemList.getChildCount())
    {
      View localView = this.mItemList.getChildAt(i);
      if (this.mItemsVisible) {}
      for (int j = 0;; j = 4)
      {
        localView.setVisibility(j);
        i += 1;
        break;
      }
    }
  }
  
  public static void updateThemeColor(int paramInt1, int paramInt2, int paramInt3)
  {
    sIconColor = paramInt3;
    sNormalColor = paramInt1;
    sLightColor = paramInt2;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (DEBUG) {
      Log.d(this.mTag, "onAttachedToWindow");
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    FontSizeUtils.updateFontSize(this.mEmptyText, 2131755439);
    int j = this.mItemList.getChildCount();
    int i = 0;
    while (i < j)
    {
      paramConfiguration = this.mItemList.getChildAt(i);
      FontSizeUtils.updateFontSize(paramConfiguration, 16908310, 2131755437);
      FontSizeUtils.updateFontSize(paramConfiguration, 16908304, 2131755438);
      i += 1;
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (DEBUG) {
      Log.d(this.mTag, "onDetachedFromWindow");
    }
    this.mCallback = null;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mItemList = ((AutoSizingList)findViewById(16908298));
    this.mItemList.setVisibility(8);
    this.mItemList.setAdapter(this.mAdapter);
    this.mEmpty = findViewById(16908292);
    this.mEmpty.setVisibility(8);
    this.mEmptyText = ((TextView)this.mEmpty.findViewById(16908310));
    this.mEmptyIcon = ((ImageView)this.mEmpty.findViewById(16908294));
    this.mEmptyText.setTextColor(sNormalColor);
    this.mEmptyIcon.setImageTintList(ColorStateList.valueOf(sIconColor));
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mHandler.removeMessages(2);
    this.mHandler.obtainMessage(2, paramCallback).sendToTarget();
  }
  
  public void setEmptyState(int paramInt1, int paramInt2)
  {
    this.mEmptyIcon.setImageResource(paramInt1);
    this.mEmptyText.setText(paramInt2);
  }
  
  public void setItems(Item[] paramArrayOfItem)
  {
    this.mHandler.removeMessages(1);
    this.mHandler.obtainMessage(1, paramArrayOfItem).sendToTarget();
  }
  
  public void setItemsVisible(boolean paramBoolean)
  {
    this.mHandler.removeMessages(3);
    H localH = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localH.obtainMessage(3, i, 0).sendToTarget();
      return;
    }
  }
  
  public void setTagSuffix(String paramString)
  {
    this.mTag = ("QSDetailItems." + paramString);
  }
  
  private class Adapter
    extends BaseAdapter
  {
    private Adapter() {}
    
    public int getCount()
    {
      if (QSDetailItems.-get2(QSDetailItems.this) != null) {
        return QSDetailItems.-get2(QSDetailItems.this).length;
      }
      return 0;
    }
    
    public Object getItem(int paramInt)
    {
      return QSDetailItems.-get2(QSDetailItems.this)[paramInt];
    }
    
    public long getItemId(int paramInt)
    {
      return 0L;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      final QSDetailItems.Item localItem = QSDetailItems.-get2(QSDetailItems.this)[paramInt];
      View localView = paramView;
      if (paramView == null) {
        localView = LayoutInflater.from(QSDetailItems.-get1(QSDetailItems.this)).inflate(2130968775, paramViewGroup, false);
      }
      if (QSDetailItems.-get3(QSDetailItems.this))
      {
        paramInt = 0;
        localView.setVisibility(paramInt);
        paramView = (ImageView)localView.findViewById(16908294);
        paramView.setImageResource(localItem.icon);
        paramView.getOverlay().clear();
        if (localItem.overlay != null)
        {
          localItem.overlay.setBounds(0, 0, localItem.overlay.getIntrinsicWidth(), localItem.overlay.getIntrinsicHeight());
          localItem.overlay.setTint(QSDetailItems.sIconColor);
          paramView.getOverlay().add(localItem.overlay);
        }
        paramView.setImageTintList(ColorStateList.valueOf(QSDetailItems.sIconColor));
        paramViewGroup = (TextView)localView.findViewById(16908310);
        paramViewGroup.setText(localItem.line1);
        TextView localTextView = (TextView)localView.findViewById(16908304);
        if (!TextUtils.isEmpty(localItem.line2)) {
          break label353;
        }
        paramInt = 0;
        label194:
        if (paramInt == 0) {
          break label358;
        }
        i = 1;
        label201:
        paramViewGroup.setMaxLines(i);
        if (paramInt == 0) {
          break label364;
        }
        i = 0;
        label214:
        localTextView.setVisibility(i);
        if (paramInt == 0) {
          break label371;
        }
        paramView = localItem.line2;
        label231:
        localTextView.setText(paramView);
        paramViewGroup.setTextColor(QSDetailItems.sNormalColor);
        localTextView.setTextColor(QSDetailItems.sLightColor);
        localView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (QSDetailItems.-get0(QSDetailItems.this) != null) {
              QSDetailItems.-get0(QSDetailItems.this).onDetailItemClick(localItem);
            }
          }
        });
        paramView = (ImageView)localView.findViewById(16908296);
        paramView.setImageTintList(ColorStateList.valueOf(QSDetailItems.sLightColor));
        if (!localItem.canDisconnect) {
          break label376;
        }
      }
      label353:
      label358:
      label364:
      label371:
      label376:
      for (int i = 0;; i = 8)
      {
        paramView.setVisibility(i);
        paramView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (QSDetailItems.-get0(QSDetailItems.this) != null) {
              QSDetailItems.-get0(QSDetailItems.this).onDetailItemDisconnect(localItem);
            }
          }
        });
        paramView = QSDetailItems.-get1(QSDetailItems.this).getResources();
        if (paramInt == 0) {
          break label383;
        }
        localView.setMinimumHeight(paramView.getDimensionPixelSize(2131755715));
        return localView;
        paramInt = 4;
        break;
        paramInt = 1;
        break label194;
        i = 2;
        break label201;
        i = 8;
        break label214;
        paramView = null;
        break label231;
      }
      label383:
      localView.setMinimumHeight(paramView.getDimensionPixelSize(2131755433));
      return localView;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onDetailItemClick(QSDetailItems.Item paramItem);
    
    public abstract void onDetailItemDisconnect(QSDetailItems.Item paramItem);
  }
  
  private class H
    extends Handler
  {
    public H()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = true;
      if (paramMessage.what == 1) {
        QSDetailItems.-wrap2(QSDetailItems.this, (QSDetailItems.Item[])paramMessage.obj);
      }
      do
      {
        return;
        if (paramMessage.what == 2)
        {
          QSDetailItems.-wrap0(QSDetailItems.this, (QSDetailItems.Callback)paramMessage.obj);
          return;
        }
      } while (paramMessage.what != 3);
      QSDetailItems localQSDetailItems = QSDetailItems.this;
      if (paramMessage.arg1 != 0) {}
      for (;;)
      {
        QSDetailItems.-wrap1(localQSDetailItems, bool);
        return;
        bool = false;
      }
    }
  }
  
  public static class Item
  {
    public boolean canDisconnect;
    public int icon;
    public CharSequence line1;
    public CharSequence line2;
    public Drawable overlay;
    public Object tag;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QSDetailItems.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */