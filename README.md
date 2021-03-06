# nettyleaker

## Environments
### Netty version
netty-all: 4.1.24.Final

### JVM version (e.g. java -version)
java version "1.8.0_181"
Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)

#### OS version (e.g. uname -a)
Darwin MacBook-Pro-6.local 17.6.0 Darwin Kernel Version 17.6.0: Tue May 8 15:22:16 PDT 2018; root:xnu-4570.61.1~1/RELEASE_X86_64 x86_64

## Steps to reproduce
1. do `mvn clean install`  under the root of this project
2. Configure HttpSnoopServer with the following configuration that we used to run the HttpSnoopServer is
```
    -Xms10M
    -Xmx10M
    -XX:MaxDirectMemorySize=3M
    -Dio.netty.leakDetectionLevel=PARANOID
    -Dio.netty.leakDetection.maxRecords=32
```
3. Run HttpSnoopServer in debug mode
4. Run HttpDMErrorClient once and wait for all the requests to be done
5. Now, give a breakpoint at line 638 of PlatformDependent class at the following and check the usedMemory Value.
```
    private static void incrementMemoryCounter(int capacity) {
        if (DIRECT_MEMORY_COUNTER != null) {
            for (;;) {
                long usedMemory = DIRECT_MEMORY_COUNTER.get();
```
6. Run the HttpDMErrorClient again. This time  usedMemory keeps going up for subsequent large HTTP requests and does not go down over time even if there is no active Http requests. This resutls in OutOfDirectMemoryError even though the body size of http requests are lower than the direct memory size been used by Netty.


