var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

function initializeCoreMod() {
    ASMAPI.loadFile("asm/asm-base.js");
    return {
        'attempt-damage-item': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ItemStack'
            },
            'transformer': function(classNode) {
                var methodName = ASMAPI.mapMethod("func_96631_a");
                var methodNode = findMethodNode(classNode, methodName);

                //setDamage
                var newInsnList = ASMAPI.listOf(
                    new VarInsnNode(Opcodes.ALOAD, 3),
                    ASMAPI.buildMethodCall(
                        "logictechcorp/reagenchant/asm/UnbreakingHooks",
                        "adjustSetDamage",
                        "(Lnet/minecraft/item/ItemStack;ILnet/minecraft/entity/LivingEntity;)I",
                        ASMAPI.MethodType.STATIC
                    ),
                    new VarInsnNode(Opcodes.ISTORE, 4),
                    new VarInsnNode(Opcodes.ALOAD, 0),
                    new VarInsnNode(Opcodes.ILOAD, 4)
                );
                var transformed = ASMAPI.insertInsnList(
                    methodNode,
                    ASMAPI.MethodType.VIRTUAL,
                    "net/minecraft/item/ItemStack",
                    ASMAPI.mapMethod("func_196085_b"),
                    "(I)V",
                    newInsnList,
                    ASMAPI.InsertMode.INSERT_BEFORE
                );

                logTransformation(transformed, classNode.name, methodName);
                return classNode;
            }
        },
        'on-block-destroyed': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ItemStack'
            },
            'transformer': function(classNode) {
                var methodName = ASMAPI.mapMethod("func_179548_a");
                var methodNode = findMethodNode(classNode, methodName);

                //onBlockDestroyed
                var newInsnList = ASMAPI.listOf(
                    ASMAPI.buildMethodCall(
                        "logictechcorp/reagenchant/asm/UnbreakingHooks",
                        "onBlockDestroyed",
                        "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)Z",
                        ASMAPI.MethodType.STATIC
                    )
                );
                var transformed = ASMAPI.insertInsnList(
                    methodNode,
                    ASMAPI.MethodType.VIRTUAL,
                    "net/minecraft/item/Item",
                    ASMAPI.mapMethod("func_179218_a"),
                    "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)Z",
                    newInsnList,
                    ASMAPI.InsertMode.REMOVE_ORIGINAL
                );

                logTransformation(transformed, classNode.name, methodName);
                return classNode;
            }
        },
        'get-attribute-modifiers': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ItemStack'
            },
            'transformer': function(classNode) {
                var methodName = ASMAPI.mapMethod("func_111283_C");
                var methodNode = findMethodNode(classNode, methodName);

                //getAttributeModifiers
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
                    methodNode,
                    ASMAPI.MethodType.VIRTUAL,
                    "net/minecraft/item/ItemStack",
                    ASMAPI.mapMethod("func_184543_l"),
                    "()Lnet/minecraft/item/Item;",
                    newInsnList,
                    ASMAPI.InsertMode.REMOVE_ORIGINAL
                );

                logTransformation(true, classNode.name, methodName);
                return classNode;
            }
        },
        'get-harvest-level': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.Item'
            },
            'transformer': function(classNode) {
                var methodName = "getHarvestLevel";
                var methodNode = findMethodNode(classNode, methodName);

                //intValue
                var newInsnList = ASMAPI.listOf(
                    new VarInsnNode(Opcodes.ISTORE, 5),
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
                    methodNode,
                    ASMAPI.MethodType.VIRTUAL,
                    "java/lang/Integer",
                    "intValue",
                    "()I",
                    newInsnList,
                    ASMAPI.InsertMode.INSERT_AFTER
                );

                logTransformation(transformed, classNode.name, methodName);
                return classNode;
            }
        }
    }
}