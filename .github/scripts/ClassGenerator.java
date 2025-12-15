///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.palantir.javapoet:javapoet:0.9.0
//DEPS com.google.code.gson:gson:2.10.1

import com.palantir.javapoet.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.lang.reflect.Type;

public class ClassGenerator {

    static class FieldDefinition {
        String name;
        String type;
    }

    public static void main(String... args) throws Exception {
        if (args.length < 3) {
            System.exit(1);
        }

        String packageName = args[0];
        String className = args[1];
        String fieldsJson = args[2];
        String targetDir = "src/main/java"; 

        // JSON parse
        Gson gson = new Gson();
        Type listType = new TypeToken<List<FieldDefinition>>(){}.getType();
        List<FieldDefinition> fields = gson.fromJson(fieldsJson, listType);

   
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);

        for (FieldDefinition field : fields) {
            TypeName typeName;
            
            if (field.type.equals("String")) {
                typeName = ClassName.get(String.class);
            } else if (field.type.equals("int") || field.type.equals("Integer")) {
                typeName = TypeName.INT;
            } else if (field.type.equals("Long")) {
                typeName = ClassName.get(Long.class);
            } else {
                typeName = ClassName.bestGuess(field.type);
            }

            FieldSpec fieldSpec = FieldSpec.builder(typeName, field.name)
                    .addModifiers(Modifier.PRIVATE)
                    .build();
            
            classBuilder.addField(fieldSpec);
            
        }

        JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build())
                .indent("    ")
                .build();

    
        File outputDir = new File(targetDir);
        javaFile.writeTo(outputDir);
        
        System.out.println("Gen: " + outputDir.getPath() + "/" + packageName.replace('.', '/') + "/" + className + ".java");
    }
}
