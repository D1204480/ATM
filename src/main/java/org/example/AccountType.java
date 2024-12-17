package org.example;

public enum AccountType {
    NORMAL("普通用戶", Constants.NORMAL_MAX_WITHDRAWAL_PER_TRANSACTION, Constants.NORMAL_MAX_DAILY_WITHDRAWAL),
    GOLD("金卡用戶", Constants.GOLD_MAX_WITHDRAWAL_PER_TRANSACTION, Constants.GOLD_MAX_DAILY_WITHDRAWAL),
    PLATINUM("白金用戶", Constants.PLATINUM_MAX_WITHDRAWAL_PER_TRANSACTION, Constants.PLATINUM_MAX_DAILY_WITHDRAWAL);

    private final String description;
    private final int maxWithdrawalPerTransaction;
    private final int maxDailyWithdrawal;

    AccountType(String description, int maxWithdrawalPerTransaction, int maxDailyWithdrawal) {
        this.description = description;
        this.maxWithdrawalPerTransaction = maxWithdrawalPerTransaction;
        this.maxDailyWithdrawal = maxDailyWithdrawal;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxWithdrawalPerTransaction() {
        return maxWithdrawalPerTransaction;
    }

    public int getMaxDailyWithdrawal() {
        return maxDailyWithdrawal;
    }
}