package hash.cracker.manager.types;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties("startTime")
public class TaskStatus {
    private Status status;
    private List<String> data;
    private Instant startTime;

    public TaskStatus() {
        this.status = Status.IN_PROGRESS;
        this.data = new ArrayList<String>();
        this.startTime = Instant.now();
    }
}
