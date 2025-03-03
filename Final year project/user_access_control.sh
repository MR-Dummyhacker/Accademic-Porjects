#!/bin/bash

# Define the path to audit and auth logs
AUDIT_LOG="/var/log/audit/audit.log"
AUTH_LOG="/var/log/auth.log"

# Define the audit key used to tracce accesses to the documents directory
AUDIT_KEY="access-documents"

# Function to ban an IP address using iptables
ban_ip() {
    local ip_address="$1"
    echo "Banning IP address: $ip_address"
        sudo fail2ban-client set sshd banip "$ip_address"
        echo "ip $ip_address has been banned"
}

# Function to terminate all SSH sessions for a given IP address
terminate_ssh_sessions() {
    local ip_address="$1"
    # Extract PIDs of sshd belonging to the IP address
    local pids=$(pgrep -u "$USER" sshd)
    for pid in $pids; do
        local session_ip=$(sudo ss -tnp | grep "$pid" | grep "ESTAB" | grep "ssh" | awk '{print $5}' | cut -d':' -f1)
        if [[ "$session_ip" == "$ip_address" ]]; then
            echo "Terminating SSH session for IP $ip_address with PID $pid"
            sudo kill "$pid"
        fi
    done
}

# Monitor the audit log file for new entries regarding the documents directory access
tail -F "$AUDIT_LOG" | while read -r line; do
    if echo "$line" | grep -qw "$AUDIT_KEY"; then
        ses_id=$(echo "$line" | awk -F'ses=' '{print $2}' | awk '{print $1}')
            # Extract the IP address from auth.log that matches the session ID
        ip_address=$(tac $AUTH_LOG | grep -m 1 "New session $ses_id" -A 2 | grep "Accepted password" | awk '{print $9}') 
                echo "Detected access to documents by IP $ip_address from session $ses_id"
                terminate_ssh_sessions "$ip_address"
                ban_ip "$ip_address"
      #      fi
       # fi
    fi
done