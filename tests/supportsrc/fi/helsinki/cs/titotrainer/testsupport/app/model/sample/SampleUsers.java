package fi.helsinki.cs.titotrainer.testsupport.app.model.sample;

import java.util.Locale;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.model.User;

public class SampleUsers {

    /* Data for the sample users */
    
    public static final String ADMIN_PASSWORD  = "administrator";
    public static final String ADMIN_FIRSTNAME = "Albert";
    public static final String ADMIN_LASTNAME  = "Administrator";
    public static final String ADMIN_USERNAME  = "adminalbert";

    public static final String NYKANEN_PASSWORD  = "nykänen";
    public static final String NYKANEN_FIRSTNAME = "Matti";
    public static final String NYKANEN_LASTNAME  = "Nykänen";
    public static final String NYKANEN_USERNAME  = "matti";
    
    public static final String PULLMAN_PASSWORD  = "pullman";
    public static final String PULLMAN_FIRSTNAME = "Mathew";
    public static final String PULLMAN_LASTNAME  = "Pullman"; 
    public static final String PULLMAN_USERNAME  = "mathew";
    
    public static final String ASSISTANT_PASSWORD  = "teacher23445";
    public static final String ASSISTANT_FIRSTNAME = "Andrew A.";
    public static final String ASSISTANT_LASTNAME  = "Assistant";
    public static final String ASSISTANT_USERNAME  = "andrew";
    
    public static final String EDITOR_PASSWORD  = "editor123";
    public static final String EDITOR_FIRSTNAME = "Eetu";
    public static final String EDITOR_LASTNAME  = "Editor";
    public static final String EDITOR_USERNAME  = "eetu";
    
    public final User admin;
    public final User nykanen;
    public final User pullman;
    public final User assistant;
    public final User editor;

    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    public SampleUsers(Session session) {
        session.save(this.admin = SampleUsers.createUserAdmin());
        session.save(this.nykanen = SampleUsers.createUserNykanen());
        session.save(this.pullman = SampleUsers.createUserPullman());
        session.save(this.assistant = SampleUsers.createUserAssistant());
        session.save(this.editor = SampleUsers.createUserEditor());
        session.flush();
    }
    
    public static User createUserAdmin() {
        User user;
        user = new User(SampleUsers.ADMIN_USERNAME, TitoBaseRole.ADMINISTRATOR);
        user.setFirstName(SampleUsers.ADMIN_FIRSTNAME);
        user.setLastName(SampleUsers.ADMIN_LASTNAME);
        user.setPasswordSha1(User.hashPassword(SampleUsers.ADMIN_PASSWORD));
        return user;
    }
    
    public static User createUserNykanen() {
        User user;
        user = new User(SampleUsers.NYKANEN_USERNAME, TitoBaseRole.STUDENT);
        user.setFirstName(SampleUsers.NYKANEN_FIRSTNAME);
        user.setLastName(SampleUsers.NYKANEN_LASTNAME);
        user.setPasswordSha1(User.hashPassword(SampleUsers.NYKANEN_PASSWORD));
        user.setPrefLocale(Locale.CHINESE);
        return user;
    }
    
    public static User createUserPullman() {
        User user;
        user = new User(SampleUsers.PULLMAN_USERNAME, TitoBaseRole.STUDENT);
        user.setFirstName(SampleUsers.PULLMAN_FIRSTNAME);
        user.setLastName(SampleUsers.PULLMAN_LASTNAME);
        user.setPasswordSha1(User.hashPassword(SampleUsers.PULLMAN_PASSWORD));        
        return user;
    }
    
    public static User createUserAssistant() {
        User user;
        user = new User(SampleUsers.ASSISTANT_USERNAME, TitoBaseRole.ASSISTANT);
        user.setFirstName(SampleUsers.ASSISTANT_FIRSTNAME);
        user.setLastName(SampleUsers.ASSISTANT_LASTNAME);
        user.setPasswordSha1(User.hashPassword(SampleUsers.ASSISTANT_PASSWORD));        
        return user;
    }
    
    public static User createUserEditor() {
        User user;
        user = new User(SampleUsers.EDITOR_USERNAME, TitoBaseRole.EDITOR);
        user.setFirstName(SampleUsers.EDITOR_FIRSTNAME);
        user.setLastName(SampleUsers.EDITOR_LASTNAME);
        user.setPasswordSha1(User.hashPassword(SampleUsers.EDITOR_PASSWORD));        
        return user;
    }
    
}