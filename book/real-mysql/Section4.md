# 4.1 MySQL 엔진 아키텍처
## 4.1.1 MySQL의 전체 구조

<img src="https://github.com/hoa0217/study-repo/assets/48192141/7b07b4ed-1b86-4604-b858-6ac6c77a6c7e" width="600">

- MySQL은 대부분의 프로그래밍 언어로부터 접근 방법을 모두 지원한다.
  - C API, JDBC, ODBC, .NET 표준 드라이버 제공하여 C/C++, PHP, 자바, 펄, 파이썬, 루비 .NET 및 코볼까지 모든 언어로 MySQL 서버에서 쿼리를 사용할 수 있다.
- MySQL서버 = MySQL 엔진 + 스토리지 엔진 

### 4.1.1.1 MySQL 엔진
- MySQL 엔진 = 커넥션 핸들러(클라이언트 접속) + SQL 파서 및 전처리기(쿼리 요청 처리) + 옵티마이저(쿼리 최적화)
- MySQL은 표준 SQL문법을 지원하기 때문에, 타 DBMS와 호환되어 실행될 수 있다.

### 4.1.1.2 스토리지 엔진
- MySQL 엔진은 요청된 SQL 분석 및 최적화등 DBMS의 두뇌를 담당
- 스토리지 엔진은 실제 데이터를 디스크 스토리지에 저장하거나 읽어오는 부분을 담당
- MySQL 서버에서 엔진은 하나지만, 스토리지 엔진은 여러개를 동시에 사용 가능
```sql
CREATE TABLE test_table (fd1 INT, fd2 INT) ENGINE=INNODB;
```
> 스토리지 엔진을 지정하면, 해당 테이블의 모든 작업은 정의된 스토리지 엔진이 처리한다.
- 각 스토리지 엔진은 성능 향상을 위해 키 캐시(MyISAM)나 InnoDB 버퍼 풀(InnoDB)같은 기능을 내장하고 있다.

### 4.1.1.3 핸들러 API
- MySQL 엔진의 쿼리 실행기에서 데이터를 쓰거나 읽어야 할 때 각 스토리지 엔진에 요청한다.
- 이를 **Handler 요청**이라 하며, 사용되는 API를 **Handler API**라고 한다.
- 스토리지 엔진 또한 이 Handler API를 사용하여 MySQL 엔진과 데이터를 주고 받는다.
  
- 아래 쿼리로 얼마나 많은 데이터 작업이 있었는지 확인 가능하다
```sql
mysql> SHOW GLOBAL STATUS LIKE 'Handler%';
+----------------------------+-----------+
| Variable_name              | Value     |
+----------------------------+-----------+
| Handler_commit             | 32304704  |
| Handler_delete             | 867       |
| Handler_discover           | 0         |
| Handler_external_lock      | 60029128  |
| Handler_mrr_init           | 0         |
| Handler_prepare            | 0         |
| Handler_read_first         | 924338    |
| Handler_read_key           | 361550629 |
| Handler_read_last          | 0         |
| Handler_read_next          | 711585244 |
| Handler_read_prev          | 0         |
| Handler_read_rnd           | 953       |
| Handler_read_rnd_next      | 3304411   |
| Handler_rollback           | 104       |
| Handler_savepoint          | 0         |
| Handler_savepoint_rollback | 0         |
| Handler_update             | 746       |
| Handler_write              | 1107501   |
+----------------------------+-----------+
18 rows in set (0.11 sec)
```

## 4.1.2 MySQL 스레딩 구조

<img src="https://github.com/hoa0217/study-repo/assets/48192141/af5282ff-ea7d-44fd-beb8-ba06ba7eeb78" width="600">

- MySQL 서버는 프로세스 기반이 아니라 스레드 기반으로 작동하며, 포그라운드(Foreground)스레드와 백그라운드(Background)스레드로 타입을 구분할 수 있다.

```sql
mysql> SELECT thread_id, name, type, processlist_user, processlist_host
       FROM performance_schema.threads ORDER BY type, thread_id;
+-----------+----------------------------------------+------------+------------------+------------------+
| thread_id | name                                   | type       | processlist_user | processlist_host |
+-----------+----------------------------------------+------------+------------------+------------------+
|         1 | thread/sql/main                        | BACKGROUND | NULL             | NULL             |
|         2 | thread/innodb/io_handler_thread        | BACKGROUND | NULL             | NULL             |
|         3 | thread/innodb/io_handler_thread        | BACKGROUND | NULL             | NULL             |
|         4 | thread/innodb/io_handler_thread        | BACKGROUND | NULL             | NULL             |
|         5 | thread/innodb/io_handler_thread        | BACKGROUND | NULL             | NULL             |
|         6 | thread/innodb/io_handler_thread        | BACKGROUND | NULL             | NULL             |
|         7 | thread/innodb/io_handler_thread        | BACKGROUND | NULL             | NULL             |
|         8 | thread/innodb/io_handler_thread        | BACKGROUND | NULL             | NULL             |
|         9 | thread/innodb/io_handler_thread        | BACKGROUND | NULL             | NULL             |
|        10 | thread/innodb/io_handler_thread        | BACKGROUND | NULL             | NULL             |
|        11 | thread/innodb/io_handler_thread        | BACKGROUND | NULL             | NULL             |
|        12 | thread/innodb/srv_purge_thread         | BACKGROUND | NULL             | NULL             |
|        13 | thread/innodb/srv_monitor_thread       | BACKGROUND | NULL             | NULL             |
|        14 | thread/innodb/srv_master_thread        | BACKGROUND | NULL             | NULL             |
|        15 | thread/innodb/srv_error_monitor_thread | BACKGROUND | NULL             | NULL             |
|        16 | thread/innodb/srv_lock_timeout_thread  | BACKGROUND | NULL             | NULL             |
|        18 | thread/sql/signal_handler              | BACKGROUND | NULL             | NULL             |
|        19 | thread/innodb/page_cleaner_thread      | BACKGROUND | NULL             | NULL             |
|    126505 | thread/sql/one_connection              | FOREGROUND | root             | localhost        |
+-----------+----------------------------------------+------------+------------------+------------------+
19 rows in set (0.12 sec)

```

- MySQL 서버에서 실행중인 스레드는 `performance_schema` 데이터베이스의 `threads` 테이블을 통해 확인할 수 있다.
- 백그라운드와 포그라운드 스레드가 type으로 표시되어 있으며, 그 중 마지막 `thread/sql/one_connection` 스레드만이 사용자의 요청을 처리하는 포그라운드 스레드이다.
- 백그라운드 스레드 개수는 MySQL 서버의 설정 내용에 따라 가변적일 수 있다.
- 동일한 이름의 스레드가 2개 이상씩 보이는 것도 MySQL 서버 설정에 의해 여러 스레드가 동일 작업을 병렬로 처리하는 경우이다.

> 참고: 여기서 소개하는 스레드 모델은 커뮤니티 에디션에서 사용되는 전통적인 스레드 모델이다. 엔터프라이즈 에디션과 Percona MySQL서버에서는 스레드 풀(Thread Pool)모델도 사용할 수 있다.   
> 가장 큰 차이점은 **포그라운드 스레드와 커넥션**의 관계이다. 전통적인 스레드 모델은 커넥션별 포그라운드 스레드가 하나씩 생성되지만 스레드풀에서는 하나의 스레드가 여러 개의 커넥션 요청을 전담한다.

### 4.1.2.1 포그라운드 스레드(클라이언트 스레드)
- 포그라운드 스레드는 최소 MySQL 서버에 접속된 **클라이언트의 수**만큼 존재하며, 각 클라이언트 사용자가 요청하는 쿼리 문장을 처리한다.
- 커넥션을 종료하면 커넥션을 담당하던 스레드는 다시 **스레드 캐시**(Thread cache)로 되돌아간다.
- 이미 스레드 캐시에 일정 개수 이상의 대기중인 스레드가 있다면, 스레드 캐시에 넣지 않고 스레드를 종료시켜 일정 개수의 스레드만 스레드 캐시에 존재하게 한다.
- 이때 스레드 캐시에 유지할 수 있는 최대 스레드 개수는 `thread_cache_size` 시스템 변수로 설정한다.
- 포그라운드 스레드는 데이터를 MySQL의 데이터 버퍼나 캐시로부터 가져오며, 버퍼나 캐시에 없는 경우 직접 디스크 또는 인덱스파일로부터 데이터를 읽어와서 처리한다.
- MyISAM 테이블은 디스크 쓰기 작업까지 포그라운드 스레드가 처리하지만, 
- InnoDB 테이블은 데이터 버퍼나 캐시까지만 포그라운드 스레드가 처리하고 나머지 버퍼로부터 디스크까지 기록하는 작업은 백그라운드 스레드가 처리한다.

> `사용자 스레드 == 포그라운드 스레드`   
> MySQL 서버에 접속하게 되면 클라이언트 요청을 처리해줄 스레드를 생성해 클라이언트에게 할당한다.
> 이 스레드는 앞단에서 클라이언트와 통신하기 때문에 포그라운드 스레드라고 하며, 사용자가 요청한작업을 처리하기 때문에 사용자 스레드라고도 한다.

### 4.1.2.2 백그라운드 스레드
MyISAM의 경우 해당 사항이 없지만, InnoDB는 아래와 같은 작업이 백그라운드로 처리된다.
- 인서트 버퍼(Insert Buffer)를 병합하는 스레드
- 로그를 디스크로 기록하는 스레드
- InnoDB 버퍼 풀의 데이터를 디스크에 기록하는 스레드
- 데이터를 버퍼로 읽어 오는 스레드
- 잠금이나 데드락을 모니터링하는 스레드

