package example.lotto.domain;

import java.util.*;

/**
 * 로또 domain 클래스
 */
public class Lotto extends LotteryNumbers {
    public Lotto(List<Integer> numbers) {
        super(copyAndSort(numbers));
    }

    @Override
    public String toString() {
        return numbers.toString();
    }

    public int countMatchingWinningNumbers(WinningNumbers winningNumbers) {
        return winningNumbers.countMatchingNumbers(numbers);
    }

    public boolean hasMatchingBonusNumber(BonusNumber bonusNumber) {
        return bonusNumber.hasMatchingNumber(numbers);
    }

    private static List<Integer> copyAndSort(List<Integer> numbers) {
        List<Integer> copiedNumbers = new ArrayList<>(numbers);
        Collections.sort(copiedNumbers);
        return copiedNumbers;
    }
}
