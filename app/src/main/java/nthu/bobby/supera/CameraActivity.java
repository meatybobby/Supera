package nthu.bobby.supera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraActivity extends Activity implements SurfaceHolder.Callback,Camera.PreviewCallback {
    private Camera camera;
    private SurfaceView previewView;
    private SurfaceHolder previewHolder;
    private boolean previewing = false;
    private Button takeButton;
    private SurfaceView drawView;
    private SurfaceHolder drawHolder;
    private int viewHeight,viewWidth;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private RenderScript rs;
    private Allocation in,out;
    private boolean indraw=false;
    private int drawWidth,drawHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        takeButton = (Button) findViewById(R.id.takeButton);
        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
            }
        });
        previewView = (SurfaceView) findViewById(R.id.surfaceView);
        drawView = (SurfaceView) findViewById(R.id.imageView);
        drawHolder = drawView.getHolder();
        previewHolder = previewView.getHolder();
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        previewHolder.addCallback(this);
        drawView.setZOrderMediaOverlay(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }
        try {
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode("auto");
            List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
            for(Camera.Size s : previewSizes) {
                if(s.width <=1000 && s.height<=800) {
                    parameters.setPreviewSize(s.width, s.height);
                    break;
                }
            }
            camera.setParameters(parameters);
            camera.setPreviewCallback(this);
            viewHeight = parameters.getPreviewSize().height;
            viewWidth = parameters.getPreviewSize().width;
            drawWidth = width;
            drawHeight = height;
            int imgformat = parameters.getPreviewFormat();
            int bitsperpixel = ImageFormat.getBitsPerPixel(imgformat);
            int frame_size = ((viewWidth * viewHeight) * bitsperpixel) / 8;
            initRender(frame_size);
            previewing = true;
            Log.d("camera", "Preview width=" + viewWidth + " Preview height=" + viewHeight + ",view width=" + width + " view height"+ height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/";
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imgPath = mediaStorageDir + "IMG_" + timeStamp + ".jpg";
            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
            image = ImageTransform.rotate(image,90);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(imgPath);
                image.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.close();
                Log.i("Supera", "Save picture to " + imgPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
        }
    };

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(indraw) return;
        indraw = true;
        if (data != null) {
            synchronized(drawHolder) {
                Canvas canvas = drawHolder.lockCanvas();
                Camera.Parameters parameters = camera.getParameters();
                int imageFormat = parameters.getPreviewFormat();
                if (imageFormat == ImageFormat.NV21) {
                    // get full picture

                    /*Rect rect = new Rect(0, 0, w, h);
                    YuvImage img = new YuvImage(data, ImageFormat.NV21, w, h, null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    if (img.compressToJpeg(rect, 100, baos)) {
                        Bitmap image = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
                        image = ImageTransform.rotate(image,90);
                        canvas.drawBitmap(image,0,0,null);
                    }*/
                    Bitmap bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
                    Allocation bmData = renderScriptNV21ToRGBA888(data);
                    bmData.copyTo(bitmap);
                    // bitmap = ImageTransform.rotate(bitmap,90);
                    Mat img = new Mat();
                    Utils.bitmapToMat(bitmap,img);
                    Mat mRotated = img.t();
                    Core.flip(mRotated, mRotated, 1);
                    mRotated = ImageEffect.getEdge(mRotated);
                    img = new Mat(drawWidth,drawHeight,mRotated.type());
                    Imgproc.resize(mRotated,img,new Size(drawWidth,drawHeight));
                    bitmap = Bitmap.createBitmap(img.width(), img.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img,bitmap);
                    canvas.drawBitmap(bitmap,0,0,null);
                }
                camera.addCallbackBuffer(data);
                drawHolder.unlockCanvasAndPost(canvas);
            }
        }
        indraw = false;
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    camera.startPreview();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    public Allocation renderScriptNV21ToRGBA888(byte[] nv21) {
        in.copyFrom(nv21);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        return out;
    }

    private void initRender(int length) {
        rs = RenderScript.create(getBaseContext());
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(length);
        in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(viewWidth).setY(viewHeight);
        out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
    }

}
