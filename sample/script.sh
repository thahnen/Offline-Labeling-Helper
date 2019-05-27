#!/usr/bin/env bash

## Das ganze Video in 1-Minuten-Videos unterteilen!
ffmpeg -i dvs_record.celex_04.dvs.avi -c copy -map 0 -segment_time 00:01:00 -f segment %02d.dvs_record.celex_04.dvs.avi

## Da das letzte irgendwie immer scheisse wird einfach loeschen!
rm $(ls *.dvs_record.celex_04.dvs.avi | tail -1)
