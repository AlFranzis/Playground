
#Build dcm4che source JARs:
mvn source:jar

# install dcm4che-core into local Maven repo:
mvn install:install-file 
-Dfile=/home/alex/dev/git-repos/Playground/al.franzis.lucene.header.extract/lib/dcm4che-core-2.0.25.jar 
-DgroupId=dcm4che 
-DartifactId=dcm4che-core 
-Dsources=/home/alex/dev/git-repos/Playground/al.franzis.lucene.header.extract/lib/dcm4che-core-2.0.25-sources.jar 
-Dversion=2.0.25 
-Dpackaging=jar

# install dcm4che-iod into local Maven repo:
mvn install:install-file 
-Dfile=/home/alex/dev/git-repos/Playground/al.franzis.lucene.header.extract/lib/dcm4che-iod-2.0.25.jar 
-DgroupId=dcm4che 
-DartifactId=dcm4che-iod 
-Dsources=/home/alex/dev/git-repos/Playground/al.franzis.lucene.header.extract/lib/dcm4che-iod-2.0.25-sources.jar 
-Dversion=2.0.25 
-Dpackaging=jar

