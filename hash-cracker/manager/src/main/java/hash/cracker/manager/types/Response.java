package hash.cracker.manager.types;

import java.util.List;

public class Response {
    private Status status;
    private List<String> data;

    public Response(Status status, List<String> data) {
        this.status = status;
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

