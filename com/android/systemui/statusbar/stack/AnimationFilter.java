package com.android.systemui.statusbar.stack;

import java.util.ArrayList;

public class AnimationFilter
{
  boolean animateAlpha;
  boolean animateDark;
  boolean animateDimmed;
  boolean animateHeight;
  boolean animateHideSensitive;
  public boolean animateShadowAlpha;
  boolean animateTopInset;
  boolean animateY;
  boolean animateZ;
  int darkAnimationOriginIndex;
  boolean hasDarkEvent;
  boolean hasDelays;
  boolean hasGoToFullShadeEvent;
  boolean hasHeadsUpDisappearClickEvent;
  
  private void combineFilter(AnimationFilter paramAnimationFilter)
  {
    this.animateAlpha |= paramAnimationFilter.animateAlpha;
    this.animateY |= paramAnimationFilter.animateY;
    this.animateZ |= paramAnimationFilter.animateZ;
    this.animateHeight |= paramAnimationFilter.animateHeight;
    this.animateTopInset |= paramAnimationFilter.animateTopInset;
    this.animateDimmed |= paramAnimationFilter.animateDimmed;
    this.animateDark |= paramAnimationFilter.animateDark;
    this.animateHideSensitive |= paramAnimationFilter.animateHideSensitive;
    this.animateShadowAlpha |= paramAnimationFilter.animateShadowAlpha;
    this.hasDelays |= paramAnimationFilter.hasDelays;
  }
  
  private void reset()
  {
    this.animateAlpha = false;
    this.animateY = false;
    this.animateZ = false;
    this.animateHeight = false;
    this.animateShadowAlpha = false;
    this.animateTopInset = false;
    this.animateDimmed = false;
    this.animateDark = false;
    this.animateHideSensitive = false;
    this.hasDelays = false;
    this.hasGoToFullShadeEvent = false;
    this.hasDarkEvent = false;
    this.hasHeadsUpDisappearClickEvent = false;
    this.darkAnimationOriginIndex = -1;
  }
  
  public AnimationFilter animateAlpha()
  {
    this.animateAlpha = true;
    return this;
  }
  
  public AnimationFilter animateDark()
  {
    this.animateDark = true;
    return this;
  }
  
  public AnimationFilter animateDimmed()
  {
    this.animateDimmed = true;
    return this;
  }
  
  public AnimationFilter animateHeight()
  {
    this.animateHeight = true;
    return this;
  }
  
  public AnimationFilter animateHideSensitive()
  {
    this.animateHideSensitive = true;
    return this;
  }
  
  public AnimationFilter animateShadowAlpha()
  {
    this.animateShadowAlpha = true;
    return this;
  }
  
  public AnimationFilter animateTopInset()
  {
    this.animateTopInset = true;
    return this;
  }
  
  public AnimationFilter animateY()
  {
    this.animateY = true;
    return this;
  }
  
  public AnimationFilter animateZ()
  {
    this.animateZ = true;
    return this;
  }
  
  public void applyCombination(ArrayList<NotificationStackScrollLayout.AnimationEvent> paramArrayList)
  {
    reset();
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      NotificationStackScrollLayout.AnimationEvent localAnimationEvent = (NotificationStackScrollLayout.AnimationEvent)paramArrayList.get(i);
      combineFilter(((NotificationStackScrollLayout.AnimationEvent)paramArrayList.get(i)).filter);
      if (localAnimationEvent.animationType == 10) {
        this.hasGoToFullShadeEvent = true;
      }
      if (localAnimationEvent.animationType == 9)
      {
        this.hasDarkEvent = true;
        this.darkAnimationOriginIndex = localAnimationEvent.darkAnimationOriginIndex;
      }
      if (localAnimationEvent.animationType == 16) {
        this.hasHeadsUpDisappearClickEvent = true;
      }
      i += 1;
    }
  }
  
  public AnimationFilter hasDelays()
  {
    this.hasDelays = true;
    return this;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\AnimationFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */