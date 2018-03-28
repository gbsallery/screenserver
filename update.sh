#!/bin/sh

echo ""
echo "Kill IntelliJ if you want outdated or deleted libraries to be removed. It locks them."
echo ""
echo "Hit <ENTER> to continue, <CTRL-C> to abort"
read -p ""
echo ""

rm -rf .idea/libraries/*.xml
rm -rf .idea_modules/glue3.iml

./sbt gen-idea
