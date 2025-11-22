package example.lotto.controller;

import example.lotto.domain.Prize;
import example.lotto.domain.Prizes;
import example.lotto.service.DrawService;
import example.lotto.service.PurchaseService;
import example.lotto.util.LottoNumberGenerator;
import example.lotto.view.InputView;
import example.lotto.view.OutputView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrencyTest {
    static class ConcurrentInputView implements InputView {
        private final List<String> inputs;
        private int index = 0;

        public ConcurrentInputView(String... inputs) {
            this.inputs = List.of(inputs);
        }

        @Override
        public String readDepositAmount() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return next();
        }

        @Override
        public String readWinningNumbers() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return next();
        }

        @Override
        public String readBonusNumber() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return next();
        }

        private String next() {
            return inputs.get(index++);
        }
    }

    static class ConcurrentOutputView implements OutputView {
        public final List<String> errorMessages;
        public Prizes prizes;
        public double profitRate;

        public ConcurrentOutputView() {
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
    @DisplayName("DI 방식: 100명이 동시에 로또 게임을 해도 각자 결과가 겹치거나 오류가 발생하지 않는다")
    void di_MultiThread_NoOverwrite_NoException() {
        // given
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        List<LottoNumberGenerator> lottoNumberGenerators = new ArrayList<>();
        lottoNumberGenerators.add(() -> List.of(1, 2, 3, 4, 5, 6)); // 1등 당첨 로또 번호 생성기
        lottoNumberGenerators.add(() -> List.of(1, 2, 3, 4, 5, 7)); // 2등 당첨 로또 번호 생성기
        lottoNumberGenerators.add(() -> List.of(1, 2, 3, 4, 5, 8)); // 3등 당첨 로또 번호 생성기
        lottoNumberGenerators.add(() -> List.of(1, 2, 3, 4, 8, 9)); // 4등 당첨 로또 번호 생성기
        lottoNumberGenerators.add(() -> List.of(1, 2, 3, 8, 9, 10)); // 5등 당첨 로또 번호 생성기
        lottoNumberGenerators.add(() -> List.of(1, 2, 8, 9, 10, 11)); // 꽝

        List<Prize> prizes = new ArrayList<>();
        prizes.add(Prize.FIRST_PRIZE);
        prizes.add(Prize.SECOND_PRIZE);
        prizes.add(Prize.THIRD_PRIZE);
        prizes.add(Prize.FOURTH_PRIZE);
        prizes.add(Prize.FIFTH_PRIZE);
        prizes.add(Prize.NONE);

        List<Double> profitRates = new ArrayList<>();
        profitRates.add(200000000.0);
        profitRates.add(3000000.0);
        profitRates.add(150000.0);
        profitRates.add(5000.0);
        profitRates.add(500.0);
        profitRates.add(0.0);

        // when
        for (int i = 0; i < threadCount; i++) {
            int finalJ = i % 6;
            futures.add(CompletableFuture.runAsync(() -> {
                InputView inputView = new ConcurrentInputView("1000", "1,2,3,4,5,6", "7");
                ConcurrentOutputView outputView = new ConcurrentOutputView();

                PurchaseService purchaseService = new PurchaseService(lottoNumberGenerators.get(finalJ));
                DrawService drawService = new DrawService();

                LottoController controller = new LottoController(purchaseService, drawService, inputView, outputView);

                controller.run();

                assertThat(outputView.prizes.getPrizesCount(prizes.get(finalJ))).isEqualTo(1);
                assertThat(outputView.profitRate).isEqualTo(profitRates.get(finalJ));
            }, executor));
        }

        // then
        assertDoesNotThrow(() ->
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join()
        );
    }
}
