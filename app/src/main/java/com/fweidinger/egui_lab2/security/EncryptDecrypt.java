package com.fweidinger.egui_lab2.security;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecrypt {

    private SecretKeySpec skeySpec;
    private Cipher cipher;

    /**
     * Constructor that takes the the password, adds salt and creates a hash. This hash is then used to generate the key.
     * @param password The input entered by the user
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws UnsupportedEncodingException
     */
    public EncryptDecrypt(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        MessageDigest sha =
                MessageDigest.getInstance("SHA-1");
        byte[] key = Arrays.copyOf(
                sha.digest(("ThisisMySalt1234" + password)
                        .getBytes("UTF-8")), 16);
        skeySpec = new SecretKeySpec(key, "AES");
        cipher = Cipher.getInstance("AES");
    }

    /**
     * This method will take a given string and encrypt it using the SecretKey and AES algorithm.
     * @param clear the clear string that shall be encrypted
     * @return the encrypted String in Base64 encoding
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String encrypt(String clear) throws BadPaddingException, IllegalBlockSizeException {
        byte[] base64 = Base64.encode(clear.getBytes(),
                Base64.NO_WRAP);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] encrypted = cipher.doFinal(base64);
        byte[] encryptedBase64 =
                Base64.encode(encrypted,
                        Base64.DEFAULT);
        return new String(encryptedBase64);
    }

    /**
     * This method decrypts a given string in Base64 encoding
     * @param encryptedBase64 the enrypted String
     * @return the decrypted String
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public String decrypt(String encryptedBase64) throws BadPaddingException, IllegalBlockSizeException,InvalidKeyException{
        byte[] encrypted = Base64.decode(
                encryptedBase64, Base64.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decryptedBase64 =
                new byte[0];
            decryptedBase64 = cipher.doFinal(encrypted);

        byte[] decrypted = Base64.decode(
                decryptedBase64, Base64.NO_WRAP);
        return new String(decrypted);
    }

}



