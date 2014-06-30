#!/bin/bash

#
# Provides variables and functions common to all run scripts.
#

clear="\033[0m"
green_fg="\033[32m"
green_light_fg="\033[1;32m"
red_fg="\033[31m"
red_light_fg="\033[1;31m"
gray_light_fg="\033[37m"
gray_dark_fg="\033[1;30m"

function cecho() {
    msg=$1
    color=$2
    if [ -t 1 ] && [ -n $color ]; then
        echo -e "${color}${msg}${clear}"
    else
        echo $msg
    fi
}

function running() {
    local name=${1:-$PROCNAME}
    local version=${2:-$PROCVERSION}
    local pid=${3:-$PID}
    cecho "Process ${name} (${version}) is running with pid ${pid}" ${green_fg}
    #cecho "Process ${name} is running (${version}, pid ${pid})" ${green_fg}
}

function not_running() {
    local name=${1:-$PROCNAME}
    local version=${2:-$PROCVERSION}
    cecho "Process ${name} (${version}) is not running" ${red_fg}
    #cecho "Process ${name} is not running (${version})" ${red_fg}
}

function not_running_pidfile() {
    local name=${1:-$PROCNAME}
    local version=${2:-$PROCVERSION}
    local pid=${3:-$PID}
    cecho "Process ${name} (${version}) is not running, but old pid file exists!" ${red_light_fg}
    #cecho "Process ${name} is not running, but old pid file exists (${version}, pid ${pid})" ${red_light_fg}
}

function running_no_pidfile() {
    local name=${1:-$PROCNAME}
    local version=${2:-$PROCVERSION}
    cecho "Process ${name} (${version}) is running, but pid file is lost!" ${green_light_fg}
    #cecho "Process ${name} is running, but pid file is lost (${version})" ${green_light_fg}
}

function check_status() {
    local pidfile=${1:-$CATALINA_PID}
    if [ -f ${pidfile} ]; then
        PID=`cat ${pidfile}`
        if ps -p "$PID" > /dev/null; then
            running 
            exit 0
        else
            not_running_pidfile
            exit 0
        fi
    else
        run_count=`ps -ef | grep -v grep | grep java | grep ${PROCNAME} | grep "$USER " | wc -l`
        if [ ${run_count} -eq 0 ] ; then
            not_running
        else
            running_no_pidfile
        fi
        exit 1          
    fi
}
