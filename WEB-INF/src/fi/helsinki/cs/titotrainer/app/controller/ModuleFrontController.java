package fi.helsinki.cs.titotrainer.app.controller;

import fi.helsinki.cs.titotrainer.app.request.TitoRequestAttribs;
import fi.helsinki.cs.titotrainer.framework.controller.PrefixRouter;
import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.response.Response;

/**
 * <p>Super class for TitoTrainer's module front controllers.</p>
 * 
 * <p>This superclass invokes {@link TitoRequestAttribs#setModuleFrontController(ModuleFrontController)}.</p>
 */
public abstract class ModuleFrontController extends PrefixRouter {
    
    @Override
    public Response handle(DefaultRequest req) throws Exception {
        TitoRequestAttribs attribs = (TitoRequestAttribs)req.getAttribs();
        attribs.setModuleFrontController(this);
        return super.handle(req);
    }
    
}
