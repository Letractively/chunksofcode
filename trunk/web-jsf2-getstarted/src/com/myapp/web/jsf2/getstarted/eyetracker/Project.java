package com.myapp.web.jsf2.getstarted.eyetracker;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class Project implements Serializable {

    private static final long serialVersionUID = 6129399429671131962L;

    private int id;
    private User owner;
    private String name;
    private String description;

    public Project(int id, User owner, String name, String description) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
    }

    public Project() {}

    public int getId() {return id;}
    public User getOwner() {return owner;}
    public String getName() {return name;}
    public String getDescription() {return description;}

    public void setId(int id) {this.id = id;}
    public void setOwner(User owner) {this.owner = owner;}
    public void setName(String name) {this.name = name;}
    public void setDescription(String description) {this.description = description;}

    public String toString() {
        return "(id=" + getId() + ") " + getName();
    }
    
    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 3;
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
        Project other = (Project) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
