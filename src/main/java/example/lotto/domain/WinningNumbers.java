package example.lotto.domain;

import example.lotto.error.*;
import example.lotto.util.InputParser;

import java.util.Arrays;
import java.util.List;

/**
 * 당첨 번호 domain 클래스
 */
public class WinningNumbers extends LotteryNumbers {
    private static final String DELIMITER = ",";

    private WinningNumbers(List<Integer> numbers) {
        super(numbers);
    }

    public static WinningNumbers from(String winningNumbers) {
        List<Integer> parsedNumbers = parseAndTranslateFormatErrors(winningNumbers);
        return validateOrThrow(parsedNumbers);
    }

    public boolean contains(int number) {
        return numbers.contains(number);
    }

    public int countMatchingNumbers(List<Integer> lottoNumbers) {
        return (int) numbers.stream()
                .filter(lottoNumbers::contains)
                .count();
    }

    private static List<Integer> parseAndTranslateFormatErrors(String input) {
        try {
            List<String> numbersString = split(input);
            return convertToIntegers(numbersString);
        } catch (InputNullOrBlankException e) {
            throw new IllegalArgumentException(ErrorMessage.WINNING_NUMBERS_NULL_OR_BLANK.getMessage());
        } catch (InputNotNumericException e) {
            throw new IllegalArgumentException(ErrorMessage.WINNING_NUMBERS_NOT_NUMERIC.getMessage());
        } catch (InputNumberOverflowException e) {
            throw new IllegalArgumentException(ErrorMessage.WINNING_NUMBERS_OUT_OF_RANGE.getMessage());
        }
    }

    private static List<String> split(String input) {
        String refinedInput = InputParser.refineInput(input);
        return Arrays.stream(refinedInput.split(DELIMITER)).toList();
    }

    private static List<Integer> convertToIntegers(List<String> inputStrings) {
        return inputStrings.stream()
                .map(InputParser::parseToInt)
                .toList();
    }

    private static WinningNumbers validateOrThrow(List<Integer> winningNumbers) {
        try {
            return new WinningNumbers(winningNumbers);
        } catch (InvalidLottoSizeException e) {
            throw new IllegalArgumentException(ErrorMessage.WINNING_NUMBERS_SIZE_INVALID.getMessage());
        } catch (LottoNumberOutOfRangeException e) {
            throw new IllegalArgumentException(ErrorMessage.WINNING_NUMBERS_OUT_OF_RANGE.getMessage());
        } catch (DuplicateLottoNumberException e) {
            throw new IllegalArgumentException(ErrorMessage.WINNING_NUMBERS_DUPLICATED.getMessage());
        }
    }

}
