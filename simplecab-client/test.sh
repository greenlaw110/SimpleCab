#!/bin/bash
echo get trips of C3DF175374EA2E41AFC6F145B287E9D4 on 2013-12-31
java -jar target/simplecab-client-1.0-SNAPSHOT-jar-with-dependencies.jar -m C3DF175374EA2E41AFC6F145B287E9D4 -u 2013-12-31
echo
echo get trips of C3DF175374EA2E41AFC6F145B287E9D4 on 2013-12-31 with no-cache
java -jar target/simplecab-client-1.0-SNAPSHOT-jar-with-dependencies.jar -m C3DF175374EA2E41AFC6F145B287E9D4 -u 2013-12-31 --no-cache
echo
echo get all trips of  C3DF175374EA2E41AFC6F145B287E9D4 and BCEB2F048FCA6F2DB11E275E6B892E15
java -jar target/simplecab-client-1.0-SNAPSHOT-jar-with-dependencies.jar -m C3DF175374EA2E41AFC6F145B287E9D4,BCEB2F048FCA6F2DB11E275E6B892E15