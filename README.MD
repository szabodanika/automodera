## Automodera
[![CodeFactor](https://www.codefactor.io/repository/github/szabodanika/automodera/badge?s=eca96e2acfb8592001f2e1918a827e850204fa7c)](https://www.codefactor.io/repository/github/szabodanika/automodera)
[![codecov](https://codecov.io/gh/szabodanika/automodera/branch/master/graph/badge.svg?token=7FVS5ZZIPU)](https://codecov.io/gh/szabodanika/hashnet)
[![Java CI with Maven](https://github.com/szabodanika/automodera/actions/workflows/maven.yml/badge.svg)](https://github.com/szabodanika/automodera/actions/workflows/maven.yml)
[![Format](https://github.com/szabodanika/automodera/actions/workflows/google-java-format.yml/badge.svg?branch=master)](https://github.com/szabodanika/automodera/actions/workflows/google-java-format.yml)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fszabodanika%automodera.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fszabodanika%automodera?ref=badge_shield)
[![wakatime](https://wakatime.com/badge/user/a8d63788-9389-46c8-8e9f-39b229a3b6c1/project/b823e566-542b-4fce-be36-4391011cd82f.svg)](https://wakatime.com/badge/user/a8d63788-9389-46c8-8e9f-39b229a3b6c1/project/b823e566-542b-4fce-be36-4391011cd82f)

A Publish-Subscribe Network for Distributing Perceptual Hashes of Images, written in Java.


## Setup

### 1. Clone Project
```
git clone https://github.com/szabodanika/automodera.git
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
By default the nodes will listen through port and refer to each other by their domains, so archive1 will send messages to origin.automodera.test:80 and origin will send its messages to archive1.automodera.test:80. You will need to set up virtual hosts so that both addresses are resolved to 127.0.0.1 or localhost and use a reverse proxy like NGINX to redirect the messages for different hosts to different ports, as you will not be able to launch two nodes listening to the same port.

Here is an example NGINX configuration that I use for development:

<details>
  <summary>
    NGINX Configuration
  </summary>
<pre>
worker_processes 1;
events {
    worker_connections	512;
}
http {
    server_names_hash_bucket_size 128;
    include			mime.types;
    default_type	application/octet-stream;
    sendfile		on;
    keepalive_timeout	65;
    server_tokens	off;
    ##
    # Origin Node
    ##
    server {
        listen		80;
        server_name	origin.automodera.test;
        location / {
            proxy_set_header	Host $host;
            proxy_set_header	X-Forwarded-Proto $scheme;
        }
    }
    ##
    # Operator Nodes
    ##
    server {
        listen		80;
        server_name	operator1.automodera.test operator2.automodera.test;
        location / {
            proxy_pass		http://127.0.0.1:9001;
            proxy_set_header	Host $host;
            proxy_set_header	X-Forwarded-Proto $scheme;
        }
    }
    ##
    # Archive Nodes
    ##
    server {
        listen		80;
        server_name	archive1.automodera.test archive2.automodera.test;
        location / {
            proxy_pass		http://127.0.0.1:9002;
            proxy_set_header	Host $host;
            proxy_set_header	X-Forwarded-Proto $scheme;
        }
    }
    ##
    # Integrator Nodes
    ##
    server {
        listen		80;
        server_name	api1.automodera.test api2.automodera.test;
        location / {
            proxy_pass		http://127.0.0.1:9003;
            proxy_set_header	Host $host;
            proxy_set_header	X-Forwarded-Proto $scheme;
        }
    }
}
</pre>
</details>

Once the networking bit is all done, time to run the generated jar executables:

```
java -jar operator-1.0-SNAPSHOT.jar
java -jar archive-1.0-SNAPSHOT.jar
java -jar integrator-1.0-SNAPSHOT.jar
```
### 4. Set up and run nodes


#### 1. Initialise origin node and network configuration
```
init --type ORIGIN --id origin --name 'Automodera Origin Node' --domain origin.automodera.test --legal-name 'Automodera Test Organisation' --admin-email contact@origin.test --address-line1 '123 High Street' --address-line2 'Block B' --post-code AB12CD --country Scotland

netinit --name 'AutomoderaPrototype' --environment dev --version 0.1 --origin origin.automodera.test
```

#### 2. Initialise archive node and connect to network
```
init --type ARCHIVE --id testarchive1 --name 'Automodera Test Archive Node 1' --domain archive1.automodera.test --legal-name 'Automodera Test Organisation' --admin-email contact@archive.test --address-line1 '123 High Street' --post-code AB12CD --country Scotland

connect origin.automodera.test
```

#### 3. Initialise integrator node and connect to network
```
init --type INTEGRATOR --id testintegrator1 --name 'Automodera Test Integrator Node 1' --domain api1.automodera.test --legal-name 'Test UGC Platform' --admin-email contact@integrator.test --address-line1 '321 High Street' --post-code BA21DC --country Scotland
```
```
connect origin.automodera.test
```

#### 4. Request certificate from origin node (archive and integrator)
```
localcert --request
```
#### 5. Sign certificates (origin)
```
certreq --list

```
```
certreq --accept --id [request id here]
```

#### 5. Check network connections
```
node --status --host origin.automodera.test
```
or
```
node --info --host origin.automodera.test
```
or
```
node --all
```

#### 6. Subscribe to string and check local image with integrator node
```
subs --add --topic string1
```
```
check --image ./testimages/supra.jpeg --xml
```


## Code Coverage
![Code Coverage](https://codecov.io/gh/szabodanika/automodera/commit/f68f68efbe04d53cc0e96cfc228f42f1cdde520d/graphs/sunburst.svg?token=7FVS5ZZIPU "Code Coverage")

## License and 3rd Party Software
This project uses 3rd party libraries. View the automatically generated FOSSA report here: [3rd Party Software Report](https://app.fossa.com/reports/59e67f12-02d5-45e9-b78d-440f2614e0f0).
Automodera is available under GNU General Public License v3.0.
