package nthu.bobby.supera;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * Created by PINK on 2016/6/11.
 */
public class ImageTransform {

    public static Bitmap resize(Bitmap src, float scale){
        Bitmap result;
        result = Bitmap.createScaledBitmap(src, (int)(src.getWidth() / scale), (int)(src.getHeight() / scale), false);

        return result;
    }

    public static Bitmap rotate(Bitmap src, int degree){
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);

        Bitmap result = Bitmap.createBitmap( src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);

        return result;
    }

    /*
    Usage: to crop a circular image from the source bitmap image
    Parameter:
        src: source image
        x: the x-axis position of the circle's center
        y: the y-axis position of the circle's center
        r: the circle's radius
    Notice: this function should use API 17 or higher
      */
    public static Bitmap getCroppedCircleBitmap(Bitmap src, float x, float y, float r) {
        Bitmap output = Bitmap.createBitmap(src.getWidth(),
                src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(x, y, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);

        return output;
    }

    /*
    Usage: to let the background image be overlaid with the source image.
    Parameter:
        src: source image
        back: background image
        x: the background image's x-axis position where the source image be put on.
        y: the background image's y-axis position where the source image be put on.
     */
    public static Bitmap getCombinationBitmap(Bitmap src, Bitmap back,float x, float y) {
        Bitmap bmOverlay = Bitmap.createBitmap(back.getWidth(), back.getHeight(), back.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(back, new Matrix(), null);
        canvas.drawBitmap(src, x, y, null);
        return bmOverlay;
    }
}
