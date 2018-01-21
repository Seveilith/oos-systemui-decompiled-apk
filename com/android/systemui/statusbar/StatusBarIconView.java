package com.android.systemui.statusbar;

import android.app.Notification;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewDebug.ExportedProperty;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView.ScaleType;
import com.android.internal.statusbar.StatusBarIcon;
import java.text.NumberFormat;

public class StatusBarIconView
  extends AnimatedImageView
{
  private static final boolean DEBUG = Build.DEBUG_ONEPLUS;
  private boolean mAlwaysScaleIcon;
  private final boolean mBlocked;
  private int mDensity;
  private StatusBarIcon mIcon;
  private Notification mNotification;
  private Drawable mNumberBackground;
  private Paint mNumberPain;
  private String mNumberText;
  private int mNumberX;
  private int mNumberY;
  @ViewDebug.ExportedProperty
  private String mSlot;
  
  public StatusBarIconView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mBlocked = false;
    this.mAlwaysScaleIcon = true;
    updateIconScale();
    this.mDensity = paramContext.getResources().getDisplayMetrics().densityDpi;
  }
  
  public StatusBarIconView(Context paramContext, String paramString, Notification paramNotification)
  {
    this(paramContext, paramString, paramNotification, false);
  }
  
  public StatusBarIconView(Context paramContext, String paramString, Notification paramNotification, boolean paramBoolean)
  {
    super(paramContext);
    this.mBlocked = paramBoolean;
    this.mSlot = paramString;
    this.mNumberPain = new Paint();
    this.mNumberPain.setTextAlign(Paint.Align.CENTER);
    this.mNumberPain.setColor(paramContext.getColor(2130839039));
    this.mNumberPain.setAntiAlias(true);
    setNotification(paramNotification);
    maybeUpdateIconScale();
    setScaleType(ImageView.ScaleType.CENTER);
    this.mDensity = paramContext.getResources().getDisplayMetrics().densityDpi;
  }
  
  public static String contentDescForNotification(Context paramContext, Notification paramNotification)
  {
    localObject1 = "";
    try
    {
      localObject2 = Notification.Builder.recoverBuilder(paramContext, paramNotification).loadHeaderAppName();
      localObject1 = localObject2;
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        Object localObject2;
        Log.e("StatusBarIconView", "Unable to recover builder", localRuntimeException);
        Parcelable localParcelable = paramNotification.extras.getParcelable("android.appInfo");
        if ((localParcelable instanceof ApplicationInfo))
        {
          localObject1 = String.valueOf(((ApplicationInfo)localParcelable).loadLabel(paramContext.getPackageManager()));
          continue;
          if (!TextUtils.isEmpty(localParcelable)) {
            paramNotification = localParcelable;
          } else {
            paramNotification = "";
          }
        }
      }
    }
    localObject2 = paramNotification.extras.getCharSequence("android.title");
    paramNotification = paramNotification.tickerText;
    if (!TextUtils.isEmpty(paramNotification)) {
      return paramContext.getString(2131690627, new Object[] { localObject1, paramNotification });
    }
  }
  
  public static Drawable getIcon(Context paramContext, StatusBarIcon paramStatusBarIcon)
  {
    int j = paramStatusBarIcon.user.getIdentifier();
    int i = j;
    if (j == -1) {
      i = 0;
    }
    paramStatusBarIcon = paramStatusBarIcon.icon.loadDrawableAsUser(paramContext, i);
    TypedValue localTypedValue = new TypedValue();
    paramContext.getResources().getValue(2131755367, localTypedValue, true);
    float f = localTypedValue.getFloat();
    if (f == 1.0F) {
      return paramStatusBarIcon;
    }
    return new ScalingDrawableWrapper(paramStatusBarIcon, f);
  }
  
  private Drawable getIcon(StatusBarIcon paramStatusBarIcon)
  {
    return getIcon(getContext(), paramStatusBarIcon);
  }
  
  private void maybeUpdateIconScale()
  {
    if ((this.mNotification != null) || (this.mAlwaysScaleIcon)) {
      updateIconScale();
    }
  }
  
  private void setContentDescription(Notification paramNotification)
  {
    if (paramNotification != null)
    {
      paramNotification = contentDescForNotification(this.mContext, paramNotification);
      if (!TextUtils.isEmpty(paramNotification)) {
        setContentDescription(paramNotification);
      }
    }
  }
  
  private boolean updateDrawable(boolean paramBoolean)
  {
    if (this.mIcon == null)
    {
      Log.w("StatusBarIconView", "No icon" + this.mSlot);
      return false;
    }
    Drawable localDrawable = getIcon(this.mIcon);
    if (localDrawable == null)
    {
      Log.w("StatusBarIconView", "No icon for slot " + this.mSlot);
      return false;
    }
    if (paramBoolean) {
      setImageDrawable(null);
    }
    setImageDrawable(localDrawable);
    return true;
  }
  
  private void updateIconScale()
  {
    Resources localResources = this.mContext.getResources();
    int i = localResources.getDimensionPixelSize(2131755359);
    float f = localResources.getDimensionPixelSize(2131755378) / i;
    setScaleX(f);
    setScaleY(f);
  }
  
  protected void debug(int paramInt)
  {
    super.debug(paramInt);
    Log.d("View", debugIndent(paramInt) + "slot=" + this.mSlot);
    Log.d("View", debugIndent(paramInt) + "icon=" + this.mIcon);
  }
  
  public boolean equalIcons(Icon paramIcon1, Icon paramIcon2)
  {
    if (paramIcon1 == paramIcon2) {
      return true;
    }
    if (paramIcon1.getType() != paramIcon2.getType()) {
      return false;
    }
    switch (paramIcon1.getType())
    {
    case 3: 
    default: 
      return false;
    case 2: 
      return (paramIcon1.getResPackage().equals(paramIcon2.getResPackage())) && (paramIcon1.getResId() == paramIcon2.getResId());
    }
    return paramIcon1.getUriString().equals(paramIcon2.getUriString());
  }
  
  public String getSlot()
  {
    return this.mSlot;
  }
  
  public StatusBarIcon getStatusBarIcon()
  {
    return this.mIcon;
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    int i = paramConfiguration.densityDpi;
    if (i != this.mDensity)
    {
      this.mDensity = i;
      maybeUpdateIconScale();
      updateDrawable();
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.mNumberBackground != null)
    {
      this.mNumberBackground.draw(paramCanvas);
      paramCanvas.drawText(this.mNumberText, this.mNumberX, this.mNumberY, this.mNumberPain);
    }
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    if (this.mNotification != null) {
      paramAccessibilityEvent.setParcelableData(this.mNotification);
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    updateDrawable();
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mNumberBackground != null) {
      placeNumber();
    }
  }
  
  void placeNumber()
  {
    int i = getContext().getResources().getInteger(17694723);
    if (this.mIcon.number > i) {}
    for (String str = getContext().getResources().getString(17039383);; str = NumberFormat.getIntegerInstance().format(this.mIcon.number))
    {
      this.mNumberText = str;
      int m = getWidth();
      int n = getHeight();
      Rect localRect = new Rect();
      this.mNumberPain.getTextBounds(str, 0, str.length(), localRect);
      i = localRect.right;
      int j = localRect.left;
      int i1 = localRect.bottom - localRect.top;
      this.mNumberBackground.getPadding(localRect);
      j = localRect.left + (i - j) + localRect.right;
      i = j;
      if (j < this.mNumberBackground.getMinimumWidth()) {
        i = this.mNumberBackground.getMinimumWidth();
      }
      this.mNumberX = (m - localRect.right - (i - localRect.right - localRect.left) / 2);
      int k = localRect.top + i1 + localRect.bottom;
      j = k;
      if (k < this.mNumberBackground.getMinimumWidth()) {
        j = this.mNumberBackground.getMinimumWidth();
      }
      this.mNumberY = (n - localRect.bottom - (j - localRect.top - i1 - localRect.bottom) / 2);
      this.mNumberBackground.setBounds(m - i, n - j, m, n);
      return;
    }
  }
  
  public boolean set(StatusBarIcon paramStatusBarIcon)
  {
    int m = 0;
    boolean bool;
    int i;
    label48:
    int j;
    label71:
    int k;
    if (this.mIcon != null)
    {
      bool = equalIcons(this.mIcon.icon, paramStatusBarIcon.icon);
      if (!bool) {
        break label137;
      }
      if (this.mIcon.iconLevel != paramStatusBarIcon.iconLevel) {
        break label132;
      }
      i = 1;
      if (this.mIcon == null) {
        break label147;
      }
      if (this.mIcon.visible != paramStatusBarIcon.visible) {
        break label142;
      }
      j = 1;
      if (this.mIcon == null) {
        break label158;
      }
      if (this.mIcon.number != paramStatusBarIcon.number) {
        break label152;
      }
      k = 1;
    }
    for (;;)
    {
      this.mIcon = paramStatusBarIcon.clone();
      setContentDescription(paramStatusBarIcon.contentDescription);
      if ((bool) || (updateDrawable(false))) {
        break label164;
      }
      return false;
      bool = false;
      break;
      label132:
      i = 0;
      break label48;
      label137:
      i = 0;
      break label48;
      label142:
      j = 0;
      break label71;
      label147:
      j = 0;
      break label71;
      label152:
      k = 0;
      continue;
      label158:
      k = 0;
    }
    label164:
    if (i == 0) {
      setImageLevel(paramStatusBarIcon.iconLevel);
    }
    if (k == 0)
    {
      if ((paramStatusBarIcon.number <= 0) || (!getContext().getResources().getBoolean(2131558411))) {
        break label328;
      }
      if (this.mNumberBackground == null) {
        this.mNumberBackground = getContext().getResources().getDrawable(2130837760);
      }
      placeNumber();
    }
    for (;;)
    {
      invalidate();
      if (j == 0)
      {
        if (DEBUG) {
          Log.i("StatusBarIconView", "set mSlot:" + this.mSlot + " icon.visible:" + paramStatusBarIcon.visible + " mBlocked:" + this.mBlocked);
        }
        if (paramStatusBarIcon.visible)
        {
          i = m;
          if (!this.mBlocked) {}
        }
        else
        {
          i = 8;
        }
        setVisibility(i);
      }
      return true;
      label328:
      this.mNumberBackground = null;
      this.mNumberText = null;
    }
  }
  
  public void setNotification(Notification paramNotification)
  {
    this.mNotification = paramNotification;
    setContentDescription(paramNotification);
  }
  
  public String toString()
  {
    return "StatusBarIconView(slot=" + this.mSlot + " icon=" + this.mIcon + " notification=" + this.mNotification + ")";
  }
  
  public void updateDrawable()
  {
    updateDrawable(true);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\StatusBarIconView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */