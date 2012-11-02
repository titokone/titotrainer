
/*
 * admin.criterion contains code for creating and managing
 * the UIs of individual criteria.
 * 
 * Example:
 * <code>
 * var cls = admin.criterion.RegisterCriterion;
 * var c = new cls();
 * </code>
 * 
 * admin.criterion.criterionClasses is a map of criterion class identifiers
 * like "RegisterCriterion" to a record with the following fields:
 * - description: the translated description of the criterion class.
 */

namespace('admin.criterion', function() {
    
    var relations = ["<", "<=", ">", ">=", "=", "!="];
    var availableRegisters = ['R0', 'R1', 'R2', 'R3', 'R4', 'R5', 'R6', 'R7'];
    
    var tr = this.tr; // Translations
    
    // Every new criterion object gets a unique ID.
    var nextId = makeIdGenerator();
    
    // These map criterion class -> locale -> message.
    var defaultAcceptMessages = {};
    var defaultRejectMessages = {};
    
    this.getDefaultAcceptMessage = function(criterionClass, locale) {
        return getNestedObject(defaultAcceptMessages, [criterionClass, locale]);
    }
    this.setDefaultAcceptMessage = function(criterionClass, locale, msg) {
        setNestedObject(defaultAcceptMessages, [criterionClass, locale], msg);
    }
    this.getDefaultRejectMessage = function(criterionClass, locale) {
        return getNestedObject(defaultRejectMessages, [criterionClass, locale]);
    }
    this.setDefaultRejectMessage = function(criterionClass, locale, msg) {
        setNestedObject(defaultRejectMessages, [criterionClass, locale], msg);
    }
    
    
    // Some private utility functions
    function makeFieldset(legendText) {
        var fieldset = document.createElement('fieldset');
        var legend = document.createElement('legend');
        $(legend).text(legendText);
        $(fieldset).append(legend);
        return fieldset;
    }
    
    function makeFormField(labelText, formElement) {
        var container = document.createElement('div');
        var label = document.createElement('label');
        label.htmlFor = formElement.id;
        $(label).text(labelText);
        
        $(container).append(label);
        $(container).append(formElement);
        return container;
    }
    
    function setSelectValue(select, value) {
        for (var i = 0; i < select.options.length; ++i) {
            if (select.options[i].value == value) {
                select.selectedIndex = i;
                $(select).change();
                return;
            }
        }
    }
    
    function makeSelectFromValues(criterionId, namePrefix, values) {
        var select = document.createElement('select');
        select.id = namePrefix + '_' + criterionId;
        select.name = namePrefix + '[' + criterionId + ']';
        
        for (var i = 0; i < values.length; ++i) {
            var opt = document.createElement('option');
            opt.text = values[i];
            opt.value = values[i];
            select.add(opt, null);
        }
        
        return select;
    }
    
    function makeRelationSelect(criterionId) {
        return makeSelectFromValues(criterionId, 'relation', relations);
    }
    
    function makeRegisterSelect(criterionId, namePrefix) {
        return makeSelectFromValues(criterionId, namePrefix, availableRegisters);
    }
    
    /*
     * Makes the skeleton UI for a criterion.
     * 
     * criterionBaseConstructor must have been applied to self.
     * 
     * Calls customPartCallback (if given) with self to
     * create a DOM object with the custom part of the GUI.
     */
    function makeUi(self, customPartCallback) {
        
        function makeAcceptAndRejectMessageBoxes() {
            var TranslatedSlotSet = namespace('admin.translatedSlots').TranslatedSlotSet;
            var slotSet = new TranslatedSlotSet(function(localeId) {
                
                var container = document.createElement('div');
                container.className = 'acceptRejectMessageFields';
                
                var fields = ['accept', 'reject'];
                for (var i = 0; i < fields.length; ++i) {
                    (function () { // For a closure
                        var field = fields[i];
                        var msgArea = document.createElement('textarea');
                        msgArea.id = field + 'Msg_' + self.id + '_' + localeId;
                        msgArea.name = field + 'Msg[' + self.id + '][' + localeId + ']';
                        msgArea.className = field + 'Msg locale_' + localeId;
                        msgArea.rows = 3;
                        msgArea.cols = 30;
                        var msgField = makeFormField(tr[field + '_msg_label'], msgArea);
                        $(container).append(msgField);
                        
                        var defaultMsgLink = document.createElement('a');
                        defaultMsgLink.href = '#';
                        defaultMsgLink.className = 'loadDefault function';
                        $(defaultMsgLink).text(tr['default_msg_link']);
                        $(defaultMsgLink).click(function() {
                            try {
                                var getter;
                                if (field == 'accept')
                                    getter = admin.criterion.getDefaultAcceptMessage;
                                else if (field == 'reject')
                                    getter = admin.criterion.getDefaultRejectMessage;
                                else
                                    debug('wtf?');
                                
                                msgArea.value = withDefault(getter(self.type, localeId), '');
                            } catch (e) {
                                debug(e);
                            }
                            return false;
                        });
                        
                        var defaultMsgLinkContainer = document.createElement('div');
                        defaultMsgLinkContainer.className = 'loadDefault function';
                        $(defaultMsgLinkContainer).append(defaultMsgLink);
                        $(container).append(defaultMsgLinkContainer);
                    })();
                }
                
                return container;
            });
            
            return slotSet.ui;
        }
        
        
        var title = tr[self.type] ? tr[self.type]['title'] : '<untranslated: ' + self.type + '>';
        
        var container = makeFieldset(title);
        
        var criterionType = document.createElement('input');
        criterionType.type = 'hidden';
        criterionType.id = 'criterionType_' + self.id;
        criterionType.name = 'criterionType[' + self.id + ']';
        criterionType.className = 'criterionType';
        criterionType.value = self.type;
        
        $(container).append(criterionType);
        
        var inputId = document.createElement('input');
        inputId.type = 'hidden';
        inputId.id = 'inputId_' + self.id;
        inputId.name = 'inputId[' + self.id + ']';
        inputId.className = 'inputId';
        
        $(container).append(inputId)
        
        var isQualityCriterion = document.createElement('input');
        isQualityCriterion.type = 'checkbox';
        isQualityCriterion.id = 'isQualityCriterion_' + self.id;
        isQualityCriterion.name = 'isQualityCriterion[' + self.id + ']';
        isQualityCriterion.className = 'isQualityCriterion';
        var isQualityCriterionLabel = document.createElement('label');
        $(isQualityCriterionLabel).attr('for', isQualityCriterion.id);
        $(isQualityCriterionLabel).text(tr['is_quality_criterion']);
        var isQualityCriterionDiv = document.createElement('div');
        isQualityCriterionDiv.className = 'isQualityCriterion';
        $(isQualityCriterionDiv).append(isQualityCriterionLabel);
        $(isQualityCriterionDiv).append(isQualityCriterion);
        
        $(container).append(isQualityCriterionDiv);
        
        if (typeof(customPartCallback) == 'function') {
            var customPart = customPartCallback.apply(self);
            $(customPart).addClass('criterionCustomPart');
            $(container).append(customPart);
        }
        
        $(container).append(makeAcceptAndRejectMessageBoxes());
        
        var deleteButton = document.createElement('button');
        deleteButton.type = 'button';
        deleteButton.className = 'delete';
        $(deleteButton).text(tr['delete_button']);
        
        $(container).append(deleteButton);
        
        $(deleteButton).click(function() {
            $(container).hide('slow', function() {
                $(this).remove();
            });
            return false;
        });
        
        return container;
    }
    
    /**
     * RegisterCriterion, SymbolCriterion and subclasses of
     * MetricCriterion have commonalities. This encapsulates the common parts of
     * their UIs.
     * 
     * @param self The criterion object being constructed.
     * @param initialParams The initial parameters given.
     * @param leftUi A DOM element for the UI of the left value.
     *               May be omitted if there is no left parameter.
     * @param leftSetter A function taking leftUi and the initial left value
     *                   (which matches leftRegex). This may be called to set the
     *                   initial value of leftUi. May be omitted if leftUi is omitted.
     */
    function makeConstantComparisonUi(self, initialParams, leftUi, leftSetter) {
        return makeUi(self, function() {
            var container = document.createElement('div');
            
            $(container).append(leftUi);
            
            var relationSelect = makeRelationSelect(self.id);
            $(container).append(relationSelect);
            
            var rightParamInput = document.createElement('input');
            rightParamInput.type = 'text';
            rightParamInput.name = 'rightParam[' + self.id + ']';
            rightParamInput.className = 'rightParam';
            
            $(container).append(rightParamInput);
            
            // Parse initialParams
            if (initialParams != null) {
                initialParams = $.trim(initialParams);
                
                var parts = initialParams.split(/\s+/);
                var leftValue = null,
                    relation = null,
                    rightParam = null;

                if (leftUi) {
                    if (parts.length > 0)
                        leftValue = parts[0];
                    if (parts.length > 1)
                        relation = parts[1];
                    if (parts.length > 2)
                        rightParam = parts[2];
                } else {
                    if (parts.length > 0)
                        relation = parts[0];
                    if (parts.length > 1)
                        rightParam = parts[1];
                }
                
                if (leftValue && leftUi && typeof(leftSetter) == 'function') {
                    leftUi.className = 'leftParam';
                    leftSetter(leftUi, leftValue);
                }
                
                if (relation)
                    setSelectValue(relationSelect, relation);
                if (rightParam)
                    rightParamInput.value = rightParam;
            }
            
            return container;
        });
    }
    
    
    function makeMetricCriterionUi(self, initialParams) {
        return makeConstantComparisonUi(self, initialParams);
    }
    
    
    function makeInstructionSetUi(self, initialParams) {
        return makeUi(self, function() {
            var container = document.createElement('div');
            
            var opcodes = APP_CONFIG.validOpcodes;
            
            var opcodeInput = document.createElement('input');
            opcodeInput.type = 'text';
            opcodeInput.value = initialParams;
            opcodeInput.name = 'params[' + this.id + ']';
            opcodeInput.className = 'opcodeInput';
            $(container).append(makeFormField(tr['comma_separated_instruction_set'], opcodeInput));
            
            var validityMessage = document.createElement('span');
            validityMessage.className = 'warning';
            $(validityMessage).fadeOut();
            $(opcodeInput).after(validityMessage);
            
            function checkValidity() {
                // Show the validityMessage warning if the list contains bad opcodes
                var parts = opcodeInput.value.split(/,/);
                
                var badOpcodes = [];
                
                for (var i = 0; i < parts.length; ++i) {
                    var part = $.trim(parts[i]).toUpperCase();
                    if (part.length > 0 && $.inArray(part, opcodes) == -1)
                        badOpcodes.push(part);
                }
                
                if (badOpcodes.length > 0) {
                    $(validityMessage).text(' ' + tr['invalid_opcodes'] + ' ' + badOpcodes.join(', '));
                    $(validityMessage).fadeIn('normal');
                } else {
                    $(validityMessage).fadeOut('fast');
                }
            }
            
            $(opcodeInput).keyup(checkValidity);
            $(opcodeInput).blur(checkValidity);
            checkValidity();
            
            return container;
        });
    }
    
    function makeModelComparisonUi(self, initialParams, leftUi, leftSetter) {
        return makeUi(self, function() {
            var container = document.createElement('div');
            
            if (!leftUi) {
                leftUi = document.createElement('input');
                leftUi.type = 'text';
                leftUi.name = 'leftParam[' + this.id + ']';
            }
            
            if (typeof(leftSetter) != 'function') {
                leftSetter = function (lui, lval) {
                    lui.value = lval;
                }
            }
            
            leftUi.className = 'leftParam';
            $(container).append(leftUi);
            
            var relationSelect = makeRelationSelect(self.id);
            $(container).append(relationSelect);
            
            var parts = initialParams.split(/\s+/);
            if (parts.length > 0) {
                leftSetter(leftUi, parts[0]);
            } if (parts.length > 1)
                setSelectValue(relationSelect, parts[1]);
            
            return container;
        });
    }
    
    
    // Criterion classes...
    
    function criterionBaseConstructor(type) {
        this.id = nextId();
        this.type = type;
        
        function setAcceptRejectMessage(which, locale, msg) {
            $(this.ui).find('.' + which + 'Msg.locale_' + locale).val(msg);
        }
        
        this.setAcceptMessage = function(locale, msg) {
            setAcceptRejectMessage.apply(this, ['accept', locale, msg]);
        }
        
        this.setRejectMessage = function(locale, msg) {
            setAcceptRejectMessage.apply(this, ['reject', locale, msg]);
        }
        
        this.setInputId = function(inputId) {
            $(this.ui).find('input.inputId').val(String(inputId));
        }
        
        this.setQualityCriterion = function (isQualityCriterion) {
            $(this.ui).find('input.isQualityCriterion')[0].checked = isQualityCriterion ? true : false;
        }
        
        this.showWarning = function() {
            var warningImg = document.createElement('img');
            warningImg.className = 'smallWarning';
            warningImg.alt = '!';
            warningImg.src = APP_CONFIG.basePath + '/public/img/big_warning.png';
            $(this.ui).prepend(warningImg);
        }
    }
    
    this.CodeSizeCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['CodeSizeCriterion']);
        
        this.ui = makeMetricCriterionUi(this, initialParams);
    }
    
    this.DataReferencesCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['DataReferencesCriterion']);
        
        this.ui = makeMetricCriterionUi(this, initialParams);
    }
    
    this.DataSizeCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['DataSizeCriterion']);
        
        this.ui = makeMetricCriterionUi(this, initialParams);
    }
    
    this.ExecutedInstructionsCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['ExecutedInstructionsCriterion']);
        
        this.ui = makeMetricCriterionUi(this, initialParams);
    }
    
    this.ForbiddenInstructionsCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['ForbiddenInstructionsCriterion']);
        
        this.ui = makeInstructionSetUi(this, initialParams);
    }
    
    this.MaxStackSizeCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['MaxStackSizeCriterion']);
        
        this.ui = makeMetricCriterionUi(this, initialParams);
    }
    
    this.MemoryReferencesCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['MemoryReferencesCriterion']);
        
        this.ui = makeMetricCriterionUi(this, initialParams);
    }
    
    this.ModelRegisterCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['ModelRegisterCriterion']);
        
        var registerSelect = makeRegisterSelect(this.id, 'leftParam');
        
        this.ui = makeModelComparisonUi(
                this,
                initialParams,
                registerSelect,
                function(leftUi, leftValue) { setSelectValue(leftUi, leftValue); }
        );
    }
    
    this.ModelScreenOutputCriterion = function() {
        criterionBaseConstructor.apply(this, ['ModelScreenOutputCriterion']);
        
        this.ui = makeUi(this);
    }
    
    this.ModelSymbolCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['ModelSymbolCriterion']);
        
        this.ui = makeModelComparisonUi(this, initialParams);
    }
    
    this.RegisterCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['RegisterCriterion']);
        
        var registerSelect = makeRegisterSelect(this.id, 'leftParam');
        
        this.ui = makeConstantComparisonUi(
                this,
                initialParams,
                registerSelect,
                function(leftUi, leftValue) { setSelectValue(leftUi, leftValue); }
        );
    };
    
    this.RequiredInstructionsCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['RequiredInstructionsCriterion']);
        
        this.ui = makeInstructionSetUi(this, initialParams);
    }
    
    this.ScreenOutputCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['ScreenOutputCriterion']);
        
        this.ui = makeUi(this, function() {
            var input = document.createElement('input');
            input.type = 'text';
            input.name = 'params[' + this.id + ']';
            if (hasValue(initialParams))
                input.value = initialParams;

            return makeFormField(tr[this.type]['expected_screen_output'], input);
        });
    }
    
    this.SymbolCriterion = function(initialParams) {
        criterionBaseConstructor.apply(this, ['SymbolCriterion']);
        
        var symbolInput = document.createElement('input');
        symbolInput.type = 'text';
        symbolInput.name = 'leftParam[' + this.id + ']';
        
        this.ui = makeConstantComparisonUi(
                this,
                initialParams,
                symbolInput,
                function(leftUi, leftValue) { leftUi.value = leftValue; }
                );
    }
    
    
    this.criterionClasses = [];
    for (var c in this) {
        if (/Criterion$/.test(c)) {
            this.criterionClasses[c] = {
                description: tr[c] ? tr[c]['title'] : '<untranslated: ' + c + '>'
            };
        }
    }
    
    this.criterionClassGroups = {
        comparisonWithPredefinedValue: ['RegisterCriterion',
                                        'ScreenOutputCriterion',
                                        'SymbolCriterion'],
        comparisonWithModelSolution: ['ModelRegisterCriterion',
                                      'ModelScreenOutputCriterion',
                                      'ModelSymbolCriterion'],
        instructions: ['RequiredInstructionsCriterion',
                       'ForbiddenInstructionsCriterion'],
        efficiency: ['CodeSizeCriterion',
                     'MaxStackSizeCriterion',
                     'DataSizeCriterion',
                     'DataReferencesCriterion',
                     'ExecutedInstructionsCriterion',
                     'MemoryReferencesCriterion'],
    };
    
    
});

