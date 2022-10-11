import RPi.GPIO as GPIO
from time import sleep
import board
import busio
import adafruit_tsl2561
import pyrebase
 
GPIO.setmode(GPIO.BOARD)

# https://business.tutsplus.com/tutorials/controlling-dc-motors-using-python-with-a-raspberry-pi--cms-20051
Motor1A = 18
Motor1B = 16
Motor1E = 22
 
GPIO.setup(Motor1A,GPIO.OUT)
GPIO.setup(Motor1B,GPIO.OUT)
GPIO.setup(Motor1E,GPIO.OUT)

i2c = busio.I2C(board.SCL, board.SDA)
sensor = adafruit_tsl2561.TSL2561(i2c)

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

def motor_open():
    print("opening blind")
    GPIO.output(Motor1A,GPIO.HIGH)
    GPIO.output(Motor1B,GPIO.LOW)
    GPIO.output(Motor1E,GPIO.HIGH)

def motor_close():
    print("closeing blind")
    GPIO.output(Motor1A,GPIO.LOW)
    GPIO.output(Motor1B,GPIO.HIGH)
    GPIO.output(Motor1E,GPIO.HIGH)

def get_lux():
    print('Lux: {}'.format(sensor.lux))
    return sensor.lux

def send_temp(temp):
    temp = str(temp)
    db.child("0001").child("Temperature").set(temp)

def send_lux(lux):
    lux = str(lux)
    db.child("0001").child("Light").set(lux)

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
 
sleep(2)
 
print ("Stopping motor")
GPIO.output(Motor1E,GPIO.LOW)
 
GPIO.cleanup()