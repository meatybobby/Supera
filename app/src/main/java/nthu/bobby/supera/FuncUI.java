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
    public Button button, btnCanny, btnBlur, btnRGB, btnEnhance;
    public TextView textView;
    public ViewAnimator viewAnimator;
    public SeekBar seekBarR, seekBarG, seekBarB;

    public FuncUI(Activity activity){
        imageView = (ImageView)activity.findViewById(R.id.imageView);
        textView = (TextView)activity.findViewById(R.id.textView);
        //button = (Button)activity.findViewById(R.id.button);
        btnBlur = (Button)activity.findViewById(R.id.btnBlur);
        btnCanny = (Button)activity.findViewById(R.id.btnCanny);
        btnRGB = (Button)activity.findViewById(R.id.btnRGB);
        btnEnhance = (Button)activity.findViewById(R.id.btnEnhance);
        viewAnimator = (ViewAnimator)activity.findViewById(R.id.viewAnimator);
        seekBarR = (SeekBar)activity.findViewById(R.id.seekBarR);
        seekBarG = (SeekBar)activity.findViewById(R.id.seekBarG);
        seekBarB = (SeekBar)activity.findViewById(R.id.seekBarB);
    }

    public void setToOnClickListener(View.OnClickListener listener) {
        //button.setOnClickListener(listener);
        btnBlur.setOnClickListener(listener);
        btnCanny.setOnClickListener(listener);
        btnRGB.setOnClickListener(listener);
        btnEnhance.setOnClickListener(listener);
    }

}
