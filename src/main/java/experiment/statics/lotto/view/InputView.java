package experiment.statics.lotto.view;

import camp.nextstep.edu.missionutils.Console;

/**
 * 프로그램의 모든 입력을 담당하는 클래스
 */
public final class InputView {
    private InputView() {}

    public static String readDepositAmount() {
        return Console.readLine();
    }

    public static String readWinningNumbers() {
        return Console.readLine();
    }

    public static String readBonusNumber() {
        return Console.readLine();
    }
}