가장 중요한것은 쓰기 쓰레드(Write thread)이다.
- 쓰기 쓰레드는 로그 스레드(Log thread)와 버퍼의 데이터를 디스크로 내려쓰는 작업을 처리함.
- MySQL5.5 버전부터 데이터 쓰기 스레드와 데이터 읽기 스레드의 개수를 2개 이상 지정할 수 있게 됐으며, 
- `innodb_write_io_threads`, `innodb_read_io_threads` 시스템 변수로 설정할 수 있다.
- InnoDB에서도 읽는건 주로 클라이언트 스레드에서 처리되기때문에 읽기 쓰레드를 많이 설정할 필요가 없지만
- 쓰기 스레드는 아주 많은 작업을 백그라운드로 처리하기 때문에, 디스크는 2~4 스토리지(DAS, SAN)는 디스크를 최적으로 사용할 수 있을 만큼 충분히 설정하는 것이 좋다.

사용자의 요청을 처리하는 도중 쓰기 작업은 지연(버퍼링)되어도 처리될 수 있지만, 데이터의 읽기 작업은 절대 지연될 수 없다.
- 대부분 쓰기 작업을 버퍼링해서 일괄 처리하는 기능이 탑재되어 있다 = InnoDB
- 하지만 MyISAM은 사용자 쓰레드가 쓰기 작업까지 함께 처리하도록 설계되어 있다.
- 이러한 이유로 InnoDB에서는 INSERT, UPDATE, DELETE 쿼리로 데이터가 변경되는 경우 데이터가 디스크로 완전히 저장될 때까지 기다리지 않아도 된다.
- 하지만 MyISAM에서 일반적인 쿼리는 쓰기 버퍼링을 사용할 수 없다.

## 4.1.3 메모리 할당 및 사용 구조

<img width="600" alt="스크린샷 2024-04-16 오후 11 28 22" src="https://github.com/hoa0217/study-repo/assets/48192141/95e07909-9188-4db3-bb00-69afe5b22be0">

- 글로벌 메모리 영역 : 모든 메모리 공간은 MySQL 서버가 시작되면서 시스템 변수로 설정해 둔 만큼 OS로부터 할당된다.
- 로컬 메모리 영역

### 4.1.3.1 글로벌 메모리 영역
일반적으로 하나의 메모리 공간만 할당된다. 필요에 의해 2개 이상을 받을 수 도 있지만 클라이언트 스레드 수와 무관하다. N개라 하더라도 모든 스레드에 의해 공유된다.

[대표적인 글로벌 메모리영역]
- 테이블 캐시
- InnoDB 버퍼풀
- InnoDB 어댑티브 해시 인덱스
- InnoDB 리두 로그 버퍼

### 4.1.3.2 로컬 메모리 영역
세션 메모리 영역이라고도 표현하며, 클라이언트 스레드가 쿼리를 처리하는데 사용되는 메모리 영역이다. = `클라이언트 메모리 영역`
- 클라이언트가 MySQL 서버에 접속하면 클라이언트 커넥션으로부터 요청을 처리하기 위해 스레드를 하나씩 할당하게 되며, 해당 스레드가 사용하는 메모리 공간이다.
- 서버와의 커넥션을 세션이라고 하기 때문에 로컬 메모리 영역을 `세션 메모리 영역`이라고도 표현한다.

로컬 메모리는 각 클라이언트 스레드별로 독립적으로 할당되며 절대 공유되지 않는다.
- 글로벌 메모리영역의 크기는 주의해서 설정해야하지만, 로컬 메모리 영역은 크게 신경쓰지 않고 설정한다.
- 가능성은 희박하지만 서버가 메모리 부족으로 멈춰 버릴 수 도 있으므로 적절한 메모리 공간을 설정하는 것이 중요하다.

각 쿼리의 용도별로 필요할 때만 메모리 공간이 할당되고 필요하지 않은 경우 할당되지 않는다.
- 커넥션이 열려 있는 동안 계속 할당된 상태로 남아있는 공간도 있고 (커넥션 버퍼나 결과 버퍼)
- 쿼리를 실행하는 순간에만 할당했다가 다시 해제하는 공간도 있다. (소트 버퍼나 조인 버퍼)

[대표적인 로컬 메모리 영역]
- 정렬 버퍼(Sort Buffer)
- 조인 버퍼
- 바이너리 로그 캐시
- 네트워크 버퍼

## 4.1.4 플러그인 스토리지 엔진 모델

<img width="600" alt="스크린샷 2024-04-16 오후 11 51 10" src="https://github.com/hoa0217/study-repo/assets/48192141/388a29b2-b748-4cf9-bdd7-465d422005ed">

