package fi.helsinki.cs.titotrainer.app.model;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.framework.access.Role;

/**
 * Represents one row in the table "user". A user is identified by his/her unique username.
 */
@Entity
@Table(name = "`user`")
public class User extends AbstractTitoEntity implements Serializable {
    
    public static final int MIN_USERNAME_LENGTH = 2;
    public static final int MAX_USERNAME_LENGTH = 16;
    
    private static final String passwordSalt = "94ivLyiY";

    ////////////////////
    // PRIVATE FIELDS //
    ////////////////////
    
    private Role role;
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String studentNumber;
    private Long courseId; // Not an object reference to implement Serializable
    private String passwordSha1;
    private String resetPasswordSha1;
    private Locale prefLocale;
    
    /**
     * Constructs a User record with all fields unset.
     */
    public User() {
        this.role = new Role();
    }
    
    /**
     * A more convenient constructor.
     */
    public User(String username, TitoBaseRole parentRole) {
        this.role = new Role(username, parentRole);
    }
    
    ///////////////
    // ACCESSORS //
    ///////////////
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        validateStringArgument(email, "email", STRING_CONSTRAINT_NOT_WHITESPACE_ONLY);
        this.email = email;
    }
    
    public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(String firstName) {
        validateStringArgument(firstName, "firstName", STRING_CONSTRAINT_NOT_WHITESPACE_ONLY);
        this.firstName = firstName;
    }
    
    @Id
    @GeneratedValue
    @Override
    public long getId() {
        return id; 
    }
    
    @Override
    public void setId(long id) {
        this.id = id;
    }
    
    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(String lastName) {
        validateStringArgument(lastName, "lastName", STRING_CONSTRAINT_NOT_WHITESPACE_ONLY);
        this.lastName = lastName;
    }
    
    //TODO: length bounds + custom checksum validator (if algo available)
    // @NotEmpty? @NotNull?
    public String getStudentNumber() {
        return studentNumber;
    }
    
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    
    /**
     * <p>The ID of the course of the student.</p>
     * 
     * <p>Administrative users may set this for themselves.</p>
     * 
     * <p>This is stored as an ID and not an object to keep {@link User}
     * easily serializable and safe for long-term storage in a session.</p>
     */
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    @Column(unique = true)
    @NotEmpty
    @Length(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH)
    public String getUsername() {
        return this.role.getName();
    }
    
    /**
     * Sets the username for this user.
     * 
     * @param username The new username
     */
    public void setUsername(String username) {
        validateStringArgument(username, "username", STRING_CONSTRAINT_NOT_BLANK);
        this.role.setName(username);
    }
    
    /**
     * An inelegant workaround to be able to temporarily set an invalid username.
     */
    public void forcefullySetUsername(String username) {
        this.role.setName(username);
    }
    
    @Pattern(regex = "[0-9a-f]{40}", message = "Password not digested correctly.")
    public String getPasswordSha1() {
        return passwordSha1;
    }
    
    public void setPasswordSha1(String passwordSha1) {
        this.passwordSha1 = passwordSha1;
    }
    
    @Pattern(regex = "[0-9a-f]{40}", message = "Reset password not digested correctly.")
    public String getResetPasswordSha1() {
        return resetPasswordSha1;
    }
    
    public void setResetPasswordSha1(String resetPasswordSha1) {
        this.resetPasswordSha1 = resetPasswordSha1;
    }
    
    public static String hashPassword(String unhashedPassword) {
        MessageDigest sha1;
        try {
            sha1 = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        
        String digestable = unhashedPassword + passwordSalt;
        
        byte[] digest = sha1.digest(digestable.getBytes());
        assert(digest.length == 20);
        
        StringBuilder sb = new StringBuilder();
        for (int b : digest) {
            int hi = (b & 0xF0) >> 4;
            int lo = b & 0xF;
            
            char hic = Character.forDigit(hi, 16);
            char loc = Character.forDigit(lo, 16);
            
            sb.append(hic);
            sb.append(loc);
        }
        
        assert(sb.length() == 40);
        
        return sb.toString();
    }
    
    @Type(type = "fi.helsinki.cs.titotrainer.app.model.types.TitoBaseRoleUserType")
    @NotNull
    public TitoBaseRole getParentRole() {
        Role[] parents = this.role.getParents();
        if (parents.length > 0) {
            assert(parents.length == 1);
            return (TitoBaseRole)parents[0];
        } else {
            return null;
        }
    }
    
    public void setParentRole(TitoBaseRole parentRole) {
        if (parentRole == null) {
            throw new NullPointerException("parentRole may not be null!");
        }
        Role[] parents = {parentRole};
        this.role.setParents(parents);
    }
    
    /**
     * An inelegant workaround to be able to temporarily set an invalid (or null) parent role.
     */
    public void forcefullySetParentRole(TitoBaseRole parentRole) {
        Role[] parents = {};
        if (parentRole != null)
            parents = new Role[] {parentRole};
        this.role.setParents(parents);
    }
    
    @Type(type = "org.hibernate.type.LocaleType")
    @Column(length = 8)
    public Locale getPrefLocale() {
        return prefLocale;
    }
    
    public void setPrefLocale(Locale prefLang) {
        this.prefLocale = prefLang;
    }
    
    public boolean inheritsRole(Role role) {
        return this.role.inherits(role);
    }
    
}
