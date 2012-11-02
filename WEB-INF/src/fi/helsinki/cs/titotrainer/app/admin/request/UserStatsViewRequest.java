package fi.helsinki.cs.titotrainer.app.admin.request;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;

public class UserStatsViewRequest extends TitoRequest {
    public long courseId;
    public long userId;
}
