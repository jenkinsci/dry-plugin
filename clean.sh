rm -rf $JENKINS_HOME/plugins/dry*

mvn clean install
cp -f target/dry.hpi $JENKINS_HOME/plugins/

cd $JENKINS_HOME
./go.sh
