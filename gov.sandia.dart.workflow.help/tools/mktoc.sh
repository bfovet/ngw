cd output
for i in *.html; do
printf "<topic label='%s' href='components/"%s"'/>\n" `basename $i .html` $i
done
