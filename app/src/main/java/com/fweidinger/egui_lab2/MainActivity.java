package com.fweidinger.egui_lab2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String newStoreTrue = "New Codestore";
    EncryptDecrypt encryptDecrypt;

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

        } else {
            promptForPasswordDialog();
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
                createPasswordDialog();
                saveCodeToPreferences(editText.getText().toString().trim());
                //finish();
                break;
            case (R.id.button_delete):
                createAlert(editText);
                break;
        }
    }

    /**
     * This method will save given data in SharedPreferences with key >>textStore<<
     *
     * @param data the string representing the data to be saved
     */
    public void saveCodeToPreferences(String data) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE);
        SharedPreferences.Editor shrdPrfEditor = sharedPreferences.edit();
        shrdPrfEditor.putString("textStore", data);
        shrdPrfEditor.apply();
        Log.d("Saving Test", "saveCodeToPreferences: " + getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", ""));
    }

    /**
     * This method will delete the data saved in SharedPreferences with key >>textStore<<
     */
    public void deleteCodeFromPreferences() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE);
        SharedPreferences.Editor shrdPrfEditor = sharedPreferences.edit();
        shrdPrfEditor.remove("textStore");
        shrdPrfEditor.apply();
        Log.d("Deletion Test", "deleteCodeFromPreferences:" + getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", ""));
    }

    /**
     * @return the string stored in SharedPreferences with key >>textStore<<
     */
    public String displayCodeStore() {
        return getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", "");
    }

    /**
     * Creates a simple alert, that prompts for user inputs.
     * On  Confirmation it will delete the contents of the codestore and close the application.
     *
     * @param editText - The EditText Object that will be set
     */
    public void createAlert(final EditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_message)
                .setTitle(R.string.alert_title);
        builder.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editText.setText("");
                deleteCodeFromPreferences();
                finish();
            }
        });
        builder.setNegativeButton(R.string.alert_deny, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void promptForPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setMessage(R.string.prompt_password_message)
                .setTitle(R.string.prompt_password_title)
                .setView(input);
        builder.setPositiveButton(R.string.prompt_enter_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    encryptDecrypt = new EncryptDecrypt(input.getText().toString());
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String hashedPassword = getApplicationContext().getSharedPreferences("password", MODE_PRIVATE).getString("password", "");
                Log.d("Password Test 3", "Pass (hash) : "+hashedPassword);
                if (encryptDecrypt.decrypt(hashedPassword).matches(input.getText().toString())) {
                    Toast.makeText(getApplicationContext(), encryptDecrypt.decrypt(hashedPassword), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "FALSCH", Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void createPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);


        builder.setMessage(R.string.password_dialog_message)
                .setTitle(R.string.password_dialog_title)
                .setView(input);

        builder.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String hashed = null;
                try {
                    encryptDecrypt = new EncryptDecrypt(input.getText().toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    hashed = encryptDecrypt.encrypt(input.getText().toString());
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("password", MODE_PRIVATE);
                SharedPreferences.Editor shrdPrfEditor = sharedPreferences.edit();
                shrdPrfEditor.putString("password", hashed);
                shrdPrfEditor.apply();
                Log.d("Password Test 1", "hash:" + getApplicationContext().getSharedPreferences("password", MODE_PRIVATE).getString("password", "")+encryptDecrypt.decrypt(getApplicationContext().getSharedPreferences("password", MODE_PRIVATE).getString("password", "")));
                input.getText();
                finish();
            }
        });
        builder.setNegativeButton(R.string.alert_deny, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
