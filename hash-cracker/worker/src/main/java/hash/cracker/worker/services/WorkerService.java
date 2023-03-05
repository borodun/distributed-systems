package hash.cracker.worker.services;

import hash.cracker.worker.types.CrackHashManagerRequest;
import hash.cracker.worker.types.CrackHashWorkerResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@EnableScheduling
public class WorkerService {

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    private final String managerUrl = "http://localhost:8080";

    private String id;

    public void ReceiveTask(CrackHashManagerRequest request) {
        System.out.println("Receive request: " + request.getAlphabet().getSymbols());
        id = request.getRequestId();
    }

    @Scheduled(initialDelay = 5000, fixedRate = 5000)
    public void SendAnswers() {
        if (id == null) {
            System.out.println("Id is null");
            return;
        }

        CrackHashWorkerResponse response = new CrackHashWorkerResponse();
        response.setRequestId(id);
        response.setAnswers(new CrackHashWorkerResponse.Answers());
        response.getAnswers().getWords().add("answer");

        restTemplate.patchForObject(managerUrl + "/internal/api/manager/hash/crack/request", response, Void.class);
    }
}
