package com.saad.genderandemotionrecognizer.mvvm;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.saad.genderandemotionrecognizer.mvvm.interfaces.NetworkCalls;
import com.saad.genderandemotionrecognizer.mvvm.mapping_utils.GenericResponse;

import java.util.List;

public class MvvmUtils {

    public static NetworkCalls getNcs() {
        return APIClient.getRetrofit().create(NetworkCalls.class);
    }

    public static void printGeneralErrors(Context context, List<String> strings) {
        StringBuilder errors = new StringBuilder();
        try {
            for (String err : strings)
                errors.append(err).append("\n");
            Toast.makeText(context, errors, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
