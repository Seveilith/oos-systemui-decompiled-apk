package com.android.systemui.statusbar.stack;

public class StackViewState
  extends ViewState
{
  public boolean belowSpeedBump;
  public int clipTopAmount;
  public boolean dark;
  public boolean dimmed;
  public int height;
  public boolean hideSensitive;
  public boolean isBottomClipped;
  public int location;
  public int notGoneIndex;
  public float shadowAlpha;
  
  public void copyFrom(ViewState paramViewState)
  {
    super.copyFrom(paramViewState);
    if ((paramViewState instanceof StackViewState))
    {
      paramViewState = (StackViewState)paramViewState;
      this.height = paramViewState.height;
      this.dimmed = paramViewState.dimmed;
      this.shadowAlpha = paramViewState.shadowAlpha;
      this.dark = paramViewState.dark;
      this.hideSensitive = paramViewState.hideSensitive;
      this.belowSpeedBump = paramViewState.belowSpeedBump;
      this.clipTopAmount = paramViewState.clipTopAmount;
      this.notGoneIndex = paramViewState.notGoneIndex;
      this.location = paramViewState.location;
      this.isBottomClipped = paramViewState.isBottomClipped;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\StackViewState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */