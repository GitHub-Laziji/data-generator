package pg.laziji.datagenerator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.laziji.commons.rereg.model.Node;
import org.laziji.commons.rereg.model.OrdinaryNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        Date startTime = new Date();
        System.out.println("Start time: " + startTime.toString());

        String path = "./config.json";
        if (args.length > 0) {
            path = args[0];
        }
        System.out.println("Load config: " + path);

        JSONObject config = loadJsonFile(path);
        System.out.println(JSON.toJSONString(config, true));

        JSONObject dbConfig = config.getJSONObject("database");
        JSONArray tableConfigs = config.getJSONArray("tables");

        URL driverUrl = new URL("file:" + dbConfig.getString("driverPath"));
        Class driverClass = new URLClassLoader(new URL[]{driverUrl}).loadClass(dbConfig.getString("driverClass"));
        Driver driver = (Driver) driverClass.newInstance();

        Properties properties = new Properties();
        properties.setProperty("user", dbConfig.getString("username"));
        properties.setProperty("password", dbConfig.getString("password"));
        try (Connection connection = driver.connect(
                dbConfig.getString("url"), properties)) {
            connection.setAutoCommit(false);

            for (int i = 0; i < tableConfigs.size(); i++) {
                JSONObject tableConfig = tableConfigs.getJSONObject(i);
                StringBuilder names = null;
                StringBuilder values = null;
                List<Node> nodes = new ArrayList<>();
                for (Map.Entry<String, Object> entry : tableConfig.getJSONObject("rules").entrySet()) {
                    if (names == null) {
                        names = new StringBuilder(entry.getKey());
                        values = new StringBuilder("?");
                    } else {
                        names.append(",").append(entry.getKey());
                        values.append(",?");
                    }
                    nodes.add(new OrdinaryNode(entry.getValue().toString()));
                }
                String tableName = tableConfig.getString("name");
                String sql = "insert into " + tableName + "(" + names + ") values(" + values + ")";
                System.out.println();
                System.out.println("Table: " + tableName);
                System.out.println("Insert sql: " + sql);
                PreparedStatement statement = connection
                        .prepareStatement(sql);
                int amount = tableConfig.getIntValue("amount");
                if (amount < 1) {
                    amount = 1;
                }
                if (amount > 100000) {
                    amount = 100000;
                }
                System.out.println("Amount: " + amount);
                int count = 0;
                while (count < amount) {
                    for (int j = 0; j < nodes.size(); j++) {
                        statement.setString(j + 1, nodes.get(j).random());
                    }
                    statement.execute();
                    count++;
                    System.out.print(String.format("\r%.2f%%", (double) count * 100 / amount));
                }
                System.out.println();
            }
            connection.commit();

            Date endTime = new Date();
            System.out.println("Complete time: " + endTime.toString()
                    + ". Duration: " + (endTime.getTime() - startTime.getTime()) + "ms");
        }
    }

    private static JSONObject loadJsonFile(String path) throws IOException {
        File configFile = new File(path);
        try (FileInputStream fis = new FileInputStream(configFile)) {
            byte[] buf = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = fis.read(buf)) > 0) {
                sb.append(new String(buf, 0, len));
            }
            return JSON.parseObject(sb.toString());
        }
    }
}
