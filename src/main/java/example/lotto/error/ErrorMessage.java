package example.lotto.error;

/**
 * 오류 메시지를 정의한 enum 클래스
 */
public enum ErrorMessage {
    // DepositAmount Error
    DEPOSIT_AMOUNT_NULL_OR_BLANK("구입 금액은 공백이거나 비어 있을 수 없습니다."),
    DEPOSIT_AMOUNT_NOT_NUMERIC("구입 금액은 숫자여야 합니다."),
    DEPOSIT_AMOUNT_LESS_THAN_MINIMUM("구입 금액은 1,000원 이상이어야 합니다."),
    DEPOSIT_AMOUNT_OVER_MAXIMUM("최대 입금 가능 금액은 2,147,483,000원입니다."),
    DEPOSIT_AMOUNT_NOT_DIVISIBLE_BY_LOTTO_PRICE("구입 금액은 1,000원 단위여야 합니다."),

    // Lotto Error
    LOTTO_SIZE_INVALID("로또 번호는 6개여야 합니다."),
    LOTTO_NUMBER_OUT_OF_RANGE("로또 번호는 1부터 45 사이의 숫자여야 합니다."),
    LOTTO_NUMBER_DUPLICATED("로또 번호는 중복될 수 없습니다."),

    // WinningNumbers Error
    WINNING_NUMBERS_NULL_OR_BLANK("당첨 번호는 공백이거나 비어 있을 수 없습니다."),
    WINNING_NUMBERS_NOT_NUMERIC("당첨 번호는 숫자여야 합니다."),
    WINNING_NUMBERS_OUT_OF_RANGE("당첨 번호는 1부터 45 사이의 숫자여야 합니다."),
    WINNING_NUMBERS_SIZE_INVALID("당첨 번호는 6개여야 합니다."),
    WINNING_NUMBERS_DUPLICATED("당첨 번호는 중복될 수 없습니다."),

    // BonusNumber Error
    BONUS_NUMBER_NULL_OR_BLANK("보너스 번호는 공백이거나 비어 있을 수 없습니다."),
    BONUS_NUMBER_NOT_NUMERIC("보너스 번호는 숫자여야 합니다."),
    BONUS_NUMBER_OUT_OF_RANGE("보너스 번호는 1부터 45 사이의 숫자여야 합니다."),
    BONUS_NUMBER_DUPLICATED_WITH_WINNING_NUMBERS("보너스 번호와 당첨 번호는 중복될 수 없습니다."),

    // Prize Error
    PRIZE_MATCH_COUNT_OUT_OF_RANGE("유효한 매칭 개수가 아닙니다.");

    private static final String ERROR_PREFIX = "[ERROR] ";

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return ERROR_PREFIX + message;
    }
}
