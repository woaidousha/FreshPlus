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

public class StateAndErrorCode {

    /**
     * 系统错误.
     */
    public static final int ERROR_SYSTEM = 10001;
    /**
     * 参数为空.
     */
    public static final int ERROR_EMPTY_PARAM = 20001;
    /**
     * 参数格式不正确.
     */
    public static final int ERROR_INCORRECT_FORMAT_PARAM = 20002;
    /**
     * 参数超过长度.
     */
    public static final int ERROR_OVERLENGTH_PARAM = 20003;
    /**
     * 用户认证失败.
     */
    public static final int ERROR_ILLEGAL_USER = 20004;
    /**
     * 无效的订单号.
     */
    public static final int ERROR_INVALID_ORDERNO = 20005;
    /**
     * 用户被踢出.
     */
    public static final int ERROR_TOKEN_KICK_OUT = 20006;
    /**
     * 用户和密码不匹配.
     */
    public static final int ERROR_NOT_MATCH_USER_WITH_PASSWORD = 20007;

    /**
     * 数据字典不需要同步.
     */
    public static final int ERROR_STRING_TABLE_NO_NEED_SYNC = 20008;

    /**
     * 金额超出范围.
     */
    public static final int ERROR_OUT_OF_RANGE = 20009;

    /**
     * 用户已经存在.
     */
    public static final int ERROR_EXIST_USER = 20101;
    /**
     * 您输入的验证码不正确，请检查.
     */
    public static final int ERROR_INCORRECT_VERIFICATION_CODE = 20102;
    /**
     * 用户不存在.
     */
    public static final int ERROR_UNEXIST_USER = 20103;

    /**
     * 用户名与手机号不匹配.
     */
    public static final int ERROR_NOT_MATCH_USER_WITH_PHONENUM = 20104;

    /**
     * 更新密码时候，新旧密码一样
     */
    public static final int ERROR_OLDPASS_EQUAL_NEWPASS = 20105;

    /**
     * 原密码错误.
     */
    public static final int ERROR_INCORRECT_OLD_PASSWORD = 20106;
    /**
     * 用户未绑定手机号.
     */
    public static final int ERROR_NOT_BIND_PHONENUM = 20107;
    /**
     * 手机号未被绑定.
     */
    public static final int ERROR_PHONENUM_NOT_BEEN_BINDED = 20108;
    /**
     * 没有可用的支付渠道.
     */
    public static final int ERROR_NO_AVAILABLE_PAY_CHANNELS = 20201;
    /**
     * 支付方式不可用.
     */
    public static final int ERROR_NO_AVAILABLE_PAY_TYPE = 20202;
    /**
     * 没有可选的彩种.
     */
    public static final int ERROR_NO_CHOICE_LOTTERY = 20301;
    /**
     * 追投已到期.
     */
    public static final int ERROR_TIMEOUT_CHASE = 20302;

    /**
     * 道具不存在或者已经停售
     */
    public static final int ERROR_PROP_NOT_EXIST = 20401;
    /**
     * 购买道具现金金额不足
     */
    public static final int ERROR_PROP_NO_MORE_CASH = 20402;
    /**
     * 没有中奖.
     */
    public static final int ERROR_NO_BONUS = 20501;
    /**
     * 已经抽过奖.
     */
    public static final int ERROR_IS_BONUSED = 20502;

    /**
     * 任务不存在
     */
    public static final int ERROR_TASK_NO_EXIST = 20503;

    /**
     * 现金账户余额不满足提现条件.
     */
    public static final int ERROR_NO_BALANCE_CASH = 20601;
    /**
     * 用户真实信息不完整.
     */
    public static final int ERROR_INCORRECT_USERINFO = 20602;
    /**
     * 输入的用户名与登录的用户不一致.
     */
    public static final int ERROR_NOT_CONSISTENT_USERNANME = 20603;

    /**
     * 应用程序是最新版本，不需要更新
     */
    public static final int ERROR_STRING_APK_NO_NEED_SYNC = 20701;

