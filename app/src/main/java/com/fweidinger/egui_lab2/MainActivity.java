package com.fweidinger.egui_lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private final String newStoreTrue = "new Codestore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout baseLayout = findViewById(R.id.baselayout);
        final LinearLayout myButtons = (LinearLayout) getLayoutInflater().inflate(R.layout.buttons_layout,null);

        final Button button = new Button(getApplicationContext());
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setText(newStoreTrue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseLayout.removeView(button);
                baseLayout.addView(displayCodeStore());
                baseLayout.addView(myButtons);
            }
        });
        baseLayout.addView(button);

    }


    /**
     * This method determines if the app is run for the first time.
     *
     * @return true: first start; false : recurring start
     */
    public boolean isFirstAppStart() {
        boolean first = false;
        SharedPreferences sharedPreferences = getSharedPreferences("firstStart", MODE_PRIVATE);
        SharedPreferences.Editor shrdPrfEditor = sharedPreferences.edit();
        if (!sharedPreferences.getBoolean("firstStart", false)) {
            first = true;
            shrdPrfEditor.putBoolean("firstStart", true);
            shrdPrfEditor.apply();
        }
        return first;
    }

    public void saveCodeToPreferences(String data){
        SharedPreferences sharedPreferences = getSharedPreferences("textStore",MODE_PRIVATE);
        SharedPreferences.Editor shrdPrfEditor = sharedPreferences.edit();
        shrdPrfEditor.putString("textStore",data);
        shrdPrfEditor.apply();
    }

    public EditText displayCodeStore() {
        EditText editText = new EditText(getApplicationContext());
        editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return editText;
    }
}
