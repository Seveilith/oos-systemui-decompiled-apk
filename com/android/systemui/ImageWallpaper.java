package com.android.systemui;

import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.os.SystemProperties;
import android.os.Trace;
import android.renderscript.Matrix4f;
import android.service.wallpaper.WallpaperService;
import android.service.wallpaper.WallpaperService.Engine;
import android.util.Log;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class ImageWallpaper
  extends WallpaperService
{
  DrawableEngine mEngine;
  boolean mIsHwAccelerated;
  WallpaperManager mWallpaperManager;
  
  private static boolean isEmulator()
  {
    return "1".equals(SystemProperties.get("ro.kernel.qemu", "0"));
  }
  
  public void onCreate()
  {
    super.onCreate();
    this.mWallpaperManager = ((WallpaperManager)getSystemService("wallpaper"));
    if (!isEmulator()) {
      this.mIsHwAccelerated = ActivityManager.isHighEndGfx();
    }
  }
  
  public WallpaperService.Engine onCreateEngine()
  {
    this.mEngine = new DrawableEngine();
    return this.mEngine;
  }
  
  public void onTrimMemory(int paramInt)
  {
    if (this.mEngine != null) {
      this.mEngine.trimMemory(paramInt);
    }
  }
  
  class DrawableEngine
    extends WallpaperService.Engine
  {
    Bitmap mBackground;
    int mBackgroundHeight = -1;
    int mBackgroundWidth = -1;
    private Display mDefaultDisplay;
    private int mDisplayHeightAtLastSurfaceSizeUpdate = -1;
    private int mDisplayWidthAtLastSurfaceSizeUpdate = -1;
    private EGL10 mEgl;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLDisplay mEglDisplay;
    private EGLSurface mEglSurface;
    private int mLastRequestedHeight = -1;
    private int mLastRequestedWidth = -1;
    int mLastRotation = -1;
    int mLastSurfaceHeight = -1;
    int mLastSurfaceWidth = -1;
    int mLastXTranslation;
    int mLastYTranslation;
    private AsyncTask<Void, Void, Bitmap> mLoader;
    private boolean mNeedsDrawAfterLoadingWallpaper;
    boolean mOffsetsChanged;
    private int mRotationAtLastSurfaceSizeUpdate = -1;
    float mScale = 1.0F;
    private boolean mSurfaceValid;
    private final DisplayInfo mTmpDisplayInfo = new DisplayInfo();
    boolean mVisible = true;
    float mXOffset = 0.5F;
    float mYOffset = 0.5F;
    
    public DrawableEngine()
    {
      super();
      setFixedSizeAllowed(true);
    }
    
    private int buildProgram(String paramString1, String paramString2)
    {
      int i = buildShader(paramString1, 35633);
      if (i == 0) {
        return 0;
      }
      int j = buildShader(paramString2, 35632);
      if (j == 0) {
        return 0;
      }
      int k = GLES20.glCreateProgram();
      GLES20.glAttachShader(k, i);
      GLES20.glAttachShader(k, j);
      GLES20.glLinkProgram(k);
      checkGlError();
      GLES20.glDeleteShader(i);
      GLES20.glDeleteShader(j);
      paramString1 = new int[1];
      GLES20.glGetProgramiv(k, 35714, paramString1, 0);
      if (paramString1[0] != 1)
      {
        paramString1 = GLES20.glGetProgramInfoLog(k);
        Log.d("ImageWallpaperGL", "Error while linking program:\n" + paramString1);
        GLES20.glDeleteProgram(k);
        return 0;
      }
      return k;
    }
    
    private int buildShader(String paramString, int paramInt)
    {
      paramInt = GLES20.glCreateShader(paramInt);
      GLES20.glShaderSource(paramInt, paramString);
      checkGlError();
      GLES20.glCompileShader(paramInt);
      checkGlError();
      paramString = new int[1];
      GLES20.glGetShaderiv(paramInt, 35713, paramString, 0);
      if (paramString[0] != 1)
      {
        paramString = GLES20.glGetShaderInfoLog(paramInt);
        Log.d("ImageWallpaperGL", "Error while compiling shader:\n" + paramString);
        GLES20.glDeleteShader(paramInt);
        return 0;
      }
      return paramInt;
    }
    
    private void checkEglError()
    {
      int i = this.mEgl.eglGetError();
      if (i != 12288) {
        Log.w("ImageWallpaperGL", "EGL error = " + GLUtils.getEGLErrorString(i));
      }
    }
    
    private void checkGlError()
    {
      int i = GLES20.glGetError();
      if (i != 0) {
        Log.w("ImageWallpaperGL", "GL error = 0x" + Integer.toHexString(i), new Throwable());
      }
    }
    
    private EGLConfig chooseEglConfig()
    {
      int[] arrayOfInt1 = new int[1];
      EGLConfig[] arrayOfEGLConfig = new EGLConfig[1];
      int[] arrayOfInt2 = getConfig();
      if (!this.mEgl.eglChooseConfig(this.mEglDisplay, arrayOfInt2, arrayOfEGLConfig, 1, arrayOfInt1)) {
        throw new IllegalArgumentException("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
      }
      if (arrayOfInt1[0] > 0) {
        return arrayOfEGLConfig[0];
      }
      return null;
    }
    
    private FloatBuffer createMesh(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
    {
      float[] arrayOfFloat = new float[20];
      arrayOfFloat[0] = paramInt1;
      arrayOfFloat[1] = paramFloat2;
      arrayOfFloat[2] = 0.0F;
      arrayOfFloat[3] = 0.0F;
      arrayOfFloat[4] = 1.0F;
      arrayOfFloat[5] = paramFloat1;
      arrayOfFloat[6] = paramFloat2;
      arrayOfFloat[7] = 0.0F;
      arrayOfFloat[8] = 1.0F;
      arrayOfFloat[9] = 1.0F;
      arrayOfFloat[10] = paramInt1;
      arrayOfFloat[11] = paramInt2;
      arrayOfFloat[12] = 0.0F;
      arrayOfFloat[13] = 0.0F;
      arrayOfFloat[14] = 0.0F;
      arrayOfFloat[15] = paramFloat1;
      arrayOfFloat[16] = paramInt2;
      arrayOfFloat[17] = 0.0F;
      arrayOfFloat[18] = 1.0F;
      arrayOfFloat[19] = 0.0F;
      FloatBuffer localFloatBuffer = ByteBuffer.allocateDirect(arrayOfFloat.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
      localFloatBuffer.put(arrayOfFloat).position(0);
      return localFloatBuffer;
    }
    
    private void drawWallpaperWithCanvas(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Canvas localCanvas = paramSurfaceHolder.lockCanvas();
      float f1;
      if (localCanvas != null) {
        f1 = paramInt3;
      }
      try
      {
        f1 += this.mBackground.getWidth() * this.mScale;
        float f2 = paramInt4 + this.mBackground.getHeight() * this.mScale;
        if ((paramInt1 < 0) || (paramInt2 < 0))
        {
          localCanvas.save(2);
          localCanvas.clipRect(paramInt3, paramInt4, f1, f2, Region.Op.DIFFERENCE);
          localCanvas.drawColor(-16777216);
          localCanvas.restore();
        }
        if (this.mBackground != null)
        {
          RectF localRectF = new RectF(paramInt3, paramInt4, f1, f2);
          localCanvas.drawBitmap(this.mBackground, null, localRectF, null);
        }
        return;
      }
      finally
      {
        paramSurfaceHolder.unlockCanvasAndPost(localCanvas);
      }
    }
    
    private boolean drawWallpaperWithOpenGL(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!initGL(paramSurfaceHolder)) {
        return false;
      }
      float f1 = paramInt3;
      float f2 = this.mBackground.getWidth();
      float f3 = this.mScale;
      float f4 = paramInt4;
      float f5 = this.mBackground.getHeight();
      float f6 = this.mScale;
      paramSurfaceHolder = paramSurfaceHolder.getSurfaceFrame();
      Matrix4f localMatrix4f = new Matrix4f();
      localMatrix4f.loadOrtho(0.0F, paramSurfaceHolder.width(), paramSurfaceHolder.height(), 0.0F, -1.0F, 1.0F);
      FloatBuffer localFloatBuffer = createMesh(paramInt3, paramInt4, f1 + f2 * f3, f4 + f5 * f6);
      paramInt3 = loadTexture(this.mBackground);
      paramInt4 = buildProgram("attribute vec4 position;\nattribute vec2 texCoords;\nvarying vec2 outTexCoords;\nuniform mat4 projection;\n\nvoid main(void) {\n    outTexCoords = texCoords;\n    gl_Position = projection * position;\n}\n\n", "precision mediump float;\n\nvarying vec2 outTexCoords;\nuniform sampler2D texture;\n\nvoid main(void) {\n    gl_FragColor = texture2D(texture, outTexCoords);\n}\n\n");
      int i = GLES20.glGetAttribLocation(paramInt4, "position");
      int j = GLES20.glGetAttribLocation(paramInt4, "texCoords");
      int k = GLES20.glGetUniformLocation(paramInt4, "texture");
      int m = GLES20.glGetUniformLocation(paramInt4, "projection");
      checkGlError();
      GLES20.glViewport(0, 0, paramSurfaceHolder.width(), paramSurfaceHolder.height());
      GLES20.glBindTexture(3553, paramInt3);
      GLES20.glUseProgram(paramInt4);
      GLES20.glEnableVertexAttribArray(i);
      GLES20.glEnableVertexAttribArray(j);
      GLES20.glUniform1i(k, 0);
      GLES20.glUniformMatrix4fv(m, 1, false, localMatrix4f.getArray(), 0);
      checkGlError();
      if ((paramInt1 > 0) || (paramInt2 > 0))
      {
        GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GLES20.glClear(16384);
      }
      localFloatBuffer.position(0);
      GLES20.glVertexAttribPointer(i, 3, 5126, false, 20, localFloatBuffer);
      localFloatBuffer.position(3);
      GLES20.glVertexAttribPointer(j, 3, 5126, false, 20, localFloatBuffer);
      GLES20.glDrawArrays(5, 0, 4);
      boolean bool = this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
      checkEglError();
      finishGL(paramInt3, paramInt4);
      return bool;
    }
    
    private void finishGL(int paramInt1, int paramInt2)
    {
      GLES20.glDeleteTextures(1, new int[] { paramInt1 }, 0);
      GLES20.glDeleteProgram(paramInt2);
      this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
      this.mEgl.eglDestroySurface(this.mEglDisplay, this.mEglSurface);
      this.mEgl.eglDestroyContext(this.mEglDisplay, this.mEglContext);
      this.mEgl.eglTerminate(this.mEglDisplay);
    }
    
    private int[] getConfig()
    {
      return new int[] { 12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12327, 12344, 12344 };
    }
    
    private DisplayInfo getDefaultDisplayInfo()
    {
      this.mDefaultDisplay.getDisplayInfo(this.mTmpDisplayInfo);
      return this.mTmpDisplayInfo;
    }
    
    private boolean initGL(SurfaceHolder paramSurfaceHolder)
    {
      this.mEgl = ((EGL10)EGLContext.getEGL());
      this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
      if (this.mEglDisplay == EGL10.EGL_NO_DISPLAY) {
        throw new RuntimeException("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
      }
      Object localObject = new int[2];
      if (!this.mEgl.eglInitialize(this.mEglDisplay, (int[])localObject)) {
        throw new RuntimeException("eglInitialize failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
      }
      this.mEglConfig = chooseEglConfig();
      if (this.mEglConfig == null) {
        throw new RuntimeException("eglConfig not initialized");
      }
      this.mEglContext = createContext(this.mEgl, this.mEglDisplay, this.mEglConfig);
      if (this.mEglContext == EGL10.EGL_NO_CONTEXT) {
        throw new RuntimeException("createContext failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
      }
      localObject = this.mEgl.eglCreatePbufferSurface(this.mEglDisplay, this.mEglConfig, new int[] { 12375, 1, 12374, 1, 12344 });
      this.mEgl.eglMakeCurrent(this.mEglDisplay, (EGLSurface)localObject, (EGLSurface)localObject, this.mEglContext);
      int[] arrayOfInt = new int[1];
      Rect localRect = paramSurfaceHolder.getSurfaceFrame();
      GLES20.glGetIntegerv(3379, arrayOfInt, 0);
      this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
      this.mEgl.eglDestroySurface(this.mEglDisplay, (EGLSurface)localObject);
      if ((localRect.width() > arrayOfInt[0]) || (localRect.height() > arrayOfInt[0]))
      {
        this.mEgl.eglDestroyContext(this.mEglDisplay, this.mEglContext);
        this.mEgl.eglTerminate(this.mEglDisplay);
        Log.e("ImageWallpaperGL", "requested  texture size " + localRect.width() + "x" + localRect.height() + " exceeds the support maximum of " + arrayOfInt[0] + "x" + arrayOfInt[0]);
        return false;
      }
      this.mEglSurface = this.mEgl.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, paramSurfaceHolder, null);
      if ((this.mEglSurface == null) || (this.mEglSurface == EGL10.EGL_NO_SURFACE))
      {
        int i = this.mEgl.eglGetError();
        if ((i == 12299) || (i == 12291))
        {
          Log.e("ImageWallpaperGL", "createWindowSurface returned " + GLUtils.getEGLErrorString(i) + ".");
          return false;
        }
        throw new RuntimeException("createWindowSurface failed " + GLUtils.getEGLErrorString(i));
      }
      if (!this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext)) {
        throw new RuntimeException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
      }
      return true;
    }
    
    private int loadTexture(Bitmap paramBitmap)
    {
      int[] arrayOfInt = new int[1];
      GLES20.glActiveTexture(33984);
      GLES20.glGenTextures(1, arrayOfInt, 0);
      checkGlError();
      int i = arrayOfInt[0];
      GLES20.glBindTexture(3553, i);
      checkGlError();
      GLES20.glTexParameteri(3553, 10241, 9729);
      GLES20.glTexParameteri(3553, 10240, 9729);
      GLES20.glTexParameteri(3553, 10242, 33071);
      GLES20.glTexParameteri(3553, 10243, 33071);
      GLUtils.texImage2D(3553, 0, 6408, paramBitmap, 5121, 0);
      checkGlError();
      return i;
    }
    
    private void loadWallpaper(boolean paramBoolean)
    {
      this.mNeedsDrawAfterLoadingWallpaper |= paramBoolean;
      if (this.mLoader != null) {
        return;
      }
      this.mLoader = new AsyncTask()
      {
        protected Bitmap doInBackground(Void... paramAnonymousVarArgs)
        {
          try
          {
            paramAnonymousVarArgs = ImageWallpaper.this.mWallpaperManager.getBitmap();
            return paramAnonymousVarArgs;
          }
          catch (RuntimeException|OutOfMemoryError paramAnonymousVarArgs)
          {
            if (paramAnonymousVarArgs != null)
            {
              Log.w("ImageWallpaper", "Unable to load wallpaper!", paramAnonymousVarArgs);
              try
              {
                ImageWallpaper.this.mWallpaperManager.clear();
              }
              catch (IOException paramAnonymousVarArgs)
              {
                for (;;)
                {
                  try
                  {
                    paramAnonymousVarArgs = ImageWallpaper.this.mWallpaperManager.getBitmap();
                    return paramAnonymousVarArgs;
                  }
                  catch (RuntimeException|OutOfMemoryError paramAnonymousVarArgs)
                  {
                    Log.w("ImageWallpaper", "Unable to load default wallpaper!", paramAnonymousVarArgs);
                  }
                  paramAnonymousVarArgs = paramAnonymousVarArgs;
                  Log.w("ImageWallpaper", "Unable reset to default wallpaper!", paramAnonymousVarArgs);
                }
              }
            }
          }
          return null;
        }
        
        protected void onPostExecute(Bitmap paramAnonymousBitmap)
        {
          ImageWallpaper.DrawableEngine.this.mBackground = null;
          ImageWallpaper.DrawableEngine.this.mBackgroundWidth = -1;
          ImageWallpaper.DrawableEngine.this.mBackgroundHeight = -1;
          if (paramAnonymousBitmap != null)
          {
            ImageWallpaper.DrawableEngine.this.mBackground = paramAnonymousBitmap;
            ImageWallpaper.DrawableEngine.this.mBackgroundWidth = ImageWallpaper.DrawableEngine.this.mBackground.getWidth();
            ImageWallpaper.DrawableEngine.this.mBackgroundHeight = ImageWallpaper.DrawableEngine.this.mBackground.getHeight();
          }
          ImageWallpaper.DrawableEngine.this.updateSurfaceSize(ImageWallpaper.DrawableEngine.this.getSurfaceHolder(), ImageWallpaper.DrawableEngine.-wrap0(ImageWallpaper.DrawableEngine.this), false);
          if (ImageWallpaper.DrawableEngine.-get0(ImageWallpaper.DrawableEngine.this)) {
            ImageWallpaper.DrawableEngine.this.drawFrame();
          }
          ImageWallpaper.DrawableEngine.-set0(ImageWallpaper.DrawableEngine.this, null);
          ImageWallpaper.DrawableEngine.-set1(ImageWallpaper.DrawableEngine.this, false);
        }
      }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
    
    EGLContext createContext(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig)
    {
      return paramEGL10.eglCreateContext(paramEGLDisplay, paramEGLConfig, EGL10.EGL_NO_CONTEXT, new int[] { 12440, 2, 12344 });
    }
    
    void drawFrame()
    {
      if (!this.mSurfaceValid) {
        return;
      }
      for (;;)
      {
        try
        {
          Trace.traceBegin(8L, "drawWallpaper");
          localObject1 = getDefaultDisplayInfo();
          k = ((DisplayInfo)localObject1).rotation;
          if (k != this.mLastRotation)
          {
            boolean bool = updateSurfaceSize(getSurfaceHolder(), (DisplayInfo)localObject1, true);
            if (!bool)
            {
              Trace.traceEnd(8L);
              if (ImageWallpaper.this.mIsHwAccelerated) {
                return;
              }
              this.mBackground = null;
              ImageWallpaper.this.mWallpaperManager.forgetLoadedWallpaper();
              return;
            }
            this.mRotationAtLastSurfaceSizeUpdate = k;
            this.mDisplayWidthAtLastSurfaceSizeUpdate = ((DisplayInfo)localObject1).logicalWidth;
            this.mDisplayHeightAtLastSurfaceSizeUpdate = ((DisplayInfo)localObject1).logicalHeight;
          }
          localObject1 = getSurfaceHolder();
          Rect localRect = ((SurfaceHolder)localObject1).getSurfaceFrame();
          i1 = localRect.width();
          i2 = localRect.height();
          if (i1 != this.mLastSurfaceWidth) {
            continue;
          }
          if (i2 == this.mLastSurfaceHeight) {
            continue;
          }
          i = 1;
          if (i != 0) {
            continue;
          }
          if (k == this.mLastRotation) {
            continue;
          }
        }
        finally
        {
          Object localObject1;
          int k;
          int i1;
          int i2;
          int i;
          float f;
          int i3;
          int i4;
          int m;
          int n;
          int i6;
          int i5;
          Trace.traceEnd(8L);
          if (!ImageWallpaper.this.mIsHwAccelerated) {
            continue;
          }
          throw ((Throwable)localObject2);
          this.mBackground = null;
          ImageWallpaper.this.mWallpaperManager.forgetLoadedWallpaper();
          return;
          this.mBackground = null;
          ImageWallpaper.this.mWallpaperManager.forgetLoadedWallpaper();
          continue;
          int j = 1;
          continue;
        }
        if ((j == 0) && (!this.mOffsetsChanged)) {
          continue;
        }
        this.mLastRotation = k;
        if (this.mBackground != null) {
          continue;
        }
        ImageWallpaper.this.mWallpaperManager.forgetLoadedWallpaper();
        loadWallpaper(true);
        Trace.traceEnd(8L);
        if (!ImageWallpaper.this.mIsHwAccelerated) {
          continue;
        }
        return;
        i = 1;
        continue;
        i = 0;
        continue;
        j = 0;
      }
      Trace.traceEnd(8L);
      if (ImageWallpaper.this.mIsHwAccelerated) {
        return;
      }
      this.mBackground = null;
      ImageWallpaper.this.mWallpaperManager.forgetLoadedWallpaper();
      return;
      this.mBackground = null;
      ImageWallpaper.this.mWallpaperManager.forgetLoadedWallpaper();
      return;
      f = i1;
      this.mScale = Math.max(1.0F, Math.max(f / this.mBackground.getWidth(), i2 / this.mBackground.getHeight()));
      i3 = i1 - (int)(this.mBackground.getWidth() * this.mScale);
      i4 = i2 - (int)(this.mBackground.getHeight() * this.mScale);
      m = i3 / 2;
      n = i4 / 2;
      i6 = i1 - this.mBackground.getWidth();
      i5 = i2 - this.mBackground.getHeight();
      k = m;
      if (i6 < 0) {
        k = m + (int)(i6 * (this.mXOffset - 0.5F) + 0.5F);
      }
      m = n;
      if (i5 < 0) {
        m = n + (int)(i5 * (this.mYOffset - 0.5F) + 0.5F);
      }
      this.mOffsetsChanged = false;
      if (i != 0)
      {
        this.mLastSurfaceWidth = i1;
        this.mLastSurfaceHeight = i2;
      }
      if ((j == 0) && (k == this.mLastXTranslation))
      {
        i = this.mLastYTranslation;
        if (m == i)
        {
          Trace.traceEnd(8L);
          if (ImageWallpaper.this.mIsHwAccelerated) {
            return;
          }
          this.mBackground = null;
          ImageWallpaper.this.mWallpaperManager.forgetLoadedWallpaper();
          return;
        }
      }
      this.mLastXTranslation = k;
      this.mLastYTranslation = m;
      if (ImageWallpaper.this.mIsHwAccelerated) {
        if (!drawWallpaperWithOpenGL((SurfaceHolder)localObject1, i3, i4, k, m)) {
          drawWallpaperWithCanvas((SurfaceHolder)localObject1, i3, i4, k, m);
        }
      }
      for (;;)
      {
        Trace.traceEnd(8L);
        if (!ImageWallpaper.this.mIsHwAccelerated) {
          break;
        }
        return;
        drawWallpaperWithCanvas((SurfaceHolder)localObject1, i3, i4, k, m);
      }
    }
    
    protected void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("ImageWallpaper.DrawableEngine:");
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" mBackground=");
      paramPrintWriter.print(this.mBackground);
      paramPrintWriter.print(" mBackgroundWidth=");
      paramPrintWriter.print(this.mBackgroundWidth);
      paramPrintWriter.print(" mBackgroundHeight=");
      paramPrintWriter.println(this.mBackgroundHeight);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" mLastRotation=");
      paramPrintWriter.print(this.mLastRotation);
      paramPrintWriter.print(" mLastSurfaceWidth=");
      paramPrintWriter.print(this.mLastSurfaceWidth);
      paramPrintWriter.print(" mLastSurfaceHeight=");
      paramPrintWriter.println(this.mLastSurfaceHeight);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" mXOffset=");
      paramPrintWriter.print(this.mXOffset);
      paramPrintWriter.print(" mYOffset=");
      paramPrintWriter.println(this.mYOffset);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" mVisible=");
      paramPrintWriter.print(this.mVisible);
      paramPrintWriter.print(" mOffsetsChanged=");
      paramPrintWriter.println(this.mOffsetsChanged);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" mLastXTranslation=");
      paramPrintWriter.print(this.mLastXTranslation);
      paramPrintWriter.print(" mLastYTranslation=");
      paramPrintWriter.print(this.mLastYTranslation);
      paramPrintWriter.print(" mScale=");
      paramPrintWriter.println(this.mScale);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" mLastRequestedWidth=");
      paramPrintWriter.print(this.mLastRequestedWidth);
      paramPrintWriter.print(" mLastRequestedHeight=");
      paramPrintWriter.println(this.mLastRequestedHeight);
      paramPrintWriter.print(paramString);
      paramPrintWriter.println(" DisplayInfo at last updateSurfaceSize:");
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  rotation=");
      paramPrintWriter.print(this.mRotationAtLastSurfaceSizeUpdate);
      paramPrintWriter.print("  width=");
      paramPrintWriter.print(this.mDisplayWidthAtLastSurfaceSizeUpdate);
      paramPrintWriter.print("  height=");
      paramPrintWriter.println(this.mDisplayHeightAtLastSurfaceSizeUpdate);
    }
    
    public void onCreate(SurfaceHolder paramSurfaceHolder)
    {
      super.onCreate(paramSurfaceHolder);
      this.mDefaultDisplay = ((WindowManager)ImageWallpaper.this.getSystemService(WindowManager.class)).getDefaultDisplay();
      setOffsetNotificationsEnabled(false);
      updateSurfaceSize(paramSurfaceHolder, getDefaultDisplayInfo(), false);
    }
    
    public void onDestroy()
    {
      super.onDestroy();
      this.mBackground = null;
      ImageWallpaper.this.mWallpaperManager.forgetLoadedWallpaper();
    }
    
    public void onOffsetsChanged(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2)
    {
      if ((this.mXOffset != paramFloat1) || (this.mYOffset != paramFloat2))
      {
        this.mXOffset = paramFloat1;
        this.mYOffset = paramFloat2;
        this.mOffsetsChanged = true;
      }
      drawFrame();
    }
    
    public void onSurfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
    {
      super.onSurfaceChanged(paramSurfaceHolder, paramInt1, paramInt2, paramInt3);
      drawFrame();
    }
    
    public void onSurfaceCreated(SurfaceHolder paramSurfaceHolder)
    {
      super.onSurfaceCreated(paramSurfaceHolder);
      this.mLastSurfaceHeight = -1;
      this.mLastSurfaceWidth = -1;
      this.mSurfaceValid = true;
    }
    
    public void onSurfaceDestroyed(SurfaceHolder paramSurfaceHolder)
    {
      super.onSurfaceDestroyed(paramSurfaceHolder);
      this.mLastSurfaceHeight = -1;
      this.mLastSurfaceWidth = -1;
      this.mSurfaceValid = false;
    }
    
    public void onSurfaceRedrawNeeded(SurfaceHolder paramSurfaceHolder)
    {
      super.onSurfaceRedrawNeeded(paramSurfaceHolder);
      drawFrame();
    }
    
    public void onVisibilityChanged(boolean paramBoolean)
    {
      if (this.mVisible != paramBoolean)
      {
        this.mVisible = paramBoolean;
        if (paramBoolean) {
          drawFrame();
        }
      }
    }
    
    public void trimMemory(int paramInt)
    {
      if ((paramInt >= 10) && (paramInt <= 15) && (this.mBackground != null))
      {
        this.mBackground.recycle();
        this.mBackground = null;
        this.mBackgroundWidth = -1;
        this.mBackgroundHeight = -1;
        ImageWallpaper.this.mWallpaperManager.forgetLoadedWallpaper();
      }
    }
    
    boolean updateSurfaceSize(SurfaceHolder paramSurfaceHolder, DisplayInfo paramDisplayInfo, boolean paramBoolean)
    {
      boolean bool = true;
      if ((this.mBackgroundWidth <= 0) || (this.mBackgroundHeight <= 0))
      {
        ImageWallpaper.this.mWallpaperManager.forgetLoadedWallpaper();
        loadWallpaper(paramBoolean);
        bool = false;
      }
      int i = Math.max(paramDisplayInfo.logicalWidth, this.mBackgroundWidth);
      int j = Math.max(paramDisplayInfo.logicalHeight, this.mBackgroundHeight);
      paramSurfaceHolder.setFixedSize(i, j);
      this.mLastRequestedWidth = i;
      this.mLastRequestedHeight = j;
      return bool;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\ImageWallpaper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */