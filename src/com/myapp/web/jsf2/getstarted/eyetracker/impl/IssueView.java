package com.myapp.web.jsf2.getstarted.eyetracker.impl;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.myapp.web.jsf2.getstarted.eyetracker.Issue;

@ManagedBean
@SessionScoped
public class IssueView implements Serializable {
    private static final long serialVersionUID = 8774534548915110502L;

    private Issue issue;

    public IssueView() {
        System.err.println("IssueView.IssueView()");
    }

    public Issue getIssue() {
        System.err.println("IssueView.getIssue()");
        return issue;
    }

    public void setIssue(Issue issue) {
        System.err.println("IssueView.setIssue()");
        this.issue = issue;
    }

    public String createNewIssue() {
        System.err.println("IssueView.createNewIssue()");
        issue = new Issue();
        return "createIssue";
    }
    
    public Date getServerTime() {
        System.err.println("IssueView.getServerTime()");
        return new Date();
    }
}
