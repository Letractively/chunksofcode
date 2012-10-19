package com.myapp.tool.gnomestart.programstate.impl.linux;


import java.io.*;


final class IO {

    private IO() {
    }

    public static final String NL = System.getProperty("line.separator");

    public static Process start(ProcessBuilder builder) {
        // hook for future modifications of the builder here
        try {
            return builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String readProcOutput(String... command) {
        ProcessBuilder builder = new ProcessBuilder(command);
        String pOutput = readProcOutput(builder);
        return pOutput;
    }

    private static String readProcOutput(ProcessBuilder pb) {
        Process p = null;

        InputStream pInputStream = null;
        InputStream pErrorStream = null;

        StringBuilder stdOut = new StringBuilder();
        StringBuilder errOut = new StringBuilder();

        try {
            p = start(pb);

            pInputStream = p.getInputStream();
            read(pInputStream, stdOut);

            pErrorStream = p.getErrorStream();
            read(pInputStream, errOut);

            int exitstatus = p.waitFor();
            if (exitstatus != 0) {
                throw new RuntimeException("process " + pb.command()
                        + " finished with status: " + exitstatus + "." + NL
                        + "---output start---" + NL + stdOut + NL + "---output end---"
                        + NL + "---error start---" + NL + errOut + NL + "---error end---");
            }
            return stdOut.toString();

        } catch (IOException e) {
            throw new RuntimeException("could not execute: " + pb.command() + NL
                    + "--- ERROR OUTPUT START ---" + NL + errOut.toString() + NL
                    + "--- ERROR OUTPUT END ---", e);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        } finally {
            close(pInputStream);
            close(pErrorStream);
        }
    }


    private static void read(InputStream in, Appendable a) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;

        for (int i = 0; (line = br.readLine()) != null; i++) {
            a.append(line);
            a.append(NL);
            if (i > 5000) {
                return;
            }
        }
    }

    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
