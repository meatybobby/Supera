package nthu.bobby.supera;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by gatto on 2016/6/16.
 */
public class ImageRGB {
    public static Bitmap apply(Bitmap src, int R, int G, int B){
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int x, y, srcR, srcG, srcB;
        for(x=0; x<width; x++){
            for(y=0; y<height; y++){
                srcR = Color.red(src.getPixel(x, y)) + R*3;
                srcG = Color.green(src.getPixel(x, y)) + G*3;
                srcB = Color.blue(src.getPixel(x, y)) + B*3;
                if(srcR>255) srcR=255;
                if(srcG>255) srcG=255;
                if(srcB>255) srcB=255;
                result.setPixel(x, y, Color.argb(1, srcR, srcG, srcB));
            }
        }
        return result;
    }
}
