# LogAnalytics
```shell
#建立文件目录
mkdir /userdata1/server_script/
cd /userdata1/server_script

#获取代码
git clone https://github.com/jialechan/logAnalytics-repo
cd logAnalytics-repo/

#根据具体配置
cp application.yml.template application.yml
vim application.yml
cp nginxConfig.sh.templat nginxConfig.sh
vim nginxConfig.sh

#给予运行权限
chmod 700 LogAnalytics.jar jq LogAnalytics.sh log_daily.sh nginxConfig.sh

#设置定时运行任务
crontab -e
00 01 * * * /userdata1/server_script/logAnalytics-repo/LogAnalytics.sh >> /userdata1/server_script/logAnalytics-repo/cronJob.log

#切换到root，设置nginx定时生成每天日志
su -
crontab -e
00 00 * * * /userdata1/server_script/logAnalytics-repo/log_daily.sh
```
