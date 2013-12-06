#!/bin/bash
continentFiles=./input/*.shp
tinyCountryFiles=./input/country/500/*.shp
smallCountryFiles=./input/country/1000/*.shp
midsizedCountryFiles=./input/country/5000/*.shp
largeCountryFiles=./input/country/10000/*.shp
hugeCountryFiles=./input/country/50000/*.shp
giantCountryFiles=./input/country/more/*.shp



for file in $continentFiles
do
filename=$(basename $file ".shp")

topojson \
  --bbox \
  --id-property iso_a2 \
  --properties n=name,t=type \
  --simplify 0.00001 \
  --out ./out_1/$filename.json \
  regions=$file

done

for file in $tinyCountryFiles
do
filename=$(basename $file ".shp")

topojson \
  --bbox \
  --id-property iso_a2 \
  --properties n=name,t=type \
  --out ./out_1/country/$filename.json \
  regions=$file

done

for file in $smallCountryFiles
do
filename=$(basename $file ".shp")

topojson \
  --bbox \
  --id-property iso_a2 \
  --properties n=name,t=type \
  --simplify 0.00000001 \
  --out ./out_1/country/$filename.json \
  regions=$file

done

for file in $midsizedCountryFiles
do
filename=$(basename $file ".shp")

topojson \
  --bbox \
  --id-property iso_a2 \
  --properties n=name,t=type \
  --simplify 0.00000005 \
  --out ./out_1/country/$filename.json \
  regions=$file

done

for file in $largeCountryFiles
do
filename=$(basename $file ".shp")

topojson \
  --bbox \
  --id-property iso_a2 \
  --properties n=name,t=type \
  --simplify 0.0000001 \
  --out ./out_1/country/$filename.json \
  regions=$file

done


for file in $hugeCountryFiles
do
filename=$(basename $file ".shp")

topojson \
  --bbox \
  --id-property iso_a2 \
  --properties n=name,t=type \
  --simplify 0.0000005 \
  --out ./out_1/country/$filename.json \
  regions=$file

done

for file in $giantCountryFiles
do
filename=$(basename $file ".shp")

topojson \
  --bbox \
  --id-property iso_a2 \
  --properties n=name,t=type \
  --simplify 0.000001 \
  --out ./out_1/country/$filename.json \
  regions=$file

done


