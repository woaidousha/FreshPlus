package com.air.update.transaction.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import android.util.Pair;

import com.air.update.tools.Util;
import com.air.update.transaction.control.ConstantInfo;

public class GetRequest {
    /**
     * Protocol method: 0 for HTTP, 1 for HTTPS
     */
    // int method;
    public GetRequest(String url) {
        mRequestUrl = url;
    }

    private String mRequestUrl;
    private ArrayList<String> mParams;
    private Map<String, String> mMapParams;

    public ArrayList<Pair<String, String>> mHeaders;

    private String mSessionParam = null;

    private String mTmp;


    public String getTmp() {
        return mTmp;
    }

    public void setTmp(String tmp) {
        this.mTmp = tmp;
    }

    public ArrayList<Pair<String, String>> getHeaders() {
        return mHeaders;
    }

    public void appendParam(String name, String value) {
        if (name == null || value == null) {
            return;
        }

        if (mParams == null)
            mParams = new ArrayList<String>();

        if (mMapParams == null) {
            mMapParams = new HashMap<String, String>();
        }

        mMapParams.put(name, value);
        mParams.add(name + "=" + value);
    }

    public void appendOptionalParam(String name, String value) {
//		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
//			return;
//		}
        appendParam(name, value);
    }

    public void appendPageParam(int start, int count) {
        appendParam(ConstantInfo.PARAM_PAGE_START, "" + start);
        appendParam(ConstantInfo.PARAM_PAGE_COUNT, "" + count);
    }

    public String getRequestUrl() {
        StringBuilder sb = new StringBuilder(mRequestUrl);
        if (!TextUtils.isEmpty(mSessionParam)) {
            sb.append(";" + mSessionParam);
        }
        if (mParams == null || mParams.isEmpty())
            return sb.toString();

        sb.append("?" + mParams.get(0));

        for (int i = 1; i < mParams.size(); i++) {
            sb.append("&" + mParams.get(i));
        }
//		Util.log(sb.toString());
        return sb.toString();
    }

    public String getParamValue(String paramName) {
        return mMapParams.get(paramName);
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

    @Override
    public String toString() {
        StringBuilder headers = new StringBuilder();

        if (mHeaders != null) {
            headers.append("headers: ");
            for (Pair<String, String> p : mHeaders) {
                if (p == null)
                    continue;

                headers.append("[" + p.first + ":" + p.second + "]");
            }
        }
        return "Get Request, url: " + mRequestUrl + ", " + headers.toString();
    }
}