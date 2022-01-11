유니크한 값 만들기

### UUID

유일성 보장
- 128bit / 16bytes / 36chars
- eg. dlb1epq2-wdfb-qawe-123t-56dfkb9512mf
- String으로 바꾸면 36chars

단점
- 크기가 너무 크다.
- 시간정보가 있지만, 시간순으로 정렬되지 않는다.

UUID 개선사항
- 용량 줄이기
- 시간의 순서나 생성시간 알수있도록
- 필요한 특정 정보 담기

&nbsp;

# 실제 예제

timestamp 가 가장 먼저오면 시간순으로 정렬이 가능하다.

## 1. twitter - Snowflake - \[8bytes]

|timestamp | machine Id (datacenterId + workerId) | sequence number |
|:--:|:--:|:--:|:--:|
| 41bites | 10bits | 12bits |
| 69년 | 0~1024 | 0~4096 |

- 스칼라 언어로 구현 [github](https://github.com/twitter-archive/snowflake/tree/scala_28)
- 8byte이므로 long으로 표현 가능. (맨 처음 bit는 0)
- 1초당 4,096,000 개 생성 가능 (1000 x 2^12)

<br>

## 2. Instagram의 ID - \[8bytes]

|timestamp| logicalShardId| autoIncrement| 
|:--:|:--:|:--:|
| 41bits | 13bits | 10bytes |
| 69년 | 0~8192 | 0~1024 |

- db의 auto increment를 이용 (발급주체는 하나)
- autoIncrement 1025가 되는 경우. 1ms를 기다려서 timestamp를 1 늘린다. 

<br>

## 3. MongoDB의 ID - \[12bytes]

|timestamp| machine Id| process Id| counter|
|:--:|:--:|:--:|:--:|
|4bytes|3bytes|2bytes|3bytes|

<br>










 

