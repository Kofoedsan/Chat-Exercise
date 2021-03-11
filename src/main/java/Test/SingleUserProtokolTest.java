package Test;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


import server.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SingleUserProtokolTest {

    Server testServer = new Server(9000);
    Thread serverThread = new Thread(testServer);

    Socket s;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    String serverResponse = "";

    @BeforeAll
    void setup() {
        serverThread.start();

    }


    @BeforeEach
    void setUp() {
        try {
            s = new Socket("localhost", 9000);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    void tearDownAll() {
        testServer.serverStop();
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void connectUserSucces() {
        try {
            try {
                dos.writeUTF("CONNECT#USER1");
                serverResponse = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            assertEquals("ONLINE#USER1, ", serverResponse);

        } finally {
//            similar to tear down
            try {
                dos.writeUTF("CLOSE#0");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    void connectUsernameFail() {

        try {
            dos.writeUTF("CONNECT#wrongInput");
            serverResponse = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals("CLOSE#2", serverResponse);
    }


    @Test
    void connectInputFail() {
        try {
            dos.writeUTF("wrongInput#wrongInput");
            serverResponse = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals("CLOSE#1", serverResponse);

//        assertEquals(true, s.isClosed());
    }

    @Test
    void closeSucces() {
        try {
            dos.writeUTF("CLOSE");
            serverResponse = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals("CLOSE#0", serverResponse);

//        assertEquals(true, s.isClosed());
    }

    @Test
    void message() {
        try {
            dos.writeUTF("CONNECT#USER1");
            String eater = dis.readUTF();
            System.out.println(eater);
            dos.writeUTF("SEND#*#messageToAll");
//
            serverResponse = dis.readUTF();
        } catch (IOException e) {
            System.out.println("communication with server failed");;
        }
        assertEquals("MESSAGE#USER1#messageToAll", serverResponse);

//        assertEquals(true, s.isClosed());
    }


}