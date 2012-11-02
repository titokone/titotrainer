
/*
 * Miscellaneous utility functions.
 */

/**
 * Writes the object to Firebug's console if Firebug is installed.
 */
function debug(obj) {
    if (typeof(window.loadFirebugConsole) == 'function')
        window.loadFirebugConsole();
    if (window.console && window.console.log) {
        window.console.log(obj);
    }
}

/**
 * An abbreviation for not null and not undefined
 */
function hasValue(x) {
    return x !== undefined && x !== null;
}

/**
 * Returns x if it is not null and not undefined,
 * otherwise returns def.
 */
function withDefault(x, def) {
    return hasValue(x) ? x : def;
}

/**
 * Returns obj.keys[0].keys[1].....keys[keys.length-1] unless
 * one of the intermediate keys is unset. If one of the keys
 * is unset or null, returns null.
 */
function getNestedObject(obj, keys) {
    var current = obj;
    for (var i = 0; i < keys.length; ++i) {
        if (hasValue(current[keys[i]]))
            current = current[keys[i]];
        else
            return null;
    }
    return current;
}

/**
 * Takes an object, an array of keys and a value.
 * Assigns value to obj.keys[0].keys[1].....keys[keys.length-1]
 * making sure each subobject exists.
 */
function setNestedObject(obj, keys, value) {
    var current = obj;
    for (var i = 0; i < keys.length - 1; ++i) {
        if (typeof(current[keys[i]]) == 'undefined') {
            current[keys[i]] = {};
        }
        current = current[keys[i]];
    }
    var lastKey = keys[keys.length - 1];
    
    current[lastKey] = value;
}

/**
 * Opens a namespace.
 * 
 * Takes any number of string arguments and returns
 * window.arg1.arg2.arg3.....argN,
 * creating the chain of nested objects if needed.
 * 
 * Also works with a dot-delimited single string argument.
 * 
 * If the final argument is a function, it is executed with the
 * namespace as 'this' and its return value is returned.
 * Otherwise the namespace is returned.
 */
function namespace() {
    var args = []; // arguments as an Array
    for (var i = 0; i < arguments.length; ++i)
        args[i] = arguments[i];

    var func = null;
    if (args.length > 0 && typeof(args[args.length - 1]) == 'function') {
        func = args.pop();
    }

    if (args.length == 1)
        args = args[0].split('.');

    var hash = window;
    for (var i = 0; i < args.length; ++i) {
        if (typeof(hash[args[i]]) == 'undefined') {
            hash[args[i]] = {};
        }
        hash = hash[args[i]];
    }

    if (func)
        return func.apply(hash);
    else
        return hash;
}

/**
 * Returns a function that returns an incrementing
 * number on each call.
 */
function makeIdGenerator(firstValue /* = 0*/) {
    if (!firstValue)
        firstValue = 0;
    var counter = firstValue;
    return function() {
        return counter++;
    }
}

/**
 * Upper-cases the first character of a string.
 */
function ucfirst(str) {
    return str.substr(0, 1).toUpperCase() + str.substr(1);
}

/**
 * Lower-cases the first character of a string.
 */
function lcfirst(str) {
    return str.substr(0, 1).toLowerCase() + str.substr(1);
}
