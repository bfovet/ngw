cd ${remote.dir}
source /etc/bashrc

echo submitting to the ${queue} queue
sbatch -N ${num.nodes} --partition=${queue} --time=${job.hours}:${job.minutes}:00 -A ${account} execute.sh 2>dart.id.err | tee dart.id.out

# set some variables
AWK=/usr/bin/awk
exitcode=0

#
# see if we have the job id in the file regardless of any exit code
#  from the job submission script
#
jobid=$(${AWK} '/^Submitted/ { print $NF; }' dart.id.out)
  
  
if [[ -n $jobid ]]; then
  # we found a job id, so we can put into the expected file and set the
  #  exit code
  printf "%s\n" $jobid > dart.id
  exitcode=0
else
  # we didn't find the job id, so let's put the error messages from
  #  the file on stderr and set the exit code
  cat dart.id.err >&2
  exitcode=1
fi
  

rm -f dart.id.{out,err}

exit ${exitcode}
