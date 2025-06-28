# Sistema de Coffee Shop BACK y FRONT

  <img width="30%" align="right" alt="Github" src="https://user-images.githubusercontent.com/48678280/88862734-4903af80-d201-11ea-968b-9c939d88a37c.gif" />

- ### Lenguaje y Tool
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kotlin/kotlin-original.svg" alt="C#" width="40" height="40"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" alt="C#" width="40" height="40"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/git/git-original.svg" alt="C#" width="40" height="40"/>

- ### Database **SQL**
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/firebase/firebase-original.svg" alt="C#" width="40" height="40"/>

--------------------------------------------------------------------------------------------------------------------------------------------------------------

### Secrets Management

> [!CAUTION]
> The app will NOT build as it is due to string resource missing. This string represents a PayPal API
> secret if you only want to test the app just create a string resource with the following ids:
> - client_id
> - secret_id

If you want to use the Paypal Integration in the app you need to create a secrets.xml inside the res
folder. There is template called secrets-template.xml that you may use if you need a guide.

Inside that file you need to set your paypal client id and secret id accordingly so the sdk can use
it.

In order to get these ids you need to go to : developer.paypal.com and create and sandboxed app
where you will see this values. Inside the app settings you need to enable the option login with
paypal and create a new return url with this name:

com.powidev.coffeshop://paypalActivity

Once done that simply rebuild the app and test the integration in sandbox.paypal.com and enter your
newly created bussiness/personal sandboxed accounts.
