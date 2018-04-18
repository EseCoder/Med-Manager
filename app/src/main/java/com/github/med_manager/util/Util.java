package com.github.med_manager.util;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import com.github.med_manager.database.MedManagerDbHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Ese Udom on 4/6/2018.
 */
public class Util {

    public static final int MED_START_CHECK_CODE1 = 1;
    public static final int MED_INTERVAL_CHECK_CODE = 2;
    public static final int MED_INT_NOTI_CLICK_CODE = 3;
    public static final int MED_START_NOTI_CLICK_CODE = 4;
    public static final int MED_END_NOTI_CLICK_CODE = 5;

    /** Instantiates the database helper class to gain access to database*/
    public static MedManagerDbHelper getStore(Context context) {
        return new MedManagerDbHelper(context);
    }

    /** Hashes a password with SHA-1 hash function */
    public static String computeSHAHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(password.getBytes("ASCII"));
            byte[] data = digest.digest();

            return convertToBase64(data);
        } catch (NoSuchAlgorithmException nsae) {
            Log.e("Utility:computeSha1", "Error initializing SHA1 message digest");
            return null;
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            return null;
        } catch (NullPointerException npe) {
            npe.getMessage();
            return null;
        }
    }

    /** Encodes data with Base 64 Encoding scheme */
    private static String convertToBase64(byte[] data) {
        return Base64.encodeToString(data, 0, data.length, Base64.DEFAULT);
    }


    /**Formats date from milliseconds to human readable form */
    public static String timeInMillisToDate(long timeInMillis, Context context) {
        return DateUtils.formatDateTime(context, timeInMillis, DateUtils.FORMAT_SHOW_DATE |
                DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_ALL);
    }

    /**Formats date to milliseconds */
    public static long dateToTimeInMillis(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTime dt = formatter.parseDateTime(dateStr);
        return dt.getMillis();
    }

    /** Utility function to check validity of an email address */
    public static boolean verifyEmail(String email) {
        return email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
