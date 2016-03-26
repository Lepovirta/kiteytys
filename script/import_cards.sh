#!/bin/bash

set -e
cd "$(dirname "$0")/.."

remote="kiteytys"

csv_file=$1
deploy_folder="/var/www/apps/kiteytys"
import_command="sh ${deploy_folder}/import_cards.sh ${csv_file}"

if [ ! -f "$csv_file" ]; then
  echo "First argument must be a CSV file!"
  exit 1
fi

echo -n "[*] Import cards to production DB (y/n)? "
read -r answer
if echo "$answer" | grep -iq "^n" ;then
  echo "[*] Import cancelled."
  exit 1
else
  echo "[*] Importing..."
  scp "$csv_file" $remote:$deploy_folder
  ssh "$remote" "$import_command"
  echo "[*] Import complete!"
fi
