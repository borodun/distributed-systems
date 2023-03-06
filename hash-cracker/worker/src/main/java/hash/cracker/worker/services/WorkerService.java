package hash.cracker.worker.services;

import hash.cracker.worker.cracker.HashCracker;
import hash.cracker.worker.types.CrackHashManagerRequest;
import hash.cracker.worker.types.CrackHashWorkerResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Service
@EnableScheduling
public class WorkerService {
    private final ConcurrentHashMap<String, Future<CrackHashWorkerResponse>> tasks = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    private final String managerUrl = "http://localhost:8080";
    HashCracker cracker = new HashCracker(1, 1);

    public void ReceiveTask(CrackHashManagerRequest request) {
        System.out.println("Receive request: " + request.getAlphabet().getSymbols());
        Future<CrackHashWorkerResponse> future = cracker.addTask(request);
        tasks.put(request.getRequestId(), future);
    }

    @Scheduled(initialDelay = 5000, fixedRate = 5000)
    public void SendAnswers() {
        for (Map.Entry<String, Future<CrackHashWorkerResponse>> entry : tasks.entrySet()) {
            String requestId = entry.getKey();
            Future<CrackHashWorkerResponse> future = entry.getValue();

            if (!future.isDone()) {
                System.out.println("Task " + requestId + " is in progress");
                continue;
            } else {
                System.out.println("Task " + requestId + " is done");
            }

            CrackHashWorkerResponse response;
            try {
                response = future.get();
                restTemplate.patchForObject(managerUrl + "/internal/api/manager/hash/crack/request", response, Void.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            tasks.remove(requestId);
        }
    }
}
