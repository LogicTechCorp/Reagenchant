var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

function initializeCoreMod() {
    ASMAPI.loadFile("asm/asm-base.js");
    return {
        'damage-item': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ItemStack'
            },
            'transformer': function(classNode) {
                var transformedMethodName = ASMAPI.mapMethod("func_222118_a");
                var newInsnList = ASMAPI.listOf(
                    ASMAPI.buildMethodCall(
                        "logictechcorp/reagenchant/asm/UnbreakingHooks",
                        "attemptDamageItem",
                        "(Lnet/minecraft/item/ItemStack;ILjava/util/Random;Lnet/minecraft/entity/player/ServerPlayerEntity;)Z",
                        ASMAPI.MethodType.STATIC
                    )
                );
                var transformed = ASMAPI.insertInsnList(
                    findMethodNode(classNode, transformedMethodName),
                    ASMAPI.MethodType.VIRTUAL,
                    "net/minecraft/item/ItemStack",
                    ASMAPI.mapMethod("func_96631_a"),
                    "(ILjava/util/Random;Lnet/minecraft/entity/player/ServerPlayerEntity;)Z",
                    newInsnList,
                    ASMAPI.InsertMode.REMOVE_ORIGINAL
                );

                logTransformation(transformed, classNode.name, transformedMethodName);
                return classNode;
            }
        },
        'on-block-destroyed': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ItemStack'
            },
            'transformer': function(classNode) {
                var transformedMethodName = ASMAPI.mapMethod("func_179548_a");
                var newInsnList = ASMAPI.listOf(
                    ASMAPI.buildMethodCall(
                        "logictechcorp/reagenchant/asm/UnbreakingHooks",
                        "onBlockDestroyed",
                        "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)Z",
                        ASMAPI.MethodType.STATIC
                    )
                );
                var transformed = ASMAPI.insertInsnList(
                    findMethodNode(classNode, transformedMethodName),
                    ASMAPI.MethodType.VIRTUAL,
                    "net/minecraft/item/Item",
                    ASMAPI.mapMethod("func_179218_a"),
                    "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)Z",
                    newInsnList,
                    ASMAPI.InsertMode.REMOVE_ORIGINAL
                );

                logTransformation(transformed, classNode.name, transformedMethodName);
                return classNode;
            }
        },
        'get-attribute-modifiers': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ItemStack'
            },
            'transformer': function(classNode) {
                var transformedMethodName = ASMAPI.mapMethod("func_111283_C");
                var newInsnList = ASMAPI.listOf(
                    new VarInsnNode(Opcodes.ALOAD, 1),
                    ASMAPI.buildMethodCall(
                        "logictechcorp/reagenchant/asm/UnbreakingHooks",
                        "getAttributeModifiers",
                        "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/inventory/EquipmentSlotType;)Lcom/google/common/collect/Multimap;",
                        ASMAPI.MethodType.STATIC
                    ),
                    new VarInsnNode(Opcodes.ASTORE, 2),
                    new VarInsnNode(Opcodes.ALOAD, 2),
                    new InsnNode(Opcodes.ARETURN)
                );
                var transformed = ASMAPI.insertInsnList(
                    findMethodNode(classNode, transformedMethodName),
                    ASMAPI.MethodType.VIRTUAL,
                    "net/minecraft/item/ItemStack",
                    ASMAPI.mapMethod("func_184543_l"),
                    "()Lnet/minecraft/item/Item;",
                    newInsnList,
                    ASMAPI.InsertMode.REMOVE_ORIGINAL
                );

                logTransformation(true, classNode.name, transformedMethodName);
                return classNode;
            }
        },
        'get-harvest-level': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.Item'
            },
            'transformer': function(classNode) {
                var transformedMethodName = "getHarvestLevel";
                var newInsnList = ASMAPI.listOf(
                    new VarInsnNode(Opcodes.ISTORE,5),
                    new VarInsnNode(Opcodes.ALOAD, 1),
                    new VarInsnNode(Opcodes.ILOAD, 5),
                    ASMAPI.buildMethodCall(
                        "logictechcorp/reagenchant/asm/UnbreakingHooks",
                        "getHarvestLevel",
                        "(Lnet/minecraft/item/ItemStack;I)I",
                        ASMAPI.MethodType.STATIC
                    )
                );
                var transformed = ASMAPI.insertInsnList(
                    findMethodNode(classNode, transformedMethodName),
                    ASMAPI.MethodType.VIRTUAL,
                    "java/lang/Integer",
                    "intValue",
                    "()I",
                    newInsnList,
                    ASMAPI.InsertMode.INSERT_AFTER
                );

                logTransformation(transformed, classNode.name, transformedMethodName);
                return classNode;
            }
        }
    }
}