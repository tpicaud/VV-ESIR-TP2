package fr.istic.vv;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

// This class visits a compilation unit and
// prints all public enum, classes or interfaces along with their public methods
public class PublicElementsPrinter extends VoidVisitorWithDefaults<Void> {

    List<String> var = new ArrayList<String>();
    List<String> method = new ArrayList<String>();
    List<String> no_getter = new ArrayList<String>();

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for (TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        if (!declaration.isPublic())
            return;
        System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous]"));
        for (MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }
        // Printing nested types in the top level
        for (BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof TypeDeclaration)
                member.accept(this, arg);
        }
        for (FieldDeclaration method : declaration.getFields()) {
            method.accept(this, arg);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        // var.add(declaration.toString());
        // visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        method.add(declaration.getNameAsString().toLowerCase());

        // if(!declaration.isPublic()) return;
        // System.out.println(" " + declaration.getDeclarationAsString(true, true));
    }

    @Override
    public void visit(FieldDeclaration declaration, Void arg) {
        for (VariableDeclarator variable : declaration.getVariables()) {
            var.add(variable.getNameAsString());
        }
        // if(!declaration.isPublic()) return;
        // System.out.println(" " + declaration.getDeclarationAsString(true, true));
    }

    public List<String> getNoGetter() {
        System.out.println(var);
        System.out.println(method);

        for (String var_name : var) {
            if (!method.contains("get" + var_name))
                no_getter.add(var_name);
        }
        return no_getter;
    }

    public void createFile() throws FileNotFoundException, UnsupportedEncodingException {
        List<String> to_write = getNoGetter();

        PrintWriter writer = new PrintWriter("no_getter.txt", "UTF-8");
        writer.println("Arrtibuts sans getter :\n");
        for (String line : to_write) {
            writer.println(line);
        }
        writer.close();
    }

    public List<String> getVar() {
        return var;
    }

}
