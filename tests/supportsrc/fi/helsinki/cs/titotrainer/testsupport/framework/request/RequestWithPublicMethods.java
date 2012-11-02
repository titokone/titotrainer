package fi.helsinki.cs.titotrainer.testsupport.framework.request;

import java.util.Collection;

import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;

public class RequestWithPublicMethods extends Request {
    @Override
    public Collection<RequestInvalidity> validateMandatoryFields() {
        return super.validateMandatoryFields();
    }
    
    @Override
    public Collection<RequestInvalidity> validatePositiveAnnotatedFields() {
        return super.validatePositiveAnnotatedFields();
    }
    
    @Override
    public Collection<RequestInvalidity> validateRegexAnnotatedFields() {
        return super.validateRegexAnnotatedFields();
    }
}
