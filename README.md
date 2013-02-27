PagePlusBalance v0.5.1
===========================
PagePlusBalance retrieves your balance from PagePlusCellular.com, currently for android phones.

Author: Travis Burtrum (moparisthebest)  
Email: android@moparisthebest.org

Features:
----------------------
1. Gets your balance directly from PagePlusCellular.com,
saving bandwidth by making fewer requests than a browser
could and not downloading any javascript, css, or images
that a browser would.
2. Optionally, over mobile data, will send your details
to a service on my server that uses the same code to grab
your balance from Page Plus as the android app and sending
you back only the data you want as plain text, saving you
a substantial amount of bandwidth.
3. Now supports getting your balance by automatically
sending a text of BAL to 7243, and parsing the response.
4. Stores your balance on your phone along with the last
time you refreshed it.
5. Open source under GNU/AGPL!

Android application icon © Nevit Dilmen [CC-BY-SA-3.0 (http://creativecommons.org/licenses/by-sa/3.0) or GFDL (http://www.gnu.org/copyleft/fdl.html)], via Wikimedia Commons
https://commons.wikimedia.org/wiki/File:Owl.svg

Changelog
----------------------
0.5.1:  
     1. Page Plus requested I remove the app from the market because I am infringing on their IP, so this update changes the icon.  New icon is © Nevit Dilmen [CC-BY-SA-3.0 or GFDL], via Wikimedia Commons
  
0.5:  
     1. Added ability to check balance via SMS (sending BAL to 7243)  
     2. Behind-the-scenes major code re-write to enable better future features.
  
0.4:  
     1. Fixed parsing of page plus's new website.  
     2. Now return list of valid phone names if you don't enter a correct one.

Android market entry
----------------------
This is NOT an official page plus application, if it wasn't obvious enough already.  I am simply a page plus user who wanted a decent way to check my balance, so I wrote this. Page Plus now all of the sudden after about 3 years wants this app removed from the market because they *may* write their own app *sometime* in the future.  If it does get removed, it will always be available at the github URL below (so bookmark it), but maybe you should email page plus to tell them you would like this app to remain on the market if it's helpful to you.

This app allows Page Plus users to check up on their balance, minutes, texts, and data using the least amount of data transfer possible, OR via SMS.

Currently tested and works on Pay-go, TnT1200, and Unlimited plans. If it doesn't work on your plan, email me the HTML source to your page plus account info and I'll add in support.

If you have issues or requests for this app, please create an issue at the github link below.  If you can't do that, email me at android@moparisthebest.org, but know that github issues will receive priority.

Code released under GNU/AGPL at github.
https://github.com/moparisthebest/PagePlusBalance

How to comply with License:
----------------------
Basically, the AGPL requires that if you distribute the code,
or if you host the application where the public can access it,
such as a public server, then you must provide the source code
along with your modifications as a download on that page.
This is to promote development of the program and share
code with everyone to improve it.  If you have any questions,
contact me at android@moparisthebest.org.  If you use the code,
I'd love to know, so drop me a line if you would.

License: (full text in LICENSE)
----------------------
PagePlusBalance retrieves your balance from PagePlusCellular.com, currently for android phones.  
Copyright (C) 2010 Travis Burtrum (moparisthebest)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

TODO:
----------------------
1. You suggest it, via a github issue or email!
