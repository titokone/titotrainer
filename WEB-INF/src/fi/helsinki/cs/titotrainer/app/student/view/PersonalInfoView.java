package fi.helsinki.cs.titotrainer.app.student.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;


public class PersonalInfoView extends TitoPageView<TitoRequest> {
    
    public Class<TitoRequest> getRequestType() {
        return TitoRequest.class;
    }
    
    protected void handle(TitoRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) {
        User user = req.getUserSession().consumeAttribute("profile", User.class, req.getUserSession().getAuthenticatedUser());
        
        tr.put("user", user);
        
        tr.put("availableLocales", req.getContext().getTitoTranslation().getSupportedLocales());

        final Locale locale = this.getTranslator(req).getLocale();
        List<Course> courses = getAvailableCourses(hs, user);
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
    }

    @SuppressWarnings("unchecked")
    private List<Course> getAvailableCourses(Session hs, User user) {
        if (user.inheritsRole(TitoBaseRole.ADMINISTRATIVE))
            return hs.createQuery("FROM Course").list();
        else
            return hs.createQuery("FROM Course WHERE hidden = FALSE").list();
    }
    
    @Override
    protected String getTemplateName() {
        return "student/personalinfo.vm";
    }
    
    
}
