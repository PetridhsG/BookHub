
cd..

start "Worker 0" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 0
start "Worker 0 Replica" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 3
start "Master" cmd /k java -cp out gr.aueb.BookingApp.backend.Master.Master







