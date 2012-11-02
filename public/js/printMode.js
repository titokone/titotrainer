
/*
 * Transforms a page to be more printer-friendly.
 * 
 * This transforms form elements into plain text etc.
 */

namespace('printMode', function() {
    
    this.printMode = function(rootElement) {
        var printModeClass = 'printMode';
        
        function applyCommonProperties(original, transformed) {
            transformed.className = printModeClass;
            $(transformed).css('display', $(original).css('display'));
        }
        
        // Remove labels for input and textarea elements that have empty values
        $(rootElement).find('input, textarea').each(function() {
            if (!$(this).val()) {
                $(rootElement).find("label[for='" + this.id + "']").remove();
            }
        });
            
        
        // Transform text inputs to divs
        $(rootElement).find(':text').each(function() {
            var newDiv = document.createElement('div');
            
            $(newDiv).text(this.value);
            
            applyCommonProperties(this, newDiv);
            $(this).replaceWith(newDiv);
        });
        
        // Transform textareas to divs with newlines replaced with <br> tags
        $(rootElement).find('textarea').each(function() {
            var newDiv = document.createElement('div');
            
            var lines = this.value.split('\n');
            for (var i = 0; i < lines.length; ++i) {
                newDiv.appendChild(document.createTextNode(lines[i]));
                newDiv.appendChild(document.createElement('br'));
            }
            
            applyCommonProperties(this, newDiv);
            $(this).replaceWith(newDiv);
        });
        
        // Transform selects to their currently selected options' texts.
        $(rootElement).find('select').each(function() {
            var newDiv = document.createElement('div');
            
            var opt = this.options[this.selectedIndex];
            $(newDiv).text($(opt).text())
            
            applyCommonProperties(this, newDiv);
            $(this).replaceWith(newDiv);
        });
        
        // Checkboxes will be left in but disabled
        $(rootElement).find(':checkbox').each(function() { this.disabled = 'disabled'; });
        
        // Remove buttons and links
        $(rootElement).find('a, :button, :submit').remove();
    };
    
});