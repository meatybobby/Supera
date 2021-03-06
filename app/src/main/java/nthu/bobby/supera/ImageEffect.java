package nthu.bobby.supera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Debug;
import android.provider.ContactsContract;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.nio.IntBuffer;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2RGB;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2RGBA;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2RGB;
import static org.opencv.imgproc.Imgproc.LINE_4;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.ellipse;

/**
 * Created by gatto on 2016/6/17.
 */
public class ImageEffect {

    public static Mat lomo(Mat src, float scale){
        Mat result = new Mat();
        result = Enhancement(src, (float) 1.1);
        result = darkMask(result, scale);
        result = HSV(result, 0, 1.2, -10);
        result = setRGB(result, 0, 10, 15);
        return result;
    }

    public static Mat oldEffect(Mat src, float scaleR, float scaleG, float scaleB) {
        Mat result = new Mat(src.rows(),src.cols(),src.type());
        int channel = src.channels();
        byte[] Value = new byte[(int) (src.total()*src.channels())];
        src.get(0,0,Value);
        int srcR, srcG, srcB;
        int dstR, dstG, dstB;
        for(int i = 0; i<Value.length; i+=channel) {
            srcR = Value[i] & 0xFF;
            srcG = Value[i+1] & 0xF3;
            srcB = Value[i+2] & 0xFF;
            dstR = (int) (0.393 * srcR + 0.769 * srcG + 0.189 * srcB);
            dstG = (int) (0.349 * srcR + 0.686 * srcG + 0.168 * srcB);
            dstB = (int) (0.272 * srcR + 0.534 * srcG + 0.131 * srcB);
            dstR += scaleR; dstG += scaleG; dstB += scaleB;

            if(dstR>255) dstR=255;  if(dstR<0) dstR=0;
            if(dstG>255) dstG=255;  if(dstG<0) dstG=0;
            if(dstB>255) dstB=255;  if(dstB<0) dstB=0;
            Value[i] = (byte)dstR;
            Value[i+1] = (byte)dstG;
            Value[i+2] = (byte)dstB;
        }
        result.put(0, 0, Value);
        return result;
    }

    public static Mat setRGB(Mat src, int R, int G, int B) {
        byte[] Value = new byte[(int) (src.total()*src.channels())];
        src.get(0,0,Value);
        int channel = src.channels();
        int srcR, srcG, srcB;
        for(int i = 0;i<Value.length;i+=channel) {
            srcR = (int) (Value[i]&0xFF) + R;
            srcG = (int) (Value[i+1]&0xFF) + G;
            srcB = (int) (Value[i+2]&0xFF) + B;
            if(srcR>255) srcR=255;
            if(srcG>255) srcG=255;
            if(srcB>255) srcB=255;
            if(srcR<0) srcR=0;
            if(srcG<0) srcG=0;
            if(srcB<0) srcB=0;
            Value[i] = (byte)srcR;
            Value[i+1] = (byte)srcG;
            Value[i+2] = (byte)srcB;
        }
        src.put(0, 0, Value);
        return src;
    }


    public static Mat Enhancement(Mat src, float scale) {
        byte[] Value = new byte[(int) (src.total() * src.channels())];
        src.get(0, 0, Value);
        int channel = src.channels();
        int srcR, srcG, srcB;
        for (int i = 0; i < Value.length; i += channel) {
            srcR = (int) (Value[i] & 0xFF) ;
            srcG = (int) (Value[i + 1] & 0xFF) ;
            srcB = (int) (Value[i + 2] & 0xFF) ;
            if (srcR > 90) srcR *= scale;
            if (srcR > 255) srcR = 255;
            if (srcG > 90) srcG *= scale;
            if (srcG > 255) srcG = 255;
            if (srcB > 90) srcB *= scale;
            if (srcB > 255) srcB = 255;
            if (srcR < 80) srcR /= scale;
            if (srcG < 80) srcG /= scale;
            if (srcB < 80) srcB /= scale;
            Value[i] = (byte) srcR;
            Value[i + 1] = (byte) srcG;
            Value[i + 2] = (byte) srcB;
        }
        src.put(0, 0, Value);
        return src;
    }


    public static Mat cartoonize(Mat img) {
        Mat result = new Mat();
        Mat temp = new Mat();
        Imgproc.cvtColor(img, temp, COLOR_RGBA2RGB);
        Imgproc.pyrDown(temp, result);
        Imgproc.pyrDown(result, temp);
        for (int i=0; i<3; i++) {
            Imgproc.bilateralFilter(temp, result, 9, 9, 7);
            Imgproc.bilateralFilter(result, temp, 9, 9, 7);
        }
        Imgproc.bilateralFilter(temp, result, 9, 9, 7);
        Imgproc.pyrUp(result, temp);
        Imgproc.pyrUp(temp, result);
        return result;
    }

