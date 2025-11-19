package example.lotto.domain;

import example.lotto.util.LottoNumberGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class PrizesTest {
    @Nested
    class SuccessTest {
        @DisplayName("로또의 당첨 결과를 확인해 Prizes 객체를 반환한다.")
        @ParameterizedTest(name = "2등 당첨 장수는 {1}장이다.")
        @MethodSource
        void should_ReturnsPrizes(Lottos lottos, int expected) {
            // given
            WinningNumbers winningNumbers = WinningNumbers.from("1,2,3,4,5,7");
            BonusNumber bonusNumber = BonusNumber.of("6", winningNumbers);

            // when
            Prizes prizes = Prizes.of(lottos, winningNumbers, bonusNumber);
            Set<Entry<Prize, Integer>> result = prizes.getPrizesCountEntries();

            // then
            assertThat(result)
                    .containsExactlyInAnyOrder(
                            entry(Prize.SECOND_PRIZE, expected),
                            entry(Prize.FIRST_PRIZE, 0),
                            entry(Prize.THIRD_PRIZE, 0),
                            entry(Prize.FOURTH_PRIZE, 0),
                            entry(Prize.FIFTH_PRIZE, 0),
                            entry(Prize.NONE, 0)
                    );
        }

        @DisplayName("로또 당첨금을 합산한 결과를 반환한다.")
        @ParameterizedTest
        @CsvSource(value = {"3000, 6000000000", "2147483000, 4294966000000000"})
        void should_ReturnsSumOfWinningsAmount(String depositAmount, long totalWinningsAmount) {
            // given
            DepositAmount deposit = DepositAmount.from(depositAmount);
            int purchasedLotto = deposit.getNumberOfPurchasableLotto();
            LottoNumberGenerator lottoNumberGenerator = new LottoNumberGenerator() {
                @Override
                public List<Integer> generateUniqueNumbersInRange() {
                    return List.of(1,2,3,4,5,6);
                }
            };
            Lottos lottos = Lottos.issue(purchasedLotto, lottoNumberGenerator);
            WinningNumbers winningNumbers = WinningNumbers.from("1,2,3,4,5,6");
            BonusNumber bonusNumber = BonusNumber.of("7", winningNumbers);

            // when
            Prizes prizes = Prizes.of(lottos, winningNumbers, bonusNumber);

            // then
            assertThat(prizes.calculateTotalWinningAmount())
                    .isEqualTo(totalWinningsAmount);
        }

        private static Stream<Arguments> should_ReturnsPrizes() {
            LottoNumberGenerator lottoNumberGenerator = new LottoNumberGenerator() {
                @Override
                public List<Integer> generateUniqueNumbersInRange() {
                    return List.of(1,2,3,4,5,6);
                }
            };
            return Stream.of(
                    Arguments.of(Lottos.issue(1, lottoNumberGenerator), 1),
                    Arguments.of(Lottos.issue(2, lottoNumberGenerator), 2),
                    Arguments.of(Lottos.issue(3, lottoNumberGenerator), 3)
            );
        }
    }
}
