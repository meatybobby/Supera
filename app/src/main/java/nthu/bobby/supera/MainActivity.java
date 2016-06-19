package nthu.bobby.supera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewAnimator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
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

public class MainActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private Camera camera;
    private SurfaceView previewView;
    private SurfaceHolder previewHolder;
    private boolean previewing = false;
    private Button takeButton;
    private SurfaceView drawView;
    private SurfaceHolder drawHolder;
    private int viewHeight, viewWidth;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private RenderScript rs;
    private Allocation in, out;
    private boolean indraw = false;
    private int drawWidth, drawHeight;
    public ImageButton btnCamera, btnAlbum, btnEffects;
    public Button btn_previewNone, btn_previewPencil, btn_previewBlur, btn_previewEdge;
    public ViewAnimator previewAnimator;
    private String previewMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        drawView = (SurfaceView) findViewById(R.id.imageView);
        drawHolder = drawView.getHolder();
        drawView.setZOrderMediaOverlay(true);

        previewView = (SurfaceView) findViewById(R.id.surfaceView);
        previewHolder = previewView.getHolder();
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        previewHolder.addCallback(this);
        previewMode = "none";

        previewAnimator = (ViewAnimator) findViewById(R.id.previewAnimator);
        btnAlbum = (ImageButton) findViewById(R.id.btnAlbum);
        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        btnEffects = (ImageButton) findViewById(R.id.btnEffects);

        btn_previewNone = (Button) findViewById(R.id.btn_previewNone);
        btn_previewPencil = (Button) findViewById(R.id.btn_previewPencil);
        btn_previewBlur = (Button) findViewById(R.id.btn_previewBlur);
        btn_previewEdge = (Button) findViewById(R.id.btn_previewEdge);

        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
            }
        });
        btnEffects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewAnimator.showNext();
            }
        });

        btn_previewNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewAnimator.showNext();
                previewMode = "none";
            }
        });
        btn_previewPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewAnimator.showNext();
                previewMode = "pencil";
            }
        });
        btn_previewBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewAnimator.showNext();
                previewMode = "blur";
            }
        });
        btn_previewEdge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewAnimator.showNext();
                previewMode = "edge";
            }
        });
    }

    public void onPause() {
        super.onPause();
        camera.stopPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    camera.startPreview();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private void getPicture() {
        Intent intent = new Intent(MainActivity.this, PictureActivity.class);
        startActivity(intent);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/";
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imgPath = mediaStorageDir + "IMG_" + timeStamp + ".jpg";
            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
            image = ImageTransform.rotate(image, 90);
            Mat img = new Mat();
            Utils.bitmapToMat(image,img);
            switch (previewMode) {
                case "none":
                    break;
                case "blur":
                    img = ImageEffect.cartoonize(img);
                    break;
                case "edge":
                    img = ImageEffect.getEdge(img);
                    break;
                case "pencil":
                    img = ImageEffect.pencil(img);
                    break;
            }
            image = Bitmap.createBitmap(img.width(),img.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(img,image);
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
            Intent intent = new Intent(MainActivity.this,PictureActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("path",imgPath);
            intent.putExtras(bundle);
            startActivity(intent);
            camera.startPreview();
        }
    };

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
            for (Camera.Size s : previewSizes) {
                if (s.width <= 900 && s.height <= 700) {
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
            Log.d("Supera", "Preview width=" + viewWidth + " Preview height=" + viewHeight + ",view width=" + width + " view height" + height);
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
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

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (indraw) return;
        indraw = true;
        if (data != null) {
            synchronized (drawHolder) {
                Canvas canvas = drawHolder.lockCanvas();
                Camera.Parameters parameters = camera.getParameters();
                int imageFormat = parameters.getPreviewFormat();
                if (imageFormat == ImageFormat.NV21) {
                    Bitmap bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
                    Allocation bmData = renderScriptNV21ToRGBA888(data);
                    bmData.copyTo(bitmap);
                    // bitmap = ImageTransform.rotate(bitmap,90);
                    Mat img = new Mat();
                    Utils.bitmapToMat(bitmap, img);
                    Mat mRotated = img.t();
                    Core.flip(mRotated, mRotated, 1);
                    switch (previewMode) {
                        case "none":
                            break;
                        case "pencil":
                            mRotated = ImageEffect.pencil(mRotated);
                            break;
                        case "blur":
                            mRotated = ImageEffect.cartoonize(mRotated);
                            break;
                        case "edge":
                            mRotated = ImageEffect.getEdge(mRotated);
                            break;
                    }
                    img = new Mat(drawWidth, drawHeight, mRotated.type());
                    Imgproc.resize(mRotated, img, new Size(drawWidth, drawHeight));
                    bitmap = Bitmap.createBitmap(img.width(), img.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img, bitmap);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                }
                camera.addCallbackBuffer(data);
                drawHolder.unlockCanvasAndPost(canvas);
            }
        }
        indraw = false;
    }

    private void initRender(int length) {
        rs = RenderScript.create(getBaseContext());
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(length);
        in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(viewWidth).setY(viewHeight);
        out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
    }

    public Allocation renderScriptNV21ToRGBA888(byte[] nv21) {
        in.copyFrom(nv21);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        return out;
    }
}