package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Map;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;

public class UpdateCategoryRequest extends TitoRequest {
    public long id;
    public Map<String, String> categoryName;
}
