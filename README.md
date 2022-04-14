# Arkyris
A diary, but with colours instead of words!

## Description
Arkyris (a combination of the Greek Gods of colour, Arke and Iris), if an app for Android that lets you keep track of how you feel, in the form of different colours.  You can even share how you feel (anonymously) with other users!  Just select a colour and post it.  You can always delete it afterwards!

## Dependencies
* Android Studio. See also [app level](https://github.com/Carkzis/Arkyris/blob/main/app/build.gradle) and [project level](https://github.com/Carkzis/Arkyris/blob/main/build.gradle) gradle builds.
* Android SDK 26 for running the app.

## Installing
* You can download the code from the Arkyris repository by clicking "Code", then "Download ZIP".
* You can then install this from within Android Studio onto an emulator or a mobile device with a minimum SDK of 26 via the "Run 'app'" command (Shift+F10 by default).
* There is a Django Rest Framework (DRF) backend already deployed on heroku, however if you would like to create you own back end, please see the [DRF docs](https://www.django-rest-framework.org/).
* Note: This is not currently on the Google Play Store!

## Executing the program
* You can run the app off a suitable emulator/mobile device.
* Login Screen: You can either log in, or register an account.  Please don't forget your password!  There is not currently a way to reset it.

<img src="https://github.com/Carkzis/Arkyris/blob/main/arkyris_screenshots/arkyris_login.jpg?raw=true" width="300" />

* Register Screen:  You can register an account from here.

<img src="https://github.com/Carkzis/Arkyris/blob/main/arkyris_screenshots/arkyris_register.jpg?raw=true" width="300" />

* Arke Tab:  This is the tab for all the public "diary" entries, an entry being a "colour" associated with a user.  From here, you can press the green "fab" to choose a colour, and add an entry!  This will also be added to the Iris tab, which is for your personal entries.  Adding an entry here will always mean it is public, however you can changing this in the Iris tab!

<img src="https://github.com/Carkzis/Arkyris/blob/main/arkyris_screenshots/arkyris_main_light.jpg?raw=true" width="300" />
<img src="https://github.com/Carkzis/Arkyris/blob/main/arkyris_screenshots/arkyris_main_dark.jpg?raw=true" width="300" />
<img src="https://github.com/Carkzis/Arkyris/blob/main/arkyris_screenshots/arkyris_color_picker.jpg?raw=true" width="300" />

* Iris Tab:  This is the tab for all of your personal entries into the colour diary.  From here, you can select your current colour by clicking on the large circle in the centre of the screen.  Once you have selected and confirmed a colour you can then click the green "fab" to add it to the database.  You can either make it public, where it will be shown in the Arke tab, or private, where it will only show in the Iris tab.
* Iris Tab continued:  If you click a specific entry for a few seconds, you will then be prompted with a choice to either change the publicity status (e.g. from public to private or vice versa), or delete the entry which will remove it from both Arke and Iris!  Or you can do neither.

<img src="https://github.com/Carkzis/Arkyris/blob/main/arkyris_screenshots/arkyris_private.jpg?raw=true" width="300" />

* Settings:  You can get to the settings through the toolbar.  Here you can choose to log out (either just from the current device or all devices), or choose to change your password.

<img src="https://github.com/Carkzis/Arkyris/blob/main/arkyris_screenshots/arkyris_settings.jpg?raw=true" width="300" />

<img src="https://github.com/Carkzis/Arkyris/blob/main/arkyris_screenshots/arkyris_change_password.jpg?raw=true" width="300" /> 

## Authors
Marc Jowett (carkzis.apps@gmail.com)

## Version History
* 1.0
  * Initial Release.  See [commits](https://github.com/Carkzis/Arkyris/commits/main).

## License
This is licensed under the BSD-3-Clause License.  You can see the LICENSE.md for further details.
