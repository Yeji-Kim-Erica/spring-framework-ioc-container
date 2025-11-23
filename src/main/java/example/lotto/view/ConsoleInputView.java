package example.lotto.view;

import camp.nextstep.edu.missionutils.Console;
import myframework.annotation.Component;

/**
 * 프로그램의 콘솔 입력을 담당하는 클래스
 */
@Component
public class ConsoleInputView implements InputView {
    @Override
    public String readDepositAmount() {
        return Console.readLine();
    }

    @Override
    public String readWinningNumbers() {
        return Console.readLine();
    }

    @Override
    public String readBonusNumber() {
        return Console.readLine();
    }
}
