# kafka-producer
This project contains all the producer for our kafka cluster. It monitor data from different sources

## Installation Guide

###1. 
clone the [privateconf projet](https://github.com/fabiofumarola/privateconfs) into ```src/main/resources```

###2. 
run ```sbt docker:publishLocal```

###3.

1. create the in folder ```/data/nginx/conf.d``` a file named monitor.conf
2. setup the server and the proxy

```
upstream monitor.datatoknowledge.it {
  # spark
  server monitor-1:8080;
}

server {
  server_name monitor.datatoknowledge.it;
  location / {
    proxy_pass http://monitor.datatoknowledge.it;
    auth_basic  "Restricted monitor.datatoknowledge.it";
    auth_basic_user_file /etc/nginx/htpasswd/wtl;
  }
}

```

```
docker run -d -e VIRTUAL_HOST=monitor.datatoknowledge.it --name monitor-1 dtk/twitter-monitor:0.1
```

## Start the worker

to start the worker we will use postman application

### 1. Track list

GET with username and password

```
http://host.datatoknowledge.it/api/v1/monitor/track

{
  "users": [],
  "terms": ["news", "cronaca", "reato", "crimine"],
  "language": ["it"]
}

```

### 2. add one worker

PUT with username and password

```
http://host.datatoknowledge.it/api/v1/worker/1
```


### 3. start the process twits

POST or GET with username and password

```
http://host.datatoknowledge.it/api/v1/operation/start

```

All the route availables are the following

```scala
  val routes = pathPrefix("api" / "v1") {
    trackRoute ~ operationRoute ~ workerRoute
  }

  def trackRoute = {
    path("monitor" / "track") {
      get {
        complete {
          (serviceActor ? Track()).mapTo[Track]
        }
      } ~
        post {
          entity(as[Track]) { track =>
            complete {
              (serviceActor ? track).mapTo[Track]
            }
          }
        }
    }
  }

  def workerRoute = {
    path("worker" / IntNumber) { number =>
      put {
        complete {
          (serviceActor ? AddWorkers(number)).mapTo[Workers]
        }
      } ~
        delete {
          complete {
            (serviceActor ? DelWorkers(number)).mapTo[Workers]
          }
        }
    } ~
      path("worker" / "list") {
        get {
          complete {
            (serviceActor ? Workers(0)).mapTo[Workers]
          }
        }
      }
  }

  def operationRoute = {
    path("operation" / "status") {
      get {
        complete {
          (serviceActor ? Status).mapTo[OperationAck]
        }
      }
    } ~
      path("operation" / "start") {
        (get | post) {
          complete {
            (serviceActor ? Start).mapTo[OperationAck]
          }
        }
      } ~
      path("operation" / "stop") {
        (get | post) {
          complete {
            (serviceActor ? Stop).mapTo[OperationAck]
          }
        }
      } ~
      path("operation" / "restart") {
        (get | post) {
          complete {
            (serviceActor ? Restart).mapTo[OperationAck]
          }
        }
      }
  }
}

```