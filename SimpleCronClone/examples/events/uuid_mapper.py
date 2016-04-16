#!/usr/bin/env python
 
# get_mc_name
#
# Expects 1 argument: 32-character UUID (no hypens)
# Example: get_mc_name f46edc7ed73549cd853b4f473857990c
#
# Output: Character name of UUID
# Example: Saladfingers
 
import urllib2
import sys
import json

# build URL using argument passed
url = 'https://us.mc-api.net/v3/name/' + sys.argv[1].replace("-","")
 
# get data
data = urllib2.urlopen(url)
d = data.read().decode("utf-8")

#parse json, return name
js = json.loads(d)
sys.stdout.write(js['name'])
