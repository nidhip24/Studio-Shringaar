import firebase from 'firebase'
import 'firebase/firestore'

const config = {
  apiKey: '******',
  authDomain: '****',
  databaseURL: '****',
  projectId: '****',
  storageBucket: '****',
  messagingSenderId: '****',
  appId: '****',
  measurementId: '****'
}

const firebaseApp = firebase.initializeApp(config)

const firestore = firebaseApp.firestore()

export default firestore
