package com.air.lib.communication.transaction.control;

import android.content.Context;
import com.air.lib.communication.transaction.bean.AirBean;
import com.air.lib.communication.transaction.bean.DeviceInfo;
import com.air.lib.communication.transaction.handler.AddUserRequest;
import com.air.lib.communication.transaction.handler.DeviceDataUploadRequest;
import com.air.lib.communication.transaction.handler.DeviceInfoUploadRequest;
import com.air.lib.communication.transaction.handler.GetReportRequest;
import com.air.lib.communication.transaction.handler.PushCmdRequest;
import com.air.lib.communication.transaction.handler.RegisterRequest;
import com.air.lib.communication.utils.LogTag;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Main service for handling transactions
 */
public class Transaction {

    private static final String TAG = "Transaction";

    public static RequestQueue sRequestQueue;

    public static void enqueueRequest(Context context, Request request) {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(context);
        }
        sRequestQueue.add(request);
    }

    public static void getReport(Context context, String deviceUuid) {
        GetReportRequest request = new GetReportRequest();
        request.setShouldCache(false);
        request.setDeviceUuid(deviceUuid);
        enqueueRequest(context, request);
    }

    public static void register(Context context, String deviceUuid) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setDeviceUuid(deviceUuid);
        enqueueRequest(context, registerRequest);
    }

    public static void uploadDeviceInfo(Context context, DeviceInfo deviceInfo, long dbId) {
        DeviceInfoUploadRequest request = new DeviceInfoUploadRequest(dbId);
        request.setDeviceInfo(deviceInfo);
        enqueueRequest(context, request);
    }

    public static void uploadDeviceData(Context context, String deviceUuid, AirBean airBean, long dbId) {
        DeviceDataUploadRequest request = new DeviceDataUploadRequest(dbId);
        request.setDeviceUuid(deviceUuid);
        request.setAirBean(airBean);
        enqueueRequest(context, request);
    }

    public static void addUser(Context context, String username, String password, String boardUuid) {
        AddUserRequest addUserRequest = new AddUserRequest();
        addUserRequest.setUsername(username);
        addUserRequest.setPassword(password);
        addUserRequest.setBoardUuid(boardUuid);
        enqueueRequest(context, addUserRequest);
    }

    public static void pushCmd(Context context, String boardUuid, String msg) {
        PushCmdRequest request = new PushCmdRequest();
        request.setBoardUuid(boardUuid);
        request.setMsg(msg);
        enqueueRequest(context, request);
    }
}
