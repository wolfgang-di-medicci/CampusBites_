## Description
Campus Bites App is a mobile application designed to track students using the campus café for breakfast, lunch, and dinner. The app stores student information and their daily café usage locally and uploads it to Firebase Firestore cloud storage. At first the projects title was called "**Digital Meal Card**" but later was changed to "**Campus Bites**" for branding purposes.

## Constructive criticism I was given
1. Add student images by incorporating a suitible database
2. Change the name and UI for branding purposes

## Features
- Store student information and their café usage locally.
- Upload data to Firebase.
- Track breakfast, lunch, and dinner usage and display reports.
- Downloads a specific day's usage by a ticker to local files in the form of txt file.
- Authentication and authorization of one ticker user per phone.
- Forgot password retreivial using predifined question and answer key assigned when signing up
- Report misuse.

## Technologies Used
- Programming Language: Java
- IDE: Android Studio
- Database for student information: Firebase
- Database for student image: Cloudinary
- Display images of students: Glide

## Installation
1. Clone this repository
2. Open the project in Android Studio.
3. Sync Gradle files.
4. Run the app on an emulator or Android device.
5. Create an account specifically for a phone using a ticker credentials assigned by the campus. For Trial purposes use username="21856" and password="1111" will setup your app with an already created account.

## Usage
- Launch the app and enter student details.
- Tick student meal usage for every meal, everyday.
- Report students trying to use more than one round per meal or try to enter using another students credentials.
- Track daily meal usage for breakfast, lunch, and dinner.
- Data is uploaded to Firebase after each meal.

## Developer
Kaleb Yohannese
