package example.lotto.domain;

import example.lotto.error.*;
import example.lotto.util.InputParser;

import java.util.List;

/**
 * 보너스 번호 domain 클래스
 */
public class BonusNumber {
    private static final int MINIMUM = 1;
    private static final int MAXIMUM = 45;

    private final int number;

    private BonusNumber(int number, WinningNumbers winningNumbers) {
        validateBonusNumberRule(number, winningNumbers);
        this.number = number;
    }

    public static BonusNumber of(String number, WinningNumbers winningNumbers) {
        int parsedNumber = parseAndTranslateFormatErrors(number);
        return new BonusNumber(parsedNumber, winningNumbers);
    }

    public boolean hasMatchingNumber(List<Integer> lottoNumbers) {
        return lottoNumbers.contains(number);
    }

    private static int parseAndTranslateFormatErrors(String input) {
        try {
            return InputParser.parseToInt(input);
        } catch (InputNullOrBlankException e) {
            throw new IllegalArgumentException(ErrorMessage.BONUS_NUMBER_NULL_OR_BLANK.getMessage());
        } catch (InputNotNumericException e) {
            throw new IllegalArgumentException(ErrorMessage.BONUS_NUMBER_NOT_NUMERIC.getMessage());
        } catch (InputNumberOverflowException e) {
            throw new IllegalArgumentException(ErrorMessage.BONUS_NUMBER_OUT_OF_RANGE.getMessage());
        }
    }

    private void validateBonusNumberRule(int number, WinningNumbers winningNumbers) {
        validateRangeAndTranslateDomainError(number);
        validateDuplication(number, winningNumbers);
    };

    private void validateRangeAndTranslateDomainError(int number) {
        try {
            LotteryNumbers.validateLottoNumberRange(number);
        } catch (LottoNumberOutOfRangeException e) {
            throw new IllegalArgumentException(ErrorMessage.BONUS_NUMBER_OUT_OF_RANGE.getMessage());
        }
    }

    private void validateDuplication(int number, WinningNumbers winningNumbers) {
        boolean isDuplicatedWithWinningNumbers = winningNumbers.contains(number);
        if (isDuplicatedWithWinningNumbers) {
            throw new IllegalArgumentException(ErrorMessage.BONUS_NUMBER_DUPLICATED_WITH_WINNING_NUMBERS.getMessage());
        }
    }
}
