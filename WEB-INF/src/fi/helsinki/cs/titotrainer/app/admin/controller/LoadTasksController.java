package fi.helsinki.cs.titotrainer.app.admin.controller;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.util.LimitedInputStream;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.LoadTasksRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class LoadTasksController extends TitoActionController<LoadTasksRequest> {
    
    private final int MAX_FILE_SIZE = 4 * 1024 * 1024; // 4 MB
    
    @Override
    protected Response handleValid(LoadTasksRequest req, Session hs) throws Exception {
        try {
            InputStream is = new LimitedInputStream(req.file.getInputStream(), MAX_FILE_SIZE) {
                @Override
                protected void raiseError(long pSizeMax, long pCount) throws IOException {
                    throw new IOException("File too large.");
                }
            };
            
            byte[] data = IOUtils.toByteArray(is);
            req.getUserSession().setAttribute("uploadedTaskFile", data);
            
        } catch (IOException e) {
            throw e;
        }
        
        return new RedirectResponse("/admin/loadtasks?courseId=" + req.courseId);
    }
    
    @Override
    public Class<LoadTasksRequest> getRequestType() {
        return LoadTasksRequest.class;
    }
}
