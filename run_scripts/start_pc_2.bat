
cd..

start "Worker 1" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 1
start "Worker 1 Replica" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 4








