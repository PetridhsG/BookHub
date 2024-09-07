
cd..

start "Worker 2" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 2
start "Worker 2 Replica" cmd /k java -cp out gr.aueb.BookingApp.backend.Worker.Worker 5
start "Reducer" cmd /k java -cp out gr.aueb.BookingApp.backend.Reducer.Reducer







