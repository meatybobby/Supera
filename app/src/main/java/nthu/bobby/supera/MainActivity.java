package nthu.bobby.supera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
public class MainActivity extends Activity implements View.OnClickListener {

    private MainUI UI;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UI = new MainUI(this);
        UI.setToOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCamera:
                type = "camera";

                break;
            case R.id.btnAlbum:
                type = "album";

                break;
        }
        Intent intent = new Intent(MainActivity.this, FunctionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        intent.putExtras(bundle);

        startActivity(intent);
        /* 傳資料給不同activity
        * //new一個Bundle物件，並將要傳遞的資料傳入
              Bundle bundle = new Bundle();
              bundle.putDouble("height",height );
              bundle.putString("sex", sex);

              //將Bundle物件assign給intent
              intent.putExtras(bundle);
        * */

    }
}