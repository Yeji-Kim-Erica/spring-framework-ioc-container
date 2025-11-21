package experiment.statics.lotto.error;

/**
 * 입력한 값이 유효한 범위를 초과한 경우 발생하는 예외 클래스
 */
public class InputNumberOverflowException extends RuntimeException {
    public InputNumberOverflowException() {
        super();
    }
}
