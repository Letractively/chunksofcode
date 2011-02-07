package com.myapp.tools.web.httpproxy;

import com.myapp.util.log.Log;
import java.net.*;

public class ProxyStarter
        extends Thread {

    private ServerSocket server = null;
    private int listenPort = 8080;
    private String forwardServer = "";
    private int forwardPort = 0;

    public ProxyStarter(int port, String proxyServer, int proxyPort) {
        this.listenPort = port;
        forwardServer = proxyServer;
        forwardPort = proxyPort;
    }

    @Override
    public void run() {
        try {
            /*listen for connections with a new ServerSocket*/
            server = new ServerSocket(listenPort);

            for (;;)
                new ProxyThread(server.accept(),
                                forwardServer,
                                forwardPort).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        server = null;
    }

    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println(
                    "Parameters:\n\t" +
                    "PORT NUMBER (required)\n\t" +
                    "FORWARD-SERVER && FORWARD-PORT (optional, but both together)");
            System.exit(-1);
        }

        int port = Integer.parseInt(args[0]);
        String fwdProxyName = "";
        int fwdProxyPort = 0;

        if (args.length == 3) {
            fwdProxyName = args[1];
            fwdProxyPort = Integer.parseInt(args[2]);
        }

        Log.logln("##############################################");
        Log.logln("##");
        Log.logln("## STARTING PROXY  ");
        Log.logln("##");
        Log.logln("## on port: " + port);
        if (fwdProxyName.length() > 0 && fwdProxyPort > 0)
            Log.logln("## forwarding to: " + fwdProxyName + ":" + fwdProxyPort);
        Log.logln("##");
        Log.logln("##############################################");

        /*starting a new ProxyStarter instance with the given args*/
        new ProxyStarter(port, fwdProxyName, fwdProxyPort).start();

        for (;;)
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}
