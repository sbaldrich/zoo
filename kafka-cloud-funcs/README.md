# Simple Usage of Spring Cloud Function with Kafka

A very simple demo of a declaration of consumers, producers and processors using Spring Cloud Function.

## The Setup

Everything lives in the main application class, so give it a look. The main components are the following:

**Supplier**
> Will emit a message everytime the application receives a `POST` request to the root path. The message will be written to the 
  `messages` topic and it's contents depend on the value of the `text` attribute in the body of the request.

**Processor**
> Reads messages from the `messages` topic, transforms them into a reversed version of themselves and writes the new value
> into the `reversed`  topic.

**Sink**
> A simple consumer that reads from `reversed` and logs to stdout.

## See it in action

1 - Build the app and start the containers:
```
./gradlew build && docker-compose up -d
```

2 - Send some post requests to `/` and check the logs:

```
http :8080/ text='this should be reversed'   
``` 

## FAQ

**What if I want to access kafka from my local env?**

> You have two choices, you can change the `KAFKA_ADVERTISED_HOSTNAME` property to `localhost` in the compose file, 
> or you can actually configure it properly by using the recommended `KAFKA_ADVERTISED_LISTENERS` and `KAFKA_LISTENER`
> properties; see more [here](https://kafka.apache.org/documentation/#brokerconfigs).