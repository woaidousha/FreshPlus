package com.air.update.transaction.control;

import android.content.Context;
import android.widget.Toast;

import com.air.update.R;
import com.air.update.tools.Util;

public class TransHelper {
    public static final int RESULT_ERR_UNKNOWN = 0x00;

    public static final int RESULT_DONE = 0x01; // ("Done."), //

    public static final int RESULT_LOADING = 0x02; // ("Loading..."), //

    // TODO: define more network errors
    public static final int RESULT_ERR_NET_GENERAL = 0x03; // ("General network error!"),
    // //
    public static final int RESULT_ERR_NET_DOWN = 0x04; // ("Network down!"),
    // //
    public static final int RESULT_ERR_NET_TIMEOUT = 0x05; // ("Network timeout!"),
    // //
    public static final int RESULT_ERR_NET_NULL_RESP = 0x06; // ("Got null response from network!"),
    // //

    // TODO: replace RESULT_ERR_SVR_GENERAL with specific errors
    public static final int RESULT_ERR_SVR_GENERAL = 0x07; // ("General server error!"),
    // //
    public static final int RESULT_ERR_NULL_POINTER_EXCEPTION = 0x08; // ("NullPointerException!"),
    // //
    public static final int RESULT_ERR_SVR_JOSON_SYNTAX_EXCEPTION = 0x09; // ("Joson syntax exception!"),

    /**
     * 上传图片视频失败
     */
    public static final int RESULT_UPLOAD_MEDIA_FAILED = 0x10;


    public static void dealResultError(int result, Context context) {
        switch (result) {
            case RESULT_ERR_NET_DOWN:
                Toast.makeText(context, R.string.error_network_link,
                        Toast.LENGTH_SHORT).show();
                break;
            case RESULT_ERR_NET_TIMEOUT:
                Toast.makeText(context, R.string.error_network_timeout,
                        Toast.LENGTH_SHORT).show();
                break;
            case RESULT_ERR_SVR_JOSON_SYNTAX_EXCEPTION:
                Toast.makeText(context, R.string.error_parse_error,
                        Toast.LENGTH_SHORT).show();
                break;
            case RESULT_ERR_NET_NULL_RESP:
                Toast.makeText(context, R.string.error_result_empty,
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                Util.log("error result === > " + result);
                Toast.makeText(context, R.string.error_unknown,
                        Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
