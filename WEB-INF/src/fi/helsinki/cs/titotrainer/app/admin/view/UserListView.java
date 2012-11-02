package fi.helsinki.cs.titotrainer.app.admin.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import fi.helsinki.cs.titotrainer.app.admin.request.UserListViewRequest;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class UserListView extends TitoPageView<UserListViewRequest> {
    
    public static final class FilterParams implements Serializable {
        
        public Long courseId;
        
        public FilterParams() {
        }
        
        public FilterParams(UserListViewRequest req) {
            this.courseId = req.courseId;
        }
        
        public String makeQueryString() {
            return "courseId=" + courseId;
        }
        
        public static FilterParams getFromSession(TitoUserSession sess) {
            return sess.getAttribute(FilterParams.class.getName(), FilterParams.class);
        }
        
        public void saveToSession(TitoUserSession sess) {
            sess.setAttribute(FilterParams.class.getName(), this);
        }
    }
    
    @Override
    protected String getTemplateName() {
        return "admin/userlist.vm";
    }
    
    @Override
    public Class<UserListViewRequest> getRequestType() {
        return UserListViewRequest.class;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void handle(UserListViewRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        
        FilterParams params = new FilterParams(req);
        params.saveToSession(req.getUserSession());
        
        Course selectedCourse = null;
        if (params.courseId != null) {
            selectedCourse = (Course)hs.get(Course.class, params.courseId);
        }
        tr.put("selectedCourse", selectedCourse);
        
        Criteria userCriteria = hs.createCriteria(User.class);
        if (selectedCourse != null)
            userCriteria.add(Restrictions.eq("courseId", selectedCourse.getId()));
        userCriteria.addOrder(Order.asc("username"));
        userCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<User> users = userCriteria.list();
        tr.put("users", users);
        
        List<Course> courses = hs.createQuery("FROM Course").list();
        tr.put("courses", courses);
        
        Map<Long, Course> courseById = new HashMap<Long, Course>();
        for (Course course : courses)
            courseById.put(course.getId(), course);
        tr.put("courseById", courseById);
    }
    
}
