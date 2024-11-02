# bitcoinjavalib

> Porting the code from this book to another language would also be a great learning tool.

writes Jimmy Song in [Programming Bitcoin](https://github.com/jimmysong/programmingbitcoin) [^1] on page 250.<br>
So this is a Java implementation of it.

---
[^1]: Programming Bitcoin by Jimmy Song (O'Reilly). Copyright 2019 Jimmy Song. 978-1-492-03149-9.

## release notes
[release notes](release-notes.md)

## run tests with [maven](https://maven.apache.org/)
```
mvn clean test  
mvn test -Dtest=Chapter7Test
mvn test -Dtest=Chapter7Test#exercise4
```

## run network tests
- network tests work only with a peer @ localhost 
```
mvn test -Dnetwork=true
mvn test -Dtest=SimpleNodeTest#handshake -Dnetwork=true
mvn test -Dtest=Chapter10Test#example2 -Dnetwork=true
```

## run disabled tests
```
mvn test -Dtest=Chapter7Test#verifyBiggestTransaction -Ddisabled=false
```
