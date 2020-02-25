#!/bin/bash
checkFilename=${input.deck.base.name}.log
resultFilename="job.props"

function printResult(){
    if [ $# -eq 0 ] ; then
        return
    fi

    if [ -e $resultFilename ] ; then
        rm $resultFilename
    fi

    line="job.results.status=$1"
    echo "$line" > $resultFilename
    echo $1
}

successString="SIERRA execution successful"
failedString="SIERRA execution failed"

if [ -e $checkFilename ] ; then
    if (grep -q "$successString" $checkFilename) then
        printResult "Successful"
    else
        if (grep -q "$failedString" $checkFilename) then
            printResult "Failed"
        else
            printResult "Undefined"
        fi
    fi
else
    printResult "Undefined"
fi
