package fi.helsinki.cs.titotrainer.app.view;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class RegistrationView extends TitoPageView<TitoRequest> {
    
    @Override
    protected String getTemplateName() {
        return "registration.vm";
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void handle(TitoRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        User user = req.getUserSession().consumeAttribute("regUser", User.class);
        if (user == null)
            user = new User();
        
        tr.put("theUser", user); // Use "theUser" instead of "user" to avoid name clash
        tr.put("availableLocales", req.getContext().getTitoTranslation().getSupportedLocales());

        final Locale locale = this.getTranslator(req).getLocale();
        List<Course> courses = hs.createQuery("FROM Course WHERE hidden = FALSE").list();
        hs.evict(courses);
        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                String s1 = ObjectUtils.toString(o1.getName(locale), "");
                String s2 = ObjectUtils.toString(o2.getName(locale), "");
                return s1.compareTo(s2);
            }
        });
        
        tr.put("availableCourses", courses);
        
        tr.put("invFields", req.getUserSession().consumeAttribute("invFields", Collection.class));
    }
    
    @Override
    public Class<TitoRequest> getRequestType() {
        return TitoRequest.class;
    }
    
}
