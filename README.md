# keyvalue


Running parameters for client
------------------------------

format-- 
masterhostip masterport
Eg: this is if you are running master on the same host machine as client
localhost 9000

Running parameters for master
-----------------------------
master accepts no parameters

Running parameters for slave
----------------------------
format--

slaveip slaveport primarydbpath secondarydbpath masterhostip masterport

localhost 9002 C:\\Users\\Milind\\Desktop\\help\\db1.txt C:\\Users\\Milind\\Desktop\\help\\sdb1.txt localhost 9000
