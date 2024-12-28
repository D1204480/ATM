package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("執行提款顯示測試")
class performWithdrawalTest {

  @Test
  @DisplayName("成功提領台幣測試")
  void shouldSuccessfullyWithdrawTWD() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // Mock account
      Account mockAccount = mock(Account.class);
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);
      when(mockAccount.canWithdraw(anyDouble())).thenReturn(true);

      // Mock static methods
      mockedStatic.when(() -> ATMOperations.validateWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class)
      )).thenReturn(true);

      mockedStatic.when(() -> ATMOperations.calculateFees(
          anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenReturn(0.0);

      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenCallRealMethod();

      // 執行提款
      ATMOperations.performWithdrawal(mockAccount, 1000, CurrencyType.TWD, false);

      // 驗證相關方法是否被調用
      verify(mockAccount).canWithdraw(anyDouble());
      verify(mockAccount).deductBalance(anyDouble());
      verify(mockAccount).addDailyWithdrawnAmount(anyInt());
    }
  }

  @Test
  @DisplayName("成功提領外幣測試")
  void shouldSuccessfullyWithdrawForeignCurrency() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // Mock account
      Account mockAccount = mock(Account.class);
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);
      when(mockAccount.canWithdraw(anyDouble())).thenReturn(true);

      // Mock static methods
      mockedStatic.when(() -> ATMOperations.validateWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class)
      )).thenReturn(true);

      mockedStatic.when(() -> ATMOperations.calculateFees(
          anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenReturn(15.0);

      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenCallRealMethod();

      // 執行提款
      double amount = 100;  // USD
      ATMOperations.performWithdrawal(mockAccount, amount, CurrencyType.USD, false);

      // 驗證相關方法是否被調用
      verify(mockAccount).canWithdraw(anyDouble());
      verify(mockAccount).deductBalance(anyDouble());
      verify(mockAccount).addDailyWithdrawnAmount(anyInt());
    }
  }

  @Test
  @DisplayName("驗證成功但餘額不足的台幣提款測試")
  void shouldFailWithdrawTWDWhenBalanceInsufficient() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // Mock account
      Account mockAccount = mock(Account.class);
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);
      when(mockAccount.canWithdraw(anyDouble())).thenReturn(false);

      // 捕獲輸出
      ByteArrayOutputStream outContent = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outContent));

      // Mock static methods
      mockedStatic.when(() -> ATMOperations.validateWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class)
      )).thenReturn(true);

      mockedStatic.when(() -> ATMOperations.calculateFees(
          anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenReturn(0.0);

      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenCallRealMethod();

      // 執行提款
      ATMOperations.performWithdrawal(mockAccount, 1000, CurrencyType.TWD, false);

      // 驗證相關方法沒有被調用
      verify(mockAccount, never()).deductBalance(anyDouble());
      verify(mockAccount, never()).addDailyWithdrawnAmount(anyInt());

      // 驗證輸出訊息
      String output = outContent.toString();
      assertTrue(output.contains("餘額不足"));
      assertTrue(output.contains("提款金額"));

      // 恢復標準輸出
      System.setOut(System.out);
    }
  }

  @Test
  @DisplayName("驗證成功但餘額不足的外幣提款測試")
  void shouldFailWithdrawForeignCurrencyWhenBalanceInsufficient() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // Mock account
      Account mockAccount = mock(Account.class);
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);
      when(mockAccount.canWithdraw(anyDouble())).thenReturn(false);

      // 捕獲輸出
      ByteArrayOutputStream outContent = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outContent));

      // Mock static methods
      mockedStatic.when(() -> ATMOperations.validateWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class)
      )).thenReturn(true);

      mockedStatic.when(() -> ATMOperations.calculateFees(
          anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenReturn(15.0);

      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenCallRealMethod();

      // 執行提款
      double amount = 100;  // USD
      ATMOperations.performWithdrawal(mockAccount, amount, CurrencyType.USD, false);

      // 驗證相關方法沒有被調用
      verify(mockAccount, never()).deductBalance(anyDouble());
      verify(mockAccount, never()).addDailyWithdrawnAmount(anyInt());

      // 驗證輸出訊息
      String output = outContent.toString();
      assertTrue(output.contains("餘額不足"));
      assertTrue(output.contains("提款金額"));

      // 恢復標準輸出
      System.setOut(System.out);
    }
  }

  @Test
  @DisplayName("提款驗證失敗的測試")
  void shouldReturnEarlyWhenValidationFails() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // Mock account
      Account mockAccount = mock(Account.class);
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);

      // Mock validateWithdrawal 返回 false
      mockedStatic.when(() -> ATMOperations.validateWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class)
      )).thenReturn(false);

      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenCallRealMethod();

      // 執行提款
      ATMOperations.performWithdrawal(mockAccount, 1000, CurrencyType.TWD, false);

      // 驗證後續方法都沒有被調用
      verify(mockAccount, never()).canWithdraw(anyDouble());
      verify(mockAccount, never()).deductBalance(anyDouble());
      verify(mockAccount, never()).addDailyWithdrawnAmount(anyInt());
    }
  }

  @Test
  @DisplayName("成功提款但餘額低於警告值的測試")
  void shouldShowWarningWhenBalanceIsLow() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // 捕獲輸出
      ByteArrayOutputStream outContent = new ByteArrayOutputStream();
      PrintStream originalOut = System.out;
      System.setOut(new PrintStream(outContent));

      // Mock account
      Account mockAccount = mock(Account.class);
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);
      when(mockAccount.canWithdraw(anyDouble())).thenReturn(true);
      when(mockAccount.isLowBalance()).thenReturn(true);  // 設置低餘額狀態

      // Mock static methods
      mockedStatic.when(() -> ATMOperations.validateWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class)
      )).thenReturn(true);

      mockedStatic.when(() -> ATMOperations.calculateFees(
          anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenReturn(0.0);

      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenCallRealMethod();

      // 執行提款
      ATMOperations.performWithdrawal(mockAccount, 1000, CurrencyType.TWD, false);

      // 驗證方法調用
      verify(mockAccount).deductBalance(anyDouble());
      verify(mockAccount).addDailyWithdrawnAmount(anyInt());
      verify(mockAccount).isLowBalance();

      // 驗證輸出訊息包含低餘額警告
      String output = outContent.toString();
      assertTrue(output.contains("警告：帳戶餘額低於"));
      assertTrue(output.contains(String.valueOf(Constants.LOW_BALANCE_WARNING)));

      // 恢復標準輸出
      System.setOut(originalOut);
    }
  }

  @Test
  @DisplayName("成功提款且餘額正常的測試")
  void shouldNotShowWarningWhenBalanceIsNormal() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // 捕獲輸出
      ByteArrayOutputStream outContent = new ByteArrayOutputStream();
      PrintStream originalOut = System.out;
      System.setOut(new PrintStream(outContent));

      // Mock account
      Account mockAccount = mock(Account.class);
      when(mockAccount.getAccountType()).thenReturn(AccountType.NORMAL);
      when(mockAccount.getDailyWithdrawnAmount()).thenReturn(0);
      when(mockAccount.canWithdraw(anyDouble())).thenReturn(true);
      when(mockAccount.isLowBalance()).thenReturn(false);  // 設置正常餘額狀態

      // Mock static methods
      mockedStatic.when(() -> ATMOperations.validateWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class)
      )).thenReturn(true);

      mockedStatic.when(() -> ATMOperations.calculateFees(
          anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenReturn(0.0);

      mockedStatic.when(() -> ATMOperations.performWithdrawal(
          any(Account.class), anyDouble(), any(CurrencyType.class), anyBoolean()
      )).thenCallRealMethod();

      // 執行提款
      ATMOperations.performWithdrawal(mockAccount, 1000, CurrencyType.TWD, false);

      // 驗證方法調用
      verify(mockAccount).deductBalance(anyDouble());
      verify(mockAccount).addDailyWithdrawnAmount(anyInt());
      verify(mockAccount).isLowBalance();

      // 驗證輸出訊息不包含低餘額警告
      String output = outContent.toString();
      assertFalse(output.contains("警告：帳戶餘額低於"));

      // 恢復標準輸出
      System.setOut(originalOut);
    }
  }
}