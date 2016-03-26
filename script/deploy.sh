#!/bin/sh

set -e
cd "$(dirname "$0")/.."

app_name="kiteytys"
remote="kiteytys"

local_jar="target/scala-2.11/kiteytys.jar"
app_jar="${app_name}.jar"
app_package="${app_name}.tar.gz"
deploy_folder="/var/www/apps/${app_name}"
deploy_command="sh ${deploy_folder}/deploy.sh"

echo "[*] Building..."
sbt "assembly"

echo "[*] Testing SSH connection..."
ssh $remote "uname -a"

echo "[*] Creating package"
cp "$local_jar" "$app_jar"
tar -zcvf "$app_package" "$app_jar"

echo "[*] Deploying to server..."
scp $app_package $remote:$deploy_folder
ssh "$remote" "$deploy_command"

echo "[*] Cleaning up..."
rm $app_jar $app_package

echo "[*] All done!"
