package experiment.statics.lotto.service;

import experiment.statics.lotto.domain.*;

import java.util.List;

/**
 * 당첨 처리 관련 로직을 담당하는 클래스
 */
public final class DrawService {
    private static final int RATE_TO_PERCENTAGE = 100;

    private DrawService() {}

    public static WinningNumbers determineWinningNumbers(String winningNumbers) {
        return WinningNumbers.from(winningNumbers);
    }

    public static BonusNumber determineBonusNumber(String input, WinningNumbers winningNumbers) {
        return BonusNumber.of(input, winningNumbers);
    }

    public static Prizes checkLotteryResult(Lottos lottos, WinningNumbers winningNumbers, BonusNumber bonusNumber) {
        List<Prize> prizes = lottos.getPrizeResults(winningNumbers, bonusNumber);
        return Prizes.from(prizes);
    }

    public static double calculateProfitRate(DepositAmount depositAmount, Prizes prizes) {
        long totalProfit = prizes.calculateTotalWinningAmount();
        double profitRatio = depositAmount.divideProfitByExpense(totalProfit);
        return profitRatio * RATE_TO_PERCENTAGE;
    }
}
