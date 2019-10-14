package edu.baylor.ecs.seer.dataflow;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

import javax.el.MethodNotFoundException;
import java.nio.file.Paths;
import java.util.List;

public class JavaParserAnalyzer {

    private void logicSwap(String packageName, String fileName) {
        SourceRoot sourceRoot = getSourceRoot();
        CompilationUnit compilationUnit = getCompilationUnit(sourceRoot, packageName, fileName);

        compilationUnit.accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(IfStmt n, Void arg) {
                n.getCondition().ifBinaryExpr(binaryExpr -> {
                    if (binaryExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS && n.getElseStmt().isPresent()) {
                        Statement thenStmt = n.getThenStmt().clone();
                        Statement elseStmt = n.getElseStmt().get().clone();
                        n.setThenStmt(elseStmt);
                        n.setElseStmt(thenStmt);
                        binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
                    }
                });
                return super.visit(n, arg);
            }
        }, null);

        // This saves all the files we just read to an output directory.
        sourceRoot.saveAll(
                CodeGenerationUtils.mavenModuleRoot(Main.class)
                        .resolve(Paths.get("output")));
    }

    public void getVariableType(String packageName, String fileName, String className, String methodName) throws Exception {
        ClassOrInterfaceDeclaration clazz = getClassFromFile(packageName, fileName, className);

        // Find method by name
        List<MethodDeclaration> methods = clazz.getMethodsByName(methodName);
        if (methods.size() == 0)
            throw new MethodNotFoundException();

        // Get the content of method's body
        MethodDeclaration method = methods.get(0);
        BlockStmt block = method.getBody().orElse(null);
        if (block == null)
            throw new MethodNotFoundException();

        String ans = block.findAll(MethodCallExpr.class).stream()
                .filter(v -> v.getName().asString().equals("getForObject"))
                .map(v -> v.getArguments().toString())
                .findFirst().orElse(null);

        System.out.println(ans);
    }

    private ClassOrInterfaceDeclaration getClassFromFile(String packageName, String fileName, String className) throws ClassNotFoundException {
        SourceRoot sourceRoot = getSourceRoot();
        CompilationUnit compilationUnit = getCompilationUnit(sourceRoot, packageName, fileName);

        // Find class by name
        ClassOrInterfaceDeclaration clazz = compilationUnit.getClassByName(className)
                .orElse(null);
        if (clazz == null)
            throw new ClassNotFoundException();

        return clazz;
    }

    private CompilationUnit getCompilationUnit(SourceRoot sourceRoot, String packageName, String fileName) {
        return sourceRoot.parse(packageName, fileName);
    }

    private SourceRoot getSourceRoot() {
        return new SourceRoot(CodeGenerationUtils.mavenModuleRoot(Main.class).resolve("src/main/java"));
    }
}
