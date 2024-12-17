package org.example;

public class Account {
    private double balance;
    private int dailyWithdrawnAmount;
    private String password;
    private int incorrectPasswordAttempts;
    private boolean isLocked;
    private AccountType accountType;

    // 建構函式，初始化帳戶餘額、密碼和帳戶類型
    public Account(double initialBalance, String password, AccountType accountType) {
        this.balance = initialBalance;
        this.password = password;
        this.accountType = accountType;
        this.dailyWithdrawnAmount = 0;
        this.incorrectPasswordAttempts = 0;
        this.isLocked = false;
    }

    // 驗證輸入密碼是否正確
    public boolean validatePassword(String inputPassword) {
        if (isLocked) return false;

        if (password.equals(inputPassword)) {
            incorrectPasswordAttempts = 0;
            return true;
        } else {
            incorrectPasswordAttempts++;
            if (incorrectPasswordAttempts >= Constants.MAX_PASSWORD_ATTEMPTS) {
                isLocked = true;
            }
            return false;
        }
    }

    // 檢查帳戶是否鎖定
    public boolean isAccountLocked() {
        return isLocked;
    }

    // 獲取當前餘額
    public double getBalance() {
        return balance;
    }

    // 獲取當日已提款金額
    public int getDailyWithdrawnAmount() {
        return dailyWithdrawnAmount;
    }

    // 獲取帳戶類型
    public AccountType getAccountType() {
        return accountType;
    }

    // 增加當日提款記錄
    public void addDailyWithdrawnAmount(int amount) {
        dailyWithdrawnAmount += amount;
    }

    // 扣減帳戶餘額
    public void deductBalance(double amount) {
        balance -= amount;
    }

    // 檢查餘額是否足夠支付提款金額與手續費
    public boolean canWithdraw(double totalAmount) {
        return balance >= totalAmount;
    }

    // 重置當日提款金額
    public void resetDailyWithdrawnAmount() {
        dailyWithdrawnAmount = 0;
    }

    // 檢查餘額是否低於警示閾值
    public boolean isLowBalance() {
        return balance < Constants.LOW_BALANCE_WARNING;
    }
}