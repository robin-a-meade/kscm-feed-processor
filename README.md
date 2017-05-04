# KSCM Feed Processor

Reads the unified 10 campus KSCM kinesis stream and pushes course changes into Banner.

## AWS Credentials

The AWS Credentials are stored in the usual place: `~/.aws/credentials`

```
[stg]
aws_access_key_id=
aws_secret_access_key=

[prd]
aws_access_key_id=
aws_secret_access_key=
```

NOTE: In above we used AWS credential _profiles_. But you don't need to set AWS_PROFILE environment variable. The app was designed so that the SPRING_PROFILES_ACTIVE determines the AWS credentials profile. For example, setting `SPRING_PROFILES_ACTIVE=stg` is sufficient. 


## How to run


```
SPRING_PROFILES_ACTIVE=stg java -jar target/kscm-feed-processor-0.0.1-SNAPSHOT.jar
```

or, if you prefer JVM system properties:

```
java -Dspring.profiles.active=stg -jar target/kscm-feed-processor-0.0.1-SNAPSHOT.jar
```

**NOTE:** Above commands are for the _staging_ environment. For _production_, swap `stg` with `prd`. 

## Logging to console

We've disabled logging to console by default in the `logback-spring.xml` logging configuration file, because this application is designed to be a server process.

When developing with your IDE, however, you'll want to enable logging to console. We used conditional directives
in `logback-spring.xml` to enable logging to console with the `console` profile is one of the active spring profiles.
Thus, in your IDE's run configuration, you'll want to add `console` as an active profile using a JVM parameter like this: `-Dspring.profiles.active=stg,console`.

