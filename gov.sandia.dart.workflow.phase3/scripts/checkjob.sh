#
# This script attempts to find the state of a specified job. There
# are a number of parameters that could be passed, but this script
# uses:
#    job.id : the id of the job
#    remote.dir : the remote directory where the job was running
#    job.id.filename : the filename that includes the job id
#
# It will return an exit status:
#   0  : script will echo:
#        * The found state from the sacct command
#        * "COMPLETED", under the assmption that the job wasn't found and
#           > 5 minutes since the job submission has elapsed
#        * "" (empty String) : unable to find any information, so
#          implying carry on
#
#  sacct doesn't return any results if pass in a bad job id, such
#    as one that doesn't exist


# outputs the msg from $1 to stdout and stderr without a newline
function err() {
  local msg="$1"
  printf "%s" "${msg}"
  printf "%s" "${msg}" >&2
}

#outputs the msg from $1 to stdout without a newline
function msg() {
  local msg="$1"
  printf "%s" "${msg}"
}

# checks to see if the file is not present or is > 5 minutes old; return
#  1 if either is true, and therefore assume completed,
#  0 otherwise
#
function should_assume_completed() {
  local FL="dart.id"
  
  if [[ !(-f "${FL}") || -n $(find "{$FL}" -min +5) ]]; then
    return 1
  else
    return 0
  fi
}


#
# gather the information on the job
#
jobid=`cat dart.id`

OUTPUT=$(sacct --noheader --jobs=$jobid --format="state%30"| head -1 | sed -e 's/^[[:space:]]*//')



#
# if we didn't get anything back, then the system does not have any
# information about the job. There may be two reasons for this:
#   1. This call has come before the job has had time to be added to the queue
#   2. The job is no longer in the history
#
if [ -z "${OUTPUT}" ]; then
  should_assume_completed
  ac=$?
  if [ $ac == 1 ]; then
    msg "COMPLETED"
  else
    msg ""
  fi
  
  EXITSTATUS=0
else
  msg "${OUTPUT}"
fi
