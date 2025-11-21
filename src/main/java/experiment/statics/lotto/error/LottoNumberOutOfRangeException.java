package experiment.statics.lotto.error;

/**
 * 로또 번호가 범위를 벗어난 경우 발생하는 예외 클래스
 */
public class LottoNumberOutOfRangeException extends IllegalArgumentException {
    public LottoNumberOutOfRangeException(String message) {
        super(message);
    }
}
