package edu.hawaii.kscm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Result {

    int status;
    StringBuilder sb = new StringBuilder();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return sb.toString();
    }

    public void addMsg(String msg) {
        this.sb.append(msg).append('\n');
    }

}