MySQL은 플러그인 모델이다.
- 수많은 사용자의 요구 조건을 만족시키기 위해 사용할 수 있는 플러그인이 다수 있다.
- 스로리지 엔진뿐만 아니라 검색어 파서, 사용자 인증 등 모두 플러긴으로 구현되어 제공된다.

<img width="600" alt="스크린샷 2024-04-16 오후 11 55 29" src="https://github.com/hoa0217/study-repo/assets/48192141/794af5fe-5cdd-4bc5-8354-091f3f61b1f9">

MySQL에서 쿼리가 실행되는 과정을 보면 대부분의 작업은 MySQL 엔진에서 처리되고 마지막 `데이터 읽기/쓰기`작업만 스토리지 엔진에 의해 처리된다.
- `데이터 읽기/쓰기`작업은 대부분 1건의 레코드 단위로 처리된다.
- MySQL 서버에서 MySQL 엔진(사람)은 스토리지 엔진(자동차)을 조정하기 위해 핸들러를 사용한다. 
  - Handler: 어떤 기능을 호출하기 위해 사용하는 운전대와 같은 역할을 하는 객체
- MySQL서버의 상태 변수 중 `Handler_`로 시작하는 것이 많다.
  - `Handler_`로 시작하는 상태변수: MySQL엔진이 각 스토리지 엔진에게 보낸 명령의 횟수

다른 스토리지 엔진을 사용하는 테이블이라도 MySQL엔진 처리내용은 대부분 동일하며, `데이터 읽기/쓰기` 영역의 처리만 차이가 있다.
- 실질적인 Group By나 Order by 등 복잡한 처리는 스토리지 엔진영역이 아닌 MySQL 엔진의 쿼리 실행기에서 처리된다.

#### 중요한 내용
- 하나의 쿼리 작업은 여러 하위 작업으로 나뉘는데, 각 하위 작업이 MySQL엔진에서 처리되는지 스토리지 엔진에서 처리되는지 구분할 줄 알아야한다.

*MySQL서버에서 지원되는 스토리지 엔진 보기*
```sql
mysql> SHOW ENGINES;
+--------------------+---------+----------------------------------------------------------------+--------------+------+------------+
| Engine             | Support | Comment                                                        | Transactions | XA   | Savepoints |
+--------------------+---------+----------------------------------------------------------------+--------------+------+------------+
| InnoDB             | DEFAULT | Supports transactions, row-level locking, and foreign keys     | YES          | YES  | YES        |
| CSV                | YES     | CSV storage engine                                             | NO           | NO   | NO         |
| MRG_MYISAM         | YES     | Collection of identical MyISAM tables                          | NO           | NO   | NO         |
| BLACKHOLE          | YES     | /dev/null storage engine (anything you write to it disappears) | NO           | NO   | NO         |
| MyISAM             | YES     | MyISAM storage engine                                          | NO           | NO   | NO         |
| MEMORY             | YES     | Hash based, stored in memory, useful for temporary tables      | NO           | NO   | NO         |
| ARCHIVE            | YES     | Archive storage engine                                         | NO           | NO   | NO         |
| FEDERATED          | NO      | Federated MySQL storage engine                                 | NULL         | NULL | NULL       |
| PERFORMANCE_SCHEMA | YES     | Performance Schema                                             | NO           | NO   | NO         |
+--------------------+---------+----------------------------------------------------------------+--------------+------+------------+
9 rows in set (0.31 sec)
```

[Support 컬럼]
- YES: MySQL 서버에 해당 스토리지 엔진이 포함되어있고, 사용 가능으로 활성화된 상태임
- DEFAULT: YES와 동일한 상태이지만 필수 스토리지 엔진임을 의미
  - 이 스토리지 엔진이 없으면 MySQL이 시작되지 않을 수 있음을 의미
- NO: 현재 MySQL 서버에 포함되지 않았음을 의미
- DISABLED: 현재 MySQL 서버에 포함되었지만 파라미터에 의해 비활성화된 상태

> NO로 표시된 스토리지엔진을 사용하려면 MySQL 서버를 다시 빌드(컴파일)해야한다. 하지만 서버만 준비되어있다면 플러그인 형태로 빌드된 스토리지 엔진 라이브러리를 다운로드해서 끼워넣을 수 있다.

