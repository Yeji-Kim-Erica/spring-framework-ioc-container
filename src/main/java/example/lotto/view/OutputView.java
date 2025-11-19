package example.lotto.view;

import example.lotto.domain.Lotto;
import example.lotto.domain.Lottos;
import example.lotto.domain.Prize;
import example.lotto.domain.Prizes;

import java.text.DecimalFormat;

/**
 * 프로그램의 모든 출력을 담당하는 클래스
 */
public final class OutputView {
    private static final String DEPOSIT_INPUT_PROMPT = "구입금액을 입력해 주세요.";
    private static final String LOTTO_QUANTITY_ISSUED = "%d개를 구매했습니다.";
    private static final String WINNING_NUMBER_INPUT_PROMPT = "당첨 번호를 입력해 주세요.";
    private static final String BONUS_NUMBER_INPUT_PROMPT = "보너스 번호를 입력해 주세요.";
    private static final String WINNING_RESULTS_INITIAL_LINE = "당첨 통계\n--";
    private static final String WINNING_RESULT_FORMAT = "%d개 일치%s (%s원) - %d개";
    private static final DecimalFormat PRIZE_MONEY_DECIMAL_FORMAT = new DecimalFormat("###,###");
    private static final String WINNING_RESULT_BONUS_NUMBER = ", 보너스 볼 일치";
    private static final String PROFIT_RATE_FORMAT = "총 수익률은 %,.1f%%입니다."; // %.1f를 사용해 소수점 둘째 자리에서 반올림

    private OutputView() {}

    public static void printBlankLine() {
        System.out.println();
    }

    public static void printDepositPrompt() {
        System.out.println(DEPOSIT_INPUT_PROMPT);
    }

    public static void printErrorMessage(IllegalArgumentException e) {
        System.out.println(e.getMessage());
        System.out.println();
    }

    public static void printLottoIssuanceDetails(Lottos lottos) {
        System.out.println();
        System.out.printf(LOTTO_QUANTITY_ISSUED, lottos.size());
        System.out.println();
        for (Lotto lotto : lottos.getLottos()) {
            System.out.println(lotto);
        }
        System.out.println();
    }

    public static void printWinningNumberPrompt() {
        System.out.println(WINNING_NUMBER_INPUT_PROMPT);
    }

    public static void printBonusNumberPrompt() {
        System.out.println(BONUS_NUMBER_INPUT_PROMPT);
    }

    public static void printWinningResults(Prizes prizes) {
        System.out.println();
        System.out.println(WINNING_RESULTS_INITIAL_LINE);
        for (Prize prize : Prize.values()) {
            if (prize == Prize.NONE) continue;

            int count = prizes.getPrizesCount(prize);
            printWinningResult(prize, count);
        }
    }

    public static void printProfitRate(double profitRate) {
        System.out.printf(PROFIT_RATE_FORMAT, profitRate);
        System.out.println();
    }

    private static void printWinningResult(Prize prize, int count) {
        int matchCount = prize.getWinningNumbersMatchCount();
        String bonusMatch = "";
        if (prize.isMatchingBonusNumber()) {
            bonusMatch = WINNING_RESULT_BONUS_NUMBER;
        }
        String prizeMoney = PRIZE_MONEY_DECIMAL_FORMAT.format(prize.getWinningsAmount());
        System.out.printf(WINNING_RESULT_FORMAT, matchCount, bonusMatch, prizeMoney, count);
        System.out.println();
    }
}
