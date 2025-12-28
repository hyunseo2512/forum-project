# WEB PROJECT 
---

### Open Source Project
     
**사이트 주제** :

- [ ] 이커머스 플랫폼 
- [ ] 핀테크 & 디지털 결제 솔루션 
- [ ] AI 기반 웹 서비스 
- [ ] 온라인 교육 플랫폼
- [x] 커뮤니티 & 포럼 플랫폼 


**참고 플랫폼** : 커뮤니티 웹 사이트 참고

* reddit : https://www.reddit.com/ 
* stack overflow : https://stackoverflow.com/
* dcinside : https://www.dcinside.com/
* 번개장터 : https://m.bunjang.co.kr/


**사이트 이름** : ?

기존 유사 사이트에 비해 다른 점 : 
* 닉네임 사용 불가능(실명제)
* 광고X

사이트가 제공하는 기능 : 
- 로그인/회원가입 
- 게시판 기능 (업로드, 삭제, 댓글, 첨부파일, 좋아요 등)
- 팔로우 기능 + 메시지 기능

### 스택 : 
- Figma, Git
- Spring Boot + JPA 
- MySql

### 사용한 탬플릿 : 
* https://bootstrapthemes.co/items/free-bootstrap-templates/
* https://startbootstrap.com/themes
* https://www.creative-tim.com/bootstrap-themes/free


## 📁필수 템플릿
<pre>
www.community.demo
├── board
│   └── detail
├── fragments
│   ├── footer
│   └── header
├── index
├── layout
│   └── layout
└── user
    ├── login
    └── signup
</pre>


## 라이브러리

* Spring Boot Devtools
* Lombok
* Spring Configuration Processor
* Spring Web
* Thymeleaf
* MySql Driver
* Spring Data JPA
* log4jdbc
* thymeleaf-layout

## Test DB

```sql
create database web1400;

use mysql;

create user 'webUser'@'localhost' identified by 'mysql';
grant all privileges on web1400.* to 'webUser'@'localhost';
flush privileges;

```
