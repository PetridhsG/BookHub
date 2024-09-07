# BookHub
In the Config file set all the ip addresses to 127.0.0.1 (backloop)

Ιn the RoomData.json file there are the data for each room that the application will use, change them as you want Edit the settings in the Config.java file as you like and then do the following:

-To compile the project run compile.bat
-To run the server side of application run open_server_side.bat
Every worker takes an argument, which is the workerID (from 0 to numberOfWorkers - 1)
Every worker replica takes an argument, which is the workerID + numberOfWorkes. e.g Worker 0 replica is Worker 3
Edit the batch file to run with different number of workers (workers configuration must be declared in the config file)
-To run customer app run open_customer_app.bat
-To run manager app run open_manager_app.bat
