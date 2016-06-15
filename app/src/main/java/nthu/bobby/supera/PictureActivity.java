package nthu.bobby.supera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureActivity extends Activity implements View.OnClickListener {
    private FuncUI UI;
    private String type;
    private String imgPath;
    private Bitmap image;
    public double window_W, window_H;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        UI = new FuncUI(this);
        UI.setToOnClickListener(this);

        // screen window's size
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        window_W = metrics.widthPixels;
        window_H = metrics.heightPixels;


        /*Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imgPath = "IMG" + timeStamp + ".jpg";
        File tmpFile = new File(Environment.getExternalStorageDirectory(), imgPath);
        intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
        startActivityForResult(intent_camera, 0);*/

        //Intent intent = this.getIntent();
        //String type = intent.getStringExtra("type");
        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");

        TextView tv = (TextView)findViewById(R.id.textView);
        tv.setText(type);
        if(type.equals("camera")) {

        }
        else if(type.equals("album")) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    /*取得並顯示照片*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0)
        {
            //利用BitmapFactory取得剛剛拍照的影像
            if(type.equals("camera")) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imgPath, bmOptions);
                // Determine how much to scale down the image
                double photoW = bmOptions.outWidth;
                double photoH = bmOptions.outHeight *0.6;
                int scaleFactor = (int)Math.ceil(Math.max((photoW / window_W), (photoH / window_W)));

                // Decode the image file into a Bitmap sized to fill the View
                image = BitmapFactory.decodeFile(imgPath);
                image = ImageTransform.resize(image, scaleFactor);
            }
            //用URI取得相簿中的影像
            else{
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();

                try {//讀取照片，型態為Bitmap
                    image = BitmapFactory.decodeStream(cr.openInputStream(uri));
                }
                catch (FileNotFoundException e)
                {
                }
            }
            UI.imageView.setImageBitmap(image);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}