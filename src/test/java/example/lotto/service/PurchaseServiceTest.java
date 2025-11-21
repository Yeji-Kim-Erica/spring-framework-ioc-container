package example.lotto.service;

import example.lotto.domain.DepositAmount;
import example.lotto.domain.Lottos;
import myframework.support.TestUtils;
import example.lotto.util.LottoNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PurchaseServiceTest {
    private PurchaseService purchaseService;

    @BeforeEach
    void setUp() {

        LottoNumberGenerator lottoNumberGenerator = new LottoNumberGenerator() {
            @Override
            public List<Integer> generateUniqueNumbersInRange() {
                return List.of(1,2,3,4,5,6);
            }
        };
        purchaseService = new PurchaseService(lottoNumberGenerator);
    }

    @Nested
    class SuccessTest {
        @Test
        @DisplayName("유효한 금액 입력 시 DepositAmount 객체를 생성한다.")
        void depositMoney_ShouldReturnCorrectLottoCount() {
            // given
            String input = "5000";

            // when
            DepositAmount result = purchaseService.depositMoney(input);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("유효한 금액으로 구매 요청 시 올바른 수량의 Lottos 객체를 반환한다.")
        void should_ReturnCorrectQuantity() {
            // given
            DepositAmount amount = DepositAmount.from("5000");

            // when
            Lottos result = purchaseService.purchaseLottos(amount);

            // then
            assertThat(result.size()).isEqualTo(5);
        }
    }

    @Nested
    class ExceptionTest {
        @DisplayName("입금 과정 중 유효성 검증 실패 시 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"2500", "1000원"})
        void should_ThrowException_WhenWrongDepositOccurs(String input) {
            // when & then
            assertThatThrownBy(() -> purchaseService.depositMoney(input))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
