import RPi.GPIO as GPIO
from time import sleep
import board
import busio
import adafruit_tsl2561
import pyrebase
 
#GPIO.setmode(GPIO.BOARD)
GPIO.setup(16,GPIO.OUT)
GPIO.setup(18,GPIO.OUT)
GPIO.setup(22,GPIO.OUT)

pwm = GPIO.PWM(22,100)

pwm.start(0)


#i2c = busio.I2C(board.SCL, board.SDA)
#sensor = adafruit_tsl2561.TSL2561(i2c)

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
    GPIO.output(16,True)
    GPIO.output(18,False)
    pwm.ChangeDutyCycle(50)
    GPIO.output(22,True)
    
#this function will activiate the motor and trun it counterclockwise
def motor_close():
    print("closeing blind")
    GPIO.output(16,False)
    GPIO.output(18,True)
    pwm.ChangeDutyCycle(50)
    GPIO.output(22,True)
    
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
 

get_height()
s = get_schedule()
print(s)
send_lux(12)
motor_open()
sleep(2)
motor_close()
sleep(2)
 
print ("Stopping motor")
GPIO.output(22,False)
 
GPIO.cleanup()
