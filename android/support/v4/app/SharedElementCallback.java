package android.support.v4.app;

import android.view.View;
import java.util.List;
import java.util.Map;

public abstract class SharedElementCallback
{
  private static int MAX_IMAGE_SIZE = 1048576;
  
  public void onMapSharedElements(List<String> paramList, Map<String, View> paramMap) {}
  
  public void onSharedElementEnd(List<String> paramList, List<View> paramList1, List<View> paramList2) {}
  
  public void onSharedElementStart(List<String> paramList, List<View> paramList1, List<View> paramList2) {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\app\SharedElementCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */