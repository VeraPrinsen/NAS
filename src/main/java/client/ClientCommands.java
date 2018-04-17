package client;

import fileoperators.FileReaderClass;
import fileoperators.FileChooserClass;
import host.Host;
import general.Protocol;
import outgoingpacketcontrol.OutgoingData;
import outgoingpacketcontrol.SendingTask;

import java.io.File;
import java.net.InetAddress;

// TODO: Implement tasks Client input
public class ClientCommands {

    public static void download(Client client) {
        // OTHER IDEA:
        // ASK SERVER FOR FILELIST
        // CHOOSE FILE FROM LIST
        // RECEIVE PACKETS AND SAVE FILE

        String fileName = client.readString("What file do you want to download?");
        byte[] dataBytes = fileName.getBytes();
        client.addExpectedDownloads(fileName);
        int taskNo = client.getSendingWindow().getNewTask();
        OutgoingData outgoingData = new OutgoingData(Protocol.DOWNLOAD, taskNo, client.getServerIP(), client.getServerPort(), dataBytes, Protocol.WS);
        client.getSendingWindow().addTask(taskNo);
        new Thread(new SendingTask(client, outgoingData)).start();
    }

    public static void upload(Client client) {
        // REQUEST IF YOU CAN UPLOAD A SPECIFIC FILE
        // IF APPROVED, SEND ALL DATA OF THE FILE

        // Choose file from window explorer
        FileChooserClass chooser = new FileChooserClass();
        try {
            chooser.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = chooser.getFile();

        // Save full filename in expected uploads
        String fullFileName = file.getAbsolutePath();
        String fileName = file.getName();
        byte[] data = FileReaderClass.fileToByteArray(fullFileName);
        int fileSize = data.length;
        client.addExpectedUploads(fullFileName);

        // and send filename to server
        String dataSentence = fileName + Protocol.DELIMITER + fileSize;
        byte[] dataBytes = dataSentence.getBytes();
        int taskNo = client.getSendingWindow().getNewTask();

        OutgoingData outgoingData = new OutgoingData(Protocol.UPLOAD, taskNo, client.getServerIP(), client.getServerPort(), dataBytes, Protocol.WS);
        client.getSendingWindow().addTask(taskNo);
        new Thread(new SendingTask(client, outgoingData)).start();
    }

    public static void fileList(Host host) {
        // ASK FOR FILELIST
        // RECEIVE PACKETS AND SHOW FILELIST

        String command = Protocol.REQUEST_FILELIST;
        InetAddress destinationIP = Protocol.getDefaultIp();
        int destinationPort = Protocol.DEFAULT_SERVER_PORT;
        int taskNo = host.getSendingWindow().getNewTask();
        byte[] data = new byte[1];
        data[0] = 0;

        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(taskNo);
        new Thread(new SendingTask(host, outgoingData)).start();
    }

    public static void testSmallData(Host host) {
        String command = Protocol.SENDDATA;
        InetAddress destinationIP = Protocol.getDefaultIp();
        int destinationPort = Protocol.DEFAULT_SERVER_PORT;
        int taskNo = host.getSendingWindow().getNewTask();
        byte[] data = "Dit is een stuk data".getBytes();

        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(taskNo);
        new Thread(new SendingTask(host, outgoingData)).start();
    }

    public static void testLargeData(Host host) {
        String command = Protocol.SENDDATA;
        InetAddress destinationIP = Protocol.getDefaultIp();
        int destinationPort = Protocol.DEFAULT_SERVER_PORT;
        int taskNo = host.getSendingWindow().getNewTask();
        String sentence = "Vanaf het moment dat Harry Potter achter gelaten wordt op de deurmat bij zijn oom en tante, zonder te weten dat hij beroemd was, wordt er een grote tijdsprong gemaakt. Van de eenjarige die net uit het puin van zijn ouderlijk huis is gered gaan we naar een tienjarige Harry Potter die op hardhandige wijze gewekt wordt door zijn tante.\n" +
                "\n" +
                "In het huis van de Duffelingen lijkt niet te zijn veranderd, het enige waaraan te zien is dat er tijd is verstreken zijn de rijen foto’s op de schoorsteenmantel.\n" +
                "Tien jaar geleden hadden er foto’s gestaan van iets wat verdacht veel op een roze strandbal, met verschillende kleuren gebreide mutsjes op, had geleden. Nu stonden er foto’s van een enorm dik, blond jongentje dat op zijn eerste fiets reed, in een draaimolen zat, een computer spelletje speelde met zijn vader en geknuffeld en gekust werd door zijn moeder. Dirk Duffeling was overduidelijk geen baby meer.";
        byte[] data = sentence.getBytes();
        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(taskNo);
        new Thread(new SendingTask(host, outgoingData)).start();
    }

    public static void textExtraLargeData(Host host) {
        // testExtraLargeData
        String command = Protocol.SENDDATA;
        InetAddress destinationIP = Protocol.getDefaultIp();
        int destinationPort = Protocol.DEFAULT_SERVER_PORT;
        int taskNo = host.getSendingWindow().getNewTask();
        String sentence = "Vanaf het moment dat Harry Potter achter gelaten wordt op de deurmat bij zijn oom en tante, zonder te weten dat hij beroemd was, wordt er een grote tijdsprong gemaakt. Van de eenjarige die net uit het puin van zijn ouderlijk huis is gered gaan we naar een tienjarige Harry Potter die op hardhandige wijze gewekt wordt door zijn tante.\n" +
                "\n" +
                "In het huis van de Duffelingen lijkt niet te zijn veranderd, het enige waaraan te zien is dat er tijd is verstreken zijn de rijen foto’s op de schoorsteenmantel.\n" +
                "Tien jaar geleden hadden er foto’s gestaan van iets wat verdacht veel op een roze strandbal, met verschillende kleuren gebreide mutsjes op, had geleden. Nu stonden er foto’s van een enorm dik, blond jongentje dat op zijn eerste fiets reed, in een draaimolen zat, een computer spelletje speelde met zijn vader en geknuffeld en gekust werd door zijn moeder. Dirk Duffeling was overduidelijk geen baby meer.\n" +
                "\n" +
                "Niets weer er ook maar op dat er nog een andere jongen in het huis woonde, maar toch was Harry Potter er nog steeds. De Duffelingen leken echter het liefst te doen alsof dit niet zo was en het zou ook niet meer dan de waarheid zijn wanneer iemand zou zeggen dat ze absoluut een hekel aan hun neefje hadden.\n" +
                "\n" +
                "Harry Potter sliep in een bezemkast onder de trap, die hij deelde met een hoop spinnen. Hij moest Dirk zijn oude kleding dragen en zijn verjaardag ging elkaar jaar stilzwijgend voorbij terwijl die van Dirk uitbundig gevierd werd. Natuurlijk mocht Harry nooit mee wanneer de Duffelingen iets leuks gingen doen, hij werd dan ook elk jaar bij Mevrouw Vaals gedumpt. Het was een oude gestoorde vrouw die twee straten verderop woonde, Harry vond het er werkelijk verschrikkelijk. Het hele huis stonk naar bloemkool en Mevrouw Vaals stond erop dat Harry fotoalbums bekeek met alle katten die ze ooit gehad had.\n" +
                "\n" +
                "Toch is er iets wat altijd anders is geweest aan Harry en dan bedoel ik niet zijn bril, die met plakband bij elkaar word gehouden of zijn veel te grote kleding die van Dirk zijn geweest. Nee, Harry Potter kwam soms in vreemde situaties terecht die hij zelf niet kon verklaren. Zijn oom en tante werden er altijd verschrikkelijk boos om en vaak werd Harry dan ook weken achtereen in zijn bezemkast opgesloten, maar hij kon er echt niets aan doen.\n" +
                "\n" +
                "Een grote frustratie van de Duffelingen was Harry’s haar, het groeide echt alle kanten op. Tante Petunia vond echter dat het tijd was dat het eraf ging en aangezien hij te vaak naar de kapper ging zou ze het zelf doen. Met een gewone keukenschaar heeft ze toen al Harry zijn haren eraf gehaald en enkel zijn pony laten zitten om ‘dat lelijke litteken’ te verbergen. Nog nooit had Harry zich zo druk gemaakt over school, hij werd al gepest en genegeerd door de rest van de klas. De volgende ochtend was zijn haar echter helemaal weer terug gegroeid en leek het alsof tante Petunia er nooit een schaar in had gezet!\n" +
                "\n" +
                "Als dat het enige voorval zou zijn geweest zou het misschien niet zo opvallen, maar er waren nog meer dingen gebeurd wat Harry toch niet normaal leek. Zo probeerde tante Petunia een keer een verschrikkelijk lelijke trui van Dirk (bruin met oranje stippen) over Harry’s hoofd te krijgen, maar deze bleef net zo lang krimpen tot hij alleen geschikt zou zijn geweest voor een handpop. Wanneer zijn neef, Dirk, achter hem aan zit om hem weer een keer in elkaar te slagen staat Harry plots op het dat van de schoolkeukens en op Dirk’s elfde verjaardag verdwijnt het glas van de boa constrictor om mysterieuze wijze.\n" +
                "\n" +
                "Het zijn een paar gebeurtenissen die Harry’s leven lijken te onderstrepen, maar naast die gekke dingen heeft hij ook dromen die haast uit een andere wereld lijken te komen. Hij droomt namelijk over vliegende motorfietsen en een man die te groot voor deze wereld lijkt. Een groene flits in een kamer en een hoge kille lach die hem kippenvel bezorgd.\n" +
                "Waarom maakte Harry van die vreemde dingen mee? En waarom droomde hij er ook over?\n" +
                "\n";

        sentence = sentence.concat(sentence);
        byte[] data = sentence.getBytes();
        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(taskNo);
        new Thread(new SendingTask(host, outgoingData)).start();
    }

    public static void testFileData(Host host) {
        FileChooserClass chooser = new FileChooserClass();
        try {
            chooser.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = chooser.getFile();
        byte[] data = FileReaderClass.fileToByteArray(file.getAbsolutePath());

        String command = Protocol.SENDDATA;
        InetAddress destinationIP = Protocol.getDefaultIp();
        int destinationPort = Protocol.DEFAULT_SERVER_PORT;
        int taskNo = host.getSendingWindow().getNewTask();

        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(taskNo);
        new Thread(new SendingTask(host, outgoingData)).start();
    }

}
