package dhbk.android.httbdd_osm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickIntentDirection(View view) {
        Intent intent = new Intent(this, DirectionActivity.class);
        startActivity(intent);
    }

    public void onClickIntentShare(View view) {
        Intent intent = new Intent(this, ShareActivity.class);
        startActivity(intent);
    }
}
