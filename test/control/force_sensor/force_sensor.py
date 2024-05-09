import time
import ADS1x15

class ForceSensor:
    def __init__(self):
        ADS = ADS1x15.ADS1115(1, 0x48)
        ADS.setGain(ADS.PGA_4_096V)
        f = ADS.toVoltage()
    
    def measure_force(self):
        left_front_value = ADS.readADC(0)
        left_back_value = ADS.readADC(1)
        right_front_value = ADS.readADC(2)
        rigth_back_value = ADS.readADC(3)

        return left_front_value, left_back_value, right_front_value, rigth_back_value

    def test(self):
        left_front_value, left_back_value = measure_force()

        if left_front_value - left_back_value > 5000:
            print("FRONT DOWN")
        elif abs(left_front_value - left_back_value) < 5000:
            print("GO DOWN")
        else:
            print("BACK DOWN")
