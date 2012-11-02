package fi.helsinki.cs.titotrainer.app.admin.view;

import java.util.List;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class CourseListView extends TitoPageView<TitoRequest> {
    
    @Override
    public Class<TitoRequest> getRequestType() {
        return TitoRequest.class;
    }
    
    @Override
    protected void handle(TitoRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) {
        List<?> courses = hs.createQuery("FROM Course").list();
        tr.put("courses", courses);
    }
    
    @Override
    protected String getTemplateName() {
        return "admin/courselist.vm";
    }
    
}

