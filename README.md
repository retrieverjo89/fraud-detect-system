# 금융거래 이상 로그 탐지 시스템

### 설계 주요 고려 사항
최대한 로직의 자유도를 보장하면서, 실시간으로 이상 로그를 탐지하고자 하였다.
초기 생각은 단순히 Kafka의 Producer / Consumer를 활용한 Topology 였는데, 그렇게 한다면 문제에서 제시된 규칙이 아닌 다른 규칙에 대해 추가적으로 개발을 하고자 할 때에는 전처리 결과가 저장되는 Database의 Schema 구조를 바꿔야 될 것으로 예상되었고, 그렇게 되면 정할 수 있는 규칙의 자유도는 떨어진다고 생각하였다. 그러다보니 자연스럽게 Kafka Streams를 활용하여 실시간 탐지할 수 있는 시스템을 구축하는 것으로 진행이 되었으며, 중간 전처리 데이터를 저장하는 저장소는 빠른 속도로 접근이 가능한 In-memory Database인 Redis를 활용하게 되었다.
진행 과정 중 가장 고민이 많았던 부분은 어떻게 Kafka Streams를 활용하여 주어진 조건에 만족하면서 다양한 형태의 로그를 정확하게 처리할 수 있을지에 대한 부분이었다. 하지만 Kafka와 Kafka Streams를 처음 사용하다보니, 배경 지식이 부족하여 표현할 수 있는 Streams topology에 한계가 있었으며, 자주 버그에 부딪히게 되었다.
결과적으로 복잡한 흐름의 사용자 로그에는 대응할 수 없지만, 간단한 흐름의 사용자 로그에는 원하는 결과가 나오는 형태로 구현할 수 있게 되었다. 대신, Kafka Streams를 활용하였기 때문에, 일부 병목이 예상되는 부분만 해결해 준다면 쉽게 Scale-out이 가능할것이다.


### 실험 환경 세팅 및 실험 결과
##### 환경 세팅
다음과 같은 형태로 두 사용자 로그를 생성하고, 탐지하도록 하였다.
![user log flow](https://raw.githubusercontent.com/retrieverjo89/fraud-detect-system/master/user_log_flow.png)
첫번째 사용자 U1 은 제시된 규칙 A 를 전부 만족하는 케이스이다. 그리고 사용자 U2는 규칙 A 중 100만원이 입금된 이후 2시간 이내의 출금/이체로 잔고가 1만원 이하가 된 경우에 해당하지 않아 이상탐지에 탐지되지 않은 사용자이다.

##### 실험 결과
앞서 설명한 환경 세팅 중, U1의 이상행동이 탐지되는 로그인 `이체` 로그는 [이 부분](https://github.com/retrieverjo89/fraud-detect-system/blob/243174a18c47287e97ea25dac630f9e7183350fb/test_log.txt#L64) 이며, 해당 로그를 통해 실제 이상행동이라고 탐지되는 부분은 [이 부분](https://github.com/retrieverjo89/fraud-detect-system/blob/243174a18c47287e97ea25dac630f9e7183350fb/test_log.txt#L104)이며, 탐지되는데까지 걸리는 시간은 29.299 초이다.
문제에 제시된 수행 시간에는 만족하지 못하는 시간이지만, 수행 로직의 복잡도가 크거나 한 것 때문에 느린 것이 아니며, Kafka Streams 에서 Join 연산을 수행할 때 항상 일정 시간의 대기시간이 존재하였다. Kafka Streams의 설정이나, 내부 동작 방식을 좀 더 잘 안다면 충분히 해결 가능할 것이라 생각하며, 해결된다면 제시된 조건과 비슷한 시간 내에 수행될 것이라 예상한다.


### 해결하지 못한  부분
##### 요구사항 미충족
- kafka streams join 에서의 30초 대기 시간 발생 이슈 해결
   두 KStreams<>를 join 하는 과정에서 일정 시간의 대기 시간이 발생하였으며, 이 부분이 해결된다면 충분히 제시된 조건과 비슷한 시간 내에 해결될 것이라 생각한다.
- 파일로부터 로그를 불러오는 요구사항
   작업 시간의 부족으로 인해 요구사항을 만족시키지 못하였다.
- 복잡한 사용자 로그 케이스에 대응하지 못함
   입금, 출금, 이체의 로그가 복잡하게 되어있다면 구현한 로직에서 완벽하게 이상행동을 감지하지 못할 것이라 생각한다.
   
### 추가 개선해야 할 부분
- Parameterized, Randomized Log generator 추가 개발
   충분히 다양한 형태의 사용자 로그 패턴을 테스트해지 못했다고 생각한다. 무작위로 생성하는 사용자 로그를 발생시켜, 테스트를 한다면 좀 더 강건한 시스템을 만들 수 있다고 생각한다.
   
- 추후 성능 이슈를 유발할 수 있는 부분 코드 개선
   Kafka Streams를 이용하여 개발하였기 때문에 이후에 쉽게 Scale-out이 가능하다고 생각한다. 하지만 이 때 예상되는 병목 포인트가 존재한다. (ex. Redis에 연결하여 데이터를 가져오고 / 쓰는 부분) 이러한 부분을 해결(ex. Redis connection pool 을 개발하여)한다면, 이후 Scale-out을 할 때 좀 더 좋은 성능을 보이면서 확장 가능할 것이다.
   
- 테스트 작성
로그에 대한 테스트 뿐만 아니라 작성한 코드에 대한 테스트 역시 제대로 진행하지 못하였다. 개발 과정에서 일부 필요한 부분만 진행하였으며 많은 부분에서 테스트를 진행하지 못하였다.

- Kafka multi-broker 환경에서의 수행 테스트
docker-compose 를 이용하여 쉽게 Kafka 네트워크를 구축하여 테스트해보고 있었는데, 기본적인 부분 완성에 많은 시간이 소비되다 보니 multi broker, 와 같은 환경에서는 테스트해보지 못하였다. single-broker 환경에서만 테스트함.

- 실제 수행 클래스인 FdsSystemApp.java 파일이 submodule에 존재
구현한 시스템을 수행하는 클래스가 maven module의 parent 모듈이 아닌 submodule에 존재한다. 이 구조를 바꾸고자 하였으나, 다른 클래스를 제대로 import하지 못하는 상황이 발생하였다.

- maven packaging 을 이용해 executable jar를 생성해야 함
현재 진행한 수행 방법은 IntelliJ IDEA 와 같은 IDE에서 FdsSystemApp의 main 함수를 수행하는 형태이다. 이 방식을 maven의 packaging plugin 을 이용해 커맨드라인에서도 수행 가능하도록 변경해야 한다.


### references

* docker-compose 이용한 환경 세팅
    * https://github.com/florimondmanca/kafka-fraud-detector
    * https://github.com/wurstmeister/kafka-docker

* kafka & kafka stream
    * http://kafka.apache.org/documentation/
    * https://medium.com/@asce4s/send-and-receive-json-objects-with-kafka-java-client-41bfbb4de108
    * https://github.com/apache/kafka/blob/2.5/streams/examples/src/main/java/org/apache/kafka/streams/examples/pageview/PageViewTypedDemo.java
    * https://www.confluent.io/blog/crossing-streams-joins-apache-kafka/
