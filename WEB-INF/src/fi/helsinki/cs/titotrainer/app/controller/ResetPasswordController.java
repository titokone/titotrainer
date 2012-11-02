package fi.helsinki.cs.titotrainer.app.controller;

import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.hibernate.Query;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.ResetPasswordRequest;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.config.Config;
import fi.helsinki.cs.titotrainer.framework.config.ConfigLoader;
import fi.helsinki.cs.titotrainer.framework.config.ConfigUtils;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class ResetPasswordController extends TitoActionController<ResetPasswordRequest> {
    
    @Override
    public Class<ResetPasswordRequest> getRequestType() {
        return ResetPasswordRequest.class;
    }

    @Override
    protected Response handleValid(ResetPasswordRequest req, Session hs) throws Exception {
        Query q = hs.createQuery("FROM User WHERE username = ?");
        q.setString(0, req.username);
        User user = (User)q.uniqueResult();
        
        Translator tr = getTranslator(req);
        Messenger msngr = req.getUserSession().getMessenger();
        if (user == null) {
            msngr.appendMessage(Messenger.GLOBAL_ERROR_CATEGORY, tr.tr("no_such_username"));
            return handleInvalid(req, hs, null);
        }
        
        String generatedPassword = generateRandomPassword();

        try {
            sendTheEmail(tr, req.getContext().getConfigLoader(), user, generatedPassword);
        } catch (Exception e) {
            msngr.appendMessage(Messenger.GLOBAL_ERROR_CATEGORY, tr.tr("sending_email_failed"));
            return handleInvalid(req, hs, null);
        }
        
        user.setResetPasswordSha1(User.hashPassword(generatedPassword));
        hs.update(user);
        hs.flush();
        
        msngr.appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, tr.tr("success"));
        return new RedirectResponse(req.getBasePath() + "/login");
    }

    @Override
    protected Response handleInvalid(ResetPasswordRequest req, Session hs,
        Collection<RequestInvalidity> invalidities) throws Exception {
        return new RedirectResponse(req.getBasePath() + "/resetpassword");
    }
    
    private String generateRandomPassword() {
        String chars = "abcdefghjklmnpqrstuvwxyz";
        chars += "ABCDEFGHJKLMNPRSTUVWXYZ";
        chars += "0123456789";
        
        Random rand = new Random();
        int len = 8;
        String pw = "";
        for (int i = 0; i < len; ++i) {
            pw += chars.charAt(rand.nextInt(chars.length()));
        }
        
        return pw;
    }
    
    private void sendTheEmail(Translator tr, ConfigLoader cl, User user, String generatedPassword) throws Exception {
        Config config = cl.load("reset-password");
        if (!config.get("enabled", "false").equals("true")) {
            logger.info("Reset password email disabled");
            return;
        }
        
        Properties props = ConfigUtils.toProperties(config);
        props.remove("enabled");
        props.remove("sender");
        
        javax.mail.Session session = javax.mail.Session.getInstance(props);
        
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(config.get("sender", "titotrainer2@noreply.com")));
        InternetAddress[] recipients = {new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName())};
        msg.setRecipients(Message.RecipientType.TO, recipients);
        msg.setSubject(tr.tr("mail_subject"));
        msg.setSentDate(new Date());
        msg.setText(tr.tr("new_password_is") + " " + generatedPassword);
        
        Transport.send(msg);
        
        logger.info("New password sent to " + user.getEmail());
    }
}
