package com.me.plugin

import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import javassist.Modifier

public class MyInject {

    private static ClassPool pool = ClassPool.getDefault()

    public static void injectDir(String path, String packageName) {
        pool.appendClassPath(path)
        int rootIndex = path.indexOf("/app/build/intermediates/")
        String saviorPath = path.substring(0, rootIndex) + "/savior/build/intermediates/classes/release"
        pool.appendClassPath(saviorPath)
        pool.insertClassPath("/Users/wangxiandeng/Library/Android/sdk/platforms/android-24/android.jar")


        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->

                String filePath = file.absolutePath
                //确保当前文件是class文件，并且不是系统自动生成的class文件
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")
                        && !filePath.contains("\$Patch.class")
                        && !filePath.contains("PatchBox.class")) {
                    // 判断当前目录是否是在我们的应用包里面
                    int index = filePath.indexOf(packageName);
                    boolean isMyPackage = index != -1;
                    if (isMyPackage) {
                        int end = filePath.length() - 6 // .class = 6
                        String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
                        //开始修改class文件
                        CtClass c = pool.getCtClass(className)

                        if (c.isFrozen()) {
                            c.defrost()
                        }
                        pool.importPackage("com.wangxiandeng.savior")

                        //给类添加$savior变量，即补丁变量
                        CtField savior = new CtField(pool.get("com.wangxiandeng.savior.Savior"), "\$savior", c);
                        savior.setModifiers(Modifier.STATIC);
                        c.addField(savior);

                        //遍历类的所有方法
                        CtMethod[] methods = c.getDeclaredMethods();
                        for (CtMethod method : methods) {
                            //在每个方法之前插入判断语句，判断类的补丁实例是否存在
                            StringBuilder injectStr = new StringBuilder();
                            injectStr.append("if(\$savior!=null){\n")
                            String javaThis = "null,"
                            if (!Modifier.isStatic(method.getModifiers())) {
                                javaThis = "this,"
                            }
                            String runStr = "\$savior.dispatchMethod(" + javaThis + "\"" + method.getName() + "." + method.getSignature() + "\" ,\$args)"
                            injectStr.append(addReturnStr(method, runStr))
                            injectStr.append("}")
                            print("插入了：" + injectStr.toString() + "语句")
                            method.insertBefore(injectStr.toString())
                        }
                        c.writeFile(path)
                        c.detach()
                    }
                }
            }
        }
    }

//解析方法签名，获取方法放回值类型
    public static String getReturnType(String methodSign) {
        String type = "";
        int index = methodSign.indexOf(")L");
        String jType = methodSign.substring(index + 2, methodSign.length() - 1);
        type = jType.replace("/", ".");
        return type;
    }

//给非void方法加入return语句，并处理基本类型返回值
    public static String addReturnStr(CtMethod method, String runStr) {
        String returnStr = "";
        String typeStr = "";
        switch (method.getReturnType()) {
            case CtClass.voidType:
                return runStr + ";\n return;"
                break;
            case CtClass.booleanType:
                returnStr = "return ((Boolean)";
                typeStr = ".booleanValue()";
                break;
            case CtClass.byteType:
                returnStr = "return ((byte)";
                typeStr = ".byteValue()";
                break;
            case CtClass.charType:
                returnStr = "return ((char)";
                typeStr = ".charValue()";
                break;
            case CtClass.doubleType:
                returnStr = "return ((Number)";
                typeStr = ".doubleValue()";
                break;
            case CtClass.floatType:
                returnStr = "return ((Number)";
                typeStr = ".floatValue()";
                break;
            case CtClass.intType:
                returnStr = "return ((Number)";
                typeStr = ".intValue()";
                break;
            case CtClass.longType:
                returnStr = "return ((Number)";
                typeStr = ".longValue()";
                break;
            case CtClass.shortType:
                returnStr = "return ((Number)";
                typeStr = ".shortValue()";
                break;
            default:
                returnStr = "return((" + getReturnType(method.getSignature()) + ")";
                break;
        }
        return returnStr + "(" + runStr + "))" + typeStr + ";\n";
    }


}