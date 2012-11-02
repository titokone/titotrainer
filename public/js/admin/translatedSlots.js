
/**
 * A constructor for a set of translated slots.
 * 
 * The created object has the following public properties:
 * - ui: The toplevel DOM element of the slotset.
 * - slots: An object mapping locale ID to a slot object.
 *          Each slot object has the following properties:
 *          - localeId: The locale identifier of the slot.
 *          - ui: The toplevel DOM element of the slot.
 * - contentFactory: A function to return a DOM element for a slot
 *                   when called with a locale identifier.
 * 
 * @param locales An array of locale identifiers.
 * @param contentFactory A function that, given a locale ID,
 *                       returns a DOM element for a slot.
 */
namespace('admin.translatedSlots').TranslatedSlotSet = function(contentFactory) {
    var tr = namespace('admin.translatedSlots').tr;
    
    var toplevel = document.createElement('table');
    toplevel.className = 'translatedSlotSet';
    
    var row = toplevel.insertRow(0);
    
    function Slot(localeId) {
        var container = row.insertCell(row.cells.length);
        container.className = 'translatedSlot';
        
        var langTitle = document.createElement('span');
        langTitle.className = 'label';
        $(langTitle).text(tr['lang_title']);
        
        var language = document.createElement('div');
        $(language).append(langTitle);
        $(language).append(document.createTextNode(tr.locale[localeId]));
        
        $(container).append(language);
        
        var content = contentFactory(localeId);
        $(container).append(content);
        
        this.localeId = localeId;
        this.ui = container;
    }
    
    this.slots = {};
    
    for (var i = 0; i < APP_CONFIG.supportedLocales.length; ++i) {
        this.slots[APP_CONFIG.supportedLocales[i]] = new Slot(APP_CONFIG.supportedLocales[i]);
    }
    
    this.ui = toplevel;
};