*플로그인 확인 쿼리*
```sql
mysql> show plugins;
+----------------------------+----------+--------------------+---------+---------+
| Name                       | Status   | Type               | Library | License |
+----------------------------+----------+--------------------+---------+---------+
| binlog                     | ACTIVE   | STORAGE ENGINE     | NULL    | GPL     |
| mysql_native_password      | ACTIVE   | AUTHENTICATION     | NULL    | GPL     |
| mysql_old_password         | ACTIVE   | AUTHENTICATION     | NULL    | GPL     |
| sha256_password            | ACTIVE   | AUTHENTICATION     | NULL    | GPL     |
| MyISAM                     | ACTIVE   | STORAGE ENGINE     | NULL    | GPL     |
| MRG_MYISAM                 | ACTIVE   | STORAGE ENGINE     | NULL    | GPL     |
| MEMORY                     | ACTIVE   | STORAGE ENGINE     | NULL    | GPL     |
| CSV                        | ACTIVE   | STORAGE ENGINE     | NULL    | GPL     |
| ARCHIVE                    | ACTIVE   | STORAGE ENGINE     | NULL    | GPL     |
| InnoDB                     | ACTIVE   | STORAGE ENGINE     | NULL    | GPL     |
| INNODB_TRX                 | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_LOCKS               | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_LOCK_WAITS          | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_CMP                 | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_CMP_RESET           | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_CMPMEM              | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_CMPMEM_RESET        | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_CMP_PER_INDEX       | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_CMP_PER_INDEX_RESET | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_BUFFER_PAGE         | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_BUFFER_PAGE_LRU     | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_BUFFER_POOL_STATS   | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_METRICS             | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_FT_DEFAULT_STOPWORD | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_FT_DELETED          | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_FT_BEING_DELETED    | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_FT_CONFIG           | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_FT_INDEX_CACHE      | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_FT_INDEX_TABLE      | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_SYS_TABLES          | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_SYS_TABLESTATS      | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_SYS_INDEXES         | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_SYS_COLUMNS         | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_SYS_FIELDS          | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_SYS_FOREIGN         | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_SYS_FOREIGN_COLS    | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_SYS_TABLESPACES     | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| INNODB_SYS_DATAFILES       | ACTIVE   | INFORMATION SCHEMA | NULL    | GPL     |
| BLACKHOLE                  | ACTIVE   | STORAGE ENGINE     | NULL    | GPL     |
| FEDERATED                  | DISABLED | STORAGE ENGINE     | NULL    | GPL     |
| PERFORMANCE_SCHEMA         | ACTIVE   | STORAGE ENGINE     | NULL    | GPL     |
| partition                  | ACTIVE   | STORAGE ENGINE     | NULL    | GPL     |
+----------------------------+----------+--------------------+---------+---------+
42 rows in set (0.56 sec)
```
- MySQL 서버는 스토리지 엔진 뿐 아니라 다양한 기능을 플러그인으로 지원한다.

## 4.1.5 컴포넌트
MySQL 8.0 부터는 기존 플러그인 아키텍처를 대체하기 위해 컴포넌트 아키텍처를 지원한다.

플러그인은 아래와 같은 단점이 있으며, 컴포넌트는 해당 단점을 보완하였다.
- 플러그인은 오직 MySQL 서버와 인터페이스 할 수 있고, 플러그인끼리는 통신할 수 없음
- 플러그인은 MySQL 서버의 변수나 함수를 직접 호출하기 때문에 안전하지 않음(캡슐화 안됨)
- 플러그인은 상호 의존 관계를 설정할 수 없어서 초기화가 어려움

MySQL 5.7버전까지는 비밀번호 검증 기능이 플러그인 형태로 제공되었지만, MySQL 8.0은 컴포넌트로 개선되었다.

```sql
// validate_password 설치
install component 'file://component_validate_password';

// 설치된 컴포넌트 확인
select * from sql.componet; 
```

> 컴포넌트 사용전엔 관련 메뉴얼을 살펴보자.

## 4.1.6 쿼리 실행 구조

<img width="600" alt="스크린샷 2024-04-17 오후 8 32 21" src="https://github.com/f-lab-edu/modoospace/assets/48192141/c1d2a03e-a4f6-4788-8696-873c040db3e1">

> 쿼리 실행 관점에서의 MySQL 구조

### 4.1.6.1 쿼리 파서
- 사용자 요청으로 들어온 쿼리를 토큰(MySQL이 인식할 수 있는 최소 단위의 어휘나 기호)로 분리해 **트리형태의 구조**로 만들어내는 작업을 한다.
- 이 과정에서 쿼리의 기본 문법 오류를 발견하고 사용자에게 오류 메시지를 전달한다.

### 4.1.6.2 전처리기
- 파서 과정에서 만들어진 **파서 트리**를 기반으로 쿼리의 구조적 문제점을 확인한다.
- 이 과정에서 각 토큰을 테이블 이름, 칼럼 이름 또는 내장 함수와 같은 개체를 매핑해 객체의 존재 여부와 객체의 접근 권한등을 확인한다.
- 실제 존재하지 않거나 권한상 사용할 수 없는 개체의 토큰은 이 단계에서 걸러진다.

### 4.1.6.3 옵티마이저
- 사용자 요청으로 들어온 쿼리를 저렴한 비용으로 가장 빠르게 처리할지를 결정하는 역할 (DBMS 두뇌)
- 어떻게하면 옵티마이저가 더 나은 선택을 하도록 유도하는가를 아는것이 중요.

