package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Map;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;

public class CreateCategoryRequest extends TitoRequest {
    public long courseId;
    public Map<String, String> categoryName;
}
