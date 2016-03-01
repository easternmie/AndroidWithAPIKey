Step by step :
--------------

###1. prepare Web API 

- a). upload folder "task_manager" to your hosting

- b). import database to your SQL

- c). adjust config file in task_manager > include > Config.php with appropriate setting (DB host, DB username, DB password, DB name)

- d). run in your browser http://{SERVER_HOST}/task_manager/v1/ you will get the customize 404 error message, (mean its work well)

###2. Testing API using postman

- a). download and install "postman" to your PC

- b). import this collection via this link https://www.getpostman.com/collections/bc9a1a88c1056c2129de

- c). test all the API's start with `/register`, `/login` and `insert`/`update`/`delete` some tasks. it should work well. 

###3. Compile android studio
    
- a). open android studio

- b). import project "AndroidLoginAndRegistration" to your android studio

- c). adjust your "String URL_LOGIN", "String URL_REGISTER" and "URL_UPDATE" to your approriate hosting API URI

- d). compile your android app to phone or emulator, it should working well.
