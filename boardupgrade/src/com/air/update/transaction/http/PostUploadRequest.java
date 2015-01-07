package com.air.update.transaction.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;
import android.util.Pair;

import com.air.update.tools.Util;


public class PostUploadRequest {

    private static final String SESSION_ID = "jsessionid";
    private String mSessionParam = null;

    /**
     * Protocol method: 0 for HTTP, 1 for HTTPS
     */
    // int method;
    public PostUploadRequest(String url) {
        mRequestUrl = url;
    }

    public String mRequestUrl;
    public ArrayList<NameValuePair> mParams;

    public ArrayList<NameValuePair> getParams() {
        return mParams;
    }

    public ArrayList<Pair<String, String>> mHeaders;

    // String mPostBody;

    public ArrayList<Pair<String, String>> getHeaders() {
        return mHeaders;
    }

    public void appendSessonId(String sessionId) {
        mSessionParam = SESSION_ID + "=" + sessionId;
    }

    @Override
    public String toString() {
        StringBuilder params = new StringBuilder();
        StringBuilder headers = new StringBuilder();

        if (mParams != null) {
            params.append("Params: ");
            for (NameValuePair p : mParams) {
                if (p == null)
                    continue;

                if ("password".equals(p.getName()))
                    continue;

                params.append("[" + p.getName() + ":" + p.getValue() + "]");
            }
        }

        if (mHeaders != null) {
            headers.append("headers: ");
            for (Pair<String, String> p : mHeaders) {
                if (p == null)
                    continue;

                headers.append("[" + p.first + ":" + p.second + "]");
            }
        }
        return "PostRequest, url: " + mRequestUrl + ", " + params.toString()
                + ", " + headers.toString();
    }

    public void appendHeaders(String name, String value) {
        if (TextUtils.isEmpty(name))
            return;

        appendHeaders(new Pair<String, String>(name,
                Util.toSafeString(value)));
    }

    public void appendHeaders(Pair<String, String>... pairs) {
        if (pairs == null)
            return;

        for (Pair<String, String> p : pairs) {
            if (p == null)
                continue;

            if (mHeaders == null)
                mHeaders = new ArrayList<Pair<String, String>>();

            mHeaders.add(p);
        }
    }

    public void appendParams(List<BasicNameValuePair> pairs) {
        for (BasicNameValuePair p : pairs) {
            if (p == null)
                continue;

            if (mParams == null)
                mParams = new ArrayList<NameValuePair>();

            mParams.add(p);
        }
    }

    public void appendOptionParam(BasicNameValuePair... pairs) {
        for (BasicNameValuePair p : pairs) {
            if (p == null || TextUtils.isEmpty((String) p.getValue()))
                continue;

            if (mParams == null)
                mParams = new ArrayList<NameValuePair>();

            mParams.add(p);
        }
    }

    public String getRequestUrl(boolean needParams) {
        StringBuilder sb = new StringBuilder(mRequestUrl);

        if (!TextUtils.isEmpty(mSessionParam)) {
            sb.append(";" + mSessionParam);
        }

        if (mParams == null || mParams.isEmpty() || !needParams)
            return sb.toString();

        sb.append("?" + mParams.get(0));

        for (int i = 1; i < mParams.size(); i++) {
            sb.append("&" + mParams.get(i));
        }
        Util.log(sb.toString());
        return sb.toString();
    }
}