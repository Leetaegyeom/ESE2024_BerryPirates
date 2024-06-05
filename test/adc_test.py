# import os
# import time
# import ADS1x15

# MAX_ANGLE = 270
# MAX_VALUE = 26366

# RATIO = MAX_ANGLE/MAX_VALUE

# ADS1 = ADS1x15.ADS1115(0, 0x48)
# ADS2 = ADS1x15.ADS1115(1, 0x48)

# ADS1.setGain(ADS1.PGA_4_096V)
# ADS2.setGain(ADS2.PGA_4_096V)

# f1 = ADS1.toVoltage()
# f2 = ADS2.toVoltage()

# print(os.path.basename(__file__))
# print("ADS1X15_LIB_VERSION: {}".format(ADS1x15.__version__))

# while True:
#     val_1 = ADS1.readADC(1)
#     val_2 = ADS2.readADC(2)
#     val_3 = ADS2.readADC(3)

#     print("Analog1: {0:d}\t{1:.3f} V".format(val_1, val_1 * f1))
#     print("Analog2: {0:d}\t{1:.3f} V".format(val_2, val_2 * f2))
#     print("Analog3: {0:d}\t{1:.3f} V".format(val_3, val_3 * f2))

#     time.sleep(0.1)

import os
import time
import ADS1x15

MAX_ANGLE = 270
MAX_VALUE = 26366

RATIO = MAX_ANGLE/MAX_VALUE

ADS1 = ADS1x15.ADS1115(0, 0x48)
ADS2 = ADS1x15.ADS1115(1, 0x48)

ADS1.setGain(ADS1.PGA_4_096V)
ADS2.setGain(ADS2.PGA_4_096V)

f1 = ADS1.toVoltage()
f2 = ADS2.toVoltage()

print(os.path.basename(__file__))
print("ADS1X15_LIB_VERSION: {}".format(ADS1x15.__version__))

while True:
    try:
        val_1 = ADS1.readADC(1)
        val_2 = ADS2.readADC(2)
        val_3 = ADS2.readADC(3)

        print("Analog1: {0:d}\t{1:.3f} V".format(val_1, val_1 * f1))
        print("Analog2: {0:d}\t{1:.3f} V".format(val_2, val_2 * f2))
        print("Analog3: {0:d}\t{1:.3f} V".format(val_3, val_3 * f2))

    except OSError as e:
        print("I2C 통신 오류 발생:", e)
        time.sleep(1)  # 1초 지연
        continue

    time.sleep(0.1)
