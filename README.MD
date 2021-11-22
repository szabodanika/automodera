## HashNet
[![CodeFactor](https://www.codefactor.io/repository/github/szabodanika/hashnet/badge?s=eca96e2acfb8592001f2e1918a827e850204fa7c)](https://www.codefactor.io/repository/github/szabodanika/hashnet)
[![codecov](https://codecov.io/gh/szabodanika/hashnet/branch/master/graph/badge.svg?token=7FVS5ZZIPU)](https://codecov.io/gh/szabodanika/hashnet)
[![Java CI with Maven](https://github.com/szabodanika/hashnet/actions/workflows/maven.yml/badge.svg)](https://github.com/szabodanika/hashnet/actions/workflows/maven.yml)
[![Format](https://github.com/szabodanika/hashnet/actions/workflows/google-java-format.yml/badge.svg?branch=master)](https://github.com/szabodanika/hashnet/actions/workflows/google-java-format.yml)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fszabodanika%2Fhashnet.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fszabodanika%2Fhashnet?ref=badge_shield)
[![wakatime](https://wakatime.com/badge/user/a8d63788-9389-46c8-8e9f-39b229a3b6c1/project/b823e566-542b-4fce-be36-4391011cd82f.svg)](https://wakatime.com/badge/user/a8d63788-9389-46c8-8e9f-39b229a3b6c1/project/b823e566-542b-4fce-be36-4391011cd82f)

A Publish-Subscribe Network for Distributing Perceptual Hashes of Images, written in Java.


## Setup

### 1. Clone Project
```
git clone https://github.com/szabodanika/hashnet.git
```
### 2. Build executable JARs
#### 1. Build project from the root directory
```
mvn clean install 
```
#### 2. Repackage executable modules (operator, archive, api) in their own directories
```
mvn package spring-boot:repackage
```
### 3. Run executables
```
java -jar operator-1.0-SNAPSHOT.jar
java -jar archive-1.0-SNAPSHOT.jar
java -jar api-1.0-SNAPSHOT.jar
```
### 4. Initialise Nodes
#### 1. Initialise origin node
```
init --type ORIGIN --id origin --name 'HashNet Origin Node' --domain origin.hashnet.test --legal-name 'HashNet Test Organisation' --admin-email contact@origin.test --address-line1 '123 High Street' --address-line2 'Block B' --post-code AB12CD --country Scotland
```
#### 2. Initialise archive node
```
init --id testarchive1 --name 'HashNet Test Archive Node 1' --domain archive1.hashnet.test --legal-name 'HashNet Test Organisation' --admin-email contact@archive.test --address-line1 '123 High Street' --address-line2 'Block A' --post-code AB12CD --country Scotland
```
#### 3. Request certificate from origin node (archive)
```
localcert --request
```
#### 4. Sign certificate (origin)
```
certreq --accept --id testarchive1 --message OK
```


## Modules
*TODO*


## Code Coverage
![Code Coverage](https://codecov.io/gh/szabodanika/hashnet/commit/f68f68efbe04d53cc0e96cfc228f42f1cdde520d/graphs/sunburst.svg?token=7FVS5ZZIPU "Code Coverage")

## License and 3rd Party Software
This project uses 3rd party libraries. View the automatically generated FOSSA report here: [3rd Party Software Report](https://app.fossa.com/reports/59e67f12-02d5-45e9-b78d-440f2614e0f0).
HashNet is available under GNU General Public License v3.0.
