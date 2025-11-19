package example.lotto.domain;

import example.lotto.util.LottoNumberGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LottosTest {
    @Nested
    class SuccessTest {
        @DisplayName("구매 수량만큼 로또를 발행한다.")
        @ParameterizedTest(name = "{0}개의 로또를 발행한다.")
        @ValueSource(ints = {1, 2, 3, 4, 5})
        void should_ReturnSizeOfLottos(int quantity) {
            // given
            LottoNumberGenerator lottoNumberGenerator = new LottoNumberGenerator() {
                @Override
                public List<Integer> generateUniqueNumbersInRange() {
                    return List.of(1, 2, 3, 4, 5, 6);
                }
            };

            // when
            Lottos lottos = Lottos.issue(quantity, lottoNumberGenerator);

            // then
            assertThat(lottos.size()).isEqualTo(quantity);
        }
    }
}
