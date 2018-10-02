import csv
import string
import math
import itertools
import datetime
import time
import random

path = "freedec_"
ext = ".csv"

allActivities = list(string.ascii_lowercase) #+ list(string.ascii_uppercase)

for n in range(26):
    n += 1
    print("n: " + str(n))
    activities = allActivities[0:n]
    p = path + str(n) + ext

    with open(p, 'w') as csvfile:
        writer = csv.writer(csvfile, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
        writer.writerow(['case','event']) #,'time'])
        for k in range(100000):
            k += 1
            for i in range(n):
                writer.writerow(['T' + str(k), random.choice(allActivities)])            
