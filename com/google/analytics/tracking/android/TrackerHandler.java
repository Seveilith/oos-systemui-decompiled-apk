package com.google.analytics.tracking.android;

import java.util.Map;

abstract class TrackerHandler
{
  abstract void sendHit(Map<String, String> paramMap);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\analytics\tracking\android\TrackerHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */