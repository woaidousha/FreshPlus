package com.air.update.transaction.control;

import android.content.Context;
import com.air.update.tools.UploadUtil;
import com.air.update.tools.Util;
import com.air.update.transaction.http.GetRequest;
import com.air.update.transaction.http.HttpClient;
import com.air.update.transaction.http.HttpException;
import com.air.update.transaction.http.PostRequest;
import com.air.update.transaction.http.PostUploadRequest;
import com.air.update.transaction.http.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;


/**
 * The NetworkThread handles HTTP requests from the transaction layer
 */
public class NetworkThread extends Thread {
    private static final String LOGTAG = "NetworkThread";

    boolean mKeepRunning;

    private TransInfo mCurrentTransInfo;

    private Context mContext;

    public NetworkThread(Context ctx) {
        mContext = ctx;
    }

    public TransInfo getCurrentTransInfo() {
        return mCurrentTransInfo;
    }

    private static NetworkThread mThreadInstance;

    public static NetworkThread getThreadInstance(Context ctx) {
        if (mThreadInstance == null) {
            mThreadInstance = new NetworkThread(ctx);
        }

        return mThreadInstance;
    }

    @Override
    public synchronized void start() {
        mKeepRunning = true;
        super.start();
    }

    public synchronized void quit() {
        mKeepRunning = false;
    }

    public synchronized boolean isRunning() {
        Util.log("----isRunning-----" + mKeepRunning);
        return mKeepRunning;
    }

