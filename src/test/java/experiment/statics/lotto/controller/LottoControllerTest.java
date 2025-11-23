package experiment.statics.lotto.controller;

import example.lotto.error.ErrorMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class LottoControllerTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    public void restoreStreams() {
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    @DisplayName("정상적인 입력이 들어오면 로또 게임이 끝까지 실행되고 결과가 출력된다.")
    void run_Success() {
        // given
        String input = "1000\n1,2,3,4,5,6\n7";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // when
        LottoController.run();

        // then
        String result = output.toString();
        // 랜덤 생성기 때문에 당첨 결과를 예측할 수 없으므로 구체적인 등수나 수익률 검증 불가, 단순 출력 여부만 확인한다
        assertThat(result).contains("총 수익률은");
    }

    @Test
    @DisplayName("잘못된 금액을 입력하면 에러 메시지를 출력하고 다시 입력을 받는다.")
    void run_Retry_WhenInputIsInvalid() {
        // given
        String input = "천원\n1000\n1,2,3,4,5,6\n7";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // when
        LottoController.run();

        // then
        String result = output.toString();
        assertThat(result).contains(ErrorMessage.DEPOSIT_AMOUNT_NOT_NUMERIC.getMessage());
        assertThat(result).contains("총 수익률은");
    }
}
