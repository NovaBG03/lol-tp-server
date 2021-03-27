package web.app.loltp.loltpserver.exceptions;

import org.springframework.http.HttpStatus;

public class LolTpException extends RuntimeException {

    private HttpStatus status;

    public LolTpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
