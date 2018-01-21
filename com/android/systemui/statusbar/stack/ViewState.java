package com.android.systemui.statusbar.stack;

import android.view.View;

public class ViewState
{
  public float alpha;
  public boolean gone;
  public boolean hidden;
  public float yTranslation;
  public float zTranslation;
  
  public void copyFrom(ViewState paramViewState)
  {
    this.alpha = paramViewState.alpha;
    this.yTranslation = paramViewState.yTranslation;
    this.zTranslation = paramViewState.zTranslation;
    this.gone = paramViewState.gone;
    this.hidden = paramViewState.hidden;
  }
  
  public void initFrom(View paramView)
  {
    this.alpha = paramView.getAlpha();
    this.yTranslation = paramView.getTranslationY();
    this.zTranslation = paramView.getTranslationZ();
    if (paramView.getVisibility() == 8) {}
    for (boolean bool = true;; bool = false)
    {
      this.gone = bool;
      this.hidden = false;
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\ViewState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */