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
    private final InputView inputView;
    private final OutputView outputView;
    private final PurchaseService purchaseService;
    private final DrawService drawService;

    @Autowired
    public LottoController(PurchaseService purchaseService, DrawService drawService,
                           InputView inputView, OutputView outputView) {
        this.purchaseService = purchaseService;
        this.drawService = drawService;
        this.inputView = inputView;
        this.outputView = outputView;
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
                outputView.printErrorMessage(e);
            }
        }
    }

    private DepositAmount attemptDeposit() {
        outputView.printDepositPrompt();
        String depositAmount = inputView.readDepositAmount();
        return purchaseService.depositMoney(depositAmount);
    }

    private Lottos buyLottosAndPrintIssuanceDetails(DepositAmount depositAmount) {
        Lottos lottos = purchaseService.purchaseLottos(depositAmount);
        outputView.printLottoIssuanceDetails(lottos);
        return lottos;
    }

    private WinningNumbers drawWinningNumbers() {
        while (true) {
            try {
                return attemptSelectingWinningNumbers();
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e);
            }
        }
    }

    private WinningNumbers attemptSelectingWinningNumbers() {
        outputView.printWinningNumberPrompt();
        String winningNumbers = inputView.readWinningNumbers();
        return drawService.determineWinningNumbers(winningNumbers);
    }

    private BonusNumber drawBonusNumber(WinningNumbers winningNumbers) {
        outputView.printBlankLine();
        while (true) {
            try {
                return attemptSelectingBonusNumber(winningNumbers);
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e);
            }
        }
    }

    private BonusNumber attemptSelectingBonusNumber(WinningNumbers winningNumbers) {
        outputView.printBonusNumberPrompt();
        String bonusNumber = inputView.readBonusNumber();
        return drawService.determineBonusNumber(bonusNumber, winningNumbers);
    }

    private Prizes drawPrizesAndPrintResult(Lottos lottos, WinningNumbers winningNumbers, BonusNumber bonusNumber) {
        Prizes prizes = drawService.checkLotteryResult(lottos, winningNumbers, bonusNumber);
        outputView.printWinningResults(prizes);
        return prizes;
    }

    private void analyzeProfitAndPrintResult(DepositAmount depositAmount, Prizes prizes) {
        double profitRate = drawService.calculateProfitRate(depositAmount, prizes);
        outputView.printProfitRate(profitRate);
    }
}
