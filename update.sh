#!/bin/bash

# 1. Wait for Internet connection
printf "%s" "Waiting for Internet connection..."
while ! timeout 0.2 ping -c 1 -n google.com &> /dev/null
do
    printf "%c" "."
    sleep 1
done
echo $'\n \e[32mConnected\e[39m'

# 2. Start update repository
echo $'\n \e[32mStarting update...\e[39m'
update=$(cd /home/${USER}/avr-ipla-tv-box/ && git pull origin develop)

# 3. Check if something has been updated, if so rebuild the project
if [ "$update" = "Already up-to-date." ];
then
  echo $' \e[32mNothing to update\e[39m'
else
  echo $' \e[32mUpdated and repository is now up-to-date\e[39m'
  echo $'\n \e[32mCompilling sources...\e[39m'
  cd /home/${USER}/avr-ipla-tv-box/ && mvn clean compile package
fi

# 4. Run application
echo $'\n \e[32mRunning application...\e[39m'
java -jar /home/${USER}/avr-ipla-tv-box/target/avr-ipla-tv-box_1.0.0.jar
