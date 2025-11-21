package example.lotto.controller;

import example.lotto.domain.*;
import example.lotto.service.DrawService;
import example.lotto.service.PurchaseService;
import example.lotto.view.InputView;
import example.lotto.view.OutputView;
import myframework.annotation.Autowired;
import myframework.annotation.Component;

/**
 * 로또 발매기 프로그램의 전체 흐름을 담당하는 클래스
 */
@Component
public class LottoController {
    private final PurchaseService purchaseService;
    private final DrawService drawService;

    @Autowired
    public LottoController(PurchaseService purchaseService, DrawService drawService) {
        this.purchaseService = purchaseService;
        this.drawService = drawService;
    }

    public void run() {
        DepositAmount depositAmount = makeDeposit();
        Lottos lottos = buyLottosAndPrintIssuanceDetails(depositAmount);
        WinningNumbers winningNumbers = drawWinningNumbers();
        BonusNumber bonusNumber = drawBonusNumber(winningNumbers);
        Prizes prizes = drawPrizesAndPrintResult(lottos, winningNumbers, bonusNumber);
        analyzeProfitAndPrintResult(depositAmount, prizes);
    }

    private DepositAmount makeDeposit() {
        while (true) {
            try {
                return attemptDeposit();
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e);
            }
        }
    }

    private DepositAmount attemptDeposit() {
        OutputView.printDepositPrompt();
        String depositAmount = InputView.readDepositAmount();
        return purchaseService.depositMoney(depositAmount);
    }

    private Lottos buyLottosAndPrintIssuanceDetails(DepositAmount depositAmount) {
        Lottos lottos = purchaseService.purchaseLottos(depositAmount);
        OutputView.printLottoIssuanceDetails(lottos);
        return lottos;
    }

    private WinningNumbers drawWinningNumbers() {
        while (true) {
            try {
                return attemptSelectingWinningNumbers();
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e);
            }
        }
    }

    private WinningNumbers attemptSelectingWinningNumbers() {
        OutputView.printWinningNumberPrompt();
        String winningNumbers = InputView.readWinningNumbers();
        return drawService.determineWinningNumbers(winningNumbers);
    }

    private BonusNumber drawBonusNumber(WinningNumbers winningNumbers) {
        OutputView.printBlankLine();
        while (true) {
            try {
                return attemptSelectingBonusNumber(winningNumbers);
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e);
            }
        }
    }

    private BonusNumber attemptSelectingBonusNumber(WinningNumbers winningNumbers) {
        OutputView.printBonusNumberPrompt();
        String bonusNumber = InputView.readBonusNumber();
        return drawService.determineBonusNumber(bonusNumber, winningNumbers);
    }

    private Prizes drawPrizesAndPrintResult(Lottos lottos, WinningNumbers winningNumbers, BonusNumber bonusNumber) {
        Prizes prizes = drawService.checkLotteryResult(lottos, winningNumbers, bonusNumber);
        OutputView.printWinningResults(prizes);
        return prizes;
    }

    private void analyzeProfitAndPrintResult(DepositAmount depositAmount, Prizes prizes) {
        double profitRate = drawService.calculateProfitRate(depositAmount, prizes);
        OutputView.printProfitRate(profitRate);
    }
}
