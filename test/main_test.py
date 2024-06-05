import sys
sys.path.append('./control_test')

from Control import Control

class SIGNAL:
    def __init__(self):
        self.right_height = 10 # cm
        # self.ref_left_distance = 0 # cm
        self.right_angle = 10 # degree
        # self.ref_left_angle = 0 # degree

class FOOTREEDOM:
    def __init__(self):
        self.control = Control()
        print("CONTROL SETTING COMPLETE __main.py")

        self.ref_value = SIGNAL()

    def run(self):
        self.control.position_control(self.ref_value)

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