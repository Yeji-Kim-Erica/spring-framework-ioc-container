package example.lotto.service;

import example.lotto.domain.*;
import example.lotto.service.DrawService;
import example.lotto.util.LottoNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

public class DrawServiceTest {
    private DrawService drawService;

    @BeforeEach
    void setUp() {
        drawService = new DrawService();
    }

    @Nested
    class SuccessTest {
        @DisplayName("유효한 당첨 번호 입력 시 WinningNumbers 객체를 생성한다.")
        @Test
        void should_CreateWinningNumbers_ForValidInput() {
            // given
            String input = "1,2,3,4,5,6";

            // when
            WinningNumbers result = drawService.determineWinningNumbers(input);

            // then
            assertThat(result.countMatchingNumbers(List.of(1,2,3,4,5,6))).isEqualTo(6);
        }

        @DisplayName("유효한 보너스 번호 입력 시 BonusNumber 객체를 생성한다.")
        @Test
        void should_CreateBonusNumber_ForValidInput() {
            // given
            String input = "7";
            WinningNumbers winningNumbers = WinningNumbers.from("1,2,3,4,5,6");

            // when
            BonusNumber result = drawService.determineBonusNumber(input, winningNumbers);

            // then
            assertThat(result.hasMatchingNumber(List.of(7))).isTrue();
        }

        @DisplayName("로또 추첨 시 로또 당첨 결과를 담고 있는 Prizes 객체를 생성한다.")
        @Test
        void should_CreatePrizes() {
            // given
            LottoNumberGenerator lottoNumberGenerator = new LottoNumberGenerator() {
                @Override
                public List<Integer> generateUniqueNumbersInRange() {
                    return List.of(1,2,3,4,5,6);
                }
            };
            Lottos lottos = Lottos.issue(3, lottoNumberGenerator);
            WinningNumbers winningNumbers = WinningNumbers.from("1,2,3,4,5,6");
            BonusNumber bonusNumber = BonusNumber.of("7", winningNumbers);

            // when
            Prizes prizes = drawService.checkLotteryResult(lottos, winningNumbers, bonusNumber);
            Set<Entry<Prize, Integer>> result = prizes.getPrizesCountEntries();

            // then
            assertThat(result)
                    .containsExactlyInAnyOrder(
                            entry(Prize.FIRST_PRIZE, 3),
                            entry(Prize.SECOND_PRIZE, 0),
                            entry(Prize.THIRD_PRIZE, 0),
                            entry(Prize.FOURTH_PRIZE, 0),
                            entry(Prize.FIFTH_PRIZE, 0),
                            entry(Prize.NONE, 0)
                    );
        }

        @DisplayName("구입 금액 대비 로또 당첨 결과 수익률을 반환한다.")
        @ParameterizedTest(name = "{0}원 입금, {3}% 수익률")
        @CsvSource(value = {"13000:1,2,3,7,8,9:10:500.0", "1000:1,2,3,4,5,7:8:150000.0"}, delimiter = ':')
        void should_ReturnProfitRate(String deposit, String winningNum, String bonusNum, double expected) {
            // given
            DepositAmount depositAmount = DepositAmount.from(deposit);
            int purchasedAmount = depositAmount.getNumberOfPurchasableLotto();
            LottoNumberGenerator lottoNumberGenerator = new LottoNumberGenerator() {
                @Override
                public List<Integer> generateUniqueNumbersInRange() {
                    return List.of(1,2,3,4,5,6);
                }
            };
            Lottos lottos = Lottos.issue(purchasedAmount, lottoNumberGenerator);
            WinningNumbers winningNumbers = WinningNumbers.from(winningNum);
            BonusNumber bonusNumber = BonusNumber.of(bonusNum, winningNumbers);
            Prizes prizes = Prizes.of(lottos, winningNumbers, bonusNumber);

            // when & then
            assertThat(drawService.calculateProfitRate(depositAmount, prizes))
                    .isEqualTo(expected);
        }
    }

    @Nested
    class ExceptionTest {
        @DisplayName("당첨 번호 선정 중 유효성 검증 실패 시 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,2,3,4,5,5", "당첨번호"})
        void should_ThrowException_WhenInvalidWinningNumbersEntered(String input) {
            // when & then
            assertThatThrownBy(() -> drawService.determineWinningNumbers(input))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("보너스 번호 선정 중 유효성 검증 실패 시 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"", "보너스번호", " ", "1"})
        void should_ThrowException_WhenInvalidBonusNumberEntered(String input) {
            // given
            WinningNumbers winningNumbers = WinningNumbers.from("1,2,3,4,5,6");

            // when & then
            assertThatThrownBy(() -> drawService.determineBonusNumber(input, winningNumbers))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
