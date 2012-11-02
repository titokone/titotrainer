#!/bin/bash

cd `dirname $0`/../../

if [ ! -f classpath.txt ]; then
    echo "classpath.txt not found. Build the project using ant first please."
    exit 1
fi

CLASSDIR="tools/classes"
TOOLPKG="fi.helsinki.cs.titotrainer.app.tools"
TOOLDIR="${CLASSDIR}/`echo $TOOLPKG | sed 's/\./\//g'`"

if [ ! -d "$TOOLDIR" ]; then
    echo "Tried to find tools in $TOOLDIR but that directory doesn't exist"
    exit 2
fi

if [ -z "$1" ]; then
    OLDPWD=`pwd`
    cd "$TOOLDIR"
    echo "Available tools:"
    find -name "*.class" | grep -v '\$' | sed 's/\.class$//' | sed 's/^\.\///' | sed 's/^/ * /'
    cd "$OLDPWD"
    exit 0
fi

exec java -cp "`cat classpath.txt`" -ea $TOOLPKG.$@
