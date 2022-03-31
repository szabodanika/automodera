#!/bin/bash
#
#  Copyright (c) Daniel Szabo 2022.
#
#  GitHub: https://github.com/szabodanika
#  Email: daniel.szabo99@outlook.com
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#

clear

cleanup() {
    # kill all processes whose parent is this process
    pkill -P $$
}

for sig in INT QUIT HUP TERM; do
  trap "
    cleanup
    trap - $sig EXIT
    kill -s $sig "'"$$"' "$sig"
done
trap cleanup EXIT

echo "1/4 Creating ./log and ./db folders if needed"
mkdir ./log
mkdir ./db

echo "2/3 Starting 3 Automodera instances..."
echo "Starting Operator"
java                              -jar ./jars/operator-0.3.jar     --dbname="./db/operator.sqlite"    --server.port="9000" &> ./log/operator.log &
echo "Starting Integrator"
java -Djava.library.path="./lib"  -jar ./jars/integrator-0.3.jar   --dbname="./db/integrator.sqlite"  --server.port="9001" &> ./log/integrator.log &
echo "Starting Archive"
java -Djava.library.path="./lib"  -jar ./jars/archive-0.3.jar      --dbname="./db/archive.sqlite"     --server.port="9002" &> ./log/archive.log &

echo "3/3 All nodes have been started. Press CTRL + C to shut them down. If the process exits on its own, all nodes terminated likely due to an error. See the the log folder for more info."

echo "Operator Node: https://127.0.0.1:9000"
echo "Integrator Node: https://127.0.0.1:9001"
echo "Archive Node: https://127.0.0.1:9002"

wait
