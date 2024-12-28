package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("手續費明細顯示測試")
class DisplayFeeDetailsTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  void setUp() {
    // 重定向 System.out 到我們的 ByteArrayOutputStream
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void restoreStreams() {
    // 恢復原始的 System.out
    System.setOut(originalOut);
  }

  @Test
  @DisplayName("測試台幣本行提款手續費明細")
  void displayTWDOwnBankFeeDetails() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // 模擬 calculateTimeBasedFee 返回 0（工作時間）
      mockedStatic.when(() -> ATMOperations.calculateTimeBasedFee(any(LocalTime.class)))
          .thenReturn(0.0);

      mockedStatic.when(() -> ATMOperations.displayFeeDetails(
          any(Double.class),
          any(CurrencyType.class),
          any(Boolean.class),
          any(Double.class)
      )).thenCallRealMethod();

      double amount = 1000;
      ATMOperations.displayFeeDetails(amount, CurrencyType.TWD, false, 0);

      String output = outContent.toString();
      assertTrue(output.contains("手續費明細："));
      assertTrue(output.contains("總手續費：0.0 TWD"));
      assertFalse(output.contains("外幣提款手續費"));
      assertFalse(output.contains("跨行提款手續費"));
    }
  }

  @Test
  @DisplayName("測試外幣跨行提款手續費明細")
  void displayForeignCurrencyNonBankFeeDetails() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // 模擬夜間時段
      mockedStatic.when(() -> ATMOperations.calculateTimeBasedFee(any(LocalTime.class)))
          .thenReturn((double) Constants.NIGHT_EXTRA_FEE);

      mockedStatic.when(() -> ATMOperations.displayFeeDetails(
          any(Double.class),
          any(CurrencyType.class),
          any(Boolean.class),
          any(Double.class)
      )).thenCallRealMethod();

      double amount = 100;  // USD
      double totalFee = 45.0;  // 假設總費用
      ATMOperations.displayFeeDetails(amount, CurrencyType.USD, true, totalFee);

      String output = outContent.toString();
      assertTrue(output.contains("手續費明細："));
      assertTrue(output.contains("外幣提款手續費"));
      assertTrue(output.contains("跨行提款手續費：" + Constants.NON_BANK_FEE));
      assertTrue(output.contains("夜間(23:00-09:00)時段手續費"));
      assertTrue(output.contains("總手續費：" + totalFee + " TWD"));
    }
  }

  @Test
  @DisplayName("測試晚間時段手續費明細")
  void displayEveningTimeFeeDetails() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      // 模擬晚間時段
      mockedStatic.when(() -> ATMOperations.calculateTimeBasedFee(any(LocalTime.class)))
          .thenReturn((double) Constants.EVENING_EXTRA_FEE);

      mockedStatic.when(() -> ATMOperations.displayFeeDetails(
          any(Double.class),
          any(CurrencyType.class),
          any(Boolean.class),
          any(Double.class)
      )).thenCallRealMethod();

      ATMOperations.displayFeeDetails(1000, CurrencyType.TWD, false,
          Constants.EVENING_EXTRA_FEE);

      String output = outContent.toString();
      assertTrue(output.contains("晚間(17:00-23:00)時段手續費"));
      assertTrue(output.contains("總手續費：" + Constants.EVENING_EXTRA_FEE));
    }
  }

  @Test
  @DisplayName("測試所有手續費類型組合")
  void displayAllFeeTypeCombination() {
    try (MockedStatic<ATMOperations> mockedStatic = Mockito.mockStatic(ATMOperations.class)) {
      mockedStatic.when(() -> ATMOperations.calculateTimeBasedFee(any(LocalTime.class)))
          .thenReturn((double) Constants.NIGHT_EXTRA_FEE);

      mockedStatic.when(() -> ATMOperations.displayFeeDetails(
          any(Double.class),
          any(CurrencyType.class),
          any(Boolean.class),
          any(Double.class)
      )).thenCallRealMethod();

      double amount = 1000;  // USD
      double totalFee = Constants.FOREIGN_CURRENCY_FEE_MIN +
          Constants.NON_BANK_FEE +
          Constants.NIGHT_EXTRA_FEE;  // 外幣 + 跨行 + 夜間

      ATMOperations.displayFeeDetails(amount, CurrencyType.USD, true, totalFee);

      String output = outContent.toString();
      assertTrue(output.contains("外幣提款手續費"));
      assertTrue(output.contains("跨行提款手續費"));
      assertTrue(output.contains("夜間(23:00-09:00)時段手續費"));
      assertTrue(output.contains("總手續費：" + totalFee + " TWD"));
    }
  }
}