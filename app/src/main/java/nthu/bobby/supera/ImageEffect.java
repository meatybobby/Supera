package nthu.bobby.supera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Debug;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.nio.IntBuffer;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2RGB;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2RGB;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.ellipse;

/**
 * Created by gatto on 2016/6/17.
 */
public class ImageEffect {
    public static Bitmap changeRGB(Bitmap src, int R, int G, int B){
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
                if(srcR<0) srcR=0;
                if(srcG<0) srcG=0;
                if(srcB<0) srcB=0;
                result.setPixel(x, y, Color.argb(1, srcR, srcG, srcB));
            }
        }
        return result;
    }

    /*public static Mat setRGB(Mat src, int R, int G, int B){
        int width = src.cols();
        int height = src.rows();

        byte[] Value = new byte[(int) (src.total()*src.channels())];
        src.get(0,0,Value);
        int srcR, srcG, srcB;
        for(int i = 0;i<Value.length;i+=4) {
            srcR = Value[i]&0xFF
        }
    }*/

    public static Bitmap Brighten(Bitmap src, double alpha,int beta){
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        int x, y, srcR, srcG, srcB;
        for(x=0; x<width; x++){
            for(y=0; y<height; y++){
                srcR = (int)alpha * Color.red(src.getPixel(x, y)) + beta;
                srcG = (int)alpha * Color.green(src.getPixel(x, y)) + beta;
                srcB = (int)alpha * Color.blue(src.getPixel(x, y)) + beta;
                if(srcR>255) srcR=255;
                if(srcG>255) srcG=255;
                if(srcB>255) srcB=255;
                result.setPixel(x, y, Color.argb(1, srcR, srcG, srcB));
            }
        }
        return result;
    }


    public static Bitmap Enhancement(Bitmap src) {
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

    public static Mat cartoonize(Mat img) {
        Mat result = new Mat();
        Mat temp = new Mat();
        Imgproc.cvtColor(img,temp,COLOR_RGBA2RGB);
        Imgproc.pyrDown(temp,result);
        Imgproc.pyrDown(result,temp);
        for(int i = 0;i<3;i++) {
            Imgproc.bilateralFilter(temp, result, 9, 9, 7);
            Imgproc.bilateralFilter(result, temp, 9, 9, 7);
        }
        Imgproc.bilateralFilter(temp,result,9,9,7);
        Imgproc.pyrUp(result,temp);
        Imgproc.pyrUp(temp,result);
        return result;
    }

    public static Mat getEdge(Mat img) {
        Mat result = new Mat();
        Mat temp = new Mat();
        Imgproc.cvtColor(img,result,COLOR_RGBA2GRAY);
        Imgproc.medianBlur(result,temp,7);
        Imgproc.adaptiveThreshold(temp,result,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,9,2);
        return result;
    }

    private static Mat dodge(Mat img,Mat mask) {
        /*Mat maskC = new Mat();
        Core.bitwise_not(mask,maskC);*/
        Mat result = new Mat(img.rows(),img.cols(),img.type());
        byte[] imgValue = new byte[(int) (img.total()*img.channels())];
        byte[] maskValue = new byte[(int)(mask.total()*mask.channels())];
        byte[] resultValue = new byte[(int)(mask.total()*mask.channels())];
        img.get(0,0,imgValue);
        mask.get(0,0,maskValue);
        for(int i = 0;i<imgValue.length;i++) {
            resultValue[i] = (byte) dodgeDouble(imgValue[i] & 0xFF,maskValue[i] & 0xFF);
        }
        result.put(0,0,resultValue);
        return result;
    }

    private static double dodgeDouble(int x,int y) {
        return ((y==255)? y:Math.min(255,((long)x<<8)/(255-y)));
    }

    private static Mat burn(Mat img,Mat mask) {
        Mat maskC = new Mat();
        Core.bitwise_not(mask,maskC);
        Mat imgC = new Mat();
        Core.bitwise_not(img,imgC);
        Mat result = new Mat();
        Core.divide(imgC,maskC,result,256);
        Core.bitwise_not(result,result);
        return result;
    }

    public static Mat pencil(Mat img) {
        Mat imgGray = new Mat();
        Mat imgGrayInv = new Mat();
        Mat imgBlur = new Mat();
        Imgproc.cvtColor(img,imgGray,COLOR_RGBA2GRAY);
        Core.bitwise_not(imgGray,imgGrayInv);
        Imgproc.GaussianBlur(imgGrayInv,imgBlur,new Size(21,21), 0,0);
        Mat result = dodge(imgGray,imgBlur);
        return result;
    }

    public static Mat cartoonEdge(Mat img) {
        Mat cartoon = ImageEffect.cartoonize(img);
        Mat edge = ImageEffect.getEdge(img);
        Mat result = new Mat();
        Imgproc.cvtColor(edge,edge,COLOR_GRAY2RGB);
        Core.bitwise_and(cartoon,edge,result);
        return result;
    }

    public static Mat darkMask(Mat imgMat){
        Mat imgMatResult = new Mat();
        Point p = new Point(imgMat.cols()/2, imgMat.rows()/2);
        Mat dark = new Mat(imgMat.rows(), imgMat.cols(), CvType.CV_32FC4, new Scalar(0.4,0.4,0.4));

        Size axes = new Size(imgMat.cols()/2.1, imgMat.rows()/2.1);
        ellipse(dark, p, axes, 0, 0, 360, new Scalar(1,1,1), -1);
        //Imgproc.GaussianBlur(dark, dark, new Size(33,33), 19, 19);
        Imgproc.blur(dark,dark, new Size(51,51));
        imgMat.convertTo(imgMat, CvType.CV_32FC4);
        imgMatResult = dark.mul(imgMat);
        imgMatResult.convertTo(imgMatResult, CvType.CV_8UC4);

        return imgMatResult;
    }

    public static Mat HSV(Mat imgMat, int hh, double ss, int vv){
        Mat imgMatResult = new Mat();
        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGB2HSV);
        int w = imgMat.cols();
        int h = imgMat.rows();

        byte[] imgValue = new byte[(int) (imgMat.total()*imgMat.channels())];
        imgMat.get(0,0,imgValue);
        int s,v;
        for(int i = 0;i<imgValue.length;i+=3) {
            // imgValue[i] = (byte) (imgValue[i]& 0xFF);
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
}
