package gr.aueb.BookingApp.Config;

public class Config {

    public static final String masterHostIP = "127.0.0.1";    

    public static final int masterServerSocketPort = 4321;

    public static final String reducerHostIP = "127.0.0.1";  

    public static final int reducerServerSocketPort = 4323;

    public static final int numberOfWorkers = 3;   

    public static int[] workerServerSocketPort = new int[]{
            4324,    // worker 0
            4325,    // worker 1
            4326,    // worker 2
            4327,    // worker 3 (worker 0 replica)
            4328,    // worker 4 (worker 1 replica)
            4329,    // worker 5 (worker 2 replica)

    };

    public static String[] workerHostIP = new String[]{
            "127.0.0.1",    // worker 0                     
            "127.0.0.1",    // worker 1                     
            "127.0.0.1",    // worker 2                      
            "127.0.0.1",    // worker 3 (worker 0 replica)  
            "127.0.0.1",    // worker 4 (worker 1 replica)  
            "127.0.0.1"     // worker 5 (worker 2 replica)   

    };
    public static final String roomDataPath = "\\app\\src\\main\\java\\gr\\aueb\\BookingApp\\Config\\" ;



}
