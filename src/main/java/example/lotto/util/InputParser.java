package example.lotto.util;

import example.lotto.error.InputNotNumericException;
import example.lotto.error.InputNullOrBlankException;
import example.lotto.error.InputNumberOverflowException;

import java.math.BigInteger;

/**
 * 사용자 입력을 변환, 검증하는 유틸리티 클래스
 */
public final class InputParser {
    private InputParser() {}

    public static String refineInput(String input) {
        boolean isNullOrBlank = (input == null) || input.isBlank();
        if (isNullOrBlank) {
            throw new InputNullOrBlankException();
        }
        return input.trim();
    }

    public static int parseToInt(String input) {
        String refinedInput = refineInput(input);
        try {
            return Integer.parseInt(refinedInput);
        } catch (NumberFormatException e) {
            distinguishNumberFormatError(input);
            throw new InputNumberOverflowException();
        }
    }

    private static void distinguishNumberFormatError(String input) {
        try {
            new BigInteger(input);
        } catch (NumberFormatException e) {
            throw new InputNotNumericException();
        }
    }
}
