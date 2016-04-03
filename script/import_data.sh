#!/bin/bash
# Import data to production database.

set -e
cd "$(dirname "$0")/.."

remote="kiteytys"

csv_file=$1
deploy_folder="/var/www/apps/kiteytys"
import_command="sh ${deploy_folder}/import_data.sh"

function import() {
  echo "[*] Importing $1 ..."
  scp "$csv_file" $remote:$deploy_folder
  ssh "$remote" "${import_command} $1 ${csv_file}"
  echo "[*] Import complete!"
}


if [ ! -f "$csv_file" ] || [[ ! "$csv_file" = *.csv ]]; then
  echo "First argument must be a CSV file!"
  exit 1
fi

echo "[*] Import data to production DB"

PS3="Pick an option: "
options=("Cards" "Owners" "Cancel")
select opt in "${options[@]}"
do
  case $opt in
    "Cards")
      import cards
      break
      ;;
    "Owners")
      import owners
      break
      ;;
    "Cancel")
      echo "[*] Import cancelled."
      exit 1
      ;;
    *) echo Invalid option;continue;;
  esac
done
