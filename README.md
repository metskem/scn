### Spring Cloud Netflix

A simple project that demonstrates a few components of the spring cloud netflix stack including testapp and more, they are here in separate modules.

## Modules

* eureka (service discovery and registration)
* zuul (proxy)
* configserver (centralized configuration server)
* testapp1 (our simple REST based test app)

You can basically startup all of them in random order, but most of them need Eureka to register themselves (or you get stacktraces).
