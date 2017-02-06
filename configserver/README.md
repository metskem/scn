### Running your config server

## Resources

* http://cloud.spring.io/spring-cloud-static/spring-cloud.html


## Intro

This Spring cloud config server can work (in our case) a bit like a proxy server between your app and one or more git repo's.  
The configuration (src/main/resources/application.yml) has 2 important properties:  

* spring.cloud.config.server.git_uri: git@github.com:metskem/{application}-config.git  # git protocol does not work on cf, it tries but fails ssh because of com.jcraft.jsch.JSchException: UnknownHostKey, so use the https url for git
* spring.cloud.config.server.searchPaths: '{profile}'

The configserver makes a git clone from the above specified repo. The "{application}" part is a variable that is resolved before constructing the git url.  
This is determined by your app's spring.application.name (you can specify that in the app's application.yml/properties or one of the other options like cmdline switches).  
The git clone is done when the first variable has to be resolved.  
The configserver also keeps track of the variable {label} which can be set with the cmdline switch --spring.cloud.config.label=mylabel.

You can use a direct URL to the config server to verify the config and show the git contents. The URL looks like :  

 http://localhost:8888/testapp/testprofile/tag4711  (here you ask for the config of application "testapp" running with profile "testprofile" and with git tag "tag4711"
    
    {
    
        "name": "testapp",
        "profiles": [
            "testprofile"
        ],
        "label": "tag4711",
        "version": "f1a5ec29e95aa52de95d6a4c8d4877f78ffab96d",
        "propertySources": [
            {
                "name": "git@github.com:metskem/configrepo-testapp.git/testprofile/application.properties",
                "source": {
                    "prop3": "prop3 from git for profile testprofile tag4711",
                    "prop1": "prop1 from git for profile testprofile tag4711"
                }
            },
            {
                "name": "git@github.com:metskem/configrepo-testapp.git/application.properties",
                "source": {
                    "prop3.ff.niet": "prop3 from git",
                    "prop1": "prop1 from git 2016-06-14 08:48 tag4711"
                }
            }
        ]
    
    }
    


## Git repo setup

The git repo has the following layout :

    metskem@athena:/tmp/configrepo-testapp$ git status
    On branch master
    Your branch is up-to-date with 'origin/master'.
    nothing to commit, working directory clean
    metskem@athena:/tmp/configrepo-testapp$ find . | grep -v .git
    .
    ./testprofile
    ./testprofile/application.properties
    ./application.properties
    metskem@athena:/tmp/configrepo-testapp$ cat application.properties 
    prop1=prop1 from git 2016-06-14 08:48
    prop3.ff.niet=prop3 from git
    metskem@athena:/tmp/configrepo-testapp$ cat testprofile/application.properties 
    prop1=prop1 from git for profile testprofile
    prop3=prop3 from git for profile testprofile
    metskem@athena:/tmp/configrepo-testapp$ git checkout tag4711
    Switched to branch 'tag4711'
    Your branch is up-to-date with 'origin/tag4711'.
    metskem@athena:/tmp/configrepo-testapp$ find . | grep -v .git
    .
    ./testprofile
    ./testprofile/application.properties
    ./application.properties
    metskem@athena:/tmp/configrepo-testapp$ cat application.properties 
    prop1=prop1 from git 2016-06-14 08:48 tag4711
    prop3.ff.niet=prop3 from git
    metskem@athena:/tmp/configrepo-testapp$ cat testprofile/application.properties 
    prop1=prop1 from git for profile testprofile tag4711
    prop3=prop3 from git for profile testprofile tag4711


## Starting your configserver and application

Locally, you can run the configserver with just:

    mvn clean package && java -jar target/configserver-0.0.1-SNAPSHOT.jar
    
On CF you can just run *cf push* with the following manifest.yml (mind the last envvar):

    applications:
    - path: target/configserver-0.0.1-SNAPSHOT.jar
      memory: 300M
      disk_quota: 1024M
      buildpack: java_buildpack
      env:
        JAVA_OPTS: -Djava.security.egd=file:///dev/urandom
        server_port: 8888 # it looks like this is ignored on cf, it always starts on 8080 or random port
        spring_cloud_config_server_git_uri: https://github.com/metskem/configrepo-{application}.git # we cannot use ssh on cf

You can then start your app with :

    mvn clean package && java -jar target/testapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=testprofile --spring.cloud.config.label=tag4711

If you omit the two last cmdline args, "default" will be the default profile, and "master" will be the default label.  
Your application.properties/yml should contain a property *spring.cloud.config.uri* that points to the configserver, example values are 

* http://konvigserver.cfapps.io
* http://localhost:8888 (which is also the default)

## Security

# Basic auth

See: http://cloud.spring.io/spring-cloud-static/spring-cloud.html#_security  
You can just add a dependency on *spring-boot-starter-security* and the config server will have basic auth.  
The userid is **user** and the password is echoed to stdout during start.  (or configure the password with *security.user.password*)  
If you turn on basic auth for your configserver, make sure that your clients pass on the credentials of course.  
For example by specifying the user and password in the cmdline like *--spring.cloud.config.uri=http://user:aap@localhost:8888* 
For diagnosing it can be handy to look at the access logging of the config server.  

# Encryption/decryption of properties

See: http://cloud.spring.io/spring-cloud-static/spring-cloud.html#_encryption_and_decryption_2  
It only protects properties when they are "at rest", not when going over the wire.  
How to get this to work?  

First start your configserver with an extra envvar:  

    export ENCRYPT_KEY=mijnsleuteltje && java -jar target/configserver-0.0.1-SNAPSHOT.jar --security.user.password=aap

Then encrypt your property:  

    curl http://user:aap@localhost:8888/encrypt -d mysecret
    c1814c19656c97f6889c3300c2475f7faab67d44f509de7a941bd702db32f1d5
    
Then put this in one of your application.properties files in git:  

    prop3={cipher}c1814c19656c97f6889c3300c2475f7faab67d44f509de7a941bd702db32f1d5
    
When you resolve this variable the "Spring way", you will get back "mysecret".  
You can also decrypt this key yourself, for example with :  

    curl http://user:aap@localhost:8888/decrypt -d c1814c19656c97f6889c3300c2475f7faab67d44f509de7a941bd702db32f1d5
    mysecret
    
There are more advanced options for asymetric key encryption and using keystores, but that is maybe for later.

### TODO

## Service discovery

