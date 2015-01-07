/* ---------------------------------------------------------------------------------------------
 *                       Copyright (c) 2013 Capital Alliance Software(Pekall) 
 *                                    All Rights Reserved
 *    NOTICE: All information contained herein is, and remains the property of Pekall and
 *      its suppliers,if any. The intellectual and technical concepts contained herein are
 *      proprietary to Pekall and its suppliers and may be covered by P.R.C, U.S. and Foreign
 *      Patents, patents in process, and are protected by trade secret or copyright law.
 *      Dissemination of this information or reproduction of this material is strictly 
 *      forbidden unless prior written permission is obtained from Pekall.
 *                                     www.pekall.com
 *--------------------------------------------------------------------------------------------- 
*/

package com.air.update.transaction.cache;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.air.update.tools.Util;


public class CacheTimeUtil {

    // all the keys
//	/**
//	 * 3.1.3 绑定用户真实信息接口
//	 */
//	public static final String KEY_BIND_USER_INFO = "BindUserInfoTransInfo";
//	/**
//	 * 3.2.1 获取用户账户信息接口
//	 */
//	public static final String KEY_ACCOUNT_INFO = "AccountInfoTransInfo";

    /**
     * 3.2.5 查询充值明细接口A
     */
    public static final String KEY_ACCOUNT_CHARGE_RECORD = "ChargeRecord";
    /**
     * 3.2.6 查询全部账户明细A
     */
    public static final String KEY_ACCOUNT_ALL = "AllAccount"; //QueryAllAccountTransInfo
    /**
     * 3.2.7 查询账户派奖明细A
     */
    public static final String KEY_ACCOUNT_AWARD = "accountAward";

    /**
     * 3.2.8 查询账户提现明细A
     */
    public static final String KEY_ACCOUNT_WITHDRAWS = "accountWithdraws";

    /**
     * 3.2.9 查询账户投注明细A
     */
    public static final String KEY_ACCOUNT_BET = "accountBet";

    /**
     * 3.3.5.查询追号记录接口A
     */
    public static final String KEY_CHASE_RECORD = "ChaseRecord";

    /**
     * 3.3.6 查询购彩记录接口A
     */
    public static final String KEY_ORDER_RECORD = "LottoOrderRecord";

    /**
     * 3.3.8 查询某彩种历史中奖信息接口A
     */
    public static final String KEY_HISTORY_BONUS = "LottoHistoryBonus";

    /**
     * 3.3.9.查询用户等待开奖彩票信息接口A
     */
    public static final String KEY_WAIT_BONUS = "WaitingLotto";

    /**
     * 3.3.10. 查询用户中奖纪录信息接口A
     */
    public static final String KEY_USER_BONUS = "UserBonus";
    /**
     * 3.4.4 获取账户道具历史接口A
     */
    public static final String KEY_USER_PROPS_HISTORY = "UserPropsHistory";

    /**
     * 3.4.3. 获取帐户道具种类接口A
     */
    public static final String KEY_USER_PROPS = "UserProps";


    //all crespond time, in second
    public static final int TIME_ACCOUNT_ALL = 30;
    public static final int TIME_ACCOUNT_AWARD = 30;
    public static final int TIME_ACCOUNT_WITHDRAWS = 30;
    public static final int TIME_ACCOUNT_CHARGE_RECORD = 30;
    public static final int TIME_ACCOUNT_BET = 30;
    public static final int TIME_CHASE_RECORD = 30;
    public static final int TIME_ORDER_RECORD = 30;
    public static final int TIME_HISTORY_BONUS = 30;
    public static final int TIME_WAIT_BONUS = 30;
    public static final int TIME_USER_BONUS = 30;
    public static final int TIME_USER_PROPS_HISTORY = 30;
    public static final int TIME_KEY_USER_PROPS = 5;

    private static Context mContext;
    private static ContentResolver mResolver;

    public static void setmResolver(ContentResolver mResolver) {
        CacheTimeUtil.mResolver = mResolver;
    }

    private static Map<String, Integer> mCacheTimes;

    static {
        mCacheTimes = new HashMap<String, Integer>();
        mCacheTimes.put(KEY_ACCOUNT_ALL, TIME_ACCOUNT_ALL);
        mCacheTimes.put(KEY_ACCOUNT_AWARD, TIME_ACCOUNT_AWARD);
        mCacheTimes.put(KEY_ACCOUNT_WITHDRAWS, TIME_ACCOUNT_WITHDRAWS);
        mCacheTimes.put(KEY_ACCOUNT_BET, TIME_ACCOUNT_BET);
        mCacheTimes.put(KEY_CHASE_RECORD, TIME_CHASE_RECORD);
        mCacheTimes.put(KEY_ORDER_RECORD, TIME_ORDER_RECORD);
        mCacheTimes.put(KEY_HISTORY_BONUS, TIME_HISTORY_BONUS);
        mCacheTimes.put(KEY_WAIT_BONUS, TIME_WAIT_BONUS);
        mCacheTimes.put(KEY_USER_BONUS, TIME_USER_BONUS);
        mCacheTimes.put(KEY_USER_PROPS_HISTORY, TIME_USER_PROPS_HISTORY);
        mCacheTimes.put(KEY_USER_PROPS, TIME_KEY_USER_PROPS);
        mCacheTimes.put(KEY_ACCOUNT_CHARGE_RECORD, TIME_ACCOUNT_CHARGE_RECORD);

        if (mResolver == null && CacheManager.getmContext() != null) {
            mResolver = CacheManager.getmContext().getContentResolver();
        }
    }

//	public static void initCacheTime(Context ctx){
//		long start = System.currentTimeMillis();
//		mContext = ctx;
//		mResolver = ctx.getContentResolver();
//		ContentValues values = new ContentValues();
//		
//		values.clear();
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_ACCOUNT_ALL);
//		values.put(CacheTime.TIME, -1);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_ACCOUNT_AWARD);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_ACCOUNT_WITHDRAWS);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_ACCOUNT_BET);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_CHASE_RECORD);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_ORDER_RECORD);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_HISTORY_BONUS);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_WAIT_BONUS);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_USER_BONUS);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_USER_PROPS_HISTORY);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		values.put(CacheTime.KEY, CacheTimeUtil.KEY_CHARGE_RECORD);
//		mResolver.insert(CacheTime.CONTENT_URI, values);
//		
//		Log.d("", "time ====== > " + (System.currentTimeMillis() - start));
//	}

    /**
     * store the cache time of the latest time.
     *
     * @param key which one
     * @return
     */
    public static boolean storeCacheTime(String key) {
        ContentValues values = new ContentValues();
//		values.put(CacheTime.TIME, System.currentTimeMillis());
        int count = 0;
        if (mResolver != null) {
//			count = mResolver.update(CacheTime.CONTENT_URI, values, CacheTime.KEY + " = ? ", new String[]{key});
        } else {
            Util.log("mResolver ==== null");
        }
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isReadyUpdate(String key) {
        Util.log(key + " limit key = " + key);
        Integer field = mCacheTimes.get(key);
        Util.log(key + " limit time = " + field);
        if (field == null || field == 0) {
            return true;
        }
        Cursor cursor = null;
        try {
//			cursor = mResolver.query(CacheTime.CONTENT_URI, CacheTime.CACHE_TIME_PROJECT,
//					CacheTime.KEY + " = ? ", new String[] { key }, null);
//			if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//				long lastTime = cursor.getLong(2);
//				if((System.currentTimeMillis() - lastTime) > field * 1000) {
//					return true;
//				}
//			}
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        return false;
    }

}
