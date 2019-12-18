package pucmm.temas.especiales.e_commerce_app.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public final class SystemProperties {
    private static final String URL = "http://ec2-3-86-40-181.compute-1.amazonaws.com";
    private static final int PORT = 6789;


    private SystemProperties() {
    }

    public static String getResource(final String resource) {
        return String.format("%s:%s/%s", URL, PORT, resource);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static CharSequence[] getOptions() {
        final CharSequence[] options = new CharSequence[PhotoOptions.values().length];
        final AtomicInteger atomic = new AtomicInteger(0);

        for (PhotoOptions obj : Arrays.asList(PhotoOptions.values())) {
            options[atomic.getAndIncrement()] = obj.getValue();
        }

        return options;
    }

    public static String getDecimalFormat(final Object obj) {
        return new DecimalFormat("#,##0.00").format(obj);
    }




    public static String generateItemCode() {
        final String time = new Date().getTime() + "";

        final int[] position = {3, 10, 13};

        return addChar(time, '-', position);
    }

    public static String addChar(String time, char ch, int[] position) throws StringIndexOutOfBoundsException {
        final StringBuilder value = new StringBuilder();
        int positionInit = 0;

        for (int i = 0; i < position.length; i++) {
            if (position[i] > time.length()) {
                throw new StringIndexOutOfBoundsException("String index out of range: " + position[i]);
            }

            value.append(time.substring(positionInit, position[i]));
            positionInit = position[i];

            if (i < position.length - 1) {
                value.append(ch);
            }
        }

        return value.toString();
    }
}
