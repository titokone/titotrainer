package fi.helsinki.cs.titotrainer.app.view.template;

import java.lang.reflect.Modifier;

import fi.helsinki.cs.titotrainer.app.controller.ModuleFrontController;
import fi.helsinki.cs.titotrainer.app.model.AbstractTitoEntity;
import fi.helsinki.cs.titotrainer.app.model.access.CurrentCredentials;
import fi.helsinki.cs.titotrainer.app.model.access.ModelAccessController;
import fi.helsinki.cs.titotrainer.framework.RequestHandler;
import fi.helsinki.cs.titotrainer.framework.access.Role;
import fi.helsinki.cs.titotrainer.framework.model.handler.ModelPermission;

/**
 * <p>Checks whether the current user can do certain things like invoke a given URL path.</p>
 * 
 * @see CurrentCredentials#getCurrentUser()
 */
public class Can {

    private static final Package MODEL_PACKAGE = AbstractTitoEntity.class.getPackage();
    
    private ModuleFrontController frontController;
    
    public Can(ModuleFrontController frontController) {
        this.frontController = frontController;
    }
    
    private Role getCurrentRole() {
        return CurrentCredentials.getCurrentUser().getParentRole();
    }
    
    /**
     * Checks whether the current user can access the request handler returned by
     * a front controller for a certain path.
     */
    public boolean accessPath(String localPath) {
        RequestHandler<?> handler = frontController.getHandlerForPrefix(localPath);
        if (handler != null)
            return frontController.getAccessController().hasAccess(getCurrentRole(), handler);
        else
            return false;
    }
    
    private Object mockEntity(String name) {
        String fullName = MODEL_PACKAGE.getName() + "." + name;
        
        try {
            Class<?> cls = Class.forName(fullName);
            int mod = cls.getModifiers();
            if (Modifier.isAbstract(mod) || Modifier.isInterface(mod))
                throw new IllegalArgumentException("Model class " + name + " is not concrete");
            
            try {
                return cls.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to construct entity " + name + " with default constructor.");
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Model class " + name + " not found", e);
        }
    }
    
    private boolean checkPerm(Object entity, ModelPermission perm) {
        if (entity instanceof String) { // A common mistake
            String op = perm.toString().toLowerCase();
            String msg = "Can." + op + "() takes an entity, not an entity name. Did you mean Can." + op + "Any()?";
            throw new IllegalArgumentException(msg);
        }
        return ModelAccessController.getInstance().hasPermission(getCurrentRole(), entity, perm);
    }
    
    /**
     * <p>Checks whether the current user may create an entity with a given class name
     * unconditionally (i.e. without restrictions on the entity's properties).</p>
     * 
     * <p>To be precise, checks whether the access controller would allow saving
     * a fake instance of the entity created through the default constructor.</p>
     */
    public boolean createAny(String entityName) {
        return checkPerm(mockEntity(entityName), ModelPermission.CREATE);
    }
    
    public boolean update(Object entity) {
        return checkPerm(entity, ModelPermission.UPDATE);
    }
    
    public boolean updateAny(String entityName) {
        return update(mockEntity(entityName));
    }
    
    public boolean delete(Object entity) {
        return checkPerm(entity, ModelPermission.DELETE);
    }
    
    public boolean deleteAny(String entityName) {
        return delete(mockEntity(entityName));
    }
    
}
