package hash.cracker.worker.services;

import hash.cracker.worker.types.CrackHashManagerRequest;
import hash.cracker.worker.types.CrackHashWorkerResponse;
import jakarta.xml.bind.DatatypeConverter;

import org.paukov.combinatorics3.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

@Service
public class WorkerService {
    private final String managerAddr = System.getenv("MANAGER_ADDR");
    private final int threadCount = Integer.parseInt(System.getenv("THREAD_COUNT"));
    private final Duration taskTimeout = Duration.parse("PT" + System.getenv("TASK_TIMEOUT"));

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(50);;
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(threadCount / 2, threadCount, 1, TimeUnit.SECONDS, this.taskQueue);
    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    Logger logger = LoggerFactory.getLogger("worker");

    public void ReceiveTask(CrackHashManagerRequest part) {
        threadPool.submit(() -> {
            List<String> alphabet = part.getAlphabet().getSymbols();
            int partNumber = part.getPartNumber();
            int partCount = part.getPartCount();
            byte[] hashBytes = DatatypeConverter.parseHexBinary(part.getHash());

            MessageDigest hasher = null;
            try {
                hasher = MessageDigest.getInstance("MD5");
            } catch(Exception e) {
                e.printStackTrace();
            }

            logger.info("Part " + partNumber + " started");
            Instant startTime = Instant.now();
            for (int i = 0; i <= part.getMaxLength(); i++) {
                Iterator<List<String>> iter = Generator.permutation(alphabet).withRepetitions(i).iterator();

                long step = 0;
                while(iter.hasNext()) {
                    List<String> el = iter.next();
                    if (step % partCount == partNumber) {
                        String str = String.join("", el);
                        byte[] md5 = hasher.digest(str.getBytes());
                        if (Arrays.equals(md5, hashBytes)) {
                            logger.info("Found hash: " + DatatypeConverter.printHexBinary(md5).toLowerCase() + "=" + str + ", part " + partNumber);
                            sendAnswer(part.getRequestId(), str);
                        }

                        Duration dur = Duration.between(startTime, Instant.now());
                        if (dur.toMillis() > taskTimeout.toMillis()) {
                            logger.warn("Part " + partNumber + " exceeded time limit (" + System.getenv("TASK_TIMEOUT") + "): exiting");
                            return;
                        }
                    }
                    step++;
                }
            }

            logger.info("Part " + partNumber + " finished");
        });
    }

    private void sendAnswer(String id, String answer) {
        CrackHashWorkerResponse response = new CrackHashWorkerResponse();
        response.setRequestId(id);
        response.setAnswers(new CrackHashWorkerResponse.Answers());
        response.getAnswers().getWords().add(answer);

        restTemplate.patchForObject(managerAddr + "/internal/api/manager/hash/crack/request", response, Void.class);
    }
}
