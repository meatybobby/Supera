package nthu.bobby.supera;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.FileNotFoundException;

public class BlendActivity extends AppCompatActivity {
    private Button srcButton,dstButton;
    private ImageView imageView;
    private Bitmap srcBitmap,dstBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blend);
        srcButton = (Button) findViewById(R.id.src_button);
        dstButton = (Button) findViewById(R.id.dst_button);
        imageView = (ImageView) findViewById(R.id.imageView);
        srcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(0);
            }
        });
        dstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(1);
            }
        });
    }

    private void selectImage(int code) {
        Intent intent_album = new Intent(Intent.ACTION_PICK);
        intent_album.setType("image/*");
        intent_album.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent_album, REQUEST_IMAGE_SELECT);
        startActivityForResult(intent_album, code);
    }

    private void blend() {
        Mat srcMat = new Mat();
        Mat dstMat = new Mat();
        Mat result = new Mat();
        Utils.bitmapToMat(srcBitmap,srcMat);
        Utils.bitmapToMat(dstBitmap,dstMat);
        Point center = new Point(dstMat.cols()/2,dstMat.rows()/2);
        result = ImageEffect.imageClone(srcMat,dstMat,center);
        Bitmap resultImg = Bitmap.createBitmap(result.width(),result.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(result,resultImg);
        imageView.setImageBitmap(resultImg);
    }

    @Override
    /*取得並顯示照片*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //用URI取得相簿中的影像
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {//讀取照片，型態為Bitmap
                if(requestCode == 0) {
                    srcBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    blend();
                } else {
                    dstBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                }
            } catch (FileNotFoundException e) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
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
}
