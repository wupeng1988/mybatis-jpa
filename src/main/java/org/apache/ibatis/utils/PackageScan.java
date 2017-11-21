/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.utils;
 
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
 
/**
 * 扫描制定包下所有类
 * 
 * @author lijianan
 * 
 */
public class PackageScan {
 
    public static List<String> getClassNames(String packageName,
            boolean recursive) {
        List<String> list = new LinkedList<String>();
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> urls = null;
        try {
            urls = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                 
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findClassesByFile(packageName, filePath, recursive, list);
                } else if ("jar".equals(protocol)) {
                    findClassesByJar(url, packageName,packageDirName, list);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        return list;
    }
 
    public static void findClassesByJar(URL url, String packageName, String packageDirName, List<String> classNames) throws IOException{
        JarFile jar = null;
        jar = ((JarURLConnection)url.openConnection()).getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
         
        while(entries.hasMoreElements()){
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
             
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    packageName = name.substring(0, idx)
                            .replace('/', '.');
                }
                 
                if ((idx != -1)) {
                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        classNames.add(packageName + '.' + className);
                    }
                }
            }
        }
    }
     
    public static void findClassesByFile(String packageName,
            String packagePath, final boolean recursive, List<String> classNames) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
 
        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
 
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findClassesByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(), recursive, classNames);
            } else {
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                classNames.add(packageName + '.' + className);
            }
        }
    }
 
    /**
     * 获取包下class资源
     * 
     * @param packageName
     * @return
     */
    public static List<String> getClassName(String packageName) {
        List<String> classNames = new ArrayList<String>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            String resourceName = packageName.replaceAll("\\.", "/");
            URL url = loader.getResource(resourceName);
            File urlFile = new File(url.toURI());
            File[] files = urlFile.listFiles();
            for (File f : files)
                getClassName(packageName, f, classNames);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return classNames;
    }
 
    private static void getClassName(String packageName, File packageFile,
            List<String> list) {
        if (packageFile.isFile()) {
            list.add(packageName + "."
                    + packageFile.getName().replace(".class", ""));
        } else {
            File[] files = packageFile.listFiles();
            String tmPackageName = packageName + "." + packageFile.getName();
            for (File f : files) {
                getClassName(tmPackageName, f, list);
            }
        }
    }
}