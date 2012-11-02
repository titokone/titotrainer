package fi.helsinki.cs.titotrainer.app.admin.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.AddLoadedTasksRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.fileconv.TaskFileLoader;
import fi.helsinki.cs.titotrainer.app.model.fileconv.TaskFileLoader.Result;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class AddLoadedTasksController extends TitoActionController<AddLoadedTasksRequest> {

    @Override
    protected Response handleValid(AddLoadedTasksRequest req, Session hs) throws Exception {
        byte[] data = req.getUserSession().getAttribute("uploadedTaskFile", byte[].class);
        if (data == null)
            return new ErrorResponse(404, "no uploaded file in memory");
        
        Document doc = new SAXReader().read(new ByteArrayInputStream(data));
        List<Result> results = TaskFileLoader.loadTasksFromXML(doc);
        
        Course course = (Course)hs.get(Course.class, req.courseId);
        if (course == null)
            return new ErrorResponse(404, "course " + req.courseId);
        
        /*
         * Say we are importing multiple tasks with the same new category.
         * We don't want to create the same category twice, so we
         * remember newly created categories here.
         */
        List<Category> createdCategories = new ArrayList<Category>();
        
        // Save each task (i.e. result)
        int tasksAdded = 0;
        int i = 0;
        for (Result result : results) {
            if ("1".equals(req.selected.get("" + i))) {
                Task task = result.getTask();
                
                Category category = null;
                if (req.category.get("" + i) != null) {
                    String categorySpec = req.category.get("" + i);
                    try {
                        if (categorySpec.startsWith("new")) {
                            int resultIndex = Integer.parseInt(categorySpec.substring("new".length()));
                            Result refResult = results.get(resultIndex);
                            TString newCatName = refResult.getCategoryName();
                            
                            // Check if we've already created category like this
                            for (Category ccat : createdCategories) {
                                if (ccat.getName().equals(newCatName)) {
                                    category = ccat;
                                    break;
                                }
                            }
                            
                            if (category == null) {
                                category = new Category(newCatName, course);
                                hs.save(category);
                                course.getCategories().add(category);
                                createdCategories.add(category);
                            }
                            
                        } else {
                            category = (Category)hs.get(Category.class, Long.parseLong(categorySpec));
                        }
                    } catch (NumberFormatException e) {
                    } catch (IndexOutOfBoundsException e) {
                    }
                }
                
                course.getTasks().add(task);
                task.setCourse(course);
                
                if (category != null) {
                    task.setCategory(category);
                    category.getTasks().add(task);
                    hs.update(category);
                }
                
                task.setCreationTime(new Date());
                task.setCreator(req.getUserSession().getAuthenticatedUser());
                
                hs.save(task);
                
                ++tasksAdded;
            }
            ++i;
        }
        
        hs.update(course);
        
        Translator tr = this.getTranslator(req);
        String msg = tr.trp("added_%d_tasks", tasksAdded);
        req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, msg);
        
        req.getUserSession().consumeAttribute("uploadedTaskFile");
        
        return new RedirectResponse("/admin/course?id=" + course.getId());
    }

    @Override
    public Class<AddLoadedTasksRequest> getRequestType() {
        return AddLoadedTasksRequest.class;
    }
    
}
