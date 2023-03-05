package hash.cracker.manager.controllers;

import hash.cracker.manager.services.ManagerService;
import hash.cracker.manager.types.Response;
import hash.cracker.manager.types.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExternalController {

    private final ManagerService service;

    @Autowired
    public ExternalController(ManagerService service) {
        this.service = service;
    }

    @PostMapping("/hash/crack")
    public ResponseEntity<String> submitTask(@RequestBody Task task) {
        return service.submitTask(task);
    }

    @GetMapping("/hash/status")
    public ResponseEntity<Response> getStatus(@RequestParam String requestId) {
        return service.getStatus(requestId);
    }
}
