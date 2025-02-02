## Ubuntu 서버 수동 배포

프로세스 확인
```bash
ps aux | grep java
```

사용 포트 확인
```bash
sudo netstat -tulnp | grep java
```

프로세스 실행
```bash
nohup java -jar ormee-0.0.1-SNAPSHOT.jar > /home/ubuntu/ormee_service.log 2>&1 & echo $! > /home/ubuntu/ormee.pid
```

프로세스 종료
```bash
kill $(cat /home/ubuntu/ormee.pid) && rm /home/ubuntu/ormee.pid /home/ubuntu/ormee_service.log
```
