package com.ant.bmr.config.common.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import cn.hutool.crypto.SecureUtil;
import org.springframework.mock.web.MockMultipartFile;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.ant.bmr.config.common.context.FileContext;
import com.ant.bmr.config.common.enums.FileTypeEnum;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@Slf4j
public class ConfigFileUtil {

    // 加锁控制并发
    private static final Lock lock = new ReentrantLock();

    public static String splitFileName(String fileOriginName) {
        String[] fileOriginNameArr = splitFileOriginName(fileOriginName);
        if (fileOriginNameArr.length == 1) {
            return fileOriginName;
        }
        StringBuilder fileNameBuilder = new StringBuilder();
        for (int i = 0; i < fileOriginNameArr.length - 1; i++) {
            fileNameBuilder.append(fileOriginNameArr[i]);
            if (i != fileOriginNameArr.length - 2) {
                fileNameBuilder.append(".");
            }
        }
        return fileNameBuilder.toString();
    }

    public static String splitFileType(String fileOriginName) {
        String[] fileOriginNameArr = splitFileOriginName(fileOriginName);
        if (fileOriginNameArr.length == 1) {
            return FileTypeEnum.TXT.getCode();
        }
        return fileOriginNameArr[fileOriginNameArr.length - 1];
    }

    private static String[] splitFileOriginName(String fileOriginName) {
        if (StringUtils.isBlank(fileOriginName)) {
            throw new RuntimeException("fileOriginName is null");
        }
        return fileOriginName.split("\\.");
    }

    // 判断文件是否可以解析
    public static List<Map<String, String>> fileAnalyze(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileType = splitFileType(originalFilename);
        List<Map<String, String>> fileItems = Lists.newArrayList();
        try {
            if (StrUtil.equals(FileTypeEnum.XML.getCode(), fileType)) {
                Document document = XmlUtil.readXML(file.getInputStream());
                NodeList propertyTags = document.getElementsByTagName(FileContext.XML_PROPERTY_TAG_NAME);
                for (int i = 0; i < propertyTags.getLength(); i++) {
                    // 保证插入顺序和取出顺序一致和线程安全问题
                    Map<String, String> fileItemMap = Collections.synchronizedMap(new LinkedHashMap<>());
                    Element item = (Element) propertyTags.item(i);
                    String name = XmlUtil.elementText(item, FileContext.XML_NAME_TAG_NAME);
                    String value = XmlUtil.elementText(item, FileContext.XML_VALUE_TAG_NAME);
                    fileItemMap.put(name, value);
                    fileItems.add(fileItemMap);
                }
            } else if (StrUtil.equals(FileTypeEnum.YML.getCode(), fileType) ||
                    StrUtil.equals(FileTypeEnum.YAML.getCode(), fileType)) {
                Map<String, Object> load = YamlUtil.load(file.getInputStream(), Map.class);
                flattenMap(load, "", fileItems);
                return fileItems;
            }
        } catch (Exception e) {
            log.error("fileAnalyze is error,file name is: {}", originalFilename, e);
            // throw new RuntimeException("fileIsAnalyze is error,file name is: " + originalFilename, e);
        }
        return fileItems;
    }

    // 文件上传路径拼接:http://localhost:9001/bmr-config/cluster-id-1/node-group-id-1/test/2025-01-20 18:09:24/test.json
    public static String concatUploadFilePath(Long clusterId, Long nodeGroupId, String originalFilename) {
        SimpleDateFormat format = new SimpleDateFormat(FileContext.FILE_PATH_DATE_FORMAT);
        return concatStrBySeparator(FileContext.FILA_PATH_SEPARATOR,
                FileContext.PATH_CLUSTER_ID_PREFIX + clusterId,
                FileContext.PATH_NODE_GROUP_ID_PREFIX + nodeGroupId,
                splitFileName(originalFilename),
                format.format(new Date()),
                originalFilename);
    }

    private static String concatStrBySeparator(String separator, String... strParams) {
        return String.join(separator, strParams);
    }

