package io.github.yangziwen.mybatistool.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.github.yangziwen.mybatistool.GenerateConfig;

@Parameters(
        separators = "=",
        commandDescription = "generate codes for mybatis")
public class GenerateCommand implements Command {

    private static final String DATABASE_DRIVER_CLASS_NAME = "database.driverClassName";

    private static final String DATABASE_URL = "database.url";

    private static final String DATABASE_USERNAME = "database.username";

    private static final String DATABASE_PASSWORD = "database.password";

    private static final String TARGET_PROJECT_JAVA = "target.project.java";

    private static final String TARGET_PROJECT_XML = "target.project.xml";

    private static final String TARGET_MODEL_PACKAGE = "target.model.package";

    private static final String TARGET_MAPPER_PACKAGE = "target.mapper.package";

    private static final String TARGET_MAPPER_XML_PACKAGE = "target.mapper.xml.package";

    private static final String TABLE_NAME = "table.name";

    private static final String MODEL_NAME = "model.name";

    @Parameter(
            names = {"-h", "--help"},
            description = "print this message",
            help = true)
    public boolean help;

    @Parameter(
            names = {"-o", "--overwrite"},
            description = "whether to overwrite the existed files")
    public boolean overwrite = false;

    @Parameter(
            names = {"-m", "--mergeable"},
            description = "merge the new mapper.xml with the old one if true, otherwise will backup the old mapper.xml")
    public boolean mergeable = false;

    @Parameter(
            names = {"-tp", "--target-project"},
            description = "the target project absolute path",
            required = true)
    public String targetProject;

    @Parameter(
            names = {"-bp", "--base-package"},
            description = "the base package of model and mapper",
            required = true)
    public String basePackage;

    @Parameter(
            names = {"-os", "--model-package-suffix"},
            description = "the sub-package of model")
    public String modelPackageSuffix = "po";

    @Parameter(
            names = {"-ps", "--mapper-package-suffix"},
            description = "the sub-package of mapper")
    public String mapperPackageSuffix = "mapper";

    @Parameter(
            names = {"-xp", "--mapper-xml-package"},
            description = "the package of mapper xml")
    public String xmlPackage = "mybatis/mapper";

    @Parameter(
            names = {"-tn", "--table-name"},
            description = "the table name",
            required = true)
    public String tableName;

    @Parameter(
            names = {"-mn", "--model-name"},
            description = "the model name, will be derived from table name if absent")
    public String modelName;


    @Override
    public void invoke(JCommander commander) {

        if (help) {
            commander.usage(name());
            return;
        }

        try {

            Properties properties = collectProperties();

            backupExistingMapperXmlIfNecessary(properties);

            List<String> warnings = generate(properties);

            if (warnings.size() > 0) {
                for (String warning : warnings) {
                    System.out.println(ansi().fg(YELLOW).a(warning).reset());
                }
            }

            System.out.println(ansi()
                    .a("codes of table[").fg(GREEN).a(tableName).reset().a("] ")
                    .a("are generated ").fg(GREEN).a("successfully").reset());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<String> generate(Properties properties) throws Exception {

        List<String> warnings = new ArrayList<>();

        ConfigurationParser parser = new ConfigurationParser(properties, warnings);

        Configuration config = parser.parseConfiguration(this.getClass().getClassLoader().getResourceAsStream("generatorConfig.xml"));

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);

        new MyBatisGenerator(config, callback, warnings).generate(null);

        return warnings;

    }

    private Properties collectProperties() {

        Properties properties = new Properties();

        properties.setProperty(DATABASE_DRIVER_CLASS_NAME, GenerateConfig.database.driver_class_name);

        properties.setProperty(DATABASE_URL, GenerateConfig.database.url);

        properties.setProperty(DATABASE_USERNAME, GenerateConfig.database.username);

        properties.setProperty(DATABASE_PASSWORD, GenerateConfig.database.password);

        properties.setProperty(TARGET_PROJECT_JAVA, targetProject + "/src/main/java");

        properties.setProperty(TARGET_PROJECT_XML, targetProject + "/src/main/resources");

        properties.setProperty(TARGET_MODEL_PACKAGE, basePackage + "." + modelPackageSuffix);

        properties.setProperty(TARGET_MAPPER_PACKAGE, basePackage + "." + mapperPackageSuffix);

        properties.setProperty(TARGET_MAPPER_XML_PACKAGE, xmlPackage);

        properties.setProperty(TABLE_NAME, tableName);

        if (modelName == null || modelName.trim().length() == 0) {
            modelName = Arrays.stream(tableName.split("_"))
                    .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1))
                    .collect(Collectors.joining(""));
        }

        properties.setProperty(MODEL_NAME, modelName);

        return properties;
    }

    private void backupExistingMapperXmlIfNecessary(Properties properties) {

        if (mergeable) {
            return;
        }

        if (!overwrite) {
            return;
        }

        String mapperXmlPackage = properties.getProperty(TARGET_PROJECT_XML) + "/" + properties.getProperty(TARGET_MAPPER_XML_PACKAGE);

        String mapperXmlFilename = modelName + "Mapper.xml";

        String mapperXmlPath = mapperXmlPackage + "/" + mapperXmlFilename;

        File mapperXmlFile = new File(mapperXmlPath);

        if (mapperXmlFile.exists()) {
            int i = 1;
            File backupFile = new File(mapperXmlPath + ".backup" + i++);
            while (backupFile.exists()) {
                backupFile = new File(mapperXmlPath + ".backup" + i++);
            }
            mapperXmlFile.renameTo(backupFile);
        }

    }

}
