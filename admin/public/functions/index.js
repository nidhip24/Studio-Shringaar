const functions = require('firebase-functions');
var rn = require('random-number');
const admin = require('firebase-admin');
const Razorpay = require('razorpay');
const nodemailer = require('nodemailer');
const cors = require('cors')({origin: true});
admin.initializeApp();

const key = '************';
const key_secret = '*********';

/**
* Here we're using Gmail to send 
*/
let transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
      user: 'nidhipkathiriya@gmail.com',
      pass: '************'
  }
});

var instance = new Razorpay({
  key_id: key,
  key_secret: key_secret
});

var gen, amountActual;
let uid, token,s;
var amount, currency, receipt, payment_capture, notes, order_id;

gen = rn.generator({
  min:  -999999999
, max:  999999999
, integer: true
});

exports.addAdminRole = functions.https.onCall((data, context) => {
  //check request is made by an admin
  if ( context.auth.token.admin !== true ) {
    return { error: 'Only admins can add other admins' }
  }

  // get user and add admin custom claim
  return admin.auth().getUserByEmail(data.email).then(user => {
    return admin.auth().setCustomUserClaims(user.uid, {
      admin: true
    })
  }).then(() => {
    return {
      message: `Success! ${data.email} has been made an admin.`
    }
  }).catch(err => {
    return err;
  });
});

exports.createOrder = functions.https.onCall( (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    return {
      error: 'auth failed'
    };
  }
  uid = context.auth.uid;
  const db = admin.firestore();

  token = 'OD' + gen();

  amountActual = parseInt(data.amt);
  amount= amountActual;
  currency='INR';
  receipt = token;
  payment_capture =true;
  notes ="Payment";
  order_id;

  let tp = data.prodList;

  s= "nop";
  return instance.orders.create({amount, currency, receipt, payment_capture, notes}).then((response) => {
    order_id=response.id;
    return db.collection('user/'+uid+'/order').add({
      orderID: order_id,
      status: 'pending',
      order_status: 'pending',
      prod: tp,
      amount: amountActual,
      timestamp: new Date()
    }).then(ref => {
      // console.log('Added document with ID: ', ref.id);
      console.log(response);
      response.key = key;
      response.docRefID = ref.id;
      response.prod = data.prodList;
      return response;
    }).catch(err => {
      console.log(err)
      return {
        error: 'could not write document =>'+ err
      };
    });
  }).catch((error) => {
    console.log(error);
    return {
      error: 'order cannot be created =>'+error
    };
  });
});

