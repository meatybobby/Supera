package nthu.bobby.supera;


import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import org.opencv.imgproc.Imgproc;

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
    public static Bitmap drawPoints(Bitmap bitmap) {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        Bitmap canvasBmp = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(canvasBmp);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        int eye1x, eye1y, eye2x, cheek;
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x);
                int cy = (int) (landmark.getPosition().y);
                canvas.drawCircle(cx, cy, 10, paint);
            }
        }
        Log.i("HHH"," " + faces.size());
        return canvasBmp;
    }
    public static Bitmap drawEyes(Bitmap bitmap) {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        Bitmap canvasBmp = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(canvasBmp);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        int eye1x, eye1y, eye2x, cheek;
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            eye1x = (int) face.getLandmarks().get(0).getPosition().x;
            eye1y = (int) face.getLandmarks().get(0).getPosition().y;
            eye2x = (int) face.getLandmarks().get(1).getPosition().x;
            cheek = (int) face.getLandmarks().get(4).getPosition().y;
            int width = (eye2x-eye1x)/2;
            int height = (cheek-eye1y)/2;
            canvas.drawRect(eye1x-width, eye1y-height, eye2x+width, eye1y+height, paint);
        }
        Log.i("HHH"," " + faces.size());
        return canvasBmp;
    }

    public static Bitmap drawEyesMosaic(Bitmap bitmap) {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        Bitmap canvasBmp = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(canvasBmp);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        int eye1x, eye1y, eye2x, cheek;
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);

            eye1x = (int) face.getLandmarks().get(0).getPosition().x;
            eye1y = (int) face.getLandmarks().get(0).getPosition().y;
            eye2x = (int) face.getLandmarks().get(1).getPosition().x;
            cheek = (int) face.getLandmarks().get(4).getPosition().y;
            int width = (eye2x-eye1x)/2;
            int height = (cheek-eye1y)/2;
            Bitmap rect = ImageTransform.getCroppedRec(bitmap, eye1x-width, eye1y-height, eye2x+width, eye1y+height);
            rect = ImageEffect.Mosaic(rect, 30);
            canvasBmp = ImageTransform.getCombinationBitmap(rect,bitmap,0,0);
        }
        Log.i("HHH"," " + faces.size());
        return canvasBmp;
    }

    public static Bitmap drawMustache(Bitmap bitmap, Bitmap mustache){
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        Bitmap result = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        float mLx, mLy, mRx, mRy, noisex, noisey;
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);

            mLx = (int) face.getLandmarks().get(6).getPosition().x;
            mLy = (int) face.getLandmarks().get(6).getPosition().y;
            mRx = (int) face.getLandmarks().get(5).getPosition().x;
            mRy = (int) face.getLandmarks().get(5).getPosition().y;
            noisex = (int) face.getLandmarks().get(2).getPosition().x;
            noisey = (int) face.getLandmarks().get(2).getPosition().y;
            float width = (mRx-mLx);
            float scale = (float) mustache.getWidth()/width;
            mustache = ImageTransform.resize(mustache, scale);
            int degree = (int)Math.toDegrees(Math.atan((mRy-mLy)/(mRx-mLx)));
            Log.i("Ry Ly Rx Lx", mRy+" "+mLy+" "+ mRx +" "+mRy);
            Log.i("Degreee", ""+degree);
            mustache = ImageTransform.rotate(mustache, degree);
            result = ImageTransform.getCombinationBitmap(mustache,bitmap,noisex-width/2, noisey-mustache.getWidth()/4);
        }
        return  result;
    }
    public static Bitmap drawCatEar(Bitmap bitmap, Bitmap catEar){
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        Bitmap result = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        float eLx, eLy, eRx, eRy, noisex, noisey;
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);

            eLx = (int) face.getLandmarks().get(0).getPosition().x;
            eLy = (int) face.getLandmarks().get(0).getPosition().y;
            eRx = (int) face.getLandmarks().get(1).getPosition().x;
            eRy = (int) face.getLandmarks().get(1).getPosition().y;
            noisex = (int) face.getLandmarks().get(2).getPosition().x;
            noisey = (int) face.getLandmarks().get(2).getPosition().y;
            //face.getPosition()
                    //face.getHeight()
                            //face.getWidth()
            float width = (eRx-eLx);
            float scale = (float) catEar.getWidth()/width/(float)2.3;
            catEar = ImageTransform.resize(catEar, scale);
            int degree = (int)Math.toDegrees(Math.atan((eRy-eLy)/(eRx-eLx)));
            //Log.i("Ry Ly Rx Lx", mRy+" "+mLy+" "+ mRx +" "+mRy);
            //Log.i("Degreee", ""+degree);
            catEar = ImageTransform.rotate(catEar, degree);
            result = ImageTransform.getCombinationBitmap(catEar,bitmap,
                    eLx+(width-catEar.getWidth())/2, face.getPosition().y-face.getHeight()/8);
        }
        return  result;
    }

    public static Bitmap drawNose(Bitmap bitmap, Bitmap nose){
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        Bitmap result = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        float noisex, noisey, cheek1x, cheek2x;
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            noisex = (int) face.getLandmarks().get(2).getPosition().x;
            noisey = (int) face.getLandmarks().get(2).getPosition().y;
            cheek1x = (int) face.getLandmarks().get(4).getPosition().x;
            cheek2x = (int) face.getLandmarks().get(3).getPosition().x;
            float width = cheek2x-cheek1x;
            float scale = (float) nose.getWidth()/width/(float)1.2;
            nose = ImageTransform.resize(nose, scale);
            result = ImageTransform.getCombinationBitmap(nose,bitmap, noisex-nose.getWidth()/2, noisey-2*nose.getWidth()/3);
        }
        return  result;
    }
    public static Bitmap drawBlush(Bitmap bitmap, Bitmap blush){
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        Bitmap result = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        float cheek1x, cheek2x, cheek1y, cheek2y;
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            cheek1x = (int) face.getLandmarks().get(4).getPosition().x;
            cheek2x = (int) face.getLandmarks().get(3).getPosition().x;
            cheek1y = (int) face.getLandmarks().get(4).getPosition().y;
            cheek2y = (int) face.getLandmarks().get(3).getPosition().y;
            float width = cheek2x-cheek1x;
            float scale = (float) blush.getWidth()/width/(float)1.2;
            blush = ImageTransform.resize(blush, scale);
            result = ImageTransform.getCombinationBitmap(blush,bitmap, cheek1x-blush.getWidth()/2, cheek1y-blush.getWidth()/2);
            result = ImageTransform.getCombinationBitmap(blush,result, cheek2x-blush.getWidth()/2, cheek2y-blush.getWidth()/2);
        }
        return  result;
    }
}
