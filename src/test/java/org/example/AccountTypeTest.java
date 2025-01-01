package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountTypeTest {

  @Test
  @DisplayName("測試普通用戶")
  void testNormalAccountType() {
    AccountType type = AccountType.NORMAL;
    assertEquals("普通用戶", type.getDescription());
    assertEquals(Constants.NORMAL_MAX_WITHDRAWAL_PER_TRANSACTION,
        type.getMaxWithdrawalPerTransaction());
    assertEquals(Constants.NORMAL_MAX_DAILY_WITHDRAWAL,
        type.getMaxDailyWithdrawal());
  }

  @Test
  @DisplayName("測試金卡用戶")
  void testGoldAccountType() {
    AccountType type = AccountType.GOLD;
    assertEquals("金卡用戶", type.getDescription());
    assertEquals(Constants.GOLD_MAX_WITHDRAWAL_PER_TRANSACTION,
        type.getMaxWithdrawalPerTransaction());
    assertEquals(Constants.GOLD_MAX_DAILY_WITHDRAWAL,
        type.getMaxDailyWithdrawal());
  }

  @Test
  @DisplayName("測試白金用戶")
  void testPlatinumAccountType() {
    AccountType type = AccountType.PLATINUM;
    assertEquals("白金用戶", type.getDescription());
    assertEquals(Constants.PLATINUM_MAX_WITHDRAWAL_PER_TRANSACTION,
        type.getMaxWithdrawalPerTransaction());
    assertEquals(Constants.PLATINUM_MAX_DAILY_WITHDRAWAL,
        type.getMaxDailyWithdrawal());
  }
}