package com.air.update.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;


public class UploadUtil {

    private static final String NAME = "upfile";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
    private static final String END = "\r\n";

    /**
     * @param requestUrl url
     * @param params     post request form data
     * @param file       upload file
     * @return Json
     */
    public static String uploadFile(String requestUrl,
                                    ArrayList<NameValuePair> params, File file) {
        if (!file.exists()) {
            return null;
        }
        String result = null;

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);// 不使用Cache
            conn.setConnectTimeout(6000);// 6秒钟连接超时
            conn.setReadTimeout(6000);// 6秒钟读数据超时
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
                    + "; boundary=" + BOUNDARY);

            StringBuilder sb = new StringBuilder();

            // 上传的表单参数部分，格式请参考文章
            for (NameValuePair entry : params) {// 构建表单字段内容
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\""
                        + entry.getName() + "\"\r\n\r\n");
                sb.append(entry.getValue());
                sb.append("\r\n");
            }

            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.write(sb.toString().getBytes());

            dos.writeBytes("Content-Disposition: form-data; name=\"" + NAME
                    + "\"; filename=\"" + file.getName() + "\"" + "\r\n"
                    + "Content-Type: image/jpeg\r\n\r\n");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }
            dos.writeBytes(END);
            fis.close();
            dos.writeBytes("--" + BOUNDARY + "--\r\n");
            dos.flush();

            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            result = br.readLine();

        } catch (Exception e) {
            Util.log(e.toString());
        }
        return result;

    }

    public static String uploadData(String requestUrl, String data) {
        String result = null;

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);// 不使用Cache
            conn.setConnectTimeout(6000);// 6秒钟连接超时
            conn.setReadTimeout(6000);// 6秒钟读数据超时
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
//			conn.setRequestProperty("Content-type","text/html");


            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            byte[] buffer = data.getBytes();
            dos.write(buffer, 0, buffer.length);
            dos.flush();

            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            result = br.readLine();

        } catch (Exception e) {
            Util.log(e.toString());
        }
        return result;

    }
}
