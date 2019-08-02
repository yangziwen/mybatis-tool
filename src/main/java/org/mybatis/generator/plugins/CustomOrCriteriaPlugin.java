package org.mybatis.generator.plugins;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class CustomOrCriteriaPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        InnerClass criteria = null;
        for (InnerClass innerClass : topLevelClass.getInnerClasses()) {
            if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {
                criteria = innerClass;
                break;
            }
        }
        if (criteria == null) {
            return true;
        }
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "value"));

        StringBuilder sb = new StringBuilder();
        sb.insert(0, "and");
        sb.append("CustomCriteria");
        method.setName(sb.toString());
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());

        sb.setLength(0);
        sb.append("addCriterion( \"(\"+ value +\")\" );");
        method.addBodyLine(sb.toString());
        method.addBodyLine("return (Criteria) this;");

        criteria.addMethod(method);

        return true;
    }
}
