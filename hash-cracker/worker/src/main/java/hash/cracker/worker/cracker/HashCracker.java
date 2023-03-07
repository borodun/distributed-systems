package hash.cracker.worker.cracker;

import hash.cracker.worker.types.CrackHashManagerRequest;
import hash.cracker.worker.types.CrackHashWorkerResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.util.List;
import java.util.concurrent.*;

import static org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator;
import static org.paukov.combinatorics.CombinatoricsFactory.createVector;

public class HashCracker {
    private BlockingQueue<Runnable> taskQueue;
    private ThreadPoolExecutor threadPool;

    public HashCracker(int numThreads, int queueSize) {
        taskQueue = new LinkedBlockingQueue<>(queueSize);
        threadPool = new ThreadPoolExecutor(1, numThreads, 0L, TimeUnit.MILLISECONDS, taskQueue);
    }

    public Future<CrackHashWorkerResponse> addTask(CrackHashManagerRequest task) {
        return threadPool.submit(() -> {
            CrackHashWorkerResponse response = new CrackHashWorkerResponse();
            response.setRequestId(task.getRequestId());

            int partNumber = task.getPartNumber();

            ICombinatoricsVector<String> vector = createVector(task.getAlphabet().getSymbols());

            for (int i = 1; i <= task.getMaxLength(); i++) {
                Generator<String> gen = createPermutationWithRepetitionGenerator(vector, i);

                long totalObjects = gen.getNumberOfGeneratedObjects();

                long[] ranges = splitNumber(totalObjects, task.getPartCount());

                long start = ranges[partNumber] + 1;
                long stop = ranges[partNumber + 1];

                List<ICombinatoricsVector<String>> vec = gen.generateObjectsRange(start, stop);

                for (ICombinatoricsVector<String> perm : vec) {
                    String str = String.join("", perm.getVector());

                    String md5str = DigestUtils.md5Hex(str);
                    if (md5str.equals(task.getHash())) {
                        System.out.println("Found hash: " + md5str + "=" + str);
                        response.setAnswers(new CrackHashWorkerResponse.Answers());
                        response.getAnswers().getWords().add(str);
                    }
                }
            }

            return response;
        });
    }

    private static long[] splitNumber(long number, int numRanges) {
        long[] ranges = new long[numRanges + 1];
        long rangeSize = number / numRanges;
        long remainder = number % numRanges;
        int currentRange = 0;
        long rangeEnd = 0;

        for (int i = 0; i < numRanges + 1; i++) {
            long rangeStart = rangeEnd;
            rangeEnd = rangeStart + rangeSize;
            if (currentRange < remainder) {
                rangeEnd++;
                currentRange++;
            }
            ranges[i] = rangeStart;
        }

        return ranges;
    }
}
