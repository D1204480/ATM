package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Account 密碼驗證測試")
class AccountTest {
  private Account account;
  private static final String CORRECT_PASSWORD = "correct123";
  private static final double INITIAL_BALANCE = 1000.0;

  @BeforeEach
  void setUp() {
    account = new Account(INITIAL_BALANCE, CORRECT_PASSWORD, AccountType.NORMAL);  // 使用NORMAL普通用戶
  }

  @Nested
  @DisplayName("密碼驗證測試")
  class PasswordValidationTests {

    @Test
    @DisplayName("輸入正確密碼時應該返回 true")
    void PasswordIsCorrect() {
      assertTrue(account.validatePassword(CORRECT_PASSWORD),
          "使用正確密碼時應該驗證成功");
    }

    @Test
    @DisplayName("輸入錯誤密碼時應該返回 false")
    void PasswordIsIncorrect() {
      assertFalse(account.validatePassword("wrong123"),
          "使用錯誤密碼時應該驗證失敗");
    }

    @Test
    @DisplayName("重複輸入錯誤密碼達到上限後應鎖定帳戶")
    void shouldLockAccountAfterMaxAttempts() {
      // 重複輸入錯誤密碼直到達到上限
      for (int i = 0; i < Constants.MAX_PASSWORD_ATTEMPTS; i++) {
        account.validatePassword("wrong" + i);
      }

      // 驗證帳戶已被鎖定（即使輸入正確密碼也應該返回 false）
      assertAll(
          "驗證帳戶鎖定狀態",
          () -> assertFalse(account.validatePassword(CORRECT_PASSWORD),
              "帳戶鎖定後，即使密碼正確也應該驗證失敗"),
          () -> assertFalse(account.validatePassword("wrong"),
              "帳戶鎖定後，錯誤密碼應該驗證失敗")
      );
    }

    @Test
    @DisplayName("輸入正確密碼後應重置錯誤嘗試次數")
    void shouldResetAttemptsAfterCorrectPassword() {
      // 先輸入一次錯誤密碼
      account.validatePassword("wrong123");

      // 輸入正確密碼
      assertTrue(account.validatePassword(CORRECT_PASSWORD),
          "正確密碼應該驗證成功");

      // 再次輸入錯誤密碼（確認計數已重置）
      assertFalse(account.validatePassword("wrong456"),
          "重置後的錯誤密碼應該驗證失敗，但不會立即鎖定");

      assertTrue(account.validatePassword(CORRECT_PASSWORD),
          "重置後應該能再次使用正確密碼");
    }

    @Test
    @DisplayName("帳戶鎖定後應該拒絕所有密碼")
    void shouldRejectAllPasswordsWhenLocked() {
      // 鎖定帳戶
      for (int i = 0; i < Constants.MAX_PASSWORD_ATTEMPTS; i++) {
        account.validatePassword("wrong" + i);
      }

      assertAll(
          "驗證鎖定狀態下的密碼驗證",
          () -> assertFalse(account.validatePassword(CORRECT_PASSWORD),
              "鎖定後不應接受正確密碼"),
          () -> assertFalse(account.validatePassword("anyPassword"),
              "鎖定後不應接受任何密碼")
      );
    }
  }

  @Test
  @DisplayName("帳號被鎖定")
  void testIsAccountLocked() {
    assertFalse(account.isAccountLocked());
    // 可以測試帳戶被鎖定後的狀態
  }

  @Test
  @DisplayName("獲取當前餘額")
  void testGetBalance() {
    assertEquals(1000, account.getBalance());
  }

  @Test
  @DisplayName("增加當日提款記錄")
  void testGetDailyWithdrawnAmount() {
    assertEquals(0, account.getDailyWithdrawnAmount());
    account.addDailyWithdrawnAmount(1000);
    assertEquals(1000, account.getDailyWithdrawnAmount());
  }

  @Test
  @DisplayName("帳戶級別")
  void testGetAccountType() {
    assertEquals(AccountType.NORMAL, account.getAccountType());
  }

  @Test
  @DisplayName("測試提款多筆累計金額")
  void testAddDailyWithdrawnAmount() {
    account.addDailyWithdrawnAmount(1000);
    account.addDailyWithdrawnAmount(2000);
    assertEquals(3000, account.getDailyWithdrawnAmount());
  }

  @Test
  @DisplayName("扣減帳戶餘額")
  void testDeductBalance() {
    account.deductBalance(1000);
    assertEquals(0, account.getBalance());
  }

  @Test
  @DisplayName("檢查餘額是否足夠支付提款金額與手續費")
  void testCanWithdraw() {
    assertTrue(account.canWithdraw(200));
    assertFalse(account.canWithdraw(60000));
  }

  @Test
  @DisplayName("重置當日提款金額")
  void testResetDailyWithdrawnAmount() {
    account.addDailyWithdrawnAmount(5000);
    account.resetDailyWithdrawnAmount();
    assertEquals(0, account.getDailyWithdrawnAmount());
  }

  @Test
  @DisplayName("檢查餘額是否低於警示閾值")
  void testIsLowBalance() {
    assertFalse(account.isLowBalance());
    account.deductBalance(45000); // 假設 LOW_BALANCE_WARNING 是 10000
    assertTrue(account.isLowBalance());
  }

  // 邊界條件測試
  @Test
  @DisplayName("扣款金額為 0, 餘額是否正常")
  void testDeductBalanceWithZero() {
    double initialBalance = account.getBalance();
    account.deductBalance(0);
    assertEquals(initialBalance, account.getBalance());
  }

  @Test
  @DisplayName("提款為0, 提款次數是否正確")
  void testAddDailyWithdrawnAmountWithZero() {
    int initialAmount = account.getDailyWithdrawnAmount();
    account.addDailyWithdrawnAmount(0);
    assertEquals(initialAmount, account.getDailyWithdrawnAmount());
  }

}