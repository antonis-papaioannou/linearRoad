# LinearRoad
An implementation of the popular Linear Road stream processing benchmark compatible with Apache Flink stream processing platform.

Linear Road simulates a tolling system on expressways of a metropolitan area based on variable pricing, i.e., tolls calculated based on dynamic factors such as traffic congestion and accident proximity. Linear Road processes position reports emitted periodically by every vehicle containing its position and speed on an expressway. Besides continuously-evaluated toll pricing and accident detection, the benchmark is also designed to answer historical queries (e.g., account balance and travel time estimates) that are issued less often.

The implementation of Linear Road used in this work fully leverages the stream-processing capabilities of the Flink platform, e.g., using sliding window operators where appropriate rather than ad-hoc management of internal state, is able to store and manage shared tables in an external key-value store, and carefully follows the original benchmark specification. In our paper [1] we describe Linear Road’s use of internal and externally-managed state, namely ephemeral operators state (e.g. window buffers) that are used to produce the application output vs. state of broader interest (such as historical table data) even beyond the streaming job that should be accessible outside of the SPS.


## The benchmark
The benchmark depends on the Kafka message broker(s) to ingest data and on Redis in-memory database to store traffic statistics used to calculate toll charges.

### Build
The *benchmark* folder contains the source code of the benchmark. It is a maven project that can automatically resolve dependencies and build-package the benchmark.
  To build the benchmark execute the command:
  ```
  mvn clean package
  ```
This process produces the packaged (jar) version of the benchmark under the name linerar_road-0.2.jar in the benchmark/target directory.

### Configuration options
The benchmark uses a configuration file (located in the benchmark/conf directory).
The benchmark ingests data from Kafka topic (see section [The data generator](#the-data-generator) for more on the data generator we provide).
It also uses an external Redis database to store statistics regarding traffic on different segments of the expressways.
To specify the Redis and Kafka  hostnames and topics, use the configuration file.

### Run
To run the benchmark as a Flink job you can use the Flink submit method.
For the command line of the Flink Job Manager run the comand:
><path to flink bin>/bin/flink run <path-to-benchmark-target-dir>/linerar_road-0.2.jar <path-to-benchmark-conf-dir>/benchmarkConf.yaml

## The data generator
The benchmark is accompanied with a data generator.
The generator is a separate maven project located in the corresponding folder.

### Build
To build the generator execute the command (within the generator folder):
```
mvn clean package
```

### Run
The generator creates sample data that can be stored in data files or import directly to the Kafka brokers.
It is also customizable regarding the number of expressways, the number of vehicles per expressway and the duration of the simulation time.
It also produces a log file with the accidents that occur and expect from the benchmark to identify them as it consumes the input data stream.

To run the generator execute the command:
>mvn exec:java -Dexec.args="\<parameters\>"

where parameters:
```
-x <number>  # the number of expressways
-d <number>  # simulation time in second e.g. 10800 results to 3 hours
-v <number>  # the number per expressway
-k <host>    # specify the hostname of the Kafka broker to put data
-s           # simulate i.e. generate data but do not import into Kafka
-p           # print generated data on stdout
-f <data file path> # dump the generated data in the specified file (data are also imported in the Kafka brokers)
-h           # print help message
```

## Credits

If you find this code useful in your research, please consider citing:

[1] Antonis Papaioannou and Kostas Magoutis, Amoeba: Aligning Stream Processing Operators with Externally-Managed State, to appear in the 14th IEEE/ACM International Conference on Utility and Cloud Computing, Leicester, UK, December 2021

bibtex:
  
@inproceedings{10.1145/3468737.3494096,  
author = {Papaioannou, Antonis and Magoutis, Kostas},  
title = {Amoeba: Aligning Stream Processing Operators with Externally-Managed State},  
year = {2021},  
isbn = {9781450385640},  
publisher = {Association for Computing Machinery},  
address = {New York, NY, USA},  
url = {https://doi.org/10.1145/3468737.3494096},  
doi = {10.1145/3468737.3494096},  
booktitle = {Proceedings of the 14th IEEE/ACM International Conference on Utility and Cloud Computing},  
location = {Leicester, United Kingdom},  
series = {UCC '21}  
}  
  
The paper is also **available** online for **open access**: https://doi.org/10.1145/3468737.3494096  

### References
1. [Apache Flink] (https://flink.apache.org)
2. [Apache Kafka] (https://kafka.apache.org)
3. [Redis] (https://redis.io)
