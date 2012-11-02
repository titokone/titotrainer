
/*
 * This file defines common functionality and hacks.
 * It is not for library functions.
 */

/*
 * Add a short date parser to tablesorter for the "dd.MM.yyyy HH:mm" date format
 * (with or without the time part).
 */
(function() {
	var pattern = /(\d?\d)\.(\d?\d)\.(\d?\d?\d\d)\s+(\d?\d:\d?\d)?/;

    $.tablesorter.addParser({ 
        id: 'titoTrainerEuropeanShortDateTime', 
        is: function(s) {
            return pattern.test(s);
        }, 
        format: function(s) { 
            var matches = pattern.exec(s);
            // We'll return "yyyyMMdd(HH:mm)", which can be sorted by textual comparison
            var datepart = matches[3] + matches[2] + matches[1];
            if (matches.length == 5) { // Has time part
            	return datepart + matches[4];
            } else {
            	return datepart;
            }
        }, 
        type: 'text'
    }); 
})();

/*
 * Tablesorter crashes on an empty table,
 * so we'll intercept and disable it in that case.
 */
(function() {
    var origFunc = $.fn.tablesorter;
    $.fn.tablesorter = function() {
        if (this.find('tbody tr').size() > 0)
            return origFunc.apply(this, arguments);
        else
            return this;
    };
})();

/*
 * A plugin for tablesorter that allows attaching
 * secondary rows to the main (sorted) rows of a table so.
 */
(function() {
    var origEntrypoint = $.fn.tablesorter;
    $.fn.tablesorter = function() {
        this.find('tr.secondary').each(function() {
            var pred = $(this).prev('tr')[0];
            this.primaryPredecessor = pred;
        });
        origEntrypoint.apply(this, arguments);
    };
    
    $.tablesorter.addWidget({
        id: 'secondaryRows',
        format: function(table) {
            $('tr.secondary').each(function() {
                if (this.primaryPredecessor) {
                    $(this).insertAfter(this.primaryPredecessor);
                }
            });
        }
    });
})();
