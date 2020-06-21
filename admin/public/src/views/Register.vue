<template>
  <div class="home container">
    <h1 class="mx-auto" style="width: 200px;">Register</h1>
    <b-row>
      <b-col md="6" offset-md="3">
        <b-form @submit.prevent="onRegister">
          <b-form-group id="input-group-01" label="Your name" label-for="input-01">
            <b-form-input
              id="input-01"
              type="text"
              v-model="formData.name"
              required
              placeholder="Enter your name"
            ></b-form-input>
          </b-form-group>
          <b-form-group id="input-group-1" label="Your email" label-for="input-1">
            <b-form-input
              id="input-1"
              type="email"
              v-model="formData.email"
              required
              placeholder="Enter email"
            ></b-form-input>
          </b-form-group>
          <b-form-group id="input-group-2" label="Password" label-for="input-2">
            <b-form-input
              id="input-2"
              type="password"
              v-model="formData.password"
              required
              placeholder="Enter Password"
            ></b-form-input>
          </b-form-group>
          <b-button type="submit" variant="primary">Submit</b-button>
        </b-form>
      </b-col>
    </b-row>
  </div>
</template>

<script>
import firebase from 'firebase'
import { mapGetters } from 'vuex'

export default {
  data () {
    return {
      formData: {
        name: '',
        email: '',
        password: ''
      }
    }
  },
  created: function () {
    if (this.user.loggedIn) {
      this.$router.replace({ name: 'Home' })
    }
  },
  methods: {
    onRegister () {
      const { email, password, name } = this.formData
      firebase
        .auth()
        .createUserWithEmailAndPassword(email, password)
        .then(data => {
          alert('Registered')
          data.user
            .updateProfile({
              displayName: name
            })
            .then(() => {
              this.$router.replace({ name: 'Home' })
            })
        })
        .catch(err => {
          this.error = err.message
        })
    }
  },
  computed: {
    // map `this.user` to `this.$store.getters.user`
    ...mapGetters({
      user: 'user'
    })
  }
}
</script>
