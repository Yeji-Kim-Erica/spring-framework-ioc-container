package example.lotto.util;

import camp.nextstep.edu.missionutils.Randoms;

import java.util.List;

/**
 * 난수 목록을 생성하는 클래스
 */
public class RandomLottoNumberGenerator implements LottoNumberGenerator {
    private static final int START_RANGE_NUMBER = 1;
    private static final int END_RANGE_NUMBER = 45;
    private static final int SIZE = 6;

    @Override
    public List<Integer> generateUniqueNumbersInRange() {
        return Randoms.pickUniqueNumbersInRange(START_RANGE_NUMBER, END_RANGE_NUMBER, SIZE);
    }
}
