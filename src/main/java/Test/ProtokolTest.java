package Test;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


import server.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProtokolTest {

    Server testServer = new Server(9000);
    Thread serverThread = new Thread(testServer);

    Socket s;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    String serverResponse = "";

    @BeforeAll
    void setup() {
        serverThread.start();


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

    }
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void connectUser() {
        try {
            dos.writeUTF("CONNECT#USER1");
            serverResponse = dis.readUTF();
        } catch (IOException e) {

            e.printStackTrace();
        }
        assertEquals("ONLINE#USER1, ", serverResponse);
    }


}