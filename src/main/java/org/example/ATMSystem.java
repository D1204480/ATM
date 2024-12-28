package org.example;

import java.util.Scanner;

public class ATMSystem {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    Account account = new Account(50000, "1234", AccountType.GOLD); // 初始化一個金卡帳戶，餘額 50,000 元，密碼 "1234"

    System.out.println("歡迎使用 ATM 系統！");
    System.out.println("您的帳戶類型是：" + account.getAccountType().getDescription());

    while (!account.isAccountLocked()) {
      if (!authenticate(scanner, account)) break; // 密碼驗證失敗則退出

      boolean exit = false;
      while (!exit) {
        int choice = showMainMenu(scanner);
        switch (choice) {
          case 1:
            performWithdrawal(account, scanner);
            break;
          case 2:
            checkBalance(account);
            break;
          case 3:
            System.out.println("感謝使用，再見！");
            exit = true;
            break;
          default:
            System.out.println("無效選項，請重新選擇。");
        }
      }
    }
    scanner.close();
  }

  // 密碼驗證
  static boolean authenticate(Scanner scanner, Account account) {
    System.out.print("請輸入密碼：");
    String password = scanner.nextLine();
    if (account.validatePassword(password)) {
      System.out.println("密碼正確，進入主選單。");
      return true;
    } else {
      System.out.println("密碼錯誤！");
      if (account.isAccountLocked()) {
        System.out.println("帳戶已鎖定，請聯繫客服！");
      }
      return false;
    }
  }

  // 顯示主選單
  static int showMainMenu(Scanner scanner) {
    System.out.println("\n請選擇操作：");
    System.out.println("1. 提款");
    System.out.println("2. 查詢餘額");
    System.out.println("3. 離開");
    System.out.print("選擇：");
    return scanner.nextInt();
  }

  // 選擇貨幣類型
  static CurrencyType selectCurrency(Scanner scanner) {
    System.out.println("\n請選擇提款貨幣：");
    System.out.println("1. 新台幣 (TWD)");
    System.out.println("2. 美元 (USD)");
    System.out.println("3. 歐元 (EUR)");
    System.out.println("4. 日圓 (JPY)");
    System.out.print("選擇：");

    int choice = scanner.nextInt();
    switch (choice) {
      case 1: return CurrencyType.TWD;
      case 2: return CurrencyType.USD;
      case 3: return CurrencyType.EUR;
      case 4: return CurrencyType.JPY;
      default:
        System.out.println("無效選擇，預設使用新台幣");
        return CurrencyType.TWD;
    }
  }

  // 執行提款操作
  static void performWithdrawal(Account account, Scanner scanner) {
    // 選擇貨幣類型
    CurrencyType currencyType = selectCurrency(scanner);

    // 輸入提款金額
    System.out.print("請輸入提款金額（" + currencyType.name() + "）：");
    double amount = scanner.nextDouble();

    // 如果是外幣，顯示換算資訊
    if (currencyType != CurrencyType.TWD) {
      double amountInTWD = currencyType.convertToTWD(amount);
      System.out.printf("換算金額：%.2f TWD (匯率：1 %s = %.2f TWD)\n",
          amountInTWD, currencyType.name(), currencyType.getExchangeRate());
    }

    // 確認是否為本行ATM
    System.out.print("是否為本行 ATM？(Y/N)：");
    boolean isNonBankATM = !scanner.next().trim().equalsIgnoreCase("Y");

    // 執行提款
    ATMOperations.performWithdrawal(account, amount, currencyType, isNonBankATM);
  }

  // 查詢帳戶餘額
  static void checkBalance(Account account) {
    System.out.println("當前餘額：" + account.getBalance() + " TWD");
    // 顯示各幣別約當金額
    System.out.println("\n約當外幣金額：");
    for (CurrencyType currency : CurrencyType.values()) {
      if (currency != CurrencyType.TWD) {
        double foreignAmount = currency.convertFromTWD(account.getBalance());
        System.out.printf("%s：%.2f\n", currency.getDescription(), foreignAmount);
      }
    }
  }
}