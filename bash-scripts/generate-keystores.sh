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

echo "1/4 Creating ./keystore folder if needed"

mkdir ./keystore

if [ ! -f ./keystore/automodera-archive.p12 ]
then
    echo "2/4 Creating PKCS12 key for Archive Node"

    keytool -genkeypair -alias automodera-archive -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore ./keystore/automodera-archive.p12 -validity 3650
else
    echo "2/4 Archive Node PKCS12 key already exists."
fi

if [ ! -f ./keystore/automodera-integrator.p12 ]
then
    echo "3/4 Creating PKCS12 key for Integrator Node"

    keytool -genkeypair -alias automodera-integrator -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore ./keystore/automodera-integrator.p12 -validity 3650
else
    echo "3/4 Integrator Node PKCS12 key already exists."
fi

if [ ! -f ./keystore/automodera-operator.p12 ]
then
    echo "4/4 Creating PKCS12 key for Operator Node"

    keytool -genkeypair -alias automodera-operator -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore ./keystore/automodera-operator.p12 -validity 3650
else
    echo "4/4 Operator Node PKCS12 key already exists."
fi


echo "4/4 PKCS12 keys created."
