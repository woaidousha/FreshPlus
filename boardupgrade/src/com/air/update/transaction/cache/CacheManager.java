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

import android.content.Context;

public class CacheManager {
    public static Context mContext;
//	public static ArrayList<LotteryType> mlottery_type;

    static {
        CacheManager mCacheManager = new CacheManager();
        InitAllArrayList();

    }

    public static Context getmContext() {
        return mContext;
    }

    public static void setmContext(Context mContext) {
        CacheManager.mContext = mContext;
    }

    public static void ClearAll() {
//		if (mlottery_type != null)
//			mlottery_type.clear();
    }

    public static void InitAllArrayList() {
//		mlottery_type = new ArrayList<LotteryType>();
    }


    public static void clearUserData() {
    }
}
