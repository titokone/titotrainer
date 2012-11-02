package fi.helsinki.cs.titotrainer.framework.request.coercer;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.misc.None;
import fi.helsinki.cs.titotrainer.framework.misc.Some;

/**
 * Places a file upload into a request.
 */
public class FileItemCoercer implements FieldCoercer<FileItem> {
    
    @Override
    public Maybe<FileItem> coerce(String field, Map<String, ?> params, Annotation[] annotations) {
        Object o = params.get(field);
        if (o instanceof FileItem) {
            return new Some<FileItem>((FileItem)o);
        } else {
            return new None<FileItem>();
        }
    }

    @Override
    public Class<FileItem> getResultType() {
        return FileItem.class;
    }
    
}
