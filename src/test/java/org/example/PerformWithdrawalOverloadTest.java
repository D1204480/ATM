package org.example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("提款方法重載測試")
class PerformWithdrawalOverloadTest {

  @Test
  @DisplayName("台幣提款方法重載測試")
  void shouldCallMainMethodWithTWD() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      Account mockAccount = mock(Account.class);

      // 只允許重載方法的實際調用
      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class),
          anyDouble(),
          anyBoolean(),
          anyBoolean()
      )).thenCallRealMethod();

      // 執行台幣提款
      ATMOperations.performWithdrawal(mockAccount, 1000, false, false);

      // 驗證調用
      mockedStatic.verify(() -> ATMOperations.performWithdrawal(
          eq(mockAccount),
          eq(1000.0),
          eq(CurrencyType.TWD),
          eq(false)
      ));
    }
  }

  @Test
  @DisplayName("美金提款方法重載測試")
  void shouldCallMainMethodWithUSD() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // Mock account
      Account mockAccount = mock(Account.class);

      // 只允許重載方法的實際調用
      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class),
          anyDouble(),
          anyBoolean(),
          anyBoolean()
      )).thenCallRealMethod();

      // 執行外幣提款
      ATMOperations.performWithdrawal(mockAccount, 1000, true, false);

      // 驗證主要方法被正確調用，並且使用 USD
      mockedStatic.verify(() -> ATMOperations.performWithdrawal(
          eq(mockAccount),
          eq(1000.0),
          eq(CurrencyType.USD),
          eq(false)
      ));
    }
  }

  @Test
  @DisplayName("跨行台幣提款方法重載測試")
  void shouldCallMainMethodWithTWDAndNonBank() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      Account mockAccount = mock(Account.class);

      // 只允許重載方法的實際調用
      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class),
          anyDouble(),
          anyBoolean(),
          anyBoolean()
      )).thenCallRealMethod();

      ATMOperations.performWithdrawal(mockAccount, 1000, false, true);

      mockedStatic.verify(() -> ATMOperations.performWithdrawal(
          eq(mockAccount),
          eq(1000.0),
          eq(CurrencyType.TWD),
          eq(true)
      ));
    }
  }

  @Test
  @DisplayName("跨行美金提款方法重載測試")
  void shouldCallMainMethodWithUSDAndNonBank() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      Account mockAccount = mock(Account.class);

      // 只允許重載方法的實際調用
      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class),
          anyDouble(),
          anyBoolean(),
          anyBoolean()
      )).thenCallRealMethod();

      ATMOperations.performWithdrawal(mockAccount, 1000, true, true);

      mockedStatic.verify(() -> ATMOperations.performWithdrawal(
          eq(mockAccount),
          eq(1000.0),
          eq(CurrencyType.USD),
          eq(true)
      ));
    }
  }
}