### 4.1.6.4 실행엔진
- 옵티마이저가 두뇌라면 실행 엔진과 핸들러는 손과 발이다.
- 옵티마이저 = 회사 경영진, 실행 엔진 = 중간 관리자, 핸들러 = 실무자

옵티마이저가 GROUP BY를 처리하기 위해 임시 테이블을 사용하기로 결정했다면?
1. 실행 엔진이 핸들러에게 임시 테이블을 만들라고 요청
2. 다시 실행 엔진은 WHERE 절에 일치하는 레코드를 읽어오라고 핸들러에게 요청
3. 읽어온 레코드들을 1번에서 준비한 임시 테이블로 저장하라고 다시 핸들러에게 요청
4. 데이터가 준비된 임시 테이블에서 필요한 방식으로 데이터를 읽어 오라고 핸들러에게 다시 요청
5. 최정적으로 실행 엔진은 결과를 사용자나 다른 모듈로 넘김

> 실행 엔진은 만들어진 계획대로 각 핸들러에게 요청해서 받은 결과를 다른 핸들러 요청의 입력으로 연결하는 역할을 수행한다.

### 4.1.6.5 핸들러(스토리지 엔진)
- 핸들러는 MySQL 서버 가장 밑단에서 실행 엔진의 요청에 따라 데이터를 디스크로 저장하고 읽어오는 역할을 담당한다.
- 결국 핸들러는 **스토리지 엔진**을 의미한다.

## 4.1.7 복제
- MySQL 서버에서 복제(Replication)은 매우 중요한 역할을 담당한다. (16장에서 자세히)

## 4.1.8 쿼리 캐시
- MySQL 서버에서 쿼리 캐시(Query Cache)는 빠른 응답을 필요로 하는 웹에서 매우 중요한 역할을 담당했다.
- 쿼리 캐시는 SQL 실행 결과를 메모리에 캐시하고, 동일 SQL 쿼리가 실행되면 즉시 결과를 반환한다.
- 하지만 테이블 데이터가 변경되면 저장된 결과 중 변경된 테이블과 관련된 것들은 모두 삭제해야 했다. (Invalidate)
  - 이는 심각한 동시 처리 성능 저하와 많은 버그의 원인이 되기도 했다.  
- 결국 8.0으로 올라오면서 쿼리 캐시는 기능에서 완전히 제거되었다.

## 4.1.9 스레드 풀
- 엔터프라이즈 에디션은 스레드풀 기능을 제공하지만, 커뮤니티 에디션은 지원하지 않는다.
- 따라서 엔터프라이즈 대신 Percona Server에서 제공하는 스레드 풀 기능을 살펴보자.
> 엔터프라이즈는 스레드풀기능이 서버에 내장되어있지만, Percona Server는 플러그인 형태로 작동한다.   
> 만약 커뮤니티에서도 사용하고자 한다면, Percona Server에서 스레드 풀 플러그인을 설치해서 사용하면된다.

스레드 풀은 내부적으로 사용자의 요청을 처리하는 **스레드 개수를 줄여서**, 동시에 처리되는 요청이 많다하더라도
MySQL서버의 CPU가 제한된 개수의 스레드 처리에만 집중할 수 있게 해 **서버의 자원 소모를 줄이는 것**이 목적이다.
- 사실 스레드풀이 눈에 띄는 성능향상을 보여주진 않는다.
- 동시에 실행중인 스레드들을 CPU가 최대한 잘 처리해낼 수준으로 줄여서 빨리 처리하게 하는 기능이다.
- 따라서 스케줄링 과정에 CPU시간을 제대로 확보하지 못한경우 쿼리 처리가 더 느려질 수 도 있다.
- 물론 제한된 수의 스레드만으로 CPU가 처리한다면, CPU의 **프로세서 친화도**(Processor affinity)도 높이고 OS입장에서 불필요한 **컨텍스트 스위치**를 줄여 오버헤드를 낮출 수 있다.
> ❓ CPU의 프로세서 친화도가 뭐지?

#### Percona Server 스레드풀
- 기본적으로 CPU 코어의 개수만큼 스레드 그룹을 생성한다. (`thread_pool_size` 시스템 변수로 조정 가능)
  - 일반적으로 CPU 코어의 개수와 맞추는 것이 **CPU 프로세서 친화도**를 높이는데 좋다.
- MySQL 서버가 처리해야 할 요청이 생기면 스레드 풀로 처리를 이관한다.
- 만약 스레드 풀이 처리중인 작업이 있는 경우 `thread_pool_oversubscribe`(기본값 3)에 설정된 개수만큼 추가로 더 받아들여 처리한다.
  - 이 값이 너무 크면 스케줄링해야할 스레드가 많아져 스레드 풀이 비효율적으로 작동할 수 있다.
