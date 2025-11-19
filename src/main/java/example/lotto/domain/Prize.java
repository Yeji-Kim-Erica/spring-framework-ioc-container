package example.lotto.domain;

import example.lotto.error.ErrorMessage;

/**
 * 로또 당첨 결과를 정의하는 enum 클래스
 */
public enum Prize {
    NONE(0, false, 0),
    FIFTH_PRIZE(3, false, 5000),
    FOURTH_PRIZE(4, false, 50000),
    THIRD_PRIZE(5, false, 1500000),
    SECOND_PRIZE(5, true, 30000000),
    FIRST_PRIZE(6, false, 2000000000);

    private static final int MINIMUM_MATCH_COUNT = 0;
    private static final int MAXIMUM_MATCH_COUNT = 6;

    private final int winningNumbersMatchCount;
    private final boolean hasMatchingBonusNumber;
    private final int winningsAmount;

    Prize(int winningNumbersMatchCount, boolean hasMatchingBonusNumber, int winningsAmount) {
        this.winningNumbersMatchCount = winningNumbersMatchCount;
        this.hasMatchingBonusNumber = hasMatchingBonusNumber;
        this.winningsAmount = winningsAmount;
    }

    public static Prize of(int winningNumbersMatchCount, boolean hasMatchingBonusNumber) {
        validateRange(winningNumbersMatchCount);
        if (isPrizeAvailable(winningNumbersMatchCount)) {
            return findPrizeByMatchCount(winningNumbersMatchCount, hasMatchingBonusNumber);
        }
        return NONE;
    }

    public int getWinningNumbersMatchCount() {
        return winningNumbersMatchCount;
    }

    public boolean isMatchingBonusNumber() {
        return hasMatchingBonusNumber;
    }

    public int getWinningsAmount() {
        return winningsAmount;
    }

    private static void validateRange(int winningNumbersMatchCount) {
        boolean isLessThanMinimum = winningNumbersMatchCount < MINIMUM_MATCH_COUNT;
        boolean isOverMaximum = winningNumbersMatchCount > MAXIMUM_MATCH_COUNT;

        boolean isOutOfRange = isLessThanMinimum || isOverMaximum;
        if (isOutOfRange) {
            throw new IllegalArgumentException(ErrorMessage.PRIZE_MATCH_COUNT_OUT_OF_RANGE.getMessage());
        }
    }

    private static boolean isPrizeAvailable(int winningNumbersMatchCount) {
        boolean hasMinimumMatchCount = winningNumbersMatchCount >= FIFTH_PRIZE.getWinningNumbersMatchCount();
        boolean isUnderMaximumMatchCount = winningNumbersMatchCount <= FIRST_PRIZE.getWinningNumbersMatchCount();
        return hasMinimumMatchCount && isUnderMaximumMatchCount;
    }

    private static Prize findPrizeByMatchCount(int winningNumbersMatchCount, boolean hasMatchingBonusNumber) {
        if (winningNumbersMatchCount == FIRST_PRIZE.winningNumbersMatchCount) {
            return FIRST_PRIZE;
        }

        if (winningNumbersMatchCount == FOURTH_PRIZE.winningNumbersMatchCount) {
            return FOURTH_PRIZE;
        }

        if (winningNumbersMatchCount == FIFTH_PRIZE.winningNumbersMatchCount) {
            return FIFTH_PRIZE;
        }

        return findSecondOrThirdPrize(hasMatchingBonusNumber);
    }

    private static Prize findSecondOrThirdPrize(boolean hasMatchingBonusNumber) {
        if (hasMatchingBonusNumber == SECOND_PRIZE.hasMatchingBonusNumber) {
            return SECOND_PRIZE;
        }

        return THIRD_PRIZE;
    }
}
