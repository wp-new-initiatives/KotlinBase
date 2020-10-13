# Onepages Integration Layer

# Setup
* we recommend downloading and installing Intellij IDEA
> https://www.jetbrains.com/idea/download/#section=mac
* Java11 with Gradle
1. download https://jdk.java.net/11/ for your system
2. unzip the folder and run `$ sudo mv ${path_to_folder} /Library/Java/JavaVirtualMachines/`
3. validate you installed it correctly: `$ java --version`
* run the setup script: `./scripts/setup.sh`

### Setup
* Open with Intellij IDEA
* Click Run
* Go to localhost:8080 
* Get the keys from other project developers: 
```
ENV
CONSUMER_APPS_URL
SEO_DB_USER
SEO_DB_PASS
SEO_DB_URL
```
* Integrate them into your system by editing your Intellij configuration and adding an `Environment variable`:
    * Open the `KotlinProject.kt` file
    * Click on the **Run** Menu
    * Select **Edit Configurations...**
    * Click on **KotlinProject** in the **SpringBoot** section.

#### Logging
This project replaces Spring Boot's internal Lo4j implementation with Log4j2 and follows Whitepages' [internal logging standards](https://cwiki.util.pages/display/CONSUMER/Logging+Standards).

#### Logging Filters
All requests & responses are injected into the logs leveraging a `filter` or middleware. You can add your own filters by following the example provided by the `RequestResponseLoggingFilter` class.

#### Adding information to Logs
Logs will generally be of the format: `logger.info("Adding 1")`. To add more information, append a `mapOf( x to y)` or a custom data object, to the log statement with the desired information, i.e. `logger.info("Adding 1", mapOf("inNumber" to inNumber))`

#### Configuring Logs
Log formatting is handled by Log4J2 and is configured using `src/main/resources/log4j2.properties`. 

#### Available variables
To see what variables are allowed and how to use them, read the property section of [the log4j2 docs](https://logging.apache.org/log4j/2.x/manual/configuration.html)

#### Debugging Logs
Should you encounter an  error during configuration, consider setting `rootLogger.level` to `debug` in `log4j2.properties`.

## Deployments
we currently use Jenkins to deploy to our environments. Our setup is spelled out in `Dockerfile` and `Jenkinsfile`
both stage and prod use the same setups
  
### staging
once you successfully merge a PR into the staging branch (we dont have a master branch since that term is a bit ambiguous), it should get deployed to the stage k8s env.
The pipeline currently (May 2020) takes around 5 min for everything to happen

### production
because we squash commits, we need something other than the typical merge to a branch. For this we have a slightly more complex approach.
1. checkout and pull the latest staging branch (this is a requirement)
2. tag using the commit hash: `git tag $(git rev-parse --short HEAD)`
3. push your tag to github: `git push origin $(git rev-parse --short HEAD)`
4. jenkins should see the tag here https://jenkins.sandbox.pages/job/platform/job/kotlinproject/view/tags/
5. build the tag: you need to be logged in to see the `Schedule a Build` button on jenkins
> your output should look like this `* [new tag]         TAG_NAME -> TAG_NAME`. If your tag is all numbers, it wont deploy, this is due to a wp-deploy bug.
> To get around this bug, either recommit (and hope you dont get all numbers) or talk with other devs about editing your local wp-deploy (sad I know) and deploy locally 
