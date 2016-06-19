package nthu.bobby.supera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import static org.opencv.imgproc.Imgproc.COLOR_GRAY2RGB;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2RGB;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class PictureActivity extends Activity implements View.OnClickListener {
    private FuncUI UI;
    private String type;
    private String imgPath;
    private Bitmap image, imgResult, imgOrig;
    private boolean opencvEnable = false;
    private ProgressDialog dialog;

    void opencvLoader(BaseLoaderCallback mLoaderCallback){
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    int Red=0, Green=0, Blue=0;
    public double window_W, window_H;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        UI = new FuncUI(this);
        UI.setToOnClickListener(this);

        // screen window's size
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        window_W = metrics.widthPixels;
        window_H = metrics.heightPixels;

        /*開啟相簿*/
        Intent intent_album = new Intent(Intent.ACTION_PICK);
        intent_album.setType("image/*");
        intent_album.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent_album, REQUEST_IMAGE_SELECT);
        startActivityForResult(intent_album, 0);


        UI.seekBarR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //UI.textView.setText("pr = " + progress);
                Red = progress;
                seekBarAction(image);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        UI.seekBarG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Green = progress;
                seekBarAction(image);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        UI.seekBarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Blue = progress;
                seekBarAction(image);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        FaceProcessor.Init(this);
    }
    void seekBarAction(Bitmap image) {
        Mat imgMat = new Mat();
        Utils.bitmapToMat(image, imgMat);
        imgMat = ImageEffect.setRGB(imgMat, Red, Green, Blue);
        //imgMat = ImageEffect.HSV();
        imgResult = Bitmap.createBitmap(imgMat.width(),imgMat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgMat, imgResult);
        UI.imageView.setImageBitmap(imgResult);
    }
    @Override
    public void onClick(View v) {
        if(opencvEnable) {
            Mat imgMat = new Mat();
            Mat imgMatResult = new Mat();
            image = imgResult;
            Utils.bitmapToMat(image, imgMat);

            switch (v.getId()) {
                case R.id.btnOrig:
                    imgResult = imgOrig.copy(Bitmap.Config.ARGB_8888, true);
                    break;

                case R.id.btnCartoon:
                    imgMatResult = ImageEffect.cartoonEdge(imgMat);
                    Utils.matToBitmap(imgMatResult, imgResult);
                    break;

                case R.id.btnLomo:
                    imgMat.convertTo(imgMatResult,-1,1.5,0);
                    Utils.matToBitmap(imgMatResult, imgResult);
                    /*imgMat = ImageEffect.darkMask(imgMat);
                    imgMatResult = ImageEffect.HSV(imgMat,-3,1.5,0);
                    imgMatResult = ImageEffect.setRGB(imgMatResult,-5,5,10);
                    Utils.matToBitmap(imgMatResult, imgResult);*/
                    /* Cat Ears
                    FaceProcessor.Init(this);
                    Bitmap catEar = BitmapFactory.decodeResource(getResources(),R.drawable.catear);
                    imgResult = FaceProcessor.drawCatEar(image, catEar);*/

                    /*FaceProcessor.Init(this);
                    Bitmap nose = BitmapFactory.decodeResource(getResources(),R.drawable.nose);
                    imgResult = FaceProcessor.drawNose(image, nose);*/
                    /*FaceProcessor.Init(this);
                    Bitmap blush = BitmapFactory.decodeResource(getResources(),R.drawable.blush);
                    imgResult = FaceProcessor.drawBlush(image, blush);*/
                    break;

                case R.id.btnOld:
                    imgMat = ImageEffect.darkMask(imgMat);
                    imgMatResult = ImageEffect.HSV(imgMat, 0, 0.8, 10);
                    imgMatResult = ImageEffect.setRGB(imgMatResult, 5,-5,-5);
                    Utils.matToBitmap(imgMatResult, imgResult);
                    break;

                case R.id.btnRed:
                    //imgResult = FaceProcessor.drawEyes(image);
                    Bitmap mustache = BitmapFactory.decodeResource(getResources(),R.drawable.mustache);
                    imgResult = FaceProcessor.drawMustache(image, mustache);
                    //Utils.matToBitmap(imgMatResult, imgResult);
                    break;
                case R.id.btnGreen:
                    imgResult = FaceProcessor.drawPoints(image);
                    //Utils.matToBitmap(imgMatResult, imgResult);
                    break;

                case R.id.btnBlue:
                    imgResult = FaceProcessor.drawEyesMosaic(image);
                    //Imgproc.cvtColor(imgMat, imgMatResult, Imgproc.COLOR_RGB2GRAY);
                    //imgMatResult = ImageEffect.setRGB(imgMatResult, 0, 0, 18);
                    //Utils.matToBitmap(imgMatResult, imgResult);
                    break;

                case R.id.btnEdge:
                    //Imgproc.Canny(imgMat,imgMatResult,123,250);
                    imgMatResult = ImageEffect.darkMask(imgMat);
                    Utils.matToBitmap(imgMatResult, imgResult);
                    break;

                case R.id.btnBlur:
                    imgMatResult = ImageEffect.cartoonize(imgMat);
                    // Imgproc.GaussianBlur(imgMat, imgMatResult, new Size(17,17), 11, 11);
                    imgResult = Bitmap.createBitmap(imgMatResult.width(),imgMatResult.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(imgMatResult, imgResult);
                    break;

                case R.id.btnRGB:
                    UI.viewAnimator.showNext();
                    //UI.textView.setText(UI.seekBarR.getProgress());
                    break;

                case R.id.btnEnhance:
                    imgMatResult = ImageEffect.Enhancement(imgMat);
                    Utils.matToBitmap(imgMatResult, imgResult);
                    break;

                case R.id.btnPencil:
                    imgMatResult = ImageEffect.pencil(imgMat);
                    Utils.matToBitmap(imgMatResult, imgResult);
                    break;

                case R.id.btnGray:
                    Imgproc.cvtColor(imgMat, imgMatResult, Imgproc.COLOR_RGB2GRAY);
                    Utils.matToBitmap(imgMatResult, imgResult);
                    break;
            }
            UI.imageView.setImageBitmap(imgResult);
        }
    }


    @Override
    /*取得並顯示照片*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0)
        {
            //用URI取得相簿中的影像
             Uri uri = data.getData();
             ContentResolver cr = this.getContentResolver();

             try {//讀取照片，型態為Bitmap
                 image = BitmapFactory.decodeStream(cr.openInputStream(uri));
                 int width = image.getWidth();
                 int height = image.getHeight();
                 if (width > 630 || height > 1120) {
                     float scale = (width > height) ? width / 630 : height / 1120;
                     image = ImageTransform.resize(image, scale);
                 }
                 imgOrig = image.copy(Bitmap.Config.ARGB_8888, true);
                 UI.imageView.setImageBitmap(image);
                 imgResult = image;
             }
             catch (FileNotFoundException e)
             {
             }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    opencvEnable = true;
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

}