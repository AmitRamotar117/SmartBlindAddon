# Smart Blind addon device code
# developed by Amit Punit n01203930
import RPi.GPIO as GPIO
from time import sleep
import board
import busio
from smbus2 import SMBus
from mlx90614 import MLX90614
import adafruit_bh1750
import time
import pyrebase
import datetime
import threading
import json
bkey = "0001"
GPIO.setmode(GPIO.BCM) # set borad to gpio
#init code for temperature sensor
bus = SMBus(1)
tempsensor = MLX90614(bus, address=0x5A)

#init code for lux sensor
i2c = board.I2C()
luxsensor = adafruit_bh1750.BH1750(i2c)

#init code for distance sensor
GPIO.setwarnings(False)
TRIG = 23
ECHO = 24

#init code of dc motor and l293d

INO = 26 #Pin 37
INT = 19 #Pin 35
EN  = 13 #Pin 33
GPIO.setup(INO,GPIO.OUT)
GPIO.setup(INT,GPIO.OUT)
GPIO.setup(EN,GPIO.OUT)

#led init
LED = 11 #Pin 23
GPIO.setup(LED,GPIO.OUT)

print ("Distance Measurement In Progress")
GPIO.setup(TRIG,GPIO.OUT)
GPIO.setup(ECHO,GPIO.IN) 
#GPIO.output(TRIG,False)
print ("Waiting For Sensor To Settle")
#time.sleep(2) 

#Pyrebase setup config
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

#this function will stop the motor   
def motor_stop():
    print ("Stopping motor")
    GPIO.output(EN,GPIO.LOW)

#this will get the current blind command from database used to either open or close the blind    
def get_command():
    cmd = db.child("0001").child("Status").get()
    print("Current command", cmd.val())
    return cmd.val()

#this function will get the height of the blind from the firebase database
def get_height():
    h = db.child("0001").child("Height").get()
    print(h.val())
    return int(h.val())

# this function will check the blinds current status to see if it matches the command in the database
def blind_status(currentStatus,cmd):
    if currentStatus == cmd:
        return True # do nothing
    else:
        return False # either open or close blind

#turns on the led
def led_on():
    GPIO.output(LED,GPIO.HIGH)
    print("led on")

#turns off the led
def led_off():
    GPIO.output(LED,GPIO.LOW)
    print("led off")

# gets data from temp sensor
def get_tempData():
    temp = tempsensor.get_object_1()
    t = "{:.0f}".format(temp)
    print(t," Degrees Celcius")
    return t

#gets data from the BH1750 light sensor
def get_luxData():
    lux = luxsensor.lux 
    l = "{:.0f}".format(lux)
    print(l," LUX")
    return l

#gets reading from the hc sr04 distance sensor
def get_distance():
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
    return distance

# to check the reading from the distance sensor
def check_distance(dis):
    newd = dis
    h = float(bheight)
    while newd < h: #loop to get new distance values untill they are less then the users blind height
        if dis > h :
            newd = get_distance()
            print(newd)
    return newd

def get_current_date_time():
    now = datetime.datetime.now()
    d = now.strftime("%d-%m-%Y")
    t = now.strftime("%H:%M")
    dateAndTime = {
        "date": d,
        "time": t
        }
    print(dateAndTime)
    return dateAndTime

# compares current date and time to see if blind should be opened or closed
def compare_time(uTime,curTime):
    if uTime["date"] == curTime["date"]: # compare dates
        if uTime["time"] == curTime["time"]: #if true compare time
            return uTime["op"] #if true will return desired operation.
    if uTime["date"] != curTime["date"]:
        return False

def test_device():
    light = get_luxData() #gets data from light sensor
    send_lux(light) # sends data to database
    temperature = get_tempData()
    send_temp(temperature)
    distance = get_distance()
    led_on()
    sleep(1)
    led_off()
    sleep(1) 
    motor_open()
    sleep(1)
    motor_close()
    sleep(1)
    motor_stop()
    sleep(3)
    print("____________________________________")

def open_protocol(): # runs when user wants to open blind
    cd = get_distance()
    led_on()
    while cd > 5: # this is the loop to open blind
        motor_open() #operate blind for 1 second
        sleep(0.5)
        cd = get_distance() # get the new height of the blind
    motor_stop()
    led_off() 
         
