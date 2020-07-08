package com.fweidinger.egui_lab2.custom_views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fweidinger.egui_lab2.R;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class serves as more secure Keyboard when the user enters the password.
 * The key-values are assigned randomly, so the position yields no information about the actual value.
 */
public class SecureKeyboard extends LinearLayout implements View.OnClickListener {
    /**
     * @param context context
     */
    public SecureKeyboard(Context context) {
        this(context, null, 0);

    }

    public SecureKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecureKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // The buttons representing the keyboard keys
    private Button Button1;
    private Button Button2;
    private Button Button3;
    private Button Button4;
    private Button Button5;
    private Button Button6;
    private Button Button7;
    private Button Button8;
    private Button Button9;
    private Button Button0;
    private Button ButtonDelete;

    // Sparse Array  that contains the mapping of the ressourceID (Button) to its value
    SparseArray<String> keyValues = new SparseArray<>();

    /**
     * Shuffles an array randomly using pseudorandom int generated by class Random.
     * This array is then used to create the "secure" randomized keyboard.
     * @param array
     */
    static void shuffleArray(int[] array) {
        Random random = new Random();  //creates pseudorandom number
        for (int i = 0; i < array.length; i++) { //iterate over array
            int randomIndexToSwap = random.nextInt(array.length); //create random number between 0 and array.length
            int temp = array[randomIndexToSwap]; // copy array[] @randomIndextoSwap
            array[randomIndexToSwap] = array[i]; // process swap
            array[i] = temp;                    //process swap
        }
    }

    // The InputConnection interface is the communication channel from an InputMethod back to the application that is receiving its input.
    // It is used to perform such things as reading text around the cursor, committing text to the text box, and sending raw key events to the application.
    InputConnection inputConnection;

    /**
     * This method initialises the keyboard and will be called from the constructor.
     * The method randomizes the keyboard number position by calling shuffleArray() and then assigns each button of the keyboard its randomized value.
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        int[] valueArray = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        shuffleArray(valueArray);


        // Initialise Buttons
        LayoutInflater.from(context).inflate(R.layout.keyboard_layout, this, true);
        Button1 = findViewById(R.id.button_1);
        Button2 = findViewById(R.id.button_2);
        Button3 = findViewById(R.id.button_3);
        Button4 = findViewById(R.id.button_4);
        Button5 = findViewById(R.id.button_5);
        Button6 = findViewById(R.id.button_6);
        Button7 = findViewById(R.id.button_7);
        Button8 = findViewById(R.id.button_8);
        Button9 = findViewById(R.id.button_9);
        Button0 = findViewById(R.id.button_0);
        ButtonDelete = findViewById(R.id.button_delete);


        // Set the onClickListener for each Button
        Button1.setOnClickListener(this);
        Button2.setOnClickListener(this);
        Button3.setOnClickListener(this);
        Button4.setOnClickListener(this);
        Button5.setOnClickListener(this);
        Button6.setOnClickListener(this);
        Button7.setOnClickListener(this);
        Button8.setOnClickListener(this);
        Button9.setOnClickListener(this);
        Button0.setOnClickListener(this);
        ButtonDelete.setOnClickListener(this);


        // Impose Mapping of ressourceID to its value
        keyValues.put(R.id.button_1, Integer.toString(valueArray[1]));
        keyValues.put(R.id.button_2, Integer.toString(valueArray[2]));
        keyValues.put(R.id.button_3, Integer.toString(valueArray[3]));
        keyValues.put(R.id.button_4, Integer.toString(valueArray[4]));
        keyValues.put(R.id.button_5, Integer.toString(valueArray[5]));
        keyValues.put(R.id.button_6, Integer.toString(valueArray[6]));
        keyValues.put(R.id.button_7, Integer.toString(valueArray[7]));
        keyValues.put(R.id.button_8, Integer.toString(valueArray[8]));
        keyValues.put(R.id.button_9, Integer.toString(valueArray[9]));
        keyValues.put(R.id.button_0, Integer.toString(valueArray[0]));

        Button1.setText(Integer.toString(valueArray[1]));
        Button2.setText(Integer.toString(valueArray[2]));
        Button3.setText(Integer.toString(valueArray[3]));
        Button4.setText(Integer.toString(valueArray[4]));
        Button5.setText(Integer.toString(valueArray[5]));
        Button6.setText(Integer.toString(valueArray[6]));
        Button7.setText(Integer.toString(valueArray[7]));
        Button8.setText(Integer.toString(valueArray[8]));
        Button9.setText(Integer.toString(valueArray[9]));
        Button0.setText(Integer.toString(valueArray[0]));


    }

    /**
     * The onClick method realises the behaviour of the keypad.
     * If any of the buttons[0-9] is pressed, its value will be taken from the »keyValues« and then inserted at the current cursor position.
     * This method also implements simple behaviour of the button_delete. If a selection is active, the selection will be deleted.
     * Otherwise the character one position before the current cursor position will be deleted.
     *
     * @param v the view
     */
    @Override
    public void onClick(View v) {

        if (inputConnection == null)
            return;   // While the InputConnection is undefined, no action is taken

        if (v.getId() == R.id.button_delete) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                inputConnection.commitText("", 1);
            }
        } else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
        }
    }


    /**
     * Sets the inputConnection. This is usually passed from the calling activity or in this case inside the DialogAlert.
     *
     * @param ic a reference to the inputConnection that channels the connection from the Input Method  to this secureKeyboard
     */
    public void setInputConnection(InputConnection ic) {
        this.inputConnection = ic;
    }
}

