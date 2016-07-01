/**
 * 
 */
package taichu.kafka.Input2Kafka.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * 功能说明：执行具体业务的线程
 */
public class DataReceiver implements Runnable {

	// http://www.cnblogs.com/fbsk/archive/2012/02/03/2336689.html
	Logger log = Logger.getLogger(DataReceiver.class);
	private Socket socket;
	private BufferedReader is;
	private PrintWriter os;
	
	//interface for kafka producer
	//TODO:怎么初始化？ 用sprint？还是manager class？
	private IKafkaProducer kfkp = null;
	//interface for msg transform of Vehicle traffic information 
	//TODO:怎么初始化？ 用sprint？还是manager class？
	private IDataTransform2KafkaMsg dt2km=null; 

	public DataReceiver(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream());

			String msg = is.readLine();

			System.out.println("DataReceiver：server got MSG(" + msg + ") from client(" + socket.getInetAddress() + ")");

			// 返回消息给客户端
			String responseMsg = "DataReceiver：服务端原样返回MSG(" + msg + ").";
			os.println(responseMsg);
			os.flush();
			
			//对msg消息进行转换
			String kafkaMsg=dt2km.ToKafkaMsg(msg);
			if (kafkaMsg!=null&&"".equals(kafkaMsg)){
				//对消息以producer方式发送给kafka系统
				boolean ret =kfkp.SendMsg2KafkaAsProducer(kafkaMsg);
				if (!ret) {
					log.warn("DataReceiver: Input msg to kafka failed!");
				}else {
					log.debug("DataReceiver: Input msg to kafka sucessful!");
				}
			}else {
				log.warn("DataReceiver: Got empty msg and cannot input to kafka!");
			}
				
			
			// TODO：怎么算一次tcp的消息收发结束？
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
