package nthu.bobby.supera;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.view.Window;

import java.io.FileNotFoundException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLActivity extends Activity {
    private GLSurfaceView view;
    private EffectsRenderer effect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl);

        view = (GLSurfaceView) findViewById(R.id.effectsview);
        effect = new EffectsRenderer(this);
        view.setEGLContextClientVersion(2);
        view.setRenderer(effect);
        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Intent intent_album = new Intent(Intent.ACTION_PICK);
        intent_album.setType("image/*");
        intent_album.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_album, 0);
    }

    @Override
    /*取得並顯示照片*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0) {
            //用URI取得相簿中的影像
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            Bitmap image;
            try {//讀取照片，型態為Bitmap
                image = BitmapFactory.decodeStream(cr.openInputStream(uri));
                int width = image.getWidth();
                int height = image.getHeight();
                if (width > 630 || height > 1120) {
                    float scale = (width > height) ? width / 630 : height / 1120;
                    image = ImageTransform.resize(image, scale);
                }
                effect.setPhoto(image);
            } catch (FileNotFoundException e) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
