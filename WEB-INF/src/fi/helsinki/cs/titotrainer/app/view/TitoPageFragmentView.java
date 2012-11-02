package fi.helsinki.cs.titotrainer.app.view;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;

/**
 * A superclass for views that render page fragments without the standard header/footer.
 */
public abstract class TitoPageFragmentView<RequestType extends TitoRequest> extends TitoPageViewCommon<RequestType> {
}
