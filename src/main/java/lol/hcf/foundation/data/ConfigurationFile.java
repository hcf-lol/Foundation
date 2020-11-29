package lol.hcf.foundation.data;

import org.bukkit.ChatColor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ConfigurationFile extends FileStorage<ConfigurationFile> {

    private static transient final Yaml YAML;

    protected ConfigurationFile(File configFile) {
        super(configFile);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfigurationFile parse(Reader in) {
        Map<String, Object> config = (Map<String, Object>) ConfigurationFile.YAML.load(in);
        super.reflectiveFieldSet(config);
        return this;
    }

    @Override
    public ConfigurationFile dump(Writer out) {
        ConfigurationFile.YAML.dump(this, out);
        return this;
    }

    static {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setSplitLines(true);
        options.setAllowReadOnlyProperties(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        // Using a custom constructor and representer to properly replace the color char
        YAML = new Yaml(new YamlConstructor(), new YamlRepresenter(), options);
        ConfigurationFile.YAML.setBeanAccess(BeanAccess.FIELD);
    }

    private static class YamlRepresenter extends Representer {
        @Override
        protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
            if (!super.classTags.containsKey(javaBean.getClass())) super.addClassTag(javaBean.getClass(), Tag.MAP);
            return super.representJavaBean(properties, javaBean);
        }

        @Override
        protected Node representScalar(Tag tag, String value, Character style) {
            return super.representScalar(tag, value.replaceAll("&", "\\\\&").replaceAll(String.valueOf(ChatColor.COLOR_CHAR), "&"), style);
        }
    }

    private static class YamlConstructor extends Constructor {

        public YamlConstructor() {
            this(Object.class);
        }

        public YamlConstructor(Class<?> theRoot) {
            this(new TypeDescription(Objects.requireNonNull(theRoot)));
        }

        public YamlConstructor(TypeDescription theRoot) {
            super(theRoot);

            super.yamlConstructors.put(Tag.STR, new ScalarConstructor());
        }

        public static class ScalarConstructor extends AbstractConstruct {
            @Override
            public Object construct(Node node) {
                return ((ScalarNode) node).getValue().replaceAll("&(?<!\\\\&)", String.valueOf(ChatColor.COLOR_CHAR)).replaceAll("\\\\&", "&");
            }
        }
    }
}
