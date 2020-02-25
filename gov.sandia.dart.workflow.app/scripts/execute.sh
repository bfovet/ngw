#!/bin/bash

source /etc/bashrc
module load sierra

export NO_SUBMIT=true

sierra -j ${num.processors} --no-fct --aprepro --pre ${sierra_code} -i ${input.deck.name}
sierra -j ${num.processors} --no-fct --aprepro --run ${sierra_code} -i ${input.deck.name}
sierra -j ${num.processors} --no-fct --aprepro --post ${sierra_code} -i ${input.deck.name}
