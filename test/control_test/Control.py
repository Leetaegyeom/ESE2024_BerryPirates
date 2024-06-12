import GPIO
import ADC
from Actuator import ActuatorController
from UltrasonicSensor import UltrasonicSensor
from ForceSensor import ForceSensor
from Potentiometer import Potentiometer

import time

class Control:
    def __init__(self):
        # self.actuator_right_height_params = GPIO.GPIO_SETTING.getSensorParams('actuator_right_height')
        self.actuator_left_height_params = GPIO.GPIO_SETTING.getSensorParams('actuator_left_height')
        # self.actuator_right_angle_params = GPIO.GPIO_SETTING.getSensorParams('actuator_right_angle')
        self.actuator_left_angle_params = GPIO.GPIO_SETTING.getSensorParams('actuator_left_angle')
        # self.ultrasonic_right_params = GPIO.GPIO_SETTING.getSensorParams('ultrasonic_right')
        self.ultrasonic_left_params = GPIO.GPIO_SETTING.getSensorParams('ultrasonic_left')
        print("GPIO SETTING COMPLETE __Control.py")

        self.force_params = ADC.ADC_SETTING.getSensorParams('force')
        self.potentiometer_params = ADC.ADC_SETTING.getSensorParams('potentiometer')
        print("ADS SETTING COMPLETE __Control.py")

        # self.ultrasonic_right = UltrasonicSensor(self.ultrasonic_right_params)
        self.ultrasonic_left = UltrasonicSensor(self.ultrasonic_left_params)
        # self.actuator_right_height = ActuatorController(self.actuator_right_height_params)
        self.actuator_left_height = ActuatorController(self.actuator_left_height_params)
        # self.actuator_right_angle = ActuatorController(self.actuator_right_angle_params)
        self.actuator_left_angle = ActuatorController(self.actuator_left_angle_params)
        self.force = ForceSensor(self.force_params)
        # time.sleep(3)
        self.potentiometer = Potentiometer(self.potentiometer_params)
        print("SENSOR & ACTUATOR SETTING COMPLETE __Control.py")

        self.distance_threshold = 1 # cm
        self.angle_threshold = 1 # degree
        self.total_control_falg = True
        # self.right_height_control_flag = True
        self.left_height_control_flag = True
        # self.right_angle_control_flag = True
        self.left_angle_control_flag = True
        print("POSITION CONTROL SETTING COMPLETE __Control.py")

    # def get_value(self):
    #     meas_right_distance = self.ultrasonic_right.get_distance()
    #     # meas_left_distance = self.ultrasonic_left.get_distance()
    #     meas_right_angle, meas_left_angle = self.potentiometer.get_angle()
    #     return [meas_left_angle, meas_left_distance, meas_right_angle, meas_right_distance]

    # 앱으로 조작, main에서 while 추가적으로 사용할 필요 없음 
    def position_control(self, ref_value):
        ref_left_distance = ref_value.left_height # cm
        ref_left_angle = ref_value.left_angle # degree

        try:
            while self.total_control_falg:
                try:
                    if self.left_height_control_flag:
                        meas_left_distance = self.ultrasonic_left.get_distance() # cm
                        left_distance_err = ref_left_distance - meas_left_distance
                        print("left_distance_err : %f" %left_distance_err, " __Control.py")

                        if abs(left_distance_err) < self.distance_threshold:
                            self.actuator_left_height.stop_actuator()
                            self.left_height_control_flag = False
                        else:
                            if left_distance_err > 0:
                                self.actuator_left_height.extend_actuator()
                            elif left_distance_err < 0:
                                self.actuator_left_height.retract_actuator()

                    meas_left_angle, meas_left_angle = self.potentiometer.get_angle()

                    if self.right_angle_control_flag:
                        right_angle_err = ref_right_angle - meas_right_angle # degree
                        print("right_angle_err : %f" %right_angle_err, " __Control.py")

                        if abs(right_angle_err) < self.angle_threshold:
                            self.actuator_right_angle.stop_actuator()
                            self.right_angle_control_flag = False
                        else:
                            if right_angle_err > 0:
                                self.actuator_right_angle.extend_actuator()
                            elif right_angle_err < 0:
                                self.actuator_right_angle.retract_actuator()

                    if not (self.right_height_control_flag) and not(self.right_angle_control_flag):
                        self.total_control_falg = False
                        self.stop_all_actuator()
                        print("POSITION CONTROL COMPLETE __Control.py")

                except OSError as e:
                    print("I2C 통신 오류 발생:", e)
                    time.sleep(1)  # 1초 지연
                    continue

        except KeyboardInterrupt:
            print("KEYBOARD INTERRUPT __Control.py")
            self.stop_all_actuator()
            self.cleanup_all_actuator()

    # 발로 조작, main에서 while 추가적으로 사용해야함, while문에서는 sleep할 필요 없음, while문 시작할 때 발로조작 모드인지 확인하는 코드 필요
    def foot_control(self, fix_angular = False, fix_height = False):
        right, left = self.force.guess_user_purpose()
        
        if right == "FRONT BACK UP" and not(fix_height):
            self.actuator_right_height.extend_actuator()
        elif right == "FRONT DOWN" and not(fix_angular):
            self.actuator_right_angle.extend_actuator()
        elif right == "FRONT BACK DOWN" and not(fix_height):
            self.actuator_right_height.retract_actuator()
        elif right == "BACK DOWN" and not(fix_angular):
            self.actuator_right_angle.retract_actuator()
        
        # if left == "FRONT BACK UP" and not(fix_height):
        #     self.actuator_left_height.extend_actuator()
        # elif left == "FRONT DOWN" and not(fix_angular):
        #     self.actuator_left_angle.extend_actuator()
        # elif left == "FRONT BACK DOWN" and not(fix_height):
        #     self.actuator_left_height.retract_actuator()
        # elif left == "BACK DOWN" and not(fix_angular):
        #     self.actuator_left_angle.retract_actuator()

        time.sleep(0.1)
        self.stop_all_actuator()
        
    def stop_all_actuator(self):
        self.actuator_right_height.stop_actuator()
        # self.actuator_left_height.stop_actuator()
        self.actuator_right_angle.stop_actuator()
        # self.actuator_left_angle.stop_actuator()
        print("STOP ALL ACTUATOR COMPLETE __Control.py")

    def cleanup_all_actuator(self):
        self.actuator_right_height.clean_up()
        # self.actuator_left_height.clean_up()
        self.actuator_right_angle.clean_up()
        # self.actuator_left_angle.clean_up()
        print("CLEAN UP ALL ACTUATOR COMPLETE __Control.py")
