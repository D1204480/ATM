package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyTypeTest {

  private static final double DELTA = 0.0001; // 浮點數比較的容許誤差

  @Test
  @DisplayName("台幣匯率")
  void testTWDProperties() {
    assertEquals("新台幣", CurrencyType.TWD.getDescription());
    assertEquals(1.0, CurrencyType.TWD.getExchangeRate(), DELTA);
  }

  @DisplayName("測試有幣別資料")
  @ParameterizedTest
  @EnumSource(CurrencyType.class)
  void testDescriptionNotNull(CurrencyType currency) {
    assertNotNull(currency.getDescription());
    assertTrue(currency.getDescription().length() > 0);
  }

  @DisplayName("測試有匯率資料")
  @ParameterizedTest
  @EnumSource(CurrencyType.class)
  void testExchangeRatePositive(CurrencyType currency) {
    assertTrue(currency.getExchangeRate() > 0);
  }

  @Test
  @DisplayName("測試外幣換台幣")
  void testConvertToTWD() {
    // 測試美元轉台幣
    assertEquals(3150.0, CurrencyType.USD.convertToTWD(100.0), DELTA);

    // 測試歐元轉台幣
    assertEquals(3420.0, CurrencyType.EUR.convertToTWD(100.0), DELTA);

    // 測試日圓轉台幣
    assertEquals(21.0, CurrencyType.JPY.convertToTWD(100.0), DELTA);

    // 測試台幣轉台幣（應該保持不變）
    assertEquals(100.0, CurrencyType.TWD.convertToTWD(100.0), DELTA);
  }

  @Test
  @DisplayName("測試台幣換外幣")
  void testConvertFromTWD() {
    // 測試台幣轉美元
    assertEquals(100.0, CurrencyType.USD.convertFromTWD(3150.0), DELTA);

    // 測試台幣轉歐元
    assertEquals(100.0, CurrencyType.EUR.convertFromTWD(3420.0), DELTA);

    // 測試台幣轉日圓
    assertEquals(100.0, CurrencyType.JPY.convertFromTWD(21.0), DELTA);

    // 測試台幣轉台幣（應該保持不變）
    assertEquals(100.0, CurrencyType.TWD.convertFromTWD(100.0), DELTA);
  }

  @Test
  @DisplayName("測試多次換匯")
  void testRoundTripConversion() {
    double originalAmount = 100.0;
    for (CurrencyType currency : CurrencyType.values()) {
      double twdAmount = currency.convertToTWD(originalAmount);
      double convertedBack = currency.convertFromTWD(twdAmount);
      assertEquals(originalAmount, convertedBack, DELTA,
          "Round trip conversion failed for " + currency.name());
    }
  }

  @Test
  @DisplayName("測試0元換匯")
  void testZeroConversion() {
    for (CurrencyType currency : CurrencyType.values()) {
      assertEquals(0.0, currency.convertToTWD(0.0), DELTA);
      assertEquals(0.0, currency.convertFromTWD(0.0), DELTA);
    }
  }
}