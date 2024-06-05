import sys
# sys.path.append('./communication')
sys.path.append('./control')

from Control import Control
from Bluetooth import Bluetooth

class SIGNAL:
    def __init__(self):
        self.right_height = 10 # cm
        self.ref_left_distance = 0 # cm
        self.right_angle = 10 # degree
        self.ref_left_angle = 0 # degree

class FOOTREEDOM:
    def __init__(self):
        self.control = Control()
        print("CONTROL SETTING COMPLETE __main.py")
        
        # self.bluetooth = Bluetooth()
        # print("BLUETOOTH SETTING COMPLETE __main.py")
        self.ref_value = SIGNAL()

    def run(self):
        # main_signal = self.bluetooth.get_main_signal()

        # 6/5 -> app_control_on 신호가 active되는 버튼 바꿔야함
        # if main_signal.app_control_on and not(main_signal.foot_control_on): # app control mode
        # print("APP CONTROL MODE ON !!")
        # ref_value = self.bluetooth.get_ref_value()
        ref_value = self.ref_value
        print("REF VALUE :\nR_HEIGHT : %f, R_ANGLE : %f, L_HEIGHT : %f, L_ANGLE : %f"%(ref_value.right_height, ref_value.right_angle, ref_value.left_height, ref_value.left_angle))
        self.control.position_control(ref_value)

        # 6/5 발로 조절 모드에서 버튼 누를 때마다 T -> F -> T로 바뀌게
        # 6/5 발로 조절 모드 나가면 각도 높이 고정 시그널 False로 
        # elif not(main_signal.app_control_on) and main_signal.foot_control_on: # foot control mode
        #     while True:
        #         foot_control_signal = self.bluetooth.get_main_signal()
        #         if not(foot_control_signal.foot_control_on):
        #             break
        #         self.control.foot_control(fix_angular = foot_control_signal.fix_angular, fix_height = foot_control_signal.fix_height)
        #         if foot_control_signal.save_pose:
        #             save_pose = self.control.get_value()
        #             self.bluetooth.send_save_pose(save_pose)

if __name__ == "__main__":
    footreedom = FOOTREEDOM()
    try:
        while True:
            footreedom.run()
    except KeyboardInterrupt:
        pass