exports.sendMail = functions.https.onCall( (data, context) => {
    
    // getting dest email by query string
    console.log(data)
    var dest = data.email
    const plist = data.product_list

    const cus_name = data.cus_name
    const cus_addr = data.cus_addr

    const GTotal = data.GTotal
    const totalDiscount = data.totalDiscount
    const allPrice = data.allPrice

    const shippingFee = data.shippingFee
    const shippingFeeDiscount = -(parseInt(data.shippingFeeDiscount))

    var p = ''
    
    plist.forEach(function(value){
      p+=`
      <tr style="border-collapse:collapse;"> 
        <td style="Margin:0;padding-bottom:5px;padding-left:20px;padding-right:20px;padding-top:25px;background-color:#F8F8F8;" bgcolor="#f8f8f8" align="left"> 
          <!--[if mso]><table width="560" cellpadding="0" cellspacing="0"><tr><td width="270" valign="top"><![endif]--> 
          <table class="es-left" cellspacing="0" cellpadding="0" align="left" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left;"> 
            <tr style="border-collapse:collapse;"> 
            <td class="es-m-p20b" width="270" align="left" style="padding:0;Margin:0;"> 
              <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                <tr style="border-collapse:collapse;"> 
                <td align="center" style="padding:0;Margin:0;font-size:0;"><img class="adapt-img" src="`+ value.img +`" alt width="103" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;"></td> 
                </tr> 
              </table></td> 
            </tr> 
          </table> 
          <!--[if mso]></td><td width="20"></td><td width="270" valign="top"><![endif]--> 
          <table class="es-right" cellspacing="0" cellpadding="0" align="right" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right;"> 
            <tr style="border-collapse:collapse;"> 
            <td width="270" align="left" style="padding:0;Margin:0;"> 
              <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                <tr style="border-collapse:collapse;"> 
                <td align="left" style="padding:0;Margin:0;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#333333;"><span style="font-size:16px;"><strong style="line-height:150%;">`+ value.name +`</strong></span></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#333333;">Category: `+ value.category +`</p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#333333;">Size: `+ value.size +`</p></td> 
                </tr> 
                <tr style="border-collapse:collapse;"> 
                <td align="left" style="padding:0;Margin:0;padding-top:20px;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#333333;"><span style="font-size:15px;"><strong style="line-height:150%;">Item Price:</strong> `+ value.price +`</span></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#333333;"><span style="font-size:15px;"><strong>Qty: </strong>`+ value.qty +`</span></p></td> 
                </tr> 
              </table></td> 
            </tr> 
          </table> 
          <!--[if mso]></td></tr></table><![endif]--></td> 
        </tr> 
      `
    })

    const mailOptions = {
        from: 'Studio Shringaar <studioshringaar@gmail.com>',
        to: dest,
        subject: 'Order Confirmation - Studio Shringaar Team', // email subject
        html: `
        <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html style="width:100%;font-family:Arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0;">
 <head> 
  <meta charset="UTF-8"> 
  <meta content="width=device-width, initial-scale=1" name="viewport"> 
  <meta name="x-apple-disable-message-reformatting"> 
  <meta http-equiv="X-UA-Compatible" content="IE=edge"> 
  <meta content="telephone=no" name="format-detection"> 
  <title>Order Confirmation Mail</title> 
  <!--[if (mso 16)]>
    <style type="text/css">
    a {text-decoration: none;}
    </style>
    <![endif]--> 
  <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> 
  <style type="text/css">

@media only screen and (max-width:600px) {p, ul li, ol li, a { font-size:16px!important; line-height:150%!important } h1 { font-size:30px!important; text-align:center; line-height:120%!important } h2 { font-size:26px!important; text-align:center; line-height:120%!important } h3 { font-size:20px!important; text-align:center; line-height:120%!important } h1 a { font-size:30px!important } h2 a { font-size:26px!important } h3 a { font-size:20px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:16px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:16px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class="gmail-fix"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } a.es-button { font-size:20px!important; display:block!important; border-width:10px 20px 10px 20px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } .es-desk-hidden { display:table-row!important; width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } .es-desk-menu-hidden { display:table-cell!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } }
#outlook a {
	padding:0;
}
.ExternalClass {
	width:100%;
}
.ExternalClass,
.ExternalClass p,
.ExternalClass span,
.ExternalClass font,
.ExternalClass td,
.ExternalClass div {
	line-height:100%;
}
.es-button {
	mso-style-priority:100!important;
	text-decoration:none!important;
}
a[x-apple-data-detectors] {
	color:inherit!important;
	text-decoration:none!important;
	font-size:inherit!important;
	font-family:inherit!important;
	font-weight:inherit!important;
	line-height:inherit!important;
}
.es-desk-hidden {
	display:none;
	float:left;
	overflow:hidden;
	width:0;
	max-height:0;
	line-height:0;
	mso-hide:all;
}
</style> 
 </head> 
 <body style="width:100%;font-family:Arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0;"> 
  <div class="es-wrapper-color" style="background-color:#555555;"> 
   <!--[if gte mso 9]>
			<v:background xmlns:v="urn:schemas-microsoft-com:vml" fill="t">
				<v:fill type="tile" color="#555555"></v:fill>
			</v:background>
		<![endif]--> 
   <table class="es-wrapper" width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;"> 
     <tr style="border-collapse:collapse;"> 
      <td valign="top" style="padding:0;Margin:0;">       
       <table class="es-content" cellspacing="0" cellpadding="0" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;"> 
         <tr style="border-collapse:collapse;"> 
          <td align="center" style="padding:0;Margin:0;"> 
           <table class="es-content-body" width="600" cellspacing="0" cellpadding="0" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#F8F8F8;"> 
             <tr style="border-collapse:collapse;"> 
              <td style="Margin:0;padding-left:10px;padding-right:10px;padding-top:20px;padding-bottom:20px;background-color:#191919;" bgcolor="#191919" align="left"> 
               <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                 <tr style="border-collapse:collapse;"> 
                  <td width="580" valign="top" align="center" style="padding:0;Margin:0;"> 
                   <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;font-size:0;"><a target="_blank" href="https://sssss" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:14px;text-decoration:none;color:#3CA7F1;"><img class="adapt-img" src="https://firebasestorage.googleapis.com/v0/b/studioshringaar-nk.appspot.com/o/logo%2Flogo.png?alt=media&token=96425688-b57b-4da1-b1eb-42b9b159bc47" alt width="105" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;"></a></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table></td> 
             </tr> 
             <tr style="border-collapse:collapse;"> 
              <td style="Margin:0;padding-top:20px;padding-bottom:20px;padding-left:20px;padding-right:20px;background-color:#FFCC99;" bgcolor="#ffcc99" align="left"> 
               <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                 <tr style="border-collapse:collapse;"> 
                  <td width="560" valign="top" align="center" style="padding:0;Margin:0;"> 
                   <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;padding-top:15px;padding-bottom:15px;"> 
                       <div> 
                        <h2 style="Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:24px;font-style:normal;font-weight:normal;color:#242424;"><span style="font-size:30px;"><strong>Your order is confirmed. </strong></span><br></h2> 
                       </div></td> 
                     </tr> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;padding-left:10px;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#242424;">Hi , we've received order no `+ data.oid +` and are working on it now.<br></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#242424;">We'll email you an update when we've shipped it.<br></p></td> 
                     </tr>
                   </table></td> 
                 </tr> 
               </table></td> 
             </tr> 
             <tr style="border-collapse:collapse;"> 
              <td style="Margin:0;padding-bottom:10px;padding-left:10px;padding-right:10px;padding-top:15px;background-color:#F8F8F8;" bgcolor="#f8f8f8" align="left"> 
               <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                 <tr style="border-collapse:collapse;"> 
                  <td width="580" valign="top" align="center" style="padding:0;Margin:0;"> 
                   <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;"><h2 style="Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:24px;font-style:normal;font-weight:normal;color:#191919;">Items ordered<br></h2></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table></td> 
             </tr> 
             ` + p + `
             <tr style="border-collapse:collapse;"> 
              <td style="Margin:0;padding-top:10px;padding-bottom:10px;padding-left:10px;padding-right:10px;background-color:#F8F8F8;" bgcolor="#f8f8f8" align="left"> 
               <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                 <tr style="border-collapse:collapse;"> 
                  <td width="580" valign="top" align="center" style="padding:0;Margin:0;"> 
                   <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                     <tr style="border-collapse:collapse;"> 
                      <td bgcolor="#f8f8f8" align="center" style="Margin:0;padding-left:10px;padding-right:10px;padding-top:20px;padding-bottom:20px;font-size:0;"> 
                       <table width="100%" height="100%" cellspacing="0" cellpadding="0" border="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                         <tr style="border-collapse:collapse;"> 
                          <td style="padding:0;Margin:0px;border-bottom:1px solid #191919;height:1px;width:100%;margin:0px;"></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;padding-bottom:15px;"> 
                       <table class="cke_show_border" width="240" height="101" cellspacing="1" cellpadding="1" border="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                         <tr style="border-collapse:collapse;"> 
                          <td style="padding:0;Margin:0;"><strong>Subtotal:</strong></td> 
                          <td style="padding:0;Margin:0;text-align:right;">$` + GTotal + `</td> 
                         </tr>
                         <tr style="border-collapse:collapse;"> 
                          <td style="padding:0;Margin:0;"><strong>Discount:</strong></td> 
                          <td style="padding:0;Margin:0;text-align:right;">$` + totalDiscount + `</td> 
                         </tr>  
                         <tr style="border-collapse:collapse;"> 
                          <td style="padding:0;Margin:0;"><strong>Shipping:</strong></td> 
                          <td style="padding:0;Margin:0;text-align:right;">$` + shippingFee + `</td> 
                         </tr> 
                         <tr style="border-collapse:collapse;"> 
                          <td style="padding:0;Margin:0;"><strong>Shipping Discount:</strong></td> 
                          <td style="padding:0;Margin:0;text-align:right;">$` + shippingFeeDiscount + `</td> 
                         </tr>
                         <tr style="border-collapse:collapse;"> 
                          <td style="padding:0;Margin:0;"><span style="font-size:18px;line-height:36px;"><strong>Order Total:</strong></span></td> 
                          <td style="padding:0;Margin:0;text-align:right;"><span style="font-size:18px;line-height:36px;"><strong>` + allPrice + `</strong></span></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table></td> 
             </tr> 
             <tr style="border-collapse:collapse;"> 
              <td style="Margin:0;padding-bottom:10px;padding-left:10px;padding-right:10px;padding-top:15px;background-color:#EEEEEE;" bgcolor="#eeeeee" align="left"> 
               <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                 <tr style="border-collapse:collapse;"> 
                  <td width="580" valign="top" align="center" style="padding:0;Margin:0;"> 
                   <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;"><h2 style="Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:24px;font-style:normal;font-weight:normal;color:#191919;">Order &amp; shipping info</h2></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table></td> 
             </tr> 
             <tr style="border-collapse:collapse;"> 
              <td style="Margin:0;padding-top:10px;padding-left:20px;padding-right:20px;padding-bottom:30px;background-color:#EEEEEE;" bgcolor="#eeeeee" align="left"> 
               <!--[if mso]><table width="560" cellpadding="0" cellspacing="0"><tr><td width="270" valign="top"><![endif]--> 
               <table class="es-left" cellspacing="0" cellpadding="0" align="left" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left;"> 
                 <tr style="border-collapse:collapse;"> 
                  <td class="es-m-p20b" width="270" align="left" style="padding:0;Margin:0;"> 
                   <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="left" style="padding:0;Margin:0;padding-top:10px;padding-bottom:10px;"><h3 style="Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:20px;font-style:normal;font-weight:bold;color:#242424;">Order details</h3></td> 
                     </tr> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="left" style="padding:0;Margin:0;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#333333;"><strong>Order №:</strong>`+ data.oid +`</p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#333333;"><strong>Shipping Method:</strong> Standard</p></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table> 
               <!--[if mso]></td><td width="20"></td><td width="270" valign="top"><![endif]--> 
               <table class="es-right" cellspacing="0" cellpadding="0" align="right" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right;"> 
                 <tr style="border-collapse:collapse;"> 
                  <td width="270" align="left" style="padding:0;Margin:0;"> 
                   <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="left" style="padding:0;Margin:0;padding-top:10px;padding-bottom:10px;"><h3 style="Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:20px;font-style:normal;font-weight:bold;color:#242424;">Shipping Address</h3></td> 
                     </tr> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="left" style="padding:0;Margin:0;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#333333;">` + cus_name + `<strong></strong></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#333333;">` + cus_addr + `</p></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table> 
               <!--[if mso]></td></tr></table><![endif]--></td> 
             </tr> 
             <tr style="border-collapse:collapse;"> 
              <td style="Margin:0;padding-left:20px;padding-right:20px;padding-top:25px;padding-bottom:30px;background-color:#F8F8F8;" bgcolor="#f8f8f8" align="left"> 
               <!--[if mso]><table width="560" cellpadding="0" cellspacing="0"><tr><td width="270" valign="top"><![endif]--> 
               <table class="es-left" cellspacing="0" cellpadding="0" align="left" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left;"> 
                 <tr style="border-collapse:collapse;"> 
                  <td class="es-m-p20b" width="270" align="left" style="padding:0;Margin:0;"> 
                   <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;padding-bottom:10px;"><h3 style="Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:20px;font-style:normal;font-weight:bold;color:#242424;">We're here to help</h3></td> 
                     </tr> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#242424;">Call <a target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:14px;text-decoration:none;color:#3CA7F1;line-height:21px;" href="tel:123456789">123456789</a> or <a target="_blank" href="https://viewstripo.email/" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:14px;text-decoration:none;color:#3CA7F1;">visit us online</a></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#242424;">for expert assistance.</p></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table> 
               <!--[if mso]></td><td width="20"></td><td width="270" valign="top"><![endif]--> 
               <table class="es-right" cellspacing="0" cellpadding="0" align="right" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right;"> 
                 <tr style="border-collapse:collapse;"> 
                  <td width="270" align="left" style="padding:0;Margin:0;"> 
                   <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;padding-bottom:10px;"><h3 style="Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:20px;font-style:normal;font-weight:bold;color:#242424;">Our guarantee</h3></td> 
                     </tr> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#242424;">Your satisfaction is 100% guaranteed.</p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:Arial, sans-serif;line-height:21px;color:#242424;">See our <a target="_blank" href="https://viewstripo.email/" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:14px;text-decoration:none;color:#3CA7F1;">Returns and Exchanges policy.</a></p></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table> 
               <!--[if mso]></td></tr></table><![endif]--></td> 
             </tr> 
           </table></td> 
         </tr> 
       </table> 
       <table cellpadding="0" cellspacing="0" class="es-footer" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top;"> 
         <tr style="border-collapse:collapse;"> 
          <td align="center" style="padding:0;Margin:0;"> 
           <table class="es-footer-body" width="600" cellspacing="0" cellpadding="0" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#242424;"> 
             <tr style="border-collapse:collapse;"> 
              <td align="left" style="padding:20px;Margin:0;"> 
               <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                 <tr style="border-collapse:collapse;"> 
                  <td width="560" valign="top" align="center" style="padding:0;Margin:0;"> 
                   <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;padding-top:10px;padding-bottom:20px;font-size:0;"> 
                       <table class="es-table-not-adapt es-social" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;"> 
                         <tr style="border-collapse:collapse;"> 
                          <td valign="top" align="center" style="padding:0;Margin:0;padding-right:15px;"><img title="Twitter" src="images/twitter-circle-gray.png" alt="Tw" width="32" height="32" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;"></td> 
                          <td valign="top" align="center" style="padding:0;Margin:0;padding-right:15px;"><img title="Facebook" src="images/facebook-circle-gray.png" alt="Fb" width="32" height="32" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;"></td> 
                          <td valign="top" align="center" style="padding:0;Margin:0;padding-right:15px;"><img title="Youtube" src="images/youtube-circle-gray.png" alt="Yt" width="32" height="32" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;"></td> 
                          <td valign="top" align="center" style="padding:0;Margin:0;"><img title="Linkedin" src="images/linkedin-circle-gray.png" alt="In" width="32" height="32" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;"></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:13px;font-family:Arial, sans-serif;line-height:20px;color:#888888;"><strong><a target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:13px;text-decoration:none;color:#AAAAAA;line-height:20px;" href="https://viewstripo.email">Browse all products</a>&nbsp;</strong>•<strong><a target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:13px;text-decoration:none;color:#AAAAAA;line-height:20px;" href="https://viewstripo.email">Locate store</a></strong></p></td> 
                     </tr> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;padding-top:20px;padding-bottom:20px;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:13px;font-family:Arial, sans-serif;line-height:16px;color:#888888;">Electro, Inc.</p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:13px;font-family:Arial, sans-serif;line-height:16px;color:#888888;">62 N. Gilbert, CA 99999</p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:13px;font-family:Arial, sans-serif;line-height:16px;color:#888888;"><a target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:13px;text-decoration:none;color:#AAAAAA;line-height:16px;" href="tel:123456789">123456789</a></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:13px;font-family:Arial, sans-serif;line-height:16px;color:#888888;"><a target="_blank" href="mailto:your@mail.com" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:13px;text-decoration:none;color:#AAAAAA;line-height:16px;">your@mail.com</a></p></td> 
                     </tr> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:13px;font-family:Arial, sans-serif;line-height:20px;color:#888888;"><strong><a target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:13px;text-decoration:none;color:#AAAAAA;line-height:20px;" class="unsubscribe" href="">Unsubscribe</a> • <a target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:13px;text-decoration:none;color:#AAAAAA;line-height:20px;" href="https://viewstripo.email">Update Preferences</a> • <a target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Arial, sans-serif;font-size:13px;text-decoration:none;color:#AAAAAA;line-height:20px;" href="https://viewstripo.email">Customer Support</a></strong></p></td> 
                     </tr> 
                     <tr style="border-collapse:collapse;"> 
                      <td align="center" style="padding:0;Margin:0;padding-top:10px;padding-bottom:10px;"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:13px;font-family:Arial, sans-serif;line-height:20px;color:#888888;"><em><span style="font-size:11px;line-height:17px;">You are receiving this email because you have visited our site or asked us about regular newsletter</span></em></p></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table></td> 
             </tr> 
           </table></td> 
         </tr> 
       </table> 
       </td> 
     </tr> 
   </table> 
  </div>  
 </body>
</html>
        ` // email content in HTML
    };

    // returning result
    return transporter.sendMail(mailOptions, (erro, info) => {
        if(erro){            
          return {
            error: true,
            message : erro.toString()
          }
        }          
        return {
          error: false,
          message: 'sent'
        }
    });
});