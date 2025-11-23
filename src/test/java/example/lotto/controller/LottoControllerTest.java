package example.lotto.controller;

import example.lotto.domain.Prize;
import example.lotto.domain.Prizes;
import example.lotto.error.ErrorMessage;
import example.lotto.service.DrawService;
import example.lotto.service.PurchaseService;
import example.lotto.util.LottoNumberGenerator;
import example.lotto.view.InputView;
import example.lotto.view.OutputView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LottoControllerTest {
     static class FakeInputView implements InputView {
        private final List<String> inputs;
        private int index = 0;

        public FakeInputView(String... inputs) {
            this.inputs = List.of(inputs);
        }

        @Override
        public String readDepositAmount() {
            return next();
        }

        @Override
        public String readWinningNumbers() {
            return next();
        }

        @Override
        public String readBonusNumber() {
            return next();
        }

        private String next() {
            return inputs.get(index++);
        }
    }

    static class FakeOutputView implements OutputView {
        public final List<String> errorMessages;
        public Prizes prizes;
        public double profitRate;

        public FakeOutputView() {
            this.errorMessages = new ArrayList<>();
        }

        @Override
        public void printErrorMessage(IllegalArgumentException e) {
            this.errorMessages.add(e.getMessage());
        };

        @Override
        public void printWinningResults(Prizes prizes) {
            this.prizes = prizes;
        };

        @Override
        public void printProfitRate(double profitRate) {
            this.profitRate = profitRate;
        };
    }

    @Test
    @DisplayName("정상적인 입력이 들어오면 로또 게임이 끝까지 실행되고 결과가 출력된다.")
    void run_Success() {
        // given
        InputView inputView = new FakeInputView("1000", "1,2,3,4,5,6", "7");
        FakeOutputView outputView = new FakeOutputView();

        LottoNumberGenerator lottoNumberGenerator = () -> List.of(1, 2, 3, 4, 5, 6);
        PurchaseService purchaseService = new PurchaseService(lottoNumberGenerator);
        DrawService drawService = new DrawService();

        LottoController controller = new LottoController(purchaseService, drawService, inputView, outputView);

        // when
        controller.run();

        // then
        assertThat(outputView.prizes.getPrizesCount(Prize.FIRST_PRIZE)).isEqualTo(1);
        assertThat(outputView.profitRate).isEqualTo(200000000.0);
    }

    @Test
    @DisplayName("잘못된 금액을 입력하면 에러 메시지를 출력하고 다시 입력을 받는다.")
    void run_Retry_WhenInputIsInvalid() {
        // given
        InputView inputView = new FakeInputView("천원", "1000", "1,2,3,4,5,6", "7");
        FakeOutputView outputView = new FakeOutputView();

        LottoNumberGenerator lottoNumberGenerator = () -> List.of(1, 2, 3, 4, 5, 7);
        PurchaseService purchaseService = new PurchaseService(lottoNumberGenerator);
        DrawService drawService = new DrawService();

        LottoController controller = new LottoController(purchaseService, drawService, inputView, outputView);

        // when
        controller.run();

        // then
        assertThat(outputView.errorMessages).isNotNull()
                        .containsExactly(ErrorMessage.DEPOSIT_AMOUNT_NOT_NUMERIC.getMessage());
        assertThat(outputView.prizes.getPrizesCount(Prize.SECOND_PRIZE)).isEqualTo(1);
    }
}
