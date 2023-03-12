package hash.cracker.manager.services;

import hash.cracker.manager.types.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ManagerService {
    private final String workerAddr = System.getenv("WORKER_ADDR");
    private final int partCount = Integer.parseInt(System.getenv("PART_COUNT"));
    private final Duration taskTimeout = Duration.parse("PT" + System.getenv("TASK_TIMEOUT"));
    private final String alph = System.getenv("ALPHABET");

    private final ConcurrentHashMap<String, TaskStatus> taskStatuses = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();
    Logger logger = LoggerFactory.getLogger("manager");

    public ManagerService() {
        for (String charString : alph.split("")) {
            alphabet.getSymbols().add(charString);
        }
        logger.info("Alhabet is: " + alphabet.getSymbols());
    }

    public ResponseEntity<String> submitTask(Task task) {
        logger.info("New task: " + task.toString());
        String requestId = UUID.randomUUID().toString();

        CrackHashManagerRequest request = new CrackHashManagerRequest();

        taskStatuses.put(requestId, new TaskStatus());

        for (int i = 0; i < partCount; i++) {
            request.setAlphabet(alphabet);
            request.setRequestId(requestId);
            request.setHash(task.getHash());
            request.setMaxLength(task.getMaxLength());
            request.setPartCount(partCount);
            request.setPartNumber(i);

            restTemplate.postForObject(workerAddr + "/internal/api/worker/hash/crack/task", request, Void.class);
        }
        return ResponseEntity.ok(requestId);
    }

    public ResponseEntity<TaskStatus> getStatus(String requestId) {
       logger.info("Get status for " + requestId);
        TaskStatus status = taskStatuses.get(requestId);
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }

        Duration dur = Duration.between(status.getStartTime(), Instant.now());
        if (dur.toMillis() > taskTimeout.toMillis() && status.getData().isEmpty()) {
            status.setStatus(Status.ERROR);
            return ResponseEntity.ok(status);
        }

        return ResponseEntity.ok(status);
    }

    public void ReceiveAnswers(CrackHashWorkerResponse response) {
        if (response.getAnswers().getWords().isEmpty()) {
            return;
        }

        TaskStatus status = taskStatuses.get(response.getRequestId());
        if (status == null) {
            return;
        }

        logger.info("Received answer:" + response.getAnswers().getWords().toString() +
            " took: " + Duration.between(status.getStartTime(), Instant.now()).toMillis() / 1000 + "s");

        status.getData().addAll(response.getAnswers().getWords());
        status.setStatus(Status.READY);
    }
}
