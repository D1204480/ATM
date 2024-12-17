package org.example;

public enum CurrencyType {
    TWD("新台幣", 1.0),
    USD("美元", 31.5),
    EUR("歐元", 34.2),
    JPY("日圓", 0.21);

    private final String description;
    private final double exchangeRate; // 對台幣的匯率

    CurrencyType(String description, double exchangeRate) {
        this.description = description;
        this.exchangeRate = exchangeRate;
    }

    public String getDescription() {
        return description;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    // 將其他貨幣轉換為台幣
    public double convertToTWD(double amount) {
        return amount * exchangeRate;
    }

    // 將台幣轉換為其他貨幣
    public double convertFromTWD(double amount) {
        return amount / exchangeRate;
    }
}