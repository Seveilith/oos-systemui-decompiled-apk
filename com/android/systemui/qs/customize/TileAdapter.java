package com.android.systemui.qs.customize;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.qs.QSIconView;
import com.android.systemui.qs.QSTile.State;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.statusbar.phone.QSTileHost;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import java.util.ArrayList;
import java.util.List;

public class TileAdapter
  extends RecyclerView.Adapter<Holder>
  implements TileQueryHelper.TileStateListener
{
  private int mAccessibilityFromIndex;
  private final AccessibilityManager mAccessibilityManager;
  private boolean mAccessibilityMoving;
  private List<TileQueryHelper.TileInfo> mAllTiles;
  private int mBackDragIndex;
  private int mBackEditIndex;
  private int mBackTileDividerIndex;
  private final ItemTouchHelper.Callback mCallbacks = new ItemTouchHelper.Callback()
  {
    private boolean isPositionInView(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      return (paramAnonymousView != null) && (paramAnonymousView.getLeft() < paramAnonymousInt1) && (paramAnonymousView.getRight() > paramAnonymousInt1) && (paramAnonymousView.getTop() < paramAnonymousInt2) && (paramAnonymousView.getBottom() > paramAnonymousInt2);
    }
    
    public boolean canDropOver(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder1, RecyclerView.ViewHolder paramAnonymousViewHolder2)
    {
      return paramAnonymousViewHolder2.getAdapterPosition() <= TileAdapter.-get3(TileAdapter.this) + 1;
    }
    
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder paramAnonymousViewHolder, List<RecyclerView.ViewHolder> paramAnonymousList, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      int j = paramAnonymousList.size();
      int k = paramAnonymousViewHolder.itemView.getWidth() / 2;
      int m = paramAnonymousViewHolder.itemView.getHeight() / 2;
      int i = 0;
      while (i < j)
      {
        paramAnonymousViewHolder = (RecyclerView.ViewHolder)paramAnonymousList.get(i);
        if (isPositionInView(paramAnonymousViewHolder.itemView, paramAnonymousInt1 + k, paramAnonymousInt2 + m)) {
          return paramAnonymousViewHolder;
        }
        i += 1;
      }
      return null;
    }
    
    public float getMoveThreshold(RecyclerView.ViewHolder paramAnonymousViewHolder)
    {
      return 0.0F;
    }
    
    public int getMovementFlags(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder)
    {
      if (paramAnonymousViewHolder.getItemViewType() == 1) {
        return makeMovementFlags(0, 0);
      }
      return makeMovementFlags(15, 0);
    }
    
    public boolean isItemViewSwipeEnabled()
    {
      return false;
    }
    
    public boolean isLongPressDragEnabled()
    {
      return true;
    }
    
    public boolean onMove(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder1, RecyclerView.ViewHolder paramAnonymousViewHolder2)
    {
      int i = paramAnonymousViewHolder1.getAdapterPosition();
      int j = paramAnonymousViewHolder2.getAdapterPosition();
      return TileAdapter.-wrap0(TileAdapter.this, i, j, paramAnonymousViewHolder2.itemView);
    }
    
    public void onSelectedChanged(RecyclerView.ViewHolder paramAnonymousViewHolder, int paramAnonymousInt)
    {
      boolean bool2 = false;
      super.onSelectedChanged(paramAnonymousViewHolder, paramAnonymousInt);
      if (paramAnonymousInt != 2) {
        paramAnonymousViewHolder = null;
      }
      if (paramAnonymousViewHolder == TileAdapter.-get1(TileAdapter.this)) {
        return;
      }
      Object localObject1;
      boolean bool1;
      if (TileAdapter.-get1(TileAdapter.this) != null)
      {
        paramAnonymousInt = TileAdapter.-get1(TileAdapter.this).getAdapterPosition();
        if (paramAnonymousInt < 0)
        {
          Log.d("TileAdapter", "onSelectedChanged:position < 0, mCurrentDrag=" + TileAdapter.-get1(TileAdapter.this));
          TileAdapter.-set3(TileAdapter.this, null);
          return;
        }
        localObject1 = (TileQueryHelper.TileInfo)TileAdapter.-get6(TileAdapter.this).get(paramAnonymousInt);
        Object localObject2 = TileAdapter.Holder.-get0(TileAdapter.-get1(TileAdapter.this));
        bool1 = bool2;
        if (paramAnonymousInt > TileAdapter.-get3(TileAdapter.this))
        {
          if (((TileQueryHelper.TileInfo)localObject1).isSystem) {
            bool1 = bool2;
          }
        }
        else
        {
          ((CustomizeTileView)localObject2).setShowAppLabel(bool1);
          TileAdapter.-get1(TileAdapter.this).stopDrag();
          TileAdapter.-set3(TileAdapter.this, null);
          localObject2 = new StringBuilder().append("TileAdapter:drop tile:").append(((TileQueryHelper.TileInfo)localObject1).spec).append(" to ");
          if (paramAnonymousInt <= TileAdapter.-get3(TileAdapter.this)) {
            break label366;
          }
          localObject1 = "bottom";
          label216:
          Log.d("TileAdapter", (String)localObject1);
        }
      }
      else if (paramAnonymousViewHolder != null)
      {
        TileAdapter.-set3(TileAdapter.this, (TileAdapter.Holder)paramAnonymousViewHolder);
        TileAdapter.-get1(TileAdapter.this).startDrag();
        paramAnonymousInt = TileAdapter.-get1(TileAdapter.this).getAdapterPosition();
        paramAnonymousViewHolder = (TileQueryHelper.TileInfo)TileAdapter.-get6(TileAdapter.this).get(paramAnonymousInt);
        localObject1 = new StringBuilder().append("TileAdapter:drag tile:").append(paramAnonymousViewHolder.spec).append(" from ");
        if (paramAnonymousInt <= TileAdapter.-get3(TileAdapter.this)) {
          break label373;
        }
      }
      label366:
      label373:
      for (paramAnonymousViewHolder = "bottom";; paramAnonymousViewHolder = "top")
      {
        Log.d("TileAdapter", paramAnonymousViewHolder);
        TileAdapter.-get4(TileAdapter.this).post(new Runnable()
        {
          public void run()
          {
            TileAdapter.this.notifyItemChanged(TileAdapter.-get3(TileAdapter.this));
          }
        });
        return;
        bool1 = true;
        break;
        localObject1 = "top";
        break label216;
      }
    }
    
    public void onSwiped(RecyclerView.ViewHolder paramAnonymousViewHolder, int paramAnonymousInt) {}
  };
  private final Context mContext;
  private Holder mCurrentDrag;
  private List<String> mCurrentSpecs;
  private final RecyclerView.ItemDecoration mDecoration = new RecyclerView.ItemDecoration()
  {
    public void onDraw(Canvas paramAnonymousCanvas, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
    {
      super.onDraw(paramAnonymousCanvas, paramAnonymousRecyclerView, paramAnonymousState);
      int m = paramAnonymousRecyclerView.getChildCount();
      int j = paramAnonymousRecyclerView.getWidth();
      int k = paramAnonymousRecyclerView.getBottom();
      int i = 0;
      for (;;)
      {
        if (i < m)
        {
          paramAnonymousState = paramAnonymousRecyclerView.getChildAt(i);
          if ((paramAnonymousRecyclerView.getChildViewHolder(paramAnonymousState).getAdapterPosition() >= TileAdapter.-get3(TileAdapter.this)) || ((paramAnonymousState instanceof TextView)))
          {
            paramAnonymousRecyclerView = (RecyclerView.LayoutParams)paramAnonymousState.getLayoutParams();
            i = paramAnonymousState.getTop();
            m = paramAnonymousRecyclerView.topMargin;
            int n = Math.round(ViewCompat.getTranslationY(paramAnonymousState));
            TileAdapter.-get2(TileAdapter.this).setBounds(0, i + m + n, j, k);
            TileAdapter.-get2(TileAdapter.this).draw(paramAnonymousCanvas);
          }
        }
        else
        {
          return;
        }
        i += 1;
      }
    }
  };
  private Holder mDivider = null;
  private int mDividerColor;
  private ColorDrawable mDrawable;
  private int mEditIndex;
  private final Handler mHandler = new Handler();
  private QSTileHost mHost;
  private final ItemTouchHelper mItemTouchHelper;
  private boolean mNeedsFocus;
  private List<TileQueryHelper.TileInfo> mOtherTiles;
  private final GridLayoutManager.SpanSizeLookup mSizeLookup = new GridLayoutManager.SpanSizeLookup()
  {
    public int getSpanSize(int paramAnonymousInt)
    {
      int i = 1;
      int j = TileAdapter.this.getItemViewType(paramAnonymousInt);
      if (j != 1)
      {
        paramAnonymousInt = i;
        if (j != 4) {}
      }
      else
      {
        paramAnonymousInt = 3;
      }
      return paramAnonymousInt;
    }
  };
  private int mTextColor;
  private int mTileBackgroundColor;
  private int mTileDividerIndex;
  private final List<TileQueryHelper.TileInfo> mTiles = new ArrayList();
  
  public TileAdapter(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAccessibilityManager = ((AccessibilityManager)paramContext.getSystemService(AccessibilityManager.class));
    this.mItemTouchHelper = new ItemTouchHelper(this.mCallbacks);
    this.mDrawable = new ColorDrawable(-13090232);
  }
  
  private TileQueryHelper.TileInfo getAndRemoveOther(String paramString)
  {
    int i = 0;
    while (i < this.mOtherTiles.size())
    {
      if (((TileQueryHelper.TileInfo)this.mOtherTiles.get(i)).spec.equals(paramString)) {
        return (TileQueryHelper.TileInfo)this.mOtherTiles.remove(i);
      }
      i += 1;
    }
    return null;
  }
  
  private <T> void move(int paramInt1, int paramInt2, List<T> paramList)
  {
    paramList.add(paramInt2, paramList.remove(paramInt1));
    notifyItemMoved(paramInt1, paramInt2);
  }
  
  private boolean move(int paramInt1, int paramInt2, View paramView)
  {
    if (paramInt2 == paramInt1) {
      return true;
    }
    Object localObject = ((TileQueryHelper.TileInfo)this.mTiles.get(paramInt1)).state.label;
    boolean bool = ((TileQueryHelper.TileInfo)this.mTiles.get(paramInt1)).isSystem;
    int j;
    int i;
    if (this.mBackDragIndex < this.mBackEditIndex)
    {
      j = 1;
      i = paramInt2;
      if (!bool)
      {
        if ((j == 0) || (paramInt2 != this.mBackEditIndex)) {
          break label220;
        }
        label81:
        if (j == 0) {
          break label234;
        }
        paramInt2 = this.mBackTileDividerIndex;
        label91:
        i = paramInt2;
        if (this.mDivider != null)
        {
          this.mDivider.itemView.setVisibility(0);
          i = paramInt2;
        }
      }
      label115:
      move(paramInt1, i, this.mTiles);
      updateDividerLocations();
      if (i < this.mEditIndex) {
        break label244;
      }
      MetricsLogger.action(this.mContext, 360, strip((TileQueryHelper.TileInfo)this.mTiles.get(i)));
      MetricsLogger.action(this.mContext, 361, paramInt1);
      localObject = this.mContext.getString(2131690624, new Object[] { localObject });
    }
    for (;;)
    {
      paramView.announceForAccessibility((CharSequence)localObject);
      saveSpecs(this.mHost);
      return true;
      j = 0;
      break;
      label220:
      i = paramInt2;
      if (paramInt2 <= this.mBackEditIndex) {
        break label115;
      }
      break label81;
      label234:
      paramInt2 = this.mBackTileDividerIndex + 1;
      break label91;
      label244:
      if (paramInt1 >= this.mEditIndex)
      {
        MetricsLogger.action(this.mContext, 362, strip((TileQueryHelper.TileInfo)this.mTiles.get(i)));
        MetricsLogger.action(this.mContext, 363, i);
        localObject = this.mContext.getString(2131690623, new Object[] { localObject, Integer.valueOf(i + 1) });
      }
      else
      {
        MetricsLogger.action(this.mContext, 364, strip((TileQueryHelper.TileInfo)this.mTiles.get(i)));
        MetricsLogger.action(this.mContext, 365, i);
        localObject = this.mContext.getString(2131690625, new Object[] { localObject, Integer.valueOf(i + 1) });
      }
    }
  }
  
  private void recalcSpecs()
  {
    if ((this.mCurrentSpecs == null) || (this.mAllTiles == null)) {
      return;
    }
    this.mOtherTiles = new ArrayList(this.mAllTiles);
    this.mTiles.clear();
    int i = 0;
    TileQueryHelper.TileInfo localTileInfo;
    while (i < this.mCurrentSpecs.size())
    {
      localTileInfo = getAndRemoveOther((String)this.mCurrentSpecs.get(i));
      if (localTileInfo != null) {
        this.mTiles.add(localTileInfo);
      }
      i += 1;
    }
    this.mTiles.add(null);
    int j;
    for (i = 0; i < this.mOtherTiles.size(); i = j + 1)
    {
      localTileInfo = (TileQueryHelper.TileInfo)this.mOtherTiles.get(i);
      j = i;
      if (localTileInfo.isSystem)
      {
        this.mOtherTiles.remove(i);
        this.mTiles.add(localTileInfo);
        j = i - 1;
      }
    }
    this.mTileDividerIndex = this.mTiles.size();
    this.mTiles.add(null);
    this.mTiles.addAll(this.mOtherTiles);
    updateDividerLocations();
    notifyDataSetChanged();
  }
  
  private void selectPosition(int paramInt, View paramView)
  {
    this.mAccessibilityMoving = false;
    List localList = this.mTiles;
    int i = this.mEditIndex;
    this.mEditIndex = (i - 1);
    localList.remove(i);
    notifyItemRemoved(this.mEditIndex - 1);
    i = paramInt;
    if (paramInt == this.mEditIndex) {
      i = paramInt - 1;
    }
    move(this.mAccessibilityFromIndex, i, paramView);
    notifyDataSetChanged();
  }
  
  private void showAccessibilityDialog(final int paramInt, final View paramView)
  {
    final TileQueryHelper.TileInfo localTileInfo = (TileQueryHelper.TileInfo)this.mTiles.get(paramInt);
    String str1 = this.mContext.getString(2131690621, new Object[] { localTileInfo.state.label });
    String str2 = this.mContext.getString(2131690622, new Object[] { localTileInfo.state.label });
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.mContext);
    paramView = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (paramAnonymousInt == 0)
        {
          TileAdapter.-wrap3(TileAdapter.this, paramInt);
          return;
        }
        paramAnonymousDialogInterface = TileAdapter.this;
        int i = paramInt;
        if (localTileInfo.isSystem) {}
        for (paramAnonymousInt = TileAdapter.-get3(TileAdapter.this);; paramAnonymousInt = TileAdapter.-get5(TileAdapter.this))
        {
          TileAdapter.-wrap0(paramAnonymousDialogInterface, i, paramAnonymousInt, paramView);
          TileAdapter.this.notifyItemChanged(TileAdapter.-get5(TileAdapter.this));
          TileAdapter.this.notifyDataSetChanged();
          return;
        }
      }
    };
    paramView = localBuilder.setItems(new CharSequence[] { str1, str2 }, paramView).setNegativeButton(17039360, null).create();
    SystemUIDialog.setShowForAllUsers(paramView, true);
    SystemUIDialog.applyFlags(paramView);
    paramView.show();
  }
  
  private void startAccessibleDrag(int paramInt)
  {
    this.mAccessibilityMoving = true;
    this.mNeedsFocus = true;
    this.mAccessibilityFromIndex = paramInt;
    List localList = this.mTiles;
    paramInt = this.mEditIndex;
    this.mEditIndex = (paramInt + 1);
    localList.add(paramInt, null);
    notifyDataSetChanged();
  }
  
  private static String strip(TileQueryHelper.TileInfo paramTileInfo)
  {
    paramTileInfo = paramTileInfo.spec;
    if (paramTileInfo.startsWith("custom(")) {
      return CustomTile.getComponentFromSpec(paramTileInfo).getPackageName();
    }
    return paramTileInfo;
  }
  
  private void updateDividerLocations()
  {
    this.mEditIndex = -1;
    this.mTileDividerIndex = this.mTiles.size();
    int i = 0;
    if (i < this.mTiles.size())
    {
      if (this.mTiles.get(i) == null)
      {
        if (this.mEditIndex != -1) {
          break label66;
        }
        this.mEditIndex = i;
      }
      for (;;)
      {
        i += 1;
        break;
        label66:
        this.mTileDividerIndex = i;
      }
    }
    if (this.mTiles.size() - 1 == this.mTileDividerIndex) {
      notifyItemChanged(this.mTileDividerIndex);
    }
  }
  
  public int getItemCount()
  {
    return this.mTiles.size();
  }
  
  public RecyclerView.ItemDecoration getItemDecoration()
  {
    return this.mDecoration;
  }
  
  public ItemTouchHelper getItemTouchHelper()
  {
    return this.mItemTouchHelper;
  }
  
  public int getItemViewType(int paramInt)
  {
    if ((this.mAccessibilityMoving) && (paramInt == this.mEditIndex - 1)) {
      return 2;
    }
    if (paramInt == this.mTileDividerIndex) {
      return 4;
    }
    if (this.mTiles.get(paramInt) == null) {
      return 1;
    }
    return 0;
  }
  
  public GridLayoutManager.SpanSizeLookup getSizeLookup()
  {
    return this.mSizeLookup;
  }
  
  public void onBindViewHolder(final Holder paramHolder, int paramInt)
  {
    boolean bool2 = false;
    int i = 0;
    int j = 1;
    if (paramHolder.getItemViewType() == 4)
    {
      this.mDivider = paramHolder;
      localObject = paramHolder.itemView;
      if (this.mTileDividerIndex < this.mTiles.size() - 1) {}
      for (paramInt = i;; paramInt = 4)
      {
        ((View)localObject).setVisibility(paramInt);
        paramHolder.itemView.setBackgroundColor(this.mTextColor);
        return;
      }
    }
    if (paramHolder.getItemViewType() == 1)
    {
      paramHolder = (TextView)paramHolder.itemView.findViewById(16908310);
      if (this.mCurrentDrag != null) {}
      for (paramInt = 2131690604;; paramInt = 2131689985)
      {
        paramHolder.setText(paramInt);
        paramHolder.setTextColor(this.mDividerColor);
        FontSizeUtils.updateFontSize(paramHolder, 2131755716);
        return;
      }
    }
    if (paramHolder.getItemViewType() == 2)
    {
      Holder.-get0(paramHolder).setClickable(true);
      Holder.-get0(paramHolder).setFocusable(true);
      Holder.-get0(paramHolder).setFocusableInTouchMode(true);
      Holder.-get0(paramHolder).setVisibility(0);
      Holder.-get0(paramHolder).setImportantForAccessibility(1);
      Holder.-get0(paramHolder).setContentDescription(this.mContext.getString(2131690620, new Object[] { Integer.valueOf(paramInt + 1) }));
      Holder.-get0(paramHolder).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          TileAdapter.-wrap1(TileAdapter.this, paramHolder.getAdapterPosition(), paramAnonymousView);
        }
      });
      if (this.mNeedsFocus)
      {
        Holder.-get0(paramHolder).requestLayout();
        Holder.-get0(paramHolder).addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
          public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
          {
            TileAdapter.Holder.-get0(paramHolder).removeOnLayoutChangeListener(this);
            TileAdapter.Holder.-get0(paramHolder).requestFocus();
          }
        });
        this.mNeedsFocus = false;
      }
      return;
    }
    Object localObject = (TileQueryHelper.TileInfo)this.mTiles.get(paramInt);
    boolean bool1;
    if (paramInt > this.mEditIndex)
    {
      ((TileQueryHelper.TileInfo)localObject).state.contentDescription = this.mContext.getString(2131690619, new Object[] { ((TileQueryHelper.TileInfo)localObject).state.label });
      Holder.-get0(paramHolder).onStateChanged(((TileQueryHelper.TileInfo)localObject).state);
      Holder.-get0(paramHolder).setAppLabel(((TileQueryHelper.TileInfo)localObject).appLabel);
      CustomizeTileView localCustomizeTileView = Holder.-get0(paramHolder);
      bool1 = bool2;
      if (paramInt > this.mEditIndex)
      {
        if (!((TileQueryHelper.TileInfo)localObject).isSystem) {
          break label552;
        }
        bool1 = bool2;
      }
      label371:
      localCustomizeTileView.setShowAppLabel(bool1);
      if (this.mAccessibilityManager.isTouchExplorationEnabled())
      {
        if ((this.mAccessibilityMoving) && (paramInt >= this.mEditIndex)) {
          break label558;
        }
        bool1 = true;
        label406:
        Holder.-get0(paramHolder).setClickable(bool1);
        Holder.-get0(paramHolder).setFocusable(bool1);
        localObject = Holder.-get0(paramHolder);
        if (!bool1) {
          break label564;
        }
      }
    }
    label552:
    label558:
    label564:
    for (paramInt = j;; paramInt = 4)
    {
      ((CustomizeTileView)localObject).setImportantForAccessibility(paramInt);
      if (bool1) {
        Holder.-get0(paramHolder).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            int i = paramHolder.getAdapterPosition();
            if (TileAdapter.-get0(TileAdapter.this))
            {
              TileAdapter.-wrap1(TileAdapter.this, i, paramAnonymousView);
              return;
            }
            if (i < TileAdapter.-get3(TileAdapter.this))
            {
              TileAdapter.-wrap2(TileAdapter.this, i, paramAnonymousView);
              return;
            }
            TileAdapter.-wrap3(TileAdapter.this, i);
          }
        });
      }
      return;
      if (this.mAccessibilityMoving)
      {
        ((TileQueryHelper.TileInfo)localObject).state.contentDescription = this.mContext.getString(2131690620, new Object[] { Integer.valueOf(paramInt + 1) });
        break;
      }
      ((TileQueryHelper.TileInfo)localObject).state.contentDescription = this.mContext.getString(2131690618, new Object[] { Integer.valueOf(paramInt + 1), ((TileQueryHelper.TileInfo)localObject).state.label });
      break;
      bool1 = true;
      break label371;
      bool1 = false;
      break label406;
    }
  }
  
  public Holder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    Context localContext = paramViewGroup.getContext();
    LayoutInflater localLayoutInflater = LayoutInflater.from(localContext);
    if (paramInt == 4) {
      return new Holder(localLayoutInflater.inflate(2130968770, paramViewGroup, false));
    }
    if (paramInt == 1) {
      return new Holder(localLayoutInflater.inflate(2130968767, paramViewGroup, false));
    }
    paramViewGroup = (FrameLayout)localLayoutInflater.inflate(2130968771, paramViewGroup, false);
    paramViewGroup.addView(new CustomizeTileView(localContext, new QSIconView(localContext)));
    return new Holder(paramViewGroup);
  }
  
  public boolean onFailedToRecycleView(Holder paramHolder)
  {
    paramHolder.clearDrag();
    return true;
  }
  
  public void onTilesChanged(List<TileQueryHelper.TileInfo> paramList)
  {
    this.mAllTiles = paramList;
    recalcSpecs();
  }
  
  public void saveSpecs(QSTileHost paramQSTileHost)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while ((i < this.mTiles.size()) && (this.mTiles.get(i) != null))
    {
      localArrayList.add(((TileQueryHelper.TileInfo)this.mTiles.get(i)).spec);
      i += 1;
    }
    paramQSTileHost.changeTiles(this.mCurrentSpecs, localArrayList);
    this.mCurrentSpecs = localArrayList;
  }
  
  public void setHost(QSTileHost paramQSTileHost)
  {
    this.mHost = paramQSTileHost;
  }
  
  public void setThemeColor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mDrawable = new ColorDrawable(paramInt1);
    this.mTextColor = paramInt2;
    this.mDividerColor = paramInt4;
    this.mTileBackgroundColor = paramInt3;
  }
  
  public void setTileSpecs(List<String> paramList)
  {
    if (paramList.equals(this.mCurrentSpecs)) {
      return;
    }
    this.mCurrentSpecs = paramList;
    recalcSpecs();
  }
  
  public class Holder
    extends RecyclerView.ViewHolder
  {
    private CustomizeTileView mTileView;
    
    public Holder(View paramView)
    {
      super();
      if ((paramView instanceof FrameLayout))
      {
        this.mTileView = ((CustomizeTileView)((FrameLayout)paramView).getChildAt(0));
        this.mTileView.setBackground(null);
        this.mTileView.getIcon().disableAnimation();
      }
    }
    
    public void clearDrag()
    {
      this.itemView.clearAnimation();
      this.mTileView.findViewById(2131952160).clearAnimation();
      this.mTileView.findViewById(2131952160).setAlpha(1.0F);
      this.mTileView.getAppLabel().clearAnimation();
      this.mTileView.getAppLabel().setAlpha(0.6F);
    }
    
    public void startDrag()
    {
      TileAdapter.-set1(TileAdapter.this, TileAdapter.-get3(TileAdapter.this));
      TileAdapter.-set0(TileAdapter.this, TileAdapter.-get1(TileAdapter.this).getAdapterPosition());
      TileAdapter.-set2(TileAdapter.this, TileAdapter.-get5(TileAdapter.this));
      this.itemView.animate().setDuration(100L).scaleX(1.2F).scaleY(1.2F);
      this.mTileView.findViewById(2131952160).animate().setDuration(100L).alpha(0.0F);
      this.mTileView.getAppLabel().animate().setDuration(100L).alpha(0.0F);
    }
    
    public void stopDrag()
    {
      this.itemView.animate().setDuration(100L).scaleX(1.0F).scaleY(1.0F);
      this.mTileView.findViewById(2131952160).animate().setDuration(100L).alpha(1.0F);
      this.mTileView.getAppLabel().animate().setDuration(100L).alpha(0.6F);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\customize\TileAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */