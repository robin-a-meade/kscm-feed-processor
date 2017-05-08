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

## How to run as a Unix/Linux service

We followed the approach outlined here:

- [Installing Spring Boot applications § Unix/Linux services § Installation as an init.d service (System V)](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-initd-service)<br>
  _docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-initd-service_

First, we configure maven to generate a _fully executable jar_. This requires adding just three lines to the pom.xml:
```
 <build>
   <plugins>
      <plugin>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-maven-plugin</artifactId>
+      <configuration>
+        <executable>true</executable>
+      </configuration>
     </plugin>
   </plugins>
 </build>
```

The deployment directory should be like:

```sh
$ pwd
/home/kscm2ban

$ tree
.
├── config
│   ├── application-prd.properties
│   ├── application.properties
│   └── application-stg.properties
├── kscm-feed-processor-0.0.8-SNAPSHOT.conf
├── kscm-feed-processor-0.0.8-SNAPSHOT.jar
├── kscm-feed-processor.jar -> kscm-feed-processor-0.0.8-SNAPSHOT.jar
└── logs
    └── kscm-feed-processor-stg.log
```

We create the unversioned symlink because we don't want the version to be part of the SysVinit service name (which we are about to create).

The `.conf` file can be used to set a handful of environment variables that affect how the jar is started. 
It is 
[part](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-script-customization-when-it-runs)
of the functionality built into Spring Boot's fully executable jar 
feature. We just need it to set JAVA_OPTS to specify the active Spring profile(s):

```sh
$ cat kscm-feed-processor-0.0.8-SNAPSHOT.conf
JAVA_OPTS=-Dspring.profiles.active=stg
```

Finally, we create the SysVinit service:
```sh
# Symlink the fully executable jar into /etc/init.d
sudo ln -s /tmp/kscm-feed-processor/kscm-feed-processor.jar /etc/init.d/kscm-feed-processor.jar

# Add the service
sudo chkconfig --add kscm-feed-processor.jar

# Check what the default run levels is has been enabled for
$ chkconfig --list kscm-feed-processor.jar
kscm-feed-processor.jar 0:off 1:off 2:off 3:on 4:on 5:on 6:off

# Manually start it
sudo service kscm-feed-processor.jar start
```

NOTE: A feature of Spring Boot's fully executable jar is that it changes the 
`current working directory` to the directory containing the `jar` file. That's
how it is able to detect the `.conf` file sitting in the same directory.

## Installation as a systemd service
The above linked spring-boot document has a section of installing as a systemd service.
However, our current deployement server is running RHEL6 and "RHEL6 isn't systemd-based. It's … the last RHEL that uses old-style SYSV init scripts" https://serverfault.com/questions/740404

## Logging to console

We've disabled logging to console by default in the `logback-spring.xml` logging configuration file, because this application is designed to be a server process.

When developing with your IDE, however, you'll want to enable logging to console. We used conditional directives
in `logback-spring.xml` to enable logging to console when the `console` profile is active.
Thus, in your IDE's run configuration, you'll want to add `console` as an active profile using a JVM parameter like this: `-Dspring.profiles.active=stg,console`.

