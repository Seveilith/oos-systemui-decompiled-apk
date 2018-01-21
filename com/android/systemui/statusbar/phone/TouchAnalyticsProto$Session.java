package com.android.systemui.statusbar.phone;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class TouchAnalyticsProto$Session
  extends MessageNano
{
  private int bitField0_;
  private String build_;
  private long durationMillis_;
  public PhoneEvent[] phoneEvents;
  private int result_;
  public SensorEvent[] sensorEvents;
  private long startTimestampMillis_;
  private int touchAreaHeight_;
  private int touchAreaWidth_;
  public TouchEvent[] touchEvents;
  private int type_;
  
  public TouchAnalyticsProto$Session()
  {
    clear();
  }
  
  public Session clear()
  {
    this.bitField0_ = 0;
    this.startTimestampMillis_ = 0L;
    this.durationMillis_ = 0L;
    this.build_ = "";
    this.result_ = 0;
    this.touchEvents = TouchEvent.emptyArray();
    this.sensorEvents = SensorEvent.emptyArray();
    this.touchAreaWidth_ = 0;
    this.touchAreaHeight_ = 0;
    this.type_ = 0;
    this.phoneEvents = PhoneEvent.emptyArray();
    this.cachedSize = -1;
    return this;
  }
  
  protected int computeSerializedSize()
  {
    int j = super.computeSerializedSize();
    int i = j;
    if ((this.bitField0_ & 0x1) != 0) {
      i = j + CodedOutputByteBufferNano.computeUInt64Size(1, this.startTimestampMillis_);
    }
    j = i;
    if ((this.bitField0_ & 0x2) != 0) {
      j = i + CodedOutputByteBufferNano.computeUInt64Size(2, this.durationMillis_);
    }
    i = j;
    if ((this.bitField0_ & 0x4) != 0) {
      i = j + CodedOutputByteBufferNano.computeStringSize(3, this.build_);
    }
    j = i;
    if ((this.bitField0_ & 0x8) != 0) {
      j = i + CodedOutputByteBufferNano.computeInt32Size(4, this.result_);
    }
    i = j;
    Object localObject;
    if (this.touchEvents != null)
    {
      i = j;
      if (this.touchEvents.length > 0)
      {
        k = 0;
        for (;;)
        {
          i = j;
          if (k >= this.touchEvents.length) {
            break;
          }
          localObject = this.touchEvents[k];
          i = j;
          if (localObject != null) {
            i = j + CodedOutputByteBufferNano.computeMessageSize(5, (MessageNano)localObject);
          }
          k += 1;
          j = i;
        }
      }
    }
    j = i;
    if (this.sensorEvents != null)
    {
      j = i;
      if (this.sensorEvents.length > 0)
      {
        k = 0;
        for (;;)
        {
          j = i;
          if (k >= this.sensorEvents.length) {
            break;
          }
          localObject = this.sensorEvents[k];
          j = i;
          if (localObject != null) {
            j = i + CodedOutputByteBufferNano.computeMessageSize(6, (MessageNano)localObject);
          }
          k += 1;
          i = j;
        }
      }
    }
    i = j;
    if ((this.bitField0_ & 0x10) != 0) {
      i = j + CodedOutputByteBufferNano.computeInt32Size(9, this.touchAreaWidth_);
    }
    j = i;
    if ((this.bitField0_ & 0x20) != 0) {
      j = i + CodedOutputByteBufferNano.computeInt32Size(10, this.touchAreaHeight_);
    }
    i = j;
    if ((this.bitField0_ & 0x40) != 0) {
      i = j + CodedOutputByteBufferNano.computeInt32Size(11, this.type_);
    }
    int k = i;
    if (this.phoneEvents != null)
    {
      k = i;
      if (this.phoneEvents.length > 0)
      {
        j = 0;
        for (;;)
        {
          k = i;
          if (j >= this.phoneEvents.length) {
            break;
          }
          localObject = this.phoneEvents[j];
          k = i;
          if (localObject != null) {
            k = i + CodedOutputByteBufferNano.computeMessageSize(12, (MessageNano)localObject);
          }
          j += 1;
          i = k;
        }
      }
    }
    return k;
  }
  
  public Session mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
    throws IOException
  {
    for (;;)
    {
      int i = paramCodedInputByteBufferNano.readTag();
      int j;
      Object localObject;
      switch (i)
      {
      default: 
        if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
          return this;
        }
        break;
      case 0: 
        return this;
      case 8: 
        this.startTimestampMillis_ = paramCodedInputByteBufferNano.readUInt64();
        this.bitField0_ |= 0x1;
        break;
      case 16: 
        this.durationMillis_ = paramCodedInputByteBufferNano.readUInt64();
        this.bitField0_ |= 0x2;
        break;
      case 26: 
        this.build_ = paramCodedInputByteBufferNano.readString();
        this.bitField0_ |= 0x4;
        break;
      case 32: 
        i = paramCodedInputByteBufferNano.readInt32();
        switch (i)
        {
        default: 
          break;
        case 0: 
        case 1: 
        case 2: 
          this.result_ = i;
          this.bitField0_ |= 0x8;
        }
        break;
      case 42: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 42);
        if (this.touchEvents == null) {}
        for (i = 0;; i = this.touchEvents.length)
        {
          localObject = new TouchEvent[i + j];
          j = i;
          if (i != 0)
          {
            System.arraycopy(this.touchEvents, 0, localObject, 0, i);
            j = i;
          }
          while (j < localObject.length - 1)
          {
            localObject[j] = new TouchEvent();
            paramCodedInputByteBufferNano.readMessage(localObject[j]);
            paramCodedInputByteBufferNano.readTag();
            j += 1;
          }
        }
        localObject[j] = new TouchEvent();
        paramCodedInputByteBufferNano.readMessage(localObject[j]);
        this.touchEvents = ((TouchEvent[])localObject);
        break;
      case 50: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 50);
        if (this.sensorEvents == null) {}
        for (i = 0;; i = this.sensorEvents.length)
        {
          localObject = new SensorEvent[i + j];
          j = i;
          if (i != 0)
          {
            System.arraycopy(this.sensorEvents, 0, localObject, 0, i);
            j = i;
          }
          while (j < localObject.length - 1)
          {
            localObject[j] = new SensorEvent();
            paramCodedInputByteBufferNano.readMessage(localObject[j]);
            paramCodedInputByteBufferNano.readTag();
            j += 1;
          }
        }
        localObject[j] = new SensorEvent();
        paramCodedInputByteBufferNano.readMessage(localObject[j]);
        this.sensorEvents = ((SensorEvent[])localObject);
        break;
      case 72: 
        this.touchAreaWidth_ = paramCodedInputByteBufferNano.readInt32();
        this.bitField0_ |= 0x10;
        break;
      case 80: 
        this.touchAreaHeight_ = paramCodedInputByteBufferNano.readInt32();
        this.bitField0_ |= 0x20;
        break;
      case 88: 
        i = paramCodedInputByteBufferNano.readInt32();
        switch (i)
        {
        default: 
          break;
        case 0: 
        case 1: 
        case 2: 
        case 3: 
        case 4: 
          this.type_ = i;
          this.bitField0_ |= 0x40;
        }
        break;
      case 98: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 98);
        if (this.phoneEvents == null) {}
        for (i = 0;; i = this.phoneEvents.length)
        {
          localObject = new PhoneEvent[i + j];
          j = i;
          if (i != 0)
          {
            System.arraycopy(this.phoneEvents, 0, localObject, 0, i);
            j = i;
          }
          while (j < localObject.length - 1)
          {
            localObject[j] = new PhoneEvent();
            paramCodedInputByteBufferNano.readMessage(localObject[j]);
            paramCodedInputByteBufferNano.readTag();
            j += 1;
          }
        }
        localObject[j] = new PhoneEvent();
        paramCodedInputByteBufferNano.readMessage(localObject[j]);
        this.phoneEvents = ((PhoneEvent[])localObject);
      }
    }
  }
  
  public Session setBuild(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    this.build_ = paramString;
    this.bitField0_ |= 0x4;
    return this;
  }
  
  public Session setDurationMillis(long paramLong)
  {
    this.durationMillis_ = paramLong;
    this.bitField0_ |= 0x2;
    return this;
  }
  
  public Session setResult(int paramInt)
  {
    this.result_ = paramInt;
    this.bitField0_ |= 0x8;
    return this;
  }
  
  public Session setStartTimestampMillis(long paramLong)
  {
    this.startTimestampMillis_ = paramLong;
    this.bitField0_ |= 0x1;
    return this;
  }
  
  public Session setTouchAreaHeight(int paramInt)
  {
    this.touchAreaHeight_ = paramInt;
    this.bitField0_ |= 0x20;
    return this;
  }
  
  public Session setTouchAreaWidth(int paramInt)
  {
    this.touchAreaWidth_ = paramInt;
    this.bitField0_ |= 0x10;
    return this;
  }
  
  public Session setType(int paramInt)
  {
    this.type_ = paramInt;
    this.bitField0_ |= 0x40;
    return this;
  }
  
  public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
    throws IOException
  {
    if ((this.bitField0_ & 0x1) != 0) {
      paramCodedOutputByteBufferNano.writeUInt64(1, this.startTimestampMillis_);
    }
    if ((this.bitField0_ & 0x2) != 0) {
      paramCodedOutputByteBufferNano.writeUInt64(2, this.durationMillis_);
    }
    if ((this.bitField0_ & 0x4) != 0) {
      paramCodedOutputByteBufferNano.writeString(3, this.build_);
    }
    if ((this.bitField0_ & 0x8) != 0) {
      paramCodedOutputByteBufferNano.writeInt32(4, this.result_);
    }
    int i;
    Object localObject;
    if ((this.touchEvents != null) && (this.touchEvents.length > 0))
    {
      i = 0;
      while (i < this.touchEvents.length)
      {
        localObject = this.touchEvents[i];
        if (localObject != null) {
          paramCodedOutputByteBufferNano.writeMessage(5, (MessageNano)localObject);
        }
        i += 1;
      }
    }
    if ((this.sensorEvents != null) && (this.sensorEvents.length > 0))
    {
      i = 0;
      while (i < this.sensorEvents.length)
      {
        localObject = this.sensorEvents[i];
        if (localObject != null) {
          paramCodedOutputByteBufferNano.writeMessage(6, (MessageNano)localObject);
        }
        i += 1;
      }
    }
    if ((this.bitField0_ & 0x10) != 0) {
      paramCodedOutputByteBufferNano.writeInt32(9, this.touchAreaWidth_);
    }
    if ((this.bitField0_ & 0x20) != 0) {
      paramCodedOutputByteBufferNano.writeInt32(10, this.touchAreaHeight_);
    }
    if ((this.bitField0_ & 0x40) != 0) {
      paramCodedOutputByteBufferNano.writeInt32(11, this.type_);
    }
    if ((this.phoneEvents != null) && (this.phoneEvents.length > 0))
    {
      i = 0;
      while (i < this.phoneEvents.length)
      {
        localObject = this.phoneEvents[i];
        if (localObject != null) {
          paramCodedOutputByteBufferNano.writeMessage(12, (MessageNano)localObject);
        }
        i += 1;
      }
    }
    super.writeTo(paramCodedOutputByteBufferNano);
  }
  
  public static final class PhoneEvent
    extends MessageNano
  {
    private static volatile PhoneEvent[] _emptyArray;
    private int bitField0_;
    private long timeOffsetNanos_;
    private int type_;
    
    public PhoneEvent()
    {
      clear();
    }
    
    public static PhoneEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new PhoneEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public PhoneEvent clear()
    {
      this.bitField0_ = 0;
      this.type_ = 0;
      this.timeOffsetNanos_ = 0L;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if ((this.bitField0_ & 0x1) != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(1, this.type_);
      }
      j = i;
      if ((this.bitField0_ & 0x2) != 0) {
        j = i + CodedOutputByteBufferNano.computeUInt64Size(2, this.timeOffsetNanos_);
      }
      return j;
    }
    
    public PhoneEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 8: 
          i = paramCodedInputByteBufferNano.readInt32();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          case 8: 
          case 9: 
          case 10: 
          case 11: 
          case 12: 
          case 13: 
          case 14: 
          case 15: 
          case 16: 
          case 17: 
          case 18: 
          case 19: 
          case 20: 
          case 21: 
          case 22: 
          case 23: 
          case 24: 
          case 25: 
          case 26: 
          case 27: 
          case 28: 
            this.type_ = i;
            this.bitField0_ |= 0x1;
          }
          break;
        case 16: 
          this.timeOffsetNanos_ = paramCodedInputByteBufferNano.readUInt64();
          this.bitField0_ |= 0x2;
        }
      }
    }
    
    public PhoneEvent setTimeOffsetNanos(long paramLong)
    {
      this.timeOffsetNanos_ = paramLong;
      this.bitField0_ |= 0x2;
      return this;
    }
    
    public PhoneEvent setType(int paramInt)
    {
      this.type_ = paramInt;
      this.bitField0_ |= 0x1;
      return this;
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if ((this.bitField0_ & 0x1) != 0) {
        paramCodedOutputByteBufferNano.writeInt32(1, this.type_);
      }
      if ((this.bitField0_ & 0x2) != 0) {
        paramCodedOutputByteBufferNano.writeUInt64(2, this.timeOffsetNanos_);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class SensorEvent
    extends MessageNano
  {
    private static volatile SensorEvent[] _emptyArray;
    private int bitField0_;
    private long timeOffsetNanos_;
    private long timestamp_;
    private int type_;
    public float[] values;
    
    public SensorEvent()
    {
      clear();
    }
    
    public static SensorEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new SensorEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public SensorEvent clear()
    {
      this.bitField0_ = 0;
      this.type_ = 1;
      this.timeOffsetNanos_ = 0L;
      this.values = WireFormatNano.EMPTY_FLOAT_ARRAY;
      this.timestamp_ = 0L;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if ((this.bitField0_ & 0x1) != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(1, this.type_);
      }
      j = i;
      if ((this.bitField0_ & 0x2) != 0) {
        j = i + CodedOutputByteBufferNano.computeUInt64Size(2, this.timeOffsetNanos_);
      }
      i = j;
      if (this.values != null)
      {
        i = j;
        if (this.values.length > 0) {
          i = j + this.values.length * 4 + this.values.length * 1;
        }
      }
      j = i;
      if ((this.bitField0_ & 0x4) != 0) {
        j = i + CodedOutputByteBufferNano.computeUInt64Size(4, this.timestamp_);
      }
      return j;
    }
    
    public SensorEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        int j;
        float[] arrayOfFloat;
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 8: 
          i = paramCodedInputByteBufferNano.readInt32();
          switch (i)
          {
          case 2: 
          case 3: 
          case 6: 
          case 7: 
          case 9: 
          case 10: 
          default: 
            break;
          case 1: 
          case 4: 
          case 5: 
          case 8: 
          case 11: 
            this.type_ = i;
            this.bitField0_ |= 0x1;
          }
          break;
        case 16: 
          this.timeOffsetNanos_ = paramCodedInputByteBufferNano.readUInt64();
          this.bitField0_ |= 0x2;
          break;
        case 29: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 29);
          if (this.values == null) {}
          for (i = 0;; i = this.values.length)
          {
            arrayOfFloat = new float[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.values, 0, arrayOfFloat, 0, i);
              j = i;
            }
            while (j < arrayOfFloat.length - 1)
            {
              arrayOfFloat[j] = paramCodedInputByteBufferNano.readFloat();
              paramCodedInputByteBufferNano.readTag();
              j += 1;
            }
          }
          arrayOfFloat[j] = paramCodedInputByteBufferNano.readFloat();
          this.values = arrayOfFloat;
          break;
        case 26: 
          i = paramCodedInputByteBufferNano.readRawVarint32();
          int k = paramCodedInputByteBufferNano.pushLimit(i);
          j = i / 4;
          if (this.values == null) {}
          for (i = 0;; i = this.values.length)
          {
            arrayOfFloat = new float[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.values, 0, arrayOfFloat, 0, i);
              j = i;
            }
            while (j < arrayOfFloat.length)
            {
              arrayOfFloat[j] = paramCodedInputByteBufferNano.readFloat();
              j += 1;
            }
          }
          this.values = arrayOfFloat;
          paramCodedInputByteBufferNano.popLimit(k);
          break;
        case 32: 
          this.timestamp_ = paramCodedInputByteBufferNano.readUInt64();
          this.bitField0_ |= 0x4;
        }
      }
    }
    
    public SensorEvent setTimeOffsetNanos(long paramLong)
    {
      this.timeOffsetNanos_ = paramLong;
      this.bitField0_ |= 0x2;
      return this;
    }
    
    public SensorEvent setTimestamp(long paramLong)
    {
      this.timestamp_ = paramLong;
      this.bitField0_ |= 0x4;
      return this;
    }
    
    public SensorEvent setType(int paramInt)
    {
      this.type_ = paramInt;
      this.bitField0_ |= 0x1;
      return this;
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if ((this.bitField0_ & 0x1) != 0) {
        paramCodedOutputByteBufferNano.writeInt32(1, this.type_);
      }
      if ((this.bitField0_ & 0x2) != 0) {
        paramCodedOutputByteBufferNano.writeUInt64(2, this.timeOffsetNanos_);
      }
      if ((this.values != null) && (this.values.length > 0))
      {
        int i = 0;
        while (i < this.values.length)
        {
          paramCodedOutputByteBufferNano.writeFloat(3, this.values[i]);
          i += 1;
        }
      }
      if ((this.bitField0_ & 0x4) != 0) {
        paramCodedOutputByteBufferNano.writeUInt64(4, this.timestamp_);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class TouchEvent
    extends MessageNano
  {
    private static volatile TouchEvent[] _emptyArray;
    private int actionIndex_;
    private int action_;
    private int bitField0_;
    public Pointer[] pointers;
    public BoundingBox removedBoundingBox;
    private boolean removedRedacted_;
    private long timeOffsetNanos_;
    
    public TouchEvent()
    {
      clear();
    }
    
    public static TouchEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new TouchEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public TouchEvent clear()
    {
      this.bitField0_ = 0;
      this.timeOffsetNanos_ = 0L;
      this.action_ = 0;
      this.actionIndex_ = 0;
      this.pointers = Pointer.emptyArray();
      this.removedRedacted_ = false;
      this.removedBoundingBox = null;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if ((this.bitField0_ & 0x1) != 0) {
        i = j + CodedOutputByteBufferNano.computeUInt64Size(1, this.timeOffsetNanos_);
      }
      j = i;
      if ((this.bitField0_ & 0x2) != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(2, this.action_);
      }
      i = j;
      if ((this.bitField0_ & 0x4) != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(3, this.actionIndex_);
      }
      j = i;
      if (this.pointers != null)
      {
        j = i;
        if (this.pointers.length > 0)
        {
          int k = 0;
          for (;;)
          {
            j = i;
            if (k >= this.pointers.length) {
              break;
            }
            Pointer localPointer = this.pointers[k];
            j = i;
            if (localPointer != null) {
              j = i + CodedOutputByteBufferNano.computeMessageSize(4, localPointer);
            }
            k += 1;
            i = j;
          }
        }
      }
      i = j;
      if ((this.bitField0_ & 0x8) != 0) {
        i = j + CodedOutputByteBufferNano.computeBoolSize(5, this.removedRedacted_);
      }
      j = i;
      if (this.removedBoundingBox != null) {
        j = i + CodedOutputByteBufferNano.computeMessageSize(6, this.removedBoundingBox);
      }
      return j;
    }
    
    public TouchEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 8: 
          this.timeOffsetNanos_ = paramCodedInputByteBufferNano.readUInt64();
          this.bitField0_ |= 0x1;
          break;
        case 16: 
          i = paramCodedInputByteBufferNano.readInt32();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
            this.action_ = i;
            this.bitField0_ |= 0x2;
          }
          break;
        case 24: 
          this.actionIndex_ = paramCodedInputByteBufferNano.readInt32();
          this.bitField0_ |= 0x4;
          break;
        case 34: 
          int j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 34);
          if (this.pointers == null) {}
          Pointer[] arrayOfPointer;
          for (i = 0;; i = this.pointers.length)
          {
            arrayOfPointer = new Pointer[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.pointers, 0, arrayOfPointer, 0, i);
              j = i;
            }
            while (j < arrayOfPointer.length - 1)
            {
              arrayOfPointer[j] = new Pointer();
              paramCodedInputByteBufferNano.readMessage(arrayOfPointer[j]);
              paramCodedInputByteBufferNano.readTag();
              j += 1;
            }
          }
          arrayOfPointer[j] = new Pointer();
          paramCodedInputByteBufferNano.readMessage(arrayOfPointer[j]);
          this.pointers = arrayOfPointer;
          break;
        case 40: 
          this.removedRedacted_ = paramCodedInputByteBufferNano.readBool();
          this.bitField0_ |= 0x8;
          break;
        case 50: 
          if (this.removedBoundingBox == null) {
            this.removedBoundingBox = new BoundingBox();
          }
          paramCodedInputByteBufferNano.readMessage(this.removedBoundingBox);
        }
      }
    }
    
    public TouchEvent setAction(int paramInt)
    {
      this.action_ = paramInt;
      this.bitField0_ |= 0x2;
      return this;
    }
    
    public TouchEvent setActionIndex(int paramInt)
    {
      this.actionIndex_ = paramInt;
      this.bitField0_ |= 0x4;
      return this;
    }
    
    public TouchEvent setTimeOffsetNanos(long paramLong)
    {
      this.timeOffsetNanos_ = paramLong;
      this.bitField0_ |= 0x1;
      return this;
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if ((this.bitField0_ & 0x1) != 0) {
        paramCodedOutputByteBufferNano.writeUInt64(1, this.timeOffsetNanos_);
      }
      if ((this.bitField0_ & 0x2) != 0) {
        paramCodedOutputByteBufferNano.writeInt32(2, this.action_);
      }
      if ((this.bitField0_ & 0x4) != 0) {
        paramCodedOutputByteBufferNano.writeInt32(3, this.actionIndex_);
      }
      if ((this.pointers != null) && (this.pointers.length > 0))
      {
        int i = 0;
        while (i < this.pointers.length)
        {
          Pointer localPointer = this.pointers[i];
          if (localPointer != null) {
            paramCodedOutputByteBufferNano.writeMessage(4, localPointer);
          }
          i += 1;
        }
      }
      if ((this.bitField0_ & 0x8) != 0) {
        paramCodedOutputByteBufferNano.writeBool(5, this.removedRedacted_);
      }
      if (this.removedBoundingBox != null) {
        paramCodedOutputByteBufferNano.writeMessage(6, this.removedBoundingBox);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
    
    public static final class BoundingBox
      extends MessageNano
    {
      private int bitField0_;
      private float height_;
      private float width_;
      
      public BoundingBox()
      {
        clear();
      }
      
      public BoundingBox clear()
      {
        this.bitField0_ = 0;
        this.width_ = 0.0F;
        this.height_ = 0.0F;
        this.cachedSize = -1;
        return this;
      }
      
      protected int computeSerializedSize()
      {
        int j = super.computeSerializedSize();
        int i = j;
        if ((this.bitField0_ & 0x1) != 0) {
          i = j + CodedOutputByteBufferNano.computeFloatSize(1, this.width_);
        }
        j = i;
        if ((this.bitField0_ & 0x2) != 0) {
          j = i + CodedOutputByteBufferNano.computeFloatSize(2, this.height_);
        }
        return j;
      }
      
      public BoundingBox mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
        throws IOException
      {
        for (;;)
        {
          int i = paramCodedInputByteBufferNano.readTag();
          switch (i)
          {
          default: 
            if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
              return this;
            }
            break;
          case 0: 
            return this;
          case 13: 
            this.width_ = paramCodedInputByteBufferNano.readFloat();
            this.bitField0_ |= 0x1;
            break;
          case 21: 
            this.height_ = paramCodedInputByteBufferNano.readFloat();
            this.bitField0_ |= 0x2;
          }
        }
      }
      
      public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
        throws IOException
      {
        if ((this.bitField0_ & 0x1) != 0) {
          paramCodedOutputByteBufferNano.writeFloat(1, this.width_);
        }
        if ((this.bitField0_ & 0x2) != 0) {
          paramCodedOutputByteBufferNano.writeFloat(2, this.height_);
        }
        super.writeTo(paramCodedOutputByteBufferNano);
      }
    }
    
    public static final class Pointer
      extends MessageNano
    {
      private static volatile Pointer[] _emptyArray;
      private int bitField0_;
      private int id_;
      private float pressure_;
      public TouchAnalyticsProto.Session.TouchEvent.BoundingBox removedBoundingBox;
      private float removedLength_;
      private float size_;
      private float x_;
      private float y_;
      
      public Pointer()
      {
        clear();
      }
      
      public static Pointer[] emptyArray()
      {
        if (_emptyArray == null) {}
        synchronized (InternalNano.LAZY_INIT_LOCK)
        {
          if (_emptyArray == null) {
            _emptyArray = new Pointer[0];
          }
          return _emptyArray;
        }
      }
      
      public Pointer clear()
      {
        this.bitField0_ = 0;
        this.x_ = 0.0F;
        this.y_ = 0.0F;
        this.size_ = 0.0F;
        this.pressure_ = 0.0F;
        this.id_ = 0;
        this.removedLength_ = 0.0F;
        this.removedBoundingBox = null;
        this.cachedSize = -1;
        return this;
      }
      
      protected int computeSerializedSize()
      {
        int j = super.computeSerializedSize();
        int i = j;
        if ((this.bitField0_ & 0x1) != 0) {
          i = j + CodedOutputByteBufferNano.computeFloatSize(1, this.x_);
        }
        j = i;
        if ((this.bitField0_ & 0x2) != 0) {
          j = i + CodedOutputByteBufferNano.computeFloatSize(2, this.y_);
        }
        i = j;
        if ((this.bitField0_ & 0x4) != 0) {
          i = j + CodedOutputByteBufferNano.computeFloatSize(3, this.size_);
        }
        j = i;
        if ((this.bitField0_ & 0x8) != 0) {
          j = i + CodedOutputByteBufferNano.computeFloatSize(4, this.pressure_);
        }
        i = j;
        if ((this.bitField0_ & 0x10) != 0) {
          i = j + CodedOutputByteBufferNano.computeInt32Size(5, this.id_);
        }
        j = i;
        if ((this.bitField0_ & 0x20) != 0) {
          j = i + CodedOutputByteBufferNano.computeFloatSize(6, this.removedLength_);
        }
        i = j;
        if (this.removedBoundingBox != null) {
          i = j + CodedOutputByteBufferNano.computeMessageSize(7, this.removedBoundingBox);
        }
        return i;
      }
      
      public Pointer mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
        throws IOException
      {
        for (;;)
        {
          int i = paramCodedInputByteBufferNano.readTag();
          switch (i)
          {
          default: 
            if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
              return this;
            }
            break;
          case 0: 
            return this;
          case 13: 
            this.x_ = paramCodedInputByteBufferNano.readFloat();
            this.bitField0_ |= 0x1;
            break;
          case 21: 
            this.y_ = paramCodedInputByteBufferNano.readFloat();
            this.bitField0_ |= 0x2;
            break;
          case 29: 
            this.size_ = paramCodedInputByteBufferNano.readFloat();
            this.bitField0_ |= 0x4;
            break;
          case 37: 
            this.pressure_ = paramCodedInputByteBufferNano.readFloat();
            this.bitField0_ |= 0x8;
            break;
          case 40: 
            this.id_ = paramCodedInputByteBufferNano.readInt32();
            this.bitField0_ |= 0x10;
            break;
          case 53: 
            this.removedLength_ = paramCodedInputByteBufferNano.readFloat();
            this.bitField0_ |= 0x20;
            break;
          case 58: 
            if (this.removedBoundingBox == null) {
              this.removedBoundingBox = new TouchAnalyticsProto.Session.TouchEvent.BoundingBox();
            }
            paramCodedInputByteBufferNano.readMessage(this.removedBoundingBox);
          }
        }
      }
      
      public Pointer setId(int paramInt)
      {
        this.id_ = paramInt;
        this.bitField0_ |= 0x10;
        return this;
      }
      
      public Pointer setPressure(float paramFloat)
      {
        this.pressure_ = paramFloat;
        this.bitField0_ |= 0x8;
        return this;
      }
      
      public Pointer setSize(float paramFloat)
      {
        this.size_ = paramFloat;
        this.bitField0_ |= 0x4;
        return this;
      }
      
      public Pointer setX(float paramFloat)
      {
        this.x_ = paramFloat;
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Pointer setY(float paramFloat)
      {
        this.y_ = paramFloat;
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
        throws IOException
      {
        if ((this.bitField0_ & 0x1) != 0) {
          paramCodedOutputByteBufferNano.writeFloat(1, this.x_);
        }
        if ((this.bitField0_ & 0x2) != 0) {
          paramCodedOutputByteBufferNano.writeFloat(2, this.y_);
        }
        if ((this.bitField0_ & 0x4) != 0) {
          paramCodedOutputByteBufferNano.writeFloat(3, this.size_);
        }
        if ((this.bitField0_ & 0x8) != 0) {
          paramCodedOutputByteBufferNano.writeFloat(4, this.pressure_);
        }
        if ((this.bitField0_ & 0x10) != 0) {
          paramCodedOutputByteBufferNano.writeInt32(5, this.id_);
        }
        if ((this.bitField0_ & 0x20) != 0) {
          paramCodedOutputByteBufferNano.writeFloat(6, this.removedLength_);
        }
        if (this.removedBoundingBox != null) {
          paramCodedOutputByteBufferNano.writeMessage(7, this.removedBoundingBox);
        }
        super.writeTo(paramCodedOutputByteBufferNano);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\TouchAnalyticsProto$Session.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */