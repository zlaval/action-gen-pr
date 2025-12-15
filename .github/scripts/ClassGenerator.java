///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.squareup:javapoet:1.13.0
// com.google.code.gson:gson:2.10.1

//import com.palantir.javapoet.*;
import com.squareup.javapoet.*;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.lang.reflect.Type;
// com.palantir.javapoet:javapoet:0.9.0
public class ClassGenerator {

    static class FieldDefinition {
        String name;
        String type;
    }

public class ClassGenerator {

    public static void main(String... args) throws Exception {
        if (args.length < 3) {
            System.err.println("Használat: java ClassGenerator <Csomagnév> <Osztálynév> <Mezők>");
            System.exit(1);
        }

        String packageName = args[0];
        String className = args[1];
        String fieldsRaw = args[2]; 
        String targetDir = "src/main/java";

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);

     
        String[] fields = fieldsRaw.split(",");

        for (String fieldPair : fields) {
            String[] parts = fieldPair.trim().split(":");
            if (parts.length != 2) {
                System.out.println("Hiba: Érvénytelen mező definíció: " + fieldPair);
                continue;
            }

            String fName = parts[0].trim();
            String fType = parts[1].trim();
            TypeName typeName;

            // Típus felismerés
            if (fType.equalsIgnoreCase("String")) {
                typeName = ClassName.get(String.class);
            } else if (fType.equalsIgnoreCase("int") || fType.equalsIgnoreCase("Integer")) {
                typeName =  ClassName.get(Integer.class); // Vagy ClassName.get(Integer.class) ha objektum kell
            } else if (fType.equalsIgnoreCase("Long")) {
                typeName = ClassName.get(Long.class);
            } else if (fType.equalsIgnoreCase("boolean")) {
                typeName = TypeName.BOOLEAN;
            } else {
                typeName = ClassName.bestGuess(fType);
            }

            FieldSpec fieldSpec = FieldSpec.builder(typeName, fName)
                    .addModifiers(Modifier.PRIVATE)
                    .build();
            
            classBuilder.addField(fieldSpec);
        }

        JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build())
                .indent("    ")
                .build();

        File outputDir = new File(targetDir);
        javaFile.writeTo(outputDir);
        
        System.out.println("Generálva: " + outputDir.getPath() + "/" + packageName.replace('.', '/') + "/" + className + ".java");
    }
}
