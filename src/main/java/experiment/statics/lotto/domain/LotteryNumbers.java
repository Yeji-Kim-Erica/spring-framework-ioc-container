package experiment.statics.lotto.domain;

import experiment.statics.lotto.error.DuplicateLottoNumberException;
import experiment.statics.lotto.error.ErrorMessage;
import experiment.statics.lotto.error.InvalidLottoSizeException;
import experiment.statics.lotto.error.LottoNumberOutOfRangeException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 로또 번호 조합의 공통 규칙을 정의하는 abstract 클래스
 */
public abstract class LotteryNumbers {
    private static final int VALID_SIZE = 6;
    private static final int NUMBER_START_RANGE = 1;
    private static final int NUMBER_END_RANGE = 45;

    protected final List<Integer> numbers;

    protected LotteryNumbers(List<Integer> numbers) {
        validateLotteryNumbersRule(numbers);
        this.numbers = numbers;
    }

    public static void validateLottoNumberRange(int number) {
        boolean isOutOfRange = (number < NUMBER_START_RANGE) || (number > NUMBER_END_RANGE);
        if (isOutOfRange) {
            throw new LottoNumberOutOfRangeException(ErrorMessage.LOTTO_NUMBER_OUT_OF_RANGE.getMessage());
        }
    }

    private void validateLotteryNumbersRule(List<Integer> numbers) {
        validateSizeOfLottoGroup(numbers);
        validateLotteryNumbers(numbers);
    }

    private void validateSizeOfLottoGroup(List<Integer> numbers) {
        boolean isInconsistentWithValidSize = (numbers.size() != VALID_SIZE);
        if (isInconsistentWithValidSize) {
            throw new InvalidLottoSizeException(ErrorMessage.LOTTO_SIZE_INVALID.getMessage());
        }
    }

    private void validateLotteryNumbers(List<Integer> numbers) {
        Set<Integer> uniqueNumbers = new HashSet<>();
        for (int number : numbers) {
            validateLottoNumberRange(number);
            validateDuplication(number, uniqueNumbers);
        }
    }

    private void validateDuplication(int number, Set<Integer> uniqueNumbers) {
        boolean hasDuplicateNumber = !uniqueNumbers.add(number);
        if (hasDuplicateNumber) {
            throw new DuplicateLottoNumberException(ErrorMessage.LOTTO_NUMBER_DUPLICATED.getMessage());
        }
    }
}