- 스레드 그룹의 모든 스레드가 일을 처리하고 있다면, 스레드 풀은 해당 스레드 그룹에 **새로운 작업 스레드**(Worker thread)를 추가할지, 아니면 기존 작업 스레드가 완료될 때까지 대기 여부를 판단해야한다.
  - 스레드 풀의 **타이머 스레드**는 주기적으로 스레드 그룹의 상태를 체크해서 `thread_pool_stall_limit`에 정의된 밀리초만큼 스레드가 작업을 끝내지 못하면 새로운 스레드를 생성해 그룹에 추가한다.
  - 전체 스레드 풀에 있는 스레드의 개수는 `thread_pool_max_threads` 개수를 넘길 수 없다.

즉, 모든 스레드 그룹의 스레드가 각자 작업을 처리하고 있는 상태에서 새로운 요청이 들어오더라도 스레드 풀은 `thread_pool_stall_limit` 시간 동안 기다려야만 새로운 요청을 처리할 수 있다.
- 따라서 응답 시간이 아주 민감한 서비스라면 `thread_pool_stall_limit`를 적절히 낮춰 설정해야하지만, 0에 가까운 값으로 설정하는 것은 권장하지 않는다. ➡️ 이럴경우 스레드 풀을 사용하지 않는게 나음
> ❓ 스레드 그룹, 스레드 풀의 차이가 뭐지?

#### Percona Server 스레드풀의 큐
- Percona Server 스레드풀 플러그인은 **선순위 큐**와 **후순위 큐**를 이용해 특정 트랜잭션이나 쿼리를 우선적으로 처리할 수 있는 기능을 제공한다.
- 먼저 시작된 트랜잭션 내 속한 SQL을 빨리 처리해주면 해당 트랜잭션이 가지고 있던 **잠금이 빨리 해제되고 잠금 경합을 낮춰서 전체적인 처리 성능을 높일 수 있다.**
> ❓ 잠금 경합이 뭐지?

<img width="600" alt="스크린샷 2024-04-17 오후 11 31 26" src="https://github.com/f-lab-edu/modoospace/assets/48192141/1364f108-d877-46b0-95ac-e4ae11551b83">

## 4.1.10 트랜잭션 지원 메타데이터

#### 테이블이 깨졌다?
- 데이터베이스 서버에서 **테이블 구조 정보**와 스토어드 프로그램등의 정보를 딕셔너리 or 메타 데이터라고 한다.
- MySQL 5.7버전까지 테이블 구조를 FRM 파일에 저장하고 일부 프로그램또한 파일로 관리했다.
- 하지만 파일 기반의 메타데이터는 생성/변경 시 트랜잭셕을 지원하지 않기 때문에 생성/변경 시 서버가 비정상적으로 종료되면 일관되지 않은 상태로 남는 문제가 있었다.
- 이를 DB나 테이블이 깨졌다라고 표현한다.

#### InnoDB 시스템 테이블
- MySQL8.0부터 메타 데이터를 전부 InnoDB의 테이블로 저장하도록 개선했다. 
- MySQL 서버가 작동하는데 기본적으로 필요한 테이블을 묶어 **시스템 테이블**이라고 한다.
  - ex) 사용자 인증과 권한에 관련된 테이블 
- 시스템 테이블과 메타데이터 모두 모아 `mysql` DB에 저장하고 있다.
- `mysql` DB는 통째로 `mysql.ibd`라는 이름의 테이블 스페이스에 저장된다. 
- 이를 통해 스키마 변경 작업 중 MySQL 서버가 비정상적으로 종료되어도 스키마 변경은 **완전한 성공** 또는 **완전한 실패**로 정리된다.

#### information_schema
- 메타 데이터를 `mysql` DB에 저장한다고 하지만, 실제 테이블 목록을 살펴보면 보이지 않을 것이다.
  - 사용자가 수정하지 못하게 보여주지 않지만 실제로 존재한다.
- 대신 `information_schema`에서 `TABLES` 테이블을 제공해준다.
  - 이는 `mysql` DB의 `tables`라는 이름의 테이블을 참조하고 있는 뷰이다.

