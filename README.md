# ![image](https://github.com/Leetaegyeom/ESE2024_BerryPirates/assets/117874932/9745f17a-75bd-40f1-967a-e133a6a404b4) ESE2024_-BerryPirates-
## 🖥 Project Name
**FootReeDom(자유분발)**

## 📃 Project Outline
FootReeDom은 사용자가 편안하고 건강한 자세를 유지할 수 있도록 돕는 발 받침대입니다. 이 프로젝트의 주요 특징은 다음과 같습니다:
+ **프로필 선택 기능 제공**
+ **발로 조절 모드**
+ **앱으로 조절 모드**
+ **자세 저장 기능 제공**

## 🗓 Development Info
* **서울시립대학교 기계정보공학과 임베디드시스템 베리해적단**
* **개발 기간:** 2024.03 ~ 2024.06
* **개발 언어:** Python & Kotlin
  
## 👥 Members
* **최강현(팀장):** 배선, 하드웨어 분석, 일정 관리
* **고강민:** 하드웨어 제작, 하드웨어 분석
* **이태겸:** 센싱 시스템, 제어 시스템, 센싱-제어 모듈 통합
* **이태훈:** 앱-라즈베리파이 블루투스 통신 구현, 앱 UI 구현
* **최규환:** 앱-라즈베리파이 블루투스 통신 구현, 제어 시스템
* **전  원 :** 시스템 통합

## Main File Structure
+ **modeling_Inventor**
  * **모델링 파일:** Inventor를 사용한 모델링 파일
+ **test**
  * **Actuator**: 리니어 액추에이터 테스트
  * **Sensor**: 포스 센서, 포텐셔미터, 초음파 센서 테스트
  * **BLE, Control, Main:** BLE 통신, 제어, 메인 시스템 테스트
+ **src**
  * **communication:** 블루투스 통신 (BLE 사용)
  * **control:** 제어 시스템 파일
  * **ui**
    - practice_1/app/src/main/java/com/example/practice_1 : UI 백엔드 파일
    - practice_1/app/src/main/res/layout : UI 프론트엔드 파일
  * **main.py:** 통신, 제어, UI 파일 통합
  * **main_app_control_test.py:** 앱 제어 모드 테스트
  * **main_foot_control_test.py:** 발 제어 모드 테스트

## 시스템 개략도
 <p align="center"><img src="https://github.com/Leetaegyeom/ESE2024_BerryPirates/assets/117874932/2088bc1f-d151-4782-97c5-829e937031a9" width="600"><p/>
   
  * **안드로이드 앱과 파이어베이스:** WIFI를 이용해 '기록' 기능과 관련된 정보를 주고 받으며 안드로이드 앱과 라즈베리파이는 BLE(Bluetooth Low Energy) 이용해 '발로 조절', '앱으로 조절' 기능과 관련된 정보를 주고 받는다.
  * **라즈베리파이:** 센서부, 구동부로 구성하였다. 센서부로는 초음파 센서, 압력 센서, 로터리 가변저항이 있으며 압력 센서와 로터리 가변저항은 아날로그 값을 반환하므로 ADC Converter를 통해 디지털 값으로 변환하여 측정값을 읽는다. 초음파 센서의 경우, 디지털 값을 반환하기 때문에 라즈베리파이와 직접 연결하여 사용한다. 구동부인 리니어 액추에이터는 모터 드라이버를 통해 간편하게 제어할 수 있도록 구성하였다.

## 발 받침대 하드웨어
<p align="center"><img src=https://github.com/Leetaegyeom/ESE2024_BerryPirates/assets/105715306/b9e93ca8-b057-485a-9558-b9e74c8d6c60)" width="600"><p/>


