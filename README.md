# LinearRoad
An implementation of the popular Linear Road stream processing benchmark compatible with Apache Flink.

Linear Road simulates a tolling system on expressways of a metropolitan area based on variable pricing, i.e., tolls calculated based on dynamic factors such as traffic congestion and accident proximity. Linear Road processes position reports emitted periodically by every vehicle containing its position and speed on an expressway. Besides continuously-evaluated toll pricing and accident detection, the benchmark is also designed to answer historical queries (e.g., account balance and travel time estimates) that are issued less often.

The implementation of Linear Road used in this work fully leverages the stream-processing capabilities of the Flink platform, e.g., using sliding window operators where appropriate rather than ad-hoc management of internal state, is able to store and manage shared tables in an external key-value store, and carefully follows the original benchmark specification. In our paper [1] we describe Linear Road’s use of internal and externally-managed state, namely ephemeral operators state (e.g. window buffers) that are used to produce the application output vs. state of broader interest (such as historical table data) even beyond the streaming job that should be accessible outside of the SPS.

If you find this code useful in your research, please consider citing:

[1] Antonis Papaioannou and Kostas Magoutis, Amoeba: Aligning Stream Processing Operators with Externally-Managed State, to appear in the 14th IEEE/ACM International Conference on Utility and Cloud Computing, Leicester, UK, December 2021

We will make the source code available in the new few days. Please check again
