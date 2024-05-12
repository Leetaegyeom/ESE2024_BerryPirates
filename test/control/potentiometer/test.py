"""
FILE    : ADS_read.py
AUTHOR  : Chandra.Wijaya
VERSION : 1.2.0
PURPOSE : read analog input

test
connect 1 potmeter 

GND ---[   x   ]------ 3.3V
           |

measure at x (connect to AIN0).
"""

MAX_ANGLE = 270
MAX_VALUE = 26366

RATIO = MAX_ANGLE/MAX_VALUE

import os
import time
import ADS1x15

# choose your sensor
# ADS = ADS1x15.ADS1013(1, 0x48)
# ADS = ADS1x15.ADS1014(1, 0x48)
# ADS = ADS1x15.ADS1015(1, 0x48)
# ADS = ADS1x15.ADS1113(1, 0x48)
# ADS = ADS1x15.ADS1114(1, 0x48)

ADS = ADS1x15.ADS1115(1, 0x48)

print(os.path.basename(__file__))
print("ADS1X15_LIB_VERSION: {}".format(ADS1x15.__version__))

# set gain to 4.096V max
ADS.setGain(ADS.PGA_4_096V)
f = ADS.toVoltage()

while True :
    val_0 = ADS.readADC(0)
    # val_1 = ADS.readADC(1)
    # val_2 = ADS.readADC(2)
    # val_3 = ADS.readADC(3)
    # print("Analog0: {0:d}\t{1:.3f} V".format(val_0, val_0 * f))
    print(int(val_0*RATIO))
    # print("Analog1: {0:d}\t{1:.3f} V".format(val_1, val_1 * f))
    # print("Analog2: {0:d}\t{1:.3f} V".format(val_2, val_2 * f))
    # print("Analog3: {0:d}\t{1:.3f} V".format(val_3, val_3 * f))

    # print("Front: {}     Back: {}".format(val_1, val_0))
    # print("Front: {0:d}".format(val_0))
    # print("\t\tBack: {0:d}".format(val_1))
    
    # front, back = val_0, val_1

    # if abs(front) < 1000 and abs(back) < 1000:
    #     print("GO UP")
    # else:
    #     if front - back > 5000:
    #         print("FRONT DOWN")
    #     elif abs(front - back) < 5000:
    #         print("GO DOWN")
    #     else:
    #         print("BACK DOWN")

    time.sleep(0.1)

# while True :
#     val_0 = ADS.readADC(0)
#     val_1 = ADS.readADC(1)
#     time.sleep(0.1)