def close_protocol(): # runs when user wants to close the blind
    cd = get_distance()
    led_on()
    while cd < bheight: # this is the loop to open blind
        motor_close() #operate blind for 1 second
        sleep(0.5)
        cd = get_distance() # get the new height of the blind
    motor_stop()
    led_off() 

def schedule_blind_protocol(userSetTime):
    cT = get_current_date_time()
    s = compare_time(userSetTime,cT)
    return s

def check_max_light(cLight,mxLight):
    if cLight > mxLight:
         return True
    else:
        return False    

def check_min_light(cLight,mnLight):
    if cLight < mnLight:
         return True
    else:
        return False

def check_max_temp(cTemp,mxTemp):
    if cTemp > mxTemp:
         return True
    else:
        return False    

def check_min_temp(cTemp,mnTemp):
    if cTemp < mnTemp:
         return True
    else:
        return False 

bheight = float(get_height()) # gets the height of the blind from database
# this code is to make sure the blind always 
# operates when the device starts
i = get_command()
if i == "close":
    blindStatus = "open"
if i == "open":
    blindStatus = "close"
#while True:
# Gets Device Firebase Json
data = db.child(bkey).get().val()
print(f"from database\n",data)

#gets tempertaure data from MLX90614ESF and saves it into firebase Json
data["Temperature"] = get_tempData()
print(data["Temperature"])

# gets light data from BH1750 and saves it to firebase json
data["Light"] = get_luxData()
print(data["Light"])

# get another reading from sensors and converts them to string 
# in order to compare user set min and max values
light = int(get_luxData())
print("light variable datatype",type(light))
temp = int(get_tempData())
print("temp variable datatype",type(temp))

# gets maximum and minimum light values form database 
# JSON and converts it into an int datatype
mxl = int(data["ULight"]["maxLight"])
print(type(mxl))
print("Max Light", mxl)

mnl = int(data["ULight"]["minLight"])
print(type(mnl))
print("Min Light", mnl)

# gets maximum and minimum Temperature values form database 
# JSON and converts it into an int datatype
mxt = int(data["UTemp"]["maxTemp"])
print(type(mxt))
print("Max Temp", mxl)

mnt = int(data["UTemp"]["minTemp"])
print(type(mnt))
print("Min Temp", mnt)

#get the current mode of the device
mode = data["Mode"]

#get current date time and operation of blind operation
userTime = data["Schedule"]
print(userTime)

#checks if current time and user set time is the same true if is false if not
sechCheck = schedule_blind_protocol(userTime)
print("is the user time same as current time? ",sechCheck)

#compare max light and temperature to see if blind is to close
cmaxl = check_max_light(light,mxl)
print("cmaxl",cmaxl)
cmaxt = check_max_temp(temp,mxt)
print("cmaxt",cmaxt)

#compare min light and temperature to see if blind is to open
cminl = check_min_light(light,mnl)
print("cminl",cminl)
cmint = check_min_temp(temp,mnt)
print("cmint",cmint)


#if in Manual mode
if mode == "man":
    statusCheck = blind_status(blindStatus,data["Status"]) 
    if statusCheck == False:
             cmd = get_command()
             if cmd == "open": #run open blind routine
                 print("blind is to be opened")
                 open_protocol()
                 print("Blinds is now opened")
                 data["Status"] = "open"
                 blindStatus = "open"
             if cmd == "close": #run close blind routine
                 print("blind is to be closed")
                 close_protocol()
                 data["Status"] = "close" #updates current blind status in firebase 
                 blindStatus = "close"  #updates current blind status on device
                 print("Blinds is now closed")
#if in Automatic mode 
if mode == "auto":
    if sechCheck == "Open":
        print("blind is to be opened")
        open_protocol()
        print("Blinds is now opened")
        data["Status"] = "open"
        blindStatus = "open"  #updates current blind status on device
         
    if sechCheck == "Close":
        print("blind is to be opened")
        close_protocol()
        print("Blinds is now opened")
        data["Status"] = "close"
        blindStatus = "close"  #updates current blind status on device
        
    if cmaxl == True or cmaxt == True:
        if blindStatus == "open":
            print("blind is to be closed")
            close_protocol()
            data["Status"] = "close"
            blindStatus = "close"  #updates current blind status on device
        
        
    if cminl == True or cmint == True:
        if blindStatus == "close":
            print("blind is to be opened")
            open_protocol()
            print("Blinds is now opened")
            data["Status"] = "open"
            blindStatus = "open"  #updates current blind status on device

db.child(bkey).set(data)
# GPIO.cleanup()
