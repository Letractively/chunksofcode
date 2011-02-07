package com.myapp.web.jsf2.getstarted.eyetracker;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.validation.constraints.Size;

@ManagedBean
@SessionScoped
public class Issue implements Serializable {

    public static enum Status {
        NEW, ASSIGNED, FIXED, REVIEW_REQUESTED
    }
    
    private static Format DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
    private static final long serialVersionUID = 6965072552678675916L;
    
    private int id;

    @Size(min=3, max=300)
    private String title;

    private Project project;
    private User creator;
    private User owner;

    private Status status = Status.NEW;
    private List<String> entries = new ArrayList<String>();
    
    @Size(min=3, max=300)
    private String newEntry;

    public Issue(int id, String title, Project project, User creator, User owner) {
        this.id = id;
        this.title = title;
        this.project = project;
        this.creator = creator;
        this.owner = owner;
    }
    
    public Issue() {}
    
    public String view() {
        System.err.println("Issue.view()");
        // do some busines logic...
        return "view";
    }
    
    public String addEntry() {
        System.err.println("Issue.addEntry()");
        
        // do some busines logic...
        if (newEntry == null) {
            return "error";
        }
        
        entries.add(newEntry);
        return "entryAdded";
    }

    public String getNewEntry() {
        return "";
    }

    public void setNewEntry(String entry) {
        System.out.println("Issue.setNewEntry(" + entry + ")");
        
        StringBuilder bui = new StringBuilder();
        bui.append(entries.size()).append(".) ");
        bui.append(DATE_FORMAT.format(new Date())).append(" : ");
        bui.append(entry);
        
        newEntry = entry;
    }

    public int getId() {return id;}
    public String getTitle() {return title;}
    public Project getProject() {return project;}
    public User getCreator() {return creator;}
    public User getOwner() {return owner;}
    public Status getStatus() {return status;}
    public List<String> getEntries() {return entries;}

    public void setId(int id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setProject(Project project) {this.project = project;}
    public void setCreator(User creator) {this.creator = creator;}
    public void setOwner(User owner) {this.owner = owner;}
    public void setStatus(Status status) {this.status = status;}

    @Override
    public int hashCode() {
        final int prime = 43;
        int result = 1;
        result = prime * (result + id);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Issue other = (Issue) obj;
        if (id != other.id)
            return false;
        return true;
    }
}