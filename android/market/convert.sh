#!/bin/sh
convert -resize 512x512\! ./orig/orig-owl.jpg 512x512.jpg
convert -resize 480x854\! ./orig/ss1.png ss1.png
convert -resize 480x854\! ./orig/ss2.png ss2.png
