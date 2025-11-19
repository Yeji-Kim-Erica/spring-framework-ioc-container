package example.lotto.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 로또 당첨 결과 목록을 관리하는 일급 컬렉션 클래스
 */
public class Prizes {
    private final Map<Prize, Integer> prizesCount;

    private Prizes(Map<Prize, Integer> prizesCount) {
        this.prizesCount = prizesCount;
    }

    public static Prizes of(Lottos lottos, WinningNumbers winningNumbers, BonusNumber bonusNumber) {
        Map<Prize, Integer> prizes = initializePrizes();
        for (Lotto lotto : lottos.getLottos()) {
            int winningNumbersMatchCount = lotto.countMatchingWinningNumbers(winningNumbers);
            boolean hasMatchingBonusNumber = lotto.hasMatchingBonusNumber(bonusNumber);
            Prize prize = Prize.of(winningNumbersMatchCount, hasMatchingBonusNumber);
            prizes.put(prize, prizes.get(prize) + 1);
        }
        return new Prizes(prizes);
    }

    public Set<Entry<Prize, Integer>> getPrizesCountEntries() {
        return Collections.unmodifiableSet(prizesCount.entrySet());
    }

    public long calculateTotalWinningAmount() {
        long sum = 0;
        for (Entry<Prize, Integer> entry : prizesCount.entrySet()) {
            Prize prize = entry.getKey();
            int count = entry.getValue();
            sum += (long) prize.getWinningsAmount() * count;
        }
        return sum;
    }

    private static Map<Prize, Integer> initializePrizes() {
        Map<Prize, Integer> map = new LinkedHashMap<>();
        for (Prize prize : Prize.values()) {
            map.put(prize, 0);
        }
        return map;
    }
}
