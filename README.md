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

After this request you get personal JWT Token. For all next requests you have to be authorization, so add JWT Token to
Requests Headers.

* Create note:

`POST:` `/api/v1/notes`

```
{  
    "title": "note_name",
    "content": "note_text",
    "category": "categoria_name (optional)",
    "schedule": {
        "startAt": "2023-06-24T15:32:10",
        "isReminded": "send_email (bool, optional)",
        "repeatCount": "count_of_reapet_redinders (int, optional)",
        "repeatInterval": "interval_between_remainders_in_ms (long, optional)"
    }
}
```

Server will create note with pointed information. If you choose *"isReminded"* in *"schedule"* 
you get letter on email at *"startAt"* in *"schedule"*.

* Update note:

`PATCH:` `/api/v1/notes/{nite_id}`

Use JSON similar *Create note* request.

* Delete note:

`DELETE:` `/api/v1/notes/{nite_id}`

Note with id `note_id` will be deleted.

* Get all notes:

`GET:` `/api/v1/notes`

Will display all created notes of the user whose JWT Token was used in the request.