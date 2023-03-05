package hash.cracker.manager.controllers;

import hash.cracker.manager.services.ManagerService;
import hash.cracker.manager.types.CrackHashWorkerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/manager/hash/crack/request")
public class InternalController {

    private final ManagerService service;

    @Autowired
    public InternalController(ManagerService service) {
        this.service = service;
    }

    @PatchMapping
    public void ReceiveAnswers(@RequestBody CrackHashWorkerResponse response) {
        service.ReceiveAnswers(response);
    }
}
