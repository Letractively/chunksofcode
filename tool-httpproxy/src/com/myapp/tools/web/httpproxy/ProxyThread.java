package com.myapp.tools.web.httpproxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.myapp.util.log.Log;

/**
 * The ProxyThread will take an HTTP request from the client
 * socket and send it to either the server that the client is
 * trying to contact, or another proxy server
 *
 * @author andre
 */
public class ProxyThread
        extends Thread {

    public static final String LINE_BREAK = "\r\n";
    /*
     */
    String fwdUrl = null;
    int fwdPort = -1;
    int timeout = 20 * 1000;
    /*
     */
    private Socket client,  server;
    private InputStream serverIn,  clientIn;
    private OutputStream serverOut,  clientOut;

    public ProxyThread(Socket client) {
        this.client = client;
    }

    public ProxyThread(Socket client, String fwdUrl, int fwdPort) {
        this(client);
        this.fwdUrl = fwdUrl;
        this.fwdPort = fwdPort;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        StringBuilder logBuilder = new StringBuilder();
        String hostName = null;
        int hostPort = -1;

        HTTPData request = null;
        HTTPData response = null;

        try {
            logAppend("CONNECTING TO CLIENT...", logBuilder);
            clientIn = new BufferedInputStream(client.getInputStream());
            clientOut = new BufferedOutputStream(client.getOutputStream());
            logAppend("\t[OK] client: " + client.toString(), logBuilder);

            logAppend("GET REQUEST FROM CLIENT...", logBuilder);
            request = streamData(clientIn, false);
            logAppend("\t[OK] bytes loaded: " + request.size(), logBuilder);

            try {
                logAppend("CONNECTING TO SERVER...", logBuilder);
                if (fwdUrl != null && fwdPort != -1)
                    server = new Socket(fwdUrl, fwdPort);
                else {
                    Object[] hostNameAndPort = request.splitHostNameAndPort();
                    if (hostNameAndPort != null) {
                        hostName = (String) hostNameAndPort[0];
                        hostPort = (Integer) hostNameAndPort[1];
                    } else {
                        hostName = fwdUrl;
                        hostPort = fwdPort;
                    }
                    server = new Socket(hostName, hostPort);
                }

                logAppend("\t[OK] server: " + server.toString(), logBuilder);
            } catch (Exception e) {
                System.err.println(logBuilder);
                e.printStackTrace();
            }

            if (server != null) {
                server.setSoTimeout(timeout);
                serverIn = new BufferedInputStream(server.getInputStream());
                serverOut = new BufferedOutputStream(server.getOutputStream());

                logAppend("SEND REQUEST TO SERVER...", logBuilder);
                request.writeTo(serverOut);
                serverOut.flush();
                logAppend("\t[OK]", logBuilder);

                logAppend("GET RESPONSE FROM SERVER...", logBuilder);
                response = streamData(serverIn, true);
                logAppend("\t[OK] bytes loaded: " + response.size(), logBuilder);

                logAppend("CLOSING STREAMS FROM AND TO SERVER...", logBuilder);
                serverIn.close();
                serverOut.close();
                logAppend("\t[OK]", logBuilder);
            }

            logAppend("SEND RESPONSE TO CLIENT...", logBuilder);
            response.writeTo(clientOut);
            logAppend("\t[OK]", logBuilder);

            logAppend("CLOSING STREAMS FROM AND TO CLIENT...", logBuilder);
            clientOut.close();
            clientIn.close();
            client.close();
            logAppend("\t[OK]", logBuilder);

            Utils.getSummary(logBuilder,
                             client,
                             hostName == null ? fwdUrl : hostName,
                             hostPort == -1 ? fwdPort : hostPort,
                             request.toString(),
                             response.toString(),
                             System.currentTimeMillis() - startTime);
            Log.logln(logBuilder.toString());

        } catch (Exception e) {
            System.err.println(logBuilder);
            e.printStackTrace();
        }
    }

    private void logAppend(String msg, StringBuilder bui) {
        bui.append(msg + "\n");
        Log.logln(msg);
    }

    /**
    get the HTTP data from an InputStream
    @param in an InputStream
    @param waitForDisconnect if we're waiting in case the HTTP header
    doesn't tell us the Content-Length
     */
    private static HTTPData streamData(InputStream in, boolean wait4Timeout) {
        HTTPData data = new HTTPData();
        try {
            /* ----- READING THE HTTP HEADER ----- */
            StringBuilder headerBui = new StringBuilder();

            /*first line from header which may contain the response code*/
            String y;
            if ((y = Utils.readHttpTerminatedLine(in)) != null) {
                headerBui.append(y + LINE_BREAK);
                data.parseHeaderMetaData(y);
            }

            /*remaining lines from the header*/
            while ((y = Utils.readHttpTerminatedLine(in)) != null) {
                if (y.length() == 0)
                    break;
                headerBui.append(y + LINE_BREAK);
                data.parseHeaderMetaData(y);
            }


            /*-------- HEADER FINISHED -------*/
            /*a blank line separates header and content*/
            headerBui.append(LINE_BREAK);
            data.saveHeader(headerBui);

            /*we are done if an error occured on the server*/
            int contentLength = data.getContentLength();
            if ((data.getResponseCode() != 200) && contentLength > 0)
                return data;

            /*------- READING THE HTTP BODY -------*/
            /*if the length was defined we do not need to wait until timeout*/
            if (contentLength > 0)
                wait4Timeout = false;

            /*read all bytes until end of data or timeout*/
            if (contentLength > 0 || wait4Timeout) {
                byte[] buffer = new byte[1024];
                int bytesReadTotal = 0;
                int bufferSize = 0;
                boolean loopAgain = true;

                while (loopAgain && (bufferSize = in.read(buffer)) >= 0) {
                    data.saveBytes(buffer, 0, bufferSize);
                    bytesReadTotal += bufferSize;
                    loopAgain = wait4Timeout || bytesReadTotal < contentLength;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
