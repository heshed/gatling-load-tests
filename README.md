gatling-load-tests 
==================

## Introduction

### [Gatling](http://gatling.io/)

Gatling is a Scala based open-source load test tool which makes a break from 
traditional load tools by utilising asynchronous concurrency and 
a DSL for scripting.

### [SBT](http://www.scala-sbt.org/)
SBT is the de-facto build tool for Scala projects.

## Installation 
### SBT
```bash
$ wget https://dl.bintray.com/sbt/rpm/sbt-0.13.7.rpm
$ sudo yum install -y sbt-0.13.7.rpm
```

### Test Resources
```bash 
$ git clone git@github.com:BBC/gatling-load-tests.git; cd gatling-load-tests
```

## Execution
```bash
$ sbt
> testOnly *SampleTest
```

## Real-time metrics
### gatling.conf
```
data {
  writers = "console, file, graphite"
}
graphite {
   host = "192.0.2.235" 
   port = 2003
   protocol = "tcp"
   rootPathPrefix = "gatling"
}
```

### Grafana/InfluxDB
```bash
docker run -d -p 8081:8081 --name grafana aidylewis/grafana
docker run -d \
           -p 8083:8083 -p 8084:8084 -p 8086:8086 -p 2003:2003 \
           -e PRE_CREATE_DB="gatling;grafana" --name influxdb davey/influxdb:latest
```

## Gatling Jenkins 
```bash 
docker run -d -p 8080:8080 --name gatling-jenkins aidylewis/gatling-jenkins
```

