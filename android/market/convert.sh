#!/bin/sh
# icons
convert -background none -resize 512x512\! ./orig/owl.svg 512x512.png
convert -background none -resize 72x72\!   ./orig/owl.svg ../res/drawable-hdpi/icon.png
convert -background none -resize 36x36\!   ./orig/owl.svg ../res/drawable-ldpi/icon.png
convert -background none -resize 48x48\!   ./orig/owl.svg ../res/drawable-mdpi/icon.png

# screen shots
convert -resize 480x854\! ./orig/ss1.png ss1.png
convert -resize 480x854\! ./orig/ss2.png ss2.png
