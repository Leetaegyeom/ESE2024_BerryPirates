import sys
# sys.path.append('./communication')
sys.path.append('./control')

from Control import Control
# from Bluetooth import Bluetooth
        

class FOOTREEDOM:
    def __init__(self):
        self.control = Control()
        print("CONTROL SETTING COMPLETE __main.py")

    def run(self):
        try:
            while True:
                self.control.foot_control(fix_angular = False, fix_height = False)
        except KeyboardInterrupt:
            self.control.stop_all_actuator()

if __name__ == "__main__":
    footreedom = FOOTREEDOM()
    footreedom.run()