#!/bin/bash

TOOL_CMD=../../../out/host/linux-x86/bin/bundle2installable

# set up directories
mkdir -p output/
mkdir -p output-splits/
mkdir -p output-deliverables/

# clean up
rm -fr output/*
rm -fr output-splits/*
rm -fr output-deliverables/*

echo "--------------------------------------------------------------------"
echo "Generating module zips"
echo "--------------------------------------------------------------------"
$TOOL_CMD generate example/bundle/bundle.zip output
echo "--------------------------------------------------------------------"

for MODULEZIP in `ls output`;
do
  echo "Splitting module: $MODULEZIP"
  echo "--------------------------------------------------------------------"
  $TOOL_CMD split-module output/$MODULEZIP output-splits/
  echo "--------------------------------------------------------------------"
done

echo "Linking deliverable: x86,res-default,gl1"
echo "--------------------------------------------------------------------"
$TOOL_CMD link output-deliverables/deliverable.zip output-splits --modules=module1,module2,module3 --splits=x86,res-default,gl1

