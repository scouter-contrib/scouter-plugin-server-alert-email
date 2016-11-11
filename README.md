# scouter-plugin-server-alert-email
### Scouter server plugin to send a alert via email

- 본 프로젝트는 스카우터 서버 플러그인으로써 서버에서 발생한 Alert 메시지를 Email로 발송하는 역할을 한다.
- 현재 지원되는 Alert의 종류는 다음과 같다.
	- Agent의 CPU (warning / fatal)
	- Agent의 Memory (warning / fatal)
	- Agent의 Disk (warning / fatal)
	- 신규 Agent 연결
	- Agent의 연결 해제
	- Agent의 재접속
	- 응답시간의 임계치 초과
	- GC Time의 임계치 초과
	- Thread 갯수의 임계치 초과

### Properties (스카우터 서버 설치 경로 하위의 conf/scouter.conf)
* **_ext\_plugin\_email\_send_alert_** : Email 발송 여부 (true / false) - 기본 값은 false
* **_ext\_plugin\_email\_debug_** : 로깅 여부 - 기본 값은 false
* **_ext\_plugin\_email\_level_** : 수신 레벨(0 : INFO, 1 : WARN, 2 : ERROR, 3 : FATAL) - 기본 값은 0
* **_ext\_plugin\_email\_smtp_hostname_** : SMTP 서버의 IP 또는 Domain - 기본 값은 smtp.gmail.com
* **_ext\_plugin\_email\_smtp_port_** : SMTP Port - 기본 값은 587
* **_ext\_plugin\_email\_username_** : Email 사용자 계정
* **_ext\_plugin\_email\_password_** : Email 사용자 비밀번호
* **_ext\_plugin\_email\_tls_enabled_** : TLS 사용 여부 - 기본 값은 true
* **_ext\_plugin\_email\_from_address_** : Email 발신자 계정
* **_ext\_plugin\_email\_to_address_** : Email 수신 계정(다중 사용자 지정 시 ',' 구분자 사용)
* **_ext\_plugin\_email\_cc_address_** : Email 참조 수신 계정(다중 사용자 지정 시 ',' 구분자 사용)
* **_ext\_plugin\_elapsed\_time_threshold_** : 응답시간의 임계치 (ms) - 기본 값은 0으로, 0일때 응답시간의 임계치 초과 여부를 확인하지 않는다.
* **_ext\_plugin\_gc\_time_threshold_** : GC Time의 임계치 (ms) - 기본 값은 0으로, 0일때 GC Time의 임계치 초과 여부를 확인하지 않는다.
* **_ext\_plugin\_thread\_count_threshold_** : Thread Count의 임계치 - 기본 값은 0으로, 0일때 Thread Count의 임계치 초과 여부를 확인하지 않는다.



* Example
```
# External Interface (Email)
ext_plugin_email_send_alert=true
ext_plugin_email_debug=true
ext_plugin_email_level=0
ext_plugin_email_smtp_hostname=smtp.gmail.com
ext_plugin_email_smtp_port=587
ext_plugin_email_username=noreply@scouter.com
ext_plugin_email_password=password
ext_plugin_email_tls_enabled=true
ext_plugin_email_from_address=noreply@scouter.com
ext_plugin_email_to_address=receiver1@scouter.com,receiver2@scouter.com
ext_plugin_email_cc_address=ccreceiver@yopmail.com

ext_plugin_elapsed_time_threshold=5000
ext_plugin_gc_time_threshold=5000
ext_plugin_thread_count_threshold=300
```

### Dependencies
* Project
    - scouter.common
    - scouter.server
* Library
    - activation-1.1.1.jar
    - commons-email-1.4.jar
    - javax.mail-1.5.2.jar
    
### Build & Deploy
* Build
    - 프로젝트 내의 build.xml을 실행한다.
    
* Deploy
    - 빌드 후 프로젝트 하위에 out 디렉토리가 생기며, 디펜던시 라이브러리와 함께 scouter-plugin-server-alert-email.jar 파일을 복사하여 스카우터 서버 설치 경로 하위의 lib/ 폴더에 저장한다.
