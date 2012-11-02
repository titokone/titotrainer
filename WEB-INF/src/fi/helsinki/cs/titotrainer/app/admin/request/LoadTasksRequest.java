package fi.helsinki.cs.titotrainer.app.admin.request;

import org.apache.commons.fileupload.FileItem;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;

public class LoadTasksRequest extends TitoRequest {
    public long courseId;
    
    public FileItem file;
}
