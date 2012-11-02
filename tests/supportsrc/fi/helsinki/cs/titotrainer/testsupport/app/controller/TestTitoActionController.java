package fi.helsinki.cs.titotrainer.testsupport.app.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class TestTitoActionController extends TitoActionController<TestTitoRequest> {

    @Override
    protected Response handleValid(TestTitoRequest req, Session hs) throws Exception {
        return new ErrorResponse(404);
    }

    @Override
    public Class<TestTitoRequest> getRequestType() {
        return TestTitoRequest.class;
    }
    
}
