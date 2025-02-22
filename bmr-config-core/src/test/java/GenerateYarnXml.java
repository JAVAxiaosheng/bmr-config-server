import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateYarnXml {
    public static void main(String[] args) {
        // 定义文件路径
        String filePath = "D:\\bmr-config-server\\yarn.xml";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 写入 XML 头部
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<configuration>\n");

            // 生成 1000 条配置项
            for (int i = 1; i <= 1000; i++) {
                writer.write("    <property>\n");
                writer.write("        <name>yarn.example.config." + i + "</name>\n");
                writer.write("        <value>value" + i + "</value>\n");
                writer.write("        <description>test-item-" + i + "</description>\n");
                writer.write("    </property>\n");
            }

            // 写入 XML 尾部
            writer.write("</configuration>\n");

            System.out.println("yarn.xml 文件生成完成！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}