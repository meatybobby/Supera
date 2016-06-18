package nthu.bobby.supera;

import android.app.Activity;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

/**
 * Created by PINK on 2016/6/11.
 */
public class FuncUI {
    public ImageView imageView;
    public Button button, btnEdge, btnBlur, btnRGB, btnEnhance, btnGray;
    public Button btnRed, btnBlue, btnGreen, btnOld, btnOrig;
    public Button btnCartoon, btnPencil, btnLomo;
    public TextView textView;
    public ViewAnimator viewAnimator;
    public SeekBar seekBarR, seekBarG, seekBarB;

    public FuncUI(Activity activity){
        imageView = (ImageView)activity.findViewById(R.id.imageView);
        textView = (TextView)activity.findViewById(R.id.textView);
        //button = (Button)activity.findViewById(R.id.button);
        btnOrig = (Button)activity.findViewById(R.id.btnOrig);
        btnBlur = (Button)activity.findViewById(R.id.btnBlur);
        btnEdge = (Button)activity.findViewById(R.id.btnEdge);
        btnRGB = (Button)activity.findViewById(R.id.btnRGB);
        btnEnhance = (Button)activity.findViewById(R.id.btnEnhance);
        btnGray = (Button)activity.findViewById(R.id.btnGray);
        btnRed = (Button)activity.findViewById(R.id.btnRed);
        btnGreen = (Button)activity.findViewById(R.id.btnGreen);
        btnBlue = (Button)activity.findViewById(R.id.btnBlue);
        btnOld = (Button)activity.findViewById(R.id.btnOld);
        btnCartoon = (Button)activity.findViewById(R.id.btnCartoon);
        btnPencil = (Button)activity.findViewById(R.id.btnPencil);
        btnLomo = (Button)activity.findViewById(R.id.btnLomo);
        viewAnimator = (ViewAnimator)activity.findViewById(R.id.viewAnimator);
        seekBarR = (SeekBar)activity.findViewById(R.id.seekBarR);
        seekBarG = (SeekBar)activity.findViewById(R.id.seekBarG);
        seekBarB = (SeekBar)activity.findViewById(R.id.seekBarB);
    }

    public void setToOnClickListener(View.OnClickListener listener) {
        //button.setOnClickListener(listener);
        btnOrig.setOnClickListener(listener);
        btnBlur.setOnClickListener(listener);
        btnEdge.setOnClickListener(listener);
        btnRGB.setOnClickListener(listener);
        btnEnhance.setOnClickListener(listener);
        btnGray.setOnClickListener(listener);
        btnRed.setOnClickListener(listener);
        btnBlue.setOnClickListener(listener);
        btnGreen.setOnClickListener(listener);
        btnOld.setOnClickListener(listener);
        btnCartoon.setOnClickListener(listener);
        btnPencil.setOnClickListener(listener);
        btnLomo.setOnClickListener(listener);
    }

}
