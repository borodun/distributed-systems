package hash.cracker.worker.cracker;

import hash.cracker.worker.types.CrackHashManagerRequest;
import hash.cracker.worker.types.CrackHashWorkerResponse;
import org.paukov.combinatorics3.Generator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;

public class HashCracker {
    private BlockingQueue<Runnable> taskQueue;
    private ThreadPoolExecutor threadPool;

    public HashCracker(int numThreads, int queueSize) {
        taskQueue = new LinkedBlockingQueue<>(queueSize);
        threadPool = new ThreadPoolExecutor(numThreads / 2, numThreads, 1, TimeUnit.SECONDS, taskQueue);
    }

    public Future<CrackHashWorkerResponse> addTask(CrackHashManagerRequest task) {
        return threadPool.submit(() -> {
            System.out.println("Task " + task.getPartNumber() + " started");

            CrackHashWorkerResponse response = new CrackHashWorkerResponse();
            response.setRequestId(task.getRequestId());
            response.setAnswers(new CrackHashWorkerResponse.Answers());

            List<String> alphabet = task.getAlphabet().getSymbols();
            int partNumber = task.getPartNumber();
            int partCount = task.getPartCount();
            byte[] hashBytes = DatatypeConverter.parseHexBinary(task.getHash());

            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch(Exception e) {
                e.printStackTrace();
                return response;
            }

            Instant timeStart = Instant.now();
            long count = 0;
            int interval = 5;
            for (int i = 0; i <= task.getMaxLength(); i++) {
                double totalCount = Math.pow(alphabet.size(), i) / partCount;
                System.out.println("Total number of objects: " + totalCount);
                Iterator<List<String>> iter = Generator.permutation(alphabet)
                    .withRepetitions(i)
                    .iterator();

                long step = 0;
                while(iter.hasNext()) {
                    long duration = Duration.between(timeStart, Instant.now()).toMillis() / 1000;
                    if (duration > interval) {
                        System.out.println("Cracking speed of part " + partNumber + ", iter " + i + ": " + (count / interval) + " (" + (step / totalCount) + "%)");
                        timeStart = Instant.now();
                        count = 0;
                    }

                    List<String> el = iter.next();
                    if (step % partCount == partNumber) {
                        String str = String.join("", el);
                        byte[] md5str = md5.digest(str.getBytes());
                        if (Arrays.equals(md5str, hashBytes)) {
                            System.out.println("Found hash: " + DatatypeConverter.printHexBinary(md5str) + "=" + str + ", part " + partNumber);
                            response.getAnswers().getWords().add(str);
                        }
                        count++;
                    }
                    step++;
                }
            }

            System.out.println("Task " + task.getPartNumber() + " finished");

            return response;
        });
    }
}