    /**
     * 新手普通用户.
     */
    public static final int NOVICE_NORMAL_USER = 30101;
    /**
     * 新手VIP用户.
     */
    public static final int NOVICE_VIP_USER = 30102;
    /**
     * 普通初级用户.
     */
    public static final int NORMAL_PRIMARY_USER = 30201;
    /**
     * 普通中级用户.
     */
    public static final int NORMAL_SECONDARY_USER = 30202;
    /**
     * 普通高级用户.
     */
    public static final int NORMAL_SENIOR_USER = 30203;
    /**
     * VIP初级.
     */
    public static final int VIP_PRIMARY_USER = 30201;
    /**
     * VIP中级.
     */
    public static final int VIP_SECONDARY_USER = 30202;
    /**
     * VIP高级.
     */
    public static final int VIP_SENIOR_USER = 30203;
    /**
     * 未下单.
     */
    public static final int NO_ORDER = 40101;
    /**
     * 已发单.
     */
    public static final int IS_ORDERED = 40102;
    /**
     * 下单失败.
     */
    public static final int FAIL_ORDER = 40103;
    /**
     * 部分流单.
     */
    public static final int PART_FLOW_ORDER = 40104;
    /**
     * 未出票.
     */
    public static final int NO_BILL = 40201;
    /**
     * 出票失败.
     */
    public static final int IS_BILLED = 40202;
    /**
     * 部分出票失败.
     */
    public static final int PART_FAIL_BILL = 40203;
    /**
     * 出票成功.
     */
    public static final int SUCCESS_BILL = 40204;

    /**
     * 账户金额不够
     */
    public static final int NOT_ENOUGH_MONEY = 40301;

    /**
     * 用户下单成功
     */
    public static final int ORDER_SUBMIT_SUCCESSED = 40302;

    /**
     * 用户下单失败
     */
    public static final int ORDER_SUBMIT_FAILED = 40303;

    /**
     * 用户未绑定手机.
     */
    public static final int NO_BIND_PHONE = 50101;
    /**
     * 用户已绑定手机.
     */
    public static final int IS_BINDED_PHONE = 50102;
    /**
     * 手机绑定中.
     */
    public static final int BINDING_PHONE = 50103;
    /**
     * 该手机号已有用户绑定.
     */
    public static final int PHONE_BINDED_TO_USER = 50110;
    /**
     * verify_token 验证失败.
     */
    public static final int NO_PHONE_BINDED_TO_USER = 50111;
    /**
     * 支付处理中.
     */
    public static final int PROCESSING_PAY = 50201;
    /**
     * 支付成功.
     */
    public static final int SUCCESS_PAY = 50202;
    /**
     * 支付失败.
     */
    public static final int FAIL_PAY = 50203;
    /**
     * 等待开奖.
     */
    public static final int WAIT_BOUNS = 50301;
    /**
     * 已中奖.
     */
    public static final int IS_BOUNSED = 50302;
    /**
     * 未中奖.
     */
    public static final int NO_BOUNS = 50303;
    /**
     * 任务进行中.
     */
    public static final int TASKING = 50401;
    /**
     * 任务可领奖.
     */
    public static final int TASK_AWARDING = 50402;
    /**
     * 任务已领奖.
     */
    public static final int TASK_IS_AWARDED = 50403;
    /**
     * 任务未开始.
     */
    public static final int TASK_NOT_START = 50404;
    /**
     * 任务奖励赠金.
     */
    public static final int TASK_AWARD_GRANT = 50501;
    /**
     * 任务奖励省钱卡.
     */
    public static final int TASK_AWARD_CARD = 50502;
    /**
     * 道具购买所得.
     */
    public static final int GET_WAY_BUY = 50601;
    /**
     * 道具任务赠予.
     */
    public static final int GET_WAY_TASK = 50602;
    /**
     * 道具摇奖所得.
     */
    public static final int GET_WAY_SHAKE = 50603;
    /**
     * 购买彩票.
     */
    public static final int BUY_LOTTRY_ = 50701;
    /**
     * 购买道具.
     */
    public static final int BUY_PROP = 50702;
    /**
     * 充值.
     */
    public static final int RECHARGE = 50703;
    /**
     * 提现.
     */
    public static final int CASH = 50704;
    /**
     * 派奖.
     */
    public static final int AWARD = 50705;

    /**
     * 返赠金.
     */
    public static final int GRAND = 50706;

    /**
     * 购买彩票退款.
     */
    public static final int BUY_LOTTRY_RETURN = 50707;

    /**
     * 购买彩票中奖停止追投退款.
     */
    public static final int BUY_LOTTRY_STOP_CHASE_RETURN = 50708;

    /**
     * 取消追投退款
     */
    public static final int BUY_LOTTRY_CANCEL_CHASE_RETURN = 50709;

    /**
     * 购买彩票冻结.
     */
    public static final int BUY_LOTTRY_FREEZED = 50710;

    /**
     * 追投投注扣款
     */
    public static final int BUY_LOTTRY_CHASE = 50711;

