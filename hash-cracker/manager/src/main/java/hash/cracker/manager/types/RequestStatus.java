package hash.cracker.manager.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestStatus {
    private Status status;
    private List<String> data;

    public RequestStatus() {
        this.status = Status.IN_PROGRESS;
        this.data = new ArrayList<String>();
    }
}
