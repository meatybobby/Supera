package nthu.bobby.supera;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by PINK on 2016/6/11.
 */
public class MainUI {
    public ImageButton btnCamera, btnAlbum;

    public MainUI(Activity activity){
        btnAlbum = (ImageButton)activity.findViewById(R.id.btnAlbum);
        btnCamera = (ImageButton)activity.findViewById(R.id.btnCamera);
    }

    public void setToOnClickListener(View.OnClickListener listener) {
        btnAlbum.setOnClickListener(listener);
        btnCamera.setOnClickListener(listener);
    }
}
