package com.myapp.web.jsf2.getstarted.eyetracker.dbdummy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.myapp.web.jsf2.getstarted.eyetracker.Issue;
import com.myapp.web.jsf2.getstarted.eyetracker.Project;
import com.myapp.web.jsf2.getstarted.eyetracker.User;

final class DataBaseDummy implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9004278407703868298L;

    private static final File dbFile;
    private static DataBaseDummy instance;

    static {
        String dbPath = System.getProperty("user.home");
        dbPath += File.separator + ".DataBaseDummy.bin";
        dbFile = new File(dbPath);
    }

    public static DataBaseDummy getInstance() {
        if (instance == null) {
            if (dbFile.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(dbFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    instance = (DataBaseDummy) ois.readObject();
                    ois.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (instance == null) {
                instance = new DataBaseDummy();
            }
        }

        return instance;
    }

    
    private List<Issue> issues = new ArrayList<Issue>();
    private List<User> users = new ArrayList<User>();
    private List<Project> projects = new ArrayList<Project>();
    private Random random = new Random();


    private DataBaseDummy() {
        User admin = new User(10, "admin", "admin@nowhere");
        User nobody = new User(20, "nobody", "nobody@nowhere");
        User batman = new User(30, "batman", "batman@nowhere");
        User brain = new User(40, "brain", "brain@nowhere");
        
        Project domination = new Project(11,
                                         admin,
                                         "world domination",
                                         "rule the earth");
        Project rescue = new Project(12,
                                     batman,
                                     "world rescue",
                                     "save the planet");
        Project server = new Project(13, admin, "petaflop", "feed the ALUs");
        
        Issue memory = new Issue(51, "not enough ram", server, brain, nobody);
        Issue cooling = new Issue(52, "cpu too hot", server, admin, batman);
        Issue opponents = new Issue(53, "too many opponents", domination, brain, nobody);
        Issue memoryLeak = new Issue(54, "memory leak", server, admin, batman);
        Issue compiler = new Issue(55, "compiler bug", server, brain, admin);

        users.add(admin);
        users.add(nobody);
        users.add(batman);
        users.add(brain);
        
        projects.add(domination);
        projects.add(rescue);
        projects.add(server);
        
        issues.add(memory);
        issues.add(cooling);
        issues.add(opponents);
        issues.add(memoryLeak);
        issues.add(compiler);
    }

    public void persist() {
        try {
            FileOutputStream fos = new FileOutputStream(dbFile, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(instance);
            oos.flush();
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object get(Class<?> c, int id) {
        if (c == Issue.class) {
            for (Issue i : issues)
                if (i.getId() == id)
                    return i;

        } else if (c == User.class) {
            for (User i : users)
                if (i.getId() == id)
                    return i;

        } else if (c == Project.class) {
            for (Project i : projects)
                if (i.getId() == id)
                    return i;
        }

        throw new RuntimeException("c=" + c + ", id=" + id);
    }

    public boolean set(Object obj) {
        Class<?> c = obj.getClass();

        if (c == Issue.class) {
            Issue x = (Issue) obj;
            return issues.remove(x);

        } else if (c == User.class) {
            User x = (User) obj;
            return users.remove(x);

        } else if (c == Project.class) {
            Project x = (Project) obj;
            return projects.remove(x);
        }

        throw new RuntimeException("c=" + c + ", obj=" + obj);
    }

    public boolean add(Object obj) {
        Class<?> c = obj.getClass();

        if (c == Issue.class) {
            Issue x = (Issue) obj;
            x.setId(random.nextInt());
            return issues.add(x);

        } else if (c == User.class) {
            User x = (User) obj;
            x.setId(random.nextInt());
            return users.add(x);

        } else if (c == Project.class) {
            Project x = (Project) obj;
            x.setId(random.nextInt());
            return projects.add(x);
        }

        throw new RuntimeException("c=" + c + ", obj=" + obj);
    }

    public boolean remove(Object obj) {
        Class<?> c = obj.getClass();

        if (c == Issue.class) {
            Issue x = (Issue) obj;
            return issues.remove(x);

        } else if (c == User.class) {
            User x = (User) obj;
            return users.remove(x);

        } else if (c == Project.class) {
            Project x = (Project) obj;
            return projects.remove(x);
        }

        throw new RuntimeException("c=" + c + ", obj=" + obj);
    }


    public List<Issue> getIssues() {
        System.err.println("DataBaseDummy.getIssues()");
        return issues;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Project> getProjects() {
        return projects;
    }
}
