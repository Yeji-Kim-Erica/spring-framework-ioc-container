package experiment.statics.lotto.domain;

import experiment.statics.lotto.error.ErrorMessage;
import experiment.statics.lotto.error.InputNotNumericException;
import experiment.statics.lotto.error.InputNullOrBlankException;
import experiment.statics.lotto.error.InputNumberOverflowException;
import experiment.statics.lotto.util.InputParser;

/**
 * 구입금액 domain 클래스
 */
public class DepositAmount {
    private static final int DEPOSIT_UNIT = 1000;
    private static final int MAXIMUM_AMOUNT = Math.floorDiv(Integer.MAX_VALUE, DEPOSIT_UNIT) * DEPOSIT_UNIT;

    private final int amount;

    private DepositAmount(int amount) {
        validateDepositAmountRule(amount);
        this.amount = amount;
    }

    public static DepositAmount from(String depositAmount) {
        int parsedAmount = parseAndTranslateFormatErrors(depositAmount);
        return new DepositAmount(parsedAmount);
    }

    public int getNumberOfPurchasableLotto(int lottoPrice) {
        return amount / lottoPrice;
    }

    public double divideProfitByExpense(long totalWinningAmount) {
        return (double) totalWinningAmount / amount;
    }

    private void validateDepositAmountRule(int amount) {
        validateDepositExceedsMinimum(amount);
        validateDepositUnderMaximum(amount);
        validateDepositDivisibleByLottoPrice(amount);
    }

    private void validateDepositExceedsMinimum(int amount) {
        boolean isLessThanMinimum = amount < DEPOSIT_UNIT;
        if (isLessThanMinimum) {
            throw new IllegalArgumentException(ErrorMessage.DEPOSIT_AMOUNT_LESS_THAN_MINIMUM.getMessage());
        }
    }

    private void validateDepositUnderMaximum(int amount) {
        boolean isOverMaximum = amount > MAXIMUM_AMOUNT;
        if (isOverMaximum) {
            throw new IllegalArgumentException(ErrorMessage.DEPOSIT_AMOUNT_OVER_MAXIMUM.getMessage());
        }
    }

    private void validateDepositDivisibleByLottoPrice(int amount) {
        boolean isNotDivisibleByLottoPrice = (amount % DEPOSIT_UNIT != 0);
        if (isNotDivisibleByLottoPrice) {
            throw new IllegalArgumentException(ErrorMessage.DEPOSIT_AMOUNT_NOT_DIVISIBLE_BY_LOTTO_PRICE.getMessage());
        }
    }

    private static int parseAndTranslateFormatErrors(String input) {
        try {
            return InputParser.parseToInt(input);
        } catch (InputNullOrBlankException e) {
            throw new IllegalArgumentException(ErrorMessage.DEPOSIT_AMOUNT_NULL_OR_BLANK.getMessage());
        } catch (InputNotNumericException e) {
            throw new IllegalArgumentException(ErrorMessage.DEPOSIT_AMOUNT_NOT_NUMERIC.getMessage());
        } catch (InputNumberOverflowException e) {
            throw new IllegalArgumentException(ErrorMessage.DEPOSIT_AMOUNT_OVER_MAXIMUM.getMessage());
        }
    }
}
