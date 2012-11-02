package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Map;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.request.Optional;

/**
 * Contains the common parts of {@link CreateTaskRequest} and
 * {@link UpdateTaskRequest}.
 */
public abstract class AbstractTaskModificationRequest extends TitoRequest {
    
    @Optional
    public Long categoryId;
    
    @Optional
    public Map<String, String> title;
    @Optional
    public Map<String, String> description;
    
    public boolean hidden;
    public int difficulty;
    public int maxSteps;
    
    @Optional
    public String type;
    
    @Optional
    public String preCode;
    @Optional
    public String postCode;
    
    @Optional
    public String modelSolution;
    
    @Optional
    public Boolean safeUpdate;
    
    // Inputs:
    @Optional
    public Map<String, String> input;
    @Optional
    public Map<String, String> inputSecret;
    
    // Criteria:
    @Optional
    public Map<String, String> criterionType;
    @Optional
    public Map<String, String> isQualityCriterion;
    
    // criterion_index -> locale -> message.
    @Optional
    public Map<String, Map<String, String>> acceptMsg;
    @Optional
    public Map<String, Map<String, String>> rejectMsg;

    @Optional
    public Map<String, String> inputId; // The input index for non-global criteria
    
    @Optional
    public Map<String, String> leftParam;
    @Optional
    public Map<String, String> relation;
    @Optional
    public Map<String, String> rightParam;
    
    @Optional
    public Map<String, String> params;
}
