package pl.piterpti.cba.pl;

/**
 * Created by Piter on 23/10/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {

    private Button startButton;
    private Button startWithDebugButton;
    private Button exitButton;

    public static boolean normalRun;

    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        init();
        addListeners();
    }

    private void init() {
        startButton = (Button) findViewById(R.id.startBtn);
        startWithDebugButton = (Button) findViewById(R.id.startDbgBtn);
        exitButton = (Button) findViewById(R.id.exitBtn);

        if(sharedPreferences == null) {
            sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        }
    }

    private void addListeners() {

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalRun();
            }
        });

        startWithDebugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runWithDebugOptions();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }

    private void normalRun() {
        normalRun = true;
        Intent intent = createMainIntent();
        startActivity(intent);
    }

    private void runWithDebugOptions() {
        Intent intent = createMainIntent();
        normalRun = false;


        startActivity(intent);
    }

    private void goToDebugOptions() {

    }

    private Intent createMainIntent() {
        Intent intent = new Intent(this, HandDetector.class);
        return intent;
    }

}
