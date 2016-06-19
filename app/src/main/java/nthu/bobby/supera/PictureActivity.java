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
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureActivity extends Activity implements View.OnClickListener {
    private FuncUI UI;
    private Bitmap imgCurrent, imgShow, imgOrig;
    private boolean opencvEnable = false;
    public ImageButton btnConfirm, btnCancel;
    public double window_W, window_H;
    private String mode;
    private boolean noSeekBar;

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
		FaceProcessor.Init(this);
		mode = "none";

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            String path = bundle.getString("path");
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

        UI.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgCurrent =  imgShow.copy(Bitmap.Config.ARGB_8888, true);
                if(!noSeekBar) {
                    UI.viewAnimator.showNext();
                }
                UI.statusBar.showNext();
            }
        });
        UI.btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgShow =  imgCurrent;
                if(!noSeekBar) {
                    UI.viewAnimator.showNext();
                }
                UI.statusBar.showNext();
                UI.imageView.setImageBitmap(imgShow);
            }
        });
        UI.btnFaceDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UI.faceMenu.showNext();
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

    private void seekBarMotion(int score){
        if(opencvEnable) {
            score = score*2-100;
            String status = mode + " " + score;
            UI.effectView.setText(status);

            Mat imgMat = new Mat();
            Mat imgMatResult = new Mat();
            Utils.bitmapToMat(imgCurrent, imgMat);
            boolean bitmap = false;

            switch (mode) {
                case "lomo":
                    //1:1.5, 50:2, 100:15
                    imgMatResult = ImageEffect.lomo(imgMat, 1 - score / 100f);
                    imgShow = Bitmap.createBitmap(imgMatResult.width(), imgMatResult.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(imgMatResult, imgShow);
                    break;
                case "Cartoon Style":
                    imgMatResult = ImageEffect.cartoonEdge(imgMat);
                    break;
                case "Edge Detect":
                    imgMatResult = ImageEffect.getEdge(imgMat);
                    break;
                case "Blur":
                    imgMatResult = ImageEffect.cartoonize(imgMat);
                    // Imgproc.GaussianBlur(imgMat, imgMatResult, new Size(17,17), 11, 11);
                    break;
                case "Pencil":
                    imgMatResult = ImageEffect.pencil(imgMat);
                    break;
                case "LOMO":
                    score = (score+100)/2;
                    imgMatResult = ImageEffect.lomo(imgMat, 1-score/100f);
                    break;
                case "Old Style":
                    imgMatResult = ImageEffect.oldEffect(imgMat, score/6, score/12, score/10);
                    break;
                case "Mosaic":
                    imgShow = ImageEffect.Mosaic(imgCurrent, (int)(score/2+75));
                    bitmap = true;
                    break;
                case "Enhance":
                    imgMatResult = ImageEffect.Enhancement(imgMat, 1.5f+(score/150f));
                    break;
                case "Red":
                    imgMatResult = ImageEffect.setRGB(imgMat, (int)(score*1.5), 0, 0);
                    break;
                case "Green":
                    imgMatResult = ImageEffect.setRGB(imgMat, 0, (int)(score*1.5), 0);
                    break;
                case "Blue":
                    imgMatResult = ImageEffect.setRGB(imgMat, 0, 0, (int)(score*1.5));
                    break;
                case "Hue":
                    imgMatResult = ImageEffect.HSV(imgMat, (int)(score*0.9f), 1, 0);
                    break;
                case "Saturation":
                    imgMatResult = ImageEffect.HSV(imgMat, 0, 1-(score/100f), 0);
                    break;
				case "Cat Ear":
                    Bitmap catEar = BitmapFactory.decodeResource(getResources(),R.drawable.catear);
                    imgShow = FaceProcessor.drawCatEar(imgCurrent, catEar);
					bitmap = true;
                    break;
				case "Blush":
					Bitmap blush = BitmapFactory.decodeResource(getResources(),R.drawable.blush);
                    imgShow = FaceProcessor.drawBlush(imgCurrent, blush);
					bitmap = true;
					break;
				case "Mustache":
					Bitmap mustache = BitmapFactory.decodeResource(getResources(),R.drawable.mustache);
                    imgShow = FaceProcessor.drawMustache(imgCurrent, mustache);
					bitmap = true;
					break;
				case "Nose":
					Bitmap nose = BitmapFactory.decodeResource(getResources(),R.drawable.nose);
                    imgShow = FaceProcessor.drawNose(imgCurrent, nose);
					bitmap = true;
					break;
            }
            if(!bitmap) {
                imgShow = Bitmap.createBitmap(imgMatResult.width(), imgMatResult.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(imgMatResult, imgShow);
            }
            UI.imageView.setImageBitmap(imgShow);
        }
    }

    private void effectSelect(){
        if(!noSeekBar) {
            UI.viewAnimator.showNext();
        }
        UI.statusBar.showNext();
        UI.effectView.setText(mode);
        UI.effectSeekBar.setProgress(50);
        seekBarMotion(50);
    }

    @Override
    public void onClick(View v) {
        noSeekBar = false;
        if(opencvEnable) {
            switch (v.getId()) {
                case R.id.btnOrig:
                    mode = "none";
                    imgShow = imgOrig.copy(Bitmap.Config.ARGB_8888, true);
					imgCurrent = imgOrig.copy(Bitmap.Config.ARGB_8888, true);
                    UI.imageView.setImageBitmap(imgShow);
                    break;

                case R.id.btnCartoon:
                    mode = "Cartoon Style"; noSeekBar = true;
                    break;
                case R.id.btnEdge:
                    mode = "Edge Detect"; noSeekBar = true;
                    break;
                case R.id.btnBlur:
                    mode = "Blur"; noSeekBar = true;
                    break;
                case R.id.btnPencil:
                    mode = "Pencil"; noSeekBar = true;
                    break;
                case R.id.btnLomo:
					mode = "LOMO";
                    break;
                case R.id.btnOld:
                    mode = "Old Style";
                    break;
                case R.id.btnMosaic:
                    mode = "Mosaic";
                    break;
                case R.id.btnRed:
                    mode = "Red";
                    break;
                case R.id.btnGreen:
                    mode = "Green";
                    break;
                case R.id.btnBlue:
                    mode = "Blue";
                    break;
                case R.id.btnHSV:
                    mode = "Hue";
                    break;
                case R.id.btnEnhance:
					mode = "Enhance";
                    break;
                case R.id.btnGray:
                    mode = "Saturation";
                    break;
				case R.id.btn_catear:
                    mode = "Cat Ear"; noSeekBar = true;
                    break;
				case R.id.btn_blush:
                    mode = "Blush"; noSeekBar = true;
                    break;
				case R.id.btn_mustache:
                    mode = "Mustache"; noSeekBar = true;
                    break;
				case R.id.btn_nose:
                    mode = "Nose"; noSeekBar = true;
                    break;
            }
            if(!mode.equals("none")) {
                effectSelect();
            }
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
            imgCurrent.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
            Log.i("path", imgPath);
            Toast.makeText(getApplication(), "Picture saved.\n"+imgPath, Toast.LENGTH_LONG).show();
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