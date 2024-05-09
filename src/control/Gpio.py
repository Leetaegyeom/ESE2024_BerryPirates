class GPIO_STRUCT:
    def __init__(self):
        self.Model = None

        # FOR ULTRA SONIC SENSOR
        self.TRIG_PIN = None
        self.ECHO_PIN = None

        # FOR LINEAR ACTUATOR 
        self.ENA_PIN = None
        self.IN1_PIN = None
        self.IN2_PIN = None


class GPIO_SETTING:
    @staticmethod
    def getSensorParams(Model):

        model_params = GPIO_STRUCT()

        ### ULTRASONIC SENSOR ###
        if Model == 'ultrasonic_right':

            model_params.Model = 'ultrasonic_r'

            model_params.TRIG_PIN = 23
            model_params.ECHO_PIN = 24

        elif Model == 'ultrasonic_left':

            model_params.Model = 'ultrasonic_left'

            model_params.TRIG_PIN = 23
            model_params.ECHO_PIN = 24
        
        ### ACTUATOR ###
        elif Model == 'actuator_right_height':

            model_params.Model = 'aactuator_right_height'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7

        elif Model == 'actuator_right_angle':

            model_params.Model = 'actuator_right_angle'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7

        elif Model == 'actuator_left_height':

            model_params.Model = 'actuator_left_height'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7

        elif Model == 'actuator_left_angle':

            model_params.Model = 'actuator_left_angle'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7

        ### FORCE SENSOR ###
        elif Model == 'force_right_front':

            model_params.Model = 'force_right_front'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7

        elif Model == 'force_right_back':

            model_params.Model = 'force_right_back'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7

        elif Model == 'force_left_front':

            model_params.Model = 'force_left_front'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7    

        elif Model == 'force_left_back':

            model_params.Model = 'force_left_back'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7
        
        ### POTENTIOMETER ###
        elif Model == 'potentiometer_right':

            model_params.Model = 'potentiometer_right'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7    

        elif Model == 'potentiometer_left':

            model_params.Model = 'potentiometer_left'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7               


        else:
            raise ValueError('Model invalid')
        
        return model_params