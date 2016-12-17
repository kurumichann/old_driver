#bin/bash

TIME_BEGIN=$(date +%F" "%T);
echo "少看本子多开车，开始摸神社啦:"$TIME_BEGIN | tee -a /usr/zhongbingyi/time_log.text;
. /usr/zhongbingyi/backup.sh;
java -jar /usr/zhongbingyi/MagnetClawer.jar 1>/usr/zhongbingyi/out.log 2>/usr/zhongbingyi/error.log;
. /usr/zhongbingyi/incremental_count.text;
TIME_END=$(date +%F" "%T);
if [ $count -gt 0 ];
then
echo "摸完了，产量很多呢:共新增了"$count"条:"$TIME_END | tee -a time_log.text;
echo "===========================" | tee -a time_log.text;
else
echo "woc白摸了这么久"$TIME_END | tee -a time_log.text;
echo "===========================" | tee -a time_log.text;
fi;
