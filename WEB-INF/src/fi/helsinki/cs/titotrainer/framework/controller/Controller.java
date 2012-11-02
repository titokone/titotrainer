package fi.helsinki.cs.titotrainer.framework.controller;

import fi.helsinki.cs.titotrainer.framework.RequestHandler;
import fi.helsinki.cs.titotrainer.framework.request.Request;

public interface Controller<RequestType extends Request> extends RequestHandler<RequestType> {
}
