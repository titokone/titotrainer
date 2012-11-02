namespace('formUtils', function() {
    this.disableForm = function (form) {
        $(form).find(':input').attr('disabled', 'disabled');
        $(form).find(':submit').remove();
    }
    
	this.highlightInvalidFields = function (form, invalidFields) {
		for (var i in invalidFields) {
			var fieldName = invalidFields[i];
			
			var mark = document.createElement('span');
			$(mark).text('*');
			mark.className = 'error';
			
			$field = $(form).find("[name='" + fieldName + "']");
			var $label = $(form).find("label[for='" + $field.attr('id') + "']");
			if ($label.size() > 0) {
				$label.append(mark);
			} else {
				$field.append(mark);
			}
		}
	}
	
	// Returns the value of a form element as it would be sent.
	// The value of an unchecked checkbox is null.
	function elementValue(element) {
	    if (element.type == 'checkbox' && !element.checked)
	        return null;
	    else
	        return $(element).val();
	}
	
	this.snapshotFormData = function (form) {
	    var data = {};
	    $(form).find('input, textarea, select').each(function() {
	        data[this.name] = elementValue(this);
	    });
	    return data;
	}
	
	/**
	 * Returns an array of form element names that have changed compared to a snapshot.
	 * 
	 * This also detects added or deleted elements.
	 */
	this.getChangedFields = function (form, snapshot) {
	    var changed = [];
	    var found = {};
	    // Check for changed or added elements
	    $(form).find('input, textarea, select').each(function() {
	        found[this.name] = true;
            if (elementValue(this) != snapshot[this.name])
                changed.push(this.name);
        });
	    
	    // Check for deleted elements
	    for (var name in snapshot) {
	        if (!found[name]) {
	            changed.push(name);
	        }
	    }
	    
	    return changed;
	}
});