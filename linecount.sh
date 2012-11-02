#!/bin/sh
find WEB-INF/src tests WEB-INF/templates public/js -name "*.java" -or -name "*.vm" -or -name "*.js" | xargs wc -l
