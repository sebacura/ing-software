#!/bin/bash

filename='./files.txt'
n=1
while read line; do
# reading each line
#echo "Line No. $n : $line"

rm -rf "$line"
n=$((n+1))
done < $filename

