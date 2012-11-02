package fi.helsinki.cs.titotrainer.framework.controller;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.framework.model.TransactionalTask;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.Response;

/**
 * A convenience class for implementing controllers.
 * @param <RequestType> The type of Request subclass expected.
 */
public abstract class AbstractController<RequestType extends Request> implements Controller<RequestType> {
    
    /**
     * Overridden for convenience in {@link AbstractController} to call
     * {@link #handleValid(Request, Session)} if the request is valid and
     * {@link #handleInvalid(Request, Session, Collection)} if the request is
     * invalid.
     * 
     * @param req The request.
     * @return The return value of {@link #handleValid(Request, Session)} or
     *         {@link #handleInvalid(Request, Session, Collection)}.
     */
    @Override
    public Response handle(final RequestType req) throws Exception {
        final Collection<RequestInvalidity> invalidities = req.validate();
        Session hs = req.getAttribs().getHibernateSession();
        
        try {
            if (useTransaction()) {
                return (new TransactionalTask<Response>() {
                    @Override
                    protected Response run(Session hs) throws Exception {
                        if (invalidities.isEmpty())
                            return handleValid(req, hs);
                        else
                            return handleInvalid(req, hs, invalidities);
                    }
                }).invoke(hs);
                
            } else {
                
                if (invalidities.isEmpty())
                    return handleValid(req, hs);
                else
                    return handleInvalid(req, hs, invalidities);
            }
        } catch (ErrorResponseException er) {
            return er.getErrorResponse();
        }
    }
    
    /**
     * <p>
     * Indicates whether {@link #handleValid(Request, Session)}
     * and {@link #handleInvalid(Request, Session, Collection)}
     * should be called in a transaction.
     * </p>
     * 
     * <p>Defaults to true.</p>
     * 
     * @return Whether a transaction should be started by the superclass.
     */
    protected boolean useTransaction() {
        return true;
    }
    
    /**
     * <p>Called by {@link #handle(Request)} if the request is valid.</p>
     * 
     * <p>If {@link #useTransaction()} returns true, this will be called in a transaction.</p>
     * 
     * @param req The valid request.
     * @param hs The hibernate session. This shall be in a transaction.
     * @return A response
     * @throws Exception if something goes wrong.
     */
    protected abstract Response handleValid(RequestType req, Session hs) throws Exception;
    
    /**
     * <p>Called by {@link #handle(Request)} if the request is invalid.</p>
     * 
     * <p>If {@link #useTransaction()} returns true, this will be called in a transaction.</p>
     * 
     * <p>The default implementation in {@link AbstractController} returns
     * an {@link ErrorResponse} with a status code of 404 and a message of
     * "Invalid request arguments."</p>
     * 
     * @param req The invalid request.
     * @param hs The hibernate session. This shall be in a transaction.
     * @param invalidities The results of calling <code>req.validate()</code>.
     * @return A response.
     * @throws Exception if something goes wrong.
     */
    protected Response handleInvalid(RequestType req, Session hs, Collection<RequestInvalidity> invalidities) throws Exception {
        return new ErrorResponse(404, "Invalid request arguments: " + StringUtils.join(invalidities, "; "));
    }
    
}
