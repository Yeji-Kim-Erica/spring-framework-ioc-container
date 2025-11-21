package experiment.statics.lotto.controller;

import experiment.statics.lotto.domain.*;
import experiment.statics.lotto.service.DrawService;
import experiment.statics.lotto.service.PurchaseService;
import experiment.statics.lotto.view.InputView;
import experiment.statics.lotto.view.OutputView;

/**
 * 로또 발매기 프로그램의 전체 흐름을 담당하는 클래스
 */
public final class LottoController {
    private LottoController() {}

    public static void run() {
        DepositAmount depositAmount = makeDeposit();
        Lottos lottos = buyLottosAndPrintIssuanceDetails(depositAmount);
        WinningNumbers winningNumbers = drawWinningNumbers();
        BonusNumber bonusNumber = drawBonusNumber(winningNumbers);
        Prizes prizes = drawPrizesAndPrintResult(lottos, winningNumbers, bonusNumber);
        analyzeProfitAndPrintResult(depositAmount, prizes);
    }

    private static DepositAmount makeDeposit() {
        while (true) {
            try {
                return attemptDeposit();
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e);
            }
        }
    }

    private static DepositAmount attemptDeposit() {
        OutputView.printDepositPrompt();
        String depositAmount = InputView.readDepositAmount();
        return PurchaseService.depositMoney(depositAmount);
    }

    private static Lottos buyLottosAndPrintIssuanceDetails(DepositAmount depositAmount) {
        Lottos lottos = PurchaseService.purchaseLottos(depositAmount);
        OutputView.printLottoIssuanceDetails(lottos);
        return lottos;
    }

    private static WinningNumbers drawWinningNumbers() {
        while (true) {
            try {
                return attemptSelectingWinningNumbers();
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e);
            }
        }
    }

    private static WinningNumbers attemptSelectingWinningNumbers() {
        OutputView.printWinningNumberPrompt();
        String winningNumbers = InputView.readWinningNumbers();
        return DrawService.determineWinningNumbers(winningNumbers);
    }

    private static BonusNumber drawBonusNumber(WinningNumbers winningNumbers) {
        OutputView.printBlankLine();
        while (true) {
            try {
                return attemptSelectingBonusNumber(winningNumbers);
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e);
            }
        }
    }

    private static BonusNumber attemptSelectingBonusNumber(WinningNumbers winningNumbers) {
        OutputView.printBonusNumberPrompt();
        String bonusNumber = InputView.readBonusNumber();
        return DrawService.determineBonusNumber(bonusNumber, winningNumbers);
    }

    private static Prizes drawPrizesAndPrintResult(Lottos lottos, WinningNumbers winningNumbers, BonusNumber bonusNumber) {
        Prizes prizes = DrawService.checkLotteryResult(lottos, winningNumbers, bonusNumber);
        OutputView.printWinningResults(prizes);
        return prizes;
    }

    private static void analyzeProfitAndPrintResult(DepositAmount depositAmount, Prizes prizes) {
        double profitRate = DrawService.calculateProfitRate(depositAmount, prizes);
        OutputView.printProfitRate(profitRate);
    }
}
