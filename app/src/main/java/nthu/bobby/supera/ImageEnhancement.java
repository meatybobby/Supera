package nthu.bobby.supera;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by gatto on 2016/6/16.
 */
public class ImageEnhancement {
    public static Bitmap apply(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int A, R=0, G=0, B=0;
        int pixel;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixel = src.getPixel(x, y);
                //set the new value
                A = Color.alpha(pixel);
                R = Color.red(src.getPixel(x, y)) ;
                G = Color.green(src.getPixel(x, y)) ;
                B = Color.blue(src.getPixel(x, y)) ;

                if(R>100) R*=1.3;   if(R>255) R=255;
                if(G>100) G*=1.3;   if(G>255) G=255;
                if(B>100) B*=1.3;   if(B>255) B=255;
                if(R<80) R/=1.5;
                if(G<80) G/=1.5;
                if(B<80) B/=1.5;
                result.setPixel(x, y, Color.argb(A,R,G,B));
            }
        }

        return result;
    }
}
