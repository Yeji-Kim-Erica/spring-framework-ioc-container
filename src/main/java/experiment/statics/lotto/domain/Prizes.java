package experiment.statics.lotto.domain;

import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * 로또 당첨 결과 목록을 관리하는 일급 컬렉션 클래스
 */
public class Prizes {
    private final EnumMap<Prize, Integer> prizesCount;

    private Prizes(EnumMap<Prize, Integer> prizesCount) {
        this.prizesCount = prizesCount;
    }

    public static Prizes from(List<Prize> prizes) {
        EnumMap<Prize, Integer> prizesCount = new EnumMap<>(Prize.class);
        for (Prize prize : prizes) {
            prizesCount.merge(prize, 1, Integer::sum);
        }
        return new Prizes(prizesCount);
    }

    public int getPrizesCount(Prize prize) {
        return prizesCount.getOrDefault(prize, 0);
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
}
