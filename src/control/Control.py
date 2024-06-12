import GPIO
import ADC
from Actuator import ActuatorController
from UltrasonicSensor import UltrasonicSensor
from ForceSensor import ForceSensor
from Potentiometer import Potentiometer

import time
import RPi.GPIO as test

class Control:
    def __init__(self):

        test.setmode(test.BCM)

        self.actuator_right_height_params = GPIO.GPIO_SETTING.getSensorParams('actuator_right_height')
        self.actuator_left_height_params = GPIO.GPIO_SETTING.getSensorParams('actuator_left_height')
        self.actuator_right_angle_params = GPIO.GPIO_SETTING.getSensorParams('actuator_right_angle')
        self.actuator_left_angle_params = GPIO.GPIO_SETTING.getSensorParams('actuator_left_angle')
        self.ultrasonic_right_params = GPIO.GPIO_SETTING.getSensorParams('ultrasonic_right')
        self.ultrasonic_left_params = GPIO.GPIO_SETTING.getSensorParams('ultrasonic_left')
        print("GPIO SETTING COMPLETE __Control.py")

        self.force_params = ADC.ADC_SETTING.getSensorParams('force')
        self.potentiometer_params = ADC.ADC_SETTING.getSensorParams('potentiometer')
        print("ADS SETTING COMPLETE __Control.py")

        self.ultrasonic_right = UltrasonicSensor(self.ultrasonic_right_params)
        self.ultrasonic_left = UltrasonicSensor(self.ultrasonic_left_params)
        self.actuator_right_height = ActuatorController(self.actuator_right_height_params)
        self.actuator_left_height = ActuatorController(self.actuator_left_height_params)
        self.actuator_right_angle = ActuatorController(self.actuator_right_angle_params)
        self.actuator_left_angle = ActuatorController(self.actuator_left_angle_params)
        self.force = ForceSensor(self.force_params)
        self.potentiometer = Potentiometer(self.potentiometer_params)
        print("SENSOR & ACTUATOR SETTING COMPLETE __Control.py")

        self.distance_threshold = 1 # cm
        self.angle_threshold = 1 # degree
        self.max_speed = 100
        self.mid_speed = 60
        self.min_speed = 20
        self.total_control_flag = True
        # self.right_height_control_flag = True # FOR TEST
        self.right_height_control_flag = False
        self.left_height_control_flag = True
        # self.right_angle_control_flag = True
        self.right_angle_control_flag = False # FOR TEST
        self.left_angle_control_flag = True
        print("POSITION CONTROL SETTING COMPLETE __Control.py")

    def get_value(self):
        meas_right_distance = self.ultrasonic_right.get_distance()
        meas_left_distance = self.ultrasonic_left.get_distance()
        meas_right_angle, meas_left_angle = self.potentiometer.get_angle()
        return [meas_left_angle, meas_left_distance, meas_right_angle, meas_right_distance]

    # 앱으로 조작, main에서 while 추가적으로 사용할 필요 없음 
    def position_control(self, ref_value):
        ref_right_distance = ref_value.right_height # cm
        ref_left_distance = ref_value.left_height # cm
        ref_right_angle = ref_value.right_angle # degree
        ref_left_angle = ref_value.left_angle # degree

        self.right_height_control_flag = False # FOR TEST
        self.left_height_control_flag = True
        self.right_angle_control_flag = False # FOR TEST
        self.left_angle_control_flag = True
        self.total_control_flag = True

        try:
            while self.total_control_flag:
                if self.right_height_control_flag:
                    meas_right_distance = self.ultrasonic_right.get_distance() # cm
                    right_distance_err = ref_right_distance - meas_right_distance
                    # print("right_distance_err : {:8.4f} __Control.py".format(right_distance_err))

                    if abs(right_distance_err) < self.distance_threshold:
                        self.actuator_right_height.stop_actuator()
                        self.right_height_control_flag = False
                    else:
                        if right_distance_err > 0:
                            # self.actuator_right_height.retract_actuator(self.mid_speed)
                            a=1
                        elif right_distance_err < 0:
                            # self.actuator_right_height.extend_actuator(self.mid_speed)
                            a=1

                if self.left_height_control_flag:
                    meas_left_distance = self.ultrasonic_left.get_distance() # cm
                    left_distance_err = ref_left_distance - meas_left_distance
                    print("left_distance_err : {:8.4f} __Control.py".format(left_distance_err))

                    if abs(left_distance_err) < self.distance_threshold:
                        self.actuator_left_height.stop_actuator()
                        self.left_height_control_flag = False
                    else:
                        if left_distance_err > 0:
                            self.actuator_left_height.retract_actuator(self.min_speed)
                        elif left_distance_err < 0:
                            self.actuator_left_height.extend_actuator(self.min_speed)

                meas_left_angle, meas_right_angle = self.potentiometer.get_angle()

                if self.right_angle_control_flag:
                    right_angle_err = ref_right_angle - meas_right_angle # degree
                    # print("right_angle_err : {:8.4f} __Control.py".format(right_angle_err))

                    if abs(right_angle_err) < self.angle_threshold:
                        self.actuator_right_angle.stop_actuator()
                        self.right_angle_control_flag = False
                    else:
                        if right_angle_err > 0:
                            a=1
                            # self.actuator_right_angle.extend_actuator(self.max_speed)
                        elif right_angle_err < 0:
                            a=1
                            # self.actuator_right_angle.retract_actuator(self.max_speed)


                if self.left_angle_control_flag:
                    left_angle_err = ref_left_angle - meas_left_angle # degree
                    print("left_angle_err  : {:8.4f} __Control.py".format(left_angle_err))

                    if abs(left_angle_err) < self.angle_threshold:
                        self.actuator_left_angle.stop_actuator()
                        self.left_angle_control_flag = False
                    else:
                        if left_angle_err > 0:
                            self.actuator_left_angle.extend_actuator(self.max_speed)
                        elif left_angle_err < 0:
                            self.actuator_left_angle.retract_actuator(self.max_speed)
            
                if not (self.right_height_control_flag) and not (self.left_height_control_flag) and not(self.right_angle_control_flag) and not(self.left_angle_control_flag):
                    self.total_control_flag = False
                    self.stop_all_actuator()
                    print("POSITION CONTROL COMPLETE __Control.py")

                time.sleep(0.1)

        except KeyboardInterrupt:
            print("KEYBOARD INTERRUPT __Control.py")
            self.stop_all_actuator()
            self.cleanup_all_actuator()

    def foot_control(self, fix_angular = False, fix_height = False):
        right, left = self.force.guess_user_purpose()
        
        # if right == "FRONT BACK UP" and not(fix_height):
        #     self.actuator_right_height.extend_actuator(self.min_speed)
        # elif right == "FRONT DOWN" and not(fix_angular):
        #     self.actuator_right_angle.extend_actuator(self.max_speed)
        # elif right == "FRONT BACK DOWN" and not(fix_height):
        #     self.actuator_right_height.retract_actuator(self.min_speed)
        # elif right == "BACK DOWN" and not(fix_angular):
        #     self.actuator_right_angle.retract_actuator(self.max_speed)
        print(left)
        if left == "FRONT BACK UP" and not(fix_height):
            self.actuator_left_height.retract_actuator(self.min_speed)
        elif left == "FRONT DOWN" and not(fix_angular):
            self.actuator_left_angle.extend_actuator(self.max_speed)
        elif left == "FRONT BACK DOWN" and not(fix_height):
            self.actuator_left_height.extend_actuator(self.min_speed)
        elif left == "BACK DOWN" and not(fix_angular):
            self.actuator_left_angle.retract_actuator(self.max_speed)

        time.sleep(0.1)
        self.stop_all_actuator()
        
    def stop_all_actuator(self):
        self.actuator_right_height.stop_actuator()
        self.actuator_left_height.stop_actuator()
        self.actuator_right_angle.stop_actuator()
        self.actuator_left_angle.stop_actuator()
        print("STOP ALL ACTUATOR COMPLETE __Control.py")

    def cleanup_all_actuator(self):
        self.actuator_right_height.clean_up()
        self.actuator_left_height.clean_up()
        self.actuator_right_angle.clean_up()
        self.actuator_left_angle.clean_up()
        print("CLEAN UP ALL ACTUATOR COMPLETE __Control.py")
