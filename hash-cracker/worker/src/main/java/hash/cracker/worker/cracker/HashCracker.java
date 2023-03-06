package hash.cracker.worker.cracker;

import hash.cracker.worker.types.CrackHashManagerRequest;
import hash.cracker.worker.types.CrackHashWorkerResponse;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.util.Arrays;
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
            CrackHashWorkerResponse response =  new CrackHashWorkerResponse();
            response.setRequestId(task.getRequestId());
            response.setAnswers(new CrackHashWorkerResponse.Answers());
            try {
                String md5Hash = task.getHash();
                int partNumber = task.getPartNumber();
                int partCount = task.getPartCount();
                List<String> alphabet = task.getAlphabet().getSymbols();
                int maxLength = task.getMaxLength();

                response.getAnswers().getWords().add("answer");

                System.out.println("Thread finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        });
    }

    public static void crack(String[] args) {
        ICombinatoricsVector<String> vector = createVector("abcd1".split(""));
        for (int i = 0; i < 4; i++) {
            System.out.println("\niter: " + i);
            Generator<String> gen = createPermutationWithRepetitionGenerator(vector, i);
            for (ICombinatoricsVector<String> perm : gen) {
                System.out.println(perm);
            }
            System.out.println("Total size: " + gen.getNumberOfGeneratedObjects());

            int parts = 4;
            int partNum = 1;
            long totalObjects = gen.getNumberOfGeneratedObjects();

            long[] ranges = splitNumber(totalObjects, parts);
            System.out.println(Arrays.toString(splitNumber(totalObjects, parts)));

            long start = ranges[partNum] + 1;
            long stop = ranges[partNum + 1];

            System.out.println("Start: " + start);
            System.out.println("Stop: " + stop);

            List<ICombinatoricsVector<String>> vec = gen.generateObjectsRange(start, stop);

            System.out.println("Part size: " + vec.size());
            for (ICombinatoricsVector<String> subSet : vec) {
                System.out.println(subSet);
            }

            System.out.println("==========");
        }
    }

    public static long[] splitNumber(long number, int numRanges) {
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
