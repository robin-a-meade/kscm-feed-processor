package edu.hawaii.kscmfeedprocessor;

/**
 *
 */
public enum Status {

    SUCCESS(0), FAILED(1);

    private int status;

    private Status(int status) {
        this.status = status;
    }

}
