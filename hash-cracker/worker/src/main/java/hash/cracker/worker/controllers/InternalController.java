package hash.cracker.worker.controllers;

import hash.cracker.worker.services.WorkerService;
import hash.cracker.worker.types.CrackHashManagerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {

    private final WorkerService service;

    @Autowired
    public InternalController(WorkerService service) {
        this.service = service;
    }

    @PostMapping("/api/worker/hash/crack/task")
    public void ReceiveTask(@RequestBody CrackHashManagerRequest request) {
        service.ReceiveTask(request);
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthcheck() {
        return ResponseEntity.ok("OK");
    }
}
