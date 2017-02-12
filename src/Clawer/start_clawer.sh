#bin/bash

TIME_BEGIN=$(date +%F" "%T);
echo "少看本子多开车，开始摸神社啦:                     "$TIME_BEGIN | tee -a /usr/zhongbingyi/time_log.text;
. /usr/zhongbingyi/backup.sh;
. /usr/zhongbingyi/clean.sh;
java -jar /usr/zhongbingyi/MagnetClawer.jar 1>/usr/zhongbingyi/out.log 2>/usr/zhongbingyi/error.log;
. /usr/zhongbingyi/incremental_count.text;
. /usr/zhongbingyi/totalrows.text;
. /usr/zhongbingyi/incremental_list.text;
TIME_END=$(date +%F" "%T);
if [ $count -gt 0 ];
then
echo -e "摸完了，产量很多呢共新增了$count条,总计$total条:        \n新增条目：$list  \n$TIME_END" | tee -a time_log.text;
echo -e "摸完了，产量很多呢共新增了$count条,总计$total条:        \n新增条目：$list  \n$TIME_END" | mail -s "日常摸神社" 349494432@qq.com;
echo "===========================" | tee -a time_log.text;
uuencode resourcelist.json resourcelist.json|mail -s "来不及解释了" 349494432@qq.com
else
echo "woc白摸了这么久:                     "$TIME_END | tee -a time_log.text;
echo "===========================" | tee -a time_log.text;
. /usr/zhongbingyi/recovery.sh;
fi;
