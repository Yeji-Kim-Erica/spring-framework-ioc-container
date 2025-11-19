package example.lotto.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LottosTest {
    @Nested
    class SuccessTest {
        @DisplayName("당첨 번호와 보너스 번호를 받아서 로또 당첨 결과를 반환한다.")
        @Test
        void should_ReturnLottoResults() {
            // given
            WinningNumbers winningNumbers = WinningNumbers.from("1,2,3,4,5,6");
            BonusNumber bonusNumber = BonusNumber.of("7", winningNumbers);
            List<Lotto> lottos = List.of(
                    new Lotto(List.of(1,2,3,4,5,6)),
                    new Lotto(List.of(1,2,3,4,5,7)),
                    new Lotto(List.of(1,2,3,4,5,8)),
                    new Lotto(List.of(1,2,3,4,7,8)),
                    new Lotto(List.of(1,2,3,7,8,9)),
                    new Lotto(List.of(1,2,7,8,9,10))
            );

            // when & then
            assertThat(new Lottos(lottos).getPrizeResults(winningNumbers, bonusNumber))
                    .containsExactly(
                            Prize.FIRST_PRIZE,
                            Prize.SECOND_PRIZE,
                            Prize.THIRD_PRIZE,
                            Prize.FOURTH_PRIZE,
                            Prize.FIFTH_PRIZE,
                            Prize.NONE
                    );
        }
    }
}
