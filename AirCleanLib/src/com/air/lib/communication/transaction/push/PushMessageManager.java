package com.air.lib.communication.transaction.push;

import com.air.lib.communication.utils.LogTag;

import java.util.HashMap;
import java.util.Map;

public class PushMessageManager {

    private static final String TAG = "PushMessageManager";

    private static final HashMap<PushMessage, Long> sSentMessage = new HashMap<PushMessage, Long>();

    private PushMessageCheckThread mPushMessageCheckThread;

    private static PushMessageManager sInstance;

    private PushMessageManager() {
    }

    public static PushMessageManager getInstance() {
        if (sInstance == null) {
            sInstance = new PushMessageManager();
        }
        return sInstance;
    }

    public void addSentMessage(PushMessage message) {
        synchronized (sSentMessage) {
            LogTag.log(TAG, "add sent message : " +  message.toJson());
            sSentMessage.put(message, System.currentTimeMillis());
            if (sSentMessage.size() == 1 && mPushMessageCheckThread == null) {
                mPushMessageCheckThread = new PushMessageCheckThread();
                mPushMessageCheckThread.start();
            }
            sSentMessage.notifyAll();
        }
    }

    public void removeSentMessage(long messageId) {
        synchronized (sSentMessage) {
            LogTag.log(TAG, "remove sent message : " +  messageId);
            for (Map.Entry<PushMessage, Long> entry : sSentMessage.entrySet()) {
                LogTag.log(TAG, "sent message :" + entry.getKey().toJson());
                if (entry.getKey().getId() == messageId) {
                    LogTag.log(TAG, "has message : " + messageId);
                    sSentMessage.remove(entry.getKey());
                    break;
                }
            }

            if (sSentMessage.isEmpty()) {
                if (mPushMessageCheckThread != null) {
                    mPushMessageCheckThread.quit();
                } else {
                }
            } else {
                LogTag.log(TAG, "sSentMessage not empty : " + sSentMessage.size());
            }
        }
    }

    public void clearAllSentMessage() {
        synchronized (sSentMessage) {
            sSentMessage.clear();
            if (sSentMessage.isEmpty() && mPushMessageCheckThread != null) {
                LogTag.log(TAG, "set message is empty, quit the thread");
                mPushMessageCheckThread.quit();
            }
        }
    }

    class PushMessageCheckThread extends Thread {

        boolean mKeepRunning;

        @Override
        public synchronized void start() {
            mKeepRunning = true;
            super.start();
        }

        public synchronized void quit() {
            mKeepRunning = false;
        }

        public synchronized boolean isRunning() {
            return mKeepRunning;
        }

        @Override
        public void run() {
            while (mKeepRunning) {
                synchronized (sSentMessage) {
                    Map<PushMessage, Long> copy = (Map<PushMessage, Long>) sSentMessage.clone();
                    for (Map.Entry<PushMessage, Long> entry : copy.entrySet()) {
                        long sentTime = entry.getValue();
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - sentTime > PushConstant.PUSH_MESSAGE_SENT_TIME) {
                            PushMessage pushMessage = entry.getKey();
                            if (pushMessage.getId() == PushConstant.MESSAGE_ID_CONNECT) {
                                LogTag.log(TAG, "connect push server timeout, reconnect again and remove the connect message");
                                sSentMessage.remove(pushMessage);
                            } else {
                                LogTag.log(TAG, "send message timeout, resend the push message");
//                                Intent intent = new Intent(MdmService.ACTION_SEND_MESSAGE);
//                                intent.putExtra("push_message", pushMessage);
//                                MdmApp.getInstance().getApplication().startService(intent);
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(PushConstant.PUSH_MESSAGE_SENT_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
