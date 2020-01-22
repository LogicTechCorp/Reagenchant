var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

function findMethodNode(classNode, methodName) {
    for each (var methodNode in classNode.methods) {
        if (methodNode.name == methodName) {
           return methodNode;
        }
    }
}

function log(message, arguments) {
    ASMAPI.log("INFO", message, arguments);
}

function logTransformation(transformed, className, methodName) {
    if(transformed) {
        log("Transformed {}::{}", [className, methodName]);
    }
    else {
        log("Unable to transform {}::{}", [className, methodName]);
    }
}