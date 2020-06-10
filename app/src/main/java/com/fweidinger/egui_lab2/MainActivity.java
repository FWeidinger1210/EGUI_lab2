package com.fweidinger.egui_lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String newStoreTrue = "New Codestore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout baseLayout = findViewById(R.id.baselayout);
        final LinearLayout myButtons = (LinearLayout) getLayoutInflater().inflate(R.layout.buttons_layout, null);
        final LinearLayout editTextLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.edittext_insert, null);
        Button buttonSave = myButtons.findViewById(R.id.button_save);
        Button buttonDelete = myButtons.findViewById(R.id.button_delete);
        buttonSave.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);


        final Button button = new Button(getApplicationContext());
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if (getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", "").matches("")) {
            button.setText(newStoreTrue);
            baseLayout.addView(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    baseLayout.removeView(button);
                    baseLayout.addView(editTextLayout);
                    baseLayout.addView(myButtons);
                    EditText editText = editTextLayout.findViewById(R.id.edittext_input);
                    editText.setText(displayCodeStore());

                }
            });

        }
        else{
            baseLayout.addView(editTextLayout);
            baseLayout.addView(myButtons);
            EditText editText = editTextLayout.findViewById(R.id.edittext_input);
            editText.setText(displayCodeStore());

        }
    }


    @Override
    public void onClick(View v) {
        EditText editText = findViewById(R.id.edittext_input);
        switch (v.getId()) {
            case (R.id.button_save):
                saveCodeToPreferences(editText.getText().toString().trim());
                finish();
                break;
            case (R.id.button_delete):
                editText.setText("");
                deleteCodeFromPreferences();
                finish();
                break;
        }
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

    public void saveCodeToPreferences(String data) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE);
        SharedPreferences.Editor shrdPrfEditor = sharedPreferences.edit();
        shrdPrfEditor.putString("textStore", data);
        shrdPrfEditor.apply();
        Log.d("Saving Test", "saveCodeToPreferences: " + getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", ""));
    }

    public void deleteCodeFromPreferences() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE);
        SharedPreferences.Editor shrdPrfEditor = sharedPreferences.edit();
        shrdPrfEditor.remove("textStore");
        shrdPrfEditor.apply();
        Log.d("Deletion Test", "deleteCodeFromPreferences:" + getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", ""));
    }

    public String displayCodeStore() {
        return getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", "");
    }


}
