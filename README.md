# 금융 거래 이상 탐지 시스템

### 설계 주요 고려 사항

### 실험 환경 세팅 및 실험 결과

### 해결하지 못한  부분
- kafka stream join 에서의 30초 대기 시간 발생 이슈 해
- Parameterized, Randomized Log generator 추가 개발
- 추후 성능 이슈를 유발할 수 있는 부분 코드 개선
- 테스트 작성
- Kafka multi-broker 환경에서의 수행 테스트
- Kafka streams의 log 시간 처리
- Redis connection pool 관리
- 실제 수행 클래스인 FdsSystemApp.java 파일이 submodule에 존재
- maven packaging 을 이용해 executable jar를 생성해야 함
- properties 를 이용한 환경결 변수 관리

### references

* docker-compose 이용한 환경 세팅
    * https://github.com/florimondmanca/kafka-fraud-detector
    * https://github.com/wurstmeister/kafka-docker

* kafka & kafka stream
    * http://kafka.apache.org/documentation/
    * https://medium.com/@asce4s/send-and-receive-json-objects-with-kafka-java-client-41bfbb4de108
    * https://github.com/apache/kafka/blob/2.5/streams/examples/src/main/java/org/apache/kafka/streams/examples/pageview/PageViewTypedDemo.java
    * https://www.confluent.io/blog/crossing-streams-joins-apache-kafka/




