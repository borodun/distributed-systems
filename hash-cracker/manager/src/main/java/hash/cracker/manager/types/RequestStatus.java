package hash.cracker.manager.types;

import java.util.List;

public class RequestStatus {
    private String requestId;
    private Status status;
    private List<String> data;

    public RequestStatus(String requestId, Status status, List<String> data) {
        this.requestId = requestId;
        this.status = status;
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
