package nthu.bobby.supera;

import android.app.Activity;
import android.hardware.Camera;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraActivity extends Activity {
    private Camera camera;
    private SurfaceView previewView;
    private SurfaceHolder previewHolder;
    private boolean previewing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        previewView = (SurfaceView)findViewById(R.id.surfaceView);
        previewHolder = previewView.getHolder();
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        previewHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                camera = Camera.open();
                camera.setDisplayOrientation(90);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if(previewing) {
                    camera.stopPreview();
                    previewing = false;
                }
                try {
                    camera.setPreviewDisplay(holder);
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFocusMode("auto");
                    camera.setParameters(parameters);
                    camera.startPreview();
                    previewing = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camera.stopPreview();
                camera.release();
                camera = null;
                previewing = false;
            }
        });
    }
}
