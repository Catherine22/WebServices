package com.catherine.webservices.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Catherine on 2017/9/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class Encryption {
    /**
     * This is used to generate a MD5 signature of a file.
     * You can't just do digest() to convert a file to MD5 String because the size of a file may be large,
     * it's impossible to convert G to bytes array. (You'll run out of memory)
     * <p>
     * Use MessageDigest.update to calculate digest of data coming by parts.
     *
     * @param path where the file is
     * @return md5 signature
     */
    public static String doMd5Safely(String path) {
        try {
            File file = new File(path);
            if (!file.exists())
                return "";

            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;//读到末尾
            while ((len = fis.read(buffer)) != -1) {//说明没有读到流的末尾
                messageDigest.update(buffer, 0, len);
            }
            byte[] digest = messageDigest.digest();//取得当前文件的MD5

            //将byte数组转换成十六进制的字符串
            StringBuilder sb = new StringBuilder();
            //把每一个byte做一个与运算 0xff
            for (byte b : digest) {
                //与运算
                int num = b & 0xff;
                String str = Integer.toHexString(num);
                if (str.length() == 1) {
                    //长度为1时前面补0
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String doMd5Safely(InputStream is) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            byte[] buffer = new byte[1024];
            int len = -1;//读到末尾
            while ((len = is.read(buffer)) != -1) {//说明没有读到流的末尾
                messageDigest.update(buffer, 0, len);
            }
            byte[] digest = messageDigest.digest();//取得当前文件的MD5

            //将byte数组转换成十六进制的字符串
            StringBuilder sb = new StringBuilder();
            //把每一个byte做一个与运算 0xff
            for (byte b : digest) {
                //与运算
                int num = b & 0xff;
                String str = Integer.toHexString(num);
                if (str.length() == 1) {
                    //长度为1时前面补0
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}