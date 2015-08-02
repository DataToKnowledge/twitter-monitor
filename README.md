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




words to track

```
http://localhost:8080/api/v1/monitor/track

{
  "users": [],
  "terms": ["news", "cronaca", "reato", "crimine"],
  "language": ["it"]
}
```

start the processing

```
http://localhost:8080/api/v1/operation/start

```