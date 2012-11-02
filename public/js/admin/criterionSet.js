
/*
 * admin.criterionSet is for managing the UIs of sets of criteria for
 * different inputs as well as the global criteria set.
 * 
 * See also admin.criterion, which contains code for managing the
 * individual criteria UIs.
 */

namespace('admin.criterionSet', function() {
    
    var tr = this.tr; // Translations
    
    /*
     * Makes an UI for a CriterionSet.
     */
    function makeUi(inputId) {
        var self = this;
        
        // The toplevel element is a fieldset
        var fieldset = document.createElement('fieldset');
        var legend = document.createElement('legend');
        
        // Criterion objects' UIs go here
        var container = document.createElement('div');
        
        // A select element for selecting a new criterion type to add
        var criterionSelect = document.createElement('select');
        criterionSelect.add(document.createElement('option'), null);
        for (var groupId in admin.criterion.criterionClassGroups) {
            var groups = admin.criterion.criterionClassGroups[groupId];
            
            var optgroup = document.createElement('optgroup');
            optgroup.label = admin.criterion.tr.groups[groupId];
            
            for (var i in groups) {
                var classId = groups[i];
                var criterionClass = admin.criterion.criterionClasses[classId];
                
                var opt = document.createElement('option');
                opt.text = criterionClass.description;
                opt.value = classId;
                $(optgroup).append(opt);
            }
            $(criterionSelect).append(optgroup);
        }
        
        // A button for adding the selected new criterion
        var addCriterionButton = document.createElement('button');
        addCriterionButton.type = 'button';
        $(addCriterionButton).text(tr['add_criterion_button']);
        $(addCriterionButton).click(function() {
            var $selectedOption = $(criterionSelect).find('option:selected');
            
            if ($selectedOption.length > 0 && $selectedOption.attr('value') != '') {
                var classId = $selectedOption.attr('value');
                var ctor = admin.criterion[classId];
                
                var criterion = new ctor('');
                $(criterion.ui).find('.loadDefault').click(); // Load default accept and reject messages
                $(self).trigger('criterionCreated', criterion);
                self.addCriterion(criterion, true);

                criterionSelect.selectedIndex = 0;
                $(criterionSelect).change();
            }
            return false;
        });
        
        // Enable/disable the addCriterionButton as taskCriterionFields changes
        function addCriterionButtonEnablement() {
            $(addCriterionButton).attr('disabled', (this.selectedIndex == 0) ? 'disabled' : '');
            return true;
        }
        $(criterionSelect).change(addCriterionButtonEnablement);
        addCriterionButtonEnablement.apply(criterionSelect);
        
        // Set the element classes
        fieldset.className = 'criterionSet';
        container.className = 'criterionContainer';
        criterionSelect.className = 'newCriterion';
        
        // Put it all together
        $(fieldset).append(legend);
        $(fieldset).append(container);
        $(fieldset).append(document.createElement('br'));
        $(fieldset).append(criterionSelect);
        $(fieldset).append(addCriterionButton);
        
        return fieldset;
    }
    
    /**
     * Creates a set of criteria.
     * 
     * @param inputId The ID of the input for which the criteria are for.
     *                Null for a global set.
     */
    this.CriterionSet = function(inputId) {
        this.inputId = inputId;
        
        this.ui = makeUi.apply(this, inputId);
        
        this.setTitle = function(title) {
            $(this.ui).find('legend').text(title);
        }
        
        this.bindCriterionCreatedCallback = function(handler) {
            $(this).bind('criterionCreated', handler);
        }
        
        this.addCriterion = function(criterion, animate /* = true*/) {
            if (animate === undefined)
                animate = true;
            
            if (animate)
                $(criterion.ui).hide();
            $(this.ui).find('.criterionContainer').append(criterion.ui);
            if (animate)
                $(criterion.ui).show('slow');
        }
    }
    
});
