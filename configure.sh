#!/bin/bash

[ $# -eq 1 ] && install_path=${1#--prefix=}
echo -n "check install java ...."
java -version >/dev/null 2>&1
if [ $? -eq 0 ]
then
	echo "\tyes"
else
	echo "\tno"
	echo "not install"
	exit
fi
echo -n "check install path ...."
echo -n "${install_path:=/usr/local/mysendmail}"
echo "\tok"

if [ "$USER" = "root" ]
then
	user=root
else
	user=home/$USER
fi

malias1="\"alias mysendmail='java -cp \$(installp)/lib/jbex-v1.4.7-eval.jar"
malias2=":\$(installp)/lib/commons-cli-1.2.jar:\$(installp)/bin/ Mysendmail'\""
echo "installp=$install_path">./Makefile
echo "alias=${malias1}$malias2">>./Makefile
echo "Mysendmail.class:./src/Mysendmail.java">>./Makefile
echo "\tjavac -d ./bin/ -cp ./lib/commons-cli-1.2.jar:\\">>./Makefile
echo "\t./lib/jbex-v1.4.7-eval.jar \\">>./Makefile
echo "\t./src/Mysendmail.java">>./Makefile
echo "install:" >>./Makefile
echo "\ttest ! -d \$(installp) && mkdir -p \$(installp);\\">>./Makefile
echo "\tcp -r ./bin \$(installp);\\">>./Makefile
echo "\tcp -r ./lib \$(installp);\\">>./Makefile
echo "\techo \$(alias) >>/$user/.bashrc;\\">>./Makefile
echo "\tsource /$user/.bashrc">>./Makefile

echo "please running make"
