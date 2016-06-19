package nthu.bobby.supera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;

public class PictureActivity extends Activity implements View.OnClickListener {
    private FuncUI UI;
    private Bitmap imgCurrent, imgShow, imgOrig;
    private boolean opencvEnable = false;
    public ImageButton btnConfirm, btnCancel;
	public double window_W, window_H;
    private String mode;

    void opencvLoader(BaseLoaderCallback mLoaderCallback){
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

        /*開啟相簿*/
        Intent intent_album = new Intent(Intent.ACTION_PICK);
        intent_album.setType("image/*");
        intent_album.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_album, 0);

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

    private void seekBarMotion(int score){
        if(opencvEnable) {
            Mat imgMat = new Mat();
            Mat imgMatResult = new Mat();
            Utils.bitmapToMat(imgCurrent, imgMat);

            switch(mode) {
                case "lomo":
                    //1:1.5, 50:2, 100:15
                    imgMatResult = ImageEffect.lomo(imgMat, 1-score/100f);
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
        if(opencvEnable) {
            switch (v.getId()) {
                case R.id.btnOrig:
					mode = "none";
                    imgShow = imgOrig.copy(Bitmap.Config.ARGB_8888, true);
					imgCurrent = imgOrig.copy(Bitmap.Config.ARGB_8888, true);
                    break;

                case R.id.btnCartoon:
					UI.viewAnimator.showNext(); mode = "cartoon";
                    break;

                case R.id.btnLomo:
					UI.viewAnimator.showNext(); mode = "lomo";
                    break;

                case R.id.btnOld:
                    UI.viewAnimator.showNext(); mode = "old";
                    break;

                case R.id.btnRed:
                    //imgMatResult = ImageEffect.HSV(imgMat, 0, 1.5, 0);
                    //Utils.matToBitmap(imgMatResult, imgShow);

                    break;
                case R.id.btnGreen:
                    //imgMatResult = ImageEffect.HSV(imgMat, 0, -100, 10);
                    /*Imgproc.cvtColor(imgMat, imgMatResult, Imgproc.COLOR_RGB2GRAY);
                    Utils.matToBitmap(imgMatResult, imgShow);
                    imgShow = ImageEffect.set(imgShow, 0, 15, 0);*/
                    break;

                case R.id.btnBlue:
                    //imgMatResult = ImageEffect.HSV(imgMat, 0, -100, 10);
                    /*Imgproc.cvtColor(imgMat, imgMatResult, Imgproc.COLOR_RGB2GRAY);
                    Utils.matToBitmap(imgMatResult, imgShow);
                    imgShow = ImageEffect.changeRGB(imgShow, 0, 0, 15);*/
                    break;

                case R.id.btnEdge:
                    UI.viewAnimator.showNext(); mode = "edge";
                    break;

                case R.id.btnBlur:
					UI.viewAnimator.showNext(); mode = "blur";
                    break;

                case R.id.btnRGB:
                    UI.viewAnimator.showNext();
                    //UI.textView.setText(UI.seekBarR.getProgress());
                    break;

                case R.id.btnEnhance:
					UI.viewAnimator.showNext(); mode = "enhance";
                    break;

                case R.id.btnPencil:
					UI.viewAnimator.showNext(); mode = "pencil";
                    break;

                case R.id.btnGray:
                    UI.viewAnimator.showNext(); mode = "gray";
                    break;
            }
            UI.imageView.setImageBitmap(imgShow);
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
                 imgCurrent = BitmapFactory.decodeStream(cr.openInputStream(uri));
             }
             catch (FileNotFoundException e)
             {}

            // resize
            int width = imgCurrent.getWidth();
            int height = imgCurrent.getHeight();
            if(width > 630 || height > 1120) {
                float scale = (width>height)? width/630 : height/1120;
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

    private void savePicture(){

    }

}