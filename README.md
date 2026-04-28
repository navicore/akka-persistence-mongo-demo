<!-- ghmig:moved -->
> **This repository has moved to [https://git.navicore.tech/navicore/akka-persistence-mongo-demo](https://git.navicore.tech/navicore/akka-persistence-mongo-demo).**
>
> The GitHub copy is archived and no longer maintained.

An Akka HTTP API Server persisting objects with Akka Persistence for Mongo
---

## RUN

```console
sbt run
```

## BUILD

```console
sbt assembly && docker build -t myimage .
```

#### Notes:

* See `application.conf` for `ENVIRONMENT` variable overrides for Mongo and Akka settings
* The only Mongo-specific things in this example are found in `application.conf` and `build.sbt`.  You should be able to use any API conforming persistence plugin with this example code.
* Works with Azure's CosmoDB