/**
 * 
 */
package taichu.kafka.Input2Kafka.common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * @author Administrator
 *
 */
public class SocketTCPServer implements IAudit, IStart, IShutdown {

	private static Logger log = Logger.getLogger("SocketTCPServer.class");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//test to start server
		 new SocketTCPServer().service();
	}
	
	
	private int port = 9923;
    private ServerSocket serverSocket;
    private ExecutorService executorService;// 线程池
    private final int POOL_SIZE = 10;// 单个CPU线程池大小

    public SocketTCPServer() {
        try {
            serverSocket = new ServerSocket(port);
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                    .availableProcessors() * POOL_SIZE);
            log.info("SocketTCPServer：服务器启动,port（" + port + "）,固定大小线程("+POOL_SIZE+")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void service() {
//        System.out.println("socket初始化成功！");
        log.info("SocketTCPServer：socket(tcp)服务端初始化成功！");
        while (true) {
            Socket socket = null;
            try {
                // 接收客户连接,只要客户进行了连接,就会触发accept();从而建立连接
                socket = serverSocket.accept();
                executorService.execute(new DataReceiver(socket));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
