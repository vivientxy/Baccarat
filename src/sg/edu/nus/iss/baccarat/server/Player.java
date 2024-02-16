package sg.edu.nus.iss.baccarat.server;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Player {
    private final String name;
    private BigDecimal wallet;
    private BigDecimal betAmount;

    public Player(String name, BigDecimal wallet) {
        this.name = name;
        this.wallet = wallet.setScale(2, RoundingMode.HALF_UP);
    }

    public String getName() {
        return name;
    }

    public BigDecimal getWallet() {
        return wallet;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setWallet(BigDecimal wallet) {
        this.wallet = wallet.setScale(2, RoundingMode.HALF_UP);
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
