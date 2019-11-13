import firebase from "firebase"

const config = {
  apiKey: "AIzaSyD8BRWgJG02B49kPDYzuV41b8mW5sXKDPA",
  authDomain: "turnout-eca1e.firebaseapp.com",
  databaseURL: "https://turnout-eca1e.firebaseio.com",
  projectId: "turnout-eca1e",
  storageBucket: "turnout-eca1e.appspot.com",
  messagingSenderId: "864962754772",
  appId: "1:864962754772:web:efa9313e3f00f7fdefa341"
};

export const firebaseApp = firebase.initializeApp(config)
export const eventsRef = firebaseApp.database().ref().child("events")
