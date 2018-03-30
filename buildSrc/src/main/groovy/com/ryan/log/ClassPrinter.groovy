package com.ryan.log

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Attribute
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.TypePath

class ClassPrinter extends ClassVisitor {

    ClassPrinter(int api) {
        super(api)
    }

    ClassPrinter(int api, ClassVisitor cv) {
        super(api, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        println  "visit"
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    void visitSource(String source, String debug) {
        super.visitSource(source, debug)
        println  "visitSource"

    }

    @Override
    void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(owner, name, desc)
        println  "visitOuterClass"

    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        println  "visitAnnotation"
        return super.visitAnnotation(desc, visible)
    }

    @Override
    AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        println  "visitTypeAnnotation"
        return super.visitTypeAnnotation(typeRef, typePath, desc, visible)
    }

    @Override
    void visitAttribute(Attribute attr) {
        super.visitAttribute(attr)
        println  "visitAttribute"
    }

    @Override
    void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access)
        println  "visitInnerClass"

    }

    @Override
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        println  "visitField"
        return super.visitField(access, name, desc, signature, value)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        println  "visitMethod"
        return super.visitMethod(access, name, desc, signature, exceptions)
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        println  "visitEnd"
    }
}