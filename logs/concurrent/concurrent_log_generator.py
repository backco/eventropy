import csv
import string
import math
import itertools
import datetime
import time

def midPermutations(k, seq, writer):
    
    i = math.floor((len(seq)/2) - math.floor(k/2))
    j = i + k
    c = 1
    midSeq = seq[i:j]

    for perm in itertools.permutations(midSeq):
        newSeq = activities
        for e in range(len(perm)):
            newSeq[i+e] = perm[e]
        for e in newSeq:
            writer.writerow(['T' + str(c), e]) 
        c += 1

path = "concurrent_"
ext = ".csv"

allActivities = list(string.ascii_lowercase) + list(string.ascii_uppercase)

for n in range(17):
    n += 9
    print("n: " + str(n))
    activities = allActivities[0:n]
    
    for k in range(9):
        k += 1
        p = path + str(len(activities)) + "_" + str(k) + ext
        with open(p, 'w') as csvfile:
            writer = csv.writer(csvfile, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
            writer.writerow(['case','event'])
            print('   k: ' + str(k))
            midPermutations(k, activities, writer)
