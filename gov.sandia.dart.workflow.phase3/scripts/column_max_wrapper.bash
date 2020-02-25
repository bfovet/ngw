#!/bin/bash

#
# This is an example bash ExternalProcessNode wrapper script
# for awk that selects a column (whitespace-delimited, for
# now) from a file sends the numeric maximum value in the
# column to its standard output.
# 
# Ports:
#              data - columnar data file
#     column_number - file containing column number
#                     (would be better as a property)
#            stdout - maximum value output
#

##########################################################
#
# This segment goes through the command-line arguments,
# where for each arg of the form "--port_name=file_name",
# it sets the shell variable named "port_name" to equal
# "file_name".
#
for arg in "$@"
do
    case $arg in 
	--*=*)
	argsansdashes="${arg#--}"
	name="${argsansdashes%=*}"
	value="${arg#*=}"
	eval "$name=\$value"
	shift
	;;
	*)
        # other args processed here
	shift    
	;;
    esac
done
##########################################################

n=`cat ${column_number}`

awk "{print \$$n}" < ${data} | sort -n | tail -1
