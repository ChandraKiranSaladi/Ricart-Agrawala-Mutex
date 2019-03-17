#!/bin/bash

# Change this to your netid
netid=cxs172130

# Root directory of your project in csgrads1 or Dc Machine
PROJDIR=/home/010/c/cx/cxs172130/AOS

# Directory where the config file is located on your local system
CONFIGLOCAL=/home/ck/Downloads/configAOS.txt

# Directory your java classes are in
BINDIR=$PROJDIR/bin

# Your main project class
PROG=InvokeMain

n=0

# Deletes the lines that start with # and the lines that start and end with whitespace
# opens a new terminal for every dc machine line mentioned in the configAOS.txt
cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    echo $i
    while [[ $n -lt $i ]]
    do
    	read line
    	p=$( echo $line | awk '{ print $1 }' )
        host=$( echo $line | awk '{ print $2 }' )
	
	gnome-terminal -e "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $netid@$host cd $PROJDIR;pwd;javac -cp $BINDIR -d $BINDIR ./src/*.java; java -cp $BINDIR $PROG $p; $SHELL" &

        n=$(( n + 1 ))
    done
)