package com.fweidinger.egui_lab2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fweidinger.egui_lab2.custom_views.SecureKeyboard;
import com.fweidinger.egui_lab2.security.EncryptDecrypt;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * The onCreateMethod contains the baseLayout, an empty linear layout, that will dynamically be altered in this application.
     * The onCreate Method also inflates the button_layout layout (Save and Delete) and the EditText_insert layout.
     * <p>
     * The behaviour of the application differentiates between two conditions:
     * 1. Empty Codestore - The EditText for user input and the Save/Delete Button are displayed. The user can freely create a new codestore and encrypt the data with a password.
     * 2. Codestore contains encrypted data - The user must enter a password to view and alter the data within the codestore.
     *
     * @param savedInstanceState
     */
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

        /*
        Checks if the codestore is empty.
        True: The »new Codestore« button will be shown. And the user can then insert his data.
        False: The codestore contains an encrypted string. The user will be asked to enter his password. See method promptForPasswordDialog.
         */
        if (getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", "").matches("")) {
            String newStoreTrue = "New Codestore";
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
            baseLayout.addView(editTextLayout);
            EditText editText = editTextLayout.findViewById(R.id.edittext_input);
            promptForPasswordDialog(editText);
            baseLayout.addView(myButtons);
        }
    }


    /**
     * This onClick method is called when the user presses the Delete or Save buttons.
     * Case button_save:  A dialog will be shown that asks the user for the password
     * Case button_delete: A alert will be displayed asking the user for confirmation
     * @param v the view
     */
    @Override
    public void onClick(View v) {
        EditText editText = findViewById(R.id.edittext_input);
        switch (v.getId()) {
            case (R.id.button_save):
                createPasswordDialog(); // Triggers a dialog asking the user to create a new password.
                break;
            case (R.id.button_delete):
                createDeletionAlert(editText); // Ask for confirmation if the user really wants to delete the codestore contents
                break;
        }
    }

    /**
     * OBSOLETE - ONLY IN TESTING
     * This method will save given data in SharedPreferences with key »textStore«.
     * This method is currently not needed but remains for testing puposes.
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
     * This method will delete the data saved in SharedPreferences with key »textStore«.
     * This is where the application stores the encryptedData saved within the codestore.
     */
    public void deleteCodeFromPreferences() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE);
        SharedPreferences.Editor shrdPrfEditor = sharedPreferences.edit();
        shrdPrfEditor.remove("textStore");
        shrdPrfEditor.apply();
        Log.d("Deletion Test", "deleteCodeFromPreferences:" + getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", ""));
    }

    /**
     * This method returns the string stored in sharedPreferences under the key "textStore".
     *
     * @return the string stored in SharedPreferences with key »textStore«
     */
    public String displayCodeStore() {
        return getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", "");
    }

    /**
     * Creates an alert asking the user for confirmation if he really wants to delete the contents of the codestore.
     * »PositiveButton«: Confirmation - delete the contents of the codestore and close the application.
     * »NegativeButton«: Abort - do not alter the contents of the codestore
     *
     * @param editText - The EditText Object that will be set
     */
    public void createDeletionAlert(final EditText editText) {
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


    /**
     * This Dialog is called when the user opens the application and the codestore is NOT empty.
     * It uses a custom layout »custom_alert« that is inflated and contains:
     * 1. EditText for the user input of the password.
     * 2. Custom Keyboard Layout that will scramble the position of the keypad values and make input more secure.
     *
     * To use the custom keyboard the input connection has to be defined in this
     * <p>
     * The alert contains one Button:
     * »PositiveButton« : This button will take the user input and try to use the insertedPassword. If the password is correct, EncryptDecrypt will be able to decrypt the data in the codestore. Otherwise Exception is thrown
     * and the codestore will shut down.
     * <p>
     * This dialog is NOT cancelable, the user is UNable to override/delete the existing codestore if he does not enter a password.
     *
     * @param editText reference to the editText that will be set with the decrypted contents of the codestore (if the correct password is entered by the user)
     */
    public void promptForPasswordDialog(final EditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_alert, null);
        final EditText input = customLayout.findViewById(R.id.editText);
        builder.setView(customLayout);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputConnection inputConnection = input.onCreateInputConnection(new EditorInfo());
        SecureKeyboard secureKeyboard = customLayout.findViewById(R.id.keyboard);
        secureKeyboard.setInputConnection(inputConnection);
        builder.setMessage(R.string.prompt_password_message)
                .setTitle(R.string.prompt_password_title)
                .setCancelable(false);
        builder.setPositiveButton(R.string.prompt_enter_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EncryptDecrypt encryptDecrypt = null;
                try {
                    encryptDecrypt = new EncryptDecrypt(input.getText().toString());
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    editText.setText(encryptDecrypt.decrypt(getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE).getString("textStore", "")));
                } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                    Toast.makeText(getApplicationContext(), "Wrong Password - Codestore will shutdown", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This dialog will ask the user to create a password that will be used to protect the contents of the codestore.
     * It uses a custom layout »custom_alert« that is inflated and contains:
     * 1. EditText for the user input of the password.
     * 2. Custom Keyboard Layout that will scramble the position of the keypad values and make input more secure.
     * This dialog contains two buttons:
     * »PositiveButton« - Confirmation: Creates a new instance of Class EncryptDecrypt. The encrypt() method is used to encrypt the String inside the EditText.
     * The encrypted String is then saved in the sharedPreferences of the Application.
     * »NegativeButton« - Abort: The user cancels the operation. No password is created, the contents of the EditText will not be saved unless the user repeats the process and enters a valid password.
     */
    public void createPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_alert, null);
        final EditText input = customLayout.findViewById(R.id.editText);
        builder.setView(customLayout);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputConnection inputConnection = input.onCreateInputConnection(new EditorInfo());
        SecureKeyboard secureKeyboard = customLayout.findViewById(R.id.keyboard);
        secureKeyboard.setInputConnection(inputConnection);
        builder.setMessage(R.string.password_dialog_message)
                .setTitle(R.string.password_dialog_title);
        builder.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String encryptedString = null;
                EncryptDecrypt encryptDecrypt = null;
                try {
                    encryptDecrypt = new EncryptDecrypt(input.getText().toString());
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    EditText editText = findViewById(R.id.edittext_input);
                    encryptedString = encryptDecrypt.encrypt(editText.getText().toString());
                } catch (BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("textStore", MODE_PRIVATE);
                SharedPreferences.Editor shrdPrfEditor = sharedPreferences.edit();
                shrdPrfEditor.putString("textStore", encryptedString);
                shrdPrfEditor.apply();
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