    // MultipartFile转为File
    private static File convertToFile(MultipartFile multipartFile) {
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("temp", null);
            // 将 MultipartFile 写入临时文件
            multipartFile.transferTo(tempFile);
            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // File转为MultipartFile
    private static MultipartFile convertToMultipartFile(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return new MockMultipartFile(
                    "file",
                    file.getName(),
                    "application/octet-stream", // 通用二进制类型
                    inputStream
            );
        } catch (Exception e) {
            throw new RuntimeException("convert to multipartFile fail fileName:" + file.getName(), e);
        }
    }

    private static String readFileContext(InputStream inputStream) {
        lock.lock();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            // 使用流式处理读取文件内容
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException("read file context error:", e);
        } finally {
            // 最终释放
            lock.unlock();
        }
    }

    private static void flattenMap(Map<String, Object> map, String prefix, List<Map<String, String>> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            String fullKey = prefix.isEmpty() ? key : prefix + FileContext.YML_KEY_SEPARATOR + key;

            if (value instanceof Map) {
                // 递归处理嵌套的Map
                flattenMap((Map<String, Object>) value, fullKey, result);
            } else {
                // 将键值对添加到结果列表中
                Map<String, String> keyValueMap = Collections.synchronizedMap(new LinkedHashMap<>());
                keyValueMap.put(fullKey, value.toString());
                result.add(keyValueMap);
            }
        }
    }

    public static MultipartFile createTempFile(String fileOriginName, String fileContext) {
        File tempFile = FileUtil.touch(System.getProperty("user.dir") + StrUtil.BACKSLASH + fileOriginName);
        log.info("文件生成路径:{}", tempFile.getPath());
        FileUtil.writeString(fileContext, tempFile, StandardCharsets.UTF_8);
        MultipartFile multipartFile = convertToMultipartFile(tempFile);
        FileUtil.del(tempFile);
        return multipartFile;
    }

    public static String getFileMd5(MultipartFile multipartFile) {
        String fileMd5;
        try {
            fileMd5 = SecureUtil.md5(multipartFile.getInputStream());
        } catch (Exception e) {
            log.error("get file md5 error,originalFilename: {}", multipartFile.getOriginalFilename(), e);
            throw new RuntimeException("get file md5 error:", e);
        }
        return fileMd5;
    }

    // 生成xml或yaml文件内容
    public static String getAnalyzeFileContext(String fileType, Map<String, String> configMap) {
        String fileContext;
        if (StrUtil.equals(FileTypeEnum.XML.getCode(), fileType)) {
            fileContext = getXmlFileContext(configMap);
        } else if (StrUtil.equals(FileTypeEnum.YML.getCode(), fileType) ||
                StrUtil.equals(FileTypeEnum.YAML.getCode(), fileType)) {
            fileContext = getYamlFileContext(configMap);
        } else {
            log.error("not support analyze file type: {}", fileType);
            throw new RuntimeException("not support analyze file type:" + fileType);
        }
        return fileContext;
    }

    // 生成XML文件内容
    private static String getXmlFileContext(Map<String, String> configMap) {
        // 创建 XML 文档
        Document document = XmlUtil.createXml("configuration");

        // 获取根元素 <configuration>
        Element rootElement = document.getDocumentElement();

        // 遍历配置项，生成 <property> 元素
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            Element propertyElement = document.createElement("property");

            // 创建 <name> 元素
            Element nameElement = document.createElement("name");
            nameElement.setTextContent(entry.getKey());
            propertyElement.appendChild(nameElement);

            // 创建 <value> 元素
            Element valueElement = document.createElement("value");
            valueElement.setTextContent(entry.getValue());
            propertyElement.appendChild(valueElement);

            // 将 <property> 添加到根元素
            rootElement.appendChild(propertyElement);
        }

        // 将 Document 转换为字符串
        return formatXml(XmlUtil.toStr(document));
    }

    // 格式化xml文件内容
    private static String formatXml(String xmlContent) {

        // 将字符串转换为 Document
        Document document = XmlUtil.parseXml(xmlContent);

        // 使用 Transformer 格式化 XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try {
            Transformer transformer = transformerFactory.newTransformer();

            // 设置格式化属性
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            // 将 Document 转换为字符串
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("format xml error: ", e);
        }
    }

    // 生成YAML文件内容
    private static String getYamlFileContext(Map<String, String> configMap) {
        Yaml yaml = new Yaml();
        return yaml.dump(configMap);
    }
}