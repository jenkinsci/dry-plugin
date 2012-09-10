rm -rf $JENKINS_HOME/plugins/dry*

mvn install
cp -f target/*.hpi $JENKINS_HOME/plugins/
