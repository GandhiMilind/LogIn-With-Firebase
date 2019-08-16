package milind.bacancytechnologypractical;

import android.util.Log;

public class Utils {


    private static String TAG = "LocationApp";
    private static boolean islog = true;

    public static void log(String tag ,String message){

        if(islog){
            Log.e(TAG,tag+" "+message);
        }

    }


}
