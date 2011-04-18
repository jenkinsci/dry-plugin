rm -rf $HUDSON_HOME/plugins/dry*

mvn install
cp -f target/*.hpi $HUDSON_HOME/plugins/
