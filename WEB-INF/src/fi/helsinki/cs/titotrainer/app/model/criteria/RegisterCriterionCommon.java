package fi.helsinki.cs.titotrainer.app.model.criteria;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * Contains the common parts of {@link RegisterCriterion} and
 * {@link ModelRegisterCriterion} due to Java's lack of
 * multiple inheritance.
 */
final class RegisterCriterionCommon {
    private static final Pattern regNamePattern = Pattern.compile("^[rR]([0-" + (TitokoneState.getNumGeneralRegisters() - 1) + "])$");
    
    static int parseRegNum(String regName) {
        Matcher matcher = regNamePattern.matcher(regName);
        if (!matcher.matches())
            throw new IllegalArgumentException(regName + " received where a register name R0-R" + (TitokoneState.getNumGeneralRegisters() - 1) + " was expected");
        
        return Integer.parseInt(matcher.group(1));
    }
}
