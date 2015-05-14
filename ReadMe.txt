1. Project Members: Rakshit Pithadia (817340687) & Harsh Shah (817354961)

2. Description: This is an Android app for sharing photos, where one can upload photos to multiple services (Dropbox, Google Drive) 
in just one click. A person would create a profile with the services he wants, choose the photos and just say upload. This profile 
is also saved if the user wants to use it again in future.
The app does not have any errors, and is working on the Android Phones provided by you.

3. Third Party Libraries: We have included some of these in our build.gradle file so it should sync whenever you try to run the app. 
The ones that is not included in gradle (included as jars) are
	a) Dropbox - https://www.dropbox.com/developers/downloads/sdks/core/android/dropbox-android-sdk-1.6.3.zip

Also, since we have incorporated Google Drive, and it uses the Google Play Services we needed to sign the app using keytool. We 
signed it just for debug mode and so for testing google drive you will have to follow the instructions mentioned here(https://developers.google.com/drive/android/get-started#install_and_configure_the_google_play_services_sdk)
For your convenience I am listing down the steps to follow in order
Step 1: Run the following command in your terminal "keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v"
If it asks you for a password enter "android".
Step 2: Copy the SHA1 fingerprint that will be listed
Step 3: Go to the project on Google Developer Console (We have sent you invitation for our project on the email ID rwhitney@mail.sdsu.edu, you will have to accept that first). Link - https://console.developers.google.com/project/eco-league-94317
Step 4: Go to APIs & auth -> Credentials. Click on "Create new Client ID". Select "Installed application". Select "Android". In the Package name enter "edu.sdsu.cs.sharepic". In Signing certificate fingerprint (SHA1) enter the SHA1 fingerprint from step 2. And finally click "Create Client ID"

Now you can run the app from Android Studio and it will let you login to Google Drive.

4. From what we have tested, there are no known bugs.

We have included a Demo Video of the app in the zip file.