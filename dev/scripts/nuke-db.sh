#!/bin/bash

cd `dirname $0`

for i in `seq 1 3 | sort -r`; do
    echo -n "${i}..."
    sleep 1s
done
echo "0"

echo
echo "Dropping schema..."
./runtool.sh SchemaTool exec drop

echo
echo "Creating schema..."
./runtool.sh SchemaTool exec create

echo
echo "Inserting test data..."
./runtool.sh InsertTestDbData

