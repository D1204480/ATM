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
}