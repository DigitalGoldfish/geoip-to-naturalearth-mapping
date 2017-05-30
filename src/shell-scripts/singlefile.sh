#!/bin/bash
continentFiles=c:/Node/Data/shapefiles-new/country/50000/FR.shp
#continentFiles=c:/Node/Data/shapefiles-new/country/5000/PT.shp


for file in $continentFiles
do
filename=$(basename $file ".shp")
echo $file
echo $filename

#node topojson \
#  --bbox \
#  --id-property iso_a2 \
#  --properties n=name,t=type \
#  --simplify 0.00001 \
#  --out $filename.json \
#  regions=$file


# WATCH OUT!   the field names ISO_A2, NAME, TYPE are case sensitive
# also make sure the --simplify parameter matches the appropriate value in generate-topojson.sh for the country being processed
# if not then the quality of the border lines will be effected
topojson --bbox --id-property ISO_A2 --properties t=TYPE,n=NAME --simplify 0.0000005 --out $filename.json regions=$file


echo -----------

done
