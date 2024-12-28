<h4>performWithdrawal 執行提款操作 測試案例</h4>
1. 成功提領台幣, 會執行以下方法
- canWithdraw
- deductBalance
- addDailyWithdrawnAmount

2. 成功提領外幣, 會執行下列方法
- canWithdraw
- deductBalance
- addDailyWithdrawnAmount

3. 驗證成功但台幣餘額不足的測試
- deductBalance 不會被執行
- addDailyWithdrawnAmount 不會被執行

4. 驗證成功但美金餘額不足的測試
- deductBalance 不會被執行
- addDailyWithdrawnAmount 不會被執行

5. 提款驗證失敗的測試, 不會執行下列方法
- canWithdraw
- deductBalance
- addDailyWithdrawnAmount

6. 成功提款但餘額低於警告值的測試, 會執行下列方法
- deductBalance
- addDailyWithdrawnAmount
- isLowBalance
- 印出低餘額警告

7. 成功提款且餘額正常的測試, 會執行下列方法
- deductBalance
- addDailyWithdrawnAmount
- isLowBalance
- 印出餘額 (不包含"餘額低於"字眼)