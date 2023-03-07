package hash.cracker.manager.services;

import hash.cracker.manager.types.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ManagerService {
    private final ConcurrentHashMap<String, RequestStatus> requestStatuses = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final String workerUrl = System.getenv("WORKER_ADDR");
    private final int partCount = Integer.parseInt(System.getenv("TASK_COUNT"));
    private final String alph = System.getenv("ALPHABET");
    private final CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();

    public ManagerService() {
        for (String charString : alph.split("")) {
            alphabet.getSymbols().add(charString);
        }
    }

    public ResponseEntity<String> submitTask(Task task) {
        System.out.println("Submit task: " + task.toString());
        String requestId = UUID.randomUUID().toString();

        CrackHashManagerRequest request = new CrackHashManagerRequest();

        requestStatuses.put(requestId, new RequestStatus());

        for (int i = 0; i < partCount; i++) {
            request.setAlphabet(alphabet);
            request.setRequestId(requestId);
            request.setHash(task.getHash());
            request.setMaxLength(task.getMaxLength());
            request.setPartCount(partCount);
            request.setPartNumber(i);

            restTemplate.postForObject(workerUrl + "/internal/api/worker/hash/crack/task", request, Void.class);
        }
        return ResponseEntity.ok(requestId);
    }

    public ResponseEntity<Response> getStatus(String requestId) {
        System.out.println("Get status");
        RequestStatus status = requestStatuses.get(requestId);
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new Response(status.getStatus(), status.getData()));
    }

    public void ReceiveAnswers(CrackHashWorkerResponse response) {
        if (response.getAnswers().getWords().isEmpty()) {
            return;
        }

        System.out.println("Receive answers:" + response.getAnswers().getWords().toString());
        requestStatuses.get(response.getRequestId()).getData().addAll(response.getAnswers().getWords());
        requestStatuses.get(response.getRequestId()).setStatus(Status.READY);
    }
}
