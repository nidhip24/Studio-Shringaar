import Vue from 'vue'
import Vuex from 'vuex'
import db from '../firebase'
import firebase from 'firebase'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    options: [],
    orderList: [],
    productList: [],
    homeList: [],
    categoryList: [],
    promocodeList: [],
    discountList: [],
    categoryListWithLink: [],
    user: {
      loggedIn: false,
      data: null
    }
  },
  getters: {
    getCategoryListWithLink (state) {
      return state.categoryListWithLink
    },
    allProduct (state) {
      return state.productList
    },
    user (state) {
      return state.user
    }
  },
  mutations: {
    ADD_DISCOUNT (state, payload) {
      const index = state.discountList.findIndex(item => item.id === payload.id)
      if (index === -1) {
        state.discountList.push({
          id: payload.id,
          name: payload.name,
          discount: payload.discount,
          endDate: payload.endDate,
          maxDiscount: payload.maxDiscount,
          cat: payload.cat,
          startDate: payload.startDate
        })
      }
    },
    ADD_PROMOCODE (state, payload) {
      const index = state.promocodeList.findIndex(item => item.id === payload.id)
      if (index === -1) {
        state.promocodeList.push({
          id: payload.id,
          discount: payload.discount,
          endDate: payload.endDate,
          maxDiscount: payload.maxDiscount,
          name: payload.name,
          startDate: payload.startDate
        })
      }
    },
    RESET_DISCOUNT (state) {
      state.discountList = []
    },
    RESET_PROMOCODE (state) {
      state.promocodeList = []
    },
    DELETE_DISCOUNT (state, p) {
      const index = state.discountList.findIndex(item => item.id === p.id)

      if (index >= 0) {
        state.discountList.splice(index, 1)
      }
    },
    DELETE_PROMOCODE (state, p) {
      const index = state.promocodeList.findIndex(item => item.id === p.id)

      if (index >= 0) {
        state.promocodeList.splice(index, 1)
      }
    },
    ADD_ORDER (state, payload) {
      state.orderList.push({
        id: payload.id,
        uid: payload.uid,
        addressID: payload.addressID,
        amount: parseInt(payload.amount, 10) / 100,
        product_list: payload.product_list,
        orderID: payload.orderID,
        payment_id: payload.payment_id,
        status: payload.status,
        order_status: payload.order_status,
        timestamp: new Date(payload.timestamp.seconds * 1000),
        prod: payload.prod,
        address: undefined,
        name: undefined,
        phone: undefined,
        product: undefined
      })
    },
    UPDATE_ORDER (state, payload) {
      const index = state.orderList.findIndex(item => item.id === payload.id)
      // alert(payload.address)
      // console.log('in updateorder')
      const temp = state.orderList[index]
      temp.address = payload.address
      temp.name = payload.name
      temp.phone = payload.phone
      temp.city = payload.city
      temp.country = payload.country
      temp.state = payload.state
      state.orderList.splice(index, 1, temp)
    },
    UPDATE_ORDER_ONE (state, payload) {
      const index = state.orderList.findIndex(item => item.id === payload.id)
      state.orderList.splice(index, 1, {
        id: payload.id,
        uid: payload.uid,
        addressID: payload.addressID,
        amount: parseInt(payload.amount, 10) / 100,
        orderID: payload.orderID,
        payment_id: payload.payment_id,
        status: payload.status,
        order_status: payload.order_status,
        timestamp: new Date(payload.timestamp.seconds * 1000),
        prod: payload.prod
      })
    },
    ADD_PRODUCT_ORDER (state, payload) {
      const producttemp = []
      const index = state.orderList.findIndex(item => item.id === payload.id)
      // console.log('in add product order')
      // console.log(state.orderList[index])
      if (state.orderList[index].product !== undefined) {
        for (const val of state.orderList[index].product) {
          producttemp.push(val)
        }
      }
      // alert('in' + index)
      producttemp.push(payload.product)
      const temp = state.orderList[index]
      temp.product = producttemp
      state.orderList.splice(index, 1, temp)
    },
    UPDATE_PRODUCT_ORDER (state, payload) {
      const producttemp = []
      const index = state.orderList.findIndex(item => item.id === payload.id)
      if (state.orderList[index].product !== undefined) {
        for (const val of state.orderList[index].product) {
          if (val.id === payload.docid) {
            const valtemp = val
            valtemp.pname = payload.pname
            valtemp.price = payload.price
            valtemp.image = payload.image
            producttemp.push(valtemp)
          } else {
            producttemp.push(val)
          }
        }
      }
      // alert('in' + index)
      const temp = state.orderList[index]
      temp.product = producttemp
      state.orderList.splice(index, 1, temp)
    },
    RESETLIST (state) {
      state.productList = []
    },
    RESETHOMELIST (state) {
      state.homeList = []
    },
    RESET_ORDER_LIST (state) {
      state.orderList = []
    },
    addProd (state, p) {
      state.productList.push({
        id: p.id,
        name: p.name,
        price: p.price,
        image: p.image,
        design: p.design,
        imagetwo: p.imagetwo,
        imagethree: p.imagethree,
        imagefour: p.imagefour,
        size: p.size,
        desc: p.desc,
        cat: p.cat,
        status: p.status,
        editing: false
      })
      // alert('in add')
    },
    updateProd (state, p) {
      const index = state.productList.findIndex(item => item.id === p.id)
      state.productList.splice(index, 1, {
        id: p.id,
        name: p.name,
        price: p.price,
        image: p.image,
        design: p.design,
        imagetwo: p.imagetwo,
        imagethree: p.imagethree,
        imagefour: p.imagefour,
        size: p.size,
        desc: p.desc,
        cat: p.cat,
        status: p.status,
        editing: false
      })
    },
    deleteProd (state, id) {
      const index = state.productList.findIndex(item => item.id === id)

      if (index >= 0) {
        state.productList.splice(index, 1)
      }
    },
    addHome (state, p) {
      state.homeList.push({
        id: p.id,
        code: p.code,
        total: p.total,
        cat: p.cat,
        img: p.img,
        img2: p.img2,
        img3: p.img3,
        img4: p.img4,
        timestamp: p.timestamp
      })
    },
    updateHome (state, p) {
      const index = state.homeList.findIndex(item => item.id === p.id)
      state.homeList.splice(index, 1, {
        id: p.id,
        code: p.code,
        total: p.total,
        cat: p.cat,
        img: p.img,
        img2: p.img2,
        img3: p.img3,
        img4: p.img4,
        timestamp: p.timestamp
      })
    },
    deleteHome (state, id) {
      const index = state.homeList.findIndex(item => item.id === id)

      if (index >= 0) {
        state.homeList.splice(index, 1)
      }
    },
    ADD_OPTION (state, c) {
      state.options.push({ value: c.name, text: c.name })
      state.categoryListWithLink.push({ name: c.name, link: c.link })
      state.categoryList.push(c.name)
    },
    RESET_OPTION (state) {
      state.options = []
      state.categoryList = []
      state.categoryListWithLink = []
      state.options.push({ value: null, text: 'Please select the Category' })
    },
    SET_LOGGED_IN (state, value) {
      state.user.loggedIn = value
    },
    SET_USER (state, data) {
      state.user.data = data
    }
  },
  actions: {
    logoutUser ({ commit }) {
      commit('SET_LOGGED_IN', false)
      commit('SET_USER', null)
    },
    checkLogin (context) {
      // alert('ayy')
      if (this.state.user.loggedIn) {
        this.$router.replace('/Home')
      }
    },
    fetchUser ({ commit, dispatch }, user) {
      commit('SET_LOGGED_IN', user !== null)
      // alert(user.admin + 'in fetch')
      if (user) {
        // console.log('in fetch')
        // console.log(user)
        commit('SET_USER', {
          displayName: user.displayName,
          email: user.email,
          uid: user.uid,
          admin: user.admin
        })
      } else {
        commit('SET_USER', null)
      }
    },
    resetList (context) {
      context.commit('RESETLIST')
    },
    initProduct (context, cat) {
      // alert('in init')
      // console.log('init ' + cat)
      db.collection('product/category/' + cat).onSnapshot(snapshot => {
        snapshot.docChanges().forEach(change => {
          if (change.type === 'added') {
            const source = change.doc.metadata.hasPendingWrites ? 'Local' : 'Server'

            if (source === 'Server') {
              context.commit('addProd', {
                id: change.doc.id,
                name: change.doc.data().pname,
                price: change.doc.data().price,
                image: change.doc.data().image,
                design: change.doc.data().design,
                imagetwo: change.doc.data().imagetwo,
                imagethree: change.doc.data().imagethree,
                imagefour: change.doc.data().imagefour,
                size: change.doc.data().size,
                desc: change.doc.data().description,
                cat: cat,
                status: change.doc.data().status,
                timestamp: change.doc.data().timestamp
              })
            }
          }
          if (change.type === 'modified') {
            context.commit('updateProd', {
              id: change.doc.id,
              name: change.doc.data().pname,
              price: change.doc.data().price,
              image: change.doc.data().image,
              design: change.doc.data().design,
              imagetwo: change.doc.data().imagetwo,
              imagethree: change.doc.data().imagethree,
              imagefour: change.doc.data().imagefour,
              size: change.doc.data().size,
              desc: change.doc.data().description,
              cat: cat,
              status: change.doc.data().status,
              timestamp: change.doc.data().timestamp
            })
          }
          if (change.type === 'removed') {
            context.commit('deleteProd', change.doc.id)
          }
        })
      })
    },
    async uploadImage (context, payload) {
      const docRef = payload.docRef
      const p = payload.p
      const img = payload.img
      const imgNo = payload.no
      // console.log('upload image')
      // console.log(docRef)
      const ext = img.name.slice(img.name.lastIndexOf('.'))
      const ref = firebase.storage().ref('productImages/' + docRef.id + '_' + imgNo + ext).put(img)
      ref.on(firebase.storage.TaskEvent.STATE_CHANGED,
        function (snapshot) {
          // Get task progress, including the number of bytes uploaded and the total number of bytes to be uploaded
          var progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100
          alert(progress + '% uploaded')
          // console.log(progress)
        },
        function (error) {
          alert('error uploading image ' + error)
          // console.log(error.code)
        },
        function () {
          // console.log('file available')
          ref.snapshot.ref.getDownloadURL().then(function (downloadURL) {
            // console.log('File available at', downloadURL)
            if (imgNo === 1) {
              db.collection('product/category/' + p.cat).doc(docRef.id).update({
                image: downloadURL
              }).then(() => {
                alert('Image updated 1')
              })
            }
            if (imgNo === 2) {
              db.collection('product/category/' + p.cat).doc(docRef.id).update({
                imagetwo: downloadURL
              }).then(() => {
                alert('Image updated 2')
              })
            }
            if (imgNo === 3) {
              db.collection('product/category/' + p.cat).doc(docRef.id).update({
                imagethree: downloadURL
              }).then(() => {
                alert('Image updated 3')
              })
            }
            if (imgNo === 4) {
              db.collection('product/category/' + p.cat).doc(docRef.id).update({
                imagefour: downloadURL
              }).then(() => {
                alert('Image updated 4')
              })
            }
          })
        })
    },
    addSize (context, payload) {
      const p = payload.p
      const docRef = payload.docRef
      const sizes = p.size.split('/')
      // console.log(p.cat + '  ' + docRef.id)
      const ref = db.collection('product/category/' + p.cat).doc(docRef.id).collection('size')
      for (const val of sizes) {
        // console.log(val)
        ref.add({
          size: val
        }).then(() => {
          // console.log('size added')
          alert('size added')
        })
        // [END OF REF]
      }
    },
    addProduct (context, p) {
      // console.log(p.cat)

      db.collection('product/category/' + p.cat).add({
        design: p.design,
        pname: p.name,
        price: p.price,
        status: 'live',
        size: p.size.split('/'),
        description: p.desc,
        timestamp: new Date()
      }).then(function (docRef) {
        // console.log('Document written with ID: ', docRef.id)
        // const payloadimg = { docRef, p }
        // alert('product added')
        alert('uploading images')
        // context.dispatch('addSize', payloadimg)
        // uploading image one
        let img = p.img
        let no = 1
        let payload = { docRef, p, img, no }
        context.dispatch('uploadImage', payload)
        // uploading image two
        if (p.img2 !== null) {
          img = p.img2
          no = 2
          payload = { docRef, p, img, no }
          context.dispatch('uploadImage', payload)
        }
        // uploading image three
        if (p.img3 !== null) {
          img = p.img3
          no = 3
          payload = { docRef, p, img, no }
          context.dispatch('uploadImage', payload)
        }
        // uploading image four
        if (p.img4 !== null) {
          img = p.img4
          no = 4
          payload = { docRef, p, img, no }
          context.dispatch('uploadImage', payload)
        }
      })
    },
    deleteProduct (context, payload) {
      // delete products
      // db.collection('product/category/' + payload.cat).doc(payload.id).delete()
      //   .then(() => {
      //     alert('Product deleted')
      //   })
      db.collection('product/category/' + payload.cat).doc(payload.id).update({
        status: 'deleted'
      }).then(() => {
        alert('Product deleted')
      })
    },
    updateProduct (context, p) {
      // console.log('in update')
      // console.log(p.name)
      db.collection('product/category/' + p.cat).doc(p.id).update({
        pname: p.name,
        price: p.price,
        image: p.image,
        imagetwo: p.imagetwo,
        imagethree: p.imagethree,
        imagefour: p.imagefour,
        status: p.status,
        design: p.design,
        size: p.size,
        description: p.desc
      }).then(() => {
        alert('Product updated')
      })
    },
    // get category
    getCategory (context) {
      context.commit('RESET_OPTION')
      db.collection('product').doc('category').get()
        .then(doc => {
          if (!doc.exists) {
            alert('document does not exist')
            // console.log('No such document!')
          } else {
            const c = doc.data().cat
            // console.log('Document category data:', c)

            for (const val of c) {
              context.commit('ADD_OPTION', val)
            }
          }
        })
        .catch(err => {
          // console.log('Error getting document', err)
          alert('error getting category ' + err)
        })
    },
    // upload image for home content
    async uploadImageHome (context, payload) {
      const docRef = payload.docRef
      const img = payload.image
      const imgNo = payload.no
      // console.log('upload image')
      // console.log(img)
      const ext = img.name.slice(img.name.lastIndexOf('.'))
      const ref = firebase.storage().ref('home/' + docRef.id + '_' + imgNo + ext).put(img)
      ref.on(firebase.storage.TaskEvent.STATE_CHANGED,
        function (snapshot) {
          // Get task progress, including the number of bytes uploaded and the total number of bytes to be uploaded
          var progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100
          alert(progress + '% uploaded')
          // console.log(progress)
        },
        function (error) {
          alert('error image upload home ' + error)
          // console.log(error.code)
        },
        function () {
          // console.log('file available')
          ref.snapshot.ref.getDownloadURL().then(function (downloadURL) {
            // console.log('File available at', downloadURL)
            let im
            if (imgNo === 1) {
              im = { img: downloadURL }
            }
            if (imgNo === 2) {
              im = { img2: downloadURL }
            }
            if (imgNo === 3) {
              im = { img3: downloadURL }
            }
            if (imgNo === 4) {
              im = { img4: downloadURL }
            }
            db.collection('home').doc(docRef.id).update(im).then(() => {
              alert('Image updated ' + imgNo)
              // console.log('updated doc')
            })
          })
        })
    },
    // addtohome
    addToHome (context, p) {
      let data = {}
      alert(p.code)
      if (p.code === 0) {
        data = {
          code: 0,
          cat: p.cat,
          total: parseInt(p.total, 10),
          timestamp: new Date()
        }
      } else if (p.code === 1) {
        data = {
          code: 1,
          cat: p.cat,
          timestamp: new Date()
        }
      } else if (p.code === 2) {
        data = {
          code: 2,
          timestamp: new Date()
        }
      } else if (p.code === 3) {
        data = {
          code: 3,
          timestamp: new Date()
        }
      }
      db.collection('home').add(data).then(function (docRef) {
        // console.log('Document written with ID: ', docRef.id)
        let image, no
        if (p.code === 2) {
          no = 1
          image = p.img
          context.dispatch('uploadImageHome', { docRef, image, no })
          no = 2
          image = p.img2
          context.dispatch('uploadImageHome', { docRef, image, no })
          no = 3
          image = p.img3
          context.dispatch('uploadImageHome', { docRef, image, no })
          no = 4
          image = p.img4
          context.dispatch('uploadImageHome', { docRef, image, no })
        } else if (p.code === 3) {
          no = 1
          image = p.img
          context.dispatch('uploadImageHome', { docRef, image, no })
        }
      })
    },
    updateHome (context, p) {
      let data = {}
      alert(p.code)
      if (p.code === 0) {
        data = {
          code: 0,
          cat: p.cat,
          total: p.total
        }
      } else if (p.code === 1) {
        data = {
          code: 1,
          cat: p.cat
        }
      } else if (p.code === 2) {
        data = {
          code: 2
        }
      } else if (p.code === 3) {
        data = {
          code: 3
        }
      }
      const docRef = {
        id: p.docID
      }
      db.collection('home').doc(p.docID).update(data).then(function (docReff) {
        // console.log('Document written with ID: ', docRef.id)
        let image, no
        if (p.code === 2) {
          no = 1
          image = p.img
          context.dispatch('uploadImageHome', { docRef, image, no })
          no = 2
          image = p.img2
          context.dispatch('uploadImageHome', { docRef, image, no })
          no = 3
          image = p.img3
          context.dispatch('uploadImageHome', { docRef, image, no })
          no = 4
          image = p.img4
          context.dispatch('uploadImageHome', { docRef, image, no })
        } else if (p.code === 3) {
          no = 1
          image = p.img
          context.dispatch('uploadImageHome', { docRef, image, no })
        }
      })
    },
    // reset home
    resetHome (context) {
      context.commit('RESETHOMELIST')
    },
    // init home
    inithome (context) {
      // alert('in home')
      db.collection('home').orderBy('timestamp', 'asc').onSnapshot(snapshot => {
        snapshot.docChanges().forEach(change => {
          if (change.type === 'added') {
            const source = change.doc.metadata.hasPendingWrites ? 'Local' : 'Server'

            if (source === 'Server') {
              // alert('inadd')
              context.commit('addHome', {
                id: change.doc.id,
                code: change.doc.data().code,
                total: change.doc.data().total,
                cat: change.doc.data().cat,
                img: change.doc.data().img,
                img2: change.doc.data().img2,
                img3: change.doc.data().img3,
                img4: change.doc.data().img4,
                timestamp: change.doc.data().timestamp
              })
            }
          }
          if (change.type === 'modified') {
            context.commit('updateHome', {
              id: change.doc.id,
              code: change.doc.data().code,
              total: change.doc.data().total,
              cat: change.doc.data().cat,
              img: change.doc.data().img,
              img2: change.doc.data().img2,
              img3: change.doc.data().img3,
              img4: change.doc.data().img4,
              timestamp: change.doc.data().timestamp
            })
          }
          if (change.type === 'removed') {
            context.commit('deleteHome', change.doc.id)
          }
        })
      })
    },
    deleteHome (context, payload) {
      // delete products
      db.collection('home').doc(payload.id).delete()
        .then(() => {
          alert('Home Item deleted')
        })
    },
    updateCategoryList (context, c) {
      db.collection('product').doc('category').set({
        cat: c
      }).then(() => {
        // console.log(c)
        alert('Category list updated')
      })
    },
    updateCategory ({ commit, state }, c) {
      // const temp = [
      //   { name: 'next', link: 'sada' },
      //   { name: 'next', link: 'sada' },
      //   { name: 'next', link: 'sada' }
      // ]
      const img = c.image
      const ext = img.name.slice(img.name.lastIndexOf('.'))
      const ref = firebase.storage().ref('category/' + c.str + ext).put(img)
      ref.on(firebase.storage.TaskEvent.STATE_CHANGED,
        function (snapshot) {
          // Get task progress, including the number of bytes uploaded and the total number of bytes to be uploaded
          var progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100
          alert(progress + '% uploaded')
          // console.log(progress)
        },
        function (error) {
          alert('error updateCategory ' + error)
          // console.log(error.code)
        },
        function () {
          ref.snapshot.ref.getDownloadURL().then(function (downloadURL) {
            // console.log('File available at', downloadURL)
            const im = { name: c.c, link: downloadURL }
            commit('ADD_OPTION', im)
            db.collection('product').doc('category').set({
              cat: state.categoryListWithLink
            }).then(() => {
              // console.log(c)
              alert('Category list updated')
            })
          })
        })
    },
    initorder (context) {
      // alert('order')
      context.commit('RESET_ORDER_LIST')
      db.collection('user').onSnapshot(snapshot => {
        snapshot.docChanges().forEach(change => {
          if (change.type === 'added') {
            const source = change.doc.metadata.hasPendingWrites ? 'Local' : 'Server'

            if (source === 'Server') {
              context.dispatch('getOrders', {
                id: change.doc.id
              })
            }
          }
          // if (change.type === 'modified') {
          //   context.commit('getOrders', {
          //     id: change.doc.id
          //   })
          // }
          // if (change.type === 'removed') {
          //   context.commit('deleteOrders', {
          //     id: change.doc.id
          //   })
          // }
        })
      })
    },
    getOrders (context, payload) {
      // alert(payload.id)
      db.collection('user/' + payload.id + '/order').onSnapshot(snapshot => {
        snapshot.docChanges().forEach(change => {
          if (change.type === 'added') {
            const source = change.doc.metadata.hasPendingWrites ? 'Local' : 'Server'

            if (source === 'Server') {
              // console.log('user id : ' + payload.id + ' doc : ' + change.doc.id)
              context.commit('ADD_ORDER', {
                id: change.doc.id,
                uid: payload.id,
                addressID: change.doc.data().addressID,
                amount: change.doc.data().amount,
                orderID: change.doc.data().orderID,
                payment_id: change.doc.data().payment_id,
                status: change.doc.data().status,
                timestamp: change.doc.data().timestamp,
                prod: change.doc.data().prod,
                product_list: change.doc.data().product_list,
                order_status: change.doc.data().order_status
              })
              if (change.doc.data().addressID !== undefined) {
                // call get address method
                context.dispatch('getAddress', {
                  id: change.doc.id,
                  uid: payload.id,
                  aid: change.doc.data().addressID
                })
              }
              // if (change.doc.data().status === 'success') {
              //   // call to get product info from collection
              //   context.dispatch('getOrderProduct', {
              //     id: change.doc.id,
              //     uid: payload.id,
              //     aid: change.doc.data().addressID,
              //     amount: change.doc.data().amount,
              //     orderID: change.doc.data().orderID,
              //     payment_id: change.doc.data().payment_id,
              //     status: change.doc.data().status,
              //     timestamp: change.doc.data().timestamp,
              //     prod: change.doc.data().prod,
              //     order_status: change.doc.data().order_status
              //   })
              // }
            }
            if (change.type === 'modified') {
              alert('in mod')
              context.commit('UPDATE_ORDER_ONE', {
                id: change.doc.id,
                uid: payload.id,
                addressID: change.doc.data().addressID,
                amount: change.doc.data().amount,
                orderID: change.doc.data().orderID,
                payment_id: change.doc.data().payment_id,
                status: change.doc.data().status,
                timestamp: change.doc.data().timestamp,
                prod: change.doc.data().prod,
                order_status: change.doc.data().order_status
              })
            }
          }// end if
        })
      })// end of collection
    },
    getAddress (context, payload) {
      console.log('k ' + payload.aid)
      db.collection('user/' + payload.uid + '/address').doc(payload.aid).get()
        .then(doc => {
          if (doc.exists) {
            context.commit('UPDATE_ORDER', {
              id: payload.id,
              address: doc.data().address,
              name: doc.data().name,
              phone: doc.data().phone,
              city: doc.data().city,
              country: doc.data().country,
              state: doc.data().state
            })
            // console.log('Document category data:', c)
          }
        })
        .catch(err => {
          alert('error getaddress ' + err)
          // console.log('Error getting document', err)
        })
    },
    getOrderProduct (context, payload) {
      db.collection('user/' + payload.uid + '/order/' + payload.id + '/product').onSnapshot(snapshot => {
        snapshot.docChanges().forEach(change => {
          if (change.type === 'added') {
            const source = change.doc.metadata.hasPendingWrites ? 'Local' : 'Server'
            const ptemp = {
              id: change.doc.id,
              cat: change.doc.data().cat,
              qty: change.doc.data().qty,
              pid: change.doc.data().pid,
              size: change.doc.data().size
            }
            context.commit('ADD_PRODUCT_ORDER', {
              id: payload.id,
              product: ptemp
            })
            context.dispatch('getProductDetail', {
              id: payload.id,
              product: ptemp
            })
            if (source === 'Server') {
              // console.log('p doc : ' + change.doc.id)
            }
          }// end if
        })
      })
    },
    getProductDetail (context, payload) {
      db.collection('product/category/' + payload.product.cat).doc(payload.product.pid).get()
        .then(doc => {
          if (!doc.exists) {
            alert('no doc exist')
            // console.log('No such document!')
          } else {
            // console.log(doc.data().name)
            context.commit('UPDATE_PRODUCT_ORDER', {
              id: payload.id,
              docid: payload.product.id,
              pname: doc.data().pname,
              price: doc.data().price,
              image: doc.data().image
            })
            // console.log('Document category data:', c)
          }
        })
        .catch(err => {
          alert('error getting product details ' + err)
          // console.log('Error getting document', err)
        })
    },
    updateorderstatus (context, p) {
      db.collection('user/' + p.uid + '/order/').doc(p.id).update({
        order_status: p.stat
      }).then(() => {
        context.dispatch('getUpdatedOrder', p)
        alert('Status updated')
      })
    },
    getUpdatedOrder (context, payload) {
      db.collection('user/' + payload.uid + '/order').doc(payload.id).get()
        .then(doc => {
          if (!doc.exists) {
            alert('No such document!')
          } else {
            context.commit('UPDATE_ORDER_ONE', {
              id: doc.id,
              uid: payload.uid,
              addressID: doc.data().addressID,
              amount: doc.data().amount,
              orderID: doc.data().orderID,
              payment_id: doc.data().payment_id,
              status: doc.data().status,
              timestamp: doc.data().timestamp,
              prod: doc.data().prod,
              order_status: doc.data().order_status
            })
            // console.log('Document category data:', c)
          }
        })
        .catch(err => {
          alert('error ' + err)
          // console.log('Error getting document', err)
        })
    },
    addPromocodee (context, p) {
      const data = {
        name: p.name,
        discount: parseInt(p.discount),
        startDate: p.startDate,
        endDate: p.endDate,
        maxDiscount: parseInt(p.maxdiscount)
      }
      db.collection('promocode').add(data).then(function (docRef) {
        alert('Promocode added')
        // console.log('Document written with ID: ', docRef.id)
      })
    },
    addDiscountt (context, p) {
      const data = {
        name: p.name,
        cat: p.cat,
        discount: parseInt(p.discount),
        startDate: p.startDate,
        endDate: p.endDate,
        maxDiscount: parseInt(p.maxdiscount)
      }
      db.collection('discount').add(data).then(function (docRef) {
        alert('Discount added')
        // console.log('Document written with ID: ', docRef.id)
      })
    },
    initPromocode (context) {
      context.commit('RESET_PROMOCODE')
      db.collection('promocode').onSnapshot(snapshot => {
        snapshot.docChanges().forEach(change => {
          if (change.type === 'added') {
            const source = change.doc.metadata.hasPendingWrites ? 'Local' : 'Server'
            context.commit('ADD_PROMOCODE', {
              id: change.doc.id,
              discount: change.doc.data().discount,
              endDate: change.doc.data().endDate,
              maxDiscount: change.doc.data().maxDiscount,
              name: change.doc.data().name,
              startDate: change.doc.data().startDate
            })
            if (source === 'Server') {
              // console.log('p doc : ' + change.doc.id)
            }
          }
          if (change.type === 'removed') {
            context.commit('DELETE_PROMOCODE', {
              id: change.doc.id
            })
          }// end if
        })
      })
    },
    initDiscount (context) {
      context.commit('RESET_DISCOUNT')
      db.collection('discount').onSnapshot(snapshot => {
        snapshot.docChanges().forEach(change => {
          if (change.type === 'added') {
            // const source = change.doc.metadata.hasPendingWrites ? 'Local' : 'Server'
            context.commit('ADD_DISCOUNT', {
              id: change.doc.id,
              name: change.doc.data().name,
              discount: change.doc.data().discount,
              endDate: change.doc.data().endDate,
              maxDiscount: change.doc.data().maxDiscount,
              cat: change.doc.data().cat,
              startDate: change.doc.data().startDate
            })
            // if (source === 'Server') {
            //   console.log('p doc : ' + change.doc.id)
            // }
          }
          if (change.type === 'removed') {
            // alert('remove')
            context.commit('DELETE_DISCOUNT', {
              id: change.doc.id
            })
          }// end if
        })
      })
    },
    deletePromocode (context, payload) {
      db.collection('promocode').doc(payload.id).delete()
        .then(() => {
          alert('Promocode deleted')
        })
    },
    deleteDiscount (context, payload) {
      db.collection('discount').doc(payload.id).delete()
        .then(() => {
          alert('Discount deleted')
        })
    }// end of module
  },
  modules: {
  }
})
