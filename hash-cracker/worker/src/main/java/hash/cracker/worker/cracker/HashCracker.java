package hash.cracker.worker.cracker;

import hash.cracker.worker.types.CrackHashManagerRequest;
import hash.cracker.worker.types.CrackHashWorkerResponse;
import jakarta.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.util.Strings;
import org.apache.commons.codec.digest.DigestUtils;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.paukov.combinatorics.permutations.PermutationGenerator;
import org.paukov.combinatorics.util.ComplexCombinationGenerator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

            String md5Hash = task.getHash();
            int partNumber = task.getPartNumber();
            int partCount = task.getPartCount();
            List<String> alphabet = task.getAlphabet().getSymbols();
            int maxLength = task.getMaxLength();

            ICombinatoricsVector<String> vector = createVector(alphabet);
            for (int i = 1; i <= maxLength; i++) {
                System.out.println("\niter: " + i);
                Generator<String> gen = createPermutationWithRepetitionGenerator(vector, i);

                System.out.println("Total size: " + gen.getNumberOfGeneratedObjects());

                long totalObjects = gen.getNumberOfGeneratedObjects();

                long[] ranges = splitNumber(totalObjects, partCount);
                System.out.println(Arrays.toString(ranges));

                long start = ranges[partNumber] + 1;
                long stop = ranges[partNumber + 1];

                System.out.println("Start: " + start);
                System.out.println("Stop: " + stop);

                List<ICombinatoricsVector<String>> vec = gen.generateObjectsRange(start, stop);

                System.out.println("Part size: " + vec.size());
                for (ICombinatoricsVector<String> perm : vec) {
                    String str = String.join("", perm.getVector());

                    if (str.equals("abcd")) {
                        System.out.println("Found abcd: " + DigestUtils.md5Hex(str));
                    }

                    String md5str = DigestUtils.md5Hex(str);
                    if (md5str.equals(md5Hash)) {
                        System.out.println(str);
                        response.getAnswers().getWords().add(str);
                    }
                }
            }

            System.out.println("Thread "+task.getRequestId()+" finished");

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
