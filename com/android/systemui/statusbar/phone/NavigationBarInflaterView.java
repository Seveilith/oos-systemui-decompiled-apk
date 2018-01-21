package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Space;
import com.android.systemui.statusbar.policy.KeyButtonView;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.android.systemui.util.Utils;
import java.util.Objects;

public class NavigationBarInflaterView
  extends FrameLayout
  implements TunerService.Tunable
{
  private boolean mAlternativeOrder;
  private SparseArray<ButtonDispatcher> mButtonDispatchers;
  private String mCurrentLayout;
  private int mDensity;
  protected LayoutInflater mLandscapeInflater;
  private View mLastRot0;
  private View mLastRot90;
  protected LayoutInflater mLayoutInflater;
  protected FrameLayout mRot0;
  protected FrameLayout mRot90;
  
  public NavigationBarInflaterView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mDensity = paramContext.getResources().getConfiguration().densityDpi;
    createInflaters();
  }
  
  private void addAll(ButtonDispatcher paramButtonDispatcher, ViewGroup paramViewGroup)
  {
    int i = 0;
    if (i < paramViewGroup.getChildCount())
    {
      if (paramViewGroup.getChildAt(i).getId() == paramButtonDispatcher.getId()) {
        paramButtonDispatcher.addView(paramViewGroup.getChildAt(i));
      }
      for (;;)
      {
        i += 1;
        break;
        if ((paramViewGroup.getChildAt(i) instanceof ViewGroup)) {
          addAll(paramButtonDispatcher, (ViewGroup)paramViewGroup.getChildAt(i));
        }
      }
    }
  }
  
  private void addGravitySpacer(LinearLayout paramLinearLayout)
  {
    paramLinearLayout.addView(new Space(this.mContext), new LinearLayout.LayoutParams(0, 0, 1.0F));
  }
  
  private void addToDispatchers(View paramView, boolean paramBoolean)
  {
    int i;
    if (this.mButtonDispatchers != null)
    {
      i = this.mButtonDispatchers.indexOfKey(paramView.getId());
      if (i < 0) {
        break label40;
      }
      ((ButtonDispatcher)this.mButtonDispatchers.valueAt(i)).addView(paramView, paramBoolean);
    }
    for (;;)
    {
      return;
      label40:
      if ((paramView instanceof ViewGroup))
      {
        paramView = (ViewGroup)paramView;
        int j = paramView.getChildCount();
        i = 0;
        while (i < j)
        {
          addToDispatchers(paramView.getChildAt(i), paramBoolean);
          i += 1;
        }
      }
    }
  }
  
  private void clearAllChildren(ViewGroup paramViewGroup)
  {
    int i = 0;
    while (i < paramViewGroup.getChildCount())
    {
      ((ViewGroup)paramViewGroup.getChildAt(i)).removeAllViews();
      i += 1;
    }
  }
  
  private void clearViews()
  {
    if (this.mButtonDispatchers != null)
    {
      int i = 0;
      while (i < this.mButtonDispatchers.size())
      {
        ((ButtonDispatcher)this.mButtonDispatchers.valueAt(i)).clear();
        i += 1;
      }
    }
    clearAllChildren((ViewGroup)this.mRot0.findViewById(2131951802));
    clearAllChildren((ViewGroup)this.mRot90.findViewById(2131951802));
  }
  
  private void createInflaters()
  {
    this.mLayoutInflater = LayoutInflater.from(this.mContext);
    Configuration localConfiguration = new Configuration();
    localConfiguration.setTo(this.mContext.getResources().getConfiguration());
    localConfiguration.orientation = 2;
    this.mLandscapeInflater = LayoutInflater.from(this.mContext.createConfigurationContext(localConfiguration));
  }
  
  public static String extractButton(String paramString)
  {
    if (!paramString.contains("[")) {
      return paramString;
    }
    return paramString.substring(0, paramString.indexOf("["));
  }
  
  public static String extractImage(String paramString)
  {
    if (!paramString.contains(":")) {
      return null;
    }
    return paramString.substring(paramString.indexOf(":") + 1, paramString.indexOf(")"));
  }
  
  public static int extractKeycode(String paramString)
  {
    if (!paramString.contains("(")) {
      return 1;
    }
    return Integer.parseInt(paramString.substring(paramString.indexOf("(") + 1, paramString.indexOf(":")));
  }
  
  public static float extractSize(String paramString)
  {
    if (!paramString.contains("[")) {
      return 1.0F;
    }
    return Float.parseFloat(paramString.substring(paramString.indexOf("[") + 1, paramString.indexOf("]")));
  }
  
  private void inflateButtons(String[] paramArrayOfString, ViewGroup paramViewGroup, boolean paramBoolean)
  {
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      inflateButton(paramArrayOfString[i], paramViewGroup, paramBoolean, i);
      i += 1;
    }
  }
  
  private void inflateChildren()
  {
    removeAllViews();
    this.mRot0 = ((FrameLayout)this.mLayoutInflater.inflate(2130968727, this, false));
    this.mRot0.setId(2131952083);
    addView(this.mRot0);
    this.mRot90 = ((FrameLayout)this.mLayoutInflater.inflate(2130968728, this, false));
    this.mRot90.setId(2131952084);
    addView(this.mRot90);
    updateAlternativeOrder();
    if ((getParent() instanceof NavigationBarView)) {
      ((NavigationBarView)getParent()).updateRotatedViews();
    }
  }
  
  private void initiallyFill(ButtonDispatcher paramButtonDispatcher)
  {
    addAll(paramButtonDispatcher, (ViewGroup)this.mRot0.findViewById(2131952090));
    addAll(paramButtonDispatcher, (ViewGroup)this.mRot0.findViewById(2131952091));
    addAll(paramButtonDispatcher, (ViewGroup)this.mRot90.findViewById(2131952090));
    addAll(paramButtonDispatcher, (ViewGroup)this.mRot90.findViewById(2131952091));
  }
  
  private boolean isSw600Dp()
  {
    return this.mContext.getResources().getConfiguration().smallestScreenWidthDp >= 600;
  }
  
  private void setupLandButton(View paramView)
  {
    Resources localResources = this.mContext.getResources();
    paramView.getLayoutParams().width = localResources.getDimensionPixelOffset(2131755384);
    int i = localResources.getDimensionPixelOffset(2131755385);
    paramView.setPadding(i, paramView.getPaddingTop(), i, paramView.getPaddingBottom());
  }
  
  private void updateAlternativeOrder()
  {
    updateAlternativeOrder(this.mRot0.findViewById(2131952090));
    updateAlternativeOrder(this.mRot0.findViewById(2131952091));
    updateAlternativeOrder(this.mRot90.findViewById(2131952090));
    updateAlternativeOrder(this.mRot90.findViewById(2131952091));
  }
  
  private void updateAlternativeOrder(View paramView)
  {
    if ((paramView instanceof ReverseLinearLayout)) {
      ((ReverseLinearLayout)paramView).setAlternativeOrder(this.mAlternativeOrder);
    }
  }
  
  protected String getDefaultLayout()
  {
    return this.mContext.getString(2131689914);
  }
  
  protected View inflateButton(String paramString, ViewGroup paramViewGroup, boolean paramBoolean, int paramInt)
  {
    Object localObject;
    if (paramBoolean)
    {
      localObject = this.mLandscapeInflater;
      float f = extractSize(paramString);
      paramString = extractButton(paramString);
      if (!"home".equals(paramString)) {
        break label149;
      }
      localObject = ((LayoutInflater)localObject).inflate(2130968629, paramViewGroup, false);
      paramString = (String)localObject;
      if (paramBoolean)
      {
        paramString = (String)localObject;
        if (isSw600Dp())
        {
          setupLandButton((View)localObject);
          paramString = (String)localObject;
        }
      }
      label69:
      if (f != 0.0F)
      {
        localObject = paramString.getLayoutParams();
        ((ViewGroup.LayoutParams)localObject).width = ((int)(((ViewGroup.LayoutParams)localObject).width * f));
      }
      paramViewGroup.addView(paramString);
      addToDispatchers(paramString, paramBoolean);
      if (!paramBoolean) {
        break label417;
      }
    }
    label149:
    label417:
    for (paramViewGroup = this.mLastRot90;; paramViewGroup = this.mLastRot0)
    {
      if (paramViewGroup != null) {
        paramString.setAccessibilityTraversalAfter(paramViewGroup.getId());
      }
      if (!paramBoolean) {
        break label425;
      }
      this.mLastRot90 = paramString;
      return paramString;
      localObject = this.mLayoutInflater;
      break;
      if ("back".equals(paramString))
      {
        localObject = ((LayoutInflater)localObject).inflate(2130968604, paramViewGroup, false);
        paramString = (String)localObject;
        if (!paramBoolean) {
          break label69;
        }
        paramString = (String)localObject;
        if (!isSw600Dp()) {
          break label69;
        }
        setupLandButton((View)localObject);
        paramString = (String)localObject;
        break label69;
      }
      if ("recent".equals(paramString))
      {
        localObject = ((LayoutInflater)localObject).inflate(2130968787, paramViewGroup, false);
        paramString = (String)localObject;
        if (!paramBoolean) {
          break label69;
        }
        paramString = (String)localObject;
        if (!isSw600Dp()) {
          break label69;
        }
        setupLandButton((View)localObject);
        paramString = (String)localObject;
        break label69;
      }
      if ("menu_ime".equals(paramString))
      {
        paramString = ((LayoutInflater)localObject).inflate(2130968715, paramViewGroup, false);
        break label69;
      }
      if ("space".equals(paramString))
      {
        paramString = ((LayoutInflater)localObject).inflate(2130968723, paramViewGroup, false);
        break label69;
      }
      if ("clipboard".equals(paramString))
      {
        paramString = ((LayoutInflater)localObject).inflate(2130968611, paramViewGroup, false);
        break label69;
      }
      if ("nav".equals(paramString))
      {
        paramString = ((LayoutInflater)localObject).inflate(2130968719, paramViewGroup, false);
        break label69;
      }
      if (paramString.startsWith("key"))
      {
        String str = extractImage(paramString);
        paramInt = extractKeycode(paramString);
        localObject = ((LayoutInflater)localObject).inflate(2130968612, paramViewGroup, false);
        ((KeyButtonView)localObject).setCode(paramInt);
        paramString = (String)localObject;
        if (str == null) {
          break label69;
        }
        ((KeyButtonView)localObject).loadAsync(str);
        paramString = (String)localObject;
        break label69;
      }
      return null;
    }
    label425:
    this.mLastRot0 = paramString;
    return paramString;
  }
  
  protected void inflateLayout(String paramString)
  {
    if (Build.DEBUG_ONEPLUS) {
      Log.d("NavigationBar", "newLayout =" + paramString + ", Caller:" + Debug.getCallers(5));
    }
    this.mCurrentLayout = paramString;
    Object localObject = paramString;
    if (paramString == null) {
      localObject = getDefaultLayout();
    }
    String[] arrayOfString = ((String)localObject).split(";", 3);
    paramString = arrayOfString[0].split(",");
    localObject = arrayOfString[1].split(",");
    arrayOfString = arrayOfString[2].split(",");
    inflateButtons(paramString, (ViewGroup)this.mRot0.findViewById(2131952090), false);
    inflateButtons(paramString, (ViewGroup)this.mRot90.findViewById(2131952090), true);
    inflateButtons((String[])localObject, (ViewGroup)this.mRot0.findViewById(2131952091), false);
    inflateButtons((String[])localObject, (ViewGroup)this.mRot90.findViewById(2131952091), true);
    addGravitySpacer((LinearLayout)this.mRot0.findViewById(2131952090));
    addGravitySpacer((LinearLayout)this.mRot90.findViewById(2131952090));
    inflateButtons(arrayOfString, (ViewGroup)this.mRot0.findViewById(2131952090), false);
    inflateButtons(arrayOfString, (ViewGroup)this.mRot90.findViewById(2131952090), true);
    if ((Utils.isSupportHideNavBar()) && ((getParent() instanceof NavigationBarView))) {
      ((NavigationBarView)getParent()).refreshButtonColor();
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (!Utils.isSupportHideNavBar()) {
      TunerService.get(getContext()).addTunable(this, new String[] { "sysui_nav_bar" });
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.mDensity != paramConfiguration.densityDpi)
    {
      this.mDensity = paramConfiguration.densityDpi;
      createInflaters();
      inflateChildren();
      clearViews();
      inflateLayout(this.mCurrentLayout);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    if (!Utils.isSupportHideNavBar()) {
      TunerService.get(getContext()).removeTunable(this);
    }
    super.onDetachedFromWindow();
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    inflateChildren();
    clearViews();
    inflateLayout(getDefaultLayout());
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if ((Utils.isSupportHideNavBar()) && (paramString2 == null)) {
      return;
    }
    if (("sysui_nav_bar".equals(paramString1)) && (!Objects.equals(this.mCurrentLayout, paramString2)))
    {
      clearViews();
      inflateLayout(paramString2);
    }
  }
  
  public void setAlternativeOrder(boolean paramBoolean)
  {
    if (paramBoolean != this.mAlternativeOrder)
    {
      this.mAlternativeOrder = paramBoolean;
      updateAlternativeOrder();
    }
  }
  
  public void setButtonDispatchers(SparseArray<ButtonDispatcher> paramSparseArray)
  {
    this.mButtonDispatchers = paramSparseArray;
    int i = 0;
    while (i < paramSparseArray.size())
    {
      initiallyFill((ButtonDispatcher)paramSparseArray.valueAt(i));
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\NavigationBarInflaterView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */