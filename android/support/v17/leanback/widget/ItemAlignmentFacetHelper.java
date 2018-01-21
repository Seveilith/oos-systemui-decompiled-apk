package android.support.v17.leanback.widget;

import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class ItemAlignmentFacetHelper
{
  private static Rect sRect = new Rect();
  
  static int getAlignmentPosition(View paramView, ItemAlignmentFacet.ItemAlignmentDef paramItemAlignmentDef, int paramInt)
  {
    GridLayoutManager.LayoutParams localLayoutParams = (GridLayoutManager.LayoutParams)paramView.getLayoutParams();
    Object localObject = paramView;
    if (paramItemAlignmentDef.mViewId != 0)
    {
      View localView = paramView.findViewById(paramItemAlignmentDef.mViewId);
      localObject = localView;
      if (localView == null) {
        localObject = paramView;
      }
    }
    int i = paramItemAlignmentDef.mOffset;
    float f;
    if (paramInt == 0)
    {
      if (paramItemAlignmentDef.mOffset >= 0)
      {
        paramInt = i;
        if (paramItemAlignmentDef.mOffsetWithPadding) {
          paramInt = i + ((View)localObject).getPaddingLeft();
        }
        i = paramInt;
        if (paramItemAlignmentDef.mOffsetPercent != -1.0F)
        {
          f = paramInt;
          if (localObject != paramView) {
            break label188;
          }
        }
      }
      label188:
      for (paramInt = localLayoutParams.getOpticalWidth((View)localObject);; paramInt = ((View)localObject).getWidth())
      {
        i = (int)(paramInt * paramItemAlignmentDef.mOffsetPercent / 100.0F + f);
        paramInt = i;
        if (paramView != localObject)
        {
          sRect.left = i;
          ((ViewGroup)paramView).offsetDescendantRectToMyCoords((View)localObject, sRect);
          paramInt = sRect.left - localLayoutParams.getOpticalLeftInset();
        }
        return paramInt;
        paramInt = i;
        if (!paramItemAlignmentDef.mOffsetWithPadding) {
          break;
        }
        paramInt = i - ((View)localObject).getPaddingRight();
        break;
      }
    }
    if (paramItemAlignmentDef.mOffset >= 0)
    {
      paramInt = i;
      if (paramItemAlignmentDef.mOffsetWithPadding) {
        paramInt = i + ((View)localObject).getPaddingTop();
      }
      label223:
      i = paramInt;
      if (paramItemAlignmentDef.mOffsetPercent != -1.0F)
      {
        f = paramInt;
        if (localObject != paramView) {
          break label374;
        }
      }
    }
    label374:
    for (paramInt = localLayoutParams.getOpticalHeight((View)localObject);; paramInt = ((View)localObject).getHeight())
    {
      i = (int)(paramInt * paramItemAlignmentDef.mOffsetPercent / 100.0F + f);
      int j = i;
      if (paramView != localObject)
      {
        sRect.top = i;
        ((ViewGroup)paramView).offsetDescendantRectToMyCoords((View)localObject, sRect);
        j = sRect.top - localLayoutParams.getOpticalTopInset();
      }
      paramInt = j;
      if (!(localObject instanceof TextView)) {
        break;
      }
      paramInt = j;
      if (!paramItemAlignmentDef.isAlignedToTextViewBaseLine()) {
        break;
      }
      return j + -((TextView)localObject).getPaint().getFontMetricsInt().top;
      paramInt = i;
      if (!paramItemAlignmentDef.mOffsetWithPadding) {
        break label223;
      }
      paramInt = i - ((View)localObject).getPaddingBottom();
      break label223;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\ItemAlignmentFacetHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */