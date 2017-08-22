import holoviews as hv
import seaborn as sns
import pandas as pd
import numpy as np
import pickle as pkl
import matplotlib.pyplot as plt
from datetime import *
from collections import OrderedDict as od
from glob import glob
from os.path import basename
from numpy import *
from scipy.stats import skew
from scipy.stats import kurtosis
from sklearn.model_selection import validation_curve
from sklearn.cross_validation import train_test_split
from sklearn.cross_validation import ShuffleSplit
from sklearn import model_selection
from sklearn.grid_search import GridSearchCV
from sklearn.linear_model import LogisticRegression
from sklearn.neighbors import KNeighborsClassifier
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis
from sklearn.naive_bayes import GaussianNB
from sklearn.ensemble import VotingClassifier
from sklearn.metrics import accuracy_score, classification_report, roc_curve, auc

column_names=['timestamp',
              'lastLatitude','lastLongitude',
              'lastAccelerometerValues[0]','lastAccelerometerValues[1]','lastAccelerometerValues[2]',
              'lastGyroscopeValues[0]','lastGyroscopeValues[1]','lastGyroscopeValues[2]',
              'lastMagnetometerValues[0]','lastMagnetometerValues[1]','lastMagnetometerValues[2]',
              'label']

datatype={'timestamp':int64,
          'lastLatitude':float64,'lastLongitude':float64,
          'lastAccelerometerValues[0]':float64,'lastAccelerometerValues[1]':float64,'lastAccelerometerValues[2]':float64,
          'lastGyroscopeValues[0]':float64,'lastGyroscopeValues[1]':float64,'lastGyroscopeValues[2]':float64,
          'lastMagnetometerValues[0]':float64,'lastMagnetometerValues[1]':float64,'lastMagnetometerValues[2]':float64,
          'label':str}

def aggr(val):
    if (val.dtype==float64):
        return val.mean()
    elif (val.dtype==object):
        return set(val).pop()

def data_parser():
    data=od()
    for recording in glob('*.csv'):
        key=basename(recording)[0:12]
        print('\nReading File\t'+recording)
        data[key]=pd.read_csv(recording,
                              na_filter=True,
                              names=column_names,
                              header=0,
                              dtype=datatype
                             )
        data[key].reset_index()
        print('\n\tAggregating '+str(len(data[key].timestamp))+' timestamps.')
        data[key].timestamp=data[key].timestamp.apply(lambda x:int((x-data[key].timestamp[0])*1e-8))
        print('\n\tFixed Rate Sampling transformed at 100ms')
        data[key]=data[key].dropna().groupby('timestamp').agg(aggr)
        print('\n\tData Cleaned to '+str(len(data[key].index))+' timestamps.')
    print('\n\t\t\tData Loaded Successfully!')
    return data

def scatterAll(dt):
    accl0=scatterPlot(dt,['lastAccelerometerValues[0]'])
    accl1=scatterPlot(dt,['lastAccelerometerValues[1]'])
    accl2=scatterPlot(dt,['lastAccelerometerValues[2]'])
    gyro0=scatterPlot(dt,['lastGyroscopeValues[0]'])
    gyro1=scatterPlot(dt,['lastGyroscopeValues[1]'])
    gyro2=scatterPlot(dt,['lastGyroscopeValues[2]'])
    magnt0=scatterPlot(dt,['lastMagnetometerValues[0]'])
    magnt1=scatterPlot(dt,['lastMagnetometerValues[1]'])
    magnt2=scatterPlot(dt,['lastMagnetometerValues[2]'])
    return (accl0+accl1+accl2+gyro0+gyro1+gyro2+magnt0+magnt1+magnt2).cols(3)

def scatterPlot(dt,dims):
    move=hv.Scatter(dt[dt['label'].str.strip()=='move'].reset_index(), vdims=dims)(style={'color':"green"})
    stop=hv.Scatter(dt[dt['label'].str.strip()=='stop'].reset_index(), vdims=dims)(style={'color':"red"})
    right=hv.Scatter(dt[dt['label'].str.strip()=='right'].reset_index(), vdims=dims)(style={'color':"blue"})
    left=hv.Scatter(dt[dt['label'].str.strip()=='left'].reset_index(), vdims=dims)(style={'color':"yellow"})
    return move*stop*left*right

def saveData(filename,data):
    filehandler = open(filename, 'w')
    pkl.dump(data, filehandler)

def loadData(filename):
    filehandler = open(filename, 'r')
    return pkl.load(filehandler)
