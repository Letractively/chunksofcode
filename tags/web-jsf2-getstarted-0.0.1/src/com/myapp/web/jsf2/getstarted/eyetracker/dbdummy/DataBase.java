package com.myapp.web.jsf2.getstarted.eyetracker.dbdummy;

import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.myapp.web.jsf2.getstarted.eyetracker.Issue;
import com.myapp.web.jsf2.getstarted.eyetracker.Project;
import com.myapp.web.jsf2.getstarted.eyetracker.User;
import com.myapp.web.jsf2.getstarted.eyetracker.Issue.Status;

@ManagedBean
@ApplicationScoped
public class DataBase {
    
    private DataBaseDummy dummy;

    public DataBase() {
        dummy = DataBaseDummy.getInstance();
    }
    
    public boolean add(Object obj) {
        return dummy.add(obj);
    }

    public Object get(Class<?> c, int id) {
        return dummy.get(c, id);
    }

    public boolean remove(Object obj) {
        return dummy.remove(obj);
    }

    public boolean set(Object obj) {
        return dummy.set(obj);
    }

    public List<Issue> getAllIssues() {
        System.err.println("Database.getAllIssues()");
        return dummy.getIssues();
    }

    public List<Project> getAllProjects() {
        return dummy.getProjects();
    }

    public List<User> getAllUsers() {
        return dummy.getUsers();
    }
    
    public Status[] getAllStatuses() {
        return Status.values();
    }
}
