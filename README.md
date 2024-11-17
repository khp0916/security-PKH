# SpringBoot-Security-Park Kyung Hoon
---
## 인증/인가 Security
  + **JWT 토큰**을 이용한 **인증**
  + 사용자 **역할**에 따라 **인가** ( ADMIN, USER )
---
## 📌 목차
[1. 주요 기능](#-주요-기능)

[2. 플로우 차트](#-플로우-차트)

[3. 인증 흐름](#-인증-흐름)

[4. 인가 흐름](#-인가-흐름)

---
## 💡 주요 기능
+ **JWT 토큰**
  1. 로그인 시 현재 시간을 가져와 만료 시간을 계산하여 토큰의 만료 시간을 설정
  2. 토큰이 유효한지, 만료되었는지, 잘못된 토큰인지 검증
     + 토큰이 만료되었다면 `Refresh-Token`을 이용해 토큰 재발급
     + `Refresh-Token` 마저 만료되었다면 `LocalStorage`에 저장된 `Refresh-Token`와 `Access-Token` 을 삭제하고 재로그인 요청
  3. 유효한 토큰이라면 해당 토큰을 기반으로 하여 사용자 정보 조회

+ **Role에 따른 인가**
  1. 회원가입시 `User_id`가 `ADMIN`일 경우에만 role이 `ROLE_ADMIN`으로 설정, 이외에는 기본적으로 전부 `ROLE_USER`
  2. 메인 페이지에서 `admin-content`와 `member-content`로 구분하여 토큰으로 조회한 `Role`이 `ROLE_ADMIN`인 관리자 계정만 모두 열람 가능
  3. `Role`이 `ROLE_USER`인 사용자의 경우 제한된 콘텐츠 열람 시도할시 액세스 거부
---
## 🗂 플로우 차트
```mermaid
---
SpringBoot-Security-PKH
---
stateDiagram-v2
[*] --> 회원가입
회원가입 --> Admin : UserId가 Admin
회원가입 --> 로그인&nbsp;시도
로그인&nbsp;시도 --> 토큰&nbsp;발급 : 인증정보 확인
토큰&nbsp;발급 --> 인가&nbsp;제한 : 토큰에서 정보 추출
인가&nbsp;제한 --> 관리자&nbsp;전용&nbsp;컨텐츠를&nbsp;포함한&nbsp;모든&nbsp;컨텐츠&nbsp;열람
Admin --> 관리자&nbsp;전용&nbsp;컨텐츠를&nbsp;포함한&nbsp;모든&nbsp;컨텐츠&nbsp;열람
회원가입 --> User : UserId가 Admin이 아님
User --> 관리자&nbsp;전용&nbsp;컨텐츠를&nbsp;제외한&nbsp;일반&nbsp;컨텐츠&nbsp;열람
인가&nbsp;제한 --> 관리자&nbsp;전용&nbsp;컨텐츠를&nbsp;제외한&nbsp;일반&nbsp;컨텐츠&nbsp;열람
관리자&nbsp;전용&nbsp;컨텐츠를&nbsp;제외한&nbsp;일반&nbsp;컨텐츠&nbsp;열람 --> 제한된&nbsp;컨텐츠&nbsp;보기&nbsp;버튼
비로그인 --> 모든&nbsp;컨텐츠&nbsp;열람&nbsp;불가
모든&nbsp;컨텐츠&nbsp;열람&nbsp;불가 --> 제한된&nbsp;컨텐츠&nbsp;보기&nbsp;버튼
제한된&nbsp;컨텐츠&nbsp;보기&nbsp;버튼 --> 로그인&nbsp;페이지로&nbsp;이동
```

---
## 🔐 인증 흐름

### 1. 메인 페이지에서 접속시 비로그인 상태에서 일반 사용자 콘텐츠의 리스트만 보여집니다.
![KakaoTalk_20241115_174017412](https://github.com/user-attachments/assets/11b9bb80-20c2-4b3f-a5f1-1a5179b6d11f)

### 2. 일반 사용자 콘텐츠와 제한된 컨텐츠 보기 클릭시 로그인이 필요하다는 알림이 뜨고 로그인 페이지로 전환됩니다.
![KakaoTalk_20241115_174017412_04](https://github.com/user-attachments/assets/97bf12c7-f1e2-40dc-8287-39dc4b787904)

### 2-1. 가입된 아이디가 있다면 로그인을 진행합니다.
![KakaoTalk_20241115_174017412_02](https://github.com/user-attachments/assets/7e2b9702-0332-4fa4-93d3-ebb4ec5be93e)


### 2-2. 가입된 아이디가 없다면 회원가입 버튼을 누르고 회원가입을 진행합니다.
![KakaoTalk_20241115_174017412_01](https://github.com/user-attachments/assets/8a7aa096-3cc8-4e7c-864f-b92d3b287330)
---
## 🔑 인가 흐름
  1. 로그인 시 `JWT` 토큰이 발급되고 **토큰에 담긴 사용자에 정보**를 참고하여 페이지에 노출시킵니다.
  2. 토큰에 담긴 사용자의 `Role`에 따라 **페이지 접근 인가**가 달라집니다.
  3. `Role`이 `User`인 **일반적인 사용자**의 경우 **허용된 범위 내의 콘텐츠만 열람이 가능**하고 **권한이 없는 콘텐츠 접근 시도시 거부**됩니다.
  4. `Role`이 `Admin`인 **관리자**의 경우 **모든 콘텐츠가 열람이 가능**합니다.

### 1. 일반적으로 회원가입후 로그인시 일반 사용자의 이름이 노출되고 일반 사용자 콘텐츠 클릭시 열람이 가능합니다.
![KakaoTalk_20241115_174017412_05](https://github.com/user-attachments/assets/b5a2b489-9dab-44fa-aef5-eccb44b23764)

### 2-1. 제한된 컨텐츠 보기 클릭시 관리자만 접근할 수 있는 콘텐츠라는 알림이 뜬 후
![KakaoTalk_20241115_174017412_06](https://github.com/user-attachments/assets/5892701e-e3d2-417a-a7c4-9f95c68b1494)

### 2-2. 접근할 수 없는 페이지를 알리는 페이지로 이동됩니다.
![KakaoTalk_20241115_174017412_07](https://github.com/user-attachments/assets/3b823ff9-a9c0-4117-aa2a-023062c1a7a2)

### 3. 회원가입시 Admin 이라는 아이디로 가입한 관리자 계정의 경우 일반 사용자 콘텐츠 및 관리자 콘텐츠 전부 열람 가능합니다.
![KakaoTalk_20241115_174017412_03](https://github.com/user-attachments/assets/c01b9c2b-db1b-4766-a1db-726fc2f30f60)
---

<div align="right">
  
[목차로](#-목차)

</div>
