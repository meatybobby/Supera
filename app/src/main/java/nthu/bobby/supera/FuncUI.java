package nthu.bobby.supera;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by PINK on 2016/6/11.
 */
public class FuncUI {
    public ImageView imageView;
    public Button button;

    public FuncUI(Activity activity){
        imageView = (ImageView)activity.findViewById(R.id.imageView);
        //button = (Button)activity.findViewById(R.id.button);

    }

    public void setToOnClickListener(View.OnClickListener listener) {
        //button.setOnClickListener(listener);
    }
}
