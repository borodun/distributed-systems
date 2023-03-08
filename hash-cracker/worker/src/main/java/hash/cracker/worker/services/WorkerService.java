package hash.cracker.worker.services;

import hash.cracker.worker.cracker.HashCracker;
import hash.cracker.worker.types.CrackHashManagerRequest;
import hash.cracker.worker.types.CrackHashWorkerResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Service
@EnableScheduling
public class WorkerService {
    private final ConcurrentHashMap<LocalTime, Future<CrackHashWorkerResponse>> tasks = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    private final String managerUrl = System.getenv("MANAGER_ADDR");
    private final int threadCount = Integer.parseInt(System.getenv("THREAD_COUNT"));
    HashCracker cracker = new HashCracker(threadCount, 50);

    public void ReceiveTask(CrackHashManagerRequest request) {
        Future<CrackHashWorkerResponse> future = cracker.addTask(request);
        LocalTime timeStart = LocalTime.now();
        tasks.put(timeStart, future);
    }

    @Scheduled(fixedRate = 1000)
    public void SendAnswers() {
        for (Map.Entry<LocalTime, Future<CrackHashWorkerResponse>> entry : tasks.entrySet()) {
            LocalTime startTime = entry.getKey();
            Future<CrackHashWorkerResponse> future = entry.getValue();

            if (!future.isDone()) {
                continue;
            }

            CrackHashWorkerResponse response;
            try {
                response = future.get();
                if (response.getAnswers() == null) {
                    continue;
                }
                restTemplate.patchForObject(managerUrl + "/internal/api/manager/hash/crack/request", response, Void.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            tasks.remove(startTime);
        }
    }
}
