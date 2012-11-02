package fi.helsinki.cs.titotrainer.app.student.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.access.CurrentCredentials;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.student.request.EditProfileRequest;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class EditProfileControllerTest extends ControllerTestCase<EditProfileRequest, EditProfileController> {

    private Session hs;
    private SampleUsers sampleUsers;
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        this.sampleUsers = new SampleUsers(this.hs);
        CurrentCredentials.setCurrentUser(sampleUsers.nykanen);
    }
    
    @Override
    protected Class<EditProfileController> getControllerType() {
        return EditProfileController.class;
    }
    
    @Test
    public void shouldNullifyParameters() throws Exception {
        EditProfileRequest req = this.createRequest(sampleUsers.nykanen);
        
        req.firstName = "   ";        
        req.lastName = "  ";
        req.email = "";
        /*
         * passwords have to be the same in this test to pass the request object's 
         * own validate method
         */
        req.passwd = "    ";
        req.passwd2 = "    ";
        
        EditProfileController crl = new EditProfileController();
        assertThat(crl.handle(req), instanceOf(RedirectResponse.class));
        
        assertNull(req.firstName);
        assertNull(req.lastName);
        assertNull(req.email);
        assertNull(req.passwd);
        assertNull(req.passwd2);
    }
    
    @Test
    public void shouldPutErrorMessageInMessengerIfPasswordsAreNotEqual() throws Exception {
        EditProfileRequest req = this.createRequest(sampleUsers.nykanen);
        
        req.passwd = "foo";
        req.passwd2 = "bar";
        
        EditProfileController crl = new EditProfileController();
        crl.handle(req);
        
        List<String> stringList = req.getUserSession().getMessenger().getMessages(Messenger.GLOBAL_ERROR_CATEGORY);
        assertTrue(stringList.contains(crl.getTranslator(req).tr("passwords_did_not_match")));
    }
    
    @Test
    public void shouldChangePassword() throws Exception {
        EditProfileRequest req = this.createRequest(sampleUsers.nykanen);
        
        req.passwd = "foo";
        req.passwd2 = "foo";
        assert(SampleUsers.NYKANEN_PASSWORD != req.passwd);
        
        EditProfileController crl = new EditProfileController();
        assertThat(crl.handle(req), instanceOf(RedirectResponse.class));
        
        hs.clear();
        
        User user = (User)hs.load(User.class, sampleUsers.nykanen.getId());
        String pswd = user.getPasswordSha1();
        String newpswd = User.hashPassword("foo");
        assertEquals(newpswd, pswd);
    }
    
    @Test
    public void shouldChangePreferredLocale() throws Exception {
        EditProfileRequest req = this.createRequest(sampleUsers.nykanen);
        assert(Locale.CHINESE.equals(sampleUsers.nykanen.getPrefLocale()));
        
        req.prefLocale = Locale.ENGLISH.toString();
        
        EditProfileController crl = new EditProfileController();
        assertThat(crl.handle(req), instanceOf(RedirectResponse.class));
        
        hs.clear();
        
        User user = (User)hs.load(User.class, sampleUsers.nykanen.getId());
        
        assertEquals(Locale.ENGLISH, user.getPrefLocale());
    }
    
    
}
