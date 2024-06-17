# ![image](https://github.com/Leetaegyeom/ESE2024_BerryPirates/assets/117874932/9745f17a-75bd-40f1-967a-e133a6a404b4) ESE2024_-BerryPirates-
## 🖥 Project Name
🦶 FootReeDom(자유분발)

## 📃 Project Outline
+ 프로필 선택 기능 제공
+ 발로 조절 모드
+ 앱으로 조절 모드
+ 자세 저장 기능 제공

## 🗓 Development Info
* 서울시립대학교 기계정보공학과 임베디드시스템 베리해적단
* 개발 기간: 2024.03 ~ 2024.06
* 개발 언어: Python & Kotlin
  
## 👥 Members
* 최강현(팀장): 배선, 하드웨어 분석, 일정 관리
* 고강민: 하드웨어 제작, 하드웨어 분석
* 이태겸: 센싱 시스템, 제어 시스템, 센싱-제어 모듈 통합
* 이태훈: 앱-라즈베리파이 블루투스 통신 구현, 앱 UI 구현
* 최규환: 앱-라즈베리파이 블루투스 통신 구현, 제어 시스템
* 전  원 : 시스템 통합

## Main File Structure
+ test
  * Actuator : linear actuator test
  * Sensor : force sensor, potentiometer, ultrasonic sensor test
  * BLE, Control, Main test
+ src
  * communication : bluetooth (with BLE)
  * control
+ ui
  * practice_1/app/src/main/java/com/example/practice_1 : UI backend file
  * practice_1/app/src/main/res/layout : UI frontend file
+ main.py : integrate communication, control, ui files
+ main_app_control_test.py : app control mode test
+ main_foot_control_test.py : foot control  mode test
  
