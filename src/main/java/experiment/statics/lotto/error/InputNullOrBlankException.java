package experiment.statics.lotto.error;

/**
 * 입력값이 null이거나, 비어 있거나, 공백 문자로만 이루어진 경우 발생하는 예외 클래스
 */
public class InputNullOrBlankException extends RuntimeException {
    public InputNullOrBlankException() {
        super();
    }
}
