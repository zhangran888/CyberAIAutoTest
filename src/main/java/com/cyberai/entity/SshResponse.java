package com.cyberai.entity;

public class SshResponse {
    private String stdOutput;
    private String errOutput;
    private int returnCode;

    public SshResponse(String stdOutput, String errOutput, int returnCode) {
        this.stdOutput = stdOutput;
        this.errOutput = errOutput;
        this.returnCode = returnCode;
    }

    public String getStdOutput() {
        return stdOutput;
    }

    public String getErrOutput() {
        return errOutput;
    }

    public int getReturnCode() {
        return returnCode;
    }
}
