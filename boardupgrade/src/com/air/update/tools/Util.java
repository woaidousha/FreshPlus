package com.air.update.tools;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class Util {
    private static final String TAG = "TestNet : ";

    public static boolean ENABLE_DEBUG = true;

    public static boolean isMobilePhone(String number) {
        if (TextUtils.isEmpty(number) || number.length() != MAX_PHONE_LEN
                || number.charAt(0) != '1')
            return false;
        if (number.charAt(1) != '3' && number.charAt(1) != '4'
                && number.charAt(1) != '5' && number.charAt(1) != '8')
            return false;
        for (int i = 2; i < number.length(); i++) {
            if (number.charAt(i) < '0' || number.charAt(i) > '9')
                return false;
        }

        return true;
    }

    public static void closeCursor(Cursor c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isGif(String filePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            byte[] typeData = new byte[3];
            fis.read(typeData);
            return new String(typeData).toLowerCase().equals("gif");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static Bitmap loadSourceImage(String imageFilePath, int bestW,
                                         int bestH) {
        if (imageFilePath.equals("") || (imageFilePath == null)) {
            return null;
        }
        Bitmap bitmapSrc = null;
        try {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;
            bitmapSrc = BitmapFactory.decodeFile(imageFilePath, op);

            float minSideLength = bestH;
            float maxPiexsLength = bestW * bestH;

            op.inSampleSize = computeSampleSize(op, (int) minSideLength,
                    (int) maxPiexsLength);

            op.inJustDecodeBounds = false;
            bitmapSrc = BitmapFactory.decodeFile(imageFilePath, op);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmapSrc;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getResources().getString(resId), duration);
    }

    /**
     * 自定义toast
     */
    public static void showToast(Context context, String msg, int duration) {
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    public static final int MAX_PHONE_LEN = 11;

    /**
     * 去掉空格减号，取后11位
     */
    public static String getValidPhone(String phone) {
        if (!TextUtils.isEmpty(phone)) {
            phone = phone.replaceAll("[\\s-]", "");
            if (phone.length() > MAX_PHONE_LEN) {
                phone = phone.substring(phone.length() - MAX_PHONE_LEN);
            }
        }
        return phone;
    }

    private static byte[] getMD5(byte[] input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (md != null) {
            md.update(input);
            return md.digest();
        } else
            return null;
    }

    public static String getMD5(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        byte[] bytes = getMD5(str.getBytes());
        String table = "0123456789abcdef";
        StringBuilder ret = new StringBuilder(2 * bytes.length);

        for (int i = 0; i < bytes.length; i++) {
            int b;
            b = 0x0f & (bytes[i] >> 4);
            ret.append(table.charAt(b));
            b = 0x0f & bytes[i];
            ret.append(table.charAt(b));
        }

        return ret.toString();
    }

    public static void sendSms(Context c, String phonenumber, String msg) {
        PendingIntent pi = PendingIntent.getActivity(c, 0, new Intent(), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phonenumber, null, msg, pi, null);// 发送信息到指定号码
    }

    public static void sendSms2(Context c, String phonenumber, String msg) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
                + phonenumber));
        intent.putExtra("sms_body", msg);
        c.startActivity(intent);
    }

    public static void makeCall(Context c, String phonenumber) {
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                + phonenumber));
        c.startActivity(i);
    }


    public static void log(String msg) {
        if (ENABLE_DEBUG) {
            StackTraceElement st = Thread.currentThread().getStackTrace()[3];
            Log.d(TAG, "[---" + st.getFileName() + ": " + st.getLineNumber()
                    + "---]  " + msg);
        }
    }

    ;

    public static void log(String tag, String msg) {
        if (ENABLE_DEBUG) {
            StackTraceElement st = Thread.currentThread().getStackTrace()[3];
            Log.d(TAG + tag,
                    "[---" + st.getFileName() + ": " + st.getLineNumber()
                            + "---]" + msg
            );
        }
    }

    ;

    private static ProgressDialog mProgress;

    public static void showProgressDialog(Context ctx, String msg,
                                          boolean cancelable) {
        if (mProgress != null) {
            if (mProgress.isShowing()) {
                return;
            }
            mProgress = null;
        }
        mProgress = new ProgressDialog(ctx);
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.setMessage(msg);
        mProgress.setCancelable(cancelable);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }

    public static void dismissProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }


    public static String bean2PostForm(Object bean) {
        StringBuffer sb = new StringBuffer();
        for (Method method : bean.getClass().getMethods()) {
            if (method.getName().startsWith("get")
                    && !method.getName().equals("getClass")) {
                try {
                    // System.out.println("method:"+method.getName());
                    Object value = method.invoke(bean, null);
                    if (value == null) {
                        continue;
                    }
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(method.getName().substring(3, 4).toLowerCase());
                    sb.append(method.getName().substring(4)).append("=")
                            .append(value);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static List<BasicNameValuePair> bean2PostForm2(Object bean) {
        List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        StringBuffer sb = new StringBuffer();
        for (Method method : bean.getClass().getMethods()) {
            if (method.getName().startsWith("get")
                    && !method.getName().equals("getClass")) {
                try {
                    // System.out.println("method:"+method.getName());
                    Object value = method.invoke(bean, null);
                    if (value == null) {
                        continue;
                    }
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    String key = method.getName().substring(3, 4).toLowerCase()
                            + method.getName().substring(4);
                    BasicNameValuePair pair = new BasicNameValuePair(key,
                            value.toString());
                    // sb.append(method.getName().substring(3,
                    // 4).toLowerCase());
                    // sb.append(method.getName().substring(4)).append("=").append(value);

                    pairs.add(pair);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return pairs;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static boolean copyFile(File srcFile, File destFile) {
        try {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            } else if (destFile.exists()) {
                destFile.delete();
            }
            InputStream inputStream = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Convert null to empty string ""
     *
     * @param str
     * @return
     */
    public final static String toSafeString(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str;
    }
}
