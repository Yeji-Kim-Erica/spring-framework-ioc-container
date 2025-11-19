package example.lotto.domain;

import example.lotto.error.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
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

public class BonusNumberTest {
    private WinningNumbers winningNumbers;

    @BeforeEach
    void setUp() {
        winningNumbers = WinningNumbers.from("1,2,3,4,5,6");
    }

    @Nested
    class SuccessTest {
        @DisplayName("당첨 번호 객체를 생성한다.")
        @Test
        void should_ReturnBonusNumber() {
            // given
            String input = "7";
            BonusNumber bonusNumber = BonusNumber.of(input, winningNumbers);

            // when & then
            assertThat(bonusNumber.hasMatchingNumber(List.of(7))).isTrue();
        }
    }

    @Nested
    class ExceptionTest {
        @DisplayName("입력값이 존재하지 않는 경우 예외가 발생한다.")
        @ParameterizedTest
        @EmptySource
        @NullSource
        @ValueSource(strings = {"  ", ""})
        void should_ThrowException_ForNullOrBlank(String input) {
            // when & then
            assertThatThrownBy(() -> BonusNumber.of(input, winningNumbers))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.BONUS_NUMBER_NULL_OR_BLANK.getMessage());
        }

        @DisplayName("입력값을 숫자로 변환 불가한 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"two", "둘", "1.00"})
        void should_ThrowException_When_NotConvertibleToNumeric(String input) {
            // when & then
            assertThatThrownBy(() -> BonusNumber.of(input, winningNumbers))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.BONUS_NUMBER_NOT_NUMERIC.getMessage());
        }

        @DisplayName("보너스 번호가 1~45 범위를 벗어나는 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"47", "0", "-1"})
        void should_ThrowException_ForInvalidBonusNumber(String input) {
            // when & then
            assertThatThrownBy(() -> BonusNumber.of(input, winningNumbers))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.BONUS_NUMBER_OUT_OF_RANGE.getMessage());
        }

        @DisplayName("보너스 번호가 당첨 번호와 중복될 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"1", "2", "3"})
        void should_ThrowException_When_DuplicatedWithWinningNumber(String input) {
            assertThatThrownBy(() -> BonusNumber.of(input, winningNumbers))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.BONUS_NUMBER_DUPLICATED_WITH_WINNING_NUMBERS.getMessage());
        }
    }
}
