#!/bin/bash

# Defines the default location for external tools in Linux and Solaris systems.
# check if the variable is already defined, if yes the given value is used otherwise it is set
setCmdVariable() {
        VARIABLE=$1
        VALUE=$2

        if [ -z "${!VARIABLE}" ] ; then
          export ${VARIABLE}="$VALUE";
        fi
}

setCmdVariable CMDAWK "/bin/awk"
setCmdVariable CMDBASENAME "/bin/basename"
setCmdVariable CMDCUT "/bin/cut"
setCmdVariable CMDCAT "/bin/cat"
setCmdVariable CMDDATE "/bin/date"
setCmdVariable CMDDIRNAME "/usr/bin/dirname"
setCmdVariable CMDECHO "/bin/echo"
setCmdVariable CMDEXPR "/usr/bin/expr"
setCmdVariable CMDENV "/usr/bin/env"
setCmdVariable CMDEGREP "/bin/egrep"
setCmdVariable CMDFIND "/usr/bin/find"
setCmdVariable CMDGREP "/bin/grep"
setCmdVariable CMDHEAD "/usr/bin/head"
setCmdVariable CMDHOSTNAME "/bin/hostname"
setCmdVariable CMDID "/usr/bin/id"
setCmdVariable CMDKILL "/bin/kill"
setCmdVariable CMDLN "/bin/ln"
setCmdVariable CMDLS "/bin/ls"
setCmdVariable CMDLSOF "/usr/sbin/lsof"
setCmdVariable CMDMD5SUM "/usr/bin/md5sum"
setCmdVariable CMDNETSTAT "/bin/netstat"
setCmdVariable CMDOPENSSL "/usr/bin/openssl"
setCmdVariable CMDPERL "/usr/bin/perl"
setCmdVariable CMDPKILL "/usr/bin/pkill"
setCmdVariable CMDPS "/bin/ps"
setCmdVariable CMDRM "/bin/rm"
setCmdVariable CMDSCP "/usr/bin/scp"
setCmdVariable CMDSED "/bin/sed"
setCmdVariable CMDSLEEP "/bin/sleep"
setCmdVariable CMDSORT "/bin/sort"
setCmdVariable CMDSSH "/usr/bin/ssh"
setCmdVariable CMDSU "/bin/su"
setCmdVariable CMDSUDO "/usr/bin/sudo"
setCmdVariable CMDTAR "/bin/tar"
setCmdVariable CMDTAIL "/usr/bin/tail"
setCmdVariable CMDTEE "/usr/bin/tee"
setCmdVariable CMDTOUCH "/bin/touch"
setCmdVariable CMDTR "/usr/bin/tr"
setCmdVariable CMDUNAME "/bin/uname"
setCmdVariable CMDWC "/usr/bin/wc"
setCmdVariable CMDWHOAMI "/usr/bin/whoami"
