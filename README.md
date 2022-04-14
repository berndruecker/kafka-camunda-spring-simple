# Confluent Cloud (Apache Kafka) + Camunda Platform 8 (Zeebe) 

## Sample Glue Code Written in Java Spring Boot

Simple example code connecting Confluent Cloud (Kafka) and Camunda Platform 8 SaaS (Zeebe) by leveraging the Java Spring Boot integrations of both frameworks.

The example automates a very simple process, that

- writes a record to Kafka
- listens to the exact same record to correlate it back to the process instance

![](architecture.png)

You can find a walk through on YouTube here:

<a href="http://www.youtube.com/watch?feature=player_embedded&v=jgRhq8h2ZT4" target="_blank"><img src="http://img.youtube.com/vi/jgRhq8h2ZT4/0.jpg" alt="Walkthrough" width="240" height="180" border="10" /></a>

# How to run

* Create Kafka cluster in Confluent Cloud: https://confluent.cloud/
* Create client credentials and add them to ``application.properties``

* Create Camunda cluster in Camunda 8 Saas: https://console.cloud.camunda.io/
* Create client credentials and add them to ``application.properties``

* Start up Java application, which will
  * Deploy the process (visible via Camunda Operate)
  * Create the topics (visible via Confluent Console)
* Kick off a new process instance, e.g.
  * via Camunda Desktop Modeler 