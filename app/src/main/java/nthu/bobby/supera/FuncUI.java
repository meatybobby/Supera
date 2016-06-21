package nthu.bobby.supera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
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
    public Button btnEdge, btnBlur, btnHSV, btnEnhance, btnGray;
    public Button btnRed, btnBlue, btnGreen, btnOld, btnMosaic;
    public Button btnCartoon, btnPencil, btnLomo, btnDetail;
    public Button btnDocumentary, btnFisheye, btnFilllight, btnGrain;
    public ImageButton btnFaceDec, btn_catear, btn_blush, btn_mustache, btn_nose;
	public ImageButton btnApply, btnDiscard;
    public ImageButton btnOrig;
    public TextView effectView;
    public ViewAnimator viewAnimator, statusBar, faceMenu;
    public SeekBar effectSeekBar;

    public FuncUI(Activity activity){
        imageView = (ImageView)activity.findViewById(R.id.imageView);
        effectView = (TextView)activity.findViewById(R.id.effect_view);

        btnOrig = (ImageButton)activity.findViewById(R.id.btnOrig);
		btnApply = (ImageButton)activity.findViewById(R.id.btnApply);
		btnDiscard = (ImageButton)activity.findViewById(R.id.btnDiscard);
        btnFaceDec = (ImageButton)activity.findViewById(R.id.btnFaceDec);
        btn_catear = (ImageButton)activity.findViewById(R.id.btn_catear);
        btn_blush = (ImageButton)activity.findViewById(R.id.btn_blush);
        btn_mustache = (ImageButton)activity.findViewById(R.id.btn_mustache);
        btn_nose = (ImageButton)activity.findViewById(R.id.btn_nose);

        btnBlur = (Button)activity.findViewById(R.id.btnBlur);
        btnEdge = (Button)activity.findViewById(R.id.btnEdge);
        btnHSV = (Button)activity.findViewById(R.id.btnHSV);
        btnMosaic = (Button)activity.findViewById(R.id.btnMosaic);
        btnEnhance = (Button)activity.findViewById(R.id.btnEnhance);
        btnGray = (Button)activity.findViewById(R.id.btnGray);
        btnRed = (Button)activity.findViewById(R.id.btnRed);
        btnGreen = (Button)activity.findViewById(R.id.btnGreen);
        btnBlue = (Button)activity.findViewById(R.id.btnBlue);
        btnOld = (Button)activity.findViewById(R.id.btnOld);
        btnCartoon = (Button)activity.findViewById(R.id.btnCartoon);
        btnPencil = (Button)activity.findViewById(R.id.btnPencil);
        btnLomo = (Button)activity.findViewById(R.id.btnLomo);
        btnDetail = (Button) activity.findViewById(R.id.btnDetail);

        btnDocumentary = (Button)activity.findViewById(R.id.btnDocumentary);
        btnFisheye = (Button)activity.findViewById(R.id.btnFisheye);
        btnFilllight = (Button)activity.findViewById(R.id.btnFilllight);
        btnGrain = (Button)activity.findViewById(R.id.btnGrain);

        viewAnimator = (ViewAnimator)activity.findViewById(R.id.viewAnimator);
        statusBar = (ViewAnimator)activity.findViewById(R.id.statusBar);
        faceMenu = (ViewAnimator)activity.findViewById(R.id.faceMenu);
        effectSeekBar = (SeekBar)activity.findViewById(R.id.seekBar_effect);
    }

    public void setToOnClickListener(View.OnClickListener listener) {
        btnOrig.setOnClickListener(listener);
        btnBlur.setOnClickListener(listener);
        btnEdge.setOnClickListener(listener);
        btnHSV.setOnClickListener(listener);
        btnMosaic.setOnClickListener(listener);
        btnEnhance.setOnClickListener(listener);
        btnGray.setOnClickListener(listener);
        btnRed.setOnClickListener(listener);
        btnBlue.setOnClickListener(listener);
        btnGreen.setOnClickListener(listener);
        btnOld.setOnClickListener(listener);
        btnCartoon.setOnClickListener(listener);
        btnPencil.setOnClickListener(listener);
        btnLomo.setOnClickListener(listener);
        btn_catear.setOnClickListener(listener);
        btn_blush.setOnClickListener(listener);
        btn_mustache.setOnClickListener(listener);
        btn_nose.setOnClickListener(listener);
        btnDetail.setOnClickListener(listener);
        btnDocumentary.setOnClickListener(listener);
        btnFisheye.setOnClickListener(listener);
        btnFilllight.setOnClickListener(listener);
        btnGrain.setOnClickListener(listener);
    }

}
