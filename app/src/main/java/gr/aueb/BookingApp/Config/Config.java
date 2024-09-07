package gr.aueb.BookingApp.Config;


// PC 1 : Master kai Worker 0 / Worker 0 replica    Menelaos
// PC 2 : Worker 1 / Worker 1 replica               Dimitris
// PC 3 : Reducer kai Worker 2 / Worker 2 replica   Sifis
// Oi clients se opoidhpote pc


public class Config {

    public static final String masterHostIP = "10.26.26.228";    // PC 1

    public static final int masterServerSocketPort = 4321;

    public static final String reducerHostIP = "10.26.19.39";   // PC 3

    public static final int reducerServerSocketPort = 4323;

    public static final int numberOfWorkers = 3;    // without the replicas

    public static int[] workerServerSocketPort = new int[]{
            4324,    // worker 0
            4325,    // worker 1
            4326,    // worker 2
            4327,    // worker 3 (worker 0 replica)
            4328,    // worker 4 (worker 1 replica)
            4329,    // worker 5 (worker 2 replica)

    };

    public static String[] workerHostIP = new String[]{
            "10.26.26.228",    // worker 0                      PC 1
            "10.26.52.70",    // worker 1                      PC 2
            "10.26.19.39",    // worker 2                      PC 3
            "10.26.26.228",    // worker 3 (worker 0 replica)   PC 1
            "10.26.52.70",    // worker 4 (worker 1 replica)   PC 2
            "10.26.19.39"     // worker 5 (worker 2 replica)   PC 3

    };
    public static final String roomDataPath = "\\app\\src\\main\\java\\gr\\aueb\\BookingApp\\Config\\" ;



}
