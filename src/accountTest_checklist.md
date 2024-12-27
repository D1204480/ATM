<h3>ACCOUNT TEST 測試案例</h3>
1. 正確密碼驗證 (testValidatePassword_WithCorrectPassword_ShouldReturnTrue)
- 輸入正確密碼時應該返回 true
- 確保基本的密碼驗證功能正常運作

2. 錯誤密碼驗證 (testValidatePassword_WithIncorrectPassword_ShouldReturnFalse)
- 輸入錯誤密碼時應該返回 false
- 驗證系統能正確識別錯誤密碼

3. 最大嘗試次數鎖定 (testValidatePassword_WithIncorrectPasswordMaxAttempts_ShouldLockAccount)
- 重複輸入錯誤密碼達到最大次數後，帳戶應被鎖定
- 帳戶鎖定後，即使輸入正確密碼也應該返回 false

4. 正確密碼重置嘗試次數 (testValidatePassword_WithCorrectPasswordResetsAttempts)
- 在輸入錯誤密碼後，輸入正確密碼應重置錯誤嘗試次數
- 重置後應該能夠繼續正常使用帳戶
- 確保錯誤計數器正確重置

5. 鎖定帳戶驗證 (testValidatePassword_WithLockedAccount_ShouldReturnFalse)
- 帳戶被鎖定後的狀態檢查
- 確保鎖定後無法使用任何密碼（包括正確密碼）
- 驗證帳戶鎖定機制的持久性