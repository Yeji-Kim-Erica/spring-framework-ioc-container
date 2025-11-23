package experiment.statics.lotto.error;

/**
 * 로또 조합의 크기가 유효하지 않은 경우 발생하는 예외 클래스
 */
public class InvalidLottoSizeException extends IllegalArgumentException {
    public InvalidLottoSizeException(String message) {
        super(message);
    }
}
