package experiment.statics.lotto.controller;

import camp.nextstep.edu.missionutils.Console;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrencyTest {
    private static final List<String> inputs = List.of(
            "1000\n1,2,3,4,5,6\n7",
            "2000\n1,2,3,4,5,6\n7",
            "3000\n1,2,3,4,5,6\n7",
            "4000\n1,2,3,4,5,6\n7",
            "5000\n1,2,3,4,5,6\n7",
            "6000\n1,2,3,4,5,6\n7"
    );
    private static final List<Integer> expected = List.of(1, 2, 3, 4, 5, 6);

    @AfterEach
    public void restoreStreams() {
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    @DisplayName("Static 방식: 동기화되지 않은 채로 100명이 동시에 로또 게임을 하면 100% 실행 오류가 발생한다 (데이터 무결성 검사까지 가기 전에 터진다)")
    void static_MultiThread_Explosion() {
        // given
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            int finalJ = i % 6;
            futures.add(CompletableFuture.supplyAsync(() -> {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                System.setOut(new PrintStream(output));

                System.setIn(new ByteArrayInputStream(inputs.get(finalJ).getBytes()));

                 try {
                    LottoController.run();

                    String result = output.toString();
                    if (!result.contains(String.format("%d개를 구매했습니다.", expected.get(finalJ)))) {
                        return "데이터 오염";
                    }
                    return "성공";
                } catch (Exception e) {
                    return "실행 오류";
                }
            }, executor));
        }

        // then
        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        int successCount, runErrorCount, dataCorruptionCount;
        successCount = 0;
        runErrorCount = 0;
        dataCorruptionCount = 0;
        for (String result : results) {
            if (result.equals("성공")) {
                successCount++;
            }
            if (result.equals("실행 오류")) {
                runErrorCount++;
            }
            if (result.equals("데이터 오염")) {
                dataCorruptionCount++;
            }
        }

        assertThat(successCount).isEqualTo(0);
        assertThat(runErrorCount).isEqualTo(threadCount);
        assertThat(dataCorruptionCount).isEqualTo(0);
    }

    @Test
    @DisplayName("Static 방식: 동기화된 채로 100명이 동시에 로또 게임을 하면 걸리는 시간을 측정한다")
    void static_MultiThread_Synchronized_TimeTaken() {
        // given
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // when
        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            int finalJ = i % 6;
            futures.add(CompletableFuture.runAsync(() -> {
                synchronized (SynchronizedLottoController.class) {
                    Console.close();
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    System.setOut(new PrintStream(output));
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException();
                    }
                    System.setIn(new ByteArrayInputStream(inputs.get(finalJ).getBytes()));

                    SynchronizedLottoController.run();

                    String result = output.toString();
                    assertThat(result).contains(String.format("%d개를 구매했습니다.", expected.get(finalJ)));
                }
            }, executor));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long end = System.currentTimeMillis();

        // then
        double timeTaken = (end - start) / 1000.0;
        System.err.printf("===== Static Synchronized 총 소요 시간: %.3f초 =====", timeTaken);
    }

    @Test
    @DisplayName("Static 방식: 동기화된 채로 로또 게임을 하면 데이터 무결성은 지켜지지만, 싱글 스레드를 사용하는 것보다 시간 비용이 더 많이 든다")
    void static_MultiThread_Synchronized_Data_Safe_But_Too_Slow() {
        // given
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // when
        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            int finalJ = i % 6;
            futures.add(CompletableFuture.runAsync(() -> {
                synchronized (SynchronizedLottoController.class) {
                    Console.close();
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    System.setOut(new PrintStream(output));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException();
                    }
                    System.setIn(new ByteArrayInputStream(inputs.get(finalJ).getBytes()));

                    SynchronizedLottoController.run();

                    String result = output.toString();
                    assertThat(result).contains(String.format("%d개를 구매했습니다.", expected.get(finalJ)));
                }
            }, executor));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long end = System.currentTimeMillis();
        long timeTakenOnMultiThread = end - start;

        start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            int finalJ = i % 6;

            Console.close();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            System.setIn(new ByteArrayInputStream(inputs.get(finalJ).getBytes()));

            LottoController.run();

            String result = output.toString();
            assertThat(result).contains(String.format("%d개를 구매했습니다.", expected.get(finalJ)));
        }

        end = System.currentTimeMillis();
        long timeTakenOnSingleThread = end - start;

        // then
        assertThat(timeTakenOnMultiThread).isGreaterThan(timeTakenOnSingleThread);
    }
}
