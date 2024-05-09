import GPIO
import ADS
from Actuator import ActuatorController
from UltrasonicSensor import UltrasonicSensor
from ForceSensor import ForceSensor

class TEST:
    def __init__(self):
        self.actuator_right_height_params = GPIO.GPIO_SETTING.getSensorParams('actuator_right_height')
        self.ultrasonic_right_params = GPIO.GPIO_SETTING.getSensorParams('ultrasonic_right')
        print("GPIO SETTING COMPLETE")

        self.force_params = ADS.ADS_SETTING.getSensorParams('force')
        self.potentiometer_params = ADS.ADS_SETTING.getSensorParams('potentiometer')
        print("ADS SETTING COMPLETE")

        self.ultrasonic_right = UltrasonicSensor(self.ultrasonic_right_params)
        self.actuator_right_height = ActuatorController(self.actuator_right_height_params)
        self.force = ForceSensor(self.force_params)
        self.potentiometer = 

        print("SENSOR & ACTUATOR SETTING COMPLETE")

        self.ref_distance = 10 # cm
        self.control_falg = True

    def position_control(self):
        try:
            while self.control_falg:
                meas_distance = self.ultrasonic_right.get_distance() # distance(cm)
                err = self.ref_distance - meas_distance
                print("err : %f" %err)

                if abs(err) < 1:
                    self.actuator_right_height.stop_actuator()
                    self.control_falg = False
                else:
                    if err > 0:
                        self.actuator_right_height.extend_actuator()
                    elif err < 0:
                        self.actuator_right_height.retract_actuator()
            print("POSITION CONTROL COMPLETE !!!")
            
        except KeyboardInterrupt:
            self.actuator_right_height.stop_actuator()

if __name__ == "__main__":
    test= TEST()
    test.run()
    # finally:
    #     actuator.clean_up()
