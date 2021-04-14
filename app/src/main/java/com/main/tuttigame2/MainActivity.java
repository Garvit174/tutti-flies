package com.main.tuttigame2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean is_mute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });

        TextView highScoreTxt = findViewById(R.id.highScoreTxt);

        final SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
        highScoreTxt.setText("High Score: " + prefs.getInt("high_score", 0));

        is_mute = prefs.getBoolean("is_mute", false);

        final ImageView volumeCtrl = findViewById(R.id.volumeCtrl);

        if (is_mute)
            volumeCtrl.setImageResource(R.drawable.volume_off);
        else
            volumeCtrl.setImageResource(R.drawable.volume_on);

        volumeCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                is_mute = !is_mute;
                if (is_mute)
                    volumeCtrl.setImageResource(R.drawable.volume_off);
                else
                    volumeCtrl.setImageResource(R.drawable.volume_on);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_mute", is_mute);
                editor.apply();

            }
        });

    }
}
