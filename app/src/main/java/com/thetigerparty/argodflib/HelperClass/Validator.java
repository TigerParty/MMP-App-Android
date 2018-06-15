package com.thetigerparty.argodflib.HelperClass;

/**
 * Created by ttpttp on 2015/12/10.
 */
public class Validator {
    public static boolean isNumeric(String value){
        return value.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isIncorrentEmail(String email) {
        CharSequence inputString = email;
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(inputString).matches();
    }
}