```sql
mysql> select * from information_schema.tables limit 10;
+---------------+--------------------+---------------------------------------+-------------+--------+---------+------------+------------+----------------+-------------+-----------------+--------------+-----------+----------------+---------------------+---------------------+------------+-----------------+----------+----------------+---------------+
| TABLE_CATALOG | TABLE_SCHEMA       | TABLE_NAME                            | TABLE_TYPE  | ENGINE | VERSION | ROW_FORMAT | TABLE_ROWS | AVG_ROW_LENGTH | DATA_LENGTH | MAX_DATA_LENGTH | INDEX_LENGTH | DATA_FREE | AUTO_INCREMENT | CREATE_TIME         | UPDATE_TIME         | CHECK_TIME | TABLE_COLLATION | CHECKSUM | CREATE_OPTIONS | TABLE_COMMENT |
+---------------+--------------------+---------------------------------------+-------------+--------+---------+------------+------------+----------------+-------------+-----------------+--------------+-----------+----------------+---------------------+---------------------+------------+-----------------+----------+----------------+---------------+
| def           | information_schema | CHARACTER_SETS                        | SYSTEM VIEW | MEMORY |      10 | Fixed      |       NULL |            384 |           0 |        16434816 |            0 |         0 |           NULL | 2024-04-17 23:53:11 | NULL                | NULL       | utf8_general_ci |     NULL | max_rows=43690 |               |
| def           | information_schema | COLLATIONS                            | SYSTEM VIEW | MEMORY |      10 | Fixed      |       NULL |            231 |           0 |        16704765 |            0 |         0 |           NULL | 2024-04-17 23:53:11 | NULL                | NULL       | utf8_general_ci |     NULL | max_rows=72628 |               |
| def           | information_schema | COLLATION_CHARACTER_SET_APPLICABILITY | SYSTEM VIEW | MEMORY |      10 | Fixed      |       NULL |            195 |           0 |        16357770 |            0 |         0 |           NULL | 2024-04-17 23:53:11 | NULL                | NULL       | utf8_general_ci |     NULL | max_rows=86037 |               |
| def           | information_schema | COLUMNS                               | SYSTEM VIEW | MyISAM |      10 | Dynamic    |       NULL |              0 |           0 | 281474976710655 |         1024 |         0 |           NULL | 2024-04-17 23:53:11 | 2024-04-17 23:53:11 | NULL       | utf8_general_ci |     NULL | max_rows=2794  |               |
| def           | information_schema | COLUMN_PRIVILEGES                     | SYSTEM VIEW | MEMORY |      10 | Fixed      |       NULL |           2565 |           0 |        16757145 |            0 |         0 |           NULL | 2024-04-17 23:53:11 | NULL                | NULL       | utf8_general_ci |     NULL | max_rows=6540  |               |
| def           | information_schema | ENGINES                               | SYSTEM VIEW | MEMORY |      10 | Fixed      |       NULL |            490 |           0 |        16574250 |            0 |         0 |           NULL | 2024-04-17 23:53:11 | NULL                | NULL       | utf8_general_ci |     NULL | max_rows=34239 |               |
| def           | information_schema | EVENTS                                | SYSTEM VIEW | MyISAM |      10 | Dynamic    |       NULL |              0 |           0 | 281474976710655 |         1024 |         0 |           NULL | 2024-04-17 23:53:11 | 2024-04-17 23:53:11 | NULL       | utf8_general_ci |     NULL | max_rows=619   |               |
| def           | information_schema | FILES                                 | SYSTEM VIEW | MEMORY |      10 | Fixed      |       NULL |           2659 |           0 |        16743723 |            0 |         0 |           NULL | 2024-04-17 23:53:11 | NULL                | NULL       | utf8_general_ci |     NULL | max_rows=6309  |               |
| def           | information_schema | GLOBAL_STATUS                         | SYSTEM VIEW | MEMORY |      10 | Fixed      |       NULL |           3268 |           0 |        16755036 |            0 |         0 |           NULL | 2024-04-17 23:53:11 | NULL                | NULL       | utf8_general_ci |     NULL | max_rows=5133  |               |
| def           | information_schema | GLOBAL_VARIABLES                      | SYSTEM VIEW | MEMORY |      10 | Fixed      |       NULL |           3268 |           0 |        16755036 |            0 |         0 |           NULL | 2024-04-17 23:53:11 | NULL                | NULL       | utf8_general_ci |     NULL | max_rows=5133  |               |
+---------------+--------------------+---------------------------------------+-------------+--------+---------+------------+------------+----------------+-------------+-----------------+--------------+-----------+----------------+---------------------+---------------------+------------+-----------------+----------+----------------+---------------+
10 rows in set (0.07 sec)
```

```sql
mysql> select * from mysql.tables;
ERROR 1146 (42S02): Table 'mysql.tables' doesn't exist
```
> 책에서는 접근이 거절됨이라고 출력된다고하지만 나는 없다고 나온다.
> ```sql
> EROR 3554 (HY000): Access to data dictionary table 'mysql. tables' is rejected.
> ```

#### InnoDB외 스토리지 엔진
- InnoDB외 MyISAM, CSV 등의 스토리지 엔진을 사용하는 테이블들은 SDI(Serialized Dictionary Information)파일을 사용한다.
- SDI는 기존에 *.FRM 파일과 동일한 역할을 한다.
- SDI는 이름 그대로 직렬화를 위한 포맷이므로, InnoDB 테이블 구조도 SDI로 변환할 수 있다.
