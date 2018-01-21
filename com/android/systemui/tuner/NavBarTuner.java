package com.android.systemui.tuner;

import android.R.styleable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.util.ArrayList;
import java.util.List;

public class NavBarTuner
  extends Fragment
  implements TunerService.Tunable
{
  private NavBarAdapter mNavBarAdapter;
  private PreviewNavInflater mPreview;
  
  private static CharSequence getLabel(String paramString, Context paramContext)
  {
    if (paramString.startsWith("home")) {
      return paramContext.getString(2131690085);
    }
    if (paramString.startsWith("back")) {
      return paramContext.getString(2131690084);
    }
    if (paramString.startsWith("recent")) {
      return paramContext.getString(2131690087);
    }
    if (paramString.startsWith("space")) {
      return paramContext.getString(2131690588);
    }
    if (paramString.startsWith("menu_ime")) {
      return paramContext.getString(2131690589);
    }
    if (paramString.startsWith("clipboard")) {
      return paramContext.getString(2131690597);
    }
    if (paramString.startsWith("key")) {
      return paramContext.getString(2131690600);
    }
    return paramString;
  }
  
  private void inflatePreview(ViewGroup paramViewGroup)
  {
    Display localDisplay = getActivity().getWindowManager().getDefaultDisplay();
    int i;
    Object localObject;
    int j;
    label71:
    float f;
    if (localDisplay.getRotation() != 1)
    {
      if (localDisplay.getRotation() != 3) {
        break label236;
      }
      i = 1;
      localObject = new Configuration(getContext().getResources().getConfiguration());
      if ((i == 0) || (((Configuration)localObject).smallestScreenWidthDp >= 600)) {
        break label241;
      }
      j = 1;
      if (j == 0) {
        break label247;
      }
      f = 0.75F;
      label79:
      ((Configuration)localObject).densityDpi = ((int)(((Configuration)localObject).densityDpi * f));
      this.mPreview = ((PreviewNavInflater)LayoutInflater.from(getContext().createConfigurationContext((Configuration)localObject)).inflate(2130968721, paramViewGroup, false));
      localObject = this.mPreview.getLayoutParams();
      if (j == 0) {
        break label253;
      }
    }
    label236:
    label241:
    label247:
    label253:
    for (int k = localDisplay.getHeight();; k = localDisplay.getWidth())
    {
      ((ViewGroup.LayoutParams)localObject).width = ((int)(k * f));
      ((ViewGroup.LayoutParams)localObject).height = ((int)(((ViewGroup.LayoutParams)localObject).height * f));
      if (j != 0)
      {
        j = ((ViewGroup.LayoutParams)localObject).width;
        ((ViewGroup.LayoutParams)localObject).width = ((ViewGroup.LayoutParams)localObject).height;
        ((ViewGroup.LayoutParams)localObject).height = j;
      }
      paramViewGroup.addView(this.mPreview);
      if (i == 0) {
        break label263;
      }
      this.mPreview.findViewById(2131952083).setVisibility(8);
      this.mPreview.findViewById(2131952084);
      return;
      i = 1;
      break;
      i = 0;
      break;
      j = 0;
      break label71;
      f = 0.95F;
      break label79;
    }
    label263:
    this.mPreview.findViewById(2131952084).setVisibility(8);
    this.mPreview.findViewById(2131952083);
  }
  
  private void notifyChanged()
  {
    this.mPreview.onTuningChanged("sysui_nav_bar", this.mNavBarAdapter.getNavString());
  }
  
  private void selectImage()
  {
    startActivityForResult(KeycodeSelectionHelper.getSelectImageIntent(), 42);
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt1 == 42) && (paramInt2 == -1) && (paramIntent != null))
    {
      Uri localUri = paramIntent.getData();
      paramInt1 = paramIntent.getFlags();
      getContext().getContentResolver().takePersistableUriPermission(localUri, paramInt1 & 0x1);
      NavBarAdapter.-wrap0(this.mNavBarAdapter, localUri);
      return;
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    paramMenu.add(0, 2, 0, getString(2131690592)).setShowAsAction(1);
    paramMenu.add(0, 3, 0, getString(2131690593));
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(2130968720, paramViewGroup, false);
    inflatePreview((ViewGroup)paramLayoutInflater.findViewById(2131952082));
    return paramLayoutInflater;
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    TunerService.get(getContext()).removeTunable(this);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 2)
    {
      if (!this.mNavBarAdapter.hasHomeButton())
      {
        new AlertDialog.Builder(getContext()).setTitle(2131690594).setMessage(2131690595).setPositiveButton(17039370, null).show();
        return true;
      }
      Settings.Secure.putString(getContext().getContentResolver(), "sysui_nav_bar", this.mNavBarAdapter.getNavString());
      return true;
    }
    if (paramMenuItem.getItemId() == 3)
    {
      Settings.Secure.putString(getContext().getContentResolver(), "sysui_nav_bar", null);
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if (!"sysui_nav_bar".equals(paramString1)) {
      return;
    }
    Context localContext = getContext();
    paramString1 = paramString2;
    if (paramString2 == null) {
      paramString1 = localContext.getString(2131689914);
    }
    paramString1 = paramString1.split(";");
    paramString2 = getString(2131690585);
    String str1 = getString(2131690586);
    String str2 = getString(2131690587);
    this.mNavBarAdapter.clear();
    int i = 0;
    while (i < 3)
    {
      this.mNavBarAdapter.addButton(new String[] { "start", "center", "end" }[i], new String[] { paramString2, str1, str2 }[i]);
      String[] arrayOfString = paramString1[i].split(",");
      int j = 0;
      int k = arrayOfString.length;
      while (j < k)
      {
        String str3 = arrayOfString[j];
        this.mNavBarAdapter.addButton(str3, getLabel(str3, localContext));
        j += 1;
      }
      i += 1;
    }
    this.mNavBarAdapter.addButton("add", getString(2131690591));
    setHasOptionsMenu(true);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramView = (RecyclerView)paramView.findViewById(16908298);
    paramBundle = getContext();
    paramView.setLayoutManager(new LinearLayoutManager(paramBundle));
    this.mNavBarAdapter = new NavBarAdapter(paramBundle);
    paramView.setAdapter(this.mNavBarAdapter);
    paramView.addItemDecoration(new Dividers(paramBundle));
    paramBundle = new ItemTouchHelper(NavBarAdapter.-get1(this.mNavBarAdapter));
    this.mNavBarAdapter.setTouchHelper(paramBundle);
    paramBundle.attachToRecyclerView(paramView);
    TunerService.get(getContext()).addTunable(this, new String[] { "sysui_nav_bar" });
  }
  
  private static class Dividers
    extends RecyclerView.ItemDecoration
  {
    private final Drawable mDivider;
    
    public Dividers(Context paramContext)
    {
      TypedValue localTypedValue = new TypedValue();
      paramContext.getTheme().resolveAttribute(16843284, localTypedValue, true);
      this.mDivider = paramContext.getDrawable(localTypedValue.resourceId);
    }
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      super.onDraw(paramCanvas, paramRecyclerView, paramState);
      int j = paramRecyclerView.getPaddingLeft();
      int k = paramRecyclerView.getWidth();
      int m = paramRecyclerView.getPaddingRight();
      int n = paramRecyclerView.getChildCount();
      int i = 0;
      while (i < n)
      {
        paramState = paramRecyclerView.getChildAt(i);
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramState.getLayoutParams();
        int i1 = paramState.getBottom() + localLayoutParams.bottomMargin;
        int i2 = this.mDivider.getIntrinsicHeight();
        this.mDivider.setBounds(j, i1, k - m, i1 + i2);
        this.mDivider.draw(paramCanvas);
        i += 1;
      }
    }
  }
  
  private static class Holder
    extends RecyclerView.ViewHolder
  {
    private TextView title;
    
    public Holder(View paramView)
    {
      super();
      this.title = ((TextView)paramView.findViewById(16908310));
    }
  }
  
  private class NavBarAdapter
    extends RecyclerView.Adapter<NavBarTuner.Holder>
    implements View.OnClickListener
  {
    private int mButtonLayout;
    private List<String> mButtons = new ArrayList();
    private final ItemTouchHelper.Callback mCallbacks = new ItemTouchHelper.Callback()
    {
      private <T> void move(int paramAnonymousInt1, int paramAnonymousInt2, List<T> paramAnonymousList)
      {
        if (paramAnonymousInt1 > paramAnonymousInt2) {}
        for (int i = paramAnonymousInt2;; i = paramAnonymousInt2 + 1)
        {
          paramAnonymousList.add(i, paramAnonymousList.get(paramAnonymousInt1));
          i = paramAnonymousInt1;
          if (paramAnonymousInt1 > paramAnonymousInt2) {
            i = paramAnonymousInt1 + 1;
          }
          paramAnonymousList.remove(i);
          return;
        }
      }
      
      public int getMovementFlags(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder)
      {
        if (paramAnonymousViewHolder.getItemViewType() != 1) {
          return makeMovementFlags(0, 0);
        }
        return makeMovementFlags(3, 0);
      }
      
      public boolean isItemViewSwipeEnabled()
      {
        return false;
      }
      
      public boolean isLongPressDragEnabled()
      {
        return false;
      }
      
      public boolean onMove(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder1, RecyclerView.ViewHolder paramAnonymousViewHolder2)
      {
        int i = paramAnonymousViewHolder1.getAdapterPosition();
        int j = paramAnonymousViewHolder2.getAdapterPosition();
        if (j == 0) {
          return false;
        }
        move(i, j, NavBarTuner.NavBarAdapter.-get0(NavBarTuner.NavBarAdapter.this));
        move(i, j, NavBarTuner.NavBarAdapter.-get2(NavBarTuner.NavBarAdapter.this));
        NavBarTuner.-wrap1(NavBarTuner.this);
        NavBarTuner.NavBarAdapter.this.notifyItemMoved(i, j);
        return true;
      }
      
      public void onSwiped(RecyclerView.ViewHolder paramAnonymousViewHolder, int paramAnonymousInt) {}
    };
    private int mCategoryLayout;
    private int mKeycode;
    private List<CharSequence> mLabels = new ArrayList();
    private ItemTouchHelper mTouchHelper;
    
    public NavBarAdapter(Context paramContext)
    {
      this.mButtonLayout = paramContext.getTheme().obtainStyledAttributes(null, R.styleable.Preference, 16842894, 0).getResourceId(3, 0);
      this.mCategoryLayout = paramContext.getTheme().obtainStyledAttributes(null, R.styleable.Preference, 16842892, 0).getResourceId(3, 0);
    }
    
    private void bindAdd(NavBarTuner.Holder paramHolder)
    {
      TypedValue localTypedValue = new TypedValue();
      Context localContext = paramHolder.itemView.getContext();
      localContext.getTheme().resolveAttribute(16843829, localTypedValue, true);
      ImageView localImageView = (ImageView)paramHolder.itemView.findViewById(16908294);
      localImageView.setImageResource(2130837682);
      localImageView.setImageTintList(ColorStateList.valueOf(localContext.getColor(localTypedValue.resourceId)));
      paramHolder.itemView.findViewById(16908304).setVisibility(8);
      paramHolder.itemView.setClickable(true);
      paramHolder.itemView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          NavBarTuner.NavBarAdapter.-wrap1(NavBarTuner.NavBarAdapter.this, paramAnonymousView.getContext());
        }
      });
    }
    
    private void bindButton(final NavBarTuner.Holder paramHolder, int paramInt)
    {
      paramHolder.itemView.findViewById(16908350).setVisibility(8);
      paramHolder.itemView.findViewById(16908304).setVisibility(8);
      bindClick(paramHolder.itemView.findViewById(2131952086), paramHolder);
      bindClick(paramHolder.itemView.findViewById(2131952085), paramHolder);
      paramHolder.itemView.findViewById(2131952087).setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          NavBarTuner.NavBarAdapter.-get3(NavBarTuner.NavBarAdapter.this).startDrag(paramHolder);
          return true;
        }
      });
    }
    
    private void bindClick(View paramView, NavBarTuner.Holder paramHolder)
    {
      paramView.setOnClickListener(this);
      paramView.setTag(paramHolder);
    }
    
    private int getLayoutId(int paramInt)
    {
      if (paramInt == 2) {
        return this.mCategoryLayout;
      }
      return this.mButtonLayout;
    }
    
    private void onImageSelected(Uri paramUri)
    {
      int i = this.mButtons.size() - 1;
      this.mButtons.add(i, "key(" + this.mKeycode + ":" + paramUri.toString() + ")");
      this.mLabels.add(i, NavBarTuner.-wrap0("key", NavBarTuner.this.getContext()));
      notifyItemInserted(i);
      NavBarTuner.-wrap1(NavBarTuner.this);
    }
    
    private void showAddDialog(final Context paramContext)
    {
      final String[] arrayOfString = new String[7];
      arrayOfString[0] = "back";
      arrayOfString[1] = "home";
      arrayOfString[2] = "recent";
      arrayOfString[3] = "menu_ime";
      arrayOfString[4] = "space";
      arrayOfString[5] = "clipboard";
      arrayOfString[6] = "key";
      final CharSequence[] arrayOfCharSequence = new CharSequence[arrayOfString.length];
      int i = 0;
      while (i < arrayOfString.length)
      {
        arrayOfCharSequence[i] = NavBarTuner.-wrap0(arrayOfString[i], paramContext);
        i += 1;
      }
      new AlertDialog.Builder(paramContext).setTitle(2131690590).setItems(arrayOfCharSequence, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if ("key".equals(arrayOfString[paramAnonymousInt]))
          {
            NavBarTuner.NavBarAdapter.-wrap3(NavBarTuner.NavBarAdapter.this, paramContext);
            return;
          }
          int i = NavBarTuner.NavBarAdapter.-get0(NavBarTuner.NavBarAdapter.this).size() - 1;
          NavBarTuner.NavBarAdapter.-wrap2(NavBarTuner.NavBarAdapter.this, paramContext, arrayOfString[paramAnonymousInt]);
          NavBarTuner.NavBarAdapter.-get0(NavBarTuner.NavBarAdapter.this).add(i, arrayOfString[paramAnonymousInt]);
          NavBarTuner.NavBarAdapter.-get2(NavBarTuner.NavBarAdapter.this).add(i, arrayOfCharSequence[paramAnonymousInt]);
          NavBarTuner.NavBarAdapter.this.notifyItemInserted(i);
          NavBarTuner.-wrap1(NavBarTuner.this);
        }
      }).setNegativeButton(17039360, null).show();
    }
    
    private void showAddedMessage(Context paramContext, String paramString)
    {
      if ("clipboard".equals(paramString)) {
        new AlertDialog.Builder(paramContext).setTitle(2131690597).setMessage(2131690598).setPositiveButton(17039370, null).show();
      }
    }
    
    private void showKeyDialogs(final Context paramContext)
    {
      final KeycodeSelectionHelper.OnSelectionComplete local5 = new KeycodeSelectionHelper.OnSelectionComplete()
      {
        public void onSelectionComplete(int paramAnonymousInt)
        {
          NavBarTuner.NavBarAdapter.-set0(NavBarTuner.NavBarAdapter.this, paramAnonymousInt);
          NavBarTuner.-wrap2(NavBarTuner.this);
        }
      };
      new AlertDialog.Builder(paramContext).setTitle(2131690600).setMessage(2131690601).setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          KeycodeSelectionHelper.showKeycodeSelect(paramContext, local5);
        }
      }).show();
    }
    
    private void showWidthDialog(final NavBarTuner.Holder paramHolder, Context paramContext)
    {
      final String str = (String)this.mButtons.get(paramHolder.getAdapterPosition());
      float f = NavigationBarInflaterView.extractSize(str);
      final AlertDialog localAlertDialog = new AlertDialog.Builder(paramContext).setTitle(2131690596).setView(2130968724).setNegativeButton(17039360, null).create();
      localAlertDialog.setButton(-1, paramContext.getString(17039370), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = NavigationBarInflaterView.extractButton(str);
          SeekBar localSeekBar = (SeekBar)localAlertDialog.findViewById(2131952088);
          if (localSeekBar.getProgress() == 75) {
            NavBarTuner.NavBarAdapter.-get0(NavBarTuner.NavBarAdapter.this).set(paramHolder.getAdapterPosition(), paramAnonymousDialogInterface);
          }
          for (;;)
          {
            NavBarTuner.-wrap1(NavBarTuner.this);
            return;
            float f = (localSeekBar.getProgress() + 25) / 100.0F;
            NavBarTuner.NavBarAdapter.-get0(NavBarTuner.NavBarAdapter.this).set(paramHolder.getAdapterPosition(), paramAnonymousDialogInterface + "[" + f + "]");
          }
        }
      });
      localAlertDialog.show();
      paramHolder = (SeekBar)localAlertDialog.findViewById(2131952088);
      paramHolder.setMax(150);
      paramHolder.setProgress((int)((f - 0.25F) * 100.0F));
    }
    
    public void addButton(String paramString, CharSequence paramCharSequence)
    {
      this.mButtons.add(paramString);
      this.mLabels.add(paramCharSequence);
      notifyItemInserted(this.mLabels.size() - 1);
      NavBarTuner.-wrap1(NavBarTuner.this);
    }
    
    public void clear()
    {
      this.mButtons.clear();
      this.mLabels.clear();
      notifyDataSetChanged();
    }
    
    public int getItemCount()
    {
      return this.mButtons.size();
    }
    
    public int getItemViewType(int paramInt)
    {
      String str = (String)this.mButtons.get(paramInt);
      if ((str.equals("start")) || (str.equals("center")) || (str.equals("end"))) {
        return 2;
      }
      if (str.equals("add")) {
        return 0;
      }
      return 1;
    }
    
    public String getNavString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      int i = 1;
      while (i < this.mButtons.size() - 1)
      {
        String str = (String)this.mButtons.get(i);
        if ((str.equals("center")) || (str.equals("end")))
        {
          if ((localStringBuilder.length() == 0) || (localStringBuilder.toString().endsWith(";"))) {
            localStringBuilder.append("space");
          }
          localStringBuilder.append(";");
          i += 1;
        }
        else
        {
          if ((localStringBuilder.length() == 0) || (localStringBuilder.toString().endsWith(";"))) {}
          for (;;)
          {
            localStringBuilder.append(str);
            break;
            localStringBuilder.append(",");
          }
        }
      }
      if (localStringBuilder.toString().endsWith(";")) {
        localStringBuilder.append("space");
      }
      return localStringBuilder.toString();
    }
    
    public boolean hasHomeButton()
    {
      int j = this.mButtons.size();
      int i = 0;
      while (i < j)
      {
        if (((String)this.mButtons.get(i)).startsWith("home")) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public void onBindViewHolder(NavBarTuner.Holder paramHolder, int paramInt)
    {
      NavBarTuner.Holder.-get0(paramHolder).setText((CharSequence)this.mLabels.get(paramInt));
      if (paramHolder.getItemViewType() == 1) {
        bindButton(paramHolder, paramInt);
      }
      while (paramHolder.getItemViewType() != 0) {
        return;
      }
      bindAdd(paramHolder);
    }
    
    public void onClick(View paramView)
    {
      NavBarTuner.Holder localHolder = (NavBarTuner.Holder)paramView.getTag();
      if (paramView.getId() == 2131952085) {
        showWidthDialog(localHolder, paramView.getContext());
      }
      while (paramView.getId() != 2131952086) {
        return;
      }
      int i = localHolder.getAdapterPosition();
      this.mButtons.remove(i);
      this.mLabels.remove(i);
      notifyItemRemoved(i);
      NavBarTuner.-wrap1(NavBarTuner.this);
    }
    
    public NavBarTuner.Holder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      LayoutInflater localLayoutInflater = LayoutInflater.from(paramViewGroup.getContext());
      paramViewGroup = localLayoutInflater.inflate(getLayoutId(paramInt), paramViewGroup, false);
      if (paramInt == 1) {
        localLayoutInflater.inflate(2130968722, (ViewGroup)paramViewGroup.findViewById(16908312));
      }
      return new NavBarTuner.Holder(paramViewGroup);
    }
    
    public void setTouchHelper(ItemTouchHelper paramItemTouchHelper)
    {
      this.mTouchHelper = paramItemTouchHelper;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\NavBarTuner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */