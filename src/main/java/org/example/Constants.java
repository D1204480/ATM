package org.example;

//銀行 ATM 提款限制規則：

//1.提款金額限制：
//單次提款不得超過 20,000 元。
//每日提款限額為 60,000 元。

//2.外幣提款：
//如果提款外幣，收取 1% 手續費，最低 10 元。

//3.非本行提款：
//如果在非本行 ATM 提款，額外收取 15 元手續費。

//4.餘額警示：
//如果帳戶餘額低於 1,000 元，提示「餘額不足」。

//5.密碼錯誤限制：
//如果密碼連續輸入錯誤 3 次，鎖定帳戶並通知用戶聯繫客服。

//6.根據帳戶類型（普通、金卡、白金）設定不同的提款限制：
//普通用戶：每日限額 60,000 元，單次限額 20,000 元。
//金卡用戶：每日限額 120,000 元，單次限額 50,000 元。
//白金用戶：無限額。

//7.根據提款時間計算不同的手續費：
//* 9:00 - 17:00（工作時間:(含 09:00，不含 17:00)）：免額外手續費。
//* 17:00 - 23:00（晚間:含 17:00，不含 23:00）：加收 10 元。
//* 23:00 - 9:00（夜間:含 23:00，不含09:00）：加收 20 元。

//8. 提供3種貨幣(美元,歐元,日幣)的國際提款選項，並根據匯率進行計算。


import java.time.LocalTime;

public class Constants {
    // 提款限額常數
    public static final int NORMAL_MAX_WITHDRAWAL_PER_TRANSACTION = 20000;
    public static final int NORMAL_MAX_DAILY_WITHDRAWAL = 60000;
    public static final int GOLD_MAX_WITHDRAWAL_PER_TRANSACTION = 50000;
    public static final int GOLD_MAX_DAILY_WITHDRAWAL = 120000;
    public static final int PLATINUM_MAX_WITHDRAWAL_PER_TRANSACTION = Integer.MAX_VALUE;
    public static final int PLATINUM_MAX_DAILY_WITHDRAWAL = Integer.MAX_VALUE;

    // 手續費相關常數
    public static final int FOREIGN_CURRENCY_FEE_MIN = 10; // 外幣提款最低手續費
    public static final int NON_BANK_FEE = 15; // 非本行 ATM 提款的額外手續費

    // 其他系統常數
    public static final int LOW_BALANCE_WARNING = 1000; // 餘額警示閾值
    public static final int MAX_PASSWORD_ATTEMPTS = 3; // 密碼錯誤次數上限

    // 時段手續費常數
    public static final int EVENING_EXTRA_FEE = 10;  // 晚間額外手續費
    public static final int NIGHT_EXTRA_FEE = 20;    // 夜間額外手續費

    // 時間區間常數
    public static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);    // 工作時間開始
    public static final LocalTime WORK_END_TIME = LocalTime.of(17, 0);     // 工作時間結束
    public static final LocalTime EVENING_END_TIME = LocalTime.of(23, 0);  // 晚間時間結束
}

