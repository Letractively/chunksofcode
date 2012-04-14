package com.myapp.tool.gnomestart.programstate;


/** represents a process of the current user having a pid and a
 * command
 * 
 * @author andre */
public final class Proc implements Comparable<Proc> {

    private final Integer pid;
    private final String command;

    
    public Proc(int pid, String command) {
        if (pid < 0) {
            throw new RuntimeException(pid + " < 0");
        }
        if (command == null) {
            throw new RuntimeException("winid == null");
        }
        this.pid = pid;
        this.command = command;
    }


    @Override
    public String toString() {
        return "ProcessState[pid="+pid+", cmd="+command.trim().replaceFirst(" .*$", " ...")+"]";
    }

    @Override
    public int compareTo(Proc o) {
        return pid.compareTo(o.pid);
    }
    
    public String getCommand() {
        return command;
    }
    
    public Integer getPid() {
        return pid;
    }
}