    /**
     * 普通投注扣款
     */
    public static final int BUY_LOTTRY_NORMAL = 50712;

    /**
     * 追投下单退款
     */
    public static final int BUY_LOTTRY_CHASE_RETURN = 50713;

    /**
     * 普通投注退款
     */
    public static final int BUY_LOTTRY_NORMAL_RETURN = 50714;

    /**
     * 某一期追投
     */
    public static final int BUY_LOTTRY_ONE_CHASE = 50715;

    /**
     * 任务返赠金.
     */
    public static final int TASK_GRAND = 50716;

    /**
     * 摇奖返赠金.
     */
    public static final int SHAKED_GRAND = 50717;

    /**
     * 派奖返赠金.
     */
    public static final int AWARD_GRAND = 50718;

    /**
     * 使用道具返赠金.
     */
    public static final int PROP_GRAND = 50719;

    /**
     * 未派奖.
     */
    public static final int WAITING_AWARD = 50801;
    /**
     * 派奖中
     */
    public static final int AWARD_ING = 50802;
    /**
     * 已派奖.
     */
    public static final int IS_AWARDED = 50803;
    /**
     * 摇奖未中奖.
     */
    public static final int SHAKE_NO_AWARD = 50901;
    /**
     * 已经摇过奖.
     */
    public static final int IS_SHAKEED = 50902;
    /**
     * 摇奖中奖.
     */
    public static final int SHAKE_AWARD = 50903;
    /**
     * 用户未摇奖
     */
    public static final int NO_SHAKE = 50904;
    /**
     * 追投已结束.
     */
    public static final int CHASE_END = 51001;
    /**
     * 追投进行中.
     */
    public static final int CHASEING = 51002;
    /**
     * 追投已停止.
     */
    public static final int CHASE_STOP = 51003;
    /**
     * 追投停止审核中.
     */
    public static final int CHASE_AUDITING = 51004;
    /**
     * 追投因中奖停止.
     */
    public static final int CHASE_AWARD_STOP = 51005;

    /**
     * 追投未开始.
     */
    public static final int CHASE_NOT_STATTED = 51006;

    /**
     * 追投取消.
     */
    public static final int CHASE_CANCELED = 51007;

    /**
     * 已经投注.
     */
    public static final int CHASE_SUBMITTED = 51008;

    /**
     * 默认账户.
     */
    public static final int DEFAULT_ACCOUNT = 51101;
    /**
     * 现金账户.
     */
    public static final int CASH_ACCOUNT = 51102;
    /**
     * 赠金账户.
     */
    public static final int GRANT_ACCOUNT = 51103;
    /**
     * 支付宝支付.
     */
    public static final int PAY_TYPE_Alipay = 51201;
    /**
     * 银联快捷支付.
     */
    public static final int PAY_TYPE_UNIONPAY = 51202;
    /**
     * 手机支付.
     */
    public static final int PAY_TYPE_PHOEN = 51203;
    /**
     * 道具正发售.
     */
    public static final int PROP_SELL = 51301;
    /**
     * 道具已停售.
     */
    public static final int PROP_STOP_SELL = 51302;
    /**
     * 道具已使用.
     */
    public static final int PROP_USED = 51303;
    /**
     * 道具未使用.
     */
    public static final int PROP_NOT_USED = 51304;
    /**
     * 提现已冻结.
     */
    public static final int CASH_FREEZED = 51401;
    /**
     * 提现审核成功.
     */
    public static final int CASH_SUCCESS = 51402;
    /**
     * 提现审核失败.
     */
    public static final int CASH_FAILED = 51403;
    /**
     * 提现申请成功
     */
    public static final int CASH_APLAY_SUCCESS = 51404;
    /**
     * 提现申请失败
     */
    public static final int CASH_APLAY_FAILED = 51405;
    /**
     * 彩种在售.
     */
    public static final int LOTTRY_SELLING = 51501;
    /**
     * 彩种停售.
     */
    public static final int LOTTRY_STOP_SELLING = 51502;

    /**
     * 预售期.
     */
    public static final int PRE_AWARD_PERIOD = 51600;
    /**
     * 已开新期.
     */
    public static final int START_NEW_AWARD_PERIOD = 51601;
    /**
     * 暂停奖期.
     */
    public static final int STOP_AWARD_PERIOD = 51602;
    /**
     * 截止投注.
     */
    public static final int CLOSE_CHASE = 51603;
    /**
     * 摇出奖号.
     */
    public static final int SHAKE_AWARD_NUM = 51604;
    /**
     * 兑奖中.
     */
    public static final int AWARDING = 51605;
    /**
     * 结期兑奖.
     */
    public static final int TIME_TO_AWARD = 51606;