    public static Mat getEdge(Mat img) {
        Mat result = new Mat();
        Mat temp = new Mat();
        Imgproc.cvtColor(img, result, COLOR_RGBA2GRAY);
        Imgproc.medianBlur(result, temp, 7);
        Imgproc.adaptiveThreshold(temp, result, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 9, 2);
        return result;
    }

    private static Mat dodge(Mat img, Mat mask) {
        Mat result = new Mat(img.rows(), img.cols(), img.type());
        byte[] imgValue = new byte[(int) (img.total() * img.channels())];
        byte[] maskValue = new byte[(int) (mask.total() * mask.channels())];
        byte[] resultValue = new byte[(int) (mask.total() * mask.channels())];
        img.get(0, 0, imgValue);
        mask.get(0, 0, maskValue);
        for (int i = 0; i < imgValue.length; i++) {
            resultValue[i] = (byte) dodgeDouble(imgValue[i] & 0xFF, maskValue[i] & 0xFF);
        }
        result.put(0, 0, resultValue);
        return result;
    }

    private static double dodgeDouble(int x, int y) {
        return ((y == 255) ? y : Math.min(255, ((long) x << 8) / (255 - y)));
    }

    public static Mat pencil(Mat img) {
        Mat imgGray = new Mat();
        Mat imgGrayInv = new Mat();
        Mat imgBlur = new Mat();
        Imgproc.cvtColor(img, imgGray, COLOR_RGBA2GRAY);
        Core.bitwise_not(imgGray, imgGrayInv);
       // Imgproc.GaussianBlur(imgGrayInv, imgBlur, new Size(15, 15), 0, 0);
        Imgproc.GaussianBlur(imgGrayInv, imgBlur, new Size(15, 15), 0, 0);
        Mat result = dodge(imgGray, imgBlur);
        return result;
    }

    public static Mat cartoonEdge(Mat img) {
        Mat cartoon = ImageEffect.cartoonize(img);
        Mat edge = ImageEffect.getEdge(img);
        Mat result = new Mat();
        Imgproc.cvtColor(edge, edge, COLOR_GRAY2RGB);
        if(cartoon.total() > edge.total()) {
           Imgproc.resize(cartoon,cartoon,new Size(edge.width(),edge.height()));
        }
        Core.bitwise_and(cartoon, edge, result);
        return result;
    }

    public static Mat darkMask(Mat imgMat, float scale) {
        Mat imgMatResult;
        float ratio = (float) imgMat.rows() / imgMat.cols();
        Mat dark = new Mat((int) (250*ratio), 250, CvType.CV_32FC4, new Scalar(0.4, 0.4, 0.4));
        Point p = new Point(dark.cols()/2, dark.rows()/2);

        Size axes = new Size(dark.cols()*scale, dark.rows()*scale);
        ellipse(dark, p, axes, 0, 0, 360, new Scalar(1,1,1), -1);
        //Imgproc.GaussianBlur(dark, dark, new Size(33,33), 19, 19);
        Imgproc.blur(dark, dark, new Size(51,51));
        Imgproc.resize(dark,dark,new Size(imgMat.width(),imgMat.height()));
        imgMat.convertTo(imgMat, CvType.CV_32FC4);
        imgMatResult = dark.mul(imgMat);
        imgMatResult.convertTo(imgMatResult, CvType.CV_8UC4);

        return imgMatResult;
    }

    public static Mat HSV(Mat imgMat, int H, double S, int V){
        Mat imgMatResult = new Mat();
        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGB2HSV);

        byte[] imgValue = new byte[(int) (imgMat.total() * imgMat.channels())];
        imgMat.get(0, 0, imgValue);
        int h, s, v;
        for (int i = 0; i < imgValue.length; i += 3) {
            // imgValue[i] = (byte) (imgValue[i]& 0xFF);
            h = (int)(imgValue[i]&0xFF) + H;
            if(h>180) h = h-180; if(h<0) h = h+180;
            imgValue[i] =(byte) h;

            s = (int) ((int)(imgValue[i+1]&0xFF) * S);
            if(s>255) s= 255; if(s<0) s=0;
            imgValue[i+1] =(byte) s;

            v = (int)(imgValue[i+2]&0xFF) + V;
            if(v>255) v= 255; if(v<0) v=0;
            imgValue[i+2] =(byte) v;
        }

        imgMat.put(0, 0, imgValue);
        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_HSV2RGB);
        return imgMat;
    }

    public static Mat detailEnhance(Mat img) {
        Mat result = new Mat();
        Photo.detailEnhance(img, result);
        return result;
    }

    public static Mat imageClone(Mat src, Mat dst, Point pos) {
        Mat result = new Mat();
        Mat srcMask = new Mat(src.rows(), src.cols(), src.type(), new Scalar(255, 255, 255, 255));
        Photo.seamlessClone(src, dst, srcMask, pos, result, Photo.MIXED_CLONE);
        return result;
    }

    public static Bitmap Mosaic(Bitmap src, int block) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int M = (width<height)? width/block : height/block;
        result = ImageTransform.resize(ImageTransform.resize(src, M), (float) 1/M);
        return result;
    }
}
