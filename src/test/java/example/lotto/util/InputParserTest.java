package example.lotto.util;

import example.lotto.error.InputNotNumericException;
import example.lotto.error.InputNullOrBlankException;
import example.lotto.error.InputNumberOverflowException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InputParserTest {
    @Nested
    class SuccessTest {
        @DisplayName("양 옆의 공백을 제거한 문자열을 반환한다.")
        @ParameterizedTest
        @CsvSource(value = {" 가나디 ,가나디", " 로또,로또", "당첨  ,당첨"})
        void should_ReturnTrimmedString(String input, String expected) {
            // when & then
            assertThat(InputParser.refineInput(input)).isEqualTo(expected);
        }

        @DisplayName("문자열을 숫자로 변환한다.")
        @Test
        void should_ReturnConvertedIntValue() {
            // when & then
            assertThat(InputParser.parseToInt("1")).isEqualTo(1);
        }
    }

    @Nested
    class ExceptionTest {
        @DisplayName("Null, 빈 문자열, 공백 문자열을 입력받을 경우 예외가 발생한다.")
        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = " ")
        void should_ThrowException_ForNullOfEmptyOrBlank(String input) {
            assertThatThrownBy(() -> InputParser.refineInput(input))
                    .isInstanceOf(InputNullOrBlankException.class);
        }

        @DisplayName("숫자로 변환 불가한 문자열을 입력받을 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"3천원", "3k", "$1000", "3,000", "3000.00", "3 000"})
        void should_ThrowException_When_NotConvertibleToNumeric(String input) {
            // when & then
            assertThatThrownBy(() -> InputParser.parseToInt(input))
                    .isInstanceOf(InputNotNumericException.class);
        }

        @DisplayName("Integer의 범위를 초과하는 문자열을 입력받을 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"999999999999999999999999999999", Integer.MAX_VALUE + 1L + ""})
        void should_ThrowException_When_IntegerOverflow(String input) {
            // when & then
            assertThatThrownBy(() -> InputParser.parseToInt(input))
                    .isInstanceOf(InputNumberOverflowException.class);
        }
    }


}
