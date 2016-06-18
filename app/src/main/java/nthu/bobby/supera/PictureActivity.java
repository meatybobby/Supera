package nthu.bobby.supera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.opencv.imgproc.Imgproc.COLOR_RGBA2RGB;
import static org.opencv.imgproc.Imgproc.CV_RGBA2mRGBA;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.ellipse;

public class PictureActivity extends Activity implements View.OnClickListener {
    private FuncUI UI;
    private String type;
    private String imgPath;
    private Bitmap image, imgResult;
    private Bitmap imgCanny, imgBlur;
    private boolean opencvEnable = false;

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
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //UI.textView.setText("pr = " + progress);
                Red = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                imgResult = ImageEffect.changeRGB(image, Red, Green, Blue);
                UI.imageView.setImageBitmap(imgResult);
            }
        });

        UI.seekBarG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Green = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                imgResult = ImageEffect.changeRGB(image, Red, Green, Blue);
                UI.imageView.setImageBitmap(imgResult);
            }
        });
        UI.seekBarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Blue = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                imgResult = ImageEffect.changeRGB(image, Red, Green, Blue);
                UI.imageView.setImageBitmap(imgResult);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*Using openCV*/
            case R.id.btnCanny:
                UI.textView.setText("Start!");

                //Imgproc.adaptiveThreshold(imgMat,imgMatResult,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,11,2);

                if(opencvEnable){
                    Mat imgMat = new Mat();
                    Mat imgMatResult = new Mat();

                    image = imgResult;
                    Utils.bitmapToMat(image, imgMat);

                    Imgproc.Canny(imgMat,imgMatResult,123,250);
                    Utils.matToBitmap(imgMatResult, imgResult);
                }
                UI.imageView.setImageBitmap(imgResult);
            break;

            case R.id.btnBlur:
                if(opencvEnable){
                    Mat imgMat = new Mat();
                    Mat imgMatResult = new Mat();

                    image = imgResult;
                    Utils.bitmapToMat(image, imgMat);
                    Imgproc.GaussianBlur(imgMat, imgMatResult, new Size(17,17), 11, 11);
                    Utils.matToBitmap(imgMatResult, imgResult);
                }
                UI.imageView.setImageBitmap(imgResult);

            break;
            case R.id.button:
                if(opencvEnable) {
                    Mat imgMat = new Mat();
                    Mat imgMatResult = new Mat();
                    image = imgResult;
                    Utils.bitmapToMat(image, imgMat);
                    Log.i("tzutzu", "imgMat type = "+ imgMat.type() );

                    Point p = new Point(imgMat.cols()/2, imgMat.rows()/2);
                    Mat dark = new Mat(imgMat.rows(), imgMat.cols(), imgMat.type()/*CvType.CV_32FC4*/, new Scalar(50,50,50));
                    //Imgproc.cvtColor(dark, dark, Imgproc.COLOR_BGR2BGRA);
                    //dark.convertTo(dark, CvType.CV_8UC4);
                    //circle(dark, p, imgMat.cols()/2, new Scalar(255,255,255), -1);
                    Size axes = new Size(imgMat.cols()/2, imgMat.rows()/2);
                    ellipse(dark, p, axes, 0, 0, 360, new Scalar(255,255,255), -1);
                    Log.i("tzutzu", "dark type QQ= "+ dark.type() );
                    Utils.matToBitmap(dark, imgResult);
                }
                UI.imageView.setImageBitmap(imgResult);

                break;

            /*Implement*/
            case R.id.btnRGB:
                image = imgResult;
                UI.viewAnimator.showNext();
                //UI.textView.setText(UI.seekBarR.getProgress());
                break;
            case R.id.btnGray:
                image = imgResult;
                if(opencvEnable){
                    Mat imgMat = new Mat();
                    Mat imgMatResult = new Mat();
                    Utils.bitmapToMat(image, imgMat);
                    Imgproc.cvtColor(imgMat, imgMatResult, Imgproc.COLOR_RGB2GRAY);
                    Utils.matToBitmap(imgMatResult, imgResult);
                }
                UI.imageView.setImageBitmap(imgResult);
                break;
            case  R.id.btnEnhance:
                image = imgResult;
                imgResult = ImageEffect.Enhancement(image);
                UI.imageView.setImageBitmap(imgResult);
                break;
            case R.id.btnMosaic:
                image = imgResult;
                imgResult = ImageEffect.Mosaic(image);
                UI.imageView.setImageBitmap(imgResult);
                break;

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
             }
             catch (FileNotFoundException e)
             {
             }

            UI.imageView.setImageBitmap(image);
            imgResult = image;
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