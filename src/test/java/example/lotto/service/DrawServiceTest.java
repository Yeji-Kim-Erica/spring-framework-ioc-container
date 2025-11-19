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
            Lottos lottos = new Lottos(List.of(
                    new Lotto(List.of(1,2,3,4,5,6)),
                    new Lotto(List.of(1,2,3,4,5,7)),
                    new Lotto(List.of(1,2,3,4,5,8)),
                    new Lotto(List.of(1,7,8,9,10,11))
            ));
            WinningNumbers winningNumbers = WinningNumbers.from("1,2,3,4,5,6");
            BonusNumber bonusNumber = BonusNumber.of("7", winningNumbers);

            // when
            Prizes prizes = drawService.checkLotteryResult(lottos, winningNumbers, bonusNumber);

            // then
            assertThat(prizes.getPrizesCount(Prize.FIRST_PRIZE)).isEqualTo(1);
            assertThat(prizes.getPrizesCount(Prize.SECOND_PRIZE)).isEqualTo(1);
            assertThat(prizes.getPrizesCount(Prize.THIRD_PRIZE)).isEqualTo(1);
            assertThat(prizes.getPrizesCount(Prize.FOURTH_PRIZE)).isEqualTo(0);
            assertThat(prizes.getPrizesCount(Prize.FIFTH_PRIZE)).isEqualTo(0);
            assertThat(prizes.getPrizesCount(Prize.NONE)).isEqualTo(1);
        }

        @DisplayName("구입 금액 대비 로또 당첨 결과 수익률을 반환한다.")
        @Test
        void should_ReturnProfitRate() {
            // given
            DepositAmount depositAmount = DepositAmount.from("8000");
            Prizes prizes = Prizes.from(
                    List.of(Prize.FIFTH_PRIZE)
            );

            // when & then
            assertThat(drawService.calculateProfitRate(depositAmount, prizes))
                    .isEqualTo(62.5);
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
