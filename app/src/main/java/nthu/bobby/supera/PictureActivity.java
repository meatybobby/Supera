package nthu.bobby.supera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import static org.opencv.imgproc.Imgproc.COLOR_GRAY2RGB;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2RGB;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class PictureActivity extends Activity implements View.OnClickListener {
    private FuncUI UI;
    private Bitmap imgCurrent, imgShow, imgOrig;
    private boolean opencvEnable = false;
    public ImageButton btnConfirm, btnCancel;
    public double window_W, window_H;
    private String mode;

    void opencvLoader(BaseLoaderCallback mLoaderCallback) {
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

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
        mode = "none";

        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("path");
        if (path != null) {
            getPictureFromPath(path);
        } else {
        /*開啟相簿*/
            Intent intent_album = new Intent(Intent.ACTION_PICK);
            intent_album.setType("image/*");
            intent_album.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent_album, 0);
        }

        FaceProcessor.Init(this);
        btnConfirm = (ImageButton) findViewById(R.id.btn_confirm_action);
        btnCancel = (ImageButton) findViewById(R.id.btn_cancel_action);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePicture();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        UI.effectSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //UI.textView.setText("pr = " + progress);
                seekBarMotion(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void seekBarMotion(int score) {
        if (opencvEnable) {
            Mat imgMat = new Mat();
            Mat imgMatResult = new Mat();
            Utils.bitmapToMat(imgCurrent, imgMat);

            switch (mode) {
                case "lomo":
                    //1:1.5, 50:2, 100:15
                    imgMatResult = ImageEffect.lomo(imgMat, 1 - score / 100f);
                    imgShow = Bitmap.createBitmap(imgMatResult.width(), imgMatResult.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(imgMatResult, imgShow);
                    break;
                case "cartoon":
                    imgMatResult = ImageEffect.cartoonEdge(imgMat);
                    imgShow = Bitmap.createBitmap(imgMatResult.width(), imgMatResult.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(imgMatResult, imgShow);
                    break;
                case "old":
                    imgMatResult = ImageEffect.oldEffect(imgMat);
                    imgShow = Bitmap.createBitmap(imgMatResult.width(), imgMatResult.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(imgMatResult, imgShow);
                    break;
                case "edge":
                    imgMatResult = ImageEffect.getEdge(imgMat);
                    imgShow = Bitmap.createBitmap(imgMatResult.width(), imgMatResult.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(imgMatResult, imgShow);
                    break;
                case "blur":
                    imgMatResult = ImageEffect.cartoonize(imgMat);
                    // Imgproc.GaussianBlur(imgMat, imgMatResult, new Size(17,17), 11, 11);
                    imgShow = Bitmap.createBitmap(imgMatResult.width(), imgMatResult.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(imgMatResult, imgShow);
                    break;
                case "pencil":
                    imgMatResult = ImageEffect.pencil(imgMat);
                    imgShow = Bitmap.createBitmap(imgMatResult.width(), imgMatResult.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(imgMatResult, imgShow);
                    break;
                case "enhance":
                    //imgShow = ImageEffect.Enhancement(imgCurrent);
                    break;
                case "gray":
                    Imgproc.cvtColor(imgMat, imgMatResult, Imgproc.COLOR_RGB2GRAY);
                    imgShow = Bitmap.createBitmap(imgMatResult.width(), imgMatResult.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(imgMatResult, imgShow);
                    break;
            }
            UI.imageView.setImageBitmap(imgShow);
        }
    }

    @Override
    public void onClick(View v) {
        if (opencvEnable) {
            switch (v.getId()) {
                case R.id.btnOrig:
                    mode = "none";
                    imgShow = imgOrig.copy(Bitmap.Config.ARGB_8888, true);
                    imgCurrent = imgOrig.copy(Bitmap.Config.ARGB_8888, true);
                    break;

                case R.id.btnCartoon:
                    UI.viewAnimator.showNext();
                    mode = "cartoon";
                    break;

                case R.id.btnLomo:
                    UI.viewAnimator.showNext();
                    mode = "lomo";
                    break;

                case R.id.btnOld:
                    UI.viewAnimator.showNext();
                    mode = "old";
                    break;

                case R.id.btnRed:
                    //imgResult = FaceProcessor.drawEyes(image);
                    //Bitmap mustache = BitmapFactory.decodeResource(getResources(),R.drawable.mustache);
                    //imgResult = FaceProcessor.drawMustache(image, mustache);
                    //Utils.matToBitmap(imgMatResult, imgResult);
                    break;
                case R.id.btnGreen:
                    //imgResult = FaceProcessor.drawPoints(image);
                    //Utils.matToBitmap(imgMatResult, imgResult);
                    break;

                case R.id.btnBlue:
                    //imgResult = FaceProcessor.drawEyesMosaic(image);
                    //Imgproc.cvtColor(imgMat, imgMatResult, Imgproc.COLOR_RGB2GRAY);
                    //imgMatResult = ImageEffect.setRGB(imgMatResult, 0, 0, 18);
                    //Utils.matToBitmap(imgMatResult, imgResult);
                    break;

                case R.id.btnEdge:
                    UI.viewAnimator.showNext();
                    mode = "edge";
                    break;

                case R.id.btnBlur:
                    UI.viewAnimator.showNext();
                    mode = "blur";
                    break;

                case R.id.btnRGB:
                    UI.viewAnimator.showNext();
                    //UI.textView.setText(UI.seekBarR.getProgress());
                    break;

                case R.id.btnEnhance:
                    UI.viewAnimator.showNext();
                    mode = "enhance";
                    break;

                case R.id.btnPencil:
                    UI.viewAnimator.showNext();
                    mode = "pencil";
                    break;

                case R.id.btnGray:
                    UI.viewAnimator.showNext();
                    mode = "gray";
                    break;
            }
            UI.imageView.setImageBitmap(imgShow);
        }
    }


    @Override
    /*取得並顯示照片*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0) {
            //用URI取得相簿中的影像
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();

            try {//讀取照片，型態為Bitmap
                imgCurrent = BitmapFactory.decodeStream(cr.openInputStream(uri));
            } catch (FileNotFoundException e) {
            }

            // resize
            int width = imgCurrent.getWidth();
            int height = imgCurrent.getHeight();
            if (width > 630 || height > 1120) {
                float scale = (width > height) ? width / 630 : height / 1120;
                imgCurrent = ImageTransform.resize(imgCurrent, scale);
            }
            imgOrig = imgCurrent.copy(Bitmap.Config.ARGB_8888, true);
            UI.imageView.setImageBitmap(imgCurrent);
            imgShow = imgCurrent;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    opencvEnable = true;
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    private void savePicture() {
        String mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgPath = mediaStorageDir + "IMG_" + timeStamp + ".jpg";
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imgPath);
            imgShow.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getPictureFromPath(String path) {
        imgCurrent = BitmapFactory.decodeFile(path);
        // resize
        int width = imgCurrent.getWidth();
        int height = imgCurrent.getHeight();
        if (width > 630 || height > 1120) {
            float scale = (width > height) ? width / 630 : height / 1120;
            imgCurrent = ImageTransform.resize(imgCurrent, scale);
        }
        imgOrig = imgCurrent.copy(Bitmap.Config.ARGB_8888, true);
        UI.imageView.setImageBitmap(imgCurrent);
        imgShow = imgCurrent;
    }
}