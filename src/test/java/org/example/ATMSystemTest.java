package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import static org.mockito.Mockito.*;

@DisplayName("ATM系統測試")
class ATMSystemTest {
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private Account account;

  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outputStream));
    account = new Account(50000, "1234", AccountType.GOLD);
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  // 模擬使用者輸入
  private void provideInput(String data) {
    ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
    System.setIn(testIn);
  }

  @Nested
  @DisplayName("密碼驗證測試")
  class AuthenticationTest {

    @Test
    @DisplayName("正確密碼驗證測試")
    void shouldAuthenticateWithCorrectPassword() {
      provideInput("1234\n");
      Scanner scanner = new Scanner(System.in);

      boolean result = ATMSystem.authenticate(scanner, account);

      assertTrue(result);
      assertTrue(outputStream.toString().contains("密碼正確"));
    }

    @Test
    @DisplayName("錯誤密碼驗證測試")
    void shouldFailWithWrongPassword() {
      provideInput("wrong\n");
      Scanner scanner = new Scanner(System.in);

      boolean result = ATMSystem.authenticate(scanner, account);

      assertFalse(result);
      assertTrue(outputStream.toString().contains("密碼錯誤"));
    }

    @Test
    @DisplayName("帳戶鎖定時的認證測試")
    void shouldShowMessageWhenAccountLocked() {
      // 先輸入錯誤密碼三次讓帳戶鎖定
      Account lockedAccount = new Account(50000, "1234", AccountType.GOLD);
      for (int i = 0; i < Constants.MAX_PASSWORD_ATTEMPTS; i++) {
        lockedAccount.validatePassword("wrongPassword");
      }

      // 確認帳戶已鎖定
      assertTrue(lockedAccount.isAccountLocked(), "帳戶應該被鎖定");

      // 模擬再次嘗試輸入正確密碼
      provideInput("1234\n");
      Scanner scanner = new Scanner(System.in);

      boolean result = ATMSystem.authenticate(scanner, lockedAccount);

      // 驗證
      assertFalse(result, "鎖定的帳戶不應該通過認證");
      String output = outputStream.toString();
      assertTrue(output.contains("密碼錯誤"));
      assertTrue(output.contains("帳戶已鎖定，請聯繫客服"));
    }

    @Test
    @DisplayName("多次錯誤密碼導致帳戶鎖定測試")
    void shouldLockAccountAfterMultipleFailedAttempts() {
      Account testAccount = new Account(50000, "1234", AccountType.GOLD);
      Scanner scanner;

      // 連續輸入錯誤密碼直到達到上限
      for (int i = 0; i < Constants.MAX_PASSWORD_ATTEMPTS; i++) {
        provideInput("wrongpass\n");
        scanner = new Scanner(System.in);
        ATMSystem.authenticate(scanner, testAccount);
      }

      // 清空之前的輸出
      outputStream.reset();

      // 再次嘗試登入
      provideInput("1234\n");
      scanner = new Scanner(System.in);
      boolean result = ATMSystem.authenticate(scanner, testAccount);

      // 驗證
      assertFalse(result, "帳戶應該被鎖定且不能登入");
      assertTrue(testAccount.isAccountLocked(), "帳戶應該被鎖定");
      String output = outputStream.toString();
      assertTrue(output.contains("帳戶已鎖定，請聯繫客服"));
    }

    @Test
    @DisplayName("帳戶鎖定狀態完整流程測試")
    void shouldHandleLockedAccountFlow() {
      Account testAccount = new Account(50000, "1234", AccountType.GOLD);
      Scanner scanner;
      String output;

      // 步驟1：第一次錯誤密碼
      provideInput("wrong1\n");
      scanner = new Scanner(System.in);
      ATMSystem.authenticate(scanner, testAccount);
      output = outputStream.toString();
      assertTrue(output.contains("密碼錯誤"));
      assertFalse(testAccount.isAccountLocked());
      outputStream.reset();

      // 步驟2：第二次錯誤密碼
      provideInput("wrong2\n");
      scanner = new Scanner(System.in);
      ATMSystem.authenticate(scanner, testAccount);
      output = outputStream.toString();
      assertTrue(output.contains("密碼錯誤"));
      assertFalse(testAccount.isAccountLocked());
      outputStream.reset();

      // 步驟3：第三次錯誤密碼（觸發鎖定）
      provideInput("wrong3\n");
      scanner = new Scanner(System.in);
      ATMSystem.authenticate(scanner, testAccount);
      output = outputStream.toString();
      assertTrue(output.contains("密碼錯誤"));
      assertTrue(output.contains("帳戶已鎖定，請聯繫客服"));
      assertTrue(testAccount.isAccountLocked());
      outputStream.reset();

      // 步驟4：嘗試使用正確密碼
      provideInput("1234\n");
      scanner = new Scanner(System.in);
      boolean result = ATMSystem.authenticate(scanner, testAccount);
      output = outputStream.toString();
      assertFalse(result, "鎖定的帳戶不應該能夠登入");
      assertTrue(output.contains("帳戶已鎖定，請聯繫客服"));
    }
  }

  @Nested
  @DisplayName("選單測試")
  class MenuTest {

    @Test
    @DisplayName("主選單顯示測試")
    void showMainMenu() {
      provideInput("1\n");  // 輸入1
      Scanner scanner = new Scanner(System.in);

      int choice = ATMSystem.showMainMenu(scanner);

      assertEquals(1, choice);  // 驗證輸出正確與否

      // 驗證是否有印出所有的選項
      String output = outputStream.toString();
      assertTrue(output.contains("1. 提款"));
      assertTrue(output.contains("2. 查詢餘額"));
      assertTrue(output.contains("3. 離開"));
    }

    @Test
    @DisplayName("貨幣選擇測試")
    void shouldSelectCurrency() {
      provideInput("2\n"); // 選擇美元
      Scanner scanner = new Scanner(System.in);

      CurrencyType currency = ATMSystem.selectCurrency(scanner);

      assertEquals(CurrencyType.USD, currency);  // 驗證輸入2是否印出正確的幣別

      // 驗證是否輸出所有的選項
      String output = outputStream.toString();
      assertTrue(output.contains("1. 新台幣"));
      assertTrue(output.contains("2. 美元"));
      assertTrue(output.contains("3. 歐元 (EUR)"));
      assertTrue(output.contains("4. 日圓 (JPY)"));
    }

    @Test
    @DisplayName("測試所有幣別選擇")
    void shouldHandleAllCurrencySelections() {
      // 測試每一種幣別選擇
      testCurrencySelection("1", CurrencyType.TWD);  // 台幣
      testCurrencySelection("2", CurrencyType.USD);  // 美元
      testCurrencySelection("3", CurrencyType.EUR);  // 歐元
      testCurrencySelection("4", CurrencyType.JPY);  // 日圓
    }

    // 輔助方法：測試特定幣別選擇
    private void testCurrencySelection(String input, CurrencyType expectedCurrency) {
      provideInput(input + "\n");
      Scanner scanner = new Scanner(System.in);
      CurrencyType currency = ATMSystem.selectCurrency(scanner);
      assertEquals(expectedCurrency, currency,
          "選擇 " + input + " 應該返回 " + expectedCurrency.name());
    }

    @Test
    @DisplayName("無效幣別選擇測試")
    void shouldHandleInvalidCurrencySelection() {
      provideInput("5\n");  // 無效的選擇
      Scanner scanner = new Scanner(System.in);

      CurrencyType currency = ATMSystem.selectCurrency(scanner);

      // 驗證返回預設幣別（台幣）
      assertEquals(CurrencyType.TWD, currency);
      assertTrue(outputStream.toString().contains("無效選擇，預設使用新台幣"));
    }
  }

  @Nested
  @DisplayName("提款操作測試")
  class WithdrawalTest {

    @Test
    @DisplayName("台幣本行提款流程測試")
    void shouldPerformTWDWithdrawal() {
      try (MockedStatic<ATMOperations> mockedStatic = mockStatic(ATMOperations.class)) {
        // 模擬輸入：選擇台幣(1) -> 金額(1000) -> 本行ATM(Y)
        provideInput("1\n1000\nY\n");
        Scanner scanner = new Scanner(System.in);

        mockedStatic.when(() -> ATMOperations.performWithdrawal(
            any(Account.class),
            anyDouble(),
            any(CurrencyType.class),
            anyBoolean()
        )).then(invocation -> null);  // void 方法使用 then(invocation -> null)

        ATMSystem.performWithdrawal(account, scanner);

        // 驗證方法調用
        mockedStatic.verify(() -> ATMOperations.performWithdrawal(
            eq(account),
            eq(1000.0),
            eq(CurrencyType.TWD),
            eq(false)
        ));
      }
    }

    @Test
    @DisplayName("美金跨行提款流程測試")
    void shouldPerformUSDWithdrawal() {
      try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
        // 模擬輸入：選擇美金(2) -> 金額(100) -> 跨行ATM(N)
        provideInput("2\n100\nN\n");
        Scanner scanner = new Scanner(System.in);

        mockedStatic.when(() -> ATMOperations.performWithdrawal(
            any(Account.class),
            anyDouble(),
            any(CurrencyType.class),
            anyBoolean()
        )).then(invocation -> null);

        ATMSystem.performWithdrawal(account, scanner);

        mockedStatic.verify(() -> ATMOperations.performWithdrawal(
            eq(account),
            eq(100.0),
            eq(CurrencyType.USD),
            eq(true)
        ));

        // 驗證是否顯示匯率訊息
        String output = outputStream.toString();
        assertTrue(output.contains("換算金額"));
        assertTrue(output.contains("匯率"));
      }
    }
  }

  @Test
  @DisplayName("查詢餘額測試")
  void shouldCheckBalance() {
    ATMSystem.checkBalance(account);

    String output = outputStream.toString();
    assertTrue(output.contains("當前餘額：50000"));
    assertTrue(output.contains("約當外幣金額"));
    assertTrue(output.contains("美元"));
    assertTrue(output.contains("歐元"));
    assertTrue(output.contains("日圓"));
  }

}