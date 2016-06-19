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
        result = darkMask(src, scale);
        result = HSV(result,0,1.3,-10);
        return result;
    }

    public static Mat oldEffect(Mat src) {
        Mat result = new Mat(src.rows(),src.cols(),src.type());
        // result = HSV(src, 0, 0.8, 10);
        // result = setRGB(result, 45, -45, -45);
        int channel = src.channels();
        byte[] Value = new byte[(int) (src.total()*src.channels())];
        src.get(0,0,Value);
        int srcR, srcG, srcB;
        int dstR, dstG, dstB;
        for(int i = 0;i<Value.length;i+=channel) {
            srcR = Value[i]&0xFF;
            srcG = Value[i+1]&0xF3;
            srcB = Value[i+2]&0xFF;
            dstR = (int) (0.393 * srcR + 0.769 * srcG + 0.189 * srcB);
            dstG = (int) (0.349 * srcR + 0.686 * srcG + 0.168 * srcB);
            dstB = (int) (0.272 * srcR + 0.534 * srcG + 0.131 * srcB);
            if(dstR>255) dstR=255;
            if(dstG>255) dstG=255;
            if(dstB>255) dstB=255;
            Value[i] = (byte)dstR;
            Value[i+1] = (byte)dstG;
            Value[i+2] = (byte)dstB;
        }
        result.put(0,0,Value);
        return result;
    }

    public static Mat setRGB(Mat src, int R, int G, int B){
        byte[] Value = new byte[(int) (src.total()*src.channels())];
        src.get(0,0,Value);
        int channel = src.channels();
        int srcR, srcG, srcB;
        for(int i = 0;i<Value.length;i+=channel) {
            srcR = (int) (Value[i]&0xFF) + R*3;
            srcG = (int) (Value[i+1]&0xFF) + G*3;
            srcB = (int) (Value[i+2]&0xFF) + B*3;
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
        src.put(0,0,Value);
        return src;
    }

    public static Mat setConBri(Mat src, double con, int bir){
        byte[] Value = new byte[(int) (src.total()*src.channels())];
        src.get(0,0,Value);
        int channel = src.channels();
        int srcR, srcG, srcB;
        for(int i = 0;i<Value.length;i+=3) {
            srcR = (int) con*(Value[i]&0xFF) + bir;
            srcG = (int) con*(Value[i+1]&0xFF) + bir;
            srcB = (int) con*(Value[i+2]&0xFF) + bir;
            if(srcR>255) srcR=255;
            if(srcG>255) srcG=255;
            if(srcB>255) srcB=255;
            if(srcR<0) srcR=0;
            if(srcG<0) srcG=0;
            if(srcB<0) srcB=0;
            Value[i] = (byte) (con*(Value[i]&0xFF) + bir);
            Value[i+1] = (byte) (con*(Value[i+1]&0xFF) + bir);
            Value[i+2] = (byte) (con*(Value[i+2]&0xFF) + bir);
        }
        src.put(0,0,Value);
        return src;
    }

    public static Bitmap Enhancement(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int A, R = 0, G = 0, B = 0;
        int pixel;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixel = src.getPixel(x, y);
                //set the new value
                A = Color.alpha(pixel);
                R = Color.red(src.getPixel(x, y));
                G = Color.green(src.getPixel(x, y));
                B = Color.blue(src.getPixel(x, y));

                if (R > 100) R *= 1.3;
                if (R > 255) R = 255;
                if (G > 100) G *= 1.3;
                if (G > 255) G = 255;
                if (B > 100) B *= 1.3;
                if (B > 255) B = 255;
                if (R < 80) R /= 1.5;
                if (G < 80) G /= 1.5;
                if (B < 80) B /= 1.5;
                result.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return result;
    }

    public static Mat cartoonize(Mat img) {
        Mat result = new Mat();
        Mat temp = new Mat();
        Imgproc.cvtColor(img, temp, COLOR_RGBA2RGB);
        Imgproc.pyrDown(temp, result);
        Imgproc.pyrDown(result, temp);
        for (int i = 0; i < 3; i++) {
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
        /*Mat maskC = new Mat();
        Core.bitwise_not(mask,maskC);*/
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

    private static Mat burn(Mat img, Mat mask) {
        Mat maskC = new Mat();
        Core.bitwise_not(mask, maskC);
        Mat imgC = new Mat();
        Core.bitwise_not(img, imgC);
        Mat result = new Mat();
        Core.divide(imgC, maskC, result, 256);
        Core.bitwise_not(result, result);
        return result;
    }

    public static Mat pencil(Mat img) {
        Mat imgGray = new Mat();
        Mat imgGrayInv = new Mat();
        Mat imgBlur = new Mat();
        Imgproc.cvtColor(img, imgGray, COLOR_RGBA2GRAY);
        Core.bitwise_not(imgGray, imgGrayInv);
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
            byte[] resultValue = new byte[(int) (edge.total() * edge.channels())];
            byte[] tempValue = new byte[(int) (cartoon.total() * cartoon.channels())];
            cartoon.get(0, 0, tempValue);
            result = new Mat(edge.rows(),edge.cols(),edge.type());
            System.arraycopy(tempValue, 0, resultValue, 0, resultValue.length);
            result.put(0, 0, resultValue);
            cartoon = result;
        }
        Core.bitwise_and(cartoon, edge, result);
        return result;
    }

    public static Mat darkMask(Mat imgMat, float scale){
        Mat imgMatResult = new Mat();
        Point p = new Point(imgMat.cols()/2, imgMat.rows()/2);
        Mat dark = new Mat(imgMat.rows(), imgMat.cols(), CvType.CV_32FC4, new Scalar(0.4, 0.4, 0.4));

        Size axes = new Size(imgMat.cols()*scale, imgMat.rows()*scale);
        ellipse(dark, p, axes, 0, 0, 360, new Scalar(1,1,1), -1);
        //Imgproc.GaussianBlur(dark, dark, new Size(33,33), 19, 19);
        Imgproc.blur(dark, dark, new Size(51,51));
        imgMat.convertTo(imgMat, CvType.CV_32FC4);
        imgMatResult = dark.mul(imgMat);
        imgMatResult.convertTo(imgMatResult, CvType.CV_8UC4);

        return imgMatResult;
    }

    public static Mat HSV(Mat imgMat, int hh, double ss, int vv){
        Mat imgMatResult = new Mat();
        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGB2HSV);

        byte[] imgValue = new byte[(int) (imgMat.total()*imgMat.channels())];
        imgMat.get(0,0,imgValue);
        int h, s, v;
        for(int i = 0;i<imgValue.length;i+=3) {
            // imgValue[i] = (byte) (imgValue[i]& 0xFF);
            h = (int)(imgValue[i]&0xFF) + hh;
            if(h>180) h = h-180; if(h<0) h = h+180;
            imgValue[i] =(byte) h;

            s = (int) ((int)(imgValue[i+1]&0xFF)*ss);
            if(s>255) s= 255; if(s<0) s=0;
            imgValue[i+1] =(byte) s;

            v = (int)(imgValue[i+2]&0xFF) + vv;
            if(v>255) v= 255; if(v<0) v=0;
            imgValue[i+2] =(byte) v;
            //if( !((imgValue[i]>0&&imgValue[i]<8) || (imgValue[i]>120&&imgValue[i]<180)) )
            //if(!((imgValue[i]>0&&imgValue[i]<100)&&(imgValue[i+1]>58&&imgValue[i+1]<173)))
                //imgValue[i+1] = 0;
        }

        imgMat.put(0,0,imgValue);
        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_HSV2RGB);
        return imgMat;
    }
    public static Mat detailEnhance(Mat img) {
        Mat result = new Mat();
        Photo.detailEnhance(img,result);
        return result;
    }

    public static Mat imageClone(Mat src, Mat dst, Point pos) {
        Mat result = new Mat();
        Mat srcMask = new Mat(src.rows(),src.cols(),src.type(),new Scalar(255,255,255,255));
        Photo.seamlessClone(src,dst,srcMask,pos,result, Photo.MIXED_CLONE);
        return result;
    }



    public static Bitmap Mosaic(Bitmap src){
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int M = (width<height)? width/30 : height/30;
        result = ImageTransform.resize( ImageTransform.resize(src, M), (float) 1/M);
        return result;
    }
}
