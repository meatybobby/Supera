package nthu.bobby.supera;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

/**
 * Created by Bobby on 2016/6/17.
 */
public class FaceProcessor {
    private static FaceDetector detector;
    public static void Init(Context context) {
        if(detector!=null) detector.release();
        detector = new FaceDetector.Builder(context).setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        if (!detector.isOperational()) {
            //new AlertDialog.Builder(context).setMessage("Face detector fail!").show();
            Log.e("Fatal error","Can't load detector");
        }
    }

    public static Bitmap drawEyes(Bitmap bitmap) {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        Bitmap canvasBmp = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(canvasBmp);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x);
                int cy = (int) (landmark.getPosition().y);
                canvas.drawCircle(cx, cy, 10, paint);
            }
        }
        canvas.drawRect(2,100,100,2,paint);
        Log.i("HHH"," " + faces.size());
        return canvasBmp;
    }
}
