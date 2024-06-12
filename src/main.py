import sys
sys.path.append('./communication')
sys.path.append('./control')

from Control import Control
from Bluetooth import Bluetooth

import time

class FOOTREEDOM:
    def __init__(self):
        self.control = Control()
        print("CONTROL SETTING COMPLETE __main.py")
        self.bluetooth = Bluetooth()
        print("BLUETOOTH SETTING COMPLETE __main.py")

    def run(self):
        main_signal = self.bluetooth.get_main_signal()
        self.bluetooth.app_control_characteristic.done = False

        # 6/5 -> app_control_on 신호가 active되는 버튼 바꿔야함
        if main_signal.app_control_on and not(main_signal.foot_control_on): # app control mode
            print("APP CONTROL MODE ON !!")
            time.sleep(1)
            ref_value = self.bluetooth.get_ref_value()
            ref_value.left_height = ref_value.left_height*12.5/5 + 9.5
            ref_value.right_height = ref_value.right_height*12.5/5 + 9.5
            print("\nREF VALUE :\nR_HEIGHT : %f, R_ANGLE :%f, L_HEIGHT : %f, L_ANGLE : \n%f"%(ref_value.right_height, ref_value.right_angle, ref_value.left_height, ref_value.left_angle))
            self.control.position_control(ref_value)
            self.bluetooth.send_done(True)
            self.bluetooth.main_signal_characteristic.value[2] = False # main_signal.app_control_on --> False로 바꿈

        # 6/5 발로 조절 모드에서 버튼 누를 때마다 T -> F -> T로 바뀌게
        # 6/5 발로 조절 모드 나가면 각도 높이 고정 시그널 False로 
        elif not(main_signal.app_control_on) and main_signal.foot_control_on: # foot control mode
            while True:
                main_signal = self.bluetooth.get_main_signal()
                if not(main_signal.foot_control_on):
                    break
                foot_control_signal = self.bluetooth.get_foot_control_signal()
                self.control.foot_control(fix_angular = foot_control_signal.fix_angular, fix_height = foot_control_signal.fix_height)
                if foot_control_signal.save_pose:
                    save_pose = self.control.get_value()
                    self.bluetooth.send_save_pose(save_pose)


if __name__ == "__main__":
    footreedom = FOOTREEDOM()
    try:
        while True:
            footreedom.run()
    except KeyboardInterrupt:
        pass