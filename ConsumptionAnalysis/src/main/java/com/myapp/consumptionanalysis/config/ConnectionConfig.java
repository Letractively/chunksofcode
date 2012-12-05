package com.myapp.consumptionanalysis.config;

import java.io.Serializable;

public final class ConnectionConfig implements Serializable
{
    private static final long serialVersionUID = 2119126219564192320L;

    private final String hostname;
    private final int portnumber;
    private final String user;
    private final String password;
    private Config config = null;


    public ConnectionConfig(String hostname, int portnumber, String user, String password) {
        this.hostname = hostname;
        this.portnumber = portnumber;
        this.user = user;
        this.password = password;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPortnumber() {
        return portnumber;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectionConfig [");
        builder.append(user);
        builder.append("@");
        builder.append(hostname);
        builder.append(":");
        builder.append(portnumber);
        builder.append("]");
        return builder.toString();
    }

    void setConfig(Config config) {
        synchronized (this) {
            if (this.config != null) {
                throw new RuntimeException("cannot set multiple times");
            }
            this.config = config;
        }
    }

    Config getConfig() {
        return config;
    }


}
