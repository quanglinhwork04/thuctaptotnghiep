package com.booking_care.utils;


import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Utils {

    public static String dateToString(Date date) {
        return null;
    }

    public static Date stringToDate(String dateStr) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
            return format.parse(dateStr);
        } catch (Exception e) {
            System.out.println("lỗi parse date");
        }
        return null;
    }

    public static Date stringToDate2(String dateStr) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.parse(dateStr);
        } catch (Exception e) {
            System.out.println("lỗi parse date");
        }
        return null;
    }

    public static String encode(String key, String data) {
        try {

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String createUserName(String username) {
        String output = "";

        username = removeAccent(username.toLowerCase());

        String[] splitName = username.split(" ");

        output = splitName[splitName.length - 1] + ".";

        for (int i = 0; i < splitName.length - 1; i++) {
            output += splitName[i].charAt(0);
        }

        return output;
    }

    public static String removeAccent(String s) { String temp = Normalizer.normalize(s, Normalizer.Form.NFD); Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+"); temp = pattern.matcher(temp).replaceAll("");
        return temp.replaceAll("đ", "d"); }

    public static void main(String[] args) {
        System.out.println(createUserName("Đặng Duy Tuấn"));
    }
}