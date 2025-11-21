package example.lotto.view;

import example.lotto.domain.Lottos;
import example.lotto.domain.Prizes;

/**
 * 프로그램의 모든 출력을 담당하는 인터페이스
 */
public interface OutputView {
    default void printBlankLine() {};

    default void printDepositPrompt() {};

    default void printErrorMessage(IllegalArgumentException e) {};

    default void printLottoIssuanceDetails(Lottos lottos) {};

    default void printWinningNumberPrompt() {};

    default void printBonusNumberPrompt() {};

    default void printWinningResults(Prizes prizes) {};

    default void printProfitRate(double profitRate) {};
}
