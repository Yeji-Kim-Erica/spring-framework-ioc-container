package example.lotto.domain;

import example.lotto.error.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PrizeTest {
    @Nested
    class SuccessTest {
        @DisplayName("당첨 번호 일치 개수와 보너스 번호 일치 여부에 따라 정확한 Prize 등급을 반환한다")
        @ParameterizedTest(name = "당첨 번호 {0}개 일치, 보너스 번호 {1} → {2} 등급")
        @CsvSource(value = {
                "6, false, FIRST_PRIZE",
                "5, true, SECOND_PRIZE",
                "5, false, THIRD_PRIZE",
                "4, false, FOURTH_PRIZE",
                "3, false, FIFTH_PRIZE",
                "2, false, NONE",
                "1, true, NONE",
                "0, true, NONE"
        })
        void should_ReturnCorrectPrize(int winningNumbersMatchCount, boolean hasMatchingBonusNumber, Prize expected) {
            // when
            Prize result = Prize.of(winningNumbersMatchCount, hasMatchingBonusNumber);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    class ExceptionTest{
        @DisplayName("유효하지 않은 매칭 개수를 입력받을 경우 예외가 발생한다")
        @ParameterizedTest(name = "{0}은 유효하지 않은 매칭 개수이다")
        @ValueSource(ints = {-1, 7, 10})
        void should_ThrowsException_ForInvalidMatchCount(int winningNumbersMatchCount) {
            // given
            boolean hasMatchingBonusNumber = true;

            // when & then
            assertThatThrownBy(() -> Prize.of(winningNumbersMatchCount, hasMatchingBonusNumber))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.PRIZE_MATCH_COUNT_OUT_OF_RANGE.getMessage()); // 적절한 ErrorMessage로 변경
        }
    }
}
