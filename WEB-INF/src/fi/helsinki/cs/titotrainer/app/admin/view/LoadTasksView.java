package fi.helsinki.cs.titotrainer.app.admin.view;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.LoadTasksViewRequest;
import fi.helsinki.cs.titotrainer.app.misc.SortUtils;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.app.model.fileconv.TaskFileLoader;
import fi.helsinki.cs.titotrainer.app.model.fileconv.TaskFileLoader.Result;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class LoadTasksView extends TitoPageView<LoadTasksViewRequest> {
    
    @Override
    protected String getTemplateName() {
        return "admin/loadtasks.vm";
    }
    
    @Override
    protected void handle(LoadTasksViewRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        Translator translator = this.getTranslator(req);
        final Locale locale = translator.getLocale();
        
        Course course = (Course)hs.get(Course.class, req.courseId);
        if (course == null)
            throw new ErrorResponseException(404, "Course " + req.courseId);
        
        tr.put("course", course);
        
        byte[] data = req.getUserSession().getAttribute("uploadedTaskFile", byte[].class);
        if (data != null) {
            try {
                Document doc = new SAXReader().read(new ByteArrayInputStream(data));
                Collection<Result> results = TaskFileLoader.loadTasksFromXML(doc);
                tr.put("results", results);
                
                // Result index -> new category name
                HashMap<Integer, TString> newCategories = new HashMap<Integer, TString>();
                // Result index -> matching category
                HashMap<Integer, Category> matchingCategories = new HashMap<Integer, Category>();
                {
                    int i = 0;
                    for (Result result : results) {
                        if (result.getCategoryName() != null) {
                            Category matching = result.matchingCategory(course.getCategories(), locale);
                            if (matching != null) {
                                matchingCategories.put(i, matching);
                            } else {
                                newCategories.put(i, result.getCategoryName());
                            }
                        }
                        ++i;
                    }
                }
                
                // Build the <select> options for categories. value -> text
                LinkedHashMap<String, String> categoryOptions = new LinkedHashMap<String, String>();
                categoryOptions.put("", "");
                // Existing categories
                {
                    List<Category> sortedCategories = SortUtils.getSortedList(course.getCategories(), new Comparator<Category>() {
                        @Override
                        public int compare(Category o1, Category o2) {
                            return o1.getName(locale).compareTo(o2.getName(locale));
                        }
                    });
                    for (Category cat : sortedCategories) {
                        categoryOptions.put("" + cat.getId(), cat.getName(locale));
                    }
                }
                // New categories
                for (Entry<Integer, TString> newCat : newCategories.entrySet()) {
                    categoryOptions.put("new" + newCat.getKey(), "(+) " + newCat.getValue().get(locale));
                }
                tr.put("categoryOptions", categoryOptions);
                
                // Make an array of result index -> key of selected category option
                {
                    ArrayList<String> selectedCategories = new ArrayList<String>();
                    int i = 0;
                    for (Result result : results) {
                        if (result.getCategoryName() != null) {
                            Category match = matchingCategories.get(i);
                            if (match != null) {
                                selectedCategories.add("" + match.getId());
                            } else if (newCategories.get(i) != null) {
                                selectedCategories.add("new" + i);
                            }
                        } else {
                            selectedCategories.add("");
                        }
                        ++i;
                    }
                    tr.put("selectedCategories", selectedCategories);
                }
                
            } catch (Exception e) {
                req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_ERROR_CATEGORY, translator.tr("invalid_file"));
                Logger.getLogger(LoadTasksView.class).warn("Error reading uploaded tasks file", e);
            }
        }
    }
    
    public Class<LoadTasksViewRequest> getRequestType() {
        return LoadTasksViewRequest.class;
    };
}
