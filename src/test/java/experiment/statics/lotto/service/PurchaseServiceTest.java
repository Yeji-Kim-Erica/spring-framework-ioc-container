package experiment.statics.lotto.service;

import experiment.statics.lotto.domain.DepositAmount;
import experiment.statics.lotto.domain.Lottos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PurchaseServiceTest {
    @Nested
    class SuccessTest {
        @DisplayName("유효한 금액 입력 시 입력한 금액만큼을 잔고로 가진 DepositAmount 객체를 생성한다.")
        @ParameterizedTest(name = "{0}원 입금, 로또 가격 {1}원: 로또 {2}장 구매 가능")
        @CsvSource(value = {"5000, 1000, 5", "10000, 1000, 10", "3000, 3000, 1"})
        void should_CreateDepositAmountOfCorrectDeposit(String input, int lottoPrice, int expected) {
            // when
            DepositAmount result = PurchaseService.depositMoney(input);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getNumberOfPurchasableLotto(lottoPrice)).isEqualTo(expected);
        }

        @DisplayName("유효한 금액으로 구매 요청 시 올바른 수량의 Lotto를 가진 Lottos 객체를 생성한다.")
        @ParameterizedTest(name = "{0}원 입금 시 로또 {1}장 발행")
        @CsvSource(value = {"5000, 5", "10000, 10", "3000, 3"})
        void should_CreateCorrectAmountOfLottos(String input, int expected) {
            // given
            DepositAmount amount = DepositAmount.from(input);

            // when
            Lottos result = PurchaseService.purchaseLottos(amount);

            // then
            assertThat(result.size()).isEqualTo(expected);
        }
    }

    @Nested
    class ExceptionTest {
        @DisplayName("입금 과정 중 유효성 검증 실패 시 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"2500", "1000원"})
        void should_ThrowException_WhenWrongDepositOccurs(String input) {
            // when & then
            assertThatThrownBy(() -> PurchaseService.depositMoney(input))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
