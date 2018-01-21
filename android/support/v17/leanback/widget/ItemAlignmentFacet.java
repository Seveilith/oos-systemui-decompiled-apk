package android.support.v17.leanback.widget;

public final class ItemAlignmentFacet
{
  private ItemAlignmentDef[] mAlignmentDefs = { new ItemAlignmentDef() };
  
  public ItemAlignmentDef[] getAlignmentDefs()
  {
    return this.mAlignmentDefs;
  }
  
  public static class ItemAlignmentDef
  {
    private boolean mAlignToBaseline;
    int mFocusViewId = -1;
    int mOffset = 0;
    float mOffsetPercent = 50.0F;
    boolean mOffsetWithPadding = false;
    int mViewId = -1;
    
    public final int getItemAlignmentFocusViewId()
    {
      if (this.mFocusViewId != -1) {
        return this.mFocusViewId;
      }
      return this.mViewId;
    }
    
    public boolean isAlignedToTextViewBaseLine()
    {
      return this.mAlignToBaseline;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\ItemAlignmentFacet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */