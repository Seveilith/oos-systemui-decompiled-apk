package android.support.v4.view.accessibility;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.View;

public class AccessibilityNodeInfoCompat
{
  static final AccessibilityNodeInfoImpl IMPL = new AccessibilityNodeInfoStubImpl();
  private final Object mInfo;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      IMPL = new AccessibilityNodeInfoApi24Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 23)
    {
      IMPL = new AccessibilityNodeInfoApi23Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 22)
    {
      IMPL = new AccessibilityNodeInfoApi22Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new AccessibilityNodeInfoApi21Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new AccessibilityNodeInfoKitKatImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 18)
    {
      IMPL = new AccessibilityNodeInfoJellybeanMr2Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 17)
    {
      IMPL = new AccessibilityNodeInfoJellybeanMr1Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new AccessibilityNodeInfoJellybeanImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new AccessibilityNodeInfoIcsImpl();
      return;
    }
  }
  
  public AccessibilityNodeInfoCompat(Object paramObject)
  {
    this.mInfo = paramObject;
  }
  
  private static String getActionSymbolicName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "ACTION_UNKNOWN";
    case 1: 
      return "ACTION_FOCUS";
    case 2: 
      return "ACTION_CLEAR_FOCUS";
    case 4: 
      return "ACTION_SELECT";
    case 8: 
      return "ACTION_CLEAR_SELECTION";
    case 16: 
      return "ACTION_CLICK";
    case 32: 
      return "ACTION_LONG_CLICK";
    case 64: 
      return "ACTION_ACCESSIBILITY_FOCUS";
    case 128: 
      return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
    case 256: 
      return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
    case 512: 
      return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
    case 1024: 
      return "ACTION_NEXT_HTML_ELEMENT";
    case 2048: 
      return "ACTION_PREVIOUS_HTML_ELEMENT";
    case 4096: 
      return "ACTION_SCROLL_FORWARD";
    case 8192: 
      return "ACTION_SCROLL_BACKWARD";
    case 65536: 
      return "ACTION_CUT";
    case 16384: 
      return "ACTION_COPY";
    case 32768: 
      return "ACTION_PASTE";
    }
    return "ACTION_SET_SELECTION";
  }
  
  public static AccessibilityNodeInfoCompat obtain(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    return wrapNonNullInstance(IMPL.obtain(paramAccessibilityNodeInfoCompat.mInfo));
  }
  
  static AccessibilityNodeInfoCompat wrapNonNullInstance(Object paramObject)
  {
    if (paramObject != null) {
      return new AccessibilityNodeInfoCompat(paramObject);
    }
    return null;
  }
  
  public void addAction(int paramInt)
  {
    IMPL.addAction(this.mInfo, paramInt);
  }
  
  public void addChild(View paramView)
  {
    IMPL.addChild(this.mInfo, paramView);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (AccessibilityNodeInfoCompat)paramObject;
    if (this.mInfo == null)
    {
      if (((AccessibilityNodeInfoCompat)paramObject).mInfo != null) {
        return false;
      }
    }
    else if (!this.mInfo.equals(((AccessibilityNodeInfoCompat)paramObject).mInfo)) {
      return false;
    }
    return true;
  }
  
  public int getActions()
  {
    return IMPL.getActions(this.mInfo);
  }
  
  public void getBoundsInParent(Rect paramRect)
  {
    IMPL.getBoundsInParent(this.mInfo, paramRect);
  }
  
  public void getBoundsInScreen(Rect paramRect)
  {
    IMPL.getBoundsInScreen(this.mInfo, paramRect);
  }
  
  public CharSequence getClassName()
  {
    return IMPL.getClassName(this.mInfo);
  }
  
  public CollectionItemInfoCompat getCollectionItemInfo()
  {
    Object localObject = IMPL.getCollectionItemInfo(this.mInfo);
    if (localObject == null) {
      return null;
    }
    return new CollectionItemInfoCompat(localObject);
  }
  
  public CharSequence getContentDescription()
  {
    return IMPL.getContentDescription(this.mInfo);
  }
  
  public Object getInfo()
  {
    return this.mInfo;
  }
  
  public int getMovementGranularities()
  {
    return IMPL.getMovementGranularities(this.mInfo);
  }
  
  public CharSequence getPackageName()
  {
    return IMPL.getPackageName(this.mInfo);
  }
  
  public CharSequence getText()
  {
    return IMPL.getText(this.mInfo);
  }
  
  public String getViewIdResourceName()
  {
    return IMPL.getViewIdResourceName(this.mInfo);
  }
  
  public int hashCode()
  {
    if (this.mInfo == null) {
      return 0;
    }
    return this.mInfo.hashCode();
  }
  
  public boolean isAccessibilityFocused()
  {
    return IMPL.isAccessibilityFocused(this.mInfo);
  }
  
  public boolean isCheckable()
  {
    return IMPL.isCheckable(this.mInfo);
  }
  
  public boolean isChecked()
  {
    return IMPL.isChecked(this.mInfo);
  }
  
  public boolean isClickable()
  {
    return IMPL.isClickable(this.mInfo);
  }
  
  public boolean isEnabled()
  {
    return IMPL.isEnabled(this.mInfo);
  }
  
  public boolean isFocusable()
  {
    return IMPL.isFocusable(this.mInfo);
  }
  
  public boolean isFocused()
  {
    return IMPL.isFocused(this.mInfo);
  }
  
  public boolean isLongClickable()
  {
    return IMPL.isLongClickable(this.mInfo);
  }
  
  public boolean isPassword()
  {
    return IMPL.isPassword(this.mInfo);
  }
  
  public boolean isScrollable()
  {
    return IMPL.isScrollable(this.mInfo);
  }
  
  public boolean isSelected()
  {
    return IMPL.isSelected(this.mInfo);
  }
  
  public boolean isVisibleToUser()
  {
    return IMPL.isVisibleToUser(this.mInfo);
  }
  
  public void recycle()
  {
    IMPL.recycle(this.mInfo);
  }
  
  public boolean removeAction(AccessibilityActionCompat paramAccessibilityActionCompat)
  {
    return IMPL.removeAction(this.mInfo, paramAccessibilityActionCompat.mAction);
  }
  
  public void setAccessibilityFocused(boolean paramBoolean)
  {
    IMPL.setAccessibilityFocused(this.mInfo, paramBoolean);
  }
  
  public void setBoundsInParent(Rect paramRect)
  {
    IMPL.setBoundsInParent(this.mInfo, paramRect);
  }
  
  public void setBoundsInScreen(Rect paramRect)
  {
    IMPL.setBoundsInScreen(this.mInfo, paramRect);
  }
  
  public void setClassName(CharSequence paramCharSequence)
  {
    IMPL.setClassName(this.mInfo, paramCharSequence);
  }
  
  public void setClickable(boolean paramBoolean)
  {
    IMPL.setClickable(this.mInfo, paramBoolean);
  }
  
  public void setCollectionInfo(Object paramObject)
  {
    IMPL.setCollectionInfo(this.mInfo, ((CollectionInfoCompat)paramObject).mInfo);
  }
  
  public void setCollectionItemInfo(Object paramObject)
  {
    IMPL.setCollectionItemInfo(this.mInfo, ((CollectionItemInfoCompat)paramObject).mInfo);
  }
  
  public void setContentDescription(CharSequence paramCharSequence)
  {
    IMPL.setContentDescription(this.mInfo, paramCharSequence);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    IMPL.setEnabled(this.mInfo, paramBoolean);
  }
  
  public void setFocusable(boolean paramBoolean)
  {
    IMPL.setFocusable(this.mInfo, paramBoolean);
  }
  
  public void setFocused(boolean paramBoolean)
  {
    IMPL.setFocused(this.mInfo, paramBoolean);
  }
  
  public void setLongClickable(boolean paramBoolean)
  {
    IMPL.setLongClickable(this.mInfo, paramBoolean);
  }
  
  public void setMovementGranularities(int paramInt)
  {
    IMPL.setMovementGranularities(this.mInfo, paramInt);
  }
  
  public void setPackageName(CharSequence paramCharSequence)
  {
    IMPL.setPackageName(this.mInfo, paramCharSequence);
  }
  
  public void setParent(View paramView)
  {
    IMPL.setParent(this.mInfo, paramView);
  }
  
  public void setScrollable(boolean paramBoolean)
  {
    IMPL.setScrollable(this.mInfo, paramBoolean);
  }
  
  public void setSelected(boolean paramBoolean)
  {
    IMPL.setSelected(this.mInfo, paramBoolean);
  }
  
  public void setSource(View paramView)
  {
    IMPL.setSource(this.mInfo, paramView);
  }
  
  public void setVisibleToUser(boolean paramBoolean)
  {
    IMPL.setVisibleToUser(this.mInfo, paramBoolean);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString());
    Rect localRect = new Rect();
    getBoundsInParent(localRect);
    localStringBuilder.append("; boundsInParent: ").append(localRect);
    getBoundsInScreen(localRect);
    localStringBuilder.append("; boundsInScreen: ").append(localRect);
    localStringBuilder.append("; packageName: ").append(getPackageName());
    localStringBuilder.append("; className: ").append(getClassName());
    localStringBuilder.append("; text: ").append(getText());
    localStringBuilder.append("; contentDescription: ").append(getContentDescription());
    localStringBuilder.append("; viewId: ").append(getViewIdResourceName());
    localStringBuilder.append("; checkable: ").append(isCheckable());
    localStringBuilder.append("; checked: ").append(isChecked());
    localStringBuilder.append("; focusable: ").append(isFocusable());
    localStringBuilder.append("; focused: ").append(isFocused());
    localStringBuilder.append("; selected: ").append(isSelected());
    localStringBuilder.append("; clickable: ").append(isClickable());
    localStringBuilder.append("; longClickable: ").append(isLongClickable());
    localStringBuilder.append("; enabled: ").append(isEnabled());
    localStringBuilder.append("; password: ").append(isPassword());
    localStringBuilder.append("; scrollable: ").append(isScrollable());
    localStringBuilder.append("; [");
    int i = getActions();
    while (i != 0)
    {
      int k = 1 << Integer.numberOfTrailingZeros(i);
      int j = i & k;
      localStringBuilder.append(getActionSymbolicName(k));
      i = j;
      if (j != 0)
      {
        localStringBuilder.append(", ");
        i = j;
      }
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public static class AccessibilityActionCompat
  {
    public static final AccessibilityActionCompat ACTION_ACCESSIBILITY_FOCUS;
    public static final AccessibilityActionCompat ACTION_CLEAR_ACCESSIBILITY_FOCUS;
    public static final AccessibilityActionCompat ACTION_CLEAR_FOCUS;
    public static final AccessibilityActionCompat ACTION_CLEAR_SELECTION;
    public static final AccessibilityActionCompat ACTION_CLICK;
    public static final AccessibilityActionCompat ACTION_COLLAPSE;
    public static final AccessibilityActionCompat ACTION_CONTEXT_CLICK = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionContextClick());
    public static final AccessibilityActionCompat ACTION_COPY;
    public static final AccessibilityActionCompat ACTION_CUT;
    public static final AccessibilityActionCompat ACTION_DISMISS;
    public static final AccessibilityActionCompat ACTION_EXPAND;
    public static final AccessibilityActionCompat ACTION_FOCUS = new AccessibilityActionCompat(1, null);
    public static final AccessibilityActionCompat ACTION_LONG_CLICK;
    public static final AccessibilityActionCompat ACTION_NEXT_AT_MOVEMENT_GRANULARITY;
    public static final AccessibilityActionCompat ACTION_NEXT_HTML_ELEMENT;
    public static final AccessibilityActionCompat ACTION_PASTE;
    public static final AccessibilityActionCompat ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY;
    public static final AccessibilityActionCompat ACTION_PREVIOUS_HTML_ELEMENT;
    public static final AccessibilityActionCompat ACTION_SCROLL_BACKWARD;
    public static final AccessibilityActionCompat ACTION_SCROLL_DOWN;
    public static final AccessibilityActionCompat ACTION_SCROLL_FORWARD;
    public static final AccessibilityActionCompat ACTION_SCROLL_LEFT;
    public static final AccessibilityActionCompat ACTION_SCROLL_RIGHT;
    public static final AccessibilityActionCompat ACTION_SCROLL_TO_POSITION;
    public static final AccessibilityActionCompat ACTION_SCROLL_UP;
    public static final AccessibilityActionCompat ACTION_SELECT;
    public static final AccessibilityActionCompat ACTION_SET_PROGRESS = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionSetProgress());
    public static final AccessibilityActionCompat ACTION_SET_SELECTION;
    public static final AccessibilityActionCompat ACTION_SET_TEXT;
    public static final AccessibilityActionCompat ACTION_SHOW_ON_SCREEN;
    final Object mAction;
    
    static
    {
      ACTION_CLEAR_FOCUS = new AccessibilityActionCompat(2, null);
      ACTION_SELECT = new AccessibilityActionCompat(4, null);
      ACTION_CLEAR_SELECTION = new AccessibilityActionCompat(8, null);
      ACTION_CLICK = new AccessibilityActionCompat(16, null);
      ACTION_LONG_CLICK = new AccessibilityActionCompat(32, null);
      ACTION_ACCESSIBILITY_FOCUS = new AccessibilityActionCompat(64, null);
      ACTION_CLEAR_ACCESSIBILITY_FOCUS = new AccessibilityActionCompat(128, null);
      ACTION_NEXT_AT_MOVEMENT_GRANULARITY = new AccessibilityActionCompat(256, null);
      ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = new AccessibilityActionCompat(512, null);
      ACTION_NEXT_HTML_ELEMENT = new AccessibilityActionCompat(1024, null);
      ACTION_PREVIOUS_HTML_ELEMENT = new AccessibilityActionCompat(2048, null);
      ACTION_SCROLL_FORWARD = new AccessibilityActionCompat(4096, null);
      ACTION_SCROLL_BACKWARD = new AccessibilityActionCompat(8192, null);
      ACTION_COPY = new AccessibilityActionCompat(16384, null);
      ACTION_PASTE = new AccessibilityActionCompat(32768, null);
      ACTION_CUT = new AccessibilityActionCompat(65536, null);
      ACTION_SET_SELECTION = new AccessibilityActionCompat(131072, null);
      ACTION_EXPAND = new AccessibilityActionCompat(262144, null);
      ACTION_COLLAPSE = new AccessibilityActionCompat(524288, null);
      ACTION_DISMISS = new AccessibilityActionCompat(1048576, null);
      ACTION_SET_TEXT = new AccessibilityActionCompat(2097152, null);
      ACTION_SHOW_ON_SCREEN = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionShowOnScreen());
      ACTION_SCROLL_TO_POSITION = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollToPosition());
      ACTION_SCROLL_UP = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollUp());
      ACTION_SCROLL_LEFT = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollLeft());
      ACTION_SCROLL_DOWN = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollDown());
      ACTION_SCROLL_RIGHT = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollRight());
    }
    
    public AccessibilityActionCompat(int paramInt, CharSequence paramCharSequence)
    {
      this(AccessibilityNodeInfoCompat.IMPL.newAccessibilityAction(paramInt, paramCharSequence));
    }
    
    AccessibilityActionCompat(Object paramObject)
    {
      this.mAction = paramObject;
    }
  }
  
  static class AccessibilityNodeInfoApi21Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoKitKatImpl
  {
    public boolean isCollectionItemSelected(Object paramObject)
    {
      return AccessibilityNodeInfoCompatApi21.CollectionItemInfo.isSelected(paramObject);
    }
    
    public Object newAccessibilityAction(int paramInt, CharSequence paramCharSequence)
    {
      return AccessibilityNodeInfoCompatApi21.newAccessibilityAction(paramInt, paramCharSequence);
    }
    
    public Object obtainCollectionInfo(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      return AccessibilityNodeInfoCompatApi21.obtainCollectionInfo(paramInt1, paramInt2, paramBoolean, paramInt3);
    }
    
    public Object obtainCollectionItemInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      return AccessibilityNodeInfoCompatApi21.obtainCollectionItemInfo(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean1, paramBoolean2);
    }
    
    public boolean removeAction(Object paramObject1, Object paramObject2)
    {
      return AccessibilityNodeInfoCompatApi21.removeAction(paramObject1, paramObject2);
    }
  }
  
  static class AccessibilityNodeInfoApi22Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoApi21Impl
  {}
  
  static class AccessibilityNodeInfoApi23Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoApi22Impl
  {
    public Object getActionContextClick()
    {
      return AccessibilityNodeInfoCompatApi23.getActionContextClick();
    }
    
    public Object getActionScrollDown()
    {
      return AccessibilityNodeInfoCompatApi23.getActionScrollDown();
    }
    
    public Object getActionScrollLeft()
    {
      return AccessibilityNodeInfoCompatApi23.getActionScrollLeft();
    }
    
    public Object getActionScrollRight()
    {
      return AccessibilityNodeInfoCompatApi23.getActionScrollRight();
    }
    
    public Object getActionScrollToPosition()
    {
      return AccessibilityNodeInfoCompatApi23.getActionScrollToPosition();
    }
    
    public Object getActionScrollUp()
    {
      return AccessibilityNodeInfoCompatApi23.getActionScrollUp();
    }
    
    public Object getActionShowOnScreen()
    {
      return AccessibilityNodeInfoCompatApi23.getActionShowOnScreen();
    }
  }
  
  static class AccessibilityNodeInfoApi24Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoApi23Impl
  {
    public Object getActionSetProgress()
    {
      return AccessibilityNodeInfoCompatApi24.getActionSetProgress();
    }
  }
  
  static class AccessibilityNodeInfoIcsImpl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoStubImpl
  {
    public void addAction(Object paramObject, int paramInt)
    {
      AccessibilityNodeInfoCompatIcs.addAction(paramObject, paramInt);
    }
    
    public void addChild(Object paramObject, View paramView)
    {
      AccessibilityNodeInfoCompatIcs.addChild(paramObject, paramView);
    }
    
    public int getActions(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.getActions(paramObject);
    }
    
    public void getBoundsInParent(Object paramObject, Rect paramRect)
    {
      AccessibilityNodeInfoCompatIcs.getBoundsInParent(paramObject, paramRect);
    }
    
    public void getBoundsInScreen(Object paramObject, Rect paramRect)
    {
      AccessibilityNodeInfoCompatIcs.getBoundsInScreen(paramObject, paramRect);
    }
    
    public CharSequence getClassName(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.getClassName(paramObject);
    }
    
    public CharSequence getContentDescription(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.getContentDescription(paramObject);
    }
    
    public CharSequence getPackageName(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.getPackageName(paramObject);
    }
    
    public CharSequence getText(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.getText(paramObject);
    }
    
    public boolean isCheckable(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isCheckable(paramObject);
    }
    
    public boolean isChecked(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isChecked(paramObject);
    }
    
    public boolean isClickable(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isClickable(paramObject);
    }
    
    public boolean isEnabled(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isEnabled(paramObject);
    }
    
    public boolean isFocusable(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isFocusable(paramObject);
    }
    
    public boolean isFocused(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isFocused(paramObject);
    }
    
    public boolean isLongClickable(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isLongClickable(paramObject);
    }
    
    public boolean isPassword(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isPassword(paramObject);
    }
    
    public boolean isScrollable(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isScrollable(paramObject);
    }
    
    public boolean isSelected(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isSelected(paramObject);
    }
    
    public Object obtain(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.obtain(paramObject);
    }
    
    public void recycle(Object paramObject)
    {
      AccessibilityNodeInfoCompatIcs.recycle(paramObject);
    }
    
    public void setBoundsInParent(Object paramObject, Rect paramRect)
    {
      AccessibilityNodeInfoCompatIcs.setBoundsInParent(paramObject, paramRect);
    }
    
    public void setBoundsInScreen(Object paramObject, Rect paramRect)
    {
      AccessibilityNodeInfoCompatIcs.setBoundsInScreen(paramObject, paramRect);
    }
    
    public void setClassName(Object paramObject, CharSequence paramCharSequence)
    {
      AccessibilityNodeInfoCompatIcs.setClassName(paramObject, paramCharSequence);
    }
    
    public void setClickable(Object paramObject, boolean paramBoolean)
    {
      AccessibilityNodeInfoCompatIcs.setClickable(paramObject, paramBoolean);
    }
    
    public void setContentDescription(Object paramObject, CharSequence paramCharSequence)
    {
      AccessibilityNodeInfoCompatIcs.setContentDescription(paramObject, paramCharSequence);
    }
    
    public void setEnabled(Object paramObject, boolean paramBoolean)
    {
      AccessibilityNodeInfoCompatIcs.setEnabled(paramObject, paramBoolean);
    }
    
    public void setFocusable(Object paramObject, boolean paramBoolean)
    {
      AccessibilityNodeInfoCompatIcs.setFocusable(paramObject, paramBoolean);
    }
    
    public void setFocused(Object paramObject, boolean paramBoolean)
    {
      AccessibilityNodeInfoCompatIcs.setFocused(paramObject, paramBoolean);
    }
    
    public void setLongClickable(Object paramObject, boolean paramBoolean)
    {
      AccessibilityNodeInfoCompatIcs.setLongClickable(paramObject, paramBoolean);
    }
    
    public void setPackageName(Object paramObject, CharSequence paramCharSequence)
    {
      AccessibilityNodeInfoCompatIcs.setPackageName(paramObject, paramCharSequence);
    }
    
    public void setParent(Object paramObject, View paramView)
    {
      AccessibilityNodeInfoCompatIcs.setParent(paramObject, paramView);
    }
    
    public void setScrollable(Object paramObject, boolean paramBoolean)
    {
      AccessibilityNodeInfoCompatIcs.setScrollable(paramObject, paramBoolean);
    }
    
    public void setSelected(Object paramObject, boolean paramBoolean)
    {
      AccessibilityNodeInfoCompatIcs.setSelected(paramObject, paramBoolean);
    }
    
    public void setSource(Object paramObject, View paramView)
    {
      AccessibilityNodeInfoCompatIcs.setSource(paramObject, paramView);
    }
  }
  
  static abstract interface AccessibilityNodeInfoImpl
  {
    public abstract void addAction(Object paramObject, int paramInt);
    
    public abstract void addChild(Object paramObject, View paramView);
    
    public abstract Object getActionContextClick();
    
    public abstract Object getActionScrollDown();
    
    public abstract Object getActionScrollLeft();
    
    public abstract Object getActionScrollRight();
    
    public abstract Object getActionScrollToPosition();
    
    public abstract Object getActionScrollUp();
    
    public abstract Object getActionSetProgress();
    
    public abstract Object getActionShowOnScreen();
    
    public abstract int getActions(Object paramObject);
    
    public abstract void getBoundsInParent(Object paramObject, Rect paramRect);
    
    public abstract void getBoundsInScreen(Object paramObject, Rect paramRect);
    
    public abstract CharSequence getClassName(Object paramObject);
    
    public abstract int getCollectionItemColumnIndex(Object paramObject);
    
    public abstract int getCollectionItemColumnSpan(Object paramObject);
    
    public abstract Object getCollectionItemInfo(Object paramObject);
    
    public abstract int getCollectionItemRowIndex(Object paramObject);
    
    public abstract int getCollectionItemRowSpan(Object paramObject);
    
    public abstract CharSequence getContentDescription(Object paramObject);
    
    public abstract int getMovementGranularities(Object paramObject);
    
    public abstract CharSequence getPackageName(Object paramObject);
    
    public abstract CharSequence getText(Object paramObject);
    
    public abstract String getViewIdResourceName(Object paramObject);
    
    public abstract boolean isAccessibilityFocused(Object paramObject);
    
    public abstract boolean isCheckable(Object paramObject);
    
    public abstract boolean isChecked(Object paramObject);
    
    public abstract boolean isClickable(Object paramObject);
    
    public abstract boolean isCollectionItemSelected(Object paramObject);
    
    public abstract boolean isEnabled(Object paramObject);
    
    public abstract boolean isFocusable(Object paramObject);
    
    public abstract boolean isFocused(Object paramObject);
    
    public abstract boolean isLongClickable(Object paramObject);
    
    public abstract boolean isPassword(Object paramObject);
    
    public abstract boolean isScrollable(Object paramObject);
    
    public abstract boolean isSelected(Object paramObject);
    
    public abstract boolean isVisibleToUser(Object paramObject);
    
    public abstract Object newAccessibilityAction(int paramInt, CharSequence paramCharSequence);
    
    public abstract Object obtain(Object paramObject);
    
    public abstract Object obtainCollectionInfo(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3);
    
    public abstract Object obtainCollectionItemInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2);
    
    public abstract void recycle(Object paramObject);
    
    public abstract boolean removeAction(Object paramObject1, Object paramObject2);
    
    public abstract void setAccessibilityFocused(Object paramObject, boolean paramBoolean);
    
    public abstract void setBoundsInParent(Object paramObject, Rect paramRect);
    
    public abstract void setBoundsInScreen(Object paramObject, Rect paramRect);
    
    public abstract void setClassName(Object paramObject, CharSequence paramCharSequence);
    
    public abstract void setClickable(Object paramObject, boolean paramBoolean);
    
    public abstract void setCollectionInfo(Object paramObject1, Object paramObject2);
    
    public abstract void setCollectionItemInfo(Object paramObject1, Object paramObject2);
    
    public abstract void setContentDescription(Object paramObject, CharSequence paramCharSequence);
    
    public abstract void setEnabled(Object paramObject, boolean paramBoolean);
    
    public abstract void setFocusable(Object paramObject, boolean paramBoolean);
    
    public abstract void setFocused(Object paramObject, boolean paramBoolean);
    
    public abstract void setLongClickable(Object paramObject, boolean paramBoolean);
    
    public abstract void setMovementGranularities(Object paramObject, int paramInt);
    
    public abstract void setPackageName(Object paramObject, CharSequence paramCharSequence);
    
    public abstract void setParent(Object paramObject, View paramView);
    
    public abstract void setScrollable(Object paramObject, boolean paramBoolean);
    
    public abstract void setSelected(Object paramObject, boolean paramBoolean);
    
    public abstract void setSource(Object paramObject, View paramView);
    
    public abstract void setVisibleToUser(Object paramObject, boolean paramBoolean);
  }
  
  static class AccessibilityNodeInfoJellybeanImpl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoIcsImpl
  {
    public int getMovementGranularities(Object paramObject)
    {
      return AccessibilityNodeInfoCompatJellyBean.getMovementGranularities(paramObject);
    }
    
    public boolean isAccessibilityFocused(Object paramObject)
    {
      return AccessibilityNodeInfoCompatJellyBean.isAccessibilityFocused(paramObject);
    }
    
    public boolean isVisibleToUser(Object paramObject)
    {
      return AccessibilityNodeInfoCompatJellyBean.isVisibleToUser(paramObject);
    }
    
    public void setAccessibilityFocused(Object paramObject, boolean paramBoolean)
    {
      AccessibilityNodeInfoCompatJellyBean.setAccesibilityFocused(paramObject, paramBoolean);
    }
    
    public void setMovementGranularities(Object paramObject, int paramInt)
    {
      AccessibilityNodeInfoCompatJellyBean.setMovementGranularities(paramObject, paramInt);
    }
    
    public void setVisibleToUser(Object paramObject, boolean paramBoolean)
    {
      AccessibilityNodeInfoCompatJellyBean.setVisibleToUser(paramObject, paramBoolean);
    }
  }
  
  static class AccessibilityNodeInfoJellybeanMr1Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoJellybeanImpl
  {}
  
  static class AccessibilityNodeInfoJellybeanMr2Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoJellybeanMr1Impl
  {
    public String getViewIdResourceName(Object paramObject)
    {
      return AccessibilityNodeInfoCompatJellybeanMr2.getViewIdResourceName(paramObject);
    }
  }
  
  static class AccessibilityNodeInfoKitKatImpl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoJellybeanMr2Impl
  {
    public int getCollectionItemColumnIndex(Object paramObject)
    {
      return AccessibilityNodeInfoCompatKitKat.CollectionItemInfo.getColumnIndex(paramObject);
    }
    
    public int getCollectionItemColumnSpan(Object paramObject)
    {
      return AccessibilityNodeInfoCompatKitKat.CollectionItemInfo.getColumnSpan(paramObject);
    }
    
    public Object getCollectionItemInfo(Object paramObject)
    {
      return AccessibilityNodeInfoCompatKitKat.getCollectionItemInfo(paramObject);
    }
    
    public int getCollectionItemRowIndex(Object paramObject)
    {
      return AccessibilityNodeInfoCompatKitKat.CollectionItemInfo.getRowIndex(paramObject);
    }
    
    public int getCollectionItemRowSpan(Object paramObject)
    {
      return AccessibilityNodeInfoCompatKitKat.CollectionItemInfo.getRowSpan(paramObject);
    }
    
    public Object obtainCollectionInfo(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      return AccessibilityNodeInfoCompatKitKat.obtainCollectionInfo(paramInt1, paramInt2, paramBoolean, paramInt3);
    }
    
    public Object obtainCollectionItemInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      return AccessibilityNodeInfoCompatKitKat.obtainCollectionItemInfo(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean1);
    }
    
    public void setCollectionInfo(Object paramObject1, Object paramObject2)
    {
      AccessibilityNodeInfoCompatKitKat.setCollectionInfo(paramObject1, paramObject2);
    }
    
    public void setCollectionItemInfo(Object paramObject1, Object paramObject2)
    {
      AccessibilityNodeInfoCompatKitKat.setCollectionItemInfo(paramObject1, paramObject2);
    }
  }
  
  static class AccessibilityNodeInfoStubImpl
    implements AccessibilityNodeInfoCompat.AccessibilityNodeInfoImpl
  {
    public void addAction(Object paramObject, int paramInt) {}
    
    public void addChild(Object paramObject, View paramView) {}
    
    public Object getActionContextClick()
    {
      return null;
    }
    
    public Object getActionScrollDown()
    {
      return null;
    }
    
    public Object getActionScrollLeft()
    {
      return null;
    }
    
    public Object getActionScrollRight()
    {
      return null;
    }
    
    public Object getActionScrollToPosition()
    {
      return null;
    }
    
    public Object getActionScrollUp()
    {
      return null;
    }
    
    public Object getActionSetProgress()
    {
      return null;
    }
    
    public Object getActionShowOnScreen()
    {
      return null;
    }
    
    public int getActions(Object paramObject)
    {
      return 0;
    }
    
    public void getBoundsInParent(Object paramObject, Rect paramRect) {}
    
    public void getBoundsInScreen(Object paramObject, Rect paramRect) {}
    
    public CharSequence getClassName(Object paramObject)
    {
      return null;
    }
    
    public int getCollectionItemColumnIndex(Object paramObject)
    {
      return 0;
    }
    
    public int getCollectionItemColumnSpan(Object paramObject)
    {
      return 0;
    }
    
    public Object getCollectionItemInfo(Object paramObject)
    {
      return null;
    }
    
    public int getCollectionItemRowIndex(Object paramObject)
    {
      return 0;
    }
    
    public int getCollectionItemRowSpan(Object paramObject)
    {
      return 0;
    }
    
    public CharSequence getContentDescription(Object paramObject)
    {
      return null;
    }
    
    public int getMovementGranularities(Object paramObject)
    {
      return 0;
    }
    
    public CharSequence getPackageName(Object paramObject)
    {
      return null;
    }
    
    public CharSequence getText(Object paramObject)
    {
      return null;
    }
    
    public String getViewIdResourceName(Object paramObject)
    {
      return null;
    }
    
    public boolean isAccessibilityFocused(Object paramObject)
    {
      return false;
    }
    
    public boolean isCheckable(Object paramObject)
    {
      return false;
    }
    
    public boolean isChecked(Object paramObject)
    {
      return false;
    }
    
    public boolean isClickable(Object paramObject)
    {
      return false;
    }
    
    public boolean isCollectionItemSelected(Object paramObject)
    {
      return false;
    }
    
    public boolean isEnabled(Object paramObject)
    {
      return false;
    }
    
    public boolean isFocusable(Object paramObject)
    {
      return false;
    }
    
    public boolean isFocused(Object paramObject)
    {
      return false;
    }
    
    public boolean isLongClickable(Object paramObject)
    {
      return false;
    }
    
    public boolean isPassword(Object paramObject)
    {
      return false;
    }
    
    public boolean isScrollable(Object paramObject)
    {
      return false;
    }
    
    public boolean isSelected(Object paramObject)
    {
      return false;
    }
    
    public boolean isVisibleToUser(Object paramObject)
    {
      return false;
    }
    
    public Object newAccessibilityAction(int paramInt, CharSequence paramCharSequence)
    {
      return null;
    }
    
    public Object obtain(Object paramObject)
    {
      return null;
    }
    
    public Object obtainCollectionInfo(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      return null;
    }
    
    public Object obtainCollectionItemInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      return null;
    }
    
    public void recycle(Object paramObject) {}
    
    public boolean removeAction(Object paramObject1, Object paramObject2)
    {
      return false;
    }
    
    public void setAccessibilityFocused(Object paramObject, boolean paramBoolean) {}
    
    public void setBoundsInParent(Object paramObject, Rect paramRect) {}
    
    public void setBoundsInScreen(Object paramObject, Rect paramRect) {}
    
    public void setClassName(Object paramObject, CharSequence paramCharSequence) {}
    
    public void setClickable(Object paramObject, boolean paramBoolean) {}
    
    public void setCollectionInfo(Object paramObject1, Object paramObject2) {}
    
    public void setCollectionItemInfo(Object paramObject1, Object paramObject2) {}
    
    public void setContentDescription(Object paramObject, CharSequence paramCharSequence) {}
    
    public void setEnabled(Object paramObject, boolean paramBoolean) {}
    
    public void setFocusable(Object paramObject, boolean paramBoolean) {}
    
    public void setFocused(Object paramObject, boolean paramBoolean) {}
    
    public void setLongClickable(Object paramObject, boolean paramBoolean) {}
    
    public void setMovementGranularities(Object paramObject, int paramInt) {}
    
    public void setPackageName(Object paramObject, CharSequence paramCharSequence) {}
    
    public void setParent(Object paramObject, View paramView) {}
    
    public void setScrollable(Object paramObject, boolean paramBoolean) {}
    
    public void setSelected(Object paramObject, boolean paramBoolean) {}
    
    public void setSource(Object paramObject, View paramView) {}
    
    public void setVisibleToUser(Object paramObject, boolean paramBoolean) {}
  }
  
  public static class CollectionInfoCompat
  {
    final Object mInfo;
    
    CollectionInfoCompat(Object paramObject)
    {
      this.mInfo = paramObject;
    }
    
    public static CollectionInfoCompat obtain(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      return new CollectionInfoCompat(AccessibilityNodeInfoCompat.IMPL.obtainCollectionInfo(paramInt1, paramInt2, paramBoolean, paramInt3));
    }
  }
  
  public static class CollectionItemInfoCompat
  {
    final Object mInfo;
    
    CollectionItemInfoCompat(Object paramObject)
    {
      this.mInfo = paramObject;
    }
    
    public static CollectionItemInfoCompat obtain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      return new CollectionItemInfoCompat(AccessibilityNodeInfoCompat.IMPL.obtainCollectionItemInfo(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean1, paramBoolean2));
    }
    
    public int getColumnIndex()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionItemColumnIndex(this.mInfo);
    }
    
    public int getColumnSpan()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionItemColumnSpan(this.mInfo);
    }
    
    public int getRowIndex()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionItemRowIndex(this.mInfo);
    }
    
    public int getRowSpan()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionItemRowSpan(this.mInfo);
    }
    
    public boolean isSelected()
    {
      return AccessibilityNodeInfoCompat.IMPL.isCollectionItemSelected(this.mInfo);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\accessibility\AccessibilityNodeInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */