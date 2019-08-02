package io.github.yangziwen.mybatistool.command;

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

    @Parameter(
            names = {"-h", "--help"},
            description = "print this message",
            help = true)
    public boolean help;

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
            names = {"--model-package-suffix"},
            description = "the sub-package of model")
    public String modelPackageSuffix = "po";

    @Parameter(
            names = {"--mapper-package-suffix"},
            description = "the sub-package of mapper")
    public String mapperPackageSuffix = "mapper";

    @Parameter(
            names = {"--mapper-xml-package-suffix"},
            description = "the sub-package of mapper xml")
    public String mapperXmlPackageSuffix = "mapper";

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

        try {
            generate(collectProperties());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<String> generate(Properties properties) throws Exception {

        List<String> warnings = new ArrayList<>();

        ConfigurationParser parser = new ConfigurationParser(properties, warnings);

        Configuration config = parser.parseConfiguration(this.getClass().getResourceAsStream("generatorConfig.xml"));

        DefaultShellCallback callback = new DefaultShellCallback(false);

        new MyBatisGenerator(config, callback, warnings).generate(null);

        return warnings;

    }

    private Properties collectProperties() {

        Properties properties = new Properties();

        properties.setProperty("database.driverClassName", GenerateConfig.database.driver_class_name);

        properties.setProperty("database.url", GenerateConfig.database.url);

        properties.setProperty("database.username", GenerateConfig.database.username);

        properties.setProperty("database.password", GenerateConfig.database.password);

        properties.setProperty("target.model.package", basePackage + "." + modelPackageSuffix);

        properties.setProperty("target.mapper.package", basePackage + "." + mapperPackageSuffix);

        properties.setProperty("target.mapper.xml.package", basePackage + "." + mapperXmlPackageSuffix);

        properties.setProperty("table.name", tableName);

        if (modelName == null || modelName.trim().length() == 0) {
            modelName = Arrays.stream(tableName.split("_"))
                    .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1))
                    .collect(Collectors.joining(""));
        }

        properties.setProperty("model.name", modelName);

        return properties;
    }

}