    @Override
    public void run() {
        Util.log("----net work thread begin-----" + this.getId());
        while (mKeepRunning) {
            synchronized (Transaction.mTransQueue) {
                mCurrentTransInfo = null;
                while (Transaction.mTransQueue.isEmpty()) {
                    try {
                        Util.log(LOGTAG, "queue is empty, thread wait...");
                        Transaction.mTransQueue.wait();
                        if (!isRunning()) {
                            Util.log("----net work thread exit!!!----");
                            return;
                        }
                        Util.log(LOGTAG, "thread wakeup");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mCurrentTransInfo = Transaction.mTransQueue.poll();
            }

            // Clear cache if the operation is refresh.
            if (mCurrentTransInfo.isRefresh()) {
                mCurrentTransInfo.clearCache();
            } else if (mCurrentTransInfo.hasCacheData()) {
                // if the operation is not refresh, and we have data
                // in the cache, just use the cache data.
                ResultObj obj = new ResultObj(TransHelper.RESULT_DONE,
                        null);
                mCurrentTransInfo.reportResult(obj);
                continue;
            }

            switch (mCurrentTransInfo.mHttpMethod) {
                case TransInfo.HTTP_GET: {
                    Response respGet = null;
                    try {
                        respGet = sendHttpGet(mCurrentTransInfo.genGetRequest());
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        // TODO: handle network/server exceptions, like timeout,
                        // network down...
                        ResultObj obj = new ResultObj(
                                TransHelper.RESULT_ERR_NET_TIMEOUT, null);
                        mCurrentTransInfo.reportResult(obj);
                        break;
                    } catch (HttpException e1) {
                        e1.printStackTrace();
                        // TODO: handle network/server exceptions, like timeout,
                        // network down...
                        ResultObj obj = new ResultObj(
                                TransHelper.RESULT_ERR_NET_DOWN, null);
                        mCurrentTransInfo.reportResult(obj);
                        break;
                    }

                    mCurrentTransInfo.handleResponse(respGet);
                    break;
                }
                case TransInfo.HTTP_POST: {
                    Response respPost = null;
                    try {
                        respPost = sendHttpPost(mCurrentTransInfo.genPostRequest());
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        ResultObj obj = new ResultObj(
                                TransHelper.RESULT_ERR_NET_TIMEOUT, null);
                        mCurrentTransInfo.reportResult(obj);
                        break;
                    } catch (HttpException e1) {
                        e1.printStackTrace();
                        // TODO: handle network/server exceptions, like timeout,
                        // network down...
                        ResultObj obj = new ResultObj(
                                TransHelper.RESULT_ERR_NET_DOWN, null);
                        mCurrentTransInfo.reportResult(obj);
                        break;
                    }

                    mCurrentTransInfo.handleResponse(respPost);
                    break;
                }
                case TransInfo.HTTP_POST_UPLOAD_FILE: {
                    Response respPost = null;
                    try {
                        respPost = sendHttpPostUploadFile(mCurrentTransInfo
                                .genPostUploadRequest());
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        ResultObj obj = new ResultObj(
                                TransHelper.RESULT_ERR_NET_TIMEOUT, null);
                        mCurrentTransInfo.reportResult(obj);
                        break;
                    } catch (HttpException e1) {
                        e1.printStackTrace();
                        // TODO: handle network/server exceptions, like timeout,
                        // network down...
                        ResultObj obj = new ResultObj(
                                TransHelper.RESULT_ERR_NET_DOWN, null);
                        mCurrentTransInfo.reportResult(obj);
                        break;
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    mCurrentTransInfo.handleResponse(respPost);
                    break;
                }
                case TransInfo.HTTP_POST_UPLOAD_DATA: {
                    Response respPost = null;
                    try {
                        respPost = sendHttpPostUploadData(mCurrentTransInfo
                                .genPostUploadRequest());
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        ResultObj obj = new ResultObj(
                                TransHelper.RESULT_ERR_NET_TIMEOUT, null);
                        mCurrentTransInfo.reportResult(obj);
                        break;
                    } catch (HttpException e1) {
                        e1.printStackTrace();
                        // TODO: handle network/server exceptions, like timeout,
                        // network down...
                        ResultObj obj = new ResultObj(
                                TransHelper.RESULT_ERR_NET_DOWN, null);
                        mCurrentTransInfo.reportResult(obj);
                        break;
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    mCurrentTransInfo.handleResponse(respPost);
                    break;
                }
                case TransInfo.HTTP_LOAD_PIC: {
                    Response respPost = null;
                    try {
                        respPost = sendLoadPic(mCurrentTransInfo.genGetRequest());
                    } catch (HttpException e1) {
                        e1.printStackTrace();
                        ResultObj obj = new ResultObj(
                                TransHelper.RESULT_ERR_NET_DOWN, null);
                        mCurrentTransInfo.reportResult(obj);
                        break;
                    } catch (IOException e) {
                        ResultObj obj = new ResultObj(
                                TransHelper.RESULT_ERR_NET_TIMEOUT, null);
                        mCurrentTransInfo.reportResult(obj);
                        e.printStackTrace();
                    }

                    mCurrentTransInfo.handleResponse(respPost);
                    break;
                }
                default:
                    throw new IllegalArgumentException();
            }

            Util.log(LOGTAG, "clear mCurrentTransInfo");
            mCurrentTransInfo = null;
        }

        Util.log("----net work thread exit!!!----");
    }

    private Response sendHttpPostUploadFile(PostUploadRequest post)
            throws SocketTimeoutException, HttpException,
            UnsupportedEncodingException {
        Util.log(post.toString());
        String result = UploadUtil.uploadFile(post.getRequestUrl(false),
                post.mParams, mCurrentTransInfo.getUploadFile());
        return new Response(result);
    }

    private Response sendHttpPostUploadData(PostUploadRequest post)
            throws SocketTimeoutException, HttpException,
            UnsupportedEncodingException {
        Util.log(post.toString());
        String result = UploadUtil.uploadData(post.getRequestUrl(false),
                mCurrentTransInfo.getUploadData());
        return new Response(result);
    }


    private Response sendHttpPost(PostRequest post)
            throws SocketTimeoutException, HttpException {
        Util.log("sendHttpPost:" + post.toString());

        Response respPost = null;

        HttpClient client = new HttpClient();
        if (post.mHeaders != null) {
            client.setHeaders(post.mHeaders);
        }
        respPost = client.post(post.getRequestUrl(), post.mParams);
        return respPost;
    }

    private Response sendHttpGet(GetRequest get) throws SocketTimeoutException,
            HttpException {
        Util.log("sendHttpGet: " + get.getRequestUrl());
        Response respGet = null;

        HttpClient client = new HttpClient();
        if (get.mHeaders != null) {
            client.setHeaders(get.mHeaders);
        }
        respGet = client.get(get.getRequestUrl());
        return respGet;
    }

    private Response sendLoadPic(GetRequest get) throws HttpException,
            IOException {
//		Util.log(LOGTAG, "sendHttpGet: " + get.toString());
        return null;
    }
}