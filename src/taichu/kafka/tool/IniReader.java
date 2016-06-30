/**
 * 
 */
package taichu.kafka.tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author taichu
 */
public class IniReader {

	protected HashMap<String, Properties> sections = new HashMap<String, Properties>();
	// transient表示此字段不参加对象的序列化，可能是以为它是中间值！
	private transient String currentSecion;
	private transient Properties current;
	private BufferedReader reader = null;

	public IniReader(String iniFilename) {
		try {
			reader = new BufferedReader(new FileReader(iniFilename));
			read(reader);
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Doesn't find Ini file(" + iniFilename + ")!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Read Ini file(" + iniFilename + ") error!");
		} finally {
			reader = null;
		}

	}

	protected void read(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			parseLine(line);
		}
	}

	protected void parseLine(String line) {
		line = line.trim();
		if (line.matches("\\[.*\\]")) {
			// 如果是 JDK 1.4(不含1.4)以下版本，修改为
			// if (line.startsWith("[") && line.endsWith("]")) {
			if (current != null) {
				sections.put(currentSecion, current);
			}
			currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
			// JDK 低于 1.4 时
			// currentSection = line.substring(1， line.length() - 1);
			current = new Properties();
		} else if (line.matches(".*=.*")) {
			// JDK 低于 1.4 时
			// } else if (line.indexOf(’=’) >= 0) {
			int i = line.indexOf('=');
			String name = line.substring(0, i);
			String value = line.substring(i + 1);
			current.setProperty(name, value);
		}
	}

	public String getValue(String section, String name) {
		Properties p = (Properties) sections.get(section);
		if (p == null) {
			return null;
		}
		String value = p.getProperty(name);
		return value;
	}

	public static void main(String[] args) throws IOException {
		String IniFilename = "D:\\RemoteSource\\git.oschina.net\\MyKafka\\src\\taichu\\kafka\\test\\MyKafkaDemo.ini";
		IniReader reader = new IniReader(IniFilename);
		System.out.println(reader.getValue("StartServer", "cmd.start.zookeepter"));
		System.out.println(reader.getValue("StartServer", "cmd.start.kafka"));
		// System.out.println(reader.getValue("section1","key1"));

		// get zookeeper server parameters
		String value = reader.getValue("StartServer", "cmd.start.zookeepter");
		System.out.println(value);
		// get kafka server parameters
		value = reader.getValue("StartServer", "cmd.start.kafka");
		System.out.println(value);
		// test get value from a not existed ini file.
		String IniFilename2 = "D:\\thisfileisnotexisted.ini";
		IniReader reader2 = new IniReader(IniFilename2);
		value = reader2.getValue("sec", "key");
		assert (value == null);
		System.out.println("value should be null. value=" + value);

	}

}
