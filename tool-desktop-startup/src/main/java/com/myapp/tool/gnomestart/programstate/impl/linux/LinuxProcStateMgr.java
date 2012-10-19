package com.myapp.tool.gnomestart.programstate.impl.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myapp.tool.gnomestart.DesktopStarter;
import com.myapp.tool.gnomestart.programstate.IProcessManager;
import com.myapp.tool.gnomestart.programstate.Proc;

public class LinuxProcStateMgr implements IProcessManager {

    private DesktopStarter starter;
    
    public LinuxProcStateMgr() {
    }

    @Override
    public void setDesktopStarter(DesktopStarter starter) {
        this.starter = starter;
    }
    @Override
    public List<Proc> determineProcessStates() {
        Map<Integer, Proc> map = new TreeMap<Integer, Proc>();
        String state = IO.readProcOutput("bash",
                                         "-c",
                                         "ps auxww | grep -v grep | grep -E \"^\\s*$(whoami)\"");
        BufferedReader br = new BufferedReader(new StringReader(state));
        String ps_auxwww_line = null;

        try {
            while ((ps_auxwww_line = br.readLine()) != null) {
                Proc p = parse(ps_auxwww_line);
                map.put(p.getPid(), p);
            }
        } catch (Exception e) {
            throw new RuntimeException(ps_auxwww_line, e);
        }

        return new ArrayList<Proc>(map.values());
    }


    /**<pre>        
    USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND
    andre     1682  0.0  0.0  69764  2884 ?        Sl   10:26   0:00 /usr/bin/gnome-keyring-daemon --daemonize --login
    andre     1700  0.0  0.2 167332  7740 ?        Ssl  10:26   0:00 gnome-session
    andre     1734  0.0  0.0  11936   412 ?        Ss   10:26   0:00 /usr/bin/ssh-agent /usr/bin/dbus-launch --exit-with-session gnome-session
    andre     1747  0.0  0.5 352708 18212 ?        Ss   10:26   0:03 /usr/lib/gnome-settings-daemon/gnome-settings-daemon
    andre     1774  0.0  0.7 364112 27952 ?        S    10:26   0:02 empathy
    </pre>*/
    private static final Pattern PS_AUXWWW_OUTPUT_PATTERN = Pattern.compile("(?ix)  "
            + "^" + " \\s* " + Pattern.quote(System.getProperty("user.name")) + " \\s+ "
            +
            // 1(pid )     2(cpu    )     3(mem    )     4(vsz   ) 
            "   (\\d+) \\s+ ([.\\d]+) \\s+ ([.\\d]+) \\s+ ([\\d]+) \\s+ "
            +
            // 5(rss )     6(tty )     7(stat)     8(start)     9(time)    10(cmd)       
            "   (\\d+) \\s+ (\\S+) \\s+ (\\S+) \\s+ (\\S+ ) \\s+ (\\S+) \\s+ ( .*)"
            + " \\s* $");

    private static Proc parse(String ps_auxwww_line) {
        Matcher m = PS_AUXWWW_OUTPUT_PATTERN.matcher(ps_auxwww_line);
        if (! m.matches()) {
            throw new RuntimeException(ps_auxwww_line);
        }

        int pid = Integer.parseInt(m.group(1));
        String command = m.group(10);

        Proc proc = new Proc(pid, command);
        return proc;
    }

    @Override
    public Process start(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException(command);
        }
        String[] exec = { "bash", "-c", command};
        starter.getLog().trace("Executing: "+Arrays.toString(exec));
        ProcessBuilder builder = new ProcessBuilder(exec);
        
        try {
            Process p = builder.start();
            return p;
        } catch (IOException e) {
            throw new RuntimeException(Arrays.toString(exec), e);
        }
    }
}
