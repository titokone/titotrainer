/*
 * Encapsulates the UI for editing an input for a task.
 */
namespace('admin.input', function() {
    
    var nextId = makeIdGenerator();
    
    var tr = this.tr; // Translations
    
    function makeUi() {
        var fieldset = document.createElement('fieldset');
        var legend = document.createElement('legend');
        $(legend).text(tr['title']);
        
        var input = document.createElement('input');
        input.type = 'text';
        input.id = 'input_' + this.id;
        input.name = 'input[' + this.id + ']';
        input.className = 'input';
        var inputLabel = document.createElement('label');
        inputLabel.htmlFor = input.id;
        $(inputLabel).text(tr['input_label']);
        
        var inputDiv = document.createElement('div');
        $(inputDiv).append(inputLabel);
        $(inputDiv).append(input);
        
        var secretCheckbox = document.createElement('input');
        secretCheckbox.type = 'checkbox';
        secretCheckbox.id = 'inputSecret_' + this.id;
        secretCheckbox.name = 'inputSecret[' + this.id + ']';
        var secretCheckboxLabel = document.createElement('label');
        secretCheckboxLabel.htmlFor = secretCheckbox.id;
        $(secretCheckboxLabel).text(tr['is_secret']);
        
        var secretDiv = document.createElement('div');
        $(secretDiv).append(secretCheckboxLabel);
        $(secretDiv).append(secretCheckbox);
        
        var deleteButton = document.createElement('button');
        deleteButton.type = 'button';
        $(deleteButton).text(tr['delete_button']);
        $(deleteButton).click(function() {
            $(fieldset).hide('slow', function() {
                $(this).remove();
            });
            return false;
        });
        
        fieldset.className = 'input';
        deleteButton.className = 'delete';
        secretDiv.className = secretCheckbox.className = secretCheckboxLabel.className = 'inputSecret';
        
        $(fieldset).append(legend);
        $(fieldset).append(secretDiv);
        $(fieldset).append(inputDiv);
        $(fieldset).append(document.createElement('br'));
        $(fieldset).append(this.criterionSet.ui);
        $(fieldset).append(deleteButton);
        
        return fieldset;
    }
    
    this.Input = function() {
        var self = this;
        this.id = nextId();
        
        this.criterionSet = new admin.criterionSet.CriterionSet();
        this.criterionSet.bindCriterionCreatedCallback(function(e, criterion) {
            criterion.setInputId(self.id);
        });
        
        this.ui = makeUi.apply(this);
        
        this.setInput = function(inputValue) {
            if (!hasValue(inputValue))
                inputValue = "";
            $(this.ui).find('input.input').attr('value', inputValue);
        }
        
        this.setSecret = function(isSecret) {
            $(this.ui).find('input.inputSecret')[0].checked = (isSecret ? true : false);
        }
    }
    
});
