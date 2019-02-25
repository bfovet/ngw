cd ${remote.dir}
source /etc/bashrc

echo submitting to the ${queue} queue
sbatch -N ${num.nodes} --partition=${queue} --time=${job.hours}:${job.minutes}:00 -A ${account} execute.sh 2>dart.id.err | tee dart.id.out
execerr=$?
suberr=$(cat dart.id.err)
exitcode=0
if [[ ${suberr} == "" &&  ${execerr} == 0 ]]; then
  grep Submitted dart.id.out | awk '{print $NF}' > dart.id
else
  echo "${suberr}" >&2
  exitcode=1
fi

rm -f dart.id.{out,err}
exit ${exitcode}
