package example.lotto.domain;

import example.lotto.error.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WinningNumbersTest {
    @Nested
    class SuccessTest {
        @DisplayName("당첨 번호 객체를 생성한다.")
        @Test
        void should_ReturnWinnerNumbers() {
            // given
            String input = "1,2,3,4,5,6";
            WinningNumbers winningNumbers = WinningNumbers.from(input);

            // when & then
            assertThat(winningNumbers.countMatchingNumbers(List.of(1, 2, 3, 4, 5, 6))).isEqualTo(6);
        }
    }

    @Nested
    class ExceptionTest {
        @DisplayName("입력값이 존재하지 않는 경우 예외가 발생한다.")
        @ParameterizedTest
        @EmptySource
        @NullSource
        @ValueSource(strings = {"  ", "", "1,,3,4,5,6", "1, ,3,4,5,6"})
        void should_ThrowException_ForNullOrBlank(String input) {
            // when & then
            assertThatThrownBy(() -> WinningNumbers.from(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.WINNING_NUMBERS_NULL_OR_BLANK.getMessage());
        }

        @DisplayName("당첨 번호의 개수가 6개가 아닐 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,2,3,4,5", "1,2,3,4,5,6,7"})
        void should_ThrowException_ForInvalidSizeOfWinningNumbers(String input) {
            assertThatThrownBy(() -> WinningNumbers.from(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.WINNING_NUMBERS_SIZE_INVALID.getMessage());
        }

        @DisplayName("입력값을 숫자로 변환 불가한 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,two,3,4,5,6", "1,둘,3,4,5,6", "1,:,3,4,5,6"})
        void should_ThrowException_When_NotConvertibleToNumeric(String input) {
            // when & then
            assertThatThrownBy(() -> WinningNumbers.from(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.WINNING_NUMBERS_NOT_NUMERIC.getMessage());
        }

        @DisplayName("1~45 범위 밖의 숫자가 당첨 번호에 포함된 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,2,3,4,5,47", "0,1,2,3,4,5", "-1,2,3,4,5,6"})
        void should_ThrowException_ForInvalidWinningNumber(String input) {
            // when & then
            assertThatThrownBy(() -> WinningNumbers.from(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.WINNING_NUMBERS_OUT_OF_RANGE.getMessage());
        }

        @DisplayName("당첨 번호에 중복된 숫자가 있을 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,2,3,4,5,5", "1,1,2,3,4,5", "1,2,2,3,4,5"})
        void should_ThrowException_ForDuplicatedWinningNumber(String input) {
            assertThatThrownBy(() -> WinningNumbers.from(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.WINNING_NUMBERS_DUPLICATED.getMessage());
        }
    }
}
