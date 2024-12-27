package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ATM 操作測試")
class ATMOperationsTest {

  @Mock
  private Account mockAccount;

  @Nested
  @DisplayName("提款限額測試")
  class WithdrawalLimitTests {

    @Test
    @DisplayName("普通帳戶 - 單次限額測試")
    void normalAccountSingleLimitTest() {
      // 設置為普通帳戶
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);

      // 測試單次限額（20,000元）
      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 20000, CurrencyType.TWD),
          "普通帳戶應該可以提款20,000元");
      assertFalse(ATMOperations.validateWithdrawal(mockAccount, 20001, CurrencyType.TWD),
          "普通帳戶不應該可以提款超過20,000元");
    }

    @Test
    @DisplayName("普通帳戶 - 每日限額測試")
    void normalAccountDailyLimitTest() {
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(50000); // 已提款50,000

      // 測試每日限額（60,000元）
      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 10000, CurrencyType.TWD),
          "普通帳戶當日總提款60,000元應該允許");
      assertFalse(ATMOperations.validateWithdrawal(mockAccount, 10001, CurrencyType.TWD),
          "普通帳戶超過每日限額應該被拒絕");
    }

    @Test
    @DisplayName("金卡帳戶 - 限額測試")
    void goldAccountLimitTest() {
      when(mockAccount.getAccountType()).thenReturn(AccountType.GOLD);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);

      // 測試單次限額（50,000元）
      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 50000, CurrencyType.TWD),
          "金卡帳戶應該可以提款50,000元");
      assertFalse(ATMOperations.validateWithdrawal(mockAccount, 50001, CurrencyType.TWD),
          "金卡帳戶不應該可以提款超過50,000元");

      // 測試每日限額（120,000元）
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(100000);
      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 20000, CurrencyType.TWD),
          "金卡帳戶當日總提款120,000元應該允許");
      assertFalse(ATMOperations.validateWithdrawal(mockAccount, 20001, CurrencyType.TWD),
          "金卡帳戶超過每日限額應該被拒絕");
    }

    @Test
    @DisplayName("白金帳戶 - 無限額測試")
    void platinumAccountLimitTest() {
      when(mockAccount.getAccountType()).thenReturn(AccountType.PLATINUM);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);

      // 白金帳戶應該沒有限制
      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 100000, CurrencyType.TWD),
          "白金帳戶應該可以提款大額金額");

      // 測試極大金額
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(1000000);
      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 500000, CurrencyType.TWD),
          "白金帳戶不應該有每日限額限制");
    }

    @Test
    @DisplayName("測試外幣提款限額換算")
    void foreignCurrencyLimitTest() {
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);

      // 假設 1 USD = 30 TWD
      // 測試單次限額（20,000 TWD ≈ 666.67 USD）
      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 666, CurrencyType.USD),
          "普通帳戶應該可以提款 666 USD");
      assertFalse(ATMOperations.validateWithdrawal(mockAccount, 700, CurrencyType.USD),
          "普通帳戶不應該可以提款超過限額的 USD");
    }
  }

  @Test
  @DisplayName("測試提款金額為0或負數")
  void invalidAmountTest() {
    when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);

    assertFalse(ATMOperations.validateWithdrawal(mockAccount, 0, CurrencyType.TWD),
        "提款金額為0應該被拒絕");
    assertFalse(ATMOperations.validateWithdrawal(mockAccount, -100, CurrencyType.TWD),
        "提款金額為負數應該被拒絕");
  }
}