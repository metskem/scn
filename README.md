# Spring Cloud Netflix

A simple project that demonstrates a few components of the spring cloud netflix stack including testapp and more, they are here in separate modules.

## Modules

* eureka (service discovery and registration)
* zuul (proxy)
* configserver (centralized configuration server)
* testapp1 (our simple REST based test app)

You can basically startup all of them in random order, but most of them need Eureka to register themselves (or you get stacktraces).

## Deploy on Azure

To deploy on Azure, we have the following setup

LB (9080) => zuul (links:9080)
             zuul (rechts:9080)
             
LB (8080) => configserver (links:8080)
             configserver (rechts:8080)
             
LB (8761) ==> eureka (links:8761)
              eureka (rechts:8761)
              
### Startup commands

__Eureka__

host links:

    nohup java -Deureka.client.serviceUrl.defaultZone=http://rechts.westeurope.cloudapp.azure.com:8761/eureka -Deureka.instance.hostname=links.westeurope.cloudapp.azure.com -jar eureka-0.0.1.jar > eureka.out &
    
host rechts:

    nohup java -Deureka.client.serviceUrl.defaultZone=http://links.westeurope.cloudapp.azure.com:8761/eureka -Deureka.instance.hostname=rechts.westeurope.cloudapp.azure.com -jar eureka-0.0.1.jar > eureka.out &
    
    
__configserver__

both links and rechts:

    nohup java -Deureka.client.serviceUrl.defaultZone=http://cluster-vip.westeurope.cloudapp.azure.com:8761/eureka -jar configserver-0.0.1.jar > configserver.out &
    
both links and rechts:

__zuul__

    nohup java -Deureka.client.serviceUrl.defaultZone=http://cluster-vip.westeurope.cloudapp.azure.com:8761/eureka -jar zuul-0.0.1.jar > zuul.out &
    
__testapp1__

both links and rechts:

    nohup java -Dfile.encoding=UTF-8 \
     -Deureka.client.serviceUrl.defaultZone=http://cluster-vip.westeurope.cloudapp.azure.com:8761/eureka \
     -Dspring.datasource.url=jdbc:mysql://testapp1-mysql.westeurope.cloudapp.azure.com/springboottest?useSSL=false \
     -Dspring.cloud.config.uri=http://cluster-vip.westeurope.cloudapp.azure.com:8080 \
     -jar testapp1-0.0.1.jar > testapp1.out &

Or if you want the address of the configserver to be retrieved from eureka (configserver should of course be registered in eureka), this will also pull the spring.datasource.url from configserver/git: 

    nohup java -Dfile.encoding=UTF-8 \
     -Deureka.client.serviceUrl.defaultZone=http://cluster-vip.westeurope.cloudapp.azure.com:8761/eureka \
     -Dspring.cloud.config.discovery.enabled=true \
     -jar testapp1-0.0.1.jar > testapp1.out &

MySQL (bitnami) server:

Ordered in Azure. after first boot, find the mysql root password:

    grep 'application password' /var/log/pre-start.log
    
On our first test it was "1xgCzPc2GZ8b", use that for ''mysql -u root -p''


## Questions to answer

### How to refresh props when changed in git repo. A POST to the refresh does not do the job.

This was "solved" by adding the property `management.security.enabled=false` . This will expose all your endpoints and let them unauthenticated in the wild.
Also, if your configservers is secured, and you specified the configserver with properties `instance.metadataMap.user` and `instance.metadataMap.password`, they don't work anymore, 
and you have to start your app with a user:password in the configserver URL, like : 

    -Dspring.cloud.config.uri=http://testuser:testpassword@cluster-vip.westeurope.cloudapp.azure.com:8080 
    
### Zuul 2.0 ? With nio?

### Should I use zuul as an global gateway?

### What about zuul filters and how dow I dynamically reload them?

### Eureka only offers authentication based on SSL (?), and no authorization , is that acceptable.
Any team can make a mistake and register appB under the name appC, rendering probably wrong responses.

### App errors 

    java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@7138c7a2 rejected from java.util.concurrent.ThreadPoolExecutor@7fff37f5[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 0]
    	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2047) ~[na:1.8.0_121]
    	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:823) [na:1.8.0_121]
