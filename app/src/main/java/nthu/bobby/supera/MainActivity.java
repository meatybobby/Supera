package nthu.bobby.supera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    public ImageButton btnCamera, btnAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAlbum = (ImageButton) findViewById(R.id.btnAlbum);
        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
        Bitmap mustache = BitmapFactory.decodeResource(getResources(),R.drawable.mustache);
    }

    private void getPicture() {
        Intent intent = new Intent(MainActivity.this, PictureActivity.class);
        startActivity(intent);
    }

}