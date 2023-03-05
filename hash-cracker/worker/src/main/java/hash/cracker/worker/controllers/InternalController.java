package hash.cracker.worker.controllers;

import hash.cracker.worker.services.WorkerService;
import hash.cracker.worker.types.CrackHashManagerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/worker/hash/crack/task")
public class InternalController {

    private final WorkerService service;

    @Autowired
    public InternalController(WorkerService service) {
        this.service = service;
    }

    @PostMapping
    public void ReceiveTask(@RequestBody CrackHashManagerRequest request) {
        service.ReceiveTask(request);
    }
}
