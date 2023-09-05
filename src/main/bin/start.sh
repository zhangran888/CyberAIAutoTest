#!/bin/bash
cd `dirname $0`
echo `pwd`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf
LOG_DIR=$DEPLOY_DIR/logs

if [ ! -d $LOG_DIR ]; then
mkdir $LOG_DIR
fi

#删除30天以上的日志文件
num=`ls $LOG_DIR| wc -l`
if [ $num -gt 30 ]; then
ls $LOG_DIR | sort | sed -n '1p' | xargs rm
fi

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

#echo "CONF_DIR:"
#echo $CONF_DIR

#echo "LIB_JARS:"
#echo $LIB_JARS ``

time=`date "+%Y%m%d"`

if [[ -n $1 ]];then
nohup java -DnewTestngEmailableReporter=true -classpath $CONF_DIR:$LIB_JARS org.testng.TestNG $xml $CONF_DIR/suite/$1 | tee -a $LOG_DIR/stdout_$time.log
else
for i in `ls $CONF_DIR/suite/*.xml`; do
#echo "i的值为$i"
xml="$xml $i"
#echo $xml
done
echo "要执行的xml文件为：$xml"
nohup java  -DnewTestngEmailableReporter=true -classpath $CONF_DIR:$LIB_JARS org.testng.TestNG -suitethreadpoolsize 10 $xml  | tee -a $LOG_DIR/stdout_$time.log
fi

#sleep 10

#nohup java  -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=5005,suspend=n,server=y -DnewTestngEmailableReporter=true -classpath $CONF_DIR:$LIB_JARS org.testng.TestNG -suitethreadpoolsize 10 -verbose 5 $xml  | tee -a $LOG_DIR/stdout_$time.log
