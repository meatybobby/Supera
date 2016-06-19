package nthu.bobby.supera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    public ImageButton btnCamera, btnAlbum, btnEffects;
    public Button btn_previewNone, btn_previewPencil, btn_previewBlur, btn_previewEdge;
    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;
    public ViewAnimator previewAnimator;
    private String previewMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        previewMode = "none";

        previewAnimator = (ViewAnimator)findViewById(R.id.previewAnimator);
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

    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("OpenCV", "called onCreateOptionsMenu");
        mItemSwitchCamera = menu.add("Toggle Native/Java camera");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String toastMesage = new String();
        Log.i("OpenCV", "called onOptionsItemSelected; selected item: " + item);

        if (item == mItemSwitchCamera) {
            mOpenCvCameraView.setVisibility(SurfaceView.GONE);
            mIsJavaCamera = !mIsJavaCamera;

            if (mIsJavaCamera) {
                mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);
                toastMesage = "Java Camera";
            }
            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
            mOpenCvCameraView.setCvCameraViewListener(this);
            mOpenCvCameraView.enableView();
            Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
            toast.show();

        }

        return true;
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mRgba = inputFrame.rgba();
        switch(previewMode){
            case "edge":
                mRgba = ImageEffect.getEdge(mRgba);
                break;
            case "cartoon":
                mRgba = ImageEffect.cartoonEdge(mRgba);
                break;
            case "pencil":
                mRgba = ImageEffect.pencil(mRgba);
                break;
            case "blur":
                mRgba = ImageEffect.cartoonize(mRgba);
                break;
            default:
        }

        return mRgba;
}

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private void getPicture() {
        Intent intent = new Intent(MainActivity.this, PictureActivity.class);
        startActivity(intent);
    }

}