    /**
     * 未返奖.
     */
    public static final int NO_AWARD = 51800;

    /**
     * 返奖中(小奖).
     */
    public static final int RETURN_AWARDING = 51801;
    /**
     * 返奖成功(小奖.
     */
    public static final int RETURN_AWARD_SUCCESS = 51802;
    /**
     * 返奖失败(小奖).
     */
    public static final int RETURN_AWARD_FAILED = 51803;
    /**
     * 返奖类型，小奖
     */
    public static final int AWARD_TYPE_SMALL = 51807;
    /**
     * 返奖类型，大奖
     */
    public static final int AWARD_TYPE_BIG = 51808;
    /**
     * 返奖操作者为系统
     */
    public static final int SYSTEM_AWARD = 51809;
    /**
     * 待审核.
     */
    public static final int RETURN_AWARD_AUDITING = 51811;
    /**
     * 客服审核通过.
     */
    public static final int GUEST_AUDITING_PASS = 51812;
    /**
     * 客服驳回.
     */
    public static final int GUEST_AUDITING_NOT_PASS = 51813;

    /**
     * 财务汇出.
     */
    public static final int FINANCES_AUDITING_PASS = 51814;
    /**
     * 财务驳回.
     */
    public static final int FINANCES_AUDITING_NOT_PASS = 51815;
    /**
     * 返奖挂起.
     */
    public static final int RETURN_AWARD_HANG = 51816;

    /**
     * 返奖成功(大奖).
     */
    public static final int RETURN_BIG_AWARD_SUCCESS = 51817;

    /**
     * 任务纬度类型，购彩金额
     */
    public static final int DIM_PURCHASE_LOTTERY = 51901;

    /**
     * 任务纬度类型，充值金额
     */
    public static final int DIM_RECHARGE_MONEY = 51902;

    /**
     * 任务纬度类型，中奖次数
     */
    public static final int DIM_BONUS_COUNT = 51903;

    /**
     * 任务纬度类型，单次中奖次数
     */
    public static final int DIM_PER_BONUS_MONEY = 51904;
    /**
     * 标志数据库某一条记录有效
     */
    public static final int RECORD_VALID = 52000;
    /**
     * 标志数据库某一条记录无效
     */
    public static final int RECORD_INVALID = 52001;

    /**
     * 当前TOKEN可用
     */
    public static final int TOKEN_AVAILABLE = 52101;
    /**
     * 当前TOKEN被踢出
     */
    public static final int TOKEN_KICK_OUT = 52102;
    /**
     * 当前TOKEN失效
     */
    public static final int TOKEN_UNAVAILABLE = 52103;

    /**
     * 短信发送次数达到上限
     */
    public static final int COUNT_LIMIT = 52104;


    /**
     * 短信发送失败
     */
    public static final int SEND_SMS_FAIL = 52105;

    /**
     * 投注方式：代购
     */
    public static final int BET_TYPE = 53100;

    /**
     * 数据版本表中的数据类型：数据字典
     */
    public static final int DATA_VERSION_TYPE_DICTIONARY = 54001;

    /**
     * 数据版本表中的数据类型：应用程序apk
     */
    public static final int DATA_VERSION_TYPE_APK = 54002;


    /**
     * 新手摇奖奖品派奖成功
     */
    public static final int FRESH_USER_SHAKE_AWARDED_SUCCESS = 55001;

    /**
     * 新手摇奖奖品派奖失败
     */
    public static final int FRESH_USER_SHAKE_AWARDED_FAIL = 55002;

    /**
     * 不是新手，未派奖
     */
    public static final int FRESH_USER_SHAKE_AWARDED_NO = 55003;

    /**
     * 任务级别 铜牌任务
     */
    public static final int TASK_LEVEL_1 = 80001;

    /**
     * 任务级别 银牌任务
     */
    public static final int TASK_LEVEL_2 = 80002;
    /**
     * 任务级别 金牌任务
     */
    public static final int TASK_LEVEL_3 = 80003;
    /**
     * 任务级别 钻石任务
     */
    public static final int TASK_LEVEL_4 = 80004;
    /**
     * 任务级别 终极任务
     */
    public static final int TASK_LEVEL_5 = 80005;
    /**
     * 错误的运营商代码
     */
    public static final int ERROR_OPERATOR_CODE = 20801;
}
