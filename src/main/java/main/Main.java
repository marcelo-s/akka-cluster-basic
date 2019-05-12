package main;

public class Main {

    public static void main(String[] args) {

        switch (args.length) {
            case 0:
                autoStart();
                break;
            case 1:
                System.out.println("Not enough arguments. It should be: frontend 2550");
                break;
            case 2: {
                String role = args[0];
                String port = args[1];
                initNode(role, port);
            }
            break;
            default:
                System.out.println("Wrong format. It should be: frontend 2550");
        }
    }

    private static void initNode(String role, String port) {
        switch (role) {
            case "frontend":
                FrontendMain.main(new String[]{port});
                break;
            case "backend":
                Backendmain.main(new String[]{port});
                break;
            default:
                System.out.println("ERROR PARSING INPUT");
        }
    }

    private static void autoStart() {
        Backendmain.main(new String[]{"2550"});
        Backendmain.main(new String[]{"2560"});
        Backendmain.main(new String[]{"2570"});
        FrontendMain.main(new String[]{"2580"});
        FrontendMain.main(new String[]{"2590"});
    }
}
