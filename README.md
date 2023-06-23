# Notes and Reminders

REST application for save notes and send reminders on your own email

# Requests

* Create new account: 

`POST:` `/api/v1/auth/reg`

```
{
    "username": "new_user_name",
    "password": "new_password",
    "email": "your_current_email",
    "timeZone": "current_time_zone_in_GTM_format"
}
```    

After registration you must go to *your_current_email*, which you point in JSON, and verify your email. Just click on URL in letter.

* Login:

`POST:` `/api/v1/auth`

```
{
    "username": "your_current_user_name",
    "password": "your_current_password"
}
```

After this request you get personal JWT Token. For all next requests you have to be authorization, so add JWT Token to Requests Headers.
