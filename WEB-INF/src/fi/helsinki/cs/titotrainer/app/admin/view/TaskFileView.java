package fi.helsinki.cs.titotrainer.app.admin.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.TaskFileRequest;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.fileconv.TaskFileMaker;
import fi.helsinki.cs.titotrainer.framework.response.ResponseBodyWriter;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.DefaultViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.View;

public class TaskFileView implements View<TaskFileRequest> {

    @SuppressWarnings("unchecked")
    @Override
    public ViewResponse handle(TaskFileRequest req) throws Exception {
        Session hs = req.getAttribs().getHibernateSession();
        List<Task> tasks = hs.createQuery("FROM Task WHERE id IN (" + StringUtils.join(req.ids, ',') + ")").list();
        
        final Document doc = TaskFileMaker.makeTasksXML(tasks);
        
        ResponseBodyWriter rw = new ResponseBodyWriter() {
            @Override
            public void writeResponse(OutputStream os, Charset charset) throws IOException {
                OutputStreamWriter osWriter = new OutputStreamWriter(os, charset);
                new XMLWriter(osWriter, OutputFormat.createPrettyPrint()).write(doc);
                osWriter.flush();
            }
        };
        
        String filename;
        if (tasks.size() == 1)
            filename = "task" + tasks.iterator().next().getId() + ".xml";
        else
            filename = "tasks.xml";
        
        DefaultViewResponse resp = new DefaultViewResponse(rw, "text/xml; charset=utf-8");
        resp.setContentDisposition("attachment; filename=" + filename);
        return resp;
    }

    @Override
    public Class<TaskFileRequest> getRequestType() {
        return TaskFileRequest.class;
    }
    
    
    
}
