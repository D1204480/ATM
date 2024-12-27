package org.example;

import java.time.LocalTime;

public class ATMOperations {

  // 驗證提款金額是否符合規則
  public static boolean validateWithdrawal(Account account, double amount, CurrencyType currencyType) {
    // 將外幣金額轉換為台幣進行驗證
    double amountInTWD = currencyType.convertToTWD(amount);
    AccountType accountType = account.getAccountType();

    if (amountInTWD > accountType.getMaxWithdrawalPerTransaction()) {
      System.out.println(accountType.getDescription() + "單次提款不得超過 " +
          accountType.getMaxWithdrawalPerTransaction() + " TWD！");
      return false;
    }

    if (account.getDailyWithdrawnAmount() + amountInTWD > accountType.getMaxDailyWithdrawal()) {
      System.out.println("今日提款已超過" + accountType.getDescription() +
          "每日限額：" + accountType.getMaxDailyWithdrawal() + " TWD！");
      return false;
    }

    return true;
  }

  // 計算時段手續費
  static double calculateTimeBasedFee(LocalTime currentTime) {
    if (currentTime.isBefore(Constants.WORK_START_TIME) ||
        currentTime.compareTo(Constants.EVENING_END_TIME) >= 0) {
      // 23:00 - 09:00 夜間時段
      return Constants.NIGHT_EXTRA_FEE;
    } else if (currentTime.compareTo(Constants.WORK_END_TIME) >= 0){
      // 17:00 - 23:00 晚間時段
      return Constants.EVENING_EXTRA_FEE;
    }
    return 0;  // 其他時間（09:00-17:00）
  }

  // 計算提款的手續費
  public static double calculateFees(double amount, CurrencyType currencyType, boolean isNonBankATM) {
    double fee = 0;
    LocalTime currentTime = LocalTime.now();

    // 如果不是台幣，計算外幣手續費
    if (currencyType != CurrencyType.TWD) {
      double amountInTWD = currencyType.convertToTWD(amount);
      fee += Math.max(amountInTWD * 0.01, Constants.FOREIGN_CURRENCY_FEE_MIN);
    }

    // 非本行提款手續費
    if (isNonBankATM) {
      fee += Constants.NON_BANK_FEE;
    }

    // 加入時段手續費
    fee += calculateTimeBasedFee(currentTime);

    return fee;
  }

  // 顯示手續費明細
  private static void displayFeeDetails(double amount, CurrencyType currencyType, boolean isNonBankATM, double totalFee) {
    LocalTime currentTime = LocalTime.now();
    System.out.println("手續費明細：");

    if (currencyType != CurrencyType.TWD) {
      double amountInTWD = currencyType.convertToTWD(amount);
      double foreignFee = Math.max(amountInTWD * 0.01, Constants.FOREIGN_CURRENCY_FEE_MIN);
      System.out.println("- 外幣提款手續費：" + foreignFee + " TWD");
      System.out.printf("  (以 %.2f %s 計算)\n", amount, currencyType.name());
    }

    if (isNonBankATM) {
      System.out.println("- 跨行提款手續費：" + Constants.NON_BANK_FEE + " TWD");
    }

    double timeFee = calculateTimeBasedFee(currentTime);
    if (timeFee > 0) {
      String timeRange = timeFee == Constants.NIGHT_EXTRA_FEE ?
          "夜間(23:00-09:00)" : "晚間(17:00-23:00)";
      System.out.println("- " + timeRange + "時段手續費：" + timeFee + " TWD");
    }

    System.out.println("總手續費：" + totalFee + " TWD");
  }

  // 執行提款操作
  public static void performWithdrawal(Account account, double amount, CurrencyType currencyType, boolean isNonBankATM) {
    // 驗證提款金額
    if (!validateWithdrawal(account, amount, currencyType)) return;

    // 將提款金額轉換為台幣
    double amountInTWD = currencyType.convertToTWD(amount);

    // 計算手續費
    double fees = calculateFees(amount, currencyType, isNonBankATM);
    double totalAmount = amountInTWD + fees;

    // 檢查餘額是否足夠
    if (!account.canWithdraw(totalAmount)) {
      System.out.println("餘額不足以支付提款金額與手續費，提款失敗！");
      if (currencyType != CurrencyType.TWD) {
        System.out.printf("提款金額 %.2f %s 約為 %.2f TWD\n",
            amount, currencyType.name(), amountInTWD);
      }
      return;
    }

    // 更新帳戶數據
    account.deductBalance(totalAmount);
    account.addDailyWithdrawnAmount((int) amountInTWD);

    // 顯示交易結果
    System.out.println("\n===== 提款交易成功 =====");
    System.out.println("帳戶類型：" + account.getAccountType().getDescription());
    if (currencyType == CurrencyType.TWD) {
      System.out.printf("提款金額：%.2f TWD\n", amount);
    } else {
      System.out.printf("提款金額：%.2f %s (約 %.2f TWD)\n",
          amount, currencyType.name(), amountInTWD);
      System.out.printf("當前匯率：1 %s = %.2f TWD\n",
          currencyType.name(), currencyType.getExchangeRate());
    }

    displayFeeDetails(amount, currencyType, isNonBankATM, fees);
    System.out.println("剩餘餘額：" + account.getBalance() + " TWD");
    System.out.println("======================");

    // 餘額警告
    if (account.isLowBalance()) {
      System.out.println("\n警告：帳戶餘額低於 " + Constants.LOW_BALANCE_WARNING + " TWD！");
    }
  }

  // 舊的方法保留用於向後兼容
  public static void performWithdrawal(Account account, double amount, boolean isForeignCurrency, boolean isNonBankATM) {
    performWithdrawal(account, amount, isForeignCurrency ? CurrencyType.USD : CurrencyType.TWD, isNonBankATM);
  }
}