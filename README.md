![Stickier logo](http://iuliux.ro/stickier/icon.png)

Stickier QR feed reader
=======================

PoliInfo is an augmented reality application, aimed to provide location and identity based content to the user in a friendly manner.

About
-----
Academic project that involves Android platform for client-side and GoogleAppEngine for server-side. Project page on [PUB wiki](http://curs.pub.ro/index.php/android-projects/159-poliinfo-reader).

Screenshots
-----------

[Screenshots wiki page](https://github.com/iuliux/StickierQR/wiki/Screenshots)

Details
-------

The system is based on the client-server architecture.

The client is represented by the user of the actual Android application and should be given a user/password pair for authentication with the server. Each user name is associated with a group of roles (e.g. John Doe may be associated with the following roles: student, teacher and guest). Access to content items is role-based (students don't have access to the content intended for teachers only).  
In the same time, a single user can be assigned any number roles and a single content item may be intended any number of roles. In this way identity based content is delivered to the client.

In order to address the second concern, location based content, the server has to be able to get the current client's location. We decided to use `QR barcodes` for this task because they offer the desired level of accuracy (when the user sees a barcode and scans it a request is sent to the server which now knows that the user is a couple of feet away from the barcode) and they represent a solid tool for augmented reality applications.  
The other alternatives were `GPS` or `GSM-based`, but neither provided the accuracy and reliability of the previously mentioned solution. Using the identification system and knowing the location of the user, the server is able to offer information that is addressed to a specific user and that is relevant to that specific location.

For instance, a student located in the "EG hallway" area is probably interested in knowing how to get to EC 004 lecture room, not AN 030 (location based) or a person from the maintenance department may consider guidance related info irrelevant since he or she already knows that place like the back of his/her hand but that person may be interested in knowing what kind of maintenance operations were conducted in that area (identity based).

##### QR recognition based on [ZXing](http://code.google.com/p/zxing/) OpenSource project