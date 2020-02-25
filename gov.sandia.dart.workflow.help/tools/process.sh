rm -rf output scratch 
mkdir -p output scratch

perl -pi -e 's/ri://g' entities.xml

java -cp saxon/\* net.sf.saxon.Transform -s:entities.xml -xsl:extractObjects.xsl  -o:objects.xml

perl -pi -e 's/ac://g' objects.xml
perl -pi -e 's/&nbsp;/ /g' objects.xml
perl -pi -e 's/&ndash;/-/g' objects.xml
perl -pi -e 's/&rsquo;/-/g' objects.xml
perl -pi -e 's/&lsquo;/-/g' objects.xml
perl -pi -e 's/&rdquo;/-/g' objects.xml
perl -pi -e 's/&ldquo;/-/g' objects.xml

java -cp saxon/\* net.sf.saxon.Transform -s:objects.xml -xsl:convertHtml.xsl 

rm scratch/Workflow\ Doc*

for cmd in `find scratch -name '*.html' |  sed 's/[-0123456789]//g' | sed 's/.html//g' | sed 's@scratch/@@g' | sort | uniq` ; do
    name=`ls scratch/$cmd* | sort -n | head -1`
    cp ${name} output/${cmd}.html
done
