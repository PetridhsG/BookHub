
cd..

start "Reducer" cmd /k java -cp out gr.aueb.BookingApp.backend.Reducer.Reducer 
start "Worker 0" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 0
start "Worker 1" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 1
start "Worker 2" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 2
start "Worker 0 Replica" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 3
start "Worker 1 Replica" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 4
start "Worker 2 Replica" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 5
start "Master" cmd /k java -cp out gr.aueb.BookingApp.backend.Master.Master





