#!/bin/bash

# Path to the auth.log
AUTH_LOG="/var/log/auth.log"

#Path to jail.local
JAIL_LOCAL_PATH="/etc/fail2ban/jail.local"

# Part 1: this part of the script calculate the number of fail log in withi the last 1 hour
# to determine threat level

# Get the current date and time of an hour ago
date_one_hour_ago=$(date --date='-1 hour' "+%Y-%m-%dT%H:%M:%S")

# calculates the toal number of failed attempts
failed_attempts=$(awk -v date="$date_one_hour_ago" '($0 ~ /Failed password for kali/ && $1 "T" $2 > date)' "$AUTH_LOG" | wc -l)
echo "There have been $failed_attempts failed login attempts for user 'kali' in the last hour."

# Part 2: This part  determines the ssh jail values based on threat level
#The three threat lvel:
#Low => less than 5 faild attempt
#Medium => Between 5 to 10 faild attempt 
#Hight => more than 10 failed attempt

if [[ $failed_attempts -lt 5 ]]; then
        sed -i '/^\[sshd\]/,/^\[/ s/^maxretry\s*=.*/maxretry = 8/' $JAIL_LOCAL_PATH
        echo "maxretry updated to 8 in the [sshd] section."
        sed -i '/^\[sshd\]/,/^\[/ s/^bantime\s*=.*/bantime = 5m/' $JAIL_LOCAL_PATH
        echo "bantime updated to 5m in the [sshd] section."

elif [[ $failed_attempts -ge 5 && $failed_attempts -lt 10 ]]; then
       sed -i '/^\[sshd\]/,/^\[/ s/^maxretry\s*=.*/maxretry = 5/' $JAIL_LOCAL_PATH
        echo "maxretry updated to 5 in the [sshd] section."
       sed -i '/^\[sshd\]/,/^\[/ s/^bantime\s*=.*/bantime = 10m/' $JAIL_LOCAL_PATH
        echo "bantime updated to 10m in the [sshd] section."
else 
        sed -i '/^\[sshd\]/,/^\[/ s/^maxretry\s*=.*/maxretry = 3/' $JAIL_LOCAL_PATH
        echo "maxretry updated to 3 in the [sshd] section."
        sed -i '/^\[sshd\]/,/^\[/ s/^bantime\s*=.*/bantime = 15m/' $JAIL_LOCAL_PATH
        echo "bantime updated to 15m in the [sshd] section."
fi

echo "Reloading fail2ban"
sudo fail2ban-client reload