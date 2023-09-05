package com.cyberai.entity;

public class SshConnection {
    private String username;
    private String password;
    private String hostname;

    public SshConnection(String username, String password, String hostname) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }
}
