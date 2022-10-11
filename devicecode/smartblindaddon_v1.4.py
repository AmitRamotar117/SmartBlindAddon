import RPi.GPIO as GPIO
from time import sleep
import board
import busio
from smbus2 import SMBus
from mlx90614 import MLX90614
import adafruit_bh1750
import time

import pyrebase

bus = SMBus(1)
tempsensor = MLX90614(bus, address=0x5A)

i2c = board.I2C()
luxsensor = adafruit_bh1750.BH1750(i2c)

GPIO.setwarnings(False)

TRIG = 23
ECHO = 24
 
GPIO.setmode(GPIO.BCM) # set borad to gpio
INO = 26 #Pin 37
INT = 19 #Pin 35
EN  = 13 #Pin 33

LED = 11 #Pin 23

GPIO.setup(LED,GPIO.OUT)

GPIO.setup(INO,GPIO.OUT)
GPIO.setup(INT,GPIO.OUT)
GPIO.setup(EN,GPIO.OUT)



# https://tutorial.cytron.io/2020/12/09/send-data-to-firebase-using-raspberry-pi/
# https://www.youtube.com/watch?v=_cdQ0BsMt-Y&t=44s
# https://github.com/thisbejim/Pyrebase
config = {
  "apiKey": "PnU2aAR7J0EZDzPAASX6VoeTUrEIl25siW2EUyLi",
  "authDomain": "smartblindaddon.firebaseapp.com",
  "databaseURL": "https://smartblindaddon-default-rtdb.firebaseio.com",
  "storageBucket": "smartblindaddon.appspot.com"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()

#this function will activate the motor and turn it clockwise
def motor_open():
    print("opening blind")
    GPIO.output(INO,GPIO.HIGH)
    GPIO.output(INT,GPIO.LOW)
    GPIO.output(EN,GPIO.HIGH)
    
#this function will activiate the motor and trun it counterclockwise
def motor_close():
    print("closeing blind")
    GPIO.output(INO,GPIO.LOW)
    GPIO.output(INT,GPIO.HIGH)
    #pwm.ChangeDutyCycle(50)
    GPIO.output(EN,GPIO.HIGH)
    
#this function will get the light lux value from the TSL2561
#def get_lux():
    #print('Lux: {}'.format(sensor.lux))
    #return sensor.lux

#this function will send temperatue data to the firebase database
def send_temp(temp):
    temp = str(temp)
    db.child("0001").child("Temperature").set(temp)

#this function will send lux data to the firebase database
def send_lux(lux):
    lux = str(lux)
    db.child("0001").child("Light").set(lux)

#this function will get the height of the blind from the firebase database
def get_height():
    h = db.child("0001").child("Height").get()
    print(h.val())
    return h.val()

def get_schedule():
    date = db.child("0001").child("Schedule").child("date").get()
    op = db.child("0001").child("Schedule").child("operation").get()
    time = db.child("0001").child("Schedule").child("time").get()
    sechedule = {
        "date": date.val(),
        "op": op.val(),
        "time": time.val() 
        }
    return sechedule

def blind_status(currentStatus):
    command = db.child("0001").child("Status").get()
    if currentStatus == command:
        return True # do nothing
    if currentStatus != command:
        return False # either open or close blind

def led_on():
    GPIO.output(LED,GPIO.HIGH)
    print("led on")

def led_off():
    GPIO.output(LED,GPIO.LOW)
    print("led off")

def get_tempData():
    temp = tempsensor.get_ambient()
    print("Ambient Temperature :", tempsensor.get_ambient())
    t = "{:.0f}".format(temp)
    print(t)
    return t

def get_luxData():
    lux = luxsensor.lux
    print("%.2f LUX" % luxsensor.lux)
    l = "{:.0f}".format(lux)
    print(l)
    return l

def get_distance():
    print ("Distance Measurement In Progress")
    GPIO.setup(TRIG,GPIO.OUT)
    GPIO.setup(ECHO,GPIO.IN) 

    GPIO.output(TRIG,False)
    print ("Waiting For Sensor To Settle")
    time.sleep(2) 

    GPIO.output(TRIG, True)
    time.sleep(0.00001)
    GPIO.output(TRIG, False) 

    while GPIO.input(ECHO)==0:
        pulse_start = time.time()

    while GPIO.input(ECHO)==1:
        pulse_end = time.time()

    pulse_duration = pulse_end - pulse_start

    distance = pulse_duration * 17150

    distance = round(distance, 0)

    print ("Distance:",distance,"cm")
    

while True:
    get_height()
    s = get_schedule()
    print(s)
    send_lux(12)
    
    get_tempData()
    
    get_luxData()
    
    get_distance()
    
    motor_open()
    sleep(2)
    motor_close()
    sleep(2)
    print ("Stopping motor")
    GPIO.output(EN,GPIO.LOW)
    sleep(6)
    
    led_on()
    sleep(2)
    led_off()
    sleep(2)
GPIO.cleanup()

