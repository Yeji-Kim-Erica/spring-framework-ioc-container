package example.lotto.service;

import example.lotto.domain.*;
import myframework.annotation.Component;

import java.util.List;

/**
 * 당첨 처리 관련 로직을 담당하는 클래스
 */
@Component
public class DrawService {
    private static final int RATE_TO_PERCENTAGE = 100;

    public WinningNumbers determineWinningNumbers(String winningNumbers) {
        return WinningNumbers.from(winningNumbers);
    }

    public BonusNumber determineBonusNumber(String input, WinningNumbers winningNumbers) {
        return BonusNumber.of(input, winningNumbers);
    }

    public Prizes checkLotteryResult(Lottos lottos, WinningNumbers winningNumbers, BonusNumber bonusNumber) {
        List<Prize> prizes = lottos.getPrizeResults(winningNumbers, bonusNumber);
        return Prizes.from(prizes);
    }

    public double calculateProfitRate(DepositAmount depositAmount, Prizes prizes) {
        long totalProfit = prizes.calculateTotalWinningAmount();
        double profitRatio = depositAmount.divideProfitByExpense(totalProfit);
        return profitRatio * RATE_TO_PERCENTAGE;
    }
}
