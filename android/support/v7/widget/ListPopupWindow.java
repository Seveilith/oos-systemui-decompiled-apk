package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.view.menu.ShowableListMenu;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import java.lang.reflect.Method;

public class ListPopupWindow
  implements ShowableListMenu
{
  private static Method sClipToWindowEnabledMethod;
  private static Method sGetMaxAvailableHeightMethod;
  private static Method sSetEpicenterBoundsMethod;
  private ListAdapter mAdapter;
  private Context mContext;
  private boolean mDropDownAlwaysVisible = false;
  private View mDropDownAnchorView;
  private int mDropDownGravity = 0;
  private int mDropDownHeight = -2;
  private int mDropDownHorizontalOffset;
  DropDownListView mDropDownList;
  private Drawable mDropDownListHighlight;
  private int mDropDownVerticalOffset;
  private boolean mDropDownVerticalOffsetSet;
  private int mDropDownWidth = -2;
  private int mDropDownWindowLayoutType = 1002;
  private Rect mEpicenterBounds;
  private boolean mForceIgnoreOutsideTouch = false;
  final Handler mHandler;
  private final ListSelectorHider mHideSelector = new ListSelectorHider();
  private boolean mIsAnimatedFromAnchor = true;
  private AdapterView.OnItemClickListener mItemClickListener;
  private AdapterView.OnItemSelectedListener mItemSelectedListener;
  int mListItemExpandMaximum = Integer.MAX_VALUE;
  private boolean mModal;
  private DataSetObserver mObserver;
  PopupWindow mPopup;
  private int mPromptPosition = 0;
  private View mPromptView;
  final ResizePopupRunnable mResizePopupRunnable = new ResizePopupRunnable();
  private final PopupScrollListener mScrollListener = new PopupScrollListener();
  private Runnable mShowDropDownRunnable;
  private final Rect mTempRect = new Rect();
  private final PopupTouchInterceptor mTouchInterceptor = new PopupTouchInterceptor();
  
  static
  {
    try
    {
      sClipToWindowEnabledMethod = PopupWindow.class.getDeclaredMethod("setClipToScreenEnabled", new Class[] { Boolean.TYPE });
    }
    catch (NoSuchMethodException localNoSuchMethodException1)
    {
      try
      {
        for (;;)
        {
          sGetMaxAvailableHeightMethod = PopupWindow.class.getDeclaredMethod("getMaxAvailableHeight", new Class[] { View.class, Integer.TYPE, Boolean.TYPE });
          try
          {
            sSetEpicenterBoundsMethod = PopupWindow.class.getDeclaredMethod("setEpicenterBounds", new Class[] { Rect.class });
            return;
          }
          catch (NoSuchMethodException localNoSuchMethodException3)
          {
            Log.i("ListPopupWindow", "Could not find method setEpicenterBounds(Rect) on PopupWindow. Oh well.");
          }
          localNoSuchMethodException1 = localNoSuchMethodException1;
          Log.i("ListPopupWindow", "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well.");
        }
      }
      catch (NoSuchMethodException localNoSuchMethodException2)
      {
        for (;;)
        {
          Log.i("ListPopupWindow", "Could not find method getMaxAvailableHeight(View, int, boolean) on PopupWindow. Oh well.");
        }
      }
    }
  }
  
  public ListPopupWindow(@NonNull Context paramContext)
  {
    this(paramContext, null, R.attr.listPopupWindowStyle);
  }
  
  public ListPopupWindow(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.listPopupWindowStyle);
  }
  
  public ListPopupWindow(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, @AttrRes int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ListPopupWindow(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, @AttrRes int paramInt1, @StyleRes int paramInt2)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler(paramContext.getMainLooper());
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ListPopupWindow, paramInt1, paramInt2);
    this.mDropDownHorizontalOffset = localTypedArray.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownHorizontalOffset, 0);
    this.mDropDownVerticalOffset = localTypedArray.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownVerticalOffset, 0);
    if (this.mDropDownVerticalOffset != 0) {
      this.mDropDownVerticalOffsetSet = true;
    }
    localTypedArray.recycle();
    if (Build.VERSION.SDK_INT >= 11) {}
    for (this.mPopup = new AppCompatPopupWindow(paramContext, paramAttributeSet, paramInt1, paramInt2);; this.mPopup = new AppCompatPopupWindow(paramContext, paramAttributeSet, paramInt1))
    {
      this.mPopup.setInputMethodMode(1);
      return;
    }
  }
  
  private int buildDropDown()
  {
    int j = 0;
    int i = 0;
    Object localObject3;
    Object localObject2;
    View localView;
    Object localObject1;
    label257:
    label273:
    label321:
    int k;
    if (this.mDropDownList == null)
    {
      localObject3 = this.mContext;
      this.mShowDropDownRunnable = new Runnable()
      {
        public void run()
        {
          View localView = ListPopupWindow.this.getAnchorView();
          if ((localView != null) && (localView.getWindowToken() != null)) {
            ListPopupWindow.this.show();
          }
        }
      };
      if (this.mModal)
      {
        bool = false;
        this.mDropDownList = createDropDownListView((Context)localObject3, bool);
        if (this.mDropDownListHighlight != null) {
          this.mDropDownList.setSelector(this.mDropDownListHighlight);
        }
        this.mDropDownList.setAdapter(this.mAdapter);
        this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
        this.mDropDownList.setFocusable(true);
        this.mDropDownList.setFocusableInTouchMode(true);
        this.mDropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
          public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
          {
            if (paramAnonymousInt != -1)
            {
              paramAnonymousAdapterView = ListPopupWindow.this.mDropDownList;
              if (paramAnonymousAdapterView != null) {
                paramAnonymousAdapterView.setListSelectionHidden(false);
              }
            }
          }
          
          public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
        });
        this.mDropDownList.setOnScrollListener(this.mScrollListener);
        if (this.mItemSelectedListener != null) {
          this.mDropDownList.setOnItemSelectedListener(this.mItemSelectedListener);
        }
        localObject2 = this.mDropDownList;
        localView = this.mPromptView;
        localObject1 = localObject2;
        if (localView != null)
        {
          localObject1 = new LinearLayout((Context)localObject3);
          ((LinearLayout)localObject1).setOrientation(1);
          localObject3 = new LinearLayout.LayoutParams(-1, 0, 1.0F);
        }
        switch (this.mPromptPosition)
        {
        default: 
          Log.e("ListPopupWindow", "Invalid hint position " + this.mPromptPosition);
          if (this.mDropDownWidth >= 0)
          {
            i = Integer.MIN_VALUE;
            j = this.mDropDownWidth;
            localView.measure(View.MeasureSpec.makeMeasureSpec(j, i), 0);
            localObject2 = (LinearLayout.LayoutParams)localView.getLayoutParams();
            i = localView.getMeasuredHeight() + ((LinearLayout.LayoutParams)localObject2).topMargin + ((LinearLayout.LayoutParams)localObject2).bottomMargin;
            this.mPopup.setContentView((View)localObject1);
            localObject1 = this.mPopup.getBackground();
            if (localObject1 == null) {
              break label541;
            }
            ((Drawable)localObject1).getPadding(this.mTempRect);
            j = this.mTempRect.top + this.mTempRect.bottom;
            k = j;
            if (!this.mDropDownVerticalOffsetSet)
            {
              this.mDropDownVerticalOffset = (-this.mTempRect.top);
              k = j;
            }
            label384:
            if (this.mPopup.getInputMethodMode() != 2) {
              break label553;
            }
          }
          break;
        }
      }
    }
    int m;
    label541:
    label553:
    for (boolean bool = true;; bool = false)
    {
      m = getMaxAvailableHeight(getAnchorView(), this.mDropDownVerticalOffset, bool);
      if ((!this.mDropDownAlwaysVisible) && (this.mDropDownHeight != -1)) {
        break label559;
      }
      return m + k;
      bool = true;
      break;
      ((LinearLayout)localObject1).addView((View)localObject2, (ViewGroup.LayoutParams)localObject3);
      ((LinearLayout)localObject1).addView(localView);
      break label257;
      ((LinearLayout)localObject1).addView(localView);
      ((LinearLayout)localObject1).addView((View)localObject2, (ViewGroup.LayoutParams)localObject3);
      break label257;
      i = 0;
      j = 0;
      break label273;
      localObject1 = (ViewGroup)this.mPopup.getContentView();
      localObject1 = this.mPromptView;
      i = j;
      if (localObject1 == null) {
        break label321;
      }
      localObject2 = (LinearLayout.LayoutParams)((View)localObject1).getLayoutParams();
      i = ((View)localObject1).getMeasuredHeight() + ((LinearLayout.LayoutParams)localObject2).topMargin + ((LinearLayout.LayoutParams)localObject2).bottomMargin;
      break label321;
      this.mTempRect.setEmpty();
      k = 0;
      break label384;
    }
    label559:
    switch (this.mDropDownWidth)
    {
    default: 
      j = View.MeasureSpec.makeMeasureSpec(this.mDropDownWidth, 1073741824);
    }
    for (;;)
    {
      m = this.mDropDownList.measureHeightOfChildrenCompat(j, 0, -1, m - i, -1);
      j = i;
      if (m > 0) {
        j = i + (k + (this.mDropDownList.getPaddingTop() + this.mDropDownList.getPaddingBottom()));
      }
      return m + j;
      j = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), Integer.MIN_VALUE);
      continue;
      j = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), 1073741824);
    }
  }
  
  private int getMaxAvailableHeight(View paramView, int paramInt, boolean paramBoolean)
  {
    if (sGetMaxAvailableHeightMethod != null) {
      try
      {
        int i = ((Integer)sGetMaxAvailableHeightMethod.invoke(this.mPopup, new Object[] { paramView, Integer.valueOf(paramInt), Boolean.valueOf(paramBoolean) })).intValue();
        return i;
      }
      catch (Exception localException)
      {
        Log.i("ListPopupWindow", "Could not call getMaxAvailableHeightMethod(View, int, boolean) on PopupWindow. Using the public version.");
      }
    }
    return this.mPopup.getMaxAvailableHeight(paramView, paramInt);
  }
  
  private void removePromptView()
  {
    if (this.mPromptView != null)
    {
      ViewParent localViewParent = this.mPromptView.getParent();
      if ((localViewParent instanceof ViewGroup)) {
        ((ViewGroup)localViewParent).removeView(this.mPromptView);
      }
    }
  }
  
  private void setPopupClipToScreenEnabled(boolean paramBoolean)
  {
    if (sClipToWindowEnabledMethod != null) {}
    try
    {
      sClipToWindowEnabledMethod.invoke(this.mPopup, new Object[] { Boolean.valueOf(paramBoolean) });
      return;
    }
    catch (Exception localException)
    {
      Log.i("ListPopupWindow", "Could not call setClipToScreenEnabled() on PopupWindow. Oh well.");
    }
  }
  
  public void clearListSelection()
  {
    DropDownListView localDropDownListView = this.mDropDownList;
    if (localDropDownListView != null)
    {
      localDropDownListView.setListSelectionHidden(true);
      localDropDownListView.requestLayout();
    }
  }
  
  @NonNull
  DropDownListView createDropDownListView(Context paramContext, boolean paramBoolean)
  {
    return new DropDownListView(paramContext, paramBoolean);
  }
  
  public void dismiss()
  {
    this.mPopup.dismiss();
    removePromptView();
    this.mPopup.setContentView(null);
    this.mDropDownList = null;
    this.mHandler.removeCallbacks(this.mResizePopupRunnable);
  }
  
  @Nullable
  public View getAnchorView()
  {
    return this.mDropDownAnchorView;
  }
  
  @Nullable
  public Drawable getBackground()
  {
    return this.mPopup.getBackground();
  }
  
  public int getHorizontalOffset()
  {
    return this.mDropDownHorizontalOffset;
  }
  
  @Nullable
  public ListView getListView()
  {
    return this.mDropDownList;
  }
  
  public int getVerticalOffset()
  {
    if (!this.mDropDownVerticalOffsetSet) {
      return 0;
    }
    return this.mDropDownVerticalOffset;
  }
  
  public int getWidth()
  {
    return this.mDropDownWidth;
  }
  
  public boolean isInputMethodNotNeeded()
  {
    return this.mPopup.getInputMethodMode() == 2;
  }
  
  public boolean isModal()
  {
    return this.mModal;
  }
  
  public boolean isShowing()
  {
    return this.mPopup.isShowing();
  }
  
  public void setAdapter(@Nullable ListAdapter paramListAdapter)
  {
    if (this.mObserver == null) {
      this.mObserver = new PopupDataSetObserver();
    }
    for (;;)
    {
      this.mAdapter = paramListAdapter;
      if (this.mAdapter != null) {
        paramListAdapter.registerDataSetObserver(this.mObserver);
      }
      if (this.mDropDownList != null) {
        this.mDropDownList.setAdapter(this.mAdapter);
      }
      return;
      if (this.mAdapter != null) {
        this.mAdapter.unregisterDataSetObserver(this.mObserver);
      }
    }
  }
  
  public void setAnchorView(@Nullable View paramView)
  {
    this.mDropDownAnchorView = paramView;
  }
  
  public void setAnimationStyle(@StyleRes int paramInt)
  {
    this.mPopup.setAnimationStyle(paramInt);
  }
  
  public void setBackgroundDrawable(@Nullable Drawable paramDrawable)
  {
    this.mPopup.setBackgroundDrawable(paramDrawable);
  }
  
  public void setContentWidth(int paramInt)
  {
    Drawable localDrawable = this.mPopup.getBackground();
    if (localDrawable != null)
    {
      localDrawable.getPadding(this.mTempRect);
      this.mDropDownWidth = (this.mTempRect.left + this.mTempRect.right + paramInt);
      return;
    }
    setWidth(paramInt);
  }
  
  public void setDropDownGravity(int paramInt)
  {
    this.mDropDownGravity = paramInt;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public void setEpicenterBounds(Rect paramRect)
  {
    this.mEpicenterBounds = paramRect;
  }
  
  public void setHorizontalOffset(int paramInt)
  {
    this.mDropDownHorizontalOffset = paramInt;
  }
  
  public void setInputMethodMode(int paramInt)
  {
    this.mPopup.setInputMethodMode(paramInt);
  }
  
  public void setModal(boolean paramBoolean)
  {
    this.mModal = paramBoolean;
    this.mPopup.setFocusable(paramBoolean);
  }
  
  public void setOnDismissListener(@Nullable PopupWindow.OnDismissListener paramOnDismissListener)
  {
    this.mPopup.setOnDismissListener(paramOnDismissListener);
  }
  
  public void setOnItemClickListener(@Nullable AdapterView.OnItemClickListener paramOnItemClickListener)
  {
    this.mItemClickListener = paramOnItemClickListener;
  }
  
  public void setPromptPosition(int paramInt)
  {
    this.mPromptPosition = paramInt;
  }
  
  public void setSelection(int paramInt)
  {
    DropDownListView localDropDownListView = this.mDropDownList;
    if ((isShowing()) && (localDropDownListView != null))
    {
      localDropDownListView.setListSelectionHidden(false);
      localDropDownListView.setSelection(paramInt);
      if ((Build.VERSION.SDK_INT >= 11) && (localDropDownListView.getChoiceMode() != 0)) {
        localDropDownListView.setItemChecked(paramInt, true);
      }
    }
  }
  
  public void setVerticalOffset(int paramInt)
  {
    this.mDropDownVerticalOffset = paramInt;
    this.mDropDownVerticalOffsetSet = true;
  }
  
  public void setWidth(int paramInt)
  {
    this.mDropDownWidth = paramInt;
  }
  
  public void show()
  {
    boolean bool3 = false;
    boolean bool2 = false;
    int m = -1;
    int j = buildDropDown();
    boolean bool1 = isInputMethodNotNeeded();
    PopupWindowCompat.setWindowLayoutType(this.mPopup, this.mDropDownWindowLayoutType);
    int i;
    label64:
    PopupWindow localPopupWindow;
    int k;
    if (this.mPopup.isShowing()) {
      if (this.mDropDownWidth == -1)
      {
        i = -1;
        if (this.mDropDownHeight != -1) {
          break label259;
        }
        if (!bool1) {
          break label211;
        }
        if (!bool1) {
          break label221;
        }
        localPopupWindow = this.mPopup;
        if (this.mDropDownWidth != -1) {
          break label216;
        }
        k = -1;
        label85:
        localPopupWindow.setWidth(k);
        this.mPopup.setHeight(0);
        label99:
        localPopupWindow = this.mPopup;
        bool1 = bool2;
        if (!this.mForceIgnoreOutsideTouch)
        {
          if (!this.mDropDownAlwaysVisible) {
            break label279;
          }
          bool1 = bool2;
        }
        label127:
        localPopupWindow.setOutsideTouchable(bool1);
        localPopupWindow = this.mPopup;
        View localView = getAnchorView();
        k = this.mDropDownHorizontalOffset;
        int n = this.mDropDownVerticalOffset;
        if (i >= 0) {
          break label285;
        }
        i = -1;
        label163:
        if (j >= 0) {
          break label288;
        }
        j = m;
        label170:
        localPopupWindow.update(localView, k, n, i, j);
      }
    }
    for (;;)
    {
      return;
      if (this.mDropDownWidth == -2)
      {
        i = getAnchorView().getWidth();
        break;
      }
      i = this.mDropDownWidth;
      break;
      label211:
      j = -1;
      break label64;
      label216:
      k = 0;
      break label85;
      label221:
      localPopupWindow = this.mPopup;
      if (this.mDropDownWidth == -1) {}
      for (k = -1;; k = 0)
      {
        localPopupWindow.setWidth(k);
        this.mPopup.setHeight(-1);
        break;
      }
      label259:
      if (this.mDropDownHeight == -2) {
        break label99;
      }
      j = this.mDropDownHeight;
      break label99;
      label279:
      bool1 = true;
      break label127;
      label285:
      break label163;
      label288:
      break label170;
      if (this.mDropDownWidth == -1)
      {
        i = -1;
        label301:
        if (this.mDropDownHeight != -1) {
          break label506;
        }
        j = -1;
        label311:
        this.mPopup.setWidth(i);
        this.mPopup.setHeight(j);
        setPopupClipToScreenEnabled(true);
        localPopupWindow = this.mPopup;
        bool1 = bool3;
        if (!this.mForceIgnoreOutsideTouch)
        {
          if (!this.mDropDownAlwaysVisible) {
            break label526;
          }
          bool1 = bool3;
        }
        localPopupWindow.setOutsideTouchable(bool1);
        this.mPopup.setTouchInterceptor(this.mTouchInterceptor);
        if (sSetEpicenterBoundsMethod == null) {}
      }
      try
      {
        sSetEpicenterBoundsMethod.invoke(this.mPopup, new Object[] { this.mEpicenterBounds });
        PopupWindowCompat.showAsDropDown(this.mPopup, getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, this.mDropDownGravity);
        this.mDropDownList.setSelection(-1);
        if ((!this.mModal) || (this.mDropDownList.isInTouchMode())) {
          clearListSelection();
        }
        if (this.mModal) {
          continue;
        }
        this.mHandler.post(this.mHideSelector);
        return;
        if (this.mDropDownWidth == -2)
        {
          i = getAnchorView().getWidth();
          break label301;
        }
        i = this.mDropDownWidth;
        break label301;
        label506:
        if (this.mDropDownHeight == -2) {
          break label311;
        }
        j = this.mDropDownHeight;
        break label311;
        label526:
        bool1 = true;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.e("ListPopupWindow", "Could not invoke setEpicenterBounds on PopupWindow", localException);
        }
      }
    }
  }
  
  private class ListSelectorHider
    implements Runnable
  {
    ListSelectorHider() {}
    
    public void run()
    {
      ListPopupWindow.this.clearListSelection();
    }
  }
  
  private class PopupDataSetObserver
    extends DataSetObserver
  {
    PopupDataSetObserver() {}
    
    public void onChanged()
    {
      if (ListPopupWindow.this.isShowing()) {
        ListPopupWindow.this.show();
      }
    }
    
    public void onInvalidated()
    {
      ListPopupWindow.this.dismiss();
    }
  }
  
  private class PopupScrollListener
    implements AbsListView.OnScrollListener
  {
    PopupScrollListener() {}
    
    public void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt)
    {
      if ((paramInt != 1) || (ListPopupWindow.this.isInputMethodNotNeeded())) {}
      while (ListPopupWindow.this.mPopup.getContentView() == null) {
        return;
      }
      ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
      ListPopupWindow.this.mResizePopupRunnable.run();
    }
  }
  
  private class PopupTouchInterceptor
    implements View.OnTouchListener
  {
    PopupTouchInterceptor() {}
    
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
      int i = paramMotionEvent.getAction();
      int j = (int)paramMotionEvent.getX();
      int k = (int)paramMotionEvent.getY();
      if ((i == 0) && (ListPopupWindow.this.mPopup != null) && (ListPopupWindow.this.mPopup.isShowing()) && (j >= 0) && (j < ListPopupWindow.this.mPopup.getWidth()) && (k >= 0) && (k < ListPopupWindow.this.mPopup.getHeight())) {
        ListPopupWindow.this.mHandler.postDelayed(ListPopupWindow.this.mResizePopupRunnable, 250L);
      }
      while (i != 1) {
        return false;
      }
      ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
      return false;
    }
  }
  
  private class ResizePopupRunnable
    implements Runnable
  {
    ResizePopupRunnable() {}
    
    public void run()
    {
      if ((ListPopupWindow.this.mDropDownList != null) && (ViewCompat.isAttachedToWindow(ListPopupWindow.this.mDropDownList)) && (ListPopupWindow.this.mDropDownList.getCount() > ListPopupWindow.this.mDropDownList.getChildCount()) && (ListPopupWindow.this.mDropDownList.getChildCount() <= ListPopupWindow.this.mListItemExpandMaximum))
      {
        ListPopupWindow.this.mPopup.setInputMethodMode(2);
        ListPopupWindow.this.show();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\ListPopupWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */