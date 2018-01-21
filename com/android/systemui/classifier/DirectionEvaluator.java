package com.android.systemui.classifier;

public class DirectionEvaluator
{
  public static float evaluate(float paramFloat1, float paramFloat2, int paramInt)
  {
    int i;
    if (Math.abs(paramFloat2) >= Math.abs(paramFloat1))
    {
      i = 1;
      switch (paramInt)
      {
      }
    }
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              return 0.0F;
              i = 0;
              break;
            } while ((i != 0) && (paramFloat2 > 0.0D));
            return 5.5F;
          } while (i == 0);
          return 5.5F;
        } while ((i != 0) && (paramFloat2 < 0.0D));
        return 5.5F;
      } while ((paramFloat1 >= 0.0D) || (paramFloat2 <= 0.0D));
      return 5.5F;
    } while ((paramFloat1 <= 0.0D) || (paramFloat2 <= 0.0D));
    return 5.5F;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\DirectionEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */