import sys
sys.path.append('./communication')
sys.path.append('./control')

from Control import Control
from Bluetooth import Bluetooth

class FOOTREEDOM:
    def __init__(self):
        self.control = Control()
        print("CONTROL SETTING COMPLETE __main.py")
        
        self.bluetooth = Bluetooth()
        print("BLUETOOTH SETTING COMPLETE __main.py")

    def run(self):
        # 통신 관련 코드(power_off, foot_control_on, foot_control_off, fix_angular, fix_height, save_pose 등 신호 할당돼야함)
        self.bluetooth.get_bluetooth_signals()
        
        if app_control and not(foot_control): # app control mode
            ref_value = [right_height, left_height, right_angle, left_angle]
            self.control.position_control(ref_value)

        elif not(app_control) and foot_control: # foot control mode
            while 발로 조작 모드 종료 신호 :
                발로 조작 모드 종료 신호 = func1
                fix_angular, fix_height = func2
                if 발로 조작 모드 종료 신호:
                    break
                self.control.foot_control(fix_angular = fix_angular, fix_height = fix_height)

