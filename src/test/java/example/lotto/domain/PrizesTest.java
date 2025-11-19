package example.lotto.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PrizesTest {
    @Nested
    class SuccessTest {
        @DisplayName("로또 당첨금을 합산한 결과를 반환한다.")
        @Test
        void should_ReturnsSumOfWinningsAmount() {
            // given
            List<Prize> prizes = List.of(
                    Prize.FIRST_PRIZE,
                    Prize.FOURTH_PRIZE,
                    Prize.NONE,
                    Prize.FIRST_PRIZE,
                    Prize.FIFTH_PRIZE
            );

            // when & then
            assertThat(Prizes.from(prizes).calculateTotalWinningAmount())
                    .isEqualTo(4000055000L);
        }
    }
}
