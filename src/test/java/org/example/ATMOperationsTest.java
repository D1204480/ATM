package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
          "普通帳戶單次可以提款上限20,000元");
      assertFalse(ATMOperations.validateWithdrawal(mockAccount, 20001, CurrencyType.TWD),
          "普通帳戶單次提款不可超過20,000元");
    }

    @Test
    @DisplayName("普通帳戶 - 每日限額測試")
    void normalAccountDailyLimitTest() {
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(50000); // 已提款50,000

      // 測試每日限額（60,000元）
      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 10000, CurrencyType.TWD),
          "普通帳戶當日總提款上限60,000元");
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
          "金卡帳戶單次可以提款上限50,000元");
      assertFalse(ATMOperations.validateWithdrawal(mockAccount, 50001, CurrencyType.TWD),
          "金卡帳戶單次提款不可超過50,000元");

      // 測試每日限額（120,000元）
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(100000);
      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 20000, CurrencyType.TWD),
          "金卡帳戶當日總提款上限120,000元");
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
          "白金帳戶沒有每日限額限制");
    }
  }

  @Nested
  @DisplayName("外幣提款測試")
  class ForeignCurrencyWithdrawalTests {

    @Test
    @DisplayName("美金提款換算台幣限額測試")
    void usdWithdrawalLimitTest() {
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);

      // 計算等值於 20,000 TWD 的 USD 金額 (約 634.92 USD)
      double usdLimit = 20000 / CurrencyType.USD.getExchangeRate();

      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 634, CurrencyType.USD),
          "允許提領等值於 20,000 TWD 以內的美金");

      assertFalse(ATMOperations.validateWithdrawal(mockAccount, 635, CurrencyType.USD),
          "不允許提領超過 20,000 TWD 的美金");
    }

    @Test
    @DisplayName("歐元提款換算台幣限額測試")
    void eurWithdrawalLimitTest() {
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);

      // 計算等值於 20,000 TWD 的 EUR 金額 (約 584.80 EUR)
      double eurLimit = 20000 / CurrencyType.EUR.getExchangeRate();

      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 584, CurrencyType.EUR),
          "應該允許提領等值於 20,000 TWD 以內的歐元");

      assertFalse(ATMOperations.validateWithdrawal(mockAccount, 585, CurrencyType.EUR),
          "不應該允許提領超過 20,000 TWD 的歐元");
    }

    @Test
    @DisplayName("日圓提款換算台幣限額測試")
    void jpyWithdrawalLimitTest() {
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);

      // 計算等值於 20,000 TWD 的 JPY 金額 (約 95,238.10 JPY)
      double jpyLimit = 20000 / CurrencyType.JPY.getExchangeRate();

      assertTrue(ATMOperations.validateWithdrawal(mockAccount, 95000, CurrencyType.JPY),
          "應該允許提領等值於 20,000 TWD 以內的日圓");

      assertFalse(ATMOperations.validateWithdrawal(mockAccount, 95239, CurrencyType.JPY),
          "不應該允許提領超過 20,000 TWD 的日圓");
    }

    @Test
    @DisplayName("餘額不足的外幣提款測試")
    void insufficientBalanceForForeignCurrencyTest() {
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.canWithdraw(anyDouble())).thenReturn(false); // 直接模擬餘額不足的情況

      // 嘗試提領 500 USD (約 15,750 TWD)
      ATMOperations.performWithdrawal(mockAccount, 500, CurrencyType.USD, false);

      // 驗證提款是否被拒絕（餘額不足）
      verify(mockAccount, never()).deductBalance(anyDouble());
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