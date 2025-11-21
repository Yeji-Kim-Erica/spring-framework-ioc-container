package experiment.statics.lotto.error;

/**
 * 로또 번호가 중복되는 경우 발생하는 예외 클래스
 */
public class DuplicateLottoNumberException extends IllegalArgumentException {
    public DuplicateLottoNumberException(String message) {
        super(message);
    }
}
