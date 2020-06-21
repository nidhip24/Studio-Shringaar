<template>
    <div>
        <h4 @click="sendmail">Send mail</h4>
    </div>
</template>

<script>
import firebase from 'firebase'

export default {
  data () {
    return {
      formdata: {
        email: ''
      }
    }
  },
  methods: {
    sendmail () {
      // alert('called')
      // const { email } = this.formdata
      const email = 'nidhipkathiriya@gmail.com'
      firebase.functions().httpsCallable('sendMail')({ email: email }).then(result => {
        console(result)
        if (result.message !== undefined) {
          alert(result.data.message)
        } else {
          console.log(result)
          alert(result.data.error)
        }
      })
    }
  }
}
</script>
