package com.catherine.webservices.entities;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DES_Capsule extends Cipher {
    private String DESI_Enc;
    private String DESK_Enc;
    private String DESI_Dec;
    private String DESK_Dec;

    public DES_Capsule(String DESI_Enc, String DESK_Enc, String DESI_Dec, String DESK_Dec) {
        this.DESI_Enc = DESI_Enc;
        this.DESK_Enc = DESK_Enc;
        this.DESI_Dec = DESI_Dec;
        this.DESK_Dec = DESK_Dec;
    }

    public String getDESI_Enc() {
        return DESI_Enc;
    }

    public String getDESI_Dec() {
        return DESI_Dec;
    }

    public String getDESK_Enc() {
        return DESK_Enc;
    }

    public String getDESK_Dec() {
        return DESK_Dec;
    }

    @Override
    public String encrypt(String message) {
        try {
            javax.crypto.Cipher ecipher = javax.crypto.Cipher.getInstance("DES/CBC/PKCS5Padding");
//				DESKeySpec desKeySpec = new DESKeySpec(context.getResources().getString(R.string.desk).getBytes("UTF-8"));
            DESKeySpec desKeySpec = new DESKeySpec(DESK_Enc.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
//				IvParameterSpec iv = new IvParameterSpec(context.getResources().getString(R.string.desi).getBytes("UTF-8"));
            IvParameterSpec iv = new IvParameterSpec(DESI_Enc.getBytes("UTF-8"));
            ecipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, iv);

            byte[] utf8 = message.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);
            enc = BASE64EncoderStream.encode(enc);
            return new String(enc);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String decrypt(String message) throws Exception {
        byte[] bytesrc = BASE64DecoderStream.decode(message.getBytes());

        javax.crypto.Cipher dcipher = javax.crypto.Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(DESK_Dec.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(DESI_Dec.getBytes("UTF-8"));
        dcipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] de = dcipher.doFinal(bytesrc);
        return new String(de);
    }
}